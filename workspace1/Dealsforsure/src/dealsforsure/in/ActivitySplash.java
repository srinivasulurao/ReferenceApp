

package dealsforsure.in;

import dealsforsure.in.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;

public class ActivitySplash extends Activity {

	// Create an instance of ProgressBar
	private ProgressBar prgLoading;
	SharedPreferences sharedPreferences;
	private int progress = 0;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
		prgLoading.setProgress(progress);

		// Run progressbar via asynctask
		new Loading().execute();
	}

	public class Loading extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			// Create progress bar loading
			while (progress < 100) {
				try {
					Thread.sleep(1000);
					progress += 30;
					prgLoading.setProgress(progress);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

			// When progressbar finish call HomeActivity class
			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(ActivitySplash.this);
			String userId = sharedPreferences.getString("userId", null);
			/*if (userId != null) {

				Intent i = new Intent(ActivitySplash.this,
						ActivityRegistration.class);
				startActivity(i);
				overridePendingTransition(R.anim.open_next, R.anim.close_main);
			} else {*/
				Intent i = new Intent(ActivitySplash.this, ActivityHome.class);
				startActivity(i);
				overridePendingTransition(R.anim.open_next, R.anim.close_main);

			//}
		}
	}

}
