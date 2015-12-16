package com.example.testapplication;

import java.io.File;

import com.example.testapplication.webservices.Constants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SecondActivity extends ActionBarActivity {

	EditText edfirstWord, etSecondWord;
	ImageView ivclose;
	RelativeLayout rlMain;
	String firstStr,secondTextStr;
TextView tvheading;
Typeface face;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second_page);
		face = Typeface.createFromAsset(this.getAssets(), "timeburner_regular.ttf");
		initilizeUI();

	}

	private void initilizeUI() {
		rlMain = (RelativeLayout) findViewById(R.id.rlmain);
		edfirstWord = (EditText) findViewById(R.id.etfirstword);
		etSecondWord = (EditText) findViewById(R.id.etsecondword);
		ivclose = (ImageView) findViewById(R.id.ivclose);
		tvheading = (TextView) findViewById(R.id.tvheading);
		edfirstWord.setTypeface(face);
		etSecondWord.setTypeface(face);
		tvheading.setTypeface(face);
		ivclose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		File file = new File(Constants.imageFolder + "/"
				+ Constants.backgroundImage + ".png");

		if (file.exists()) {
			String pathName = Constants.imageFolder + "/"
					+ Constants.backgroundImage + ".png";
			Resources res = getResources();
			Bitmap bitmap = BitmapFactory.decodeFile(pathName);
			BitmapDrawable bd = new BitmapDrawable(res, bitmap);

			rlMain.setBackgroundDrawable(bd);
		}

		File file2 = new File(Constants.imageFolder + "/"
				+ Constants.closeButtonImage + ".png");

		if (file2.exists()) {
			String pathName = Constants.imageFolder + "/"
					+ Constants.closeButtonImage + ".png";
			Resources res = getResources();
			Bitmap bitmap = BitmapFactory.decodeFile(pathName);
			BitmapDrawable bd = new BitmapDrawable(res, bitmap);

			ivclose.setBackgroundDrawable(bd);
		}

		edfirstWord.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					firstStr = ((TextView) edfirstWord).getText()
							.toString();

					if (firstStr == null || firstStr.length() == 0) {

						displayConforim();
						return true;
					}else{
						
						
						tvheading.setText(firstStr);
					}

					// do something
				}
				return false;
			}

		});

		etSecondWord.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					secondTextStr = ((TextView) etSecondWord).getText()
							.toString();

					if (secondTextStr == null || secondTextStr.length() == 0) {

						displayConforim();
						return true;
					}else{
						
						
						tvheading.setText(firstStr+" "+secondTextStr);
					}

					// do something
				}
				return false;
			}

		});

	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                                                        INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

	void displayConforim() {
		TextView tcText, tvheader;
		
		Button btOK;
		String message;
		LayoutInflater li = LayoutInflater.from(SecondActivity.this);
		View promptsView = li.inflate(R.layout.dialog_validate, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				SecondActivity.this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		tcText = (TextView) promptsView.findViewById(R.id.tvpromocode);
		tvheader = (TextView) promptsView.findViewById(R.id.tvheader);
		// ivClose = (ImageView) promptsView.findViewById(R.id.ivclose);
		btOK = (Button) promptsView.findViewById(R.id.btnset);

		message = "Please fill the field before proceding";

		tcText.setText(message);

		btOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// notifyDataSetChanged();

				alertDialog.cancel();
			}
		});

	}

}
