package com.texastech.activity;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.InjectView;

import com.loopj.android.http.RequestParams;
import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.bean.GetProgram;
import com.texastech.bean.GetProgram.Program;
import com.texastech.bean.GetStatus;
import com.texastech.bean.GetStatus.Status;
import com.texastech.helper.AlertManager;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class PushNotificationActivity extends BaseActivity  implements HttpTaskListener{

	@InjectView(R.id.btn_save)
	TextView btnSave;
	
	@InjectView(R.id.et_push_msg)
	EditText etPushMsg;
	
	@InjectView(R.id.tv_date)
	TextView tvDate;
	
	@InjectView(R.id.spin_role)
	Spinner spinRole;
	
	@InjectView(R.id.spin_program)
	Spinner spinProgram;
	
	@InjectView(R.id.spin_graduation_mm)
	Spinner spinGraTerm;
	
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
	}

	@Override
	protected void initXmlView() {
		btnSave.setVisibility(View.VISIBLE);
		btnSave.setText("Send");
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(etPushMsg.getText().toString())){
					etPushMsg.setError("Please enter message.");
				}else{
					sendHttpRequest(Action.SEND_PUSH_NOTIFICATION, null);
				}
			}
		});
		
		tvDate.setText(""+Calendar.getInstance().get(Calendar.YEAR));
		
		String[] stringsMM = getResources().getStringArray(R.array.gra_mm); 
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.row_spinner,R.id.tv_lable,stringsMM);
		spinGraTerm.setAdapter(adapter);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_push_notification);
		
		sendHttpRequest(Action.GET_STATUS_TYPE, null);
	}
	
	
	
	@Override
	public void sendHttpRequest(Action ac, String... param) {
		showProgressBar();
		RestClient client = new RestClient(ac, this);
		switch (ac) {
			case GET_STATUS_TYPE:case GET_PROGRAM:
				client.get();
			break;	
			//---------------//
			case SEND_PUSH_NOTIFICATION:
				RequestParams params = new RequestParams();
				 
				params.add("Graduation_Year", 	tvDate.getText().toString());
				//params.add("user_id", 			""+AppConstant.loginUser.ID);
				params.add("Program", 	spinProgram.getSelectedItemPosition()!=0? spinProgram.getSelectedItem().toString():"");
				
				if(spinRole.getSelectedItem()==null || spinRole.getSelectedItemPosition()==0){
					params.add("status_id", "");
				}else{
					Status status =(Status)spinRole.getSelectedItem();
					params.add("status_id", status.status_id);
				}
				
				if(spinGraTerm.getSelectedItemPosition()==0){
					params.add("Graduation_Month", 	"");
				}else{
					params.add("Graduation_Month", 	""+spinGraTerm.getSelectedItemPosition());
				}
				
				params.add("message", 			etPushMsg.getText().toString());
				params.add("pushID", 			settingPre.getGUID());
				
				client.post(params);
			break;	
		}
	}
	
	
	@Override
	public void onSuccess(Action ac, String response) {
		if(!isActivityVisible)return;
		
		switch (ac) {
			case GET_STATUS_TYPE:
				GetStatus getStatus = gson.fromJson(response, GetStatus.class);
				if(getStatus.success){
					Status status = new Status();
					status.status_id = "-1";
					status.status_type = "Select:";
					getStatus.status.add(0, status);
					ArrayAdapter<Status> adapter = new ArrayAdapter<Status>(getApplicationContext(), R.layout.row_spinner,R.id.tv_lable,getStatus.status);
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
					ArrayAdapter<Program> adapter = new ArrayAdapter<Program>(getApplicationContext(), R.layout.row_spinner,R.id.tv_lable,getProgram.programs);
					spinProgram.setAdapter(adapter);
				}
				dismissProgressBar();
			break;
			//------------------//
			case SEND_PUSH_NOTIFICATION:
				//{"success":true, "result":"Push Notification Message Sent Successfully"}
				try {
					JSONObject object = new JSONObject(response);
					if(object.getBoolean("success")){
						AlertManager.showSuccessMessage(this, "", object.getString("result"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dismissProgressBar();
			break;
		}
	}

	@Override
	public void onFaliure(Action ac, String error) {
		if(!isActivityVisible)return;
		showMessage("Alert!", error);
		dismissProgressBar();
	}
}
