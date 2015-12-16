package com.aotd.helpers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.aotd.utils.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SyncUploadPDF extends AsyncTask<String, Void, Integer>{

	private Context context;
	private String filePath;
	private Handler syncDataHandler;
	private String pathToOurFile;
	private String fileName;
	
	private String urlServer = Utils.PDF_SIGN_UPLOAD_URL;
	
	HttpURLConnection connection = null;
	DataOutputStream outputStream = null;
	DataInputStream inputStream = null;
	
	private String lineEnd = "\r\n";
	private String twoHyphens = "--";
	private String boundary = "*****";

	int bytesRead, bytesAvailable, bufferSize;
	byte[] buffer;
	int maxBufferSize = 1 * 1024 * 1024;
	
	
	public SyncUploadPDF(Context context, String filePath,Handler syncDataHandler) {

		this.context = context;
		this.filePath = filePath;
		
		fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
		
		String[] parts = fileName.split("_");
		fileName = parts[0].toString()+".pdf";
		
		Log.e("", "kkk sync fileName : "+ fileName);
		
		this.syncDataHandler = syncDataHandler;
	}

	@Override
	protected Integer doInBackground(String... params) {
		
		if (new File(filePath).exists() && !fileName.contains("_")) {

			try {

				pathToOurFile = filePath;
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

				Log.i("", "kkk sync uploadpdf response massage is: "
						+ serverResponseMessage);

				fileInputStream.close();
				outputStream.flush();
				outputStream.close();

				return serverResponseCode;
			}

			catch (Exception e) {
				Log.i("", "kkk sync uploadpdf Exception massage is: "
						+ e.toString());
				e.printStackTrace();
			}

			return null;
		} else {
			Log.i("", "kkk sync uploadpdf else case is: " + filePath + "_sign.pdf"+"in this no pdf..");
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		Log.i("", "kkk sync uploadpdf response code is: " + result+" filePath "+filePath);
		super.onPostExecute(result);

		Message m = new Message();
		Bundle messageData = new Bundle();
		m.setData(messageData);

		if (result != null && result == 200) {

			m.what = 12;
			messageData.putString("success","success" );
			try {
				
				File deletedFile = new File(filePath);
				if(deletedFile.exists())
				deletedFile.delete();
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				
				syncDataHandler.sendMessage(m);
			}
			
		} else {
			
			m.what = 13;
			messageData.putString("HttpError","error");
			syncDataHandler.sendMessage(m);
		}
		
		
	}

}
