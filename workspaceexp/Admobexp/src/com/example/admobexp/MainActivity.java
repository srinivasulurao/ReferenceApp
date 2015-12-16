package com.example.admobexp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class MainActivity extends ActionBarActivity {
	
	private AdView adView;
    private static final String AD_UNIT_ID = "ca-app-pub-8728968316870813/1780604716";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		adView = new AdView(MainActivity.this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);
        
     
        LinearLayout layout = (LinearLayout) findViewById(R.id.rlview);
        layout.addView(adView);

         AdRequest adRequest = new AdRequest.Builder().build();

         adView.loadAd(adRequest);
	
	
	
	}

	
}
