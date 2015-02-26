package br.com.makemerun.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import br.com.makemerun.R;
import br.com.makemerun.database.GoalDB;
import br.com.makemerun.model.Goal;
import br.com.makemerun.model.Subgoal;

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
		final double kmGoal = (double)bundle.getInt("kmGoal");
		final double kmTest = bundle.getDouble("kmRunning");
		final double avgSpeed = bundle.getDouble("avgSpeed");
		final double sdSpeed = bundle.getDouble("sdSpeed");

		TextView txResults = (TextView) findViewById(R.id.txResults);
		TextView btnAction = (TextView) findViewById(R.id.btnAction);
		TextView txRunningTime = (TextView) findViewById(R.id.txRunningTime);
		TextView txKmRunning = (TextView) findViewById(R.id.txKmRunning);

		String msg = null;

		if(kmTest >= (kmGoal - 0.1)){
			msg = getString(R.string.description_test_results_goal_completed);
			result = TEST_RESULT_GOAL_COMPLETED;
		}else if(kmTest < 0.1){
			msg = getString(R.string.description_test_results_under_recommended);
			result = TEST_RESULT_UNDER_RECOMMENDED;
		}else{
			msg = String.format(getString(R.string.description_test_results_ok), String.format("%.2f", kmTest) + "km");
			result = TEST_RESULT_OK;
		}

		txResults.setText(msg);
		txRunningTime.setText(time);
		txKmRunning.setText(String.format("%.2f", kmTest) + "km");

		btnAction.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view){
				Intent intent;

				if(result == TEST_RESULT_GOAL_COMPLETED){
					intent = new Intent(PostTest.this,ChooseGoal.class);
				}else if(result == TEST_RESULT_UNDER_RECOMMENDED){
					intent = new Intent(PostTest.this,ChooseGoal.class);
				}else{
					Goal goal = new Goal(kmGoal, kmTest, avgSpeed, sdSpeed, -1);
					goal.setCurrent(true);
					goal.setSubgoals(makePlan(kmGoal, kmTest));
					GoalDB db = new GoalDB(PostTest.this);
					db.insertGoal(goal);

					intent = new Intent(PostTest.this,SubgoalsList.class);
				}
				startActivity(intent);
			}
		});
	}
	
	private final int N_SUBGOALS = 4;
	
	private List<Subgoal> makePlan(double kmGoal, double kmTest){
		final double tax = (double)1 / (double)N_SUBGOALS;
		final int nSprints = (int) Math.floor(kmGoal/kmTest);		
		final double kmTotalRunning = Math.ceil((double)nSprints / (double)2) * kmTest;
		final double kmTotalWalking = (kmGoal - kmTotalRunning);

		final double increaseTax = kmTotalWalking * tax;
		final double walkingPartialRef = kmTotalWalking/(Math.ceil(nSprints - Math.ceil(nSprints / 2)));

		List<Subgoal> subgoals = new ArrayList<Subgoal>();
		
		for(int i = 0; i <= N_SUBGOALS; i++){
			Subgoal subgoal = new Subgoal();

			subgoal.setId(i);
			subgoal.setKmTotalWalking(kmTotalWalking - i * increaseTax);
			subgoal.setKmTotalRunning(kmTotalRunning + i * increaseTax);

			subgoal.setKmPartialRunning(kmTest + walkingPartialRef*i*tax);
			subgoal.setKmPartialWalking(walkingPartialRef - walkingPartialRef*i*tax);

			subgoal.setCompleted(false);
			subgoal.setLast(false);

			subgoals.add(subgoal);
		}

		return subgoals;
	}

}
