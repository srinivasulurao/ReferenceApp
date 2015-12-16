package com.youflik.youflik.chat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;
import com.youflik.youflik.chat.EmoticonsGridAdapter.KeyClickListener;
import com.youflik.youflik.chatadapters.ChatOthersConversationAdapter;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.proxy.HttpPutClient;
import com.youflik.youflik.thirdPartyProfileView.ThirdPartyUserDetailActivity;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

public class ChatOthersConversationActivity extends FragmentActivity implements KeyClickListener{

	private ListView mChatOthersMessageList;

	private GridView mStickersGridView;
	private Button mSendButton,mMediaButton,listLoadEarlierMessages,mOptionsButton;
	private EditText mEditSendMessage;
	private TextView mOtherUserName;
	private ProgressDialog mPDialog;
	private ImageView mOtherUserProfileImage;
	private LinearLayout mParentLinearLayout,mSendDataLayout;
	private LinearLayout mEmoticonFooter;

	private PopupWindow popupWindow;
	private View popUpView;

	private Bitmap[] emoticons;

	private String mUserName,mMessage,mOtherUserProfileImagePath,
	               mFirstName,mLastName,mJid;
	private ArrayList<ChatOthersConversationModel> mChatOtherMessageList;
	private ArrayList<StickersModel> mStickersArrayList;
	private static  String  GET_CHAT_OTHERS_MESSAGES = Util.API+"other_messages/";
	
	// variables for storing the user details
	private String isBlock,isUserBlocked,mBlockId;
	private String mUserId;

	private static int pageCount = 0;
	private static String pagination_Date_String;
	private boolean flag_loading=false;
	private  String mTimeForLoadMore;
	private int arrayListIntialSize;
	private boolean isKeyBoardVisible;
	private int keyboardHeight;
	    
	private ImageLoader imageLoader;
	private DisplayImageOptions profile_options;

	private ChatOthersConversationAdapter mConversationAdapter;

	private static  int NO_OF_EMOTICONS;
	private AlertDialogManager alert = new AlertDialogManager();  
	@SuppressLint("NewApi") @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_chat_others_messages);
		
		/*ActionBar actionBar = this.getActionBar();
		actionBar.hide();
*/
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

		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			mUserName = extras.getString("username");
			mFirstName = extras.getString("firstname");
			mLastName = extras.getString("lastname");
			mJid = extras.getString("jid");
			mOtherUserProfileImagePath = extras.getString("profile_image_path");
		}
		initView();
        ConnectionDetector conn = new ConnectionDetector(this);
        if(conn.isConnectingToInternet()){
        	System.out.println("JID:"+mJid);
        	new GetUserDetails().execute(mJid);
        } else {
        	alert.showAlertDialog(this,"Connection Error","Check your Internet Connection",false);
        }
		
		if(conn.isConnectingToInternet()){
			new GetChatOthersMessages().execute(mUserName);
			DataBaseHandler db = new DataBaseHandler(ChatOthersConversationActivity.this);
			mStickersArrayList = db.getStickers();
			readEmoticons();
			enablePopUpView();
			checkKeyboardHeight(mParentLinearLayout);
			enableFooterView();} else {
				alert.showAlertDialog(this,"Connection Error","Check your Internet Connection",false);
			}
		/*mChatOthersMessageList.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow.isShowing())
					popupWindow.dismiss();	
				return false;
			}
		});
		 */
		// Defining default height of keyboard which is equal to 230 dip
		final float popUpheight = getResources().getDimension(R.dimen.keyboard_height);
		changeKeyboardHeight((int) popUpheight);
          
		mOptionsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Dialog d = new Dialog(ChatOthersConversationActivity.this);
				d.requestWindowFeature(Window.FEATURE_NO_TITLE);
				AlertDialog.Builder builderSingle = new AlertDialog.Builder(ChatOthersConversationActivity.this);
				final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ChatOthersConversationActivity.this,android.R.layout.simple_list_item_1);
				if(isBlock.equalsIgnoreCase("0") && isUserBlocked.equalsIgnoreCase("0")){
				arrayAdapter.add("View Profile");
				}
				if(isBlock.equalsIgnoreCase("0")){
					arrayAdapter.add("Block User");
				}else{
					arrayAdapter.add("Un-Block User");
				}
				builderSingle.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				builderSingle.setAdapter(arrayAdapter,new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String strName = arrayAdapter.getItem(which);
                        if(strName.equalsIgnoreCase("View Profile")){
							Intent intent = new Intent(ChatOthersConversationActivity.this,ThirdPartyUserDetailActivity.class);
							intent.putExtra("UserID", mUserId);
							Util.THIRD_PARTY_USER_NAME = mUserName;
							Util.THIRD_PARTY_USER_ID = mUserId;
							startActivity(intent);
						}else{

							AlertDialog.Builder builderInner = new AlertDialog.Builder(ChatOthersConversationActivity.this);

							 if(strName.equalsIgnoreCase("Block User")) {
								builderInner.setMessage("Are you sure do you want to Block this user");
							}if(strName.equalsIgnoreCase("Un-Block User")){
								builderInner.setMessage("Are you sure do you want to Un-Block this user");
							}
							builderInner.setPositiveButton("Ok",new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,int which) {
									dialog.dismiss();
									ConnectionDetector conn = new ConnectionDetector(ChatOthersConversationActivity.this);
									if(strName.equalsIgnoreCase("Un-Block User")){	
										if(conn.isConnectingToInternet()){
											isBlock="0";
											mSendDataLayout.setVisibility(View.VISIBLE);
										    new UnBlockUser().execute(mUserId);	
										  } else {
											alert.showAlertDialog(ChatOthersConversationActivity.this,"Connection Error","Check your Internet Connection",false);
										}
									} if(strName.equalsIgnoreCase("Block User")){				
										if(conn.isConnectingToInternet()){
											isBlock="1";
											mSendDataLayout.setVisibility(View.GONE);
										    new BlockUser().execute(mUserId);
										}else {
										alert.showAlertDialog(ChatOthersConversationActivity.this,"Connection Error","Check your Internet Connection",false);
									}
								}
								}	
							});
							builderInner.setNegativeButton("cancel",new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							builderInner.show();
						}	
					}
				});
				builderSingle.show();
			}
		});

		mMediaButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ConnectionDetector conn = new ConnectionDetector(ChatOthersConversationActivity.this);
				if(conn.isConnectingToInternet()){
					if (!popupWindow.isShowing()) {

						popupWindow.setHeight((int) (keyboardHeight));

						if (isKeyBoardVisible) {
							mEmoticonFooter.setVisibility(LinearLayout.GONE);
						} else {
							mEmoticonFooter.setVisibility(LinearLayout.VISIBLE);
						}
						popupWindow.showAtLocation(mParentLinearLayout, Gravity.BOTTOM, 0, 0);

					} else {
						popupWindow.dismiss();
					}
				} else
					alert.showAlertDialog(ChatOthersConversationActivity.this,"Connection Error","Check your Internet Connection",false);

			}

		});

		listLoadEarlierMessages.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ConnectionDetector conn = new ConnectionDetector(ChatOthersConversationActivity.this);
				if(conn.isConnectingToInternet()){
					new GetConversationsLoadMore().execute(mTimeForLoadMore);
				} else {
					alert.showAlertDialog(ChatOthersConversationActivity.this,"Connection Error","Check your internet connection", false);
				}
			}

		});
		mSendButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				mMessage = mEditSendMessage.getText().toString().trim();
				ConnectionDetector conn = new ConnectionDetector(ChatOthersConversationActivity.this);
				if(conn.isConnectingToInternet()){
					if(mMessage.length()<=0){
						Toast.makeText(ChatOthersConversationActivity.this,R.string.string_addSomething,Toast.LENGTH_LONG).show();
					} else {
						new PostSendMessage().execute(mMessage);
					}
				} else {
					alert.showAlertDialog(ChatOthersConversationActivity.this,"Connection Error","Check your internet connection", false);
				}
			}
		});
	}

	/**
	 * Reading all emoticons in local cache
	 */
	private void readEmoticons () {
		NO_OF_EMOTICONS = mStickersArrayList.size();
		emoticons = new Bitmap[NO_OF_EMOTICONS];
		for (short i = 0; i < NO_OF_EMOTICONS; i++) {			
			emoticons[i] = getImage(mStickersArrayList.get(i).getStickerUrl());
		}

	}

	/**
	 * Enabling all content in footer i.e. post window
	 */
	private void enableFooterView() {


		mEditSendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupWindow.isShowing()) {

					popupWindow.dismiss();

				}


			}
		});
	}

	/**
	 * Overriding onKeyDown for dismissing keyboard on key down
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		} 
	}

	/**
	 * Checking keyboard height and keyboard visibility
	 */
	int previousHeightDiffrence = 0;
	private void checkKeyboardHeight(final View parentLayout) {

		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {

						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);

						int screenHeight = parentLayout.getRootView()
								.getHeight();
						int heightDifference = screenHeight - (r.bottom);

						if (previousHeightDiffrence - heightDifference > 50) {							
							popupWindow.dismiss();
						}

						previousHeightDiffrence = heightDifference;
						if (heightDifference > 100) {

							isKeyBoardVisible = true;
							changeKeyboardHeight(heightDifference);

						} else {

							isKeyBoardVisible = false;

						}

					}
				});

	}

	/**
	 * change height of emoticons keyboard according to height of actual
	 * keyboard
	 * 
	 * @param height
	 *            minimum height by which we can make sure actual keyboard is
	 *            open or not
	 */
	private void changeKeyboardHeight(int height) {

		if (height > 100) {
			keyboardHeight = height;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, keyboardHeight);
			mEmoticonFooter.setLayoutParams(params);
		}

	}

	/**
	 * Defining all components of emoticons keyboard
	 */
	private void enablePopUpView() {

		ViewPager pager = (ViewPager) popUpView.findViewById(R.id.emoticons_pager);
		pager.setOffscreenPageLimit(3);


		EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(ChatOthersConversationActivity.this, mStickersArrayList, this);
		pager.setAdapter(adapter);

		// Creating a pop window for emoticons keyboard
		popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT,
				(int) keyboardHeight, false);



		/*TextView backSpace = (TextView) popUpView.findViewById(R.id.back);
		backSpace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
				mEditSendMessage.dispatchKeyEvent(event);	
			}
		});
		 */
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				mEmoticonFooter.setVisibility(LinearLayout.GONE);
			}
		});
	}

	/**
	 * For loading smileys from assets
	 */
	private Bitmap getImage(String path) {

		InputStream is = null;
		try {
			URL url = new URL(path);
			HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
			is = connection.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Bitmap temp = BitmapFactory.decodeStream(is);
		return temp;
	}


	private void initView() {

		mChatOthersMessageList = (ListView)findViewById(R.id.chatlistview);
		mSendButton = (Button)findViewById(R.id.sendMessage);
		mMediaButton = (Button)findViewById(R.id.sendMedia);
		mOptionsButton = (Button) findViewById(R.id.OptionsButton);
		mEditSendMessage = (EditText)findViewById(R.id.messageText);
		mOtherUserProfileImage = (ImageView) findViewById(R.id.with_user_image);
		mOtherUserName = (TextView) findViewById(R.id.withUserName);
		mParentLinearLayout = (LinearLayout)findViewById(R.id.parentRelativeLayout);
		mEmoticonFooter = (LinearLayout)findViewById(R.id.footer_for_emoticons);
		mSendDataLayout = (LinearLayout)findViewById(R.id.sendDataLayout);

		popUpView = getLayoutInflater().inflate(R.layout.others_emoticons_popup, null);
		mStickersGridView = (GridView)popUpView.findViewById(R.id.emoticons_grid);
		// capitalize the first character of first name of other user
		String capsName = mFirstName.substring(0,1).toUpperCase(Locale.getDefault())+mFirstName.substring(1);

		if((mLastName==null) || (mLastName.equalsIgnoreCase("null"))|| (mLastName.equalsIgnoreCase(""))){
			mOtherUserName.setText(capsName);
		}else mOtherUserName.setText(capsName+mLastName);

		imageLoader.displayImage(mOtherUserProfileImagePath,mOtherUserProfileImage, profile_options, new SimpleImageLoadingListener() {
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



		//mMediaButton.setVisibility(View.GONE);
		mChatOthersMessageList.setHeaderDividersEnabled(true);
		listLoadEarlierMessages = new Button(ChatOthersConversationActivity.this);
		listLoadEarlierMessages.setText("Load Earlier Messages");
		listLoadEarlierMessages.setTextSize(16f);
		listLoadEarlierMessages.setGravity(Gravity.CENTER);
		View v = (View) listLoadEarlierMessages;
		mChatOthersMessageList.addHeaderView(v);

		

	}
	public static Bitmap getBitmapFromUrl(String url) {
		Bitmap bitmap = null;
		HttpGet httpRequest = null;
		httpRequest = new HttpGet(url);
		HttpClient httpclient = new DefaultHttpClient();

		HttpResponse response = null;
		try {
			response = (HttpResponse) httpclient.execute(httpRequest);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (response != null) {
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufHttpEntity = null;
			try {
				bufHttpEntity = new BufferedHttpEntity(entity);
			} catch (IOException e) {
				e.printStackTrace();
			}

			InputStream instream = null;
			try {
				instream = bufHttpEntity.getContent();
			} catch (IOException e) {
				e.printStackTrace();
			}

			bitmap = BitmapFactory.decodeStream(instream);
		}
		return bitmap;
	}
	// Async class for loading other chat messages 
	class GetChatOthersMessages extends AsyncTask<String,Void,ArrayList<ChatOthersConversationModel>>{

		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(ChatOthersConversationActivity.this);
			} else mPDialog.show();
		}

		@Override
		protected ArrayList<ChatOthersConversationModel> doInBackground(String... params) {
			String userName = params[0];
			System.out.println("Get others Conversation for user:  "+userName+" "+GET_CHAT_OTHERS_MESSAGES+userName);
			JSONObject receivedJSONResponse = HttpGetClient.sendHttpPost(GET_CHAT_OTHERS_MESSAGES+userName);
			if(receivedJSONResponse != null){
				try{
					JSONArray othersMessageArray = receivedJSONResponse.getJSONArray("conversations");
					mChatOtherMessageList =  new ArrayList<ChatOthersConversationModel>();
					for(int i=0;i<othersMessageArray.length();i++){

						JSONObject messageObject = othersMessageArray.getJSONObject(i);
						ChatOthersConversationModel otherMessage = new ChatOthersConversationModel();

						otherMessage.setTime(messageObject.getString("time"));
						otherMessage.setFromuser(messageObject.getString("fromuser"));
						otherMessage.setTouser(messageObject.getString("touser"));
						otherMessage.setBody(messageObject.getString("body"));		
						otherMessage.setProfile_photo(messageObject.getString("profile_photo"));
						otherMessage.setFirstname(messageObject.getString("firstname"));
						otherMessage.setLastname(messageObject.getString("lastname"));
						otherMessage.setJid(messageObject.getString("jid"));
						otherMessage.setCreated_date(messageObject.getString("created_date"));
						if(i==0){
							mTimeForLoadMore=messageObject.getString("time");
						}
						mChatOtherMessageList.add(otherMessage);
					}
				} catch(JSONException e){
				}
			}

			return mChatOtherMessageList;
		}

		@Override
		protected void onPostExecute(ArrayList<ChatOthersConversationModel> result){
			mPDialog.dismiss();
			if(result == null){

			} else if (result.size()==0){

			} else {
				mChatOthersMessageList.setVisibility(View.VISIBLE);
				mConversationAdapter = new ChatOthersConversationAdapter(ChatOthersConversationActivity.this,mChatOtherMessageList);
				mChatOthersMessageList.setAdapter(mConversationAdapter);
				mChatOthersMessageList.setSelection(mConversationAdapter.getCount());
				arrayListIntialSize = mChatOtherMessageList.size();
				if(mChatOtherMessageList.size()<20){
					listLoadEarlierMessages.setVisibility(View.GONE);
				}
			}
		}
	}

	//Async class for sending the message
	public class PostSendMessage extends AsyncTask<String,Void,String>{
		String responseMessage;
		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(ChatOthersConversationActivity.this);
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			String message = params[0];
			mMessage = message;
			JSONObject sendMessageJSONObject = new JSONObject();
			try{
				sendMessageJSONObject.put("with_username",mUserName);
				sendMessageJSONObject.put("body",message);
				JSONObject receivedJSONResponse = HttpPostClient.sendHttpPost(Util.API+"other_messages",sendMessageJSONObject);
				if(receivedJSONResponse!=null){
					responseMessage = receivedJSONResponse.getString("message");
				}
			}
			catch(JSONException e){
			}
			return responseMessage;
		}
		@Override
		protected void onPostExecute(String result){
			mPDialog.dismiss();
			if(result != null){
				//Toast.makeText(ChatOthersConversationActivity.this,result,Toast.LENGTH_LONG).show();
				ChatOthersConversationModel otherMessage = new ChatOthersConversationModel();

				otherMessage.setFromuser(Util.USERNAME);
				otherMessage.setTouser(mUserName);
				otherMessage.setBody(mMessage);		
				//otherMessage.setProfile_photo(messageObject.getString("profile_photo"));
				otherMessage.setFirstname(Util.FIRSTNAME);
				otherMessage.setLastname(Util.LASTNAME);

				// set the current time for sending a message
				String chatFormat = "yyyy-mm-dd hh:mm:ssa";
				Date currentDate;
				currentDate = Calendar.getInstance().getTime();
				otherMessage.setCreated_date(new SimpleDateFormat(chatFormat).format(currentDate));

				if((mConversationAdapter == null) || (mConversationAdapter.getCount()==0)){
					System.out.println("First message in the conversation");
					mChatOtherMessageList.add(otherMessage);
					mConversationAdapter =  new ChatOthersConversationAdapter(ChatOthersConversationActivity.this,mChatOtherMessageList);
					mChatOthersMessageList.setAdapter(mConversationAdapter);
					listLoadEarlierMessages.setVisibility(View.GONE);
					arrayListIntialSize = arrayListIntialSize+1;
					System.out.println("Size of the array List:"+arrayListIntialSize);
                   } else {
					mChatOtherMessageList.add(otherMessage);	
					mConversationAdapter.notifyDataSetChanged();
					mChatOthersMessageList.setSelection(mConversationAdapter.getCount());
					arrayListIntialSize = arrayListIntialSize+1;
					System.out.println("Size of the array List:"+arrayListIntialSize);
				}
				mEditSendMessage.getText().clear();

			} 
		}	
	}

	// Async class for load more in conversations
	class GetConversationsLoadMore extends AsyncTask<String,Void,ArrayList<ChatOthersConversationModel>>{
		JSONObject receivedJSONResponse ;

		@Override
		protected void onPreExecute(){
			if(mPDialog == null){
				mPDialog = Util.createProgressDialog(ChatOthersConversationActivity.this);
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}

		@Override
		protected ArrayList<ChatOthersConversationModel> doInBackground(String... params) {

			String last_date = params[0];

			System.out.println("EXECUTING THE LOAD MORE BACKGROUND:");
			System.out.println("The Last Date is:"+ last_date);

			receivedJSONResponse = HttpGetClient.sendHttpPost(GET_CHAT_OTHERS_MESSAGES+mUserName+"?last_date="+last_date);	

			if(receivedJSONResponse!=null){

				try {
					JSONArray othersMessageArray = receivedJSONResponse.getJSONArray("conversations");
					if(othersMessageArray.length()>0){
						for(int i=0;i<othersMessageArray.length();i++){

							JSONObject messageObject = othersMessageArray.getJSONObject(i);	
							ChatOthersConversationModel otherMessage = new ChatOthersConversationModel();

							otherMessage.setTime(messageObject.getString("time"));
							otherMessage.setFromuser(messageObject.getString("fromuser"));
							otherMessage.setTouser(messageObject.getString("touser"));
							otherMessage.setBody(messageObject.getString("body"));		
							otherMessage.setProfile_photo(messageObject.getString("profile_photo"));
							otherMessage.setFirstname(messageObject.getString("firstname"));
							otherMessage.setLastname(messageObject.getString("lastname"));
							otherMessage.setJid(messageObject.getString("jid"));
							otherMessage.setCreated_date(messageObject.getString("created_date"));
							if(i==0){
								mTimeForLoadMore=messageObject.getString("time");
							}
							mChatOtherMessageList.add(i,otherMessage);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			return mChatOtherMessageList;
		}
		@Override
		protected void onPostExecute(ArrayList<ChatOthersConversationModel> result){
			super.onPostExecute(result);
			mPDialog.dismiss();
			System.out.println("Check for the values of size of both the arrray list");
			System.out.println("Result array list size:"+result.size());
			System.out.println("Intial ArrayList Size:"+arrayListIntialSize);
			if(result==null){
            }else if (result.size()== arrayListIntialSize){	
				Toast.makeText(ChatOthersConversationActivity.this,R.string.string_noMessageLoad,Toast.LENGTH_LONG).show();
			} else {
				mConversationAdapter.notifyDataSetChanged();	
				//mChatOthersMessageList.setSelection(result.size());
				if(result.size()-arrayListIntialSize<20){
					listLoadEarlierMessages.setVisibility(View.GONE);
				}
				arrayListIntialSize = result.size();
			}
		}
	}
	
	// Async class used for displaying dialog on click of user profile image
		private class GetUserDetails extends AsyncTask<String, Void, Void>{

			@Override
			protected void onPreExecute(){
				if(mPDialog == null){
					mPDialog = Util.createProgressDialog(ChatOthersConversationActivity.this);
					mPDialog.show();
				} else mPDialog.show();
			}

			@Override
			protected Void doInBackground(String... params) {
				// TODO Auto-generated method stub

				String JID = params[0];
				JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API + "getUserDet?user_mail_id="+JID);
				System.out.println("The API string is :"+Util.API + "getUserDet?user_mail_id="+JID);
				JSONArray userarray;
				JSONObject details;
				try {
					if(jsonObjectRecived!=null){
						if(jsonObjectRecived.getString("error").equalsIgnoreCase("false")){
							userarray = jsonObjectRecived.getJSONArray("userDet");
							details = userarray.getJSONObject(0);
							details.getString("user_profile_picture_path");
							details.getString("firstname");
							details.getString("lastname");
							mUserId = details.getString("user_id");
							isBlock = details.getString("is_block");
							mBlockId =details.getString("block_id");
							details.getString("current_loc");
							details.getString("bio");
							isUserBlocked = details.getString("is_user_blocked");
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
				if(isBlock.equalsIgnoreCase("0")&& (isUserBlocked.equalsIgnoreCase("0"))){
					mSendDataLayout.setVisibility(View.VISIBLE);
				} else {
					mSendDataLayout.setVisibility(View.GONE);
				}
			}
		}
		
		private class BlockUser extends AsyncTask<String, Void, String>{

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				String userId = params[0];
				String responseStatus = null;
				JSONObject sendMessageJSONObject = new JSONObject();
				try{
					sendMessageJSONObject.put("blocked_user_id",userId);
					JSONObject receivedJSONResponse = HttpPostClient.sendHttpPost(Util.API+"block",sendMessageJSONObject);
					if(receivedJSONResponse!=null){
						responseStatus = receivedJSONResponse.getString("status");
					}
				}
				catch(JSONException e){
				}
				return responseStatus;
			}
		}

		private class UnBlockUser extends AsyncTask<String, Void, String>{

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				String userId = params[0];
				String responseStatus = null;
				JSONObject sendMessageJSONObject = new JSONObject();
				try{
					sendMessageJSONObject.put("blocked_user_id",userId);
					JSONObject receivedJSONResponse = HttpPutClient.sendHttpPost(Util.API+"block/" +mBlockId,sendMessageJSONObject);
					if(receivedJSONResponse!=null){
						responseStatus = receivedJSONResponse.getString("status");
					}
				}
				catch(JSONException e){
				}
				return responseStatus;
			}
			
		}

	
	@Override
	public void keyClickedIndex(final String index) {
		ImageGetter imageGetter = new ImageGetter() {
			public Drawable getDrawable(String source) {    
				StringTokenizer st = new StringTokenizer(index, ".");
				Drawable d = new BitmapDrawable(index);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}
		};

		Spanned cs = Html.fromHtml("<img src ='"+ index +"'/>", imageGetter, null);        

		int cursorPosition = mEditSendMessage.getSelectionStart();		
		mEditSendMessage.getText().insert(cursorPosition, cs);

	}

	/*class GetStickers extends AsyncTask<Void,Void,ArrayList<String>>{
        @Override
        protected void onPreExecute(){
        	if(mPDialog == null){
        		mPDialog = Util.createProgressDialog(ChatOthersConversationActivity.this);
        		mPDialog.show();
        	}  else mPDialog.show();
        }
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			JSONObject receivedJSONResponse = HttpGetClient.sendHttpPost(Util.API+"stickers");
			if(receivedJSONResponse!=null){
				try {
					JSONArray stickersArray = receivedJSONResponse.getJSONArray("stickers");
					mStickersArrayList = new ArrayList<String>();
					String url;
					for(int i=0;i<stickersArray.length();i++){
						JSONObject stickerObj = stickersArray.getJSONObject(i);
						url = stickerObj.getString("url");
						mStickersArrayList.add(url);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return mStickersArrayList;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result){
			mPDialog.dismiss();
			if(result == null){

			} else if(result.size()==0){

			} else {
			    //Toast.makeText(ChatOthersConversationActivity.this,"No of stickers:"+result.size(),Toast.LENGTH_LONG).show();
				ConnectionDetector conn = new ConnectionDetector(ChatOthersConversationActivity.this);
				if(conn.isConnectingToInternet()){
			    readEmoticons();
				enablePopUpView();
				checkKeyboardHeight(mParentLinearLayout);
				enableFooterView();
				} else {
				 alert.showAlertDialog(ChatOthersConversationActivity.this,"Connection Error","Check your Internet Connection",false);
				}
			}
		}
	}
	 */
} 

