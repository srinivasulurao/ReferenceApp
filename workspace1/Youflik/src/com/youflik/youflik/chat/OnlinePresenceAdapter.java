package com.youflik.youflik.chat;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.models.ConversationsModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.thirdPartyProfileView.ThirdPartyUserDetailActivity;
import com.youflik.youflik.utils.Util;

public class OnlinePresenceAdapter extends BaseAdapter{

	private ArrayList<PresenceModel> listData;
	private LayoutInflater layoutInflater;
	private Context prova;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	private ProgressDialog mPDialog;

	String dialog_withUserImage;
	String dialog_withUserFirstName;
	String dialog_withUserLastName;
	String dialog_withUserId;
	String dialog_isUserBlocked;
	String dialog_blockID;
	String dialog_with_User_Image;
	String dialog_user_location;
	String dialog_user_bio;

	static String JID;
	static String UserID;
	static String CONVID;

	private ImageView withUserPhoto;

	private TextView userName,userLoc,userBio;

	private Button sendMessage,viewProfile;

	public static Dialog onlineDialog;

	public OnlinePresenceAdapter(Context context,ArrayList<PresenceModel> listData){
		this.listData = listData;
		prova = context;
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder;
		if (layoutInflater == null)
			layoutInflater = (LayoutInflater) prova.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.custom_user_presence, null);
			holder = new ViewHolder();
			holder.userName = (TextView) convertView.findViewById(R.id.presenceUserName);
			holder.userpresenceImage = (ImageView) convertView.findViewById(R.id.onlinefriendimage);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final PresenceModel presenceItem = (PresenceModel)listData.get(position);

		String NameCaps = presenceItem.getUser().split("@")[0];
		String output = NameCaps.substring(0, 1).toUpperCase() + NameCaps.substring(1);
		if(presenceItem.getUserName().toString().length() > 0 ){
			String nameoutput = presenceItem.getUserName().substring(0, 1).toUpperCase() + presenceItem.getUserName().substring(1);
			holder.userName.setText(nameoutput);
		}else{
			holder.userName.setText(output);

		}

		holder.userpresenceImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new LoadImageUrlFromUser().execute(presenceItem.getUser());
			}
		});

		imageLoader.displayImage(presenceItem.getUserImagePath(), holder.userpresenceImage, options, new SimpleImageLoadingListener() {
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
		/*holder.userpresence.setText(presenceItem.getUserStatus());

		if(presenceItem.getUserStatus().equalsIgnoreCase("ONLINE")){
			holder.userpresence.setBackgroundResource(R.color.com_facebook_blue);
		}else{
			holder.userpresence.setBackgroundResource(R.color.white);
		}*/
		return convertView;
	}

	static class ViewHolder{
		TextView userName;
		ImageView userpresenceImage;
	}


	private class LoadImageUrlFromUser extends AsyncTask<String, Void, Void>{

		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(prova);
				mPDialog.show();
			} else mPDialog.show();
		}


		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub

			JID = params[0];
			/*UserID = params[1];
			CONVID = params[2];*/
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API + "getUserDet?user_mail_id="+JID);
			JSONArray userarray;
			JSONObject details;
			try {
				if(jsonObjectRecived!=null){
					if(jsonObjectRecived.getString("error").equalsIgnoreCase("false")){
						userarray = jsonObjectRecived.getJSONArray("userDet");
						details = userarray.getJSONObject(0);

						dialog_withUserImage = details.getString("user_profile_picture_path");
						dialog_withUserFirstName = details.getString("firstname");
						dialog_withUserLastName = details.getString("lastname");
						dialog_withUserId = details.getString("user_id");
						dialog_isUserBlocked = details.getString("is_block");
						dialog_blockID = details.getString("block_id");
						dialog_user_location = details.getString("current_loc");
						dialog_user_bio = details.getString("bio");

					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mPDialog.dismiss();
			final String withUserFullName; 

			if(dialog_withUserLastName.equalsIgnoreCase("null") || dialog_withUserLastName.equalsIgnoreCase("") ){
				withUserFullName = dialog_withUserFirstName;
			}else
			{
				withUserFullName = dialog_withUserFirstName + " " + dialog_withUserLastName	;
			}

			onlineDialog = new Dialog(prova);
			onlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			onlineDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			onlineDialog.setContentView(R.layout.custom_dialog_chat_profile);
			onlineDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

			withUserPhoto = (ImageView)onlineDialog.findViewById(R.id.userImage);
			userName = (TextView)onlineDialog.findViewById(R.id.userName);
			userLoc = (TextView) onlineDialog.findViewById(R.id.userLocation);
			userBio = (TextView) onlineDialog.findViewById(R.id.userBio);
			sendMessage = (Button) onlineDialog.findViewById(R.id.sendMessage);
			viewProfile = (Button) onlineDialog.findViewById(R.id.viewProfile);

			userName.setText(withUserFullName);

			if(dialog_user_location.equalsIgnoreCase("null") || dialog_user_location.equalsIgnoreCase("")){
				userLoc.setText("");
			}else{
				userLoc.setText(dialog_user_location);
			}
			if(dialog_user_bio.equalsIgnoreCase("null") || dialog_user_bio.equalsIgnoreCase("")){
				userBio.setText("");
			}else{
				userBio.setText(dialog_user_bio);
			}


			sendMessage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onlineDialog.dismiss();

					int returnConversationsID = 0;

					// TODO Auto-generated method stub

					DataBaseHandler datebasehandler = new DataBaseHandler(prova);

					ConversationsModel checkConversations = new ConversationsModel();
					checkConversations = datebasehandler.checkConversationID(JID);

					if(checkConversations == null){
						ConversationsModel conversions= new ConversationsModel();
						conversions.setLast_message("");
						conversions.setEnd_time("endtime");
						conversions.setLast_message_direction("in");
						conversions.setLogin_user_display_name(Util.USERNAME);
						conversions.setLogin_user_id(Integer.parseInt(Util.USER_ID));
						conversions.setLogin_user_jid(Util.CHAT_LOGIN_JID);
						conversions.setLogin_user_resource("mobile");
						conversions.setWith_user_display_name(withUserFullName);
						conversions.setWith_user_id(Integer.parseInt("0"));
						conversions.setWith_user_jid(JID);
						conversions.setWith_user_profilepicurl("message.getFrom()");
						conversions.setWith_user_resource("mobile");
						conversions.setStart_time("starttime");
						conversions.setMessage_iseen("yes");
						conversions.setMessage_isseen_count("0");
						datebasehandler.insertConversions(conversions);

						checkConversations = datebasehandler.checkConversationID(JID);
						if(checkConversations == null){

						}else{
							returnConversationsID = checkConversations.getConversation_id();
						}
						//
						//getting the latest conversation id
					}else{
						returnConversationsID = checkConversations.getConversation_id();
					}
					Intent intent = new Intent(prova, ChatMessagingActivity.class);
					intent.putExtra("Connversation_ID", returnConversationsID);
					intent.putExtra("Connversation_JID", JID);
					prova.startActivity(intent);


				}
			});

			viewProfile.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onlineDialog.dismiss();
					Intent intent = new Intent(prova,ThirdPartyUserDetailActivity.class);
					intent.putExtra("UserID", dialog_withUserId);
					Util.THIRD_PARTY_USER_NAME = dialog_withUserFirstName;
					Util.THIRD_PARTY_USER_ID = dialog_withUserId;
					prova.startActivity(intent);

				}
			});

			onlineDialog.show();

			imageLoader.getInstance().displayImage(dialog_withUserImage, withUserPhoto, options, new SimpleImageLoadingListener() {
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
