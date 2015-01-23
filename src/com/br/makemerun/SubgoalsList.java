package com.br.makemerun;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.br.makemerun.R;
import com.br.makemerun.database.GoalDB;
import com.br.makemerun.model.Goal;

public class SubgoalsList extends Activity{

	private TextView txGoal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subgoals_list);
		
		txGoal = (TextView) findViewById(R.id.txGoal);
		GoalDB db = new GoalDB(this.getApplicationContext());
		Goal goal = db.getCurrentGoal();
		txGoal.setText(goal.getGoal() + "km");
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

	
	
}
