package com.br.makemerun.model;


import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;

public class Timer{
	private String timerValue;
	private long startTime = 0L;
	private Handler customHandler = new Handler();
	private long timeInMilliseconds = 0L;
	private long timeBuffer = 0L;
	
	private Integer runningTime = 3;
	private Integer walkingTime = 6;
	private Boolean isRunning = false;
	private Boolean wasPaused = false;

	private Context context;
	
	private ChangeTimeListener timeListener;
	private ChangeExerciseListener exerciseListener;
	
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

				if(exerciseListener != null)
					exerciseListener.onChangeExercise(isRunning);
			}

			if(timeListener != null)
				timeListener.onChangeTime(timeInMilliseconds);

			customHandler.postDelayed(this, 900);
			
		}

	};
	
	public void start(){
		if(exerciseListener != null)
			exerciseListener.onChangeExercise(isRunning);
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 900);
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
	
	public void setChangeExerciseListener(ChangeExerciseListener listener){
		this.exerciseListener = listener;
	}
}
