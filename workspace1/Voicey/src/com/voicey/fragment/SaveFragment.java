package com.voicey.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.voicey.activity.R;
import com.voicey.utils.Constants;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SaveFragment extends Fragment implements OnClickListener {

	EditText etTitle,etMood;
	TextView tvsave,tvcancel;
	LinearLayout llPublic;
	LinearLayout llUser;
	ImageView ivMaximize,ivMinimize;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_save, container, false);

		initilizeUI(v);

		return v;
	}

	private void initilizeUI(View v) {

		try {

			etTitle = (EditText) v.findViewById(R.id.ettitle);
			etMood=(EditText) v.findViewById(R.id.etmood);
			llPublic=(LinearLayout) v.findViewById(R.id.llpublic);
			llUser=(LinearLayout) v.findViewById(R.id.lluser);
			ivMaximize=(ImageView)v.findViewById(R.id.ivmaximize);
			tvsave = (TextView) v.findViewById(R.id.tvsave);
			tvcancel= (TextView) v.findViewById(R.id.tvcancel);
			ivMinimize=(ImageView)v.findViewById(R.id.ivminimize);
			
			etMood.setVisibility(View.GONE);
			llPublic.setVisibility(View.GONE);
			llUser.setVisibility(View.GONE);
			ivMinimize.setVisibility(View.GONE);
			llUser.setVisibility(View.GONE);
			
			ivMinimize.setOnClickListener(this);
			ivMaximize.setOnClickListener(this);
			tvsave.setOnClickListener(this);
			tvcancel.setOnClickListener(this);
			
			

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.tvsave:
			save();
			break;
			
		case R.id.ivmaximize:
			etMood.setVisibility(View.VISIBLE);
			llPublic.setVisibility(View.VISIBLE);
			llUser.setVisibility(View.VISIBLE);
			ivMinimize.setVisibility(View.VISIBLE);
			ivMaximize.setVisibility(View.GONE);
			break;
			
		case R.id.ivminimize:
			etMood.setVisibility(View.GONE);
			llPublic.setVisibility(View.GONE);
			llUser.setVisibility(View.GONE);
			ivMinimize.setVisibility(View.GONE);
			ivMaximize.setVisibility(View.VISIBLE);
			break;

		case R.id.tvcancel:
			Fragment fragment = new RecordFragment();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			break;
			
			

		}

	}

	public void save() {
		try {
			File f = new File(Constants.temp_url);

			if (f.exists()) {
				String title = ((TextView) etTitle).getText().toString();
				InputStream in = new FileInputStream(Constants.temp_url);
				OutputStream out = new FileOutputStream(
						Environment.getExternalStorageDirectory() + "/"
								+ Constants.app_folder + "/" + title + ".3gp");

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();

				f.delete();

				Fragment fragment = new RecordFragment();
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
