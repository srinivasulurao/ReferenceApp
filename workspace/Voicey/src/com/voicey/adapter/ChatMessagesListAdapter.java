package com.voicey.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.voicey.activity.R;
import com.voicey.model.ChatMessages;

public class ChatMessagesListAdapter extends ArrayAdapter<ChatMessages> {
	Context context;
	ViewHolder holder = null;
	List<ChatMessages> chatMessageList;
	ChatMessages chatMessages;
	
	public ChatMessagesListAdapter(Context context, int resourceId,
			List<ChatMessages> chatMessageList) {
		super(context, resourceId, chatMessageList);
		this.context = context;
		this.chatMessageList = chatMessageList;
		
	}
	private class ViewHolder {
		TextView  tvFromvalue;
		public TextView message_left,message_right;
		public ImageView messageImage_left,messageImage_right;
		public LinearLayout contentBody_left;
		public LinearLayout contentBody_right,llbuttonleft;
	

	}

	
	public View getView(final int position, View convertView, ViewGroup parent) {
		chatMessages = chatMessageList.get(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.chat_message, null);
            holder = new ViewHolder();
			holder.message_left = (TextView) convertView
					.findViewById(R.id.chat_compact_message_left);
			holder.message_right = (TextView) convertView
					.findViewById(R.id.chat_compact_message_right);
			holder.contentBody_left = (LinearLayout) convertView
					.findViewById(R.id.contentBody_left);
			holder.llbuttonleft = (LinearLayout) convertView
					.findViewById(R.id.llbuttonleft);
			holder.messageImage_right= (ImageView) convertView
					.findViewById(R.id.chat_compact_image_right);
			
			holder.contentBody_right = (LinearLayout) convertView
					.findViewById(R.id.contentBody_right);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		holder.contentBody_right.setId(position);
		
		   
		
		if(chatMessages.getType().equals("IN")){
			
			holder.contentBody_right.setVisibility(View.GONE);
			holder.contentBody_left.setVisibility(View.VISIBLE);
			holder.message_left.setText(chatMessages.getChatMessage());
			
		}else{
			holder.message_right.setText(chatMessages.getChatMessage());
			holder.contentBody_right.setVisibility(View.VISIBLE);
			holder.contentBody_left.setVisibility(View.GONE);
			
			if(chatMessages.getImgBiteMap()!=null){
				holder.messageImage_right.setVisibility(View.VISIBLE);
				holder.messageImage_right.setImageBitmap(chatMessages.getImgBiteMap());
			}else{
				holder.messageImage_right.setVisibility(View.GONE);
				
			}
			
		}
		
		if(chatMessages.getIsDispalyDetail().equals("0")){
			
			 holder.llbuttonleft.setVisibility(View.GONE);
		}else{
			holder.llbuttonleft.setVisibility(View.VISIBLE);
		}
		
		holder.contentBody_right.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				
				chatMessages = chatMessageList.get(view.getId());
				
				if(chatMessages.getIsDispalyDetail().equals("0")){
					
					chatMessages.setIsDispalyDetail("1");
					
				}else{
					
					chatMessages.setIsDispalyDetail("0");
				}
				
				
				notifyDataSetChanged();
				
				return true;
			}        	
        });
		   
		
		
		
		
		
		
		
		return convertView;
	}
	
	
	
	

}
