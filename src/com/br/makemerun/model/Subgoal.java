package com.br.makemerun.model;

public class Subgoal {
	private double kmWalking;
	private double kmRunning;

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
}
