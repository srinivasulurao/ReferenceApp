package com.voicey.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.voicey.activity.R;
import com.voicey.adapter.FriendMessageListAdapter.MutableWatcher;
import com.voicey.model.FriendMessages;

public class ChatFriendListAdapter extends ArrayAdapter<FriendMessages> {
	Context context;
	List<FriendMessages> friendMessageList;
	ViewHolder holder = null;
	FriendMessages friendMessages;
	Typeface face;
	
	public ChatFriendListAdapter(Context context, int resourceId,
			List<FriendMessages> friendMessageList) {
		super(context, resourceId, friendMessageList);
		this.context = context;
		this.friendMessageList = friendMessageList;
		
		//imageLoader = new ImageLoader(context);

	

	}

	/* private view holder class */
	private class ViewHolder {
		TextView  tvFromvalue;
	

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		friendMessages = friendMessageList.get(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.chat_friend_detail, null);

			holder = new ViewHolder();
			holder.tvFromvalue = (TextView) convertView
					.findViewById(R.id.tvfromValue);
			
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		face = Typeface.createFromAsset(context.getAssets(), "ARIAL.TTF");
		
		
		if (friendMessages.getGroupId() != null
				&& !friendMessages.getGroupId().equals("null")
				&& friendMessages.getGroupId().length() > 0) {
			
			holder.tvFromvalue.setText(friendMessages.getGroupName());

		}else{
			
			holder.tvFromvalue.setText(friendMessages.getFriendName());	
			
		}
		holder.tvFromvalue.setTypeface(face);

		
		
		
		return convertView;
	}

}
