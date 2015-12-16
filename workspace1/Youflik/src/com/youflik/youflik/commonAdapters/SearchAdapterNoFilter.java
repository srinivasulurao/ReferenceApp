package com.youflik.youflik.commonAdapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.youflik.youflik.models.SearchModel;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.proxy.HttpPutClient;
import com.youflik.youflik.utils.Util;

public class SearchAdapterNoFilter extends BaseAdapter{

	private ArrayList<SearchModel> listData;
	private LayoutInflater layoutInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private AlertDialog.Builder mCheckConnectAlert;
	private AlertDialog mConnectAlert=null; 
	private int mConnectFlag;
	private ProgressDialog mPDialog;
	private int mConnectListPosition;
	private Context prova;
	String[] sections;
	private boolean state;
	Activity contex_a;

	//variables for checkConnect status of a user
	private static final String SEND_FRIEND_REQUEST_API =Util.API+"friend_request";
	private static String UNFRIEND_API =Util.API+"unfriend/";
	private static String CANCEL_PENDING_FRIEND_REQUEST_API=Util.API+"cancel_pending_friend_request/";
	private static String ACCEPT_REJECT_FRIEND_REQUEST_API = Util.API+"friend_request/";

	private String mFriendRequestFromUser,mFriendRequestToUser,mAcceptRejectStatus;
	private String mUnFriendUserId,mCancelPendingFriendRequestId,mAcceptRejectFriendRequestId;

	public SearchAdapterNoFilter(Activity contex_a,ArrayList<SearchModel> listData){
		this.listData = listData;
		//layoutInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		this.contex_a = contex_a;
		prova = contex_a;

		options = new DisplayImageOptions.Builder()
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
		// TODO Auto-generated method stub
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		final SearchModel connectionsItem = (SearchModel) listData.get(position);
		if (layoutInflater == null)
			layoutInflater = (LayoutInflater) prova.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.custom_search_members_1, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.search_user_name);
			holder.image = (ImageView) convertView.findViewById(R.id.search_user_image);
			holder.connectImage = (ImageView) convertView.findViewById(R.id.search_members_check_connect);

			convertView.setTag(holder);		

		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		// addind Font 
		Typeface typeFace=Typeface.createFromAsset(prova.getAssets(),"fonts/BentonSans-Regular.otf");
		if(connectionsItem.getLastname()==null || connectionsItem.getLastname().equalsIgnoreCase("null") || connectionsItem.getLastname().equalsIgnoreCase(" "))
		{
			holder.name.setText(connectionsItem.getFirstname());
		}else
		{
			holder.name.setText(connectionsItem.getFirstname() +" "+ connectionsItem.getLastname());
		}

		holder.name.setTypeface(typeFace); 

		String imageUrl = connectionsItem.getUser_profile_photo_path();

		imageLoader.displayImage(imageUrl, holder.image, options, new SimpleImageLoadingListener() {
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


		// check for connected,pending,accept friend request

		// is friend
		if(connectionsItem.getIs_friend().equalsIgnoreCase("true")){
			holder.connectImage.setImageDrawable(prova.getResources().getDrawable(R.drawable.unfriend));
			holder.connectImage.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					mConnectFlag=1;
					mConnectListPosition = position;
					mUnFriendUserId = connectionsItem.getUser_id();
					mConnectAlert = getCheckConnectAlert("Do you want to unfriend?");
					mConnectAlert.show();
				}

			});
		}
		// got a friend request 
		else if(connectionsItem.getIs_accept_friend_request().equalsIgnoreCase("true")){
			holder.connectImage.setImageDrawable(prova.getResources().getDrawable(R.drawable.accept));
			holder.connectImage.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					mConnectFlag =2;
					mConnectListPosition = position;
					mAcceptRejectFriendRequestId = connectionsItem.getAccept_friend_request_id();
					mFriendRequestFromUser = connectionsItem.getUser_id();
					mConnectAlert = getCheckConnectAlert("Do you want to accept friend request?");
					mConnectAlert.show();
				}
			});
		}
		// cancel a pending friend request
		else if(connectionsItem.getIs_pending_friend_request().equalsIgnoreCase("true")){

			holder.connectImage.setImageDrawable(prova.getResources().getDrawable(R.drawable.pending));
			holder.connectImage.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					mConnectFlag=3;
					mConnectListPosition = position;
					mCancelPendingFriendRequestId = connectionsItem.getPending_friend_request_id();
					mConnectAlert = getCheckConnectAlert("Do you want to cancel friend request?");
					mConnectAlert.show();
				}
			});

		} // to connect 
		else {
			holder.connectImage.setImageDrawable(prova.getResources().getDrawable(R.drawable.addfriend));
			holder.connectImage.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					mConnectFlag =4;
					mConnectListPosition = position;
					mFriendRequestToUser = connectionsItem.getUser_id();
					mConnectAlert = getCheckConnectAlert("Do you want to send a friend request?");
					mConnectAlert.show();	
				}
			});
		}
		return convertView;
	} 

	static class ViewHolder{
		ImageView image,connectImage;
		TextView name;
	}

	// dialog for different connect
	public AlertDialog getCheckConnectAlert(String message){

		mCheckConnectAlert = new AlertDialog.Builder(prova);
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
						/*case 2:
				System.out.println("Url:"+ACCEPT_REJECT_FRIEND_REQUEST_API);
				mAcceptRejectStatus="1";
				new PutAcceptRejectFriendRequest().execute();
				break;
						 */case 3:
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
					/*if(mConnectFlag == 2){
				mAcceptRejectStatus="0";
				new PutAcceptRejectFriendRequest().execute();
			}*/
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
				mPDialog = Util.createProgressDialog(prova); 
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
						SearchModel model = (SearchModel) listData.get(mConnectListPosition);
						model.setIs_friend("false");
						notifyDataSetChanged();
					}
					Toast.makeText(prova,result.getString("message"),Toast.LENGTH_LONG).show();
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
				mPDialog = Util.createProgressDialog(prova); 
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
						SearchModel model = (SearchModel) listData.get(mConnectListPosition);
						if(mAcceptRejectStatus.equalsIgnoreCase("0")){
							model.setIs_accept_friend_request("false");
							model.setAccept_friend_request_id(null);
						} else {
							model.setIs_accept_friend_request("false");
							model.setIs_friend("true");
						}

						notifyDataSetChanged();

					}
					Toast.makeText(prova,result.getString("message"),Toast.LENGTH_LONG).show();
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
				mPDialog = Util.createProgressDialog(prova); 
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			String pendingFriendRequestId = params[0];
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
						SearchModel model = (SearchModel) listData.get(mConnectListPosition);
						model.setIs_pending_friend_request("false");
						model.setPending_friend_request_id(null);
						notifyDataSetChanged();
					}
					Toast.makeText(prova,result.getString("message"),Toast.LENGTH_LONG).show();
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
				mPDialog = Util.createProgressDialog(prova); 
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
						SearchModel model = (SearchModel) listData.get(mConnectListPosition);
						model.setIs_pending_friend_request("true");
						JSONObject  response = result.getJSONObject("resultIds");
						model.setPending_friend_request_id(response.getString("friend_request_id"));
						notifyDataSetChanged();			}
					Toast.makeText(prova,result.getString("message"),Toast.LENGTH_LONG).show();
				}
			}catch (JSONException e) { 
				e.printStackTrace();
			}
		}
	}
}



