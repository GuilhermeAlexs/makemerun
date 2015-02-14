package com.br.makemerun.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.achartengine.model.XYSeries;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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

public class StartRun extends Activity implements ChangeLocationListener, ChangeTimeListener{

	private TextView timerValue;
	//private TextView speedText;
	private TextView distanceValue;
	//private TextView runningKmText;
	private TextView partialKmText;
	private TextView startStopButton;
	private ImageView soundButton;
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

	private long startRun = 0;
	private long endRun = 0;
	private boolean runningStarted = false;
	private long runningTime = 0;
	private long totalTime = 0;

	private boolean started = false;

	private AlertDialog providerAlertDialog;
	private AlertDialog gpsSignalAlertDialog;

	private boolean soundOn = true;
	
	private double runningSpeedSum = 0;
	private int runningSpeedSamples = 0;
	private List<Double> runningSpeedList = new ArrayList<Double>();
	private List<Double> walkingSpeedList = new ArrayList<Double>();
	private XYSeries runningSpeedSeries;
	private XYSeries walkingSpeedSeries;
	private int mode_index = 1;

	private final int POST_RUN_REQUEST = 2;

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

		runningSpeedSeries = new XYSeries(getString(R.string.description_speed) + "running");
		walkingSpeedSeries = new XYSeries(getString(R.string.description_speed) + "walking");

		startStopButton = (TextView) findViewById(R.id.btnStartStop);
		timerValue = (TextView) findViewById(R.id.txTimerValue);
		distanceValue = (TextView) findViewById(R.id.txDistance);
		stateIcon = (ImageView) findViewById(R.id.icState);
		soundButton = (ImageView) findViewById(R.id.btnSound);
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

		startStopButton.setText(getString(R.string.button_start));

		startStopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(!started){
					if (canStart()) {
						started = true;
						mapService.startMapping();
						startStopButton.setText(getString(R.string.button_stop));
						if(soundOn)
							Splash.voice.speak(getString(R.string.voice_start_running), TextToSpeech.QUEUE_FLUSH, null);
					} else {
						gpsSignalAlertDialog.show();
					}
				}else{
					AlertDialog.Builder builder = new AlertDialog.Builder(StartRun.this);

				    builder.setTitle(StartRun.this.getString(R.string.title_quit));
				    builder.setMessage(StartRun.this.getString(R.string.description_are_you_sure));

				    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) {
							started = false;
							mapService.stopMapping();
							Intent intent = new Intent();
							setResult(RESULT_CANCELED,intent);
							finish();
				        }
				    });

				    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				        @Override
				        public void onClick(DialogInterface dialog, int which) {
				            dialog.dismiss();
				        }
				    });

				    AlertDialog alert = builder.create();
				    alert.show();
				}
			}
		});

		soundButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(soundOn){
					soundButton.setImageResource(R.drawable.voloff);
					soundOn = false;
				}else{
					soundButton.setImageResource(R.drawable.volon);
					soundOn = true;
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
			mapService.setChangeLocationListener(StartRun.this);
			mapService.setChangeTimeListener(StartRun.this);
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
			mapService.stopMapping();
			v.vibrate(VIBRATION_TIME_END);

			if(soundOn)
				Splash.voice.speak(getString(R.string.voice_end_training), TextToSpeech.QUEUE_FLUSH, null);

			Intent intent = new Intent(StartRun.this, PostRun.class);
            intent.putExtra("subgoal",  subgoal);
            intent.putExtra("totalDistance", totalDistance);
            intent.putExtra("totalDistanceWalking", totalDistanceWalking);
            intent.putExtra("totalDistanceRunning", totalDistanceRunning);
            intent.putExtra("partialDistanceWalking", partialDistanceWalking);
            intent.putExtra("partialDistanceRunning", partialDistanceRunning);
            intent.putExtra("time", this.timerValue.getText());
			StatsDB statsDB = new StatsDB(this);
			statsDB.deleteStats(subgoal); //Mais rápido deletar do que fazer um update...
			statsDB.insertStats(subgoal, StatsDB.RUNNING_SPRINT, runningSpeedSeries);
			statsDB.insertStats(subgoal, StatsDB.WALKING_SPRINT, walkingSpeedSeries);
			startActivityForResult(intent,POST_RUN_REQUEST);
		}else if(currState == WALKING_STATE){
			v.vibrate(VIBRATION_TIME_CHANGE);

			if(soundOn)
				Splash.voice.speak(getString(R.string.voice_start_walking), TextToSpeech.QUEUE_FLUSH, null);

			stateIcon.setImageResource(R.drawable.walkicon);
			partialKmText.setText(String.format("%.2f", partialDistanceWalking) + "km");
		}else if(currState == RUNNING_STATE){
			v.vibrate(VIBRATION_TIME_CHANGE);

			if(soundOn)
				Splash.voice.speak(getString(R.string.voice_start_running), TextToSpeech.QUEUE_FLUSH, null);

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

		double partialDistance = distance - lastChangeKm;
		double speedAvg;

		kmTotalProgress.setProgress((int)(Math.round(distance*1000)));
		kmPartialProgress.setProgress((int)(Math.round(partialDistance*1000)));

		//Coletando informações sobre a velocidade de corrida e printando elementos da tela
		if(currState == RUNNING_STATE){
			if(speed > 1)
				runningSpeedList.add(speed);
			partialKmText.setText(String.format("%.2f", partialDistanceRunning - partialDistance) + "km");
		}else if(currState == WALKING_STATE){
			if(speed > 1)
				walkingSpeedList.add(speed);
			partialKmText.setText(String.format("%.2f", partialDistanceWalking - partialDistance) + "km");
		}

		//Verifica se a corrida acabou (atingiu o Goal KM, ou seja, a distância total) ou se já deu a distância
		//necessária para trocar de exercicio (corrida e caminhada)
		if(distance >= totalDistance){
		   if(currState == RUNNING_STATE){
			   speedAvg = getAvgSpeed(runningSpeedList);
			   runningSpeedSeries.add(mode_index, speedAvg);
			   runningSpeedSum = runningSpeedSum + speedAvg;
			   runningSpeedSamples++;
		   }else{
			   speedAvg = getAvgSpeed(walkingSpeedList);
			   walkingSpeedSeries.add(mode_index, speedAvg);
		   }

		   currState = END_STATE;
		   changeViewState();
		}else if(currState == RUNNING_STATE && partialDistance >= this.partialDistanceRunning){
		   speedAvg = getAvgSpeed(runningSpeedList);
		   runningSpeedSeries.add(mode_index, speedAvg);
		   runningSpeedSum = runningSpeedSum + speedAvg;
		   runningSpeedSamples++;
		   runningSpeedList.clear();
		   mode_index++;
		   lastChangeKm = distance;
		   currState = WALKING_STATE;
		   kmPartialProgress.setMax((int)(Math.round(partialDistanceWalking*1000)));
		   changeViewState();
		}else if(currState == WALKING_STATE && partialDistance >= this.partialDistanceWalking){
		   speedAvg = getAvgSpeed(walkingSpeedList);
		   walkingSpeedSeries.add(mode_index, speedAvg);
		   walkingSpeedList.clear();
		   mode_index++;
		   lastChangeKm = distance;
		   currState = RUNNING_STATE;
		   kmPartialProgress.setMax((int)(Math.round(partialDistanceRunning*1000)));
		   changeViewState();
		}

		distanceValue.setText(df.format(distance) + "km");
	}

	private double getAvgSpeed(List<Double> speedList) {
		double avgSpeed = 0;

		Collections.sort(speedList, new Comparator<Double>() {
			@Override
			public int compare(Double speed1, Double speed2) {
				if (speed1 > speed2) {
					return 1;
				} else if (speed1 == speed2) {
					return 0;
				}
				return -1;
			}
		});

		if (speedList.size() == 0) {
			return 0;
		} else if (speedList.size() % 2 == 0) {
			avgSpeed = speedList.get(speedList.size() / 2)
					+ speedList.get((speedList.size() / 2) - 1);
			avgSpeed = avgSpeed / 2;
		} else {
			avgSpeed = speedList.get((int) Math.floor(speedList.size() / 2));
		}

		return avgSpeed;
	}
	
	@Override
	public void onChangeTime(long mili) {
		int secs = (int) (mili/1000);

		totalTime = secs;

		//Coleta o tempo de corrida
		if(!runningStarted && currState == RUNNING_STATE){
			startRun = secs;
			runningStarted = true;
		}else{
			//Verifica se o começo da corrida já foi marcado, mas o fim não.
			//Ou seja, se a corrida terminou, ele anexa o tempo dela.
			if(runningStarted  && currState != RUNNING_STATE){
				endRun = secs;
				runningTime = (endRun - startRun) + runningTime;
				runningStarted = false;
			}
		}

		int mins = secs/60;
		secs = secs % 60;
		int hours = mins/60;
		timerValue.setText("" + String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":"
				+ String.format("%02d", secs));		
	}

	@Override
	public void onBackPressed() {	
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

	    builder.setTitle(this.getString(R.string.title_quit));
	    builder.setMessage(this.getString(R.string.description_are_you_sure));

	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	mapService.stopMapping();
	    		Intent intent = new Intent();
	    		setResult(RESULT_CANCELED, intent);
	    		finish();
	        }
	    });

	    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
	        }
	    });

	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	private void setProviderPopup() {
		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(getString(R.string.title_gps_off));
		alertDialogBuilder.setMessage(R.string.description_turn_on);
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
		alertDialogBuilder.setTitle(getString(R.string.title_looking_satellites));
		alertDialogBuilder.setMessage(R.string.description_almost_there);
		alertDialogBuilder.setCancelable(true);
		gpsSignalAlertDialog = alertDialogBuilder.create();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == POST_RUN_REQUEST) {
	        if (resultCode == RESULT_OK) {
				Intent intent = new Intent();
				intent.putExtra("runningSpeed", (this.runningSpeedSum/(double)this.runningSpeedSamples));
				intent.putExtra("runningTime", runningTime);
				intent.putExtra("totalTime", totalTime);

				setResult(RESULT_OK,intent);
				finish();
	        }
	    }
	}
}