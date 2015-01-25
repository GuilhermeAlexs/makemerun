package com.br.makemerun.view;

import com.br.makemerun.R;
import com.br.makemerun.model.Subgoal;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
 
public class SubgoalsArrayAdapter extends ArrayAdapter<Subgoal> {
	private final Context context;
	private final Subgoal[] values;
 
	public SubgoalsArrayAdapter(Context context, Subgoal[] values) {
		super(context, R.layout.subgoals_item, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.subgoals_item, parent, false);
		TextView walkingText = (TextView) rowView.findViewById(R.id.txWalking);
		TextView runningText = (TextView) rowView.findViewById(R.id.txRunning);
		
		int secs = (int)(values[position].getTimeWalking() % 60);
		int mins = (int) ((values[position].getTimeWalking()/60) % 60);
		int hours = (int) ((values[position].getTimeWalking()/3600) % 24);

		String timeWalking = "" + String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":"
				+ String.format("%02d", secs);
		walkingText.setText(timeWalking);
		
		secs = (int)(values[position].getTimeRunning() % 60);
		mins = (int) ((values[position].getTimeRunning()/60) % 60);
		hours = (int) ((values[position].getTimeRunning()/3600) % 24);

		String timeRunning = "" + String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":"
				+ String.format("%02d", secs);
		runningText.setText(timeRunning);

		if(position%2 == 0){
			rowView.setBackgroundColor(Color.rgb(30, 30, 30));
		}
		return rowView;
	}
}