package com.omkar.myactivitytracker;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.devadvance.circularseekbar.CircularSeekBar;

import com.personaltrainer.database.LoginDB;
import com.personaltrainer.model.ActivityPoints;

import com.personaltrainer.tabs.TabLayoutActivity;
import com.personaltrainer.widgets.Utils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Dashboard extends Activity implements OnClickListener {

	RelativeLayout rel_dash_first, rel_dash_second, rel_dash_first_,rel_dash_second_;
	LoginDB logDB;
	Intent in_Activity;
	CircularSeekBar seekBar;
	TextView txtProgress;
	TextView txtDate,txt,txtMaxPoints;
	Handler handler = new Handler();
	int var=0;
	int var_=0;
	ProgressDialog pDialog;


	@SuppressLint("NewApi")
	void initilizeUI()
	{
		File sd = new File(Environment.getExternalStorageDirectory()+"/"+Utils.appName_);
		if(!sd.exists())
		{
			sd.mkdir();
		}
		
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(Utils.appName);
		actionBar.setIcon(R.drawable.actionbaricon);

		rel_dash_first = (RelativeLayout)findViewById(R.id.rel_dash_first);
		rel_dash_second = (RelativeLayout)findViewById(R.id.rel_dash_second);
		rel_dash_first_ = (RelativeLayout)findViewById(R.id.rel_dash_first_);
		rel_dash_second_ = (RelativeLayout)findViewById(R.id.rel_dash_second_);

		txtProgress = (TextView)findViewById(R.id.seekArcProgress);
		seekBar = (CircularSeekBar)findViewById(R.id.circularSeekBar1);
		txtDate = (TextView)findViewById(R.id.date);
		txt = (TextView)findViewById(R.id.txt);
		txtMaxPoints = (TextView)findViewById(R.id.txtMaxPoints);

	}

	//#880174DF
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

		initilizeUI();

		rel_dash_first.setOnClickListener(this);
		rel_dash_second.setOnClickListener(this);
		rel_dash_first_.setOnClickListener(this);
		rel_dash_second_.setOnClickListener(this);

	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		BackgroundTask bg = new BackgroundTask();
		bg.execute("");

		seekBar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		
	}

	/*
	 * Calculate the Points..
	 */
	private class BackgroundTask extends AsyncTask<String, Void, String>
	{

		int OverallPoints,achievedPoints;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			
			
		}
		@Override
		protected String doInBackground(String... params) {
			
			initilizePoints();
			
			//Calculate Individual Activity Points:
			Utils.MorningPoints(Dashboard.this);
			Utils.NoonPoints(Dashboard.this);
			Utils.NightPoints(Dashboard.this);
			Utils.ToDoPoints(Dashboard.this);

			OverallPoints = Utils.OverallPoints;
			achievedPoints = Utils.achievedPoints;
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			seekBar.setMax(OverallPoints);
			seekBar.setProgress(achievedPoints);
			txtProgress.setText(Integer.toString(achievedPoints));
			txtMaxPoints.setText("out of "+Integer.toString(OverallPoints)+" pts.");

			if(Utils.currentDateOnDashboard != null)
			{
				txt.setText("Achieved Points. "+Utils.currentDateOnDashboard);
			}
			else
			{
				txt.setText("Achieved Points.");
			}
		}
		
	}
	
	void initilizePoints()
	{
		Utils.total_count = 0;
		//Utils.currentDateOnDashboard="";

		Utils.OverallPoints = 0;
		Utils.achievedPoints=0;

		Utils.morning_achieved_points=0;
		Utils.morning_overall_points=0;

		Utils.noon_achieved_points=0;
		Utils.noon_overall_points=0;

		Utils.evening_overall_points=0;
		Utils.evening_achieved_points=0;

		Utils.night_overall_points=0;
		Utils.night_achieved_points=0;
		
		Utils.todo_achieved_points=0;
		Utils.todo_overall_points=0;
		Utils.todo_add_points=0;
	}

	void showDialogBox()
	{

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Complete the Day.");
		alertDialog.setMessage("Would you like to add message to your previous day("+Utils.currentDateOnDashboard+")");

		alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {

				dialog.cancel();
				Intent in = new Intent(Dashboard.this,DayEnd.class);
				in.putExtra("from", "previous");
				startActivity(in);
			}
		});

		alertDialog.setNegativeButton("SKIP", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();
			}
		});
		alertDialog.show();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.rel_dash_first:

			in_Activity = new Intent(Dashboard.this, TabLayoutActivity.class);
			in_Activity.putExtra("from",Utils.tab);
			startActivity(in_Activity);


			break;

		case R.id.rel_dash_second:
			in_Activity = new Intent(Dashboard.this,History.class);
			startActivity(in_Activity);

			break;

		case R.id.rel_dash_first_:
			in_Activity = new Intent(Dashboard.this,Settings.class);
			startActivity(in_Activity);
			break;

		case R.id.rel_dash_second_:
			in_Activity = new Intent(Dashboard.this,Notes.class);
			startActivity(in_Activity);
			break;
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.splash, menu);

		MenuItem item = menu.findItem(R.id.action_add);
		item.setVisible(false);
		
		



		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) 
		{
		case R.id.action_rate:
			Intent in = new Intent(Dashboard.this, DayEnd.class);
			in.putExtra("from", "today");
			startActivity(in);
			break;

		}
		return true;
	}

	void setProgressColor(int progress)
	{/*
		if(progress>=0 && progress<10)
		{
			seekBar.setCircleProgressColor(getResources().getColor(R.color.blue1));
		}
		else if( progress>10 && progress<20)
		{
			seekBar.setCircleProgressColor(getResources().getColor(R.color.blue2));
		}
		else if( progress>30 && progress<40)
		{
			seekBar.setCircleProgressColor(getResources().getColor(R.color.blue3));
		}
		else if( progress>50 && progress<60)
		{
			seekBar.setCircleProgressColor(getResources().getColor(R.color.blue4));
		}
		else if( progress>70&& progress<80)
		{
			seekBar.setCircleProgressColor(getResources().getColor(R.color.blue5));
		}
		else if( progress>90 && progress<100)
		{
			seekBar.setCircleProgressColor(getResources().getColor(R.color.blue6));
		}
	*/}

	/*
	 * Smooth Progress for Circular Seek Bar..
	 */
	private Runnable seekBarProgress = new Runnable() 
	{
		public void run() 
		{
			int iTime = 50;
			seekBar.setProgress(var_);

			var_=var_+10;
			txtProgress.setText(Integer.toString(var_));
			setProgressColor(var_);
			handler.postDelayed(this,iTime); 

			if(var_>=var)
			{
				handler.removeCallbacks(seekBarProgress);
			}
		}
	};

}
