package com.youflik.youflik.ImageDetailPager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class LikeDetailActivity extends ActionBarActivity {

	private ListView mLikeList;
	private TextView mNoText;
	private String mPhotoId,mVideoId,mPostId;
	private ProgressDialog mPDialog;
	private ArrayList<LikeDetailModel> mLikeArrayList;
	private LikeDetailAdapter mLikeAdapter;
	private boolean isPhoto,isVideo,isPost;
	private static int pageCount = 0;
	private static String pagination_Date_String;
	private boolean flag_loading=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_like_detail);
		mLikeList = (ListView) findViewById(R.id.activity_like_detail_list);
		mNoText = (TextView)findViewById(R.id.activity_like_detail_no_list);
		
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.custom_actionbar_like_detail);
		
		Bundle extras = getIntent().getExtras();

		if(extras.containsKey("photo")){
			mPhotoId = extras.getString("photo_id");
			isPhoto =  true;
			isVideo = false;
			isPost = false;
			new GetLikeList().execute(mPhotoId);
		} 
		if(extras.containsKey("video")){
			mVideoId = extras.getString("video_id");
			isVideo = true;
			isPhoto = false;
			isPost = false;
			new GetLikeList().execute(mVideoId);
		}
		if(extras.containsKey("text")){
			mPostId = extras.getString("post_id");
			isPost =  true;
			isPhoto = false;
			isVideo = false;
			new GetLikeList().execute(mPostId);
		}
		mLikeList.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
				{
					if(flag_loading == false)
					{
						flag_loading = true;
						ConnectionDetector conn = new ConnectionDetector(LikeDetailActivity.this);
						if(conn.isConnectingToInternet()){
							if(isPost)	new GetLikeListLoadMore().execute(mPostId);
							if(isPhoto) new GetLikeListLoadMore().execute(mPhotoId);
							if(isVideo) new GetLikeListLoadMore().execute(mVideoId);			
						}else{
							Crouton.makeText(LikeDetailActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
						}
					}
				}
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_down_in_reverse,R.anim.push_down_out_reverse);

	}

	class GetLikeList extends AsyncTask<String,Void,ArrayList<LikeDetailModel>>{
		JSONObject receivedJSONResponse ;

		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(LikeDetailActivity.this);
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}


		@Override
		protected ArrayList<LikeDetailModel> doInBackground(String... params) {
			JSONObject sendGetLikeJSONObject = new JSONObject();
			String id = params[0];
			if(isPost){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"like?field=post_id&id="+id);
			}
			if(isPhoto){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"like?field=photo_id&id="+id);
			}
			if(isVideo){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"like?field=video_id&id="+id); 
			}
			if(receivedJSONResponse!=null){
				mLikeArrayList = new ArrayList<LikeDetailModel>();
				try {
					JSONArray likeArray = receivedJSONResponse.getJSONArray("likedUsers");
					for(int i=0;i<likeArray.length();i++){
						JSONObject likeObject = likeArray.getJSONObject(i);
						LikeDetailModel likemodel = new LikeDetailModel();
						likemodel.setCreated_date(likeObject.getString("created_date"));
						likemodel.setLike_id(likeObject.getString("like_id"));
						likemodel.setFirstname(likeObject.getString("firstname"));
						likemodel.setLastname(likeObject.getString("lastname"));
						likemodel.setUser_id(likeObject.getString("user_id"));
						likemodel.setUsername(likeObject.getString("username"));
						likemodel.setUser_profile_photo(likeObject.getString("user_profile_photo"));
						pagination_Date_String = likeObject.getString("created_date");
						mLikeArrayList.add(likemodel);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return mLikeArrayList;
		}
		@Override
		protected void onPostExecute(ArrayList<LikeDetailModel> result){
			super.onPostExecute(result);
			mPDialog.dismiss();
			if(result==null){
				mLikeList.setVisibility(View.GONE);
	            mNoText.setVisibility(View.VISIBLE);
	            mNoText.setText("No likes found for this post");
			}else if (result.size()==0){	
				mLikeList.setVisibility(View.GONE);
	            mNoText.setVisibility(View.VISIBLE);
	            mNoText.setText("No likes found for this post");
			} else {
				mLikeList.setVisibility(View.VISIBLE);
	            mNoText.setVisibility(View.GONE);
	            mLikeAdapter = new LikeDetailAdapter(LikeDetailActivity.this,result);
				mLikeList.setAdapter(mLikeAdapter);
				if(result.size()<20){
					flag_loading = true;
				}
			}
		}
	}
	class GetLikeListLoadMore extends AsyncTask<String,Void,ArrayList<LikeDetailModel>>{
		JSONObject receivedJSONResponse ;

		@Override
		protected void onPreExecute(){
			mPDialog.show();
		}

		@Override
		protected ArrayList<LikeDetailModel> doInBackground(String... params) {
			JSONObject sendGetLikeJSONObject = new JSONObject();
			String id = params[0];
			if(isPost){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"like?field=post_id&id="+id+"?last_date="+pagination_Date_String);
			}
			if(isPhoto){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"like?field=photo_id&id="+id+"?last_date="+pagination_Date_String);
			}
			if(isVideo){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"like?field=video_id&id="+id+"?last_date="+pagination_Date_String); 
			}
			if(receivedJSONResponse!=null){

				try {
					JSONArray likeArray = receivedJSONResponse.getJSONArray("likedUsers");
					for(int i=0;i<likeArray.length();i++){
						JSONObject likeObject = likeArray.getJSONObject(i);
						LikeDetailModel likemodel = new LikeDetailModel();
						likemodel.setCreated_date(likeObject.getString("created_date"));
						likemodel.setLike_id(likeObject.getString("like_id"));
						likemodel.setFirstname(likeObject.getString("firstname"));
						likemodel.setLastname(likeObject.getString("lastname"));
						likemodel.setUser_id(likeObject.getString("user_id"));
						likemodel.setUsername(likeObject.getString("username"));
						likemodel.setUser_profile_photo(likeObject.getString("user_profile_photo"));
						mLikeArrayList.add(likemodel);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return mLikeArrayList;
		}
		@Override
		protected void onPostExecute(ArrayList<LikeDetailModel> result){
			super.onPostExecute(result);
			mPDialog.dismiss();
			if(result==null){
               
			}else if (result.size()==0){	
			} else {
				 mLikeAdapter.notifyDataSetChanged();
				pageCount = pageCount+20;
				mLikeList.setSelection(pageCount);
				if(result.size()<20){
					flag_loading = true;
				}
			}
		}
	}
}
