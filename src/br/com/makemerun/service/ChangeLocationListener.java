package br.com.makemerun.service;

import java.util.List;

import android.location.Location;

public interface ChangeLocationListener {
	public void onChangeLocation(List<Location> path);
	public void onChangeProviderState(int state);
	public void onAcquiredGpsSignal();
}
