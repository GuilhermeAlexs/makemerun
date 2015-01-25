package com.br.makemerun.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

import com.br.makemerun.R;
import com.br.makemerun.database.GoalDB;
import com.br.makemerun.model.Goal;
import com.br.makemerun.model.Subgoal;

public class SubgoalsList extends Activity{

	private TextView txKm;
	private ListView listSubgoals;
	private GoalDB db;
	private Subgoal [] subgoals;
	private final int N_SUBGOALS = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subgoals_list);

		txKm = (TextView) findViewById(R.id.txKm);
		listSubgoals = (ListView) findViewById(R.id.listSubgoals);

		db = new GoalDB(this.getApplicationContext());
		Goal goal = db.getCurrentGoal();

		subgoals = new Subgoal[N_SUBGOALS + 1];
		int increaseTax = (int)Math.ceil((double)goal.getTimeBase()/(double)N_SUBGOALS);

		for(int i = 0; i < N_SUBGOALS + 1; i++){
			subgoals[i] = new Subgoal();
			subgoals[i].setTimeWalking(goal.getTimeBase() - i * increaseTax);
			subgoals[i].setTimeRunning(goal.getTimeBase() + i * increaseTax);
		}

		txKm.setText(goal.getKm() + "km");
		
		listSubgoals.setAdapter(new SubgoalsArrayAdapter(this, subgoals));
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
