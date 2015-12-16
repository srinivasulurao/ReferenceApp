package com.fivestarchicken.lms;

import java.io.File;
import java.util.ArrayList;

import com.fivestarchicken.lms.ActivityEmployeeLogin.LanguageSelectedListener;
import com.fivestarchicken.lms.adapter.AdapterDropDown;
import com.fivestarchicken.lms.libs.CircularImageView;
import com.fivestarchicken.lms.model.User;
import com.fivestarchicken.lms.utils.Commons;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityCarrierGraph extends ActionBarActivity  {



	SharedPreferences sharedPreferences;

	private String starLevel,profileImage;

	ImageView ivOneStar,ivTwoStar,ivThreeStar,ivFourStar,ivfiveStar;
	CircularImageView ciProfileImage;

	private ActionBar actionbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.carrier_star_graph);

		actionbar = getSupportActionBar();

		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
		actionbar.setCustomView(mCustomView);
		actionbar.setDisplayShowCustomEnabled(true);
		
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityCarrierGraph.this);

		starLevel = sharedPreferences.getString("starLevel", null);
		profileImage=sharedPreferences.getString("profileImg", null);
		ivOneStar=(ImageView)findViewById(R.id.ivoneStar);
		ivTwoStar=(ImageView)findViewById(R.id.ivtwoStar);
		ivThreeStar=(ImageView)findViewById(R.id.ivthreeStar);
		ivFourStar=(ImageView)findViewById(R.id.ivfourStar);
		ivfiveStar=(ImageView)findViewById(R.id.ivfiveStar);
		ciProfileImage = (CircularImageView)findViewById(R.id.ciprofileimage);
		
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/" + Commons.appFolder + "/" + profileImage);

		if (file.exists()) {

			Bitmap myBitmap = BitmapFactory.decodeFile(file
					.getAbsolutePath());

			ciProfileImage.setImageBitmap(myBitmap);

		} 
		
		
		if (starLevel.equals("0")) {
			
		}else if(starLevel.equals("1")){
			ivOneStar.setImageResource(R.drawable.one_star_gold);
			ivTwoStar.setImageResource(R.drawable.two_star_green);
			
		}else if(starLevel.equals("2")){
			
			ivOneStar.setImageResource(R.drawable.one_star_gold);
			ivTwoStar.setImageResource(R.drawable.two_star_gold);
			ivThreeStar.setImageResource(R.drawable.three_star_green);
			
		}else if(starLevel.equals("3")){
			ivOneStar.setImageResource(R.drawable.one_star_gold);
			ivTwoStar.setImageResource(R.drawable.two_star_gold);
			ivThreeStar.setImageResource(R.drawable.three_star_gold);
			ivFourStar.setImageResource(R.drawable.four_star_green);
			
		}else if(starLevel.equals("4")){
			
			ivOneStar.setImageResource(R.drawable.one_star_gold);
			ivTwoStar.setImageResource(R.drawable.two_star_gold);
			ivThreeStar.setImageResource(R.drawable.three_star_gold);
			ivFourStar.setImageResource(R.drawable.four_star_gold);
			ivfiveStar.setImageResource(R.drawable.five_star_green);
			
		}else if(starLevel.equals("5")){
			
			ivOneStar.setImageResource(R.drawable.one_star_gold);
			ivTwoStar.setImageResource(R.drawable.two_star_gold);
			ivThreeStar.setImageResource(R.drawable.three_star_gold);
			ivFourStar.setImageResource(R.drawable.four_star_gold);
			ivfiveStar.setImageResource(R.drawable.five_star_gold);
			
		}
		
		
		ivOneStar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (starLevel.equals("0")) {
					
					Intent intent = new Intent(getApplicationContext(),
							ActivityExamModule.class);
					intent.putExtra("star_level", starLevel);
					startActivity(intent);
					
				}else if(starLevel.equals("1")){
					dialogLay("You have already cleared this Star Level");
					
					
				}else if(starLevel.equals("2")){
					dialogLay("You have already cleared this Star Level");
					
				}else if(starLevel.equals("3")){
					dialogLay("You have already cleared this Star Level");
					
				}else if(starLevel.equals("4")){
					dialogLay("You have already cleared this Star Level");
					
				}else if(starLevel.equals("5")){
					dialogLay("You have already cleared this Star Level");
					
				}
			}
		});

		ivTwoStar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (starLevel.equals("0")) {
					dialogLay("You have to clear the prevoius Star Levels to go Ahead");
					
				}else if(starLevel.equals("1")){
					Intent intent = new Intent(getApplicationContext(),
							ActivityExamModule.class);
					intent.putExtra("star_level", starLevel);
					startActivity(intent);
					
				}else if(starLevel.equals("2")){
					dialogLay("You have already cleared this Star Level");
					
				}else if(starLevel.equals("3")){
					dialogLay("You have already cleared this Star Level");
					
				}else if(starLevel.equals("4")){
					dialogLay("You have already cleared this Star Level");
					
				}else if(starLevel.equals("5")){
					dialogLay("You have already cleared this Star Level");
					
				}

			}
		});
		ivThreeStar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (starLevel.equals("0")) {
					dialogLay("You have to clear the prevoius Star Levels to go Ahead");
					
				}else if(starLevel.equals("1")){
					dialogLay("You have to clear the prevoius Star Levels to go Ahead");
					
				}else if(starLevel.equals("2")){
					Intent intent = new Intent(getApplicationContext(),
							ActivityExamModule.class);
					intent.putExtra("star_level", starLevel);
					startActivity(intent);
					
				}else if(starLevel.equals("3")){
					dialogLay("You have already cleared this Star Level");
					
				}else if(starLevel.equals("4")){
					dialogLay("You have already cleared this Star Level");
					
				}else if(starLevel.equals("5")){
					dialogLay("You have already cleared this Star Level");
					
				}

			}
		});
		ivFourStar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (starLevel.equals("0")) {
					dialogLay("You have to clear the prevoius Star Levels to go Ahead");
					
				}else if(starLevel.equals("1")){
					dialogLay("You have to clear the prevoius Star Levels to go Ahead");
					
				}else if(starLevel.equals("2")){
					dialogLay("You have to clear the prevoius Star Levels to go Ahead");
					
				}else if(starLevel.equals("3")){
					Intent intent = new Intent(getApplicationContext(),
							ActivityExamModule.class);
					intent.putExtra("star_level", starLevel);
					startActivity(intent);
					
				}else if(starLevel.equals("4")){
					dialogLay("You have already cleared this Star Level");
					
				}else if(starLevel.equals("5")){
					dialogLay("You have already cleared this Star Level");
					
				}

			}
		});
		ivfiveStar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (starLevel.equals("0")) {
					dialogLay("You have to clear the prevoius Star Levels to go Ahead");
					
				}else if(starLevel.equals("1")){
					dialogLay("You have to clear the prevoius Star Levels to go Ahead");
					
				}else if(starLevel.equals("2")){
					dialogLay("You have to clear the prevoius Star Levels to go Ahead");
					
				}else if(starLevel.equals("3")){
					dialogLay("You have to clear the prevoius Star Levels to go Ahead");
					
				}else if(starLevel.equals("4")){
					Intent intent = new Intent(getApplicationContext(),
							ActivityExamModule.class);
					intent.putExtra("star_level", starLevel);
					startActivity(intent);
					
				}else if(starLevel.equals("5")){
					dialogLay("You have already cleared this Star Level");
					
				}

			}
		});

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Previous page or exit
			finish();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	

	
	void dialogLay(String msg) {// Create custom dialog object
		final Dialog dialog = new Dialog(this);
		// hide to default title for Dialog
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// inflate the layout dialog_layout.xml and set it as contentView
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_lay, null, false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

		// Retrieve views from the inflated dialog layout and update their
		// values
		TextView txtTitle = (TextView) dialog
				.findViewById(R.id.txt_dialog_title);
		txtTitle.setText("Custom Dialog");

		TextView txtMessage = (TextView) dialog
				.findViewById(R.id.txt_dialog_message);
		txtMessage.setText(msg);

		Button btnOpenBrowser = (Button) dialog
				.findViewById(R.id.btn_open_browser);
		btnOpenBrowser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// Open the browser
				// Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
				// .parse("http://www.android-ios-tutorials.com"));
				// startActivity(browserIntent);
				// Dismiss the dialog
				dialog.dismiss();
			}
		});

		Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Close the dialog
				dialog.dismiss();
			}
		});

		// Display the dialog
		dialog.show();
	}
}
