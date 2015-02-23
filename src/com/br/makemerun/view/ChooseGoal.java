package com.br.makemerun.view;

import com.br.makemerun.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseGoal extends Activity{
	
	private TextView btnOk;
	private ImageView btnUp;
	private ImageView btnDown;
	private TextView txGoal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_goal);
		
		btnOk = (TextView) findViewById(R.id.btnOk);
		btnUp = (ImageView) findViewById(R.id.btnUp);
		btnDown = (ImageView) findViewById(R.id.btnDown);
		txGoal = (TextView) findViewById(R.id.txGoal);

		btnUp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int newGoal = Integer.parseInt(txGoal.getText().toString().replace("km", "")) + 1;
				if(newGoal > 150)
					newGoal = 150;
				txGoal.setText("" + newGoal + "km");
			}
		});

		btnDown.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int newGoal = Integer.parseInt(txGoal.getText().toString().replace("km", "")) - 1;
				if(newGoal <= 1)
					newGoal = 1;
				txGoal.setText("" + newGoal + "km");
			}
		});

		btnOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), RunTest.class);
				intent.putExtra("goal", Integer.parseInt(txGoal.getText().toString().replace("km", "")));
				startActivity(intent);
				overridePendingTransition(R.drawable.activity_in, R.drawable.activity_out);				
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
	
	@Override
	public void onBackPressed() {
		this.moveTaskToBack(true);
	}
}
