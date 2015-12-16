package com.omkar.myactivitytracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.omkar.myactivitytracker.Notes.NotesAdapter.ViewHolder;

import com.personaltrainer.database.LoginDB;
import com.personaltrainer.model.Todo_model;

import com.personaltrainer.tabs.TabLayoutActivity;
import com.personaltrainer.widgets.TodoInterface;
import com.personaltrainer.widgets.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker.OnValueChangeListener;

public class Todo extends Activity implements OnClickListener, TodoInterface{

	private EditText edtToDoName, et;
	private TextView txtPoints;
	private ImageView imgSave;
	private ListView listTodo;
	private NumberPicker np;
	private LoginDB logDB;
	private List<Todo_model> mList = new ArrayList<Todo_model>();
	private TodoAdapter mAdapter;
	BackgroundTask bg ;

	private void initilizeUI()
	{
		edtToDoName = (EditText)findViewById(R.id.edtToDoName);
		txtPoints = (TextView)findViewById(R.id.txtPoints);
		imgSave = (ImageView)findViewById(R.id.imgSave);
		listTodo = (ListView)findViewById(R.id.listTodo);

		logDB = new LoginDB(this);

		imgSave.setOnClickListener(this);
		txtPoints.setOnClickListener(this);
		
		String s1 = "Add\nPoints";
		txtPoints.setText(s1);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo);

		initilizeUI();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		bg = new BackgroundTask();
		bg.execute("");

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.imgSave:

			String sTodoName = edtToDoName.getText().toString().trim();
			String sPoints = txtPoints.getText().toString().trim();

			if(sTodoName.equalsIgnoreCase(""))
				Utils.showAlertBoxSingle(Todo.this, "Error", "Enter the Todo Name");

			else if(sPoints.equalsIgnoreCase("Points") || sPoints.equalsIgnoreCase("0"))
				Utils.showAlertBoxSingle(Todo.this, "Error", "Select the points.");

			else
			{
				edtToDoName.setText("");
				txtPoints.setText("Points");

				logDB.addTODOActivities(sTodoName, sPoints, "false", "true");

				bg = new BackgroundTask();
				bg.execute("");
				
				Intent in = new Intent("com.omkar.myactivitytracker");
				sendBroadcast(in);
			}

			break;

		case R.id.txtPoints:
			showNumberDialog();
			break;
		}
	}



	@Override
	public void showNumberDialog() {
		// TODO Auto-generated method stub


		final Dialog d = new Dialog(Todo.this);
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

				String sValue = et.getText().toString().trim();

				if(sValue.equalsIgnoreCase("0"))
				{
					Utils.showAlertBoxSingle(Todo.this, "Error", "Please Select the value greater then zero");
				}
				else
					txtPoints.setText(sValue);

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

	class BackgroundTask extends AsyncTask<String, Void, String>
	{


		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mList = new ArrayList<Todo_model>();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			Cursor c = logDB.getTodoList();
			
			//Add first with UnChecked:
			if(c.moveToFirst())
			{
				do
				{
					Todo_model todo = new Todo_model();

					
						todo.setId(c.getString(0));
						todo.setName(c.getString(1));
						todo.setPoints(c.getString(2));
						todo.setAdded(c.getString(3));
						todo.setDate(c.getString(4));
						todo.setEnable(c.getString(5));

						mList.add(todo);
					
				}while(c.moveToNext());
			}

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
				map.put("TAG", "No Todo List is Available.");
				oslist.add(map);

				ListAdapter adapter = new SimpleAdapter(Todo.this, oslist, R.layout.nodata, new String[] {"TAG"}, 
						new int[] {R.id.txtNoData});

				listTodo.setAdapter(adapter);
			}
			else
			{
				Collections.reverse(mList);
				mAdapter = new TodoAdapter(Todo.this, mList);
				listTodo.setAdapter(mAdapter);
			}
		}

	}

	@Override
	public void getToDOList() {
		// TODO Auto-generated method stub

		Cursor c = logDB.getTodoList();
		mList = new ArrayList<Todo_model>();

		if(c.moveToFirst())
		{
			do
			{
				Todo_model todo = new Todo_model();

				todo.setId(c.getString(0));
				todo.setName(c.getString(1));
				todo.setPoints(c.getString(2));
				todo.setAdded(c.getString(3));
				todo.setDate(c.getString(4));


				mList.add(todo);

			}while(c.moveToNext());
		}


		if(mList.isEmpty())
		{
			ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("TAG", "No Todo List is Available.");
			oslist.add(map);

			ListAdapter adapter = new SimpleAdapter(this, oslist, R.layout.nodata, new String[] {"TAG"}, 
					new int[] {R.id.txtNoData});

			listTodo.setAdapter(adapter);
		}
		else
		{
			mAdapter = new TodoAdapter(Todo.this, mList);
			listTodo.setAdapter(mAdapter);
		}

	}


	class TodoAdapter extends BaseAdapter
	{
		private  LayoutInflater inflater=null;
		public Context mContext;

		LoginDB logDB;
		int num;
		List<Todo_model> cn;

		public  class ViewHolder
		{
			public TextView txtId;
			public CheckBox chkName;
			public TextView txtPoints;
			public TextView txtAdded;
			public TextView txtDate;
			public ImageView imgDelete;
			public RelativeLayout relMain;

		}

		public TodoAdapter(Context context, List<Todo_model> mList)
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
				vi = inflater.inflate(R.layout.todo_helper,null);

				holder.chkName = (CheckBox)vi.findViewById(R.id.chk_todo_name);
				holder.txtPoints = (TextView)vi.findViewById(R.id.txtPoints);
				holder.txtId = (TextView)vi.findViewById(R.id.txtId);
				holder.txtAdded= (TextView)vi.findViewById(R.id.txtAdded);
				holder.txtDate = (TextView)vi.findViewById(R.id.txtDate);
				holder.imgDelete = (ImageView)vi.findViewById(R.id.imgDelete);
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
				Todo_model todo = new Todo_model();

				todo = cn.get(position);

				final String sId = todo.getId();
				final String sName = todo.getName();
				final String sPoints = todo.getPoints();
				final String sAdded = todo.getAdded();
				final String sDate = todo.getDate();
				final String sEnable = todo.getEnable();

				holder.chkName.setText(sName);
				holder.txtId.setText(sId);
				holder.txtPoints.setText(sPoints+" points");
				holder.txtAdded.setText(sAdded);
				holder.txtDate.setText(sDate);

				


				/*
				 * Delete Event Listener.
				 */
				holder.imgDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
						alertDialog.setTitle("Confirm Delete");
						alertDialog.setMessage("Are you sure you want to remove this item ?");


						alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {

								dialog.cancel();

								logDB.ToDoDeleteRowById(Integer.parseInt(sId));
								bg = new BackgroundTask();
								bg.execute("");

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

				/*
				 * Checkbox Listener.
				 */
				holder.chkName.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						if(isChecked)
						{
							logDB.UpdateTodoCheck("true", Integer.parseInt(sId));
							logDB.UpdateTodoDate(Utils.getTodaysDate(Todo.this), Integer.parseInt(sId));
							
							Intent in = new Intent("com.omkar.myactivitytracker");
							sendBroadcast(in);
						}
						else
						{
							logDB.UpdateTodoCheck("false", Integer.parseInt(sId));
							logDB.UpdateTodoDate("", Integer.parseInt(sId));
							
							Intent in = new Intent("com.omkar.myactivitytracker");
							sendBroadcast(in);
						}
					}
				});

				


				if(sEnable.equalsIgnoreCase("false"))
				{
					holder.relMain.setBackgroundResource(R.color.gray);
					holder.chkName.setChecked(true);
					holder.chkName.setEnabled(false);
				}
				else
				{
					holder.chkName.setEnabled(true);
					
					if(sAdded.equalsIgnoreCase("true"))
					{
						holder.chkName.setChecked(true);
					}
					else
					{
						holder.chkName.setChecked(false);
					}
					
					if(position % 2 == 0)
					{
						holder.relMain.setBackgroundResource(R.drawable.background1);
					}
					else
					{
						holder.relMain.setBackgroundResource(R.drawable.background2);
					}
				}


			}


			return vi;
		}

	}

}
