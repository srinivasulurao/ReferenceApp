package com.youflik.youflik.ImageDetailPager;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.youflik.youflik.R;
import com.youflik.youflik.models.PhotosModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.utils.Util;
import com.youflik.youflik.utils.Util.Extra;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;

public class ImageDetail extends ImagePagerBaseActivity {

	private ViewPager pager;
	private int pagerPosition;
	private String checkActivity;
	private String UserGalleryPhotosURL;
	private ArrayList<PhotosModel> galleryPhotoSearch = new ArrayList<PhotosModel>();
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
			UserGalleryPhotosURL = Util.API +"photo?user_id=" + Util.USER_ID +"&get_all_photos=1";
		}
		if(checkActivity.equalsIgnoreCase("ThirdParty")){
			String FriendID = bundle.getString("FriendID");
			UserGalleryPhotosURL = Util.API +"photo?user_id=" + FriendID + "&get_all_photos=1";
		}
	
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}
		new GalleryPhotoAsync().execute();
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

	private class GalleryPhotoAsync extends AsyncTask<Void, Void, ArrayList<PhotosModel>>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(ImageDetail.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected ArrayList<PhotosModel> doInBackground(
				Void... params) {
			// TODO Auto-generated method stub
			JSONArray userGalleryPhotosResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(UserGalleryPhotosURL);

			if(jsonObjectRecived != null){

				try{
					userGalleryPhotosResponse = jsonObjectRecived.getJSONArray("photos");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< userGalleryPhotosResponse.length();i++){
					PhotosModel photosData = new PhotosModel();
					JSONObject photoDetails;
					try{
						photoDetails = userGalleryPhotosResponse.getJSONObject(i);
						photosData.setPhoto_id(photoDetails.getString("photo_id"));
						photosData.setPhoto_name(photoDetails.getString("photo_name"));
						photosData.setPhoto_desc(photoDetails.getString("photo_desc"));
						photosData.setPhoto_taken_date(photoDetails.getString("photo_taken_date"));
						photosData.setLoc_id(photoDetails.getString("loc_id"));
						photosData.setOriginal_dimensions(photoDetails.getString("original_dimensions"));
						photosData.setUploaded_date(photoDetails.getString("uploaded_date"));
						photosData.setActual_photo_path_thumb3(photoDetails.getString("actual_photo_path_thumb3"));
						photosData.setPhoto_like_count(photoDetails.getString("photo_like_count"));
						photosData.setPhoto_comment_count(photoDetails.getString("photo_comment_count"));
						photosData.setPhoto_share_count(photoDetails.getString("photo_share_count"));
						photosData.setPost_id(photoDetails.getString("post_id"));
						photosData.setTrack_id(photoDetails.getString("track_id"));
						photosData.setLike_id(photoDetails.getString("like_id"));
						photosData.setUser_liked(photoDetails.getString("user_liked"));
						photosData.setCustom_privacy(photoDetails.getString("custom_privacy"));
						photosData.setActual_photo_path(photoDetails.getString("actual_photo_path"));
						galleryPhotoSearch.add(photosData);
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return galleryPhotoSearch;
			}else{
				return null;
			}
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(ArrayList<PhotosModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result == null){
			}else if(result.size()==0){
				/*ImagePagerAdapter adapter = new ImagePagerAdapter(getApplicationContext(), new ArrayList<UserPhotosGalleryModel>());
				pager.setAdapter(adapter);
				pager.setCurrentItem(pagerPosition);*/
			} else{
				ImageDetailAdapter adapter = new ImageDetailAdapter(ImageDetail.this, result);
				pager.setAdapter(adapter);
				pager.setCurrentItem(pagerPosition);			}
       }
	}
}
