package com.omkar.myactivitytracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.devadvance.circularseekbar.CircularSeekBar;
import com.devadvance.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener;
import com.personaltrainer.database.LoginDB;
import com.personaltrainer.model.ActivitiesModel;

import com.personaltrainer.tabs.TabLayoutActivity;
import com.personaltrainer.widgets.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Activities extends Activity implements OnClickListener, NumberPicker.OnValueChangeListener{

	public static ListView list_activity;
	Button btn_add_new, bRate;
	Intent in_AddNew;
	LoginDB logDB;
	String  sNewTime="", mId="", mPoints="";
	public static ActivitiesAdapter mAdapter;
	static TextView txtId, txtTime, txtPoints, txtMyPoints, txtName;
	private TimePicker timePicker;
	private int hour;
	private int minute;
	static final int TIME_DIALOG_ID = 111;
	List<ActivitiesModel> mList;
	ProgressDialog pDialog;
	BackgroundTask bg;


	void initilizeUI()
	{

		timePicker = (TimePicker)findViewById(R.id.timePicker1);
		timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
		list_activity = (ListView)findViewById(R.id.list_activity);
		logDB = new LoginDB(this);
		bg = new BackgroundTask();

	}







	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activities);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		initilizeUI();

		TabLayoutActivity tabparent;
		tabparent = (TabLayoutActivity) getParent();
		@SuppressWarnings("deprecation")
		int num = tabparent.getTabHost().getCurrentTab();
		Utils.currentTab = Integer.toString(num+1);

		//TestFunction();

	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		BackgroundTask bg = new BackgroundTask();
		bg.execute("");
		
		list_activity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int id,
					long position) {
				
				mList = new ArrayList<ActivitiesModel>();
				if(Utils.currentTab.equals(Utils.strMorning))    // Load Morning Activities.
				{
					mList = logDB.getActivityContacts();
				}
				else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
				{
					mList =logDB.getNoonActivityContacts();
				}
				/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
				{
					mList = logDB.getEveningActivityContacts();
				}*/
				else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
				{
					mList = logDB.getNightActivityContacts();
				}

				if(!mList.isEmpty())
				{
					txtName = (TextView)view.findViewById(R.id.txt_morning_activity);
					txtTime= (TextView)view.findViewById(R.id.txt_morning_time);
					txtId = (TextView)view.findViewById(R.id.txt_id);
					txtPoints = (TextView)view.findViewById(R.id.txt_mypoints);
					TextView txt_morning_time = (TextView)view.findViewById(R.id.txt_morning_time);
					TextView txt_morning_points = (TextView)view.findViewById(R.id.txt_morning_points);


					final String sName = txtName.getText().toString();
					final String sTime = txtTime.getText().toString();
					final String sId = txtId.getText().toString();
					final String sPoints = txtPoints.getText().toString();
					final String sQty = txt_morning_time.getText().toString();
					String my_pts = txt_morning_points.getText().toString();

					showDetailPopup(sId, sPoints, sName, sQty, my_pts);

				}


			}
		});
		
	}



	private class BackgroundTask extends AsyncTask<String, Void, String>
	{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mList = new ArrayList<ActivitiesModel>();
			
			pDialog = new ProgressDialog(Activities.this);
			pDialog.setMessage("Loading..");
			pDialog.setProgressStyle(pDialog.STYLE_SPINNER);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			
			if(Utils.currentTab.equals(Utils.strMorning))   
			{
				mList = logDB.getActivityContacts();
				
			}
			else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
			{
				mList =logDB.getNoonActivityContacts();
				
			}
			/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
			{
				mList = logDB.getEveningActivityContacts();
				
			}*/
			else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
			{
				mList = logDB.getNightActivityContacts();
				
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			pDialog.dismiss();
			if(mList.isEmpty())
			{
				ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("TAG", "Click plus button to Add Activities.");
				oslist.add(map);

				ListAdapter adapter = new SimpleAdapter(Activities.this, oslist, R.layout.nodata, new String[] {"TAG"}, 
						new int[] {R.id.txtNoData});

				list_activity.setAdapter(adapter);
			}
			else
			{
				mAdapter = new ActivitiesAdapter(Activities.this,mList);
				list_activity.setAdapter(mAdapter);
				
				Intent in = new Intent("com.omkar.myactivitytracker");
				sendBroadcast(in);
			}
		}
		
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:

			final Calendar c = Calendar.getInstance();
			hour = c.get(Calendar.HOUR_OF_DAY);
			minute = c.get(Calendar.MINUTE);
			return new TimePickerDialog(this, timePickerListener, hour, minute,true);

		}
		return null;
	}
	public TimePickerDialog.OnTimeSetListener timePickerListener =  new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

			hour = selectedHour;
			minute = selectedMinute;

			timePicker.setCurrentHour(hour);
			timePicker.setCurrentMinute(minute);

			if(hour == 0)
			{
				sNewTime = Integer.toString(minute)+" min";
			}
			else
			{
				sNewTime = Integer.toString(hour)+"."+Integer.toString(minute)+" hr";
			}


			if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
			{
				logDB.UpdateActivityTime(sNewTime, Integer.parseInt(txtId.getText().toString().trim()));
			}
			else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
			{
				logDB.UpdateNoonActivityTime(sNewTime, Integer.parseInt(txtId.getText().toString().trim()));
			}
			/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
			{
				logDB.UpdateEveningActivityTime(sNewTime, Integer.parseInt(txtId.getText().toString().trim()));
			}*/
			else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
			{
				logDB.UpdateNightActivityTime(sNewTime, Integer.parseInt(txtId.getText().toString().trim()));
			}


		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{

		}
	}

	public void myEditClickHandler(View v) 
	{
		RelativeLayout vwParentRow  = (RelativeLayout)v.getParent();
		txtName = (TextView)vwParentRow.getChildAt(1);
		txtTime= (TextView)vwParentRow.getChildAt(2);
		txtId = (TextView)vwParentRow.getChildAt(5);

		String sName = txtName.getText().toString();
		String sTime = txtTime.getText().toString();
		String sId = txtId.getText().toString();

		RelativeLayout relPoints = (RelativeLayout)vwParentRow.getChildAt(3);
		txtPoints = (TextView)relPoints.getChildAt(0);
		String sPoints = txtPoints.getText().toString();

		//	showDetailPopup(sId, sPoints, sName);



	}

	public static class ActivitiesAdapter extends BaseAdapter
	{
		private  LayoutInflater inflater=null;
		public Context mContext;

		LoginDB logDB;
		int num;
		List<ActivitiesModel> cn;

		public  class ViewHolder
		{
			public TextView txtActivity;
			public TextView txtTime;
			public TextView txtPoints;
			public TextView text_pts;
			public TextView txtId;
			public TextView txtMaxPts;
			public TextView txtCategory;
			public ImageView btnDetails;
			public RelativeLayout rel_main;
		}

		public ActivitiesAdapter(Context context, List<ActivitiesModel> mList)
		{
			mContext = context;
			logDB = new LoginDB(mContext);
			cn = mList;

			/*inflater = ( LayoutInflater )mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);*/
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			num = cn.size();
			if(num<=0)
				return 0;
			return num;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, final View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View vi = convertView;
			inflater = ( LayoutInflater )mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ViewHolder holder = new ViewHolder();
			if(convertView==null)
			{
				
				vi = inflater.inflate(R.layout.zrow_activity,null);

				holder.txtActivity = (TextView)vi.findViewById(R.id.txt_morning_activity);
				holder.txtTime = (TextView)vi.findViewById(R.id.txt_morning_time);
				holder.txtPoints = (TextView)vi.findViewById(R.id.txt_morning_points);
				holder.text_pts = (TextView)vi.findViewById(R.id.txt_mypoints);
				holder.txtId = (TextView)vi.findViewById(R.id.txt_id);
				holder.txtMaxPts = (TextView)vi.findViewById(R.id.txtMaxPts);
				holder.txtCategory = (TextView)vi.findViewById(R.id.txt_category);
				holder.btnDetails= (ImageView)vi.findViewById(R.id.bDetails);
				holder.rel_main = (RelativeLayout)vi.findViewById(R.id.rel_main);

				vi.setTag( holder );
			}
			else
			{
				holder=(ViewHolder)vi.getTag();
			}
			
			
			
			if(num<=0){}

			else
			{
				ActivitiesModel am = new ActivitiesModel();
				am = cn.get(position);

				final String sId = Integer.toString(am.get_id());
				String sName = am.get_name();
				String sTime = am.get_time();
				String sPoints = am.get_mypoints();
				String sPts = am.get_points();
				String sCategory = am.get_category();

				String sDate = am.get_date();
				String today_date = Utils.getTodaysDate(mContext);

				Date d1 = Utils.stringTodate(mContext, sDate);
				Date d2 = Utils.stringTodate(mContext, today_date);

				
				holder.txtPoints.setText(sPoints);
				holder.txtId.setText(sId);
				holder.txtActivity.setText(sName);
				holder.txtTime.setText(sTime);
				holder.txtCategory.setText(sCategory);
				holder.txtMaxPts.setText(sPts);
				holder.text_pts.setText(sPts);
				holder.btnDetails.setFocusable(false);

				if(sPoints.equalsIgnoreCase("0"))
				{
					holder.txtPoints.setTextColor(mContext.getResources().getColor(R.color.red));
				}
				else
				{
					holder.txtPoints.setTextColor(mContext.getResources().getColor(R.color.green));
					holder.txtPoints.setTypeface(Typeface.DEFAULT_BOLD);
				}

				if(position % 2 == 0)
				{
					holder.rel_main.setBackgroundResource(R.drawable.background1);
				}
				else
				{
					holder.rel_main.setBackgroundResource(R.drawable.background2);
				}


			}

			return vi;


		}



	}



	public void show()
	{

		final Dialog d = new Dialog(Activities.this);
		d.setTitle("NumberPicker");
		d.setContentView(R.layout.znumberpicker);
		Button b1 = (Button) d.findViewById(R.id.button1);
		Button b2 = (Button) d.findViewById(R.id.button2);
		final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
		np.setMaxValue(100);
		np.setMinValue(0);
		np.setWrapSelectorWheel(false);
		np.setOnValueChangedListener(this);
		b1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {

				String s = txtId.getText().toString();
				String s1 = mId;

				if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
				{
					logDB.UpdateActivityPoints(Integer.toString(np.getValue()), Integer.parseInt(txtId.getText().toString().trim()));
				}
				else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
				{
					logDB.UpdateNoonActivityPoints(Integer.toString(np.getValue()), Integer.parseInt(txtId.getText().toString().trim()));
				}
				/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
				{
					logDB.UpdateEveningActivityPoints(Integer.toString(np.getValue()), Integer.parseInt(txtId.getText().toString().trim()));
				}*/
				else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
				{
					logDB.UpdateNightActivityPoints(Integer.toString(np.getValue()), Integer.parseInt(txtId.getText().toString().trim()));
				}

				d.dismiss();
			}    
		});
		b2.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				d.dismiss();
			}    
		});
		d.show();


	}
	@Override
	public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}



	@SuppressLint("NewApi")
	void showDetailPopup(final String sId, final String sPoints, final String sName, final String sQty, final String my_pts)
	{

		final Dialog dialog = new Dialog(Activities.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		dialog.setContentView(R.layout.holocircle);

		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		
		TextView txtHeader = (TextView)dialog.findViewById(R.id.txtHeader);
		final CircularSeekBar seekbar = (CircularSeekBar)dialog.findViewById(R.id.circularSeekBar1);
		final TextView txtProgress = (TextView)dialog.findViewById(R.id.seekArcProgress);
		final TextView txtMaxPts = (TextView)dialog.findViewById(R.id.txtMaxPoints);
		Button bSave = (Button)dialog.findViewById(R.id.btnSave);
		Button bDelete = (Button)dialog.findViewById(R.id.btnDelete);

		txtHeader.setText(sName);
		String sMaxPoints = "Out of "+sPoints+" pts.";
		String sQnty = "Quantity: "+sQty;

		txtMaxPts.setText(sMaxPoints+"\n"+sQnty);
		txtProgress.setText(my_pts);


		try
		{
			seekbar.setMax(Integer.parseInt(sPoints));
			seekbar.setProgress(Integer.parseInt(my_pts));
		}
		catch(Exception e)
		{}

		bSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String str = Utils.currentDateOnDashboard;

				txtPoints.setText(txtProgress.getText().toString().trim());
				if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
				{
					logDB.UpdateActivityMyPoints(txtPoints.getText().toString().trim(), Integer.parseInt(sId));
					
					if(Utils.currentDateOnDashboard.equalsIgnoreCase(""))
					{
						logDB.UpdateActivityDate(Utils.getTodaysDate(Activities.this), Integer.parseInt(sId));
					}
					else
					{
						logDB.UpdateActivityDate(Utils.currentDateOnDashboard, Integer.parseInt(sId));
					}
					
					bg = new BackgroundTask();
					bg.execute("");
					

				}
				else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
				{
					logDB.UpdateNoonActivityMyPoints(txtPoints.getText().toString().trim(), Integer.parseInt(sId));
					
					if(Utils.currentDateOnDashboard.equalsIgnoreCase(""))
					{
						logDB.UpdateNoonActivityDate(Utils.getTodaysDate(Activities.this), Integer.parseInt(sId));
					}
					else
					{
						logDB.UpdateNoonActivityDate(Utils.currentDateOnDashboard, Integer.parseInt(sId));
					}

					bg = new BackgroundTask();
					bg.execute("");

				}
				else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
				{
					logDB.UpdateNightActivityMyPoints(txtPoints.getText().toString().trim(), Integer.parseInt(sId));
					
					if(Utils.currentDateOnDashboard.equalsIgnoreCase(""))
					{
						logDB.UpdateNightActivityDate(Utils.getTodaysDate(Activities.this), Integer.parseInt(sId));
					}
					else
					{
						logDB.UpdateNightActivityDate(Utils.currentDateOnDashboard, Integer.parseInt(sId));
					}

					bg = new BackgroundTask();
					bg.execute("");
				}

				dialog.cancel();
			}
		});

		seekbar.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(CircularSeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(CircularSeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(CircularSeekBar circularSeekBar,
					int progress, boolean fromUser) {
				// TODO Auto-generated method stub

				txtProgress.setText(Integer.toString(progress));


			}
		});

		bDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(Activities.this);
				alertDialog.setTitle("Confirm Delete ?");
				alertDialog.setMessage("Are you sure you want to remove this item.");

				alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {

						deleteItem(sId);
						dialog.cancel();
					}
				});

				alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.cancel();
					}
				});
				alertDialog.show();

				dialog.dismiss();
			}

		});

		//dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);
		dialog.show();
	}

	void deleteItem(String sId)
	{

		if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
		{
			logDB.Activitydelete_byID(Integer.parseInt(sId));
			bg = new BackgroundTask();
			bg.execute("");
			
		}
		else if(Utils.currentTab.equals(Utils.strNoon))
		{
			logDB.NoonActivitydelete_byID(Integer.parseInt(sId));
			bg = new BackgroundTask();
			bg.execute("");
		}
		/*else if(Utils.currentTab.equals("3"))
		{
			logDB.EveningActivitydelete_byID(Integer.parseInt(sId));
			bg = new BackgroundTask();
			bg.execute("");
		}*/
		else if(Utils.currentTab.equals(Utils.strNight))
		{
			logDB.NightActivitydelete_byID(Integer.parseInt(sId));
			bg = new BackgroundTask();
			bg.execute("");
		}
	}

	void refreshListView()
	{
		mList = new ArrayList<ActivitiesModel>();
		if(Utils.currentTab.equals(Utils.strMorning))    // Load Morning Activities.
		{
			mList = logDB.getActivityContacts();
		}
		else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
		{
			mList =logDB.getNoonActivityContacts();
		}
		/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
		{
			mList = logDB.getEveningActivityContacts();
		}*/
		else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
		{
			mList = logDB.getNightActivityContacts();
		}

		mAdapter = new ActivitiesAdapter(Activities.this,mList);
		list_activity.setAdapter(mAdapter);
	}

}
