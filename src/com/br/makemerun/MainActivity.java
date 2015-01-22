package com.br.makemerun;

import java.util.List;

import com.br.makemerun.model.ChangeTimeListener;
import com.br.makemerun.model.Timer;
import com.br.makemerun.service.ChangeLocationListener;
import com.br.makemerun.service.MapService;
import com.br.makemerun.service.MapService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements ChangeLocationListener, ChangeTimeListener{

	private Button startPauseButton;
	private TextView timerValue;
	public Boolean isStart = true;
	private boolean mBound = false;
	private MapService mapService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startPauseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(isStart){
					
				}
				else{
					
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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

	@Override
	public void onChangeLocation(List<Location> path) {
		//	
	}

	@Override
	public void onChangeTime(long mili) {
		int secs = (int) (mili/1000);
		int mins = secs/60;
		secs = secs % 60;
		int hours = mins/60;
		timerValue.setText("" + hours + ":" + mins + ":"
				+ String.format("%02d", secs));		
	}

}
