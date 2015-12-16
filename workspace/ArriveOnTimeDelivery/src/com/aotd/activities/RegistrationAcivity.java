package com.aotd.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.aotd.utils.Utils;

public class RegistrationAcivity extends Activity implements OnClickListener {

	private Button mBtnSend = null;
	private Button mBtnBack = null;
	private EditText mEmailId = null;

	public ImageView imgOnline;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		intializeUI();

	}

	private void intializeUI() {
		imgOnline = (ImageView) findViewById(R.id.aotd_img_mode);

		mBtnSend = (Button) findViewById(R.id.reg_send_Btn);
		mBtnBack = (Button) findViewById(R.id.reg_back_Btn);
		mEmailId = (EditText) findViewById(R.id.reg_email_EditTxt);

		mBtnSend.setOnClickListener(this);
		mBtnBack.setOnClickListener(this);

	}

	@Override
	protected void onResume() {

		super.onResume();

		if (Utils.checkNetwork(getApplicationContext()))
			imgOnline.setBackgroundResource(R.drawable.online);
		else
			imgOnline.setBackgroundResource(R.drawable.offline);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case (R.id.reg_send_Btn):

			// Intent intent=new
			// Intent(RegistrationAcivity.this,DriverGpsScreenActivity.class);
			// startActivity(intent);
			//

			break;

		case (R.id.reg_back_Btn):

			// Intent login_intent=new
			// Intent(RegistrationAcivity.this,LoginActivity.class);
			// startActivity(login_intent);
			finish();

			break;

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			this.finish();
			return true;

		}
		return super.onKeyDown(keyCode, event);
	}

}