package com.br.makemerun.model;

public class Subgoal {
	private int timeWalking;
	private int timeRunning;

	public Subgoal(){
		this.timeWalking = 0;
		this.timeRunning = 0;
	}

	public Subgoal(int timeWalking, int timeRunning) {
		this.timeWalking = timeWalking;
		this.timeRunning = timeRunning;
	}

	public int getTimeWalking() {
		return timeWalking;
	}

	public void setTimeWalking(int timeWalking) {
		this.timeWalking = timeWalking;
	}

	public int getTimeRunning() {
		return timeRunning;
	}

	public void setTimeRunning(int timeRunning) {
		this.timeRunning = timeRunning;
	}
}
