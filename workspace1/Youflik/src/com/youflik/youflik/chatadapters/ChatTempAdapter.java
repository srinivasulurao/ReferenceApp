package com.youflik.youflik.chatadapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.youflik.youflik.chat.ChatImagePreviewSave;
import com.youflik.youflik.models.ConversationsMessagesModel;

public class ChatTempAdapter extends BaseAdapter{

	private ArrayList<ConversationsMessagesModel> listData;
	private LayoutInflater layoutInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Context prova;

	public ChatTempAdapter(Context context,ArrayList<ConversationsMessagesModel> listData){
		this.listData = listData;
		imageLoader = ImageLoader.getInstance();
		prova = context;
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (layoutInflater == null)
			layoutInflater = (LayoutInflater) prova.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.custom_chat_ui, null);
			holder = new ViewHolder();

			holder.message_left = (TextView) convertView.findViewById(R.id.chat_compact_message_left);
			holder.message_right = (TextView) convertView.findViewById(R.id.chat_compact_message_right);


			holder.messageImage_left = (ImageView) convertView.findViewById(R.id.chat_compact_image_left);
			holder.messageImage_right = (ImageView) convertView.findViewById(R.id.chat_compact_image_right);

			holder.contentBody_left = (LinearLayout) convertView.findViewById(R.id.contentBody_left);
			holder.contentBody_right = (LinearLayout) convertView.findViewById(R.id.contentBody_right);

			holder.timeLeft = (TextView) convertView.findViewById(R.id.time_left);
			holder.timeRight = (TextView) convertView.findViewById(R.id.time_right);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		final ConversationsMessagesModel connectionsItem = (ConversationsMessagesModel) listData.get(position);

		Typeface typeFace=Typeface.createFromAsset(prova.getAssets(),"fonts/BentonSans-Regular.otf");
		// addind Font 

		holder.message_left.setTypeface(typeFace);
		holder.message_right.setTypeface(typeFace);

		String imageUrl = connectionsItem.getUserImage();

		if(connectionsItem.getConvmessage_direction().equalsIgnoreCase("in")){

			String timestr = connectionsItem.getConvmessage_time();
			String[] datestr = timestr.split(" "); 

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			try {

				Date date = formatter.parse(datestr[0]);
				//System.out.println(date);
				String datecheck = formatter.format(Calendar.getInstance().getTime());
				if(datestr[0].equalsIgnoreCase(datecheck)){
					SimpleDateFormat timeformaterfull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date datetime = timeformaterfull.parse(connectionsItem.getConvmessage_time());
					SimpleDateFormat timeformater = new SimpleDateFormat("h:mm a");
					holder.timeLeft.setText(timeformater.format(datetime));
				}else{
					SimpleDateFormat formater = new SimpleDateFormat("MMM dd,yyyy");
					holder.timeLeft.setText(formater.format(date));
				}



			} catch (ParseException e) {
				e.printStackTrace();
			}

			holder.contentBody_right.setVisibility(View.GONE);
			holder.contentBody_left.setVisibility(View.VISIBLE);
			holder.message_left.setText(connectionsItem.getConvmessage_message());

			if(connectionsItem.getConvmessage_message().contains("$$IMAGE$##$")
					|| connectionsItem.getConvmessage_message().contains("$$STICKER$##$")){
				String checkImage = null;
				if(connectionsItem.getConvmessage_message().contains("$$IMAGE$##$")){
					checkImage = connectionsItem.getConvmessage_message().substring(11);
				}else{
					checkImage = connectionsItem.getConvmessage_message().substring(13);
				}


				holder.message_left.setVisibility(View.GONE);
				holder.messageImage_left.setVisibility(View.VISIBLE);

				imageLoader.displayImage(checkImage, holder.messageImage_left, options, new SimpleImageLoadingListener() {
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
				}
						);

			}else{
				holder.message_left.setVisibility(View.VISIBLE);
				holder.messageImage_left.setVisibility(View.GONE);

			}

		}else{
			holder.contentBody_right.setVisibility(View.VISIBLE);
			holder.contentBody_left.setVisibility(View.GONE);
			holder.message_right.setText(connectionsItem.getConvmessage_message());

			String timestr = connectionsItem.getConvmessage_time();
			String[] datestr = timestr.split(" "); 

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date date = formatter.parse(datestr[0]);
				//System.out.println(date);
				String datecheck = formatter.format(Calendar.getInstance().getTime());
				if(datestr[0].equalsIgnoreCase(datecheck)){
					SimpleDateFormat timeformaterfull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date datetime = timeformaterfull.parse(connectionsItem.getConvmessage_time());
					SimpleDateFormat timeformater = new SimpleDateFormat("h:mm a");
					holder.timeRight.setText(timeformater.format(datetime));
				}else{
					SimpleDateFormat formater = new SimpleDateFormat("MMM dd,yyyy");
					holder.timeRight.setText(formater.format(date));
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}

			if(connectionsItem.getConvmessage_message().contains("$$IMAGE$##$")
					|| connectionsItem.getConvmessage_message().contains("$$STICKER$##$") ){
				String checkImage = null;
				if(connectionsItem.getConvmessage_message().contains("$$IMAGE$##$")){
					checkImage = connectionsItem.getConvmessage_message().substring(11);
				}else{
					checkImage = connectionsItem.getConvmessage_message().substring(13);
				}

				holder.message_right.setVisibility(View.GONE);
				holder.messageImage_right.setVisibility(View.VISIBLE);

				imageLoader.displayImage(checkImage, holder.messageImage_right, options, new SimpleImageLoadingListener() {
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
				}
						);

			}else{
				holder.message_right.setVisibility(View.VISIBLE);
				holder.messageImage_right.setVisibility(View.GONE);
			}

		}

		holder.messageImage_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(connectionsItem.getConvmessage_message().contains("$$IMAGE$##$")){
					String checkImage = connectionsItem.getConvmessage_message().substring(11);
					Intent intent = new Intent(prova, ChatImagePreviewSave.class);
					intent.putExtra("ImagePath", checkImage );
					prova.startActivity(intent); 
				}
			}
		});

		holder.messageImage_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(connectionsItem.getConvmessage_message().contains("$$IMAGE$##$")){
					String checkImage = connectionsItem.getConvmessage_message().substring(11);
					Intent intent = new Intent(prova, ChatImagePreviewSave.class);
					intent.putExtra("ImagePath", checkImage );
					prova.startActivity(intent); 
				}
			}
		});



		return convertView;
	}

	static class ViewHolder{
		public TextView message_left,message_right;
		public ImageView messageImage_left,messageImage_right;
		public LinearLayout contentBody_left;
		public LinearLayout contentBody_right;
		public TextView timeLeft;
		public TextView timeRight;
	}


}
