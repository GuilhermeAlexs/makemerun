package com.br.makemerun.model;

public class Subgoal {
	private int id;
	private double kmTotalWalking;
	private double kmTotalRunning;
	private double kmPartialWalking;
	private double kmPartialRunning;
	private long totalTime;
	private long partialRunningTime;
	private long partialWalkingTime;
	private boolean completed;
	private boolean last;

	public Subgoal(){
		this.kmTotalWalking = 0;
		this.kmTotalRunning = 0;
	}

	public Subgoal(double kmTotalWalking, double kmTotalRunning) {
		this.kmTotalWalking = kmTotalWalking;
		this.kmTotalRunning = kmTotalRunning;
	}

	public Subgoal(int id, double kmTotalWalking, double kmTotalRunning) {
		this.kmTotalWalking = kmTotalWalking;
		this.kmTotalRunning = kmTotalRunning;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getKmTotalWalking() {
		return kmTotalWalking;
	}

	public void setKmTotalWalking(double kmTotalWalking) {
		this.kmTotalWalking = kmTotalWalking;
	}

	public double getKmTotalRunning() {
		return kmTotalRunning;
	}

	public void setKmTotalRunning(double kmTotalRunning) {
		this.kmTotalRunning = kmTotalRunning;
	}

	public double getKmPartialWalking() {
		return kmPartialWalking;
	}

	public void setKmPartialWalking(double kmPartialWalking) {
		this.kmPartialWalking = kmPartialWalking;
	}

	public double getKmPartialRunning() {
		return kmPartialRunning;
	}

	public void setKmPartialRunning(double kmPartialRunning) {
		this.kmPartialRunning = kmPartialRunning;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public long getPartialRunningTime() {
		return partialRunningTime;
	}

	public void setPartialRunningTime(long runningTime) {
		this.partialRunningTime = runningTime;
	}

	public long getPartialWalkingTime() {
		return partialWalkingTime;
	}

	public void setPartialWalkingTime(long walkingTime) {
		this.partialWalkingTime = walkingTime;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}
}
