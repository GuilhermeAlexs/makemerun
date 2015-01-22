package com.br.makemerun;

import com.br.makemerun.model.Timer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button startPauseButton;
	private TextView timerValue;
	public Boolean isStart = true;
	Timer timer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startPauseButton = (Button) findViewById(R.id.startButton);
		timer = new Timer(this.getApplicationContext(), 3, 6);
		startPauseButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(isStart){
					timer.start();
				}
				else{
					timer.pause();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
