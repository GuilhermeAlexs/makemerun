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

import com.br.makemerun.model.ChangeTimeListener;
import com.br.makemerun.service.ChangeLocationListener;
import com.br.makemerun.service.MapService;
import com.br.makemerun.service.MapService.LocalBinder;

public class RunTest extends Activity implements ChangeLocationListener, ChangeTimeListener{

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
		setContentView(R.layout.activity_run_test);
		
		startPauseButton = (Button) findViewById(R.id.startButton);
		timerValue = (TextView) findViewById(R.id.txTimerValue);
		distanceValue = (TextView) findViewById(R.id.txDistance);
		speedText = (TextView) findViewById(R.id.txSpeed);
		stateText = (TextView) findViewById(R.id.txState);
		stateIcon = (ImageView) findViewById(R.id.icState);

		
		startPauseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(isStart){
					stateText.setText("Running");
					stateIcon.setBackgroundResource(R.drawable.runicon);
					mapService.startTestMapping();
					startPauseButton.setBackgroundResource(R.drawable.stop);
				}
				else{
					mapService.stopTestMapping();
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
			mapService.setChangeLocationListener(RunTest.this);
			mapService.setChangeTimeListener(RunTest.this);
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

		if(path.size() > 0 && path.size() % 4 == 0){
			double velMedia = 0;
			
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
			
			speedText.setText("" + df.format(velMedia) + "km/h");
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

}
