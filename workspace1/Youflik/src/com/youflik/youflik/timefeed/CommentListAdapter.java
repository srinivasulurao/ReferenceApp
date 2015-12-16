package com.youflik.youflik.timefeed;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;
import com.youflik.youflik.ImageDetailPager.CommentsDetailActivity;
import com.youflik.youflik.ImageDetailPager.CommentsDetailActivity.PostAddComment;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.proxy.HttpPutClient;
import com.youflik.youflik.utils.Util;

public class CommentListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<CommentListModel> commentList;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mProfileOptions;
	private ProgressDialog mPDialog;
	private static int commentEditPosition,commentDeletePosition;
	private LayoutInflater layoutInflater;

	//variables for deleting a comment
	private String mDeleteTrackId,mDeleteOrgPostOwnerId,
	mDeletePhotoId,mDeleteVideoId,
	mDeletePostId,mCommentId,mDeleteSongId="null";
	private static final String DELETE_COMMENT = Util.API+"delete_comment/";

	//variables for updating the comment
	private String mOriginalComment,mUpdatedComment;
	private static final String EDIT_COMMENT_API = Util.API+"comment/";

	public CommentListAdapter(Context c,ArrayList<CommentListModel> commentListModel){
		this.mContext=c;
		commentList = commentListModel;
		mImageLoader = ImageLoader.getInstance();
		mProfileOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

	}

	@Override
	public int getCount() {
		return commentList.size();
	}

	@Override
	public Object getItem(int position) {
		return commentList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (layoutInflater == null)
			layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final CommentListModel model = commentList.get(position);

		if(convertView==null){
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.custom_view_timefeed_list_comment_dialog,parent,false);
			holder.profilePic = (ImageView)convertView.findViewById(R.id.custom_view_timefeed_listcomment_dialog_profilepic);
			holder.name = (TextView) convertView.findViewById(R.id.custom_view_timefeed_listcomment_dialog_firstname);
			holder.status = (TextView)convertView.findViewById(R.id.custom_view_timefeed_listcomment_dialog_status);
			holder.time = (TextView) convertView.findViewById(R.id.custom_view_timefeed_listcomment_dialog_time);
			holder.edit=(TextView)convertView.findViewById(R.id.custom_view_timefeed_listcomment_edit);
			holder.delete=(TextView)convertView.findViewById(R.id.custom_view_timefeed_listcomment_delete);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder)convertView.getTag();
		}
		Typeface typeFace=Typeface.createFromAsset(mContext.getAssets(),"fonts/BentonSans-Book.otf");

		if(model.getCommentOwnerId().equalsIgnoreCase(Util.USER_ID))
		{
			holder.edit.setVisibility(View.VISIBLE); 
			holder.delete.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.edit.setVisibility(View.INVISIBLE);
			holder.delete.setVisibility(View.INVISIBLE);
		}

		holder.edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				commentEditPosition = position;


				CommentsDetailActivity.mAddComment.setText(holder.status.getText().toString().trim());
				CommentsDetailActivity.mCommentPost.setText("Update");

				CommentsDetailActivity.mCommentPost.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View v) {
						if(CommentsDetailActivity.mCommentPost.getText().toString().trim().equalsIgnoreCase("Update")){
							mCommentId = model.getCommentId();
							mUpdatedComment = CommentsDetailActivity.mAddComment.getText().toString().trim();
							new PutEditComment().execute(mCommentId);
						}
						else
						{
							//Toast.makeText(mContext, "post", Toast.LENGTH_SHORT).show();
							((CommentsDetailActivity)mContext).new PostAddComment().execute();;
						}
					}

				});

			}
		});
		holder.delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CommentsDetailActivity.mAddComment.getText().clear();
				CommentsDetailActivity.mCommentPost.setText(R.string.post);
				CommentsDetailActivity.mCommentPost.setVisibility(View.GONE);
				CommentsDetailActivity.mCommentPostLayout.setVisibility(View.GONE);

				commentDeletePosition = position;
				mDeleteTrackId = model.getTrackId();
				mDeleteOrgPostOwnerId = model.getOrgPostOwnerId();
				mDeletePostId = model.getPostId();
				mDeletePhotoId = model.getPhotoId();
				mDeleteVideoId = model.getVideoId();
				mCommentId = model.getCommentId();


				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
				alertBuilder.setTitle("Delete Comment");
				alertBuilder.setMessage("Are you sure to delete this comment?");
				alertBuilder.setPositiveButton("YES",new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new DeleteComment().execute(mCommentId);
						dialog.dismiss();
					}
				});

				alertBuilder.setNegativeButton("NO",new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
				AlertDialog alertDialog = alertBuilder.create();
				alertDialog.show();
			}
		});

		/*if(model.getLastName().equalsIgnoreCase("null")){*/
		holder.name.setText(model.getFirstName());	 
		/*} else {
			holder.name.setText(model.getFirstName()+" "+model.getLastName());

		}*/

		holder.status.setText(model.getCommentText());
		holder.status.setTypeface(typeFace);
		mImageLoader.displayImage(model.getThumb3(),holder.profilePic,mProfileOptions,new SimpleImageLoadingListener()
		{  @Override
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
		return convertView;
	}
	class ViewHolder{
		ImageView profilePic;
		TextView name,status,time,edit,delete;
	}
	class DeleteComment extends AsyncTask<String,Void,JSONObject>{

		JSONObject receivedJSONResponse=null;

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			if(mPDialog==null){
				mPDialog = Util.createProgressDialog(mContext);
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}
		@Override
		protected JSONObject doInBackground(String... params) {

			String comment_id = params[0];
			JSONObject sendDeleteJSONObject = new JSONObject();

			try {
				sendDeleteJSONObject.put("track_id", mDeleteTrackId);
				sendDeleteJSONObject.put("org_post_owner_id",mDeleteOrgPostOwnerId);

				// check for different posts photo,video,song
				if(mDeletePhotoId==null ||mDeletePhotoId.equalsIgnoreCase("null")){}
				else {
					sendDeleteJSONObject.put("photo_id",mDeletePhotoId);
				}
				if(mDeleteVideoId==null || mDeleteVideoId.equalsIgnoreCase("null")){}
				else {sendDeleteJSONObject.put("video_id",mDeleteVideoId);
				}

				sendDeleteJSONObject.put("post_id", mDeletePostId);

				//check for correct input parameters to delete API
				receivedJSONResponse =HttpPostClient.sendHttpPost(DELETE_COMMENT+comment_id,sendDeleteJSONObject);
			} catch (JSONException e) { e.printStackTrace();
			}
			return receivedJSONResponse;
		}

		@Override 
		protected void onPostExecute(JSONObject result){
			mPDialog.dismiss();
			try{
				if(result!=null){
					if(result.getString("status").equalsIgnoreCase("1")){
						commentList.remove(commentDeletePosition);
						notifyDataSetChanged();
						Toast.makeText(mContext,result.getString("messages"),Toast.LENGTH_LONG).show();
					} else {
						if(result.has("errorMessages")){
							Toast.makeText(mContext,result.getString("errorMessages"),Toast.LENGTH_LONG).show();
						}
					}  
				}
			}catch(JSONException e){
			}
		}
	}

	class PutEditComment extends AsyncTask<String,Void,JSONObject>{
		private JSONObject mEditCommentJsonObject,receivedJSONResponse;
		@Override
		protected void onPreExecute(){
			if(mPDialog==null){
				mPDialog = Util.createProgressDialog(mContext);
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			String commentId = params[0];
			mEditCommentJsonObject = new JSONObject();
			try {
				mEditCommentJsonObject.put("comment_text",mUpdatedComment);	
				receivedJSONResponse = HttpPutClient.sendHttpPost(EDIT_COMMENT_API+commentId, mEditCommentJsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return receivedJSONResponse;
		}

		@Override
		protected void onPostExecute(JSONObject result){
			super.onPostExecute(result);
			mPDialog.dismiss();
			if(result!=null){
				try {
					if(result.getString("status").equalsIgnoreCase("1")){
						CommentListModel model = (CommentListModel)commentList.get(commentEditPosition);
						model.setCommentText(mUpdatedComment);
						notifyDataSetChanged();
						Toast.makeText(mContext, result.getString("message"),Toast.LENGTH_LONG).show();					
						CommentsDetailActivity.mAddComment.getText().clear();
						CommentsDetailActivity.mCommentPost.setText(R.string.post);
						CommentsDetailActivity.mCommentPost.setVisibility(View.GONE);
						CommentsDetailActivity.mCommentPostLayout.setVisibility(View.GONE);

					} else {
						Toast.makeText(mContext, result.getString("message"),Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
