package com.br.makemerun.view;

import java.text.DecimalFormat;
import java.util.List;

import org.achartengine.model.XYSeries;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.br.makemerun.R;
import com.br.makemerun.database.StatsDB;
import com.br.makemerun.model.ChangeTimeListener;
import com.br.makemerun.service.ChangeLocationListener;
import com.br.makemerun.service.MapService;
import com.br.makemerun.service.MapService.LocalBinder;
import com.br.makemerun.view.widgets.CircularProgressBar;

public class MainActivity extends Activity implements ChangeLocationListener, ChangeTimeListener{

	private TextView timerValue;
	//private TextView speedText;
	private TextView distanceValue;
	//private TextView runningKmText;
	private TextView partialKmText;
	private TextView startStopButton;
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
	private double totalDistanceRunning;
	private double totalDistanceWalking;
	private double partialDistanceRunning;
	private double partialDistanceWalking;
	private double lastChangeKm = 0;

	private double currState = RUNNING_STATE;

	private double runningSpeed = 0;
	private double runningSpeedSamples = 0;

	private long startRun = 0;
	private long endRun = 0;
	private long runningTime = 0;
	private long totalTime = 0;

	private final int POST_RUN_REQUEST = 2;
	private XYSeries speedSeries;

	private boolean started = false;
	
	private AlertDialog providerAlertDialog;
	private AlertDialog gpsSignalAlertDialog;

	private final String VOICE_RUNNING = "Start Running!";
	private final String VOICE_WALKING = "Start Walking!";
	private final String VOICE_END_WORKOUT = "Nice Job! See you in next training day.";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_run);
		
		Bundle bundle = this.getIntent().getExtras();

		subgoal = bundle.getInt("subgoal");
		totalDistance = bundle.getDouble("totalDistance");
		totalDistanceRunning = bundle.getDouble("totalDistanceRunning");
		totalDistanceWalking = bundle.getDouble("totalDistanceWalking");
		partialDistanceRunning = bundle.getDouble("partialDistanceRunning");
		partialDistanceWalking = bundle.getDouble("partialDistanceWalking");

		speedSeries = new XYSeries("Speed");

		startStopButton = (TextView) findViewById(R.id.btnStartStop);
		timerValue = (TextView) findViewById(R.id.txTimerValue);
		distanceValue = (TextView) findViewById(R.id.txDistance);
		stateIcon = (ImageView) findViewById(R.id.icState);
		//speedText = (TextView) findViewById(R.id.txSpeed);
		//runningKmText = (TextView) findViewById(R.id.txRunningKm);
		partialKmText = (TextView) findViewById(R.id.txPartial);
		kmTotalProgress = (CircularProgressBar) findViewById(R.id.progressKmTotal);
		kmPartialProgress = (CircularProgressBar) findViewById(R.id.progressKmPartial);
		stateIcon.setImageResource(R.drawable.runicon);
		partialKmText.setText(String.format("%.2f", partialDistanceRunning) + "km");

		kmTotalProgress.setMax((int)(Math.round(totalDistance*1000)));
		kmPartialProgress.setMax((int)(Math.round(partialDistanceRunning*1000)));

		setProviderPopup();
		setGpsSignalPopup();

		startStopButton.setText("Start");
		
		startStopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(!started){
					if (canStart()) {
						started = true;
						mapService.startMapping();
						startStopButton.setText("Stop");
					} else {
						gpsSignalAlertDialog.show();
					}
				}else{
					started = false;
					mapService.pauseMapping();
					Intent intent = new Intent();
					setResult(RESULT_CANCELED,intent);
					finish();
				}
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

	@Override
	public void onChangeProviderState(int state) {
		if (state == MapService.PROVIDER_DISABLED) {
			mapService.stopGPS();
			if (gpsSignalAlertDialog.isShowing()) {
				gpsSignalAlertDialog.hide();
			}
			providerAlertDialog.show();
		} else {
			if (providerAlertDialog.isShowing()) {
				providerAlertDialog.hide();
				mapService.startGPS();
			}
		}
	}

	@Override
	public void onAcquiredGpsSignal() {
		if (gpsSignalAlertDialog.isShowing()) {
			gpsSignalAlertDialog.hide();
		}
	}

	private boolean canStart() {
		if (mapService.hasGpsSignal()) {
			return true;
		}
		return false;
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mapService = binder.getService();
			mBound = true;
			mapService.setChangeLocationListener(MainActivity.this);
			mapService.setChangeTimeListener(MainActivity.this);
			if (!mapService.isProviderEnabled()) {
				if (gpsSignalAlertDialog.isShowing()) {
					gpsSignalAlertDialog.hide();
				}
				providerAlertDialog.show();
			}
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
			
			Splash.voice.speak(VOICE_END_WORKOUT, TextToSpeech.QUEUE_FLUSH, null);
			
			Intent intent = new Intent(MainActivity.this, PostRun.class);
            intent.putExtra("subgoal",  subgoal);
            intent.putExtra("totalDistance", totalDistance);
            intent.putExtra("totalDistanceWalking", totalDistanceWalking);
            intent.putExtra("totalDistanceRunning", totalDistanceRunning);
            intent.putExtra("partialDistanceWalking", partialDistanceWalking);
            intent.putExtra("partialDistanceRunning", partialDistanceRunning);
            intent.putExtra("time", this.timerValue.getText());
			StatsDB statsDB = new StatsDB(this);
			statsDB.deleteStats(subgoal); //Mais rápido deletar do que fazer um update...
			statsDB.insertStats(subgoal, speedSeries);
			startActivityForResult(intent,POST_RUN_REQUEST);
		}else if(currState == WALKING_STATE){
			v.vibrate(VIBRATION_TIME_CHANGE);
			
			Splash.voice.speak(VOICE_WALKING, TextToSpeech.QUEUE_FLUSH, null);
			
			stateIcon.setImageResource(R.drawable.walkicon);
			partialKmText.setText(String.format("%.2f", partialDistanceWalking) + "km");
		}else if(currState == RUNNING_STATE){
			v.vibrate(VIBRATION_TIME_CHANGE);
			
			Splash.voice.speak(VOICE_RUNNING, TextToSpeech.QUEUE_FLUSH, null);

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
			if(speed > 1) {
				runningSpeed = speed + runningSpeed;
				runningSpeedSamples++;
				speedSeries.add(distance, speed);
			}
		}
		
		double partialDistance = distance - lastChangeKm;

		kmTotalProgress.setProgress((int)(Math.round(distance*1000)));
		kmPartialProgress.setProgress((int)(Math.round(partialDistance*1000)));
		
		if(currState == RUNNING_STATE)
			partialKmText.setText(String.format("%.2f", partialDistanceRunning - partialDistance) + "km");
		else if(currState == WALKING_STATE)
			partialKmText.setText(String.format("%.2f", partialDistanceWalking - partialDistance) + "km");

		//Verifica se a corrida acabou (atingiu o Goal KM, ou seja, a distância total) ou se já deu a distância
		//necessária para trocar de exercicio (corrida e caminhada)
		if(distance >= totalDistance){
		   currState = END_STATE;
		   changeViewState();
		}else if(currState == RUNNING_STATE && partialDistance >= this.partialDistanceRunning){
		   lastChangeKm = distance;
		   currState = WALKING_STATE;
		   kmPartialProgress.setMax((int)(Math.round(partialDistanceWalking*1000)));
		   changeViewState();
		}else if(currState == WALKING_STATE && partialDistance >= this.partialDistanceWalking){
		   lastChangeKm = distance;
		   currState = RUNNING_STATE;
		   kmPartialProgress.setMax((int)(Math.round(partialDistanceRunning*1000)));
		   changeViewState();
		}

		distanceValue.setText(df.format(distance) + "km");
	}
	
	@Override
	public void onChangeTime(long mili) {
		int secs = (int) (mili/1000);

		totalTime = secs;

		if(currState == RUNNING_STATE){
			startRun = secs;
		}else{
			endRun = secs;
			runningTime = (endRun - startRun) + runningTime;
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

	private void setProviderPopup() {
		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("GPS locations is off.");
		alertDialogBuilder.setMessage("Turn it back on");
		alertDialogBuilder.setCancelable(true);
		Button tryAgain = new Button(this);
		tryAgain.setText("I'm ready");
		tryAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mapService != null && mapService.isProviderEnabled()) {
					providerAlertDialog.hide();
					mapService.startGPS();
				}
			}
		});
		alertDialogBuilder.setView(tryAgain);
		providerAlertDialog = alertDialogBuilder.create();
	}

	private void setGpsSignalPopup() {
		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Looking for available satellites.");
		alertDialogBuilder.setMessage("Almost there...");
		alertDialogBuilder.setCancelable(true);
		gpsSignalAlertDialog = alertDialogBuilder.create();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == POST_RUN_REQUEST) {
	        if (resultCode == RESULT_OK) {
				Intent intent = new Intent();
				intent.putExtra("runningSpeed", runningSpeed/(double)runningSpeedSamples);
				intent.putExtra("runningTime", runningTime);
				intent.putExtra("totalTime", totalTime);

				setResult(RESULT_OK,intent);
				finish();
	        }
	    }
	}
}