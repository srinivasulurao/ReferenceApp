package com.fivestarchicken.lms.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.fivestarchicken.lms.ActivityExam;
import com.fivestarchicken.lms.ActivityResultDetail;
import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.adapter.AdapterViewResult;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.model.ExamModule;
import com.fivestarchicken.lms.model.Result;

public class FragmentViewResult  extends Fragment {
	
	ListView lvViewResult;
	AdapterViewResult adapterViewResult;
	List<Result> viewResultList = new ArrayList<Result>();
	private DbAdapter dh;
	Result result;
	String userId,languageType;;
	SharedPreferences sharedPreferences;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_view_result, container, false);

		initilizeUI(v);

		return v;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		}
	}
	
	private void initilizeUI(View v) {

		try {
			
			 lvViewResult=(ListView)v.findViewById(R.id.lvviewresult);
			 
			 this.dh = new DbAdapter(getActivity());
			 
			 sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				userId = sharedPreferences.getString("userId", null);
				
				  languageType = sharedPreferences.getString("languageType", null);
			 viewResultList=dh.getResult(userId,languageType);
			 
			
			 
			 adapterViewResult = new AdapterViewResult(getActivity(),
						R.layout.adapter_view_result, viewResultList);

			    lvViewResult.setAdapter(adapterViewResult);
			    
			    
			    lvViewResult.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent,
							View view, int position, long id) {
						
						result=(Result) parent.getItemAtPosition(position);	
						Intent i=new Intent();
						i.setClass(getActivity(), ActivityResultDetail.class);
						i.putExtra("resultId", result.getResultId());
						startActivity(i);
						
						
						
						
					}
			    });
			    
			
		}	
		 catch (Exception e) {

			e.printStackTrace();
		}
	}

}
