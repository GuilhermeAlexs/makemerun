package br.com.makemerun.view;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.makemerun.R;
import br.com.makemerun.database.GoalDB;
import br.com.makemerun.model.Goal;
import br.com.makemerun.service.MapService;
import br.com.makemerun.service.MapService.LocalBinder;
import br.com.makemerun.view.widgets.CircularProgressBar;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

public class Splash extends Activity {
	private CircularProgressBar progressLoading;
	private TextView txLoading;
	private MapService mapService;
	public static TextToSpeech voice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		voice = new TextToSpeech(getApplicationContext(), 
			      new TextToSpeech.OnInitListener() {
			      @Override
			      public void onInit(int status) {
			         if(status != TextToSpeech.ERROR){
			             voice.setLanguage(getResources().getConfiguration().locale);
			             voice.playSilence(2, TextToSpeech.QUEUE_FLUSH, null);
			            }				
			         }
			      });
		progressLoading = (CircularProgressBar) this.findViewById(R.id.progressLoading);
		progressLoading.setMax(4);
		progressLoading.setProgress(0);

		List<Double> speedList = new ArrayList<Double>();
		speedList.add((double)60);
		speedList.add((double)0);
		speedList.add((double)10);
		speedList.add((double)11);
		speedList.add((double)10);
		speedList.add((double)12);
		speedList.add((double)14);
		speedList.add((double)10);
		speedList.add((double)90);
		speedList.add((double)12);
		Log.d("Splash", "speed = " + getAvgSpeed(speedList));
		txLoading = (TextView) this.findViewById(R.id.txLoading);
		new LoadingTask().execute();
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
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mapService = binder.getService();
			//mapService.setChangeLocationListener(Splash.this);
			mapService.startGPS();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	@SuppressWarnings("rawtypes")
	class LoadingTask extends AsyncTask<Void, String, Class>{
		int progress = 0;

		@Override 
		protected Class doInBackground(Void... params) {
			Class nextView = null;
			try {
				GoalDB db = new GoalDB(getApplicationContext());

				publishProgress(getString(R.string.loading_current_goal));

				Goal goal = db.getCurrentGoal();

				if(goal != null)
					nextView = SubgoalsList.class;
				else
					nextView = ChooseGoal.class;

				Thread.sleep(1000);
				publishProgress(getString(R.string.loading_voice_system));
				Thread.sleep(800);
				publishProgress(getString(R.string.loading_first_fix));
				Thread.sleep(500);
				publishProgress(getString(R.string.loading_first_fix));
				Thread.sleep(600);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return nextView;
		}

		@Override 
		protected void onProgressUpdate(String... values) {
			progress++;
			progressLoading.setProgress(progress);
			txLoading.setText(values[0]);
		}
		
		@Override
	    protected void onPostExecute(Class result) {
	       super.onPostExecute(result);
	       Intent intent = new Intent(Splash.this, result);
	       startActivity(intent);
	    }
	}
}


