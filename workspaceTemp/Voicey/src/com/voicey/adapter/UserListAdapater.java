package com.voicey.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.voicey.activity.R;
import com.voicey.model.Friend;
import com.voicey.model.User;

public class UserListAdapater extends ArrayAdapter<User> {

	Context context;
	List<User> userList;
	ViewHolder holder = null;
	
	public UserListAdapater(Context context, int resourceId,
			List<User> userList) {
		super(context, resourceId, userList);
		this.context = context;
		this.userList = userList;
	}
	
	private class ViewHolder {

		TextView tvUserName;
		TextView tvUserId;
		RelativeLayout rlBody;
		ImageView remove;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		User user =  getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.user_list, null);
			holder = new ViewHolder();

			holder.tvUserName = (TextView) convertView.findViewById(R.id.tvuserName);
			holder.tvUserId = (TextView) convertView.findViewById(R.id.tvuserid);
			holder.remove = (ImageView) convertView.findViewById(R.id.remove_user);
			holder.rlBody = (RelativeLayout) convertView
					.findViewById(R.id.rlbody);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
			holder.remove.setVisibility(View.GONE);
		if (position % 4 == 0)
			// holder.rlBody.setBackgroundColor(Color.parseColor("#22b14c"));

			holder.rlBody
					.setBackgroundResource(R.drawable.list_background1);
		else if (position % 4 == 1)
			holder.rlBody
					.setBackgroundResource(R.drawable.list_background2);
		else if (position % 4 == 2)
			holder.rlBody
					.setBackgroundResource(R.drawable.list_background3);
		else if (position % 4 == 3)
			holder.rlBody
					.setBackgroundResource(R.drawable.list_background4);
		
		holder.tvUserName.setText(user.getUserName());
		holder.tvUserId.setText( user.getUserCode());
		
		return convertView;
	}
			

}
