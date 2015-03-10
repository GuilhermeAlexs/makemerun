package br.com.makemerun.view;

import java.util.HashMap;

import br.com.makemerun.R;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

public class AppTracker extends Application{
	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	/**
	   * Enum used to identify the tracker that needs to be used for tracking.
	   *
	   * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
	   * storing them all in Application object helps ensure that they are created only once per
	   * application instance.
	   */
	  public enum TrackerName {
	    APP_TRACKER, // Tracker used only in this app.
	    GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
	    ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	  }

	  synchronized Tracker getTracker(TrackerName trackerId) {
		    if (!mTrackers.containsKey(trackerId)) {
		      GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		      analytics.setLocalDispatchPeriod(500);
		      analytics.setDryRun(false);
		      analytics.getLogger().setLogLevel(Logger.LogLevel.INFO);
		      Tracker t = analytics.newTracker(R.xml.app_tracker);
		      mTrackers.put(trackerId, t);
		    }

		    return mTrackers.get(trackerId);
	  }
}
