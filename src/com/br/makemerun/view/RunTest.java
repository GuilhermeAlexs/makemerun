package com.br.makemerun.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import com.br.makemerun.R;
import com.br.makemerun.database.GoalDB;
import com.br.makemerun.model.ChangeTimeListener;
import com.br.makemerun.model.Goal;
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
	private int secondsElapsed;
	private List<Double> speedList;
	static Goal goal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_test);
		
		speedList = new ArrayList<Double>();
		
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
					int km = getIntent().getIntExtra("goal", 0);
					goal = new Goal(km, secondsElapsed, 0);
					goal.setCurrent(true);
					GoalDB db = new GoalDB(view.getContext());
					db.insertGoal(goal);
					Intent intent = new Intent(view.getContext(), SubgoalsList.class);
					intent.putExtra("avgSpeed", getAvgSpeed());
					intent.putExtra("getStdDeviation", getStandardDeviation());
					startActivity(intent);
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
			double speed = 0;
			
			Location x0 = path.get(path.size() - 4);
			Location x1 = path.get(path.size() - 3);
			Location x2 = path.get(path.size() - 2);
			Location x3 = path.get(path.size() - 1);
			
			double dist = x0.distanceTo(x1)/1000;
			double deltat = x1.getElapsedRealtimeNanos() - x0.getElapsedRealtimeNanos();
			deltat = ((deltat/1000000000)/60)/60;
			if(deltat > 0)
				speed += dist/deltat;
			
			dist = x1.distanceTo(x2)/1000;
			deltat = x2.getElapsedRealtimeNanos() - x1.getElapsedRealtimeNanos();
			deltat = ((deltat/1000000000)/60)/60;
			if(deltat > 0)
				speed += dist/deltat;
			
			dist = x2.distanceTo(x3)/1000;
			deltat = x3.getElapsedRealtimeNanos() - x2.getElapsedRealtimeNanos();
			deltat = ((deltat/1000000000)/60)/60;
			if(deltat > 0){
				speed += dist/deltat;
			}
			
			speed = speed / 3;
			speedList.add(speed);
			
			speedText.setText("" + df.format(speed) + "km/h");
		}

		distance = distance / 1000;
		
		distanceValue.setText(df.format(distance) + "km");
	}

	@Override
	public void onChangeTime(long mili) {
		int secs = (int) (mili/1000);
		secondsElapsed = secs;
		int mins = secs/60;
		secs = secs % 60;
		int hours = mins/60;
		timerValue.setText("" + String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":"
				+ String.format("%02d", secs));		
	}
	
	private double getAvgSpeed(){
		double avgSpeed = 0;
		
		Collections.sort(speedList, new Comparator<Double>() {
	        @Override
	        public int compare(Double  speed1, Double  speed2)
	        {
	        	if(speed1 > speed2){
	        		return 1;
	        	}
	        	else if(speed1 == speed2){
	        		return 0;
	        	}
        		return -1;
	        }

	    });
		
		if(speedList.size() % 2 == 0){
			avgSpeed = speedList.get(speedList.size()/2) + speedList.get(speedList.size()/2 - 1);
			avgSpeed = avgSpeed / 2;
		}
		else{
			avgSpeed = speedList.get((int) Math.floor(speedList.size()/2));
		}
		
		return avgSpeed;
	}
	
	private double getStandardDeviation(){
		Double avgSpeed = getAvgSpeed();
		Double sum = 0d;
		
		for(Double speed : speedList){
			sum += (avgSpeed - speed)*(avgSpeed - speed);
		}
		return Math.sqrt(sum/speedList.size());
	}
}
