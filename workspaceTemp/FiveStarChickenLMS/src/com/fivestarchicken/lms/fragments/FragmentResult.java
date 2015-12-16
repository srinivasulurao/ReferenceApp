package com.fivestarchicken.lms.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.utils.Commons;

public class FragmentResult extends Fragment {

	private TextView resultFail, name, date, questionsAnswered, score,tvStatus,tvPercentage,tvPassingScore,
			tvResultPass,tvVideoCall;
	private DbAdapter dh;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result, container, false);
		this.dh = new DbAdapter(getActivity());
		initilizeUI(v);

		return v;
	}

	private void initilizeUI(View v) {

		try {

			name = (TextView) v.findViewById(R.id.exam_candidate_name);
			date = (TextView) v.findViewById(R.id.exam_date);
			questionsAnswered = (TextView) v
					.findViewById(R.id.exam_questions_answered);
			score = (TextView) v.findViewById(R.id.exam_passing_score);
		/*	resultPass = (TextView) v.findViewById(R.id.exam_pass);
			resultFail = (TextView) v.findViewById(R.id.exam_fail);*/
			tvVideoCall=(TextView)v.findViewById(R.id.tvvideocall);
			tvStatus=(TextView)v.findViewById(R.id.tvstatus);
			tvPercentage=(TextView)v.findViewById(R.id.tvpercentage);

			Bundle bundle = this.getArguments();

			String result = bundle.getString("EXAM_RESULT");
			String Date = bundle.getString("EXAM_DATE");
			String answers = bundle.getString("QUESTIONED_ANSWERED");
			String Score = bundle.getString("EXAM_SCORE");
			String Name = bundle.getString("CANDIDATE_NAME");
			String passMark = bundle.getString("PASS_MARK");
			String grade = bundle.getString("GRADE");

			name.setText( Name);
			date.setText( Date);
			questionsAnswered.setText( answers);
			score.setText(passMark+" %");
			tvPercentage.setText(Score+" %");
			
			if (grade.equals("1")) {
				tvStatus.setText("Pass");	
				tvStatus.setBackgroundColor(getResources().getColor(R.color.interviewpass));
				tvVideoCall.setVisibility(View.GONE);
			} else if(grade.equals("2")) {
				tvStatus.setText("Pass");	
				tvVideoCall.setVisibility(View.VISIBLE);
				tvStatus.setBackgroundColor(getResources().getColor(R.color.interviewavg));
			}else{
				
				tvStatus.setText("Fail");
				tvStatus.setBackgroundColor(getResources().getColor(R.color.interviewfail));
				tvVideoCall.setVisibility(View.GONE);
			}
			
			tvVideoCall.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					String SKYPENAME = dh.getConfigureValue(Commons.skype_name);

					if (SKYPENAME != null) {

					
					String mySkypeUri = "skype:" + SKYPENAME
							+ "?call&video=true";
					SkypeUri(getActivity(), mySkypeUri);
					}
					// TODO Auto-generated method stub
					
				}
			});

		} catch (Exception e) {

			e.printStackTrace();
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
}
