package com.youflik.youflik.ImageDetailPager;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.timefeed.CommentListAdapter;
import com.youflik.youflik.timefeed.CommentListAdapterTemp;
import com.youflik.youflik.timefeed.CommentListModel;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CommentsDetailActivity  extends ActionBarActivity{

	//variables for listing the comments 
	private static final String GET_COMMENT_LIST_API = Util.API+"comment/";
	private static final String POST_ADD_COMMENT_API=Util.API+"comment";
	private ArrayList<CommentListModel> commentListArray;
	private ListView mCommentListView;
	private LinearLayout mCommentTitleLayout,mCommentAddLayout;
	public static LinearLayout mCommentPostLayout;
	public static EditText mAddComment;
	private ImageView mCommentListClose;
	private TextView mCommentListTitle,mNoComment;
	public static Button mCommentPost;
	private CommentListAdapter mCommentListAdapter;
	private CommentListAdapterTemp mCommentListAdapterTemp;
	private AlertDialogManager alert = new AlertDialogManager();
	private ProgressDialog mPDialog;

	private String  mPostId,mCommentText,mId,mCommentType,mPostOwnerId,privacy;
	private String isPhoto,isVideo;
	private String fromWhere;
	private static String pagination_Date_String="";
	private boolean flag_loading = false;
	private static int pageCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_timefeed_list_comment_dialog);

		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.custom_actionbar_comment_detail);

		if(commentListArray!=null){
			commentListArray.clear();	
		}	
		Bundle extras =  getIntent().getExtras();
		if(extras!=null){
			fromWhere = extras.getString("from");
			mCommentType = extras.getString("commentType");
			if(fromWhere.equalsIgnoreCase("Timefeed")){
				mId =  extras.getString("id");
				mPostOwnerId = extras.getString("org_post_owner_id");	
				privacy= extras.getString("privacy");
				mPostId =  extras.getString("post_id");
			}
			if(fromWhere.equalsIgnoreCase("Gallery")){
				mId =  extras.getString("id");
			}
			if(fromWhere.equalsIgnoreCase("TimeLog")){
				mId =  extras.getString("id");
				mPostOwnerId = extras.getString("org_post_owner_id");	
				privacy= extras.getString("privacy");
				mPostId =  extras.getString("post_id");
			}
			
		}
		initView();  
		ConnectionDetector conn = new ConnectionDetector(this);
		if(conn.isConnectingToInternet()){
			new GetCommentList().execute(mId,mCommentType); 
		} else {
			alert.showAlertDialog(this,"Connection Error","Check your Internet Connection",false);	
		}

		mAddComment.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
				mCommentPost.setVisibility(View.VISIBLE);
				mCommentPostLayout.setVisibility(View.VISIBLE);
				if(mAddComment.getText().toString().trim().length()==0){
					mCommentPost.setVisibility(View.GONE);	
					mCommentPostLayout.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(mAddComment.getText().toString().trim().length()==0){
					mCommentPost.setVisibility(View.GONE);	
					mCommentPostLayout.setVisibility(View.GONE);
				}
			}


		});
		mCommentPost.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				mCommentText = mAddComment.getText().toString().trim();
				if(mCommentPost.getText().toString().trim().equalsIgnoreCase("Post")){
					ConnectionDetector conn = new ConnectionDetector(CommentsDetailActivity.this);
					if(conn.isConnectingToInternet()){
						new PostAddComment().execute();
					}else {
						alert.showAlertDialog(CommentsDetailActivity.this,"Connection Error","Check your Internet Connection",false);
					}
				}					}

		});

		mCommentListClose.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mCommentListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
				{
					if(flag_loading == false)
					{
						flag_loading = true;
						ConnectionDetector conn = new ConnectionDetector(CommentsDetailActivity.this);
						if(conn.isConnectingToInternet()){
							new GetCommentListLoadMore().execute(mId,mCommentType);
						}else{
							Crouton.makeText(CommentsDetailActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
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
	private void initView() {

		mCommentListView = (ListView )findViewById(R.id.fragment_timefeed_listcomment_dialog_listview);
		mAddComment = (EditText)findViewById(R.id.fragment_timefeed_listcomment_dialog_addComment);
		mCommentListClose = (ImageView)findViewById(R.id.fragment_timefeed_listcomment_dialog_imageclose);
		mCommentListTitle = (TextView)findViewById(R.id.fragment_timefeed_listcomment_dialog_title);
		mNoComment = (TextView)findViewById(R.id.fragment_timefeed_listcomment_dialog_nocomments);
		mCommentPost = (Button)findViewById(R.id.fragment_timefeed_listcomment_dialog_post);
		mCommentTitleLayout = (LinearLayout) findViewById(R.id.fragment_timefeed_list_comment_title_layout);
		mCommentAddLayout = (LinearLayout) findViewById(R.id.fragment_timefeed_listcomment_add_layout);
		mCommentPostLayout = (LinearLayout) findViewById(R.id.fragment_timefeed_listcomment_post_layout);
		mNoComment.setVisibility(View.GONE);
		mCommentTitleLayout.setVisibility(View.GONE);

		if(fromWhere.equalsIgnoreCase("Timefeed")){
			mCommentAddLayout.setVisibility(View.VISIBLE);
			mCommentPostLayout.setVisibility(View.GONE);
		} else {
			mCommentAddLayout.setVisibility(View.GONE);
			mCommentPostLayout.setVisibility(View.GONE);
		}
	}

	//Async class for getting the list of comments for a post in time feed
	class GetCommentList extends AsyncTask<String,Void,ArrayList<CommentListModel>>{

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(CommentsDetailActivity.this);
				mPDialog.show();
			}
			else { mPDialog.show();}
		}
		@Override
		protected ArrayList<CommentListModel> doInBackground(String... params) {
			String post_id = params[0];
			String comment_type= params[1];

			JSONObject receivedJSONResponse = HttpGetClient.sendHttpPost(GET_COMMENT_LIST_API+post_id+"?comment_type="+comment_type.replaceAll(" ","%20"));
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
							pagination_Date_String = comments.getString("created_date");
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

			if(result ==null){

				mCommentListView.setVisibility(View.GONE);
				mNoComment.setVisibility(View.VISIBLE);
				mNoComment.setText("No Comments found for this post");
				mNoComment.setGravity(Gravity.CENTER);
				flag_loading=true;
			} else if(result.size()==0)
			{   
			mCommentListView.setVisibility(View.GONE);
			mNoComment.setVisibility(View.VISIBLE);
			mNoComment.setText("No Comments found for this post");
			flag_loading=true;
			} else{
				mCommentListView.setVisibility(View.VISIBLE);
				mNoComment.setVisibility(View.GONE);
				if(fromWhere.equalsIgnoreCase("Timefeed"))
				{
				mCommentListAdapter = new CommentListAdapter(CommentsDetailActivity.this,result);
				mCommentListView.setAdapter(mCommentListAdapter);
				}
				else
				{
					mCommentListAdapterTemp = new CommentListAdapterTemp(CommentsDetailActivity.this,result);
					mCommentListView.setAdapter(mCommentListAdapterTemp);
				}
				if(result.size()<20){
					flag_loading=true;	
				}
			}
		}
	}


	//Async class for getting the next 20 list of comments for a post in time feed
	class GetCommentListLoadMore extends AsyncTask<String,Void,ArrayList<CommentListModel>>{

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			mPDialog.show();
		}
		@Override
		protected ArrayList<CommentListModel> doInBackground(String... params) {
			String post_id = params[0];
			String comment_type=params[1];
			JSONObject receivedJSONResponse = HttpGetClient.sendHttpPost(GET_COMMENT_LIST_API+post_id+"?comment_type="+comment_type.replaceAll(" ","%20")+"&last_date="+pagination_Date_String.replaceAll(" ","%20"));

			if(receivedJSONResponse!=null){
				try { 
					JSONArray receivedJSONResponseArray = receivedJSONResponse.getJSONArray("comments");
					if(receivedJSONResponseArray.length()>0){

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
							pagination_Date_String = comments.getString("created_date");
							commentListArray.add(model);
						}
					}else {
					}
				} catch (JSONException e) {
					e.printStackTrace();}
			}
			return commentListArray;
		}
		@Override
		protected void onPostExecute(ArrayList<CommentListModel> result){ 
			mPDialog.dismiss();
			if(result ==null){
				flag_loading=true;
			} else {
				if(result.size()==0)
				{   
					flag_loading=true;
				} 
				if(result.size()>0){
					mCommentListView.setVisibility(View.VISIBLE);
					mNoComment.setVisibility(View.GONE);
					if(fromWhere.equalsIgnoreCase("Timefeed"))
					{
					mCommentListAdapter.notifyDataSetChanged();
					}
					else
					{
						mCommentListAdapterTemp.notifyDataSetChanged();
					}
					pageCount = pageCount+20;
					mCommentListView.setSelection(pageCount);
					if(result.size()<20){
						flag_loading =true;
					}
				}
			}
		}
	}

	//Async class for adding the comment
	public class PostAddComment extends AsyncTask<Void,Void,JSONObject>{
		JSONObject receivedJSONResponse;
		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(CommentsDetailActivity.this);
				mPDialog.show();
			}else
			{ mPDialog.show();}
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject postJSONObject = new JSONObject();
			try {
				postJSONObject.put("commentType",mCommentType);
				postJSONObject.put("originalPostId",mPostId);
				postJSONObject.put("comment_text",mAddComment.getText().toString().trim());
				postJSONObject.put("org_post_owner_id",mPostOwnerId);
				postJSONObject.put("id",mId);
				postJSONObject.put("privacy_type",privacy);

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
						newCommentModel.setCommentText(mAddComment.getText().toString().trim());
						newCommentModel.setCommentId(result.getString("comment_id"));
						newCommentModel.setPostId(mPostId);
						newCommentModel.setOrgPostOwnerId(mPostOwnerId);
						newCommentModel.setFirstName(Util.FIRSTNAME);
						newCommentModel.setCommentOwnerId(Util.USER_ID);
						newCommentModel.setThumb3(Util.NM_PROFILE_PIC);
						if(fromWhere.equalsIgnoreCase("Timefeed"))
						{
						if(mCommentListAdapter == null){
							ConnectionDetector conn = new ConnectionDetector(CommentsDetailActivity.this);
							if(conn.isConnectingToInternet()){
								commentListArray = new ArrayList<CommentListModel>();
								commentListArray.add(newCommentModel);
								new GetCommentList().execute(mId,mCommentType);
							} else {
								alert.showAlertDialog(CommentsDetailActivity.this,"Connection Error","Check your Internet Connection",false);
							}
						}	
						else if(mCommentListAdapter.getCount()==0) {
							ConnectionDetector conn = new ConnectionDetector(CommentsDetailActivity.this);
							if(conn.isConnectingToInternet()){
								commentListArray = new ArrayList<CommentListModel>();
								commentListArray.add(newCommentModel);
								new GetCommentList().execute(mId,mCommentType);
							} else {
								alert.showAlertDialog(CommentsDetailActivity.this,"Connection Error","Check your Internet Connection",false);
							}
						} else {
							commentListArray.add(newCommentModel);
							mCommentListAdapter.notifyDataSetChanged();
							}
						}
						else
						{

							if(mCommentListAdapterTemp == null){
								ConnectionDetector conn = new ConnectionDetector(CommentsDetailActivity.this);
								if(conn.isConnectingToInternet()){
									commentListArray = new ArrayList<CommentListModel>();
									commentListArray.add(newCommentModel);
									new GetCommentList().execute(mId,mCommentType);
								} else {
									alert.showAlertDialog(CommentsDetailActivity.this,"Connection Error","Check your Internet Connection",false);
								}
							}	
							else if(mCommentListAdapterTemp.getCount()==0) {
								ConnectionDetector conn = new ConnectionDetector(CommentsDetailActivity.this);
								if(conn.isConnectingToInternet()){
									commentListArray = new ArrayList<CommentListModel>();
									commentListArray.add(newCommentModel);
									new GetCommentList().execute(mId,mCommentType);
								} else {
									alert.showAlertDialog(CommentsDetailActivity.this,"Connection Error","Check your Internet Connection",false);
								}
							} else {
								commentListArray.add(newCommentModel);
								mCommentListAdapterTemp.notifyDataSetChanged();}
							
						}
					}	
					mAddComment.getText().clear();
					Toast.makeText(CommentsDetailActivity.this, result.getString("message"),Toast.LENGTH_LONG).show();
				}

			}catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
