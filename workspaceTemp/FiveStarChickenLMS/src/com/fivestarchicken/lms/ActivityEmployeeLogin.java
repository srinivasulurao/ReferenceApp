package com.fivestarchicken.lms;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fivestarchicken.lms.adapter.AdapterDropDown;
import com.fivestarchicken.lms.adapter.AdapterEmployee;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.fragments.FragmentSyscType;
import com.fivestarchicken.lms.fragments.FragmentSyscType.SyncDialogListener;
import com.fivestarchicken.lms.model.Answer;
import com.fivestarchicken.lms.model.Blog;
import com.fivestarchicken.lms.model.BlogDetail;
import com.fivestarchicken.lms.model.Certificate;
import com.fivestarchicken.lms.model.Configure;
import com.fivestarchicken.lms.model.Exam;
import com.fivestarchicken.lms.model.ExamModule;
import com.fivestarchicken.lms.model.ModuleDescription;
import com.fivestarchicken.lms.model.Questions;
import com.fivestarchicken.lms.model.Result;
import com.fivestarchicken.lms.model.User;
import com.fivestarchicken.lms.utils.Commons;
import com.fivestarchicken.lms.webservice.Webservice;

public class ActivityEmployeeLogin extends ActionBarActivity implements
		OnClickListener, SyncDialogListener {

	private TextView newAssesment, tvSyncData;
	private ListView usersList;
	private ActionBar actionbar;
	private DbAdapter dh;
	AdapterEmployee adapter;

	public static final int progress_bar_type = 0;
	private ProgressDialog pDialog;
	String lastSyncTime;
	Integer languageSelection;
	String userName, password, branchId;
	User user;
	SharedPreferences sharedPreferences;
	Webservice Webservices = new Webservice();
	
	ExamModule examModule, interviewModel;

	List<ExamModule> examModuleList = new ArrayList<ExamModule>();
	List<Blog> blogList = new ArrayList<Blog>();
	List<Configure> configureList = new ArrayList<Configure>();
	List<Certificate> certificateList = new ArrayList<Certificate>();

	ArrayList<ModuleDescription> downloadFileList = new ArrayList<ModuleDescription>();

	GridView list;

	ArrayList<User> employeeList;

	/*
	 * String[] itemname = { "rajesh", "gaurav", "kartik", "vinay", "malesh",
	 * "rajeev", "munna", "jagdish" };
	 * 
	 * Integer[] imgid = { R.drawable.image, R.drawable.image, R.drawable.image,
	 * R.drawable.image, R.drawable.image, R.drawable.image, R.drawable.image,
	 * R.drawable.image, };
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_employee_login);

		actionbar = getSupportActionBar();

		// actionbar.setDisplayHomeAsUpEnabled(true);
		// actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
		actionbar.setCustomView(mCustomView);
		actionbar.setDisplayShowCustomEnabled(true);

		newAssesment = (TextView) findViewById(R.id.tv_new_assesment);
		tvSyncData = (TextView) findViewById(R.id.tv_sync_data);
		// usersList = (ListView) findViewById(R.id.users_List);

		newAssesment.setOnClickListener(this);
		tvSyncData.setOnClickListener(this);

		this.dh = new DbAdapter(this);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityEmployeeLogin.this);

		branchId = sharedPreferences.getString("branchId", null);

		employeeList = dh.getEmployeeList();
		lastSyncTime = dh.getLastSynctime(Commons.genralModule);

		adapter = new AdapterEmployee(this, R.layout.adapter_employee,
				employeeList);
		list = (GridView) findViewById(R.id.gv_users_List);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				/*
				 * String Slecteditem = itemname[+position];
				 * Toast.makeText(getApplicationContext(), Slecteditem,
				 * Toast.LENGTH_SHORT).show();
				 * 
				 * Intent intent = new Intent(getApplicationContext(),
				 * ActivityEmpLogin.class); startActivity(intent);
				 */

				// userName = itemname[+position];
				user = (User) parent.getItemAtPosition(position);
				// userName="a@a.c";
				dialogLogin(user);

			}
		});
	}

	void dialogManagerLogin() {
		ImageView ivClose;
		final EditText etEmailId, etPassward;
		Button btLogin;

		LayoutInflater li = LayoutInflater.from(ActivityEmployeeLogin.this);
		View promptsView = li.inflate(R.layout.dialog_manager_login, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ActivityEmployeeLogin.this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		btLogin = (Button) promptsView.findViewById(R.id.employee_login);
		etEmailId = (EditText) promptsView.findViewById(R.id.etmanager_email);
		etPassward = (EditText) promptsView
				.findViewById(R.id.etmanager_password);

		ivClose = (ImageView) promptsView.findViewById(R.id.ivclose);
		btLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				userName = ((TextView) etEmailId).getText().toString();
				password = ((TextView) etPassward).getText().toString();

				if (userName == null || userName.length() == 0) {

					Toast.makeText(ActivityEmployeeLogin.this,
							"Please enter user name", Toast.LENGTH_SHORT)
							.show();

				} else if (password == null || password.length() == 0) {

					Toast.makeText(ActivityEmployeeLogin.this,
							"Please enter password", Toast.LENGTH_SHORT).show();

				} else {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(etEmailId.getWindowToken(), 0);
					alertDialog.cancel();
					if (Commons.isNetworkAvailable(ActivityEmployeeLogin.this)) {

						new AsyloginManager().execute();

					}
				}
			}
		});

		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etEmailId.getWindowToken(), 0);

				alertDialog.cancel();

			}
		});

	}

	@Override
	public void onSyncSelect(String type) {

		if (type.equals(Commons.SYNC_TYPE_RESTORE)) {

			dh.clearAllData();
            lastSyncTime = "0000";

		} else if (type.equals(Commons.SYNC_TYPE_UPDATE)) {

			lastSyncTime = dh.getLastSynctime(Commons.genralModule);

		}

		if (Commons.isNetworkAvailable(ActivityEmployeeLogin.this)) {

			new AsySyncData().execute();
		}

	}

	void dialogLogin(final User user) {

		ImageView ivClose;
		Spinner employeeLanguage;
		ArrayList<String> spinnerOption = new ArrayList<String>();
		Button btLogin;
		final EditText etPassword;
		TextView tvUserName;

		LayoutInflater li = LayoutInflater.from(ActivityEmployeeLogin.this);
		View promptsView = li.inflate(R.layout.dialog_employee_login, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ActivityEmployeeLogin.this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();
		ivClose = (ImageView) promptsView.findViewById(R.id.ivclose);
		employeeLanguage = (Spinner) promptsView
				.findViewById(R.id.employee_language);
		btLogin = (Button) promptsView.findViewById(R.id.employee_login);

		tvUserName = (TextView) promptsView.findViewById(R.id.tvusername);
		tvUserName.setText(user.getEmail());
		etPassword = (EditText) promptsView
				.findViewById(R.id.employee_password);

		spinnerOption.add("--Select Language--");
		spinnerOption.add("English");
		spinnerOption.add("Kannada");
		spinnerOption.add("Telugu");

		employeeLanguage.setAdapter(new AdapterDropDown(
				ActivityEmployeeLogin.this, R.layout.spinner_content,
				spinnerOption));
		employeeLanguage
				.setOnItemSelectedListener(new LanguageSelectedListener());

		btLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);

				password = ((TextView) etPassword).getText().toString();
				if (password == null || password.length() == 0) {

					Toast.makeText(ActivityEmployeeLogin.this,
							"Please enter password", Toast.LENGTH_SHORT).show();

				} else if (languageSelection == null || languageSelection == 0) {

					Toast.makeText(ActivityEmployeeLogin.this,
							"Please enter Language", Toast.LENGTH_SHORT).show();

				} else {

					validatePassward(user, password);

				}
			}
		});
		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);

				alertDialog.cancel();

			}
		});

	}

	void validatePassward(User user, String passward) {

		String base64Pssard = base64Convertion(passward).trim();
		String userPassward = user.getPassward().trim();
		// String base64Pssard="cGFuZGkxMjM=";
		// String userPassward="cGFuZGkxMjM=";
		if (base64Pssard.equals(userPassward)) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("userId", user.getUserId());
			editor.putString("profileImg", user.getProfileImage());
			editor.putString("starLevel", user.getStarRate());
			editor.putString("languageType", languageSelection.toString());

			editor.commit();

			Intent i = new Intent();
			i.setClass(ActivityEmployeeLogin.this, ActivityHome.class);
			startActivity(i);

		} else {

			Toast.makeText(ActivityEmployeeLogin.this,
					"user name and passward does not match. ",
					Toast.LENGTH_SHORT).show();
		}

	}

	public String base64Convertion(String passward) {

		byte[] data = null;
		try {
			data = passward.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String base64 = Base64.encodeToString(data, Base64.DEFAULT);

		// Receiving side

		return base64;
	}

	public void downloadfiles() {

		downloadFileList.clear();
		ArrayList<ModuleDescription> fileList=new ArrayList<ModuleDescription>();

		fileList .addAll(dh.getFileList());
		fileList .addAll(dh.getBlogFileList());

		for (ModuleDescription mod : fileList) {

			if (mod.getType().equals(Commons.TYPE_IMAGE)) {

				File imagefile = new File(Commons.app_image_folder + "/"
						+ mod.getDetail());

				if (!imagefile.exists()) {
					downloadFileList.add(mod);

				}
			} else if (mod.getType().equals(Commons.TYPE_VIDEO)) {

				File imagefile = new File(Commons.app_video_folder + "/"
						+ mod.getDetail());

				if (!imagefile.exists()) {

					downloadFileList.add(mod);
				}

			}else if (mod.getType().equals(Commons.TYPE_PDF)) {

				File imagefile = new File(Commons.app_pdf_folder + "/"
						+ mod.getDetail());

				if (!imagefile.exists()) {

					downloadFileList.add(mod);
				}

			}

		}

		if (downloadFileList.size() > 0) {

			if (Commons.isNetworkAvailable(ActivityEmployeeLogin.this)) {

				new AsyDownloadFile().execute();

			} else {

				Commons.internetErrorMessage(ActivityEmployeeLogin.this);
			}

		}

	}

	public class LanguageSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {

			languageSelection = position;

			

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	}

	private class AsySyncData extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(ActivityEmployeeLogin.this);
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(String... unused) {

			return Webservices.syncData(branchId, lastSyncTime);
		}

		protected void onPostExecute(String response) {
			try {
				progDailog.dismiss();
				Questions questions;
				Answer answer;
				Result result;
				Certificate certificate;
				String resultStatus, blogStatus,moduleStatus,certificateStatus,interviewStatus;
				ModuleDescription moduleDescription;
				Blog blog;
				Configure configure ;
				BlogDetail blogDetail;
				examModuleList.clear();
				certificateList.clear();
				configureList.clear();
				
				if (response != null && response.length() > 0) {

					JSONObject joresult = new JSONObject(response);

					String status = joresult.getString("status");

					// syncTime=joresult.getString("sync_time");

					if (status.equals("200")) {

						/*JSONArray examJarray = joresult.getJSONArray("Exam");

						for (int i = 0; i < examJarray.length(); i++) {
							JSONObject jOexam = (JSONObject) examJarray.get(i);

							exam = new Exam();
							exam.setExamId(jOexam.getString("examId"));
							exam.setTitle(jOexam.getString("examName"));
							exam.setLevel(jOexam.getString("star"));*/
						
						moduleStatus=joresult.getString("ModuleStaus");
						
						if (moduleStatus.equals("1")) {
							JSONArray examModuleJarray = joresult
									.getJSONArray("Modules");

							for (int j = 0; j < examModuleJarray.length(); j++) {
								JSONObject jOexamModule = (JSONObject) examModuleJarray
										.get(j);
								examModule = new ExamModule();
								
								examModule.setModuleId(jOexamModule
										.getString("module_id"));
								examModule.setModuleName(jOexamModule
										.getString("module_name"));
								examModule.setPassingScore(jOexamModule
										.getString("pass_mark"));
								examModule.setDuration(jOexamModule
										.getString("time_duration"));
								examModule.setTotalQuestions(jOexamModule
										.getString("question_count"));
								examModule.setCategoryId(jOexamModule
										.getString("CategoryId"));
								examModule.setStarLevel(jOexamModule
										.getString("star"));
								examModule.setLanguageType(jOexamModule
										.getString("lang_type"));
								JSONArray descriptionJarray = jOexamModule
										.getJSONArray("Module_Descrition");

								for (int k = 0; k < descriptionJarray.length(); k++) {
									JSONObject joDescription = (JSONObject) descriptionJarray
											.get(k);

									moduleDescription = new ModuleDescription();
									moduleDescription.setDetail(joDescription
											.getString("details"));
									moduleDescription.setModuleId(joDescription
											.getString("module_id"));
									moduleDescription.setOrder(joDescription
											.getString("order"));
									moduleDescription.setType(joDescription
											.getString("type"));
									
									moduleDescription.setLanguageType(jOexamModule
											.getString("lang_type"));

									examModule.getDescriptionList().add(
											moduleDescription);
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
									
									questions.setLanguageType(jOexamModule
											.getString("lang_type"));

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
										answer.setLanguageType(joQuestion
												.getString("lang_type"));
										answer.setModuleId(jOexamModule
												.getString("module_id"));
										questions.getAnswareList().add(answer);
									}

									examModule.getQuestionsList()
											.add(questions);
								}

								examModuleList.add(examModule);

							}

							

						dh.saveExam(examModuleList);
						}
						
						interviewStatus=joresult.getString("GeneralExamStatus");
						
						if (interviewStatus.equals("1")) {
						
						JSONArray interviewJarray = joresult
								.getJSONArray("GeneralExam");
						for (int g = 0; g < interviewJarray.length(); g++) {

						JSONObject jOinterview = (JSONObject) interviewJarray
						.get(g);

						interviewModel = new ExamModule();
						interviewModel.setModuleId(Commons.INTERVIEW_MODULEID);
						interviewModel.setPassingScore(jOinterview
								.getString("pass_mark"));
						interviewModel.setTotalQuestions(jOinterview
								.getString("genral_question_count"));
						interviewModel.setDuration(jOinterview
								.getString("time_duration"));
						interviewModel.setModuleName(jOinterview
								.getString("genralExamName"));
						interviewModel.setDescription(jOinterview
								.getString("Description"));
						interviewModel.setLanguageType(jOinterview
								.getString("lang_type"));
					

						JSONArray questionJarray = jOinterview
								.getJSONArray("generalquestion");

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
							questions.setModuleId(Commons.INTERVIEW_MODULEID);
							questions.setLanguageType(joQuestion
									.getString("lang_type"));

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
								answer.setModuleId(Commons.INTERVIEW_MODULEID);
								answer.setLanguageType(joQuestion
										.getString("lang_type"));
								questions.getAnswareList().add(answer);
							}

							interviewModel.getQuestionsList().add(questions);
						}

						dh.saveInterviewExam(interviewModel);
						}
						}

						resultStatus = joresult.getString("ResultStatus");

						if (resultStatus.equals("1")) {

							JSONArray resultJarray = joresult
									.getJSONArray("Result");

							for (int i = 0; i < resultJarray.length(); i++) {
								JSONObject jOresult = (JSONObject) resultJarray
										.get(i);

								result = new Result();
								result.setResultId(jOresult
										.getString("result_id"));
								// result.setExamId(jOresult.getString("exam_id"));
								/*result.setExamName(jOresult
										.getString("exam_name"))*/;
								result.setModuleId(jOresult
										.getString("module_id"));
								result.setModuleName(jOresult
										.getString("module_name"));
								result.setPercentage(jOresult
										.getString("result_percent"));
								result.setStatus(jOresult
										.getString("module_passed"));
								result.setUserId(jOresult
										.getString("user_id"));
								result.setExamDate(jOresult
										.getString("exam_taken_date"));
								// resultList.add(result);
								dh.insertResult(result);

							}
						}

						blogStatus = joresult.getString("BlogStaus");

						if (blogStatus.equals("1")) {

							JSONArray blogJarray = joresult
									.getJSONArray("Blogs");

							for (int i = 0; i < blogJarray.length(); i++) {
								JSONObject jOblog = (JSONObject) blogJarray
										.get(i);
								blog = new Blog();
								blog.setBlogId(jOblog.getString("blog_id"));
								blog.setBlogTitle(jOblog
										.getString("blog_title"));
								blog.setSyncDate(getDateTime());
								blog.setLanguageType(jOblog
										.getString("lang_type"));
								JSONArray blogDetailJarray = jOblog
										.getJSONArray("Blog_description");

								for (int j = 0; j < blogDetailJarray.length(); j++) {
									JSONObject jOblogDetail = (JSONObject) blogDetailJarray
											.get(j);

									blogDetail = new BlogDetail();
									blogDetail.setBlogId(jOblogDetail
											.getString("blog_id"));
									blogDetail.setType(jOblogDetail
											.getString("type"));
									blogDetail.setOrder(jOblogDetail
											.getString("order"));
									blogDetail.setDetail(jOblogDetail
											.getString("details"));
									blogDetail.setLanguageType(jOblogDetail
											.getString("lang_type"));

									blog.getBlogDetailList().add(blogDetail);

								}

								blogList.add(blog);

							}

							dh.saveBlog(blogList);
							
							
							
						}
						
						
						JSONArray configJarray = joresult
								.getJSONArray("ModuleLimit");

						for (int i = 0; i < configJarray.length(); i++) {
							JSONObject jOconfig = (JSONObject) configJarray
									.get(i);
							configure = new Configure();
							configure.setConfigureTitle(jOconfig.getString("type"));
							configure.setConfigureValue(jOconfig.getString("days"));
							configureList.add(configure);
							
						}
						configure = new Configure();
						configure.setConfigureTitle(Commons.skype_name);
						configure.setConfigureValue(joresult.getString("AdminSkypeID"));
						configureList.add(configure);
						
						configure = new Configure();
						configure.setConfigureTitle(Commons.admin_email);
						configure.setConfigureValue(joresult.getString("AdminMailID"));
						configureList.add(configure);
						
						configure = new Configure();
						configure.setConfigureTitle(Commons.admin_phone);
						configure.setConfigureValue(joresult.getString("AdminPhone"));
						configureList.add(configure);
						
						dh.saveConfig(configureList);
						
						certificateStatus=joresult.getString("CerticateStatus");
						
						if (certificateStatus.equals("1")) {
						
						JSONArray certificateJarray = joresult
								.getJSONArray("Certicates");

						for (int i = 0; i < certificateJarray.length(); i++) {
							JSONObject jOcertificate = (JSONObject) certificateJarray
									.get(i);
							
							certificate=new Certificate();
							certificate.setStarValue(jOcertificate.getString("star"));
							certificate.setFileName(jOcertificate.getString("certificatename"));
							certificate.setUserId(jOcertificate.getString("user_id"));
							//certificate.setPassDate(joresult.getString("AdminSkypeID"));
							certificateList.add(certificate);
						}
						dh.saveCertificate(certificateList);
						}

						lastSyncTime = joresult.getString("sync_time");

						dh.saveSyncTime(Commons.genralModule, lastSyncTime);

						downloadfiles();

						Toast.makeText(ActivityEmployeeLogin.this,
								"sync finished successfully", Toast.LENGTH_LONG)
								.show();

					} else {

						String errormessage = joresult.getString("message");

						Toast.makeText(ActivityEmployeeLogin.this,
								errormessage, Toast.LENGTH_LONG).show();

					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_employee_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Previous page or exit
			finish();

			return true;

		case R.id.action_logout:

			dh.clearUserData();

			SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("managerId");
			editor.commit();

			Intent i = new Intent();
			i.setClass(ActivityEmployeeLogin.this, ActivityLogin.class);
			startActivity(i);

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class AsyDownloadFile extends AsyncTask<String, String, Boolean> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(progress_bar_type);
		}

		@Override
		protected Boolean doInBackground(String... urls) {
			int index = 0;
			String filePath = null, fileUrl = null;
			for (ModuleDescription mod : downloadFileList) {
				index++;
				if (mod.getType().equals(Commons.TYPE_IMAGE)) {
					filePath = Commons.app_image_folder + "/" + mod.getDetail();
					fileUrl = Commons.app_image_path + "/" + mod.getDetail();

				} else if (mod.getType().equals(Commons.TYPE_VIDEO)) {

					filePath = Commons.app_video_folder + "/" + mod.getDetail();
					fileUrl = Commons.app_video_path + "/" + mod.getDetail();

				}else if (mod.getType().equals(Commons.TYPE_PDF)) {

					filePath = Commons.app_pdf_folder + "/" + mod.getDetail();
					fileUrl = Commons.app_pdf_path + "/" + mod.getDetail();

				}

				try {
					URL url = new URL(fileUrl);
					File file = new File(filePath);

					long startTime = System.currentTimeMillis();
					Log.d("ImageManager", "download begining");
					Log.d("ImageManager", "download url:" + url);
					Log.d("ImageManager", "downloaded file name:" + filePath);
					/* Open a connection to that URL. */
					URLConnection ucon = url.openConnection();

					/*
					 * Define InputStreams to read from the URLConnection.
					 */
					InputStream is = ucon.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);

					/*
					 * Read bytes to the Buffer until there is nothing more to
					 * read(-1).
					 */
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					int current = 0;
					while ((current = bis.read()) != -1) {
						baf.append((byte) current);
					}

					/* Convert the Bytes read to a String. */
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(baf.toByteArray());
					fos.close();
					/*
					 * Log.d("ImageManager", "download ready in" +
					 * ((System.currentTimeMillis() - startTime) / 1000) +
					 * " sec");
					 */

				} catch (IOException e) {
					Log.d("ImageManager", "Error: " + e);
					return false;
				}

				publishProgress(""
						+ (int) ((index * 100) / downloadFileList.size()));
				// publishProgress("" );

			}

			return true;
		}

		protected void onProgressUpdate(String... progress) {

			pDialog.setProgress(Integer.parseInt(progress[0]));

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Boolean audioInfo) {

			pDialog.dismiss();

		}
	}

	private class AsyloginManager extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(ActivityEmployeeLogin.this);
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(String... unused) {
			// regjson = userFunction.login(email, passward, typeValue);

			return Webservices.managerLogin(userName, password);
		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();
				if (result != null && result.length() > 0) {

					JSONObject joresult = new JSONObject(result);

					String status = joresult.getString("status");

					// syncTime=joresult.getString("sync_time");

					if (status.equals("200")) {

						FragmentManager fm = getSupportFragmentManager();
						FragmentSyscType frag = new FragmentSyscType();
						frag.show(fm, "txn_tag");

					} else {

						String errormessage = joresult.getString("message");

						Toast.makeText(ActivityEmployeeLogin.this,
								errormessage, Toast.LENGTH_LONG).show();

					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case progress_bar_type:
			pDialog = new ProgressDialog(ActivityEmployeeLogin.this);
			pDialog.setMessage("Downloading file. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setMax(100);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.show();
			return pDialog;
		default:
			return null;
		}
	}
	
	private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
}
	
	public void onBackPressed() {

		exitFromView();

	}

	private void exitFromView() {

		new AlertDialog.Builder(this)
				.setTitle("Five Star Chicken LMS")
				.setMessage("Do you want to exit?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										getApplicationContext(),
										MainActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.putExtra("EXIT", true);
								startActivity(intent);
							    finish();
							}

						}).setNegativeButton("No", null).show();
	}
	

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_new_assesment:
			Intent intent = new Intent(getApplicationContext(),
					ActivityRegister.class);
			startActivity(intent);
			break;

		case R.id.tv_sync_data:

			/*
			 * lastSyncTime=dh.getLastSynctime(Commons.genralModule);
			 * 
			 * if (Commons.isNetworkAvailable(ActivityEmployeeLogin.this)) {
			 * 
			 * new AsySyncData().execute();
			 * 
			 * }
			 */

			dialogManagerLogin();

			break;

		default:
			break;
		}
	}

}
