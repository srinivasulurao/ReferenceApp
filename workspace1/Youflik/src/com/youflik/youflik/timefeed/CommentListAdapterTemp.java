package com.youflik.youflik.timefeed;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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

public class CommentListAdapterTemp extends BaseAdapter {

	private Context mContext;
	private ArrayList<CommentListModel> commentList;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mProfileOptions;
	private LayoutInflater layoutInflater;

	public CommentListAdapterTemp(Context c,ArrayList<CommentListModel> commentListModel){
		this.mContext=c;
		commentList = commentListModel;
		mImageLoader = ImageLoader.getInstance();
		mProfileOptions = new DisplayImageOptions.Builder()
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
		return commentList.size();
	}

	@Override
	public Object getItem(int position) {
		return commentList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (layoutInflater == null)
			layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final CommentListModel model = commentList.get(position);

		if(convertView==null){
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.custom_view_timefeed_list_comment_dialog,parent,false);
			holder.profilePic = (ImageView)convertView.findViewById(R.id.custom_view_timefeed_listcomment_dialog_profilepic);
			holder.name = (TextView) convertView.findViewById(R.id.custom_view_timefeed_listcomment_dialog_firstname);
			holder.status = (TextView)convertView.findViewById(R.id.custom_view_timefeed_listcomment_dialog_status);
			holder.time = (TextView) convertView.findViewById(R.id.custom_view_timefeed_listcomment_dialog_time);
			holder.edit=(TextView)convertView.findViewById(R.id.custom_view_timefeed_listcomment_edit);
			holder.delete=(TextView)convertView.findViewById(R.id.custom_view_timefeed_listcomment_delete);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder)convertView.getTag();
		}
		Typeface typeFace=Typeface.createFromAsset(mContext.getAssets(),"fonts/BentonSans-Book.otf");
		holder.edit.setVisibility(View.GONE);
		holder.delete.setVisibility(View.GONE);
		/*if(model.getLastName().equalsIgnoreCase("null")){*/
		holder.name.setText(model.getFirstName());	 
		/*} else {
			holder.name.setText(model.getFirstName()+" "+model.getLastName());

		}*/

		holder.status.setText(model.getCommentText());
		holder.status.setTypeface(typeFace);
		mImageLoader.displayImage(model.getThumb3(),holder.profilePic,mProfileOptions,new SimpleImageLoadingListener()
		{  @Override
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
		return convertView;
	}
	class ViewHolder{
		ImageView profilePic;
		TextView name,status,time,edit,delete;
	}

}
