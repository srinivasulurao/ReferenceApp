package com.fivestarchicken.lms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.libs.CustomViewPager;
import com.fivestarchicken.lms.model.Answer;
import com.fivestarchicken.lms.model.Certificate;
import com.fivestarchicken.lms.model.Exam;
import com.fivestarchicken.lms.model.ExamModule;
import com.fivestarchicken.lms.model.ModuleDescription;
import com.fivestarchicken.lms.model.Questions;
import com.fivestarchicken.lms.model.Result;
import com.fivestarchicken.lms.utils.Commons;
import com.fivestarchicken.lms.webservice.Webservice;
import com.google.gson.Gson;

public class ActivityExam extends ActionBarActivity {
	private ActionBar actionbar;
	CustomViewPager viewPager;
	static ArrayList<Questions> questionsList;
	public HashMap<String, String> mapAnswerSelect = new HashMap<String, String>();
	private DbAdapter dh;
	Button button2, button1;
	String examModuleStr, userId;
	SharedPreferences sharedPreferences;
	Result result;
	boolean blink;
	TextView tvtimmer;

	String selectedAnswer;
	Webservice Webservices = new Webservice();
	CountDownTimer countDownTimer;
	Gson gson;
	ExamModule examModule;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exam);
		try {

			actionbar = getSupportActionBar();
			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setHomeButtonEnabled(true);
			actionbar.setDisplayShowHomeEnabled(false);
			actionbar.setDisplayShowTitleEnabled(false);
			LayoutInflater mInflater = LayoutInflater.from(this);
			View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
			actionbar.setCustomView(mCustomView);
			actionbar.setDisplayShowCustomEnabled(true);

			gson = new Gson();
			Bundle b = getIntent().getExtras();
			examModuleStr = b.getString("examModule");
			examModule = gson.fromJson(examModuleStr, ExamModule.class);

			this.dh = new DbAdapter(ActivityExam.this);
			questionsList = dh.getQuestionList(examModule.getModuleId(),
					examModule.getLanguageType());
			// exam = dh.getExam(examModule.getExamId());
			viewPager = (CustomViewPager) findViewById(R.id.pager);
			viewPager.setAdapter(new AdapterFragmentExam());

			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(ActivityExam.this);
			userId = sharedPreferences.getString("userId", null);

			button1 = (Button) findViewById(R.id.button1);
			tvtimmer = (TextView) findViewById(R.id.TextView01);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			String inputString = examModule.getDuration();

			Date date = sdf.parse("1970-01-01 " + inputString);

			ShowCountTimer(ActivityExam.this, tvtimmer, date.getTime(),
					10 * 1000);

			button1.setText("Next");
			button2 = (Button) findViewById(R.id.button2);
			button2.setText("Previous");
			button1.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					int index = viewPager.getCurrentItem();

					index++;
					if (index == 1) {
						button2.setVisibility(View.VISIBLE);

					}

					else if (index == questionsList.size() - 1) {
						button1.setText("Finish");

					} else if (index == questionsList.size()) {

						if (countDownTimer != null) {
							countDownTimer.cancel();
							countDownTimer = null;
						}

						dialogConfirm();
					}

					viewPager.setCurrentItem(index, true);

				}
			});

			button2.setVisibility(View.GONE);
			button2.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					int index = viewPager.getCurrentItem();

					index--;

					if (index < 1) {
						button2.setVisibility(View.GONE);

					}

					if (index < questionsList.size()) {
						button1.setText("Next");

					}
					viewPager.setCurrentItem(index, true);

				}
			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public void dialogConfirm() {

		Button btContinue;
		TextView tvmessage;
		LayoutInflater li = LayoutInflater.from(ActivityExam.this);
		View promptsView = li.inflate(R.layout.dialog_confirm, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ActivityExam.this);
		alertDialogBuilder.setCancelable(false);

		alertDialogBuilder.setMessage("Press continue to see result");
		alertDialogBuilder.setPositiveButton("Continue",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						saveResult();

					}
				});
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		// StringBuffer answareSelect = new StringBuffer();

	}

	public void saveResult() {

		StringBuffer answareSelect = new StringBuffer();

		for (Map.Entry<String, String> entry : mapAnswerSelect.entrySet()) {
			answareSelect.append(entry.getKey() + "-" + entry.getValue() + ",");
		}
		selectedAnswer = answareSelect.toString();

		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = df.format(c.getTime());

		/*
		 * result = new Result(); result.setAnswerSelection(selectedAnswer);
		 * result.setModuleId(examModule.getModuleId());
		 * result.setModuleName(examModule.getModuleName());
		 * result.setStatus(Commons.statusProcessing);
		 * //result.setExamId(examModule.getExamId());
		 * result.setExamDate(formattedDate); //
		 * result.setExamName(exam.getTitle());
		 */// Long resultId = dh.insertResult(result);

		if (Commons.isNetworkAvailable(ActivityExam.this)) {

			new AsySendExamDetail().execute();

		} else {
			/*
			 * Intent i = new Intent(); i.setClass(ActivityExam.this,
			 * ActivityResultDetail.class); i.putExtra("resultId",
			 * resultId.toString()); startActivity(i); finish();
			 */
		}

	}

	public void ShowCountTimer(final Context context, final TextView textviews,
			long totalTimeCountInMilliseconds,
			final long timeBlinkInMilliseconds) {

		blink = false;

		countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 500) {

			@Override
			public void onTick(long leftTimeInMilliseconds) {
				long seconds = leftTimeInMilliseconds / 1000;

				if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {
					// timeramaining.setTextAppearance(getApplicationContext(),
					// R.style.blinkText);
					// change the style of the textview .. giving a red alert
					// style

					if (blink) {
						textviews.setTextColor(Color.RED);
						// if blink is true, textview will be visible
					} else {
						textviews.setTextColor(Color.parseColor("#000000"));
					}

					blink = !blink; // toggle the value of blink
				}

				textviews
						.setText("Time Remaining : "
								+ String.format(
										"%02d:%02d:%02d",
										TimeUnit.MILLISECONDS
												.toHours(leftTimeInMilliseconds),
										TimeUnit.MILLISECONDS
												.toMinutes(leftTimeInMilliseconds)
												- TimeUnit.HOURS
														.toMinutes(TimeUnit.MILLISECONDS
																.toHours(leftTimeInMilliseconds)), // The
																									// change
																									// is
																									// in
																									// this
																									// line
										TimeUnit.MILLISECONDS
												.toSeconds(leftTimeInMilliseconds)
												- TimeUnit.MINUTES
														.toSeconds(TimeUnit.MILLISECONDS
																.toMinutes(leftTimeInMilliseconds))));

				// textviews.setText(String.format("%02d", seconds %
				// 100)+" Seconds remaining");
				// format the textview to show the easily readable format
			}

			@SuppressWarnings("deprecation")
			@Override
			public void onFinish() {
				dialogConfirm();

			}

		}.start();
	}

	private class AsySendExamDetail extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(ActivityExam.this);
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(false);
			progDailog.show();
		}

		protected String doInBackground(String... unused) {
			// regjson = userFunction.login(email, passward, typeValue);

			return Webservices.sendExamDetail(userId, examModule.getModuleId(),
					selectedAnswer, examModule.getLanguageType());
		}

		protected void onPostExecute(String resultStr) {
			try {
				progDailog.dismiss();
				result = new Result();
				String isLevelComplete, starValue;
				Certificate certificate;

				if (resultStr != null && resultStr.length() > 0) {

					JSONObject joresult = new JSONObject(resultStr);

					String status = joresult.getString("status");

					if (status.equals("200")) {
						JSONObject resultjson = joresult
								.getJSONObject("result");

						result.setModuleId(resultjson.getString("module_id"));
						result.setModuleName(resultjson
								.getString("module_name"));
						result.setPercentage(resultjson
								.getString("result_percent"));
						result.setAnswerSelection(resultjson
								.getString("question_answered"));
						result.setResultId(resultjson.getString("result_id"));
						result.setExamDate(resultjson
								.getString("exam_taken_date"));
						result.setUserId(resultjson.getString("user_id"));

						result.setStatus(resultjson.getString("module_passed"));

						starValue = resultjson.getString("module_star_value");
						result.setStarLevel(starValue);

						Long resultId = dh.insertResult(result);

						isLevelComplete = resultjson
								.getString("star_completed");

						if (isLevelComplete.equals("1")) {

							dh.updateStar(starValue, result.getUserId());

							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putString("starLevel", starValue);

							editor.commit();

							certificate = new Certificate();
							certificate.setStarValue(starValue);
							certificate.setFileName(resultjson
									.getString("certificatename"));
							certificate.setUserId(result.getUserId());

							dh.insertCertificate(certificate);

						}

						Intent i = new Intent();
						i.setClass(ActivityExam.this,
								ActivityResultDetail.class);
						i.putExtra("resultId", resultId.toString());
						startActivity(i);
						finish();

					} else {
						String errormessage = joresult.getString("message");

						Toast.makeText(ActivityExam.this, errormessage,
								Toast.LENGTH_LONG).show();

					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public class AdapterFragmentExam extends FragmentPagerAdapter {
		final int PAGE_COUNT = questionsList.size();

		public AdapterFragmentExam() {
			super(getSupportFragmentManager());
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}

		@Override
		public Fragment getItem(int position) {

			return FragmentExam.create(position, ActivityExam.this);
		}

	}

	public static class FragmentExam extends Fragment {
		public static final String ARG_PAGE = "ARG_PAGE",
				QUESTION = "QUESTION";

		private int mPage;
		// public HashMap<String, String> mapAnswerSelect = new HashMap<String,
		// String>();
		RadioButton rbAnsware1, rbAnsware2, rbAnsware3;
		static Context activityContext;
		Questions questions;
		Answer answer;
		int pos;

		public static FragmentExam create(int page, Context context) {

			Bundle args = new Bundle();
			args.putInt(ARG_PAGE, page);
			activityContext = context;
			FragmentExam fragment = new FragmentExam();
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mPage = getArguments().getInt(ARG_PAGE);

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			return inflater.inflate(R.layout.fragment_exam, container, false);
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {

			TextView tvQuestions = (TextView) getView().findViewById(
					R.id.tvquestion);
			final RadioGroup rgAnsware = (RadioGroup) getView().findViewById(
					R.id.rgansware);
			RadioGroup.LayoutParams rprms;

			// mapAnswerSelect

			final Questions questions = questionsList.get(mPage);

			for (int i = 0; i < questions.getAnswareList().size(); i++) {
				answer = questions.getAnswareList().get(i);

				RadioButton radioButton = new RadioButton(activityContext);
				radioButton.setText(answer.getAnswerText());
				radioButton.setId(i);
				rprms = new RadioGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				rgAnsware.addView(radioButton, rprms);
			}

			tvQuestions.setText(questions.getQuestionText());

			rgAnsware.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {

					RadioButton checkedRadioButton = (RadioButton) rgAnsware
							.findViewById(checkedId);
					pos = rgAnsware.indexOfChild(checkedRadioButton);

					answer = questions.getAnswareList().get(pos);

					((ActivityExam) getActivity()).getMapAnswerSelect().put(
							questions.getQuestionId(), answer.getAnswerId());

					// pos=group.indexOfChild(findViewById(group.getCheckedRadioButtonId()));

				}
			});

			/*
			 * final RadioGroup rg = (RadioGroup)getView().
			 * findViewById(R.id.rgansware);
			 * 
			 * 
			 * 
			 * rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			 * 
			 * @Override= public void onCheckedChanged(RadioGroup group, int
			 * checkedId) {
			 * 
			 * 
			 * } });
			 */

		}
	}

	public HashMap<String, String> getMapAnswerSelect() {
		return mapAnswerSelect;
	}

	public void setMapAnswerSelect(HashMap<String, String> mapAnswerSelect) {
		this.mapAnswerSelect = mapAnswerSelect;
	}

}