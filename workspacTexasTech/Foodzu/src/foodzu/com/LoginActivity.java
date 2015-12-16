package foodzu.com;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.Settings.Secure;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import foodzu.com.Utils.URLs;
import foodzu.com.Utils.Utils;

public class LoginActivity extends Activity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final int RC_SIGN_IN = 0;
	String checklogin = "NO";
	// Logcat tag
	private static final String TAG = "MainActivity";

	// Profile pic image size in pixels
	private static final int PROFILE_PIC_SIZE = 400;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	private Button btnLogin;
	private EditText etusername, etpassword;
	private TextView signup;
	private ImageView G_login, F_login;
	SharedPreferences sharedpreferences;
	public static final String MyLogin = "Login";
	CheckBox ShowPwd;
	String username, password;

	private HashMap<String, String> userHashmap;
	// private ArrayList<HashMap<String, String>> friendList;

	private ProgressDialog pd;

	public static final String USER_MAP = "userHashmap";
	public static final String FRIEND_LIST = "friendList";

	public static final String USER_ID = "userId";
	public static final String NAME = "name";
	public static final String USER_NAME = "userName";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String BIRTHDAY = "birthday";
	public static final String GENDER = "gender";
	public static final String EMAIL_ID = "emailId";
	public static final String IMAGE_URL = "imageUrl";
	String useremail, fz_userid;

	public static String G_FIRST_NAME = "firstName";
	public static String G_LAST_NAME = "lastName";
	public static String G_BIRTHDAY = "birthday";
	public static int G_GENDER = 0;
	public static String G_EMAIL_ID = "emailId";
	private String android_id;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// if (pd != null && pd.isShowing())
			// pd.dismiss();

			if (msg.what == 1) {
				if (getIntent().getStringExtra("FB") != null) {
					if (getIntent().getStringExtra("FB").equals("FB_EXE")) {
						finish();

						Editor editor = sharedpreferences.edit();
						editor.putString("f_login", "signin");
						editor.putString("login_status", "OK_FB");
						editor.putString("user_email", useremail);
						editor.putString("user_id", fz_userid);
						editor.commit();

						startActivity(new Intent(LoginActivity.this,
								CheckoutActivity.class).putExtra("GT",
								Utils.GDT));
					}
				} else {
					updateUI(true, "F");
					// startActivity(new Intent(LoginActivity.this,
					// HomeActivity.class));
					onBackPressed();
				}
				// pd.dismiss();
			} else if (msg.what == 2) {
				// pd.dismiss();
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		sharedpreferences = getSharedPreferences(MyLogin, Context.MODE_PRIVATE);
		ShowPwd = (CheckBox) findViewById(R.id.chk_pw);
		signup = (TextView) findViewById(R.id.signup);
		etusername = (EditText) findViewById(R.id.etusername);
		etpassword = (EditText) findViewById(R.id.etpassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		F_login = (ImageView) findViewById(R.id.facebook_icon);
		G_login = (ImageView) findViewById(R.id.google_icon);
		G_login.setOnClickListener(this);
		F_login.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		signup.setOnClickListener(this);

		alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

		android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

		// Initializing google plus api client
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		if (getIntent().getStringExtra("FB") != null)
			if (getIntent().getStringExtra("FB").equals("FB_EXE"))
				F_login.performClick();

		// try {
		// PackageInfo info = getPackageManager().getPackageInfo(
		// getPackageName(), PackageManager.GET_SIGNATURES);
		// for (Signature signature : info.signatures) {
		// MessageDigest md = MessageDigest.getInstance("SHA");
		// md.update(signature.toByteArray());
		// Log.d("KeyHash:",
		// Base64.encodeToString(md.digest(), Base64.DEFAULT));
		// // wcl0bAvRmfH2ekc6ve5WzFMk5S0= (developer)
		//
		// // 2jmj715rSw0yVb/vlWAYkK/YBwk=Remove (release)
		//
		// etpassword.setText(Base64.encodeToString(md.digest(),
		// Base64.DEFAULT));
		// }
		// } catch (NameNotFoundException e) {
		// } catch (NoSuchAlgorithmException e) {
		// }
		ShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// checkbox status is changed from uncheck to checked.
				if (!isChecked) {
					// show password
					etpassword
							.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
				} else {
					// hide password
					etpassword
							.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {

		super.onActivityResult(requestCode, responseCode, intent);
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
		if (checklogin.equals("YES"))
			Session.getActiveSession().onActivityResult(this, requestCode,
					responseCode, intent);
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		Toast.makeText(this, "User logged in with G+!", Toast.LENGTH_LONG)
				.show();
		getProfileInformation();
		new loginwithgoogle_validate().execute();

	}

	private void updateUI(boolean isSignedIn, String access) {
		if (isSignedIn) {

			finish();
			Editor editor = sharedpreferences.edit();
			if (access.equals("N")) {
				editor.putString("login", "success");
				editor.putString("login_status", "OK_N");
				editor.putString("user_email", username);
				editor.putString("user_pword", password);
				editor.putString("user_id", fz_userid);
			} else if (access.equals("G")) {
				editor.putString("g_login", "signin");
				editor.putString("login_status", "OK_G");
				editor.putString("user_email", useremail);
				editor.putString("user_id", fz_userid);

			} else if (access.equals("F")) {
				editor.putString("f_login", "signin");
				editor.putString("login_status", "OK_FB");
				editor.putString("user_email", useremail);
				editor.putString("user_id", fz_userid);
			}
			editor.commit();

			new Intent(LoginActivity.this, HomeActivity.class);

		} else {
			// G_login.setImageResource(R.drawable.google_icon);
		}
	}

	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();

				useremail = Plus.AccountApi.getAccountName(mGoogleApiClient);

				G_EMAIL_ID = useremail;
				G_FIRST_NAME = currentPerson.getDisplayName();
				G_LAST_NAME = "";
				G_GENDER = currentPerson.getGender();
				G_BIRTHDAY = currentPerson.getBirthday();

				Log.e(TAG, "Name: " + personName + ", plusProfile: "
						+ personGooglePlusProfile + ", email: " + useremail
						+ ", Image: " + personPhotoUrl);

				// txtName.setText(personName);
				// txtEmail.setText(email);

				// by default the profile url gives 50x50 px image only
				// we can replace the value with whatever dimension we want by
				// replacing sz=X
				personPhotoUrl = personPhotoUrl.substring(0,
						personPhotoUrl.length() - 2)
						+ PROFILE_PIC_SIZE;

				// new LoadProfileImage(G_login).execute(personPhotoUrl);

			} else {
				Toast.makeText(getApplicationContext(),
						"Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		updateUI(false, "G");
	}

	@Override
	public void onClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				LoginActivity.this);
		switch (v.getId()) {
		case R.id.google_icon:
			google();
			break;
		case R.id.signup:
			startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
			break;
		case R.id.facebook_icon:
			facebook();
			break;
		case R.id.btnLogin:
			username = etusername.getText().toString();
			password = etpassword.getText().toString();
			if (!username.equals("") || !password.equals("")) {
				login();
			} else {
				builder.setMessage("Username or Password field is empty!")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
			break;
		}

	}

	void login() {
		if (new Utils(LoginActivity.this).isNetworkAvailable()) {
			new login_validate().execute();
		} else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									login();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	void google() {
		if (new Utils(LoginActivity.this).isNetworkAvailable()) {
			checklogin = "NO";
			signInWithGplus();
		} else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									google();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	public void facebook() {
		if (new Utils(LoginActivity.this).isNetworkAvailable()) {
			checklogin = "YES";
			logintoFB();
		} else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									facebook();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	public void logintoFB() {
		try {
			Session.openActiveSession(this, true, new Session.StatusCallback() {

				@Override
				public void call(Session session, SessionState state,
						Exception exception) {

					if (session.isOpened()) {
						boolean isPermissionAvailable = false;
						for (int i = 0; i < session.getPermissions().size(); i++) {
							if (session.getPermissions().get(i)
									.contains("email")) {
								// pd = ProgressDialog
								// .show(LoginActivity.this, "", "");
								isPermissionAvailable = true;

								Request.newMeRequest(session,
										new GraphUserCallback() {

											@Override
											public void onCompleted(
													final GraphUser user,
													Response response) {

												if (user != null) {
													getUserInfoFromFacebook(user);
												}
											}
										}).executeAsync();
							}
						}
						if (!isPermissionAvailable)
							getPermissionUserInfo();
					}
				}
			});

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void getUserInfoFromFacebook(final GraphUser user) {
		userHashmap = new HashMap<String, String>();

		userHashmap.put(USER_ID, user.getId());
		userHashmap.put(USER_NAME, user.getUsername());
		userHashmap.put(FIRST_NAME, user.getFirstName());
		userHashmap.put(LAST_NAME, user.getLastName());
		userHashmap.put(BIRTHDAY, user.getBirthday());
		userHashmap.put(GENDER, (String) user.getProperty("gender"));
		userHashmap.put(EMAIL_ID, user.asMap().get("email").toString());
		useremail = user.asMap().get("email").toString();

		new loginwithFB_validate().execute();
	}

	private void getPermissionUserInfo() {
		String[] permissions = { "basic_info", "email" };
		Session.getActiveSession().requestNewReadPermissions(
				new NewPermissionsRequest(LoginActivity.this, Arrays
						.asList(permissions)));
	}

	public class login_validate extends AsyncTask<String, String, String> {

		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				LoginActivity.this);

		protected void onPreExecute() {

			dialog = Utils.createProgressDialog(LoginActivity.this);
			dialog.setCancelable(false);
			dialog.show();

		}

		protected String doInBackground(String... params) {

			// InputStream inputStream = null;
			String result = null;
			String URL = URLs.APP_LOGIN_URL;
			try {

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("emailid", username));
				pairs.add(new BasicNameValuePair("password", password));
				pairs.add(new BasicNameValuePair("applogin", "1"));
				pairs.add(new BasicNameValuePair("type", "N"));

				DefaultHttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(URL);
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				result = EntityUtils.toString(httpResponse.getEntity());

			} catch (Exception e) {
				System.out.println(e);
			}

			return result;
		}

		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj_main = new JSONObject(result);
					String res = jObj_main.getString("success").toString();
					if (res.equals("1")) {
						JSONObject jObj_user = jObj_main
								.getJSONObject("result");
						JSONArray jObj_user_detail = jObj_user
								.getJSONArray("user_details");
						JSONObject jObj_user_id = jObj_user_detail
								.getJSONObject(0);
						fz_userid = jObj_user_id.getString("user_id");
						updateUI(true, "N");
					} else {

						builder.setMessage("Username or Password Mismatch")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});
						AlertDialog alert = builder.create();
						alert.show();
					}
				}
			} catch (JSONException e) {
				System.out.println("Normal: " + e);
			}
		}
	}

	public class loginwithgoogle_validate extends
			AsyncTask<String, String, String> {

		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				LoginActivity.this);

		protected void onPreExecute() {

			dialog = Utils.createProgressDialog(LoginActivity.this);
			dialog.setCancelable(false);
			dialog.show();

		}

		protected String doInBackground(String... params) {

			String result = null;
			try {

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("firstname", G_FIRST_NAME));
				pairs.add(new BasicNameValuePair("lastname", ""));
				if (G_BIRTHDAY != null)
					pairs.add(new BasicNameValuePair("birthday", G_BIRTHDAY));
				else
					pairs.add(new BasicNameValuePair("birthday", ""));
				if (G_GENDER == 1)
					pairs.add(new BasicNameValuePair("gender", "FEMALE"));
				else
					pairs.add(new BasicNameValuePair("gender", "MALE"));
				pairs.add(new BasicNameValuePair("emailid", G_EMAIL_ID));
				pairs.add(new BasicNameValuePair("deviceid", android_id));
				pairs.add(new BasicNameValuePair("type", "G"));
				pairs.add(new BasicNameValuePair("applogin", "1"));

				DefaultHttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(URLs.APP_LOGIN_URL);
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				result = EntityUtils.toString(httpResponse.getEntity());

			} catch (Exception e) {
				System.out.println("Google: " + e);
			}

			return result;
		}

		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj_main = new JSONObject(result);
					String res = jObj_main.getString("success").toString();
					if (res.equals("1")) {
						username = useremail;
						JSONObject jObj_user = jObj_main
								.getJSONObject("result");
						JSONArray jObj_user_detail = jObj_user
								.getJSONArray("user_details");
						JSONObject jObj_user_id = jObj_user_detail
								.getJSONObject(0);
						fz_userid = jObj_user_id.getString("user_id");
						updateUI(true, "G");
					} else {

						builder.setMessage("G+ login not authenticated!")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});
						AlertDialog alert = builder.create();
						alert.show();
					}
				}
			} catch (JSONException e) {
				System.out.println(e);
			}
		}
	}

	public class loginwithFB_validate extends AsyncTask<String, String, String> {

		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				LoginActivity.this);

		protected void onPreExecute() {

			dialog = Utils.createProgressDialog(LoginActivity.this);
			dialog.setCancelable(false);
			dialog.show();

		}

		protected String doInBackground(String... params) {

			String result = null;
			try {

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("firstname", userHashmap
						.get(FIRST_NAME)));
				pairs.add(new BasicNameValuePair("lastname", userHashmap
						.get(LAST_NAME)));
				pairs.add(new BasicNameValuePair("birthday", userHashmap
						.get(BIRTHDAY)));
				pairs.add(new BasicNameValuePair("gender", userHashmap
						.get(GENDER)));
				pairs.add(new BasicNameValuePair("emailid", userHashmap
						.get(EMAIL_ID)));
				pairs.add(new BasicNameValuePair("deviceid", android_id));
				pairs.add(new BasicNameValuePair("type", "F"));
				pairs.add(new BasicNameValuePair("applogin", "1"));

				DefaultHttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(URLs.APP_LOGIN_URL);
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				result = EntityUtils.toString(httpResponse.getEntity());

			} catch (Exception e) {
				System.out.println("Facebook: " + e);
			}

			return result;
		}

		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj_main = new JSONObject(result);
					String res = jObj_main.getString("success").toString();
					if (res.equals("1")) {
						username = useremail;
						JSONObject jObj_user = jObj_main
								.getJSONObject("result");
						JSONArray jObj_user_detail = jObj_user
								.getJSONArray("user_details");
						JSONObject jObj_user_id = jObj_user_detail
								.getJSONObject(0);
						fz_userid = jObj_user_id.getString("user_id");
						handler.sendEmptyMessage(1);
					} else {

						builder.setMessage("Facebook login not authenticated!")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});
						AlertDialog alert = builder.create();
						alert.show();
					}
				}
			} catch (JSONException e) {
				System.out.println("Facebook: " + e);
			}
		}
	}

	// private static String convertInputStreamToString(InputStream inputStream)
	// throws IOException {
	// BufferedReader bufferedReader = new BufferedReader(
	// new InputStreamReader(inputStream));
	// String line = "";
	// String result = "";
	// while ((line = bufferedReader.readLine()) != null)
	// result += line;
	//
	// inputStream.close();
	// return result;
	// }

	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	// private void signOutFromGplus() {
	// if (mGoogleApiClient.isConnected()) {
	// Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
	// mGoogleApiClient.disconnect();
	// mGoogleApiClient.connect();
	// updateUI(false, "G");
	// }
	// }

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	private void resolveSignInError() {

		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	// private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
	// ImageView bmImage;
	//
	// public LoadProfileImage(ImageView bmImage) {
	// this.bmImage = bmImage;
	// }
	//
	// protected Bitmap doInBackground(String... urls) {
	// String urldisplay = urls[0];
	// Bitmap mIcon11 = null;
	// try {
	// InputStream in = new java.net.URL(urldisplay).openStream();
	// mIcon11 = BitmapFactory.decodeStream(in);
	// } catch (Exception e) {
	// Log.e("Error", e.getMessage());
	// e.printStackTrace();
	// }
	// return mIcon11;
	// }
	//
	// protected void onPostExecute(Bitmap result) {
	// bmImage.setImageBitmap(result);
	// }
	// }
}
