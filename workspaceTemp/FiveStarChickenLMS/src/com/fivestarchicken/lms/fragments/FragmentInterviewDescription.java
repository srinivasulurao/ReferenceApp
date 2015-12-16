package com.fivestarchicken.lms.fragments;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.TextView;

import com.fivestarchicken.lms.ActivityExam;
import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.model.ExamModule;
import com.fivestarchicken.lms.utils.Commons;

public class FragmentInterviewDescription extends Fragment  {
	
	ExamModule examModule;
	private DbAdapter dh;
	TextView txStartExam;
	TextView tvDescription;
	String languageType;
	SharedPreferences sharedPreferences;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_interview_description, container, false);
		
		txStartExam=(TextView)v.findViewById(R.id.tvstartexam);
		tvDescription=(TextView)v.findViewById(R.id.tvdescription);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		languageType = sharedPreferences.getString("intlanguageType", null);
		
		this.dh = new DbAdapter(getActivity());
		examModule=dh.getModuleDetail(Commons.INTERVIEW_MODULEID,languageType);
		tvDescription.setText(examModule.getDescription());
		
		txStartExam.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Fragment fragment = new FragmentInterviewExam();
				
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.fl_container, fragment).commit();
				
			}
		});
		
		return v;
		
	}
	
	

}
