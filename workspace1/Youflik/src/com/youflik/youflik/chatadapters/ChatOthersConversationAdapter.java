package com.youflik.youflik.chatadapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;
import com.youflik.youflik.chat.ChatOthersConversationModel;
import com.youflik.youflik.utils.Util;

public class ChatOthersConversationAdapter extends BaseAdapter{
      private Context mContext;
      private ArrayList<ChatOthersConversationModel> mOthersConversationList;
      private ImageLoader imageLoader;
      private DisplayImageOptions profile_options;
      private boolean isImage = false;
	
	public ChatOthersConversationAdapter(Context c,ArrayList<ChatOthersConversationModel> list){
		
		this.mContext = c;
		this.mOthersConversationList =  list;
		imageLoader = ImageLoader.getInstance();
		profile_options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		
	}
	@Override
	public int getCount() {
		return mOthersConversationList.size();
	}

	@Override
	public Object getItem(int position) {	
		return mOthersConversationList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		ViewHolder holder;
		ChatOthersConversationModel conversationData = (ChatOthersConversationModel) mOthersConversationList.get(position);
		String imagePath = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.custom_chat_ui,parent,false);
			holder = new ViewHolder();
			holder.contentLeft = (LinearLayout)convertView.findViewById(R.id.contentBody_left);
			holder.contentRight=(LinearLayout)convertView.findViewById(R.id.contentBody_right);
			
			holder.messageLeft = (TextView)convertView.findViewById(R.id.chat_compact_message_left);
		    holder.messageRight = (TextView)convertView.findViewById(R.id.chat_compact_message_right);
		    
		    holder.imageLeft = (ImageView)convertView.findViewById(R.id.chat_compact_image_left);
		    holder.imageRight = (ImageView)convertView.findViewById(R.id.chat_compact_image_right);
		    
		    holder.timeLeft = (TextView) convertView.findViewById(R.id.time_left);
		    holder.timeRight = (TextView)convertView.findViewById(R.id.time_right);
		    convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Typeface typeFace=Typeface.createFromAsset(mContext.getAssets(),"fonts/BentonSans-Regular.otf");
		// addind Font 

		holder.messageLeft.setTypeface(typeFace);
		holder.messageRight.setTypeface(typeFace);
		
		// setting the data
		String timeFormat = "yyyy-mm-dd hh:mm:ssa";
		String sendTime = null;
		SimpleDateFormat conversationTimeFormat = new SimpleDateFormat(timeFormat); 
		
		try {
			Date date = conversationTimeFormat.parse(conversationData.getCreated_date());
			sendTime =  new SimpleDateFormat("hh:mm a").format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(conversationData.getFromuser().equalsIgnoreCase(Util.USERNAME)){
			
			holder.contentRight.setVisibility(View.VISIBLE);
			holder.contentLeft.setVisibility(View.GONE);
			
			if(conversationData.getBody().contains("$$STICKER$##$")){
				holder.imageRight.setVisibility(View.VISIBLE);
				holder.messageRight.setVisibility(View.GONE);
				 imagePath = conversationData.getBody().substring(13);
				 imageLoader.displayImage(imagePath,holder.imageRight,profile_options, new SimpleImageLoadingListener(){
					 @Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri, View view, int current,
								int total) {

						}

					 
				 });
				
			} else {
				holder.imageRight.setVisibility(View.GONE);
				holder.messageRight.setVisibility(View.VISIBLE);
				
				holder.messageRight.setText(conversationData.getBody());
			}
			
			holder.timeRight.setText(sendTime);
		} else {
			holder.contentLeft.setVisibility(View.VISIBLE);
			holder.contentRight.setVisibility(View.GONE);
			if(conversationData.getBody().contains("$$STICKER$##$")){
				
				imagePath = conversationData.getBody().substring(13);
				holder.imageLeft.setVisibility(View.VISIBLE);
				holder.messageLeft.setVisibility(View.GONE);
				
				imageLoader.displayImage(imagePath, holder.imageLeft,profile_options,new SimpleImageLoadingListener(){
					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view, int current,
							int total) {

					}

	
				});
				
			}else{
				holder.messageLeft.setVisibility(View.VISIBLE);
				holder.imageLeft.setVisibility(View.GONE);
				holder.messageLeft.setText(conversationData.getBody());
			}
			
			
			holder.timeLeft.setText(sendTime);
		}
		
		return convertView;
	}

	
	class ViewHolder{
		
		LinearLayout contentRight,contentLeft;
		TextView messageLeft,messageRight;
		ImageView imageLeft,imageRight;
		TextView timeLeft,timeRight;
	}
}
