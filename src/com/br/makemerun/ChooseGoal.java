package com.br.makemerun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class ChooseGoal extends Activity{
	
	Button btNext;
	NumberPicker npGoal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_goal);
		btNext = (Button) findViewById(R.id.btNext);
		npGoal = (NumberPicker) findViewById(R.id.npGoal);
		populateNumberPicker();
		
		btNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), RunTest.class);
				intent.putExtra("goal", npGoal.getValue());
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void populateNumberPicker(){

		npGoal.setMinValue(1);
		npGoal.setMaxValue(100);
		npGoal.setWrapSelectorWheel(true);
		npGoal.setValue(1);
	}
	
}
