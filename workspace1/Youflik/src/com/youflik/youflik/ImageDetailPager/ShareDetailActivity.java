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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.utils.Util;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ShareDetailActivity extends ActionBarActivity{

	private ListView mShareList;
	private TextView mNoText,mShareDetailTitle;
	private String mPhotoId,mVideoId,mPostId;
	private ProgressDialog mPDialog;
	private ArrayList<ShareDetailModel> mShareArrayList;
	private ShareDetailAdapter mShareAdapter;
	private boolean isPhoto,isVideo,isPost;
	private static int pageCount = 0;
	private static String pagination_Date_String;
	private boolean flag_loading=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_like_detail);
		mShareList = (ListView) findViewById(R.id.activity_like_detail_list);
		mNoText = (TextView)findViewById(R.id.activity_like_detail_no_list);

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.custom_actionbar_share_detail);

		Bundle extras = getIntent().getExtras();
		if(extras.containsKey("photo")){
			mPhotoId = extras.getString("photo_id");
			isPhoto = true;
			new GetShareList().execute(mPhotoId);
		} 
		if(extras.containsKey("video")){
			mVideoId = extras.getString("video_id");
			isVideo = true;
			new GetShareList().execute(mVideoId);
		}
		if(extras.containsKey("text")){
			mPostId = extras.getString("post_id");
			isPost =  true;
			new GetShareList().execute(mPostId);
		}
		mShareList.setOnScrollListener(new OnScrollListener(){

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
						ConnectionDetector conn = new ConnectionDetector(ShareDetailActivity.this);
						if(conn.isConnectingToInternet()){
							if(isPost)	new GetShareListLoadMore().execute(mPostId);
							if(isPhoto) new GetShareListLoadMore().execute(mPhotoId);
							if(isVideo) new GetShareListLoadMore().execute(mVideoId);
						}else{
							Crouton.makeText(ShareDetailActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
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
	class GetShareList extends AsyncTask<String,Void,ArrayList<ShareDetailModel>>{
		JSONObject receivedJSONResponse ;

		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(ShareDetailActivity.this);
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}

		@Override
		protected ArrayList<ShareDetailModel> doInBackground(String... params) {
			JSONObject sendGetShareJSONObject = new JSONObject();
			String id = params[0];
			if(isPost){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"share?field=post_id&id="+id);
			}
			if(isPhoto){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"share?field=photo_id&id="+id);
			}
			if(isVideo){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"share?field=video_id&id="+id);	
			}
			if(receivedJSONResponse!=null){
				mShareArrayList = new ArrayList<ShareDetailModel>();
				try {
					JSONArray ShareArray = receivedJSONResponse.getJSONArray("sharedUsers");
					for(int i=0;i<ShareArray.length();i++){
						JSONObject ShareObject = ShareArray.getJSONObject(i);
						ShareDetailModel Sharemodel = new ShareDetailModel();
						Sharemodel.setCreated_date(ShareObject.getString("created_date"));
						Sharemodel.setShare_id(ShareObject.getString("share_id"));
						Sharemodel.setFirstname(ShareObject.getString("firstname"));
						Sharemodel.setLastname(ShareObject.getString("lastname"));
						Sharemodel.setUser_id(ShareObject.getString("user_id"));
						Sharemodel.setUsername(ShareObject.getString("username"));
						Sharemodel.setUser_profile_photo(ShareObject.getString("user_profile_photo"));
						mShareArrayList.add(Sharemodel);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			return mShareArrayList;
		}
		@Override
		protected void onPostExecute(ArrayList<ShareDetailModel> result){
			super.onPostExecute(result);
			mPDialog.dismiss();
			if(result==null){
				mShareList.setVisibility(View.GONE);
				mNoText.setText("No shares found for this post");
				mNoText.setVisibility(View.VISIBLE);	
			}else if (result.size()==0){	
				mShareList.setVisibility(View.GONE);
				mNoText.setText("No shares found for this post");
				mNoText.setVisibility(View.VISIBLE);
			} else {
				mNoText.setVisibility(View.GONE);
				mShareList.setVisibility(View.VISIBLE);
				mShareAdapter = new ShareDetailAdapter(ShareDetailActivity.this,result);
				mShareList.setAdapter(mShareAdapter);
				if(result.size()<20){
					flag_loading = true;
				}
			}
		}
	}

	class GetShareListLoadMore extends AsyncTask<String,Void,ArrayList<ShareDetailModel>>{
		JSONObject receivedJSONResponse ;

		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(ShareDetailActivity.this);
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}

		@Override
		protected ArrayList<ShareDetailModel> doInBackground(String... params) {
			JSONObject sendGetShareJSONObject = new JSONObject();
			String id = params[0];
			if(isPost){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"share?field=post_id&id="+id+"?last_date="+pagination_Date_String);
			}
			if(isPhoto){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"share?field=photo_id&id="+id+"?last_date="+pagination_Date_String);
			}
			if(isVideo){
				receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"share?field=video_id&id="+id+"?last_date="+pagination_Date_String);	
			}
			if(receivedJSONResponse!=null){

				try {
					JSONArray ShareArray = receivedJSONResponse.getJSONArray("sharedUsers");
					for(int i=0;i<ShareArray.length();i++){
						JSONObject ShareObject = ShareArray.getJSONObject(i);
						ShareDetailModel Sharemodel = new ShareDetailModel();
						Sharemodel.setCreated_date(ShareObject.getString("created_date"));
						Sharemodel.setShare_id(ShareObject.getString("share_id"));
						Sharemodel.setFirstname(ShareObject.getString("firstname"));
						Sharemodel.setLastname(ShareObject.getString("lastname"));
						Sharemodel.setUser_id(ShareObject.getString("user_id"));
						Sharemodel.setUsername(ShareObject.getString("username"));
						Sharemodel.setUser_profile_photo(ShareObject.getString("user_profile_photo"));
						mShareArrayList.add(Sharemodel);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			return mShareArrayList;
		}
		@Override
		protected void onPostExecute(ArrayList<ShareDetailModel> result){
			super.onPostExecute(result);
			mPDialog.dismiss();
			if(result==null){

			}else if (result.size()==0){	


			} else {

				mShareAdapter.notifyDataSetChanged();
				pageCount = pageCount+20;
				mShareList.setSelection(pageCount);
				if(result.size()<20){
					flag_loading = true;
				}
			}
		}
	}
}
