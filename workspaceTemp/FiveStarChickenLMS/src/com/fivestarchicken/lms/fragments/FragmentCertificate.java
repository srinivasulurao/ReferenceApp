package com.fivestarchicken.lms.fragments;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.adapter.AdapterCertificate;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.model.Certificate;
import com.fivestarchicken.lms.utils.Commons;

public class FragmentCertificate extends Fragment {
	
	ListView lvCertificate;
	AdapterCertificate adapterCertificate;
	List<Certificate> certificateList = new ArrayList<Certificate>();
	private DbAdapter dh;
	String userId;
	SharedPreferences sharedPreferences;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_certificate, container, false);

		initilizeUI(v);
		
		return v;
	}
	
	private void initilizeUI(View v) {

		try {
			
			lvCertificate = (ListView) v.findViewById(R.id.lvCertificate);

			this.dh = new DbAdapter(getActivity());
		
			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			userId = sharedPreferences.getString("userId", null);
			
			certificateList=dh.getCertificateList(userId);
			
			adapterCertificate = new AdapterCertificate(getActivity(),
					R.layout.adapter_blog, certificateList);
			
			lvCertificate.setAdapter(adapterCertificate);
			 
			lvCertificate.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					
					Certificate certificate = (Certificate) parent.getItemAtPosition(position);	
					
					File file=new File(Commons.app_pdf_folder+"/"+certificate.getFileName());
					
					if(file.exists()){
								
						Uri path = Uri.fromFile(file);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(path, "application/pdf");
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						
					}else{
						if (Commons.isNetworkAvailable(getActivity())) {

							new AsyPdfDownloadFile().execute(certificate.getFileName());

						} else {

							Commons.internetErrorMessage(getActivity());
						}

						
					}
					
					
					
				}
			});
			
			
			
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	private class AsyPdfDownloadFile extends AsyncTask<String, String, Boolean> {
		 ProgressDialog progressDialog;
		 String fileName;
		 File file;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Downloading file...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(false);
			progressDialog.show();
			
		}

		@Override
		protected Boolean doInBackground(String... urls) {
			int index = 0;
			
				try {
					
					fileName=urls[0];
					
					URL url = new URL(Commons.app_certificate_path+urls[0]);
					//URL url = new URL("http://www.hdwallpapers.in/walls/crysis_hd_1080p-HD.jpg");
					 file = new File(Commons.app_pdf_folder+urls[0]);
					
					long startTime = System.currentTimeMillis();
					Log.d("ImageManager", "download begining");
					Log.d("ImageManager", "download url:" + url);
				
					/* Open a connection to that URL. */
					URLConnection ucon = url.openConnection();
					ucon.connect();
					
					int lengthofFile = ucon.getContentLength();

					/*
					 * Define InputStreams to read from the URLConnection.
					 */
					InputStream is = ucon.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);

					/*
					 * Read bytes to the Buffer until there is nothing more to
					 * read(-1).
					 */
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					int current = 0;
					long total = 0;
					while ((current = bis.read()) != -1) {
						baf.append((byte) current);
						total += current;
						publishProgress("" + (int) ((total * 100) / lengthofFile));
					}

					/* Convert the Bytes read to a String. */
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(baf.toByteArray());
					fos.close();
					/*
					 * Log.d("ImageManager", "download ready in" +
					 * ((System.currentTimeMillis() - startTime) / 1000) +
					 * " sec");
					 */

				} catch (IOException e) {
					Log.d("ImageManager", "Error: " + e);
					return false;
				}

				/*publishProgress(""
						+ (int) ((index * 100) ));
				// publishProgress("" );
*/
			

			return true;
		}

		protected void onProgressUpdate(String... progress) {

			progressDialog.setProgress(Integer.parseInt(progress[0]));

		}

		
		@Override
		protected void onPostExecute(Boolean audioInfo) {

			progressDialog.dismiss();
			
			Uri path = Uri.fromFile(file);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(path, "application/pdf");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		}
	}

}
