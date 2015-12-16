package com.youflik.youflik.ImageDetailPager;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.polites.android.GestureImageView;
import com.youflik.youflik.R;
import com.youflik.youflik.models.PhotosModel;
import com.youflik.youflik.utils.Util;

public class ImageDetailAdapter extends PagerAdapter {
	private List<PhotosModel> listData;
	private LayoutInflater layoutInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Context prova;
	private GestureImageView mGalleryImage;
	private TextView mLikeCountDetail,mCommentCountDetail,mShareCountDetail;

	public ImageDetailAdapter(Context context, List<PhotosModel> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		prova = context;
		options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override 
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
	 
		 View  imageLayout = layoutInflater.inflate(R.layout.image_detailed_view, view, false);
		 assert imageLayout != null;
		 
		 mGalleryImage = (GestureImageView) imageLayout.findViewById(R.id.image_view_pager);
		 mCommentCountDetail = (TextView)imageLayout.findViewById(R.id.comment_count_photo_detail);
		 mLikeCountDetail = (TextView)imageLayout.findViewById(R.id.like_count_photo_detail);
		 mShareCountDetail = (TextView)imageLayout.findViewById(R.id.share_count_photo_detail);
		// mDescPhotoDetail = (TextView)imageLayout.findViewById(R.id.desc_photo_detail);
		 
		final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
		
		final PhotosModel app = listData.get(position);
		String SIcon=app.getActual_photo_path();
		
		mCommentCountDetail.setText(app.getPhoto_comment_count());		
		mShareCountDetail.setText(app.getPhoto_share_count());
		mLikeCountDetail.setText(app.getPhoto_like_count());
		
/*		if(!(app.getPhoto_desc().equalsIgnoreCase("null"))){
		mDescPhotoDetail.setText(app.getPhoto_desc());
		} else {
			mDescPhotoDetail.setText("");
		}*/
		
		
		imageLoader.getInstance().displayImage(SIcon, mGalleryImage, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				//spinner.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				/*String message = null;
				switch (failReason.getType()) {
				case IO_ERROR:
					message = "Input/Output error";
					break;
				case DECODING_ERROR:
					message = "Image can't be decoded";
					break;
				case NETWORK_DENIED:
					message = "Downloads are denied";
					break;
				case OUT_OF_MEMORY:
					message = "Out Of Memory error";
					break;
				case UNKNOWN:
					message = "Unknown error";
					break;
				}

				spinner.setVisibility(View.GONE);*/
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

				/*spinner.setVisibility(View.GONE);*/
			}
		});
        mCommentCountDetail.setOnClickListener(new OnClickListener(){
        	
			@Override
			public void onClick(View v) {
				int commentCount = Integer.parseInt(app.getPhoto_comment_count());
				if(commentCount==0){
					Toast.makeText(prova,R.string.string_noCommentPhoto,Toast.LENGTH_LONG).show();
				} else {
				Intent commentListIntent = new Intent(prova,CommentsDetailActivity.class);
				commentListIntent.putExtra("id",app.getPhoto_id());
				commentListIntent.putExtra("from","Gallery");
				commentListIntent.putExtra("commentType","photofeed");
				((Activity)prova).startActivity(commentListIntent);
				((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			 }
			}
        });
        mShareCountDetail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int shareCount = Integer.parseInt(app.getPhoto_share_count());
				if(shareCount == 0){
					Toast.makeText(prova,R.string.string_noSharesPhoto,Toast.LENGTH_LONG).show();
				}else {
				Intent shareListIntent = new Intent(prova,ShareDetailActivity.class);
				shareListIntent.putExtra("photo_id",app.getPhoto_id());
				shareListIntent.putExtra("photo","true");
				((Activity)prova).startActivity(shareListIntent);
				((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			   } }
        	});
		mLikeCountDetail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int likeCount = Integer.parseInt(app.getPhoto_like_count());
				if(likeCount ==0){
					Toast.makeText(prova,R.string.string_noLikesPhoto,Toast.LENGTH_LONG).show();
				} else {
				Intent likeListIntent = new Intent(prova,LikeDetailActivity.class);
				likeListIntent.putExtra("photo_id",app.getPhoto_id());
				likeListIntent.putExtra("photo","true");
				((Activity)prova).startActivity(likeListIntent);
				((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			  	}}
		      });
			
		view.addView(imageLayout, 0);
		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() { 
		return null;
	}
}
