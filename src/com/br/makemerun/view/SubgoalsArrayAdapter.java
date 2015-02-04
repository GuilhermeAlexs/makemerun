package com.br.makemerun.view;

import com.br.makemerun.R;
import com.br.makemerun.model.Subgoal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class SubgoalsArrayAdapter extends ArrayAdapter<Subgoal> {
	private final Context context;
	private final Subgoal[] values;

	public SubgoalsArrayAdapter(Context context, Subgoal[] values) {
		super(context, R.layout.subgoals_item, values);
		this.context = context;
		this.values = values;
	}
 
	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.subgoals_item, parent, false);
		TextView walkingText = (TextView) rowView.findViewById(R.id.txWalking);
		TextView runningText = (TextView) rowView.findViewById(R.id.txRunning);
		TextView walkingPartialText = (TextView) rowView.findViewById(R.id.txPartialWalking);
		TextView runningPartialText = (TextView) rowView.findViewById(R.id.txPartialRunning);

		if(values[position].getKmWalking() < 10)
			walkingText.setText(String.format("%.2f", values[position].getKmWalking()) + "km");
		else
			walkingText.setText(String.format("%.1f", values[position].getKmWalking()) + "km");

		if(values[position].getKmRunning() < 10)
			runningText.setText(String.format("%.2f", values[position].getKmRunning()) + "km");
		else
			runningText.setText(String.format("%.1f", values[position].getKmRunning()) + "km");

		walkingPartialText.setText("" + String.format("%.2f", values[position].getKmPartialWalking()) + "km");
		runningPartialText.setText("" + String.format("%.2f", values[position].getKmPartialRunning()) + "km");

		if(values[position].isCompleted()){
			ImageView walkingIcon = (ImageView) rowView.findViewById(R.id.iconWalking);
			ImageView runningIcon = (ImageView) rowView.findViewById(R.id.iconRunning);
			walkingText.setTextColor(Color.DKGRAY);
			runningText.setTextColor(Color.DKGRAY);
			walkingPartialText.setTextColor(Color.DKGRAY);
			runningPartialText.setTextColor(Color.DKGRAY);
			walkingIcon.setImageResource(R.drawable.walkicon_off);
			runningIcon.setImageResource(R.drawable.runicon_off);
		}

		if(position % 2 == 0){
			int back = Color.rgb(40, 40, 40);
			rowView.setBackgroundColor(back);
			walkingText.setBackgroundColor(back);
			runningText.setBackgroundColor(back);
			walkingPartialText.setBackgroundColor(back);
			runningPartialText.setBackgroundColor(back);
		}
		return rowView;
	}
}