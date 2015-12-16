package com.fivestarchicken.lms.webservice;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

import com.fivestarchicken.lms.model.NewEmployee;
import com.fivestarchicken.lms.model.User;
import com.fivestarchicken.lms.utils.Commons;

public class Webservice {
	
	
	public static String url = "http://taskdynamo.com/lmsstage/app/";

	// http://voicey.me/web-services/audio.php
	FTPClient ftpClient = null;

	
	
	public String userLogin(String userName,String password) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "login_control?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "user_email=" + userName + "&user_pass="+password));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	
	public String managerLogin(String userName,String password) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "login_control/check_manager?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "user_email=" + userName + "&user_pass="+password));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	public String sendMessage(String userId,String message) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "exam_control/saveUserMessage?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "user_id=" + userId + "&message="+message));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	public String syncData(String branchId,String syncTime) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "exam_control/view_all_mocks_branch?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName+"branch_id=" +branchId+"&sync_date="+URLEncoder.encode(syncTime,
						"UTF-8")));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	
	
	
	public String getExamDetails(String userId,String langType,String starRate) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "exam_control/view_all_mocks?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "user_id=" + userId + "&lang_type="+langType+ "&star_rate="+starRate));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	public String sendExamDetail(String userId,String moduleId,String answerSelection,String languageType) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "exam_control/employeeresult?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "user_id=" + userId + "&module_id="+moduleId+ "&res="+answerSelection+ "&lang_type="+languageType));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	public String editProfile(User user) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "exam_control/editprofile?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "user_id=" + user.getUserId() + "&user_name="+user.getUserName()+ "&user_phone="+user.getPhone()+ "&user_email="+user.getEmail()));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	public String profileView(String userId,String langType,String syncTime) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "login_control/viewuser?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "user_id=" +userId + "&lang_type="+langType+ "&check_sync_time="+ URLEncoder.encode(syncTime,
							"UTF-8")));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	
	public String registerUser(NewEmployee newEmployee) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "login_control/viewuser?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName ));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	
	public String uploadProfilePic(User user){

		String result = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String imageBase64 = null;
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		String response = null;
		String converttitle = null;
		File file = null;
		file = new File(user.getImagePath());
		
		String methodName = "exam_control/changeprofilepic?";
		
		if (file.exists()) {

			try {
		int serverResponseCode = 0;
		// open a URL connection to the Servlet
		FileInputStream fileInputStream = new FileInputStream(
				file);
		URL uploadUrl = new URL(url+methodName+"user_id=" + user.getUserId());

		// Open a HTTP connection to the URL
		conn = (HttpURLConnection) uploadUrl.openConnection();
		conn.setDoInput(true); // Allow InputsFF
		conn.setDoOutput(true); // Allow Outputs
		conn.setUseCaches(false); // Don't use a
									// Cached
									// Copy
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("ENCTYPE",
				"multipart/form-data");
		conn.setRequestProperty("Content-Type",
				"multipart/form-data;boundary=" + boundary);
		conn.setRequestProperty("uploaded_file",
				file.toString());

		dos = new DataOutputStream(conn.getOutputStream());

		dos.writeBytes(twoHyphens + boundary + lineEnd);
		dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
				+ file + "\"" + lineEnd);

		dos.writeBytes(lineEnd);

		// create a buffer of maximum size
		bytesAvailable = fileInputStream.available();

		bufferSize = Math
				.min(bytesAvailable, maxBufferSize);
		buffer = new byte[bufferSize];

		// read file and write it into form...
		bytesRead = fileInputStream.read(buffer, 0,
				bufferSize);

		while (bytesRead > 0) {

			dos.write(buffer, 0, bufferSize);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable,
					maxBufferSize);
			bytesRead = fileInputStream.read(buffer, 0,
					bufferSize);

		}

		// send multipart form data necesssary after
		// file data...
		dos.writeBytes(lineEnd);
		dos.writeBytes(twoHyphens + boundary + twoHyphens
				+ lineEnd);

		// Responses from the server (code and
		// message)
		serverResponseCode = conn.getResponseCode();
		String serverResponseMessage = conn
				.getResponseMessage();

		Log.i("uploadFile", "HTTP Response is : "
				+ serverResponseMessage + ": "
				+ serverResponseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));

		Log.d("BuffrerReader", "" + in);

		if (in != null) {

			response = convertStreamToString(in);
			Log.e("FINAL_RESPONSE-LENGTH",
					"" + response.length());
			Log.e("FINAL_RESPONSE", response);
		}

		fileInputStream.close();
		dos.flush();
		dos.close();

	} catch (MalformedURLException ex) {

		ex.printStackTrace();

		Log.e("Upload file to server",
				"error: " + ex.getMessage(), ex);
	} catch (Exception e) {

		e.printStackTrace();

		Log.e("Upload file to server Exception",
				"Exception : " + e.getMessage(), e);
	}
		}
		
		return response;
	}
	
	
	public Boolean downloadProfilePic(String fileName) {

		String PATH = Environment.getExternalStorageDirectory() + "/"
				+ Commons.appFolder + "/" + fileName; 
		
		

		// this is the downloader method
		try {
			URL url = new URL(Commons.imagePath
					+ fileName);
			File file = new File(PATH);

			long startTime = System.currentTimeMillis();
			Log.d("ImageManager", "download begining");
			Log.d("ImageManager", "download url:" + url);
			Log.d("ImageManager", "downloaded file name:" + PATH);
			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();
			
			
			/*Log.d("ImageManager",
					"download ready in"
							+ ((System.currentTimeMillis() - startTime) / 1000)
							+ " sec");*/

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
			return false;
		}

		return true;
	}
	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}
	
	public String forgotPassword(String email) {

		// karuppasamy.g@omkarlabs.com

		InputStream inputStream = null;
		String result = null;
		String methodName = "exam_control/forgot_password_email?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "email_id=" + email));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	public String uploadProfilePicRegister(String candidateID,String imagePath) {

		String result = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String imageBase64 = null;
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		String response = null;
		String converttitle = null;
		File file = null;
		file = new File(imagePath);

		String methodName = "exam_control/saveInterviewUserProfileImage?";

		if (file.exists()) {

			try {
				int serverResponseCode = 0;
				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(file);
				URL uploadUrl = new URL(url + methodName + "candidate_id="
						+ candidateID);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) uploadUrl.openConnection();
				conn.setDoInput(true); // Allow InputsFF
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a
				// Cached
				// Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", file.toString());

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
						+ file + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
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

				// send multipart form data necesssary after
				// file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and
				// message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseCode);

				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));

				Log.d("BuffrerReader", "" + in);

				if (in != null) {

					response = convertStreamToString(in);
					Log.e("FINAL_RESPONSE-LENGTH", "" + response.length());
					Log.e("FINAL_RESPONSE", response);
				}

				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {

				ex.printStackTrace();

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {

				e.printStackTrace();

				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
			}
		}

		return response;
	}


	// code by TH
	public String changePassword(String userId, String oldpw, String newpw,
			String confrmpw) {

		// karuppasamy.g@omkarlabs.com

		InputStream inputStream = null;
		String result = null;
		String methodName = "exam_control/changepassword?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "user_id=" + userId + "&old_pass=" + oldpw
					+ "&new_pass=" + newpw + "&re_new_pass=" + confrmpw));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
 
 public String interviewRegitserAPI(String username, String email,
			String mobile, String branchid,String imagePath) {

		// karuppasamy.g@omkarlabs.com

		InputStream inputStream = null;
		String result = null;
		String methodName = "exam_control/saveInterviewuserdetails?";

		// http://taskdynamo.com/lmsstage/app/exam_control/saveInterviewuserdetails?username=test&email=bala@gmail.com&mobile=987456321&branchid=14&location=test&city=dfdsaf

		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "username=" + username + "&email=" + email
					+ "&mobile=" + mobile + "&branchid=" + branchid));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null){
				result = convertInputStreamToString(inputStream);
				
				JSONObject joresult = new JSONObject(result);
				
				String status = joresult.getString("status");

				if (status.equals("200")) {
					
					String candidateId = joresult.getString("candidate_id");
					
					uploadProfilePicRegister(candidateId,imagePath);
					
				}
				
			}
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	// code by TH
	public String interviewresultAPImeth(String candidateId, String testresult,String languageType) {

		// karuppasamy.g@omkarlabs.com

		InputStream inputStream = null;
		String result = null;
		String methodName = "exam_control/saveInterviewResult?";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "candidate_id=" + candidateId + "&res="
					+ testresult+"&lang_type="+languageType));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	

	
	public static String convertStreamToString(BufferedReader is)
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
