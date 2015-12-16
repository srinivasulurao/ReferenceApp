package com.fivestarchicken.lms.fragments;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.libs.CircularImageView;
import com.fivestarchicken.lms.model.User;
import com.fivestarchicken.lms.utils.Commons;
import com.fivestarchicken.lms.webservice.Webservice;

@SuppressLint("NewApi")
public class FragmentProfile extends Fragment {

	TextView tvname, tvphone, tvemail, tvrole;
	private DbAdapter dh;
	User user;
	SharedPreferences sharedPreferences;
	Webservice Webservices = new Webservice();
	String userId;
	CircularImageView ciProfileImage;
	EditText etProfile = null;
	File imgFile;
	ImageView ivprofileEdit;
	String syncTime;
	String lastSyncTime;
	LinearLayout llbody;
	LinearLayout llStarRate;

	private final static int ACTIVITY_TAKE_PHOTO = 1;
	static public final int RESULT_LOAD_IMAGE = 267;
	static public final int PIC_CROP = 4044;

	FrameLayout flEditUserName, flEditEmail, flEditPhoneNumber;
	private TextView changePwd;
	private EditText et_old_pwd;
	private String oldPassword;
	private String newPassword;
	private String confirmPassword;
	private EditText et_pwd;
	private EditText et_confirm_pwd;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_profile, container, false);

		initilizeUI(v);

		return v;
	}

	public void onClick(View v) {

		switch (v.getId()) {

		}
	}

	private void initilizeUI(View v) {

		try {

			tvname = (TextView) v.findViewById(R.id.tvname);
			tvphone = (TextView) v.findViewById(R.id.tvphone);
			tvemail = (TextView) v.findViewById(R.id.tvemail);
			ciProfileImage = (CircularImageView) v
					.findViewById(R.id.ciprofileimage);
			ivprofileEdit = (ImageView) v.findViewById(R.id.iveditProfilepic);
			flEditUserName = (FrameLayout) v.findViewById(R.id.flusername);
			flEditEmail = (FrameLayout) v.findViewById(R.id.flemail);
			flEditPhoneNumber = (FrameLayout) v
					.findViewById(R.id.flphonenumber);
			llStarRate= (LinearLayout) v
					.findViewById(R.id.llrate);
			llbody = (LinearLayout) v.findViewById(R.id.llbody);

			// /////
			changePwd = (TextView) v.findViewById(R.id.txt_changepassword);

			// tvrole = (TextView) v.findViewById(R.id.tvrole);
			this.dh = new DbAdapter(getActivity());
			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			userId = sharedPreferences.getString("userId", null);

			/*
			 * if (Commons.isNetworkAvailable(getActivity())) { lastSyncTime =
			 * dh.getLastSynctime(Commons.profileModule);
			 * 
			 * 
			 * new AsyProfileView().execute(); }else{
			 */

			loadProfileValues();
			// }

			flEditUserName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					displayEditProfile("username");
				}
			});
			flEditEmail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					displayEditProfile("email");
				}
			});
			flEditPhoneNumber.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					displayEditProfile("phone");
				}
			});

			ivprofileEdit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent i = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

					startActivityForResult(i, RESULT_LOAD_IMAGE);

				}
			});

			changePwd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dialogChangePwd();

				}
			});

			// tvrole.setText("Role : "+user.getRole());*/

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	// code by TH
	void dialogChangePwd() {// final User user

		ImageView ivClose;
		Button btChngPwd;

		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.dialog_change_pwd, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		ivClose = (ImageView) promptsView.findViewById(R.id.ivclose_CP);
		et_old_pwd = (EditText) promptsView.findViewById(R.id.et_old_pwd_CP);
		et_pwd = (EditText) promptsView.findViewById(R.id.et_pwd_CP);
		et_confirm_pwd = (EditText) promptsView
				.findViewById(R.id.et_confirm_pwd_CP);
		btChngPwd = (Button) promptsView.findViewById(R.id.btn_chngePwd_CP);

		btChngPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// forgotPwd = ((EditText) etEmail).getText().toString();
				// if (forgotPwd == null || password.length() == 0) {
				//
				// Toast.makeText(ActivityLogin.this, "Please enter Email Id",
				// Toast.LENGTH_SHORT).show();
				//
				// } else {
				//
				// // validatePassward(user, password);
				//
				// }

				oldPassword = et_old_pwd.getText().toString();
				newPassword = et_pwd.getText().toString();
				confirmPassword = et_confirm_pwd.getText().toString();

				if (oldPassword.isEmpty()) {
					Toast.makeText(getActivity(), "Please enter old password",
							Toast.LENGTH_SHORT).show();
				} else {
					if (newPassword.isEmpty()) {
						Toast.makeText(getActivity(),
								"Please enter new password", Toast.LENGTH_SHORT)
								.show();
					} else {

						if (confirmPassword.isEmpty()) {

							Toast.makeText(getActivity(),
									"Please enter confirmPassword",
									Toast.LENGTH_SHORT).show();

						} else {

							if (!checkPassWordAndConfirmPassword(newPassword,
									confirmPassword)) {
								confirmPassword = et_confirm_pwd.getText()
										.toString();
								et_confirm_pwd
										.setError("Password Not Matching.");
							} else {
								changePwd();
							}

						}

					}
				}

			}
		});

		// et_pwd.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before,
		// int count) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// public void afterTextChanged(Editable s) {
		// // TODO Auto-generated method stub
		// // min lenth 10 and max lenth 12 (2 extra for - as per phone
		// // matcher format)
		// newPassword = et_pwd.getText().toString();
		// isValidnewPassword(newPassword);
		//
		// // Toast.makeText(
		// // getApplicationContext(),
		// //
		// "Password should contain minimum of 7charcters with 1upper case letter and 1numerical",
		// // Toast.LENGTH_LONG).show();
		// }
		//
		// });

		// et_confirm_pwd.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before,
		// int count) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// public void afterTextChanged(Editable s) {
		// // TODO Auto-generated method stub
		// // min lenth 10 and max lenth 12 (2 extra for - as per phone
		// // matcher format)
		// confirmPassword = et_confirm_pwd.getText().toString();
		// isValidconfirmPassword(confirmPassword);
		//
		// // Toast.makeText(
		// // getApplicationContext(),
		// //
		// "Password should contain minimum of 7charcters with 1upper case letter and 1numerical",
		// // Toast.LENGTH_LONG).show();
		// }
		//
		// });

		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				alertDialog.cancel();

			}
		});

	}

	// code by TH
	public boolean checkPassWordAndConfirmPassword(String password,
			String confirmPassword) {
		boolean pstatus = false;
		if (confirmPassword != null && password != null) {
			if (password.equals(confirmPassword)) {
				pstatus = true;
			}
		}
		return pstatus;
	}

	// code by TH
	public void changePwd() {
		new changePwdAPI().execute();
	}

	// code by TH
	private class changePwdAPI extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(getActivity());
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(String... unused) {
			// regjson = userFunction.login(email, passward, typeValue);

			return Webservices.changePassword(userId, oldPassword, newPassword,
					confirmPassword);
			
		//	return Webservices.changePassword(userId, oldPassword, newPassword);

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

						Toast.makeText(getActivity(), Message,
								Toast.LENGTH_LONG).show();

						user.setPassward(newPassword);

						dh.updateUser(user);

					} else {

						String errormessage = joresult.getString("message");

						Toast.makeText(getActivity(), errormessage,
								Toast.LENGTH_LONG).show();

					}

				}

			} catch (Exception e) {

				e.printStackTrace();
			}

		}
	}

	void loadProfileValues() {
		llbody.setVisibility(View.VISIBLE);
		user = dh.getUserDetail(userId);

		// String lastSyncTime = dh.getLastSynctime(Commons.resultModule);

		tvname.setText(user.getUserName());
		tvphone.setText(user.getPhone());
		tvemail.setText(user.getEmail());
		
         Integer countStar = new Integer(user.getStarRate());
		
		Integer blankStarCount=Commons.MAX_STAR-countStar;
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				50, 50);
		
		for (Integer i = 0; i < countStar; i++) {

			ImageView myImage = new ImageView(getActivity());
			myImage.setLayoutParams(layoutParams);
			myImage.setImageResource(R.drawable.star_on);
			llStarRate.addView(myImage);

		}
		
		for (Integer i = 0; i < blankStarCount; i++) {

			ImageView myImage = new ImageView(getActivity());
			myImage.setLayoutParams(layoutParams);
			myImage.setImageResource(R.drawable.star_off);
			llStarRate.addView(myImage);

		}


		loadProfileImage();
	}

	void loadProfileImage() {

		File folder = new File(Environment.getExternalStorageDirectory() + "/"
				+ Commons.appFolder + "");
		if (!folder.exists()) {
			folder.mkdir();// If there is no folder it will be created.
		}

		if (user.getProfileImage() != null
				&& user.getProfileImage().length() > 0) {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/" + Commons.appFolder + "/" + user.getProfileImage());

			if (file.exists()) {

				Bitmap myBitmap = BitmapFactory.decodeFile(file
						.getAbsolutePath());

				ciProfileImage.setImageBitmap(myBitmap);

			} else {
				if (Commons.isNetworkAvailable(getActivity())) {
					new AsyDownloadProfilePic().execute();
				}

			}
		}
	}

	void displayEditProfile(final String type) {

		TextView tvheadder;
		final// final String editTextStr;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.dialog_profile_edit, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);

		tvheadder = (TextView) promptsView.findViewById(R.id.tvheader);
		etProfile = (EditText) promptsView.findViewById(R.id.etprofile);
		alertDialogBuilder.setPositiveButton("Save", null);

		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String editTextStr = ((TextView) etProfile).getText()
								.toString();

						if (type.equals("username")) {

							if (editTextStr.trim().length() <= 0) {

								Toast.makeText(getActivity(),
										"Please enter user name",
										Toast.LENGTH_LONG).show();

							} else if (!editTextStr.equals(user.getUserName())) {

								user.setUserName(editTextStr);

								if (Commons.isNetworkAvailable(getActivity())) {
									new asyEditProfile().execute();
								}
								alertDialog.cancel();

							} else {
								alertDialog.cancel();

							}

						} else if (type.equals("email")) {

							if (editTextStr.trim().length() <= 0) {

								Toast.makeText(getActivity(),
										"Please enter email", Toast.LENGTH_LONG)
										.show();

							} else if (!android.util.Patterns.EMAIL_ADDRESS
									.matcher(editTextStr).matches()) {

								Toast.makeText(getActivity(),
										"Please enter valid email id",
										Toast.LENGTH_LONG).show();

							} else if (!editTextStr.equals(user.getEmail())) {

								user.setEmail(editTextStr);
								if (Commons.isNetworkAvailable(getActivity())) {
									new asyEditProfile().execute();
								}
								alertDialog.cancel();

							} else {
								alertDialog.cancel();

							}

						} else if (type.equals("phone")) {

							if (editTextStr.trim().length() <= 0) {

								Toast.makeText(getActivity(),
										"Please enter phone number",
										Toast.LENGTH_LONG).show();

							} else if (!(editTextStr.trim().length() == 10)) {

								Toast.makeText(getActivity(),
										"Please enter 10 digit phone number",
										Toast.LENGTH_LONG).show();

							}

							else if (!editTextStr.equals(user.getPhone())) {

								user.setPhone(editTextStr);
								if (Commons.isNetworkAvailable(getActivity())) {
									new asyEditProfile().execute();
								}

								alertDialog.cancel();

							} else {
								alertDialog.cancel();

							}

						}

					}

				});
			}
		});
		alertDialog.show();

		if (type.equals("username")) {

			tvheadder.setText("User Name");
			etProfile.setText(user.getUserName());

		} else if (type.equals("email")) {

			tvheadder.setText("Email");
			etProfile.setText(user.getEmail());
			etProfile.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
					| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		} else if (type.equals("phone")) {

			tvheadder.setText("Phone Number");
			etProfile.setText(user.getPhone());
			etProfile.setInputType(InputType.TYPE_CLASS_PHONE);

		}

	}

	private class asyEditProfile extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(getActivity());
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(String... unused) {
			// regjson = userFunction.login(email, passward, typeValue);

			return Webservices.editProfile(user);
		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();

				JSONObject joresult = new JSONObject(result);

				String status = joresult.getString("status");
				syncTime = joresult.getString("sync_time");
				if (status.equals("200")) {

					JSONObject joValues = new JSONObject(
							joresult.getString("result"));
					user = new User();
					user.setUserId(joValues.getString("user_id"));
					user.setEmail(joValues.getString("user_email"));
					user.setPhone(joValues.getString("user_phone"));
					user.setUserName(joValues.getString("user_name"));
					user.setRole(joValues.getString("role"));
					user.setProfileImage(joValues.getString("profile_pic"));
					user.setStarRate(joValues.getString("profile_star"));

					dh.updateUser(user);

					dh.updateSyncStatus(Commons.profileModule, syncTime);
					loadProfileValues();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (RESULT_LOAD_IMAGE):
			if (resultCode == Activity.RESULT_OK) {

				try {

					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getActivity().getContentResolver().query(
							selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

					String picturePath = cursor.getString(columnIndex);
					cursor.close();

					imgFile = new File(picturePath);

					// new LoginActss().execute();

					try {
						// call the standard crop action intent (the user device
						// may not support it)
						Intent cropIntent = new Intent(
								"com.android.camera.action.CROP");
						// indicate image type and Uri
						cropIntent.setDataAndType(Uri.fromFile(imgFile),
								"image/*");
						// set crop properties
						cropIntent.putExtra("crop", "true");
						// indicate aspect of desired crop
						cropIntent.putExtra("aspectX", 1);
						cropIntent.putExtra("aspectY", 1);
						// indicate output X and Y
						cropIntent.putExtra("outputX", 256);
						cropIntent.putExtra("outputY", 256);
						// retrieve data on return
						cropIntent.putExtra("return-data", true);
						// start the activity - we handle returning in
						// onActivityResult
						startActivityForResult(cropIntent, PIC_CROP);
					}
					// respond to users whose devices do not support the crop
					// action
					catch (ActivityNotFoundException anfe) {
						// display an error message
						String errorMessage = "Whoops - your device doesn't support the crop action!";

					}

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

				Cursor cursor = getActivity().getContentResolver().query(
						selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				user.setImagePath(picturePath);

				if (Commons.isNetworkAvailable(getActivity())) {
					new AsyuploadProfilePic().execute();
				}

			}
			break;
		}
	}

	private class AsyuploadProfilePic extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.uploadProfilePic(user);
		}

		protected void onProgressUpdate(Void... progress) {

			progDailog = new ProgressDialog(getActivity());
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
						syncTime = joresult.getString("sync_time");

						JSONObject joValues = new JSONObject(
								joresult.getString("result"));
						user = new User();
						user.setUserId(joValues.getString("user_id"));
						user.setEmail(joValues.getString("user_email"));
						user.setPhone(joValues.getString("user_phone"));
						user.setUserName(joValues.getString("user_name"));
						user.setRole(joValues.getString("role"));
						user.setProfileImage(joValues.getString("profile_pic"));
						user.setStarRate(joValues.getString("profile_star"));

						dh.updateUser(user);

						dh.updateSyncStatus(Commons.profileModule, syncTime);
						loadProfileValues();
					} else {

						loadProfileValues();

					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				loadProfileValues();
			}
		}
	}

	private class AsyProfileView extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.profileView(userId, "1", lastSyncTime);
		}

		protected void onProgressUpdate(Void... progress) {

			progDailog = new ProgressDialog(getActivity());
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
						syncTime = joresult.getString("sync_time");

						JSONObject joValues = new JSONObject(
								joresult.getString("result"));
						user = new User();
						user.setUserId(joValues.getString("user_id"));
						user.setEmail(joValues.getString("user_email"));
						user.setPhone(joValues.getString("user_phone"));
						user.setUserName(joValues.getString("user_name"));
						user.setRole(joValues.getString("role"));
						user.setProfileImage(joValues.getString("profile_pic"));
						user.setStarRate(joValues.getString("profile_star"));

						dh.updateUser(user);

						dh.updateSyncStatus(Commons.profileModule, syncTime);
						loadProfileValues();
					} else {

						loadProfileValues();

					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				loadProfileValues();
			}
		}
	}

	private class AsyDownloadProfilePic extends
			AsyncTask<String, Void, Boolean> {
		ProgressDialog progDailog;

		@Override
		protected Boolean doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.downloadProfilePic(user.getProfileImage());
		}

		protected void onProgressUpdate(Void... progress) {

			progDailog = new ProgressDialog(getActivity());
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Boolean audioInfo) {

			progDailog.dismiss();

			loadProfileImage();

		}
	}

}
