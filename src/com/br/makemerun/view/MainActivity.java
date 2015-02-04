package com.br.makemerun.view;

import java.text.DecimalFormat;
import java.util.List;

import org.achartengine.model.XYSeries;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;

import com.br.makemerun.R;
import com.br.makemerun.database.StatsDB;
import com.br.makemerun.model.ChangeTimeListener;
import com.br.makemerun.service.ChangeLocationListener;
import com.br.makemerun.service.MapService;
import com.br.makemerun.service.MapService.LocalBinder;
import com.br.makemerun.view.widgets.CircularProgressBar;

public class MainActivity extends Activity implements ChangeLocationListener, ChangeTimeListener{

	private ImageView startPauseButton;
	private TextView timerValue;
	//private TextView speedText;
	private TextView distanceValue;
//	private TextView runningKmText;
	private TextView partialKmText;
	private TextView stopButton;
	private RelativeLayout layoutStateIcon;
	private ImageView stateIcon;
	private CircularProgressBar kmTotalProgress;
	private CircularProgressBar kmPartialProgress;
	
	public Boolean isStart = true;
	private boolean mBound = false;
	private MapService mapService;
	
	private int VIBRATION_TIME_CHANGE = 500;
	private int VIBRATION_TIME_END = 1000;

	private final int END_STATE = 0;
	private final int RUNNING_STATE = 1;
	private final int WALKING_STATE = 2;

	private int subgoal;
	
	private double totalDistance;

	private double partialDistanceRunning;
	private double partialDistanceWalking;
	private double lastChangeKm = 0;

	private double currState = RUNNING_STATE;

	private double runningSpeed = 0;
	private double runningSpeedSamples = 0;

	private int runningTime = 0;

	private final int STATISTICS_REQUEST = 2;
	private XYSeries speedSeries;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_run);

		Bundle bundle = this.getIntent().getExtras();

		subgoal = bundle.getInt("subgoal");
		totalDistance = bundle.getDouble("totalDistance");
		partialDistanceRunning = bundle.getDouble("partialDistanceRunning");
		partialDistanceWalking = bundle.getDouble("partialDistanceWalking");

		speedSeries = new XYSeries("Speed");
		
		startPauseButton = (ImageView) findViewById(R.id.startButton);
		stopButton = (TextView) findViewById(R.id.btnStop);
		timerValue = (TextView) findViewById(R.id.txTimerValue);
		distanceValue = (TextView) findViewById(R.id.txDistance);
		stateIcon = (ImageView) findViewById(R.id.icState);
		//speedText = (TextView) findViewById(R.id.txSpeed);
		//runningKmText = (TextView) findViewById(R.id.txRunningKm);
		partialKmText = (TextView) findViewById(R.id.txPartial);
		kmTotalProgress = (CircularProgressBar) findViewById(R.id.progressKmTotal);
		kmPartialProgress = (CircularProgressBar) findViewById(R.id.progressKmPartial);
		layoutStateIcon = (RelativeLayout) findViewById(R.id.layStateIcon);
		stateIcon.setImageResource(R.drawable.runicon);
		partialKmText.setText(String.format("%.2f", partialDistanceRunning) + "km");

		kmTotalProgress.setMax((int)(Math.round(totalDistance*1000)));
		kmPartialProgress.setMax((int)(Math.round(partialDistanceRunning*1000)));

		stopButton.setVisibility(View.INVISIBLE);

		stopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				mapService.pauseMapping();
				startPauseButton.setVisibility(View.VISIBLE);
				layoutStateIcon.setVisibility(View.INVISIBLE);
				Intent intent = new Intent();
				setResult(RESULT_CANCELED,intent);
				finish();
			}
		});

		startPauseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				mapService.startMapping();
				stopButton.setVisibility(View.VISIBLE);
				startPauseButton.setVisibility(View.INVISIBLE);
				layoutStateIcon.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, MapService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mapService = binder.getService();
			mBound = true;
			mapService.setChangeLocationListener(MainActivity.this);
			mapService.setChangeTimeListener(MainActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
		}
	};

	private double calculateSpeed(List<Location> path){
		//DecimalFormat df = new DecimalFormat("0.0");
		double velMedia = 0;

		if(path.size() > 0 && path.size() % 4 == 0){
			Location x0 = path.get(path.size() - 4);
			Location x1 = path.get(path.size() - 3);
			Location x2 = path.get(path.size() - 2);
			Location x3 = path.get(path.size() - 1);

			double dist = x0.distanceTo(x1)/1000;
			double deltat = x1.getElapsedRealtimeNanos() - x0.getElapsedRealtimeNanos();
			deltat = ((deltat/1000000000)/60)/60;

			if(deltat > 0)
				velMedia += dist/deltat;

			dist = x1.distanceTo(x2)/1000;
			deltat = x2.getElapsedRealtimeNanos() - x1.getElapsedRealtimeNanos();
			deltat = ((deltat/1000000000)/60)/60;

			if(deltat > 0)
				velMedia += dist/deltat;

			dist = x2.distanceTo(x3)/1000;
			deltat = x3.getElapsedRealtimeNanos() - x2.getElapsedRealtimeNanos();
			deltat = ((deltat/1000000000)/60)/60;

			if(deltat > 0){
				velMedia += dist/deltat;
			}

			velMedia = velMedia / 3;

			//speedText.setText("" + df.format(velMedia) + "km/h");
		}
		return velMedia;
	}

	//Faz tudo o que é necessário para informar o usuário que tá na hora de trocar de exercicio,
	//ou seja, muda o ícone, tocar um alarme vibratório ou então volta para a SubgoalList.
	private void changeViewState(){
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		if(currState == END_STATE){
			mapService.pauseMapping();
			v.vibrate(VIBRATION_TIME_END);
			Intent intent = new Intent(MainActivity.this, Statistics.class);
			StatsDB statsDB = new StatsDB(this);
			statsDB.deleteStats(subgoal); //Mais rápido deletar do que fazer um update...
			statsDB.insertStats(subgoal, speedSeries);
			startActivityForResult(intent,STATISTICS_REQUEST);
		}else if(currState == WALKING_STATE){
			v.vibrate(VIBRATION_TIME_CHANGE);
			stateIcon.setImageResource(R.drawable.walkicon);
			partialKmText.setText(String.format("%.2f", partialDistanceWalking) + "km");
		}else if(currState == RUNNING_STATE){
			v.vibrate(VIBRATION_TIME_CHANGE);
			stateIcon.setImageResource(R.drawable.runicon);
			partialKmText.setText(String.format("%.2f", partialDistanceRunning) + "km");
		}
	}

	@Override
	public void onChangeLocation(List<Location> path) {
		double distance = 0;
		DecimalFormat df = new DecimalFormat("0.00"); 
		Location oldLoc = path.get(0);

		for(Location loc: path){
			distance = loc.distanceTo(oldLoc) + distance;
			oldLoc = loc;
		}

		double speed = calculateSpeed(path);

		distance = distance / 1000;

		//Coletando informações sobre a velocidade de corrida.
		if(currState == RUNNING_STATE){
			if(speed > 1) 
				speedSeries.add(distance, speed);
		}
		
		double partialDistance = distance - lastChangeKm;

		kmTotalProgress.setProgress((int)(Math.round(distance*1000)));
		kmPartialProgress.setProgress((int)(Math.round(partialDistance*1000)));
		partialKmText.setText(String.format("%.2f", partialDistanceWalking - partialDistance) + "km");

		//Verifica se a corrida acabou (atingiu o Goal KM, ou seja, a distância total) ou se já deu a distância
		//necessária para trocar de exercicio (corrida e caminhada)
		if(distance >= totalDistance){
		   currState = END_STATE;
		   changeViewState();
		}else if(currState == RUNNING_STATE && partialDistance >= this.partialDistanceRunning){
		   lastChangeKm = distance;
		   currState = WALKING_STATE;
		   changeViewState();
		}else if(currState == WALKING_STATE && partialDistance >= this.partialDistanceWalking){
		   lastChangeKm = distance;
		   currState = RUNNING_STATE;
		   changeViewState();
		}

		distanceValue.setText(df.format(distance) + "km");
	}
	
	@Override
	public void onChangeTime(long mili) {
		int secs = (int) (mili/1000);

		if(currState == RUNNING_STATE){
			runningTime = secs + runningTime;
		}

		int mins = secs/60;
		secs = secs % 60;
		int hours = mins/60;
		timerValue.setText("" + String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":"
				+ String.format("%02d", secs));		
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	@Override
	public void onChangeProviderState(int state) {
	}

	@Override
	public void onAcquiredGpsSignal() {
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == STATISTICS_REQUEST) {
	        if (resultCode == RESULT_OK) {
				Intent intent = new Intent();
				intent.putExtra("runningSpeed", runningSpeed/(double)runningSpeedSamples);
				intent.putExtra("runningTime", runningTime);
				setResult(RESULT_OK,intent);
				finish();
	        }
	    }
	}
}