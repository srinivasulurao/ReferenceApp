package com.omkar.myactivitytracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


import com.personaltrainer.database.LoginDB;

import com.personaltrainer.widgets.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddNote extends Activity implements OnClickListener{

	EditText edtNewNote;
	LoginDB logDB;
	String sFrom ="", sTag="";
	String sId;
	TextView txt_AudioTime;
	Button btn_PlayAudio, btn_SaveAudio;
	ImageView img_speak,img_Play,img_Stop, img_picture;
	RelativeLayout relAudio;
	private MediaRecorder myAudioRecorder;
	private String outputFile = null;
	String audio_file_name="", picture_file_name="";

	private long startTime = 0L;
	private Handler customHandler = new Handler();
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	MediaPlayer m ;

	protected boolean _taken = true;
	File sdImageMainDirectory;
	protected static final String PHOTO_TAKEN = "photo_taken";

	void generateRandomNum()
	{
		final Random rand = new Random();

		final int min=100;
		final int max=999;

		int randomNum = rand.nextInt((max - min) + 1) + min;
		audio_file_name = Integer.toString(randomNum);
	}

	void initilizeUI()
	{

		sTag = "play";
		sFrom = getIntent().getStringExtra("from");
		ActionBar actionBar = getActionBar();

		if(sFrom.equalsIgnoreCase("edit"))
		{
			actionBar.setTitle(" Edit Note");
			sId = getIntent().getStringExtra("id");
		}
		else
		{
			actionBar.setTitle(" New Note");
		}

		edtNewNote = (EditText)findViewById(R.id.edtNewNote);
		img_speak = (ImageView)findViewById(R.id.img_speak);
		img_picture = (ImageView)findViewById(R.id.img_picture);

		img_Play = (ImageView)findViewById(R.id.img_Play);
		img_Play.setBackgroundResource(R.drawable.play_gray);

		img_Stop = (ImageView)findViewById(R.id.img_Stop);
		img_Stop.setBackgroundResource(R.drawable.stop_gray);
		img_Stop.setEnabled(false);

		btn_PlayAudio = (Button)findViewById(R.id.btn_PlayAudio);
		btn_SaveAudio = (Button)findViewById(R.id.btn_SaveAudio);
		relAudio = (RelativeLayout)findViewById(R.id.relAudio);
		txt_AudioTime = (TextView)findViewById(R.id.txt_AudioTime);
		logDB = new LoginDB(this);


	}

	void setUpMediaRecorder()
	{
		outputFile = Environment.getExternalStorageDirectory()+"/"+Utils.appName_+"/"+audio_file_name+".3gp";

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnote);

		initilizeUI();

		img_speak.setOnClickListener(this);
		img_Play.setOnClickListener(this);
		img_Stop.setOnClickListener(this);
		btn_PlayAudio.setOnClickListener(this);
		btn_SaveAudio.setOnClickListener(this);
		img_picture.setOnClickListener(this);

		if(sFrom.equalsIgnoreCase("edit"))
		{
			getNoteData();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	void getNoteData()
	{
		Cursor cursor = logDB.getNotes();
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String mId = cursor.getString(0);
				if(sId.equalsIgnoreCase(mId))
				{
					edtNewNote.setText(cursor.getString(2));
					audio_file_name = cursor.getString(4);
				}

			}while(cursor.moveToNext());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.notes_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) 
		{
		case R.id.action_save:

			if(edtNewNote.getText().toString().equals(""))
			{
				Utils.showAlertBoxSingle(AddNote.this, "Error", "Please Enter the Notes to Save.");
			}
			else
			{
				if(sFrom.equalsIgnoreCase("edit"))
				{
					logDB.UpdateNotes(Utils.getTodaysDate(AddNote.this), edtNewNote.getText().toString().trim(), Integer.parseInt(sId),
							audio_file_name,picture_file_name);
					finish();
				}
				else
				{
					logDB.addNotes(Utils.getTodaysDate(AddNote.this), edtNewNote.getText().toString().trim(), 
							audio_file_name, picture_file_name);
					finish();
				}
			}

			break;

		}
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch(v.getId())
		{
		case R.id.img_speak:

			relAudio.setVisibility(View.VISIBLE);
			break;

		case R.id.img_Play:

			File f = new File(Environment.getExternalStorageDirectory()+"/"+Utils.appName_+"/"+audio_file_name+".3gp");
			if(f.exists())
			{
				f.delete();
			}

			generateRandomNum();


			startTime = SystemClock.uptimeMillis();
			customHandler.postDelayed(updateTimerThread, 0);

			img_Play.setBackgroundResource(R.drawable.play);
			img_Stop.setBackgroundResource(R.drawable.stop_gray);
			img_Stop.setEnabled(true);
			setUpMediaRecorder();
			start();
			break;

		case R.id.img_Stop:

			timeSwapBuff += timeInMilliseconds;
			customHandler.removeCallbacks(updateTimerThread);

			img_Stop.setEnabled(false);
			img_Stop.setBackgroundResource(R.drawable.stop);
			img_Play.setBackgroundResource(R.drawable.play_gray);
			btn_PlayAudio.setEnabled(true);
			stop();

			break;

		case R.id.btn_PlayAudio:


			if(btn_PlayAudio.getTag().toString().equalsIgnoreCase("1"))
			{
				sTag = "1";
				try
				{
					play();
				}catch(Exception e)
				{
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
				}
			}

			if(btn_PlayAudio.getTag().toString().equalsIgnoreCase("2"))
			{
				sTag = "2";
				try
				{
					stop_audio();
				}catch(Exception e)
				{
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
				}
			}


			if(sTag.equalsIgnoreCase("1"))
			{
				btn_PlayAudio.setTag("2");
				btn_PlayAudio.setText("Stop");
			}

			if(sTag.equalsIgnoreCase("2"))
			{
				btn_PlayAudio.setTag("1");
				btn_PlayAudio.setText("Play");
			}





			break;

		case R.id.img_picture:
			/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 111);*/

			try {
				Random generator = new Random();
				int n = 10000;
				n = generator.nextInt(n);
				String fname = "Image-"+ n +".png";
				picture_file_name = Environment.getExternalStorageDirectory()+"/"+Utils.appName_+"/"+fname;
				sdImageMainDirectory = new File(picture_file_name);
				startCameraActivity();
			} catch (Exception e) {
				finish();
				Toast.makeText(this, "Error occured. Please try again later.",
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.btn_SaveAudio:


			break;
		}

	}

	private Runnable updateTimerThread = new Runnable() {
		public void run() {

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

			updatedTime = timeSwapBuff + timeInMilliseconds;

			int secs = (int) (updatedTime / 1000);

			int mins = secs / 60;

			secs = secs % 60;

			int milliseconds = (int) (updatedTime % 1000);

			txt_AudioTime.setText("" + mins + ":"

		                    + String.format("%02d", secs)
					);

			customHandler.postDelayed(this, 0);
		}

	};

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();

		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;

		float scaleHeight = ((float) newHeight) / height;

		// CREATE A MATRIX FOR THE MANIPULATION

		Matrix matrix = new Matrix();

		// RESIZE THE BIT MAP

		matrix.postScale(scaleWidth, scaleHeight);

		// RECREATE THE NEW BITMAP

		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

		return resizedBitmap;

	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == 111) 
		{
			Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
			byte[] byteArray = byteArrayOutputStream .toByteArray();

			//picture_file_name = Base64.encodeToString(byteArray, Base64.DEFAULT);
			//Bitmap resized = Bitmap.createScaledBitmap(bitmap, 250, 250, true);
			Display mDisplay = getWindowManager().getDefaultDisplay();
			final int width  = mDisplay.getWidth();
			final int height = mDisplay.getHeight();
			Bitmap resized = getResizedBitmap(bitmap, height, width);
			SaveImage(resized);

		}

	}*/
	protected void startCameraActivity() {

		Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);

		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 0:
			finish();
			break;
		}
	}

	private void SaveImage(Bitmap finalBitmap) {


		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/saved_images");    
		myDir.mkdirs();
		Random generator = new Random();
		int n = 10000;
		n = generator.nextInt(n);
		String fname = "Image-"+ n +".png";
		picture_file_name = Environment.getExternalStorageDirectory()+"/"+Utils.appName_+"/"+fname;
		File f = new File(Environment.getExternalStorageDirectory()+"/"+Utils.appName_+"/"+fname);
		if (f.exists ()) f.delete (); 
		try {
			FileOutputStream out = new FileOutputStream(f);
			finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start(){
		try {
			myAudioRecorder.prepare();
			myAudioRecorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
	}

	private void stop(){
		myAudioRecorder.stop();
		myAudioRecorder.release();
		myAudioRecorder  = null;
		Toast.makeText(getApplicationContext(), "Audio recorded successfully",
				Toast.LENGTH_LONG).show();
	}

	private void play() throws IllegalArgumentException,   
	SecurityException, IllegalStateException, IOException{

		m = new MediaPlayer();
		m.setDataSource(outputFile);
		m.prepare();
		m.start();

		m.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				btn_PlayAudio.setTag("1");
				btn_PlayAudio.setText("Play");
			}
		});

	}

	private void stop_audio() throws IllegalArgumentException,   
	SecurityException, IllegalStateException, IOException{

		m.stop();

	}

	
	@Override
	public void onBackPressed() {
	
		//super.onBackPressed();
		
		if(edtNewNote.getText().toString().trim().equalsIgnoreCase(""))
		{
			finish();
		}
		else
		{
			ShowConfirmBox();
		}
	}
	
	void ShowConfirmBox()
	{

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddNote.this);
		alertDialog.setTitle("Confirm Discard");
		alertDialog.setMessage("Would you like to Discard the Notes ?");
		
		alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {

				finish();
				dialog.cancel();
			}
		});

		alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();
			}
		});
		
		alertDialog.show();
	
	}
	

}
