package com.omkar.myactivitytracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


import com.personaltrainer.database.LoginDB;
import com.personaltrainer.model.ActivityPoints;

import com.personaltrainer.widgets.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;



public class StatGraph extends Activity {

	private GraphicalView mChart;
	private String[] mMonth = new String[] {"0", "Morning", "Afternoon" , "Evening", "Night"};

	Date date1=null, date2=null;
	double morningPoints, noonPoints, eveningPoints, nightPoints;
	LoginDB logDB;
	List<ActivityPoints> mList=new ArrayList<ActivityPoints>();
	boolean isData=false;
	String sFrom="";

	void initilizeUI()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Todays Statictics");

		logDB = new LoginDB(this);
		sFrom = getIntent().getStringExtra("from");

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.achart);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		
		initilizeUI();

		if(sFrom.equalsIgnoreCase(Utils.TODAYS_GRAPH))
		{
			ShowTodaysStat();
		}
		else
		{
			showPreviousStat();
		}





	}

	void showPreviousStat()
	{

		Calendar c = Calendar.getInstance();
		Date todaysDate = c.getTime();
		SimpleDateFormat df =new SimpleDateFormat("dd/MMM/yyyy");
		String date = df.format(todaysDate);

		mList = logDB.getActivityPoints();
		for(int i=0; i<mList.size(); i++)
		{
			ActivityPoints am = mList.get(i);
			String sDate = am.getDate();

			morningPoints = Double.parseDouble(am.getMorning_points());
			noonPoints = Double.parseDouble(am.getNoon_points());
			eveningPoints = Double.parseDouble(am.getEvening_points());
			nightPoints = Double.parseDouble(am.getNight_points());

			ShowChart();


		}


	}

	void ShowTodaysStat()
	{
		Calendar c = Calendar.getInstance();
		Date todaysDate = c.getTime();
		SimpleDateFormat df =new SimpleDateFormat("dd/MMM/yyyy");
		String date = df.format(todaysDate);

		mList = logDB.getActivityPoints();
		for(int i=0; i<mList.size(); i++)
		{
			ActivityPoints am = mList.get(i);
			String sDate = am.getDate();

			try{
				date1 = df.parse(date);
				date2 = df.parse(sDate);
			}catch(Exception e){}

			if(date1.equals(date2))
			{
				morningPoints = Double.parseDouble(am.getMorning_points());
				noonPoints = Double.parseDouble(am.getNoon_points());
				eveningPoints = Double.parseDouble(am.getEvening_points());
				nightPoints = Double.parseDouble(am.getNight_points());

				ShowChart();

			}
			else
			{

			}
		}

	}



	private void ShowChart()
	{

		int z[]={0,1,2,3,4};
		double x[]={0.0,morningPoints,noonPoints,eveningPoints,nightPoints};

		XYSeries xSeries=new XYSeries("");


		for(int i=0;i<z.length;i++)
		{
			xSeries.add(z[i],x[i]);

		}

		XYMultipleSeriesDataset dataset=new XYMultipleSeriesDataset();

		dataset.addSeries(xSeries);


		XYSeriesRenderer Xrenderer=new XYSeriesRenderer();
		Xrenderer.setColor(Color.BLACK);
		Xrenderer.setPointStyle(PointStyle.CIRCLE);
		Xrenderer.setDisplayChartValues(true);
		Xrenderer.setLineWidth(2);
		Xrenderer.setFillPoints(true);

		XYMultipleSeriesRenderer mRenderer=new XYMultipleSeriesRenderer();

		mRenderer.setChartTitle("X Vs Y Chart");
		mRenderer.setXTitle("X Values");
		mRenderer.setYTitle("Y Values");
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setXLabels(0);
		mRenderer.setPanEnabled(false);

		mRenderer.setShowGrid(true);

		mRenderer.setAxisTitleTextSize(16);
		mRenderer.setLabelsTextSize(20);

		mRenderer.setClickEnabled(true);

		for(int i=0;i<z.length;i++)
		{
			mRenderer.addXTextLabel(i, mMonth[i]);
		}

		mRenderer.addSeriesRenderer(Xrenderer);


		LinearLayout chart_container=(LinearLayout)findViewById(R.id.Chart_layout);
		mChart=(GraphicalView)ChartFactory.getLineChartView(getBaseContext(), dataset, mRenderer);

		mChart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				SeriesSelection series_selection=mChart.getCurrentSeriesAndPoint();

				if(series_selection!=null)
				{
					int series_index=series_selection.getSeriesIndex();

					String select_series="X Series";
					if(series_index==0)
					{
						select_series="X Series";
					}else
					{
						select_series="Y Series";
					}

					String month=mMonth[(int)series_selection.getXValue()];

					int amount=(int)series_selection.getValue();

					Toast.makeText(getBaseContext(), select_series+"in" + month+":"+amount, Toast.LENGTH_LONG).show();
				}
			}
		});

		chart_container.addView(mChart);


	}
}
