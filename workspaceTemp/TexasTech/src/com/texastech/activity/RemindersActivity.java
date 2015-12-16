package com.texastech.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;

import com.teaxstech.db.DbHelper;
import com.texastech.adapter.ReminderAdapter;
import com.texastech.app.AlarmReceiver;
import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.bean.Reminder;
import com.texastech.helper.DateTimeUtil;
import com.texastech.helper.TimePickerFragment;
import com.texastech.helper.TimePickerFragment.TimePickedListener;

public class RemindersActivity  extends BaseActivity implements TimePickedListener{

	//private PopupWindow mpopup;
	private Dialog mpopup;
	
	@InjectView(R.id.list_view)
	ListView listView;
	
	@InjectView(R.id.btn_save)
	TextView btnAdd;
	
	TextView setTvTime;
	
	private DbHelper dbHelper;
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText("Reminders");
	}

	@Override
	protected void initXmlView() {
		btnAdd.setVisibility(View.VISIBLE);
		btnAdd.setText("Add");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_list);
		try {
			dbHelper = new DbHelper(getApplicationContext());
			List<Reminder> reminders = dbHelper.getReminderList(DbHelper.TBL_REMINDER);
			listView.setAdapter(new ReminderAdapter(getApplicationContext(), reminders));
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	@OnClick(R.id.btn_save)
	public void onAddNewReminder(){
		mpopup = new Dialog(context);
		mpopup.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		mpopup.setContentView(R.layout.dialog_reminder);
		
		/*View popUpView = getLayoutInflater().inflate(R.layout.dialog_reminder, null); // inflating popup layout
        mpopup = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true); //Creation of popup
        mpopup.setAnimationStyle(android.R.style.Animation_Dialog);   
        mpopup.showAtLocation(popUpView, Gravity.BOTTOM, 0, 0);    // Displaying popup
        mpopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);*/
        
        final RadioButton radioOnce = (RadioButton)mpopup.findViewById(R.id.radio_once);
        final EditText editText = (EditText)mpopup.findViewById(R.id.et_reminder);
        
        final TextView tvTime = (TextView)mpopup.findViewById(R.id.tv_time);
    	tvTime.setOnClickListener(new OnClickListener() {                    
            @Override
            public void onClick(View v){
            	setTvTime = (TextView)v;
                TimePickerFragment newFragment = new TimePickerFragment(RemindersActivity.this);
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        
        TextView btnOk = (TextView)mpopup.findViewById(R.id.btn_save);
        btnOk.setOnClickListener(new OnClickListener() 
        {                    
            @Override
            public void onClick(View v) 
            {
            	if(TextUtils.isEmpty(tvTime.getText().toString())){
            		Toast.makeText(getApplicationContext(), "Please select time.", Toast.LENGTH_LONG).show();
            	}else if(TextUtils.isEmpty(editText.getText().toString())){
            		//Toast.makeText(getApplicationContext(), "Field should not be blank.", Toast.LENGTH_LONG).show();
            		editText.setError("Field should not be blank.");
            	}else{
            		//saveReminder
            		Reminder reminder = new Reminder();
            		reminder.setName(editText.getText().toString());
            		reminder.setReminderTime(setTvTime.getText().toString());
            		reminder.setReminderType(radioOnce.isChecked()? "J":"W");
            		reminder.setReminderDateTime(DateTimeUtil.getCurrentDate("yyyy-MM-dd")+" "+setTvTime.getText().toString());
            		
            		int rowId= dbHelper.saveReminder(DbHelper.TBL_REMINDER,reminder);
            		if(rowId!=-1){
            			try {
							Intent activate = new Intent(context, AlarmReceiver.class);
							activate.setAction(AlarmReceiver.ACTION_REMINDER);
							activate.putExtra("ROW_ID", rowId);
							
							SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd h:mm a");
							Date d = f.parse(reminder.getReminderDateTime());
							long milliseconds = d.getTime();
							
							if(radioOnce.isChecked()){
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
            		
            		List<Reminder> reminders = dbHelper.getReminderList(DbHelper.TBL_REMINDER);
        			listView.setAdapter(new ReminderAdapter(getApplicationContext(), reminders));
            		mpopup.dismiss(); 
            	}
                //dismissing the popup
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
        mpopup.show();
	}
	
	
	
	@Override
    public void onTimePicked(Calendar time){
        // display the selected time in the TextView
		setTvTime.setText(DateFormat.format("h:mm a", time));
    }
}
