package com.br.makemerun.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

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
		kmProgress.setMax((int)Math.round(goal.getKm()*1000));
		kmProgress.setSubTitle(getString(R.string.description_of) + " " + goal.getKm() + "km");
		kmProgress.setProgress((int)Math.round(goal.getProgressKm()*1000));
    	kmProgress.setTitle(String.format("%.2f",goal.getProgressKm()));
		kmProgress.setIndeterminate(false);

		speedProgress = (CircularProgressBar) findViewById(R.id.progressSpeed);
		speedProgress.setMax(35);
		speedProgress.setSubTitle("km/h");
    	speedProgress.setProgress((int)Math.round(goal.getLastSpeedRunning()));
    	speedProgress.setTitle("" + ((int)Math.ceil(goal.getLastSpeedRunning())));
		speedProgress.setIndeterminate(false);

		timeProgress = (CircularProgressBar) findViewById(R.id.progressTime);
    	timeProgress.setMax((int) goal.getLastTotalTimeRunning());
    	timeProgress.setProgress((int)goal.getLastTimeRunning());
    	timeProgress.setTitle("" + ((int)Math.ceil((double)goal.getLastTimeRunning()/(double)60)));
    	timeProgress.setSubTitle("min");
		timeProgress.setIndeterminate(false);

		listSubgoals = (ListView) findViewById(R.id.listSubgoals);

		subgoals = new Subgoal[N_SUBGOALS + 1];

		final double tax = (double)1 / (double)N_SUBGOALS;
		final double nPartials = goal.getKm()/goal.getKmBase();		
		final double kmTotalRunning = Math.ceil(nPartials / 2) * goal.getKmBase();
		final double kmTotalWalking = (goal.getKm() - kmTotalRunning);

		final double increaseTax = kmTotalWalking/(double)4;
		double walkingPartialRef = kmTotalWalking/(Math.ceil(nPartials - Math.ceil(nPartials / 2)));
		double partialRunning;
		double partialWalking;

		for(int i = 0; i <= N_SUBGOALS; i++){
			subgoals[i] = new Subgoal();

			if(i <= goal.getProgress()){
				subgoals[i].setCompleted(true);
			}else if(i == goal.getProgress()){
				kmProgress.setProgress((int)(kmTotalRunning + i * increaseTax));
				kmProgress.setTitle(String.format("%.2f",kmTotalRunning + i * increaseTax));

	        	speedProgress.setProgress((int)goal.getSpeedBase());
	        	speedProgress.setTitle(String.format("%.1f", goal.getSpeedBase()));
			}

			subgoals[i].setKmWalking(kmTotalWalking - i * increaseTax);
			subgoals[i].setKmRunning(kmTotalRunning + i * increaseTax);

			partialWalking = walkingPartialRef - walkingPartialRef*i*tax;
			partialRunning = goal.getKmBase() + walkingPartialRef*i*tax;

			subgoals[i].setKmPartialWalking(partialWalking);
			subgoals[i].setKmPartialRunning(partialRunning);
		}

		listAdapter = new SubgoalsArrayAdapter(this, subgoals);

		listSubgoals.setAdapter(listAdapter);
		listSubgoals.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	choosenSubgoal = position;
                Intent intent = new Intent(SubgoalsList.this, StartRun.class);
                intent.putExtra("subgoal",  position);
                intent.putExtra("totalDistance",  goal.getKm());
                intent.putExtra("totalDistanceWalking", subgoals[position].getKmWalking());
                intent.putExtra("totalDistanceRunning", subgoals[position].getKmRunning());
                intent.putExtra("partialDistanceWalking", subgoals[position].getKmPartialWalking());
                intent.putExtra("partialDistanceRunning", subgoals[position].getKmPartialRunning());
                startActivityForResult(intent,RUNNING_RESULTS_REQUEST);
            }
        });
		
		TextView txGiveUp = (TextView) findViewById(R.id.txGiveUp);
		txGiveUp.setOnClickListener(new View.OnClickListener() {
 			@Override
			public void onClick(View v) {
        		AlertDialog.Builder builder = new AlertDialog.Builder(SubgoalsList.this);

        	    builder.setTitle(SubgoalsList.this.getString(R.string.title_quit));
        	    builder.setMessage(SubgoalsList.this.getString(R.string.description_are_you_sure));

        	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	        public void onClick(DialogInterface dialog, int which) {
        	        	db.deleteGoal(goal);
                        Intent intent = new Intent(SubgoalsList.this, ChooseGoal.class);
                        startActivity(intent);
        	            dialog.dismiss();
        	        }
        	    });

        	    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        	        @Override
        	        public void onClick(DialogInterface dialog, int which) {
        	            dialog.dismiss();
        	        }
        	    });

        	    AlertDialog alert = builder.create();
        	    alert.show();
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
	        	long runningTime = bundle.getLong("runningTime");
	        	long totalTime = bundle.getLong("totalTime");

	        	Goal goal = db.getCurrentGoal();
	        	goal.setProgressKm(subgoals[choosenSubgoal].getKmRunning());
	        	goal.setLastSpeedRunning(speed);
	        	goal.setLastTimeRunning(runningTime);
	        	goal.setLastTotalTimeRunning(totalTime);
	        	goal.setProgress(choosenSubgoal);
	        	goal.setSpeedBase(speed);
	        	db.updateGoal(goal);

	        	kmProgress.setProgress((int)Math.round(subgoals[choosenSubgoal].getKmRunning()*1000));
	        	kmProgress.setTitle(String.format("%.2f",subgoals[choosenSubgoal].getKmRunning()));

	        	speedProgress.setProgress((int)Math.round(speed));
	        	speedProgress.setTitle("" + ((int)Math.ceil(speed)));

	        	timeProgress.setMax((int) totalTime);
	        	timeProgress.setProgress((int)runningTime);
	        	timeProgress.setTitle("" + ((int)Math.ceil((double)runningTime/(double)60)));
	        	timeProgress.setSubTitle("min");

	        	subgoals[choosenSubgoal].setCompleted(true);

	        	listAdapter.notifyDataSetChanged();
	        }
	    }
	}

	@Override
	public void onBackPressed() {
		this.moveTaskToBack(true);
	}
}