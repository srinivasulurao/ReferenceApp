package com.youflik.youflik.ImageDetailPager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import com.youflik.youflik.R;
import com.youflik.youflik.models.PhotosModel;
import com.youflik.youflik.models.VideosModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.utils.Util;
import com.youflik.youflik.utils.Util.Extra;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class videoDetail extends ImagePagerBaseActivity {

	private ViewPager pager;
	private int pagerPosition;
	private String checkActivity;
	private String UserGalleryVideosURL;
	private ArrayList<VideosModel> galleryVideoSearch = new ArrayList<VideosModel>();
	private static final String STATE_POSITION = "STATE_POSITION";
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		setContentView(R.layout.activity_image_detail_pager);
		initView();

		Bundle bundle = getIntent().getExtras();

		assert bundle != null;
		//String[] imageUrls = bundle.getStringArray(Extra.IMAGES);
		pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);
		checkActivity = bundle.getString("Activity");

		if(checkActivity.equalsIgnoreCase("User")){
			//UserGalleryVideosURL = Util.API +"photo?user_id=" + Util.USER_ID +"&all=1";
			UserGalleryVideosURL=Util.API +"video?user_id=" + Util.USER_ID +"&get_all_videos=1";
		}
		if(checkActivity.equalsIgnoreCase("ThirdParty")){
			String FriendID = bundle.getString("FriendID");
			//UserGalleryPhotosURL = Util.API +"photo?user_id=" + FriendID + "&all=1";
			UserGalleryVideosURL=Util.API +"video?user_id=" + FriendID+"&get_all_videos=1";
		}
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		new GalleryVideoAsync().execute();
	}

	private void initView() {
		// TODO Auto-generated method stub
		pager = (ViewPager) findViewById(R.id.pager_photos);
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	public Parcelable saveState() { 
		return null;
	}

	private class GalleryVideoAsync extends AsyncTask<Void, Void, ArrayList<VideosModel>>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(videoDetail.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected ArrayList<VideosModel> doInBackground(
				Void... params) {
			// TODO Auto-generated method stub
			JSONArray userGalleryVideosResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(UserGalleryVideosURL);

			if(jsonObjectRecived != null){

				try{
					userGalleryVideosResponse = jsonObjectRecived.getJSONArray("videos");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< userGalleryVideosResponse.length();i++){
					VideosModel videosData = new VideosModel();
					JSONObject videosDetails;
					try{
						videosDetails = userGalleryVideosResponse.getJSONObject(i);
						videosData.setVideo_id(videosDetails.getString("video_id"));
						videosData.setVideo_title(videosDetails.getString("video_title"));
						videosData.setCreated_date(videosDetails.getString("created_date"));	
						videosData.setSelected_video_poster(videosDetails.getString("selected_video_poster"));	
						videosData.setVideo_play_count(videosDetails.getString("video_play_count"));	
						videosData.setVideo_like_count(videosDetails.getString("video_like_count"));	
						videosData.setVideo_path(videosDetails.getString("video_path"));
						videosData.setVideo_comment_count(videosDetails.getString("video_comment_count"));	
						videosData.setVideo_share_count(videosDetails.getString("video_share_count"));
						videosData.setVideo_desc(videosDetails.getString("video_desc"));
						videosData.setPost_id(videosDetails.getString("post_id"));
						galleryVideoSearch.add(videosData);
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return galleryVideoSearch;
			}else{
				return null;
			}
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(ArrayList<VideosModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();

			if(result == null){
			}else if(result.size()==0){
				/*ImagePagerAdapter adapter = new ImagePagerAdapter(getApplicationContext(), new ArrayList<UserPhotosGalleryModel>());
				pager.setAdapter(adapter);
				pager.setCurrentItem(pagerPosition);*/
			} else{
				videoDetailAdapter adapter = new videoDetailAdapter(videoDetail.this, result);
				pager.setAdapter(adapter);
				pager.setCurrentItem(pagerPosition);			}
		}
	}
}
