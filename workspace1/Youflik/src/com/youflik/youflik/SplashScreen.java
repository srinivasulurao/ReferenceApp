package com.youflik.youflik;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.youflik.youflik.chat.ChatConversationsActivity;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.ConversationsMessagesModel;
import com.youflik.youflik.models.ConversationsModel;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.register.RegisterNormal;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.SessionManager;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SplashScreen extends ActionBarActivity {
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;
	private ProgressDialog pDialog;

	// Session Manager Class
	SessionManager session;
	Context context;
	private JSONObject jsonObjRecv,jsonObjUserDetails;
	AlertDialogManager alert = new AlertDialogManager();


	// gcm 
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	static final String TAG = "YOUFLIK GCM";
	String regid;

	private String WhichLogin;
	private String OauthSocial;
	private String OauthSocialUserID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_splash_screen);
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();

		session = new SessionManager(getBaseContext()); 
		// METHOD 1     
		context = getApplicationContext();

		if(session.isLoggedIn()){
			// get user data from session

			HashMap<String, String> user = session.getUserDetails();

			Util.CHAT_USERNAME = user.get(SessionManager.KEY_NAME);
			Util.USERPASSWORD = user.get(SessionManager.KEY_PASSWORD);
			Util.USERNAME = user.get(SessionManager.KEY_NAME);
			Util.USER_ID = user.get(SessionManager.KEY_USER_ID);
			Util.API_TOKEN = user.get(SessionManager.KEY_CSFR_TOKEN);
			Util.CHAT_PASSWORD	 = user.get(SessionManager.KEY_CHAT_PASSWORD);

			OauthSocial = user.get(SessionManager.OAUTH_SOCIAL);
			OauthSocialUserID = user.get(SessionManager.OAUTH_USER_ID_SOCIAL);
			WhichLogin = user.get(SessionManager.WHICH_LOGIN);

			ConnectionDetector conn = new ConnectionDetector(SplashScreen.this);
			if(conn.isConnectingToInternet()){
				new LoginAsyncNormal().execute();
			}else{
				Crouton.makeText(SplashScreen.this, getString(R.string.crouton_message), Style.ALERT).show();
			}
			//System.out.println("RAJESH " + Util.admin + Util.API_TOKEN + "   " +Util.USER_ID);
		}else{
			Intent i=new Intent(getBaseContext(),MainScreen.class);
			startActivity(i);
			finish();
		}
	}





	private class LoginAsyncNormal extends AsyncTask<Void, Void, Integer>{

		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(SplashScreen.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected Integer doInBackground(Void... params) {
			JSONObject jsonObjSend = new JSONObject();

			try {

				if(WhichLogin.equalsIgnoreCase("Normal")){
					jsonObjSend.put("username",Util.USERNAME);
					jsonObjSend.put("password",Util.USERPASSWORD);
				}else{
					jsonObjSend.put("oauth",OauthSocial);
					jsonObjSend.put("oauth_user_id",OauthSocialUserID);
				}

			} 
			catch (JSONException e) {
				e.printStackTrace();
			}

			jsonObjRecv = HttpPostClient.sendHttpPost(Util.LOGINAPI, jsonObjSend);
			if(HttpPostClient.statuscode == 200 & jsonObjRecv!=null){

				try {

					if(jsonObjRecv.getString("error").equalsIgnoreCase("false")){
						String logintoken=jsonObjRecv.getString("csrf_token");
						Util.API_TOKEN = logintoken;
						jsonObjUserDetails = jsonObjRecv.getJSONObject("userDet");   
						Util.USER_ID = jsonObjUserDetails.getString("user_id");
						Util.FIRSTNAME = jsonObjUserDetails.getString("firstname");
						Util.USERNAME = jsonObjUserDetails.getString("username");
						Util.CHAT_USERNAME = jsonObjUserDetails.getString("username");
						Util.USEREMAIL = jsonObjUserDetails.getString("email");
						Util.CHAT_PASSWORD = jsonObjRecv.getString("password");
						//
                        Util.NM_PROFILE_PIC=jsonObjUserDetails.getString("user_profile_photo");


						// create session again
						/*session.createLoginSession(Util.USERNAME, Util.USERPASSWORD,
								Util.USER_ID,Util.USEREMAIL,Util.API_TOKEN,Util.CHAT_PASSWORD);*/
					}else{
						return null;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return 1;
		}

		@SuppressLint("NewApi") @Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if(HttpPostClient.statuscode == 500){
				Crouton.makeText(SplashScreen.this, getString(R.string.crouton_message_serverError), Style.ALERT).show();
			}else if(HttpPostClient.statuscode == 504){
				Crouton.makeText(SplashScreen.this, getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
			}
			else if(HttpPostClient.statuscode == 401)
			{
				Crouton.makeText(SplashScreen.this, getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
			}
			else{
				if(result == null){
					Crouton.makeText(SplashScreen.this, getString(R.string.crouton_message_passwordIncorrect), Style.ALERT).show();
				}else{
					// redirect to timefeed 
					if(Util.API_TOKEN.equalsIgnoreCase(""))
					{
						Crouton.makeText(SplashScreen.this, getString(R.string.crouton_message_passwordIncorrect), Style.ALERT).show();
					}
					else
					{

						connect();

						if (checkPlayServices()) {
							gcm = GoogleCloudMessaging.getInstance(context);
							regid = getRegistrationId(context);
							registerInBackground();
						} else {
						}
						Intent i = new Intent(getApplicationContext(),MainActivity.class);
						startActivity(i);
						finish();
					}
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
					/*	 if (type == Presence.Type.available)
							 Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
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

						DataBaseHandler datebasehandler = new DataBaseHandler(SplashScreen.this);
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
											new NotificationCompat.Builder(SplashScreen.this)
									.setSmallIcon(R.drawable.ic_launcher)
									.setContentTitle(checkConversations.getWith_user_display_name())
									.setContentText(1 + " new messages");
									// Creates an explicit intent for an Activity in your app
									Intent resultIntent = new Intent(SplashScreen.this, ChatConversationsActivity.class);

									resultIntent.putExtra("Conversation_Name", checkConversations.getWith_user_display_name());
									resultIntent.putExtra("Conversations_Name_Image", checkConversations.getWith_user_profilepicurl());
									resultIntent.putExtra("Connversation_ID", checkConversations.getConversation_id());
									resultIntent.putExtra("Connversation_JID", checkConversations.getWith_user_jid());

									// The stack builder object will contain an artificial back stack for the
									// started Activity.
									// This ensures that navigating backward from the Activity leads out of
									// your application to the Home screen.
									TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashScreen.this);
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
											new NotificationCompat.Builder(SplashScreen.this)
									.setSmallIcon(R.drawable.ic_launcher)
									.setContentTitle(checkConversations.getWith_user_display_name())
									.setContentText(countformessage + " new messages");
									// Creates an explicit intent for an Activity in your app
									Intent resultIntent = new Intent(SplashScreen.this, ChatConversationsActivity.class);

									resultIntent.putExtra("Conversation_Name", checkConversations.getWith_user_display_name());
									resultIntent.putExtra("Conversations_Name_Image", checkConversations.getWith_user_profilepicurl());
									resultIntent.putExtra("Connversation_ID", checkConversations.getConversation_id());
									resultIntent.putExtra("Connversation_JID", checkConversations.getWith_user_jid());

									// The stack builder object will contain an artificial back stack for the
									// started Activity.
									// This ensures that navigating backward from the Activity leads out of
									// your application to the Home screen.
									TaskStackBuilder stackBuilder = TaskStackBuilder.create(SplashScreen.this);
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