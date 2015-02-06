package com.br.makemerun.model;

public class Subgoal {
	private double kmWalking;
	private double kmRunning;
	private double kmPartialWalking;
	private double kmPartialRunning;
	private int numberOfWalkings;
	private int numberOfRunnings;
	private boolean completed;

	public Subgoal(){
		this.kmWalking = 0;
		this.kmRunning = 0;
	}

	public Subgoal(double kmWalking, double kmRunning) {
		this.kmWalking = kmWalking;
		this.kmRunning = kmRunning;
	}

	public double getKmWalking() {
		return kmWalking;
	}

	public void setKmWalking(double kmWalking) {
		this.kmWalking = kmWalking;
	}

	public double getKmRunning() {
		return kmRunning;
	}

	public void setKmRunning(double kmRunning) {
		this.kmRunning = kmRunning;
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

	public int getNumberOfWalkings() {
		return numberOfWalkings;
	}

	public void setNumberOfWalkings(int numberOfWalkings) {
		this.numberOfWalkings = numberOfWalkings;
	}

	public int getNumberOfRunnings() {
		return numberOfRunnings;
	}

	public void setNumberOfRunnings(int numberOfRunnings) {
		this.numberOfRunnings = numberOfRunnings;
	}
}
