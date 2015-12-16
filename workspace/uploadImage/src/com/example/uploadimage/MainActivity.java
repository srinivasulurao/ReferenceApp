package com.example.uploadimage;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    TextView tv;
    Button b;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         
        b = (Button)findViewById(R.id.but);
        tv = (TextView)findViewById(R.id.tv);
        tv.setText("Uploading file path :- '/sdcard/android_1.png'");
         
        b.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
            	new saveAudioAsyncTask().execute();
            }
            	
            });
    }
    
	private class saveAudioAsyncTask extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return  uploadFile(Environment.getExternalStorageDirectory() + "/Voicey/1.jpg");
		}

		protected void onProgressUpdate(Void... progress) {

			

		}

		@Override
		protected void onPostExecute(String result) {
			
				

	}
	}
     
    public String uploadFile(String sourceFileUri) {
    	String result = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String imageBase64=null;
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		String response = null;
    	File name = new File(
    			sourceFileUri);

		if (name.exists()) {

			try {
				int serverResponseCode = 0;
				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(
						name);
				URL url = new URL(
						"http://voicey.me/web-services/imageuptest.php?voiceid=185");

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url
						.openConnection();
				conn.setDoInput(true); // Allow InputsFF
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a
											// Cached
											// Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection",
						"Keep-Alive");
				conn.setRequestProperty("ENCTYPE",
						"multipart/form-data");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary="
								+ boundary);
				conn.setRequestProperty("uploaded_file",
						name.toString());

				dos = new DataOutputStream(
						conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary
						+ lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
						+ name + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream
						.available();

				bufferSize = Math.min(bytesAvailable,
						maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0,
						bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream
							.available();
					bufferSize = Math.min(bytesAvailable,
							maxBufferSize);
					bytesRead = fileInputStream.read(
							buffer, 0, bufferSize);

				}

				// send multipart form data necesssary after
				// file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary
						+ twoHyphens + lineEnd);

				// Responses from the server (code and
				// message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn
						.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": "
						+ serverResponseCode);

				

				BufferedReader in = new BufferedReader(
						new InputStreamReader(
								conn.getInputStream()));

				Log.d("BuffrerReader", "" + in);

				if (in != null) {

					response = convertStreamToString(in);
					Log.e("FINAL_RESPONSE-LENGTH", ""
							+ response.length());
					Log.e("FINAL_RESPONSE", response);
				}

			
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {

				ex.printStackTrace();

				Log.e("Upload file to server", "error: "
						+ ex.getMessage(), ex);
			} catch (Exception e) {

				e.printStackTrace();

				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
			}
		}
		return response;
    }
    
    public  String convertStreamToString(BufferedReader is)
			throws IOException {
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {

				while ((line = is.readLine()) != null) {
					sb.append(line).append("");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}
         
}
