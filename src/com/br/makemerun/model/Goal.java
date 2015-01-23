package com.br.makemerun.model;

public class Goal {
	private int id;
	private int goal;
	private int timeBase;
	private int progress;
	private boolean isCurrent;

	public Goal(){
		
	}

	public Goal(int id, int goal, int timeBase, int progress) {
		this.id = id;
		this.goal = goal;
		this.timeBase = timeBase;
		this.progress = progress;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGoal() {
		return goal;
	}
	public void setGoal(int goal) {
		this.goal = goal;
	}
	public int getTimeBase() {
		return timeBase;
	}
	public void setTimeBase(int timeBase) {
		this.timeBase = timeBase;
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
