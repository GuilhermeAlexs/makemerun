package com.br.makemerun.view;

import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.br.makemerun.R;
import com.br.makemerun.database.GoalDB;
import com.br.makemerun.database.StatsDB;
import com.br.makemerun.model.Goal;
import com.br.makemerun.model.MetricUtils;
import com.br.makemerun.model.Subgoal;
import com.br.makemerun.view.TestDraw.SwipeGestureDetector;
import com.br.makemerun.view.widgets.AlternatedCircle;
import com.br.makemerun.view.widgets.CircularProgressBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class SubgoalsList extends Activity{
	private ListView listSubgoals;
	private AlternatedCircle circle;
	private ImageView btnLeft;
	private ImageView btnRight;

	private TextView walkingText;
	private TextView runningText;
	private TextView walkingPartialText;
	private TextView runningPartialText;
	private TextView txExplain;

	private SubgoalsArrayAdapter listAdapter;
	private GoalDB db;
	private int choosenSubgoal;
	public static List<Subgoal> subgoals;
	public static Subgoal selectedSubgoal;

	private final int RUNNING_RESULTS_REQUEST = 1;

	public static CircularProgressBar kmRunningProgress;
	public static CircularProgressBar speedRunningProgress;
	public static CircularProgressBar timeRunningProgress;

	private static final int PROGRESS_KM_RUNNING = 0;
	private static final int PROGRESS_KM_WALKING = 1;

	private static final int PROGRESS_SPEED_RUNNING = 3;
	private static final int PROGRESS_SPEED_WALKING = 4;

	private static final int PROGRESS_TIME_RUNNING = 5;
	private static final int PROGRESS_TIME_WALKING = 6;

	public static int currKmStatsView = PROGRESS_KM_RUNNING;
	public static int currTimeStatsView = PROGRESS_TIME_RUNNING;
	public static int currSpeedStatsView = PROGRESS_SPEED_RUNNING;

	private InterstitialAd interstitial;
	private double lastTimeAd = -1d;

	private AdRequest adRequest;
	private ImageView btnRun;
	
	public static void updateStatsView(int id){
		selectedSubgoal = subgoals.get(id);
		
		if(!selectedSubgoal.isCompleted()){
			kmRunningProgress.setProgress(0);
			kmRunningProgress.setTitle("0,00");
		}else{
			kmRunningProgress.setProgress((int) (selectedSubgoal.getKmTotalRunning()*1000));
			kmRunningProgress.setTitle(String.format("%.2f",selectedSubgoal.getKmTotalRunning()));
		}
		kmRunningProgress.setProgressColor(Color.parseColor("#ff9900"));
		kmRunningProgress.setTitleColor(Color.parseColor("#ff9900"));
		currKmStatsView = PROGRESS_KM_RUNNING;
		
		double timeRunning = (selectedSubgoal.getTotalRunningTime() / (double) 3600); //horas
		long paceRunning = (long) (MetricUtils.convertToPace(selectedSubgoal.getKmTotalRunning() / timeRunning ) * 60); //segundos
		double speedRunning = selectedSubgoal.getKmTotalRunning() / timeRunning;
		if(!selectedSubgoal.isCompleted()){
			speedRunningProgress.setProgress(0);
			speedRunningProgress.setTitle("--:--");
		}else{
			speedRunningProgress.setProgress((int)Math.round( speedRunning * 10));
			speedRunningProgress.setTitle("" + MetricUtils.formatTime(paceRunning));
		}
		speedRunningProgress.setProgressColor(Color.parseColor("#ff9900"));
		speedRunningProgress.setTitleColor(Color.parseColor("#ff9900"));
		currTimeStatsView = PROGRESS_TIME_RUNNING;

		timeRunningProgress.setMax((int) selectedSubgoal.getTotalTime());

		if(!selectedSubgoal.isCompleted()){
			timeRunningProgress.setProgress(0);
			timeRunningProgress.setTitle("--:--");
		}else{
			timeRunningProgress.setProgress((int)selectedSubgoal.getTotalRunningTime());
			timeRunningProgress.setTitle("" + MetricUtils.formatTime(selectedSubgoal.getTotalRunningTime()));
		}
		timeRunningProgress.setProgressColor(Color.parseColor("#ff9900"));
		timeRunningProgress.setTitleColor(Color.parseColor("#ff9900"));
		currSpeedStatsView = PROGRESS_SPEED_RUNNING;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subgoals_cards);

		//initAdMob();
		
		db = new GoalDB(this.getApplicationContext());
		final Goal goal = db.getCurrentGoal();
		//SubgoalDB subgoalDB = new SubgoalDB(this);
		subgoals = goal.getSubgoals();

		kmRunningProgress = (CircularProgressBar) findViewById(R.id.progressRunningGoal);
		speedRunningProgress = (CircularProgressBar) findViewById(R.id.progressRunningSpeed);
		timeRunningProgress = (CircularProgressBar) findViewById(R.id.progressRunningTime);
		
		kmRunningProgress.setMax((int)Math.round(goal.getKm()*1000));
		kmRunningProgress.setSubTitle(getString(R.string.description_of) + " " + goal.getKm() + "km");
		kmRunningProgress.setIndeterminate(false);
		
		kmRunningProgress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				switch(currKmStatsView){
					case PROGRESS_KM_WALKING:
						if(selectedSubgoal != null){
							if(!selectedSubgoal.isCompleted()){
								kmRunningProgress.setProgress(0);
								kmRunningProgress.setTitle("0,00");
							}else{
								kmRunningProgress.setProgress((int) (selectedSubgoal.getKmTotalRunning()*1000));
								kmRunningProgress.setTitle(String.format("%.2f",selectedSubgoal.getKmTotalRunning()));
							}
						}
						
						kmRunningProgress.setProgressColor(Color.parseColor("#ff9900"));
						kmRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));
						kmRunningProgress.setTitleColor(Color.parseColor("#ff9900"));
						kmRunningProgress.setSubTitle(getString(R.string.description_of) + " " + goal.getKm() + "km");
				    	currKmStatsView = PROGRESS_KM_RUNNING; 
						break;
					case PROGRESS_KM_RUNNING:
						if(selectedSubgoal != null){
							if(!selectedSubgoal.isCompleted()){
								kmRunningProgress.setProgress(0);
								kmRunningProgress.setTitle("0,00");
							}else{
								kmRunningProgress.setProgress((int)Math.round((selectedSubgoal.getKmTotalWalking())*1000));
								kmRunningProgress.setTitle(String.format("%.2f", selectedSubgoal.getKmTotalWalking()));
							}
						}

						kmRunningProgress.setProgressColor(Color.parseColor("#3299bb"));
						kmRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));
						kmRunningProgress.setTitleColor(Color.parseColor("#3299bb"));
						kmRunningProgress.setSubTitle(getString(R.string.description_of) + " " + goal.getKm() + "km");
				    	currKmStatsView = PROGRESS_KM_WALKING; 
						break;
				}
			}
		});

		speedRunningProgress.setMax((int)Math.round(goal.getSpeedBase())*10);
		speedRunningProgress.setSubTitle("min/km");
		speedRunningProgress.setIndeterminate(false);
		speedRunningProgress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				switch(currSpeedStatsView){
					case PROGRESS_SPEED_WALKING:
						if(selectedSubgoal != null){
							if(!selectedSubgoal.isCompleted()){
								speedRunningProgress.setProgress(0);
								speedRunningProgress.setTitle("--:--");
							}else{
								double timeRunning = (selectedSubgoal.getTotalRunningTime() / (double) 3600); //horas
								long paceRunning = (long) (MetricUtils.convertToPace(selectedSubgoal.getKmTotalRunning() / timeRunning ) * 60); //segundos
								double speedRunning = selectedSubgoal.getKmTotalRunning() / timeRunning;
								speedRunningProgress.setTitle("" + MetricUtils.formatTime(paceRunning));
								speedRunningProgress.setProgress((int)Math.round(speedRunning*10));							
							}
						}
						
						speedRunningProgress.setProgressColor(Color.parseColor("#ff9900"));
						speedRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));
						speedRunningProgress.setTitleColor(Color.parseColor("#ff9900"));
				    	currSpeedStatsView = PROGRESS_SPEED_RUNNING; 
						break;
					case PROGRESS_SPEED_RUNNING:
						if(selectedSubgoal != null){
							if(!selectedSubgoal.isCompleted()){
								speedRunningProgress.setProgress(0);
								speedRunningProgress.setTitle("--:--");
							}else{
								double timeWalking = selectedSubgoal.getTotalWalkingTime() / (double) 3600;
								double speedWalking = selectedSubgoal.getKmTotalWalking() / timeWalking;
								long paceWalking = (long) (MetricUtils.convertToPace(speedWalking) * 60);
								speedRunningProgress.setTitle("" + MetricUtils.formatTime(paceWalking));
								speedRunningProgress.setProgress((int)Math.round(speedWalking*10));					
							}
						}
						
						speedRunningProgress.setProgressColor(Color.parseColor("#3299bb"));
						speedRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));
						speedRunningProgress.setTitleColor(Color.parseColor("#3299bb"));
				    	currSpeedStatsView = PROGRESS_SPEED_WALKING; 
						break;
				}
			}
		});

    	timeRunningProgress.setSubTitle(getString(R.string.description_time));
		timeRunningProgress.setIndeterminate(false);
		timeRunningProgress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				switch(currTimeStatsView){
					case PROGRESS_TIME_WALKING:
						if(selectedSubgoal != null){
							if(!selectedSubgoal.isCompleted()){
								timeRunningProgress.setProgress(0);
								timeRunningProgress.setTitle("--:--");
							}else{
								timeRunningProgress.setMax((int) selectedSubgoal.getTotalTime());
								timeRunningProgress.setProgress((int)selectedSubgoal.getTotalRunningTime());
								timeRunningProgress.setTitle("" + MetricUtils.formatTime(selectedSubgoal.getTotalRunningTime()));
							}
						}
						
						timeRunningProgress.setProgressColor(Color.parseColor("#ff9900"));
						timeRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));
						timeRunningProgress.setTitleColor(Color.parseColor("#ff9900"));
						currTimeStatsView = PROGRESS_TIME_RUNNING; 
						break;
					case PROGRESS_TIME_RUNNING:
						if(selectedSubgoal != null){
							if(!selectedSubgoal.isCompleted()){
								timeRunningProgress.setProgress(0);
								timeRunningProgress.setTitle("--:--");
							}else{
								timeRunningProgress.setMax((int) selectedSubgoal.getTotalTime());
								timeRunningProgress.setProgress((int)(selectedSubgoal.getTotalWalkingTime()));
								timeRunningProgress.setTitle("" + MetricUtils.formatTime(selectedSubgoal.getTotalWalkingTime()));
							}
						}

						timeRunningProgress.setProgressColor(Color.parseColor("#3299bb"));
						timeRunningProgress.setProgressBackgroundColor(Color.parseColor("#444444"));
						timeRunningProgress.setTitleColor(Color.parseColor("#3299bb"));
						
						currTimeStatsView = PROGRESS_TIME_WALKING; 
						break;
				}
			}
		});

		if(goal.getProgress() != -1){
			updateStatsView(goal.getProgress());
		}else{
			kmRunningProgress.setProgress(0);
			kmRunningProgress.setTitle("0,00");
			
			speedRunningProgress.setProgress(0);
			speedRunningProgress.setTitle("--:--");
			
			timeRunningProgress.setProgress(0);
			timeRunningProgress.setTitle("--:--");
		}

//		listSubgoals = (ListView) findViewById(R.id.listSubgoals);
//
//		listSubgoals = (ListView) findViewById(R.id.listSubgoals);
//		listAdapter = new SubgoalsArrayAdapter(this, subgoals);
//		listSubgoals.setAdapter(listAdapter);
//		listSubgoals.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                    long id) {
//            	choosenSubgoal = position;
//            	Subgoal subgoal = subgoals.get(position);
//                Intent intent = new Intent(SubgoalsList.this, StartRun.class);
//                intent.putExtra("subgoal",  position);
//                intent.putExtra("totalDistance",  goal.getKm());
//                intent.putExtra("totalDistanceWalking", subgoal.getKmTotalWalking());
//                intent.putExtra("totalDistanceRunning", subgoal.getKmTotalRunning());
//                intent.putExtra("partialDistanceWalking", subgoal.getKmPartialWalking());
//                if(choosenSubgoal == 4)
//                	intent.putExtra("partialDistanceRunning", goal.getKm());
//                else
//                	intent.putExtra("partialDistanceRunning", subgoal.getKmPartialRunning());
//                startActivityForResult(intent,RUNNING_RESULTS_REQUEST);
//            }
//        });

		selectedSubgoal = subgoals.get(0);
		for(Subgoal sg: subgoals){
			if(sg.isLast())
				selectedSubgoal = sg;
		}

		circle = (AlternatedCircle) findViewById(R.id.circle);
		walkingText = (TextView) findViewById(R.id.txWalking);
		runningText = (TextView) findViewById(R.id.txRunning);
		walkingPartialText = (TextView) findViewById(R.id.txPartialWalking);
		runningPartialText = (TextView) findViewById(R.id.txPartialRunning);
		txExplain = (TextView) findViewById(R.id.txExplain);
		btnLeft = (ImageView) findViewById(R.id.btnLeft);
		btnRight = (ImageView) findViewById(R.id.btnRight);
		btnRun = (ImageView) findViewById(R.id.btnRun);

		btnLeft.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				leftClick();
			}
		});
		
		btnRight.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				rightClick();
			}
		});
		

		btnRun.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
               Intent intent = new Intent(SubgoalsList.this, StartRun.class);
              intent.putExtra("subgoal",  choosenSubgoal);
              intent.putExtra("totalDistance",  goal.getKm());
              intent.putExtra("totalDistanceWalking", selectedSubgoal.getKmTotalWalking());
              intent.putExtra("totalDistanceRunning", selectedSubgoal.getKmTotalRunning());
              intent.putExtra("partialDistanceWalking", selectedSubgoal.getKmPartialWalking());
              if(choosenSubgoal == subgoals.size())
              	intent.putExtra("partialDistanceRunning", goal.getKm());
              else
              	intent.putExtra("partialDistanceRunning", selectedSubgoal.getKmPartialRunning());
              startActivityForResult(intent,RUNNING_RESULTS_REQUEST);
			}
		});
		
		RelativeLayout cardLayout = (RelativeLayout) findViewById(R.id.cardLayout);
		final GestureDetector gestureDetector = new GestureDetector(this,new SwipeGestureDetector());
		cardLayout.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				gestureDetector.onTouchEvent(event);
				return true;
			}
		});

		ImageView txGiveUp = (ImageView) findViewById(R.id.btnGiveUp);
		txGiveUp.setOnClickListener(new View.OnClickListener() {
 			@Override
			public void onClick(View v) {
        		AlertDialog.Builder builder = new AlertDialog.Builder(SubgoalsList.this);

        	    builder.setTitle(SubgoalsList.this.getString(R.string.title_quit));
        	    builder.setMessage(SubgoalsList.this.getString(R.string.description_are_you_sure));

        	    builder.setPositiveButton(SubgoalsList.this.getString(R.string.button_yes), new DialogInterface.OnClickListener() {
        	        public void onClick(DialogInterface dialog, int which) {
        	        	db.deleteGoal(goal);
        	        	StatsDB statsDB = new StatsDB(SubgoalsList.this);
        	        	statsDB.deleteAll();
                        Intent intent = new Intent(SubgoalsList.this, ChooseGoal.class);
                        startActivity(intent);
        	            dialog.dismiss();
        	        }
        	    });

        	    builder.setNegativeButton(SubgoalsList.this.getString(R.string.button_no), new DialogInterface.OnClickListener() {
        	        @Override
        	        public void onClick(DialogInterface dialog, int which) {
        	            dialog.dismiss();
        	        }
        	    });

        	    AlertDialog alert = builder.create();
        	    alert.show();
			}
        });
	
		circle.setTotalDistance((float)goal.getKm());
		updateCard();
	}

	private void updateCard(){
		walkingText.setText(String.format("%.2f",selectedSubgoal.getKmTotalWalking()) + "km");
		runningText.setText(String.format("%.2f",selectedSubgoal.getKmTotalRunning()) + "km");

		walkingPartialText.setText(String.format("%.2f",selectedSubgoal.getKmPartialWalking()) + "km");
		runningPartialText.setText(String.format("%.2f",selectedSubgoal.getKmPartialRunning()) + "km");

		txExplain.setText(getString(R.string.description_subgoal,
				String.format("%.2f",selectedSubgoal.getKmPartialRunning()) + "km",
				String.format("%.2f",selectedSubgoal.getKmPartialWalking()) + "km"));
		
		circle.setRunDistance((float) selectedSubgoal.getKmPartialRunning());
		circle.setWalkDistance((float) selectedSubgoal.getKmPartialWalking());
	}

	private void initAdMob(){
	    interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId(MarketingConfig.adUnitId);

	    adRequest = new AdRequest.Builder().build();

	    interstitial.setAdListener(new AdListener() {
			public void onAdLoaded() {
				displayInterstitial();
			}
		});
	}
	
    public void displayInterstitial() {
        if(interstitial.isLoaded())
          interstitial.show();
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
		
//	    double timeNow = (Calendar.getInstance().getTimeInMillis()/60000);
//	    if(lastTimeAd == -1 || (timeNow - lastTimeAd) >= 10){
//	    	interstitial.loadAd(adRequest);
//	    	lastTimeAd = timeNow;
//	    }
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
	        	long runningTime = bundle.getLong("runningTime");
	        	long totalTime = bundle.getLong("totalTime");

	        	Goal goal = db.getCurrentGoal();
	        	Subgoal subgoal = subgoals.get(choosenSubgoal);

	        	goal.setProgress(choosenSubgoal);
	        	subgoal.setTotalRunningTime(runningTime);
	        	subgoal.setTotalWalkingTime(totalTime - runningTime);
	        	subgoal.setTotalTime(totalTime);
	        	subgoal.setCompleted(true);
	        	goal.setSubgoals(subgoals);
	        	db.updateGoal(goal);

	        	updateStatsView(choosenSubgoal);
	        	
	        	listAdapter.notifyDataSetChanged();
	        }
	    }
	}

	@Override
	public void onBackPressed() {
		this.moveTaskToBack(true);
	}
	
	private void leftClick(){
		choosenSubgoal--;

		if(choosenSubgoal < 0)
			choosenSubgoal = 0;

		updateStatsView(choosenSubgoal);
		updateCard();

		if(choosenSubgoal == 0){
			btnLeft.setVisibility(View.INVISIBLE);
			btnRight.setVisibility(View.VISIBLE);
		}else{
			btnLeft.setVisibility(View.VISIBLE);
			btnRight.setVisibility(View.VISIBLE);
		}
	}
	
	private void rightClick(){
		choosenSubgoal++;

		if(choosenSubgoal > subgoals.size() - 1)
			choosenSubgoal = subgoals.size() - 1;

		updateStatsView(choosenSubgoal);
		updateCard();

		if(choosenSubgoal == (subgoals.size() - 1)){
			btnRight.setVisibility(View.INVISIBLE);
			btnLeft.setVisibility(View.VISIBLE);
		}else{
			btnRight.setVisibility(View.VISIBLE);
			btnLeft.setVisibility(View.VISIBLE);
		}
	}

	class SwipeGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {       	
			if((e1.getRawX() - e2.getRawX()) > 10){
				rightClick();
			}else if((e2.getRawX() - e1.getRawX()) > 10){
				leftClick();
			}

			return false;
		}
	}
}