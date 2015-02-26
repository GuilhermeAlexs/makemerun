package br.com.makemerun.view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.makemerun.R;
import br.com.makemerun.model.ChangeTimeListener;
import br.com.makemerun.service.ChangeLocationListener;
import br.com.makemerun.service.MapService;
import br.com.makemerun.service.MapService.LocalBinder;
import br.com.makemerun.view.widgets.CircularProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class RunTest extends Activity implements ChangeLocationListener,
		ChangeTimeListener {

	private TextView startStopButton;
	private TextView timerValue;
	private TextView distanceValue;
	private ImageView stateIcon;
	private CircularProgressBar kmPartialProgress;
	private boolean mBound = false;
	private MapService mapService;
	private double runDistance = 0;
	private List<Double> speedList;
	AlertDialog providerAlertDialog;
	AlertDialog gpsSignalAlertDialog;

	private final int NUM_SAMPLES_SPEED = 4;

	private int km;

	private boolean started = false;
	private long totalTime = 0;
	
	private InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_test);

	    interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId(MarketingConfig.adUnitId);

	    AdRequest adRequest = new AdRequest.Builder().build();

	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);

		km = getIntent().getIntExtra("goal", 0);

		speedList = new ArrayList<Double>();
		startStopButton = (TextView) findViewById(R.id.btnStartStop);
		timerValue = (TextView) findViewById(R.id.txTimerValue);
		distanceValue = (TextView) findViewById(R.id.txDistance);
		stateIcon = (ImageView) findViewById(R.id.icState);
		kmPartialProgress = (CircularProgressBar) findViewById(R.id.progressKmPartial);

		kmPartialProgress.setMax((int)Math.round(km*1000));

		stateIcon.setBackgroundResource(R.drawable.runicon);

		setProviderPopup();
		setGpsSignalPopup();
		startStopButton.setText(getString(R.string.button_start));

		startStopButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(!started){
					if (canStart()) {
						started = true;
						mapService.startMapping();
						startStopButton.setText(RunTest.this.getString(R.string.button_stop));
					} else {
						gpsSignalAlertDialog.show();
					}
				}else{
					started = false;
					mapService.stopMapping();
					startStopButton.setText(RunTest.this.getString(R.string.button_start));

					Intent intent = new Intent(view.getContext(),
							PostTest.class);

					intent.putExtra("time", timerValue.getText());
					intent.putExtra("kmGoal", km);
					intent.putExtra("kmRunning", runDistance);
					intent.putExtra("avgSpeed", runDistance/((double)totalTime/(double)3600));
					intent.putExtra("sdSpeed", getSpeedStandardDeviation());
					startActivity(intent);
				}
			}
		});
	}

    public void displayInterstitial() {
      if (interstitial.isLoaded()) {
        interstitial.show();
      }
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
		Intent intent = new Intent(this, MapService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	@Override
	public void onBackPressed() {
		// finish() is called in super: we only override this method to be able
		// to override the transition
		super.onBackPressed();
		this.mapService.stopMapping();
		overridePendingTransition(R.drawable.activity_back_in,
				R.drawable.activity_back_out);
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mapService = binder.getService();
			mBound = true;
			mapService.setChangeLocationListener(RunTest.this);
			mapService.setChangeTimeListener(RunTest.this);
			if (!mapService.isProviderEnabled()) {
				if (gpsSignalAlertDialog.isShowing()) {
					gpsSignalAlertDialog.hide();
				}
				providerAlertDialog.show();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
		}
	};

	@Override
	public void onChangeLocation(List<Location> path) {
		double distance = 0;
		double speed = 0;
		DecimalFormat df = new DecimalFormat("0.00");

		distance = getDistance(path);
		kmPartialProgress.setProgress((int)Math.round(distance));

		distance = distance / 1000;
		runDistance = distance;
		distanceValue.setText(df.format(distance) + "km");

		if (path.size() > 0 && path.size() % NUM_SAMPLES_SPEED == 0) {
			speed = getCurrentSpeed(path);
			speedList.add(speed);
		}
	}

	@Override
	public void onChangeTime(long mili) {
		int secs = (int) (mili / 1000);
		totalTime = secs;
		int mins = secs / 60;
		secs = secs % 60;
		int hours = mins / 60;
		timerValue.setText("" + String.format("%02d", hours) + ":"
				+ String.format("%02d", mins) + ":"
				+ String.format("%02d", secs));
	}

	@Override
	public void onChangeProviderState(int state) {
		if (state == MapService.PROVIDER_DISABLED) {
			mapService.stopGPS();
			if (gpsSignalAlertDialog.isShowing()) {
				gpsSignalAlertDialog.hide();
			}
			providerAlertDialog.show();
		} else {
			if (providerAlertDialog.isShowing()) {
				providerAlertDialog.hide();
				mapService.startGPS();
			}
		}
	}

	@Override
	public void onAcquiredGpsSignal() {
		if (gpsSignalAlertDialog.isShowing()) {
			gpsSignalAlertDialog.hide();
		}
	}

	private boolean canStart() {
		if (mapService.hasGpsSignal()) {
			return true;
		}
		return false;
	}

	private double getCurrentSpeed(List<Location> path) {

		double speed = 0;
		List<Location> samples = new ArrayList<Location>();
		double distance = 0;
		double deltat;
		ListIterator<Location> iterator;

		iterator = path.listIterator(path.size() - 4);
		while (iterator.hasNext()) {
			samples.add(iterator.next());
		}

		iterator = samples.listIterator();
		while (iterator.hasNext()) {
			Location currentLoc = (Location) iterator.next();
			if (!iterator.hasNext())
				break;
			Location nextLoc = (Location) iterator.next();
			distance = currentLoc.distanceTo(nextLoc) / 1000;
			deltat = nextLoc.getElapsedRealtimeNanos()
					- currentLoc.getElapsedRealtimeNanos();
			deltat = ((deltat / 1000000000) / 60) / 60;
			if (deltat > 0)
				speed += (distance / deltat);
		}
		speed = speed / 3;
		return speed;
	}

	private double getDistance(List<Location> path) {
		double distance = 0;
		Location oldLoc = path.get(0);

		for (Location loc : path) {
			distance += loc.distanceTo(oldLoc);
			oldLoc = loc;
		}
		return distance;
	}

	private double getAvgSpeed() {
		double avgSpeed = 0;

		Collections.sort(speedList, new Comparator<Double>() {
			@Override
			public int compare(Double speed1, Double speed2) {
				if (speed1 > speed2) {
					return 1;
				} else if (speed1 == speed2) {
					return 0;
				}
				return -1;
			}
		});

		if (speedList.size() == 0) {
			return 0;
		} else if (speedList.size() % 2 == 0) {
			avgSpeed = speedList.get(speedList.size() / 2)
					+ speedList.get(speedList.size() / 2 - 1);
			avgSpeed = avgSpeed / 2;
		} else {
			avgSpeed = speedList.get((int) Math.floor(speedList.size() / 2));
		}

		return avgSpeed;
	}

	private double getSpeedStandardDeviation() {
		Double avgSpeed = getAvgSpeed();
		Double sum = 0d;

		if (speedList.size() == 0)
			return 0;

		for (Double speed : speedList) {
			sum += (avgSpeed - speed) * (avgSpeed - speed);
		}

		return Math.sqrt(sum / speedList.size());
	}

	private void setProviderPopup() {
		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(getString(R.string.title_gps_off));
		alertDialogBuilder.setMessage(getString(R.string.description_turn_on));
		alertDialogBuilder.setCancelable(true);
		Button tryAgain = new Button(this);
		tryAgain.setText(getString(R.string.button_try_again));
		tryAgain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mapService != null && mapService.isProviderEnabled()) {
					providerAlertDialog.hide();
					mapService.startGPS();
				}
			}
		});
		alertDialogBuilder.setView(tryAgain);
		providerAlertDialog = alertDialogBuilder.create();
	}

	private void setGpsSignalPopup() {
		AlertDialog.Builder alertDialogBuilder;
		alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(getString(R.string.title_looking_satellites));
		alertDialogBuilder.setMessage(getString(R.string.description_almost_there));
		alertDialogBuilder.setCancelable(true);
		gpsSignalAlertDialog = alertDialogBuilder.create();
	}
}
