package com.youflik.youflik.commonAdapters;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.MainActivity;
import com.youflik.youflik.R;
import com.youflik.youflik.ViewPhotoTimefeed;
import com.youflik.youflik.ImageDetailPager.CommentsDetailActivity;
import com.youflik.youflik.ImageDetailPager.LikeDetailActivity;
import com.youflik.youflik.ImageDetailPager.ShareDetailActivity;
import com.youflik.youflik.ImageDetailPager.videoDetail;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.Timefeeds;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.thirdPartyProfileView.ThirdPartyUserDetailActivity;
import com.youflik.youflik.timefeed.CommentListAdapter;
import com.youflik.youflik.timefeed.CommentListModel;
import com.youflik.youflik.userprofile.UserDetailActivity;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;



public class FeedAdapter extends BaseAdapter{

	private ArrayList<Timefeeds> listData;
	private LayoutInflater layoutInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions profile_options,feed_option;
	private Context prova;
	private AlertDialogManager alert = new AlertDialogManager();
	private Integer likeID;
	//Progress dialog 
	private ProgressDialog mPDialog;
	String str_fr,str_ra;
	//common variables
	private String mTrackId,mOriginalPostId;
	//variables for listing the comments in dialog
	private static final String GET_COMMENT_LIST_API = Util.API+"comment/";
	private ArrayList<CommentListModel> commentListArray;
	private ListView mCustomDialogListView;
	public static EditText mCustomDialogAddComment;
	private ImageView mCustomDialogClose;
	private TextView mCustomDialogTitle,mCustomDialogTextNoComment;
	public static Button mCustomDialogPost;
	private CommentListAdapter mCommentListAdapter;

	//variables for add comment
	private String mCommentType,mCommentText,mOrgPostOwnerId,mId,org_post_owner_id_share,privacy_type_share ;
	private static final String POST_ADD_COMMENT_API=Util.API+"comment";
	public static  Dialog mCustomDialogListComment;

	//variables for hiding a post
	private static final String HIDE_POST_API =Util.API+"hide_post";
	private String mHidePost,mOffensivePost,mHarassment,mSpam;
	private Dialog mDialogHidePost,mDialogFilterPost;

	//variables for share a post
	private static final String SHARE_POST_API = Util.API+"share";
	private String mStatusUpdateText,mSharePhotoId,mShareVideoId,mTrackType, mShareSongId,privacy_type;
	private String mShareText,mSharePhoto,mShareVideo,mShareSong,org_postId,mOrgPostOwnerId_view;

	private Dialog mDialogSharePost;
	public static String pos;


	public FeedAdapter(Context context,ArrayList<Timefeeds> listData){
		this.listData = listData;
		//layoutInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		prova = context;

		profile_options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

		/*	BitmapFactory.Options resizeOptions = new BitmapFactory.Options();
		resizeOptions.inSampleSize = 3; // decrease size 3 times
		resizeOptions.inScaled = true;*/

		feed_option = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder_timefeed)
		.showImageForEmptyUri(R.drawable.placeholder_timefeed) 
		.showImageOnFail(R.drawable.placeholder_timefeed)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.resetViewBeforeLoading(false)  // default
		.imageScaleType(ImageScaleType.EXACTLY)
		.build();

	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (layoutInflater == null)
			layoutInflater = (LayoutInflater) prova.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.custom_timefeed_main, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name_main);
			holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp_main);
			holder.FeedText = (TextView) convertView.findViewById(R.id.txtStatusMsg_main);
			holder.ProfilePic = (ImageView) convertView.findViewById(R.id.profilePic_main);
			holder.FeedImage=(ImageView) convertView.findViewById(R.id.feedImage_main);
			holder.ownerImage=(ImageView)convertView.findViewById(R.id.fr_user_image_main);
			holder.connectedImage=(ImageView)convertView.findViewById(R.id.fr_connected_user_main);
			holder.FriendRequestContainer=(RelativeLayout)convertView.findViewById(R.id.friend_request_container_main);
			holder.postDetails=(LinearLayout)convertView.findViewById(R.id.like_cmnt_share_main);
			holder.postDetailsOnPhoto=(LinearLayout)convertView.findViewById(R.id.like_cmnt_share_photo_main);
			holder.PostTransaction=(LinearLayout)convertView.findViewById(R.id.like_cmnt_share_trans_container_main);
			holder.feedLike=(ImageView)convertView.findViewById(R.id.feed_like_main);
			holder.feedLikePost=(ImageView)convertView.findViewById(R.id.feed_like_post_main);
			holder.feedShare=(ImageView)convertView.findViewById(R.id.feed_share_main);
			holder.feedComment=(ImageView)convertView.findViewById(R.id.feed_comment_main);
			holder.HidePost=(ImageView)convertView.findViewById(R.id.hide_post_main);
			holder.likeCount=(TextView)convertView.findViewById(R.id.feed_like_count_main);
			holder.commentCount=(TextView)convertView.findViewById(R.id.feed_comment_count_main);
			holder.shareCount=(TextView)convertView.findViewById(R.id.feed_share_count_main);
			holder.likeCountPhoto=(TextView)convertView.findViewById(R.id.feed_like_count_photo_main);
			holder.commentCountPhoto=(TextView)convertView.findViewById(R.id.feed_comment_count_photo_main);
			holder.shareCountPhoto=(TextView)convertView.findViewById(R.id.feed_share_count_photo_main);
			holder.video_overlay=(ImageView)convertView.findViewById(R.id.overlay_video_post_main);
			holder.textShareCotainer=(LinearLayout)convertView.findViewById(R.id.text_share_container_main);
			holder.textShareProfilePic=(ImageView)convertView.findViewById(R.id.textShare_user_profile_pic_main);
			holder.textShare_username=(TextView)convertView.findViewById(R.id.textshare_username_main);
			holder.textShare_text=(TextView)convertView.findViewById(R.id.textshare_text_main);
			holder.mainContainer=(LinearLayout)convertView.findViewById(R.id.main_feed_container_main);
			holder.patchContainer=(LinearLayout)convertView.findViewById(R.id.patchContainer_main);
			holder.location=(TextView)convertView.findViewById(R.id.txtLoc_main);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final Timefeeds feedItem = (Timefeeds) listData.get(position);

		Typeface typeFace=Typeface.createFromAsset(prova.getAssets(),"fonts/BentonSans-Regular.otf");

		if(feedItem.getDisp_msg()==null ||feedItem.getDisp_msg().equalsIgnoreCase("null") || feedItem.getDisp_msg().equalsIgnoreCase(" "))
		{
			String str = ("<b>" + feedItem.getOwner_name()) + " </b>";
			holder.name.setText(Html.fromHtml(str));
		}
		else
		{
			String str = ("<b>" + feedItem.getOwner_name()) + " </b>" + " " + "<font color=\"#808080\">" + feedItem.getDisp_msg() + "</font>";
			holder.name.setText(Html.fromHtml(str));
		}

		holder.timestamp.setText(feedItem.getTimeago());
		//holder.timestamp.setTypeface(typeFace);

		String imageUrl = feedItem.getOwner_profile_img();
		imageLoader.displayImage(imageUrl, holder.ProfilePic, profile_options, new SimpleImageLoadingListener() {
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


		// TO SHARE THE POST
		holder.feedShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(feedItem.getCan_share().equalsIgnoreCase("1"))
				{
					mTrackType = feedItem.getTrack_type();
					final EditText mEditStatusUpdate;
					ImageView mImageClose,mImageEdit,mImagePostPic;
					TextView mShareUserName,mShareStatus;
					String mStatusDispMsg,mSharePostImg = null;
					//Toast.makeText(prova, "IN PROGRESS"+feedItem.getTrack_id(), Toast.LENGTH_SHORT).show();	
					mDialogSharePost = new Dialog(prova); 
					mDialogSharePost.requestWindowFeature(Window.FEATURE_NO_TITLE);
					mDialogSharePost.setContentView(R.layout.dialog_share);
					mDialogSharePost.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					mEditStatusUpdate = (EditText)mDialogSharePost.findViewById(R.id.dialog_share_write);
					mImageClose = (ImageView)mDialogSharePost.findViewById(R.id.dialog_share_close);
					mImageEdit = (ImageView)mDialogSharePost.findViewById(R.id.dialog_share_edit);
					mImagePostPic = (ImageView)mDialogSharePost.findViewById(R.id.dialog_share_user_profile_pic);
					mShareUserName = (TextView)mDialogSharePost.findViewById(R.id.dialog_share_username);
					mShareStatus = (TextView)mDialogSharePost.findViewById(R.id.dialog_share_status);
					mShareUserName.setText(feedItem.getOwner_name());

					if(mTrackType.equalsIgnoreCase("status update") || mTrackType.equalsIgnoreCase("post_like")|| mTrackType.equalsIgnoreCase("comment_added")){
						mImagePostPic.setVisibility(View.INVISIBLE);
						mStatusDispMsg = feedItem.getDisp_msg();
						mOriginalPostId = feedItem.getPost_id();
						privacy_type_share=feedItem.getPrivacy_type();
						if(mTrackType.equalsIgnoreCase("status update"))
						{
							org_post_owner_id_share=feedItem.getOrg_owner_id();
						}
						else if(mTrackType.equalsIgnoreCase("post_like"))
						{
							org_post_owner_id_share=feedItem.getOrg_owner_id();
						}
						else if(mTrackType.equalsIgnoreCase("comment_added"))
						{
							org_post_owner_id_share=feedItem.getData().getOwner_id();
						}
						if(mStatusDispMsg!=null) mShareStatus.setText(mStatusDispMsg);
						mShareText="true";
						mSharePhoto="false";
						mShareVideo="false";
					}

					//check share for text_share type
					if(mTrackType.equalsIgnoreCase("text_share")){
						mImagePostPic.setVisibility(View.INVISIBLE);
						mStatusDispMsg = feedItem.getDisp_msg();
						mOriginalPostId = feedItem.getData().getPost_id();
						org_post_owner_id_share=feedItem.getData().getPost_owner_id();
						privacy_type_share=feedItem.getPrivacy_type();

						mShareText="true";
						mSharePhoto="false";
						mShareVideo="false";

					}

					//check share for photo type post
					if( mTrackType.equalsIgnoreCase("post_photo")||
							mTrackType.equalsIgnoreCase("cover_photo")|| mTrackType.equalsIgnoreCase("cover_photo_like") ||
							mTrackType.equalsIgnoreCase("cover_photo_comment_added")|| mTrackType.equalsIgnoreCase("profile_photo")||
							mTrackType.equalsIgnoreCase("profile_photo_comment_added") || mTrackType.equalsIgnoreCase("profile_photo_like")|| 
							mTrackType.equalsIgnoreCase("photo_like")|| mTrackType.equalsIgnoreCase("photo_comment_added")){

						privacy_type_share=feedItem.getPrivacy_type();

						if(mTrackType.equalsIgnoreCase("post_photo"))
						{
							org_post_owner_id_share=feedItem.getOrg_owner_id();
						}
						else if(mTrackType.equalsIgnoreCase("cover_photo")|| mTrackType.equalsIgnoreCase("cover_photo_like")||
								mTrackType.equalsIgnoreCase("cover_photo_comment_added")|| mTrackType.equalsIgnoreCase("profile_photo")
								||mTrackType.equalsIgnoreCase("profile_photo_comment_added")|| mTrackType.equalsIgnoreCase("profile_photo_like")
								||mTrackType.equalsIgnoreCase("photo_like")|| mTrackType.equalsIgnoreCase("photo_comment_added"))
						{
							org_post_owner_id_share=feedItem.getData().getOwner_id();
						}

						mShareText="false";
						mSharePhoto="true";
						mShareVideo="false";
						mStatusDispMsg = feedItem.getDisp_msg();
						mOriginalPostId = feedItem.getPost_id();

						if(mStatusDispMsg!=null) mShareStatus.setText(mStatusDispMsg);

						mSharePostImg = feedItem.getData().getPath();
						mSharePhotoId = feedItem.getData().getId();
						mImagePostPic.setVisibility(View.VISIBLE);
					}
					if(mTrackType.equalsIgnoreCase("photo_share")){

						mShareText="false";
						mSharePhoto="true";
						mShareVideo="false";
						privacy_type_share=feedItem.getPrivacy_type();

						org_post_owner_id_share=feedItem.getData().getPost_owner_id();
						mStatusDispMsg = feedItem.getDisp_msg();
						mSharePhotoId = feedItem.getData().getPhoto_id();
						mSharePostImg = feedItem.getData().getPhoto_path();
						mOriginalPostId = feedItem.getData().getPost_id();
						mImagePostPic.setVisibility(View.VISIBLE);

						if(mStatusDispMsg!=null) mShareStatus.setText(mStatusDispMsg);
					}

					// check the share for video  post

					if( mTrackType.equalsIgnoreCase("video_comment_added")|| mTrackType.equalsIgnoreCase("video_like") || 
							mTrackType.equalsIgnoreCase("post_video")  ||
							mTrackType.equalsIgnoreCase("video_uploaded")){
						privacy_type_share=feedItem.getPrivacy_type();

						mOriginalPostId = feedItem.getPost_id();
						mShareVideoId = feedItem.getData().getId();
						mShareText="false";
						mSharePhoto="false";
						mShareVideo="true";
						if(mTrackType.equalsIgnoreCase("post_video"))
						{
							org_post_owner_id_share=feedItem.getOrg_owner_id();
						}
						else if(mTrackType.equalsIgnoreCase("video_comment_added")|| mTrackType.equalsIgnoreCase("video_like")||
								mTrackType.equalsIgnoreCase("video_uploaded"))
						{
							org_post_owner_id_share=feedItem.getData().getOwner_id();
						}


						mStatusDispMsg = feedItem.getDisp_msg();

						if(mStatusDispMsg!=null) mShareStatus.setText(mStatusDispMsg);
						mSharePostImg = feedItem.getData().getCover();
						mImagePostPic.setVisibility(View.VISIBLE);
					}

					if (mTrackType.equalsIgnoreCase("video_share")){

						mShareText="false";
						mSharePhoto="false";
						mShareVideo="true";
						privacy_type_share=feedItem.getPrivacy_type();

						mShareVideoId = feedItem.getData().getVideo_id();
						mSharePostImg = feedItem.getData().getVideo_cover();
						mOriginalPostId = feedItem.getData().getPost_id();
						mStatusDispMsg = feedItem.getDisp_msg();
						org_post_owner_id_share=feedItem.getData().getPost_owner_id();

						if(mStatusDispMsg!=null) mShareStatus.setText(mStatusDispMsg);

					}

					if(mSharePostImg!=null){
						imageLoader.displayImage(mSharePostImg, mImagePostPic, profile_options, new SimpleImageLoadingListener() {

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
							mDialogSharePost.dismiss();
						}
					});

					if(mEditStatusUpdate.getText().toString().trim().length()>0){
						mStatusUpdateText = mEditStatusUpdate.getText().toString().trim();
					}

					mDialogSharePost.show();
					mImageEdit.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {

							mStatusUpdateText = mEditStatusUpdate.getText().toString().trim();
							ConnectionDetector conn = new ConnectionDetector(prova);
							if(conn.isConnectingToInternet()){
								new PostShare().execute(); 
							}else {
								alert.showAlertDialog(prova, "Connnection Error", "Check your internet connection!",false);
							}
						}
					});
				}
				else
				{
					Toast.makeText(prova, "User cannot share this post", Toast.LENGTH_SHORT).show();
				}
			}
		});

		//To show like list on photo
		holder.likeCountPhoto.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String id;
				String trackType = feedItem.getTrack_type();

				/*if(feedItem.getLike_count().equalsIgnoreCase("0")|| feedItem.getLike_count().equalsIgnoreCase("null")){
				Toast.makeText(prova,"No Likes for this post",Toast.LENGTH_LONG).show();
			} else {
				 */if (trackType.contains("photo")){   
					 if(trackType.equalsIgnoreCase("photo_share")){
						 id = feedItem.getData().getPhoto_id();
					 } else {
						 id  = feedItem.getData().getId();
					 }
					 Intent likeCountPhotoIntent =  new Intent(prova,LikeDetailActivity.class);
					 likeCountPhotoIntent.putExtra("photo","true");
					 likeCountPhotoIntent.putExtra("photo_id",id);
					 prova.startActivity(likeCountPhotoIntent);
					 ((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);

				 } 
				 if(trackType.contains("video")){
					 if(trackType.equalsIgnoreCase("video_share")){
						 id =  feedItem.getData().getVideo_id();
					 } else {
						 id  = feedItem.getData().getId();
					 }	     
					 Intent likeCountVideoIntent =  new Intent(prova,LikeDetailActivity.class);
					 likeCountVideoIntent.putExtra("video","true");
					 likeCountVideoIntent.putExtra("video_id",id);
					 prova.startActivity(likeCountVideoIntent);
					 ((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);

				 }
			}
		});
		// To show comment list on photo
		holder.commentCountPhoto.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String trackType = feedItem.getTrack_type();
				privacy_type=feedItem.getPrivacy_type();

				if(trackType.equalsIgnoreCase("photo_share")|| trackType.equalsIgnoreCase("video_share")){
					if(trackType.contains("photo")) 
					{
						mId = feedItem.getData().getPhoto_id();
						mOrgPostOwnerId = feedItem.getData().getPost_owner_id();
						org_postId=feedItem.getData().getPost_id();
					}
					if(trackType.contains("video"))
					{
						mId = feedItem.getData().getVideo_id();
						mOrgPostOwnerId = feedItem.getData().getPost_owner_id();
						org_postId=feedItem.getData().getPost_id();
					}
				}


				else if(trackType.equalsIgnoreCase("post_photo") ||trackType.equalsIgnoreCase("post_video") )
				{
					mId=feedItem.getData().getId();
					mOrgPostOwnerId = feedItem.getOrg_owner_id();
					org_postId=feedItem.getPost_id();
				}

				else if(trackType.equalsIgnoreCase("photo_comment_added")|| trackType.equalsIgnoreCase("video_comment_added") 
						|| trackType.equalsIgnoreCase("photo_like") || trackType.equalsIgnoreCase("video_like")|| trackType.equalsIgnoreCase("video_uploaded")
						|| trackType.equalsIgnoreCase("profile_photo") || trackType.equalsIgnoreCase("cover_photo") 
						|| trackType.equalsIgnoreCase("profile_photo_like")|| trackType.equalsIgnoreCase("cover_photo_like")
						|| trackType.equalsIgnoreCase("profile_photo_comment_added")|| trackType.equalsIgnoreCase("cover_photo_comment_added"))
				{
					org_postId=feedItem.getPost_id();
					mOrgPostOwnerId = feedItem.getOrg_owner_id();
					mId=feedItem.getData().getId();
				}

				Intent commentCountIntent =  new Intent(prova,CommentsDetailActivity.class);
				commentCountIntent.putExtra("post_id",org_postId);
				commentCountIntent.putExtra("org_post_owner_id",mOrgPostOwnerId);
				commentCountIntent.putExtra("id",mId);
				commentCountIntent.putExtra("commentType",trackType);
				commentCountIntent.putExtra("privacy",privacy_type);
				commentCountIntent.putExtra("from","Timefeed");
				prova.startActivity(commentCountIntent);
				((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);

			}

		});
		// To show share list on photo
		holder.shareCountPhoto.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String id;
				String trackType = 	feedItem.getTrack_type();
				if (trackType.contains("photo")){   
					if(trackType.equalsIgnoreCase("photo_share")){
						id = feedItem.getData().getPhoto_id();

					} else {
						id  = feedItem.getData().getId();
					}
					Intent shareCountPhotoIntent =  new Intent(prova,ShareDetailActivity.class);
					shareCountPhotoIntent.putExtra("photo","true");
					shareCountPhotoIntent.putExtra("photo_id",id);
					((Activity) prova).startActivity(shareCountPhotoIntent);
					((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);

				} 
				if(trackType.contains("video")){
					if(trackType.equalsIgnoreCase("video_share")){
						id =  feedItem.getData().getVideo_id();
					} else {
						id  = feedItem.getData().getId();
					}	    
					Intent shareCountVideoIntent =  new Intent(prova,ShareDetailActivity.class);
					shareCountVideoIntent.putExtra("video","true");
					shareCountVideoIntent.putExtra("video_id",id);
					prova.startActivity(shareCountVideoIntent);
					((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);

				}
			}				
		});
		// To show like list on text
		holder.likeCount.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String postId,trackType;
				trackType = feedItem.getTrack_type();
				/*if(feedItem.getLike_count().equalsIgnoreCase("0")|| feedItem.getLike_count().equalsIgnoreCase("null")){
					Toast.makeText(prova,"No Likes for this post",Toast.LENGTH_LONG).show();
				} else {
				 */if(trackType.equalsIgnoreCase("text_share")){
					 postId = feedItem.getData().getPost_id();
				 }else {
					 postId = feedItem.getPost_id();
				 }
				 Intent likeCountIntent =  new Intent(prova,LikeDetailActivity.class);
				 likeCountIntent.putExtra("post_id",postId);
				 likeCountIntent.putExtra("text","true");
				 prova.startActivity(likeCountIntent);
				 ((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);

			}		
		});
		//To show comment count list on text
		holder.commentCount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String trackType = feedItem.getTrack_type();
				privacy_type=feedItem.getPrivacy_type();

				if(trackType.equalsIgnoreCase("text_share"))
				{
					mId=feedItem.getData().getPost_id();
					mOrgPostOwnerId = feedItem.getData().getPost_owner_id();
					org_postId=feedItem.getData().getPost_id();

				}
				else if(trackType.equalsIgnoreCase("status update"))
				{
					mId=feedItem.getPost_id();
					mOrgPostOwnerId = feedItem.getOrg_owner_id();
					org_postId=feedItem.getPost_id();
				}

				else if(trackType.equalsIgnoreCase("comment_added")||trackType.equalsIgnoreCase("post_like"))
				{
					org_postId=feedItem.getPost_id();
					mOrgPostOwnerId = feedItem.getPost_owner_id();
					mId=feedItem.getPost_id();
				}


				Intent commentCountIntent =  new Intent(prova,CommentsDetailActivity.class);
				commentCountIntent.putExtra("post_id",org_postId);
				commentCountIntent.putExtra("org_post_owner_id",mOrgPostOwnerId);
				commentCountIntent.putExtra("id",mId);
				commentCountIntent.putExtra("commentType",trackType);
				commentCountIntent.putExtra("privacy",privacy_type);
				commentCountIntent.putExtra("from","Timefeed");
				prova.startActivity(commentCountIntent);
				((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);

			}			

		});
		// To show share count list on text
		holder.shareCount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {	
				String postId,trackType;
				trackType = feedItem.getTrack_type();
				if(trackType.equalsIgnoreCase("text_share")){
					postId = feedItem.getData().getPost_id();
				}else {
					postId = feedItem.getPost_id();
				}
				Intent shareCountIntent =  new Intent(prova,ShareDetailActivity.class);
				shareCountIntent.putExtra("post_id",postId);
				shareCountIntent.putExtra("text","true");
				prova.startActivity(shareCountIntent);
				((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);


			}	


		});
		// TO SHOW COMMENTS  & ADD COMMENTS
		holder.feedComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(commentListArray!=null){
					commentListArray.clear();

				} 

				String trackType = feedItem.getTrack_type();
				privacy_type=feedItem.getPrivacy_type();

				if(trackType.equalsIgnoreCase("photo_share")|| trackType.equalsIgnoreCase("video_share")){
					if(trackType.contains("photo")) 
					{
						mId = feedItem.getData().getPhoto_id();
						mOrgPostOwnerId = feedItem.getData().getPost_owner_id();
						org_postId=feedItem.getData().getPost_id();
					}
					if(trackType.contains("video"))
					{
						mId = feedItem.getData().getVideo_id();
						mOrgPostOwnerId = feedItem.getData().getPost_owner_id();
						org_postId=feedItem.getData().getPost_id();
					}
				}
				else if(trackType.equalsIgnoreCase("text_share"))
				{
					mId=feedItem.getData().getPost_id();
					mOrgPostOwnerId = feedItem.getData().getPost_owner_id();
					org_postId=feedItem.getData().getPost_id();

				}
				else if(trackType.equalsIgnoreCase("status update"))
				{
					mId=feedItem.getPost_id();
					mOrgPostOwnerId = feedItem.getOrg_owner_id();
					org_postId=feedItem.getPost_id();
				}
				else if(trackType.equalsIgnoreCase("post_photo") ||trackType.equalsIgnoreCase("post_video") )
				{
					mId=feedItem.getData().getId();
					mOrgPostOwnerId = feedItem.getOrg_owner_id();
					org_postId=feedItem.getPost_id();
				}
				else if(trackType.equalsIgnoreCase("comment_added")||trackType.equalsIgnoreCase("post_like"))
				{
					org_postId=feedItem.getPost_id();
					mOrgPostOwnerId = feedItem.getPost_owner_id();
					mId=feedItem.getPost_id();
				}
				else if(trackType.equalsIgnoreCase("photo_comment_added")|| trackType.equalsIgnoreCase("video_comment_added") 
						|| trackType.equalsIgnoreCase("photo_like") || trackType.equalsIgnoreCase("video_like")|| trackType.equalsIgnoreCase("video_uploaded")
						|| trackType.equalsIgnoreCase("profile_photo") || trackType.equalsIgnoreCase("cover_photo") 
						|| trackType.equalsIgnoreCase("profile_photo_like")|| trackType.equalsIgnoreCase("cover_photo_like")
						|| trackType.equalsIgnoreCase("profile_photo_comment_added")|| trackType.equalsIgnoreCase("cover_photo_comment_added"))
				{
					org_postId=feedItem.getPost_id();
					mOrgPostOwnerId = feedItem.getOrg_owner_id();
					mId=feedItem.getData().getId();
				}

				Intent commentCountIntent =  new Intent(prova,CommentsDetailActivity.class);
				commentCountIntent.putExtra("post_id",org_postId);
				commentCountIntent.putExtra("org_post_owner_id",mOrgPostOwnerId);
				commentCountIntent.putExtra("id",mId);
				commentCountIntent.putExtra("commentType",trackType);
				commentCountIntent.putExtra("privacy",privacy_type);
				commentCountIntent.putExtra("from","Timefeed");
				prova.startActivity(commentCountIntent);
				((Activity)prova).overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);

			}
		});


		// REPORT THE POST
		holder.HidePost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mDialogHidePost = new Dialog(prova);
				mDialogHidePost.requestWindowFeature(Window.FEATURE_NO_TITLE);
				mDialogHidePost.setContentView(R.layout.hide_post);
				mDialogHidePost.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

				final Button post;
				final Button harassment;
				final Button offensive;
				final Button spam;

				post = (Button) mDialogHidePost.findViewById(R.id.hide_postId);
				harassment = (Button)mDialogHidePost.findViewById(R.id.hide_post_harassment);
				offensive = (Button)mDialogHidePost.findViewById(R.id.hide_post_offensive);
				spam = (Button)mDialogHidePost.findViewById(R.id.hide_post_spam);

				mDialogHidePost.show();
				mHidePost="0";
				mHarassment="0";
				mOffensivePost="0";
				mSpam="0";

				mTrackId = feedItem.getTrack_id();
				mOriginalPostId = feedItem.getPost_id();
				post.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						mHidePost = "1";
						post.setBackgroundColor(Color.WHITE);
						ConnectionDetector conn = new ConnectionDetector(prova);
						listData.remove(position);
						notifyDataSetChanged();
						if(conn.isConnectingToInternet()){
							new HidePost().execute();
						} else {
							alert.showAlertDialog(prova, "Connection Error", "Check your Internet Connection",false);	
						}
					}
				});

				harassment.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						mHarassment="1";
						harassment.setBackgroundColor(Color.WHITE);
						ConnectionDetector conn = new ConnectionDetector(prova);
						listData.remove(position);
						notifyDataSetChanged();
						if(conn.isConnectingToInternet()){
							new HidePost().execute();
						} else {
							alert.showAlertDialog(prova, "Connection Error", "Check your Internet Connection",false);	
						}
					}
				});

				offensive.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						mOffensivePost="1";
						offensive.setBackgroundColor(Color.WHITE);
						ConnectionDetector conn = new ConnectionDetector(prova);
						listData.remove(position);
						notifyDataSetChanged();
						if(conn.isConnectingToInternet()){
							new HidePost().execute();
						} else {
							alert.showAlertDialog(prova, "Connection Error", "Check your Internet Connection",false);	
						}
					}
				});

				spam.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) { 

						mSpam="1";
						spam.setBackgroundColor(Color.WHITE);
						ConnectionDetector conn = new ConnectionDetector(prova);
						listData.remove(position);
						notifyDataSetChanged();
						if(conn.isConnectingToInternet()){
							new HidePost().execute();
						} else {
							alert.showAlertDialog(prova, "Connection Error", "Check your Internet Connection",false);	
						}
					}
				});
			}
		});	



		/*PopupMenu hidePostMenu = new PopupMenu(prova,v);
               MenuInflater inflater = hidePostMenu.getMenuInflater();
      		 inflater.inflate(R.menu.menu_hide_post, hidePostMenu.getMenu());

      		 hidePostMenu.show();
      	 	 hidePostMenu.setOnMenuItemClickListener(new OnMenuItemClickListener(){

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					int id = item.getItemId();
					mTrackId = feedItem.getTrack_id();
					mOriginalPostId = feedItem.getPost_id();

					mHidePost="0";
					mOffensivePost="0";
					mHarassment="0";
					mSpam="0";

					switch(id){
					case R.id.menu_hide_postId:
						mHidePost = "1";
						break;
					case R.id.menu_hide_post_offensive:
						mOffensivePost="1";
						break;
					case R.id.menu_hide_post_harassment:
						mHarassment = "1";
						break;
					case R.id.menu_hide_post_spam:
						mSpam="1";
						break;
					}
					new HidePost().execute();
					return true;


				}

      	 	 });*/
		// IDENTIFY THE POST LIKE
		if(feedItem.getUser_liked()!=null)
		{
			likeID=Integer.parseInt(feedItem.getUser_liked());

			if(feedItem.getTrack_type().contains("photo")|| feedItem.getTrack_type().contains("video"))
			{
				if(likeID>0)
				{
					holder.feedLike.setImageResource(R.drawable.like_blue);
				}
				else
				{
					holder.feedLike.setImageResource(R.drawable.like_white);

				}
			}
			else
			{
				if(likeID>0)
				{
					holder.feedLikePost.setImageResource(R.drawable.like_blue);
				}
				else
				{
					holder.feedLikePost.setImageResource(R.drawable.like_white);

				}
			}

		}
		else
		{
			if(feedItem.getTrack_type().contains("photo")|| feedItem.getTrack_type().contains("video"))
			{
				holder.feedLikePost.setImageResource(R.drawable.like_white);
			}
			else
			{
				holder.feedLikePost.setImageResource(R.drawable.like_white);
			}
		}
		// LIKING A POST
		holder.feedLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ConnectionDetector conn = new ConnectionDetector(prova);
				if(conn.isConnectingToInternet())
				{

					if(feedItem.getTrack_type().equalsIgnoreCase("video_like")||feedItem.getTrack_type().equalsIgnoreCase("video_comment_added")||
							feedItem.getTrack_type().equalsIgnoreCase("video_uploaded")||feedItem.getTrack_type().equalsIgnoreCase("photo_comment_added")
							|| feedItem.getTrack_type().equalsIgnoreCase("profile_photo")||feedItem.getTrack_type().equalsIgnoreCase("photo_like")
							||feedItem.getTrack_type().equalsIgnoreCase("post_photo")|| feedItem.getTrack_type().equalsIgnoreCase("cover_photo")
							|| feedItem.getTrack_type().equalsIgnoreCase("cover_photo_like")||feedItem.getTrack_type().equalsIgnoreCase("profile_photo_like")||
							feedItem.getTrack_type().equalsIgnoreCase("profile_photo_comment_added")||feedItem.getTrack_type().equalsIgnoreCase("cover_photo_comment_added")||
							feedItem.getTrack_type().equalsIgnoreCase("post_video"))
					{
						if(feedItem.getUser_liked() != null)
						{
							if(Integer.parseInt(feedItem.getUser_liked())>0)
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getData().getId(),feedItem.getOrg_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLike.setImageResource(R.drawable.like_white);
								int temp_count=Integer.parseInt(holder.likeCountPhoto.getText().toString().trim())-1;
								holder.likeCountPhoto.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
							else
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getData().getId(),feedItem.getOrg_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLike.setImageResource(R.drawable.like_blue);
								int temp_count=Integer.parseInt(holder.likeCountPhoto.getText().toString().trim())+1;
								holder.likeCountPhoto.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
						}
						else
						{
							new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getData().getId(),feedItem.getOrg_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
							holder.feedLike.setImageResource(R.drawable.like_blue);
							int temp_count=Integer.parseInt(holder.likeCountPhoto.getText().toString().trim())+1;
							holder.likeCountPhoto.setText(String.valueOf(temp_count));
							feedItem.setLike_count(String.valueOf(temp_count));

						}
					}
					else if(feedItem.getTrack_type().equalsIgnoreCase("photo_share"))
					{
						/*					new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getData().getPost_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position));
					holder.feedLikePost.setImageResource(R.drawable.like_blue);*/

						if(feedItem.getUser_liked()!=null)
						{
							if(Integer.parseInt(feedItem.getUser_liked())>0)
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getData().getPost_id(),feedItem.getData().getPhoto_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLike.setImageResource(R.drawable.like_white);
								int temp_count=Integer.parseInt(holder.likeCountPhoto.getText().toString().trim())-1;
								holder.likeCountPhoto.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
							else
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getData().getPost_id(),feedItem.getData().getPhoto_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLike.setImageResource(R.drawable.like_blue);
								int temp_count=Integer.parseInt(holder.likeCountPhoto.getText().toString().trim())+1;
								holder.likeCountPhoto.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
						}
						else
						{
							new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getData().getPost_id(),feedItem.getData().getPhoto_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
							holder.feedLike.setImageResource(R.drawable.like_blue);
							int temp_count=Integer.parseInt(holder.likeCountPhoto.getText().toString().trim())+1;
							holder.likeCountPhoto.setText(String.valueOf(temp_count));
							feedItem.setLike_count(String.valueOf(temp_count));

						}

					}
					else if(feedItem.getTrack_type().equalsIgnoreCase("video_share"))
					{
						/*					new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getData().getPost_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position));
					holder.feedLikePost.setImageResource(R.drawable.like_blue);*/

						if(feedItem.getUser_liked()!=null)
						{
							if(Integer.parseInt(feedItem.getUser_liked())>0)
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getData().getPost_id(),feedItem.getData().getVideo_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLike.setImageResource(R.drawable.like_white);
								int temp_count=Integer.parseInt(holder.likeCountPhoto.getText().toString().trim())-1;
								holder.likeCountPhoto.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
							else
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getData().getPost_id(),feedItem.getData().getVideo_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLike.setImageResource(R.drawable.like_blue);
								int temp_count=Integer.parseInt(holder.likeCountPhoto.getText().toString().trim())+1;
								holder.likeCountPhoto.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
						}
						else
						{
							new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getData().getPost_id(),feedItem.getData().getVideo_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
							holder.feedLike.setImageResource(R.drawable.like_blue);
							int temp_count=Integer.parseInt(holder.likeCountPhoto.getText().toString().trim())+1;
							holder.likeCountPhoto.setText(String.valueOf(temp_count));
							feedItem.setLike_count(String.valueOf(temp_count));

						}

					}

				}

				else
				{
					alert.showAlertDialog(prova, "Connection Error", "Please check your internet connection", false);
				}
			}
		});

		holder.feedLikePost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ConnectionDetector conn = new ConnectionDetector(prova);
				if(conn.isConnectingToInternet())
				{

					if(feedItem.getTrack_type().equalsIgnoreCase("status update"))

					{
						if(feedItem.getUser_liked()!=null)
						{
							if(Integer.parseInt(feedItem.getUser_liked())>0)
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getPost_id(),feedItem.getOrg_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLikePost.setImageResource(R.drawable.like_white);
								int temp_count=Integer.parseInt(holder.likeCount.getText().toString().trim())-1;
								holder.likeCount.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
							else
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getPost_id(),feedItem.getOrg_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLikePost.setImageResource(R.drawable.like_blue);
								int temp_count=Integer.parseInt(holder.likeCount.getText().toString().trim())+1;
								holder.likeCount.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
						}
						else
						{
							new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getPost_id(),feedItem.getOrg_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
							holder.feedLikePost.setImageResource(R.drawable.like_blue);
							int temp_count=Integer.parseInt(holder.likeCount.getText().toString().trim())+1;
							holder.likeCount.setText(String.valueOf(temp_count));
							feedItem.setLike_count(String.valueOf(temp_count));

						}

					}
					//
					else if(feedItem.getTrack_type().equalsIgnoreCase("comment_added")||feedItem.getTrack_type().equalsIgnoreCase("post_like"))

					{
						/*new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getPost_id(),feedItem.getPost_owner_id(),Integer.toString(position));
						holder.feedLikePost.setImageResource(R.drawable.like_blue);*/

						if(feedItem.getUser_liked()!=null)
						{
							if(Integer.parseInt(feedItem.getUser_liked())>0)
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getPost_id(),feedItem.getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLikePost.setImageResource(R.drawable.like_white);
								int temp_count=Integer.parseInt(holder.likeCount.getText().toString().trim())-1;
								holder.likeCount.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
							else
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getPost_id(),feedItem.getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLikePost.setImageResource(R.drawable.like_blue);
								int temp_count=Integer.parseInt(holder.likeCount.getText().toString().trim())+1;
								holder.likeCount.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
						}
						else
						{
							new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getPost_id(),feedItem.getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
							holder.feedLikePost.setImageResource(R.drawable.like_blue);
							int temp_count=Integer.parseInt(holder.likeCount.getText().toString().trim())+1;
							holder.likeCount.setText(String.valueOf(temp_count));
							feedItem.setLike_count(String.valueOf(temp_count));

						}

					}
					else if(feedItem.getTrack_type().equalsIgnoreCase("text_share"))
					{
						/*						new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getPost_id(),feedItem.getData().getPost_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position));
						holder.feedLikePost.setImageResource(R.drawable.like_blue);*/
						if(feedItem.getUser_liked()!=null)
						{
							if(Integer.parseInt(feedItem.getUser_liked())>0)
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getData().getPost_id(),feedItem.getData().getPost_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLikePost.setImageResource(R.drawable.like_white);
								int temp_count=Integer.parseInt(holder.likeCount.getText().toString().trim())-1;
								holder.likeCount.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
							else
							{
								new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getData().getPost_id(),feedItem.getData().getPost_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
								holder.feedLikePost.setImageResource(R.drawable.like_blue);
								int temp_count=Integer.parseInt(holder.likeCount.getText().toString().trim())+1;
								holder.likeCount.setText(String.valueOf(temp_count));
								feedItem.setLike_count(String.valueOf(temp_count));

							}
						}
						else
						{
							new LikePostAsync().execute(feedItem.getTrack_type(),feedItem.getData().getPost_id(),feedItem.getData().getPost_id(),feedItem.getData().getPost_owner_id(),Integer.toString(position),feedItem.getPrivacy_type());
							holder.feedLikePost.setImageResource(R.drawable.like_blue);
							int temp_count=Integer.parseInt(holder.likeCount.getText().toString().trim())+1;
							holder.likeCount.setText(String.valueOf(temp_count));
							feedItem.setLike_count(String.valueOf(temp_count));

						}

					}

				}

				else
				{
					alert.showAlertDialog(prova, "Connection Error", "Please check your internet connection", false);
				}

			}
		});

		// TEMPORARY --- IF TRACKER TYPE IS "null"
		if( feedItem.getTrack_type()==null || feedItem.getTrack_type().equalsIgnoreCase(" ")||feedItem.getTrack_type().equalsIgnoreCase("null"))
		{
			holder.feedLikePost.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.GONE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.FeedText.setVisibility(View.GONE);
			holder.FeedImage.setVisibility(View.GONE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.mainContainer.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.GONE);
			holder.location.setVisibility(View.GONE);


		}
		//TRACKER TYPE = post_photo 
		else if(feedItem.getTrack_type().equalsIgnoreCase("post_photo"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}

			if(feedItem.getData().getLocation()==null ||feedItem.getData().getLocation().equalsIgnoreCase("null")||
					feedItem.getData().getLocation().equalsIgnoreCase(""))
			{
				holder.location.setVisibility(View.GONE);
			}else
			{
				holder.location.setVisibility(View.VISIBLE);
				holder.location.setText(feedItem.getData().getLocation());
			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getPhoto_share_count()==null||feedItem.getData().getPhoto_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getPhoto_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getPhoto_share_count());
			}
			holder.patchContainer.setVisibility(View.VISIBLE); 
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				//makeTextViewResizable(holder.FeedText, 3, "View More", true);
				holder.FeedText.setVisibility(View.VISIBLE);
			}
			holder.FeedImage.setVisibility(View.VISIBLE);

			String extension = feedItem.getData().getPath().substring(feedItem.getData().getPath().lastIndexOf("."));
			/*if(extension.equalsIgnoreCase(".gif"))
			{
				Ion.with(prova)
				.load(feedItem.getData().getPath())
				.withBitmap()
				.placeholder(R.drawable.placeholder_timefeed)
				.error(R.drawable.placeholder_timefeed)
				.intoImageView(holder.FeedImage);		
			}
			else{*/
			imageLoader.displayImage(feedItem.getData().getPath(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
			//}

		}
		//TRACKER TYPE= status update
		else if(feedItem.getTrack_type().equalsIgnoreCase("status update"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCount.setText("0");
			}
			else
			{
				holder.likeCount.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCount.setText("0");
			}
			else
			{
				holder.commentCount.setText(feedItem.getComment_count());

			}
			if(feedItem.getData()!=null)
				if(feedItem.getData().getLocation()==null ||feedItem.getData().getLocation().equalsIgnoreCase("null")||
				feedItem.getData().getLocation().equalsIgnoreCase(""))
				{
					holder.location.setVisibility(View.GONE);
				}else
				{
					holder.location.setVisibility(View.VISIBLE);
					holder.location.setText(feedItem.getData().getLocation());
				}
			//TEMP COUNT FOR SHARE
			if(feedItem.getPost_share_count()==null||feedItem.getPost_share_count().equalsIgnoreCase("null") || 
					feedItem.getPost_share_count().equalsIgnoreCase(""))
			{
				holder.shareCount.setText("0");
			}
			else
			{
				holder.shareCount.setText(feedItem.getPost_share_count());
			}
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.feedLikePost.setVisibility(View.VISIBLE);
			holder.feedLike.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.postDetailsOnPhoto.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.FeedText.setVisibility(View.VISIBLE);
			holder.FeedText.setText(feedItem.getStatus_update_text());
			holder.FeedImage.setVisibility(View.GONE);

		}
		//TRACKER TYPE = post_video
		else if(feedItem.getTrack_type().equalsIgnoreCase("post_video"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			if(feedItem.getData().getLocation()==null ||feedItem.getData().getLocation().equalsIgnoreCase("null")||
					feedItem.getData().getLocation().equalsIgnoreCase(""))
			{
				holder.location.setVisibility(View.GONE);
			}else
			{
				holder.location.setVisibility(View.VISIBLE);
				holder.location.setText(feedItem.getData().getLocation());
			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getVideo_share_count()==null||feedItem.getData().getVideo_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getVideo_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getVideo_share_count());
			}
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.VISIBLE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);

			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}
			holder.FeedImage.setVisibility(View.VISIBLE);
			imageLoader.displayImage(feedItem.getData().getCover(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
		}
		//TRACKER TYPE = video_like
		else if(feedItem.getTrack_type().equalsIgnoreCase("video_like"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getVideo_share_count()==null||feedItem.getData().getVideo_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getVideo_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getVideo_share_count());
			}

			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.VISIBLE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);

			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			imageLoader.displayImage(feedItem.getData().getCover(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
		}
		//TRACKER TYPE = photo_like
		else if(feedItem.getTrack_type().equalsIgnoreCase("photo_like"))
		{		
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if( feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			holder.location.setVisibility(View.GONE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getPhoto_share_count()==null||feedItem.getData().getPhoto_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getPhoto_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getPhoto_share_count());
			}
			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			String extension = feedItem.getData().getPath().substring(feedItem.getData().getPath().lastIndexOf("."));
			/*			if(extension.equalsIgnoreCase(".gif"))
			{
				Ion.with(prova)
				.load(feedItem.getData().getPath())
				.withBitmap()
				.placeholder(R.drawable.placeholder_timefeed)
				.error(R.drawable.placeholder_timefeed)
				.intoImageView(holder.FeedImage);		
			}else{*/
			imageLoader.displayImage(feedItem.getData().getPath(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
			//	}

		}
		//TRACKER TYPE = video_comment_added
		else if(feedItem.getTrack_type().equalsIgnoreCase("video_comment_added"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getVideo_share_count()==null||feedItem.getData().getVideo_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getVideo_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getVideo_share_count());
			}

			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.VISIBLE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);

			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			imageLoader.displayImage(feedItem.getData().getCover(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
		}

		//TRACKER TYPE= post_like
		else if(feedItem.getTrack_type().equalsIgnoreCase("post_like"))
		{
			if( feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCount.setText("0");
			}
			else
			{
				holder.likeCount.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCount.setText("0");
			}
			else
			{
				holder.commentCount.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getPost_share_count()==null||feedItem.getPost_share_count().equalsIgnoreCase("null") || 
					feedItem.getPost_share_count().equalsIgnoreCase(""))
			{
				holder.shareCount.setText("0");
			}
			else
			{
				holder.shareCount.setText(feedItem.getPost_share_count());
			}

			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.GONE);
			holder.feedLikePost.setVisibility(View.VISIBLE);
			holder.postDetailsOnPhoto.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.VISIBLE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.FeedText.setText(feedItem.getStatus_update_text());
			holder.FeedText.setVisibility(View.VISIBLE);
			holder.FeedImage.setVisibility(View.GONE);
		}
		//TRACKER TYPE= relationship_accepted
		else if(feedItem.getTrack_type().equalsIgnoreCase("relationship_accepted"))
		{
			if(feedItem.getData().getLastname()!=null ||feedItem.getData().getLastname()!=" " || feedItem.getData().getLastname()!="null")
			{
				str_ra = ("<b>" + feedItem.getOwner_name()) + " </b>" + " " + "<font color=\"#808080\">" + feedItem.getDisp_msg() 
						+ "</font>" +" "+"<b>" + feedItem.getData().getFirstname() +" "+feedItem.getData().getLastname()+ " </b>";
			}
			else
			{
				str_ra = ("<b>" + feedItem.getOwner_name()) + " </b>" + " " + "<font color=\"#808080\">" + feedItem.getDisp_msg() + "</font>" +" "+"<b>" + feedItem.getData().getFirstname() + " </b>";

			}
			holder.name.setText(Html.fromHtml(str_ra));
			holder.location.setVisibility(View.GONE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.GONE);
			holder.FeedText.setVisibility(View.GONE);
			holder.FeedImage.setVisibility(View.GONE);
			holder.FriendRequestContainer.setVisibility(View.VISIBLE);

			imageLoader.displayImage(feedItem.getOwner_profile_img_thumb(), holder.ownerImage, profile_options, new SimpleImageLoadingListener() {
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


			imageLoader.displayImage(feedItem.getData().getPath(), holder.connectedImage, profile_options, new SimpleImageLoadingListener() {
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


		}
		//TRACKER TYPE= friend_request_accepted
		else if(feedItem.getTrack_type().equalsIgnoreCase("friend_request_accepted"))
		{
			if(feedItem.getData().getLastname()!=null ||feedItem.getData().getLastname()!=" " || feedItem.getData().getLastname()!="null")
			{
				str_fr = ("<b>" + feedItem.getOwner_name()) + " </b>" + " " + "<font color=\"#808080\">" +
						feedItem.getDisp_msg() + "</font>"+" " +"<b>" + feedItem.getData().getFirstname()+" "+feedItem.getData().getLastname()+ " </b>";
			}
			else
			{
				str_fr = ("<b>" + feedItem.getOwner_name()) + " </b>" + " " + "<font color=\"#808080\">" + feedItem.getDisp_msg() + "</font>"+" " +"<b>" + feedItem.getData().getFirstname()+ " </b>";
			}
			holder.name.setText(Html.fromHtml(str_fr));
			holder.location.setVisibility(View.GONE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.GONE);
			holder.FeedText.setVisibility(View.GONE);
			holder.FeedImage.setVisibility(View.GONE);
			holder.FriendRequestContainer.setVisibility(View.VISIBLE);

			imageLoader.displayImage(feedItem.getOwner_profile_img_thumb(), holder.ownerImage, profile_options, new SimpleImageLoadingListener() {
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
			imageLoader.displayImage(feedItem.getData().getPath(), holder.connectedImage, profile_options, new SimpleImageLoadingListener() {
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


		}

		//TRACKER TYPE = photo_comment_added
		// change holder.postDetails.setVisibility(View.GONE);
		//holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
		else if(feedItem.getTrack_type().equalsIgnoreCase("photo_comment_added"))
		{
			if( feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getPhoto_share_count()==null||feedItem.getData().getPhoto_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getPhoto_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getPhoto_share_count());
			}
			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.FeedText.setVisibility(View.GONE);

			holder.FeedImage.setVisibility(View.VISIBLE);
			String extension = feedItem.getData().getPath().substring(feedItem.getData().getPath().lastIndexOf("."));
			/*			if(extension.equalsIgnoreCase(".gif"))
			{
				Ion.with(prova)
				.load(feedItem.getData().getPath())
				.withBitmap()
				.placeholder(R.drawable.placeholder_timefeed)
				.error(R.drawable.placeholder_timefeed)
				.intoImageView(holder.FeedImage);		
			}else{*/
			imageLoader.displayImage(feedItem.getData().getPath(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
			//		}


		}
		//TRACKER TYPE = profile_photo
		else if(feedItem.getTrack_type().equalsIgnoreCase("profile_photo"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if( feedItem.getComment_count()==null ||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getPhoto_share_count()==null||feedItem.getData().getPhoto_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getPhoto_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getPhoto_share_count());
			}
			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.VISIBLE);

			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			String extension = feedItem.getData().getPath().substring(feedItem.getData().getPath().lastIndexOf("."));
			/*if(extension.equalsIgnoreCase(".gif"))
			{
				Ion.with(prova)
				.load(feedItem.getData().getPath())
				.withBitmap()
				.placeholder(R.drawable.placeholder_timefeed)
				.error(R.drawable.placeholder_timefeed)
				.intoImageView(holder.FeedImage);		
			}else{*/
			imageLoader.displayImage(feedItem.getData().getPath(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
			//	}


		}

		//TRACKER TYPE = cover_photo
		else if(feedItem.getTrack_type().equalsIgnoreCase("cover_photo"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getPhoto_share_count()==null||feedItem.getData().getPhoto_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getPhoto_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getPhoto_share_count());
			}
			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.VISIBLE);

			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			String extension = feedItem.getData().getPath().substring(feedItem.getData().getPath().lastIndexOf("."));
			/*if(extension.equalsIgnoreCase(".gif"))
			{
				Ion.with(prova)
				.load(feedItem.getData().getPath())
				.withBitmap()
				.placeholder(R.drawable.placeholder_timefeed)
				.error(R.drawable.placeholder_timefeed)
				.intoImageView(holder.FeedImage);		
			}else{*/
			imageLoader.displayImage(feedItem.getData().getPath(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
			//	}

		}
		//TRACKER TYPE = profile_photo_like
		else if(feedItem.getTrack_type().equalsIgnoreCase("profile_photo_like"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getPhoto_share_count()==null||feedItem.getData().getPhoto_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getPhoto_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getPhoto_share_count());
			}
			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);

			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			String extension = feedItem.getData().getPath().substring(feedItem.getData().getPath().lastIndexOf("."));
			/*if(extension.equalsIgnoreCase(".gif"))
			{
				Ion.with(prova)
				.load(feedItem.getData().getPath())
				.withBitmap()
				.placeholder(R.drawable.placeholder_timefeed)
				.error(R.drawable.placeholder_timefeed)
				.intoImageView(holder.FeedImage);		
			}else{*/
			imageLoader.displayImage(feedItem.getData().getPath(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
			//		}

		}
		//TRACKER TYPE = cover_photo_like
		else if(feedItem.getTrack_type().equalsIgnoreCase("cover_photo_like"))
		{
			if( feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if( feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getPhoto_share_count()==null||feedItem.getData().getPhoto_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getPhoto_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getPhoto_share_count());
			}
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.location.setVisibility(View.GONE);

			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			String extension = feedItem.getData().getPath().substring(feedItem.getData().getPath().lastIndexOf("."));
			/*	if(extension.equalsIgnoreCase(".gif"))
			{
				Ion.with(prova)
				.load(feedItem.getData().getPath())
				.withBitmap()
				.placeholder(R.drawable.placeholder_timefeed)
				.error(R.drawable.placeholder_timefeed)
				.intoImageView(holder.FeedImage);		
			}else{*/
			imageLoader.displayImage(feedItem.getData().getPath(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
			//	}

		}
		//TRACKER TYPE = cover_photo_comment_added
		else if(feedItem.getTrack_type().equalsIgnoreCase("cover_photo_comment_added"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getPhoto_share_count()==null||feedItem.getData().getPhoto_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getPhoto_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getPhoto_share_count());
			}
			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.VISIBLE);

			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			String extension = feedItem.getData().getPath().substring(feedItem.getData().getPath().lastIndexOf("."));
			/*if(extension.equalsIgnoreCase(".gif"))
			{
				Ion.with(prova)
				.load(feedItem.getData().getPath())
				.withBitmap()
				.placeholder(R.drawable.placeholder_timefeed)
				.error(R.drawable.placeholder_timefeed)
				.intoImageView(holder.FeedImage);		
			}else{*/
			imageLoader.displayImage(feedItem.getData().getPath(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
			//	}

		}
		//TRACKER TYPE = profile_photo_comment_added
		else if(feedItem.getTrack_type().equalsIgnoreCase("profile_photo_comment_added"))
		{
			if( feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getPhoto_share_count()==null||feedItem.getData().getPhoto_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getPhoto_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getPhoto_share_count());
			}
			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.VISIBLE);

			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			String extension = feedItem.getData().getPath().substring(feedItem.getData().getPath().lastIndexOf("."));
			/*if(extension.equalsIgnoreCase(".gif"))
			{
				Ion.with(prova)
				.load(feedItem.getData().getPath())
				.withBitmap()
				.placeholder(R.drawable.placeholder_timefeed)
				.error(R.drawable.placeholder_timefeed)
				.intoImageView(holder.FeedImage);		
			}else{*/
			imageLoader.displayImage(feedItem.getData().getPath(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
			//		}

		}
		//TRACKER TYPE= comment_added
		else if(feedItem.getTrack_type().equalsIgnoreCase("comment_added"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCount.setText("0");
			}
			else
			{
				holder.likeCount.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCount.setText("0");
			}
			else
			{
				holder.commentCount.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getPost_share_count()==null||feedItem.getPost_share_count().equalsIgnoreCase("null") || 
					feedItem.getPost_share_count().equalsIgnoreCase(""))
			{
				holder.shareCount.setText("0");
			}
			else
			{
				holder.shareCount.setText(feedItem.getPost_share_count());
			}
			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.GONE);
			holder.feedLikePost.setVisibility(View.VISIBLE);
			holder.postDetails.setVisibility(View.VISIBLE);
			holder.postDetailsOnPhoto.setVisibility(View.GONE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.FeedText.setText(feedItem.getStatus_update_text());
			holder.FeedText.setVisibility(View.VISIBLE);
			holder.FeedImage.setVisibility(View.GONE);
		}
		//TRACKER TYPE= text_share
		else if(feedItem.getTrack_type().equalsIgnoreCase("text_share"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCount.setText("0");
			}
			else
			{
				holder.likeCount.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCount.setText("0");
			}
			else
			{
				holder.commentCount.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getPost_share_count()==null||feedItem.getPost_share_count().equalsIgnoreCase("null") || 
					feedItem.getPost_share_count().equalsIgnoreCase(""))
			{
				holder.shareCount.setText("0");
			}
			else
			{
				holder.shareCount.setText(feedItem.getPost_share_count());
			}
			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.VISIBLE);
			holder.feedLike.setVisibility(View.GONE);
			holder.feedLikePost.setVisibility(View.VISIBLE);
			holder.postDetails.setVisibility(View.VISIBLE);
			holder.postDetailsOnPhoto.setVisibility(View.GONE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.FeedText.setVisibility(View.VISIBLE);
			holder.FeedImage.setVisibility(View.GONE);
			//holder.FeedText.setText(feedItem.getData().getPost_text());
			if (TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.textShare_username.setText(feedItem.getData().getPost_owner_name());
			holder.textShare_text.setText(feedItem.getData().getPost_text());

			imageLoader.displayImage(feedItem.getData().getPost_owner_image(), holder.textShareProfilePic, profile_options, new SimpleImageLoadingListener() {
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
		}
		//TRACKER TYPE = photo_share
		else if(feedItem.getTrack_type().equalsIgnoreCase("photo_share"))
		{
			if( feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getPhoto_share_count()==null||feedItem.getData().getPhoto_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getPhoto_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getPhoto_share_count());
			}
			holder.location.setVisibility(View.GONE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.PostTransaction.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);

			if (feedItem.getData().getText()==null || TextUtils.isEmpty(feedItem.getData().getText()) || feedItem.getData().getText().equalsIgnoreCase("null") || feedItem.getData().getText().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);
			} else {
				holder.FeedText.setText(feedItem.getData().getText());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			String extension = feedItem.getData().getPhoto_path().substring(feedItem.getData().getPhoto_path().lastIndexOf("."));
			/*if(extension.equalsIgnoreCase(".gif"))
			{
				Ion.with(prova)
				.load(feedItem.getData().getPhoto_path())
				.withBitmap()
				.placeholder(R.drawable.placeholder_timefeed)
				.error(R.drawable.placeholder_timefeed)
				.intoImageView(holder.FeedImage);		
			}else{*/
			imageLoader.displayImage(feedItem.getData().getPhoto_path(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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
			//		}

		}

		//TRACKER TYPE = video_share
		else if(feedItem.getTrack_type().equalsIgnoreCase("video_share"))
		{
			if(feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if( feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getVideo_share_count()==null||feedItem.getData().getVideo_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getVideo_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getVideo_share_count());
			}
			holder.location.setVisibility(View.GONE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.VISIBLE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.VISIBLE);

			if (feedItem.getData().getShare_text()==null || TextUtils.isEmpty(feedItem.getData().getShare_text()) || feedItem.getData().getShare_text().equalsIgnoreCase("null") || feedItem.getData().getShare_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);

			} else {
				holder.FeedText.setText(feedItem.getData().getShare_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			imageLoader.displayImage(feedItem.getData().getVideo_cover(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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

		}
		//TRACKER TYPE = video_uploaded
		else if(feedItem.getTrack_type().equalsIgnoreCase("video_uploaded"))
		{
			if( feedItem.getLike_count()==null||feedItem.getLike_count().equalsIgnoreCase("null") || feedItem.getLike_count().equalsIgnoreCase(""))
			{
				holder.likeCountPhoto.setText("0");
			}
			else
			{
				holder.likeCountPhoto.setText(feedItem.getLike_count());
			}
			if(feedItem.getComment_count()==null||feedItem.getComment_count().equalsIgnoreCase("null") || feedItem.getComment_count().equalsIgnoreCase(""))
			{
				holder.commentCountPhoto.setText("0");
			}
			else
			{
				holder.commentCountPhoto.setText(feedItem.getComment_count());

			}
			//TEMP COUNT FOR SHARE
			if(feedItem.getData().getVideo_share_count()==null||feedItem.getData().getVideo_share_count().equalsIgnoreCase("null") || 
					feedItem.getData().getVideo_share_count().equalsIgnoreCase(""))
			{
				holder.shareCountPhoto.setText("0");
			}
			else
			{
				holder.shareCountPhoto.setText(feedItem.getData().getVideo_share_count());
			}
			holder.location.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.VISIBLE);
			holder.mainContainer.setVisibility(View.VISIBLE);
			holder.video_overlay.setVisibility(View.VISIBLE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.VISIBLE);
			holder.feedLikePost.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.VISIBLE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.VISIBLE);

			if (feedItem.getStatus_update_text()==null || TextUtils.isEmpty(feedItem.getStatus_update_text()) || feedItem.getStatus_update_text().equalsIgnoreCase("null") || feedItem.getStatus_update_text().equalsIgnoreCase(" ")) {

				holder.FeedText.setVisibility(View.GONE);

			} else {
				holder.FeedText.setText(feedItem.getStatus_update_text());
				holder.FeedText.setVisibility(View.VISIBLE);
			}

			holder.FeedImage.setVisibility(View.VISIBLE);
			imageLoader.displayImage(feedItem.getData().getCover(), holder.FeedImage, feed_option, new SimpleImageLoadingListener() {
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

		}

		else
		{
			holder.location.setVisibility(View.GONE);
			holder.feedLike.setVisibility(View.GONE);  
			holder.feedLikePost.setVisibility(View.GONE);
			holder.postDetails.setVisibility(View.GONE);
			holder.postDetailsOnPhoto.setVisibility(View.GONE);
			holder.FriendRequestContainer.setVisibility(View.GONE);
			holder.PostTransaction.setVisibility(View.GONE);
			//holder.FeedText.setText("UNDEFINED TRACKER TYPE :" + feedItem.getTrack_type().toString());
			holder.FeedText.setVisibility(View.GONE);
			holder.FeedImage.setVisibility(View.GONE);
			holder.video_overlay.setVisibility(View.GONE);
			holder.textShareCotainer.setVisibility(View.GONE);
			holder.patchContainer.setVisibility(View.GONE);
			holder.mainContainer.setVisibility(View.GONE);

		}
		// TO GET THE DETAIL VIEW OF THE IMAGE
		holder.FeedImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Toast.makeText(prova, "detail view of image", Toast.LENGTH_SHORT).show();
				String post_id,photo_id;
				if(feedItem.getTrack_type().equalsIgnoreCase("post_photo")||feedItem.getTrack_type().equalsIgnoreCase("photo_like")||
						feedItem.getTrack_type().equalsIgnoreCase("photo_comment_added")||feedItem.getTrack_type().equalsIgnoreCase("profile_photo")||
						feedItem.getTrack_type().equalsIgnoreCase("cover_photo")||feedItem.getTrack_type().equalsIgnoreCase("profile_photo_like")||
						feedItem.getTrack_type().equalsIgnoreCase("cover_photo_like")||feedItem.getTrack_type().equalsIgnoreCase("cover_photo_comment_added")||
						feedItem.getTrack_type().equalsIgnoreCase("profile_photo_comment_added"))
				{   
					post_id = feedItem.getPost_id();
					photo_id = feedItem.getData().getId();

					Intent intent = new Intent(prova, ViewPhotoTimefeed.class);
					intent.putExtra("post_id",post_id);
					intent.putExtra("photo_id",photo_id);
					intent.putExtra("track_type",feedItem.getTrack_type());
					intent.putExtra("disp_msg",feedItem.getDisp_msg());
					intent.putExtra("owner_name",feedItem.getOwner_name());
					intent.putExtra("photo_like_count",feedItem.getLike_count());
					intent.putExtra("photo_comment_count",feedItem.getComment_count());
					intent.putExtra("status_update_text",feedItem.getStatus_update_text());
					intent.putExtra("ImagePath",feedItem.getData().getPath());
					intent.putExtra("privacy",feedItem.getPrivacy_type());
					// FOR LIKE IN DETAIL VIEW
					intent.putExtra("Id",feedItem.getData().getId());
					intent.putExtra("ownerID",feedItem.getOrg_owner_id());
					intent.putExtra("user_liked",feedItem.getUser_liked());
					intent.putExtra("photo_share_count",feedItem.getData().getPhoto_share_count());
					intent.putExtra("can_share",feedItem.getCan_share());
					prova.startActivity(intent); 
				}
				else if(feedItem.getTrack_type().equalsIgnoreCase("photo_share"))
				{    
					post_id =  feedItem.getData().getPost_id();
					photo_id = feedItem.getData().getPhoto_id();
					Intent intent = new Intent(prova, ViewPhotoTimefeed.class);
					intent.putExtra("post_id",post_id);
					intent.putExtra("photo_id",photo_id);
					intent.putExtra("track_type",feedItem.getTrack_type());
					intent.putExtra("disp_msg",feedItem.getDisp_msg());
					intent.putExtra("owner_name",feedItem.getOwner_name());
					intent.putExtra("photo_like_count",feedItem.getLike_count());
					intent.putExtra("photo_comment_count",feedItem.getComment_count());
					intent.putExtra("status_update_text",feedItem.getStatus_update_text());
					intent.putExtra("ImagePath",feedItem.getData().getPhoto_path());
					intent.putExtra("privacy",feedItem.getPrivacy_type());
					// FOR LIKE IN DETAIL VIEW
					intent.putExtra("Id",feedItem.getData().getPhoto_id());
					intent.putExtra("ownerID",feedItem.getData().getPost_owner_id());
					intent.putExtra("user_liked",feedItem.getUser_liked());
					intent.putExtra("photo_share_count",feedItem.getData().getPhoto_share_count());
					intent.putExtra("can_share",feedItem.getCan_share());
					prova.startActivity(intent); 
				}
				else if(feedItem.getTrack_type().contains("video"))
				{
					String extension = MimeTypeMap.getFileExtensionFromUrl(feedItem.getData().getVideo_url());
					String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
					Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
					mediaIntent.setDataAndType(Uri.parse(feedItem.getData().getVideo_url()), mimeType);
					prova.startActivity(mediaIntent);
				}

			}
		});
		// TO PLAY A VIDEO IN TIMEFEED
		holder.video_overlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String extension = MimeTypeMap.getFileExtensionFromUrl(feedItem.getData().getVideo_url());
				String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
				Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
				mediaIntent.setDataAndType(Uri.parse(feedItem.getData().getVideo_url()), mimeType);
				prova.startActivity(mediaIntent);
			}
		});
		// Third Party Intents
		holder.name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {/*
				if(feedItem.getTrack_owner_id().equalsIgnoreCase(Util.USER_ID))
				{
					Intent user_profile= new Intent(prova,UserDetailActivity.class);
					prova.startActivity(user_profile);
				}
				else
				{
					Intent intent = new Intent(prova,ThirdPartyUserDetailActivity.class);
					intent.putExtra("UserID", feedItem.getTrack_owner_id());
					Util.THIRD_PARTY_USER_NAME = feedItem.getOwner_name();
					Util.THIRD_PARTY_USER_ID=feedItem.getTrack_owner_id();
					prova.startActivity(intent);
				}
			 */}
		});
		holder.ProfilePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(feedItem.getTrack_owner_id().equalsIgnoreCase(Util.USER_ID))
				{
					Intent user_profile= new Intent(prova,UserDetailActivity.class);
					prova.startActivity(user_profile);
				}else{
					Intent intent = new Intent(prova,ThirdPartyUserDetailActivity.class);
					intent.putExtra("UserID", feedItem.getTrack_owner_id());
					Util.THIRD_PARTY_USER_NAME = feedItem.getOwner_name();
					Util.THIRD_PARTY_USER_ID=feedItem.getTrack_owner_id();
					prova.startActivity(intent);
				}
			}
		});

		holder.ownerImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(feedItem.getTrack_owner_id().equalsIgnoreCase(Util.USER_ID))
				{
					Intent user_profile= new Intent(prova,UserDetailActivity.class);
					prova.startActivity(user_profile);
				}else{
					Intent intent = new Intent(prova,ThirdPartyUserDetailActivity.class);
					intent.putExtra("UserID", feedItem.getTrack_owner_id());
					Util.THIRD_PARTY_USER_NAME = feedItem.getOwner_name();
					Util.THIRD_PARTY_USER_ID=feedItem.getTrack_owner_id();
					prova.startActivity(intent);
				}
			}
		});

		holder.connectedImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(feedItem.getData().getId().equalsIgnoreCase(Util.USER_ID))
				{
					Intent user_profile= new Intent(prova,UserDetailActivity.class);
					prova.startActivity(user_profile);
				}else{
					Intent intent = new Intent(prova,ThirdPartyUserDetailActivity.class);
					intent.putExtra("UserID", feedItem.getData().getId());
					Util.THIRD_PARTY_USER_NAME = feedItem.getData().getFirstname();
					Util.THIRD_PARTY_USER_ID=feedItem.getData().getId();
					prova.startActivity(intent);
				}
			}
		});

		holder.FeedText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Util.textflag1)
				{
					holder.FeedText.setMaxLines(Integer.MAX_VALUE);
					Util.textflag1=false;
				}
				else
				{
					holder.FeedText.setMaxLines(5);
					Util.textflag1=true;

				}
			}
		});

		holder.textShare_text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Util.textflag2)
				{
					holder.textShare_text.setMaxLines(Integer.MAX_VALUE);
					Util.textflag2=false;
				}
				else
				{
					holder.textShare_text.setMaxLines(5);
					Util.textflag2=true;

				}
			}
		});
		return convertView;
	}

	static class ViewHolder{
		ImageView ProfilePic,FeedImage,ownerImage,connectedImage,feedLike,feedLikePost,textShareProfilePic,
		feedShare,feedComment,HidePost,video_overlay;
		TextView FeedText,name,timestamp,textShare_username,textShare_text;
		RelativeLayout FriendRequestContainer;
		LinearLayout postDetails,postDetailsOnPhoto,PostTransaction,textShareCotainer,mainContainer,patchContainer;
		TextView likeCount,commentCount,shareCount,likeCountPhoto,commentCountPhoto,shareCountPhoto,location;
	}


	//Async class for getting the list of comments for a post in time feed
	class GetCommentList extends AsyncTask<String,Void,ArrayList<CommentListModel>>{

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(prova);
				mPDialog.show();
			}
			else { mPDialog.show();}
		}
		@Override
		protected ArrayList<CommentListModel> doInBackground(String... params) {
			String post_id = params[0];

			JSONObject receivedJSONResponse = HttpGetClient.sendHttpPost(GET_COMMENT_LIST_API+post_id);

			if(receivedJSONResponse!=null){
				try { 
					JSONArray receivedJSONResponseArray = receivedJSONResponse.getJSONArray("comments");
					if(receivedJSONResponseArray.length()>0){

						commentListArray = new ArrayList<CommentListModel>();
						for(int i=0;i<receivedJSONResponseArray.length();i++){
							JSONObject comments = receivedJSONResponseArray.getJSONObject(i); 
							CommentListModel model = new CommentListModel();
							model.setCreatedDate(comments.getString("created_date"));
							model.setPostId(comments.getString("post_id"));
							model.setPhotoId(comments.getString("photo_id"));
							model.setVideoId(comments.getString("video_id"));
							model.setSongId(comments.getString("song_id"));
							model.setCommentText(comments.getString("comment_text"));
							model.setCommentId(comments.getString("comment_id"));
							model.setCommentOwnerId(comments.getString("comment_owner_id"));
							model.setFirstName(comments.getString("firstname"));
							model.setLastName(comments.getString("lastname"));
							model.setUserProfilePhoto(comments.getString("user_profile_photo"));
							model.setOrgPostOwnerId(comments.getString("org_post_owner_id"));
							model.setTrackId(comments.getString("track_id"));
							model.setThumb1(comments.getString("thumb1"));
							model.setThumb2(comments.getString("thumb2"));
							model.setThumb3(comments.getString("thumb3"));
							commentListArray.add(model);
						}
					}	
					else {

					}
				} catch (JSONException e) {
					e.printStackTrace();}
			}
			return commentListArray;
		}
		@Override
		protected void onPostExecute(ArrayList<CommentListModel> result){ 
			mPDialog.dismiss();
			mCustomDialogListComment.show();
			if(result ==null){

				mCustomDialogListView.setVisibility(View.GONE);
				mCustomDialogTextNoComment.setVisibility(View.VISIBLE);
				mCustomDialogTextNoComment.setText("No Comments");
				mCustomDialogTextNoComment.setGravity(Gravity.CENTER);
			} else {
				if(result.size()==0)
				{   
					mCustomDialogListView.setVisibility(View.GONE);
					mCustomDialogTextNoComment.setVisibility(View.VISIBLE);
					mCustomDialogTextNoComment.setText("No Comments");
				} 
				if(result.size()>0){

					mCustomDialogListView.setVisibility(View.VISIBLE);
					mCustomDialogTextNoComment.setVisibility(View.GONE);
					mCommentListAdapter = new CommentListAdapter(prova,result);
					mCustomDialogListView.setAdapter(mCommentListAdapter);
				}
			}
		}
	}

	//Async class for adding the comment
	class PostAddComment extends AsyncTask<Void,Void,JSONObject>{
		JSONObject receivedJSONResponse;
		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(prova);
				mPDialog.show();
			}else
			{ mPDialog.show();}
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject postJSONObject = new JSONObject();
			try {
				postJSONObject.put("commentType",mCommentType);
				postJSONObject.put("originalPostId",mOriginalPostId);
				postJSONObject.put("comment_text",mCommentText);
				postJSONObject.put("org_post_owner_id",mOrgPostOwnerId);
				postJSONObject.put("id",mId);

				receivedJSONResponse = HttpPostClient.sendHttpPost(POST_ADD_COMMENT_API,postJSONObject);
				if(receivedJSONResponse!=null){
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return receivedJSONResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result){
			mPDialog.dismiss();
			super.onPostExecute(result);
			try {
				if(result != null){
					if(result.getString("status").equalsIgnoreCase("1")){
						CommentListModel newCommentModel = new CommentListModel();
						newCommentModel.setCommentText(mCommentText);
						newCommentModel.setCommentId(result.getString("comment_id"));
						newCommentModel.setPostId(mOriginalPostId);
						newCommentModel.setOrgPostOwnerId(mOrgPostOwnerId);
						newCommentModel.setFirstName(Util.FIRSTNAME);
						newCommentModel.setCommentOwnerId(Util.USER_ID);
						newCommentModel.setUserProfilePhoto(Util.NM_PROFILE_PIC);

						if(mCommentListAdapter == null){
							ConnectionDetector conn = new ConnectionDetector(prova);
							if(conn.isConnectingToInternet()){
								commentListArray = new ArrayList<CommentListModel>();
								commentListArray.add(newCommentModel);
								new GetCommentList().execute(mOriginalPostId);
							} else {
								alert.showAlertDialog(prova,"Connection Error","Check your Internet Connection",false);
							}
						}	
						else if(mCommentListAdapter.getCount()==0) {
							ConnectionDetector conn = new ConnectionDetector(prova);
							if(conn.isConnectingToInternet()){
								commentListArray = new ArrayList<CommentListModel>();
								commentListArray.add(newCommentModel);
								new GetCommentList().execute(mOriginalPostId);
							} else {
								alert.showAlertDialog(prova,"Connection Error","Check your Internet Connection",false);
							}
						} else {
							commentListArray.add(newCommentModel);
							mCommentListAdapter.notifyDataSetChanged();}
					}	
					mCustomDialogAddComment.getText().clear();
					Toast.makeText(prova, result.getString("message"),Toast.LENGTH_LONG).show();
				}

			}catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class HidePost extends AsyncTask<Void,Void,JSONObject>{
		private JSONObject mReceivedHidePostJSONResponse;
		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(prova);
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject hidePostJSONObject = new JSONObject();
			try {
				hidePostJSONObject.put("track_id", mTrackId);
				hidePostJSONObject.put("post_id", mOriginalPostId);
				if(mHidePost.equalsIgnoreCase("1")){
					hidePostJSONObject.put("hide", mHidePost);
				}
				if(mOffensivePost.equalsIgnoreCase("1")){
					hidePostJSONObject.put("offensive_content", mOffensivePost);
				}
				if(mHarassment.equalsIgnoreCase("1")){
					hidePostJSONObject.put("harassment", mHarassment);
				}
				if(mSpam.equalsIgnoreCase("1")){
					hidePostJSONObject.put("spam", mSpam);
				}
				mReceivedHidePostJSONResponse = HttpPostClient.sendHttpPost(HIDE_POST_API, hidePostJSONObject);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return mReceivedHidePostJSONResponse;
		}

		@Override
		protected void onPostExecute(JSONObject result){
			mPDialog.dismiss();
			if(result!=null){
				try {
					if(result.getString("status").equalsIgnoreCase("1")){
						Toast.makeText(prova, result.getString("message"),Toast.LENGTH_LONG).show();
						mDialogHidePost.dismiss();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class PostShare extends AsyncTask<Void,Void,JSONObject>{
		JSONObject receivedJSONResponse ;

		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(prova);
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
				sendSharePostJSONObject.put("share_post_id",mOriginalPostId);
				sendSharePostJSONObject.put("status_update_text",mStatusUpdateText);
				if(mShareText.equalsIgnoreCase("true")){
					trackType = "text_share";
				}
				if(mSharePhoto.equalsIgnoreCase("true")){
					sendSharePostJSONObject.put("photo_id",mSharePhotoId);
					trackType="photo_share";
				}
				if(mShareVideo.equalsIgnoreCase("true")){
					sendSharePostJSONObject.put("video_id", mShareVideoId);
					trackType = "video_share";
				}

				sendSharePostJSONObject.put("org_post_owner_id",org_post_owner_id_share);
				sendSharePostJSONObject.put("privacy_type",privacy_type_share);

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
					Toast.makeText(prova, result.getString("message"),Toast.LENGTH_LONG).show();
					mDialogSharePost.dismiss();
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
				mPDialog = Util.createProgressDialog(prova);
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
			pos=params[4];

			try {
				jsonObjSend.put("likeType",likeType);
				jsonObjSend.put("post_id",postID);
				jsonObjSend.put("id",Id);
				jsonObjSend.put("org_post_owner_id",ownerID);
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
							listData.get(Integer.parseInt(pos)).setUser_liked(result.getString("like_id"));
						}
						else
						{
							listData.get(Integer.parseInt(pos)).setUser_liked("0");

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// view more & less
	public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

		if (tv.getTag() == null) {
			tv.setTag(tv.getText());
		}
		ViewTreeObserver vto = tv.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {

				ViewTreeObserver obs = tv.getViewTreeObserver();
				obs.removeGlobalOnLayoutListener(this);
				if (maxLine == 0) {
					int lineEndIndex = tv.getLayout().getLineEnd(0);
					String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
					tv.setText(text);
					tv.setMovementMethod(LinkMovementMethod.getInstance());
					tv.setText(
							addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
									viewMore), BufferType.SPANNABLE);
				} else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
					int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
					String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
					tv.setText(text);
					tv.setMovementMethod(LinkMovementMethod.getInstance());
					tv.setText(
							addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
									viewMore), BufferType.SPANNABLE);
				} else {
					int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
					String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
					tv.setText(text);
					tv.setMovementMethod(LinkMovementMethod.getInstance());
					tv.setText(
							addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
									viewMore), BufferType.SPANNABLE);
				}
			}
		});

	}

	private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
			final int maxLine, final String spanableText, final boolean viewMore) {
		String str = strSpanned.toString();
		SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

		if (str.contains(spanableText)) {
			ssb.setSpan(new ClickableSpan() {

				@Override
				public void onClick(View widget) {

					if (viewMore) {
						tv.setLayoutParams(tv.getLayoutParams());
						tv.setText(tv.getTag().toString(), BufferType.SPANNABLE);
						tv.invalidate();
						makeTextViewResizable(tv, -1, "View Less", false);
					} else {
						tv.setLayoutParams(tv.getLayoutParams());
						tv.setText(tv.getTag().toString(), BufferType.SPANNABLE);
						tv.invalidate();
						makeTextViewResizable(tv, 3, "View More", true);
					}

				}
			}, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

		}
		return ssb;

	}



}



