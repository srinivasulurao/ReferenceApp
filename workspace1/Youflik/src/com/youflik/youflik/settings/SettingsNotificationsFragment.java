/**
 * 
 */
package com.youflik.youflik.settings;

import java.util.ArrayList;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.youflik.youflik.R;
import com.youflik.youflik.models.SettingsNotificationsModel;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

/**
 * @author Local Account
 * 
 */
public class SettingsNotificationsFragment extends Fragment implements OnClickListener {
	private TextView mVibration,mRingtone;
	private ToggleButton mTogglevibration,mToggleRingtone;

	private ProgressDialog pDialog;
    //private String 

	private ListView settings_notifications_list;
	private String mSettingsId;
	private static final String SETTINGS_NOTIFICATIONS_API = Util.API+ "settings";
	private ArrayList<SettingsNotificationsModel> notification_model_array;
	private SettingsNotificationsAdapter adapter;
	private AlertDialogManager alert = new AlertDialogManager();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings_notifications, container, false);
		mVibration = (TextView)rootView.findViewById(R.id.fragment_settings_notifications_vibration_text);
		mRingtone =  (TextView)rootView.findViewById(R.id.fragment_settings_notifications_ringtone_text);
		mTogglevibration = (ToggleButton) rootView.findViewById(R.id.fragment_settings_notifications_vibration_toggle);
		mToggleRingtone = (ToggleButton) rootView.findViewById(R.id.fragment_settings_notifications_ringtone_toggle);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {
		        @Override
		        public boolean onKey(View v, int keyCode, KeyEvent event) {
		            if( keyCode == KeyEvent.KEYCODE_BACK ) {  	
		            	FragmentManager fm = getActivity().getSupportFragmentManager();
		        		FragmentTransaction ft = fm.beginTransaction();
		        		ft.setCustomAnimations(R.anim.left_in, R.anim.right_out);
		        		ft.replace(R.id.frame_container, new SettingsFragment());
		        		ft.commit();
		                return true;
		            } else {
		                return false;
		            }
		        }
		    });
	}

	@Override
	public void onClick(View v) {
	}
}
