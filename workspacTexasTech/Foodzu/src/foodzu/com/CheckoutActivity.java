package foodzu.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.Settings.Secure;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import foodzu.com.adapters.Cart_CheckoutAdapter;
import foodzu.com.models.Country_Model;
import foodzu.com.models.Data_Models;
import foodzu.com.models.Products;
import foodzu.com.models.Shipping_Model;

@SuppressLint("DefaultLocale")
public class CheckoutActivity extends Activity implements OnItemClickListener,
		OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

	String CHK_login = "NO";
	private ImageView G_login, F_login;
	// G+ variables
	// Google client to interact with Google API

	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;

	public static String G_FIRST_NAME = "firstName";
	public static String G_LAST_NAME = "lastName";
	public static String G_BIRTHDAY = "birthday";
	public static int G_GENDER = 0;
	public static String G_EMAIL_ID = "emailId";
	private String android_id;
	// --------------------------------------

	// FB variables
	private HashMap<String, String> userHashmap;
	public static final String F_USER_MAP = "userHashmap";
	public static final String F_FRIEND_LIST = "friendList";

	public static final String F_USER_ID = "userId";
	public static final String F_NAME = "name";
	public static final String F_USER_NAME = "userName";
	public static final String F_FIRST_NAME = "firstName";
	public static final String F_LAST_NAME = "lastName";
	public static final String F_BIRTHDAY = "birthday";
	public static final String F_GENDER = "gender";
	public static final String F_EMAIL_ID = "emailId";
	public static final String F_IMAGE_URL = "imageUrl";
	String FM_ID = "";
	// =======================================

	Spinner country;
	Button save, cancel;
	Utils UTL = new Utils(CheckoutActivity.this);
	ArrayList<Country_Model> countrylist_array;
	Country_Model CM_DATA;
	Shipping_Model Shipping_Address;
	ArrayList<String> countries;
	private static Data_Models Country_Arraylist;
	AutoCompleteTextView city;
	EditText full_name, home_address, landmark, pincode, phone_num;
	String username_text, address_text, city_text, landmark_text, pincode_text,
			phonenum_text;
	ArrayAdapter<String> adapter;
	String RUN = "NO";
	private static Data_Models Cart_checklist;
	private static ArrayList<Products> Cart_Arraylist;
	ListView cartproduct_list;
	TextView total_amt_value, shipping_amt_value, discount_amt_value,
			payable_amt_value, service_amt_value, vat_amt_value,
			checkout_header_tv, checkout_stage_tv, tvaddress, login_header,
			login_subheader, login_change_emailid, login_mailnotify_text,
			login_emailid_header, login_div, Notify_user;
	Double total_grand_cost = 0.00;
	Button place_order, btnconfirm, btncontinue;
	ImageView iveditaddrress;
	static RelativeLayout main_rel_layout, view_rel_layout, login_layout,
			address_layout;
	static LinearLayout Email_change, chk_password_view, vatview, serviceview;
	RadioButton cod, online, guest_user, foodzu_user;
	EditText Name, EmailId, Mobile, login_emailid;
	private Cart_CheckoutAdapter cart_adapt;
	CheckBox ShowPwd;
	RadioGroup RG;
	SharedPreferences sharedpreferences;
	public static final String MyLogin = "Login";
	String New_Email = "", New_password, address_status = "NO", USER_ID = "";
	static String address_input = "", fz_userid;
	HashMap<String, String> USER;
	JSONObject parent;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	StringBuilder plist;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_checkout);
		ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(R.string.page_checkout);
		actionBar.setDisplayHomeAsUpEnabled(true);

		alertDialogBuilder = new AlertDialog.Builder(CheckoutActivity.this);

		// GOOGLE & FB BUTTON DECLARE
		F_login = (ImageView) findViewById(R.id.facebook_icon);
		G_login = (ImageView) findViewById(R.id.google_icon);
		G_login.setOnClickListener(this);
		F_login.setOnClickListener(this);

		// Address view references
		address_layout = (RelativeLayout) findViewById(R.id.address_layout);
		save = (Button) findViewById(R.id.btnsave);
		cancel = (Button) findViewById(R.id.btncancel);

		full_name = (EditText) findViewById(R.id.etfullname);
		home_address = (EditText) findViewById(R.id.etaddress1);
		city = (AutoCompleteTextView) findViewById(R.id.etcity);
		landmark = (EditText) findViewById(R.id.etaddress2);
		pincode = (EditText) findViewById(R.id.etpincode);
		phone_num = (EditText) findViewById(R.id.etphonenumber);
		country = (Spinner) findViewById(R.id.country);
		countrylist_array = new ArrayList<Country_Model>();
		countries = new ArrayList<String>();
		new getCountryData().execute();

		// Login View references
		login_layout = (RelativeLayout) findViewById(R.id.main_login_layout);
		Email_change = (LinearLayout) findViewById(R.id.Email_change);
		login_header = (TextView) findViewById(R.id.login_header_tv);
		login_subheader = (TextView) findViewById(R.id.login_subheader);
		login_change_emailid = (TextView) findViewById(R.id.login_change_emailid);
		login_mailnotify_text = (TextView) findViewById(R.id.login_text_emailId);
		login_emailid_header = (TextView) findViewById(R.id.login_emailid_header);
		login_emailid = (EditText) findViewById(R.id.login_emailid);
		login_div = (TextView) findViewById(R.id.login_div);
		Notify_user = (TextView) findViewById(R.id.Notify_user);
		btncontinue = (Button) findViewById(R.id.btncontinue);
		ShowPwd = (CheckBox) findViewById(R.id.chk_pw);
		chk_password_view = (LinearLayout) findViewById(R.id.chk_password_view);

		Name = (EditText) findViewById(R.id.name_et);
		EmailId = (EditText) findViewById(R.id.email_et);
		Mobile = (EditText) findViewById(R.id.mobile_et);
		main_rel_layout = (RelativeLayout) findViewById(R.id.main_rel_layout);
		view_rel_layout = (RelativeLayout) findViewById(R.id.view_rel_layout);
		cartproduct_list = (ListView) findViewById(R.id.cartcheckout_list);
		iveditaddrress = (ImageView) findViewById(R.id.ivedit);
		total_amt_value = (TextView) findViewById(R.id.total_amt_value);
		shipping_amt_value = (TextView) findViewById(R.id.shipping_amt_value);
		discount_amt_value = (TextView) findViewById(R.id.discount_amt_value);
		payable_amt_value = (TextView) findViewById(R.id.payable_amt_value);
		vatview = (LinearLayout) findViewById(R.id.vat_view);
		serviceview = (LinearLayout) findViewById(R.id.service_view);
		vat_amt_value = (TextView) findViewById(R.id.vat_amt_value);
		service_amt_value = (TextView) findViewById(R.id.service_amt_value);
		checkout_header_tv = (TextView) findViewById(R.id.checkout_header_tv);
		checkout_stage_tv = (TextView) findViewById(R.id.checkout_stage_tv);
		tvaddress = (TextView) findViewById(R.id.tvaddress);
		place_order = (Button) findViewById(R.id.btnPlaceOrder);
		btnconfirm = (Button) findViewById(R.id.btnconfirm);
		cod = (RadioButton) findViewById(R.id.rbcod);
		online = (RadioButton) findViewById(R.id.rbonline);
		guest_user = (RadioButton) findViewById(R.id.rbguest);
		foodzu_user = (RadioButton) findViewById(R.id.rbfoodzu);
		RG = (RadioGroup) findViewById(R.id.login_mode);
		vatview.setVisibility(View.GONE);
		serviceview.setVisibility(View.GONE);
		if (getIntent().getStringExtra("condition") != null) {
			if (getIntent().getStringExtra("condition").equals("save_address")) {
				view_rel_layout.setVisibility(View.VISIBLE);
				login_layout.setVisibility(View.GONE);
			}
		}

		android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

		// Initializing google plus api client
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		city.setAdapter(new GooglePlacesAutocompleteAdapter(this,
				R.layout.googleplace_list));
		city.setOnItemClickListener(this);

		sharedpreferences = getSharedPreferences(MyLogin, Context.MODE_PRIVATE);
		if (sharedpreferences.getString("login_status", "").equals("OK_N")
				|| sharedpreferences.getString("login_status", "").equals(
						"OK_G")
				|| sharedpreferences.getString("login_status", "").equals(
						"OK_FB")) {
			USER_ID = sharedpreferences.getString("user_id", "");
			Editor editor = sharedpreferences.edit();
			editor.putString("guest_email", "");
			editor.commit();
			login_layout.setVisibility(View.GONE);
			user();
		} else {
			firstlogin();
			login_layout.setVisibility(View.VISIBLE);
		}

		Cart_checklist = new Data_Models();
		Cart_Arraylist = new ArrayList<Products>();

		if (Cart_checklist != null)
			Cart_Arraylist = Cart_checklist.getcartdata();

		cart_adapt = new Cart_CheckoutAdapter(CheckoutActivity.this,
				Cart_Arraylist);
		cartproduct_list.setAdapter(cart_adapt);

		if (getIntent().getStringExtra("GT") != null){
			// for (int i = 0; i < Cart_Arraylist.size(); i++) {
			Utils.GDT = getIntent().getStringExtra("GT");
			total_grand_cost = Double.parseDouble(getIntent().getStringExtra(
					"GT"));
		}
		// + Double.valueOf(Cart_Arraylist.get(i).getfinal_price())
		// * Double.valueOf(Cart_Arraylist.get(i).getitem_qty_count());
		// }

		country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// int position = country.getSelectedItemPosition();
				// Toast.makeText(getApplicationContext(),
				// "You have selected " + countrylist[+position],
				// Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				username_text = ((TextView) full_name).getText().toString();
				address_text = ((TextView) home_address).getText().toString();
				phonenum_text = ((TextView) phone_num).getText().toString();
				pincode_text = ((TextView) pincode).getText().toString();
				city_text = ((TextView) city).getText().toString();
				landmark_text = ((TextView) landmark).getText().toString();

				if (username_text.trim().length() <= 0) {

					Toast.makeText(CheckoutActivity.this,
							"Please enter User Name", Toast.LENGTH_LONG).show();

				} else if (address_text.trim().length() <= 0) {

					Toast.makeText(CheckoutActivity.this,
							"Please enter Full Address", Toast.LENGTH_LONG)
							.show();

				} else if (!(pincode_text.trim().length() == 6)) {

					Toast.makeText(CheckoutActivity.this,
							"Please provide valid Pincode", Toast.LENGTH_LONG)
							.show();

				} else if (city_text.trim().length() <= 0) {

					Toast.makeText(CheckoutActivity.this, "Please enter city",
							Toast.LENGTH_LONG).show();

				} else if (!(phonenum_text.trim().length() == 10)) {

					Toast.makeText(CheckoutActivity.this,
							"Please enter valid 10 digit phone number",
							Toast.LENGTH_LONG).show();

				} else {
					new validatePincode().execute();
				}
			}
		});

		place_order.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SlideToleft();
				productlist();
				view_rel_layout.setVisibility(View.VISIBLE);
				if (Shipping_Address != null) {
					String Address = Shipping_Address.getS_address() + "\n"
							+ Shipping_Address.getS_city() + "\n"
							+ Shipping_Address.getS_landmark() + "\n"
							+ Shipping_Address.getS_pin() + "\n"
							+ Shipping_Address.getS_country();
					if (Shipping_Address.getS_city().toLowerCase()
							.matches("(?i).*bangalore.*")
							|| Shipping_Address.getS_city().toLowerCase()
									.matches("(?i).*bengaluru.*"))
						cod.setVisibility(View.VISIBLE);
					else
						cod.setVisibility(View.GONE);
					Name.setText(Shipping_Address.getS_name());
					EmailId.setText(New_Email);
					tvaddress.setText(Address);
				}

			}
		});

		guest_user.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btncontinue.setText("Continue");
			}
		});

		foodzu_user.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btncontinue.setText("Next");
			}
		});

		cod.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnconfirm.setText("Confirm Now");
			}
		});

		online.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnconfirm.setText("Pay Now");

			}
		});

		btnconfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnconfirm.getText().toString().trim().equals("Pay Now"))
					new online_payment_async().execute();
				else if (btnconfirm.getText().toString().trim()
						.equals("Confirm Now"))
					show_captcha_dialog();

			}
		});

		login_change_emailid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeEmail();
				Notify_user.setVisibility(View.GONE);
				RG.setVisibility(View.VISIBLE);
			}
		});

		btncontinue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btncontinue.getText().equals("Login & Continue")) {
					if (login_emailid.getText().length() == 0) {
						Toast.makeText(CheckoutActivity.this,
								"Not valid Password", Toast.LENGTH_LONG).show();

					} else {
						New_password = login_emailid.getText().toString();
						login();
					}
				} else if (btncontinue.getText().equals("Continue")) {
					if (login_emailid.getText().length() == 0
							|| !(android.util.Patterns.EMAIL_ADDRESS
									.matcher(login_emailid.getText()).matches())) {
						Toast.makeText(CheckoutActivity.this,
								"Not valid Email", Toast.LENGTH_LONG).show();
					} else {
						if (UTL.isNetworkAvailable()) {
							New_Email = login_emailid.getText().toString();
							new validate_userexistence().execute();
						} else
							Toast.makeText(CheckoutActivity.this,
									"Internet not Available! ",
									Toast.LENGTH_LONG).show();
					}
				} else {
					if (android.util.Patterns.EMAIL_ADDRESS.matcher(
							login_emailid.getText()).matches()) {
						New_Email = login_emailid.getText().toString();
						firstlogin_SetPassword();
						login_emailid.setText("");
						RG.setVisibility(View.GONE);

					} else
						Toast.makeText(CheckoutActivity.this,
								"Email Id not Valid", Toast.LENGTH_LONG).show();
				}
			}
		});

		iveditaddrress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(CheckoutActivity.this,
						AddAddress.class).putExtra("Info", USER));
				RUN = "YES";
			}
		});

		ShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!isChecked) {
					// show password
					login_emailid
							.setTransformationMethod(PasswordTransformationMethod
									.getInstance());
				} else {
					// hide password
					login_emailid
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

	void show_captcha_dialog() {

		TextView captcha;
		// final EditText captcha_text;
		final Button btclose;
		final CheckBox accept;

		LayoutInflater li = LayoutInflater.from(CheckoutActivity.this);
		View promptsView = li.inflate(R.layout.dialog_captcha, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				CheckoutActivity.this);
		alertDialogBuilder.setCancelable(true);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		captcha = (TextView) promptsView.findViewById(R.id.captcha);
		// captcha_text = (EditText)
		// promptsView.findViewById(R.id.captcha_text);
		btclose = (Button) promptsView.findViewById(R.id.btn_ok);
		accept = (CheckBox) promptsView.findViewById(R.id.chkterms);
		Random rand = new Random();
		final int num = rand.nextInt(90000) + 10000;

		captcha.setText(Integer.toString(num));
		btclose.setEnabled(false);

		accept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// displayTermsCondition();
				if (accept.isChecked()) {
					btclose.setEnabled(true);
				} else
					btclose.setEnabled(false);
			}
		});
		btclose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if
				// (Integer.parseInt(captcha_text.getText().toString())==num){
				new cod_payment_async().execute();
				alertDialog.cancel();
				// }
				// else
				// Toast.makeText(CheckoutActivity.this,
				// "CAPTCHA MISMATCH! RETRY", Toast.LENGTH_SHORT).show();

			}
		});

	}

	void productlist() {
		plist = new StringBuilder();

		for (int i = 0; i < Cart_Arraylist.size(); i++) {

			plist.append("," + Cart_Arraylist.get(i).getitem_id() + "-"
					+ Cart_Arraylist.get(i).getitem_qty_count() + "-"
					+ Cart_Arraylist.get(i).getpd_wieght());

		}
		System.out.println(plist.toString());
	}

	public String getpaymentcost() {
		String result = null;
		String URL = URLs.CART_COST_URL;
		String C_Code = "";

		for (int i = 0; countrylist_array.size() > i; i++) {
			if (USER.get("User_Country").equals(
					countrylist_array.get(i).getcountry_name()))
				C_Code = countrylist_array.get(i).getcountry_id().toString();
		}
		System.out.println(plist.toString());

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("prodlist", plist.substring(1)));
		pairs.add(new BasicNameValuePair("user_pincode", USER
				.get("User_Pincode")));
		pairs.add(new BasicNameValuePair("user_country", C_Code));
		pairs.add(new BasicNameValuePair("user_city", USER.get("User_City")));

		System.out.println(pairs.toString());

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URL);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);

			result = EntityUtils.toString(httpResponse.getEntity());
			System.out.println("COST RESULT:" + result);
		} catch (Exception e) {

			e.printStackTrace();

			Log.e("COST CAL URL", "Exception : " + e.getMessage(), e);
		}

		return result;
	}

	public String savecart(String status) {
		String result = null;
		String URL = "";
		if (status.equals("ONLINE"))
			URL = URLs.ONLINE_PAY_URL;
		else if (status.equals("COD"))
			URL = URLs.COD_URL;
		String C_Code = "";

		for (int i = 0; countrylist_array.size() > i; i++) {
			if (USER.get("User_Country").equals(
					countrylist_array.get(i).getcountry_name()))
				C_Code = countrylist_array.get(i).getcountry_id().toString();
		}

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if (!USER_ID.equals(""))
			pairs.add(new BasicNameValuePair("user_id", USER_ID));
		else
			pairs.add(new BasicNameValuePair("user_id", sharedpreferences
					.getString("user_id", fz_userid)));
		pairs.add(new BasicNameValuePair("prodlist", plist.substring(1)));
		pairs.add(new BasicNameValuePair("email_id", sharedpreferences
				.getString("user_email", "")));
		pairs.add(new BasicNameValuePair("user_pincode", USER
				.get("User_Pincode")));
		pairs.add(new BasicNameValuePair("user_country", C_Code));
		pairs.add(new BasicNameValuePair("user_city", USER.get("User_City")));

		System.out.println(pairs.toString());

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URL);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);

			result = EntityUtils.toString(httpResponse.getEntity());
			System.out.println("RETURN RESULT:" + result);
		} catch (Exception e) {

			e.printStackTrace();

			Log.e("PAY URL", "Exception : " + e.getMessage(), e);
		}

		return result;
	}

	@Override
	public void onResume() {
		super.onResume();
		sharedpreferences = getSharedPreferences(MyLogin, Context.MODE_PRIVATE);
		if (RUN.equals("YES")) {
			user();
			RUN = "NO";
		}
	}

	void user() {
		if (IsInternetPresent())
			new getUserData().execute();
		else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									user();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	void login() {
		if (IsInternetPresent())
			new login_validate().execute();
		else {
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

	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		String str = (String) adapterView.getItemAtPosition(position);
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public void firstlogin() {
		Email_change.setVisibility(View.GONE);
		chk_password_view.setVisibility(View.GONE);
		login_div.setVisibility(View.GONE);
	}

	public void changeEmail() {
		Email_change.setVisibility(View.GONE);
		chk_password_view.setVisibility(View.GONE);
		login_div.setVisibility(View.GONE);
		// login_header.setText("Create Account");
		login_subheader.setText("Before you place your Order");
		login_emailid_header.setText("Email Address");
		login_emailid.setText(New_Email);
		btncontinue.setText("Continue");
	}

	public void firstlogin_SetPassword() {
		Email_change.setVisibility(View.VISIBLE);
		chk_password_view.setVisibility(View.VISIBLE);
		login_div.setVisibility(View.VISIBLE);

		// login_header.setText("Create Account");
		login_mailnotify_text.setText(New_Email);
		login_emailid_header.setText("Enter Password");
		login_emailid.setHint("Enter Your Password");
		btncontinue.setText("Login & Continue");
		login_subheader.setText("Login to Foodzu account");
	}

	public static void SlideToleft() {
		Animation slide = null;
		slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 5.2f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f);

		slide.setDuration(1000);
		slide.setFillAfter(true);
		slide.setFillEnabled(true);
		view_rel_layout.startAnimation(slide);

		slide.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view_rel_layout.clearAnimation();
			}
		});

	}

	public class login_validate extends AsyncTask<String, String, String> {

		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckoutActivity.this);

		protected void onPreExecute() {
			dialog = Utils.createProgressDialog(CheckoutActivity.this);
			dialog.setCancelable(false);
			if (dialog.isShowing())
				dialog.dismiss();
			dialog.show();

		}

		protected String doInBackground(String... params) {

			// InputStream inputStream = null;
			String result = null;
			String URL = URLs.APP_LOGIN_URL;
			try {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("emailid", New_Email));
				pairs.add(new BasicNameValuePair("password", New_password));
				pairs.add(new BasicNameValuePair("applogin", "1"));
				pairs.add(new BasicNameValuePair("type", "N"));

				DefaultHttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(URL);
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				result = EntityUtils.toString(httpResponse.getEntity());

				// HttpClient httpclient = new DefaultHttpClient();
				// HttpResponse httpResponse = httpclient.execute(new
				// HttpGet(URL
				// + New_Email + "&password=" + New_password));
				// inputStream = httpResponse.getEntity().getContent();
				// if (inputStream != null)
				// result = convertInputStreamToString(inputStream);
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

						Editor editor = sharedpreferences.edit();
						editor.putString("login", "success");
						editor.putString("login_status", "OK_N");
						editor.putString("user_email", New_Email);
						editor.putString("user_pword", New_password);
						editor.putString("user_id", fz_userid);
						editor.commit();
						USER_ID = sharedpreferences.getString("user_id", "");
						login_layout.setVisibility(View.GONE);
						user();
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
				System.out.println(e);
			}
		}
	}

	public class getUserData extends AsyncTask<Void, Void, Void> {

		Dialog dialog;

		protected void onPreExecute() {
			dialog = Utils.createProgressDialog(CheckoutActivity.this);
			dialog.setCancelable(false);
			if (dialog.isShowing())
				dialog.dismiss();
			dialog.show();

		}

		protected Void doInBackground(Void... params) {
			String userDetails;
			userDetails = get_userDetails(sharedpreferences.getString(
					"user_email", ""));
			try {
				if (userDetails != null && userDetails.length() > 0) {

					JSONObject jObj_main = new JSONObject(userDetails);

					JSONObject result = jObj_main.getJSONObject("resultdata");
					if (result.getInt("is_address") == 0)
						address_status = "NO";
					else {
						JSONArray user_detail = result
								.getJSONArray("user_address");
						if (user_detail.length() > 0) {
							for (int i = 0; i < user_detail.length(); i++) {
								USER = new HashMap<String, String>();
								JSONObject data = user_detail.getJSONObject(i);
								USER.put("User_Name",
										data.getString("first_name"));
								USER.put("User_Email", data.getString("email"));
								USER.put("User_Address",
										data.getString("address"));
								USER.put("User_City", data.getString("city"));
								USER.put("User_Pincode",
										data.getString("pincode"));
								USER.put("User_Landmark",
										data.getString("landmark"));
								USER.put("User_Phone", data.getString("phone"));
								USER.put("User_Country",
										data.getString("country"));
							}
							address_status = "YES";
						}
					}
				}
			} catch (JSONException e) {
				System.out.println(e);
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (address_status.equals("NO")) {
				// if (sharedpreferences.getString("user_pword", "").equals(""))
				// address_layout.setVisibility(View.GONE);
				// else
				address_layout.setVisibility(View.VISIBLE);
				adapter = new ArrayAdapter<String>(CheckoutActivity.this,
						android.R.layout.simple_spinner_item, countries);
				country.setAdapter(adapter);
				country.setSelection(adapter.getPosition("India"));
			} else {
				String Address = USER.get("User_Address") + "\n"
						+ USER.get("User_City") + "\n"
						+ USER.get("User_Landmark") + "\n"
						+ USER.get("User_Pincode") + "\n"
						+ USER.get("User_Country");
				Name.setText(USER.get("User_Name"));
				EmailId.setText(USER.get("User_Email"));
				Mobile.setText(USER.get("User_Phone"));
				tvaddress.setText(Address);
				if (USER.get("User_City").toLowerCase()
						.matches("(?i).*bangalore.*")
						|| USER.get("User_City").toLowerCase()
								.matches("(?i).*bengaluru.*"))
					cod.setVisibility(View.VISIBLE);
				else
					cod.setVisibility(View.GONE);
				login_layout.setVisibility(View.GONE);
				main_rel_layout.setVisibility(View.VISIBLE);
				new costcalculation_async().execute();
			}
			dialog.dismiss();
		}
	}

	public class validatePincode extends AsyncTask<Void, Void, String> {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckoutActivity.this);

		protected void onPreExecute() {
		}

		protected String doInBackground(Void... params) {

			return check_pincode(pincode_text);
		}

		protected void onPostExecute(String result) {
			JSONObject jObj_main;
			try {
				jObj_main = new JSONObject(result);

				if (jObj_main.getInt("success") == 0) {
					builder.setMessage(
							"Delivery not available to the current pincode!")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				} else {

					Shipping_Address = new Shipping_Model();
					Shipping_Address.setS_name(username_text);
					if (New_Email.equals(""))
						Shipping_Address.setS_mail(sharedpreferences.getString(
								"user_email", ""));
					else
						Shipping_Address.setS_mail(New_Email);
					Shipping_Address.setS_address(address_text);
					Shipping_Address.setS_landmark(landmark_text);
					Shipping_Address.setS_city(city_text);
					Shipping_Address.setS_country(getcountryid(country
							.getSelectedItem().toString()));
					Shipping_Address.setS_pin(pincode_text);
					Shipping_Address.setS_phone(phonenum_text);
					new save_Shipping_address().execute();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public class costcalculation_async extends AsyncTask<Void, Void, String> {
		String Total, S_Cost, D_Cost, Ser_Cost, VAT_Cost;

		protected void onPreExecute() {
			productlist();
		}

		protected String doInBackground(Void... params) {

			return getpaymentcost();
		}

		protected void onPostExecute(String result) {
			JSONObject jObj_main;
			try {
				jObj_main = new JSONObject(result);
				Total = jObj_main.getString("grand_total");
				S_Cost = jObj_main.getString("shipping");
				D_Cost = jObj_main.getString("discount");
				Ser_Cost = jObj_main.getString("service");
				VAT_Cost = jObj_main.getString("vat");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			checkout_stage_tv.setText("STEP 1");
			total_amt_value.setText("\u20B9 "
					+ Double.valueOf(total_grand_cost));
			shipping_amt_value.setText("\u20B9  " + Double.valueOf(S_Cost));
			discount_amt_value.setText("\u20B9  " + Double.valueOf(D_Cost));
			payable_amt_value.setText("\u20B9 " + Double.valueOf(Total));

			if (Integer.parseInt(Ser_Cost) > 0) {
				serviceview.setVisibility(View.VISIBLE);
				service_amt_value.setText("\u20B9 " + Double.valueOf(Ser_Cost));
			}
			if (Integer.parseInt(VAT_Cost) > 0) {
				vatview.setVisibility(View.VISIBLE);
				vat_amt_value.setText("\u20B9 " + Double.valueOf(VAT_Cost));
			}
		}
	}

	public class cod_payment_async extends AsyncTask<Void, Void, String> {

		protected void onPreExecute() {
		}

		protected String doInBackground(Void... params) {

			return savecart("COD");
		}

		protected void onPostExecute(String result) {
			JSONObject jObj_main;
			try {
				jObj_main = new JSONObject(result);
				int data = jObj_main.getInt("sucess");
				if (data == 1) {
					alertDialogBuilder
							.setMessage("Order done successfully.")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											Utils.ORDER = "DONE";
											finish();
											alertDialog.dismiss();
										}
									});
					alertDialog = alertDialogBuilder.create();
					alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					alertDialog.show();
				} else {
					alertDialogBuilder
							.setMessage("Order Unsuccessful.")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											alertDialog.dismiss();
										}
									});
					alertDialog = alertDialogBuilder.create();
					alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					alertDialog.show();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public class online_payment_async extends AsyncTask<Void, Void, String> {
		String URL;

		protected void onPreExecute() {
		}

		protected String doInBackground(Void... params) {

			return savecart("ONLINE");
		}

		protected void onPostExecute(String result) {
			JSONObject jObj_main;
			try {
				jObj_main = new JSONObject(result);
				JSONObject data = jObj_main.getJSONObject("result");
				URL = data.getString("url");
				System.out.println(URL);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			finish();
			startActivity(new Intent(CheckoutActivity.this, PayuActivity.class)
					.putExtra("URL", URL));
		}
	}

	public class validate_userexistence extends AsyncTask<Void, Void, String> {

		protected void onPreExecute() {
		}

		protected String doInBackground(Void... params) {
			// savecart();
			return check_user(New_Email);
		}

		protected void onPostExecute(String result) {
			JSONObject jObj_main;
			try {
				jObj_main = new JSONObject(result);

				if (jObj_main.getInt("success") == 0) {
					new guest_login_validate().execute();
				} else {
					Notify_user.setVisibility(View.VISIBLE);
					Notify_user
							.setText("Hi, Foodzu User please enter password & login.");
					firstlogin_SetPassword();
					login_emailid.setText("");
					RG.setVisibility(View.GONE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public class guest_login_validate extends AsyncTask<String, String, String> {

		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckoutActivity.this);

		protected void onPreExecute() {
			dialog = Utils.createProgressDialog(CheckoutActivity.this);
			dialog.setCancelable(false);
			if (dialog.isShowing())
				dialog.dismiss();
			dialog.show();

		}

		protected String doInBackground(String... params) {

			String result = null;
			String URL = URLs.APP_LOGIN_URL;
			try {

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("emailid", New_Email));
				pairs.add(new BasicNameValuePair("applogin", "1"));
				pairs.add(new BasicNameValuePair("type", "T"));
				Editor editor = sharedpreferences.edit();
				editor.putString("guest_email", New_Email);
				editor.commit();
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

						login_layout.setVisibility(View.GONE);
						address_layout.setVisibility(View.VISIBLE);
						adapter = new ArrayAdapter<String>(
								CheckoutActivity.this,
								android.R.layout.simple_spinner_item, countries);
						country.setAdapter(adapter);
						country.setSelection(adapter.getPosition("India"));

					} else {

						builder.setMessage("Guest checkout denied.")
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

	public String check_user(String email) {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.CHK_USER_URL + email;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(URL));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	public String get_userDetails(String email) {
		// InputStream inputStream = null;
		String result = null;
		// String URL = URLs.APP_LOGIN_URL;
		String URL = URLs.FETCH_ADDRESS_URL;
		try {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			if (sharedpreferences.getString("guest_email", "").equals(""))
				pairs.add(new BasicNameValuePair("emailid", email));
			else
				pairs.add(new BasicNameValuePair("emailid", sharedpreferences
						.getString("guest_email", "")));
			// pairs.add(new BasicNameValuePair("applogin", "1"));
			// if (sharedpreferences.getString("login", "").equals("success")) {
			// pairs.add(new BasicNameValuePair("type", "N"));
			// pairs.add(new BasicNameValuePair("password", pword));
			// } else if (sharedpreferences.getString("g_login", "").equals(
			// "signin"))
			// pairs.add(new BasicNameValuePair("type", "G"));
			// else if (sharedpreferences.getString("f_login", "")
			// .equals("signin"))
			// pairs.add(new BasicNameValuePair("type", "F"));
			// else
			// pairs.add(new BasicNameValuePair("type", "T"));

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(URL);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
			System.out.println(pairs);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			result = EntityUtils.toString(httpResponse.getEntity());

			// HttpClient httpclient = new DefaultHttpClient();
			// HttpResponse httpResponse = httpclient.execute(new HttpGet(URL));
			// inputStream = httpResponse.getEntity().getContent();
			// if (inputStream != null)
			// result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	public class save_Shipping_address extends AsyncTask<Void, Void, String> {

		protected void onPreExecute() {
		}

		protected String doInBackground(Void... params) {
			return saveaddress(Shipping_Address);
		}

		protected void onPostExecute(String result) {
			try {
				if (result != null && result.length() > 0) {
					JSONObject jObj_main = new JSONObject(result);
					if (jObj_main.getInt("success") == 1) {
						main_rel_layout.setVisibility(View.VISIBLE);
						address_layout.setVisibility(View.GONE);
						if (address_status.equals("NO"))
							new getUserData().execute();
					}
				}
			} catch (JSONException e) {
				System.out.println(e);
			}
		}
	}

	public String getcountryid(String countryname) {

		for (int i = 0; countrylist_array.size() > i; i++) {
			if (countrylist_array.get(i).getcountry_name().equals(countryname))
				return countrylist_array.get(i).getcountry_id();
		}
		return countryname;

	}

	public class getCountryData extends AsyncTask<Void, Void, Void> {

		protected void onPreExecute() {
		}

		protected Void doInBackground(Void... params) {

			String countries = get_countries();
			try {
				if (countries != null && countries.length() > 0) {
					countrylist_array.clear();
					JSONObject jObj_main = new JSONObject(countries);
					JSONArray result = jObj_main.getJSONArray("result");
					if (result.length() > 0) {
						for (int i = 0; i < result.length(); i++) {
							JSONObject data = result.getJSONObject(i);
							CM_DATA = new Country_Model();
							CM_DATA.setcountry_id(data.getString("countryid"));
							CM_DATA.setcountry_name(data
									.getString("countryname"));
							countrylist_array.add(CM_DATA);
							CheckoutActivity.this.countries.add(data
									.getString("countryname"));
						}
					}
				}
			} catch (JSONException e) {
				System.out.println(e);
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			Country_Arraylist = new Data_Models();
			Country_Arraylist.setcountrydata(countrylist_array);
		}
	}

	public String get_countries() {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.GETCOUNTRYS_URL;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(URL));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	public String check_pincode(String pincode) {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.CHK_PINCODE_URL + pincode;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(URL));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	public String saveaddress(Shipping_Model data) {
		String result = null;
		try {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("S_email", data.getS_mail()));
			pairs.add(new BasicNameValuePair("S_username", data.getS_name()));
			pairs.add(new BasicNameValuePair("S_address", data.getS_address()));
			pairs.add(new BasicNameValuePair("S_landmark", data.getS_landmark()));
			pairs.add(new BasicNameValuePair("S_city", data.getS_city()));
			pairs.add(new BasicNameValuePair("S_country", data.getS_country()));
			pairs.add(new BasicNameValuePair("S_phonenumber", data.getS_phone()));
			pairs.add(new BasicNameValuePair("S_pincode", data.getS_pin()));

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(URLs.SHIPPING_ADDRESS_URL);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);

			result = EntityUtils.toString(httpResponse.getEntity());
			System.out.println(result);
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	public static ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {

			URL url = new URL(
					"https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
							+ input
							+ "&types=geocode&sensor=false&key=AIzaSyAsPPV7T0TZXhOaMUKKShN9lEwCh-luqRo");

			System.out.println("URL: " + url);
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e("EXCEPTION_PLACES_API", "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e("EXCEPTION_PLACES_API", "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {

			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				System.out.println(predsJsonArray.getJSONObject(i).getString(
						"description"));
				System.out
						.println("============================================================");
				resultList.add(predsJsonArray.getJSONObject(i).getString(
						"description"));
			}
		} catch (JSONException e) {
			Log.e("JSON EXCEPTION_PLACES", "Cannot process JSON results", e);
		}

		return resultList;
	}

	class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String>
			implements Filterable {
		private ArrayList<String> resultList;

		public GooglePlacesAutocompleteAdapter(Context context,
				int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	public boolean IsInternetPresent() {
		if (new Utils(CheckoutActivity.this).isNetworkAvailable())
			return true;
		else
			return false;
	}

	// UI Change

	private void updateUI(boolean isSignedIn, String access) {
		if (isSignedIn) {

			Editor editor = sharedpreferences.edit();
			// if (access.equals("N")) {
			// editor.putString("login", "success");
			// editor.putString("login_status", "OK_N");
			// editor.putString("user_email", username);
			// editor.putString("user_pword", password);
			// editor.putString("user_id", fz_userid);
			// } else
			if (access.equals("G")) {
				editor.putString("g_login", "signin");
				editor.putString("login_status", "OK_G");
				editor.putString("user_email", G_EMAIL_ID);
				editor.putString("user_id", fz_userid);

			} else if (access.equals("F")) {
				editor.putString("f_login", "signin");
				editor.putString("login_status", "OK_FB");
				editor.putString("user_email", FM_ID);
				editor.putString("user_id", fz_userid);
			}
			editor.commit();
			user();

		}
	}

	// GOOGLE PLUS LOGIN CODE STARTS HERE

	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			mConnectionResult = result;

			if (mSignInClicked) {
				resolveSignInError();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {

		super.onActivityResult(requestCode, responseCode, intent);
		if (requestCode == 0) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
//		if (CHK_login.equals("YES"))
//			Session.getActiveSession().onActivityResult(this, requestCode,
//					responseCode, intent);
	}

	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		getProfileInformation();
		new loginwithgoogle_validate().execute();

	}

	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				// String personName = currentPerson.getDisplayName();
				// String personPhotoUrl = currentPerson.getImage().getUrl();
				// String personGooglePlusProfile = currentPerson.getUrl();

				G_EMAIL_ID = Plus.AccountApi.getAccountName(mGoogleApiClient);
				G_FIRST_NAME = currentPerson.getDisplayName();
				G_LAST_NAME = "";
				G_GENDER = currentPerson.getGender();
				G_BIRTHDAY = currentPerson.getBirthday();

			} else {
				Toast.makeText(getApplicationContext(),
						"Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		updateUI(false, "G");
	}

	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

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
				mConnectionResult.startResolutionForResult(this, 0);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	void google() {
		if (new Utils(CheckoutActivity.this).isNetworkAvailable()) {
			CHK_login = "NO";
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

	public class loginwithgoogle_validate extends
			AsyncTask<String, String, String> {

		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckoutActivity.this);

		protected void onPreExecute() {

			dialog = Utils.createProgressDialog(CheckoutActivity.this);
			dialog.setCancelable(false);
			if (dialog.isShowing())
				dialog.dismiss();
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

	// GOOGLE LOGIN CODE ENDS HERE

	// FACEBOOK LOGIN CODE STARTS HERE

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			if (msg.what == 1) {
				updateUI(true, "F");
			} else if (msg.what == 2) {
			}
			return false;
		}
	});

	void facebook() {
		if (new Utils(CheckoutActivity.this).isNetworkAvailable()) {
//			CHK_login = "YES";
//			LoginActivity LA = new LoginActivity();
//			LA.logintoFB();
//			logintoFB();
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

	void logintoFB() {
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {

				if (session.isOpened()) {
					boolean isPermissionAvailable = false;
					for (int i = 0; i < session.getPermissions().size(); i++) {
						if (session.getPermissions().get(i).contains("email")) {
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
	}

	private void getUserInfoFromFacebook(final GraphUser user) {
		userHashmap = new HashMap<String, String>();

		userHashmap.put(F_USER_ID, user.getId());
		userHashmap.put(F_USER_NAME, user.getUsername());
		userHashmap.put(F_FIRST_NAME, user.getFirstName());
		userHashmap.put(F_LAST_NAME, user.getLastName());
		userHashmap.put(F_BIRTHDAY, user.getBirthday());
		userHashmap.put(F_GENDER, (String) user.getProperty("gender"));
		userHashmap.put(F_EMAIL_ID, user.asMap().get("email").toString());
		FM_ID = user.asMap().get("email").toString();
		new loginwithFB_validate().execute();
	}

	private void getPermissionUserInfo() {
		String[] permissions = { "basic_info", "email" };
		Session.getActiveSession().requestNewReadPermissions(
				new NewPermissionsRequest(CheckoutActivity.this, Arrays
						.asList(permissions)));
	}

	public class loginwithFB_validate extends AsyncTask<String, String, String> {

		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				CheckoutActivity.this);

		protected void onPreExecute() {
			dialog = Utils.createProgressDialog(CheckoutActivity.this);
			dialog.setCancelable(false);
			if (dialog.isShowing())
				dialog.dismiss();
			dialog.show();

		}

		protected String doInBackground(String... params) {

			String result = null;
			try {

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("firstname", userHashmap
						.get(F_FIRST_NAME)));
				pairs.add(new BasicNameValuePair("lastname", userHashmap
						.get(F_LAST_NAME)));
				pairs.add(new BasicNameValuePair("birthday", userHashmap
						.get(F_BIRTHDAY)));
				pairs.add(new BasicNameValuePair("gender", userHashmap
						.get(F_GENDER)));
				pairs.add(new BasicNameValuePair("emailid", userHashmap
						.get(F_EMAIL_ID)));
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

	// FACEBOOK LOGIN CODE ENDS HERE

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.google_icon:
			google();
			break;
		case R.id.facebook_icon:
			finish();
			startActivity(new Intent(CheckoutActivity.this, LoginActivity.class).putExtra("FB", "FB_EXE"));
//			facebook();
			break;
		}

	}

	@Override
	public void onBackPressed() {
		if (Utils.Reload.equals("SUB"))
			Utils.Reload = "YES";
		else
			Utils.Reload = "NO";
		System.out.println("BACK");
		super.onBackPressed();
	}
}
