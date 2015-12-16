package foodzu.com;

import foodzu.com.Utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PayuActivity extends Activity {

	private WebView webView;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payu_webview);

		webView = (WebView) findViewById(R.id.payu_WView);
		// webView.getSettings().setJavaScriptEnabled(true);
		// webView.loadUrl(getIntent().getStringExtra("URL").toString());

		webView.setWebViewClient(new MyBrowser());
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.loadUrl(getIntent().getStringExtra("URL").toString());

	}

	private class MyBrowser extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

	}

	@Override
	public void onBackPressed() {
		alertDialogBuilder = new AlertDialog.Builder(PayuActivity.this);
		alertDialogBuilder
				.setMessage("Proceed only if Payment done successfully, else you might lose order/cart data.")
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Utils.ORDER = "DONE";
								finish();
							}
						})
					.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						})	;
		alertDialog = alertDialogBuilder.create();
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.show();
	}
}
