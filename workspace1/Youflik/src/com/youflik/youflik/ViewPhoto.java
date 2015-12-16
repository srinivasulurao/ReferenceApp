package com.youflik.youflik;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.youflik.youflik.ImageDetailPager.CommentsDetailActivity;
import com.youflik.youflik.ImageDetailPager.LikeDetailActivity;
import com.youflik.youflik.ImageDetailPager.ShareDetailActivity;

public class ViewPhoto extends ActionBarActivity implements OnClickListener{
	private String imagePath,mPhotoId;
	private ImageLoader imageLoader;
	private TextView mLikeDetail,mCommentDetail,mShareDetail;
	private DisplayImageOptions options;
	private Bitmap showedImgae;
	public Menu menuInstance;
	private GestureImageView imageView;
	private MediaScannerConnection msConn = null;
	private String photo_like_count,photo_comment_count,photo_share_count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detailed_view);
		mLikeDetail = (TextView) findViewById(R.id.like_count_photo_detail);
		mCommentDetail = (TextView)findViewById(R.id.comment_count_photo_detail);
		mShareDetail = (TextView)findViewById(R.id.share_count_photo_detail);


		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		imagePath = bundle.getString("ImagePath");
		mPhotoId = bundle.getString("photo_id");

		photo_like_count = bundle.getString("photo_like_count");
		photo_comment_count = bundle.getString("photo_comment_count");
		photo_share_count = bundle.getString("photo_share_count");

		if(photo_like_count==null || photo_like_count.equalsIgnoreCase("null"))
		{
			mLikeDetail.setText("0");
		}
		else
		{
			mLikeDetail.setText(photo_like_count);

		}
		if(photo_comment_count==null || photo_comment_count.equalsIgnoreCase("null"))
		{
			mCommentDetail.setText("0");
		}
		else
		{
			mCommentDetail.setText(photo_comment_count);

		}
		if(photo_share_count==null || photo_share_count.equalsIgnoreCase("null"))
		{
			mShareDetail.setText("0");
		}
		else
		{
			mShareDetail.setText(photo_share_count);

		}

		imageView = (GestureImageView)findViewById(R.id.image_view_pager);
		final ProgressBar spinner = (ProgressBar)findViewById(R.id.loading);

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.placeholder_timefeed)
		.showImageOnFail(R.drawable.placeholder_timefeed)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.build();


		imageLoader.displayImage(imagePath, imageView, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				spinner.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
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

				spinner.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

				showedImgae = loadedImage;
				spinner.setVisibility(View.GONE);
			}
		});

		mLikeDetail.setOnClickListener(this);
		mCommentDetail.setOnClickListener(this);
		mShareDetail.setOnClickListener(this);

	}

	public void downloadImage(){

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/DCIM/YouflikSavedImages");    
		myDir.mkdirs();
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		String fname = "Youflik-"+ n +".jpg";
		File file = new File (myDir, fname);
		if (file.exists ()) file.delete (); 
		try {
			FileOutputStream out = new FileOutputStream(file);
			showedImgae.compress(Bitmap.CompressFormat.JPEG, 100, out);
			Toast.makeText(ViewPhoto.this, "Image Saved", Toast.LENGTH_SHORT).show();
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(file);
		mediaScanIntent.setData(contentUri);
		getApplicationContext().sendBroadcast(mediaScanIntent);
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		menuInstance = menu;
		getMenuInflater().inflate(R.menu.save_image, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.saveImage: 
			downloadImage();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// onclick for listing comments,like and share
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(!(mPhotoId.equalsIgnoreCase("null"))){
			switch(id){
			case R.id.comment_count_photo_detail:
				Intent commentListIntent = new Intent(ViewPhoto.this,CommentsDetailActivity.class);
				commentListIntent.putExtra("id",mPhotoId);
				commentListIntent.putExtra("from","Gallery");
				commentListIntent.putExtra("commentType","photofeed");
				startActivity(commentListIntent);
				overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
				break;
			case R.id.like_count_photo_detail:
				Intent likeListIntent = new Intent(ViewPhoto.this,LikeDetailActivity.class);
				likeListIntent.putExtra("photo_id",mPhotoId);
				likeListIntent.putExtra("photo","true");
				startActivity(likeListIntent);
				overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
				break;
			case R.id.share_count_photo_detail:
				Intent shareListIntent = new Intent(ViewPhoto.this,ShareDetailActivity.class);
				shareListIntent.putExtra("photo_id",mPhotoId);
				shareListIntent.putExtra("photo","true");
				startActivity(shareListIntent);
				overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
				break;	
			}
		} 
	}
}
