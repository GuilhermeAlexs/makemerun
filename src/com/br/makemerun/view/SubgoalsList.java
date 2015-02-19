package com.br.makemerun.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.br.makemerun.R;
import com.br.makemerun.database.GoalDB;
import com.br.makemerun.database.StatsDB;
import com.br.makemerun.model.Goal;
import com.br.makemerun.model.MetricUtils;
import com.br.makemerun.model.Subgoal;
import com.br.makemerun.view.widgets.CircularProgressBar;

public class SubgoalsList extends Activity implements OnGestureListener{
	private ListView listSubgoals;
	private SubgoalsArrayAdapter listAdapter;
	private GoalDB db;
	private Subgoal [] subgoals;
	private int choosenSubgoal;
	private final int N_SUBGOALS = 4;

	private final int RUNNING_RESULTS_REQUEST = 1;

	private CircularProgressBar kmRunningProgress;
	private CircularProgressBar speedRunningProgress;
	private CircularProgressBar timeRunningProgress;

	private GestureDetector gestureDetector;
	private ViewSwitcher lastStatsSwitcher;

	private final int PROGRESS_KM_RUNNING = 0;
	private final int PROGRESS_KM_WALKING = 1;
	private final int PROGRESS_KM_TOTAL = 2;

	private final int PROGRESS_SPEED_RUNNING = 3;
	private final int PROGRESS_SPEED_WALKING = 4;

	private final int PROGRESS_TIME_RUNNING = 5;
	private final int PROGRESS_TIME_WALKING = 6;
	private final int PROGRESS_TIME_TOTAL = 7;

	private int currKmStatsView = PROGRESS_KM_RUNNING;
	private int currTimeStatsView = PROGRESS_TIME_RUNNING;
	private int currSpeedStatsView = PROGRESS_SPEED_RUNNING;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subgoals_list);

		db = new GoalDB(this.getApplicationContext());
		final Goal goal = db.getCurrentGoal();

		lastStatsSwitcher = (ViewSwitcher) findViewById(R.id.lastStatsSwitcher);
		lastStatsSwitcher.setInAnimation(this, android.R.anim.fade_in);
		lastStatsSwitcher.setOutAnimation(this, android.R.anim.fade_out);
		gestureDetector = new GestureDetector(this, this);

		kmRunningProgress = (CircularProgressBar) findViewById(R.id.progressRunningGoal);
		kmRunningProgress.setMax((int)Math.round(goal.getKm()*1000));
		kmRunningProgress.setSubTitle(getString(R.string.description_of) + " " + goal.getKm() + "km");
		kmRunningProgress.setProgress((int)Math.round(goal.getProgressKm()*1000));
    	kmRunningProgress.setTitle(String.format("%.2f",goal.getProgressKm()));
		kmRunningProgress.setIndeterminate(false);
		
		kmRunningProgress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				switch(currKmStatsView){
					case PROGRESS_KM_WALKING:
						kmRunningProgress.setProgressColor(Color.parseColor("#ff9900"));
						kmRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));

						kmRunningProgress.setSubTitle(getString(R.string.description_of) + " " + goal.getKm() + "km");
						kmRunningProgress.setProgress((int)Math.round(goal.getProgressKm()*1000));
				    	kmRunningProgress.setTitle(String.format("%.2f",goal.getProgressKm()));
				    	currKmStatsView = PROGRESS_KM_RUNNING; 
						break;
					case PROGRESS_KM_RUNNING:
						kmRunningProgress.setProgressColor(Color.parseColor("#3299bb"));
						kmRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));

						kmRunningProgress.setSubTitle(getString(R.string.description_of) + " " + goal.getKm() + "km");
						kmRunningProgress.setProgress((int)Math.round((goal.getKm() - goal.getProgressKm())*1000));
				    	kmRunningProgress.setTitle(String.format("%.2f",goal.getKm() - goal.getProgressKm()));
				    	currKmStatsView = PROGRESS_KM_WALKING; 
						break;
				}
			}
		});

		speedRunningProgress = (CircularProgressBar) findViewById(R.id.progressRunningSpeed);
		speedRunningProgress.setMax((int)Math.round(goal.getSpeedBase())*10);
		speedRunningProgress.setSubTitle("min/km");
    	speedRunningProgress.setProgress((int)Math.round((goal.getProgressKm() / (goal.getLastTimeRunning()/(double)3600)*10)));
    	speedRunningProgress.setTitle("" + String.format("%.1f",MetricUtils.convertToPace(goal.getProgressKm() / (goal.getLastTimeRunning()/(double)3600))));
		speedRunningProgress.setIndeterminate(false);
		speedRunningProgress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				switch(currSpeedStatsView){
					case PROGRESS_SPEED_WALKING:
						speedRunningProgress.setProgressColor(Color.parseColor("#ff9900"));
						speedRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));
						double speedRunning = goal.getProgressKm() / (goal.getLastTimeRunning()/(double)3600);
						speedRunningProgress.setTitle("" + String.format("%.1f", MetricUtils.convertToPace(speedRunning)));
				    	speedRunningProgress.setProgress((int)Math.round(speedRunning*10));
				    	currSpeedStatsView = PROGRESS_SPEED_RUNNING; 
						break;
					case PROGRESS_SPEED_RUNNING:
						speedRunningProgress.setProgressColor(Color.parseColor("#3299bb"));
						speedRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));
						double speedWalking = (goal.getKm() - goal.getProgressKm()) / ((goal.getLastTotalTimeRunning() - goal.getLastTimeRunning()) / (double)3600);
						speedRunningProgress.setTitle("" + String.format("%.1f",MetricUtils.convertToPace(speedWalking)));
				    	speedRunningProgress.setProgress((int)Math.round(speedWalking*10));
				    	currSpeedStatsView = PROGRESS_SPEED_WALKING; 
						break;
				}
			}
		});

		timeRunningProgress = (CircularProgressBar) findViewById(R.id.progressRunningTime);
    	timeRunningProgress.setMax((int) goal.getLastTotalTimeRunning());
    	timeRunningProgress.setProgress((int)goal.getLastTimeRunning());
    	timeRunningProgress.setTitle("" + String.format("%.1f",(double)goal.getLastTimeRunning()/(double)60));
    	timeRunningProgress.setSubTitle("tempo");
		timeRunningProgress.setIndeterminate(false);
		timeRunningProgress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				switch(currTimeStatsView){
					case PROGRESS_TIME_WALKING:
						timeRunningProgress.setProgressColor(Color.parseColor("#ff9900"));
						timeRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));

						timeRunningProgress.setMax((int) goal.getLastTotalTimeRunning());
						timeRunningProgress.setProgress((int)goal.getLastTimeRunning());
						timeRunningProgress.setTitle("" + String.format("%.1f",(double)goal.getLastTimeRunning()/(double)60));
						currTimeStatsView = PROGRESS_TIME_RUNNING; 
						break;
					case PROGRESS_TIME_RUNNING:
						timeRunningProgress.setProgressColor(Color.parseColor("#3299bb"));
						timeRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));

						timeRunningProgress.setMax((int) goal.getLastTotalTimeRunning());
						timeRunningProgress.setProgress((int)(goal.getLastTotalTimeRunning() - goal.getLastTimeRunning()));
						timeRunningProgress.setTitle("" + String.format("%.1f",((double)goal.getLastTotalTimeRunning() - goal.getLastTimeRunning())/(double)60));
						currTimeStatsView = PROGRESS_TIME_WALKING; 
						break;
				}
			}
		});

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
		StatsDB statsDB = new StatsDB(this);

		for(int i = 0; i <= N_SUBGOALS; i++){
			subgoals[i] = new Subgoal();

			if(statsDB.getStatsBySubgoal(i, StatsDB.RUNNING_SPRINT) != null){
				subgoals[i].setCompleted(true);
			}else if(i == goal.getProgress()){
				kmRunningProgress.setProgress((int)(kmTotalRunning + i * increaseTax));
				kmRunningProgress.setTitle(String.format("%.2f",kmTotalRunning + i * increaseTax));

	        	speedRunningProgress.setProgress((int)goal.getSpeedBase());
	        	speedRunningProgress.setTitle(String.format("%.1f", goal.getSpeedBase()));
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
        	        	StatsDB statsDB = new StatsDB(SubgoalsList.this);
        	        	statsDB.deleteAll();
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

	        	kmRunningProgress.setProgress((int)Math.round(subgoals[choosenSubgoal].getKmRunning()*1000));
	        	kmRunningProgress.setTitle(String.format("%.2f",subgoals[choosenSubgoal].getKmRunning()));

	        	speedRunningProgress.setProgress((int)Math.round(MetricUtils.convertToPace(speed)));
	        	speedRunningProgress.setTitle("" + String.format("%.1f",MetricUtils.convertToPace(speed)));

	        	timeRunningProgress.setMax((int) totalTime);
	        	timeRunningProgress.setProgress((int)runningTime);
	        	timeRunningProgress.setTitle("" + String.format("%.1f",(double)runningTime/(double)60));
	        	timeRunningProgress.setSubTitle("min");

	        	subgoals[choosenSubgoal].setCompleted(true);

	        	listAdapter.notifyDataSetChanged();
	        }
	    }
	}

	@Override
	public void onBackPressed() {
		this.moveTaskToBack(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	private boolean isViewContains(View view, int rx, int ry) {
	    int[] l = new int[2];
	    view.getLocationOnScreen(l);
	    int x = l[0];
	    int y = l[1];
	    int w = view.getWidth();
	    int h = view.getHeight();

	    if (rx < x || rx > x + w || ry < y || ry > y + h) {
	        return false;
	    }
	    return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,
			float arg3) {
		
		if(isViewContains(lastStatsSwitcher,(int)e1.getRawX(),(int)e1.getRawY()) &&
		   isViewContains(lastStatsSwitcher,(int)e2.getRawX(),(int)e2.getRawY()) &&
		   Math.abs(e2.getRawX() - e1.getRawX()) > 50){
			lastStatsSwitcher.showNext();
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float arg2,
			float arg3) {

		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
}