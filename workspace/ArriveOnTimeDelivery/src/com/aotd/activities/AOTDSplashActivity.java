package com.aotd.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AOTDSplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

		Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						wait(1000);
					}

				} catch (InterruptedException e) {

				} finally {
					Intent i = new Intent(AOTDSplashActivity.this,
							LoginActivity.class);
					startActivity(i);
					finish();

				}
			}
		};

		// ParseAnalytics.trackAppOpened(getIntent());
		splashThread.start();

	}

}
