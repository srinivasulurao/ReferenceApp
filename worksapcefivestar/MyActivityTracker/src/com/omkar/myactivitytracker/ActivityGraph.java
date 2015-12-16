package com.omkar.myactivitytracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


import com.personaltrainer.database.LoginDB;
import com.personaltrainer.model.ActivityPoints;

import com.personaltrainer.widgets.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityGraph extends Activity {

	public ActionBar actionBar;

	private GraphicalView mChart;
	LoginDB logDB;
	String sFrom="";
	boolean isData=false;
	Date date1=null, date2=null;
	LinearLayout chart_container,chart_container1;
	List<ActivityPoints> mList=new ArrayList<ActivityPoints>();
	double morningPoints, noonPoints, eveningPoints, nightPoints;
	private String[] mMonth = new String[] {"Morning", "Afternoon" , "Evening", "Night"};
	TextView tv;
	Long date;
	Cursor cursor;
	String mDate="";
	
	void initilizeUI()
	{
		logDB = new LoginDB(this);
		mList = logDB.getActivityPoints();
		sFrom = getIntent().getStringExtra("from");
		mDate = getIntent().getStringExtra("DATE");
		actionBar = getActionBar();
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.achart);

		initilizeUI();

		if(sFrom.equalsIgnoreCase(Utils.strMorning))
		{
			actionBar.setTitle("Points for Morning Activity");
			cursor = logDB.getMorningPoints();
		}
		else if(sFrom.equalsIgnoreCase(Utils.strNoon))
		{
			actionBar.setTitle("Points for Afternoon Activity");
			cursor = logDB.getNoonPoints();
		}
		/*else if(sFrom.equalsIgnoreCase(Utils.strEvening))
		{
			actionBar.setTitle("Points for Evening Activity");
			cursor = logDB.getEveningPoints();
		}*/
		else if(sFrom.equalsIgnoreCase(Utils.strNight))
		{
			actionBar.setTitle("Points for Night Activity");
			cursor = logDB.getNightPoints();
		}
		
		if(cursor.getCount()>0)
		{
			
			LoadActivityGraph(cursor);
		}
		else
		{
			Utils.showAlertBoxSingle(getApplicationContext(), "Warning", "NO Data Found.");
		}
	}

	void LoadActivityGraph(Cursor cursor)
	{
		List<Double> income=new ArrayList<Double>(); 
		List<Double> expense=new ArrayList<Double>(); 
		List<String> mMonth=new ArrayList<String>(); 

		/*mMonth.add("");
		income.add(0.0);
		expense.add(0.0);*/

		int count = 0;
		
		XYSeries incomeSeries = new XYSeries("My Points");
		XYSeries expenseSeries = new XYSeries("Max Points");
		
		

		if (cursor.moveToFirst()) 
		{
			do
			{
				String sName = cursor.getString(1);
				String sPoints = cursor.getString(2);
				String sDate = cursor.getString(3);
				
				Date d1 = Utils.stringTodate(ActivityGraph.this, sDate);
				Date d2 = Utils.stringTodate(ActivityGraph.this, mDate);

				if(d1.equals(d2))
				{
					mMonth.add(sName);
					income.add(Double.parseDouble(sPoints));
					expense.add(100.0);
					count=count+1;
				}
				
			}while (cursor.moveToNext());
		}

		if(count!=0)
		{
		for(int i=0;i<count;i++){
			incomeSeries.add(i,income.get(i));
			expenseSeries.add(i,expense.get(i));
		}
	
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(incomeSeries);
		dataset.addSeries(expenseSeries);

		XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
		incomeRenderer.setColor(Color.rgb(130, 130, 230));
		incomeRenderer.setFillPoints(true);
		incomeRenderer.setLineWidth(2);
		incomeRenderer.setDisplayChartValues(true);
		incomeRenderer.setChartValuesTextSize(20);

		XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
		expenseRenderer.setColor(Color.rgb(220, 80, 80));
		expenseRenderer.setFillPoints(true);
		expenseRenderer.setLineWidth(2);
		expenseRenderer.setDisplayChartValues(true);
		expenseRenderer.setChartValuesTextSize(20);

		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setXLabels(0);
		multiRenderer.setZoomButtonsVisible(true);
		multiRenderer.setClickEnabled(true);
		multiRenderer.setBarWidth(30);
		multiRenderer.setBarSpacing(0.0);
		multiRenderer.setPointSize(20);
		multiRenderer.setLabelsTextSize(20);
		
		multiRenderer.setLegendTextSize(20);

		for(int i=0; i< count;i++){
			multiRenderer.addXTextLabel(i, mMonth.get(i));
		}

		multiRenderer.addSeriesRenderer(incomeRenderer);
		multiRenderer.addSeriesRenderer(expenseRenderer);

		chart_container1 = (LinearLayout)findViewById(R.id.Chart_layout);
		GraphicalView mChart = (GraphicalView)ChartFactory.getBarChartView(getApplicationContext(), dataset, multiRenderer, Type.DEFAULT);
		chart_container1.addView(mChart);
		}
		else
		{
			Utils.showAlertBoxSingle(ActivityGraph.this, "Warning", "No Data Found for this date.");
		}

	}
}
