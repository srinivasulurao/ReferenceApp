package com.youflik.youflik.chatadapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
import com.youflik.youflik.chat.ChatMessagingActivity;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.models.ConversationsModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.thirdPartyProfileView.ThirdPartyUserDetailActivity;
import com.youflik.youflik.utils.Util;

public class ConversationsAdapter extends BaseAdapter{

	private ArrayList<ConversationsModel> listData;
	private LayoutInflater layoutInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Context prova;

	String dialog_withUserImage;
	String dialog_withUserFirstName;
	String dialog_withUserLastName;
	String dialog_withUserId;
	String dialog_isUserBlocked;
	String dialog_blockID;
	String dialog_with_User_Image;
	String dialog_user_location;
	String dialog_user_bio;
	String dialog_is_user_blocked;

	static String JID;
	static String UserID;
	static String CONVID;

	private ImageView withUserPhoto;

	private TextView userName,userLoc,userBio;

	private Button sendMessage,viewProfile;

	public static Dialog replyDialog;

	private ProgressDialog mPDialog;

	public ConversationsAdapter(Context context,ArrayList<ConversationsModel> listData){
		this.listData = listData;
		imageLoader = ImageLoader.getInstance();
		prova = context;

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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (layoutInflater == null)
			layoutInflater = (LayoutInflater) prova.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.custom_chat_conversations, null);
			holder = new ViewHolder();
			holder.friendName = (TextView) convertView.findViewById(R.id.conversationsFriendName);
			holder.textMsg = (TextView) convertView.findViewById(R.id.lastconversationmsg);
			holder.textMsgTime = (TextView) convertView.findViewById(R.id.conversationTime);
			holder.friendImage = (ImageView) convertView.findViewById(R.id.chatfriendimage);
			holder.textisseenCount = (TextView) convertView.findViewById(R.id.unseenCount);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		final ConversationsModel conversationsItem = (ConversationsModel)listData.get(position);

		if(conversationsItem.getWith_user_display_name().contains("@")){
			holder.friendName.setText(conversationsItem.getWith_user_display_name().split("@")[0]);
		}else{
			holder.friendName.setText(conversationsItem.getWith_user_display_name());
		}

		if(conversationsItem.getLast_message().contains("$$IMAGE$##$")){
			holder.textMsg.setText("Image");
		}else if(conversationsItem.getLast_message().contains("$$STICKER$##$")){
			holder.textMsg.setText("Sticker");
		}else{
			holder.textMsg.setText(conversationsItem.getLast_message());
		}

		if(conversationsItem.getMessage_iseen().equalsIgnoreCase("no")){
			holder.textisseenCount.setVisibility(View.VISIBLE);
			holder.textisseenCount.setText(conversationsItem.getMessage_isseen_count());
		}else{
			holder.textisseenCount.setVisibility(View.GONE);
		}

		try {
			String endTtimeString = conversationsItem.getEnd_time();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = formatter.parse(endTtimeString);
			SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

			//for yesterday
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			String yesterday =  formater.format(cal.getTime());

			// for today 
			Date todayDate = new Date();
			String todaydate = formater.format(todayDate);


			if(yesterday.equalsIgnoreCase(formater.format(date))){
				holder.textMsgTime.setText("YESTERDAY");
			}else if(todaydate.equalsIgnoreCase(formater.format(date))){
				//holder.textMsgTime.setText("TODAY");
				SimpleDateFormat timeformaterfull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date datetime = timeformaterfull.parse(endTtimeString);
				SimpleDateFormat timeformater = new SimpleDateFormat("h:mm a");
				holder.textMsgTime.setText(timeformater.format(datetime));
			}else{
				holder.textMsgTime.setText(formater.format(date));
			}



		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		holder.friendImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new LoadImageUrlFromUser().execute(conversationsItem.getWith_user_jid(),String.valueOf(conversationsItem.getWith_user_id())
						,String.valueOf(conversationsItem.getConversation_id()));
			}
		});
		//holder.textMsgTime.setText(conversationsItem.getEnd_time());

		imageLoader.displayImage(conversationsItem.getWith_user_profilepicurl(), holder.friendImage, options, new SimpleImageLoadingListener() {
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

		return convertView;
	}

	static class ViewHolder{
		ImageView friendImage;
		TextView friendName;
		TextView textMsgTime;
		TextView textMsg;
		TextView textisseenCount;
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
			UserID = params[1];
			CONVID = params[2];
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
						dialog_is_user_blocked =  details.getString("is_user_blocked");

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

			replyDialog = new Dialog(prova);
			replyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			replyDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			replyDialog.setContentView(R.layout.custom_dialog_chat_profile);
			replyDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

			/*DisplayMetrics metrics = prova.getResources().getDisplayMetrics();
			int screenWidth = (int) (metrics.widthPixels * 0.80);
			replyDialog.getWindow().setLayout(screenWidth, LayoutParams.WRAP_CONTENT); //set below the setContentview
			 */
			withUserPhoto = (ImageView)replyDialog.findViewById(R.id.userImage);
			userName = (TextView)replyDialog.findViewById(R.id.userName);
			userLoc = (TextView) replyDialog.findViewById(R.id.userLocation);
			userBio = (TextView) replyDialog.findViewById(R.id.userBio);
			sendMessage = (Button) replyDialog.findViewById(R.id.sendMessage);
			viewProfile = (Button) replyDialog.findViewById(R.id.viewProfile);

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
					replyDialog.dismiss();
					DataBaseHandler db = new DataBaseHandler(prova);
					db.changeIsseen(JID, "yes", "0");
					Intent intent = new Intent(prova, ChatMessagingActivity.class);
					intent.putExtra("Conversation_Name", withUserFullName);
					intent.putExtra("Conversations_Name_Image", dialog_with_User_Image);
					intent.putExtra("Connversation_ID", Integer.parseInt(CONVID));
					intent.putExtra("Connversation_JID", JID);
					prova.startActivity(intent);
				}
			});

			viewProfile.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					replyDialog.dismiss();
					Intent intent = new Intent(prova,ThirdPartyUserDetailActivity.class);
					intent.putExtra("UserID", dialog_withUserId);
					Util.THIRD_PARTY_USER_NAME = dialog_withUserFirstName;
					Util.THIRD_PARTY_USER_ID = dialog_withUserId;
					prova.startActivity(intent);

				}
			});

			if(dialog_isUserBlocked.equalsIgnoreCase("0") && dialog_is_user_blocked.equalsIgnoreCase("0")){
				replyDialog.show();
			}

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
