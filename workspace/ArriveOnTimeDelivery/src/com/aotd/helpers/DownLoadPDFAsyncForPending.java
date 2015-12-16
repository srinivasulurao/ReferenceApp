package com.aotd.helpers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.aotd.activities.R;
import com.aotd.activities.PendingOrders.PdfHandler;




import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DownLoadPDFAsyncForPending extends
		AsyncTask<String, Void, String> {

	private static final String TAG = "DownLoadPDFAsync";

	private String mFileName;
	private FileOutputStream fout;
	private Context mContext;


	public static final String MIME_TYPE_PDF = "application/pdf";

	private ProgressDialog mProgressDialog;

	private PdfHandler mPdfHandler;
	
	public DownLoadPDFAsyncForPending(Context context, PdfHandler pdfHandler) {

		mContext = context;
		mProgressDialog = ProgressDialog.show(mContext,null,null);
		mProgressDialog.setContentView(R.layout.loader);
		mPdfHandler = pdfHandler;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog.show();
	}
	
	@Override
	protected String doInBackground(String... params) {

		try {
			String pdfUrl = params[0];
			String orderId = params[1];
			InputStream is = downloadPdf(pdfUrl);
			Log.i("","kkk download file in bytes "+is.available());
			if (is != null ) {
				setFileName(pdfUrl, orderId);
				writePdfFile(is, fout);
				return "Success";
			}

		} catch (Exception e) {
			Log.e("Error downloading pdf", e.toString());

			return null;
		}
		return null;

	}
	
	private void setFileName(String pdfUrl, String orderName) throws Exception {
		Log.i("", "kkk pdf url: "+pdfUrl);
		// set local filename to last part of URL
		try {
			mFileName = orderName + ".pdf";

			// String[] strURLParts = pdfUrl.split("/");
			// if (strURLParts.length > 0) {
			// mFileName = strURLParts[strURLParts.length - 1];
			// if (!(mFileName.contains(".pdf"))) {
			// mFileName = mFileName + ".pdf";
			// }
			// } else {
			// mFileName = orderName + ".pdf";
			// }

			String newFolder = "/AOTD";
			String extStorageDirectory = Environment
					.getExternalStorageDirectory().toString();
			File myNewFolder = new File(extStorageDirectory + newFolder);
			if (!myNewFolder.exists()) {
				myNewFolder.mkdir();
			}
			File file = new File(myNewFolder, mFileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			fout = new FileOutputStream(file);
		} catch (IOException e) {
			Log.e(TAG, "Problem in file creation." + "/n" + e.toString());
		}
	}
	
	private void writePdfFile(InputStream is, FileOutputStream fout)
			throws FileNotFoundException, IOException {
		// byte[] dataBuffer = new byte[4096];
		// int nRead = 0;
		// BufferedInputStream bufferedStreamInput = new
		// BufferedInputStream(is);
		// // must be world readable so external Intent can open!
		// @SuppressWarnings("deprecation")
		// FileOutputStream outputStream = mContext.openFileOutput(mFileName,
		// Context.MODE_WORLD_WRITEABLE);
		// while ((nRead = bufferedStreamInput.read(dataBuffer)) > 0) {
		// outputStream.write(dataBuffer, 0, nRead);
		// }
		// is.close();
		// outputStream.close();

		try {

			byte[] buffer = new byte[4096];
			int readLen = 0;
			BufferedInputStream bufferedStreamInput = new BufferedInputStream(
					is);
			while ((readLen = bufferedStreamInput.read(buffer)) > 0) {
				fout.write(buffer, 0, readLen);
			}
			fout.close();
		} catch (Exception e) {
			Log.e(TAG, "Unable to write data into file." + "/n" + e.toString());

		}

	}

	private InputStream downloadPdf(String pdfUrl) {
		Log.i("", "kkk download pdf url :"+pdfUrl);
		URL u;
		try {
			u = new URL(pdfUrl);
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();
			


			Log.i("", "kkk download pdf file respone code is :"+c.getResponseCode());
			Log.i("", "kkk download pdf file respone meassage is :"+c.getResponseMessage());

			if(c.getResponseCode() == 200){
				InputStream in = c.getInputStream();
				return in;
			}else{
				return null;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
@Override
protected void onPostExecute(String result) {
	super.onPostExecute(result);
	if (mProgressDialog != null) {
		mProgressDialog.dismiss();
	}
	if (result == null) {
		mPdfHandler.sendEmptyMessage(0);

	} else {
		mPdfHandler.sendEmptyMessage(1);

	}
}

}


