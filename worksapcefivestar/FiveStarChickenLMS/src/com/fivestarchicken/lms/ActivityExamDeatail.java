package com.fivestarchicken.lms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.model.ExamModule;
import com.fivestarchicken.lms.utils.Commons;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityExamDeatail extends ActionBarActivity {

	String description, userId;
	TextView tvModuleName, tvDuration, tvTotalQuestion, tvPassingScore;
	ImageView ivvideo;
	private ActionBar actionbar;
	private DbAdapter dh;
	TextView tvsatrtexam, tvtutorial;
	String examModuleStr;
	ExamModule examModule;
	Gson gson;
	SharedPreferences sharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_examp_detail);
		gson = new Gson();
		Bundle b = getIntent().getExtras();
		examModuleStr = b.getString("examModule");
		examModule = gson.fromJson(examModuleStr, ExamModule.class);
		this.dh = new DbAdapter(ActivityExamDeatail.this);
		actionbar = getSupportActionBar();

		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
		actionbar.setCustomView(mCustomView);
		actionbar.setDisplayShowCustomEnabled(true);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityExamDeatail.this);
		userId = sharedPreferences.getString("userId", null);
		tvsatrtexam = (TextView) findViewById(R.id.tvsatrtexam);
		tvtutorial = (TextView) findViewById(R.id.tvtutorial);

		tvModuleName = (TextView) findViewById(R.id.tvmodulename);
		tvDuration = (TextView) findViewById(R.id.tvduration);
		tvTotalQuestion = (TextView) findViewById(R.id.tvtotalquestion);
		tvPassingScore = (TextView) findViewById(R.id.tvpassingscore);

		tvModuleName.setText(examModule.getModuleName());
		tvDuration.setText(examModule.getDuration());
		tvTotalQuestion.setText(examModule.getTotalQuestions());
		tvPassingScore.setText(examModule.getPassingScore() + "%");

		tvsatrtexam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Commons.isNetworkAvailable(ActivityExamDeatail.this)) {
					if (validateExam()) {
						Intent i = new Intent();
						i.setClass(ActivityExamDeatail.this, ActivityExam.class);
						i.putExtra("examModule", examModuleStr);
						startActivity(i);
					}
				} else {

					Commons.internetErrorMessage(ActivityExamDeatail.this);
				}

			}
		});
		tvtutorial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent();
				i.setClass(ActivityExamDeatail.this,
						ActivityModuleDetails.class);
				i.putExtra("examModule", examModuleStr);
				startActivity(i);

			}
		});

	}

	Boolean validateExam() {
		Boolean isValidExam = true;

		if (dh.isModulePassed(examModule.getModuleId(), userId)) {

			Toast.makeText(ActivityExamDeatail.this,
					"Already passed this module", Toast.LENGTH_LONG).show();
			isValidExam = false;

		} else if (!validateFailExam()) {

			

			isValidExam = false;

		} else if (!validateExamLevel()) {

			/*Toast.makeText(ActivityExamDeatail.this, "min 3 month",
					Toast.LENGTH_LONG).show();
*/
			isValidExam = false;

		}

		return isValidExam;
	}

	Boolean validateExamLevel() {
		Boolean isValidExam = true;
		try {

			Integer countStar = new Integer(examModule.getStarLevel());

			if (countStar > 1) {

				String levelStartTime = dh.getLevelStartTime(
						String.valueOf(countStar - 1), userId);

				if (levelStartTime != "0000") {

					Calendar c = Calendar.getInstance();
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					//String formattedDate = df.format(c.getTime());
					Date date1 = df.parse(df.format(c.getTime()));
					Date date2 = df.parse(levelStartTime);
					Long diff = date2.getTime() - date1.getTime();

					//Integer datediff = diff.intValue();
					String minFailLimit = dh
							.getConfigureValue(Commons.STAR_PASS_LIMT);

					Integer minLimit = Integer.parseInt(minFailLimit);
					Long diffDays=	TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

					if (minLimit > diffDays) {
						
						Toast.makeText(ActivityExamDeatail.this, "Please take this exam after "+(minLimit-diffDays)+" days",
								Toast.LENGTH_LONG).show();

						isValidExam = false;
					} else {

						isValidExam = true;
					}
				}
			}

		} catch (Exception e) {

			return false;
		}
		return isValidExam;
	}

	Boolean validateFailExam() {
		Boolean isValidExam = true;
		try {
			String lastFailtime = dh.getLastFailTime(examModule.getModuleId(),
					userId);
			if (lastFailtime != "0000") {
				Calendar c = Calendar.getInstance();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				//String formattedDate = df.format(c.getTime());
				Date date1 = df.parse(df.format(c.getTime()));
				Date date2 = df.parse(lastFailtime);
				Long diff =   date1.getTime()-date2.getTime();

				//Integer datediff = diff.intValue();
				String minFailLimit = dh
						.getConfigureValue(Commons.MODULE_FAIL_LIMIT);

				Integer minLimit = Integer.parseInt(minFailLimit);
				Long diffDays=	TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

				if (minLimit > diffDays) {
					
					
					
					Toast.makeText(ActivityExamDeatail.this, "Please take this exam after "+(minLimit-diffDays)+" days",
							Toast.LENGTH_LONG).show();

					isValidExam = false;
				} else {

					isValidExam = true;
				}
			}

			// System.out.println ("Days: " + TimeUnit.DAYS.convert(diff,
			// TimeUnit.MILLISECONDS));
		} catch (Exception e) {

			return false;
		}
		return isValidExam;
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
