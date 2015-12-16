package com.omkar.myactivitytracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;



import com.omkar.myactivitytracker.SplashActivity.MyHandler;
import com.personaltrainer.database.LoginDB;


import com.personaltrainer.widgets.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.transition.ChangeBounds;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Settings extends Activity implements OnClickListener{

	RelativeLayout relReport, relTellaFriend, relImport, relBackUp, relSetPassword, relChangePassword;
	CheckBox ask_login;
	String sPassword ="";

	String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

	void initilizeUI()
	{
		getActionBar().setTitle(" Settings");

		relReport = (RelativeLayout)findViewById(R.id.relReport);
		relTellaFriend = (RelativeLayout)findViewById(R.id.relTellaFriend);
		relImport = (RelativeLayout)findViewById(R.id.relImport);
		relBackUp = (RelativeLayout)findViewById(R.id.relBackUp);
		relSetPassword = (RelativeLayout)findViewById(R.id.relSetPassword);
		relChangePassword = (RelativeLayout)findViewById(R.id.relChangePassword);
		ask_login = (CheckBox)findViewById(R.id.ask_login);

		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		initilizeUI();

		relReport.setOnClickListener(this);
		relTellaFriend.setOnClickListener(this);
		relImport.setOnClickListener(this);
		relBackUp.setOnClickListener(this);
		relSetPassword.setOnClickListener(this);
		relChangePassword.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);
		boolean isLock = preferences.getBoolean("enable", false);
		sPassword = preferences.getString("password", "");

		if(sPassword == null)
		{
			sPassword = "";
		}

		if(isLock)
		{
			ask_login.setChecked(true);
		}
		else
		{
			ask_login.setChecked(false);
		}

		ask_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub

				SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(Settings.this);
				boolean isLock = preferences1.getBoolean("lock", false);

				if(!isLock)
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "You have not set the password.");
					ask_login.setChecked(false);
				}
				else
				{
					if(isChecked )
					{
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);
						SharedPreferences.Editor editor = preferences.edit();
						editor.putBoolean("enable", true);
						editor.commit();
					}
					else
					{
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);
						SharedPreferences.Editor editor = preferences.edit();
						editor.putBoolean("enable", false);
						editor.commit();
					}
				}
			}
		});
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.relReport:
			reposrtProblem();
			break;

		case R.id.relTellaFriend:
			TellFriend();
			break;

		case R.id.relImport:
			AlertDialog.Builder alertDialog_ = new AlertDialog.Builder(Settings.this);
			alertDialog_.setTitle("Confirm Import");
			alertDialog_.setMessage("Are you sure you want to Import the Data ?");


			alertDialog_.setPositiveButton("YES", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					importDB();
					dialog.cancel();
				}
			});

			alertDialog_.setNegativeButton("NO", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

					dialog.cancel();
				}
			});

			alertDialog_.show();
			break;

		case R.id.relBackUp:


			AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);
			alertDialog.setTitle("Confirm Backup");
			alertDialog.setMessage("Are you sure you want to take the Back up ?");


			alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {

					try
					{
						backupDatabase();
					}
					catch(Exception e)
					{
						Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
					}
					dialog.cancel();
				}
			});

			alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

					dialog.cancel();
				}
			});

			alertDialog.show();



			break;

		case R.id.relSetPassword:

			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			boolean isLock = preferences.getBoolean("lock", false);
			final String sMail = preferences.getString("email", "");

			if(isLock)
			{

				AlertDialog.Builder malertDialog = new AlertDialog.Builder(Settings.this);
				malertDialog.setTitle("Warning");
				malertDialog .setMessage("Password is Already Set. ");


				malertDialog .setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {

						dialog.cancel();

						ChanegPassword();
					}
				});

				malertDialog .setNegativeButton("Forgot Password", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.cancel();

						if(hasConnection(Settings.this))
						{
							new ForgotPassword(new MyHandler_(), sMail).start();
						}
						else
						{
							Utils.showAlertBoxSingle(Settings.this, "Error", "Please Check for Internet Connection");
						}
					}
				});

				malertDialog .show();

			}
			else
			{
				SetPassword();
			}



			break;

		case R.id.relChangePassword:
			ChanegPassword();
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.splash, menu);

		MenuItem item = menu.findItem(R.id.action_rate);
		item.setVisible(false);

		MenuItem item_ = menu.findItem(R.id.action_add);
		item_.setIcon(R.drawable.aboutus);


		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub


		switch (item.getItemId()) 
		{
		case R.id.action_add:
			
			String msg1="My Activity Tracker 1.2 is a Mobile Application developed to keep track of your daily activities.\n\n"
			+"We are sure if you use this App regularly and monitor your activities and correct them, you will be more happy, healthy each day.\n\nThis App is developed by";
			
			String msg2 = "www.OmkarSoft.com\n";
			String msg3 = "Please help share this App by recommending your friends/colleagues/family members on Facebook, Twitter, Google+, Pinterest.";


			final Dialog dialog = new Dialog(Settings.this);
			dialog.setTitle("About Us");
			dialog.setContentView(R.layout.aboutus);

			TextView txt1 = (TextView)dialog.findViewById(R.id.txt1);
			TextView txt2 = (TextView)dialog.findViewById(R.id.txt2);
			TextView txt3 = (TextView)dialog.findViewById(R.id.txt3);
			
			txt1.setTextSize(20);txt2.setTextSize(20);txt3.setTextSize(18);
			
			txt1.setTypeface(Typeface.SANS_SERIF);
			txt2.setTypeface(Typeface.SANS_SERIF);
			txt3.setTypeface(Typeface.SANS_SERIF);
			
			
			txt1.setText(msg1);
			txt2.setText(msg2);
			txt3.setText(msg3);
			
			dialog.show();
			
			
			
			
			
			
			break;


		default:
			break;
		}

		return true;
	}

	void ChanegPassword()
	{
		final Dialog dialog = new Dialog(Settings.this);
		dialog.setTitle("Change Password");
		dialog.setContentView(R.layout.change_password);

		final EditText edt_mail = (EditText)dialog.findViewById(R.id.edt_mail);
		final EditText edt_Oldpassword = (EditText)dialog.findViewById(R.id.edt_oldPassword);
		final EditText edt_newpassword = (EditText)dialog.findViewById(R.id.edt_newPassword);
		Button btn_submit = (Button)dialog.findViewById(R.id.btn_submit);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final String mPassword = preferences.getString("password","");

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String sMail = edt_mail.getText().toString().trim();
				String old_pw = edt_Oldpassword.getText().toString().trim();
				String new_pw = edt_newpassword.getText().toString().trim();

				if(sMail.equals(""))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Please Enter the Email.");
				}
				else if(!sMail.matches(EMAIL_REGEX))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Please Enter the Valid Email.");
				}
				else if(old_pw.equals(""))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Please Enter the Old Password.");
				}
				else if(new_pw.equals(""))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Please Enter the New Password.");
				}
				else if(!old_pw.equals(mPassword))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "You have Entered incorrect old passowrd.");
				}
				else
				{
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("email",sMail);
					editor.putString("password",new_pw);
					editor.putBoolean("lock", true);
					editor.commit();

					dialog.cancel();
				}


			}
		});


		dialog.show();
	}

	void SetPassword()
	{
		final Dialog dialog = new Dialog(Settings.this);
		dialog.setTitle("Set Password");
		dialog.setContentView(R.layout.set_password);

		final EditText edt_mail = (EditText)dialog.findViewById(R.id.edt_mail);
		final EditText edt_password = (EditText)dialog.findViewById(R.id.edt_password);
		final EditText edt_confirmpassword = (EditText)dialog.findViewById(R.id.edt_confirmpassword);
		Button btn_submit = (Button)dialog.findViewById(R.id.btn_submit);

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String sMail = edt_mail.getText().toString().trim();
				String sPassword = edt_password.getText().toString().trim();
				String sConfirmPassword = edt_confirmpassword.getText().toString().trim();

				if(sMail.equals(""))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Pleas Enter the Email");
				}
				else if(!sMail.matches(EMAIL_REGEX))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Pleas Enter the Valid Email");
				}
				else if(sPassword.equals(""))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Pleas Enter the Password");
				}
				else if(sConfirmPassword.equals(""))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Pleas Enter the Confirm Password");
				}
				else if(!sPassword.equals(sConfirmPassword))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Password and Confirm Password din't match.");
				}
				else
				{
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("email",sMail);
					editor.putString("password",sPassword);
					editor.putBoolean("lock", true);
					editor.putBoolean("enable", true);
					editor.commit();

					dialog.cancel();

					ask_login.setChecked(true);
				}

			}
		});

		dialog.show();
	}

	void TellFriend()
	{
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = "I discovered this nice app to monitor and improve my daily activities. Download it for free.\n"+
				"https://play.google.com/store/apps/details?id=com.omkar.myactivitytracker";
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Activity Tracker");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	void reposrtProblem()
	{
		final Dialog dialog = new Dialog(Settings.this);
		dialog.setTitle("Report Problem/Suggestion");
		dialog.setContentView(R.layout.reportproblem);

		final EditText edtFromMail = (EditText)dialog.findViewById(R.id.edtFromEmail);
		final EditText edtToMail = (EditText)dialog.findViewById(R.id.edtToEmail);
		final EditText edtMessage = (EditText)dialog.findViewById(R.id.edtMessage);
		Button btnSubmit = (Button)dialog.findViewById(R.id.btnSubmit);

		edtToMail.setText("work@omkarsoft.com");
		edtToMail.setFocusable(false);

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(edtFromMail.getText().toString().trim().equals(""))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Please Enter the From Email");
				}
				else if(!edtFromMail.getText().toString().trim().matches(EMAIL_REGEX))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Please Enter the Valid Email");
				}
				else if(edtMessage.getText().toString().trim().equals(""))
				{
					Utils.showAlertBoxSingle(Settings.this, "Error", "Please Enter Message to Report");
				}
				else
				{
					dialog.dismiss();

					new ReportProblem(new MyHandler(),edtFromMail.getText().toString().trim(), edtToMail.getText().toString().trim(), edtMessage.getText().toString().trim()).start();

				}


			}
		});

		dialog.show();
	}

	public class MyHandler extends Handler
	{
		public void handleMessage(android.os.Message msg) 
		{
			String errorMsg=msg.getData().getString("HttpError");
			int len = errorMsg.length();
			if(errorMsg.length()>0)
			{
				Utils.showAlertBoxSingle(Settings.this, "Error", "Unable to send Email.");
			}
			else
			{
				Utils.showAlertBoxSingle(Settings.this, "Report", "Email Sent Successfully.");
			}
		}
	}

	private class ReportProblem extends Thread
	{
		private Handler parentHandler;
		String sMsg="";
		String sFrom="";
		String sTo="";
		String sContent="";


		public ReportProblem(Handler pHandler, String sFromMail, String sToMail, String sMessage)
		{
			this.parentHandler = pHandler;
			this.sFrom = sFromMail;
			this.sTo = sToMail;
			this.sContent = sMessage;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			List<NameValuePair> pairs = new ArrayList<NameValuePair>(); 
			pairs.add(new BasicNameValuePair("from_mail",sFrom));  
			pairs.add(new BasicNameValuePair("to_mail",sTo));  
			pairs.add(new BasicNameValuePair("content",sContent));  

			sMsg="";
			String str2 ="";
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://mapimoapp.com/personal_trainer/forget_password.php?page=send_report");
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				str2 = EntityUtils.toString(httpEntity);

				//[{"Result" : "0"}]

				JSONObject jObject = new JSONObject(str2);
				String sRes = jObject.getString("Result");
				if(sRes.equalsIgnoreCase("0"))
				{
					sMsg="Error";
				}

			}
			catch(Exception e)
			{
				sMsg = "Error";
			}

			Message messageToParent = new Message();
			Bundle messageData = new Bundle();
			messageToParent.what = 0;
			messageData.putString("HttpError",sMsg);
			messageToParent.setData(messageData);
			parentHandler.sendMessage(messageToParent);
		}
	}

	void importDB()
	{
		try
		{
			File sd = Environment.getExternalStorageDirectory();
			File data  = Environment.getDataDirectory();

			if (sd.canWrite()) 
			{
				String  currentDBPath= "//data//" + "com.omkar.myactivitytracker"
						+ "//databases//" + Utils.appName_;
				File sd_ = new File(Environment.getExternalStorageDirectory()+"/"+Utils.appName_);
				String backupDBPath = sd_+"/"+Utils.appName_;

				File  backupDB= new File(data, currentDBPath);
				File currentDB  = new File(backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Utils.showAlertBoxSingle(Settings.this, "Import Data", "Data Imported Successfully"+"/n"+backupDBPath);

			}
		}
		catch(Exception e)
		{
			Utils.showAlertBoxSingle(Settings.this, "Error", e.toString());
		}
	}

	public void backupDatabase() throws IOException {

		try {
			File sd = new File(Environment.getExternalStorageDirectory()+"/"+Utils.appName_);
			File data = Environment.getDataDirectory();

			if (sd.canWrite())
			{
				String currentDBPath = "//data//com.omkar.myactivitytracker//databases//"+Utils.appName_;
				String backupDBPath = Utils.appName_;
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists())
				{
					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();

					Utils.showAlertBoxSingle(Settings.this, "BackUp", "Data Back up Successfull"+"\n"+sd.toString());
				}
				else
				{
					Utils.showAlertBoxSingle(Settings.this, "BackUp", "Unable to Take the Backup.");
				}
			}

		}catch (Exception e) {
		}

	}


	public class MyHandler_ extends Handler
	{
		public void handleMessage(android.os.Message msg) 
		{
			String errorMsg=msg.getData().getString("HttpError");
			int len = errorMsg.length();
			if(errorMsg.length()>0)
			{
				Utils.showAlertBoxSingle(Settings.this, "Error", "Unable to send password to your email");
			}
			else
			{
				Utils.showAlertBoxSingle(Settings.this, "Forgot Password", "Password Sent to your email.");
			}
		}
	}

	private class ForgotPassword extends Thread
	{
		private Handler parentHandler;
		String sMsg="";
		String sMail;


		public ForgotPassword(Handler pHandler,String mail)
		{
			this.parentHandler = pHandler;
			this.sMail = mail;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			List<NameValuePair> pairs = new ArrayList<NameValuePair>(); 
			pairs.add(new BasicNameValuePair("mail",sMail));  
			pairs.add(new BasicNameValuePair("password",sPassword));  


			String s1 = sPassword;
			sMsg="";
			String str2 ="";
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://mapimoapp.com/personal_trainer/forget_password.php?page=forget_password");
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				str2 = EntityUtils.toString(httpEntity);

				//[{"Result" : "0"}]

				JSONObject jObject = new JSONObject(str2);
				String sRes = jObject.getString("Result");
				if(sRes.equalsIgnoreCase("0"))
				{
					sMsg="Error";
				}

			}
			catch(Exception e)
			{
				sMsg = "Error";
			}

			Message messageToParent = new Message();
			Bundle messageData = new Bundle();
			messageToParent.what = 0;
			messageData.putString("HttpError",sMsg);
			messageToParent.setData(messageData);
			parentHandler.sendMessage(messageToParent);
		}
	}

	/*Chexk for Internet Connection*/
	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(
				Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}

}
