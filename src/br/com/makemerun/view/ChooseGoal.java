package br.com.makemerun.view;

import br.com.makemerun.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseGoal extends Activity{
	private TextView btnOk;
	private ImageView btnUp;
	private ImageView btnDown;
	private TextView txGoal;
	private CheckBox checkDoTest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_goal);
		overridePendingTransition(R.drawable.activity_in, R.drawable.activity_out);	

		btnOk = (TextView) findViewById(R.id.btnOk);
		btnUp = (ImageView) findViewById(R.id.btnUp);
		btnDown = (ImageView) findViewById(R.id.btnDown);
		txGoal = (TextView) findViewById(R.id.txGoal);
		checkDoTest = (CheckBox) findViewById(R.id.checkDoTest);

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
				Intent intent;

				if(checkDoTest.isChecked())
					intent = new Intent(v.getContext(), RunTest.class);
				else
					intent = new Intent(v.getContext(), ManualTestInput.class);

				intent.putExtra("goal", Integer.parseInt(txGoal.getText().toString().replace("km", "")));
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
	
	@Override
	public void onBackPressed() {
		this.moveTaskToBack(true);
	}
}
