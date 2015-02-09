package com.br.makemerun.view;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
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
	private XYSeries series;
    private XYSeriesRenderer renderer;
    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

		Bundle bundle = this.getIntent().getExtras();
		int subgoal = bundle.getInt("subgoal");
		
        StatsDB db = new StatsDB(this);
        series = db.getStatsBySubgoal(subgoal);

        mDataset = new XYMultipleSeriesDataset();
        mDataset.addSeries(series);

        renderer = new XYSeriesRenderer();
        renderer.setLineWidth(6);
        renderer.setColor(Color.rgb(0, 153, 255));
        renderer.setDisplayBoundingPoints(true);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(7);

        mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);

        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        mRenderer.setAxisTitleTextSize(16);
        mRenderer.setLabelsTextSize(20);
        mRenderer.setLegendTextSize(18);
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setXTitle(this.getString(R.string.description_distance));
        mRenderer.setYTitle(this.getString(R.string.description_speed));
        
        mRenderer.setPanEnabled(false, false);
     	mRenderer.setYAxisMax(series.getMaxY());
     	mRenderer.setYAxisMin(series.getMinY());
     	mRenderer.setXAxisMax(series.getMaxX());
     	mRenderer.setXAxisMin(series.getMinX());
     	mRenderer.setShowGrid(true);

     	LinearLayout graphLay = (LinearLayout) findViewById(R.id.layGraph);
        chartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
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
