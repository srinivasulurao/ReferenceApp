package com.youflik.youflik;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.register.RegisterNormal;
import com.youflik.youflik.register.RegistrationFacebook;
import com.youflik.youflik.register.RegistrationTwitter;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class JoinActivity extends ActionBarActivity {
	TextView join_label,join_email,join_back,join_option;
	ImageView join_twitter,join_facebook;
	LoginButton authButton;
	boolean isFetching = false; 
	private static final List<String> PERMISSIONS = Arrays.asList("email","user_location");
	static String TWITTER_CONSUMER_KEY = "fLvMzkvbwVb6i86kK6Hl3xOGT"; 
	static String TWITTER_CONSUMER_SECRET = "a6XBhhl29SyCUC1cY60zP9fSo3zXSu1HwYVC16lXcg7hILLGDg"; 
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	static final String TWITTER_CALLBACK_URL = "oauth://join";
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
	private static Twitter twitter;
	private static RequestToken requestToken;
	// Shared Preferences
	public static SharedPreferences TwitterSharedPreferences;
	ConnectionDetector conn = new ConnectionDetector(JoinActivity.this);
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);
		//twitter
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		if(conn.isConnectingToInternet()){
			if(TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0){
				//Crouton.makeText(Join.this, getString(R.string.crouton_message_twitter), Style.INFO).show();
				return;
			}
			TwitterSharedPreferences = JoinActivity.this.getSharedPreferences("MyPref_twitter", 0);
			CallForData();
		}
		else
		{
			//Crouton.makeText(Join.this, getString(R.string.crouton_message), Style.ALERT).show();
		}
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		Typeface font_style=Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/BentonSans-Regular.otf");
		join_label = (TextView)findViewById(R.id.join_label);
		join_label.setTypeface(font_style);
		join_email= (TextView)findViewById(R.id.join_email);
		join_email.setTypeface(font_style);
		join_option= (TextView)findViewById(R.id.join_option_email);
		join_option.setTypeface(font_style);
		join_twitter=(ImageView)findViewById(R.id.join_twitter);
		join_facebook=(ImageView)findViewById(R.id.authButton);


		join_facebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(conn.isConnectingToInternet()){
					performFacebookLogin();

				}else{
					Crouton.makeText(JoinActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
				}

			}
		});

		join_twitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(conn.isConnectingToInternet()){
					loginToTwitter();
				}
				else
				{
					Crouton.makeText(JoinActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
				}
			}
		});

		join_email.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(conn.isConnectingToInternet()){
					Intent register = new  Intent(JoinActivity.this,RegisterNormal.class);
					startActivity(register);
				}
				else
				{
					Crouton.makeText(JoinActivity.this, getString(R.string.crouton_message), Style.ALERT).show();
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
		// TODO Auto-generated method stub
		super.finish();
	}
	private void performFacebookLogin()
	{
		final Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, Arrays.asList("basic_info", "email"));
		Session openActiveSession = Session.openActiveSession(this, true, new Session.StatusCallback()
		{
			@Override
			public void call(Session session, SessionState state, Exception exception)
			{
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
								try{
									org.json.JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
									String email = graphResponse.optString("email");
									System.out.println("email"+email);
									String id = graphResponse.optString("id");
									Util.fb_name=user.getName();
									Util.fb_firstname=user.getFirstName().toString();
									Util.fb_lastname=user.getLastName().toString();
									Util.fb_email=user.getProperty("email").toString();
									Util.fb_json=user.getInnerJSONObject().toString();
									Util.fb_gender=user.getProperty("gender").toString();
									Util.fb_userid=user.getId().toString();
									Util.fb_flag="FACEBOOK";

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											Intent registration = new Intent(getApplicationContext(),RegistrationFacebook.class);
											registration.putExtra("fb_name", Util.fb_name);
											registration.putExtra("fb_firstname",Util.fb_firstname);
											registration.putExtra("fb_lastname",Util.fb_lastname);
											registration.putExtra("fb_email",Util.fb_email);
											registration.putExtra("fb_json",Util.fb_json);
											registration.putExtra("fb_gender",Util.fb_gender);
											registration.putExtra("fb_userid",Util.fb_userid);
											registration.putExtra("fb_flag",Util.fb_flag);
											startActivity(registration);
										}

									});



									if (email == null || email.length() < 0)
									{

										return;
									}
								}
								catch(Exception e){

								}
							}
						}
					});
					getMe.executeAsync();
				}
				else
				{
					/*				if (!session.isOpened())
						Log.d("FACEBOOK", "!session.isOpened()");
					else
						Log.d("FACEBOOK", "isFetching");*/
				}
			}
		});
	}

	// twitter
	private void loginToTwitter() {
		// Check if already logged in
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
			Crouton.makeText(JoinActivity.this, getString(R.string.crouton_message_twitter_info), Style.CONFIRM).show();

		}
	}

	private boolean isTwitterLoggedInAlready() {
		return TwitterSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	protected void onResume() {
		super.onResume();
	}
	private void logoutFromTwitter() {
		Editor e = TwitterSharedPreferences.edit();
		e.remove(PREF_KEY_OAUTH_TOKEN);
		e.remove(PREF_KEY_OAUTH_SECRET);
		e.remove(PREF_KEY_TWITTER_LOGIN);
		e.commit();
	}
	public void CallForData()
	{
		// Redirect 
		if (!isTwitterLoggedInAlready()) {
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				try {
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
					Editor e = TwitterSharedPreferences.edit();

					e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_OAUTH_SECRET,accessToken.getTokenSecret());
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit(); // save changes

					//Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

					long userID = accessToken.getUserId();
					User user = twitter.showUser(userID);
					String username = user.getName();									

					// setting data for intent
					Util.twitter_uname=user.getName().toString();
					Util.twitter_sname=user.getScreenName().toString();
					Util.twitter_location=user.getLocation().toString();
					Util.twitter_json=twitter.showUser(userID).toString();
					Util.twitter_id= Long.toString(userID);
					Util.twitter_flag="TWITTER";
					this.finish();
					logoutFromTwitter();
					Intent register_Twitter = new Intent(JoinActivity.this,RegistrationTwitter.class);
					register_Twitter.putExtra("tw_name", Util.twitter_uname);
					register_Twitter.putExtra("tw_screen",Util.twitter_sname);
					register_Twitter.putExtra("tw_location",Util.twitter_location);
					register_Twitter.putExtra("tw_json",	Util.twitter_json);
					register_Twitter.putExtra("tw_id",Util.twitter_id);
					register_Twitter.putExtra("tw_flag",Util.twitter_flag);

					startActivity(register_Twitter);

				} catch (Exception e) {
					// Check log for login errors
					//Log.e("Twitter Login Error", "> " + e.getMessage()); 
				}
			}

		}// END REDIRECT
	}
}

