package com.example.syncexm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SyncReceiver extends BroadcastReceiver {
	static int noOfTimes = 0;
	
	// Method gets called when Broad Case is issued from MainActivity for every 10 seconds
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		noOfTimes++;
		Toast.makeText(context, "sync running " + noOfTimes + " times", Toast.LENGTH_SHORT).show();
		AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        // Checks if new records are inserted in Remote MySQL DB to proceed with Sync operation
        client.post("http://taskdynamo.com/lmsstage/app/exam_control/checkresult?user_id=44&check_sync_time=2015-05-15,13:05:03",params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                System.out.println(response);
                try {
                	
                	Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                	// Create JSON object out of the response sent by getdbrowcount.php
                    /*JSONObject obj = new JSONObject(response);
                    System.out.println(obj.get("count"));
                    // If the count value is not zero, call MyService to display notification 
                    if(obj.getInt("count") != 0){
                    	final Intent intnt = new Intent(context, MyService.class);
                    	// Set unsynced count in intent data
                    	intnt.putExtra("intntdata", "Unsynced Rows Count "+obj.getInt("count"));
                    	// Call MyService
                    	context.startService(intnt);
                    }else{
                    	Toast.makeText(context, "Sync not needed", Toast.LENGTH_SHORT).show();
                    }*/
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                String content) {
                // TODO Auto-generated method stub
               
                	Toast.makeText(context, "Error occured!", Toast.LENGTH_SHORT).show();
                }
            
        });
	}	

}
