package com.br.makemerun.view;

import com.br.makemerun.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

public class PostRun extends Activity{
	private final int STATISTICS_REQUEST = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_run);

		Splash.voice.speak("Nice job! See you next training day.", TextToSpeech.QUEUE_FLUSH, null);

		Bundle bundle = this.getIntent().getExtras();

		final int subgoal = bundle.getInt("subgoal");
		final double totalDistanceRunning = bundle.getDouble("totalDistanceRunning");
		final double totalDistanceWalking = bundle.getDouble("totalDistanceWalking");
		final double partialDistanceRunning = bundle.getDouble("partialDistanceRunning");
		final double partialDistanceWalking = bundle.getDouble("partialDistanceWalking");
		final String time = bundle.getString("time");
		
		TextView txTime = (TextView) findViewById(R.id.txTime);
		TextView txKmTotalRunning = (TextView) findViewById(R.id.txKmTotalRunning);
		TextView txKmPartialRunning = (TextView) findViewById(R.id.txKmPartialRunning);
		TextView txKmTotalWalking = (TextView) findViewById(R.id.txKmTotalWalking);
		TextView txKmPartialWalking = (TextView) findViewById(R.id.txKmPartialWalking);
		
		txTime.setText(time);
		txKmTotalRunning.setText(String.format("%.2f",totalDistanceRunning) + "km");
		txKmPartialRunning.setText(String.format("%.2f",partialDistanceRunning) + "km");
		
		txKmTotalWalking.setText(String.format("%.2f",totalDistanceWalking) + "km");
		txKmPartialWalking.setText(String.format("%.2f",partialDistanceWalking) + "km");

		TextView btnSkip = (TextView) findViewById(R.id.btnSkip);
		TextView btnStats = (TextView) findViewById(R.id.btnStats);

		btnSkip.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		btnStats.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(PostRun.this,Statistics.class);
				intent.putExtra("subgoal", subgoal);
				startActivityForResult(intent, STATISTICS_REQUEST);
			}
		});		 
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == STATISTICS_REQUEST) {
	        if (resultCode == RESULT_OK) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
	        }
	    }
	}
}
