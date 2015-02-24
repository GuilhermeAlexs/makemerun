package com.br.makemerun.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import com.br.makemerun.R;
import com.br.makemerun.view.widgets.AlternatedCircle;

public class TestDraw extends Activity{
	private int i = 0;
	private float run = 1000;
	private float walk = 1000;
	private float initWalk = 1000;
	private AlternatedCircle altCircle;
	private GestureDetector myG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_subgoal_card);
        altCircle = (AlternatedCircle) findViewById(R.id.circle);
        altCircle.setTotalDistance(4000);
        altCircle.setRunDistance(run);
        altCircle.setWalkDistance(walk);
        RelativeLayout lay = (RelativeLayout) findViewById(R.id.cardLayout);
        myG = new GestureDetector(this,new SwipeGestureDetector());
        lay.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				myG.onTouchEvent(event);
				return true;
			}
		});
    }

	class SwipeGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if((e1.getRawX() - e2.getRawX()) > 10){
				i++;
				
				if(i*0.25 > 1){
					i--;
					return true;
				}
				run = (float) (run + (initWalk*i*0.25));
				walk = (float) (walk - (initWalk*i*0.25));
			}else if((e2.getRawX() - e1.getRawX()) > 10){
				run = (float) (run - (initWalk*i*0.25));
				walk = (float) (walk + (initWalk*i*0.25));
				i--;
				if(i < 0)
					i = 0;
			}

		    altCircle.setRunDistance(run);
		    altCircle.setWalkDistance(walk);
			return false;
		}
	}
}
