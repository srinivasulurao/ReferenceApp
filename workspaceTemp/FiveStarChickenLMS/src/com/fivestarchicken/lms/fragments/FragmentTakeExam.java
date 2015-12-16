package com.fivestarchicken.lms.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.fivestarchicken.lms.ActivityExamModule;
import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.adapter.AdapterExamCategory;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.model.Answer;
import com.fivestarchicken.lms.model.Exam;
import com.fivestarchicken.lms.model.ExamModule;
import com.fivestarchicken.lms.model.Questions;
import com.fivestarchicken.lms.utils.Commons;
import com.fivestarchicken.lms.webservice.Webservice;

public class FragmentTakeExam extends Fragment {

	ListView lvExamCategory;
	AdapterExamCategory adapterExamCategory;
	List<Exam> examList = new ArrayList<Exam>();
	private DbAdapter dh;
	Webservice Webservices = new Webservice();
	SharedPreferences sharedPreferences;
	String userId,starLevel;
	Exam exam;
	ExamModule examModule;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_take_exam, container, false);

		initilizeUI(v);

		/*if (dh.isAllExamClear()) {

			//starLevel = dh.getStarLevel(userId);
			if (starLevel == null) {

				starLevel = "1";
			} else {

				Integer starLevelint = Integer.parseInt(starLevel) + 1;

				starLevel = starLevelint.toString();
			}

			if (Commons.isNetworkAvailable(getActivity())) {

				new AsyExamDetail().execute();

			}
		} else {*/
			//examList = dh.getExamDetail();
		
			adapterExamCategory = new AdapterExamCategory(getActivity(),
					R.layout.adapter_exam_category, examList);

			lvExamCategory.setAdapter(adapterExamCategory);

			lvExamCategory.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					exam = (Exam) parent.getItemAtPosition(position);

					Intent i = new Intent(getActivity(),
							ActivityExamModule.class);
					i.putExtra("examId", exam.getExamId());
					startActivity(i);
				}
			});
		//}

		return v;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		}
	}

	private void initilizeUI(View v) {

		try {
			lvExamCategory = (ListView) v.findViewById(R.id.lvexamcategory);

			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			userId = sharedPreferences.getString("userId", null);
			this.dh = new DbAdapter(getActivity());

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/*private class AsyExamDetail extends AsyncTask<String, Void, String> {
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

			return Webservices.getExamDetails(userId, "1", starLevel);
		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();
				Questions questions;
				Answer answer;
				examList.clear();
				if (result != null && result.length() > 0) {

					JSONObject joresult = new JSONObject(result);

					String status = joresult.getString("status");

					if (status.equals("200")) {

						JSONArray examJarray = joresult.getJSONArray("result");

						for (int i = 0; i < examJarray.length(); i++) {
							JSONObject jOexam = (JSONObject) examJarray.get(i);

							exam = new Exam();
							exam.setExamId(jOexam.getString("exam_id"));
							exam.setCategoryName(jOexam
									.getString("category_name"));
							exam.setTitle(jOexam.getString("exam_name"));
							exam.setModuleCount(jOexam
									.getString("count_module"));

							JSONArray examModuleJarray = jOexam
									.getJSONArray("modules");

							for (int j = 0; j < examModuleJarray.length(); j++) {
								JSONObject jOexamModule = (JSONObject) examModuleJarray
										.get(j);
								examModule = new ExamModule();
								examModule.setExamId(jOexam
										.getString("exam_id"));
								examModule.setModuleId(jOexamModule
										.getString("module_id"));
								examModule.setModuleName(jOexamModule
										.getString("module_name"));
								examModule.setPassingScore(jOexamModule
										.getString("pass_mark"));
								examModule.setDuration(jOexamModule
										.getString("time_duration"));
								examModule.setTotalQuestions(jOexamModule
										.getString("random_ques_no"));
								examModule.setLevel(jOexamModule
										.getString("star_rate"));
								
								if (jOexamModule.getString("module_passed")
										.equals("Passed")) {
									
									examModule.setIsPassed("1");
								}else{
									
									examModule.setIsPassed("0");
								}

								JSONArray questionJarray = jOexamModule
										.getJSONArray("questions");

								for (int k = 0; k < questionJarray.length(); k++) {
									JSONObject joQuestion = (JSONObject) questionJarray
											.get(k);
									questions = new Questions();
									questions.setAnswerCount(joQuestion
											.getString("answer_count"));
									questions.setQuestionId(joQuestion
											.getString("question_id"));
									questions.setQuestionText(joQuestion
											.getString("question"));
									questions.setModuleId(jOexamModule
											.getString("module_id"));

									JSONArray answareJarray = joQuestion
											.getJSONArray("answers");

									for (int l = 0; l < answareJarray.length(); l++) {
										JSONObject joAnsware = (JSONObject) answareJarray
												.get(l);
										answer = new Answer();
										answer.setAnswerId(joAnsware
												.getString("answer_id"));
										answer.setAnswerText(joAnsware
												.getString("answer"));
										answer.setQuestionId(joQuestion
												.getString("question_id"));
										questions.getAnswareList().add(answer);
									}

									examModule.getQuestionsList()
											.add(questions);
								}
								
								exam.getExamModuleList().add(examModule);

							}
							
							exam.setModuleCount(Integer.toString(exam.getExamModuleList().size()));
							examList.add(exam);
						}

						dh.saveExam(examList);

						//examList = dh.getExamDetail();
						adapterExamCategory = new AdapterExamCategory(
								getActivity(), R.layout.adapter_exam_category,
								examList);

						lvExamCategory.setAdapter(adapterExamCategory);

						lvExamCategory
								.setOnItemClickListener(new OnItemClickListener() {
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {

										exam = (Exam) parent
												.getItemAtPosition(position);

										Intent i = new Intent(getActivity(),
												ActivityExamModule.class);
										i.putExtra("examId", exam.getExamId());
										startActivity(i);
									}
								});

					} else {

						String errormessage = joresult.getString("message");

						Toast.makeText(getActivity(), errormessage,
								Toast.LENGTH_LONG).show();

					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}*/

}
