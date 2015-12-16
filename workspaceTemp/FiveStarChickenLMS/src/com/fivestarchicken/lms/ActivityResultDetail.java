package com.fivestarchicken.lms;

import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.model.ExamModule;
import com.fivestarchicken.lms.model.Result;
import com.fivestarchicken.lms.utils.Commons;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityResultDetail extends ActionBarActivity {

	private ActionBar actionbar;
	String resultId,languageType;
	private DbAdapter dh;
	Result result;
	ImageView ivHome;
	ExamModule examModule;
	SharedPreferences sharedPreferences;
	TextView tvModuleName, tvTotalQuestion, tvPassingScore, tvExamDate,
			tvStatus, tvPercentage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_detail);
		actionbar = getSupportActionBar();

		/*actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);*/
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		Bundle b = getIntent().getExtras();
		resultId = b.getString("resultId");
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityResultDetail.this);
	    languageType = sharedPreferences.getString("languageType", null);
		View mCustomView = mInflater.inflate(R.layout.actionbar_result, null);
		ivHome=(ImageView)mCustomView.findViewById(R.id.ivhome);
		
		ivHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent();
				i.setClass(ActivityResultDetail.this, ActivityHome.class);
				startActivity(i);
				
			}
		});
		actionbar.setCustomView(mCustomView);
		actionbar.setDisplayShowCustomEnabled(true);

		this.dh = new DbAdapter(ActivityResultDetail.this);
		result = dh.getResultDetail(resultId);

		examModule = dh.getModuleDetail(result.getModuleId(),languageType);
		
		if(examModule==null){
			examModule = dh.getModuleDetail(result.getModuleId(),Commons.DEFAULT_LANGUAGE_TYPE);
			
		}

		tvModuleName = (TextView) findViewById(R.id.tvmodulename);
		tvTotalQuestion = (TextView) findViewById(R.id.tvtotalquestion);
		tvPassingScore = (TextView) findViewById(R.id.tvpassingscore);
		tvExamDate = (TextView) findViewById(R.id.tvexamdate);
		tvStatus = (TextView) findViewById(R.id.tvstatus);
		tvPercentage = (TextView) findViewById(R.id.tvpercentage);
		tvModuleName.setText(examModule.getModuleName());
		tvTotalQuestion.setText(examModule.getTotalQuestions());
		tvPassingScore.setText(examModule.getPassingScore() + " %");
		tvExamDate.setText(result.getExamDate());
		tvStatus.setText(result.getStatus());
		if (result.getPercentage() != null
				&& !result.getPercentage().equals("null")
				&& result.getPercentage().length() > 0) {
			tvPercentage.setText(result.getPercentage() + " %");
		} else {
			tvPercentage.setText("-");

		}

		if (result.getStatus().equals(Commons.statusFail)) {
			// tvStatus.setb
			tvStatus.setBackgroundColor(getResources().getColor(
					R.color.failbackground));

		} else if (result.getStatus().equals(Commons.statusPass)) {

			tvStatus.setBackgroundColor(getResources().getColor(R.color.interviewpass));

		} else if (result.getStatus().equals(Commons.statusProcessing)) {

			tvStatus.setBackgroundColor(getResources().getColor(
					R.color.processingbackground));
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Previous page or exit
			finish();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
