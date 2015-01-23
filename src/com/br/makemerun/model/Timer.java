package com.br.makemerun.model;

import com.br.makemerun.service.ChangeLocationListener;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;

public class Timer{

	private String timerValue;
	private long startTime = 0L;
	private Handler customHandler = new Handler();
	private ChangeTimeListener timeListener;
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

			if(timeListener != null)
				timeListener.onChangeTime(timeInMilliseconds);

			customHandler.postDelayed(this, 1000);
			
		}

	};
	
	public void start(){
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 1000);
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

	public String getTimerValue() {
		return timerValue;
	}

	public void setTimerValue(String timerValue) {
		this.timerValue = timerValue;
	}
	
	public void setChangeTimeListener(ChangeTimeListener listener){
		this.timeListener = listener;
	}
	
}
