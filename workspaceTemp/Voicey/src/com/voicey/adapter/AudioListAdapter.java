package com.voicey.adapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.voicey.activity.R;
import com.voicey.model.AudioInfo;
import com.voicey.model.DownloadStatus;
import com.voicey.utils.Constants;
import com.voicey.webservices.Webservices;

public class AudioListAdapter extends ArrayAdapter<AudioInfo> {

	Context context;
	AudioInfo audioInfo;
	ViewHolder holder = null;
	private MediaPlayer mediaPlayer;
	// private int mediaFileLengthInMilliseconds;
	Webservices Webservices = new Webservices();
	private final Handler handler = new Handler();
	String audioId, audioUrl;
	private Handler mHandler = new Handler();
	private Boolean isActivePopup;
	List<AudioInfo> productRegistrationList;

	public AudioListAdapter(Context context, int resourceId,
			List<AudioInfo> ProductRegistrationList) {
		super(context, resourceId, ProductRegistrationList);
		this.context = context;
		this.productRegistrationList = ProductRegistrationList;
	}

	/* private view holder class */
	private class ViewHolder {

		TextView tvTitle;
		TextView tvMood;
		TextView tvUserId;
		TextView tvCount;
		RelativeLayout rlBody;
		ImageView ivTrash;
		ImageView ivshare;
		ImageView ivringtone;
		ImageView ivnotify;

		// SeekBar songProgressBar;

	}

	public View getView(int position, View convertView, ViewGroup parent) {

		audioInfo = productRegistrationList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_page, null);
			holder = new ViewHolder();

			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvtitle);
			holder.tvMood = (TextView) convertView.findViewById(R.id.tvmood);
			holder.tvUserId = (TextView) convertView
					.findViewById(R.id.tvuserId);
			holder.ivTrash = (ImageView) convertView.findViewById(R.id.ivtrash);
			holder.ivshare = (ImageView) convertView.findViewById(R.id.ivshare);
			holder.ivringtone = (ImageView) convertView
					.findViewById(R.id.ivringtone);
			holder.ivnotify = (ImageView) convertView
					.findViewById(R.id.ivnotify);
			isActivePopup = true;

			holder.tvCount = (TextView) convertView.findViewById(R.id.tvcount);

			holder.rlBody = (RelativeLayout) convertView
					.findViewById(R.id.rlbody);
			/*
			 * holder.songProgressBar = (SeekBar) convertView
			 * .findViewById(R.id.songProgressBar);
			 */

			// mediaPlayer = new MediaPlayer();
			// mediaPlayer.setOnBufferingUpdateListener(getActivity());
			// mediaPlayer.setOnCompletionListener(context);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.tvTitle.setText(audioInfo.getTitle());
		holder.tvCount.setText("P " + audioInfo.getCounter());

		holder.tvTitle.setId(audioInfo.getPosition());
		holder.tvTitle.setTag(holder);
		holder.tvUserId.setId(audioInfo.getPosition());
		holder.ivTrash.setId(audioInfo.getPosition());
		holder.ivshare.setId(audioInfo.getPosition());
		holder.ivnotify.setId(audioInfo.getPosition());
		holder.ivringtone.setId(audioInfo.getPosition());
		
		holder.tvUserId.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(View v) {
				audioInfo = productRegistrationList.get(v.getId());

				List<AudioInfo> copyAudioList = new ArrayList<AudioInfo>();
				copyAudioList.addAll(productRegistrationList);
				if (audioInfo.getUser_control().equals("1")) {
					for (AudioInfo ai : copyAudioList) {

						if (ai.getUser_control().equals("0")) {

							productRegistrationList.remove(ai);

						} else if (!audioInfo.getUser_code().equals(
								ai.getUser_code())) {

							productRegistrationList.remove(ai);

						}

					}
				} else {

					for (AudioInfo ai : copyAudioList) {
						if (ai.getUser_control().equals("1")) {

							productRegistrationList.remove(ai);

						}

					}

				}

				notifyDataSetChanged();
			}

		});

		holder.tvTitle.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				if (isActivePopup) {
					audioInfo = productRegistrationList.get(v.getId());
					holder = (ViewHolder) v.getTag();
					displayAlert(audioInfo, v.getId(), holder);

					Integer count = new Integer(audioInfo.getCounter());
					count = count + 1;
					audioInfo.setCounter(count.toString());
					holder.tvCount.setText("P " + audioInfo.getCounter());
					/*
					 * holder.rlBody.setBackgroundColor(Color
					 * .parseColor("#e1f2fa"));
					 */
					isActivePopup = false;
				}

			}
		});

		holder.tvMood.setText("# " + audioInfo.getSource());

		if (audioInfo.getUser_control().equals("1")) {
			holder.tvUserId.setText(audioInfo.getUser_code());
		} else {
			holder.tvUserId.setText("Anonymous");
		}
		Typeface face = Typeface.createFromAsset(context.getAssets(),
				"verdana.ttf");

		holder.tvTitle.setTypeface(face);
		holder.tvMood.setTypeface(face);
		holder.tvUserId.setTypeface(face);
		holder.tvUserId.setPaintFlags(holder.tvUserId.getPaintFlags()
				| Paint.UNDERLINE_TEXT_FLAG);

		if (position % 4 == 0)
			holder.rlBody.setBackgroundColor(Color.parseColor("#22b14c"));
		else if (position % 4 == 1)
			holder.rlBody.setBackgroundColor(Color.parseColor("#00a2e8"));
		else if (position % 4 == 2)
			holder.rlBody.setBackgroundColor(Color.parseColor("#7092be"));
		else if (position % 4 == 3)
			holder.rlBody.setBackgroundColor(Color.parseColor("#c8bfe7"));

		if (audioInfo.getIsDeleteRequired() != null
				&& audioInfo.getIsDeleteRequired()) {

			holder.ivTrash.setVisibility(View.VISIBLE);
		} else {

			holder.ivTrash.setVisibility(View.GONE);

		}

		holder.ivshare.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				audioInfo = productRegistrationList.get(v.getId());
				
				File f = new File(Environment.getExternalStorageDirectory()
						+ "/Voicey/" + audioInfo.getTitle() + ".3gp");

				if (f.exists()) {
					/*shareAudioWhatsApp(Environment.getExternalStorageDirectory()
							+ "/Voicey/" + audioInfo.getTitle() + ".3gp");*/
					
					shareallthing(Environment.getExternalStorageDirectory()
							+ "/Voicey/" + audioInfo.getTitle() + ".3gp");
				}else{
				new voiceyDownload().execute();
				}

			}
		});

		holder.ivTrash.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				audioInfo = productRegistrationList.get(v.getId());
				audioId = audioInfo.getId();
				new VoiceyDelete().execute();
				productRegistrationList.remove(audioInfo);
				notifyDataSetChanged();

				File f = new File(Environment.getExternalStorageDirectory()
						+ "/Voicey/" + audioInfo.getTitle() + ".3gp");

				if (f.exists()) {
					f.delete();
				}

			}
		});
		
		holder.ivnotify.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				
				audioInfo = productRegistrationList.get(v.getId());
				
				notificationMail(audioInfo);
				
			}
		});
			
		holder.ivringtone.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				audioInfo = productRegistrationList.get(v.getId());
				
				displayRingtoneAlert(audioInfo);
				
				
				/*File f = new File(Environment.getExternalStorageDirectory()
						+ "/Voicey/" + audioInfo.getTitle() + ".3gp");

				if (f.exists()) {
					
					setRingTone(Environment.getExternalStorageDirectory()
							+ "/Voicey/" + audioInfo.getTitle() + ".3gp");
				}*/
				
			}
		});
		//
		// URL

		return convertView;
	}

	SeekBar songProgressBar;

	void displayAlert(AudioInfo audioInfo, final Integer position,
			final ViewHolder holder) {
		// SeekBar songProgressBar;
		TextView tvcancel;
		TextView tvtitle;
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.play_audio, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		tvcancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvtitle = (TextView) promptsView.findViewById(R.id.tvtitle);

		Typeface face = Typeface.createFromAsset(context.getAssets(),
				"verdana.ttf");

		tvtitle.setTypeface(face);
		tvcancel.setTypeface(face);

		songProgressBar = (SeekBar) promptsView
				.findViewById(R.id.songProgressBar);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();
		tvtitle.setText(audioInfo.getTitle());
		tvcancel.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					if (mediaPlayer != null) {
						mediaPlayer.stop();
					}
					/*
					 * if (position % 2 == 0)
					 * holder.rlBody.setBackgroundColor(Color
					 * .parseColor("#ffffff")); else
					 * holder.rlBody.setBackgroundColor(Color
					 * .parseColor("#e6e3e3"));
					 */
					isActivePopup = true;
					alertDialog.cancel();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		audioId = audioInfo.getId();
		new AudioCountInc().execute();
		mediaPlayer = new MediaPlayer();
		String url = Constants.audio_url + audioInfo.getFileName();

		try {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
			}
			mediaPlayer.setDataSource(url);
			// mediaPlayer = new MediaPlayer();
			// mediaPlayer.setDataSource(Environment.getExternalStorageDirectory()
			// + "/"+ Constants.app_folder + "/" + "abc" + ".3gp");
			mediaPlayer.prepare();
			mediaPlayer.start();
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			// mediaPlayer.setDataSource(url);
			// mediaPlayer.setDataSource();
			// mediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// mediaFileLengthInMilliseconds = mediaPlayer.getDuration();

		// mediaPlayer.start();

		updateProgressBar();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				mediaPlayer.start();
				songProgressBar.setProgress(0);
				songProgressBar.setMax(100);

				// Updating progress bar
				updateProgressBar();

			}
		});

	}

	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = mediaPlayer.getDuration();
			long currentDuration = mediaPlayer.getCurrentPosition();

			int secs = (int) (currentDuration / 1000);
			int maxsec = (int) (totalDuration / 1000);

			Double percentage = ((double) secs / maxsec) * 100;
			songProgressBar.setProgress(percentage.intValue());
			if (secs < maxsec) {
				mHandler.postDelayed(this, 100);
			}
		}
	};

	/*
	 * private void primarySeekBarProgressUpdater() {
	 * songProgressBar.setProgress((int) (((float) mediaPlayer
	 * .getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); if
	 * (mediaPlayer.isPlaying()) { Runnable notification = new Runnable() {
	 * public void run() { primarySeekBarProgressUpdater(); } };
	 * handler.postDelayed(notification, 100); } }
	 */

	private class AudioCountInc extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.AudioCountInc(audioId);
		}

		protected void onPostExecute(String result) {

		}
	}

	private class VoiceyDelete extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.voiceyDelete(audioId);
		}

		protected void onPostExecute(String result) {

		}
	}

	private class voiceyDownload extends AsyncTask<String, Void, DownloadStatus> {
		Dialog dialog;

		@Override
		protected DownloadStatus doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.voiceyDownload(audioInfo);
		}
		
		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(context);

			dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			dialog.setContentView(R.layout.loading_layout);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			dialog.show();

		}

		protected void onPostExecute(DownloadStatus downloadStatus) {
			dialog.dismiss();
			
			if(downloadStatus.getStatus().equals("1")){
				
				shareAudioWhatsApp(downloadStatus.getUrl());
				
			}else{

		        Toast.makeText(context, "Error occurred while downloading ", Toast.LENGTH_LONG).show();
		    }

		}
	}
	
	public void shareAudioWhatsApp(String url) {

	    
	    
	    File name = new File(url);

		if (name.exists()) {
	    Intent share = new Intent(Intent.ACTION_SEND);
	   // share.setType("image/jpeg");
	    share.setType("audio/3gp");
	   /* share.putExtra(Intent.EXTRA_STREAM,
	            Uri.parse( Environment.getExternalStorageDirectory()+ File.separator+"temporary_file.jpg"));*/
	    
	    share.putExtra(Intent.EXTRA_STREAM,
	            Uri.parse(url)); 
	    if(isPackageInstalled("com.whatsapp",context)){
	          share.setPackage("com.whatsapp"); 
	          context. startActivity(Intent.createChooser(share, "Share Video"));

	    }else{

	        Toast.makeText(context, "Please Install Whatsapp", Toast.LENGTH_LONG).show();
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
	
	public void notificationMail(AudioInfo audioInfo) {
		
		String message=" Voicey Title:"+audioInfo.getTitle()+" , Report by:"+audioInfo.getUser_code();
		
		 Intent email = new Intent(Intent.ACTION_SEND);
		  email.putExtra(Intent.EXTRA_EMAIL, new String[]{ Constants.notifican_mail});
		  //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
		  //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
		  email.putExtra(Intent.EXTRA_SUBJECT, Constants.notifican_mail_subject);
		  email.putExtra(Intent.EXTRA_TEXT, message);

		  //need this to prompts email client only
		  email.setType("message/rfc822");
		  
		  context. startActivity(Intent.createChooser(email, "Choose an Email client :"));
		
		
	}
	
	public void shareallthing(String url){
		
		String path = url;


        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("video/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + path));
        context.startActivity(Intent.createChooser(share, "Share Sound File"));
	}
	
	void displayRingtoneAlert( final AudioInfo audioInfo){
		TextView tvcancel;
		TextView tvGenral,tvspecific;
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.ringtone_popup, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		tvspecific = (TextView) promptsView.findViewById(R.id.tvspecific);
		tvGenral = (TextView) promptsView.findViewById(R.id.tvgenral);
		tvcancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		Typeface face = Typeface.createFromAsset(context.getAssets(),
				"verdana.ttf");

		tvspecific.setTypeface(face);
		tvGenral.setTypeface(face);	
		tvcancel.setTypeface(face);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();
		
		tvcancel.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					
					alertDialog.cancel();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		tvspecific.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {


					
					//

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		tvGenral.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					
					alertDialog.cancel();
					File f = new File(Environment.getExternalStorageDirectory()
							+ "/Voicey/" + audioInfo.getTitle() + ".3gp");

					if (f.exists()) {
						
						setRingTone(Environment.getExternalStorageDirectory()
								+ "/Voicey/" + audioInfo.getTitle() + ".3gp");
					}
					
					//alertDialog.cancel();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		
		
	}
	static public final int CONTACT_CHOOSER_ACTIVITY_CODE = 73729;
	public void setRingToneContact(String url){
		
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		((Activity)context).startActivityForResult(intent, CONTACT_CHOOSER_ACTIVITY_CODE);
		
	}
	
	
	
	public void setRingTone(String url){
		 try {
				String filepath = url;
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
					Uri newUri = context.getContentResolver().insert(uri, content);
					//ringtoneUri = newUri;
				
					RingtoneManager.setActualDefaultRingtoneUri(context,
					RingtoneManager.TYPE_RINGTONE,newUri);
					Toast.makeText(context, "Ringtone Changed successfully", Toast.LENGTH_LONG).show();
					
				 } 
				 }catch (Exception e) {
				        e.printStackTrace();
				    }
		
		
	}

}
