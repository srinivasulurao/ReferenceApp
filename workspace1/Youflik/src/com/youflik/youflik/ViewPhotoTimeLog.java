package com.youflik.youflik;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.polites.android.GestureImageView;
import com.youflik.youflik.ImageDetailPager.CommentsDetailActivity;
import com.youflik.youflik.ImageDetailPager.LikeDetailActivity;
import com.youflik.youflik.ImageDetailPager.ShareDetailActivity;
import com.youflik.youflik.ViewPhotoTimefeed.PostShare;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

public class ViewPhotoTimeLog extends ActionBarActivity implements OnClickListener{
	private String imagePath,mPostId,mPhotoLikeCount,mPhotoCommentCount,mStatusUpdateText,mPhotoId,
	mTrackType,mOwnerName,mDispMsg,mStatusUpdateSharePost,mSharePhotoCount,can_share;
	private ImageLoader imageLoader;
	private TextView mLikeDetail,mCommentDetail,mShareDetail,mDescPhotoFeed;
	private EditText mEditStatusUpdate;
	private ImageButton mSharePhoto,LikePhoto;
	private DisplayImageOptions options;
	private Bitmap showedImgae;
	public Menu menuInstance;
	private GestureImageView imageView;
	private MediaScannerConnection msConn = null;
	private Dialog mDialogSharePhoto;
	private ProgressDialog mPDialog;
	private static final String SHARE_POST_API = Util.API+"share";
	private AlertDialogManager alert = new AlertDialogManager();
	private String user_liked,Id,ownerID,Privacy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_view_timefeed);

		// get the extras from intent
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;

		imagePath = bundle.getString("ImagePath");
		mPostId = bundle.getString("post_id");
		mPhotoId = bundle.getString("photo_id");
		mPhotoLikeCount = bundle.getString("photo_like_count");
		mPhotoCommentCount = bundle.getString("photo_comment_count");
		mStatusUpdateText = bundle.getString("status_update_text");
		mOwnerName = bundle.getString("owner_name");
		mDispMsg = bundle.getString("disp_msg");
		Privacy = bundle.getString("privacy");
		mSharePhotoCount=bundle.getString("photo_share_count");
		can_share=bundle.getString("can_share");
		// FOR LIKE IN DETAIL VIEW
		user_liked= bundle.getString("user_liked");
		Id= bundle.getString("Id");
		ownerID= bundle.getString("ownerID");
		mTrackType=bundle.getString("track_type");

		//initalize view
		LikePhoto=(ImageButton)findViewById(R.id.like_photo_detail);
		mLikeDetail = (TextView) findViewById(R.id.like_count_photo_detail_timefeed);
		mCommentDetail = (TextView)findViewById(R.id.comment_count_photo_detail_timefeed);
		mShareDetail = (TextView)findViewById(R.id.share_count_photo_detail_timefeed);
		mDescPhotoFeed = (TextView) findViewById(R.id.desc_photo_detail_timefeed);
		mSharePhoto = (ImageButton) findViewById(R.id.share_photo_detail);
		imageView = (GestureImageView)findViewById(R.id.image_view_pager_timefeed);
		final ProgressBar spinner = (ProgressBar)findViewById(R.id.loading_timefeed);	
		if(Util.IsFriend.equalsIgnoreCase("1"))
		{
			LikePhoto.setVisibility(View.VISIBLE);
			mSharePhoto.setVisibility(View.VISIBLE);
		}else
		{
			LikePhoto.setVisibility(View.GONE);
			mSharePhoto.setVisibility(View.GONE);

		}

		if(mPhotoLikeCount==null ){
			mLikeDetail.setText("0");
		} else {
			mLikeDetail.setText(mPhotoLikeCount);
		}
		if(mPhotoCommentCount == null){
			mCommentDetail.setText("0");
		} else {
			mCommentDetail.setText(mPhotoCommentCount);
		}
		if(mSharePhotoCount == null){
			mShareDetail.setText("0");
		} else {
			mShareDetail.setText(mSharePhotoCount);
		}

		mDescPhotoFeed.setText(mStatusUpdateText);

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.placeholder_timefeed)
		.showImageOnFail(R.drawable.placeholder_timefeed)
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();

		imageLoader.getInstance().displayImage(imagePath, imageView, options, new SimpleImageLoadingListener() {
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
		mSharePhoto.setOnClickListener(this);
		LikePhoto.setOnClickListener(this);

		// IDENTIFY THE POST LIKE
		if(user_liked!=null)
		{
			if(Integer.parseInt(user_liked)>0)
			{
				LikePhoto.setImageResource(R.drawable.like_blue);
			}
			else
			{
				LikePhoto.setImageResource(R.drawable.like_white);
			}
		}
		else
		{
			LikePhoto.setImageResource(R.drawable.like_white);
		}
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
			Toast.makeText(ViewPhotoTimeLog.this, "Image Saved", Toast.LENGTH_SHORT).show();
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

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch(id){
		case R.id.comment_count_photo_detail_timefeed:
			Intent commentListIntent = new Intent(this,CommentsDetailActivity.class);
			commentListIntent.putExtra("post_id",mPostId);
			if(Util.IsFriend.equalsIgnoreCase("1"))
			{
				commentListIntent.putExtra("from","Timefeed");
			}
			else
			{
				commentListIntent.putExtra("from","TimeLog");

			}
			commentListIntent.putExtra("commentType",mTrackType);
			commentListIntent.putExtra("privacy",Privacy);
			commentListIntent.putExtra("id",mPhotoId);
			commentListIntent.putExtra("org_post_owner_id",ownerID);
			startActivity(commentListIntent);
			overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			break;
		case R.id.like_count_photo_detail_timefeed:
			Intent likeListIntent = new Intent(this,LikeDetailActivity.class);
			likeListIntent.putExtra("photo_id",mPhotoId);
			likeListIntent.putExtra("photo","true");
			startActivity(likeListIntent);
			overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			break;
		case R.id.share_count_photo_detail_timefeed:
			Intent shareListIntent = new Intent(this,ShareDetailActivity.class);
			shareListIntent.putExtra("photo_id",mPhotoId);
			shareListIntent.putExtra("photo","true");
			startActivity(shareListIntent);
			overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			break;
		case R.id.share_photo_detail:
			if(can_share.equalsIgnoreCase("1"))
			{
				showPhotoShareDialog();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "User cannot share this post", Toast.LENGTH_SHORT);
			}
			break;

		case R.id.like_photo_detail:
			new LikePostAsync().execute(mTrackType,mPostId,Id,ownerID,Privacy);

		}	
	}	

	public void showPhotoShareDialog(){

		final EditText mEditStatusUpdate;
		ImageView mImageClose,mImageEdit,mImagePostPic;
		TextView mShareUserName,mShareStatus;

		//Toast.makeText(prova, "IN PROGRESS"+feedItem.getTrack_id(), Toast.LENGTH_SHORT).show();	
		mDialogSharePhoto = new Dialog(ViewPhotoTimeLog.this); 
		mDialogSharePhoto.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialogSharePhoto.setContentView(R.layout.dialog_share);
		mDialogSharePhoto.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mEditStatusUpdate = (EditText)mDialogSharePhoto.findViewById(R.id.dialog_share_write);
		mImageClose = (ImageView)mDialogSharePhoto.findViewById(R.id.dialog_share_close);
		mImageEdit = (ImageView)mDialogSharePhoto.findViewById(R.id.dialog_share_edit);
		mImagePostPic = (ImageView)mDialogSharePhoto.findViewById(R.id.dialog_share_user_profile_pic);
		mShareUserName = (TextView)mDialogSharePhoto.findViewById(R.id.dialog_share_username);
		mShareStatus = (TextView)mDialogSharePhoto.findViewById(R.id.dialog_share_status);

		mShareUserName.setText(mOwnerName);
		if(mDispMsg!=null) mShareStatus.setText(mDispMsg);
		if(imagePath!=null){
			imageLoader.displayImage(imagePath, mImagePostPic,options, new SimpleImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
				}

				@Override
				public void onLoadingFailed(String imageUri, View view,FailReason failReason) {
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
		}


		//close the share dialog
		mImageClose.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				mDialogSharePhoto.dismiss();
			}
		});

		if(mEditStatusUpdate.getText().toString().trim().length()>0){
			mStatusUpdateSharePost = mEditStatusUpdate.getText().toString().trim();
		}

		mDialogSharePhoto.show();
		mImageEdit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				mStatusUpdateSharePost = mEditStatusUpdate.getText().toString().trim();
				ConnectionDetector conn = new ConnectionDetector(ViewPhotoTimeLog.this);
				if(conn.isConnectingToInternet()){
					new PostShare().execute(); 
				}else {
					alert.showAlertDialog(ViewPhotoTimeLog.this, "Connnection Error", "Check your internet connection!",false);
				}
			}
		});
	}


	class PostShare extends AsyncTask<Void,Void,JSONObject>{
		JSONObject receivedJSONResponse ;

		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(ViewPhotoTimeLog.this);
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}


		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject sendSharePostJSONObject = new JSONObject();

			String trackType = null;
			try {
				trackType="photo_share";
				sendSharePostJSONObject.put("share_post_id",mPostId);
				sendSharePostJSONObject.put("status_update_text",mStatusUpdateSharePost);
				sendSharePostJSONObject.put("photo_id",mPhotoId); 		
				sendSharePostJSONObject.put("org_post_owner_id",ownerID);
				sendSharePostJSONObject.put("privacy_type",Privacy);
				sendSharePostJSONObject.put("track_type",trackType);
				receivedJSONResponse = HttpPostClient.sendHttpPost(SHARE_POST_API, sendSharePostJSONObject);
			} catch(JSONException e ){
			}
			return receivedJSONResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result){
			super.onPostExecute(result);
			mPDialog.dismiss();
			if(result!=null){
				try {
					Toast.makeText(ViewPhotoTimeLog.this, result.getString("message"),Toast.LENGTH_LONG).show();
					mDialogSharePhoto.dismiss();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private class LikePostAsync extends AsyncTask<String, Void, JSONObject>{
		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(ViewPhotoTimeLog.this);
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}
		@Override
		protected JSONObject doInBackground(String... params) {
			// TODO Auto-generated method stub
			// JSON object to hold the information, which is sent to the server
			JSONObject jsonObjSend = new JSONObject();
			String likeType = params[0];
			String postID=params[1];
			String Id=params[2];
			String ownerID=params[3];
			String privacy=params[4];
			//pos=params[4];

			try {
				jsonObjSend.put("likeType",likeType);
				jsonObjSend.put("post_id",postID);
				jsonObjSend.put("id",Id);
				jsonObjSend.put("org_post_owner_id",ownerID);
				jsonObjSend.put("privacy_type",privacy);
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}

			JSONObject jsonObjRecv = HttpPostClient.sendHttpPost(Util.API+"like", jsonObjSend);
			return jsonObjRecv;
		}
		@Override
		protected void onPostExecute(JSONObject result){
			super.onPostExecute(result);
			mPDialog.dismiss();
			if(result!=null){
				try {
					//Toast.makeText(prova, result.getString("message"),Toast.LENGTH_LONG).show();
					mPDialog.dismiss();
					if(result.getString("status").equalsIgnoreCase("1"))
					{
						if(result.getString("likeUnlikeStatus").equalsIgnoreCase("Like"))
						{
							LikePhoto.setImageResource(R.drawable.like_blue);
							int temp_count=Integer.parseInt(mLikeDetail.getText().toString().trim())+1;
							mLikeDetail.setText(String.valueOf(temp_count));
						}
						else
						{
							LikePhoto.setImageResource(R.drawable.like_white);
							int temp_count=Integer.parseInt(mLikeDetail.getText().toString().trim())-1;
							if(mLikeDetail.getText().toString().trim().equalsIgnoreCase("0"))
							{
								
							}else
							{
							mLikeDetail.setText(String.valueOf(temp_count));
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
