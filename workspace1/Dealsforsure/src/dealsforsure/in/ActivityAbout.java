package dealsforsure.in;

import dealsforsure.in.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActivityAbout extends ActionBarActivity implements OnClickListener {

	// Create an instance of ActionBar
	private ActionBar actionbar;
	String userId, userName;
	TextView tvwebsite;
	// Declare view objects
	private Button btnShare, btnRate, btlogin;
	SharedPreferences sharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Get ActionBar and set back private Button on actionbar
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityAbout.this);
		userId = sharedPreferences.getString("userId", null);

		// Connect view objects and xml ids
		btnShare = (Button) findViewById(R.id.btnShare);
		btnRate = (Button) findViewById(R.id.btnRate);
		btlogin = (Button) findViewById(R.id.btnmerchant);
		tvwebsite=(TextView)findViewById(R.id.tvwebsite);
		btnShare.setOnClickListener(this);
		btnRate.setOnClickListener(this);
		btlogin.setOnClickListener(this);
		tvwebsite.setOnClickListener(this);
		if (userId == null) {

			btlogin.setVisibility(View.VISIBLE);

		} else {
			btlogin.setVisibility(View.GONE);

		}

		// Get ActionBar and set back private Button on actionbar
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

	}

	// Listener for option menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Previous page or exit
			finish();
			overridePendingTransition(R.anim.open_main, R.anim.close_next);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btnShare:
			// Share this app to other application
			Intent iShare = new Intent(Intent.ACTION_SEND);
			iShare.setType("text/plain");
			iShare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
			
			String sAux = "\nLet me recommend you this application\n\n";
			  sAux = sAux + "https://play.google.com/store/apps/details?id=dealsforsure.in \n\n";
			
			iShare.putExtra(Intent.EXTRA_TEXT, sAux);
			startActivity(Intent.createChooser(iShare,
					getString(R.string.share_via)));

			break;

		case R.id.btnRate:
			// Rate this app in Play Store
			Intent iRate = new Intent(Intent.ACTION_VIEW);
			iRate.setData(Uri.parse(getString(R.string.gplay_url)));
			startActivity(iRate);

			break;

		case R.id.btnmerchant:
			// Rate this app in Play Store
			Intent i = new Intent(this, ActivityMerchantRegister.class);
			i.putExtra("type", "merchant");
			startActivity(i);
			finish();

			break;
			
		case R.id.tvwebsite:
			// Rate this app in Play Store
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.dealsforsure.in"));
			startActivity(browserIntent);
			break;

		default:
			break;
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);

	}

}