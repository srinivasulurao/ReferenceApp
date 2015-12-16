package com.youflik.youflik.chatadapters;

import java.util.ArrayList;
import java.util.Locale;

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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
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
import com.youflik.youflik.chat.ChatOthersConversationActivity;
import com.youflik.youflik.chat.ChatOthersMessagesModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.thirdPartyProfileView.ThirdPartyUserDetailActivity;
import com.youflik.youflik.utils.Util;

public class ChatOthersMessageListAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<ChatOthersMessagesModel> mChatList;
	private ImageLoader imageLoader;
	private DisplayImageOptions profile_options,feed_option;

	// variables used for displaying the dialog on click of the others profile image
	String mDialogStringUserImage,mDialogStringUserFirstName,mDialogStringUserLastName,
	mDialogStringUserId,mDialogStringUserBlocked,mDialogStringblockID,
	mDialogStringUserLocation,mDialogStringUserBio,is_user_blocked,
	mDialogStringUserName,mDialogStringjid;

	private ImageView mDialogUserPhoto;
	private TextView  mDialogUserName,mDialogUserLoc,mDialogUserBio;
	private Button    mDialogSendMessage,mDialogViewProfile;

	public static Dialog replyDialog;

	private ProgressDialog mPDialog;

	public ChatOthersMessageListAdapter(Context context,ArrayList<ChatOthersMessagesModel> list){
		this.mContext = context;
		this.mChatList =  list;

		imageLoader = ImageLoader.getInstance();
		profile_options = new DisplayImageOptions.Builder()
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
		return mChatList.size();
	}

	@Override
	public Object getItem(int position) {

		return mChatList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ChatOthersMessagesModel chatModel = (ChatOthersMessagesModel) mChatList.get(position);
		LayoutInflater inflater = LayoutInflater.from(mContext);;
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.custom_view_fragment_chat_others,parent,false);
			holder.profilePic = (ImageView)convertView.findViewById(R.id.custom_view_fragment_chat_others_profile_pic);
			holder.firstName = (TextView)convertView.findViewById(R.id.custom_view_fragment_chat_others_firstname);
			holder.message = (TextView)convertView.findViewById(R.id.custom_view_fragment_chat_others_message);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder)convertView.getTag();
		}

		//capitalize the first character of the user FirstName
		holder.firstName.setText(
				chatModel.getFirstname().substring(0,1).toUpperCase(Locale.getDefault()).toString()+
				chatModel.getFirstname().substring(1));

		if(chatModel.getBody().contains("$$STICKER$##$")){
			holder.message.setText("Image");
		} else holder.message.setText(chatModel.getBody());

		String profilePicturePath = chatModel.getProfile_photo();
		imageLoader.displayImage(profilePicturePath, holder.profilePic, profile_options, new SimpleImageLoadingListener() {
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

		/*System.out.println("Chat Model data");
		System.out.println("***************");
		System.out.println("Message Users:"+chatModel.getMessageusers());
		System.out.println("ConvId:"+chatModel.getConvid());
		System.out.println("Time:"+chatModel.getTime());
		System.out.println("Body:"+chatModel.getBody());
		System.out.println("Profile Photo:"+chatModel.getProfile_photo());
		System.out.println("First Name:"+chatModel.getFirstname());
		System.out.println("Last Name:"+chatModel.getLastname());
		System.out.println("LocationName:"+chatModel.getLocation_name());
		System.out.println("JID:"+chatModel.getJid());
		System.out.println("Created Date:"+chatModel.getCreated_date());*/

		holder.profilePic.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//System.out.println("OnClick Listener for the Profile Picture");
				new LoadImageUrlFromUser().execute(chatModel.getJid());
			}

		});

		return convertView;
	}

	class ViewHolder{
		private ImageView profilePic; 
		private TextView  firstName,message;
	}


	// Async class used for displaying dialog on click of user profile image
	private class LoadImageUrlFromUser extends AsyncTask<String, Void, Void>{

		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(mContext);
				mPDialog.show();
			} else mPDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub

			String JID = params[0];
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API + "getUserDet?user_mail_id="+JID);
			JSONArray userarray;
			JSONObject details;
			try {
				if(jsonObjectRecived!=null){
					if(jsonObjectRecived.getString("error").equalsIgnoreCase("false")){
						userarray = jsonObjectRecived.getJSONArray("userDet");
						details = userarray.getJSONObject(0);

						mDialogStringUserName = details.getString("username");
						mDialogStringUserImage = details.getString("user_profile_picture_path");
						mDialogStringUserFirstName = details.getString("firstname");
						mDialogStringUserLastName = details.getString("lastname");
						mDialogStringUserId = details.getString("user_id");
						mDialogStringUserBlocked = details.getString("is_block");
						mDialogStringblockID = details.getString("block_id");
						mDialogStringUserLocation = details.getString("current_loc");
						mDialogStringUserBio = details.getString("bio");
						is_user_blocked = details.getString("is_user_blocked");
						mDialogStringjid =JID;
						System.out.println("User Image Path in Background Thread:"+mDialogStringUserImage);

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
			String withUserFullName,firstChar; 

			if(mDialogStringUserLastName.equalsIgnoreCase("null") || mDialogStringUserLastName.equalsIgnoreCase("") ){
				withUserFullName = mDialogStringUserFirstName;

			}else
			{
				withUserFullName = mDialogStringUserFirstName + " " + mDialogStringUserLastName	;
			}
			firstChar = withUserFullName.substring(0,1).toUpperCase(Locale.getDefault()).toString();
			withUserFullName = firstChar+withUserFullName.substring(1);

			replyDialog = new Dialog(mContext);
			replyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			replyDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			replyDialog.setContentView(R.layout.custom_dialog_chat_profile);
			replyDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

			/*DisplayMetrics metrics = prova.getResources().getDisplayMetrics();
			int screenWidth = (int) (metrics.widthPixels * 0.80);
			replyDialog.getWindow().setLayout(screenWidth, LayoutParams.WRAP_CONTENT); //set below the setContentview
			 */
			mDialogUserPhoto = (ImageView)replyDialog.findViewById(R.id.userImage);
			mDialogUserName = (TextView)replyDialog.findViewById(R.id.userName);
			mDialogUserLoc = (TextView) replyDialog.findViewById(R.id.userLocation);
			mDialogUserBio = (TextView) replyDialog.findViewById(R.id.userBio);
			mDialogSendMessage = (Button) replyDialog.findViewById(R.id.sendMessage);
			mDialogViewProfile = (Button) replyDialog.findViewById(R.id.viewProfile);

			mDialogUserName.setText(withUserFullName);
			mDialogUserLoc.setText(mDialogStringUserLocation);

			if( (mDialogStringUserBio == null) || mDialogStringUserBio.equalsIgnoreCase("null")){
				mDialogUserBio.setText("");
			} else mDialogUserBio.setText(mDialogStringUserBio);

			System.out.println("User Profile Picture path in Post Execute"+mDialogStringUserImage);

			imageLoader.displayImage(mDialogStringUserImage, mDialogUserPhoto, profile_options, new SimpleImageLoadingListener() {
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
			mDialogSendMessage.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					replyDialog.dismiss();
					Intent sendMessageIntent = new Intent(mContext,ChatOthersConversationActivity.class);
					sendMessageIntent.putExtra("username",mDialogStringUserName);
					sendMessageIntent.putExtra("firstname",mDialogStringUserFirstName);
					sendMessageIntent.putExtra("lastname",mDialogStringUserLastName);
					sendMessageIntent.putExtra("jid",mDialogStringjid);
					sendMessageIntent.putExtra("profile_image_path",mDialogStringUserImage);
					mContext.startActivity(sendMessageIntent);

				}
			});
			mDialogViewProfile.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					replyDialog.dismiss();
					Intent intent = new Intent(mContext,ThirdPartyUserDetailActivity.class);
					intent.putExtra("UserID", mDialogStringUserId);
					Util.THIRD_PARTY_USER_NAME = mDialogStringUserFirstName;
					Util.THIRD_PARTY_USER_ID = mDialogStringUserId;
					mContext.startActivity(intent);
				}

			});
			if(mDialogStringUserBlocked.equalsIgnoreCase("0") && is_user_blocked.equalsIgnoreCase("0")){
				replyDialog.show();
			}
		}
	}
}