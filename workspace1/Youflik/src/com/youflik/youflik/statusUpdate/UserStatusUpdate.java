package com.youflik.youflik.statusUpdate;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.proxy.HttpClientStatusUpdate;
import com.youflik.youflik.selectCustomFriends.SelectCustomFriends;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.ImageFilePath;
import com.youflik.youflik.utils.InternalStorageContentProvider;
import com.youflik.youflik.utils.PlaceJSONParser;
import com.youflik.youflik.utils.Util;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class UserStatusUpdate extends ActionBarActivity implements OnClickListener {
	PlacesTask placesTask;
	ParserTask parserTask;
	//select csv
	private static String custom_name=" ";
	private static String custom_id=" ";
	public static final int result = 111;
	private String string_customUsers="";
	//end csv
	private TextView post_text,postCustomFriends;
	private EditText postMessage;
	private AutoCompleteTextView location;
	private ImageView image_post,image_postClose;
	private ProgressDialog pDialog;
	private File picfile, videoFile,sendFile;
	private static final int SELECT_PICTURE = 1;
	private static final int SELECT_VIDEO = 2;
	private static final int SELECT_CAMERA_IMAGE_REQUEST = 3;
	private static final int SELECT_CAMERA_VIDEO_REQUEST = 4;
	private File mFileTemp, mVideoFileTemp;
	private Bitmap bitmap_postImage, scaledBitmap;
	public Menu menuInstance;
	public ImageLoader imageLoader;
	private String checkActivity;
	DisplayImageOptions profile_options;
	AlertDialogManager alert = new AlertDialogManager();
	JSONObject jsonObjRecv;
	private  boolean asyncFlag=true;
	private  boolean fileFlag;
	private static String vp_flag;
	private String s_text,s_loc,s_privacy,s_custom;
	LinearLayout loc_container;
	private Spinner privacy;
	private static boolean loc_flag=false;
	private String tracker;
	private String thirdPartyUserID="null";
	ConnectionDetector conn = new ConnectionDetector(UserStatusUpdate.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status_update);
		Bundle bundle = getIntent().getExtras();
		checkActivity = bundle.getString("StatusUpdateType");
		Typeface typeFace=Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/BentonSans-Regular.otf");
		ActionBar actionBar = getSupportActionBar();
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("Post Something");
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#292929")));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.custom_status_update);
		post_text=(TextView)findViewById(R.id.post_a);
		post_text.setTypeface(typeFace);
		postMessage = (EditText) findViewById(R.id.PostMessage);
		postMessage.setTypeface(typeFace);
		location = (AutoCompleteTextView) findViewById(R.id.status_loc);
		location.setTypeface(typeFace);
		location.setThreshold(1);
		image_post = (ImageView) findViewById(R.id.post_image);
		image_postClose = (ImageView)findViewById(R.id.post_imageclose);
		loc_container=(LinearLayout)findViewById(R.id.loc_container);
		postCustomFriends=(TextView)findViewById(R.id.status_custom_friends);
		postCustomFriends.setTypeface(typeFace);
		privacy=(Spinner)findViewById(R.id.status_privacy);
		image_post.setVisibility(View.INVISIBLE);
		image_postClose.setVisibility(View.INVISIBLE);
		if(checkActivity.equalsIgnoreCase("User"))
		{
			postMessage.setHint("Hey, what's up "+Util.FIRSTNAME);

		}else{
			postMessage.setHint("Hey, what's up "+Util.THIRD_PARTY_USER_NAME);

		}
		//postMessage.setHint("Hey, What's up "+Util.FIRSTNAME);
		//SETTING THE SPINNER FOR PRIVACY
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.privacy_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		privacy.setAdapter(adapter);


		imageLoader =imageLoader.getInstance();
		profile_options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();


		image_postClose.setOnClickListener(this);

		//post_text.setOnClickListener(this);
		post_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(conn.isConnectingToInternet()){
					new StatusUpdateAsync().execute();
				}else{
					Crouton.makeText(UserStatusUpdate.this, getString(R.string.crouton_message), Style.ALERT).show();
				}

			}
		});

		location.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				placesTask = new PlacesTask();
				placesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});


		privacy.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				//Toast.makeText(getApplicationContext(),privacy.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();		
				if(privacy.getSelectedItem().toString().equalsIgnoreCase("Public"))
				{
					s_privacy="public_privacy";
					postCustomFriends.setVisibility(view.GONE);
					postCustomFriends.setText("");
					postCustomFriends.setHint("Add Custom Friends");
					string_customUsers="";
					custom_name=" ";
					custom_id=" ";
				}
				else if (privacy.getSelectedItem().toString().equalsIgnoreCase("Friends")) {
					s_privacy="friends_privacy";
					postCustomFriends.setVisibility(view.GONE);
					postCustomFriends.setText("");
					postCustomFriends.setHint("Add Custom Friends");
					string_customUsers="";
					custom_name=" ";
					custom_id=" ";
				}
				else if (privacy.getSelectedItem().toString().equalsIgnoreCase("Custom")) {
					s_privacy="custom_privacy";
					//postCustomFriends.setVisibility(view.VISIBLE);
					//
					Intent addcon = new Intent(getApplicationContext(),SelectCustomFriends.class);
					addcon.putExtra("usercheckedid", custom_id);
					//Toast.makeText(getApplicationContext(), custom_id, Toast.LENGTH_SHORT).show();
					startActivityForResult(addcon,result);	
					//
				}
				else if (privacy.getSelectedItem().toString().equalsIgnoreCase("Friends of Friends")) {
					s_privacy="fof_privacy";
					postCustomFriends.setVisibility(view.GONE);
					postCustomFriends.setText("");
					postCustomFriends.setHint("Add Custom Friends");
					string_customUsers="";
					custom_name=" ";
					custom_id=" ";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		postCustomFriends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent addcon = new Intent(getApplicationContext(),SelectCustomFriends.class);
				addcon.putExtra("usercheckedid", custom_id);
				//Toast.makeText(getApplicationContext(), custom_id, Toast.LENGTH_SHORT).show();
				startActivityForResult(addcon,result);				
			}
		});

	}
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_down_in_reverse,R.anim.push_down_out_reverse);

	}
	private static String removeLastChar(String str) {
		return str.substring(1,str.length()-1);
	}
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		menuInstance = menu;
		getMenuInflater().inflate(R.menu.menu_status_update, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		super.onOptionsItemSelected(item);

		switch(item.getItemId()){
		case R.id.gallery_video:
			/*asyncFlag=false;
			fileFlag=true;*/
			Intent intent = new Intent(Intent.ACTION_PICK,	android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(Intent.createChooser(intent, "Select Video"),	SELECT_VIDEO);
			break;
		case R.id.loc:
			if(loc_flag==false)
			{
				loc_container.setVisibility(View.VISIBLE);
				//loc_container.setFocusable(true);
				//loc_container.setFocusableInTouchMode(true);
				loc_flag=true;
			}
			else
			{
				loc_container.setVisibility(View.GONE);
				loc_flag=false;
				location.setText("");
				location.setHint("Add Location");
			}
			break;
		case R.id.gallery_image: 
			/*	asyncFlag=false;
			fileFlag=false;*/
			intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			 intent.setType("image/*"); 
			startActivityForResult(Intent.createChooser(intent, "Select a photo"),SELECT_PICTURE);
			break;     

		case R.id.camera:
			/*	asyncFlag=false; 
			fileFlag=false;*/
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) 
			{
				mFileTemp = new File(Environment.getExternalStorageDirectory(),Util.TEMP_PHOTO_FILE_NAME);
			} else {
				mFileTemp = new File(getApplicationContext().getFilesDir(),Util.TEMP_PHOTO_FILE_NAME);
			}
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			try {
				Uri mImageCaptureUri = null;
				if (mFileTemp == null) {
				} else {

				}
				if (Environment.MEDIA_MOUNTED.equals(state)) {
					mImageCaptureUri = Uri.fromFile(mFileTemp);
				} else {
					mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
				}
				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
						mImageCaptureUri);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, SELECT_CAMERA_IMAGE_REQUEST);

			} catch (ActivityNotFoundException e) {
			}
			break;

		case R.id.video:
			/*	asyncFlag=false;
			fileFlag=true;*/
			state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) 
			{
				mVideoFileTemp = new File(Environment.getExternalStorageDirectory(),Util.TEMP_VIDEO_FILE_NAME);
			} else {
				mVideoFileTemp = new File(getApplicationContext().getFilesDir(), Util.TEMP_VIDEO_FILE_NAME);
			}
			intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
			try
			{
				Uri mImageCaptureUri = null;

				if (Environment.MEDIA_MOUNTED.equals(state)) {
					mImageCaptureUri = Uri.fromFile(mVideoFileTemp);
				} else {
					mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
				}
				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
						mImageCaptureUri);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, SELECT_CAMERA_VIDEO_REQUEST);
			} catch (ActivityNotFoundException e) {

			}
			break;
		}
		/*	asyncFlag=false;
		fileFlag=true;*/
		return true;		

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {

			if (requestCode == result	&& resultCode == Activity.RESULT_OK && null != data) 
			{
				postCustomFriends.setText("");
				string_customUsers="";
				custom_name=data.getStringExtra("CustomUserName");
				custom_id=data.getStringExtra("CustomUserID");
				//if csv not selected then make it to public
				//remove first and last character
				postCustomFriends.setText(removeLastChar(custom_name).replaceAll("\\s+",""));
				string_customUsers=removeLastChar(custom_id).replaceAll("\\s+","");/// send this user id to server while creating the custom event  st.replaceAll("\\s+","")
				if(string_customUsers.equalsIgnoreCase(""))
				{
					postCustomFriends.setVisibility(View.GONE);
					privacy.setSelection(0);
					s_privacy="public_privacy";
				}
				else {
					postCustomFriends.setVisibility(View.VISIBLE);
				}
			}

			if (requestCode == SELECT_PICTURE	&& resultCode == Activity.RESULT_OK && null != data) {

				asyncFlag=false;
				fileFlag=false;

				Uri selectedImage = data.getData();
				System.out.println("URI ------"+selectedImage.toString());
				System.out.println("URI ------"+selectedImage.getPath());
				if (Build.VERSION.SDK_INT < 19) {

					String[] filePathColumn = { MediaStore.Images.Media.DATA };
					Cursor cursor = getApplicationContext().getContentResolver()	.query(selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);
					cursor.close();
					picfile = new File(picturePath);
					bitmap_postImage = BitmapFactory.decodeFile(picturePath);
					setImagePreview(bitmap_postImage);
				}
				else
				{
					//System.out.println("URI ---update---"+getPath(getApplicationContext(), selectedImage));
					System.out.println("URI ---update other---"+ImageFilePath.getPath(getApplicationContext(), selectedImage));
					//System.out.println("URI ------"+selectedImage.getPath());
					ParcelFileDescriptor parcelFileDescriptor; 
					try {
						parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImage, "r");
						FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
						Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
						parcelFileDescriptor.close();
						setImagePreview(image);
						picfile = new File(getPath(getApplicationContext(), selectedImage));

					} catch (FileNotFoundException e) {
						//Toast.makeText(getApplicationContext(), "You need to select default Gallery option", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if (requestCode == SELECT_VIDEO && resultCode == Activity.RESULT_OK
					&& null != data) {
				asyncFlag=false;
				fileFlag=true;
				Uri selectedVideo = data.getData();
				String[] filePathColumn = { MediaStore.Video.Media.DATA };
				Cursor cursor = getApplicationContext().getContentResolver()	.query(selectedVideo, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String videoPath = cursor.getString(columnIndex);
				cursor.close();
				videoFile = new File(videoPath);
				bitmap_postImage = ThumbnailUtils.createVideoThumbnail(videoPath, Thumbnails.MICRO_KIND);
				setImagePreview(bitmap_postImage);
			}
			if (requestCode == SELECT_CAMERA_IMAGE_REQUEST&& resultCode == Activity.RESULT_OK) {
				asyncFlag=false;
				fileFlag=false;
				picfile = mFileTemp;
				bitmap_postImage = BitmapFactory.decodeFile(picfile.getAbsolutePath());
				setImagePreview(bitmap_postImage);
			}
			if (requestCode == SELECT_CAMERA_VIDEO_REQUEST&& resultCode == Activity.RESULT_OK) {
				asyncFlag=false;
				fileFlag=true;
				videoFile = mVideoFileTemp;
				bitmap_postImage = ThumbnailUtils.createVideoThumbnail(videoFile.getPath(), Thumbnails.MICRO_KIND);
				setImagePreview(bitmap_postImage);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id == R.id.post_imageclose){
			image_post.setVisibility(View.INVISIBLE);
			image_postClose.setVisibility(View.INVISIBLE);
			asyncFlag=true;
		}
	}

	public Bitmap setImagePreview(Bitmap bitmap_postImage)
	{
		scaledBitmap = Bitmap.createScaledBitmap(bitmap_postImage, 200,200, true);
		image_post.setVisibility(View.VISIBLE);
		image_post.setImageBitmap(scaledBitmap);
		//image_post.setImageBitmap(bitmap_postImage);
		image_postClose.setVisibility(View.VISIBLE);
		return scaledBitmap;
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		if (postMessage.getText().toString().trim().length() > 0)
		{

			AlertDialog alert_back = new AlertDialog.Builder(this).create();
			alert_back.setTitle("Exit?");
			alert_back.setMessage("Your post will be discarded");

			alert_back.setButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			alert_back.setButton2("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					UserStatusUpdate.this.finish();
				}
			});
			alert_back.show();

		}else
		{
			UserStatusUpdate.this.finish();
		}


	}

	private class StatusUpdateAsync extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			pDialog = new ProgressDialog(UserStatusUpdate.this);
			pDialog.setMessage("Posting..");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONObject jsonObjSend = new JSONObject();

			try {

				if(asyncFlag==true && checkActivity.equalsIgnoreCase("User"))
				{
					if(s_privacy.equalsIgnoreCase("custom_privacy"))
					{
						if(postCustomFriends.getText().toString().trim().length()>0)
						{

							if(location.getText().toString().trim().length()>0)
							{
								jsonObjSend.put("location_name", location.getText().toString().trim());
							}
							// SETTING PRIVACY
							jsonObjSend.put(s_privacy, "1");
							//SETTING CUSTOM USER
							jsonObjSend.put("csv_custom_list_users",string_customUsers);
							// SETTING TRACKER
							jsonObjSend.put("tracker_type","status update");



							if(postMessage.getText().toString().trim().length()>0)
							{
								jsonObjSend.put("status_update_text", postMessage.getText().toString().trim());
								jsonObjRecv = HttpPostClient.sendHttpPost(Util.API + "post", jsonObjSend);
							}
							else
							{
								return 3;
							}
						}
						else
						{
							//Toast.makeText(UserStatusUpdate.this, "Please select the custom users", Toast.LENGTH_SHORT).show();
							return 1;

						}
					}
					else
					{

						if(location.getText().toString().trim().length()>0)
						{
							jsonObjSend.put("location_name", location.getText().toString().trim());
						}
						// SETTING PRIVACY
						jsonObjSend.put(s_privacy, "1");

						// SETTING TRACKER
						jsonObjSend.put("tracker_type","status update");
						if(postMessage.getText().toString().trim().length()>0)
						{
							jsonObjSend.put("status_update_text", postMessage.getText().toString().trim());
							jsonObjRecv = HttpPostClient.sendHttpPost(Util.API + "post", jsonObjSend);
						}
						else
						{
							return 3;
						}

					}
				}
				else if (asyncFlag==false && checkActivity.equalsIgnoreCase("User")) {
					if(s_privacy.equalsIgnoreCase("custom_privacy"))
					{ 
						if(postCustomFriends.getText().toString().trim().length()>0)
						{
							if(postMessage.getText().toString().trim().length()>0)
							{
								s_text=postMessage.getText().toString().trim();
							}
							else
							{
								s_text="null";
							}
							// SETTING LOCATION
							if(location.getText().toString().trim().length()>0)
							{
								s_loc=location.getText().toString().trim();
							}
							else
							{
								s_loc="null";
							}
							// SETTING CUSTOM USER (if privacy is "custom_privacy")
							s_custom=string_customUsers;
							if(fileFlag)
							{
								sendFile=videoFile;
								vp_flag="1";

							}else
							{
								sendFile=picfile;
								vp_flag="0";
							}
							jsonObjRecv = HttpClientStatusUpdate.sendHttpPostImage(Util.API + "post",sendFile,vp_flag,s_text,s_loc,s_privacy,s_custom,thirdPartyUserID);

						}
						else
						{
							//Toast.makeText(getApplicationContext(), "Please select the custom users", Toast.LENGTH_SHORT).show();
							return 1;

						}	}
					else
					{
						if(postMessage.getText().toString().trim().length()>0)
						{
							s_text=postMessage.getText().toString().trim();
						}
						else
						{
							s_text="null";
						}
						// SETTING LOCATION
						if(location.getText().toString().trim().length()>0)
						{
							s_loc=location.getText().toString().trim();
						}
						else
						{
							s_loc="null";
						}
						// SETTING CUSTOM USER (if privacy is "custom_privacy")
						s_custom="null";

						if(fileFlag)
						{
							sendFile=videoFile;
							vp_flag="1";

						}else
						{
							sendFile=picfile;
							vp_flag="0";
						}
						jsonObjRecv = HttpClientStatusUpdate.sendHttpPostImage(Util.API + "post",sendFile,vp_flag,s_text,s_loc,s_privacy,s_custom,thirdPartyUserID);
					}
				}
				//FOR THIRD PARTY POSTING
				else if(asyncFlag==true && checkActivity.equalsIgnoreCase("ThirdPartyUser"))
				{
					if(s_privacy.equalsIgnoreCase("custom_privacy"))
					{
						if(postCustomFriends.getText().toString().trim().length()>0)
						{

							if(location.getText().toString().trim().length()>0)
							{
								jsonObjSend.put("location_name", location.getText().toString().trim());
							}
							// SETTING PRIVACY
							jsonObjSend.put(s_privacy, "1");
							//SETTING CUSTOM USER
							jsonObjSend.put("csv_custom_list_users",string_customUsers);
							// SETTING TRACKER
							jsonObjSend.put("tracker_type","status update");
							//SETTING PARAMETER FOR THIRDPARTY POSTING
							jsonObjSend.put("to_user",Util.THIRD_PARTY_USER_ID);
							if(postMessage.getText().toString().trim().length()>0)
							{
								jsonObjSend.put("status_update_text", postMessage.getText().toString().trim());
								jsonObjRecv = HttpPostClient.sendHttpPost(Util.API + "post", jsonObjSend);
							}
							else
							{
								return 3;
							}

						}	
						else
						{
							//	Toast.makeText(getApplicationContext(), "Please select the custom users", Toast.LENGTH_SHORT).show();
							return 1;
						}}
					else
					{
						if(location.getText().toString().trim().length()>0)
						{
							jsonObjSend.put("location_name", location.getText().toString().trim());
						}
						// SETTING PRIVACY
						jsonObjSend.put(s_privacy, "1");

						// SETTING TRACKER
						jsonObjSend.put("tracker_type","status update");
						//SETTING PARAMETER FOR THIRDPARTY POSTING
						jsonObjSend.put("to_user",Util.THIRD_PARTY_USER_ID);
						if(postMessage.getText().toString().trim().length()>0)
						{
							jsonObjSend.put("status_update_text", postMessage.getText().toString().trim());
							jsonObjRecv = HttpPostClient.sendHttpPost(Util.API + "post", jsonObjSend);
						}
						else
						{
							return 3;
						}
					}
				}
				else if (asyncFlag==false && checkActivity.equalsIgnoreCase("ThirdPartyUser")) {
					if(s_privacy.equalsIgnoreCase("custom_privacy"))
					{
						if(postCustomFriends.getText().toString().trim().length()>0)
						{
							if(postMessage.getText().toString().trim().length()>0)
							{
								s_text=postMessage.getText().toString().trim();
							}
							else
							{
								s_text="null";
							}
							// SETTING LOCATION
							if(location.getText().toString().trim().length()>0)
							{
								s_loc=location.getText().toString().trim();
							}
							else
							{
								s_loc="null";
							}

							// SETTING CUSTOM USER (if privacy is "custom_privacy")
							s_custom=string_customUsers;

							if(fileFlag)
							{
								sendFile=videoFile;
								vp_flag="1";

							}else
							{
								sendFile=picfile;
								vp_flag="0";
							}
							jsonObjRecv = HttpClientStatusUpdate.sendHttpPostImage(Util.API + "post",sendFile,vp_flag,s_text,s_loc,s_privacy,s_custom,Util.THIRD_PARTY_USER_ID);

						}else
						{
							//Toast.makeText(getApplicationContext(), "Please select the custom users", Toast.LENGTH_SHORT).show();
							return 1;

						}	}
					else
					{
						if(postMessage.getText().toString().trim().length()>0)
						{
							s_text=postMessage.getText().toString().trim();
						}
						else
						{
							s_text="null";
						}
						// SETTING LOCATION
						if(location.getText().toString().trim().length()>0)
						{
							s_loc=location.getText().toString().trim();
						}
						else
						{
							s_loc="null";
						}
						// SETTING PRIVACY
						//s_privacy="public_privacy";

						// SETTING CUSTOM USER (if privacy is "custom_privacy")
						s_custom="null";

						if(fileFlag)
						{
							sendFile=videoFile;
							vp_flag="1";

						}else
						{
							sendFile=picfile;
							vp_flag="0";
						}
						jsonObjRecv = HttpClientStatusUpdate.sendHttpPostImage(Util.API + "post",sendFile,vp_flag,s_text,s_loc,s_privacy,s_custom,Util.THIRD_PARTY_USER_ID);
					}
				}

				//END THIRD PARTY POSTING

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return 2;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==1)
			{
				pDialog.dismiss();
				Toast.makeText(getApplicationContext(), "Please select the custom users", Toast.LENGTH_SHORT).show();

			}
			else if(result==3)
			{
				pDialog.dismiss();
				Toast.makeText(getApplicationContext(), "Please write something !", Toast.LENGTH_SHORT).show();

			}
			else
			{
				pDialog.dismiss();
				finish();
			}

		}

	}
	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

		JSONObject jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData) {

			List<HashMap<String, String>> places = null;

			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try{
				jObject = new JSONObject(jsonData[0]);

				// Getting the parsed data as a List construct
				places = placeJsonParser.parse(jObject);

			}catch(Exception e){
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			String[] from = new String[] { "description"};
			int[] to = new int[] { android.R.id.text1 };

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			location.setAdapter(adapter);
		}
	}

	// Fetches all places from GooglePlaces AutoComplete Web Service
	private class PlacesTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "AIzaSyCuUvvgFmC8I2e-Cwavzl7dkvEcq8aUoIs";

			String input="raichur";

			try {
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			// place type to be searched
			String types = "types=geocode";

			// Sensor enabled
			String sensor = "sensor=true";

			// Building the parameters to the web service
			String parameters = input+"&"+types+"&"+sensor+"&"+"key="+key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;
			try{
				// Fetching the data from we service
				data = Util.downloadUrl(url);
			}catch(Exception e){
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Creating ParserTask
			parserTask = new ParserTask();

			// Starting Parsing the JSON string returned by Web Service
			parserTask.execute(result);
		}
	}
	//get path for kitkat gallery
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
	//end get path for kitkat gallery
}
