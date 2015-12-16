package com.aotd.activities;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aotd.utils.Utils;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class CameraActivity extends Activity {

	private static final int CAMERA_REQUEST = 1888; 
	private ImageView imageView;
	TextView txtOrderNum;

	String orderid="";
	String imagePath="";
	String serverResponseMessage="";
	

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 

		orderid = getIntent().getStringExtra("ORDERID");

		txtOrderNum = (TextView)findViewById(R.id.aotd_delivery_orderNum_btn_pup);
		txtOrderNum.setText(orderid);

		imageView = (ImageView)findViewById(R.id.imageView1);
		Button photoButton = (Button)findViewById(R.id.button1);
		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
				startActivityForResult(cameraIntent, CAMERA_REQUEST); 
			}
		});
		
		Button btn_finish = (Button)findViewById(R.id.finish);
		btn_finish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		
		Button btnUpload = (Button)findViewById(R.id.update);
		btnUpload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				final ProgressDialog pd = new ProgressDialog(CameraActivity.this);
				pd.setMessage("Uploading Image");
				pd.setIndeterminate(false);
				pd.setCancelable(false);
				pd.show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						
						
						uploadFile(imagePath);
						
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								
								pd.dismiss();
								
								if(serverResponseMessage.equals("OK"))
								{
									Toast.makeText(getApplicationContext(), "Image Uploaded Sucessfully", Toast.LENGTH_SHORT).show();
								}
								else
								{
									Toast.makeText(getApplicationContext(), "Image Upload Failed !!", Toast.LENGTH_SHORT).show();
								}
							}
						});
						
						
						
					}
				}).start();
				
			}
		});

	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (requestCode == CAMERA_REQUEST) {  
			Bitmap bitmap = (Bitmap) data.getExtras().get("data"); 
			imageView.setImageBitmap(bitmap);

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

			File fileDir = new File(Environment.getExternalStorageDirectory()
					+ "/AOTD");
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			imagePath = Environment.getExternalStorageDirectory() + "/AOTD/"
					+ orderid + ".jpg";
			File carmeraFile = new File(imagePath);

			File file = new File(imagePath);
			try {
				file.createNewFile();
				FileOutputStream fo = new FileOutputStream(file);

				fo.write(bytes.toByteArray());
				fo.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}

			
		}  
	} 


	public int uploadFile(String sourceFileUri)
	{
		String fileName = sourceFileUri;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;  
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024; 
		File sourceFile = new File(sourceFileUri); 

		try{
			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			URL url = new URL(Utils.IMAGE_URL);

			// Open a HTTP  connection to  the URL
			conn = (HttpURLConnection) url.openConnection(); 
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("uploaded_file", fileName); 

			dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd); 
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
					+ fileName + "\"" + lineEnd);

			dos.writeBytes(lineEnd);

			// create a buffer of  maximum size
			bytesAvailable = fileInputStream.available(); 

			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);  

			while (bytesRead > 0) {

				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);   

			}

			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// Responses from the server (code and message)
			int serverResponseCode = conn.getResponseCode();
			serverResponseMessage = conn.getResponseMessage();

			Log.i("uploadFile", "HTTP Response is : "
					+ serverResponseMessage + ": " + serverResponseCode);


		}catch(Exception e){
			String str = e.toString();

			String s="";

		}

		return 1;
	}

}
