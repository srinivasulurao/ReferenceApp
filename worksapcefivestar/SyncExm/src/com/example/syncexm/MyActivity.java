package com.example.syncexm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MyActivity extends Activity implements DownloadResultReceiver.Receiver {

    private ListView listView = null;

  //  private ArrayAdapter arrayAdapter = null;
    
    private ArrayAdapter<String> arrayAdapter = null;

    private DownloadResultReceiver mReceiver;

    final String url = "http://javatechig.com/api/get_category_posts/?dev=1&slug=android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Allow activity to show indeterminate progressbar */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        /* Set activity layout */
        setContentView(R.layout.activity_my);

        /* Initialize listView */
        listView = (ListView) findViewById(R.id.listView);
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        
        /*AlarmManager alarms = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);

         Starting Download Service 
     
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);

         Send optional extras to Download IntentService 
        intent.putExtra("url", url);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("requestId", 101);
        
        final PendingIntent pIntent = PendingIntent.getBroadcast(this,
				1234567, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarms.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), 10000, pIntent);*/
        
        
     /*   Intent intent = new Intent(MyActivity.this,
        		DownloadService.class);
        final PendingIntent pIntent = PendingIntent.getService(MyActivity.this, 0,
        		intent, 0);

        AlarmManager alarms  = (AlarmManager) MyActivity.this
                .getSystemService(Context.ALARM_SERVICE);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), 1000*60,
                pIntent);*/
        
        
        Intent alarmIntent = new Intent(getApplicationContext(), SyncReceiver.class);
		// Pending Intent Object
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// Alarm Manager Object
		AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		// Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in 
		// Remote MySQL DB
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, pendingIntent);

       //startService(intent);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadService.STATUS_RUNNING:

                setProgressBarIndeterminateVisibility(true);
                break;
            case DownloadService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
                setProgressBarIndeterminateVisibility(false);

                String[] results = resultData.getStringArray("result");

                /* Update ListView with result */
                
                arrayAdapter = new ArrayAdapter<String>(MyActivity.this, android.R.layout.simple_list_item_1, results);
               // arrayAdapter = new ArrayAdapter(MyActivity.this, android.R.layout.simple_list_item_2, results);
                listView.setAdapter(arrayAdapter);

                break;
            case DownloadService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}