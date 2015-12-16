package com.voicey.fragment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.voicey.activity.HomeActivity;
import com.voicey.activity.R;
import com.voicey.webservices.Webservices;

public class UserEditFragment extends Fragment {

	EditText etUserName, etPhoneNum, etEmail;
	TextView tvSave, tvuserId, cam, gal, close;
	SharedPreferences sharedPreferences;
	String userCode, userName, userPhone, userEmail;
	Webservices Webservices = new Webservices();
	String[] codes;
	View grpview;
	LayoutInflater li;
	Dialog releaseNote;
	// Activity request codes
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;

	// directory name to store captured images and videos
	private static final int RESULT_LOAD_IMAGE = 1;
	ImageView propic, change1, change2;
	String possibleEmail, mPhoneNumber;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.user_edit, container, false);

		initilizeUI(v);

		return v;
	}

	private void initilizeUI(View v) {

		try {
			etUserName = (EditText) v.findViewById(R.id.etname);
			etPhoneNum = (EditText) v.findViewById(R.id.etphno);
			etEmail = (EditText) v.findViewById(R.id.etmail);
			propic = (ImageView) v.findViewById(R.id.capture);
			change1 = (ImageView) v.findViewById(R.id.imgchange);
			change2 = (ImageView) v.findViewById(R.id.imgchangemail);
			tvuserId = (TextView) v.findViewById(R.id.tvuseridvaluel);
			tvSave = (TextView) v.findViewById(R.id.tvsave);

			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			userCode = sharedPreferences.getString("userCode", null);
			userName = sharedPreferences.getString("userName", null);
			userPhone = sharedPreferences.getString("userPhone", null);
			userEmail = sharedPreferences.getString("userEmail", null);

			TelephonyManager tMgr = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (tMgr.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
				mPhoneNumber = tMgr.getLine1Number();
				if (mPhoneNumber.length() == 0)
					;
				else
					userPhone = mPhoneNumber;
			}
			Account[] accounts = AccountManager.get(getActivity())
					.getAccountsByType("com.google");
			for (Account account : accounts) {
				if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
					possibleEmail = account.name;
				}
			}
			userEmail = possibleEmail;

			tvuserId.setText(userCode);
			etUserName.setText(userName);
			etPhoneNum.setText(userPhone);
			etEmail.setText(userEmail);

			// Checking camera availability
			if (!isDeviceSupportCamera()) {
				Toast.makeText(getActivity(),
						"Sorry! Your device doesn't support camera",
						Toast.LENGTH_LONG).show();
			} else {
				getFromSdcard();

				propic.setOnClickListener(new OnClickListener() {

					@SuppressLint("NewApi")
					public void onClick(View v) {
						Chooser();
					}
				});
			}

			change1.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					changedata();
				}
			});
			
			change2.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					changedata();
				}
			});
			
			tvSave.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					try {
						userName = ((TextView) etUserName).getText().toString();
						userPhone = ((TextView) etPhoneNum).getText()
								.toString();
						userEmail = ((TextView) etEmail).getText().toString();

						if (userName.length() == 0) {
							Toast.makeText(getActivity(), "Profile Name Blank",
									Toast.LENGTH_LONG).show();
						} else
							new saveUserNameAsyncTask().execute();
						// } else if(userPhone.length() < 6 ||
						// userPhone.length() > 13){
						// Toast.makeText(getActivity(),
						// "Phone number Invalid",
						// Toast.LENGTH_LONG).show();
						// } else if(!userEmail.equals("") &&
						// android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
						// {

						// } else {
						// Toast.makeText(getActivity(),
						// "Email Invalid",
						// Toast.LENGTH_LONG).show();
						// }
						Fragment fragment = new InboxFragment();
						FragmentManager fragmentManager = getActivity()
								.getFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager
								.beginTransaction();
						fragmentTransaction.replace(R.id.frame_container,
								fragment);
						fragmentTransaction.addToBackStack(null);
						fragmentTransaction.commit();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void getFromSdcard() {
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "Voicey" + File.separator + "profile_"
				+ userCode + ".png");
		if (f.exists()) {
			String path = f.getAbsolutePath();
			System.out.println(path);
			Bitmap myBitmap = BitmapFactory.decodeFile(path);
			propic.setImageBitmap(myBitmap);
		} else {
			Resources res = getResources();
			/** from an Activity */
			propic.setImageDrawable(res.getDrawable(R.drawable.add_img));
		}
	}

	public String GetCountryZipCode() {
		String CountryID = "";
		String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		codes = this.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < codes.length; i++) {
			String[] g = codes[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = codes[i];
				break;
			}
		}
		return CountryZipCode;
	}

	void changedata() {

		li = LayoutInflater.from(getActivity());
		grpview = li.inflate(R.layout.change_number, null);

		releaseNote = new Dialog(getActivity());
		releaseNote.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		releaseNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		releaseNote.setContentView(grpview);
		releaseNote.setCancelable(false);
		releaseNote.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		EditText mob = (EditText) grpview.findViewById(R.id.etMobNum);
		EditText mail = (EditText) grpview.findViewById(R.id.etmail);
		EditText verify = (EditText) grpview.findViewById(R.id.etverify);
		final TextView codeset = (TextView) grpview
				.findViewById(R.id.countrycode);
		close = (TextView) grpview.findViewById(R.id.tvclose);
		TextView verifycode = (TextView) grpview.findViewById(R.id.save_grp);
		TextView sendmail = (TextView) grpview.findViewById(R.id.tvsendmail);

		codes = this.getResources().getStringArray(R.array.CountryCodes);
		codeset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				TelephonyManager tMgr = (TelephonyManager) getActivity()
						.getSystemService(Context.TELEPHONY_SERVICE);
				if (tMgr.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
					mPhoneNumber = tMgr.getLine1Number();
					String cd = GetCountryZipCode();
					String[] s = cd.split(",");
					codeset.setText("+" + s[0].replaceAll("[^0-9]", ""));
				} else {
					Toast.makeText(getActivity(), "Your device has no SIM!",
							Toast.LENGTH_LONG).show();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setTitle("Select Your Region");
					builder.setItems(codes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									// Do something with the selection
									String c = codes[item];
									String[] s = c.split(",");
									codeset.setText("+"
											+ s[0].replaceAll("[^0-9]", ""));
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}
			}
		});

		sendmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		verifycode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// releaseNote.dismiss();
			}
		});
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
			}
		});
		releaseNote.show();
	}

	void Chooser() {

		li = LayoutInflater.from(getActivity());
		grpview = li.inflate(R.layout.photo_select, null);

		releaseNote = new Dialog(getActivity());
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
		TextView tv1 = (TextView) grpview.findViewById(R.id.tvvideo);
		TextView tv2 = (TextView) grpview.findViewById(R.id.textView34);

		tv1.setVisibility(View.GONE);
		tv2.setVisibility(View.GONE);
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
			}
		});
		releaseNote.show();
	}

	private class saveUserNameAsyncTask extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.saveUserName(userCode, userName, userPhone,
					userEmail);
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(getActivity());

			dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			dialog.setContentView(R.layout.loading_layout);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			dialog.show();

		}

		@Override
		protected void onPostExecute(String result) {
			try {
				dialog.dismiss();
				if (result != null) {

					JSONObject jObj = new JSONObject(result);
					sharedPreferences = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					SharedPreferences.Editor editor = sharedPreferences.edit();
					String Name = URLDecoder.decode(jObj.getString("Name"),
							"UTF-8");
					editor.putString("userName", Name);
					editor.putString("userPhone", userPhone);
					editor.putString("userEmail", userEmail);

					editor.commit();
					// new GetAudioList().execute();

					Toast.makeText(getActivity(),
							"Profile Updated successfully.", Toast.LENGTH_LONG)
							.show();

				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	/**
	 * Checking device has camera hardware or not
	 * */
	private boolean isDeviceSupportCamera() {
		if (getActivity().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if the result is capturing Image
		if (requestCode == RESULT_LOAD_IMAGE
				&& resultCode == Activity.RESULT_OK && null != data) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
			propic.setImageBitmap(bitmap);
			try {
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

				File f = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "Voicey" + File.separator
						+ "profile_" + userCode + ".png");
				f.createNewFile();
				FileOutputStream fo = new FileOutputStream(f);
				fo.write(bytes.toByteArray());

				fo.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				createFolder();
				Bitmap bmp = (Bitmap) data.getExtras().get("data");

				// String filePath = "/sdcard/Voicey/profile.png";

				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(
							Environment.getExternalStorageDirectory()
									+ File.separator + "Voicey"
									+ File.separator + "profile_" + userCode
									+ ".png");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				BufferedOutputStream bos = new BufferedOutputStream(
						fileOutputStream);

				// choose another format if PNG doesn't suit you
				bmp.compress(CompressFormat.PNG, 100, bos);

				propic.setImageBitmap(bmp);

				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(getActivity(), "User cancelled image capture",
						Toast.LENGTH_SHORT).show();
			} else {
				// failed to capture image
				Toast.makeText(getActivity(), "Sorry! Failed to capture image",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void createFolder() {
		String RootDir = Environment.getExternalStorageDirectory()
				+ File.separator + "Voicey";
		File RootFile = new File(RootDir);
		RootFile.mkdir();
	}
}
