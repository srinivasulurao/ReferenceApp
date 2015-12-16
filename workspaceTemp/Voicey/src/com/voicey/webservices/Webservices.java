package com.voicey.webservices;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;

import com.voicey.model.AudioInfo;
import com.voicey.model.DownloadStatus;
import com.voicey.model.Friend;
import com.voicey.model.FriendShareAudio;
import com.voicey.model.InviteGroup;
import com.voicey.model.Remender;
import com.voicey.model.VoiceyReply;
import com.voicey.utils.Constants;

public class Webservices {

	String url = "http://voicey.me/web-services/";

	// http://voicey.me/web-services/audio.php
	FTPClient ftpClient = null;

	public String getUserId(String regId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "index.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?regId=" + regId + "&device_type=android"));

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

	public String saveUserName(String userCode, String userName, String userPhone, String userEmail) {
		String result = null;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		String response;
		String methodName = "updatename.php?";
		
		try {
			String convertedName = URLEncoder.encode(userName, "UTF-8");
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("user_id", userCode));
			pairs.add(new BasicNameValuePair("name", convertedName));
			pairs.add(new BasicNameValuePair("phone", userPhone));
			pairs.add(new BasicNameValuePair("email", userEmail));

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);

			result = EntityUtils.toString(httpResponse.getEntity());
			
			File file = new File(Constants.image_folder + "/"
					+ "profile_"+userCode+".png");

			if (file.exists()) {

				try {
					int serverResponseCode = 0;
					// open a URL connection to the Servlet
					FileInputStream fileInputStream = new FileInputStream(
							file);
					URL url = new URL(
							"http://voicey.me/web-services/profile_image.php?user_id="+userCode
									 );

					// Open a HTTP connection to the URL
					conn = (HttpURLConnection) url.openConnection();
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

		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	}


	public String getNotificationCount(String userCode) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "notifycounter.php";
		try {

			// http://voicey.me/web-services/updatename.php?user_id=&name=

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?to_user=" + userCode));

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

	public String getUserList(String userCode) {

		InputStream inputStream = null;
		String result = null;
		String methodName = "userlist.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?userid=" + userCode));

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

	public String getAudioList() {
		InputStream inputStream = null;
		String result = null;
		String methodName = "users-audio.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName));

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

	public String getFriendList(String userId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "friendlist.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?user_id=" + userId));

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
	
	public String deleteRemender(String timerId) {
		String result = null;
		String methodName = "timer.php?";
		try {

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("timer_id",timerId));
			
			// pairs.add(new
			// BasicNameValuePair("voiceid",friendShareAudio.getVoiceyId()));
			// pairs.add(new BasicNameValuePair("name",
			// friend.getFriendName()));

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	public String saveRemender(Remender remender) {
		String result = null;
		String methodName = "timer.php?";
		try {

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("user_id", remender.getUserId()));
			pairs.add(new BasicNameValuePair("shared_id", remender.getShareId()));
			pairs.add(new BasicNameValuePair("alert_time", remender
					.getAlertTime()));
			// pairs.add(new
			// BasicNameValuePair("voiceid",friendShareAudio.getVoiceyId()));
			// pairs.add(new BasicNameValuePair("name",
			// friend.getFriendName()));

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	public String updateTodo(String sharedId) {
		String result = null;
		String methodName = "shared_todo.php?";
		try {

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();

			pairs.add(new BasicNameValuePair("shared_id", sharedId));

			// pairs.add(new
			// BasicNameValuePair("voiceid",friendShareAudio.getVoiceyId()));
			// pairs.add(new BasicNameValuePair("name",
			// friend.getFriendName()));

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	public String saveComments(String comment, String sharedId) {
		String result = null;
		String methodName = "send_comment.php?";
		try {
			
			String convertcomments = URLEncoder.encode(comment,
					"UTF-8");

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("comment", convertcomments));
			pairs.add(new BasicNameValuePair("shared_id", sharedId));

			// pairs.add(new
			// BasicNameValuePair("voiceid",friendShareAudio.getVoiceyId()));
			// pairs.add(new BasicNameValuePair("name",
			// friend.getFriendName()));

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	public String getSentRequestList(String userId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "compareto.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?compareto=" + userId));

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

	public String getReceiveRequestList(String userId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "comparefrom.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?comparefrom=" + userId));

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

	public String getSelfAudioList(String userId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "users-audio.php";

		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?userid=" + userId));

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
	
	public String getFriendMessageList(String userId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "sharelist_group.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();
			String shareurl = url + methodName + "?userid=" + userId;
			
			// make GET request to the given URL
						HttpResponse httpResponse = httpclient
								.execute(new HttpGet(shareurl));

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
	
	
	public String getFriendMessageDetailList(String userId,String friendId,String groupId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "sharelist.php";
		try {

			// create HttpClient

			//
			
			StringBuffer shareurl= new StringBuffer();
			HttpClient httpclient = new DefaultHttpClient();
			shareurl.append(url + methodName + "?userid=" + userId);
			
			if(groupId!=null&&!groupId.equals("null")){
				
				shareurl.append("&user_group="+ groupId);
			}else{
			
			
			shareurl.append("&user_from="+friendId );
			}
			
			// make GET request to the given URL
						HttpResponse httpResponse = httpclient
								.execute(new HttpGet(shareurl.toString()));

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
			
			

	public String getShareAudioList(String userId, String type) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "sharelist.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();
			String shareurl = url + methodName + "?userid=" + userId;

			if (type.equals("todo")) {
				shareurl = shareurl + "&to_do=1";

			}
			
			if (type.equals("orderby")) {
				shareurl = shareurl + "&order_by=1";

			}
			// make GET request to the given URL
			HttpResponse httpResponse = httpclient
					.execute(new HttpGet(shareurl));

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

	public AudioInfo downloadVideo(AudioInfo audioInfo) {

		String PATH = Environment.getExternalStorageDirectory() + "/"
				+ Constants.app_folder + "/" + audioInfo.getVideoFilePath(); // put
																				// the
																				// downloaded
																				// file
																				// here

		// this is the downloader method
		try {
			URL url = new URL(Constants.audio_url
					+ audioInfo.getVideoFilePath());
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
			Log.d("ImageManager",
					"download ready in"
							+ ((System.currentTimeMillis() - startTime) / 1000)
							+ " sec");

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}

		return audioInfo;
	}

	public String getpreviousMessageList(String userId, String friendId,
			String sharedId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "shared_info.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?sender_id=" + userId + "&receiver_id="
					+ friendId + "&shared_id=" + sharedId));

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

	public String getSelfShareAudioList(String userId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "selfsharedlist.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?userid=" + userId));

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

	public String getSortAudioList(String sortValue) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "sortbyid.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?sortvalue=" + sortValue));

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

	public String shareFriendAudio(FriendShareAudio friendShareAudio) {
		String result = null;
		String methodName = "sharefile.php?";

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("shared_from", friendShareAudio
				.getUserCode()));
		pairs.add(new BasicNameValuePair("shared_to", friendShareAudio
				.getFriendlistStr()));
		pairs.add(new BasicNameValuePair("voiceid", friendShareAudio
				.getVoiceyId()));
		pairs.add(new BasicNameValuePair("forward_user", friendShareAudio
				.getForwardUser()));
		pairs.add(new BasicNameValuePair("gid",friendShareAudio.getSharedGroupCode()));
		// pairs.add(new BasicNameValuePair("name", friend.getFriendName()));

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	}

	public String getCategoryList() {
		InputStream inputStream = null;
		String result = null;
		String methodName = "category.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName));

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

	public String getSortSelfAudioList(String sortValue, String userId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "sortbyid.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?sortvalue=" + sortValue + "&userid="
					+ userId));

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

	public String AudioCountInc(String audioId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "incrementaudio.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?votid=" + audioId));

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

	public String voiceyDelete(String audioId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "deletevoice.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?votid=" + audioId));

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

	public String shareDelete(String shareId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "delete_shared_file.php";
		try {

			// create HttpClient

			//
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "?shared_id=" + shareId));

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
	
	public String userMessageDelete(String sharedFrom,String sharedTo,String isTodoDelete,String isdeleteLastMeg,String groupId) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "delete_inbox_msgsb2.php?";
		
		
		try {
			
			StringBuffer urlBuff=new StringBuffer(); 
			
			if(groupId!=null){
			urlBuff.append(url
					+ methodName + "gid=" +groupId+"&shared_to="+sharedTo+"&to_do="+isTodoDelete+"&last_id="+isdeleteLastMeg);
			}else{
				urlBuff.append(url
						+ methodName + "shared_from=" + sharedFrom+"&shared_to="+sharedTo+"&to_do="+isTodoDelete+"&last_id="+isdeleteLastMeg);
				
			}
			
			HttpClient httpclient = new DefaultHttpClient();

			HttpResponse httpResponse = httpclient.execute(new HttpGet(urlBuff.toString()));

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
	
	public String allMessageDelete(String sharedTo,String isTodoDelete) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "delete_inbox_msgsall.php?";
		try {
			
			HttpClient httpclient = new DefaultHttpClient();

			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName + "shared_to="+sharedTo+"&to_do="+isTodoDelete));

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

	public DownloadStatus voiceyDownload(AudioInfo audioInfo) {

		DownloadStatus downloadStatus = new DownloadStatus();

		try {
			String audioUrl = Constants.audio_url + audioInfo.getFileName();
			URL url = new URL(audioUrl);

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			urlConnection.connect();

			File SDCardRoot = Environment.getExternalStorageDirectory();

			File file = new File(Environment.getExternalStorageDirectory()
					+ "/Voicey/" + audioInfo.getFileName());

			FileOutputStream fileOutput = new FileOutputStream(file);

			InputStream inputStream = urlConnection.getInputStream();

			int totalSize = urlConnection.getContentLength();

			int downloadedSize = 0;

			byte[] buffer = new byte[1024];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {

				fileOutput.write(buffer, 0, bufferLength);

				downloadedSize += bufferLength;

			}
			// close the output stream when done
			fileOutput.close();
			downloadStatus.setStatus("1");
			downloadStatus.setUrl(Environment.getExternalStorageDirectory()
					+ "/Voicey/" + audioInfo.getFileName());

			// catch some possible errors...
		} catch (Exception e) {
			e.printStackTrace();
			downloadStatus.setStatus("0");
		}

		return downloadStatus;
	}

	public String sendFriendRequest(Friend friend) {
		String result = null;
		String methodName = "sendrequests.php";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("from_user", friend.getUserId()));
		pairs.add(new BasicNameValuePair("to_user", friend.getFriendId()));
		// pairs.add(new BasicNameValuePair("name", friend.getFriendName()));

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	}

	public String editFriend(Friend friend) {
		String result = null;
		String methodName = "editfriend.php";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("user_id", friend.getUserId()));
		pairs.add(new BasicNameValuePair("added_friend", friend.getFriendId()));
		pairs.add(new BasicNameValuePair("friend_name", friend.getFriendName()));

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	}

	public String deleteFriend(Friend friend) {
		String result = null;
	//	String methodName = "deletefriend.php";
		String methodName = "banned_friend.php";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		/*pairs.add(new BasicNameValuePair("user_id", friend.getUserId()));
		pairs.add(new BasicNameValuePair("added_friend", friend.getFriendId()));*/
		
		
		pairs.add(new BasicNameValuePair("fid", friend.getBlockId()));

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	}

	public String acceptFriendRequest(Friend friend) {
		String result = null;
		String methodName = "acceptrequest.php";

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("user_id", friend.getUserId()));
		pairs.add(new BasicNameValuePair("added_friend", friend.getFriendId()));
		// pairs.add(new BasicNameValuePair("name", friend.getFriendName()));

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	}

	public String replayText(VoiceyReply voiceyReply) {
		String result = null;
		try {

			String converttitle = URLEncoder.encode(voiceyReply.getTitle(),
					"UTF-8");

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			String methodName = "quickshare.php";
			
			Calendar c = Calendar.getInstance();
			
			 long time_val = c.getTimeInMillis();
			 String formatted_date = (DateFormat.format("yyyy-MM-dd hh:mm:ss", time_val))
                     .toString();
		
			 
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("userid", voiceyReply.getUserid()));
			pairs.add(new BasicNameValuePair("title", converttitle));

			pairs.add(new BasicNameValuePair("public_control", voiceyReply
					.getPublic_control()));
			pairs.add(new BasicNameValuePair("shared_to", voiceyReply
					.getSharedFriendCode()));
			pairs.add(new BasicNameValuePair("shared_from", voiceyReply
					.getUsercode()));
			pairs.add(new BasicNameValuePair("user_control", voiceyReply
					.getUser_control()));
			pairs.add(new BasicNameValuePair("cat_type", voiceyReply.getType()));
			pairs.add(new BasicNameValuePair("category_id", "1"));
			pairs.add(new BasicNameValuePair("date_time",formatted_date));
			pairs.add(new BasicNameValuePair("reply_to",voiceyReply.getId()));
			pairs.add(new BasicNameValuePair("gid",voiceyReply.getSharedGroupCode()));

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

		}

		return result;
	}
	
	public String sendGroupInvite(InviteGroup inviteGroup) {
		String result = null;
		try {

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			String methodName = "suggest_friends.php";
			
			Calendar c = Calendar.getInstance();
			
			 long time_val = c.getTimeInMillis();
			 String formatted_date = (DateFormat.format("yyyy-MM-dd hh:mm:ss", time_val))
                     .toString();
		
			 
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("shared_from", inviteGroup.getSharedFrom()));
			pairs.add(new BasicNameValuePair("shared_to", inviteGroup.getSharedTo()));

			pairs.add(new BasicNameValuePair("gid", inviteGroup
					.getgId()));
		

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

		}

		return result;
	}
	
	
	public String forwardImage(VoiceyReply voiceyReply) {
		String result = null;
		try {

			String converttitle = URLEncoder.encode(voiceyReply.getTitle(),
					"UTF-8");

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			String methodName = "quickshare.php";
			
			Calendar c = Calendar.getInstance();
			
			 long time_val = c.getTimeInMillis();
			 String formatted_date = (DateFormat.format("yyyy-MM-dd hh:mm:ss", time_val))
                     .toString();
		
			 
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("userid", voiceyReply.getUserid()));
			pairs.add(new BasicNameValuePair("title", converttitle));

			pairs.add(new BasicNameValuePair("public_control", voiceyReply
					.getPublic_control()));
			pairs.add(new BasicNameValuePair("shared_to", voiceyReply
					.getSharedFriendCode()));
			pairs.add(new BasicNameValuePair("shared_from", voiceyReply
					.getUsercode()));
			pairs.add(new BasicNameValuePair("user_control", voiceyReply
					.getUser_control()));
			pairs.add(new BasicNameValuePair("cat_type", voiceyReply.getType()));
			pairs.add(new BasicNameValuePair("category_id", "1"));
			pairs.add(new BasicNameValuePair("image_name",voiceyReply.getImage_name()));
			pairs.add(new BasicNameValuePair("gid",voiceyReply.getSharedGroupCode()));
			
			pairs.add(new BasicNameValuePair("date_time",formatted_date));

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

		}

		return result;
	}
	
	public String forwardVideo(VoiceyReply voiceyReply) {
		String result = null;
		try {

			String converttitle = URLEncoder.encode(voiceyReply.getTitle(),
					"UTF-8");

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			String methodName = "quickshare.php";
			
			Calendar c = Calendar.getInstance();
			
			 long time_val = c.getTimeInMillis();
			 String formatted_date = (DateFormat.format("yyyy-MM-dd hh:mm:ss", time_val))
                     .toString();
		
			 
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("userid", voiceyReply.getUserid()));
			pairs.add(new BasicNameValuePair("title", converttitle));

			pairs.add(new BasicNameValuePair("public_control", voiceyReply
					.getPublic_control()));
			pairs.add(new BasicNameValuePair("shared_to", voiceyReply
					.getSharedFriendCode()));
			pairs.add(new BasicNameValuePair("shared_from", voiceyReply
					.getUsercode()));
			pairs.add(new BasicNameValuePair("user_control", voiceyReply
					.getUser_control()));
			pairs.add(new BasicNameValuePair("cat_type", voiceyReply.getType()));
			pairs.add(new BasicNameValuePair("category_id", "1"));
			pairs.add(new BasicNameValuePair("image_name",voiceyReply.getImage_name()));
			pairs.add(new BasicNameValuePair("image_name",voiceyReply.getVideo_name()));
			pairs.add(new BasicNameValuePair("date_time",formatted_date));
			pairs.add(new BasicNameValuePair("gid",voiceyReply.getSharedGroupCode()));

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

		}

		return result;
	}

	public String replayImage(VoiceyReply voiceyReply) {
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
		String response;
		String converttitle = null;

		try {
			if(voiceyReply.getTitle()!=null){
			converttitle = URLEncoder.encode(voiceyReply.getTitle(),
					"UTF-8");
			}
			Calendar c = Calendar.getInstance();
			 long time_val = c.getTimeInMillis();
			 String formatted_date = (DateFormat.format("yyyy-MM-dd hh:mm:ss", time_val))
                     .toString();
			

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			String methodName = "quickshare.php";
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("userid", voiceyReply.getUserid()));
			pairs.add(new BasicNameValuePair("title", converttitle));

			pairs.add(new BasicNameValuePair("public_control", voiceyReply
					.getPublic_control()));
			pairs.add(new BasicNameValuePair("shared_to", voiceyReply
					.getSharedFriendCode()));
			pairs.add(new BasicNameValuePair("shared_from", voiceyReply
					.getUsercode()));
			pairs.add(new BasicNameValuePair("user_control", voiceyReply
					.getUser_control()));
			pairs.add(new BasicNameValuePair("cat_type", voiceyReply.getType()));
			pairs.add(new BasicNameValuePair("category_id", "1"));
			 pairs.add(new BasicNameValuePair("date_time",formatted_date));
				pairs.add(new BasicNameValuePair("reply_to",voiceyReply.getId()));
				pairs.add(new BasicNameValuePair("gid",voiceyReply.getSharedGroupCode()));
			 if (voiceyReply.getVideo_name() != null) {
				 
				 pairs.add(new BasicNameValuePair("video_upload","1"));
			 }

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

			if (result != null && result.length() > 0) {
				JSONObject jObj = new JSONObject(result);
				String status = jObj.getString("status");
				if (status.equals("1")) {

					String voiceId = jObj.getString("voiceid");
					String sharedTo=  jObj.getString("shared_to");
					String sharedFrom =  jObj.getString("shared_from");
					File file = new File(Constants.image_folder + "/"
							+ voiceyReply.getImage_name() + ".jpeg");

					if (file.exists()) {

						try {
							int serverResponseCode = 0;
							// open a URL connection to the Servlet
							FileInputStream fileInputStream = new FileInputStream(
									file);
							URL url = new URL(
									"http://voicey.me/web-services/imageuptest.php?voiceid="
											+ voiceId );

							// Open a HTTP connection to the URL
							conn = (HttpURLConnection) url.openConnection();
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
					
					if (voiceyReply.getVideo_name() != null) {

						File videoFile = new File(voiceyReply.getVideo_name());

						if (videoFile.exists()) {

							try {
								int serverResponseCode = 0;
								// open a URL connection to the Servlet
								FileInputStream fileInputStream = new FileInputStream(
										videoFile);
								URL url = new URL(
										"http://voicey.me/web-services/video_upload.php?voiceid="
												+ voiceId + "&shared_to="+sharedTo+"&shared_from="+sharedFrom);

								// Open a HTTP connection to the URL
								conn = (HttpURLConnection) url.openConnection();
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
										videoFile.toString());

								dos = new DataOutputStream(
										conn.getOutputStream());

								dos.writeBytes(twoHyphens + boundary + lineEnd);
								dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
										+ videoFile + "\"" + lineEnd);

								dos.writeBytes(lineEnd);

								// create a buffer of maximum size
								bytesAvailable = fileInputStream.available();

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
									bytesRead = fileInputStream.read(buffer, 0,
											bufferSize);

								}

								// send multipart form data necesssary
								// after
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

								Log.e("Upload file to server",
										"error: " + ex.getMessage(), ex);
							} catch (Exception e) {

								e.printStackTrace();

								Log.e("Upload file to server Exception",
										"Exception : " + e.getMessage(), e);
							}

						}

					}
				}
			}
		} catch (Exception e) {

			e.printStackTrace();

		}

		return result;
	}

	public String saveQuickSaveAuto(AudioInfo audioInfo) {
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
		/*
		 * ArrayList<NameValuePair> nameValuePairs = new
		 * ArrayList<NameValuePair>(); String methodName = "quickshare.php";
		 * List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		 * pairs.add(new BasicNameValuePair("userid", audioInfo.getUserid()));
		 * pairs.add(new BasicNameValuePair("title", audioInfo.getTitle()));
		 * pairs.add(new BasicNameValuePair("source", audioInfo.getSource()));
		 * pairs.add(new BasicNameValuePair("public_control", audioInfo
		 * .getPublic_control())); pairs.add(new BasicNameValuePair("shared_to",
		 * audioInfo.getFriendStr())); pairs.add(new
		 * BasicNameValuePair("shared_from", audioInfo .getUser_code()));
		 * pairs.add(new BasicNameValuePair("user_control", audioInfo
		 * .getUser_control())); pairs.add(new BasicNameValuePair("cat_type",
		 * audioInfo.getType())); if (audioInfo.getType().equals("classifield"))
		 * { pairs.add(new BasicNameValuePair("category_id", audioInfo
		 * .getCategoryId())); pairs.add(new BasicNameValuePair("your_ad",
		 * audioInfo.getYourAd())); } try {
		 * 
		 * DefaultHttpClient httpClient = new DefaultHttpClient(); HttpPost
		 * httpPost = new HttpPost(url + methodName); httpPost.setEntity(new
		 * UrlEncodedFormEntity(pairs));
		 * 
		 * HttpResponse httpResponse = httpClient.execute(httpPost); HttpEntity
		 * httpEntity = httpResponse.getEntity();
		 * 
		 * result = EntityUtils.toString(httpResponse.getEntity());
		 * 
		 * if (result != null && result.length() > 0) { JSONObject jObj = new
		 * JSONObject(result); String status = jObj.getString("status"); if
		 * (status.equals("1")) {
		 * 
		 * String voiceId = jObj.getString("voiceid");
		 */
		
		String voiceId = null;
		try {
			File name = new File(Environment.getExternalStorageDirectory()
					+ "/Voicey/" + audioInfo.getFileName() + ".3gp");

			if (name.exists()) {

				try {
					
					

					int serverResponseCode = 0;
					// open a URL connection to the Servlet
					FileInputStream fileInputStream = new FileInputStream(name);
					
					Calendar c = Calendar.getInstance();
					
					 long time_val = c.getTimeInMillis();
					 String formatted_date = (DateFormat.format("yyyy-MM-dd hh:mm:ss", time_val))
		                    .toString();
					 
					 String endate=URLEncoder.encode(formatted_date);
					String restUrl = URLEncoder.encode(audioInfo.getTitle());
					StringBuffer urlStr=new StringBuffer();
					urlStr.append("http://voicey.me/web-services/quick_share_audio.php?userid="
									+ audioInfo.getUserid() + "&title="
									+ restUrl + "&source="
									+ audioInfo.getSource()
									+ "&public_control="
									+ audioInfo.getPublic_control()
									+ "&shared_to=" + audioInfo.getFriendStr()
									+ "&shared_from="
									+ audioInfo.getUser_code()
									+ "&user_control="
									+ audioInfo.getUser_control()
									+ "&cat_type=" + audioInfo.getType()
									+ "&date_time=" + endate
									+ "&category_id="
									+ audioInfo.getCategoryId() );
					
					if(audioInfo.getId()!=null){
						urlStr.append("&reply_to="
									+ audioInfo.getId());
						
					}
					
					
					
					URL url = new URL(urlStr.toString());

					// Open a HTTP connection to the URL
					conn = (HttpURLConnection) url.openConnection();
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
					conn.setRequestProperty("uploaded_file", name.toString());

					dos = new DataOutputStream(conn.getOutputStream());

					dos.writeBytes(twoHyphens + boundary + lineEnd);
					dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
							+ name + "\"" + lineEnd);

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

					dos.writeBytes(lineEnd);
					dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

					serverResponseCode = conn.getResponseCode();
					String serverResponseMessage = conn.getResponseMessage();

					Log.i("uploadFile", "HTTP Response is : "
							+ serverResponseMessage + ": " + serverResponseCode);

					BufferedReader in = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));

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

					Log.e("Upload file to server", "error: " + ex.getMessage(),
							ex);
				} catch (Exception e) {

					e.printStackTrace();

					Log.e("Upload file to server Exception",
							"Exception : " + e.getMessage(), e);
				}
			}

			if (response != null && response.length() > 0) {
				JSONObject jObj = new JSONObject(response);
				String status = jObj.getString("status");
				if (status.equals("1")) {

					voiceId = jObj.getString("voiceid");
				}
			}

			if (audioInfo.getType().equals("classifield")) {

				File file = new File(Constants.image_folder + "/"
						+ audioInfo.getImageName() + ".jpeg");

				if (file.exists()) {

					try {

						int serverResponseCode = 0;
						// open a URL connection to the Servlet
						FileInputStream fileInputStream = new FileInputStream(
								file);
						URL url = new URL(
								"http://voicey.me/web-services/imageuptest.php?voiceid="
										+ voiceId + "");

						// Open a HTTP connection to the URL
						conn = (HttpURLConnection) url.openConnection();
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

						bufferSize = Math.min(bytesAvailable, maxBufferSize);
						buffer = new byte[bufferSize];

						// read file and write it into form...
						bytesRead = fileInputStream.read(buffer, 0, bufferSize);

						while (bytesRead > 0) {

							dos.write(buffer, 0, bufferSize);
							bytesAvailable = fileInputStream.available();
							bufferSize = Math
									.min(bytesAvailable, maxBufferSize);
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

						Log.e("Upload file to server Exception", "Exception : "
								+ e.getMessage(), e);
					}

				}

			}

			if (audioInfo.getVideoFilePath() != null) {

				File videoFile = new File(audioInfo.getVideoFilePath());

				if (videoFile.exists()) {

					try {
						int serverResponseCode = 0;
						// open a URL connection to the Servlet
						FileInputStream fileInputStream = new FileInputStream(
								videoFile);
						URL url = new URL(
								"http://voicey.me/web-services/video_upload.php?voiceid="
										+ voiceId + "");

						// Open a HTTP connection to the URL
						conn = (HttpURLConnection) url.openConnection();
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
								videoFile.toString());

						dos = new DataOutputStream(conn.getOutputStream());

						dos.writeBytes(twoHyphens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
								+ videoFile + "\"" + lineEnd);

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
							bufferSize = Math
									.min(bytesAvailable, maxBufferSize);
							bytesRead = fileInputStream.read(buffer, 0,
									bufferSize);

						}

						// send multipart form data necesssary
						// after
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

						Log.e("Upload file to server Exception", "Exception : "
								+ e.getMessage(), e);
					}

				}

			}

			// }

			// }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public String saveQuickSaveAudio(AudioInfo audioInfo) {
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
		String response;

		try {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			String methodName = "quickshare.php";
			String converttitle = URLEncoder.encode(audioInfo.getTitle(),
					"UTF-8");
			
			Calendar c = Calendar.getInstance();
			
			 long time_val = c.getTimeInMillis();
			 String formatted_date = (DateFormat.format("yyyy-MM-dd hh:mm:ss", time_val))
                    .toString();
			 

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("userid", audioInfo.getUserid()));
			pairs.add(new BasicNameValuePair("title", converttitle));
			pairs.add(new BasicNameValuePair("source", audioInfo.getSource()));
			pairs.add(new BasicNameValuePair("public_control", audioInfo
					.getPublic_control()));
			pairs.add(new BasicNameValuePair("shared_to", audioInfo
					.getFriendStr()));
			pairs.add(new BasicNameValuePair("shared_from", audioInfo
					.getUser_code()));
			pairs.add(new BasicNameValuePair("user_control", audioInfo
					.getUser_control()));
			pairs.add(new BasicNameValuePair("cat_type", audioInfo.getType()));
			pairs.add(new BasicNameValuePair("date_time",formatted_date));
			if (audioInfo.getType().equals("classifield")) {
				pairs.add(new BasicNameValuePair("category_id", audioInfo
						.getCategoryId()));
				pairs.add(new BasicNameValuePair("your_ad", audioInfo
						.getYourAd()));
			}

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

			if (result != null && result.length() > 0) {
				JSONObject jObj = new JSONObject(result);
				String status = jObj.getString("status");
				if (status.equals("1")) {

					String voiceId = jObj.getString("voiceid");

					File name = new File(
							Environment.getExternalStorageDirectory()
									+ "/Voicey/" + audioInfo.getTitle()
									+ ".3gp");

					if (name.exists()) {

						try {
							int serverResponseCode = 0;
							// open a URL connection to the Servlet
							FileInputStream fileInputStream = new FileInputStream(
									name);
							URL url = new URL(
									"http://voicey.me/web-services/audio-file-upload.php?voiceid="
											+ voiceId + "");

							// Open a HTTP connection to the URL
							conn = (HttpURLConnection) url.openConnection();
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
									name.toString());

							dos = new DataOutputStream(conn.getOutputStream());

							dos.writeBytes(twoHyphens + boundary + lineEnd);
							dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
									+ name + "\"" + lineEnd);

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

							dos.writeBytes(lineEnd);
							dos.writeBytes(twoHyphens + boundary + twoHyphens
									+ lineEnd);

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

					if (audioInfo.getType().equals("classifield")) {

						File file = new File(Constants.image_folder + "/"
								+ audioInfo.getTitle() + ".jpeg");

						if (file.exists()) {

							try {
								int serverResponseCode = 0;
								// open a URL connection to the Servlet
								FileInputStream fileInputStream = new FileInputStream(
										file);
								URL url = new URL(
										"http://voicey.me/web-services/imageuptest.php?voiceid="
												+ voiceId + "");

								// Open a HTTP connection to the URL
								conn = (HttpURLConnection) url.openConnection();
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
										file.toString());

								dos = new DataOutputStream(
										conn.getOutputStream());

								dos.writeBytes(twoHyphens + boundary + lineEnd);
								dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
										+ file + "\"" + lineEnd);

								dos.writeBytes(lineEnd);

								// create a buffer of maximum size
								bytesAvailable = fileInputStream.available();

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
									bytesRead = fileInputStream.read(buffer, 0,
											bufferSize);

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

								Log.e("Upload file to server",
										"error: " + ex.getMessage(), ex);
							} catch (Exception e) {

								e.printStackTrace();

								Log.e("Upload file to server Exception",
										"Exception : " + e.getMessage(), e);
							}

						}

					}

					if (audioInfo.getVideoFilePath() != null) {

						File videoFile = new File(audioInfo.getVideoFilePath());

						if (videoFile.exists()) {

							try {
								int serverResponseCode = 0;
								// open a URL connection to the Servlet
								FileInputStream fileInputStream = new FileInputStream(
										videoFile);
								URL url = new URL(
										"http://voicey.me/web-services/video_upload.php?voiceid="
												+ voiceId + "");

								// Open a HTTP connection to the URL
								conn = (HttpURLConnection) url.openConnection();
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
										videoFile.toString());

								dos = new DataOutputStream(
										conn.getOutputStream());

								dos.writeBytes(twoHyphens + boundary + lineEnd);
								dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
										+ videoFile + "\"" + lineEnd);

								dos.writeBytes(lineEnd);

								// create a buffer of maximum size
								bytesAvailable = fileInputStream.available();

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
									bytesRead = fileInputStream.read(buffer, 0,
											bufferSize);

								}

								// send multipart form data necesssary
								// after
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

								Log.e("Upload file to server",
										"error: " + ex.getMessage(), ex);
							} catch (Exception e) {

								e.printStackTrace();

								Log.e("Upload file to server Exception",
										"Exception : " + e.getMessage(), e);
							}

						}

					}

				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public String saveAudio(AudioInfo audioInfo) {
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
		String response;
		try {

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			String converttitle = URLEncoder.encode(audioInfo.getTitle(),
					"UTF-8");
			Calendar c = Calendar.getInstance();
			
			 long time_val = c.getTimeInMillis();
			 String formatted_date = (DateFormat.format("yyyy-MM-dd hh:mm:ss", time_val))
                    .toString();
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("userid", audioInfo.getUserid()));
			pairs.add(new BasicNameValuePair("title", converttitle));
			pairs.add(new BasicNameValuePair("source", audioInfo.getSource()));
			pairs.add(new BasicNameValuePair("public_control", audioInfo
					.getPublic_control()));
			pairs.add(new BasicNameValuePair("user_control", audioInfo
					.getUser_control()));
			pairs.add(new BasicNameValuePair("cat_type", audioInfo.getType()));
			 pairs.add(new BasicNameValuePair("date_time",formatted_date));
			if (audioInfo.getType().equals("classifield")) {

				if (audioInfo.getImagebitmap() != null) {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					audioInfo.getImagebitmap().compress(
							Bitmap.CompressFormat.PNG, 100,
							byteArrayOutputStream);
					byte[] byteArray = byteArrayOutputStream.toByteArray();

					imageBase64 = Base64.encodeToString(byteArray,
							Base64.DEFAULT);
				}

				pairs.add(new BasicNameValuePair("category_id", audioInfo
						.getCategoryId()));
				pairs.add(new BasicNameValuePair("your_ad", audioInfo
						.getYourAd()));
				// pairs.add(new BasicNameValuePair("image", imageBase64));

			}

			try {

				DefaultHttpClient httpClient = new DefaultHttpClient();
				// HttpPost httpPost = new
				// HttpPost(Constants.baseUrl+"?page=training_course");

				HttpPost httpPost = new HttpPost(
						"http://voicey.me/web-services/audio.php");
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();

				result = EntityUtils.toString(httpResponse.getEntity());
				if (result != null && result.length() > 0) {
					JSONObject jObj = new JSONObject(result);
					String status = jObj.getString("status");
					if (status.equals("1")) {

						String voiceId = jObj.getString("voiceid");

						File name = new File(
								Environment.getExternalStorageDirectory()
										+ "/Voicey/" + audioInfo.getTitle()
										+ ".3gp");

						if (name.exists()) {

							try {
								int serverResponseCode = 0;
								// open a URL connection to the Servlet
								FileInputStream fileInputStream = new FileInputStream(
										name);
								URL url = new URL(
										"http://voicey.me/web-services/audio-file-upload.php?voiceid="
												+ voiceId + "");

								// Open a HTTP connection to the URL
								conn = (HttpURLConnection) url.openConnection();
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

								dos.writeBytes(twoHyphens + boundary + lineEnd);
								dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
										+ name + "\"" + lineEnd);

								dos.writeBytes(lineEnd);

								// create a buffer of maximum size
								bytesAvailable = fileInputStream.available();

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
									bytesRead = fileInputStream.read(buffer, 0,
											bufferSize);

								}

								dos.writeBytes(lineEnd);
								dos.writeBytes(twoHyphens + boundary
										+ twoHyphens + lineEnd);

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

								Log.e("Upload file to server",
										"error: " + ex.getMessage(), ex);
							} catch (Exception e) {

								e.printStackTrace();

								Log.e("Upload file to server Exception",
										"Exception : " + e.getMessage(), e);
							}
						}

						if (audioInfo.getType().equals("classifield")) {

							File file = new File(Constants.image_folder + "/"
									+ audioInfo.getTitle() + ".jpeg");

							if (file.exists()) {

								try {
									int serverResponseCode = 0;
									// open a URL connection to the Servlet
									FileInputStream fileInputStream = new FileInputStream(
											file);
									URL url = new URL(
											"http://voicey.me/web-services/imageuptest.php?voiceid="
													+ voiceId + "");

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
											file.toString());

									dos = new DataOutputStream(
											conn.getOutputStream());

									dos.writeBytes(twoHyphens + boundary
											+ lineEnd);
									dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
											+ file + "\"" + lineEnd);

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

							if (audioInfo.getVideoFilePath() != null) {

								File videoFile = new File(
										audioInfo.getVideoFilePath());

								if (videoFile.exists()) {

									try {
										int serverResponseCode = 0;
										// open a URL connection to the Servlet
										FileInputStream fileInputStream = new FileInputStream(
												videoFile);
										URL url = new URL(
												"http://voicey.me/web-services/video_upload.php?voiceid="
														+ voiceId + "");

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
										conn.setRequestProperty(
												"uploaded_file",
												videoFile.toString());

										dos = new DataOutputStream(
												conn.getOutputStream());

										dos.writeBytes(twoHyphens + boundary
												+ lineEnd);
										dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\""
												+ videoFile + "\"" + lineEnd);

										dos.writeBytes(lineEnd);

										// create a buffer of maximum size
										bytesAvailable = fileInputStream
												.available();

										bufferSize = Math.min(bytesAvailable,
												maxBufferSize);
										buffer = new byte[bufferSize];

										// read file and write it into form...
										bytesRead = fileInputStream.read(
												buffer, 0, bufferSize);

										while (bytesRead > 0) {

											dos.write(buffer, 0, bufferSize);
											bytesAvailable = fileInputStream
													.available();
											bufferSize = Math.min(
													bytesAvailable,
													maxBufferSize);
											bytesRead = fileInputStream.read(
													buffer, 0, bufferSize);

										}

										// send multipart form data necesssary
										// after
										// file data...
										dos.writeBytes(lineEnd);
										dos.writeBytes(twoHyphens + boundary
												+ twoHyphens + lineEnd);

										// Responses from the server (code and
										// message)
										serverResponseCode = conn
												.getResponseCode();
										String serverResponseMessage = conn
												.getResponseMessage();

										Log.i("uploadFile",
												"HTTP Response is : "
														+ serverResponseMessage
														+ ": "
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

										Log.e("Upload file to server",
												"error: " + ex.getMessage(), ex);
									} catch (Exception e) {

										e.printStackTrace();

										Log.e("Upload file to server Exception",
												"Exception : " + e.getMessage(),
												e);
									}

								}

							}

						}

					}

				}

				// is = httpEntity.getContent();

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
	public String creatgroup(String userCode, String gName, String friend) {
		String result = null;
		String methodName = "user_group.php?";
		try {
//			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("super_user", userCode));
			pairs.add(new BasicNameValuePair("group_name", gName));
			pairs.add(new BasicNameValuePair("username", friend));	


			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Group Creation Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	
	}
	
	public String getgroupList(String usercode) {
		InputStream inputStream = null;
		String result = null;
		String methodName = "group_name_list.php";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName+"?super_user=" + usercode));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null){
				result = convertInputStreamToString(inputStream);
				System.out.println(result);
			}
			else {

			}

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	
	public String deleteGroup(Friend friend) {
		String result = null;
		String methodName = "delete_group.php";

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("del_gid", friend.getGroupID()));

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	}

	public String getGroupUserList(String ID) {

		InputStream inputStream = null;
		String result = null;
		String methodName = "group_name_list.php";

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("gid", ID));
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;

	}
	
	public String getInviteFriendList(String userCode,String groupId) {

		InputStream inputStream = null;
		String result = null;
		String methodName = "friendlist.php";

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("user_id", userCode));
		pairs.add(new BasicNameValuePair("gid", groupId));
		
		
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
		
		

	}
	
	public String getInviteUserList(String userCode,String groupId) {

		InputStream inputStream = null;
		String result = null;
		String methodName = "friendlist.php?";

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("user_id", userCode));
		pairs.add(new BasicNameValuePair("gid", groupId));
		pairs.add(new BasicNameValuePair("users", "1"));
		
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
		
		

	}
	
	
	public String editGroup(Friend friend) {
		String result = null;
		String methodName = "group_name_list.php";
		try {
		
		String convertGroupName= URLEncoder.encode(friend.getGroupName(),
				"UTF-8");


		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("edit_gid", friend.getGroupID()));
		pairs.add(new BasicNameValuePair("group_name", convertGroupName));

		

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	}
	
	public String addFriendsAuto(String ME, String My_frd) {
		String result = null;
		String methodName = "contacts_friend.php";
		try {

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("user_id", ME));
			pairs.add(new BasicNameValuePair("added_friend", My_frd));


			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {

			e.printStackTrace();

			Log.e("AUTO FRIENDS",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	
	}
	
	public String RemoveGroupMember(Friend friend) {
		String result = null;
		String methodName = "delete_group.php";

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("del_gid", friend.getGroupID()));
		pairs.add(new BasicNameValuePair("del_uid", friend.getUserId()));

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			// HttpPost httpPost = new
			// HttpPost(Constants.baseUrl+"?page=training_course");

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());

		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	}


	public String addmember(String gName, String friend) {
		String result = null;
		String methodName = "user_group.php?";
		try {

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("gid", gName));
			pairs.add(new BasicNameValuePair("username", friend));	


			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Group Creation Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	
	}
	public String getAllUsers() {

		InputStream inputStream = null;
		String result = null;
		String methodName = "allusers.php";
		try {

			HttpClient httpclient = new DefaultHttpClient();

			HttpResponse httpResponse = httpclient.execute(new HttpGet(url
					+ methodName));
			inputStream = httpResponse.getEntity().getContent();

			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			System.out.println("ALL_USERS: "+e.toString());
		}

		return result;

	}
	
	public String addInvitemember(AudioInfo audioInfo) {
		String result = null;
		String methodName = "user_group.php?";
		try {

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("gid", audioInfo.getGroupId()));
			pairs.add(new BasicNameValuePair("username", audioInfo.getGroupAdd()));	
			pairs.add(new BasicNameValuePair("vid", audioInfo.getId()));	


			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url + methodName);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();

			result = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {

			e.printStackTrace();

			Log.e("Group Creation Exception",
					"Exception : " + e.getMessage(), e);
		}

		return result;
	
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
	
	

}
