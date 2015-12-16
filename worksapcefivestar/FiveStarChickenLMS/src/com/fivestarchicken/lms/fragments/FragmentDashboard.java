package com.fivestarchicken.lms.fragments;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fivestarchicken.lms.ActivityCarrierGraph;
import com.fivestarchicken.lms.ActivityEmployeeLogin;
import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.utils.Commons;
import com.fivestarchicken.lms.webservice.Webservice;

@SuppressLint("NewApi")
public class FragmentDashboard extends Fragment {

	DashboardSelectListener mListener;
	RelativeLayout rrprofile, rrtakeexample, rrviewresult, rrblog, rrcontacts,
			rrcerticate;
	ImageView ivNew;
	private DbAdapter dh;
	Webservice Webservices;
	String userId;
	SharedPreferences sharedPreferences;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_dashboard, container, false);

		this.dh = new DbAdapter(getActivity());
		Webservices = new Webservice();
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		userId = sharedPreferences.getString("userId", null);

		initilizeUI(v);

		return v;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {

			mListener = (DashboardSelectListener) activity;
			;

		} catch (ClassCastException e) {

		}
	}

	private void initilizeUI(View v) {

		try {
			rrprofile = (RelativeLayout) v.findViewById(R.id.rrprofile);
			rrtakeexample = (RelativeLayout) v.findViewById(R.id.rrtakeexample);
			rrviewresult = (RelativeLayout) v.findViewById(R.id.rrviewresult);
			rrblog = (RelativeLayout) v.findViewById(R.id.rrblog);
			rrcontacts = (RelativeLayout) v.findViewById(R.id.rrcontacts);
			rrcerticate = (RelativeLayout) v.findViewById(R.id.rrcertificate);
			ivNew = (ImageView) v.findViewById(R.id.ivnew);

			rrtakeexample.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (mListener != null) {

						mListener.onDashBoardSelect(Commons.TAKE_EXAM);
					}

					Intent i = new Intent(getActivity(),
							ActivityCarrierGraph.class);// ActivityExamModule

					startActivity(i);

					/*
					 * Fragment fragment = new FragmentTakeExam();
					 * FragmentManager fragmentManager = getFragmentManager();
					 * fragmentManager.beginTransaction()
					 * .replace(R.id.frame_container, fragment).commit();
					 */

				}
			});
			rrprofile.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (mListener != null) {

						mListener.onDashBoardSelect(Commons.PROFILE);
					}

					Fragment fragment = new FragmentProfile();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();

				}
			});
			rrviewresult.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (mListener != null) {

						mListener.onDashBoardSelect(Commons.VIEW_RESULT);
					}

					Fragment fragment = new FragmentViewResult();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();

				}
			});

			rrblog.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mListener != null) {

						mListener.onDashBoardSelect(Commons.DEFAULT_SELECT);
					}

					Fragment fragment = new FragmentBlog();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();

				}
			});

			rrcontacts.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mListener != null) {

						mListener.onDashBoardSelect(Commons.DEFAULT_SELECT);
					}

					displayContactSelect();

					/*
					 * String SKYPENAME =
					 * dh.getConfigureValue(Commons.skype_name);
					 * 
					 * if (SKYPENAME != null) {
					 * 
					 * String mySkypeUri = "skype:" + SKYPENAME +
					 * "?call&video=true"; SkypeUri(getActivity(), mySkypeUri);
					 * }
					 */
				}

			});

			rrcerticate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mListener != null) {

						mListener.onDashBoardSelect(Commons.DEFAULT_SELECT);
					}
					/*
					 * File file=new File(Commons.app_pdf_folder+"/pdf.pdf");
					 * Uri path = Uri.fromFile(file); Intent intent = new
					 * Intent(Intent.ACTION_VIEW); intent.setDataAndType(path,
					 * "application/pdf");
					 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 * startActivity(intent);
					 */

					Fragment fragment = new FragmentCertificate();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();

				}
			});

			checkNewBlog();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	void checkNewBlog() {

		try {

			String todate, fromdate;
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			Date date = new Date();
			todate = dateFormat.format(date);
			Date dateBefore = new Date(date.getTime() - 3 * 24 * 3600 * 1000);
			fromdate = dateFormat.format(dateBefore);

			Boolean isNewBlog = dh.isNewBlog(fromdate, todate);

			if (isNewBlog) {
				ivNew.setVisibility(View.VISIBLE);
				final Animation animation = new AlphaAnimation(1, 0);
				animation.setDuration(1000);
				animation.setInterpolator(new LinearInterpolator());
				animation.setRepeatCount(Animation.INFINITE);
				animation.setRepeatMode(Animation.REVERSE);
				ivNew.startAnimation(animation);
			} else {

				ivNew.setVisibility(View.GONE);
			}
		} catch (Exception e) {

		}
	}

	void displayContactSelect() {

		TextView tvVideoCall, tvSendMessage, tvContactInfo;
		final Dialog dialog = new Dialog(getActivity());
		// hide to default title for Dialog
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// inflate the layout dialog_layout.xml and set it as contentView
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_contact_select, null,
				false);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		dialog.show();
		tvVideoCall = (TextView) view.findViewById(R.id.tvvideocall);
		tvSendMessage = (TextView) view.findViewById(R.id.tvsendmessage);
		tvContactInfo = (TextView) view.findViewById(R.id.tvcontactinfo);

		tvVideoCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String SKYPENAME = dh.getConfigureValue(Commons.skype_name);

				if (SKYPENAME != null) {

					String mySkypeUri = "skype:" + SKYPENAME
							+ "?call&video=true";
					SkypeUri(getActivity(), mySkypeUri);
					dialog.cancel();
				}

			}
		});

		tvSendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				dialogSendMessage();

			}
		});

		tvContactInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogContactInfo();
				dialog.cancel();

			}
		});

	}

	void dialogSendMessage() {

		Button btSend;
		final EditText etMessage;
		ImageView ivClose;

		final Dialog dialog = new Dialog(getActivity());
		// hide to default title for Dialog
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// inflate the layout dialog_layout.xml and set it as contentView
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_sendmessage, null, false);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		dialog.show();
		etMessage = (EditText) view.findViewById(R.id.etmessage);
		ivClose = (ImageView) view.findViewById(R.id.ivclose);
		btSend = (Button) view.findViewById(R.id.employee_login);
		btSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String message = ((TextView) etMessage).getText().toString();

				if (message == null || message.length() == 0) {
					Toast.makeText(getActivity(), "Please enter message",
							Toast.LENGTH_SHORT).show();

				} else {
					
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);

					dialog.cancel();
					if (Commons.isNetworkAvailable(getActivity())) {
						new AsySendMessage().execute(URLEncoder.encode(message
								.toString()));

					}
				}

			}
		});

		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
				dialog.cancel();

			}
		});

	}

	void dialogContactInfo() {
		TextView tvEmail, tvPhone;
		ImageView ivClose;
		final Dialog dialog = new Dialog(getActivity());
		// hide to default title for Dialog
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// inflate the layout dialog_layout.xml and set it as contentView
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_contactinfo, null, false);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		dialog.show();
		ivClose = (ImageView) view.findViewById(R.id.ivclose);
		tvEmail = (TextView) view.findViewById(R.id.tvemail);
		tvPhone = (TextView) view.findViewById(R.id.tvphone);
		String adminEmail = dh.getConfigureValue(Commons.admin_email);
		String adminPhone = dh.getConfigureValue(Commons.admin_phone);
		tvEmail.setText(adminEmail);
		tvPhone.setText(adminPhone);
		
		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();

			}
		});

	}

	private class AsySendMessage extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(getActivity());
			progDailog.setMessage("Sending...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(String... message) {
			// regjson = userFunction.login(email, passward, typeValue);

			return Webservices.sendMessage(userId, message[0]);
		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();
				if (result != null && result.length() > 0) {

					JSONObject joresult = new JSONObject(result);

					String status = joresult.getString("status");

					// syncTime=joresult.getString("sync_time");

					if (status.equals("200")) {

					} else {

						String errormessage = joresult.getString("message");

						Toast.makeText(getActivity(), errormessage,
								Toast.LENGTH_LONG).show();

					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void SkypeUri(Context myContext, String mySkypeUri) {

		// Make sure the Skype for Android client is installed.
		if (!Commons.isSkypeClientInstalled(myContext)) {
			Commons.goToSkypeMarket(myContext);
			return;
		}
		Uri skypeUri = Uri.parse(mySkypeUri);
		Intent myIntent = new Intent(Intent.ACTION_VIEW, skypeUri);
		myIntent.setComponent(new ComponentName("com.skype.raider",
				"com.skype.raider.Main"));
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		myContext.startActivity(myIntent);

		return;
	}

	public static interface DashboardSelectListener {
		void onDashBoardSelect(String type);
	}

}
