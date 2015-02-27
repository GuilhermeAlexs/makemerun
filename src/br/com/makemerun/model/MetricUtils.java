package br.com.makemerun.model;

public class MetricUtils {
	public static double convertToPace(double speed){
		return (1/speed)*60;
	}
	
	public static double convertPaceToSpeed(int min, int sec){
		double deltaT = (min*60 + sec)/(double)3600;
		return 1/deltaT;
	}

	public static double convertPaceToSpeed(String text){
		String [] values = text.split(":");
		return MetricUtils.convertPaceToSpeed(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
	}

	public static String formatTime(long secs){
		int mins = (int) (secs / 60);
		secs = secs % 60;
		return "" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
	}
}
