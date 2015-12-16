package com.youflik.youflik.ImageDetailPager;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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
import com.youflik.youflik.models.VideosModel;

public class videoDetailAdapter extends PagerAdapter {
	private List<VideosModel> listData;
	private LayoutInflater layoutInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Context prova;
	private GestureImageView mGalleryVideoThumb;
	private TextView mLikeCountDetail,mCommentCountDetail,mShareCountDetail,mDescVideoDetail;

	public videoDetailAdapter(Context context, List<VideosModel> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		prova = context;
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.gallery_placeholder)
		.showImageForEmptyUri(R.drawable.gallery_placeholder) 
		.showImageOnFail(R.drawable.gallery_placeholder)
		.resetViewBeforeLoading(false)
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
			 
		 View imageLayout = layoutInflater.inflate(R.layout.video_detailed_view, view, false);
	     assert imageLayout != null;
		 
	     mGalleryVideoThumb = (GestureImageView) imageLayout.findViewById(R.id.video_view_pager);
		 mCommentCountDetail = (TextView)imageLayout.findViewById(R.id.comment_count_video_detail);
		 mLikeCountDetail = (TextView)imageLayout.findViewById(R.id.like_count_video_detail);
		 mShareCountDetail = (TextView)imageLayout.findViewById(R.id.share_count_video_detail);
		 mDescVideoDetail = (TextView)imageLayout.findViewById(R.id.desc_video_detail);
		 
		 final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading_video);
		 final VideosModel app = listData.get(position);
		 
			// PLAYING A VIDEO
			mGalleryVideoThumb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					String extension = MimeTypeMap.getFileExtensionFromUrl(app.getVideo_path());
					String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
					Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
					mediaIntent.setDataAndType(Uri.parse(app.getVideo_path()), mimeType);
					mediaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					prova.startActivity(mediaIntent);
				}
			});
	 
		mCommentCountDetail.setText(app.getVideo_comment_count());		
		mShareCountDetail.setText(app.getVideo_share_count());
		mLikeCountDetail.setText(app.getVideo_like_count());
		
		if(!(app.getVideo_desc().equalsIgnoreCase("null"))){
		mDescVideoDetail.setText(app.getVideo_desc());
		} else {
			mDescVideoDetail.setText("");
		}
		
		String SIcon=app.getSelected_video_poster();
		if(app.getSelected_video_poster()==null || app.getSelected_video_poster().equalsIgnoreCase("null")|| app.getSelected_video_poster().equalsIgnoreCase(""))
		{
			mGalleryVideoThumb.setImageResource(R.drawable.gallery_placeholder);
		}
		else
		{
		imageLoader.displayImage(SIcon, mGalleryVideoThumb, options, new SimpleImageLoadingListener() {
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

		}
		
       mCommentCountDetail.setOnClickListener(new OnClickListener(){
       	
			@Override
			public void onClick(View v) {
				int commentCount = Integer.parseInt(app.getVideo_comment_count());
				if(commentCount==0){
					Toast.makeText(prova,R.string.string_noCommentVideo,Toast.LENGTH_LONG).show();
				} else {
				Intent commentListIntent = new Intent(prova,CommentsDetailActivity.class);
				commentListIntent.putExtra("id",app.getVideo_id());
				commentListIntent.putExtra("from","Gallery");
				commentListIntent.putExtra("commentType","videofeed");
				((Activity)prova).startActivity(commentListIntent);
				((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);

			 }
			}
       });
       
       mShareCountDetail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int shareCount = Integer.parseInt(app.getVideo_share_count());
				if(shareCount == 0){
					Toast.makeText(prova,R.string.string_noSharesVideo,Toast.LENGTH_LONG).show();
				}else {
				Intent shareListIntent = new Intent(prova,ShareDetailActivity.class);
				shareListIntent.putExtra("video_id",app.getVideo_id());
				shareListIntent.putExtra("video","true");
				((Activity)prova).startActivity(shareListIntent);
				((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);

			   } }
       	});
       
		mLikeCountDetail.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int likeCount = Integer.parseInt(app.getVideo_like_count());
				if(likeCount ==0){
					Toast.makeText(prova,R.string.string_noLikesVideo,Toast.LENGTH_LONG).show();
				} else {
				Intent likeListIntent = new Intent(prova,LikeDetailActivity.class);
				likeListIntent.putExtra("video_id",app.getVideo_id());
				likeListIntent.putExtra("video","true");
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
