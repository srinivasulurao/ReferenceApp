package com.omkar.myactivitytracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.devadvance.circularseekbar.CircularSeekBar;
import com.devsmart.android.ui.HorizontalListView;
import com.omkar.myactivitytracker.Activities.ActivitiesAdapter.ViewHolder;
import com.omkar.myactivitytracker.Notes.BackgroundTask;
import com.omkar.myactivitytracker.Notes.BackgroundTaskArchieveList;

import com.personaltrainer.database.LoginDB;
import com.personaltrainer.model.ActivitiesModel;
import com.personaltrainer.model.ActivityPoints;

import com.personaltrainer.tabs.TabLayoutActivity;
import com.personaltrainer.widgets.Utils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.DeletedContacts;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class DayEnd extends Activity implements OnClickListener{

	CircularSeekBar morningSeekbar, noonSeekbar, eveningSeekbar, nightSeekbar;
	TextView morningProgress, noonProgress, eveningProgress, nightProgress;
	TextView txtMorning, txtNoon, txtEvening, txtNight, txtTodoPoints;
	EditText edtMessage;
	String sFrom="", title="";
	LoginDB logDB;
	EditText txtSpinner;
	int morningValue,noonValue,eveningValue,nightValue;
	List<Date> dateList;
	ImageView imgSmiley;
	TextView txtMessage;
	int imgPosition=0;
	String sJSONData="";
	Dialog dialog;
	List<String> data = new ArrayList<String>();

	private String[] state = { "Hurray! My Day was superb", "Huh!! My day went scray", "Hmm, Not bad, The day was quite ok", "It was really an awesome day.", 
			"It was fabolous"
	};

	void initilizeUI()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(" Complete your day");

		morningSeekbar = (CircularSeekBar)findViewById(R.id.morningSeekbar);
		noonSeekbar = (CircularSeekBar)findViewById(R.id.noonSeekbar);
		eveningSeekbar = (CircularSeekBar)findViewById(R.id.eveningSeekbar);
		nightSeekbar = (CircularSeekBar)findViewById(R.id.nightSeekbar);

		morningProgress = (TextView)findViewById(R.id.morningProgress);
		noonProgress = (TextView)findViewById(R.id.noonProgress);
		eveningProgress = (TextView)findViewById(R.id.eveningProgress);
		nightProgress = (TextView)findViewById(R.id.nightProgress);

		txtMorning = (TextView)findViewById(R.id.txtMorning);
		txtNoon = (TextView)findViewById(R.id.txtNoon);
		txtEvening= (TextView)findViewById(R.id.txtEvening);
		txtNight= (TextView)findViewById(R.id.txtNight);

		txtTodoPoints = (TextView)findViewById(R.id.txtTodoPoints);

		imgSmiley = (ImageView)findViewById(R.id.imgSmiley);
		edtMessage = (EditText)findViewById(R.id.edtMessage);
		txtMessage = (TextView)findViewById(R.id.txtMessage);

		txtSpinner = (EditText)findViewById(R.id.txtSpinner);
		txtSpinner.setInputType(InputType.TYPE_NULL);

		sFrom = getIntent().getStringExtra("from");
		logDB = new LoginDB(this);

		morningSeekbar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		noonSeekbar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		eveningSeekbar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		nightSeekbar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});


		AddNewDayMessage("");


	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dayend);

		initilizeUI();
		imgSmiley.setOnClickListener(this);

		TextView txtAdd = (TextView)findViewById(R.id.txtAdd);
		txtAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(DayEnd.this);
				alertDialog.setTitle("Add New Message");



				final EditText ed = new EditText(DayEnd.this);
				alertDialog.setView(ed);

				LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT,      
						LayoutParams.WRAP_CONTENT
						);
				params.setMargins(20, 20, 20, 20);
				ed.setPadding(10, 10, 10, 10);
				ed.setLayoutParams(params);



				alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {

						if(ed.getText().toString().trim().equalsIgnoreCase(""))
						{
							Utils.showAlertBoxSingle(DayEnd.this, "Error","Please Input the text");
						}
						else
						{
							dialog.cancel();
							String str = ed.getText().toString().trim();
							AddNewDayMessage(str);
						}

					}
				});
				alertDialog.show();

			}
		});


		txtSpinner.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dialog = new Dialog(DayEnd.this);
				dialog.setTitle("Choosse your Message"); 
				dialog.setContentView(R.layout.showmessages);
				
				ListView lv = (ListView)dialog.findViewById(R.id.listMessage);
				
				
				MessageAdapter mAdapter = new MessageAdapter(DayEnd.this, data);
				lv.setAdapter(mAdapter);
				
				dialog.show();
				
			}
		});
		/*txtSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView txt = (TextView)view.findViewById(R.id.txtMessage);
				title = txt.getText().toString().trim();

			

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});*/

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		showResult();

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.notes_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) 
		{
		case R.id.action_save:

			String sDate = Utils.currentDateOnDashboard;

			if(sDate.equalsIgnoreCase(""))
			{
				Utils.showAlertBoxSingle(DayEnd.this, "Warning", "Please make sure you have added the points to your activities, before you end the day");
			}
			else
			{
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						saveJSONData();
						SaveData();

						updateMorningMyPoints();
						updateNoongMyPoints();
						updateEveningMyPoints();
						updateNightMyPoints();
						updateToDoPoints();
						Utils.currentDateOnDashboard="";

						finish();
					}
				}).start();



			}


			break;

		}
		return true;
	}


	void saveJSONData()
	{
		try
		{
			JSONObject jMain = new JSONObject();

			List<ActivitiesModel> MorningList = new ArrayList<ActivitiesModel>();
			List<ActivitiesModel> NoonList = new ArrayList<ActivitiesModel>();
			List<ActivitiesModel> EveningList = new ArrayList<ActivitiesModel>();
			List<ActivitiesModel> NightList = new ArrayList<ActivitiesModel>();

			MorningList = logDB.getActivityContacts();
			NoonList = logDB.getNoonActivityContacts();
			EveningList = logDB.getEveningActivityContacts();
			NightList = logDB.getNightActivityContacts();
			Cursor c = logDB.getTodoList();

			JSONArray jMorning = new JSONArray();
			JSONArray jNoon = new JSONArray();
			JSONArray jEvening = new JSONArray();
			JSONArray jNight = new JSONArray();
			JSONArray jTodo = new JSONArray();

			for(int i=0; i<MorningList.size(); i++)
			{
				JSONObject jo = new JSONObject();
				ActivitiesModel am = MorningList.get(i);
				jo.put("name", am.get_name());
				jo.put("points", am.get_points());
				jo.put("mypoints", am.get_mypoints());

				jMorning.put(jo);
			}

			for(int i=0; i<NoonList.size(); i++)
			{
				JSONObject jo = new JSONObject();
				ActivitiesModel am = NoonList.get(i);
				jo.put("name", am.get_name());
				jo.put("points", am.get_points());
				jo.put("mypoints", am.get_mypoints());

				jNoon.put(jo);
			}

			for(int i=0; i<EveningList.size(); i++)
			{
				JSONObject jo = new JSONObject();
				ActivitiesModel am = EveningList.get(i);
				jo.put("name", am.get_name());
				jo.put("points", am.get_points());
				jo.put("mypoints", am.get_mypoints());

				jEvening.put(jo);
			}

			for(int i=0; i<NightList.size(); i++)
			{
				JSONObject jo = new JSONObject();
				ActivitiesModel am = NightList.get(i);
				jo.put("name", am.get_name());
				jo.put("points", am.get_points());
				jo.put("mypoints", am.get_mypoints());

				jNight.put(jo);
			}


			if(c.moveToFirst())
			{
				do
				{
					JSONObject jo = new JSONObject();

					String sChecked = c.getString(3);
					String sEnabled = c.getString(5);

					if(sChecked.equalsIgnoreCase("true") && sEnabled.equalsIgnoreCase("true"))
					{
						jo.put("name", c.getString(1));
						jo.put("points", c.getString(2));
						jo.put("mypoints", c.getString(3));
						jo.put("enable", c.getString(5));

						jTodo.put(jo);
					}

				}while(c.moveToNext());
			}


			jMain.put("MORNING", jMorning);
			jMain.put("NOON", jNoon);
			jMain.put("EVENING", jEvening);
			jMain.put("NIGHT", jNight);
			jMain.put("TODO", jTodo);

			sJSONData = jMain.toString();


		}catch(Exception e){}

	}

	void SaveData()
	{
		String sMessage = edtMessage.getText().toString().trim();
		String img_val = Integer.toString(imgPosition);


		List<ActivityPoints> mList = logDB.getActivityPoints();
		if(mList.isEmpty())
		{
			//Insert.
			logDB.addActivitiesPointsAll(Double.toString(Utils.morning_achieved_points), Double.toString(Utils.noon_achieved_points),
					Double.toString(Utils.evening_achieved_points), Double.toString(Utils.night_achieved_points), 
					Utils.currentDateOnDashboard,sMessage,title,img_val,Double.toString(Utils.OverallPoints),sJSONData);

		}
		else
		{

			ActivityPoints am = getDateList();
			if(am != null)
			{
				//Update.
				logDB.UpdateActivityPoints(am.getId(), Double.toString(Utils.morning_achieved_points), Double.toString(Utils.noon_achieved_points),
						Double.toString(Utils.evening_achieved_points), Double.toString(Utils.night_achieved_points), sMessage,title,img_val,
						Double.toString(Utils.OverallPoints),sJSONData);

			}
			else
			{
				//Insert.
				logDB.addActivitiesPointsAll(Double.toString(Utils.morning_achieved_points), Double.toString(Utils.noon_achieved_points),
						Double.toString(Utils.evening_achieved_points), Double.toString(Utils.night_achieved_points), 
						Utils.currentDateOnDashboard,sMessage,title,img_val,Double.toString(Utils.OverallPoints),sJSONData);


			}
		}



	}


	void updateMorningMyPoints()
	{
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getActivityContacts();

		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			int id = am.get_id();
			logDB.UpdateActivityMyPoints("0", id);
			logDB.UpdateActivityDate("", id);
		}
	}

	void updateNoongMyPoints()
	{
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getNoonActivityContacts();

		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			int id = am.get_id();
			logDB.UpdateNoonActivityMyPoints("0", id);
			logDB.UpdateNoonActivityDate("", id);
		}
	}

	void updateEveningMyPoints()
	{
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getEveningActivityContacts();

		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			int id = am.get_id();
			logDB.UpdateEveningActivityMyPoints("0", id);
			logDB.UpdateEveningActivityDate("", id);
		}
	}

	void updateNightMyPoints()
	{
		List<ActivitiesModel> mList = new ArrayList<ActivitiesModel>();
		mList = logDB.getNightActivityContacts();

		for(int i=0;i<mList.size();i++)
		{
			ActivitiesModel am = mList.get(i);
			int id = am.get_id();
			logDB.UpdateNightActivityMyPoints("0", id);
			logDB.UpdateNightActivityDate("", id);
		}
	}

	void updateToDoPoints()
	{
		Cursor c = logDB.getTodoList();
		if(c.moveToFirst())
		{
			do
			{
				int id = Integer.parseInt(c.getString(0));
				if(c.getString(3).equalsIgnoreCase("true"))
				{
					logDB.UpdateTodoEnable("false", id);
				}
				logDB.UpdateTodoCheck("false", id);
				logDB.UpdateTodoDate("", id);

			}while(c.moveToNext());
		}
	}

	ActivityPoints getDateList()
	{
		List<ActivityPoints> mList = logDB.getActivityPoints();
		for(int i=0; i<mList.size(); i++)
		{
			ActivityPoints am = mList.get(i);

			String sDate = am.getDate();
			String today = Utils.getTodaysDate(DayEnd.this);

			Date d1 = Utils.stringTodate(DayEnd.this, sDate);
			Date d2 = Utils.stringTodate(DayEnd.this, today);

			if(d1 != null)
			{
				if(d1.equals(d2))
				{
					return am;
				}
			}

		}
		return null;
	}

	void showResult()
	{
		morningSeekbar.setMax(Utils.morning_overall_points);
		morningSeekbar.setProgress(Utils.morning_achieved_points);
		txtMorning.setText("out of "+Integer.toString(Utils.morning_overall_points)+" pts."+"\n"+"   Morning");

		noonSeekbar.setMax(Utils.noon_overall_points);
		noonSeekbar.setProgress(Utils.noon_achieved_points);
		txtNoon.setText("out of "+Integer.toString(Utils.noon_overall_points)+" pts."+"\n"+"     Noon");

		eveningSeekbar.setMax(Utils.todo_overall_points);
		eveningSeekbar.setProgress(Utils.todo_achieved_points);
		txtEvening.setText("out of "+Integer.toString(Utils.todo_overall_points)+" pts."+"\n"+"     Todo's");

		nightSeekbar.setMax(Utils.night_overall_points);
		nightSeekbar.setProgress(Utils.night_achieved_points);
		txtNight.setText("out of "+Integer.toString(Utils.night_overall_points)+" pts."+"\n"+"     Evening");

		morningProgress.setText(Integer.toString(Utils.morning_achieved_points)+" pts");
		noonProgress.setText(Integer.toString(Utils.noon_achieved_points)+" pts");
		eveningProgress.setText(Integer.toString(Utils.todo_achieved_points)+" pts");
		nightProgress.setText(Integer.toString(Utils.night_achieved_points)+" pts");




		double total_=0.0;
		total_ = ((double)Utils.achievedPoints/(double)Utils.OverallPoints)*100.0;
		Double d = new Double(total_);
		int total = d.intValue();
		if(total>=0 && total <=25)
		{
			imgSmiley.setBackgroundResource(R.drawable.g);

		}
		else if(total>25 && total <=50)
		{
			imgSmiley.setBackgroundResource(R.drawable.a);

		}
		else if(total>50 && total <=75)
		{
			imgSmiley.setBackgroundResource(R.drawable.b);

		}
		else if(total>75 && total <=100)
		{
			imgSmiley.setBackgroundResource(R.drawable.h);

		}
	}

	void ActivityProgressSetUp()
	{
		Double dMorning = new Double(Utils.MORNING_POINTS);
		Double dNoon = new Double(Utils.NOON_POINTS);
		Double dEvening = new Double(Utils.EVENING_POINTS);
		Double dNight = new Double(Utils.NIGHT_POINTS);

		morningValue = 0; noonValue=0; eveningValue=0; nightValue=0;

		morningValue = dMorning.intValue();
		noonValue = dNoon.intValue();
		eveningValue = dEvening.intValue();
		nightValue = dNight.intValue();

		morningProgress.setText(Integer.toString(morningValue));
		noonProgress.setText(Integer.toString(noonValue));
		eveningProgress.setText(Integer.toString(eveningValue));
		nightProgress.setText(Integer.toString(nightValue));

		morningSeekbar.setProgress(morningValue);
		noonSeekbar.setProgress(noonValue);
		eveningSeekbar.setProgress(eveningValue);
		nightSeekbar.setProgress(nightValue);

		double  dtotal = ((morningValue + noonValue + eveningValue + nightValue)/400.0)*100.0 ;
		Double d = new Double(dtotal);
		int total = d.intValue();

		if(total>=0 && total <=25)
		{
			imgSmiley.setBackgroundResource(R.drawable.g);
			txtMessage.setText("Huh !! My day went Scary.");
		}
		else if(total>25 && total <=50)
		{
			imgSmiley.setBackgroundResource(R.drawable.a);
			txtMessage.setText("Hmmm, Not bad ! Was Good.");
		}
		else if(total>50 && total <=75)
		{
			imgSmiley.setBackgroundResource(R.drawable.b);
			txtMessage.setText("Hey, It was fab.");
		}
		else if(total>75 && total <=100)
		{
			imgSmiley.setBackgroundResource(R.drawable.h);
			txtMessage.setText("Ooooh,, It wa sawesome day.");
		}



	}

	void PreviopusActivityProgressSetUp()
	{
		Double dMorning = new Double(Utils.previous_morningPoints);
		Double dNoon = new Double(Utils.previous_noonPoints);
		Double dEvening = new Double(Utils.previous_eveningPoints);
		Double dNight = new Double(Utils.previous_nightPoints);


		morningValue = dMorning.intValue();
		noonValue = dNoon.intValue();
		eveningValue = dEvening.intValue();
		nightValue = dNight.intValue();

		morningProgress.setText(Integer.toString(morningValue));
		noonProgress.setText(Integer.toString(noonValue));
		eveningProgress.setText(Integer.toString(eveningValue));
		nightProgress.setText(Integer.toString(nightValue));

		morningSeekbar.setProgress(morningValue);
		noonSeekbar.setProgress(noonValue);
		eveningSeekbar.setProgress(eveningValue);
		nightSeekbar.setProgress(nightValue);
	}


	void ShowSmileyPopUp()
	{
		final Dialog dialog = new Dialog(DayEnd.this);
		dialog.setTitle("Choose Smiley !");
		dialog.setContentView(R.layout.smiley);

		HorizontalListView hListView = (HorizontalListView)dialog.findViewById(R.id.hlistview);
		SmileyAdapter sAdapter = new SmileyAdapter(DayEnd.this);
		hListView.setAdapter(sAdapter);

		dialog.show();
	}

	class SmileyAdapter extends BaseAdapter
	{
		private  LayoutInflater inflater=null;
		public Context mContext;
		int num=10;

		public  class ViewHolder
		{
			public ImageView img;
		}

		public SmileyAdapter(Context context)
		{
			mContext = context;
			inflater = ( LayoutInflater )mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return num;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View vi = convertView;
			ViewHolder holder = new ViewHolder();
			holder = new ViewHolder();
			if(convertView==null)
			{
				vi = inflater.inflate(R.layout.smiley_helper,null);
				holder.img= (ImageView)vi.findViewById(R.id.img);
				vi.setTag( holder );
			}
			else
			{
				holder=(ViewHolder)vi.getTag();
			}

			if(num<=0){}

			else
			{
				if(position==0)
					holder.img.setBackgroundResource(R.drawable.a);
				else if(position==1)
					holder.img.setBackgroundResource(R.drawable.b);
				else if(position==2)
					holder.img.setBackgroundResource(R.drawable.c);
				else if(position==3)
					holder.img.setBackgroundResource(R.drawable.d);
				else if(position==4)
					holder.img.setBackgroundResource(R.drawable.e);
				else if(position==5)
					holder.img.setBackgroundResource(R.drawable.f);
				else if(position==6)
					holder.img.setBackgroundResource(R.drawable.g);
				else if(position==7)
					holder.img.setBackgroundResource(R.drawable.h);
				else if(position==8)
					holder.img.setBackgroundResource(R.drawable.i);
				else if(position==9)
					holder.img.setBackgroundResource(R.drawable.j);
				else
					holder.img.setBackgroundResource(R.drawable.e);




				holder.img.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						imgPosition = position;

						if(position==0)
							imgSmiley.setBackgroundResource(R.drawable.a);
						else if(position==1)
							imgSmiley.setBackgroundResource(R.drawable.b);
						else if(position==2)
							imgSmiley.setBackgroundResource(R.drawable.c);
						else if(position==3)
							imgSmiley.setBackgroundResource(R.drawable.d);
						else if(position==4)
							imgSmiley.setBackgroundResource(R.drawable.e);
						else if(position==5)
							imgSmiley.setBackgroundResource(R.drawable.f);
						else if(position==6)
							imgSmiley.setBackgroundResource(R.drawable.g);
						else if(position==7)
							imgSmiley.setBackgroundResource(R.drawable.h);
						else if(position==8)
							imgSmiley.setBackgroundResource(R.drawable.i);
						else if(position==9)
							imgSmiley.setBackgroundResource(R.drawable.j);
						else
							imgSmiley.setBackgroundResource(R.drawable.e);
					}
				});

			}




			return vi;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.imgSmiley:
			ShowSmileyPopUp();
			break;
		}
	}

	private void AddNewDayMessage(String s)
	{
		data = new ArrayList<String>();
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DayEnd.this);
			boolean is_first = preferences.getBoolean("ISFIRST", false);

			if(!s.equalsIgnoreCase(""))
			{

				String str = preferences.getString("MESSAGES", "");

				List<String> data = new ArrayList<String>();
				JSONObject jo = new JSONObject(str);
				JSONArray ja = new JSONArray(jo.getString("msg"));

				ja.put(s);

				JSONObject jo_ = new JSONObject();
				jo_.put("msg", ja);

				String sMsg = jo_.toString();

				SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(DayEnd.this);
				SharedPreferences.Editor editor = preferences1.edit();
				editor.putString("MESSAGES",sMsg);
				editor.commit();

			}
			else
			{
				if(is_first)
				{}
				else{
					JSONArray ja = new JSONArray();
					ja.put("Hurray ! My day was good");
					ja.put("Huh ! My day went Scary.");
					ja.put("Hmm, Not bad, It was quite Ok");
					ja.put("It was an really an awesome day");
					ja.put("It was fabalous.");



					JSONObject jo = new JSONObject();
					jo.put("msg", ja);

					String sMsg = jo.toString();


					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("MESSAGES",sMsg);
					editor.putBoolean("ISFIRST", true);
					editor.commit();
				}

			}
		}catch(Exception e){}

		getMessages();

	}






	private void getMessages()
	{
		data = new ArrayList<String>();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DayEnd.this);
		String str = preferences.getString("MESSAGES", "");

		
		try{
			JSONObject jo = new JSONObject(str);
			JSONArray ja = new JSONArray(jo.getString("msg"));

			for(int i=0;i<ja.length();i++)
			{
				String s = ja.getString(i);
				data.add(s);
			}


			String sFirst = data.get(0);
			title = sFirst;
			txtSpinner.setText(sFirst);

		}catch(Exception e){}
	}




	public  class MessageAdapter extends BaseAdapter
	{
		private  LayoutInflater inflater=null;
		public Context mContext;

		int num;
		List<String> cn;

		public  class ViewHolder
		{
			public TextView txtMessage;
			public ImageView btnDelete;
			public RelativeLayout relMain;
		}

		public MessageAdapter(Context context, List<String> mList)
		{
			mContext = context;
			cn = mList;
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
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public JSONArray RemoveJSONArray( JSONArray jarray,int pos) {

			JSONArray Njarray=new JSONArray();
			try{
				for(int i=0;i<jarray.length();i++){     
					if(i!=pos)
						Njarray.put(jarray.get(i));     
				}
			}catch (Exception e){e.printStackTrace();}
			return Njarray;
		}

		@SuppressLint("NewApi")
		private void deleteMessage(int pos)
		{
			dialog.cancel();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
			String str = preferences.getString("MESSAGES", "");

			try
			{
				JSONObject jo = new JSONObject(str);
				JSONArray ja = new JSONArray(jo.getString("msg"));

				JSONArray ja_ = RemoveJSONArray(ja, pos);

				JSONObject jo_ = new JSONObject();
				jo_.put("msg", ja_);

				String sMsg = jo_.toString();

				SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(mContext);
				SharedPreferences.Editor editor = preferences1.edit();
				editor.putString("MESSAGES",sMsg);
				editor.commit();
			}
			catch(Exception e){}

			getMessages();

		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			View vi = convertView;
			inflater = ( LayoutInflater )mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ViewHolder holder = new ViewHolder();

			if(convertView==null)
			{

				vi = inflater.inflate(R.layout.message,null);

				holder.txtMessage= (TextView)vi.findViewById(R.id.txtMessage);
				holder.btnDelete= (ImageView)vi.findViewById(R.id.imgDelete);
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
				final String str = cn.get(position);
				if(!str.equalsIgnoreCase(""))
				{
					holder.txtMessage.setText(str);
				}
				
				holder.relMain.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						title = str;
						txtSpinner.setText(str);
					}
				});

				
				holder.relMain.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub

						AlertDialog.Builder builderSingle = new AlertDialog.Builder(
								mContext);
						builderSingle.setTitle("Choose the option.");
						final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
								mContext,
								android.R.layout.select_dialog_singlechoice);

						arrayAdapter.add("Delete");

						builderSingle.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});

						builderSingle.setAdapter(arrayAdapter,
								new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								if(which == 0)
								{
									deleteMessage(position);
								}


							}
						});
						builderSingle.show();

						return false;
					}
				});



			}


			return vi;
		}

	}
}
