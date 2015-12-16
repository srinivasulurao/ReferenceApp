package com.texastech.app;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.loopj.android.http.RequestParams;
import com.texastech.bean.GetProfileInfo;
import com.texastech.bean.GetProfileInfo.ProfileInfo;
import com.texastech.bean.GetProgram;
import com.texastech.bean.GetProgram.Program;
import com.texastech.bean.GetStatus;
import com.texastech.bean.GetStatus.Status;
import com.texastech.helper.AlertManager;
import com.texastech.helper.AppConstant;
import com.texastech.helper.AppUtil;
import com.texastech.helper.CameraDialog;
import com.texastech.helper.MLog;
import com.texastech.helper.MonthYearPicker;
import com.texastech.helper.MultimediaAction;
import com.texastech.helper.PushRegistration;
import com.texastech.helper.ValidationManager;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class SignUpActivity extends BaseActivity implements HttpTaskListener{
	
	@InjectView(R.id.layout_program_date)
	LinearLayout programLayout;
	
	@InjectView(R.id.btn_save)
	TextView btnSave;
	
	@InjectView(R.id.place_holder)
	
	ImageView userProfilePic;
	
	@InjectView(R.id.tv_date)
	TextView tvDate;
	
	@InjectView(R.id.spin_role)
	Spinner spinRole;
	
	@InjectView(R.id.spin_program)
	Spinner spinProgram;
	
	@InjectView(R.id.et_first_name)
	EditText etFirstName;
	
	@InjectView(R.id.et_last_name)
	EditText etLastName;
	
	@InjectView(R.id.et_email)
	EditText etEmail;
	
	@InjectView(R.id.et_rn)
	EditText etRN;
	
	@InjectView(R.id.et_userName)
	EditText etUserName;
	
	@InjectView(R.id.et_password)
	EditText etPassword;
	
	@InjectView(R.id.spin_graduation_mm)
	Spinner spinGraMM;
	
	private File userProfile=null;
	
	private boolean isUserProfile=false;
	
	private MonthYearPicker myp;
	
	private List<Status> statusList;
	
	private List<Program> programs;
	
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
		
		if(getIntent().getStringExtra("TITLE").equals("Profile")){
			isUserProfile= true;
		}else{
			isUserProfile= false;
		}
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_signup);
		
		myp = new MonthYearPicker(this);
		myp.build(new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//tvDate.setText(""+myp.getSelectedYear());
			}
		}, null);
		
		tvDate.setText(""+Calendar.getInstance().get(Calendar.YEAR));
		
		
		/*tvDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myp.show();
			}
		});*/
		sendHttpRequest(Action.GET_STATUS_TYPE, null);
	}
	
	@Override
	protected void initXmlView() {
		String[] stringsMM = getResources().getStringArray(R.array.gra_mm); 
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.row_spinner,R.id.tv_lable,stringsMM);
		spinGraMM.setAdapter(adapter);
		
		userProfilePic.setBackgroundColor(getResources().getColor(R.color.gray));
		btnSave.setVisibility(View.VISIBLE);
		if(isUserProfile){
			btnSave.setText("Update");
		}
		
		spinRole.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				programLayout.setVisibility(position==1?View.VISIBLE:View.GONE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		
	}
	
	
	@OnClick(R.id.btn_save)
	public void onSaveEvent(View view){
		if(TextUtils.isEmpty(etFirstName.getText().toString())){
			etFirstName.setError("Please enter first name.");
			return;
		}
		
		if(TextUtils.isEmpty(etLastName.getText().toString())){
			etLastName.setError("Please enter last name.");
			return;
		}
		
		if(TextUtils.isEmpty(etEmail.getText().toString())){
			etEmail.setError("Please enter email.");
			return;
		}
		
		if(!ValidationManager.isValidEmail(etEmail.getText().toString())){
			etEmail.setError("Please enter valid email.");
			return;
		}
		if(TextUtils.isEmpty(etUserName.getText().toString())){
			etUserName.setError("Please enter UserName");
			return;
		}
		if(TextUtils.isEmpty(etPassword.getText().toString())){
			etPassword.setError("Please enter password");
			return;
		}
		
		if(userProfile!=null && userProfile.exists()){
			sendHttpRequest(Action.UPLOAD_USER_IMAGE, null);
		}else{
			sendHttpRequest(Action.CREATE_ACCOUNT, "");
		} 
	}
	
	@OnClick(R.id.place_holder)
	public void selectUserPic(){
		CameraDialog dialog = new CameraDialog(this);
		dialog.show();
	}
	
	
	@Override
	public void sendHttpRequest(Action ac, String... param) {
		showProgressBar();
		
		RestClient client = new RestClient(ac, this);
		RequestParams params = new RequestParams();
		
		switch (ac) {
			case CREATE_ACCOUNT:
				params.add("FirstName", etFirstName.getText().toString());
				params.add("LastName", 	etLastName.getText().toString());
				params.add("Email", 	etEmail.getText().toString());
				params.add("RN_No", 	etRN.getText().toString());
				
				params.add("Username", 	etUserName.getText().toString());
				params.add("Password", 	etPassword.getText().toString());
				params.put("User_photo", param[0]);
				
				if(spinRole.getSelectedItem()==null || spinRole.getSelectedItemPosition()==0){
					params.add("status_id", "");
					params.add("Role", 	"");
				}else{
					Status status =(Status)spinRole.getSelectedItem();
					params.add("status_id", status.status_id);
					params.add("Role", 		status.status_type);
				}
				
				
				params.add("Graduation_Date", 	"");
				if(!programLayout.isShown()){
					params.add("Program", 	"");
					params.add("Graduation_Month", 	"");
					params.add("Graduation_Year", 	"");
				}else{
					params.add("Program", 	spinProgram.getSelectedItemPosition()!=0? spinProgram.getSelectedItem().toString():"");
					
					if(spinGraMM.getSelectedItemPosition()==0){
						params.add("Graduation_Month", 	"");
					}else{
						params.add("Graduation_Month", 	""+spinGraMM.getSelectedItemPosition());
					}
					
					if(!tvDate.isShown()){
						params.add("Graduation_Year", 	"");
					}else{
						params.add("Graduation_Year", 	tvDate.getText().toString());
					}
				}
				
				if(isUserProfile)
					//params.add("login_id", 	""+settingPre.getRegistrationId());
					params.add("login_id", 	""+AppConstant.loginUser.id);
				
				params.add("DeviceToken", 	PushRegistration.getDeviceToken(getApplicationContext()));
				params.add("DeviceID", 		PushRegistration.getDeviceToken(getApplicationContext()));
				params.put("DeviceType", 	"Android");
				params.put("GUID", 			settingPre.getGUID());
				
				client.post(params);
			break;
			//-----------------//
			case GET_PROFILE_DETAILS:
				//params.add("login_id", 	""+settingPre.getRegistrationId());
				params.add("login_id", 	""+AppConstant.loginUser.id);
				client.post(params);
			break;
			//-----------------//
			case GET_STATUS_TYPE:case GET_PROGRAM:
				client.get();
			break;	
			//----------------//
			case UPLOAD_USER_IMAGE:
				try {
					params.put("User_photo", userProfile);
					client.post(params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			break;
		}
	}

	@Override
	public void onSuccess(Action ac, String response) {
		if(!isActivityVisible)return;
		
		switch (ac) {
			case CREATE_ACCOUNT:
				try {
					JSONObject object = new JSONObject(response);
					if(object.getBoolean("Success")){
						settingPre.setUserName(etUserName.getText().toString());
						settingPre.setPassword(etPassword.getText().toString());
						
						if(isUserProfile){
							AlertManager.showSuccessMessage(this, "Message", "Account updated successfully!");
						}else{
							//settingPre.setRegistrationId(object.getString("Result"));
							AlertManager.showSuccessMessage(this, "Message", "Account created successfully!");
						}
					}else{
						showMessage("Alert!", object.getString("Result"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				dismissProgressBar();
			break;
			//-----------------//
			case GET_PROFILE_DETAILS:
				try {
					JSONObject object = new JSONObject(response);
					if(object.getBoolean("success")){
						GetProfileInfo profileInfo = gson.fromJson(response, GetProfileInfo.class);
						ProfileInfo info =  profileInfo.profileInfos.get(0);
						
						MyApplication.getLoader().displayImage(IMAGE_URL+info.User_image_URL, userProfilePic, AppUtil.getImageSetting());
						etFirstName.setText(info.FirstName);
						etLastName.setText(info.LastName);
						etEmail.setText(info.Email);
						etRN.setText(info.RN_No);
						etPassword.setText(info.Password);
						etUserName.setText(info.ScreenName);
						tvDate.setText(info.Graduation_Year);
						
						if(!TextUtils.isEmpty(info.Graduation_Month) && Integer.parseInt(info.Graduation_Month)!=0){
							spinGraMM.setSelection(Integer.parseInt(info.Graduation_Month));
						}
						
						if(!TextUtils.isEmpty(info.status_type)){
							for (int i = 0; i < statusList.size(); i++) {
								if(statusList.get(i).status_type.equals(info.status_type)){
									spinRole.setSelection(i);
									break;
								}
							}
						}
						
						if(!TextUtils.isEmpty(info.Program)){
							for (int i = 0; i < programs.size(); i++) {
								if(programs.get(i).Title.equals(info.Program)){
									spinProgram.setSelection(i);
									break;
								}
							}
						}
					}else{
						showMessage("Alert!", object.getString("Result"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				dismissProgressBar();
			break;
			//-----------------//
			case GET_STATUS_TYPE:
				GetStatus getStatus = gson.fromJson(response, GetStatus.class);
				if(getStatus.success){
					Status status = new Status();
					status.status_id = "-1";
					status.status_type = "Select:";
					getStatus.status.add(0, status);
					statusList = getStatus.status;
					ArrayAdapter<Status> adapter = new ArrayAdapter<Status>(getApplicationContext(), R.layout.row_spinner,R.id.tv_lable,statusList);
					spinRole.setAdapter(adapter);
				}
				sendHttpRequest(Action.GET_PROGRAM, null);
			break;
			//-----------------//
			case GET_PROGRAM:
				GetProgram getProgram = gson.fromJson(response, GetProgram.class);
				if(getProgram.success){
					Program program = new Program();
					program.ID = "-1";
					program.Title = "Select:";
					getProgram.programs.add(0, program);
					programs = getProgram.programs;
					ArrayAdapter<Program> adapter = new ArrayAdapter<Program>(getApplicationContext(), R.layout.row_spinner,R.id.tv_lable,programs);
					spinProgram.setAdapter(adapter);
				}
				
				if(isUserProfile)
					sendHttpRequest(Action.GET_PROFILE_DETAILS, null);
				else
					dismissProgressBar();
			break;
			//------------------//
			case UPLOAD_USER_IMAGE:
				//{"Success":true,"Result":"uploads\/users\/2018786546plasma1.png"}
			try {
				JSONObject object = new JSONObject(response);
				sendHttpRequest(Action.CREATE_ACCOUNT, object.getBoolean("Success")?object.getString("Result"):"");
			} catch (Exception e) {
				e.printStackTrace();
				dismissProgressBar();
			}
			break;
		}
	}

	@Override
	public void onFaliure(Action ac, String error) {
		if(!isActivityVisible)return;
		showMessage("Alert!", error);
		dismissProgressBar();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case MultimediaAction.CHOOSE_PHOTO:{
					Uri targetImgUri = data.getData();
					if(targetImgUri!=null){
						MyApplication.getLoader().displayImage(targetImgUri.toString(), userProfilePic);
						String imagePath = getPathFromUri(targetImgUri);//userProfile
						if (imagePath != null) {
							userProfile = new File(imagePath);
						}
					}
				}
				break;
				//------------------------------//	
				case MultimediaAction.TAKE_PHOTO:{
					Uri targetImgUri = CameraDialog.mCapturedImageURI;
					MLog.v("targetImgUri", ""+targetImgUri);
					if(targetImgUri!=null){
						MyApplication.getLoader().displayImage(targetImgUri.toString(), userProfilePic);
						String imagePath = getPathFromUri(targetImgUri); 
						MLog.v("imagePath", ""+imagePath);
						if (imagePath != null) {
							userProfile = new File(imagePath);
						}
					}
				}
				break;
			}//end of switch
		} 
	}
	
	public String getPathFromUri(Uri uri) {
		String selectedImagePath=null;
		if (uri != null) {
			selectedImagePath = getPath(uri);
			if (selectedImagePath == null)
				selectedImagePath = uri.getPath();
		}
		return selectedImagePath;
	}
	
 	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
}
