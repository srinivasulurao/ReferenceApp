package com.youflik.youflik.thirdPartyProfileView;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;
import com.youflik.youflik.RestrictedPage;
import com.youflik.youflik.ViewPhoto;
import com.youflik.youflik.chat.ChatMessagingActivity;
import com.youflik.youflik.chat.ChatOthersConversationActivity;
import com.youflik.youflik.commonAdapters.ThirdPartyTimelog;
import com.youflik.youflik.commonAdapters.TopFansAdapter;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.ConversationsModel;
import com.youflik.youflik.models.MyPojo;
import com.youflik.youflik.models.SearchModel;
import com.youflik.youflik.models.Timefeeds;
import com.youflik.youflik.models.TopFanModel;
import com.youflik.youflik.models.UserDetailsModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.proxy.HttpPutClient;
import com.youflik.youflik.statusUpdate.UserStatusUpdate;
import com.youflik.youflik.utils.ListUtility;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ThirdPartyFragmentProfileView extends Fragment {
	TextView bio,friend_count,follower_count,website,location;
	static TextView song;
	TextView topLabel;
	ImageView cover,profile;
	GridView topfans;
	public ImageLoader imageLoader;
	DisplayImageOptions profile_options,cover_option;
	private ProgressDialog pDialog,PDialog;
	private String Song_URL;
	private ArrayList<TopFanModel> topFanSearch = new ArrayList<TopFanModel>();
	private TopFansAdapter adapter;
	private String friedID;
	private Button post,message,load_more;
	private static String is_friend,jid;
	private static String is_frnd,is_accept,is_pending;
	private String profile_like_count,profile_comment_count,profile_share_count,cover_like_count,cover_comment_count,cover_share_count,profile_post_id,
	profile_photo_id,cover_post_id,cover_photo;
	private String FeedURL ;
	MyPojo feed;
	private ArrayList<Timefeeds> feedSearch = new ArrayList<Timefeeds>();
	private static String pagination_Date_String;
	private ThirdPartyTimelog timelog_adapter;
	private ListView feedListView;
	private ImageView status;
	private AlertDialog mConnectAlert=null; 
	private int mConnectFlag;
	private AlertDialog.Builder mCheckConnectAlert;
	private ProgressDialog mPDialog;
	JSONObject userprofile_info_Details;

	//variables for checkConnect status of a user
	private static final String SEND_FRIEND_REQUEST_API =Util.API+"friend_request";
	private static String UNFRIEND_API =Util.API+"unfriend/";
	private static String CANCEL_PENDING_FRIEND_REQUEST_API=Util.API+"cancel_pending_friend_request/";
	private static String ACCEPT_REJECT_FRIEND_REQUEST_API = Util.API+"friend_request/";

	private String mFriendRequestFromUser,mFriendRequestToUser,mAcceptRejectStatus;
	private String mUnFriendUserId,mCancelPendingFriendRequestId,mAcceptRejectFriendRequestId;

	private int returnConversationsID;
	private String othersUserName,OthersFirstName,OthersSecondName,othersImage;
	private Handler SongHandler = new Handler();
	private String status_404;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_thirdparty_profile_view, container, false);
		Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(),"fonts/BentonSans-Regular.otf");

		bio=(TextView)rootView.findViewById(R.id.thirdParty_bio);
		bio.setTypeface(typeFace);
		friend_count=(TextView)rootView.findViewById(R.id.thirdParty_friend_count);
		follower_count=(TextView)rootView.findViewById(R.id.thirdParty_follow_count);
		website=(TextView)rootView.findViewById(R.id.thirdParty_website_blog);
		website.setTypeface(typeFace);
		location=(TextView)rootView.findViewById(R.id.thirdParty_location);
		location.setTypeface(typeFace);
		song=(TextView)rootView.findViewById(R.id.thirdParty_song_url);
		song.setTypeface(typeFace);
		cover=(ImageView)rootView.findViewById(R.id.thirdParty_cover_pic);
		profile=(ImageView)rootView.findViewById(R.id.thirdPartyprofilepic);
		topLabel=(TextView)rootView.findViewById(R.id.thirdPartytoplabel);
		topfans=(GridView)rootView.findViewById(R.id.thirdParty_topfans);
		post=(Button)rootView.findViewById(R.id.thirdPartyPost);
		message=(Button)rootView.findViewById(R.id.thirdPartyMessage);
		feedListView = (ListView) rootView.findViewById(R.id.ThirdParty_TimeLogfeedListview);
		load_more=(Button)rootView.findViewById(R.id.ThirdParty_load_more_timelog);
		status=(ImageView)rootView.findViewById(R.id.friend_status);
		ThirdPartyUserDetailActivity thirdParty = (ThirdPartyUserDetailActivity) getActivity();
		friedID = thirdParty.FriendID;
		FeedURL = Util.API + "timelog?user_id=" + friedID;
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		topFanSearch.clear();
		imageLoader =imageLoader.getInstance();

		profile_options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(false)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

		cover_option = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder_timefeed)
		.showImageForEmptyUri(R.drawable.placeholder_timefeed) 
		.showImageOnFail(R.drawable.placeholder_timefeed)
		.cacheInMemory(true)
		.cacheOnDisc(false)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		ConnectionDetector conn = new ConnectionDetector(getActivity());
		if(conn.isConnectingToInternet())
		{
			new UserProfileInfoFragmentAsync().execute();	
			//new UserTopFanAsync().execute();
		}
		else 
		{
			Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();

		}

		song.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SongHandler.post(new Runnable() {
					public void run() { 
						//play the song
						try {
							Util.mediaPlayer.setDataSource(Song_URL); 
							Util.mediaPlayer.prepare();
						} catch (Exception e) {
							e.printStackTrace();
						}

						if(Util.mediaPlayer.isPlaying()){
							Util.mediaPlayer.pause();
							song.setSelected(false);
							song.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_play_song, 0, 0, 0);
						}else {
							Util.mediaPlayer.start();
							song.setSelected(true);
							song.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pause_song, 0, 0, 0);

						}
						//end
					}
				});
			}
		});

		website.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				website.setSelected(true);
			}
		});
		location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				location.setSelected(true);		
			}
		});
		friend_count.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "Followers", Toast.LENGTH_SHORT).show();
			}
		});
		follower_count.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "Following", Toast.LENGTH_SHORT).show();
			}
		});
		post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),UserStatusUpdate.class);
				//intent.putExtra("StatusUpdateType", "User");
				intent.putExtra("StatusUpdateType", "ThirdPartyUser");
				startActivity(intent);
			}
		});

		message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(is_frnd.equalsIgnoreCase("1")){
					// XMPP Chat
					DataBaseHandler datebasehandler = new DataBaseHandler(getActivity());
					/*String[] from_User_Full_Name = fromName.split("@");
					String from_User_Name = from_User_Full_Name[0];*/
					ConversationsModel checkConversations = new ConversationsModel();
					checkConversations = datebasehandler.checkConversationID(jid);

					if(checkConversations == null){
						ConversationsModel conversions= new ConversationsModel();
						conversions.setLast_message("");
						conversions.setEnd_time("endtime");
						conversions.setLast_message_direction("in");
						conversions.setLogin_user_display_name(Util.USERNAME);
						conversions.setLogin_user_id(Integer.parseInt(Util.USER_ID));
						conversions.setLogin_user_jid(Util.CHAT_LOGIN_JID);
						conversions.setLogin_user_resource("mobile");
						conversions.setWith_user_display_name(jid);
						conversions.setWith_user_id(Integer.parseInt("0"));
						conversions.setWith_user_jid(jid);
						conversions.setWith_user_profilepicurl("message.getFrom()");
						conversions.setWith_user_resource("mobile");
						conversions.setStart_time("starttime");
						conversions.setMessage_iseen("yes");
						conversions.setMessage_isseen_count("0");
						datebasehandler.insertConversions(conversions);

						checkConversations = datebasehandler.checkConversationID(jid);
						if(checkConversations == null){

						}else{
							returnConversationsID = checkConversations.getConversation_id();
						}
						//
						//getting the latest conversation id
					}else{
						returnConversationsID = checkConversations.getConversation_id();
					}
					int conId = returnConversationsID;
					Intent intent = new Intent(getActivity(), ChatMessagingActivity.class);
					intent.putExtra("Connversation_ID", conId);
					intent.putExtra("Connversation_JID", jid);
					startActivity(intent);
				}else{
					// Others Chat

					Intent chatIntent = new Intent(getActivity(),ChatOthersConversationActivity.class);
					chatIntent.putExtra("username",othersUserName);
					chatIntent.putExtra("firstname",OthersFirstName);
					chatIntent.putExtra("lastname","");
					chatIntent.putExtra("jid", jid);
					chatIntent.putExtra("profile_image_path",othersImage);
					startActivity(chatIntent); 

				}

			}
		});

		profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(getActivity(), ViewPhoto.class);
					intent.putExtra("ImagePath",userprofile_info_Details.getString("user_profile_photo_path"));
					intent.putExtra("photo_id",userprofile_info_Details.getString("profile_photo_id"));
					intent.putExtra("photo_like_count",userprofile_info_Details.getString("profile_photo_like_count"));
					intent.putExtra("photo_comment_count",userprofile_info_Details.getString("profile_photo_comment_count"));
					intent.putExtra("photo_share_count",userprofile_info_Details.getString("profile_photo_share_count"));
					startActivity(intent); 
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		cover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(getActivity(), ViewPhoto.class);
					intent.putExtra("ImagePath",userprofile_info_Details.getString("user_cover_photo_path"));
					intent.putExtra("photo_id",userprofile_info_Details.getString("cover_photo_id"));
					intent.putExtra("photo_like_count",userprofile_info_Details.getString("cover_photo_like_count"));
					intent.putExtra("photo_comment_count",userprofile_info_Details.getString("cover_photo_comment_count"));
					intent.putExtra("photo_share_count",userprofile_info_Details.getString("cover_photo_share_count"));
					startActivity(intent); 	
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

		load_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imageLoader.clearMemoryCache();
				imageLoader.clearDiscCache();
				load_more.setVisibility(View.GONE);
				new GetFeedLoadMoreAsyncClass().execute();				
			}
		});

		status.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(is_frnd.equalsIgnoreCase("1"))
				{
					mConnectFlag=1;
					mUnFriendUserId = friedID;
					mConnectAlert = getCheckConnectAlert("Do you want to unfriend?");
					mConnectAlert.show();
				}
				else
				{
					if (is_pending.equalsIgnoreCase("1")) {
						mConnectFlag=3;
						mConnectAlert = getCheckConnectAlert("Do you want to cancel friend request?");
						mConnectAlert.show();
					}
					else if(is_accept.equalsIgnoreCase("1")){
						mConnectFlag =2;
						mFriendRequestFromUser = friedID;
						mConnectAlert = getCheckConnectAlert("Do you want to accept friend request");
						mConnectAlert.show();
					}
					else
					{
						mConnectFlag =4;
						mFriendRequestToUser = friedID;
						mConnectAlert = getCheckConnectAlert("Do you want to send a friend request?");
						mConnectAlert.show();	
					}
				}
			}
		});		

	}

	private class UserProfileInfoFragmentAsync extends AsyncTask<Void, Void, JSONObject>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(getActivity());
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}


		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"user/"+friedID);
			return jsonObjectRecived;
		}

		@SuppressWarnings("static-access")
		@Override
		protected void onPostExecute(JSONObject app) {
			// TODO Auto-generated method stub
			super.onPostExecute(app);
			pDialog.dismiss();
			JSONArray UserProfileInfoModelResponse = null;
			//JSONObject userprofile_info_Details;
			try {
				if(app.getString("status").equalsIgnoreCase("1"))
				{
					ConnectionDetector conn = new ConnectionDetector(getActivity());
					if(conn.isConnectingToInternet())
					{
						topFanSearch.clear();
						new UserTopFanAsync().execute();
					}
					else 
					{
						Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();

					}

					UserProfileInfoModelResponse = app.getJSONArray("userDetails");  

					if(UserProfileInfoModelResponse.length() > 0){
						userprofile_info_Details = UserProfileInfoModelResponse.getJSONObject(0);
						Song_URL=userprofile_info_Details.getString("actual_song_path");
						is_accept=userprofile_info_Details.getString("is_accept_friend_request");
						is_pending=userprofile_info_Details.getString("is_pending_friend_request");
						is_frnd=userprofile_info_Details.getString("is_friend");
						Util.IsFriend=userprofile_info_Details.getString("is_friend");
						//new 
						othersUserName = userprofile_info_Details.getString("username");
						OthersFirstName = userprofile_info_Details.getString("firstname");
						othersImage = userprofile_info_Details.getString("user_profile_photo_path");
						jid = userprofile_info_Details.getString("jid");
						if(is_accept.equalsIgnoreCase("1"))
						{
							mAcceptRejectFriendRequestId=userprofile_info_Details.getString("accept_friend_request_id");
						}
						if(is_pending.equalsIgnoreCase("1"))
						{
							mCancelPendingFriendRequestId=userprofile_info_Details.getString("pending_friend_request_id");
						}
						if(userprofile_info_Details.getString("bio").equalsIgnoreCase("null") || userprofile_info_Details.getString("bio").equalsIgnoreCase("")){
							bio.setText("NA");}
						else{	bio.setText(userprofile_info_Details.getString("bio"));}

						if(userprofile_info_Details.getString("fried_count").equalsIgnoreCase("null") || userprofile_info_Details.getString("fried_count").equalsIgnoreCase("")){
							friend_count.setText("0");}
						else{friend_count.setText(userprofile_info_Details.getString("fried_count"));}

						if(userprofile_info_Details.getString("follower_count").equalsIgnoreCase("null") || userprofile_info_Details.getString("follower_count").equalsIgnoreCase("")){
							follower_count.setText("0");}
						else{follower_count.setText(userprofile_info_Details.getString("follower_count"));}


						if(userprofile_info_Details.getString("current_city").equalsIgnoreCase("null") || userprofile_info_Details.getString("current_city").equalsIgnoreCase("")){
							location.setText("NA");}
						else{location.setText(userprofile_info_Details.getString("current_city"));}

						if(userprofile_info_Details.getString("website_blog").equalsIgnoreCase("null") || userprofile_info_Details.getString("website_blog").equalsIgnoreCase("")){
							website.setText("NA");}
						else{website.setText(userprofile_info_Details.getString("website_blog"));}

						if(userprofile_info_Details.getString("song_title").equalsIgnoreCase("null") || userprofile_info_Details.getString("song_title").equalsIgnoreCase("")){
							song.setText("NA");}
						else{song.setText(userprofile_info_Details.getString("song_title"));}


						if(userprofile_info_Details.getString("is_friend").equalsIgnoreCase("1"))
						{
							is_friend="1";
							post.setVisibility(View.VISIBLE);
							message.setVisibility(View.VISIBLE);
						}
						else if (userprofile_info_Details.getString("is_friend").equalsIgnoreCase("0")) {
							is_friend="0";
							post.setVisibility(View.GONE);
							message.setVisibility(View.VISIBLE);

						}

						//setting proflie pic
						imageLoader.displayImage(userprofile_info_Details.getString("user_profile_photo_path"), profile, profile_options, new SimpleImageLoadingListener() {
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
								);// end setting profile pic
						//setting cover pic
						imageLoader.displayImage(userprofile_info_Details.getString("user_cover_photo_path"),cover, cover_option, new SimpleImageLoadingListener() {
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

						// status
						// check for connected,pending,accept friend request
						// is friend
						if(is_frnd.equalsIgnoreCase("1")){
							status.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.unfriend));

						}

						else
						{
							// got a friend request 
							if(is_accept.equalsIgnoreCase("1")){
								status.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.accept));

							}

							// cancel a pending friend request
							else if(is_pending.equalsIgnoreCase("1")){

								status.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pending));

							}

							// to connect 
							else {
								status.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.addfriend));

							}
						}

					}
				}
				else
				{
					getActivity().finish();
					Intent error_page= new Intent(getActivity(),RestrictedPage.class);
					startActivity(error_page);
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}

		}

	}

	private class UserTopFanAsync extends AsyncTask<Void, Void, ArrayList<TopFanModel>>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(getActivity());
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected ArrayList<TopFanModel> doInBackground(
				Void... params) {
			// TODO Auto-generated method stub
			JSONArray userTopFanResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"user/"+friedID);

			if(jsonObjectRecived != null){

				try{
					if(jsonObjectRecived.getString("status").equalsIgnoreCase("0")){
						return null;
					}
					userTopFanResponse = jsonObjectRecived.getJSONArray("topFans");
				}catch(JSONException e){
					e.printStackTrace();
				}

				if(userTopFanResponse.length() > 0){
					for(int i = 0; i< userTopFanResponse.length();i++){
						TopFanModel photosData = new TopFanModel();
						JSONObject photoDetails;
						try{
							photoDetails = userTopFanResponse.getJSONObject(i);
							photosData.setUser_two(photoDetails.getString("user_two"));
							photosData.setTotalCount(photoDetails.getString("totalCount"));
							photosData.setUsername(photoDetails.getString("username"));
							photosData.setFirstname(photoDetails.getString("firstname"));
							photosData.setTopfanphoto(photoDetails.getString("topfanphoto"));
							topFanSearch.add(photosData);
						}catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					return topFanSearch;
				}else{
					return null;
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<TopFanModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result == null){
				topfans.setVisibility(View.GONE);
				topLabel.setVisibility(View.GONE);
			}else if(result.size()==0){
				topfans.setVisibility(View.GONE);
				topLabel.setVisibility(View.GONE);

			}  else{
				adapter = new TopFansAdapter(getActivity(), result);
				topfans.setAdapter(adapter);

			}

			timelog_adapter = new ThirdPartyTimelog(getActivity(), feedSearch);
			feedSearch.clear();
			ConnectionDetector conn = new ConnectionDetector(getActivity());
			if(conn.isConnectingToInternet())
			{		
				new GetFeedAsyncClass().execute();		
			}
			else{
				Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
			}
		}
	}

	//TIMELOG
	private class GetFeedAsyncClass extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			if(PDialog == null){
				PDialog = Util.createProgressDialog(getActivity());
				PDialog.setCancelable(false);
				PDialog.show();}
			else{
				PDialog.setCancelable(false);
				PDialog.show();
			}}

		@Override
		protected Void doInBackground(Void... params) {
			JSONArray feedResponse = null;
			JSONObject jsonObjectRecived;
			jsonObjectRecived = HttpGetClient.sendHttpPost(FeedURL);

			if(jsonObjectRecived != null){
				feed=new Gson().fromJson(jsonObjectRecived.toString(),MyPojo.class);
				for(int i=0 ; i < feed.getTimefeeds().length ; i++)
				{
					Timefeeds timefeedmodel = new Timefeeds();
					timefeedmodel = feed.getTimefeeds()[i];
					pagination_Date_String=feed.getTimefeeds()[i].getTrack_time();
					feedSearch.add(timefeedmodel);
					/*					if(!flag_refresh){
						flag_refresh = true;
						refresh_Date_String =feed.getTimefeeds()[i].getTrack_time();
					}*/
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if(feedSearch == null)
			{
				//Toast.makeText(getActivity(), "No feeds to load", Toast.LENGTH_SHORT).show();

			} 
			else if(feedSearch.size()==0)
			{
				//Toast.makeText(getActivity(), "No feeds to load", Toast.LENGTH_SHORT).show();

			}      
			else
			{
				feedListView.setAdapter(timelog_adapter);		
				load_more.setVisibility(View.VISIBLE);
				ListUtility.setListViewHeightBasedOnChildren(feedListView);

			}
			PDialog.dismiss();
		}

	}

	private class GetFeedLoadMoreAsyncClass extends AsyncTask<Void, Void, ArrayList<Timefeeds>>{

		@Override
		protected void onPreExecute() {
			if(PDialog == null){
				PDialog = Util.createProgressDialog(getActivity());
				PDialog.setCancelable(false);
				PDialog.show();}
			else{
				PDialog.setCancelable(false);
				PDialog.show();
			}}

		@Override
		protected ArrayList<Timefeeds> doInBackground(Void... params) {
			JSONArray feedResponse = null;
			JSONObject jsonObjectRecived_loadMore;

			jsonObjectRecived_loadMore = HttpGetClient.sendHttpPost(FeedURL+"&last_date="+ pagination_Date_String.replaceAll(" ", "%20"));
			if(jsonObjectRecived_loadMore != null){
				feed=new Gson().fromJson(jsonObjectRecived_loadMore.toString(),MyPojo.class);
				if(feed.getTimefeeds().length > 0){
					for(int i=0 ; i < feed.getTimefeeds().length ; i++)
					{
						Timefeeds timefeedmodel = new Timefeeds();
						timefeedmodel = feed.getTimefeeds()[i];
						pagination_Date_String=feed.getTimefeeds()[i].getTrack_time();
						feedSearch.add(timefeedmodel);

					}
					return feedSearch;
				}
				else
				{
					return null;
				}
			}
			else
			{
				return null;
			}



		}

		@Override
		protected void onPostExecute(ArrayList<Timefeeds> result) {

			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result == null){
				if(timelog_adapter.isEmpty()){
					//flag_loading = false;
					Toast.makeText(getActivity(), "No feeds to load", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getActivity(), "No feeds to load", Toast.LENGTH_SHORT).show();
				}
				//flag_loading = true;
			}else if(result.size()==0){
				if(timelog_adapter.isEmpty()){
					Toast.makeText(getActivity(), "No feeds to load", Toast.LENGTH_SHORT).show();
				}
				//flag_loading = true;
				Toast.makeText(getActivity(), "No feeds to load", Toast.LENGTH_SHORT).show();
			}      else{
				timelog_adapter.notifyDataSetChanged();
				ListUtility.setListViewHeightBasedOnChildren(feedListView);
				load_more.setVisibility(View.VISIBLE);

			}
			PDialog.dismiss(); 

		}

	}

	// dialog for different connect
	public AlertDialog getCheckConnectAlert(String message){

		mCheckConnectAlert = new AlertDialog.Builder(getActivity());
		//mCheckConnectAlert.setTitle("Connect");
		mCheckConnectAlert.setMessage(message);
		if(mConnectFlag==2){
			mCheckConnectAlert.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mAcceptRejectStatus="1";
					new PutAcceptRejectFriendRequest().execute(mAcceptRejectFriendRequestId);
					dialog.dismiss();
				}
			});
			mCheckConnectAlert.setNegativeButton("REJECT",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mAcceptRejectStatus="0";
					new PutAcceptRejectFriendRequest().execute(mAcceptRejectFriendRequestId);
					dialog.dismiss();
				}
			});
		} else {
			mCheckConnectAlert.setPositiveButton("YES",new DialogInterface.OnClickListener()
			{ 
				@Override
				public void onClick(DialogInterface dialog, int which) {

					switch(mConnectFlag){
					case 1:
						new PostUnfriend().execute(mUnFriendUserId);
						break;
					case 3:
						new PostCancelPendingFriendRequest().execute(mCancelPendingFriendRequestId);
						break;
					case 4:
						new PostSendFriendRequest().execute();
						break;
					default:
						break;	
					}
					dialog.dismiss()	;}
			});
			mCheckConnectAlert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		mConnectAlert = mCheckConnectAlert.create();
		return mConnectAlert;
	}

	//async to unfriend a connected friend
	private class PostUnfriend extends AsyncTask<String,Void,JSONObject>{
		private JSONObject receivedUnfriendJSONResponse;
		@Override
		protected void onPreExecute(){
			if(mPDialog==null){
				mPDialog = Util.createProgressDialog(getActivity()); 
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			String userId = params[0];
			JSONObject unfriendSendJSONObject = new JSONObject(); 
			receivedUnfriendJSONResponse = HttpPostClient.sendHttpPost(UNFRIEND_API+userId, unfriendSendJSONObject);
			return receivedUnfriendJSONResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result){
			mPDialog.dismiss();
			try {
				if(result!=null){
					if(result.getString("status").equalsIgnoreCase("1")){
						status.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.addfriend));
						is_frnd="0";
						is_pending="0";
						is_accept="0";
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet())
						{
							new UserProfileInfoFragmentAsync().execute();	

							//new UserTopFanAsync().execute();

							//new GetFeedAsyncClass().execute();	
						}
						else 
						{
							Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();

						}

					}
					Toast.makeText(getActivity(),result.getString("message"),Toast.LENGTH_LONG).show();
				}
			}catch (JSONException e) { 
				e.printStackTrace();
			}
		}

	}

	// async to accept reject friend request
	private class PutAcceptRejectFriendRequest extends AsyncTask<String,Void,JSONObject>{
		private JSONObject receivedAcceptRejectJSONResponse;
		@Override
		protected void onPreExecute(){
			if(mPDialog==null){
				mPDialog = Util.createProgressDialog(getActivity()); 
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			String acceptRejectFriendRequestId = params[0];
			JSONObject acceptRejectSendJSONObject = new JSONObject();
			try {
				acceptRejectSendJSONObject.put("friend_req_send_user",mFriendRequestFromUser);
				acceptRejectSendJSONObject.put("accept_reject_status",mAcceptRejectStatus);

				receivedAcceptRejectJSONResponse = HttpPutClient.sendHttpPost(ACCEPT_REJECT_FRIEND_REQUEST_API+acceptRejectFriendRequestId,acceptRejectSendJSONObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return receivedAcceptRejectJSONResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result){
			mPDialog.dismiss();
			try {
				if(result!=null){
					if(result.getString("status").equalsIgnoreCase("1")){
						if(mAcceptRejectStatus.equalsIgnoreCase("0")){
							status.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.addfriend));
							is_frnd="0";
							is_pending="0";
							is_accept="0";

							ConnectionDetector conn = new ConnectionDetector(getActivity());
							if(conn.isConnectingToInternet())
							{
								new UserProfileInfoFragmentAsync().execute();	

								//new UserTopFanAsync().execute();

								//new GetFeedAsyncClass().execute();	
							}
							else 
							{
								Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();

							}


						} else {
							status.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.unfriend));
							is_frnd="1";
							ConnectionDetector conn = new ConnectionDetector(getActivity());
							if(conn.isConnectingToInternet())
							{
								new UserProfileInfoFragmentAsync().execute();	

								//new UserTopFanAsync().execute();

								//new GetFeedAsyncClass().execute();	
							}
							else 
							{
								Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();

							}

						}

					}
					Toast.makeText(getActivity(),result.getString("message"),Toast.LENGTH_LONG).show();
				}
			}catch (JSONException e) { 
				e.printStackTrace();
			}
		}

	}

	// async to cancel the pending friend request
	private class PostCancelPendingFriendRequest extends AsyncTask<String,Void,JSONObject>{
		private JSONObject receivedCancelPendingRequestJSONResponse;
		@Override
		protected void onPreExecute(){
			if(mPDialog==null){
				mPDialog = Util.createProgressDialog(getActivity()); 
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			//String pendingFriendRequestId = params[0];
			JSONObject sendJSONObject = new JSONObject(); 
			receivedCancelPendingRequestJSONResponse = HttpPostClient.sendHttpPost(CANCEL_PENDING_FRIEND_REQUEST_API+mCancelPendingFriendRequestId, sendJSONObject);
			return receivedCancelPendingRequestJSONResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result){
			mPDialog.dismiss();
			try {
				if(result!=null){
					if(result.getString("status").equalsIgnoreCase("1")){
						status.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.addfriend));
						is_frnd="0";
						is_pending="0";
						is_accept="0";
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet())
						{
							new UserProfileInfoFragmentAsync().execute();	

							//	new UserTopFanAsync().execute();

							//new GetFeedAsyncClass().execute();	
						}
						else 
						{
							Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();

						}


					}
					Toast.makeText(getActivity(),result.getString("message"),Toast.LENGTH_LONG).show();
				}
			}catch (JSONException e) { 
				e.printStackTrace();
			}
		}
	}

	// async for sending friend request
	private class PostSendFriendRequest extends AsyncTask<Void,Void,JSONObject>{
		private JSONObject receivedSendFriendRequestJSONResponse;
		@Override
		protected void onPreExecute(){
			if(mPDialog==null){
				mPDialog = Util.createProgressDialog(getActivity()); 
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject sendJSONObject = new JSONObject(); 
			try {
				sendJSONObject.put("user_two",mFriendRequestToUser);
				receivedSendFriendRequestJSONResponse = HttpPostClient.sendHttpPost(SEND_FRIEND_REQUEST_API, sendJSONObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return receivedSendFriendRequestJSONResponse;
		}
		@Override
		protected void onPostExecute(JSONObject result){
			mPDialog.dismiss();
			try {
				if(result!=null){
					if(result.getString("status").equalsIgnoreCase("1")){
						status.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pending));
						is_frnd="0";
						is_pending="1";
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet())
						{
							new UserProfileInfoFragmentAsync().execute();	

							//	new UserTopFanAsync().execute();

							//new GetFeedAsyncClass().execute();	
						}
						else 
						{
							Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();

						}

					}
					Toast.makeText(getActivity(),result.getString("message"),Toast.LENGTH_LONG).show();
				}
			}catch (JSONException e) { 
				e.printStackTrace();
			}
		}
	}
}
