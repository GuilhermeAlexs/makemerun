package com.br.makemerun.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.br.makemerun.R;
import com.br.makemerun.database.GoalDB;
import com.br.makemerun.model.Goal;
import com.br.makemerun.model.Subgoal;
import com.br.makemerun.view.widgets.CircularProgressBar;

public class SubgoalsList extends Activity{
	private ListView listSubgoals;
	private SubgoalsArrayAdapter listAdapter;
	private GoalDB db;
	private Subgoal [] subgoals;
	private int choosenSubgoal;
	private final int N_SUBGOALS = 4;
	
	private final int RUNNING_RESULTS_REQUEST = 1;

	private CircularProgressBar kmProgress;
	private CircularProgressBar speedProgress;
	private CircularProgressBar timeProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subgoals_list);

		db = new GoalDB(this.getApplicationContext());
		final Goal goal = db.getCurrentGoal();

		kmProgress = (CircularProgressBar) findViewById(R.id.progressGoal);
		kmProgress.setMax((int)Math.ceil(goal.getKm()));
		kmProgress.setTitle("0%");
		kmProgress.setSubTitle("0 of " + goal.getKm() + "km");
		kmProgress.setProgress(0);
		kmProgress.setIndeterminate(false);

		speedProgress = (CircularProgressBar) findViewById(R.id.progressSpeed);
		speedProgress.setMax(20);
		speedProgress.setTitle("0");
		speedProgress.setSubTitle("km/h");
		speedProgress.setProgress(0);
		speedProgress.setIndeterminate(false);

		timeProgress = (CircularProgressBar) findViewById(R.id.progressTime);
		timeProgress.setTitle("0");
		timeProgress.setSubTitle("min");
		timeProgress.setProgress(0);
		timeProgress.setIndeterminate(false);

		listSubgoals = (ListView) findViewById(R.id.listSubgoals);

		subgoals = new Subgoal[N_SUBGOALS + 1];

		final double tax = (double)1 / (double)N_SUBGOALS;
		final double dividedTotalKm = goal.getKm()/(double)2;
		final double increaseTax = dividedTotalKm/(double)4;

		for(int i = 0; i <= N_SUBGOALS; i++){
			subgoals[i] = new Subgoal();

			if(i <= goal.getProgress()){
				subgoals[i].setCompleted(true);
			}

			subgoals[i].setKmWalking(dividedTotalKm - i * increaseTax);
			subgoals[i].setKmRunning(dividedTotalKm + i * increaseTax);

			subgoals[i].setKmPartialWalking(goal.getKmBase()*(1 - i*tax));
			subgoals[i].setKmPartialRunning(goal.getKmBase()*(1 + i*tax));
		}

		listAdapter = new SubgoalsArrayAdapter(this, subgoals);

		listSubgoals.setAdapter(listAdapter);
		listSubgoals.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	choosenSubgoal = position;
                Intent intent = new Intent(SubgoalsList.this, MainActivity.class);
                intent.putExtra("totalDistance",  goal.getKm());
                intent.putExtra("partialWalking", subgoals[position].getKmPartialWalking());
                intent.putExtra("partialRunning", subgoals[position].getKmPartialRunning());
                startActivityForResult(intent,RUNNING_RESULTS_REQUEST);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == RUNNING_RESULTS_REQUEST) {
	        if (resultCode == RESULT_OK) {
	        	Bundle bundle = data.getExtras();
	        	double speed = bundle.getDouble("runningSpeed");
	        	int time = bundle.getInt("runningTime");

	        	Goal goal = db.getCurrentGoal();
	        	goal.setProgress(choosenSubgoal);
	        	db.updateGoal(goal);

	        	kmProgress.setProgress((int)Math.ceil(subgoals[choosenSubgoal].getKmRunning()));
	        	kmProgress.setTitle(String.format("%.2f",subgoals[choosenSubgoal].getKmRunning()));

	        	speedProgress.setProgress((int)Math.floor(speed));
	        	speedProgress.setTitle(String.format("%.2f", speed));

	        	timeProgress.setMax((int)Math.ceil(goal.getKm()/speed));
	        	timeProgress.setProgress(time);
	        	timeProgress.setTitle(String.format("%.2f",(double)time/(double)60));
	        	timeProgress.setSubTitle("min");
	        	subgoals[choosenSubgoal].setCompleted(true);
	        	this.listAdapter.notifyDataSetChanged();
	        }
	    }
	}
}