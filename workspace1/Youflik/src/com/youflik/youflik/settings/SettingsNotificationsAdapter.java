package com.youflik.youflik.settings;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.youflik.youflik.R;
import com.youflik.youflik.models.SettingsNotificationsItemModel;

public class SettingsNotificationsAdapter extends BaseAdapter {

	private ArrayList<SettingsNotificationsItemModel> listItemData;
	private Context context;

	SettingsNotificationsAdapter(Context c,ArrayList<SettingsNotificationsItemModel> result) {
		this.context = c;
		this.listItemData = result;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItemData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItemData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SettingsNotificationsItemModel listItem = (SettingsNotificationsItemModel) listItemData.get(position);
		LayoutInflater inflater = LayoutInflater.from(context);
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.custom_view_settings_notifications, parent, false);
			//holder.settings_notifications_type = (TextView) convertView.findViewById(R.id.custom_view_settings_notifications_text);
			//holder.settings_notifcations_on = (ToggleButton) convertView.findViewById(R.id.custom_view_settings_notifications_toggle);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		return convertView;
    }

	class Holder {
		TextView settings_notifications_type;
		ToggleButton settings_notifcations_on;
	}
}