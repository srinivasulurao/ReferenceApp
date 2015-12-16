package com.voicey.fragment;

import java.io.File;
import java.io.IOException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.voicey.activity.R;
import com.voicey.utils.Constants;

public class RecordFragment extends Fragment implements OnClickListener {

	ImageView ivstop, ivplay;
	ImageButton ivstart;
	private long startTime = 0L;
	private Handler customHandler = new Handler();
	private Handler mHandler = new Handler();
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	RelativeLayout rlbottombutton;
	LinearLayout llbotombitton;
	MediaPlayer m;
	private SeekBar songProgressBar;
	TextView tvTimmer, tvComment, tvcancle, tvsave, tvplaycancel, tvmaxTime;
	private String outputFile = null;
	private MediaRecorder myAudioRecorder;
	String audio_file_name = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.record_page, container, false);

		initilizeUI(v);

		return v;
	}

	private void initilizeUI(View v) {

		try {

			ivstart = (ImageButton) v.findViewById(R.id.ivstart);
			ivstop = (ImageView) v.findViewById(R.id.ivstop);
			ivplay = (ImageView) v.findViewById(R.id.ivplay);
			tvTimmer = (TextView) v.findViewById(R.id.currentduration);
			tvmaxTime = (TextView) v.findViewById(R.id.totalduration);
			tvComment = (TextView) v.findViewById(R.id.tvcomment);
			songProgressBar = (SeekBar) v.findViewById(R.id.songProgressBar);
			/*
			 * rlbottombutton = (RelativeLayout) v
			 * .findViewById(R.id.rlbottombutton1);
			 */
			llbotombitton = (LinearLayout) v.findViewById(R.id.llbotombitton);
			llbotombitton.setVisibility(View.GONE);
			ivstop.setVisibility(View.GONE);
			ivplay.setVisibility(View.GONE);
			songProgressBar.setVisibility(View.GONE);
			tvTimmer.setVisibility(View.GONE);
			tvmaxTime.setVisibility(View.GONE);
			// tvcancle = (TextView) v.findViewById(R.id.tvcancle);
			tvsave = (TextView) v.findViewById(R.id.tvsave);
			// tvplaycancel = (TextView) v.findViewById(R.id.tvplaycancel);

			tvcancle.setOnClickListener(this);
			tvsave.setOnClickListener(this);
			tvplaycancel.setOnClickListener(this);

			ivplay.setOnClickListener(this);
			ivstop.setOnClickListener(this);
			// ivstart.setOnClickListener(this);

			ivstart.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						startRecord();

					} else if (event.getAction() == MotionEvent.ACTION_UP) {

						try {
							stopRecord();
							play();

						} catch (Exception e) {

						}
					}
					return false;
				}
			});

			tvComment.setText("Record your voice");

			File f = new File(Environment.getExternalStorageDirectory() + "/"
					+ Constants.app_folder + "");
			if (f.mkdir()) {
				System.out.println("Directory created");
			} else {
				System.out.println("Directory is not created");
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		/*
		 * case R.id.ivstart:
		 * 
		 * start(); break;
		 */

		case R.id.ivstop:

			stopPlay();

			break;

		case R.id.ivplay:
			try {
				play();
			} catch (Exception e) {

			}
			break;
		/*
		 * case R.id.tvcancle: cancle(); break;
		 */

		/*
		 * case R.id.tvplaycancel: cancle(); break;
		 */
		case R.id.tvsave:

			Fragment fragment = new SaveFragment();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			break;

		}
	}

	void setUpMediaRecorder() {
		outputFile = Constants.temp_url;

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
	}

	private Runnable updateTimerThread = new Runnable() {
		public void run() {

			int maxSec = 6;
			// timeSwapBuff = 0L;

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

			updatedTime = timeInMilliseconds;

			int secs = (int) (updatedTime / 1000);

			int mins = secs / 60;

			secs = secs % 60;

			int milliseconds = (int) (updatedTime % 1000);

			if (mins == 0 && secs <= 6) {
				Double percentage = ((double) secs / maxSec) * 100;
				songProgressBar.setProgress(percentage.intValue());

				Integer secInt = new Integer(secs);
				Integer maxCount = new Integer(Constants.total_audio_time);
				Integer actualSec = maxCount - secInt;

				tvTimmer.setText("" + String.format("%02d", actualSec) + " Sec");
				customHandler.postDelayed(this, 100);
			} else {
				stopRecord();

			}

		}

	};

	private void startRecord() {
		try {

			File f = new File(Constants.temp_url);

			if (f.exists()) {
				f.delete();
			}
			songProgressBar.setVisibility(View.VISIBLE);
			tvTimmer.setVisibility(View.VISIBLE);
			tvmaxTime.setVisibility(View.VISIBLE);
			// ivstop.setVisibility(View.VISIBLE);
			// ivstart.setVisibility(View.GONE);
			startTime = SystemClock.uptimeMillis();
			tvComment.setText("Recording...");

			tvmaxTime.setText("06 Sec");
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			customHandler.postDelayed(updateTimerThread, 0);
			setUpMediaRecorder();

			myAudioRecorder.prepare();
			myAudioRecorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Toast.makeText(getApplicationContext(), "Recording started",
		// Toast.LENGTH_LONG).show();
	}

	private void stopRecord() {
		timeSwapBuff += timeInMilliseconds;
		customHandler.removeCallbacks(updateTimerThread);
		// ivplay.setVisibility(View.VISIBLE);

		rlbottombutton.setVisibility(View.GONE);
		songProgressBar.setVisibility(View.GONE);
		tvTimmer.setVisibility(View.GONE);
		tvmaxTime.setVisibility(View.GONE);
		llbotombitton.setVisibility(View.VISIBLE);

		// btn_PlayAudio.setEnabled(true);
		myAudioRecorder.stop();
		myAudioRecorder.release();
		myAudioRecorder = null;
		// Toast.makeText(getApplicationContext(),
		// "Audio recorded successfully",
		// Toast.LENGTH_LONG).show();
	}

	void cancle() {

		if (myAudioRecorder != null) {

			myAudioRecorder.release();
		}
		if (m != null) {
			m.stop();

		}
		customHandler.removeCallbacksAndMessages(null);
		mHandler.removeCallbacksAndMessages(null);
		songProgressBar.setVisibility(View.GONE);
		tvTimmer.setVisibility(View.GONE);
		tvmaxTime.setVisibility(View.GONE);
		llbotombitton.setVisibility(View.GONE);
		ivstop.setVisibility(View.GONE);
		ivplay.setVisibility(View.GONE);
		ivstart.setVisibility(View.VISIBLE);
		rlbottombutton.setVisibility(View.VISIBLE);
		tvComment.setText("Record your voice");
		tvTimmer.setText("06 Sec");

	}

	private void play() throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {

		m = new MediaPlayer();
		m.setDataSource(outputFile);
		m.prepare();
		m.start();
		ivplay.setVisibility(View.GONE);
		ivstop.setVisibility(View.VISIBLE);
		tvComment.setText("Playing...");
		songProgressBar.setVisibility(View.VISIBLE);
		tvTimmer.setVisibility(View.VISIBLE);
		tvmaxTime.setVisibility(View.VISIBLE);
		songProgressBar.setProgress(0);
		songProgressBar.setMax(100);

		// Updating progress bar
		updateProgressBar();

		m.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// songProgressBar.setVisibility(View.GONE);
				// tvTimmer.setVisibility(View.GONE);
				// tvmaxTime.setVisibility(View.GONE);
				// TODO Auto-generated method stub
				// btn_PlayAudio.setTag("1");
				// btn_PlayAudio.setText("Play");
				m.start();
				songProgressBar.setProgress(0);
				songProgressBar.setMax(100);

				// Updating progress bar
				updateProgressBar();

			}
		});

	}

	private void stopPlay() {

		m.stop();
		songProgressBar.setVisibility(View.GONE);
		tvTimmer.setVisibility(View.GONE);
		tvmaxTime.setVisibility(View.GONE);
		ivstop.setVisibility(View.GONE);
		ivplay.setVisibility(View.VISIBLE);
		tvComment.setText("Play your voicey");

	}

	private void stop_audio() throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {

		m.stop();

	}

	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = m.getDuration();
			long currentDuration = m.getCurrentPosition();

			int secs = (int) (currentDuration / 1000);
			int maxsec = (int) (totalDuration / 1000);

			secs = secs % 60;
			maxsec = maxsec % 60;
			Double percentage = ((double) secs / maxsec) * 100;
			songProgressBar.setProgress(percentage.intValue());

			tvTimmer.setText("" + String.format("%02d", secs) + " Sec");

			tvmaxTime.setText("" + String.format("%02d", maxsec) + " Sec");

			if (secs < maxsec) {
				mHandler.postDelayed(this, 100);
			}
		}
	};
}
