package com.fivestarchicken.lms.sync;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.model.Exam;
import com.fivestarchicken.lms.model.Result;
import com.fivestarchicken.lms.utils.Commons;
import com.fivestarchicken.lms.webservice.Webservice;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SyncServices extends Service {
	int numMessages = 0;
	private DbAdapter dh;
	String lastSyncTime, userId,syncTime,url;
	Result result;
	List<Result> resultList = new ArrayList<Result>();
	String isPendingSync,isSyncServer;
	String methodName;
	SharedPreferences sharedPreferences;
	AsyncHttpClient client;
	public SyncServices() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		

	}

	@Override
	public void onStart(Intent intent, int startId) {
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
		
		this.dh = new DbAdapter(SyncServices.this);
		RequestParams params = new RequestParams();
		isPendingSync= intent.getStringExtra("isPendingSync");
		isSyncServer= intent.getStringExtra("isSyncServer");
		
		
		if(isSyncServer.equals("1")){
		lastSyncTime = dh.getLastSynctime(Commons.resultModule);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(SyncServices.this);
		userId = sharedPreferences.getString("userId", null);
		methodName = "exam_control/viewresult?";
		
		client = new AsyncHttpClient();
		
		
		 try {
			url=Webservice.url+methodName+"user_id="
						+ userId + "&last_sync_time=" + URLEncoder.encode(lastSyncTime,
						"UTF-8");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		client.post(url, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						System.out.println(response);
						saveResult(response);
							
					}
					
					@Override
					public void onFailure(int statusCode, Throwable error,
							String content) {
						// TODO Auto-generated method stub

						Toast.makeText(SyncServices.this, "Error occured!",
								Toast.LENGTH_SHORT).show();
					}

				});
		}
		
		if(isPendingSync.equals("1")){
			
			resultList.clear();	
			
			resultList=dh.getPendingResultList();
			
			for(Result result:resultList){
			
			
			methodName = "exam_control/employeeresult?";
			
			client = new AsyncHttpClient();
			
			
			 try {
				url=Webservice.url+methodName+"user_id="
							+ userId + "&module_id="+result.getModuleId()+ "&res="+result.getAnswerSelection();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
			client.post(url, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							System.out.println(response);
							saveResult(response);
								
						}
						
						@Override
						public void onFailure(int statusCode, Throwable error,
								String content) {
							// TODO Auto-generated method stub

							Toast.makeText(SyncServices.this, "Error occured!",
									Toast.LENGTH_SHORT).show();
						}

					});
			}
			
			
			
		}
		
		
		
	}
	
	
	void saveResult(String response){
		try {
		JSONObject obj = new JSONObject(response);
		String status = obj.getString("status");
		if (status.equals("200")) {
			
			syncTime=obj.getString("sync_time");
			JSONArray resultJarray = obj.getJSONArray("result");

			for (int i = 0; i < resultJarray.length(); i++) {
				JSONObject jOresult = (JSONObject) resultJarray.get(i);
				
				result=new  Result();
				result.setResultId(jOresult.getString("result_id"));
				//result.setExamId(jOresult.getString("exam_id"));
				//result.setExamName(jOresult.getString("exam_name"));
				result.setModuleId(jOresult.getString("module_id"));
				result.setModuleName(jOresult.getString("module_name"));
				result.setPercentage(jOresult.getString("result_percent"));
				result.setStatus(jOresult.getString("module_passed"));
				//resultList.add(result);
				dh.checkPendingResult(result);
		}
			
			
			//dh.checkPendingResult(result);
			
			if(lastSyncTime.equals("0000")){
				
				dh.insertSyncStatus(Commons.resultModule, syncTime)	;
				
				
			}else{
				dh.updateSyncStatus(Commons.resultModule, syncTime)	;
				
			}
			
			
		}


	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

	}

}
