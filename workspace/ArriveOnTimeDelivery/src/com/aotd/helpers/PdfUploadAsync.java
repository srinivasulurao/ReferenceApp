package com.aotd.helpers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.aotd.utils.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class PdfUploadAsync extends AsyncTask<String, Void, Integer> {

	private Context ctx;
	private String orderNumber;
	private ProgressDialog mProgressDialog;
	private String fileName;
	private String condition;
	private Handler uploadPdfHandler;
	private String yes_or_No;

	HttpURLConnection connection = null;
	DataOutputStream outputStream = null;
	DataInputStream inputStream = null;

	private String pathToOurFile;

	private String urlServer = Utils.PDF_SIGN_UPLOAD_URL;
	private String lineEnd = "\r\n";
	private String twoHyphens = "--";
	private String boundary = "*****";

	int bytesRead, bytesAvailable, bufferSize;
	byte[] buffer;
	int maxBufferSize = 1 * 1024 * 1024;
	
	private String dlOrRt;

	public PdfUploadAsync(Context ctx, String orderNumber, String condition,
			Handler uploadPdfHandler, String yes_or_No) {
		
		Log.i("", "kkk.... pdf upload url.."+urlServer);
		this.ctx = ctx;
		this.orderNumber = orderNumber;

		mProgressDialog = new ProgressDialog(ctx);
		mProgressDialog.setMessage("Please wait...");
		mProgressDialog.setCancelable(false);
		this.condition = condition;

		if (condition.equals("updateSingDelivered")) {
			fileName = "dl" + orderNumber + ".pdf";
			dlOrRt = "dl";
		} else if (condition.equals("UpdateSecondSignatureForDeliver")) {
			fileName = "rt" + orderNumber + ".pdf";
			dlOrRt = "rt";
		}

		this.uploadPdfHandler = uploadPdfHandler;
		Log.i("", "kk yes r no :" + yes_or_No);

		this.yes_or_No = yes_or_No;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog.show();
	}

	@Override
	protected Integer doInBackground(String... params) {
		if (new File(Environment.getExternalStorageDirectory() + "/AOTD/"+dlOrRt
				+ orderNumber + "_sign.pdf").exists()) {

			try {

				pathToOurFile = Environment.getExternalStorageDirectory()
						+ "/AOTD/" + dlOrRt+orderNumber + "_sign.pdf";
				FileInputStream fileInputStream = new FileInputStream(new File(
						pathToOurFile));

				URL url = new URL(urlServer);
				connection = (HttpURLConnection) url.openConnection();
				
//				connection.setConnectTimeout(30*1000);// 1 min
//				connection.setReadTimeout(30000);
				// Allow Inputs & Outputs
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);

				// Enable POST method
				connection.setRequestMethod("POST");

				connection.setRequestProperty("Connection", "Keep-Alive");
				connection.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);

				outputStream = new DataOutputStream(
						connection.getOutputStream());
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				
				outputStream
						.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
								+ fileName + "\"" + lineEnd);
				
				outputStream.writeBytes(lineEnd);

				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// Read file
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {
					outputStream.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}

				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(twoHyphens + boundary + twoHyphens
						+ lineEnd);

				// Responses from the server (code and message)
				int serverResponseCode = connection.getResponseCode();
				String serverResponseMessage = connection.getResponseMessage();

				Log.i("", "kkk uploadpdf response massage is: "
						+ serverResponseMessage);

				fileInputStream.close();
				outputStream.flush();
				outputStream.close();

				return serverResponseCode;
			}

			catch (Exception e) {
				Log.i("", "kkk uploadpdf Exception massage is: "
						+ e.toString());
				e.printStackTrace();
			}

			return null;
		} else {
			Log.i("", "kkk uploadpdf else case is: "
					+ Environment.getExternalStorageDirectory() + "/AOTD/"+dlOrRt
					+ orderNumber + "_sign.pdf"+"in this no pdf..");
			return null;
		}
	}

	@Override
	protected void onPostExecute(Integer result) {
		Log.i("", "kkk uploadpdf response code is: " + result);
		super.onPostExecute(result);

		Message m = new Message();
		Bundle messageData = new Bundle();
		Log.i("", "kk yes r no :" + yes_or_No);

		messageData.putString("yes_or_No", yes_or_No);
		m.setData(messageData);

		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		if (result != null && result == 200) {

			m.what = 200;

			uploadPdfHandler.sendMessage(m);
		} else {
			m.what = 404;

			uploadPdfHandler.sendMessage(m);
		}
	}
}
