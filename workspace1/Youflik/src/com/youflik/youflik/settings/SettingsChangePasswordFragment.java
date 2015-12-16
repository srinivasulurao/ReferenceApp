package com.youflik.youflik.settings;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.SessionManager;
import com.youflik.youflik.utils.Util;

public class SettingsChangePasswordFragment extends Fragment implements OnFocusChangeListener {
	private EditText mEditCurrentPassword,mEditNewPassword,mEditConfirmPassword;
	private Button mSavePassword,mCancel;

	private String mCurrentPassword,mNewPassword,mConfirmPassword;
	private static String SETTINGS_CHANGE_PASSWORD_API=Util.API+"change_password";

	private ProgressDialog pDialog;
	private AlertDialogManager alert = new AlertDialogManager();
	private SessionManager session;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_settings_change_password, container, false);
		initView(rootView);
		session = new SessionManager(getActivity()); 
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		mEditCurrentPassword.setOnFocusChangeListener(this);
		mEditNewPassword.setOnFocusChangeListener(this);
		mEditConfirmPassword.setOnFocusChangeListener(this);

		mSavePassword.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				mCurrentPassword = (mEditCurrentPassword).getText().toString().trim();
				mNewPassword     = (mEditNewPassword).getText().toString().trim();
				mConfirmPassword = (mEditConfirmPassword).getText().toString().trim();
				if(mCurrentPassword.length()>5 && mNewPassword.length()>5 && mConfirmPassword.length()>5){
					ConnectionDetector conn = new ConnectionDetector(getActivity());
					if(conn.isConnectingToInternet()){
						new PostChangePasswordAsync().execute();
					}
				}else {
					Toast.makeText(getActivity(),"Password should be atleast 6 characters",Toast.LENGTH_LONG).show();
				}
			}
		});
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( keyCode == KeyEvent.KEYCODE_BACK ) {

					replaceFragment(new SettingsFragment());

					return true;
				} else {
					return false;
				}
			}
		});

	}

	private void initView(View v) {
		Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/BentonSans-Regular.otf");
		mEditCurrentPassword = (EditText)v.findViewById(R.id.fragment_settings_change_password_current);
		mEditNewPassword = (EditText)v.findViewById(R.id.fragment_settings_change_password_new);
		mEditConfirmPassword = (EditText)v.findViewById(R.id.fragment_settings_change_password_confirm);
		mSavePassword = (Button)v.findViewById(R.id.fragment_settings_change_password_save);
		//mCancel= (Button)v.findViewById(R.id.fragment_settings_change_password_cancel);

		mEditNewPassword.setTypeface(typeFace);
		mEditCurrentPassword.setTypeface(typeFace);
		mEditConfirmPassword.setTypeface(typeFace);

	}

	public void replaceFragment(Fragment fragment) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setCustomAnimations(R.anim.left_in, R.anim.right_out);
		ft.replace(R.id.frame_container, fragment);
		ft.commit();
	}


	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id =v.getId();
		int len = ((EditText) v).getText().toString().trim().length();
		if((!hasFocus) && len<6){
			((EditText) v).setError("Password Should be atleast 6 characters");
		}
	}
	class PostChangePasswordAsync extends AsyncTask<Void,Void,JSONObject>{
		String message = null,errorMessages=null;	
		@Override
		protected void onPreExecute(){
			if(pDialog==null){
				pDialog = Util.createProgressDialog(getActivity());
				pDialog.show();
			} else { pDialog.show(); }
		}
		@Override
		protected JSONObject doInBackground(Void... params) {

			JSONObject jsonObjSend = new JSONObject();
			JSONObject receivedJsonObject = null;
			try {
				jsonObjSend.put("old_password",mCurrentPassword);
				jsonObjSend.put("new_password",mNewPassword);
				jsonObjSend.put("conf_password",mConfirmPassword);
				receivedJsonObject = HttpPostClient.sendHttpPost(SETTINGS_CHANGE_PASSWORD_API, jsonObjSend);
			}
			catch (JSONException e) { e.printStackTrace();}
			return receivedJsonObject;
		}
		@Override
		protected void onPostExecute(JSONObject result){
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result!=null){
				try{
					message = result.getString("message");
					if(result.has("errorMessages")){
						errorMessages = result.getString("errorMessages");
						Toast.makeText(getActivity(), errorMessages, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
					}
					if(result.getString("status").equalsIgnoreCase("1")){
						session.changePassword(mNewPassword);
						replaceFragment(new SettingsFragment());
					}
				}catch(JSONException e){ e.printStackTrace();}
			}
		}
	}
}