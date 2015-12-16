package com.fivestarchicken.lms;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.fivestarchicken.lms.ActivityEmployeeLogin.LanguageSelectedListener;
import com.fivestarchicken.lms.adapter.AdapterDropDown;
import com.fivestarchicken.lms.libs.CircularImageView;
import com.fivestarchicken.lms.model.NewEmployee;
import com.fivestarchicken.lms.utils.Commons;
import com.fivestarchicken.lms.webservice.Webservice;

public class ActivityRegister extends ActionBarActivity implements
		OnClickListener {

	private EditText name;
	private EditText phNumber;
	private EditText email;
	/*private EditText city;
	private EditText location;*/
	private TextView tvRegister;

	private String NAME;
	private String PHNUMBER;
	private String EMAIL;
	/*private String CITY;
	private String LOCATION;*/
	private ImageView takePhoto;
	Integer languageSelection;
	// File imgFile;
	private ActionBar actionbar;
	String imagePath;
	Spinner employeeLanguage;
	ArrayList<String> spinnerOption;
	// protected static final int CAMERA_REQUEST = 0;
	// protected static final int GALLERY_PICTURE = 1;
	// protected static final int RESULT_LOAD_IMAGE = 0;

	private Intent pictureActionIntent = null;

	Bitmap bitmap;

	String selectedImagePath;
	CircularImageView ciProfileImage;
	private View v;
	private TextView cam;

	private View grpview;
	private Dialog releaseNote;
	private TextView gal;
	private TextView close;
	NewEmployee newEmployee;

	// private int CAMERA_CAPTURE_IMAGE_REQUEST_CODE;
	private int grp_pic_count;

	Webservice webservices = new Webservice();
	private final static int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;
	static public final int RESULT_LOAD_IMAGE = 267;
	static public final int PIC_CROP = 4044;

	SharedPreferences sharedPreferences;

	String branchId;

	private String candidateId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);

		actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
		actionbar.setCustomView(mCustomView);
		actionbar.setDisplayShowCustomEnabled(true);

		ciProfileImage = (CircularImageView) findViewById(R.id.Pic_image);

		name = (EditText) findViewById(R.id.userName_Register);
		phNumber = (EditText) findViewById(R.id.phoneNumber_Register);
		email = (EditText) findViewById(R.id.emailId_Register);
		//city = (EditText) findViewById(R.id.city_Register);
		//location = (EditText) findViewById(R.id.location_Register);
		takePhoto = (ImageView) findViewById(R.id.uploadPic_Register);
		tvRegister = (TextView) findViewById(R.id.tvregister);
		employeeLanguage = (Spinner)findViewById(R.id.employee_language);
		takePhoto.setOnClickListener(this);
		tvRegister.setOnClickListener(this);

		// code by TH

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityRegister.this);

		branchId = sharedPreferences.getString("branchId", null);
		languageSelection=null;
		spinnerOption = new ArrayList<String>();
		spinnerOption.add("--Select Language--");
		spinnerOption.add("English");
		spinnerOption.add("Kannada");
		spinnerOption.add("Telugu");
		
		employeeLanguage.setAdapter(new AdapterDropDown(
				ActivityRegister.this, R.layout.spinner_content,
				spinnerOption));
		employeeLanguage
				.setOnItemSelectedListener(new LanguageSelectedListener());

	}
	
	public class LanguageSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {

			languageSelection = position;
		}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		
		}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tvregister:
			SaveData();

			// Intent i = new Intent();
			// i.setClass(ActivityRegister.this, ActivityInterviewExam.class);
			// startActivity(i);

			break;

		case R.id.uploadPic_Register:
			// startDialog();
			dialogPhotoSelect();

			break;

		default:
			break;
		}

	}

	// code by TH
	private class interviewReviewAPI extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(ActivityRegister.this);
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(String... unused) {
			// regjson = userFunction.login(email, passward, typeValue);

			return webservices.interviewRegitserAPI(NAME, EMAIL, PHNUMBER,
					branchId,imagePath);

		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();
				if (result != null && result.length() > 0) {

					JSONObject joresult = new JSONObject(result);

					Log.d("TAG", "joresult" + joresult);

					String status = joresult.getString("status");

					if (status.equals("200")) {

						String Message = joresult.getString("message");

						candidateId = joresult.getString("candidate_id");

						Log.d("TAG", "candidateId :" + candidateId);

						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("candidateId", candidateId);
						editor.putString("intlanguageType", languageSelection.toString());

						editor.commit();

						

						Intent i = new Intent();
						i.setClass(ActivityRegister.this,
								ActivityInterviewExam.class);
						// i.putExtra("languageType", languageSelection);

						startActivity(i);
						finish();

					} else {

						String errormessage = joresult.getString("message");

						Toast.makeText(ActivityRegister.this, errormessage,
								Toast.LENGTH_LONG).show();

					}

				}

			} catch (Exception e) {

				e.printStackTrace();
			}

		}
	}

	void dialogPhotoSelect() {

		LayoutInflater li = LayoutInflater.from(ActivityRegister.this);// getActivity()
		grpview = li.inflate(R.layout.dialog_photo_select, null);

		releaseNote = new Dialog(ActivityRegister.this);// getActivity()
		releaseNote.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		releaseNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		releaseNote.setContentView(grpview);
		releaseNote.setCancelable(false);
		releaseNote.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		cam = (TextView) grpview.findViewById(R.id.tvcamera);
		gal = (TextView) grpview.findViewById(R.id.tvgallery);
		close = (TextView) grpview.findViewById(R.id.tvcancel);

		cam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent,
						CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
			}
		});

		gal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
				// String path = Environment.getExternalStorageDirectory()
				// + "/images/imagename.jpg";
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
				// if (control_pic.equals("EDIT")) {
				// GroupFriendList.clear();
				// new getGroupUserlistAsyncTask().execute();
				// } else {
				// control_pic = "CREATE";
				// creategroup();
				// }
			}
		});
		releaseNote.show();
	}

	// code by TH
	private void SaveData() {

		NAME = URLEncoder.encode(name.getText().toString());
		PHNUMBER = phNumber.getText().toString();
		EMAIL = email.getText().toString();
		//CITY = URLEncoder.encode(city.getText().toString());
	//	LOCATION = URLEncoder.encode(location.getText().toString());

		if (NAME.isEmpty()) {

			showAlert("Enter the Name");
		} else if (PHNUMBER.isEmpty()) {
			// enter number
			showAlert("Enter the Phone Number");
		} else if (EMAIL.isEmpty()) {
			// enter Email
			showAlert("Enter the Email");
		} /*else if (CITY.isEmpty()) {
			// enter city
			showAlert("Enter the City");
		} else if (LOCATION.isEmpty()) {
			// enter location
			showAlert("Enter the Location");
		}*/ else if (imagePath == null) {
			// enter location
			showAlert("Please add Profile Image");
		} else if (languageSelection == null || languageSelection == 0) {

			showAlert("Please enter Language");

		}else {

			// newEmployee.setName(URLEncoder.encode(NAME, "UTF-8"));
			// newEmployee.setPhone(PHNUMBER);
			//
			// newEmployee.setEmail(EMAIL);
			// newEmployee.setCity(URLEncoder.encode(CITY, "UTF-8"));
			// newEmployee.setLocation(URLEncoder.encode(LOCATION,
			// "UTF-8"));
			// newEmployee.setImagePath("");

			if (Commons.isNetworkAvailable(ActivityRegister.this)) {

				new interviewReviewAPI().execute();

			} else {
				showAlert("Check your internet connection");
			}
		}

	}

	/**
	 * Checking device has camera hardware or not
	 * */
	private boolean isDeviceSupportCamera() {
		if (ActivityRegister.this.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}// getActivity()

	/**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (RESULT_LOAD_IMAGE):
			if (resultCode == Activity.RESULT_OK) {

				try {

					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = ActivityRegister.this.getContentResolver()
							.query(selectedImage, filePathColumn, null, null,
									null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

					String picturePath = cursor.getString(columnIndex);
					cursor.close();

					File imgFile = new File(picturePath);

					if (imgFile.exists()) {

						Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
								.getAbsolutePath());

						ciProfileImage.setImageBitmap(myBitmap);
						imagePath = picturePath;

						storeImage(myBitmap, imagePath);

					}

					// new LoginActss().execute();

					/*
					 * try { // call the standard crop action intent (the user
					 * device // may not support it) Intent cropIntent = new
					 * Intent( "com.android.camera.action.CROP"); // indicate
					 * image type and Uri
					 * cropIntent.setDataAndType(Uri.fromFile(imgFile),
					 * "image/*"); // set crop properties
					 * cropIntent.putExtra("crop", "true"); // indicate aspect
					 * of desired crop cropIntent.putExtra("aspectX", 1);
					 * cropIntent.putExtra("aspectY", 1); // indicate output X
					 * and Y cropIntent.putExtra("outputX", 256);
					 * cropIntent.putExtra("outputY", 256); // retrieve data on
					 * return cropIntent.putExtra("return-data", true); // start
					 * the activity - we handle returning in // onActivityResult
					 * startActivityForResult(cropIntent, PIC_CROP); } //
					 * respond to users whose devices do not support the crop //
					 * action catch (ActivityNotFoundException anfe) { //
					 * display an error message String errorMessage =
					 * "Whoops - your device doesn't support the crop action!";
					 * 
					 * }
					 */

				} catch (Exception ex) {
				}
				// Toast.makeText(this, ex.getMessage(),
				// Toast.LENGTH_LONG);
			}

			break;

		case (PIC_CROP):
			if (resultCode == Activity.RESULT_OK && null != data) {

				/*
				 * Bundle extras = data.getExtras(); // get the cropped bitmap
				 * final Bitmap thePic = extras.getParcelable("data");
				 */

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				File file = new File(picturePath);

				if (file.exists()) {

					Bitmap myBitmap = BitmapFactory.decodeFile(file
							.getAbsolutePath());

					ciProfileImage.setImageBitmap(myBitmap);

					storeImage(myBitmap, imagePath);

				}
				// user.setImagePath(picturePath);

			}
			break;

		case (CAMERA_CAPTURE_IMAGE_REQUEST_CODE):
			if (resultCode == Activity.RESULT_OK) {

				try {
					final Bitmap thePic = (Bitmap) data.getExtras().get("data");
					ciProfileImage.setImageBitmap(thePic);

					File file = new File(Commons.image_folder + "/temp.jpeg");

					if (file.exists())
						file.delete();
					try {
						FileOutputStream out = new FileOutputStream(file);
						thePic.compress(Bitmap.CompressFormat.JPEG, 90, out);
						imagePath = Commons.image_folder + "/temp.jpeg";
						out.flush();
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					

				} catch (Exception ex) {
				}
			}
			break;

		}
	}

	private boolean storeImage(Bitmap imageData, String filename) {
		// get path to external storage (SD card)

		// String iconsStoragePath = Environment.getExternalStorageDirectory() +
		// "/myAppDir/myImages/"

		String iconsStoragePath = null;

		if (!Commons.image_folder.isEmpty()) {

			iconsStoragePath = Commons.image_folder + "/temp.png";// Commons.temperaryImg

		} else {
			deleteFile(Commons.image_folder);
		}

		File sdIconStorageDir = new File(iconsStoragePath);

		// create storage directories, if they don't exist
		sdIconStorageDir.mkdirs();

		try {
			String filePath = sdIconStorageDir.toString() + filename;
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);

			BufferedOutputStream bos = new BufferedOutputStream(
					fileOutputStream);

			// choose another format if PNG doesn't suit you
			imageData.compress(CompressFormat.PNG, 100, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			Log.w("TAG", "Error saving image file: " + e.getMessage());
			return false;
		} catch (IOException e) {
			Log.w("TAG", "Error saving image file: " + e.getMessage());
			return false;
		}

		return true;
	}

	/*private class uploadProfilePic extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return webservices.uploadProfilePicRegister(candidateId);
		}

		protected void onProgressUpdate(Void... progress) {

			progDailog = new ProgressDialog(ActivityRegister.this);
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {

			progDailog.dismiss();

			try {
				if (result != null && result.length() > 0) {

					JSONObject joresult = new JSONObject(result);

					String status = joresult.getString("status");

					if (status.equals("200")) {
						// syncTime = joresult.getString("sync_time");
						//
						// JSONObject joValues = new JSONObject(
						// joresult.getString("result"));
						// user = new User();
						// user.setUserId(joValues.getString("user_id"));
						// user.setEmail(joValues.getString("user_email"));
						// user.setPhone(joValues.getString("user_phone"));
						// user.setUserName(joValues.getString("user_name"));
						// user.setRole(joValues.getString("role"));
						// user.setProfileImage(joValues.getString("profile_pic"));
						// user.setStarRate(joValues.getString("profile_star"));
						//
						// dh.updateUser(user);
						//
						// dh.updateSyncStatus(Commons.profileModule, syncTime);
						// loadProfileValues();
					} else {

						// loadProfileValues();

					}

				}

			} catch (Exception e) {

				e.printStackTrace();

			}
		}
	}
*/
	private class AsyProfileView extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return webservices.registerUser(newEmployee);
		}

		protected void onProgressUpdate(Void... progress) {

			progDailog = new ProgressDialog(ActivityRegister.this);
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {

			progDailog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject joresult = new JSONObject(result);

					String status = joresult.getString("status");

					if (status.equals("200")) {

					} else {

					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
	}

	private void showAlert(String msg) {
		try {
			Toast.makeText(ActivityRegister.this, msg, Toast.LENGTH_LONG)
					.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
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

}
