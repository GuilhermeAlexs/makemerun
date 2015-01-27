package com.br.makemerun.view.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class GoalCompletionView extends View{
	private RectF rectF;
	private Paint p;

	public GoalCompletionView(Context context) {
		super(context);
		rectF = new RectF(50, 20, 100, 80);
		p = new Paint();

		p.setAntiAlias(true);
		p.setColor(Color.RED);
		p.setStyle(Paint.Style.STROKE); 
		p.setStrokeWidth(5);
 	}
 
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.CYAN);

		//p.setAlpha(0x80);
		//rectF.set(canvas., top, right, bottom);
		canvas.drawArc (rectF, 90, 45, true, p);
	}
}
