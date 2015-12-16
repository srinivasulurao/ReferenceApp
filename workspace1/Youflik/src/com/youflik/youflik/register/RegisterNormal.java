package com.youflik.youflik.register;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.youflik.youflik.MainActivity;
import com.youflik.youflik.R;
import com.youflik.youflik.SplashScreen;
import com.youflik.youflik.chat.ChatConversationsActivity;
import com.youflik.youflik.commonAdapters.genderAdapter;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.ConversationsMessagesModel;
import com.youflik.youflik.models.ConversationsModel;
import com.youflik.youflik.models.GenderModel;
import com.youflik.youflik.proxy.HttpGetClient_NoToken;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.utils.SessionManager;
import com.youflik.youflik.utils.Util;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class RegisterNormal extends Activity{

	private EditText  edit_firstname,edit_username,edit_password,edit_email,edit_lastname;
	private TextView  text_dob,terms;
	private Spinner   spinner_gender;
	private CheckBox terms_check;
	private Calendar calendar;
	private DatePickerDialog.OnDateSetListener date;
	private boolean isValidName;
	Typeface font_style;
	private Button join;
	private ArrayList<GenderModel> gendersearch = new ArrayList<GenderModel>();
	private String gender_name,gender_id;
	private ProgressDialog pDialog;
	private genderAdapter adapter_gender;
	private String getGenderURL = Util.API+"gender";
	private String firstname,username,email,dob,password,lastname;

	JSONArray registerResponse = null;
	private JSONObject jsonObjRecv;

	// gcm 
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	Context context;
	static final String TAG = "YOUFLIK GCM";
	String regid;

	// Session Manager Class
	SessionManager session;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		font_style=Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/BentonSans-Regular.otf");
		Bundle extras = getIntent().getExtras();
		session = new SessionManager(getBaseContext()); 
		initview();
		//setting the hint for gender spinner
		GenderModel genderData = new GenderModel();
		genderData.setGender_id("0");
		genderData.setGendername("Select Gender");
		gendersearch.add(genderData);
		//end
		context = getApplicationContext();
		new GetMemberGender().execute();
		// set the selected gender using onItemSelectedListener
		spinner_gender	.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent,
					View view, int position, long id) {
				GenderModel gender_Item = (GenderModel) spinner_gender.getSelectedItem();
				if (position == 0) {

				} 
				else
				{
					gender_id=gender_Item.getGender_id();
					gender_name=gender_Item.getGendername();		    
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		date =  new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				String dateFormat = "yyyy-MM-dd";
				SimpleDateFormat sdf =  new SimpleDateFormat(dateFormat);
				text_dob.setText(sdf.format(calendar.getTime()));
			}
		};


		//text listener to validate fullname
		edit_firstname.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) { }
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
			@Override
			public void afterTextChanged(Editable s) {
				if(isValidPersonName(edit_firstname))
				{
					edit_firstname.setError(null);
				}
			}
		});

		//text listener to validate username
		edit_username.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) { }
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) { 
				if(edit_username.getText().toString().trim().length()>=6)
				{
					edit_username.setError(null);
				}
			}
			@Override
			public void afterTextChanged(Editable s) {}
		});

		join.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getUserDetails();
			}
		});

		text_dob.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getUserDob();
			}
		});

		terms.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youflik.org/#legal"));
				startActivity(browserIntent);	
			}
		});
	}



	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}



	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}



	private void initview() {
		text_dob      = (TextView) findViewById(R.id.signup_dob);
		text_dob.setTypeface(font_style);

		edit_firstname = (EditText) findViewById(R.id.signup_firstname);
		edit_firstname.setTypeface(font_style);

		edit_lastname= (EditText) findViewById(R.id.signup_lastname);
		edit_lastname.setTypeface(font_style);

		edit_username = (EditText) findViewById(R.id.signup_username);
		edit_username.setTypeface(font_style);

		edit_password = (EditText) findViewById(R.id.signup_password);

		edit_email = (EditText) findViewById(R.id.signup_Email);
		edit_email.setTypeface(font_style);

		spinner_gender = (Spinner) findViewById(R.id.signup_gender);

		terms=(TextView)findViewById(R.id.signup_termsandconditions);
		terms.setTypeface(font_style);

		join=(Button)findViewById(R.id.signup_join);

		terms_check = (CheckBox) findViewById(R.id.signup_checkbox_terms);

		calendar = Calendar.getInstance();

	}
	// async for gender
	private class GetMemberGender extends AsyncTask<Void, Void, ArrayList<GenderModel>> 
	{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(RegisterNormal.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected ArrayList<GenderModel> doInBackground(Void... params) 
		{
			// TODO Auto-generated method stub
			JSONArray genderResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient_NoToken.sendHttpPost(getGenderURL);
			if(jsonObjectRecived != null){

				try{
					genderResponse = jsonObjectRecived.getJSONArray("genderList");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< genderResponse.length();i++){
					GenderModel genderData = new GenderModel();
					JSONObject genderDetails;
					try{
						genderDetails = genderResponse.getJSONObject(i);
						genderData.setGender_id(genderDetails.getString("gender_id"));
						genderData.setGendername(genderDetails.getString("gendername"));

						gendersearch.add(genderData);
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return gendersearch;
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<GenderModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result == null) {
			} /*else if (result.size() == 0) {
			} */else {
				adapter_gender = new genderAdapter(getApplicationContext(), result);
				spinner_gender.setAdapter(adapter_gender);
			}
		}
	}
	// user defined method to check for valid person name
	public boolean isValidPersonName(EditText edt) throws NumberFormatException {
		String name,checkedName = null;
		if (edt.getText().toString().length() <= 0) {
			edt.setError("Name cannot be empty");
			isValidName = false;		
			checkedName = "";
		} else if (!edt.getText().toString().matches("[a-zA-Z ]+")) {
			edt.setError("Enter a valid Name ");
			isValidName = false;
			checkedName = "";
		} else {
			checkedName = edt.getText().toString().trim();
			isValidName = true;
		}
		return isValidName;
	}

	//user defined method to check for valid email 
	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	private void getUserDob() {
		new DatePickerDialog(this,2,date,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();;
	}
	public void clearDetails() {
		// TODO Auto-generated method stub
		edit_firstname.getText().clear();
		edit_firstname.setError(null);
		edit_lastname.getText().clear();
		edit_username.getText().clear();
		edit_username.setError(null);
		edit_password.getText().clear();
		edit_email.getText().clear();
		text_dob.setText("");
		spinner_gender.setSelection(0);
		terms_check.setChecked(false);
	}
	private void getUserDetails() {
		CharSequence cs;
		firstname = edit_firstname.getText().toString().trim();
		lastname=edit_lastname.getText().toString().trim();
		username = edit_username.getText().toString().trim();
		email = edit_email.getText().toString().trim();
		dob = text_dob.getText().toString().trim();
		password = edit_password .getText().toString().trim();

		cs = (CharSequence) email;

		if(firstname.length()<4)
		{
			edit_firstname.setError("Enter atleast 4 characters");
		}
		if(username.length()<6)
		{
			edit_username.setError("Enter atleast 6 characters");
		}
		if(password.length()<6)
		{
			edit_password.setError("Enter atleast 6 characters");
		}
		if(!isEmailValid(cs))
		{
			edit_email.setError("Enter a valid email");
		}

		if(spinner_gender.getSelectedItemPosition()<=0)
		{
			Toast.makeText(RegisterNormal.this,"Please select the gender",Toast.LENGTH_SHORT).show();
		}

		if(dob.length()<=0)
		{
			Toast.makeText(RegisterNormal.this,"Please select the DOB",Toast.LENGTH_SHORT).show();

		}

		if(firstname.length()>4 && (username.length()>=6) && (password.length()>=6) && (isEmailValid(cs)) && (spinner_gender.getSelectedItemPosition()>0) && dob.length()>0)
		{
			if(terms_check.isChecked())
			{
				// call for async for registering the user
				ConnectionDetector conn = new ConnectionDetector(RegisterNormal.this);
				if(conn.isConnectingToInternet()){
					new Register().execute();	
				}else{
					Crouton.makeText(RegisterNormal.this, getString(R.string.crouton_message), Style.ALERT).show();
				}
			}
			else 
			{
				Toast.makeText(RegisterNormal.this, "Accept the terms and conditions", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			//Toast.makeText(RegisterNormal.this,"Enter valid details",Toast.LENGTH_LONG).show();
		}
	}

	public class Register extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(RegisterNormal.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}
		@Override
		protected JSONObject doInBackground(Void... params) {
			String sucess_msg = null;
			JSONObject createRegisterJsonObject = new JSONObject();
			JSONObject response_postUser = null;
			String error = null;
			try {

				createRegisterJsonObject.put("firstname",firstname);
				createRegisterJsonObject.put("username",username);
				createRegisterJsonObject.put("gender_id",gender_id);
				createRegisterJsonObject.put("password",password);
				createRegisterJsonObject.put("email",email);
				createRegisterJsonObject.put("dob",dob);
				createRegisterJsonObject.put("lastname",lastname);

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			response_postUser = HttpPostClient.sendHttpPost(Util.API+ "signup",createRegisterJsonObject);

			return response_postUser;
		}

		@SuppressLint("NewApi") @Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			pDialog.dismiss();
			String responseMessage;
			JSONArray errorMessages;
			if(result!=null)
			{  
				try {
					responseMessage = result.getString("message");
					if(responseMessage.equalsIgnoreCase("successfully registered"))
					{
						String csrftoken =  result.getString("csrf_token");
						Util.API_TOKEN = csrftoken;
						JSONObject jsonObjUserDetails = result.getJSONObject("userDet"); 

						Util.USER_ID = jsonObjUserDetails.getString("user_id");
						Util.FIRSTNAME = jsonObjUserDetails.getString("firstname");
						Util.USERNAME = jsonObjUserDetails.getString("username");
						Util.USEREMAIL = jsonObjUserDetails.getString("email");
						Util.CHAT_USERNAME = jsonObjUserDetails.getString("username");
						Util.CHAT_PASSWORD = result.getString("password");
						Util.NM_PROFILE_PIC=jsonObjUserDetails.getString("user_profile_photo");
						session.createLoginSession(username, password,
								Util.USER_ID,Util.USEREMAIL,Util.API_TOKEN,Util.CHAT_PASSWORD,"Normal","Normal","Normal");

						connect();
						if (checkPlayServices()) {
							gcm = GoogleCloudMessaging.getInstance(context);
							regid = getRegistrationId(context);
							if (regid.isEmpty()) {
								registerInBackground();
							}
						} else {
						}
						Toast.makeText(RegisterNormal.this, responseMessage,Toast.LENGTH_LONG).show();
						//clearDetails(); 

						Intent i = new Intent(getApplicationContext(),MainActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(i);
						//finish();
					}
					else {  
						errorMessages = result.getJSONArray("errorMessages");
						String error="";
						if(errorMessages.length()>0)
						{
							for(int i=0;i<errorMessages.length();i++)
							{
								error  = error.concat(" ").concat(errorMessages.getString(i));
							}
							Toast.makeText(RegisterNormal.this, error,Toast.LENGTH_LONG).show(); 	 
						}
					}
				} catch(JSONException e){
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
		//.println(Util.USERNAME + id );
		HttpPostClient.sendHttpPost("http://www.youflik.me:8000/subscribe", jsonObjSend);
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
					Roster roster = Util.connection.getRoster();
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
						DataBaseHandler datebasehandler = new DataBaseHandler(RegisterNormal.this);
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
											new NotificationCompat.Builder(RegisterNormal.this)
									.setSmallIcon(R.drawable.ic_launcher)
									.setContentTitle(checkConversations.getWith_user_display_name())
									.setContentText(1 + " new messages");
									// Creates an explicit intent for an Activity in your app
									Intent resultIntent = new Intent(RegisterNormal.this, ChatConversationsActivity.class);

									resultIntent.putExtra("Conversation_Name", checkConversations.getWith_user_display_name());
									resultIntent.putExtra("Conversations_Name_Image", checkConversations.getWith_user_profilepicurl());
									resultIntent.putExtra("Connversation_ID", checkConversations.getConversation_id());
									resultIntent.putExtra("Connversation_JID", checkConversations.getWith_user_jid());

									// The stack builder object will contain an artificial back stack for the
									// started Activity.
									// This ensures that navigating backward from the Activity leads out of
									// your application to the Home screen.
									TaskStackBuilder stackBuilder = TaskStackBuilder.create(RegisterNormal.this);
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
											new NotificationCompat.Builder(RegisterNormal.this)
									.setSmallIcon(R.drawable.ic_launcher)
									.setContentTitle(checkConversations.getWith_user_display_name())
									.setContentText(countformessage + " new messages");
									// Creates an explicit intent for an Activity in your app
									Intent resultIntent = new Intent(RegisterNormal.this, ChatConversationsActivity.class);

									resultIntent.putExtra("Conversation_Name", checkConversations.getWith_user_display_name());
									resultIntent.putExtra("Conversations_Name_Image", checkConversations.getWith_user_profilepicurl());
									resultIntent.putExtra("Connversation_ID", checkConversations.getConversation_id());
									resultIntent.putExtra("Connversation_JID", checkConversations.getWith_user_jid());

									// The stack builder object will contain an artificial back stack for the
									// started Activity.
									// This ensures that navigating backward from the Activity leads out of
									// your application to the Home screen.
									TaskStackBuilder stackBuilder = TaskStackBuilder.create(RegisterNormal.this);
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
