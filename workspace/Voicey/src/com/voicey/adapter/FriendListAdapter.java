package com.voicey.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.voicey.activity.R;
import com.voicey.model.Friend;

public class FriendListAdapter extends ArrayAdapter<Friend> {

	Context context;
	List<Friend> friendList;
	ViewHolder holder = null;

	public FriendListAdapter(Context context, int resourceId,
			List<Friend> friendList) {
		super(context, resourceId, friendList);
		this.context = context;
		this.friendList = friendList;
	}

	private class ViewHolder {

		TextView tvFriendName;
		TextView tvFriendId;
		RelativeLayout rlBody;
		TextView tvAccept;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		Friend	friend =  getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.friend_list, null);
			holder = new ViewHolder();

			holder.tvFriendName = (TextView) convertView.findViewById(R.id.tvfriendName);
			holder.tvFriendId = (TextView) convertView.findViewById(R.id.tvfriendid);
			holder.rlBody = (RelativeLayout) convertView
					.findViewById(R.id.rlbody);
			holder.tvAccept= (TextView) convertView.findViewById(R.id.tvaccept);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		if(friend.getType().equals("request")){
			
			
		}
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
		
		holder.tvFriendName.setText(friend.getFriendName());
		holder.tvFriendId.setText( friend.getFriendId());
		
		return convertView;
	}

}
