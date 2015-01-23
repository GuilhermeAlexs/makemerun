package com.br.makemerun.model;

import java.util.LinkedList;
import java.util.List;

import android.location.Location;

public class Map {
	private List<Location> path;

	public Map(){
		this.path = new LinkedList<Location>();
	}

	public void addPoint(Location location){
		path.add(location);
	}

	public List<Location> getPath(){
		return this.path;
	}
	
	public double getLength(){
		double distance = 0;
		Location oldLoc = path.get(0);

		for(Location loc: path){
			distance = loc.distanceTo(oldLoc) + distance;
			oldLoc = loc;
		}
		
		return distance;
	}
}
