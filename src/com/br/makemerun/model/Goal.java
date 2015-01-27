package com.br.makemerun.model;

public class Goal {
	private int id;
	private double km;
	private double kmBase;
	private double speedBase;
	private double speedDeviation;
	private int progress;
	private boolean isCurrent;

	public Goal(){
		
	}

	public Goal(double km, double kmBase, double speedBase, double speedDeviation, int progress) {
		this.km = km;
		this.kmBase = kmBase;
		this.speedBase = speedBase;
		this.speedDeviation = speedDeviation;
		this.progress = progress;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getKm() {
		return km;
	}
	public void setKm(double km) {
		this.km = km;
	}
	public double getKmBase() {
		return kmBase;
	}

	public void setKmBase(double kmBase) {
		this.kmBase = kmBase;
	}
	
	public double getSpeedBase() {
		return speedBase;
	}

	public void setSpeedBase(double speedBase) {
		this.speedBase = speedBase;
	}

	public double getSpeedDeviation() {
		return speedDeviation;
	}

	public void setSpeedDeviation(double speedDeviation) {
		this.speedDeviation = speedDeviation;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean isCurrent() {
		return isCurrent;
	}

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}
}
