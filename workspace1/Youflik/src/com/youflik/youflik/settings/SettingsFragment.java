package com.youflik.youflik.settings;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.SessionManager;
import com.youflik.youflik.utils.Util;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SettingsFragment extends Fragment {
	private TextView mSettingsNotifications,mSettingsChangeEmail,mSettingsChangePassword,
	mPrivacyPolicy,mTerms,mBlockUsers,mAbout,mLogout;
	private Spinner mSettingsLanguage;
	private Fragment mReplaceFragment;
	private ProgressDialog pDialog;
	private static final String SETTINGS_LOGOUT_APT =Util.API+"logout";
	private AlertDialogManager alert = new AlertDialogManager();
	SessionManager session;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		session = new SessionManager(getActivity()); 
		initView(rootView);
		return rootView;
	}
	private void initView(View rootView) {
		mSettingsNotifications = (TextView) rootView.findViewById(R.id.fragment_settings_notifications);
		mSettingsChangeEmail = (TextView) rootView.findViewById(R.id.fragment_settings_change_email);
		mSettingsChangePassword = (TextView) rootView.findViewById(R.id.fragment_settings_change_password);
		mPrivacyPolicy = (TextView) rootView.findViewById(R.id.fragment_settings_privacy_policy);
		mTerms  = (TextView) rootView.findViewById(R.id.fragment_settings_terms);
		mBlockUsers = (TextView) rootView.findViewById(R.id.fragment_settings_block);
		mAbout = (TextView) rootView.findViewById(R.id.fragment_settings_about);
		mLogout = (TextView)rootView.findViewById(R.id.fragment_settings_logout);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		mPrivacyPolicy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youflik.org/#legal"));
				startActivity(browserIntent);	
			}
		});
		mAbout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youflik.org/#aboutus"));
				startActivity(browserIntent);	
			}
		});

		mTerms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youflik.org/#legal"));
				startActivity(browserIntent);	
			}
		});

		mSettingsNotifications.setOnClickListener(new OnClickListener()
		{		
			@Override
			public void onClick(View v) {
				mReplaceFragment = new SettingsNotificationsFragment();
				replaceFragment(mReplaceFragment);
			}
		});
		mSettingsChangePassword.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				mReplaceFragment = new SettingsChangePasswordFragment();
				replaceFragment(mReplaceFragment);
			}

		});
		mLogout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				ConnectionDetector conn = new ConnectionDetector(getActivity());
				if(conn.isConnectingToInternet()){
					new GCMUnregister().execute();
				}else {
					Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
				}
			}

		});

		mBlockUsers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent block = new Intent(getActivity(),SettingsBlockUserActivity.class);
				startActivity(block);
			}
		});
	}
	private void replaceFragment(Fragment fragment) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft= fm.beginTransaction();
		ft.setCustomAnimations(R.anim.right_in, R.anim.left_out);
		ft.replace(R.id.frame_container,fragment);
		ft.commit();
	}
	class GetLogoutAsync extends AsyncTask<Void,Void,JSONObject>{
		@Override
		protected void onPreExecute(){
			if(pDialog == null){
				pDialog = Util.createProgressDialog(getActivity());
				pDialog.show();
			} else {
				pDialog.show();
			}
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject receivedJsonObject = HttpGetClient.sendHttpPost(SETTINGS_LOGOUT_APT); 
			return receivedJsonObject;
		}
		@Override
		protected void onPostExecute(JSONObject result){
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result!=null){
				try {
					Toast.makeText(getActivity(), result.getString("message"),Toast.LENGTH_LONG).show();
					session.logoutUser();
					clearApplicationData();
					getActivity().finish();
				} catch (JSONException e) { e.printStackTrace();}
			}
		}

		public void clearApplicationData() {
			File cache = getActivity().getCacheDir();
			File appDir = new File(cache.getParent());
			if (appDir.exists()) {
				String[] children = appDir.list();
				for (String s : children) {
					if (!s.equals("lib")) {
						deleteDir(new File(appDir, s));
					}
				}
			}
		}

		public  boolean deleteDir(File dir) {
			if (dir != null && dir.isDirectory()) {
				String[] children = dir.list();
				for (int i = 0; i < children.length; i++) {
					boolean success = deleteDir(new File(dir, children[i]));
					if (!success) {
						return false;
					}
				}
			}

			return dir.delete();
		}
	}


	private class GCMUnregister extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONObject jsonObjSend = new JSONObject();

			try {
				jsonObjSend.put("token",Util.GCM_KEY_STORE);
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
			HttpPostClient.sendHttpPost("http://www.youflik.me:8000/unsubscribe",jsonObjSend); 
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			new GetLogoutAsync().execute();
		}

	}
}
