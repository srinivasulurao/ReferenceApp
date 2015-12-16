package com.texastech.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.texastech.app.R;

public class CameraDialog extends Dialog implements android.view.View.OnClickListener {

	private Button btnTake, btnChoose, btnCancel;
	
	private Activity activity;
	
	public static Uri mCapturedImageURI;
	
	public CameraDialog(Activity activity) {
		super(activity);
 		this.activity = activity;  
		mCapturedImageURI = null;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multimedia_dialog_layout);
		setTitle("Camara");
		
		btnTake = (Button)findViewById(R.id.btn_take);
		btnChoose = (Button)findViewById(R.id.btn_choose);
		btnCancel = (Button)findViewById(R.id.btn_cancel);
		
		btnTake.setOnClickListener(this);
		btnChoose.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_take:
				String fileName = "capturedImage.jpg";
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.TITLE, fileName);
				mCapturedImageURI = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
				activity.startActivityForResult(cameraIntent, MultimediaAction.TAKE_PHOTO);
				
				dismiss();
			break;
			//----------------------------//
			case R.id.btn_choose:
				//choose photo 
				Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				//Intent pickImageIntent = new Intent(Intent.ACTION_PICK);	
				activity.startActivityForResult(pickImageIntent, MultimediaAction.CHOOSE_PHOTO );
				dismiss();
			break;
			//----------------------------//
			case R.id.btn_cancel:
				dismiss();
			break;
		}//end of switch
	}
}
