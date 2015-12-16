package com.texastech.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.teaxstech.db.DbHelper;
import com.texastech.app.AlarmReceiver;
import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.bean.Reminder;
import com.texastech.helper.DateTimeUtil;
import com.texastech.helper.TimePickerFragment;
import com.texastech.helper.TimePickerFragment.TimePickedListener;

@SuppressLint("NewApi")
public class ToDoActivity extends BaseActivity implements TimePickedListener{

	private boolean isListEnable = true;
	
	//private PopupWindow mpopup;
	private Dialog mpopup;
	
	private ToDoAdapter adapter;
	
	private TextView setTvTime;
	
	@InjectView(R.id.btn_clear)
	TextView tvClear;
	
	@InjectView(R.id.btn_edit)
	TextView tvEdit;
	
	@InjectView(R.id.tv_no_to_do_item)
	TextView tvNoToDo;
	
	@InjectView(R.id.list_to_do)
	ListView listToDo;
 
	@InjectView(R.id.btn_add_new)
	Button btnAddNew;
	
	private List<Reminder> toDoList = new ArrayList<Reminder>();

	private DbHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_to_do);
		try {
			dbHelper = new DbHelper(getApplicationContext());
			setListView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setListView(){
		toDoList.clear();
		if (dbHelper.getReminderList(DbHelper.TBL_TODO).isEmpty()) {
			listToDo.setVisibility(View.GONE);
			tvNoToDo.setVisibility(View.VISIBLE);
		} else {
			listToDo.setVisibility(View.VISIBLE);
			tvNoToDo.setVisibility(View.GONE);
			toDoList.addAll(dbHelper.getReminderList(DbHelper.TBL_TODO));
		}
		adapter = new ToDoAdapter();
		listToDo.setAdapter(adapter);
	}

	@Override
	public void setTitle(TextView tv) {
		tv.setText("TO-DO");
	} 

	@Override
	public void initXmlView() {
		 
	}

	 
	@OnClick(R.id.btn_add_new)
	public void addNewToDo(View view){
		showPopUpWindow(null);
	}
	
	
	private void showPopUpWindow(final Reminder reminder){
		/*View popUpView = getLayoutInflater().inflate(R.layout.dialog_to_do, null); // inflating popup layout
        mpopup = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true); //Creation of popup
        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);   
        mpopup.showAtLocation(popUpView, Gravity.CENTER, 0, 0); */   // Displaying popup
        
		mpopup = new Dialog(context);
		mpopup.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		mpopup.setContentView(R.layout.dialog_to_do);
		
        final EditText editText = (EditText)mpopup.findViewById(R.id.et_to_do);
        final Button btnJustOnce = (Button)mpopup.findViewById(R.id.btn_justOnce);
        final Button btnWeekDay = (Button)mpopup.findViewById(R.id.btn_weekDay);
        final TextView tvTime = (TextView)mpopup.findViewById(R.id.tvTime);
        
        //-----------------------------//
        if(reminder!=null){
        	editText.setText(reminder.getName());
			tvTime.setText(reminder.getReminderTime());
			if(reminder.getReminderType().equals("")){
				btnWeekDay.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
				btnWeekDay.setTag(0);
				
				btnJustOnce.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
				btnJustOnce.setTag(0);
			}else{
				if(btnJustOnce.getTag().toString().equals("J")){
					btnWeekDay.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
					btnWeekDay.setTag(0);
					
					btnJustOnce.setBackgroundColor(getResources().getColor(R.color.red));
					btnJustOnce.setTag(1);
				}else{
					btnJustOnce.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
					btnJustOnce.setTag(0);
					
					btnWeekDay.setBackgroundColor(getResources().getColor(R.color.red));
					btnWeekDay.setTag(1);
				}
			}
        }
        //-----------------------------//
    	tvTime.setOnClickListener(new OnClickListener() {                    
            @Override
            public void onClick(View v){
            	setTvTime = (TextView)v;
                TimePickerFragment newFragment = new TimePickerFragment(ToDoActivity.this);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        
        TextView btnOk = (TextView)mpopup.findViewById(R.id.btn_save);
        btnOk.setOnClickListener(new OnClickListener(){                    
            @Override
            public void onClick(View v){
            	if(TextUtils.isEmpty(editText.getText().toString())){
            		editText.setError("Field should not be blank!");
            		return;
            	}
            	
            	if(reminder!=null){ //delete previous
            		Intent activate = new Intent(context, AlarmReceiver.class);
        			PendingIntent alarmIntent = PendingIntent.getBroadcast(context,reminder.getId(), activate, 0);
        			AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        			alarms.cancel(alarmIntent);
        			dbHelper.deleteReminderRow(DbHelper.TBL_TODO, reminder.getId());
            	}
            	
                //dismissing the popup
            	Reminder toDo = new Reminder();
				toDo.setName(editText.getText().toString());
				
				if(TextUtils.isEmpty(tvTime.getText().toString()) ||(btnJustOnce.getTag().equals(0) && btnWeekDay.getTag().equals(0))){
					//no time
					toDo.setReminderTime("");
					toDo.setReminderType("");
					toDo.setReminderDateTime("");
					tvTime.setText("");
					dbHelper.saveReminder(DbHelper.TBL_TODO,toDo);
				}else{
					toDo.setReminderTime(tvTime.getText().toString());
					toDo.setReminderType(btnJustOnce.getTag().equals(1)? "J" :"W");
					toDo.setReminderDateTime(DateTimeUtil.getCurrentDate("yyyy-MM-dd")+" "+setTvTime.getText().toString());
					
					int rowId = dbHelper.saveReminder(DbHelper.TBL_TODO,toDo);
					if(rowId!=-1){
            			try {
							Intent activate = new Intent(context, AlarmReceiver.class);
							activate.setAction(AlarmReceiver.ACTION_TODO);
							activate.putExtra("ROW_ID", rowId);
							
							SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd h:mm a");
							Date d = f.parse(toDo.getReminderDateTime());
							long milliseconds = d.getTime();
							
							if(toDo.getReminderType().equals("J")){
								PendingIntent alarmIntent = PendingIntent.getBroadcast(context, rowId, activate, 0);
								AlarmManager alarms = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
								alarms.set(AlarmManager.RTC_WAKEUP, milliseconds, alarmIntent);
							}else{
								PendingIntent alarmIntent = PendingIntent.getBroadcast(context, rowId, activate, 0);
								AlarmManager alarms = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
								alarms.setRepeating(AlarmManager.RTC_WAKEUP, milliseconds,AlarmManager.INTERVAL_DAY, alarmIntent);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
            		}
				}
				mpopup.dismiss(); 
				setListView();
            }
        });
        
        TextView btnCancel = (TextView)mpopup.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new OnClickListener() 
        {                    
            @Override
            public void onClick(View v) 
            {
                mpopup.dismiss(); //dismissing the popup
            }
        });
        
        
        btnWeekDay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnJustOnce.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
				btnJustOnce.setTag(0);
				
				int tag =Integer.parseInt(btnWeekDay.getTag().toString());
				if (tag==0) {
					btnWeekDay.setBackgroundColor(getResources().getColor(R.color.red));
					btnWeekDay.setTag(1);
				}else {
					btnWeekDay.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
					btnWeekDay.setTag(0);
				}
			}
		});
        
        
        btnJustOnce.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnWeekDay.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
				btnWeekDay.setTag(0);
				
				int tag =Integer.parseInt(btnJustOnce.getTag().toString());
				if (tag==0) {
					btnJustOnce.setBackgroundColor(getResources().getColor(R.color.red));
					btnJustOnce.setTag(1);
				}else {
					btnJustOnce.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
					btnJustOnce.setTag(0);
				}
			}
		});
        mpopup.show();
	}
	
	
	
	@Override
    public void onTimePicked(Calendar time){
        // display the selected time in the TextView
		setTvTime.setText(DateFormat.format("h:mm a", time));
    }
	
	@OnClick(R.id.btn_clear)
	public void clearEvent(){
		if(!isListEnable){
			tvClear.setBackgroundResource(R.drawable.white_listview);
			tvClear.setTextColor(Color.RED);
			tvEdit.setBackgroundResource(R.drawable.red_mapview);
			tvEdit.setTextColor(Color.WHITE);
			isListEnable=true;
			adapter.notifyDataSetChanged();
		}else{//delete selected items
			boolean isCheckItem= false;
			for(Reminder reminder: toDoList)
				if(reminder.isChecked()){
					isCheckItem= true;
					break;
				}
			
			if(isCheckItem)showToDoAlert();
		}
	}
	
	@OnClick(R.id.btn_edit)
	public void editEvent(){
		if(isListEnable){
			tvClear.setBackgroundResource(R.drawable.red_listview);
			tvClear.setTextColor(Color.WHITE);
			tvEdit.setBackgroundResource(R.drawable.white_mapview);
			tvEdit.setTextColor(Color.RED);
			isListEnable=false;
			adapter.notifyDataSetChanged();
		}
	}
	
	
	
	
	private class ToDoAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public ToDoAdapter() {
			inflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return toDoList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = convertView;

			if (view == null) {
				view = inflater.inflate(R.layout.row_to_do, null);
			}

			TextView tv = (TextView) view.findViewById(R.id.tv_to_do);
			tv.setText(toDoList.get(position).getName());

			CheckBox checkBox = (CheckBox) view.findViewById(R.id.chk);
			ImageView ivEdit = (ImageView) view.findViewById(R.id.iv_edit);

			if (!isListEnable) {
				checkBox.setVisibility(View.GONE);
				ivEdit.setVisibility(View.VISIBLE);

				ivEdit.setTag(toDoList.get(position));
				ivEdit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Reminder reminder = (Reminder)v.getTag();
						showPopUpWindow(reminder);
					}
				});
			} else {
				checkBox.setChecked(toDoList.get(position).isChecked());
				checkBox.setVisibility(View.VISIBLE);
				ivEdit.setVisibility(View.GONE);

				checkBox.setTag(position);
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
						int temp = Integer.parseInt(buttonView.getTag().toString());
						toDoList.get(temp).setChecked(isChecked);
					}
				});
			}
			return view;
		}
	}
	
	
	
	private void showToDoAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Message");
		alertDialog.setMessage("Are you sure you want to delete?");

		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						 
						for(Reminder reminder: toDoList){
							if(reminder.isChecked()){
								dbHelper.deleteReminderRow(DbHelper.TBL_TODO, reminder.getId());
								if(!reminder.getReminderTime().equals("")){
									Intent activate = new Intent(context, AlarmReceiver.class);
				        			PendingIntent alarmIntent = PendingIntent.getBroadcast(context,reminder.getId(), activate, 0);
				        			AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				        			alarms.cancel(alarmIntent);
								}
							} 
						}
						setListView();
					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		// Showing Alert Message
		alertDialog.show();
	}
}
