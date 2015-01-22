package com.br.makemerun.model;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.widget.TextView;

public class Timer{

	private TextView timerValue;
	private long startTime = 0L;
	private Handler customHandler = new Handler();
	
	long timeInMilliseconds = 0L;
	long timeBuffer = 0L;
	
	private Integer runningTime = 3;
	private Integer walkingTime = 6;
	private Boolean isRunning = true;
	private Boolean wasPaused = false;
	
	private Context context;
	
	public Timer(Context ctx, Integer runningTime, Integer walkingTime){
		context = ctx;
		this.runningTime = runningTime;
		this.walkingTime = walkingTime;
		start();
	}
	
	private Runnable updateTimerThread = new Runnable() {

		public void run() {
			
			if(!wasPaused){
				if(isRunning){
					timeInMilliseconds = runningTime*1000 - (SystemClock.uptimeMillis() - startTime);
				}
				else{
					timeInMilliseconds = walkingTime*1000 - (SystemClock.uptimeMillis() - startTime);
				}
			}
			else{
				timeInMilliseconds = timeBuffer - (SystemClock.uptimeMillis() - startTime);
			}
			
			if(timeInMilliseconds <= 0){
				isRunning = !isRunning;
				startTime = SystemClock.uptimeMillis();
				Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(500);
				wasPaused = false;
			}

			int secs = (int) (timeInMilliseconds / 1000);
			int mins = secs / 60;
			secs = secs % 60;
			int milliseconds = (int) (timeInMilliseconds % 1000);
			timerValue.setText("" + mins + ":"
					+ String.format("%02d", secs) + ":"
					+ String.format("%03d", milliseconds));
			customHandler.postDelayed(this, 0);
			
		}

	};
	
	public void start(){
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);
	}
	
	public void pause(){
		wasPaused = true;
		timeBuffer = timeInMilliseconds;
		customHandler.removeCallbacks(updateTimerThread);
	}

	public Integer getRunningTime() {
		return runningTime;
	}

	public void setRunningTime(Integer runningTime) {
		this.runningTime = runningTime;
	}

	public Integer getWalkingTime() {
		return walkingTime;
	}

	public void setWalkingTime(Integer walkingTime) {
		this.walkingTime = walkingTime;
	}
	
}
