package com.br.makemerun.view;

import java.util.List;

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

import com.br.makemerun.R;
import com.br.makemerun.model.Subgoal;
 
public class SubgoalsArrayAdapter extends ArrayAdapter<Subgoal> {
	private final Context context;
	private final List<Subgoal> values;

	public SubgoalsArrayAdapter(Context context, List<Subgoal> values) {
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

		if(values.get(position).getKmTotalWalking() < 10)
			walkingText.setText(String.format("%.2f", values.get(position).getKmTotalWalking()) + "km");
		else
			walkingText.setText(String.format("%.1f", values.get(position).getKmTotalWalking()) + "km");

		if(values.get(position).getKmTotalRunning() < 10)
			runningText.setText(String.format("%.2f", values.get(position).getKmTotalRunning()) + "km");
		else
			runningText.setText(String.format("%.1f", values.get(position).getKmTotalRunning()) + "km");

		walkingPartialText.setText("" + String.format("%.2f", values.get(position).getKmPartialWalking()) + "km");
		runningPartialText.setText("" + String.format("%.2f", values.get(position).getKmPartialRunning()) + "km");

		if(values.get(position).isCompleted()){
			ImageView icStats = (ImageView) rowView.findViewById(R.id.icStats);
			icStats.setVisibility(View.VISIBLE);
			final int subgoal = position;
			icStats.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					SubgoalsList.updateStatsView(subgoal);
//					Intent intent = new Intent(context,Statistics.class);
//					intent.putExtra("subgoal", subgoal);
//					context.startActivity(intent);
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