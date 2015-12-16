package com.texastech.app;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.teaxstech.db.DbHelper;
import com.texastech.bean.Reminder;
import com.texastech.helper.MLog;

public class AlarmReceiver extends BroadcastReceiver {
	
	public static final String ACTION_TODO = "com.texastech.app.ACTION_TODO";
	
	public static final String ACTION_REMINDER = "com.texastech.app.ACTION_REMINDER";
	 
	private int notificationCount = 100;
	 
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			int rowId = intent.getIntExtra("ROW_ID", -1);
			MLog.v(">>ROW_ID", ""+rowId);
			if(rowId==-1)return;
			DbHelper dbHelper = new DbHelper(context);
			
			if(ACTION_TODO.equals(action)){
				Reminder reminder = dbHelper.getReminderDetail(DbHelper.TBL_TODO,rowId);
				if(reminder!=null){
					if(reminder.getReminderType().equalsIgnoreCase("W")){
						Calendar c = Calendar.getInstance();
					    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
					    if(Calendar.SATURDAY != dayOfWeek && Calendar.SUNDAY != dayOfWeek){ 
							//remaining all days
							alramRecived(context, "To Do",reminder.getName());
						}
					}else{
						//once //delete from db
						dbHelper.deleteReminderRow(DbHelper.TBL_TODO,rowId);
						alramRecived(context, "To Do",reminder.getName());
					}
				}
			}else if(ACTION_REMINDER.equals(action)){
				Reminder reminder = dbHelper.getReminderDetail(DbHelper.TBL_REMINDER,rowId);
				if(reminder!=null){
					if(reminder.getReminderType().equalsIgnoreCase("W")){
						Calendar c = Calendar.getInstance();
					    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
					    if(Calendar.SATURDAY != dayOfWeek && Calendar.SUNDAY != dayOfWeek){ 
							//remaining all days
							alramRecived(context, "Reminder",reminder.getName());
						}
					}else{
						//once
						//delete from db
						dbHelper.deleteReminderRow(DbHelper.TBL_REMINDER,rowId);
						alramRecived(context, "Reminder",reminder.getName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void alramRecived(Context context, String title,String message){
		generateNotification(context, title,  message);
		/*if (!ValidationManager.isApplicationBroughtToBackground(context.getApplicationContext())) {
			Intent i = new Intent(context, DisplayAlert.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("Title", "Alert !");
			i.putExtra("Message", message);
			context.startActivity(i);
		}else{
			generateNotification(context, "Alert !",  message);
		}*/
	}
	
	
	private  void generateNotification(Context context, String title,String message) {
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.app_icon_sml, context.getString(R.string.app_name), when);
		//notification.sound = Uri.parse("android.resource://com.via.via.phase2/" + R.raw.alert);
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		Intent notificationIntent = new Intent(context, SplashActivity.class);
		//notificationIntent.putExtra("Title", title);
		//notificationIntent.putExtra("Message", message);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(notificationCount, notification);
		notificationCount++;
	}
}
