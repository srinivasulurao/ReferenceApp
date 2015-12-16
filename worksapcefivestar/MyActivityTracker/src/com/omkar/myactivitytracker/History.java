package com.omkar.myactivitytracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.devadvance.circularseekbar.CircularSeekBar;
import com.omkar.myactivitytracker.Activities.ActivitiesAdapter.ViewHolder;

import com.personaltrainer.database.LoginDB;
import com.personaltrainer.model.ActivitiesModel;
import com.personaltrainer.model.ActivityPoints;

import com.personaltrainer.widgets.Utils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@SuppressLint("UseValueOf")
public class History extends Activity {

	ListView list_history;
	List<ActivityPoints> mList;
	LoginDB logDB;
	HistoryAdapter hAdapter;
	String sDay ;

	private void initilizeU() {
		// TODO Auto-generated method stub

		ActionBar actionBar = getActionBar();
		actionBar.setTitle(" History");

		mList = new ArrayList<ActivityPoints>();
		list_history = (ListView)findViewById(R.id.list_history);
		logDB = new LoginDB(this);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		initilizeU();

	}

	class BackgroundTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mList = new ArrayList<ActivityPoints>();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			mList = logDB.getActivityPoints();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if(mList.isEmpty())
			{
				ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("TAG", "There is no History Available.");
				oslist.add(map);

				ListAdapter adapter = new SimpleAdapter(History.this, oslist, R.layout.nodata, new String[] {"TAG"}, 
						new int[] {R.id.txtNoData});

				list_history.setAdapter(adapter);

			}
			else
			{
				Collections.reverse(mList);
				hAdapter = new HistoryAdapter(History.this, mList);
				list_history.setAdapter(hAdapter);
			}
		}

	}

	@SuppressLint("UseValueOf")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		BackgroundTask bg = new BackgroundTask();
		bg.execute("");

		list_history.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("UseValueOf")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int id, long position) {
				// TODO Auto-generated method stub

				if(mList.isEmpty())
				{}
				else
				{
					final TextView txtMorning = (TextView)view.findViewById(R.id.txtMorning);
					final TextView txtNoon = (TextView)view.findViewById(R.id.txtNoon);
					final TextView txtEvening = (TextView)view.findViewById(R.id.txtEvening);
					final TextView txtNight = (TextView)view.findViewById(R.id.txtNight);
					final TextView txtId = (TextView)view.findViewById(R.id.txtId);
					final TextView txtDay = (TextView)view.findViewById(R.id.txtDay);


					sDay = txtDay.getText().toString().trim();


					final Dialog dialog = new Dialog(History.this);
					dialog.setTitle("Result");
					dialog.setContentView(R.layout.history_popup);

					dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

					CircularSeekBar morningSeekbar = (CircularSeekBar)dialog.findViewById(R.id.morningSeekbar);
					CircularSeekBar noonSeekbar = (CircularSeekBar)dialog.findViewById(R.id.noonSeekbar);
					CircularSeekBar eveningSeekbar = (CircularSeekBar)dialog.findViewById(R.id.eveningSeekbar);
					CircularSeekBar nightSeekbar = (CircularSeekBar)dialog.findViewById(R.id.nightSeekbar);

					TextView morningProgress = (TextView)dialog.findViewById(R.id.morningProgress);
					TextView noonProgress = (TextView)dialog.findViewById(R.id.noonProgress);
					TextView eveningProgress = (TextView)dialog.findViewById(R.id.eveningProgress);
					TextView nightProgress = (TextView)dialog.findViewById(R.id.nightProgress);

					TextView txtMorning_ = (TextView)dialog.findViewById(R.id.txtMorning);
					TextView txtNoon_ = (TextView)dialog.findViewById(R.id.txtNoon);
					TextView txtEvening_ = (TextView)dialog.findViewById(R.id.txtEvening);
					TextView txtNight_= (TextView)dialog.findViewById(R.id.txtNight);

					TextView txtTodo = (TextView)dialog.findViewById(R.id.txtToDo);

					Button bClose = (Button)dialog.findViewById(R.id.btnClose);
					Button bDelete = (Button)dialog.findViewById(R.id.btnDelete);


					morningProgress.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							final Dialog dialog = new Dialog(History.this);
							dialog.setTitle("Morning Data");
							dialog.setContentView(R.layout.activity_data);
							ListView mListViewData = (ListView)dialog.findViewById(R.id.list_data);

							JSONArray jData = getIndividualActivityData(Utils.strMorning);
							if(jData.length()<=0)
							{

								ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("TAG", "No Data Available.");
								oslist.add(map);

								ListAdapter adapter = new SimpleAdapter(History.this, oslist, R.layout.nodata, new String[] {"TAG"}, 
										new int[] {R.id.txtNoData});

								mListViewData.setAdapter(adapter);

							}
							else{
								ActivityDataAdapter adapter = new ActivityDataAdapter(History.this,jData);
								mListViewData.setAdapter(adapter);
							}
							dialog.show();

						}
					});

					noonProgress.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							final Dialog dialog = new Dialog(History.this);
							dialog.setTitle("Noon Data");
							dialog.setContentView(R.layout.activity_data);
							ListView mListViewData = (ListView)dialog.findViewById(R.id.list_data);

							JSONArray jData = getIndividualActivityData(Utils.strNoon);
							if(jData.length()<=0)
							{

								ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("TAG", "No Data Available.");
								oslist.add(map);

								ListAdapter adapter = new SimpleAdapter(History.this, oslist, R.layout.nodata, new String[] {"TAG"}, 
										new int[] {R.id.txtNoData});

								mListViewData.setAdapter(adapter);

							}
							else{
								ActivityDataAdapter adapter = new ActivityDataAdapter(History.this,jData);
								mListViewData.setAdapter(adapter);
							}

							dialog.show();

						}
					});

					eveningProgress.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							final Dialog dialog = new Dialog(History.this);
							dialog.setTitle("Todo Data");
							dialog.setContentView(R.layout.activity_data);
							ListView mListViewData = (ListView)dialog.findViewById(R.id.list_data);

							JSONArray jData = getIndividualActivityData("todo");
							if(jData.length()<=0)
							{

								ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("TAG", "No Data Available.");
								oslist.add(map);

								ListAdapter adapter = new SimpleAdapter(History.this, oslist, R.layout.nodata, new String[] {"TAG"}, 
										new int[] {R.id.txtNoData});

								mListViewData.setAdapter(adapter);

							}
							else{
								ActivityDataAdapter adapter = new ActivityDataAdapter(History.this,jData);
								mListViewData.setAdapter(adapter);
							}
							dialog.show();

						}
					});

					nightProgress.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							final Dialog dialog = new Dialog(History.this);
							dialog.setTitle("Evening Data");
							dialog.setContentView(R.layout.activity_data);
							ListView mListViewData = (ListView)dialog.findViewById(R.id.list_data);

							JSONArray jData = getIndividualActivityData(Utils.strNight);
							if(jData.length()<=0)
							{

								ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("TAG", "No Data Available.");
								oslist.add(map);

								ListAdapter adapter = new SimpleAdapter(History.this, oslist, R.layout.nodata, new String[] {"TAG"}, 
										new int[] {R.id.txtNoData});

								mListViewData.setAdapter(adapter);

							}
							else{
								ActivityDataAdapter adapter = new ActivityDataAdapter(History.this,jData);
								mListViewData.setAdapter(adapter);
							}

							dialog.show();

						}
					});

					bDelete.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							logDB.ActivityPointsdelete_byID(Integer.parseInt(txtId.getText().toString().trim()));
							BackgroundTask bg = new BackgroundTask();
							bg.execute("");
							dialog.cancel();
						}
					});
					bClose.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});

					int MorningPoints = getActivityOverallPoints(Utils.strMorning);
					int NoonPoints = getActivityOverallPoints(Utils.strNoon);
					int NightPoints = getActivityOverallPoints(Utils.strNight);

					
					
					txtMorning_.setText("Out of "+Integer.toString(MorningPoints)+" pts.");
					txtNoon_.setText("Out of "+Integer.toString(NoonPoints)+" pts.");
					//txtEvening_.setText("Out of "+Double.toString(todo_achieved)+" pts.");
					txtNight_.setText("Out of "+Integer.toString(NightPoints)+" pts.");


					Double dMorning = new Double(Double.parseDouble(txtMorning.getText().toString().trim()));
					Double dNoon = new Double(Double.parseDouble(txtNoon.getText().toString().trim()));
					Double dEvening = new Double(Double.parseDouble(txtEvening.getText().toString().trim()));
					Double dNight = new Double(Double.parseDouble(txtNight.getText().toString().trim()));

					int iMorning = dMorning.intValue();
					int iNoon= dNoon.intValue();
					int iEvening= dEvening.intValue();
					int iNight = dNight.intValue();

					morningSeekbar.setMax(MorningPoints); 
					morningSeekbar.setProgress(iMorning); 

					noonSeekbar.setMax(NoonPoints);
					noonSeekbar.setProgress(iNoon); 	

					eveningSeekbar.setMax(iEvening);
					eveningSeekbar.setProgress(iEvening);	

					nightSeekbar.setMax(NightPoints);
					nightSeekbar.setProgress(iNight);			

					morningProgress.setText(Integer.toString(iMorning)+" pts");
					noonProgress.setText(Integer.toString(iNoon)+" pts");
					eveningProgress.setText(Integer.toString(iEvening)+" pts");
					nightProgress.setText(Integer.toString(iNight)+" pts");
					txtEvening_.setText("Out of "+Integer.toString(iEvening)+" pts.");

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



					dialog.show();
				}
			}
		});
	}



	private int getActivityOverallPoints(String sFrom)
	{
		JSONArray ja = new JSONArray();
		int points=0;
		String sActivity="";
		try
		{
			JSONObject jo = new JSONObject(sDay);
			if(sFrom.equalsIgnoreCase(Utils.strMorning))
			{
				sActivity = jo.getString("MORNING");
			}
			else if(sFrom.equalsIgnoreCase(Utils.strNoon))
			{
				sActivity = jo.getString("NOON");
			}
			else if(sFrom.equalsIgnoreCase("todo"))
			{
				sActivity = jo.getString("TODO");
			}
			else if(sFrom.equalsIgnoreCase(Utils.strNight))
			{
				sActivity = jo.getString("NIGHT");
			}

			ja = new JSONArray(sActivity);

			for(int i=0; i<ja.length(); i++)
			{
				JSONObject jObject = ja.getJSONObject(i);
				points += Integer.parseInt(jObject.getString("points"));
			}

		}
		catch(Exception e)
		{}
		return points;
	}

	JSONArray getIndividualActivityData(String sFrom)
	{
		JSONArray ja = new JSONArray();
		try
		{
			JSONObject jo = new JSONObject(sDay);


			if(sFrom.equalsIgnoreCase(Utils.strMorning))
			{
				String sMorning = jo.getString("MORNING");
				ja = new JSONArray(sMorning);

			}
			else if(sFrom.equalsIgnoreCase(Utils.strNoon))
			{
				String sMorning = jo.getString("NOON");
				ja = new JSONArray(sMorning);

			}
			else if(sFrom.equalsIgnoreCase(Utils.strNight))
			{
				String sMorning = jo.getString("NIGHT");
				ja = new JSONArray(sMorning);

			}
			else
			{
				String sMorning = jo.getString("TODO");
				ja = new JSONArray(sMorning);
			}
		}
		catch(Exception e)
		{}

		return ja;
	}

	public class ActivityDataAdapter extends BaseAdapter
	{
		private  LayoutInflater inflater=null;
		public Context mContext;

		LoginDB logDB;
		int num;
		JSONArray cn;

		public  class ViewHolder
		{
			public TextView txtName;
			public SeekBar seekbar;
			public TextView txtMin;
			public TextView txtVal;
			public TextView txtMax;
		}


		public ActivityDataAdapter(Context context, JSONArray ja)
		{
			mContext = context;
			cn = ja;
			inflater = ( LayoutInflater )mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			num = cn.length();
			if(num<=0)
			{
				return 0;
			}
			else
			{
				return num;
			}

		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View vi = convertView;

			ViewHolder holder = new ViewHolder();
			holder = new ViewHolder();
			if(convertView==null)
			{

				vi = inflater.inflate(R.layout.zdata_helper,null);

				holder.txtName = (TextView)vi.findViewById(R.id.txtName);
				holder.seekbar = (SeekBar)vi.findViewById(R.id.seekbar);
				holder.txtMin= (TextView)vi.findViewById(R.id.txtMin);
				holder.txtVal= (TextView)vi.findViewById(R.id.txtVal);
				holder.txtMax= (TextView)vi.findViewById(R.id.txtMax);

				vi.setTag( holder );
			}
			else
			{
				holder=(ViewHolder)vi.getTag();
			}


			if(num<=0)
			{}

			else
			{
				String sName="";
				String pts ="";
				String my_pts ="";
				try
				{
					String sData = cn.get(position).toString();
					JSONObject jo = new JSONObject(sData);

					sName = jo.getString("name");
					pts = jo.getString("points");
					my_pts = jo.getString("mypoints");

					holder.seekbar.setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							// TODO Auto-generated method stub
							return true;
						}
					});
					
				}
				catch(Exception e){}

				if(my_pts.equalsIgnoreCase("true"))
				{
					holder.txtName.setText(sName+" ( 100% )");
					holder.seekbar.setMax(Integer.parseInt(pts));
					holder.seekbar.setProgress(Integer.parseInt(pts));
					
					holder.txtVal.setText(pts+" pts");
					holder.txtMax.setText(pts);
				}
				else if(my_pts.equalsIgnoreCase("false"))
				{
					holder.txtName.setText(sName+" ( 0% )");
					holder.seekbar.setMax(Integer.parseInt(pts));
					holder.seekbar.setProgress(0);
					
					holder.txtVal.setText("0 pts");
					holder.txtMax.setText(pts);
				}
				else
				{
					double dPercent = ((double)Integer.parseInt(my_pts)/(double)Integer.parseInt(pts))*100.0;
					int value = Utils.convertDoubletoInt(dPercent);

					holder.txtName.setText(sName+" ( "+ Integer.toString(value)+" % )");
					holder.txtVal.setText(my_pts+" pts");
					holder.txtMax.setText(pts);
					
					if(pts!=null)
					{
						holder.seekbar.setMax(Integer.parseInt(pts));
					}
					else
					{
						holder.seekbar.setMax(0);
					}
					
					if(my_pts!=null)
					{
						holder.seekbar.setProgress(Integer.parseInt(my_pts));
					}
					else
					{
						holder.seekbar.setProgress(0);
					}

					

				}





			}

			return vi;
		}

	}



	double todo_total=0.0, todo_achieved=0.0;

	void getTodoPoints(String sData)
	{
		todo_total=0.0;
		todo_achieved=0.0;;

		try
		{
			JSONObject jMain = new JSONObject(sData);
			String sTODO = jMain.getString("TODO");
			JSONArray ja = new JSONArray(sTODO);

			for(int i=0; i<ja.length(); i++)
			{
				JSONObject jo = ja.getJSONObject(i);

				if(jo.getString("mypoints").equalsIgnoreCase("true"))
				{
					todo_total+= Double.parseDouble(jo.getString("points"));
					todo_achieved+= Double.parseDouble(jo.getString("points"));
				}
				else
				{
					todo_total+= Double.parseDouble(jo.getString("points"));
				}
			}


		}
		catch(Exception e){}
	}

	public  class HistoryAdapter extends BaseAdapter
	{
		private  LayoutInflater inflater=null;
		public Context mContext;

		LoginDB logDB;
		int num;
		List<ActivityPoints> cn;

		public  class ViewHolder
		{
			public TextView txtId;
			public TextView txtMorning;
			public TextView txtNoon;
			public TextView txtEvening;
			public TextView txtNight;
			public TextView txtDate;
			public TextView txtMessage;
			public TextView txtTitle;
			public TextView txtPercent;
			public ImageView img;
			public RelativeLayout linMain;
			public TextView txtTotal;
			public TextView txtDay;
		}

		public HistoryAdapter(Context context, List<ActivityPoints> mList)
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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View vi = convertView;

			ViewHolder holder = new ViewHolder();
			holder = new ViewHolder();
			if(convertView==null)
			{

				vi = inflater.inflate(R.layout.histiry_helper,null);

				holder.txtId = (TextView)vi.findViewById(R.id.txtId);
				holder.txtMorning= (TextView)vi.findViewById(R.id.txtMorning);
				holder.txtNoon= (TextView)vi.findViewById(R.id.txtNoon);
				holder.txtEvening= (TextView)vi.findViewById(R.id.txtEvening);
				holder.txtNight= (TextView)vi.findViewById(R.id.txtNight);
				holder.txtDate= (TextView)vi.findViewById(R.id.txtDate);
				holder.txtMessage= (TextView)vi.findViewById(R.id.txtMessage);
				holder.txtTitle= (TextView)vi.findViewById(R.id.txtTitle);
				holder.txtPercent= (TextView)vi.findViewById(R.id.txtPercent);
				holder.img = (ImageView)vi.findViewById(R.id.img);
				holder.linMain = (RelativeLayout)vi.findViewById(R.id.linMain);
				holder.txtTotal = (TextView)vi.findViewById(R.id.txtTotal);
				holder.txtDay = (TextView)vi.findViewById(R.id.txtDay);

				vi.setTag( holder );
			}
			else
			{
				holder=(ViewHolder)vi.getTag();
			}

			if(num<=0){}

			else
			{
				final ActivityPoints am = cn.get(position);

				String sId = Integer.toString(am.getId());
				String sMorning = am.getMorning_points();
				String sNoon = am.getNoon_points();
				String sEvening = am.getEvening_points();
				String sNight = am.getNight_points();
				String sDate = am.getDate();
				String sMessage = am.getMessage();
				String sTitle = am.getTitle();
				String sImgVal = am.getImgvalue();
				String sTotal = am.getTotal();
				String sData = am.getData();



				getTodoPoints(sData);


				int val = Integer.parseInt(sImgVal);
				Double dMorning = Double.parseDouble(sMorning);
				Double dNoon = Double.parseDouble(sNoon);
				Double dEvening = Double.parseDouble(sEvening);
				Double dNight = Double.parseDouble(sNight);
				Double DTotal = Double.parseDouble(sTotal);

				Double todoAchieved = todo_achieved;
				Double todoTotal = todo_total;

				Double total = ((dMorning + dNoon + dEvening + dNight + todo_achieved )/ ( DTotal )  )*100.0;
				Double dTotal = new Double(total);
				int score = dTotal.intValue();

				holder.txtId.setText(sId);
				holder.txtMorning.setText(sMorning);
				holder.txtNoon.setText(sNoon);
				holder.txtEvening.setText(Double.toString(todo_achieved));
				holder.txtNight.setText(sNight);
				holder.txtDate.setText(sDate);
				holder.txtMessage.setText(sMessage);
				holder.txtTitle.setText(sTitle);
				holder.txtPercent.setText(Integer.toString(score)+"%");
				holder.txtTotal.setText(sTotal);
				holder.txtDay.setText(sData);

				if(position%2 == 0)
				{
					holder.linMain.setBackgroundResource(R.color.listbg1);
				}
				else
				{
					holder.linMain.setBackgroundResource(R.color.listbg2);
				}

				if(val==0)
					holder.img.setBackgroundResource(R.drawable.a);
				else if(val==1)
					holder.img.setBackgroundResource(R.drawable.b);
				else if(val==2)
					holder.img.setBackgroundResource(R.drawable.c);
				else if(val==3)
					holder.img.setBackgroundResource(R.drawable.d);
				else if(val==4)
					holder.img.setBackgroundResource(R.drawable.e);
				else if(val==5)
					holder.img.setBackgroundResource(R.drawable.f);
				else if(val==6)
					holder.img.setBackgroundResource(R.drawable.g);
				else if(val==7)
					holder.img.setBackgroundResource(R.drawable.h);
				else if(val==8)
					holder.img.setBackgroundResource(R.drawable.i);
				else if(val==9)
					holder.img.setBackgroundResource(R.drawable.j);
				else
					holder.img.setBackgroundResource(R.drawable.e);



			}
			return vi;
		}
	}

}
