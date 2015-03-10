package br.com.makemerun.view;


import java.util.Map;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

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
import android.widget.TextView;

public class Splash extends Activity {
	private CircularProgressBar progressLoading;
	private TextView txLoading;
	private MapService mapService;
	public static TextToSpeech voice;
	private Tracker t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		overridePendingTransition(R.drawable.activity_in, R.drawable.activity_out);

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

		txLoading = (TextView) this.findViewById(R.id.txLoading);
		new LoadingTask().execute();

		startSession();
	}

	private void startSession(){
		new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
        		t = ((AppTracker) getApplication()).getTracker(AppTracker.TrackerName.APP_TRACKER);
	       	    Map<String, String> hit = new HitBuilders.ScreenViewBuilder().build();
	       	    hit.put("&sc", "start");
	       	    t.send(hit);
            }
        }).start();
	}

	private void stopSession(){
	   new Thread(new Runnable() 
       {
           @Override
           public void run() 
           {
	       		t = ((AppTracker) getApplication()).getTracker(AppTracker.TrackerName.APP_TRACKER);
	       	    Map<String, String> hit = new HitBuilders.ScreenViewBuilder().build();
	       	    hit.put("&sc", "end");
	       	    t.send(hit);
           }
       }).start();
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
	protected void onDestroy(){
		super.onDestroy();
		stopSession();
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


