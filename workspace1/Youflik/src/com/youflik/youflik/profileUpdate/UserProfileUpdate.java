package com.youflik.youflik.profileUpdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;
import com.youflik.youflik.commonAdapters.SongsAdapter;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.NotificationGeneralModel;
import com.youflik.youflik.models.SongsModel;
import com.youflik.youflik.models.UserDetailsModel;
import com.youflik.youflik.notification.NotificationGeneralAdapter;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPostClientImageUpdate;
import com.youflik.youflik.proxy.HttpPutClient;
import com.youflik.youflik.songs.ProfileSongs;
import com.youflik.youflik.utils.InternalStorageContentProvider;
import com.youflik.youflik.utils.PlaceJSONParser;
import com.youflik.youflik.utils.Util;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import eu.janmuller.android.simplecropimage.CropImage;

public class UserProfileUpdate extends Activity {
	private Button done,change_cover,change_profile;
	private ImageView profile_pic,cover_pic;
	private EditText bio,website;
	private TextView song_name;
	public ImageLoader imageLoader;
	DisplayImageOptions profile_options,cover_option;
	private ProgressDialog pDialog;
	private String Song_URL;
	AutoCompleteTextView location;
	PlacesTask placesTask;
	ParserTask parserTask;
	private String bio_tmp,location_tmp,website_tmp,song_tmp;
	RadioButton gallery,camera;
	RadioGroup  select; 
	private  File mFileTemp;
	public static final String TAG = "UserProfileUpdate";    
	public static final int REQUEST_CODE_GALLERY      = 0x1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
	public static final int REQUEST_CODE_CROP_IMAGE   = 0x3;
	public static final int SONG_RESULT = 0x4;
	public int flag;
	public String photo_updated_path;
	private JSONObject jsonObjectReceive;
	private static String updateString="NoUpdate";
	private  boolean photoFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_update);
		imageLoader =imageLoader.getInstance();
		Typeface typeFace=Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/BentonSans-Regular.otf");
		photoFlag=false;
		profile_options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

		cover_option = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.gallery_placeholder)
		.showImageForEmptyUri(R.drawable.gallery_placeholder) 
		.showImageOnFail(R.drawable.gallery_placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

		done=(Button)findViewById(R.id.update_done);
		change_cover=(Button)findViewById(R.id.update_change_cover);
		change_profile=(Button)findViewById(R.id.update_change_profile);
		profile_pic=(ImageView)findViewById(R.id.update_profilepic);
		cover_pic=(ImageView)findViewById(R.id.update_user_cover_pic);
		bio=(EditText)findViewById(R.id.update_bio);
		bio.setTypeface(typeFace);
		location=(AutoCompleteTextView)findViewById(R.id.update_location);
		location.setTypeface(typeFace);
		location.setThreshold(1);
		website=(EditText)findViewById(R.id.update_website);
		website.setTypeface(typeFace);
		song_name=(TextView)findViewById(R.id.update_songs);
		song_name.setTypeface(typeFace);

		ConnectionDetector conn = new ConnectionDetector(getApplicationContext());
		if(conn.isConnectingToInternet())
		{
			new UserProfileInfoFragmentAsync().execute();	
		}
		else 
		{
			Crouton.makeText(UserProfileUpdate.this, getString(R.string.crouton_message), Style.ALERT).show();

		}

		done.setOnClickListener(new  OnClickListener() {

			@Override
			public void onClick(View v) {
				ConnectionDetector conn = new ConnectionDetector(UserProfileUpdate.this);
				if(conn.isConnectingToInternet())
				{
					if(photoFlag)
					{
						if(bio.getText().toString().equalsIgnoreCase(bio_tmp) && location.getText().toString().equalsIgnoreCase(location_tmp)
								&& website.getText().toString().equalsIgnoreCase(website_tmp) && song_name.getText().toString().equalsIgnoreCase(song_tmp))
						{
							updateString="update";
							Intent intent = new Intent();
							intent.putExtra("update", updateString);
							setResult(RESULT_OK,intent);
							finish();
						}

						else
						{
							new profileUpdate().execute();	

						}

					}
					else
					{
						if(bio.getText().toString().equalsIgnoreCase(bio_tmp) && location.getText().toString().equalsIgnoreCase(location_tmp) && website.getText().toString().equalsIgnoreCase(website_tmp))
						{
							updateString="update";
							Intent intent = new Intent();
							intent.putExtra("update", updateString);
							setResult(RESULT_OK,intent);
							finish();
						}

						else
						{
							new profileUpdate().execute();	

						}
					}
				}
				else{
					Crouton.makeText(UserProfileUpdate.this, getString(R.string.crouton_message), Style.ALERT).show();
				}
			}
		});

		change_cover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				flag=2;
				final Dialog dialog = new Dialog(UserProfileUpdate.this);  
				//dialog.getWindow().requestFeature(Window.FEATURE_CUSTOM_TITLE);  
				dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
						WindowManager.LayoutParams.FLAG_FULLSCREEN);  
				dialog.setContentView(R.layout.dialog_select_gallery);  
				//dialog.getWindow().setBackgroundDrawable(  
				//new ColorDrawable(Color.argb(255, 204, 255, 229))); 
				dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setTitle("Select");
				dialog.show(); 
				select=(RadioGroup)dialog.findViewById(R.id.select);
				gallery =(RadioButton)dialog.findViewById(R.id.gallery);
				gallery.setChecked(false);
				camera =(RadioButton)dialog.findViewById(R.id.camera);
				camera.setChecked(false);
				camera.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View v) {
						dialog.dismiss();  
						String state = Environment.getExternalStorageState();
						if (Environment.MEDIA_MOUNTED.equals(state)) {
							mFileTemp = new File(Environment.getExternalStorageDirectory(), Util.TEMP_PHOTO_FILE_NAME);
						}
						else {
							mFileTemp = new File(getApplicationContext().getFilesDir(), Util.TEMP_PHOTO_FILE_NAME);
						}
						//
						takePicture();


					}});
				gallery.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View v) {
						dialog.dismiss();  
						openGallery();
						String state = Environment.getExternalStorageState();
						if (Environment.MEDIA_MOUNTED.equals(state)) {
							mFileTemp = new File(Environment.getExternalStorageDirectory(), Util.TEMP_PHOTO_FILE_NAME);
						}
						else {
							mFileTemp = new File(getApplicationContext().getFilesDir(), Util.TEMP_PHOTO_FILE_NAME);
						}

					}});
			} 
		});

		change_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				flag=1;
				final Dialog dialog = new Dialog(UserProfileUpdate.this);  
				//dialog.getWindow().requestFeature(Window.FEATURE_CUSTOM_TITLE);  
				dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
						WindowManager.LayoutParams.FLAG_FULLSCREEN);  
				dialog.setContentView(R.layout.dialog_select_gallery);  
				//dialog.getWindow().setBackgroundDrawable(  
				//new ColorDrawable(Color.argb(255, 204, 255, 229))); 
				dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setTitle("SELECT");
				dialog.show(); 
				select=(RadioGroup)dialog.findViewById(R.id.select);
				gallery =(RadioButton)dialog.findViewById(R.id.gallery);
				gallery.setChecked(false);
				camera =(RadioButton)dialog.findViewById(R.id.camera);
				camera.setChecked(false);
				camera.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View v) {
						dialog.dismiss();  
						String state = Environment.getExternalStorageState();
						if (Environment.MEDIA_MOUNTED.equals(state)) {
							mFileTemp = new File(Environment.getExternalStorageDirectory(), Util.TEMP_PHOTO_FILE_NAME);
						}
						else {
							mFileTemp = new File(getApplicationContext().getFilesDir(), Util.TEMP_PHOTO_FILE_NAME);
						}
						//
						takePicture();


					}});
				gallery.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View v) {
						dialog.dismiss();  
						openGallery();
						String state = Environment.getExternalStorageState();
						if (Environment.MEDIA_MOUNTED.equals(state)) {
							mFileTemp = new File(Environment.getExternalStorageDirectory(), Util.TEMP_PHOTO_FILE_NAME);
						}
						else {
							mFileTemp = new File(getApplicationContext().getFilesDir(), Util.TEMP_PHOTO_FILE_NAME);
						}

					}});

			}
		});
		song_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ConnectionDetector conn = new ConnectionDetector(UserProfileUpdate.this);
				if(conn.isConnectingToInternet()){
					Intent songssearch = new Intent(UserProfileUpdate.this,ProfileSongs.class);
					startActivityForResult(songssearch,SONG_RESULT);	
				}else{
					Crouton.makeText(UserProfileUpdate.this, getString(R.string.crouton_message), Style.ALERT).show();
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
	}

	private class UserProfileInfoFragmentAsync extends AsyncTask<Void, Void, UserDetailsModel>{

		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(UserProfileUpdate.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}


		@Override
		protected UserDetailsModel doInBackground(Void... params) {

			JSONArray UserProfileInfoModelResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"user/"+Util.USER_ID);
			if(jsonObjectRecived != null){

				try{
					UserProfileInfoModelResponse = jsonObjectRecived.getJSONArray("userDetails");   
				}catch(JSONException e){
					e.printStackTrace();
				}
				UserDetailsModel UserProfileInfoModelData = new UserDetailsModel();
				JSONObject userprofile_info_Details;
				try{
					userprofile_info_Details = UserProfileInfoModelResponse.getJSONObject(0);
					UserProfileInfoModelData.setFirstname(userprofile_info_Details.getString("firstname"));
					UserProfileInfoModelData.setBio(userprofile_info_Details.getString("bio"));
					UserProfileInfoModelData.setWebsite_blog(userprofile_info_Details.getString("website_blog"));
					UserProfileInfoModelData.setCurrent_city(userprofile_info_Details.getString("current_city"));
					UserProfileInfoModelData.setUser_profile_photo_path(userprofile_info_Details.getString("user_profile_photo_path"));
					UserProfileInfoModelData.setUser_cover_photo_path(userprofile_info_Details.getString("user_cover_photo_path"));
					UserProfileInfoModelData.setSong_title(userprofile_info_Details.getString("song_title"));
					UserProfileInfoModelData.setSong_cover_photo_path(userprofile_info_Details.getString("song_cover_photo_path"));
					UserProfileInfoModelData.setActual_song_path(userprofile_info_Details.getString("actual_song_path"));
					Song_URL=userprofile_info_Details.getString("actual_song_path");
					UserProfileInfoModelData.setSong_basename(userprofile_info_Details.getString("song_basename"));
					UserProfileInfoModelData.setFried_count(userprofile_info_Details.getString("fried_count"));
					UserProfileInfoModelData.setBio(userprofile_info_Details.getString("bio"));
					UserProfileInfoModelData.setFollower_count(userprofile_info_Details.getString("follower_count"));
					UserProfileInfoModelData.setIs_accept_friend_request(userprofile_info_Details.getString("is_accept_friend_request"));
					UserProfileInfoModelData.setIs_pending_friend_request(userprofile_info_Details.getString("is_pending_friend_request"));
					UserProfileInfoModelData.setIs_friend(userprofile_info_Details.getString("is_friend"));

					return UserProfileInfoModelData;
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				return null;
			}
			return null;
		}

		@SuppressWarnings("static-access")
		@Override
		protected void onPostExecute(UserDetailsModel app) {
			super.onPostExecute(app);
			pDialog.dismiss();
			if(app == null){

			}else{
				if(app.getBio().equalsIgnoreCase("null") || app.getBio().equalsIgnoreCase("")){
					bio.setText("");
					bio_tmp="";
				}
				else{	
					bio.setText(app.getBio());
					bio_tmp=app.getBio();
				}


				if(app.getCurrent_city().equalsIgnoreCase("null") || app.getCurrent_city().equalsIgnoreCase("")){
					location.setText("");
					location_tmp="";
				}
				else{
					location.setText(app.getCurrent_city());
					location_tmp=app.getCurrent_city();
				}

				if(app.getWebsite_blog().equalsIgnoreCase("null") || app.getWebsite_blog().equalsIgnoreCase("")){
					website.setText("");
					website_tmp="";
				}
				else{
					website.setText(app.getWebsite_blog());
					website_tmp=app.getWebsite_blog();
				}

				if(app.getSong_title().equalsIgnoreCase("null") || app.getSong_title().equalsIgnoreCase("")){
					song_name.setText("");
					song_tmp="";
				}
				else{song_name.setText(app.getSong_title());
				song_tmp=app.getSong_title();
				}

				//setting proflie pic
				imageLoader.displayImage(app.getUser_profile_photo_path(), profile_pic, profile_options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {

					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

					}
				}, new ImageLoadingProgressListener() { 
					@Override
					public void onProgressUpdate(String imageUri, View view, int current,
							int total) {

					}
				}
						);// end setting profile pic
				//setting cover pic
				imageLoader.displayImage(app.getUser_cover_photo_path(),cover_pic, cover_option, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {

					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view, int current,
							int total) {

					}
				}
						);



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

	private class profileUpdate extends AsyncTask<Void, Void, Integer>{
		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(UserProfileUpdate.this);
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
				jsonObjSend.put("bio",bio.getText().toString());
				jsonObjSend.put("location_name",location.getText().toString());
				jsonObjSend.put("website_blog",website.getText().toString());
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
			JSONObject jsonObjRecv = HttpPutClient.sendHttpPost(Util.API+"user/"+Util.USER_ID, jsonObjSend);
			//Log.i("jsonObjRecv",jsonObjRecv.toString());
			return null;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			updateString="update";
			Intent intent = new Intent();
			intent.putExtra("update", updateString);
			setResult(RESULT_OK,intent);
			finish();
		}

	}
	// change profile and cover photo
	private void takePicture() {  

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 

		try {
			Uri mImageCaptureUri = null;
			String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mImageCaptureUri = Uri.fromFile(mFileTemp);
			}
			else {

				mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
			}	
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
		} catch (ActivityNotFoundException e) {

		}
	}

	private void openGallery() {

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY); 
	}

	private void startCropImage() {
		Intent intent = new Intent(UserProfileUpdate.this, CropImage.class);
		intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
		intent.putExtra(CropImage.SCALE, true);

		if(flag==1){
			intent.putExtra(CropImage.ASPECT_X, 2);
			intent.putExtra(CropImage.ASPECT_Y, 2);
		}else{
			intent.putExtra(CropImage.ASPECT_X, 3);
			intent.putExtra(CropImage.ASPECT_Y, 2);
		}
		startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	}
	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode !=RESULT_OK) {

			return;
		}

		Bitmap bitmap;

		switch (requestCode) {

		case SONG_RESULT:
			//	setMemberDetails();
			if(resultCode == Activity.RESULT_OK && null != data)
			{
				song_name.setText(data.getStringExtra("SongTitle"));
			}
			break;
		case REQUEST_CODE_GALLERY:
			try {
				InputStream inputStream = UserProfileUpdate.this.getContentResolver().openInputStream(data.getData());
				FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
				copyStream(inputStream, fileOutputStream);
				fileOutputStream.close();
				inputStream.close();
				startCropImage();

			} catch (Exception e) {

			}

			break;
		case REQUEST_CODE_TAKE_PICTURE:

			startCropImage();
			break;
		case REQUEST_CODE_CROP_IMAGE:

			String path = data.getStringExtra(CropImage.IMAGE_PATH);
			if (path == null) {
				return;
			}

			bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());

			if(flag==1)
			{   ConnectionDetector conn = new ConnectionDetector(UserProfileUpdate.this);
			if(conn.isConnectingToInternet())
			{
				new PicUploadAsync().execute(1,7);
			}
			else
			{
				Crouton.makeText(UserProfileUpdate.this, getString(R.string.crouton_message), Style.ALERT).show();
			}
			}
			else if (flag==2) {
				ConnectionDetector conn = new ConnectionDetector(UserProfileUpdate.this);
				if(conn.isConnectingToInternet())
				{
					new PicUploadAsync().execute(2,9);
				}
				else
				{
					Crouton.makeText(UserProfileUpdate.this, getString(R.string.crouton_message), Style.ALERT).show();
				}
			}
			else
			{
			}

			break;
		}
		super.onActivityResult(requestCode, resultCode, data); 
	}
	private class PicUploadAsync extends AsyncTask<Integer, Void, Integer>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(UserProfileUpdate.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			Integer param_flag=params[0];
			Integer api_flag=params[1];
			if(api_flag==7)
			{
				jsonObjectReceive = HttpPostClientImageUpdate.sendHttpPostImage(Util.API+"user_profile_photo", mFileTemp,param_flag);
			}else
			{
				jsonObjectReceive = HttpPostClientImageUpdate.sendHttpPostImage(Util.API+"user_cover_photo", mFileTemp,param_flag);
			}

			try {
				if(api_flag==7)
				{
					Util.NM_PROFILE_PIC=jsonObjectReceive.getString("actual_photo_path");
				}
				else
				{
					Util.USER_COVER_PIC=jsonObjectReceive.getString("actual_photo_path");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			photoFlag=true;
			if(flag==1){
				//Util.NM_PROFILE_PIC=photo_updated_path;
				imageLoader.displayImage(Util.NM_PROFILE_PIC, profile_pic, profile_options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override 
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {

					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view, int current,
							int total) {

					}
				}
						);

				/*
				imageLoader.displayImage(Util.NM_PROFILE_PIC, profile_pic, profile_options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {

					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view, int current,
							int total) {

					}
				}
						);*/


			}
			else if (flag==2) {
				//Util.USER_COVER_PIC=photo_updated_path;
				//setting of profile image
				imageLoader.displayImage(Util.USER_COVER_PIC, cover_pic, cover_option, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override 
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {

					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view, int current,
							int total) {

					}
				}
						);


			}
		}


	}
}
