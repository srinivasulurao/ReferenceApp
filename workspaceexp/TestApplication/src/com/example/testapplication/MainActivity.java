package com.example.testapplication;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.testapplication.webservices.Constants;
import com.example.testapplication.webservices.Webservices;

public class MainActivity extends ActionBarActivity {

	Webservices Webservices;
	String backgroundURL, closeButtonURL, goButtonURL;
	RelativeLayout rlMain;
	ImageView ivGo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initilizeUI();
		Webservices = new Webservices();
		File f = new File(Constants.imageFolder);
		if (f.mkdir()) {
			System.out.println("Directory created");
		} else {
			System.out.println("Directory is not created");
		}
		if (isNetworkAvailable()) {

			new GetImageUrl().execute();

		} else {
			Toast.makeText(getBaseContext(), "Error in connection .",
					Toast.LENGTH_LONG).show();

		}

	}
	
	private void initilizeUI() {
		
		rlMain=(RelativeLayout)findViewById(R.id.rlmain);
		ivGo=(ImageView)findViewById(R.id.ivGo);
		
		ivGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i;
				i = new Intent(MainActivity.this, SecondActivity.class);
				
				startActivity(i);
				// TODO Auto-generated method stub
				
			}
		});
	}

	private class GetImageUrl extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getImageUrl();
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(MainActivity.this);

			dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			dialog.setContentView(R.layout.loading_layout);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			dialog.show();

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();

			try {
				if (result != null && result.length() > 0) {

					JSONObject resultJson = new JSONObject(result);
					;
					backgroundURL = resultJson.getString("backgroundURL");
					closeButtonURL = resultJson.getString("closeButtonURL");
					goButtonURL = resultJson.getString("goButtonURL");

					HashMap myMap = new HashMap<String, String>();
					myMap.put(Constants.backgroundImage, backgroundURL);
					myMap.put(Constants.closeButtonImage, closeButtonURL);
					myMap.put(Constants.goButtonImage, goButtonURL);

					new ImageDownloader().execute(myMap);

				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	class ImageDownloader extends AsyncTask<HashMap, Void, String> {
		
		Dialog dialog;

		protected String doInBackground(HashMap... urls) {
			
			publishProgress((Void[]) null);
			String result;
			HashMap<String, String> hashUrls = urls[0];
			try {
				Iterator it = hashUrls.entrySet().iterator();
				while (it.hasNext()) {

					HashMap.Entry pair = (HashMap.Entry) it.next();

					String Url = pair.getValue().toString();
					String image = pair.getKey().toString();
					Bitmap bmp = null;

					URL url = new URL(Url);
					bmp = BitmapFactory.decodeStream(url.openConnection()
							.getInputStream());

					File file = new File(Constants.imageFolder + "/" + image
							+ ".png");

					if (file.exists())
						file.delete();

					FileOutputStream out = new FileOutputStream(file);
					bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.flush();
					out.close();

					// ivimage.setImageBitmap(bmp);

				}
				result = "success";
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				result = "fail";
			}
			return result;
		}
		
		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(MainActivity.this);

			dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			dialog.setContentView(R.layout.loading_layout);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			dialog.show();

		}

		protected void onPostExecute(String result) {
			dialog.dismiss();

			if (result != null && result.equals("success")) {
				
				File file = new File(Constants.imageFolder+"/"+Constants.backgroundImage+".png");

				if (file.exists()){
				 String pathName = Constants.imageFolder+"/"+Constants.backgroundImage+".png";
			        Resources res = getResources();
			        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
			        BitmapDrawable bd = new BitmapDrawable(res, bitmap);
			       
			        rlMain.setBackgroundDrawable(bd);
				}
				
				File file2 = new File(Constants.imageFolder+"/"+Constants.goButtonImage+".png");

				if (file2.exists()){
				 String pathName = Constants.imageFolder+"/"+Constants.goButtonImage+".png";
			        Resources res = getResources();
			        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
			        BitmapDrawable bd = new BitmapDrawable(res, bitmap);
			       
			        ivGo.setBackgroundDrawable(bd);
				}
			        
			        
				// touchImage.setImageBitmap(result);
			}else {
				Toast.makeText(getBaseContext(), "Error while downloading .",
						Toast.LENGTH_LONG).show();

			}
		}
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivity = (ConnectivityManager) MainActivity.this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
