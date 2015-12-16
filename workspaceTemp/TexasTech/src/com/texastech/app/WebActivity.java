package com.texastech.app;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import butterknife.InjectView;

import com.texastech.httputil.HttpConst;

public class WebActivity extends BaseActivity{

	@InjectView(R.id.web_view)
	WebView view;
	
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_web);
		String url = getIntent().getStringExtra("URL");
		if(url !=null){
			showProgressBar();
			
			if(url.endsWith(".pdf"))
				url = HttpConst.GOOGLE_DOC_URL+url;
			
			view.loadUrl(url);
		}
	}
	


	@Override
	protected void initXmlView() {
		view.getSettings().setJavaScriptEnabled(true);
		view.getSettings().setDomStorageEnabled(true);
		view.getSettings().setLoadWithOverviewMode(true);
		
		view.getSettings().setLoadWithOverviewMode(true);
		view.getSettings().setUseWideViewPort(true);
		
		/** Wait for the page to load then send the location information **/
		view.setWebViewClient(new WebViewClient(){
			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				dismissProgressBar();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				dismissProgressBar();
			}
		});
	}
}
