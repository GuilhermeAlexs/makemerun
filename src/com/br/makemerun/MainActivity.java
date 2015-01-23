package com.br.makemerun;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.br.makemerun.model.ChangeExerciseListener;
import com.br.makemerun.model.ChangeTimeListener;
import com.br.makemerun.service.ChangeLocationListener;
import com.br.makemerun.service.MapService;
import com.br.makemerun.service.MapService.LocalBinder;

public class MainActivity extends Activity implements ChangeLocationListener, ChangeTimeListener, ChangeExerciseListener{

	private Button startPauseButton;
	private TextView timerValue;
	private TextView speedText;
	private TextView distanceValue;
	private TextView stateText;
	private ImageView stateIcon;
	public Boolean isStart = true;
	private boolean mBound = false;
	private MapService mapService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startPauseButton = (Button) findViewById(R.id.startButton);
		timerValue = (TextView) findViewById(R.id.txTimerValue);
		distanceValue = (TextView) findViewById(R.id.txDistance);
		stateText = (TextView) findViewById(R.id.txState);
		stateIcon = (ImageView) findViewById(R.id.icState);
		speedText = (TextView) findViewById(R.id.txSpeed);

		startPauseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(isStart){
					mapService.startMapping();
					startPauseButton.setBackgroundResource(R.drawable.pause);
				}
				else{
					mapService.pauseMapping();
					startPauseButton.setBackgroundResource(R.drawable.play);
				}
				isStart = !isStart;
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
			mapService.setChangeExerciseListener(MainActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
		}
	};

	@Override
	public void onChangeLocation(List<Location> path) {
		double distance = 0;
		DecimalFormat df = new DecimalFormat("0.00"); 
		Location oldLoc = path.get(0);

		for(Location loc: path){
			distance = loc.distanceTo(oldLoc) + distance;
			oldLoc = loc;
		}

		if(path.size() >= 2){
			Location x0 = path.get(path.size() - 2);
			Location x1 = path.get(path.size() - 1);
			
			double dist = x0.distanceTo(x1)/1000;
			double deltat = x1.getElapsedRealtimeNanos() - x0.getElapsedRealtimeNanos();
			deltat = ((deltat/1000000000)/60)/60;

			if(deltat > 0)
				speedText.setText("" + df.format(dist/deltat) + "km/h");
		}

		distance = distance / 1000;
		
		distanceValue.setText(df.format(distance) + "km");
	}

	@Override
	public void onChangeTime(long mili) {
		int secs = (int) (mili/1000);
		int mins = secs/60;
		secs = secs % 60;
		int hours = mins/60;
		timerValue.setText("" + String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":"
				+ String.format("%02d", secs));		
	}

	@Override
	public void onChangeExercise(boolean isRunning) {
		if(isRunning){
			stateIcon.setImageResource(R.drawable.runicon);
			stateText.setText("Running");
		}else{
			stateIcon.setImageResource(R.drawable.walkicon);
			stateText.setText("Walking");
		}
	}
}
