package com.aotd.dialog;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aotd.activities.PdfSignatureActivity;
import com.aotd.activities.R;
import com.aotd.activities.SignatureActivity;
import com.aotd.model.SignatureModel;

public class EnterLastNameDialog extends Dialog implements
		android.view.View.OnClickListener {

	private EditText etName = null;
	private Button btnSave = null;
	private Button btnBack = null;
	private SignatureActivity context;
	private Bitmap signBitmap = null;
	private SignatureModel signModel = null;
	private String orderNumber;
	private String condition;

	public EnterLastNameDialog(Context context, String mOrderNum,
			String condition) {
		super(context);
		this.context = (SignatureActivity) context;
		this.setTitle("Type Last Name");
		orderNumber = mOrderNum;
		this.condition = condition;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aotd_lastname_dialog);
		initializeUI();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.lastname_btn_back:
			EnterLastNameDialog.this.dismiss();
			break;

		case R.id.lastname_btn_save:

			if (namevalidate(etName.getText().toString())) {

				this.signBitmap = context.signatureBitmap;

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				signBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] byteArray = stream.toByteArray();

				signModel = new SignatureModel();
				signModel.setBytes(byteArray);
				signModel.setLastname(etName.getText().toString());

				context.marrSignModel.add(signModel);
				Log.e("", "marrSignModel " + context.marrSignModel.size());

				signModel = null;
				context.viewFlipper.showNext();
				context.changeMode();
				context.getNotIfExist();

				// added logic
				try {
					File file = new File(
							Environment.getExternalStorageDirectory()
									+ "/AOTD/" + orderNumber + ".pdf");
					if (file.exists()) {

						Bitmap scaled = getResizedBitmap(byteArray, 80, 350);

						ByteArrayOutputStream bs = new ByteArrayOutputStream();
						scaled.compress(Bitmap.CompressFormat.JPEG, 100, bs);

						PdfSignatureActivity.toSaveThePDF(bs.toByteArray(),
								orderNumber, condition);

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					EnterLastNameDialog.this.dismiss();

				}

			} else
				Toast.makeText(context, "please enter last name",
						Toast.LENGTH_SHORT).show();

			break;
		}
	}

	private Bitmap getResizedBitmap(byte[] bs2, int h, int w) {
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		Bitmap bm = BitmapFactory.decodeByteArray(bs2, 0, bs2.length,
				bmpFactoryOptions);

		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
				/ (float) h);
		int widthRatio = (int) Math
				.ceil(bmpFactoryOptions.outWidth / (float) w);

		if (heightRatio > 1 || widthRatio > 1) {
			if (heightRatio > widthRatio) {
				bmpFactoryOptions.inSampleSize = heightRatio;
			} else {
				bmpFactoryOptions.inSampleSize = widthRatio;
			}
		}
		bmpFactoryOptions.inDither = false;
		bmpFactoryOptions.inScaled = false;
		bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

		bmpFactoryOptions.inJustDecodeBounds = false;

		bm = BitmapFactory.decodeByteArray(bs2, 0, bs2.length,
				bmpFactoryOptions);
		// // Resize
		// Matrix matrix = new Matrix();
		// matrix.postScale(heightRatio, widthRatio);
		//
		// bm = Bitmap.createBitmap(bm, 0, 0, h, w, matrix, true);

		return bm;

	}

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

	private void initializeUI() {

		etName = (EditText) findViewById(R.id.lastname_edittext_name);
		btnBack = (Button) findViewById(R.id.lastname_btn_back);
		btnSave = (Button) findViewById(R.id.lastname_btn_save);

		btnBack.setOnClickListener(this);
		btnSave.setOnClickListener(this);

	}

	private boolean namevalidate(String name) {
		if (!name.equalsIgnoreCase(""))
			return true;
		else
			return false;
	}
}