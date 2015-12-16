package com.example.exampleapp;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	
	 private final String TAG = "MainScreen";
	    private String textToBeShown = "These are the text";
	    private String sampleText = "Here are more text";
	    private TextView mTextView = null;

	    Handler handler = new Handler() {

	        public void handleMessage(Message msg) {
	            if (msg.what == 1) {
	                updateUI();
	            }
	        };
	    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTextView = (TextView) findViewById(R.id.ui_main_textView);
		
		  Log.e(TAG, "Width = " + mTextView.getWidth());
        mTextView.setTextSize(20f);
        
        Log.e(TAG, "Width = " + mTextView.getWidth());
        for (int i = 0; i < 100; i++) {
            textToBeShown = textToBeShown + " =" + i + "= " + sampleText;
        }

        // I am using timer as the in UI is not created and
        // we can't get the width.
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                // So that UI thread can handle UI work
                handler.sendEmptyMessage(1);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000 * 1);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void updateUI() {
    	
    	  Log.e(TAG, "Width = " + mTextView.getWidth());

        // Set text
        mTextView.setText(textToBeShown);
        // Check the width
        Log.e(TAG, "Width = " + mTextView.getWidth());

        // Check height of one line
        Log.e(TAG, "Line height= " + mTextView.getLineHeight());

        // Check total height for TextView
        Log.e(TAG, "Text height= " + mTextView.getHeight());

        // No of line we can show in textview
        int totalLine = mTextView.getHeight() / mTextView.getLineHeight();
        Log.e(TAG, "Total Lines are height= " + totalLine);


        for (int i = 0; i < totalLine; i++) {
            // Get No of characters fit in that textView
            int number = mTextView.getPaint().breakText(textToBeShown, 0, textToBeShown.length(), true,
                    mTextView.getWidth(), null);
            Log.e(TAG, "Number of chracters = " + number);

            // Show the text that fit into line
            Log.e(TAG, textToBeShown.substring(0, number));
            // Update the text to show next
            textToBeShown = textToBeShown.substring(number, textToBeShown.length());
        }
    }
	    


	
}
