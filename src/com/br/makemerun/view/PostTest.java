package com.br.makemerun.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.br.makemerun.R;
import com.br.makemerun.database.GoalDB;
import com.br.makemerun.model.Goal;

public class PostTest extends Activity{
	private final int TEST_RESULT_OK = 1;
	private final int TEST_RESULT_UNDER_RECOMMENDED = 2;
	private final int TEST_RESULT_GOAL_COMPLETED = 3;
	private int result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_test);

		Bundle bundle = this.getIntent().getExtras();

		String time = bundle.getString("time");
		final int kmGoal = bundle.getInt("kmGoal");
		final double kmRunning = bundle.getDouble("kmRunning");
		final double avgSpeed = bundle.getDouble("avgSpeed");
		final double sdSpeed = bundle.getDouble("sdSpeed");

		TextView txResults = (TextView) findViewById(R.id.txResults);
		TextView btnAction = (TextView) findViewById(R.id.btnAction);
		TextView txRunningTime = (TextView) findViewById(R.id.txRunningTime);
		TextView txKmRunning = (TextView) findViewById(R.id.txKmRunning);

		String msg = null;

		if(kmRunning >= kmGoal){
			msg = getString(R.string.description_test_results_goal_completed);
			result = TEST_RESULT_GOAL_COMPLETED;
		}else if(kmRunning < 0.1){
			msg = getString(R.string.description_test_results_under_recommended);
			result = TEST_RESULT_UNDER_RECOMMENDED;
		}else{
			msg = String.format(getString(R.string.description_test_results_ok), kmRunning);
			result = TEST_RESULT_OK;
		}

		txResults.setText(msg);
		txRunningTime.setText(time);
		txKmRunning.setText(String.format("%.2f", kmRunning) + "km");

		btnAction.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view){
				Intent intent;

				if(result == TEST_RESULT_GOAL_COMPLETED){
					intent = new Intent(PostTest.this,ChooseGoal.class);
				}else if(result == TEST_RESULT_UNDER_RECOMMENDED){
					intent = new Intent(PostTest.this,ChooseGoal.class);
				}else{
					Goal goal = new Goal(kmGoal, kmRunning, avgSpeed, sdSpeed, -1);
					goal.setCurrent(true);
					GoalDB db = new GoalDB(PostTest.this);
					db.insertGoal(goal);

					intent = new Intent(PostTest.this,SubgoalsList.class);
				}
				startActivity(intent);
			}
		});
	}

}
