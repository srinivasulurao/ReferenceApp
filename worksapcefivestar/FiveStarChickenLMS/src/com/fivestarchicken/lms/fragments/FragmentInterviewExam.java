package com.fivestarchicken.lms.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.fivestarchicken.lms.ActivityInterviewExam;
import com.fivestarchicken.lms.ActivityRegister;
import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.libs.CustomViewPager;
import com.fivestarchicken.lms.model.Answer;
import com.fivestarchicken.lms.model.ExamModule;
import com.fivestarchicken.lms.model.Questions;
import com.fivestarchicken.lms.utils.Commons;
import com.fivestarchicken.lms.webservice.Webservice;

public class FragmentInterviewExam extends Fragment {

	CustomViewPager viewPager;
	static ArrayList<Questions> questionsList;
	public HashMap<String, String> mapAnswerSelect = new HashMap<String, String>();
	private DbAdapter dh;
	Button button2, button1;
	TextView tvtimmer;
	ExamModule examModule;
	boolean blink;
	CountDownTimer countDownTimer;
	String selectedAnswer;
	String languageType;

	Webservice webservices = new Webservice();

	SharedPreferences sharedPreferences;
	private String candidateId;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_interview_exam, container,
				false);

		try {
			this.dh = new DbAdapter(getActivity());
			
			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());

			candidateId = sharedPreferences.getString("candidateId", null);
			languageType = sharedPreferences.getString("intlanguageType", null);
			
			examModule = dh.getModuleDetail(Commons.INTERVIEW_MODULEID,languageType);
			questionsList = dh.getQuestionList(Commons.INTERVIEW_MODULEID,languageType);
			button1 = (Button) v.findViewById(R.id.button1);
			button2 = (Button) v.findViewById(R.id.button2);
			viewPager = (CustomViewPager) v.findViewById(R.id.pager);
			viewPager.setAdapter(new AdapterFragmentExam());
			button1.setText("Next");
			button2.setText("Previous");

			tvtimmer = (TextView) v.findViewById(R.id.TextView01);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			String inputString = examModule.getDuration();

			Date date = sdf.parse("1970-01-01 " + inputString);

			ShowCountTimer(getActivity(), tvtimmer, date.getTime(), 10 * 1000);

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

		}

		// code by TH

		

		Log.d("TAG", "candidateId :" + candidateId);

		return v;

	}

	public class AdapterFragmentExam extends FragmentPagerAdapter {
		final int PAGE_COUNT = questionsList.size();

		public AdapterFragmentExam() {
			super(getActivity().getSupportFragmentManager());
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}

		@Override
		public Fragment getItem(int position) {

			return FragmentExam.create(position, getActivity());
		}

	}

	public static class FragmentExam extends Fragment {
		public static final String ARG_PAGE = "ARG_PAGE",
				QUESTION = "QUESTION";

		private int mPage;
		public HashMap<String, String> mapAnswerSelect = new HashMap<String, String>();
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

					FragmentManager fragmentManager = getFragmentManager();

					FragmentInterviewExam fragment = (FragmentInterviewExam) fragmentManager
							.findFragmentById(R.id.fl_container);
					fragment.getMapAnswerSelect().put(
							questions.getQuestionId(), answer.getAnswerId());

					/*
					 * ((getActivity()) getActivity()).getMapAnswerSelect().put(
					 * questions.getQuestionId(), answer.getAnswerId());
					 */
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

	public void ShowCountTimer(final Context context, final TextView textviews,
			long totalTimeCountInMilliseconds,
			final long timeBlinkInMilliseconds) {

		blink = false;

		countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 500) {

			@Override
			public void onTick(long leftTimeInMilliseconds) {
				long seconds = leftTimeInMilliseconds / 1000;

				if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {

					if (blink) {
						textviews.setTextColor(Color.RED);

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
																.toHours(leftTimeInMilliseconds)),
										TimeUnit.MILLISECONDS
												.toSeconds(leftTimeInMilliseconds)
												- TimeUnit.MINUTES
														.toSeconds(TimeUnit.MILLISECONDS
																.toMinutes(leftTimeInMilliseconds))));
			}

			@SuppressWarnings("deprecation")
			@Override
			public void onFinish() {
				// dialogConfirm();

			}

		}.start();
	}

	public void dialogConfirm() {

		Button btContinue;
		TextView tvmessage;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.dialog_confirm, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
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

		// code by TH
		if (Commons.isNetworkAvailable(getActivity())) {

			new interviewresultAPI().execute();

		} else {
			showAlert("Check your internet connection");
		}

	}

	// code by TH
	private void showAlert(String msg) {
		try {
			AlertDialog.Builder networkBuilder = new AlertDialog.Builder(
					getActivity());
			networkBuilder.setTitle("Alert");
			networkBuilder.setMessage(msg);
			networkBuilder.setCancelable(false);
			networkBuilder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {

						}
					});
			networkBuilder.create().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// code by TH
	private class interviewresultAPI extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(getActivity());
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(String... unused) {
			// regjson = userFunction.login(email, passward, typeValue);

			return webservices.interviewresultAPImeth(candidateId,
					selectedAnswer,languageType);

		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();
				if (result != null && result.length() > 0) {

					JSONObject joresult = new JSONObject(result);

					Log.d("TAG", "joresult" + joresult);

					String status = joresult.getString("status");

					if (status.equals("200")) {

						String Message = joresult.getString("message");

						JSONObject examResult = joresult
								.getJSONObject("result");

						Log.d("TAG", "examResult :" + examResult);

						String examResul = examResult.getString("exam_passed");

						String examDate = examResult
								.getString("exam_taken_date");

						String examquestionsAnsd = examResult
								.getString("question_answered");

						String examScore = examResult
								.getString("result_percent");

						String candidateNam = examResult
								.getString("candidateName");
						String passScore = examResult
								.getString("pass_mark");
						
						String grade = examResult
								.getString("Grade");

						
						Bundle bundle = new Bundle();
						bundle.putString("EXAM_RESULT", examResul);
						bundle.putString("EXAM_DATE", examDate);
						bundle.putString("QUESTIONED_ANSWERED", examquestionsAnsd);
						bundle.putString("EXAM_SCORE", examScore);
						bundle.putString("CANDIDATE_NAME", candidateNam);
						bundle.putString("PASS_MARK", passScore);
						bundle.putString("GRADE", grade);

						Fragment fragment = new FragmentResult();
						fragment.setArguments(bundle);
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.fl_container, fragment)// frame_container
								.commit();

						
						
						

					} else {

						String errormessage = joresult.getString("message");

						Toast.makeText(getActivity(), errormessage,
								Toast.LENGTH_LONG).show();

					}

				}

			} catch (Exception e) {

				e.printStackTrace();
			}

		}
	}

	public HashMap<String, String> getMapAnswerSelect() {
		return mapAnswerSelect;
	}

	public void setMapAnswerSelect(HashMap<String, String> mapAnswerSelect) {
		this.mapAnswerSelect = mapAnswerSelect;
	}

}
