package br.com.makemerun.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import br.com.makemerun.R;
import br.com.makemerun.model.MetricUtils;

public class ManualTestInput extends Activity{
	private TextView btnOk;
	private EditText inputDistance;
	private EditText inputPace;
	private TextView wrongDistance;
	private TextView wrongPace;
	private Vibrator vibrator;

	private final double DEFAULT_SPEED = 9;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manual_input);
		overridePendingTransition(R.drawable.activity_in, R.drawable.activity_out);

		Bundle bundle = this.getIntent().getExtras();
		final int kmGoal = bundle.getInt("goal");

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		btnOk = (TextView) findViewById(R.id.btnOk);
		wrongDistance = (TextView) findViewById(R.id.txWrongDistance);
		wrongPace = (TextView) findViewById(R.id.txWrongPace);
		inputDistance = (EditText) findViewById(R.id.inputDistanceTest);
		inputPace = (EditText) findViewById(R.id.inputPaceTest);

		btnOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(!validateFields()){
					vibrator.vibrate(100);
					return;
				}

				Intent intent = new Intent(ManualTestInput.this,PostTest.class);

				intent.putExtra("time", "--:--");
				intent.putExtra("kmGoal", kmGoal);
				intent.putExtra("kmRunning", Double.parseDouble(inputDistance.getText().toString()));
				
				if(inputPace.getText().toString().length() != 0)
					intent.putExtra("avgSpeed",  MetricUtils.convertPaceToSpeed(inputPace.getText().toString()));
				else
					intent.putExtra("avgSpeed", DEFAULT_SPEED);

				intent.putExtra("sdSpeed", 0);

				startActivity(intent);
			}
		});
	}

	private boolean validateFields(){
		boolean result = true;

		wrongDistance.setVisibility(View.INVISIBLE);
		wrongPace.setVisibility(View.INVISIBLE);

		if(inputDistance.getText().toString().length() == 0){
			wrongDistance.setText(getString(R.string.field_error_distance_required));
			wrongDistance.setVisibility(View.VISIBLE);
			result = false;
		}else{
			String distanceString = inputDistance.getText().toString();

			if(Double.parseDouble(distanceString) < 0.1){
				wrongDistance.setText(getString(R.string.field_error_distance_low_value));
				wrongDistance.setVisibility(View.VISIBLE);
				result = false;
			}
		}

		if(inputPace.getText().toString().length() != 0){
			String [] paceString = inputPace.getText().toString().split(":");

			if(inputPace.getText().toString().length() <= 3 || paceString.length > 2 || paceString.length < 2){
				wrongPace.setText(getString(R.string.field_error_invalid_pace));
				wrongPace.setVisibility(View.VISIBLE);
				result = false;
			}else{
				int min = Integer.parseInt(paceString[0]);
				int sec = Integer.parseInt(paceString[1]);

				if((min <= 0 && sec <= 0) || min > 60 || sec > 60){
					wrongPace.setText(getString(R.string.field_error_invalid_pace));
					wrongPace.setVisibility(View.VISIBLE);
					result = false;
				}
			}
		}

		return result;
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