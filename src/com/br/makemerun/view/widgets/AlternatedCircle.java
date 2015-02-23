package com.br.makemerun.view.widgets;

import com.br.makemerun.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class AlternatedCircle extends View
{
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint runSectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint walkSectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float strokeWidth;
    private float runDistance = 500;
    private float walkDistance = 500;
    private float totalDistance = 4000;
    private RectF circleBounds = new RectF (10, 10, 400, 400);

    private final int STROKE_WIDTH = 1;

	public AlternatedCircle(Context context) {
        super(context);
		init(null, 0);
	}

	public AlternatedCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public AlternatedCircle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	public void init(AttributeSet attrs, int style){
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.AlternatedCircle, style, 0);

		String color;

		color = a.getString(R.styleable.AlternatedCircle_runSectionColor);
		if(color == null)
			runSectionPaint.setColor(Color.parseColor("#ff9900"));
		else
			runSectionPaint.setColor(Color.parseColor(color));

		color = a.getString(R.styleable.AlternatedCircle_walkSectionColor);
		if(color == null)
			runSectionPaint.setColor(Color.parseColor("#000000"));
		else
			walkSectionPaint.setColor(Color.parseColor(color));

		color = a.getString(R.styleable.AlternatedCircle_backgroundColor);
		if(color == null)
			runSectionPaint.setColor(Color.parseColor("#000000"));
		else
			backgroundPaint.setColor(Color.parseColor(color));

		strokeWidth = a.getInt(R.styleable.AlternatedCircle_strokeWidth, STROKE_WIDTH);

		a.recycle();
	}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float angleRun = (360*runDistance)/totalDistance;
        float angleWalk = (360*walkDistance)/totalDistance;
        float currAngle = -90;
        int type = 0;

        while(currAngle <= 360){
        	if(type == 0){
        		canvas.drawArc(circleBounds, currAngle, angleRun, true, runSectionPaint);
        		currAngle = angleRun + currAngle;
        		type = 1;
        	}else{
        		canvas.drawArc(circleBounds, currAngle, angleWalk, true, walkSectionPaint);
        		currAngle = angleWalk + currAngle;
        		type = 0;
        	}
        }

        float sectionWidth = ((circleBounds.right - circleBounds.left)/(float)2)*(Math.abs(1 - (strokeWidth/(float)100)));
        canvas.drawCircle(circleBounds.centerX(), circleBounds.centerY(), sectionWidth, backgroundPaint);
        super.onDraw(canvas);
    }

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		final int min = Math.min(width, height);
		setMeasuredDimension(min+2*((int)STROKE_WIDTH), min+2*((int)STROKE_WIDTH));

		circleBounds.set(STROKE_WIDTH, STROKE_WIDTH, min + STROKE_WIDTH, min + STROKE_WIDTH);
	}

	public float getRunDistance() {
		return runDistance;
	}
	public void setRunDistance(float runDistance) {
		this.runDistance = runDistance;
		invalidate();
	}
	public float getWalkDistance() {
		return walkDistance;
	}
	public void setWalkDistance(float walkDistance) {
		this.walkDistance = walkDistance;
		invalidate();
	}
	public float getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(float totalDistance) {
		this.totalDistance = totalDistance;
		invalidate();
	}
}
