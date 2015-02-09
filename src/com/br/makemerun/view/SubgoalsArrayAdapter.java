package com.br.makemerun.view;

import com.br.makemerun.R;
import com.br.makemerun.model.Subgoal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
			ImageView icStats = (ImageView) rowView.findViewById(R.id.icStats);
			icStats.setVisibility(View.VISIBLE);
			final int subgoal = position;
			icStats.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent intent = new Intent(context,Statistics.class);
					intent.putExtra("subgoal", subgoal);
					context.startActivity(intent);
				}
			});
		}

		if(position % 2 == 0){
			int back = Color.rgb(25, 25, 25);
			rowView.setBackgroundColor(back);
			walkingText.setBackgroundColor(back);
			runningText.setBackgroundColor(back);
			walkingPartialText.setBackgroundColor(back);
			runningPartialText.setBackgroundColor(back);
		}
		return rowView;
	}
}