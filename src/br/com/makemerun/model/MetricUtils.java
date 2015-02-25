package br.com.makemerun.model;

public class MetricUtils {
	public static double convertToPace(double speed){
		return (1/speed)*60;
	}
	public static String formatTime(long secs){
		int mins = (int) (secs / 60);
		secs = secs % 60;
		return "" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
	}
}
