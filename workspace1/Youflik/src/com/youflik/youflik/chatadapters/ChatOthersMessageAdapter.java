package com.youflik.youflik.chatadapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;
import com.youflik.youflik.chat.ChatOthersModel;

public class ChatOthersMessageAdapter extends BaseAdapter{
	
	private Context mContext;
	private ArrayList<ChatOthersModel> mChatList;
	private ImageLoader imageLoader;
	private DisplayImageOptions profile_options,feed_option;
	
	
	public ChatOthersMessageAdapter(Context context,ArrayList<ChatOthersModel> list){
		this.mContext = context;
		this.mChatList =  list;
		
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
		
		return mChatList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return mChatList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ChatOthersModel chatModel = (ChatOthersModel) mChatList.get(position);
		LayoutInflater inflater = LayoutInflater.from(mContext);;
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.custom_view_fragment_chat_others,parent,false);
			holder.profilePic = (ImageView)convertView.findViewById(R.id.custom_view_fragment_chat_others_profile_pic);
			holder.firstName = (TextView)convertView.findViewById(R.id.custom_view_fragment_chat_others_firstname);
			holder.message = (TextView)convertView.findViewById(R.id.custom_view_fragment_chat_others_message);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder)convertView.getTag();
		}
			
		holder.firstName.setText(chatModel.getFirstname());
		holder.message.setText(chatModel.getBody());
		String profilePicturePath = chatModel.getProfile_photo();
		imageLoader.displayImage(profilePicturePath, holder.profilePic, profile_options, new SimpleImageLoadingListener() {
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
        
		System.out.println("Chat Model data");
		System.out.println("***************");
		System.out.println("Message Users:"+chatModel.getMessageusers());
		System.out.println("ConvId:"+chatModel.getConvid());
		System.out.println("Time:"+chatModel.getTime());
		System.out.println("Body:"+chatModel.getBody());
		System.out.println("Profile Photo:"+chatModel.getProfile_photo());
		System.out.println("First Name:"+chatModel.getFirstname());
		System.out.println("Last Name:"+chatModel.getLastname());
		System.out.println("LocationName:"+chatModel.getLocation_name());
		System.out.println("JID:"+chatModel.getJid());
		System.out.println("Created Date:"+chatModel.getCreated_date());
		
		return convertView;
	}
   
	class ViewHolder{
		private ImageView profilePic; 
		private TextView  firstName,message;
	}
}
 