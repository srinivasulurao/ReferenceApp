package dealsforsure.in;

import dealsforsure.in.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
		} else {
			Intent i = new Intent(MainActivity.this, ActivityHome.class);
			startActivity(i);
		}
	}

	
	}



