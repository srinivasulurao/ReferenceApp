package com.youflik.youflik.userprofile;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;
import com.youflik.youflik.ViewPhoto;
import com.youflik.youflik.commonAdapters.TimelogAdapter;
import com.youflik.youflik.commonAdapters.TopFansAdapter;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.MyPojo;
import com.youflik.youflik.models.Timefeeds;
import com.youflik.youflik.models.TopFanModel;
import com.youflik.youflik.models.UserDetailsModel;
import com.youflik.youflik.profileUpdate.UserProfileUpdate;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.utils.ListUtility;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FragmentProfileView extends Fragment {
	TextView bio,friend_count,follower_count,website,location;
	static TextView song;
	TextView topLabel;
	TextView reload;
	ImageView cover,profile;
	GridView topfans;
	ScrollView scrollview;
	public ImageLoader imageLoader;
	DisplayImageOptions profile_options,cover_option;
	private ArrayList<TopFanModel> topFanSearch = new ArrayList<TopFanModel>();
	private ProgressDialog pDialog;
	private TopFansAdapter adapter;
	private String Song_URL;
	private Button edit,load_more;
	//DECLARATION FOR TIMELOG
	private ListView feedListView;
	JSONArray feedResponse;
	private String FeedURL = Util.API + "timelog?user_id=" + Util.USER_ID;
	private ArrayList<Timefeeds> feedSearch = new ArrayList<Timefeeds>();
	private static String pagination_Date_String;
	private TimelogAdapter timelog_adapter;
	UserDetailsModel userDet= new UserDetailsModel();
	private static final int UPDATE_RESULT = 0x4;
	private Boolean updateFlag=false;
	private String updateF;
	private String profile_like_count,profile_comment_count,profile_share_count,cover_like_count,cover_comment_count,cover_share_count,profile_post_id,
	profile_photo_id,cover_post_id,cover_photo_id;
	public Handler SongHandler = new Handler();

	MyPojo feed;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
		Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(),"fonts/BentonSans-Regular.otf");

		bio=(TextView)rootView.findViewById(R.id.user_bio);
		bio.setTypeface(typeFace);
		friend_count=(TextView)rootView.findViewById(R.id.user_friend_count);
		follower_count=(TextView)rootView.findViewById(R.id.user_follow_count);
		website=(TextView)rootView.findViewById(R.id.user_website_blog);
		website.setTypeface(typeFace);
		location=(TextView)rootView.findViewById(R.id.user_location);
		location.setTypeface(typeFace);
		song=(TextView)rootView.findViewById(R.id.user_song_url);
		song.setTypeface(typeFace);
		topLabel=(TextView)rootView.findViewById(R.id.toplabel);
		topfans=(GridView)rootView.findViewById(R.id.user_topfans);
		cover=(ImageView)rootView.findViewById(R.id.user_cover_pic);
		profile=(ImageView)rootView.findViewById(R.id.profilepic);
		edit=(Button)rootView.findViewById(R.id.edit);
		//TIMELOG
		feedListView = (ListView) rootView.findViewById(R.id.TimeLogfeedListview);
		scrollview=(ScrollView)rootView.findViewById(R.id.userdetailsscrollViewpage);
		load_more=(Button)rootView.findViewById(R.id.load_more_timelog);
		reload=(TextView)rootView.findViewById(R.id.reload_timelog);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		topFanSearch.clear();

		timelog_adapter = new TimelogAdapter(getActivity(), feedSearch);
		feedSearch.clear();
		imageLoader =imageLoader.getInstance();
		profile_options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

		cover_option = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder_timefeed)
		.showImageForEmptyUri(R.drawable.placeholder_timefeed) 
		.showImageOnFail(R.drawable.placeholder_timefeed)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		final ConnectionDetector conn = new ConnectionDetector(getActivity());
		if(conn.isConnectingToInternet())
		{
			new UserProfileInfoFragmentAsync().execute();	
			new UserTopFanAsync().execute();
		}
		else 
		{
			Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();

		}
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
		song.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//play the song
				SongHandler.post(new Runnable() {
					public void run() {
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
						// end
					}
				});
			}
		});
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent edit = new Intent(getActivity(),UserProfileUpdate.class);
				startActivityForResult(edit,UPDATE_RESULT);	
			}
		});
		//TIMELOG

		//ConnectionDetector conn = new ConnectionDetector(getActivity());
		if(conn.isConnectingToInternet())
		{		
			new GetFeedAsyncClass().execute();		
		}
		else{
			Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
		}

		profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), ViewPhoto.class);
				intent.putExtra("ImagePath",userDet.getUser_profile_photo_path());
				intent.putExtra("photo_id",userDet.getProfile_photo_id());
				intent.putExtra("photo_like_count",userDet.getProfile_photo_like_count());
				intent.putExtra("photo_comment_count",userDet.getProfile_photo_comment_count());
				intent.putExtra("photo_share_count",userDet.getProfile_photo_share_count());
				startActivity(intent); 
			}
		});

		cover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ViewPhoto.class);
				intent.putExtra("ImagePath",userDet.getUser_cover_photo_path());
				intent.putExtra("photo_id",userDet.getCover_photo_id());
				intent.putExtra("photo_like_count",userDet.getCover_photo_like_count());
				intent.putExtra("photo_comment_count",userDet.getCover_photo_comment_count());
				intent.putExtra("photo_share_count",userDet.getCover_photo_share_count());
				startActivity(intent); 				
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

		reload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(conn.isConnectingToInternet())
				{		
					new GetFeedAsyncClass().execute();		
				}
				else{
					Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
				}				
			}
		});
	}

	private class UserProfileInfoFragmentAsync extends AsyncTask<Void, Void, UserDetailsModel>{

		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(getActivity());
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}


		@Override
		protected UserDetailsModel doInBackground(Void... params) {
			JSONArray UserProfileInfoModelResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"user/"+Util.USER_ID);
			if(jsonObjectRecived != null){

				try{
					UserProfileInfoModelResponse = jsonObjectRecived.getJSONArray("userDetails");   
				}catch(JSONException e){
					e.printStackTrace();
				}
				UserDetailsModel UserProfileInfoModelData = new UserDetailsModel();
				JSONObject userprofile_info_Details;
				try{
					userprofile_info_Details = UserProfileInfoModelResponse.getJSONObject(0);
					UserProfileInfoModelData.setFirstname(userprofile_info_Details.getString("firstname"));
					UserProfileInfoModelData.setLastname(userprofile_info_Details.getString("lastname"));
					UserProfileInfoModelData.setBio(userprofile_info_Details.getString("bio"));
					UserProfileInfoModelData.setWebsite_blog(userprofile_info_Details.getString("website_blog"));
					UserProfileInfoModelData.setCurrent_city(userprofile_info_Details.getString("current_city"));
					UserProfileInfoModelData.setUser_profile_photo_path(userprofile_info_Details.getString("user_profile_photo_path"));
					UserProfileInfoModelData.setUser_cover_photo_path(userprofile_info_Details.getString("user_cover_photo_path"));
					UserProfileInfoModelData.setSong_title(userprofile_info_Details.getString("song_title"));
					UserProfileInfoModelData.setSong_cover_photo_path(userprofile_info_Details.getString("song_cover_photo_path"));
					UserProfileInfoModelData.setActual_song_path(userprofile_info_Details.getString("actual_song_path"));
					Song_URL=userprofile_info_Details.getString("actual_song_path");
					UserProfileInfoModelData.setSong_basename(userprofile_info_Details.getString("song_basename"));
					UserProfileInfoModelData.setFried_count(userprofile_info_Details.getString("fried_count"));
					UserProfileInfoModelData.setBio(userprofile_info_Details.getString("bio"));
					UserProfileInfoModelData.setFollower_count(userprofile_info_Details.getString("follower_count"));
					UserProfileInfoModelData.setIs_accept_friend_request(userprofile_info_Details.getString("is_accept_friend_request"));
					UserProfileInfoModelData.setIs_pending_friend_request(userprofile_info_Details.getString("is_pending_friend_request"));
					UserProfileInfoModelData.setIs_friend(userprofile_info_Details.getString("is_friend"));
					//new 
					UserProfileInfoModelData.setUsername(userprofile_info_Details.getString("username"));
					UserProfileInfoModelData.setProfile_photo_like_count(userprofile_info_Details.getString("profile_photo_like_count"));
					UserProfileInfoModelData.setProfile_photo_comment_count(userprofile_info_Details.getString("profile_photo_comment_count"));
					UserProfileInfoModelData.setProfile_photo_share_count(userprofile_info_Details.getString("profile_photo_share_count"));
					UserProfileInfoModelData.setCover_photo_like_count(userprofile_info_Details.getString("cover_photo_like_count"));
					UserProfileInfoModelData.setCover_photo_comment_count(userprofile_info_Details.getString("cover_photo_comment_count"));
					UserProfileInfoModelData.setCover_photo_share_count(userprofile_info_Details.getString("cover_photo_share_count"));
					UserProfileInfoModelData.setProfile_photo_post_id(userprofile_info_Details.getString("profile_photo_post_id"));
					UserProfileInfoModelData.setProfile_photo_id(userprofile_info_Details.getString("profile_photo_id"));
					UserProfileInfoModelData.setCover_photo_post_id(userprofile_info_Details.getString("cover_photo_post_id"));
					UserProfileInfoModelData.setCover_photo_id(userprofile_info_Details.getString("cover_photo_id"));
					UserProfileInfoModelData.setJid(userprofile_info_Details.getString("jid"));
					return UserProfileInfoModelData;
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				return null;
			}
			return null;
		}

		@SuppressWarnings("static-access")
		@Override
		protected void onPostExecute(UserDetailsModel app) {
			super.onPostExecute(app);
			pDialog.dismiss();
			userDet=app;
			if(app == null){

			}else{
				if(app.getBio().equalsIgnoreCase("null") || app.getBio().equalsIgnoreCase("")){
					bio.setText("NA");}
				else{	bio.setText(app.getBio());}

				if(app.getFried_count().equalsIgnoreCase("null") || app.getFried_count().equalsIgnoreCase("")){
					friend_count.setText("0");}
				else{friend_count.setText(app.getFried_count());}

				if(app.getFollower_count().equalsIgnoreCase("null") || app.getFollower_count().equalsIgnoreCase("")){
					follower_count.setText("0");}
				else{follower_count.setText(app.getFollower_count());}

				if(app.getCurrent_city().equalsIgnoreCase("null") || app.getCurrent_city().equalsIgnoreCase("")){
					location.setText("NA");}
				else{location.setText(app.getCurrent_city());}

				if(app.getWebsite_blog().equalsIgnoreCase("null") || app.getWebsite_blog().equalsIgnoreCase("")){
					website.setText("NA");}
				else{website.setText(app.getWebsite_blog());}

				if(app.getSong_title().equalsIgnoreCase("null") || app.getSong_title().equalsIgnoreCase("")){
					song.setText("NA");}
				else{song.setText(app.getSong_title());}

				//setting proflie pic
				imageLoader.displayImage(app.getUser_profile_photo_path(), profile, profile_options, new SimpleImageLoadingListener() {
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
				imageLoader.displayImage(app.getUser_cover_photo_path(),cover, cover_option, new SimpleImageLoadingListener() {
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



			}

		}

	}

	private class UserTopFanAsync extends AsyncTask<Void, Void, ArrayList<TopFanModel>>{

		@Override
		protected void onPreExecute() {
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
			JSONArray userTopFanResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"user/"+Util.USER_ID);

			if(jsonObjectRecived != null){

				try{
					userTopFanResponse = jsonObjectRecived.getJSONArray("topFans");
				}catch(JSONException e){
					e.printStackTrace();
				}

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
		}
	}
	//TIMELOG
	private class GetFeedAsyncClass extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(getActivity());
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
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
			
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(HttpGetClient.statuscode == 500)
			{
				reload.setVisibility(View.VISIBLE);
			}
			else if(HttpGetClient.statuscode == 504)
			{
				reload.setVisibility(View.VISIBLE);
			}
			else if(HttpGetClient.statuscode==401)
			{
				reload.setVisibility(View.VISIBLE);

			}
			else if(HttpGetClient.statuscode == 200)
			{
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
					reload.setVisibility(View.GONE);
					feedListView.setAdapter(timelog_adapter);		
					load_more.setVisibility(View.VISIBLE);
					ListUtility.setListViewHeightBasedOnChildren(feedListView);
				}
			}
			pDialog.dismiss();
		}

	}

	private class GetFeedLoadMoreAsyncClass extends AsyncTask<Void, Void, ArrayList<Timefeeds>>{

		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(getActivity());
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
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
			super.onPostExecute(result);
			if(HttpGetClient.statuscode == 500)
			{
			}
			else if(HttpGetClient.statuscode == 504)
			{
			}
			else if(HttpGetClient.statuscode==401)
			{

			}
			else if(HttpGetClient.statuscode == 200)
			{
				if(result == null){
					if(timelog_adapter.isEmpty()){
						Toast.makeText(getActivity(), "No feeds to load", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getActivity(), "No feeds to load", Toast.LENGTH_SHORT).show();

					}
				}else if(result.size()==0){
					if(timelog_adapter.isEmpty()){
						Toast.makeText(getActivity(), "No feeds to load", Toast.LENGTH_SHORT).show();
					}
					Toast.makeText(getActivity(), "No feeds to load", Toast.LENGTH_SHORT).show();
				}      else{
					timelog_adapter.notifyDataSetChanged();
					ListUtility.setListViewHeightBasedOnChildren(feedListView);
					load_more.setVisibility(View.VISIBLE);
				}
			}
			pDialog.dismiss(); 

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode !=getActivity().RESULT_OK) {

			return;
		}

		Bitmap bitmap;

		switch (requestCode) {

		case UPDATE_RESULT:
			if(resultCode == getActivity().RESULT_OK && null != data)
			{
				updateF=data.getStringExtra("update");
				if(updateF.equalsIgnoreCase("update"))
				{
					new UserProfileInfoFragmentAsync().execute();	
					Util.mediaPlayer.reset();
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data); 
	}
}
