package com.youflik.youflik.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jivesoftware.smack.XMPPConnection;
import org.json.JSONObject;

import com.youflik.youflik.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.ImageView;


public class Util {
	public static String IsFriend="is_friend";
	public static String API = "http://api.youflik.com/public/Api/v1/";
	public static String fb_name,fb_gender,fb_email,fb_firstname,fb_lastname,fb_userid,fb_flag,fb_json;
	public static String twitter_uname,twitter_json,twitter_id,twitter_location,twitter_sname,twitter_flag;
	public static boolean block_flag=false;
	// login
	public static String login_facebook_id,login_twitter_id,login_fb_flag,login_tw_flag;
	public static String API_TOKEN;
	public static String LOGINAPI = API + "login";
	public static String USER_ID;
	public static String USERNAME;
	public static String USERPASSWORD;
	public static String USEREMAIL;
	public static String THIRD_PARTY_USER_ID;
	public static String THIRD_PARTY_USER_NAME;
	public static String FIRSTNAME; //displayed in nav menu
	public static String LASTNAME;  // displayed in nav menu
	public static String USER_COVER_PIC;
	public static final String TEMP_PHOTO_FILE_NAME = "youflik_temp_photo.jpg";
	public static final String TEMP_VIDEO_FILE_NAME = "youflik_temp_photo.mp4";

	public static String FB_PROFILE_PIC;
	public static String TW_PROFILE_PIC;
	public static String NM_PROFILE_PIC;
	public static boolean textflag1=true;
	public static boolean textflag2=true;

	// json object for cache
	public static JSONObject Timefeed_Json;
	// PROFILE SONG
	public static MediaPlayer mediaPlayer= new MediaPlayer();

	public static class Extra {
		public static final String IMAGES = "com.nostra13.example.universalimageloader.IMAGES";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}
	// GCM Details Start 

	public static final String GCMSENDER_ID= "452552521137";
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String PROPERTY_APP_VERSION = "appVersion";
	public static final String PROPERTY_REG_ID = "registration_id";
	public static String GCM_KEY_STORE = "";

	// GCM Details End

	//
	// Chat
	public static String HOST = "c";
	public static final int PORT = 5222;
	public static final String SERVICE = "youflik.com";
	public static  String CHAT_USERNAME;
	public static  String CHAT_PASSWORD;
	public static String CHAT_LOGIN_JID;
	public static XMPPConnection connection;;
	public static boolean IncomingChatMessage = false;
	public static String with_User_Image;
	//
	// TIMEFEED FILTER CONTANTS
	public static String feedFilter="All";
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	public static ProgressDialog createProgressDialog(Context mContext) {
		ProgressDialog dialog = new ProgressDialog(mContext);
		try {
			dialog.show();
		} catch (BadTokenException e) {

		}
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.progressdialog);
		ImageView imageView = (ImageView) dialog.findViewById(R.id.spinnerImageView);
		AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
		spinner.start();
		dialog.getWindow().getAttributes().gravity=Gravity.BOTTOM;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
		lp.dimAmount=0.2f;
		dialog.getWindow().setAttributes(lp); 
		//dialog.setIndeterminate(true);
		// dialog.setMessage(Message);
		return dialog;
	}

	/** A method to download json data from url */
	public static String downloadUrl(String strUrl) throws IOException{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try{
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while( ( line = br.readLine()) != null){
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		}catch(Exception e){
			Log.d("Exception while downloading url", e.toString());
		}finally{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}


}
