package br.com.makemerun.view;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.makemerun.R;
import br.com.makemerun.database.GoalDB;
import br.com.makemerun.model.Goal;
import br.com.makemerun.model.MetricUtils;
import br.com.makemerun.model.Subgoal;
import br.com.makemerun.view.widgets.AlternatedCircle;
import br.com.makemerun.view.widgets.CircularProgressBar;
import br.com.makemerun.view.widgets.CustomViewPager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class SubgoalsList extends Activity{
	private AlternatedCircle circle;
	private ImageView btnLeft;
	private ImageView btnRight;
	private ImageView btnHelp;

	private TextView walkingText;
	private TextView runningText;
	private TextView walkingPartialText;
	private TextView runningPartialText;
	private TextView txExplain;

	private GoalDB db;
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
	private ViewPager subgoalPager;
	private MyPagerAdapter subgoalPagerAdapter;
	private Goal goal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subgoals_cards);
		overridePendingTransition(R.drawable.activity_in, R.drawable.activity_out);

		initAdMob();
		
		db = new GoalDB(this.getApplicationContext());
		goal = db.getCurrentGoal();
		subgoals = goal.getSubgoals();

		kmRunningProgress = (CircularProgressBar) findViewById(R.id.progressRunningGoal);
		speedRunningProgress = (CircularProgressBar) findViewById(R.id.progressRunningSpeed);
		timeRunningProgress = (CircularProgressBar) findViewById(R.id.progressRunningTime);

		kmRunningProgress.setMax((int)Math.round(goal.getKm()*1000));
		kmRunningProgress.setSubTitle(getString(R.string.description_of) + " " + goal.getKm() + "km");
		kmRunningProgress.setIndeterminate(false);

		kmRunningProgress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				selectedSubgoal = subgoals.get(subgoalPager.getCurrentItem());
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

		selectedSubgoal = subgoals.get(0);
		for(Subgoal sg: subgoals){
			if(sg.isLast())
				selectedSubgoal = sg;
		}

		subgoalPager = (CustomViewPager)findViewById(R.id.subgoalPager);

		subgoalPagerAdapter = new MyPagerAdapter();
		subgoalPager.setAdapter(subgoalPagerAdapter);
		subgoalPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
	        @Override
	        public void onPageSelected(int position) {
	        	  selectedSubgoal = subgoals.get(position);
				  updateStatsView(position);
	        }

	        @Override
	        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	        }

	        @Override
	        public void onPageScrollStateChanged(int state) {
	        }
	    });
		btnHelp = (ImageView) findViewById(R.id.btnHelp);

		ImageView txGiveUp = (ImageView) findViewById(R.id.btnGiveUp);
		txGiveUp.setOnClickListener(new View.OnClickListener() {
 			@Override
			public void onClick(View v) {
        		AlertDialog.Builder builder = new AlertDialog.Builder(SubgoalsList.this);

        	    builder.setTitle(SubgoalsList.this.getString(R.string.title_quit));
        	    builder.setMessage(SubgoalsList.this.getString(R.string.description_are_you_sure_loose_data));

        	    builder.setPositiveButton(SubgoalsList.this.getString(R.string.button_yes), new DialogInterface.OnClickListener() {
        	        public void onClick(DialogInterface dialog, int which) {
        	        	db.deleteGoal(goal);
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
	}

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

	private void updateCard(int position){
		Subgoal sb = subgoals.get(position);
		walkingText.setText(String.format("%.2f",sb.getKmTotalWalking()) + "km");
		runningText.setText(String.format("%.2f",sb.getKmTotalRunning()) + "km");

		walkingPartialText.setText(String.format("%.2f",sb.getKmPartialWalking()) + "km");
		
		if(position == subgoals.size() - 1)
			runningPartialText.setText(String.format("%.2f",goal.getKm()) + "km");
		else
			runningPartialText.setText(String.format("%.2f",sb.getKmPartialRunning()) + "km");

		if(position == subgoals.size() - 1)
			txExplain.setText(getString(R.string.description_subgoal,
				String.format("%.2f",goal.getKm()) + "km",
				String.format("%.2f",sb.getKmPartialWalking()) + "km"));
		else
			txExplain.setText(getString(R.string.description_subgoal,
				String.format("%.2f",sb.getKmPartialRunning()) + "km",
				String.format("%.2f",sb.getKmPartialWalking()) + "km"));

		circle.setTotalDistance((float) goal.getKm());
		circle.setRunDistance((float) sb.getKmPartialRunning());
		circle.setWalkDistance((float) sb.getKmPartialWalking());
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
		
	    double timeNow = (Calendar.getInstance().getTimeInMillis()/60000);
	    if(lastTimeAd == -1 || (timeNow - lastTimeAd) >= 10){
	    	interstitial.loadAd(adRequest);
	    	lastTimeAd = timeNow;
	    }
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

	        	goal = db.getCurrentGoal();
	        	Subgoal subgoal = subgoals.get(subgoalPager.getCurrentItem());

	        	goal.setProgress(subgoalPager.getCurrentItem());
	        	subgoal.setTotalRunningTime(runningTime);
	        	subgoal.setTotalWalkingTime(totalTime - runningTime);
	        	subgoal.setTotalTime(totalTime);
	        	subgoal.setCompleted(true);
	        	goal.setSubgoals(subgoals);
	        	db.updateGoal(goal);

	        	updateStatsView(subgoalPager.getCurrentItem());
	        }
	    }
	}

	@Override
	public void onBackPressed() {
		this.moveTaskToBack(true);
	}
	
	private class MyPagerAdapter extends PagerAdapter{
		  int NumberOfPages = 5;
		  private int explainVisibility = View.VISIBLE;

		  @Override
		  public int getCount() {
		   return NumberOfPages;
		  }

		  @Override
		  public boolean isViewFromObject(View view, Object object) {
		   return view == object;
		  }

		  @Override
		  public Object instantiateItem(ViewGroup parent, int position) {
				LayoutInflater inflater = (LayoutInflater) SubgoalsList.this.getApplicationContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				View rootView = inflater.inflate(R.layout.subgoal_card, parent, false);
		        circle = (AlternatedCircle) rootView.findViewById(R.id.circle);
		        walkingText = (TextView) rootView.findViewById(R.id.txWalking);
		        runningText = (TextView) rootView.findViewById(R.id.txRunning);
		        walkingPartialText = (TextView) rootView.findViewById(R.id.txPartialWalking);
		        runningPartialText = (TextView) rootView.findViewById(R.id.txPartialRunning);
		        txExplain = (TextView) rootView.findViewById(R.id.txExplain);
		        btnLeft = (ImageView) rootView.findViewById(R.id.btnLeft);
		        btnRight = (ImageView) rootView.findViewById(R.id.btnRight);

		        txExplain.setVisibility(explainVisibility);
				btnHelp.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						if(txExplain.getVisibility() != View.GONE)
							txExplain.setVisibility(View.GONE);
						else
							txExplain.setVisibility(View.VISIBLE);
						
						explainVisibility = txExplain.getVisibility();
						subgoalPager.requestLayout();
					//	subgoalPager.invalidate();
					}
				});

				RelativeLayout cardLayout = (RelativeLayout) rootView.findViewById(R.id.cardLayout);
				final Goal goal = db.getCurrentGoal();
				cardLayout.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
		              Intent intent = new Intent(SubgoalsList.this, StartRun.class);
		              intent.putExtra("subgoal",  selectedSubgoal.getId());
		              intent.putExtra("totalDistance",  goal.getKm());
		              intent.putExtra("totalDistanceWalking", selectedSubgoal.getKmTotalWalking());
		              intent.putExtra("totalDistanceRunning", selectedSubgoal.getKmTotalRunning());
		              intent.putExtra("partialDistanceWalking", selectedSubgoal.getKmPartialWalking());
		              if(selectedSubgoal.getId() == (subgoals.size() - 1))
		              	intent.putExtra("partialDistanceRunning", goal.getKm());
		              else
		              	intent.putExtra("partialDistanceRunning", selectedSubgoal.getKmPartialRunning());
		              startActivityForResult(intent,RUNNING_RESULTS_REQUEST);
					}
				});

			  if(position == (subgoals.size() - 1)){
				btnRight.setVisibility(View.INVISIBLE);
			  }else if(position == 0){
				btnLeft.setVisibility(View.INVISIBLE);
			  }else{
				btnRight.setVisibility(View.VISIBLE);
				btnLeft.setVisibility(View.VISIBLE);
			  }

			  updateStatsView(subgoalPager.getCurrentItem());
			  updateCard(position);

			  parent.addView(rootView);
		      return rootView;
		  }

		  @Override
		  public void destroyItem(ViewGroup container, int position, Object object) {
		      container.removeView((View)object);
		  }
	}
}