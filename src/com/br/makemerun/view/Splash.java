package com.br.makemerun.view;

import com.br.makemerun.R;
import com.br.makemerun.database.GoalDB;
import com.br.makemerun.model.Goal;
import com.br.makemerun.view.widgets.CircularProgressBar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class Splash extends Activity {
	private CircularProgressBar progressLoading;
	private TextView txLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		progressLoading = (CircularProgressBar) this.findViewById(R.id.progressLoading);
		progressLoading.setMax(4);
		progressLoading.setProgress(0);
		
		txLoading = (TextView) this.findViewById(R.id.txLoading);
		new LoadingTask().execute();
	}

	@SuppressWarnings("rawtypes")
	class LoadingTask extends AsyncTask<Void, String, Class>{
		int progress = 0;

		@Override 
		protected Class doInBackground(Void... params) {
			Class nextView = null;
			try {
				GoalDB db = new GoalDB(getApplicationContext());

				publishProgress("Loading current goal...");

				Goal goal = db.getCurrentGoal();

				if(goal != null)
					nextView = SubgoalsList.class;
				else
					nextView = ChooseGoal.class;

				Thread.sleep(1000);
				publishProgress("Getting GPS first fix...");
				Thread.sleep(1000);
				publishProgress("Getting GPS first fix...");
				Thread.sleep(2000);
				publishProgress("Getting GPS first fix...");
				Thread.sleep(600);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return nextView;
		}

		@Override 
		protected void onProgressUpdate(String... values) {
			progress++;
			progressLoading.setProgress(progress);
			txLoading.setText(values[0]);
		}
		
		@Override
	    protected void onPostExecute(Class result) {
	       super.onPostExecute(result);
	       Intent intent = new Intent(Splash.this, result);
	       startActivity(intent);
	    }
	}
}


