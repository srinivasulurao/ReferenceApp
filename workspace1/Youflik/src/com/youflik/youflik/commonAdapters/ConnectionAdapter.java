package com.youflik.youflik.commonAdapters;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.sax.StartElementListener;
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
import com.youflik.youflik.models.FriendsModel;
import com.youflik.youflik.thirdPartyProfileView.ThirdPartyUserDetailActivity;
import com.youflik.youflik.utils.Util;

public class ConnectionAdapter extends BaseAdapter{

	private ArrayList<FriendsModel> listData;
	private LayoutInflater layoutInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Context prova;

	public ConnectionAdapter(Context context,ArrayList<FriendsModel> listData){
		this.listData = listData;
		//layoutInflater = LayoutInflater.from(context);
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
			convertView = layoutInflater.inflate(R.layout.custom_user_friends, null);
			holder = new ViewHolder();
			holder.textViewName = (TextView) convertView.findViewById(R.id.friendsname);
			holder.imageViewPhoto = (ImageView) convertView.findViewById(R.id.friendsImage);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		FriendsModel connectionsItem = (FriendsModel) listData.get(position);

		Typeface typeFace=Typeface.createFromAsset(prova.getAssets(),"fonts/BentonSansComp-Regular.otf");

		if(connectionsItem.getUser_two().equalsIgnoreCase(Util.USER_ID))
		{
			//Toast.makeText(prova, "you present", Toast.LENGTH_SHORT).show();
			holder.textViewName.setText("You");
			holder.textViewName.setTypeface(typeFace);
		}
		else
		{
			//Toast.makeText(prova, "Nooo", Toast.LENGTH_SHORT).show();
			if(connectionsItem.getLastname().equalsIgnoreCase("null") || connectionsItem.getLastname()==null)
			{
				holder.textViewName.setText(connectionsItem.getFirstname());
			}
			else
			{
				holder.textViewName.setText(connectionsItem.getFirstname()+" "+ connectionsItem.getLastname());
			}
			holder.textViewName.setTypeface(typeFace);
		}
		String imageUrl = connectionsItem.getThumb3();

		imageLoader.displayImage(imageUrl, holder.imageViewPhoto, options, new SimpleImageLoadingListener() {
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

/*		holder.imageViewPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


			};
		});*/
		 
		return convertView;
	}

	static class ViewHolder{
		ImageView imageViewPhoto;
		TextView textViewName;

	}

}
