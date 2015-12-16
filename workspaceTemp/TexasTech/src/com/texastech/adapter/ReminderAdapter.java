package com.texastech.adapter;

import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teaxstech.db.DbHelper;
import com.texastech.app.AlarmReceiver;
import com.texastech.app.R;
import com.texastech.bean.Reminder;

public class ReminderAdapter extends BaseAdapter{

	private List list;
	
	private DbHelper dbHelper;
	
	private LayoutInflater inflater;
	
	private Context context;
	
	public ReminderAdapter(Context _context, List _list) {
		list = _list;
		context = _context;
		inflater = LayoutInflater.from(context);
		try {
			dbHelper = new DbHelper(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view==null)
			view = inflater.inflate(R.layout.row_reminder, null);
		
		TextView tvTitle = (TextView)view.findViewById(R.id.tv_title);
		TextView tvTime = (TextView)view.findViewById(R.id.tv_time);
		
		Reminder reminder = (Reminder)getItem(position);
		
		tvTitle.setText(reminder.getName());
		tvTime.setText(reminder.getReminderTime());
		
		ImageView btnDelete= (ImageView)view.findViewById(R.id.btn_delete);
		btnDelete.setTag(reminder);
		btnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Reminder reminder = (Reminder)v.getTag();

				Intent activate = new Intent(context, AlarmReceiver.class);
				PendingIntent alarmIntent = PendingIntent.getBroadcast(context,reminder.getId(), activate, 0);
				AlarmManager alarms = (AlarmManager)v.getContext().getSystemService(Context.ALARM_SERVICE);
				alarms.cancel(alarmIntent);
				
				list.remove(reminder);
				notifyDataSetChanged();
				dbHelper.deleteReminderRow(DbHelper.TBL_REMINDER,reminder.getId());
			}
		});
		return view;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
