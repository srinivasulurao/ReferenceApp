package com.example.example;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    TextView t1;
	Button b1;
	String selectedPath = "";
	static final int SELECT_AUDIO = 2;
	//FileInputStream fis;
	private String lineEnd = "\r\n";
	private String twoHyphens = "--";
	private String boundary = "*****";
	private int bytesRead, bytesAvailable,bufferSize;
	private byte[] buffer;
	private int maxBufferSize = 1*1024*1024;
	
	private Uri image_uri;  
	private String response;    
	private HttpURLConnection conn = null;
	private DataOutputStream dos = null;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		t1=(TextView)findViewById(R.id.textView1);
		b1=(Button)findViewById(R.id.button1);
		
		b1.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
		
		//shareImageWhatsApp() ;
				
				//new VoiceyDelete().execute();
				//setContactUrl();
				//setringtone();
				sharemms();
				//shareImageWhatsApp();
			}
		});
		// new saveAudioAsyncTask ().execute();
		
	}
	public void setringtone(){
		
		 try {
		String filepath =Environment.getExternalStorageDirectory() + "/Voicey/" + "75yarini" + ".3gp";
		File ringtoneFile = new File(filepath);
		if (ringtoneFile.exists()) {
			
			ContentValues content = new ContentValues();
			content.put(MediaStore.MediaColumns.DATA,ringtoneFile.getAbsolutePath());
			content.put(MediaStore.MediaColumns.TITLE, "test");
			content.put(MediaStore.MediaColumns.SIZE, 215454);
			content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
			content.put(MediaStore.Audio.Media.ARTIST, "artist");
			content.put(MediaStore.Audio.Media.DURATION, 230);
			content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
			content.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
			content.put(MediaStore.Audio.Media.IS_ALARM, false);
			content.put(MediaStore.Audio.Media.IS_MUSIC, false);
			
			
			
			Uri uri = MediaStore.Audio.Media.getContentUriForPath(
			ringtoneFile.getAbsolutePath());
			Uri newUri = this.getContentResolver().insert(uri, content);
			//ringtoneUri = newUri;
		
			RingtoneManager.setActualDefaultRingtoneUri(this,
			RingtoneManager.TYPE_RINGTONE,newUri);
			
		 } 
		 }catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	
	static public final int CONTACT_CHOOSER_ACTIVITY_CODE = 73729;
	static public final int CONTACT_SHARE_CODE = 5555;
	public void setContactUrl(){
		
		
		

		// start contact search activity within any method you like
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		startActivityForResult(intent, CONTACT_CHOOSER_ACTIVITY_CODE);
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    switch (requestCode) {
	        case (CONTACT_CHOOSER_ACTIVITY_CODE) :
	            if (resultCode == Activity.RESULT_OK) {

	                try{
	                	
	                	String filepath =Environment.getExternalStorageDirectory() + "/Voicey/" + "75yarini" + ".3gp";
	            		File ringtoneFile = new File(filepath);
	                    Uri contactData = data.getData();
	                    String contactId = contactData.getLastPathSegment();
	                    String[] PROJECTION = new String[] {
	                            ContactsContract.Contacts._ID,
	                            ContactsContract.Contacts.DISPLAY_NAME,
	                            ContactsContract.Contacts.HAS_PHONE_NUMBER,
	                    };
	                    Cursor localCursor =  getContentResolver().query(contactData, PROJECTION, null, null, null);
	                    localCursor.moveToFirst();
	                    //--> use moveToFirst instead of this:  localCursor.move(Integer.valueOf(contactId)); /*CONTACT ID NUMBER*/

	                    String contactID = localCursor.getString(localCursor.getColumnIndexOrThrow("_id"));
	                    String contactDisplayName = localCursor.getString(localCursor.getColumnIndexOrThrow("display_name"));

	                    Uri localUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactID);
	                    localCursor.close();
	                    ContentValues localContentValues = new ContentValues();

	                    localContentValues.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
	                    localContentValues.put(ContactsContract.Data.CUSTOM_RINGTONE, ringtoneFile.getAbsolutePath());
	                    getContentResolver().update(localUri, localContentValues, null, null);

	                    Toast.makeText(this, "Ringtone assigned to: " + contactDisplayName, Toast.LENGTH_LONG);

	                } catch(Exception ex){
	                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
	                }
	            }
	            break;
	            
	        case (CONTACT_SHARE_CODE) :
	        	
	        	Intent share = new Intent(Intent.ACTION_SEND);
			// share.setType("image/jpeg");
			share.setType("audio/3gp");
	
			/*
			 * share.putExtra(Intent.EXTRA_STREAM, Uri.parse(
			 * Environment.getExternalStorageDirectory()+
			 * File.separator+"temporary_file.jpg"));
			 */
			share.putExtra(Intent.EXTRA_TEXT, "picture_text");
			share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + "/Voicey/" + "75yarini" + ".3gp"));
			share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			if (isPackageInstalled("com.whatsapp", this)) {
				share.setPackage("com.whatsapp");
				startActivity(Intent.createChooser(share,
						"Share Video"));

			} else {

				Toast.makeText(this, "Please Install Whatsapp",
						Toast.LENGTH_LONG).show();
			}
		
	        	
	        	  break;
	    }

	}
	public void sharemms() {
		File name = new File(Environment.getExternalStorageDirectory() + "/Voicey/" + "75yarini" + ".3gp");
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.setClassName("com.android.mms",
				"com.android.mms.ui.ComposeMessageActivity");

		sendIntent.putExtra("sms_body",
				" Voicey.  Say it. Send it. 6 Sec");
		// final File file1 = new
		// File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Downloadtest.3gp");
		Uri uri = Uri.fromFile(name);
		
		sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
		sendIntent.setType("video/3gp");
		startActivity(Intent.createChooser(sendIntent, "Send file"));
	}
		
	public void shareImagewithtestWhatsApp() {
		
		Intent share1 = new Intent(Intent.ACTION_SEND);
		
		share1.setType("audio/3gp");

		
		share1.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + "/Voicey/" + "75yarini" + ".3gp"));
		share1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		if (isPackageInstalled("com.whatsapp", this)) {
			share1.setPackage("com.whatsapp");
			startActivity(Intent.createChooser(share1,
					"Share Video"));

		} else {

			Toast.makeText(this, "Please Install Whatsapp",
					Toast.LENGTH_LONG).show();
		}
		
		Intent share = new Intent(Intent.ACTION_SEND);
				// share.setType("image/jpeg");
				//share.setType("audio/3gp");
		share.setType("text/plain");
				/*
				 * share.putExtra(Intent.EXTRA_STREAM, Uri.parse(
				 * Environment.getExternalStorageDirectory()+
				 * File.separator+"temporary_file.jpg"));
				 */
				share.putExtra(Intent.EXTRA_TEXT, "picture_text");
				
				share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				if (isPackageInstalled("com.whatsapp", this)) {
					share.setPackage("com.whatsapp");
					//startActivity(Intent.createChooser(share,"Share Video"));
					
					 startActivityForResult(Intent.createChooser(share, "Share Video"),CONTACT_SHARE_CODE);

				} else {

					Toast.makeText(this, "Please Install Whatsapp",
							Toast.LENGTH_LONG).show();
				}
			
	
	
	}
			/*String filepath =Environment.getExternalStorageDirectory() + "/Voicey/" + "75yarini" + ".3gp";
			File ringtoneFile = new File(filepath);
			if (ringtoneFile.exists()) {		
			
	Intent shareIntent = new Intent();
	shareIntent.setAction(Intent.ACTION_SEND);

	//Target whatsapp:
	shareIntent.setPackage("com.whatsapp");
	//Add text and then Image URI
	shareIntent.putExtra(Intent.EXTRA_TEXT, "picture_text");
	shareIntent.putExtra(Intent.EXTRA_STREAM,Environment.getExternalStorageDirectory() + "/Voicey/" + "75yarini" + ".3gp"); 
	shareIntent.setType("audio/3gp");
	shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

	try {
	    startActivity(shareIntent);
	} catch (android.content.ActivityNotFoundException ex) {
	   // ToastHelper.MakeShortText("Whatsapp have not been installed.");
	}
			}*/
	
	//}
	
	
	public void shareImageWhatsApp() {

	    Bitmap adv = BitmapFactory.decodeResource(getResources(), R.drawable.launcher);
	    
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    adv.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	    File f = new File(Environment.getExternalStorageDirectory()
	            + File.separator + "temporary_file.jpg");
	    try {
	        f.createNewFile();
	        new FileOutputStream(f).write(bytes.toByteArray());
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	   // File name = new File("/storage/sdcard1/bluetooth/abc.3gp");

		if (f.exists()) {
	    Intent share = new Intent(Intent.ACTION_SEND);
	    share.setType("image/jpeg");
	    //share.setType("audio/3gp");
	   /* share.putExtra(Intent.EXTRA_STREAM,
	            Uri.parse( Environment.getExternalStorageDirectory()+ File.separator+"temporary_file.jpg"));*/
	    share.putExtra(android.content.Intent.EXTRA_SUBJECT, "Trip from Voyajo");
	    share.putExtra(Intent.EXTRA_STREAM,
	            Uri.parse(Environment.getExternalStorageDirectory()
	    	            + File.separator + "temporary_file.jpg")); 
	    if(isPackageInstalled("com.whatsapp",this)){
	          share.setPackage("com.whatsapp"); 
	          startActivityForResult(Intent.createChooser(share, "Share Video"),CONTACT_SHARE_CODE);

	    }else{

	        Toast.makeText(getApplicationContext(), "Please Install Whatsapp", Toast.LENGTH_LONG).show();
	    }
		}

	}

	private boolean isPackageInstalled(String packagename, Context context) {
	    PackageManager pm = context.getPackageManager();
	    try {
	        pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
	        return true;
	    } catch (NameNotFoundException e) {
	        return false;
	    }
	}
	
	private class VoiceyDelete extends AsyncTask<String, Void, String> {
		
		

		@Override
		protected String doInBackground(String... urls) {
			
			try {
			URL url = new URL("http://voicey.me/web-services/audio/78balonchik shalom.3gp");

	        //create the new connection
	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

	        //set up some things on the connection
	        urlConnection.setRequestMethod("GET");
	        urlConnection.setDoOutput(true);

	        //and connect!
	        urlConnection.connect();

	        //set the path where we want to save the file
	        //in this case, going to save it on the root directory of the
	        //sd card.
	        File SDCardRoot = Environment.getExternalStorageDirectory();
	        //create a new file, specifying the path, and the filename
	        //which we want to save the file as.
	        File file = new File(Environment.getExternalStorageDirectory() + "/Voicey/" + "78balonchik shalom" + ".3gp");

	        //this will be used to write the downloaded data into the file we created
	        FileOutputStream fileOutput = new FileOutputStream(file);

	        //this will be used in reading the data from the internet
	        InputStream inputStream = urlConnection.getInputStream();

	        //this is the total size of the file
	        int totalSize = urlConnection.getContentLength();
	        //variable to store total downloaded bytes
	        int downloadedSize = 0;

	        //create a buffer...
	        byte[] buffer = new byte[1024];
	        int bufferLength = 0; //used to store a temporary size of the buffer

	        //now, read through the input buffer and write the contents to the file
	        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
	                //add the data in the buffer to the file in the file output stream (the file on the sd card
	                fileOutput.write(buffer, 0, bufferLength);
	                //add up the size so we know how much is downloaded
	                downloadedSize += bufferLength;
	                //this is where you would do something to report the prgress, like this maybe
	               // updateProgress(downloadedSize, totalSize);

	        }
	        //close the output stream when done
	        fileOutput.close();

	//catch some possible errors...
	} catch (MalformedURLException e) {
	        e.printStackTrace();
	} catch (IOException e) {
	        e.printStackTrace();
	}
			
			
			return null;
		}

		protected void onPostExecute(String result) {
			
			

		}
	}
	
	   
	
	
	   
	        
	}

	

