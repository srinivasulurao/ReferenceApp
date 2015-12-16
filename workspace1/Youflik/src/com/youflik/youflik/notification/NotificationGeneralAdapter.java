package com.youflik.youflik.notification;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
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
import com.youflik.youflik.models.NotificationGeneralModel;

public class NotificationGeneralAdapter extends BaseAdapter{

	private ArrayList<NotificationGeneralModel> listData;
	private LayoutInflater layoutInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Context prova;

	public NotificationGeneralAdapter(Context context,ArrayList<NotificationGeneralModel> listData){
		this.listData = listData;
		//layoutInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		prova = context;

		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
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
		final ViewHolder holder;
		final NotificationGeneralModel notificationItem = (NotificationGeneralModel) listData.get(position);
		if (layoutInflater == null)
			layoutInflater = (LayoutInflater) prova.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.custom_general_notification, null);
			holder = new ViewHolder();
			holder.textViewName = (TextView) convertView.findViewById(R.id.NotificationUserName);
			holder.imageViewUserPhoto = (ImageView) convertView.findViewById(R.id.notificationsImage);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		String str = ("<b>" + notificationItem.getNotification_send_user_firstname()) + " </b>" + " " + "<font color=\"#808080\">" + notificationItem.getNotification_message() + "</font>";
		holder.textViewName.setText(Html.fromHtml(str));


		imageLoader.displayImage(notificationItem.getNotification_send_user_profile_photo_thumb1(), holder.imageViewUserPhoto, options, new SimpleImageLoadingListener() {
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


		return convertView;
	}

	static class ViewHolder{
		ImageView imageViewUserPhoto;
		TextView textViewName;
	}


}

