package com.br.makemerun.view;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.br.makemerun.R;
import com.br.makemerun.database.StatsDB;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Statistics extends Activity{
	private GraphicalView chartView;
	private XYSeries walkingSeries;
	private XYSeries runningSeries;
    private XYSeriesRenderer walkingRenderer;
    private XYSeriesRenderer runningRenderer;
    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

		Bundle bundle = this.getIntent().getExtras();
		int subgoal = bundle.getInt("subgoal");

        StatsDB db = new StatsDB(this);
        walkingSeries = db.getStatsBySubgoal(subgoal, StatsDB.WALKING_SPRINT);
        runningSeries = db.getStatsBySubgoal(subgoal, StatsDB.RUNNING_SPRINT);

        mDataset = new XYMultipleSeriesDataset();
        mDataset.addSeries(walkingSeries);
        mDataset.addSeries(runningSeries);

        walkingRenderer = new XYSeriesRenderer();
        walkingRenderer.setLineWidth(8);
        walkingRenderer.setFillPoints(true);
        walkingRenderer.setColor(Color.rgb(0, 153, 255));

        runningRenderer = new XYSeriesRenderer();
        runningRenderer.setLineWidth(8);
        runningRenderer.setFillPoints(true);
        runningRenderer.setColor(Color.rgb(255, 153, 0));

        mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(walkingRenderer);
        mRenderer.addSeriesRenderer(runningRenderer);

        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        mRenderer.setAxisTitleTextSize(16);
        mRenderer.setLabelsTextSize(20);
        mRenderer.setLegendTextSize(18);
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setXTitle(this.getString(R.string.description_distance));
        mRenderer.setYTitle(this.getString(R.string.description_speed));

        mRenderer.setPanEnabled(false, false);
     	mRenderer.setYAxisMax(runningSeries.getMaxY() + 10);
     	mRenderer.setYAxisMin(0);
     	mRenderer.setPanEnabled(true);
     	mRenderer.setPanEnabled(true, true);
     	mRenderer.setZoomEnabled(false,true);
     	mRenderer.setBarWidth(120);


     	if(runningSeries.getMaxX() >= walkingSeries.getMaxX()){
     		mRenderer.setXAxisMax(runningSeries.getMaxX());
     		//mRenderer.setPanLimits(new double[]{0,runningSeries.getMaxX() + 1,0,0});
     	}else{
     		mRenderer.setXAxisMax(walkingSeries.getMaxX());
     		//mRenderer.setPanLimits(new double[]{0,walkingSeries.getMaxX() + 1,0,0});
     	}

     	mRenderer.setXAxisMin(0);
     	mRenderer.setShowGrid(true);

     	LinearLayout graphLay = (LinearLayout) findViewById(R.id.layGraph);
        chartView = ChartFactory.getBarChartView(this, mDataset, mRenderer, Type.STACKED);
        graphLay.addView(chartView);

        TextView txSkip = (TextView) findViewById(R.id.txSkip);
        txSkip.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent();
				setResult(RESULT_OK,intent);
				finish();
			}
		});
    }
}
