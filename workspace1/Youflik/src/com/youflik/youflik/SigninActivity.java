package com.youflik.youflik;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.youflik.youflik.chat.ChatConversationsActivity;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.forgotpassword.ActivityForgotPassword;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.ConversationsMessagesModel;
import com.youflik.youflik.models.ConversationsModel;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.register.RegisterNormal;
import com.youflik.youflik.utils.SessionManager;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SigninActivity extends ActionBarActivity {
	TextView login_label,forget_password;
	ImageView login_twitter,login_facebook;
	EditText username,password;
	Button Login;
	boolean isFetching = false; 
	String redirectFlag="false";
	private static final List<String> PERMISSIONS = Arrays.asList("email","user_location");
	static String TWITTER_CONSUMER_KEY = "fLvMzkvbwVb6i86kK6Hl3xOGT"; 
	static String TWITTER_CONSUMER_SECRET = "a6XBhhl29SyCUC1cY60zP9fSo3zXSu1HwYVC16lXcg7hILLGDg"; 
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	static final String TWITTER_CALLBACK_URL = "oauth://signin";
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
	private static Twitter twitter;
	private static RequestToken requestToken;
	public static SharedPreferences LoginTwitterSharedPreferences;
	ConnectionDetector conn = new ConnectionDetector(SigninActivity.this);
	private ProgressDialog pDialog;
	private JSONObject jsonObjRecv,jsonObjUserDetails;
	private String logintoken="";
	private String oauth_tw,oauth_user_id_tw,oauth_fb,oauth_user_id_fb;

	// gcm 
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	Context context;
	static final String TAG = "YOUFLIK GCM";
	String regid;

	// Session Manager Class
	SessionManager session;


	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		context = getApplicationContext();
		session = new SessionManager(getBaseContext()); 
		// Check if twitter keys are set
		if(conn.isConnectingToInternet()){
			if(TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0){
				//Crouton.makeText(Join.this, getString(R.string.crouton_message_twitter), Style.INFO).show();
				return;
			}
			// Shared Preferences
			LoginTwitterSharedPreferences = SigninActivity.this.getSharedPreferences("MyPref_twitter_Login", 0);
			CallForData();
		}
		else
		{
			//Crouton.makeText(Join.this, getString(R.string.crouton_message), Style.ALERT).show();
		}
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		Typeface font_style=Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/BentonSans-Regular.otf");
		login_label = (TextView)findViewById(R.id.login_label);
		login_label.setTypeface(font_style);

		forget_password= (TextView)findViewById(R.id.forgetPassword);
		forget_password.setTypeface(font_style);

		username= (EditText)findViewById(R.id.login_username);
		username.setTypeface(font_style);

		password= (EditText)findViewById(R.id.login_password);
		password.setTypeface(font_style);

		login_twitter=(ImageView)findViewById(R.id.login_twitter);
		login_facebook=(ImageView)findViewById(R.id.authButton_login);

		Login=(Button)findViewById(R.id.login_submit);

		Login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(username.getText().toString().trim().length() >= 3 && password.getText().toString().trim().length() >= 6){

					ConnectionDetector conn = new ConnectionDetector(SigninActivity.this);
					if(conn.isConnectingToInternet()){
						InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
						inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
						new LoginAsyncNormal().execute();
					}else{
						Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
					}

				}else{
					//Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_fillall), Style.ALERT).show();
					Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_passwordIncorrect), Style.ALERT).show();
				}


			}
		});

		login_facebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(conn.isConnectingToInternet()){
					performFacebookLogin();
				}else{
					Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
				}

			}
		});

		login_twitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Check if twitter keys are set
				if(conn.isConnectingToInternet()){
					loginToTwitter();
				}
				else
				{
					Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
				}
			}
		});

		forget_password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(conn.isConnectingToInternet()){
					// forget password link
					Intent forgotPasswordIntent = new Intent(SigninActivity.this,ActivityForgotPassword.class);
					startActivity(forgotPasswordIntent);
				}
				else
				{
					Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
				}
			}
		});

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}


	@Override
	public void finish() {
		super.finish();
	}

	private void performFacebookLogin()
	{
		final Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, Arrays.asList("email"));
		Session openActiveSession = Session.openActiveSession(this, true, new Session.StatusCallback()
		{
			@Override
			public void call(Session session, SessionState state, Exception exception)
			{

				/*// Add code to print out the key hash
				try {
					PackageInfo info = getPackageManager().getPackageInfo(
							"com.youflik.youflik", 
							PackageManager.GET_SIGNATURES);
					for (Signature signature : info.signatures) {
						MessageDigest md = MessageDigest.getInstance("SHA");
						md.update(signature.toByteArray());
						Toast.makeText(SigninActivity.this, Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_LONG).show();
						//Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
					}
				} catch (NameNotFoundException e) {

				} catch (NoSuchAlgorithmException e) {

				}*/
				if (session.isOpened() && !isFetching)
				{
					isFetching = true;
					session.requestNewReadPermissions(newPermissionsRequest);
					Request getMe = Request.newMeRequest(session, new GraphUserCallback()
					{
						@Override
						public void onCompleted(GraphUser user, Response response)
						{
							if (user != null)
							{
								org.json.JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
								String email = graphResponse.optString("email");

								String id = graphResponse.optString("id");
							
								Util.login_facebook_id=user.getId().toString();
								Util.login_fb_flag="FACEBOOK";

								oauth_fb=Util.login_fb_flag;
								oauth_user_id_fb=	Util.login_facebook_id;

								ConnectionDetector conn = new ConnectionDetector(SigninActivity.this);
								if(conn.isConnectingToInternet()){
									InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
									inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
									new LoginAsyncFacebook().execute();
								}else{
									Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
								}

								finish();

								if (email == null || email.length() < 0)
								{
									/*System.out.println(
											"Facebook Login"+
													"An email address is required for your account, we could" +
											" not find an email associated with this Facebook account. Please associate a email with this account or login the oldskool way.");*/

									return;
								}
							}
						}
					});
					getMe.executeAsync();
				}
				else
				{
					/*if (!session.isOpened())
						Log.d("FACEBOOK", "!session.isOpened()");
					else
						Log.d("FACEBOOK", "isFetching");*/
				}
			}
		});
	}

	// twitter
	private void loginToTwitter() {
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
				this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));

			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_twitter_info), Style.CONFIRM).show();
		}
	}

	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return LoginTwitterSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	protected void onResume() {
		super.onResume();
	}
	private void logoutFromTwitter() {
		Editor e = LoginTwitterSharedPreferences.edit();
		e.remove(PREF_KEY_OAUTH_TOKEN);
		e.remove(PREF_KEY_OAUTH_SECRET);
		e.remove(PREF_KEY_TWITTER_LOGIN);
		e.commit();
	}
	public void CallForData()
	{
		if (!isTwitterLoggedInAlready()) {
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				try {
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
					Editor e = LoginTwitterSharedPreferences.edit();
					e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_OAUTH_SECRET,accessToken.getTokenSecret());
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit(); // save changes

					long userID = accessToken.getUserId();
					User user = twitter.showUser(userID);
					String username = user.getName();									
					Util.login_twitter_id= Long.toString(userID);
					Util.login_tw_flag="TWITTER";

					oauth_tw=Util.login_tw_flag;
					oauth_user_id_tw=Util.login_twitter_id;
					logoutFromTwitter();
					ConnectionDetector conn = new ConnectionDetector(SigninActivity.this);
					if(conn.isConnectingToInternet()){
						new LoginAsyncTwitter().execute();
						this.finish();
					}else{
						Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
					}

				} catch (Exception e) {
				}
			}

		}// END REDIRECT
	}
	private class LoginAsyncNormal extends AsyncTask<Void, Void, JSONObject>{

		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(SigninActivity.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject jsonObjSend = new JSONObject();

			try {
				jsonObjSend.put("username",username.getText().toString().trim());
				jsonObjSend.put("password",password.getText().toString().trim());
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}

			jsonObjRecv = HttpPostClient.sendHttpPost(Util.LOGINAPI, jsonObjSend);

			return jsonObjRecv;
		}

		@SuppressLint("NewApi") @Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if(HttpPostClient.statuscode == 500)
			{
				Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverError), Style.ALERT).show();
			}
			else if(HttpPostClient.statuscode == 504)
			{
				Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
			}
			else if(HttpPostClient.statuscode==401)
			{
				Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_auth), Style.ALERT).show();
			}
			else if(HttpPostClient.statuscode == 200 & jsonObjRecv!=null)
			{
				try {
					if(jsonObjRecv.getString("error").equalsIgnoreCase("false"))
					{
						if(result.getString("status").equalsIgnoreCase("1"))
						{
							logintoken=jsonObjRecv.getString("csrf_token");
							if(logintoken!=null)
							{
								Util.API_TOKEN=logintoken;

							}
							jsonObjUserDetails = jsonObjRecv.getJSONObject("userDet");   

							Util.USER_ID = jsonObjUserDetails.getString("user_id");
							Util.FIRSTNAME = jsonObjUserDetails.getString("firstname");
							Util.USERNAME = jsonObjUserDetails.getString("username");
							Util.LASTNAME=jsonObjUserDetails.getString("lastname");
							Util.CHAT_USERNAME = jsonObjUserDetails.getString("username");
							Util.USEREMAIL = jsonObjUserDetails.getString("email");
							Util.CHAT_PASSWORD = jsonObjRecv.getString("password");
							//
							Util.NM_PROFILE_PIC=jsonObjUserDetails.getString("user_profile_photo");
							session.createLoginSession(username.getText().toString().trim(), password.getText().toString().trim(),
									Util.USER_ID,Util.USEREMAIL,Util.API_TOKEN,Util.CHAT_PASSWORD,"Normal","Normal","Normal");

							// redirect to timefeed 
							if(logintoken.equalsIgnoreCase(""))
							{
								Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_passwordIncorrect), Style.ALERT).show();
							}
							else
							{
								connect();

								if (checkPlayServices()) {
									gcm = GoogleCloudMessaging.getInstance(context);
									regid = getRegistrationId(context);
									if (regid.isEmpty()) {
										registerInBackground();
									}
								} else {
									Log.i(TAG, "No valid Google Play Services APK found.");
								}
								Intent i = new Intent(getApplicationContext(),MainActivity.class);
								//	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(i);
								//finish();
							}
						}
						else
						{
							Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_passwordIncorrect), Style.ALERT).show();

						}
					}
					else
					{
						Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverError), Style.ALERT).show();

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


	}
	private class LoginAsyncTwitter extends AsyncTask<Void, Void, JSONObject>{

		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(SigninActivity.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject jsonObjSend = new JSONObject();

			try {
				jsonObjSend.put("oauth",oauth_tw);
				jsonObjSend.put("oauth_user_id",oauth_user_id_tw);
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}

			jsonObjRecv = HttpPostClient.sendHttpPost(Util.LOGINAPI, jsonObjSend);
			return jsonObjRecv;
		}

		@SuppressLint("NewApi") @Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if(HttpPostClient.statuscode == 500)
			{
				Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverError), Style.ALERT).show();
			}
			else if(HttpPostClient.statuscode == 504)
			{
				Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
			}
			else if(HttpPostClient.statuscode==401)
			{
				Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_auth), Style.ALERT).show();
			}
			else if(HttpPostClient.statuscode == 200 & jsonObjRecv!=null)
			{
				try {
					if(jsonObjRecv.getString("error").equalsIgnoreCase("false"))
					{
						if(result.getString("status").equalsIgnoreCase("1"))
						{
							logintoken=jsonObjRecv.getString("csrf_token");
							if(logintoken!=null)
							{
								Util.API_TOKEN=logintoken;

							}
							jsonObjUserDetails = jsonObjRecv.getJSONObject("userDet");   

							Util.USER_ID = jsonObjUserDetails.getString("user_id");
							Util.FIRSTNAME = jsonObjUserDetails.getString("firstname");
							Util.USERNAME = jsonObjUserDetails.getString("username");
							Util.LASTNAME=jsonObjUserDetails.getString("lastname");
							Util.CHAT_USERNAME = jsonObjUserDetails.getString("username");
							Util.USEREMAIL = jsonObjUserDetails.getString("email");
							Util.CHAT_PASSWORD = jsonObjRecv.getString("password");
							//
							Util.NM_PROFILE_PIC=jsonObjUserDetails.getString("user_profile_photo");
							session.createLoginSession(username.getText().toString().trim(), password.getText().toString().trim(),
									Util.USER_ID,Util.USEREMAIL,Util.API_TOKEN,Util.CHAT_PASSWORD,"Twitter","Twitter",oauth_user_id_tw);

							// redirect to timefeed 
							if(logintoken.equalsIgnoreCase(""))
							{
								Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_passwordIncorrect), Style.ALERT).show();
							}
							else
							{					connect();
							if (checkPlayServices()) {
								gcm = GoogleCloudMessaging.getInstance(context);
								regid = getRegistrationId(context);
								if (regid.isEmpty()) {
									registerInBackground();
								}
							} else {
							}
							Intent i = new Intent(getApplicationContext(),MainActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

							startActivity(i);}
						}
						else
						{
							Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_passwordIncorrect), Style.ALERT).show();
							// REDIRECT TO SIGNUP
							Intent join = new Intent(SigninActivity.this,JoinActivity.class);
							join.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(join);
							//finish();
						}
					}
					else
					{
						Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverError), Style.ALERT).show();

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


	}
	private class LoginAsyncFacebook extends AsyncTask<Void, Void, JSONObject>{

		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(SigninActivity.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject jsonObjSend = new JSONObject();

			try {
				jsonObjSend.put("oauth",oauth_fb);
				jsonObjSend.put("oauth_user_id",oauth_user_id_fb);
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}

			jsonObjRecv = HttpPostClient.sendHttpPost(Util.LOGINAPI, jsonObjSend);
			return jsonObjRecv;
		}

		@SuppressLint("NewApi") @Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if(HttpPostClient.statuscode == 500)
			{
				Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverError), Style.ALERT).show();
			}
			else if(HttpPostClient.statuscode == 504)
			{
				Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
			}
			else if(HttpPostClient.statuscode==401)
			{
				Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_auth), Style.ALERT).show();
			}
			else if(HttpPostClient.statuscode == 200 & jsonObjRecv!=null)
			{
				try {
					if(jsonObjRecv.getString("error").equalsIgnoreCase("false"))
					{
						if(result.getString("status").equalsIgnoreCase("1"))
						{
							logintoken=jsonObjRecv.getString("csrf_token");
							if(logintoken!=null)
							{
								Util.API_TOKEN=logintoken;

							}
							jsonObjUserDetails = jsonObjRecv.getJSONObject("userDet");   

							Util.USER_ID = jsonObjUserDetails.getString("user_id");
							Util.FIRSTNAME = jsonObjUserDetails.getString("firstname");
							Util.USERNAME = jsonObjUserDetails.getString("username");
							Util.LASTNAME=jsonObjUserDetails.getString("lastname");
							Util.CHAT_USERNAME = jsonObjUserDetails.getString("username");
							Util.USEREMAIL = jsonObjUserDetails.getString("email");
							Util.CHAT_PASSWORD = jsonObjRecv.getString("password");
							//
							Util.NM_PROFILE_PIC=jsonObjUserDetails.getString("user_profile_photo");
							session.createLoginSession(username.getText().toString().trim(), password.getText().toString().trim(),
									Util.USER_ID,Util.USEREMAIL,Util.API_TOKEN,Util.CHAT_PASSWORD,"Facebook","Facebook",oauth_user_id_fb);

							// redirect to timefeed 
							if(logintoken.equalsIgnoreCase(""))
							{
								Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_passwordIncorrect), Style.ALERT).show();
							}
							else
							{
								connect();
								if (checkPlayServices()) {
									gcm = GoogleCloudMessaging.getInstance(context);
									regid = getRegistrationId(context);
									if (regid.isEmpty()) {
										registerInBackground();
									}
								} else {
								}
								Intent i = new Intent(getApplicationContext(),MainActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(i);
							}
						}
						else
						{
							Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_passwordIncorrect), Style.ALERT).show();
							// REDIRECT TO SIGNUP
							Intent join = new Intent(SigninActivity.this,JoinActivity.class);
							join.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(join);
							finish();
						}
					}
					else
					{
						Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverError), Style.ALERT).show();

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */

	public boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						Util.PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				finish();
			}
			return false;
		}
		return true;
	}

	private void sendRegistrationIdToServer(String id) throws IOException {

		JSONObject jsonObjSend = new JSONObject();

		try {
			jsonObjSend.put("user",Util.USERNAME);
			jsonObjSend.put("type","android");
			jsonObjSend.put("token",id);
			Util.GCM_KEY_STORE = id;
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		//System.out.println(Util.USERNAME + id );
		HttpPostClient.sendHttpPost("http://www.youflik.com:8000/subscribe", jsonObjSend);
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */

	public void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(Util.GCMSENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.

					sendRegistrationIdToServer(regid);

					// For this demo: we don't need to send it because the device will send
					// upstream messages to a server that echo back the message using the
					// 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}
		}.execute(null, null, null);
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getYouflikGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(RegisterNormal.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getYouflikGcmPreferences(context);
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Util.PROPERTY_REG_ID, regId);
		editor.putInt(Util.PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	@SuppressLint("NewApi")
	public String getRegistrationId(Context context) {
		final SharedPreferences prefs = getYouflikGcmPreferences(context);
		String registrationId = prefs.getString(Util.PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			return "";
		}

		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.

		int registeredVersion = prefs.getInt(Util.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			return "";
		}

		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}







	public void connect() {
		//final ProgressDialog dialog = ProgressDialog.show(context, "Connecting...", "Please wait...", false);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				// Create a connection
				ConnectionConfiguration connConfig = new ConnectionConfiguration(Util.HOST, Util.PORT, Util.SERVICE);
				Util.connection = new XMPPConnection(connConfig);
				try {
					Util.connection.connect();
				} catch (XMPPException ex) {
					setConnection(null);
				}
				try {

					Util.connection.login(Util.CHAT_USERNAME, Util.CHAT_PASSWORD);

					// Set the status to available
					Presence presence = new Presence(Presence.Type.available);
					Util.connection.sendPacket(presence);
					setConnection(Util.connection);
					/*// VCARD 
					VCard card = new VCard();
					card.load(Util.connection);
					URL url;
					try {
						url = new URL(Util.user_profile_pic);
						card.setAvatar(url);
						card.save(Util.connection);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//VCARD
					 */					Roster roster = Util.connection.getRoster();
					 Collection<RosterEntry> entries = roster.getEntries();
					 for (RosterEntry entry : entries) {

						 Presence entryPresence = roster.getPresence(entry.getUser());
						 Presence.Type type = entryPresence.getType();
						 /*if (type == Presence.Type.available)
						 Log.d("XMPPChatDemoActivity", "Presence : " + entryPresence);*/
					 }
				} catch (XMPPException ex) {
					setConnection(null);
				}
				//dialog.dismiss();
			}
		});
		t.start();
		//dialog.show();

	}

	/**
	 * Called by Settings dialog when a connection is establised with the XMPP
	 * server
	 * 
	 * @param connection
	 */
	public void setConnection(XMPPConnection connection) {
		Util.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message
								.getFrom());

						DataBaseHandler datebasehandler = new DataBaseHandler(SigninActivity.this);
						ConversationsModel checkConversations = new ConversationsModel();
						checkConversations = datebasehandler.checkConversationID(fromName);

						if(checkConversations == null){

							ConversationsModel conversions= new ConversationsModel();
							conversions.setLast_message(message.getBody());
							conversions.setEnd_time("endtime");
							conversions.setLast_message_direction("in");
							conversions.setLogin_user_display_name(fromName);
							conversions.setLogin_user_id(Integer.parseInt(Util.USER_ID));
							conversions.setLogin_user_jid(Util.CHAT_LOGIN_JID);
							conversions.setLogin_user_resource("mobile");
							conversions.setWith_user_display_name(fromName);
							conversions.setWith_user_id(0);
							conversions.setWith_user_jid(fromName);
							conversions.setWith_user_profilepicurl("message.getFrom()");
							conversions.setWith_user_resource("mobile");
							conversions.setStart_time("starttime");
							conversions.setMessage_iseen("no");
							conversions.setMessage_isseen_count("1");
							datebasehandler.insertConversions(conversions);
							//
							//getting the latest conversation id
							checkConversations = datebasehandler.checkConversationID(fromName);
							if(checkConversations == null){

							}else{
								ConversationsMessagesModel messageModel = new ConversationsMessagesModel();
								messageModel.setConvmessage_type("Chat");
								messageModel.setConvmessage_direction("in");
								messageModel.setConvmessage_time("time");
								messageModel.setConvmessage_message(message.getBody());
								messageModel.setConversation_id(checkConversations.getConversation_id());

								if(isApplicationSentToBackground(context)){
									NotificationCompat.Builder mBuilder =
											new NotificationCompat.Builder(SigninActivity.this)
									.setSmallIcon(R.drawable.ic_launcher)
									.setContentTitle(checkConversations.getWith_user_display_name())
									.setContentText(1 + " new messages");
									// Creates an explicit intent for an Activity in your app
									Intent resultIntent = new Intent(SigninActivity.this, ChatConversationsActivity.class);

									resultIntent.putExtra("Conversation_Name", checkConversations.getWith_user_display_name());
									resultIntent.putExtra("Conversations_Name_Image", checkConversations.getWith_user_profilepicurl());
									resultIntent.putExtra("Connversation_ID", checkConversations.getConversation_id());
									resultIntent.putExtra("Connversation_JID", checkConversations.getWith_user_jid());

									// The stack builder object will contain an artificial back stack for the
									// started Activity.
									// This ensures that navigating backward from the Activity leads out of
									// your application to the Home screen.
									TaskStackBuilder stackBuilder = TaskStackBuilder.create(SigninActivity.this);
									// Adds the back stack for the Intent (but not the Intent itself)
									stackBuilder.addParentStack(SplashScreen.class);
									// Adds the Intent that starts the Activity to the top of the stack
									stackBuilder.addNextIntent(resultIntent);
									PendingIntent resultPendingIntent =
											stackBuilder.getPendingIntent(
													0,
													PendingIntent.FLAG_UPDATE_CURRENT
													);
									mBuilder.setContentIntent(resultPendingIntent);

									mBuilder.setAutoCancel(true);
									mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
									//Vibration
									mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

									//LED
									mBuilder.setLights(Color.RED, 3000, 3000);

									NotificationManager mNotificationManager =
											(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
									// mId allows you to update the notification later on.
									mNotificationManager.notify(101, mBuilder.build());
								}



								try{
									datebasehandler.insertConversionsMessages(messageModel);
								}catch(Exception e){
								}finally{
									datebasehandler.close();
								}
							}
						}else{
							ConversationsMessagesModel messageModel = new ConversationsMessagesModel();
							messageModel.setConvmessage_type("Chat");
							messageModel.setConvmessage_direction("in");
							messageModel.setConvmessage_time("time");
							messageModel.setConvmessage_message(message.getBody());
							messageModel.setConversation_id(checkConversations.getConversation_id());
							try{

								datebasehandler.insertConversionsMessages(messageModel);
								int getCount = Integer.valueOf(checkConversations.getMessage_isseen_count());
								getCount = getCount + 1;
								datebasehandler.updateLastMessageinConversations(String.valueOf(checkConversations.getConversation_id()), message.getBody()
										, "time", "in" , "no" , Integer.toString(getCount));

								int countformessage = Integer.parseInt(checkConversations.getMessage_isseen_count()) + 1 ;

								if(isApplicationSentToBackground(context)){
									NotificationCompat.Builder mBuilder =
											new NotificationCompat.Builder(SigninActivity.this)
									.setSmallIcon(R.drawable.ic_launcher)
									.setContentTitle(checkConversations.getWith_user_display_name())
									.setContentText(countformessage + " new messages");
									// Creates an explicit intent for an Activity in your app
									Intent resultIntent = new Intent(SigninActivity.this, ChatConversationsActivity.class);

									resultIntent.putExtra("Conversation_Name", checkConversations.getWith_user_display_name());
									resultIntent.putExtra("Conversations_Name_Image", checkConversations.getWith_user_profilepicurl());
									resultIntent.putExtra("Connversation_ID", checkConversations.getConversation_id());
									resultIntent.putExtra("Connversation_JID", checkConversations.getWith_user_jid());

									// The stack builder object will contain an artificial back stack for the
									// started Activity.
									// This ensures that navigating backward from the Activity leads out of
									// your application to the Home screen.
									TaskStackBuilder stackBuilder = TaskStackBuilder.create(SigninActivity.this);
									// Adds the back stack for the Intent (but not the Intent itself)
									stackBuilder.addParentStack(SplashScreen.class);
									// Adds the Intent that starts the Activity to the top of the stack
									stackBuilder.addNextIntent(resultIntent);
									PendingIntent resultPendingIntent =
											stackBuilder.getPendingIntent(
													0,
													PendingIntent.FLAG_UPDATE_CURRENT
													);
									mBuilder.setContentIntent(resultPendingIntent);

									mBuilder.setAutoCancel(true);
									mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
									//Vibration
									mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

									//LED
									mBuilder.setLights(Color.RED, 3000, 3000);

									NotificationManager mNotificationManager =
											(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
									// mId allows you to update the notification later on.
									mNotificationManager.notify(101, mBuilder.build());
								}




							}catch(Exception e){
							}finally{
								datebasehandler.close();
							}
						}
						Util.IncomingChatMessage = true;
					}
				}
			}, filter);
		}
	}


	public static boolean isApplicationSentToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}

		return false;
	}

}
