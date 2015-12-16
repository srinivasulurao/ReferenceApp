package com.personaltrainer.tabs;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.devadvance.circularseekbar.CircularSeekBar;
import com.devadvance.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener;
import com.omkar.myactivitytracker.Activities;
import com.omkar.myactivitytracker.R;
import com.omkar.myactivitytracker.Todo;
import com.personaltrainer.database.LoginDB;
import com.personaltrainer.model.ActivitiesModel;
import com.personaltrainer.widgets.Utils;



import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class TabLayoutActivity extends TabActivity implements OnTabChangeListener,OnClickListener, NumberPicker.OnValueChangeListener, OnItemSelectedListener{

	public static TabHost tabHost;  
	public static TextView title;
	public static ImageView imgOnline;
	static final int TIME_DIALOG_ID = 111;
	private int hour;
	private int minute;
	private TimePicker timePicker;
	Spinner category;
	int eatValue;
	String sNewTime ="", sFrom="", sCurrent="", sActionBarTitle="";
	LoginDB logDB ;
	TextView txtTime,txtPoints,txtId;

	List<ActivitiesModel> mList=null;
	List<ActivitiesModel> mDefaultList = null;
	EditText edtMyPoints, edtQuantity, edtPoints;
	ListView mListView;
	private int numValue=0;
	NumberPicker np, np_;
	EditText et, et_;
	Menu mnu;
	ProgressDialog pDialog;

	private String[] state = { "Unit", "AM", "Cups", "Calorie","Day", "Gallon", "Hrs", "Km",
			"Litre", "Miles", "Month", "Min","Other","PM","Sec","Spoon", "Weeek"
	};



	private void setTabs()
	{
		addTab("Morning", R.drawable.morning_tab, Activities.class);
		addTab("Afternoon", R.drawable.noon_tab, Activities.class);
		//addTab("Evening", R.drawable.eve_tab, Activities.class);
		addTab("Evening", R.drawable.night_tab, Activities.class);
		addTab("Todo's", R.drawable.todo_tab, Todo.class);
	}



	private void addTab(String labelId, int drawableId, Class<?> c)
	{
		tabHost = getTabHost();
		Intent intent = new Intent(this, c).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		TabHost.TabSpec spec = tabHost.newTabSpec(labelId);	

		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_idicatormain, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);

		spec.setIndicator(tabIndicator);
		spec.setContent(intent);

		tabHost.addTab(spec);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablayout);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

		sFrom = getIntent().getStringExtra("from");
		logDB = new LoginDB(this);


		Utils.currentTab ="1";
		mList = new ArrayList<ActivitiesModel>();
		mDefaultList = new ArrayList<ActivitiesModel>();

		mList = logDB.getActivityContacts();
		mDefaultList = logDB.getDefaultActivityContacts();

		timePicker = (TimePicker)findViewById(R.id.timePicker1);

		setTabs();

		getTabHost().setOnTabChangedListener(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		IntentFilter filter = new IntentFilter();
        filter.addAction("com.omkar.myactivitytracker");  
		
        registerReceiver(reciever, filter);
        
        
		UpdateActionBarTitle up = new UpdateActionBarTitle();
		up.execute("");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		unregisterReceiver(reciever);
	}

	
	private BroadcastReceiver reciever = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			UpdateActionBarTitle up = new UpdateActionBarTitle();
			up.execute("");
		}
	};
	

	class UpdateActionBarTitle extends AsyncTask<String, Void, String>
	{
		int pos;
		String sTitle="";
		ActionBar actionBar;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			actionBar = getActionBar();
			Utils.initilizePoints();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Utils.initilizePoints();
			pos = getTabHost().getCurrentTab();
			if(pos==0)
			{
				Utils.MorningPoints(TabLayoutActivity.this);
				sTitle = " Morning"+"   "+Integer.toString(Utils.morning_achieved_points)+"/"+Integer.toString(Utils.morning_overall_points);
			}
			else if(pos==1)
			{
				Utils.NoonPoints(TabLayoutActivity.this);
				sTitle = " Afternoon"+"   "+Integer.toString(Utils.noon_achieved_points)+"/"+Integer.toString(Utils.noon_overall_points);
			}
			else if(pos ==2)
			{
				Utils.NightPoints(TabLayoutActivity.this);
				sTitle = " Evening"+"   "+Integer.toString(Utils.night_achieved_points)+"/"+Integer.toString(Utils.night_overall_points);
			}
			else if(pos == 3)
			{
				Utils.ToDoPoints(TabLayoutActivity.this);
				sTitle = " Todo's"+"   "+Integer.toString(Utils.todo_achieved_points)+"/"+Integer.toString(Utils.todo_add_points);
				
				
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			actionBar.setTitle(sTitle);

		}

	}


	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		int pos = getTabHost().getCurrentTab();
		Utils.currentTab = Integer.toString(pos+1);


		if(pos==1)
		{
			UpdateActionBarTitle up = new UpdateActionBarTitle();
			up.execute("");

			mList = logDB.getNoonActivityContacts();
			mDefaultList = logDB.getDefaultNoonActivityContacts();

			MenuItem item_ = mnu.findItem(R.id.action_add);
			item_.setVisible(true);

		}
		else if(pos==2)
		{
			UpdateActionBarTitle up = new UpdateActionBarTitle();
			up.execute("");

			mList = logDB.getNightActivityContacts();
			mDefaultList = logDB.getDefaultNightActivityContacts();

			MenuItem item_ = mnu.findItem(R.id.action_add);
			item_.setVisible(true);
		}
		else if(pos == 0)
		{
			UpdateActionBarTitle up = new UpdateActionBarTitle();
			up.execute("");

			mList = logDB.getActivityContacts();
			mDefaultList = logDB.getDefaultActivityContacts();

			MenuItem item_ = mnu.findItem(R.id.action_add);
			item_.setVisible(true);
		}
		else
		{
			UpdateActionBarTitle up = new UpdateActionBarTitle();
			up.execute("");
			
			MenuItem item = mnu.findItem(R.id.action_rate);
			item.setVisible(false);

			MenuItem item_ = mnu.findItem(R.id.action_add);
			item_.setVisible(false);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.splash, menu);

		this.mnu = menu;

		MenuItem item = menu.findItem(R.id.action_rate);
		item.setVisible(false);


		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub


		switch (item.getItemId()) 
		{
		case R.id.action_add:

			BackgroundDefaultActivities bg = new BackgroundDefaultActivities();
			bg.execute("");

			break;


		default:
			break;
		}

		return true;
	}


	class BackgroundDefaultActivities extends AsyncTask<String, Void, String>
	{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mList = new ArrayList<ActivitiesModel>();
			mDefaultList = new ArrayList<ActivitiesModel>();

			pDialog = new ProgressDialog(TabLayoutActivity.this);
			pDialog.setMessage("Loading..");
			pDialog.setProgressStyle(pDialog.STYLE_SPINNER);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
			{
				mList = logDB.getActivityContacts();
				mDefaultList = logDB.getDefaultActivityContacts();

			}
			else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
			{
				mList = logDB.getNoonActivityContacts();
				mDefaultList = logDB.getDefaultNoonActivityContacts();

			}
			/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
			{
				mList = logDB.getEveningActivityContacts();
				mDefaultList = logDB.getDefaultEveningActivityContacts();

			}*/
			else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
			{
				mList = logDB.getNightActivityContacts();
				mDefaultList = logDB.getDefaultNightActivityContacts();

			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			pDialog.dismiss();
			showDefaultActivitiesList();
		}

	}

	class BackgroundLoadDefaultActivities extends AsyncTask<String, Void, String>
	{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			mDefaultList = new ArrayList<ActivitiesModel>();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
			{

				mDefaultList = logDB.getDefaultActivityContacts();

			}
			else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
			{

				mDefaultList = logDB.getDefaultNoonActivityContacts();

			}
			/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
			{

				mDefaultList = logDB.getDefaultEveningActivityContacts();

			}*/
			else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
			{

				mDefaultList = logDB.getDefaultNightActivityContacts();

			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if(mDefaultList.isEmpty())
			{
				ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("TAG", "There are no Saved Activities.");
				oslist.add(map);

				ListAdapter adapter = new SimpleAdapter(TabLayoutActivity.this, oslist, R.layout.nodata, new String[] {"TAG"}, 
						new int[] {R.id.txtNoData});

				mListView.setAdapter(adapter);
			}
			else{
				DefaultActivitiesAdapter mAdapter = new DefaultActivitiesAdapter(TabLayoutActivity.this,mDefaultList);
				mListView.setAdapter(mAdapter);
			}

		}

	}

	class BackgroundRefreshDefaultActivities extends AsyncTask<String, Void, String>
	{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mList = new ArrayList<ActivitiesModel>();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
			{
				mList = logDB.getActivityContacts();
			}
			else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
			{
				mList = logDB.getNoonActivityContacts();
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

			Activities.ActivitiesAdapter mAdapter = new Activities.ActivitiesAdapter(getApplicationContext(), mList);
			Activities.list_activity.setAdapter(mAdapter);
			
			Intent in = new Intent("com.omkar.myactivitytracker");
			sendBroadcast(in);
		}

	}

	private void checkMultipleData(String sFrom, String mName)
	{
		List<ActivitiesModel> mList =  new ArrayList<ActivitiesModel>();

		if(sFrom.equalsIgnoreCase(Utils.strMorning))
		{
			mList = logDB.getActivityContacts();

			for(int i=0; i<mList.size(); i++)
			{
				ActivitiesModel am = mList.get(i);
				String sName = am.get_name();
				if(sName.equalsIgnoreCase(mName))
				{
					int id = am.get_id();
					logDB.Activitydelete_byID(id);
				}
			}
		}
		else if(sFrom.equalsIgnoreCase(Utils.strNoon))
		{
			mList = logDB.getNoonActivityContacts();
			for(int i=0; i<mList.size(); i++)
			{
				ActivitiesModel am = mList.get(i);
				String sName = am.get_name();
				if(sName.equalsIgnoreCase(mName))
				{
					int id = am.get_id();
					logDB.NoonActivitydelete_byID(id);
				}
			}
		}
		else if(sFrom.equalsIgnoreCase(Utils.strNight))
		{
			mList = logDB.getNightActivityContacts();
			for(int i=0; i<mList.size(); i++)
			{
				ActivitiesModel am = mList.get(i);
				String sName = am.get_name();
				if(sName.equalsIgnoreCase(mName))
				{
					int id = am.get_id();
					logDB.NightActivitydelete_byID(id);
				}
			}
		}



	}


	void showDefaultActivitiesList()
	{
		final Dialog dialog = new Dialog(TabLayoutActivity.this);
		dialog.setTitle("Activity List");
		dialog.setContentView(R.layout.showactivitieslist);

		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		final Button btnAddNew = (Button)dialog.findViewById(R.id.btnAddNew);
		mListView = (ListView)dialog.findViewById(R.id.list_activity);

		if(mDefaultList.isEmpty())
		{
			ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("TAG", "There are no Saved Activities.");
			oslist.add(map);

			ListAdapter adapter = new SimpleAdapter(this, oslist, R.layout.nodata, new String[] {"TAG"}, 
					new int[] {R.id.txtNoData});

			mListView.setAdapter(adapter);
		}
		else
		{

			DefaultActivitiesAdapter adapter = new DefaultActivitiesAdapter(TabLayoutActivity.this, mDefaultList);
			mListView.setAdapter(adapter);

		}


		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position,
					long id) {
				// TODO Auto-generated method stub

				mDefaultList = new ArrayList<ActivitiesModel>();
				if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
				{
					mDefaultList = logDB.getDefaultActivityContacts();
				}
				else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
				{
					mDefaultList = logDB.getDefaultNoonActivityContacts();
				}
				else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
				{
					mDefaultList = logDB.getDefaultNightActivityContacts();
				}
				if(!mDefaultList.isEmpty())
				{


					//dialog.cancel();


					TextView txtName = (TextView)view.findViewById(R.id.activityName);
					EditText edtQty = (EditText)view.findViewById(R.id.edtQuantity);
					EditText edtPoints = (EditText)view.findViewById(R.id.points);
					TextView txtCategory = (TextView)view.findViewById(R.id.txtCategory);

					Drawable img = getResources().getDrawable(R.drawable.tick);
					txtName.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
					txtName.setCompoundDrawablePadding(10);

					if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
					{
						checkMultipleData(Utils.strMorning, txtName.getText().toString().trim());
						logDB.addActivities(new ActivitiesModel(txtName.getText().toString(), edtQty.getText().toString(), 
								edtPoints.getText().toString().trim(),txtCategory.getText().toString().trim()));
						

						BackgroundRefreshDefaultActivities brRefresh = new BackgroundRefreshDefaultActivities();
						brRefresh.execute("");

					}
					else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
					{
						checkMultipleData(Utils.strNoon, txtName.getText().toString().trim());
						logDB.addNoonActivities(new ActivitiesModel(txtName.getText().toString(), edtQty.getText().toString(), 
								edtPoints.getText().toString().trim(),txtCategory.getText().toString().trim()));
						

						BackgroundRefreshDefaultActivities brRefresh = new BackgroundRefreshDefaultActivities();
						brRefresh.execute("");
					}
					
					else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
					{
						checkMultipleData(Utils.strNight, txtName.getText().toString().trim());
						logDB.addNightActivities(new ActivitiesModel(txtName.getText().toString(), edtQty.getText().toString(),
								edtPoints.getText().toString().trim(),txtCategory.getText().toString().trim()));
						

						BackgroundRefreshDefaultActivities brRefresh = new BackgroundRefreshDefaultActivities();
						brRefresh.execute("");
					}


				
				}
			}
		});


		btnAddNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AddNewActivity();
			}
		});


		//dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);
		dialog.show();

	}


	String unit="";
	//Add New ACtivity:
	void AddNewActivity()
	{
		final Dialog dialog = new Dialog(TabLayoutActivity.this);
		dialog.setTitle("Add New Activity");
		dialog.setContentView(R.layout.znewactivity);

		dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		final EditText edtName = (EditText)dialog.findViewById(R.id.edt_ActivityName);
		txtTime = (TextView)dialog.findViewById(R.id.setTime);
		txtPoints = (TextView)dialog.findViewById(R.id.setPoints);
		category = (Spinner)dialog.findViewById(R.id.spinner_unit);
		LinearLayout linTime = (LinearLayout)dialog.findViewById(R.id.lin_Time);
		LinearLayout linPoints = (LinearLayout)dialog.findViewById(R.id.lin_Points);
		final ImageView bSave = (ImageView)dialog.findViewById(R.id.bSave);
		final ImageView bCancel = (ImageView)dialog.findViewById(R.id.bCancel);

		final NumberPicker np_qty = (NumberPicker)dialog.findViewById(R.id.numberPicker_qty);
		final NumberPicker np_pts = (NumberPicker)dialog.findViewById(R.id.numberPicker_pts);

		ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, state);
		adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category.setAdapter(adapter_state);
		category.setOnItemSelectedListener(this);

		np_qty.setMaxValue(1000);
		np_qty.setMinValue(0);
		np_qty.setWrapSelectorWheel(false);

		np_pts.setMaxValue(1000);
		np_pts.setMinValue(0);
		np_pts.setWrapSelectorWheel(false);

		int childCount = np_qty.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = np_qty.getChildAt(i);

			if (childView instanceof EditText) {
				et = (EditText) childView;
			}
		}

		int childCount_ = np_pts.getChildCount();
		for (int i = 0; i < childCount_; i++) {
			View childView = np_pts.getChildAt(i);

			if (childView instanceof EditText) {
				et_ = (EditText) childView;

			}
		}



		linPoints.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sCurrent = "new";
				showNumberPicker();
			}
		});
		linTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sCurrent = "new";
				ShowQuantity(unit);
			}
		});
		bSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String sPts="";
				unit = category.getSelectedItem().toString();
				String sQty = et.getText().toString().trim();
				sPts = et_.getText().toString().trim();

				if(edtName.getText().toString().trim().equals(""))
				{
					Utils.showAlertBoxSingle(TabLayoutActivity.this, "Error", "Enter Activity Name.");
				}
				else if(sPts.equalsIgnoreCase("0"))
				{
					Utils.showAlertBoxSingle(TabLayoutActivity.this, "Error", "Points cannot be set to 0. Select the valid number.");
				}
				else
				{
					if(sQty.equalsIgnoreCase("0"))
					{
						sNewTime = "";
					}
					else
					{
						sNewTime = sQty+" "+unit;
					}

					
					if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
					{

						logDB.addDefaultActivities(new ActivitiesModel(edtName.getText().toString(), sNewTime, sPts, unit));
						dialog.dismiss();

						BackgroundLoadDefaultActivities bg = new BackgroundLoadDefaultActivities();
						bg.execute("");

					}					
					else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
					{

						logDB.addDefaultNoonActivities(new ActivitiesModel(edtName.getText().toString(),  sNewTime, sPts, unit));
						dialog.dismiss();

						BackgroundLoadDefaultActivities bg = new BackgroundLoadDefaultActivities();
						bg.execute("");

					}
					/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
					{
						logDB.addDefaultEveningActivities(new ActivitiesModel(edtName.getText().toString(),  sNewTime, sPts, unit));
						dialog.dismiss();

						BackgroundLoadDefaultActivities bg = new BackgroundLoadDefaultActivities();
						bg.execute("");

					}*/
					else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
					{

						logDB.addDefaultNightActivities(new ActivitiesModel(edtName.getText().toString(),  sNewTime, sPts, unit));
						dialog.dismiss();

						BackgroundLoadDefaultActivities bg = new BackgroundLoadDefaultActivities();
						bg.execute("");

					}
				}
			}
		});
		bCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();


			}

		});
		dialog.show();
	}

	//Show Number Picker Dialog:
	public void showNumberPicker()
	{

		final Dialog d = new Dialog(TabLayoutActivity.this);
		d.setTitle("Select the Points.");
		d.setContentView(R.layout.znumberpicker);
		Button b1 = (Button) d.findViewById(R.id.button1);
		Button b2 = (Button) d.findViewById(R.id.button2);
		np = (NumberPicker) d.findViewById(R.id.numberPicker1);
		np.setMaxValue(1000);
		np.setMinValue(0);
		np.setWrapSelectorWheel(false);


		int childCount = np.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = np.getChildAt(i);

			if (childView instanceof EditText) {
				et = (EditText) childView;


			}
		}


		np.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				// TODO Auto-generated method stub

			}
		});


		b1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {


				if(sCurrent.equalsIgnoreCase("new"))
				{
					txtPoints.setText(et.getText().toString().trim());
				}
				else
				{
					edtPoints.setText(et.getText().toString().trim());

					if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
					{
						logDB.UpdateDefaultActivityPoints(edtPoints.getText().toString().trim(),
								Integer.parseInt(txtId.getText().toString().trim()));
					}
					else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
					{
						logDB.UpdateDefaultNoonActivityPoints(edtPoints.getText().toString().trim(),
								Integer.parseInt(txtId.getText().toString().trim()));
					}
					/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
					{
						logDB.UpdateDefaultEveningActivityPoints(edtPoints.getText().toString().trim(),
								Integer.parseInt(txtId.getText().toString().trim()));
					}*/
					else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
					{
						logDB.UpdateDefaultNightActivityPoints(edtPoints.getText().toString().trim(),
								Integer.parseInt(txtId.getText().toString().trim()));
					}
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



	void showEdit_Qty_Pts(final String sQty, String sPts, String sCategory)
	{
		int pos=0;
		final Dialog d = new Dialog(TabLayoutActivity.this);
		List<String> list = new ArrayList<String>();

		list.add("AM");
		list.add("Cups");
		list.add("Calorie");
		list.add("Day");
		list.add("Gallon");
		list.add("Hrs");
		list.add("Km");
		list.add("Litre");
		list.add("Miles");
		list.add("Month");
		list.add("Min");
		list.add("Other");
		list.add("Pm");
		list.add("Sec");
		list.add("Spoon");
		list.add("Unit");
		list.add("Week");
		
		
		for(int i=0; i<list.size(); i++)
		{
			String sItem = list.get(i);
			
			if(sItem.equalsIgnoreCase(sCategory))
			{
				pos=i;
			}
		}
		

		d.setTitle("Choose Quantity and Points");
		d.setContentView(R.layout.edit_qty_pts);

		d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		Button b1 = (Button) d.findViewById(R.id.button1);
		Button b2 = (Button) d.findViewById(R.id.button2);
		final Spinner sp = (Spinner)d.findViewById(R.id.spinner_unit);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(dataAdapter);
		
		sp.setSelection(pos);

		if(!sQty.equalsIgnoreCase(""))
		{

			np = (NumberPicker) d.findViewById(R.id.numberPicker1);
			np.setMaxValue(10000);
			np.setMinValue(1);
			np.setValue(Integer.parseInt(sQty.replaceAll("\\D+","")));
			np.setWrapSelectorWheel(false);

			int childCount = np.getChildCount();
			for (int i = 0; i < childCount; i++) {
				View childView = np.getChildAt(i);

				if (childView instanceof EditText) {
					et = (EditText) childView;
				}
			}
		}
		else
		{
			np = (NumberPicker) d.findViewById(R.id.numberPicker1);
			np.setEnabled(false);
			sp.setEnabled(false);
		}

		np_ = (NumberPicker) d.findViewById(R.id.numberPicker2);
		np_.setMaxValue(10000);
		np_.setMinValue(1);
		np_.setValue(Integer.parseInt(sPts));
		np_.setWrapSelectorWheel(false);



		int childCount_ = np_.getChildCount();
		for (int i = 0; i < childCount_; i++) {
			View childView = np_.getChildAt(i);

			if (childView instanceof EditText) {
				et_ = (EditText) childView;
			}
		}

		b1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {

				if(!sQty.equalsIgnoreCase(""))
				{
					unit = sp.getSelectedItem().toString();
					sNewTime = et.getText().toString().trim()+" "+unit;
				}
				else
				{
					sNewTime = "";
				}

				if(et_.getText().toString().trim().equalsIgnoreCase("0") || 
						et_.getText().toString().trim().equalsIgnoreCase("000"))
				{
					Utils.showAlertBoxSingle(TabLayoutActivity.this, "Error", "Points cannot be Zero.");
				}
				else
				{
					if(sCurrent.equalsIgnoreCase("new"))
					{
						txtTime.setText(sNewTime);
						txtPoints.setText(et_.getText().toString().trim());
					}
					else
					{
						edtQuantity.setText(sNewTime);
						edtPoints.setText(et_.getText().toString().trim());
						
						if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
						{
							logDB.UpdateDefaultActivityTime(edtQuantity.getText().toString().trim(), 
									Integer.parseInt(txtId.getText().toString().trim()));

							logDB.UpdateDefaultActivityPoints(edtPoints.getText().toString().trim(), 
									Integer.parseInt(txtId.getText().toString().trim()));
						}
						else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
						{
							logDB.UpdateDefaultNoonActivityTime(edtQuantity.getText().toString().trim(), 
									Integer.parseInt(txtId.getText().toString().trim()));

							logDB.UpdateDefaultNoonActivityPoints(edtPoints.getText().toString().trim(), 
									Integer.parseInt(txtId.getText().toString().trim()));
						}
						else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
						{
							logDB.UpdateDefaultNightActivityTime(edtQuantity.getText().toString().trim(), 
									Integer.parseInt(txtId.getText().toString().trim()));

							logDB.UpdateDefaultNightActivityPoints(edtPoints.getText().toString().trim(), 
									Integer.parseInt(txtId.getText().toString().trim()));
						}
						
						BackgroundLoadDefaultActivities bg = new BackgroundLoadDefaultActivities();
						bg.execute("");
					}
					d.dismiss();
				}


				
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

	void ShowQuantity(final String mCategory)
	{

		final Dialog d = new Dialog(TabLayoutActivity.this);
		List<String> list = new ArrayList<String>();

		list.add("Am");
		list.add("Cups");
		list.add("Calorie");
		list.add("Day");
		list.add("Gallon");
		list.add("Hrs");
		list.add("Km");
		list.add("Litre");
		list.add("Min");
		list.add("Month");
		list.add("Others");
		list.add("Miles");
		list.add("Pm");
		list.add("Sec");
		list.add("Spoons");
		list.add("Unit");
		list.add("Week");


		d.setTitle("Choose the Quantity");
		d.setContentView(R.layout.showquanity);



		Button b1 = (Button) d.findViewById(R.id.button1);
		Button b2 = (Button) d.findViewById(R.id.button2);
		final Spinner sp = (Spinner)d.findViewById(R.id.spinner_unit);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(dataAdapter);

		np = (NumberPicker) d.findViewById(R.id.numberPicker1);
		np.setMaxValue(10000);
		np.setMinValue(0);
		np.setWrapSelectorWheel(false);
		//np.setOnValueChangedListener(TabLayoutActivity.this);

		int childCount = np.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = np.getChildAt(i);

			if (childView instanceof EditText) {
				et = (EditText) childView;


			}
		}

		b1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {

				unit = sp.getSelectedItem().toString();
				sNewTime = et.getText().toString().trim()+" "+unit;

				if(sCurrent.equalsIgnoreCase("new"))
				{
					txtTime.setText(sNewTime);
				}
				else
				{
					edtQuantity.setText(sNewTime);

					if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
					{
						logDB.UpdateDefaultActivityTime(edtQuantity.getText().toString().trim(), 
								Integer.parseInt(txtId.getText().toString().trim()));
					}
					else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
					{
						logDB.UpdateDefaultNoonActivityTime(edtQuantity.getText().toString().trim(), 
								Integer.parseInt(txtId.getText().toString().trim()));
					}
					else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
					{
						logDB.UpdateDefaultNightActivityTime(edtQuantity.getText().toString().trim(), 
								Integer.parseInt(txtId.getText().toString().trim()));
					}
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


			if(hour == 0)
			{
				sNewTime = Integer.toString(minute)+" min";
			}
			else
			{
				sNewTime = Integer.toString(hour)+"."+Integer.toString(minute)+" hr";
			}

			if(sCurrent.equalsIgnoreCase("new"))
			{
				txtTime.setText(sNewTime);
			}
			else
			{
				edtQuantity.setText(sNewTime);

				if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
				{
					logDB.UpdateDefaultActivityTime(edtQuantity.getText().toString().trim(), 
							Integer.parseInt(txtId.getText().toString().trim()));
				}
				else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
				{
					logDB.UpdateDefaultNoonActivityTime(edtQuantity.getText().toString().trim(), 
							Integer.parseInt(txtId.getText().toString().trim()));
				}
				/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
				{
					logDB.UpdateDefaultEveningActivityTime(edtQuantity.getText().toString().trim(), 
							Integer.parseInt(txtId.getText().toString().trim()));
				}*/
				else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
				{
					logDB.UpdateDefaultNightActivityTime(edtQuantity.getText().toString().trim(), 
							Integer.parseInt(txtId.getText().toString().trim()));
				}
			}

		}
	};

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// TODO Auto-generated method stub
		numValue = newVal;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}


	//Custom Adapter
	class ActivitiesAdapter extends BaseAdapter
	{

		private  LayoutInflater inflater=null;
		public Context mContext;

		LoginDB logDB;
		int num;
		List<ActivitiesModel> cn;

		public  class ViewHolder
		{
			public TextView txtId;
			public TextView txtActivity;
			public EditText txtPoints;
			public EditText txt_mypoints;
			public EditText txt_Qty;
			public RelativeLayout relMain;

		}

		public ActivitiesAdapter(Context context, List<ActivitiesModel> mList)
		{
			mContext = context;
			logDB = new LoginDB(mContext);
			cn = mList;

			inflater = ( LayoutInflater )mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View vi = convertView;


			ViewHolder holder = new ViewHolder();
			if(convertView==null)
			{
				vi = inflater.inflate(R.layout.zrate_helper,null);

				holder.txtId = (TextView)vi.findViewById(R.id.txtId);
				holder.txtActivity = (TextView)vi.findViewById(R.id.activityName);
				holder.txtPoints = (EditText)vi.findViewById(R.id.points);
				holder.txt_mypoints = (EditText)vi.findViewById(R.id.my_points);
				holder.txt_Qty = (EditText)vi.findViewById(R.id.edtQuantity);
				holder.relMain = (RelativeLayout)vi.findViewById(R.id.relMain);


				vi.setTag( holder );
			}
			else
			{
				holder=(ViewHolder)vi.getTag();
			}

			if(num<=0){}

			else
			{
				final ActivitiesModel am = cn.get(position);

				final String sId = Integer.toString(am.get_id());
				String sName = am.get_name();
				String sPoints = am.get_points();
				String sQty = am.get_time();

				holder.txtId.setText(sId);
				holder.txtActivity.setText(sName);
				holder.txtPoints.setText(sPoints);
				holder.txt_Qty.setText(sQty);

				holder.txt_mypoints.setFocusable(false);

				if(position % 2 == 0)
				{
					holder.relMain.setBackgroundResource(R.color.listbg1);
				}
				else
				{
					holder.relMain.setBackgroundResource(R.color.listbg2);
				}

			}

			return vi;


		}

	}


	void RefreshDefaultList()
	{
		mDefaultList = new ArrayList<ActivitiesModel>();
		if(Utils.currentTab.equals(Utils.strMorning))    // Load Morning Activities.
		{
			mDefaultList = logDB.getDefaultActivityContacts();
		}
		else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
		{
			mDefaultList =logDB.getDefaultNoonActivityContacts();
		}
		/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
		{
			mDefaultList = logDB.getDefaultEveningActivityContacts();
		}*/
		else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
		{
			mDefaultList = logDB.getDefaultNightActivityContacts();
		}

		if(mDefaultList.isEmpty())
		{
			ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("TAG", "There are no Saved Activities.");
			oslist.add(map);

			ListAdapter adapter = new SimpleAdapter(this, oslist, R.layout.nodata, new String[] {"TAG"}, 
					new int[] {R.id.txtNoData});

			mListView.setAdapter(adapter);
		}
		else{
			DefaultActivitiesAdapter mAdapter = new DefaultActivitiesAdapter(TabLayoutActivity.this,mDefaultList);
			mListView.setAdapter(mAdapter);
		}
	}


	class DefaultActivitiesAdapter extends BaseAdapter
	{

		private  LayoutInflater inflater=null;
		public Context mContext;

		LoginDB logDB;
		int num;
		List<ActivitiesModel> cn;

		public  class ViewHolder
		{
			public TextView txtId;
			public TextView txtActivity;
			public EditText txtPoints;
			public EditText txt_mypoints;
			public EditText txt_Qty;
			public RelativeLayout rel_main;
			public RelativeLayout relPoints;
			public TextView txtCategory;
			public ImageView imgEdit;

		}

		public DefaultActivitiesAdapter(Context context, List<ActivitiesModel> mList)
		{
			mContext = context;
			logDB = new LoginDB(mContext);
			cn = mList;

			inflater = ( LayoutInflater )mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View vi = convertView;


			ViewHolder holder = new ViewHolder();
			if(convertView==null)
			{
				vi = inflater.inflate(R.layout.zdefaulthelper,null);

				holder.txtId = (TextView)vi.findViewById(R.id.txtId);
				holder.txtActivity = (TextView)vi.findViewById(R.id.activityName);
				holder.txtPoints = (EditText)vi.findViewById(R.id.points);
				holder.txt_mypoints = (EditText)vi.findViewById(R.id.my_points);
				holder.txt_Qty = (EditText)vi.findViewById(R.id.edtQuantity);
				holder.txtCategory = (TextView)vi.findViewById(R.id.txtCategory);
				holder.rel_main = (RelativeLayout)vi.findViewById(R.id.rel_main);
				holder.relPoints = (RelativeLayout)vi.findViewById(R.id.relPoints);
				holder.imgEdit = (ImageView)vi.findViewById(R.id.imgEdit);

				vi.setTag( holder );
			}
			else
			{
				holder=(ViewHolder)vi.getTag();
			}

			if(num<=0){}

			else
			{
				final ActivitiesModel am = cn.get(position);

				final String sId = Integer.toString(am.get_id());
				final String sName = am.get_name();
				final String sPoints = String.format("%03d", Integer.parseInt(am.get_points()));
				final String sQty = am.get_time();
				final String sCategory = am.get_category();
				String sDate = am.get_date();

				
				
				
				holder.txtId.setText(sId);
				holder.txtActivity.setText(sName);
				holder.txtPoints.setText(sPoints);
				holder.txt_Qty.setText(sQty);
				holder.txtCategory.setText(sCategory);

				holder.txtActivity.setFocusable(false);

				holder.imgEdit.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub


						AlertDialog.Builder alertDialog = new AlertDialog.Builder(TabLayoutActivity.this);
						alertDialog.setTitle("Confirm Delete");
						alertDialog.setMessage("Are you sure you want to delete this item ?");


						alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {

								dialog.cancel();

								if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
								{
									logDB.DefaultActivitydelete_byID(Integer.parseInt(sId));
									BackgroundLoadDefaultActivities bg = new BackgroundLoadDefaultActivities();
									bg.execute("");
								}
								else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
								{
									logDB.DefaultNoonActivitydelete_byID(Integer.parseInt(sId));
									BackgroundLoadDefaultActivities bg = new BackgroundLoadDefaultActivities();
									bg.execute("");
								}
								else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
								{
									logDB.DefaultNightActivitydelete_byID(Integer.parseInt(sId));
									BackgroundLoadDefaultActivities bg = new BackgroundLoadDefaultActivities();
									bg.execute("");
								}
							}
						});

						alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

								dialog.cancel();
							}
						});

						alertDialog.show();


					}
				});

				holder.txt_Qty.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sCurrent = "old";
						RelativeLayout rl = (RelativeLayout)v.getParent();
						txtId  = (TextView)rl.getChildAt(1);
						edtQuantity = (EditText)rl.getChildAt(2);
						RelativeLayout relPoints = (RelativeLayout)rl.getChildAt(3);
						edtPoints = (EditText)relPoints.getChildAt(0);
						showEdit_Qty_Pts(sQty, sPoints, sCategory);

					}
				});

				holder.txtPoints.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sCurrent = "old";
						RelativeLayout rl_ = (RelativeLayout)v.getParent();
						RelativeLayout rl = (RelativeLayout)rl_.getParent();
						txtId  = (TextView)rl.getChildAt(1);
						edtQuantity = (EditText)rl.getChildAt(2);
						RelativeLayout relPoints = (RelativeLayout)rl.getChildAt(3);
						edtPoints = (EditText)relPoints.getChildAt(0);
						showEdit_Qty_Pts(sQty, sPoints, sCategory);
					}
				});


				if(IsActivityAdded(sName))
				{
					Drawable img = getResources().getDrawable(R.drawable.tick);
					holder.txtActivity.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
					holder.txtActivity.setCompoundDrawablePadding(10);
					
				}
				else
				{
					holder.txtActivity.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
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

	private boolean IsActivityAdded(String mName)
	{
		boolean isTrue=false;
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();

		if(Utils.currentTab.equalsIgnoreCase(Utils.strMorning))
		{
			mList = logDB.getActivityContacts();
		}
		else if(Utils.currentTab.equalsIgnoreCase(Utils.strNoon))
		{
			mList = logDB.getNoonActivityContacts();
		}
		/*else if(Utils.currentTab.equalsIgnoreCase(Utils.strEvening))
		{
			mList = logDB.getEveningActivityContacts();
		}*/
		else if(Utils.currentTab.equalsIgnoreCase(Utils.strNight))
		{
			mList = logDB.getNightActivityContacts();
		}


		for(int i=0; i<mList.size(); i++)
		{
			ActivitiesModel am = mList.get(i);
			String sName = am.get_name();

			if(sName.equalsIgnoreCase(mName))
			{
				isTrue = true;
				break;
			}
		}
		return isTrue;
	}

	public void myClickHandler(View v) 
	{
		RelativeLayout vwParentRow  = (RelativeLayout)v.getParent();
		edtMyPoints = (EditText)vwParentRow.getChildAt(4);

		final Dialog dialog = new Dialog(TabLayoutActivity.this);
		dialog.setTitle("Set your Points");
		dialog.setContentView(R.layout.holocircle);


		Button bSave = (Button)dialog.findViewById(R.id.btnSave);
		final TextView txtValue = (TextView)dialog.findViewById(R.id.seekArcProgress);
		CircularSeekBar seekBar = (CircularSeekBar)dialog.findViewById(R.id.circularSeekBar1);

		seekBar.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {

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
				txtValue.setText(Integer.toString(progress));
			}
		});


		bSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				edtMyPoints.setText(txtValue.getText().toString());
				dialog.dismiss();
			}
		});


		dialog.show();

	}



	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		category.setSelection(position);
		String selState = (String) category.getSelectedItem();
	}



	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}




}
