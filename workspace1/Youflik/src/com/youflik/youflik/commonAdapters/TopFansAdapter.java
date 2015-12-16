package com.youflik.youflik.commonAdapters;

import java.util.ArrayList;

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
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;
import com.youflik.youflik.models.TopFanModel;
import com.youflik.youflik.models.GenderModel;
import com.youflik.youflik.thirdPartyProfileView.ThirdPartyUserDetailActivity;
import com.youflik.youflik.userprofile.UserDetailActivity;
import com.youflik.youflik.utils.Util;

public class TopFansAdapter extends BaseAdapter{

	private ArrayList<TopFanModel> listData;
	private LayoutInflater layoutInflater;
	private Context prova;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public TopFansAdapter(Context context,ArrayList<TopFanModel> listData){
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		prova = context;

		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		//.displayer(new RoundedBitmapDisplayer(70))//////
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
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.custom_topfans, null);
			holder = new ViewHolder();
			holder.topfans= (ImageView) convertView.findViewById(R.id.topfan_imageview);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final TopFanModel topFan_Item = (TopFanModel) listData.get(position);

		imageLoader.displayImage(topFan_Item.getTopfanphoto(), holder.topfans, options, new SimpleImageLoadingListener() {
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

		holder.topfans.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(topFan_Item.getUser_two().equalsIgnoreCase(Util.USER_ID))
				{
					if(Util.mediaPlayer.isPlaying()){
						Util.mediaPlayer.reset();
					}
					else {
						Util.mediaPlayer.reset();
					}
					Intent user_profile= new Intent(prova,UserDetailActivity.class);
					prova.startActivity(user_profile);
				}else{
					if(Util.mediaPlayer.isPlaying()){
						Util.mediaPlayer.reset();
					}
					else {
						Util.mediaPlayer.reset();
					}
					Intent intent = new Intent(prova,ThirdPartyUserDetailActivity.class);
					intent.putExtra("UserID", topFan_Item.getUser_two());
					Util.THIRD_PARTY_USER_NAME = topFan_Item.getFirstname();
					Util.THIRD_PARTY_USER_ID=topFan_Item.getUser_two();
					prova.startActivity(intent);
				}			
			}
		});

		return convertView;
	}

	static class ViewHolder{

		ImageView topfans;


	}

}

