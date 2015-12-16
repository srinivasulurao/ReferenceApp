package com.voicey.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.voicey.activity.R;
import com.voicey.adapter.ChatMessagesListAdapter;
import com.voicey.model.ChatMessages;
import com.voicey.utils.Constants;


public class ChatMessageFragment extends Fragment { 
	
	
	ImageView ivsend,ivrecord,ivaddimage,ivDispalyImg;
	EmojiconEditText etMessageText;
	TextView tvFriendName;
	public ChatMessagesListAdapter chatMessagesAdapter;
	Bitmap imagebitmap;
	ListView chatListView;
	String videoUrl;
	private Boolean isActivePopup;
	MediaPlayer m;
	 SeekBar songProgressBar;
	private MediaRecorder myAudioRecorder;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	TextView tvTimmer, tvComment, tvmaxTime, tvcancel;
	private long startTime = 0L;
	String userCode, userId, userName;
	SharedPreferences sharedPreferences;
	ImageView ivstop, ivplay,ivClose;
	ImageButton ivstart;
	Button sendStickers;
	private final static int ACTIVITY_TAKE_PHOTO = 1;
	static public final int RESULT_LOAD_IMAGE = 267;
	private static final int RESULT_LOAD_VIDEO = 5;
	private FragmentActivity myContext;
	Dialog alertDialog;
	String outputFile;
	FrameLayout flSmilys;
	private Handler customHandler = new Handler();
	private Handler mHandler = new Handler();
	
	
	ArrayList<ChatMessages> messagesArrayList = new ArrayList<ChatMessages>();
	ChatMessages chatMessages;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_chat_message, container, false);

		initilizeUI(v);

		return v;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		
		

		}
	}

	private void initilizeUI(View v) {

		try {
			
			ivsend=(ImageView)v.findViewById(R.id.ivsend);
			etMessageText=(EmojiconEditText)v.findViewById(R.id.etmessageText);
			chatListView=(ListView)v.findViewById(R.id.chatlistview);
			ivrecord=(ImageView)v.findViewById(R.id.ivrecord);
			ivaddimage=(ImageView)v.findViewById(R.id.ivaddimage);
			tvFriendName=(TextView)v.findViewById(R.id.tvFriendName);
			flSmilys = (FrameLayout) v.findViewById(R.id.emojicons);
			sendStickers=(Button)v.findViewById(R.id.sendStickers);
			ivsend.setVisibility(View.GONE);
			flSmilys.setVisibility(View.GONE);
			myContext = (FragmentActivity) super.getActivity();
			
			
			String strtext = getArguments().getString("friendName");
			tvFriendName.setText(strtext);
			
			
			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			userName = sharedPreferences.getString("userName", null);
			userCode = sharedPreferences.getString("userCode", null);
			
			ivrecord.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					
					displayAlert();

				}
			});
			
			ivaddimage.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					
					displayPhotoSelect("image");

				}
			});
			
			sendStickers.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					
					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);

					imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
							.getWindowToken(), 0);

					/*
					 * if (imm.isAcceptingText()) {
					 * 
					 * imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0); }
					 */
					// imm.hideSoftInputFromWindow(myEditText.getWindowToken(),
					// 0);
					
					SharedPreferences.Editor editor = sharedPreferences.edit();
					
					editor.putString("emotion", "chat");
					editor.commit();

					flSmilys.setVisibility(View.VISIBLE);
					
					setEmojiconFragment(false);

				}
			});
			
			
			etMessageText.addTextChangedListener(new TextWatcher() {
	            @Override
	            public void onTextChanged(CharSequence s, int start, int before, int count) {

	                // TODO Auto-generated method stub
	            }

	            @Override
	            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	                // TODO Auto-generated method stub
	            }

	       

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					if (s.toString().length() > 0) {
						
						ivsend.setVisibility(View.VISIBLE);
						ivrecord.setVisibility(View.GONE);
						ivaddimage.setVisibility(View.GONE);
						
					}else{
						ivrecord.setVisibility(View.VISIBLE);
						ivsend.setVisibility(View.GONE);
						ivaddimage.setVisibility(View.VISIBLE);
						
						
					}
					
				}
	        });
			
			
			ivsend.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					String messageText=etMessageText.getText().toString();
					etMessageText.setText(null);
					
					if(messageText.length() > 0){
						
						
						chatMessages=new ChatMessages();
						chatMessages.setChatMessage(messageText);
						chatMessages.setType("out");
						
						addChatMessage(chatMessages);
						
					}
					
					flSmilys.setVisibility(View.GONE);
					
				}
			});
		

			
		
		} catch (Exception e) {

		e.printStackTrace();
	}

}
	
	public void updatesmily(Emojicon emojicon) {
		EmojiconsFragment.input(etMessageText,
				emojicon);
		
	}
	

	private void setEmojiconFragment(boolean useSystemDefault) {

		myContext
				.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.emojicons,
						EmojiconsFragment.newInstance(useSystemDefault))
				.commit();
	}
	
	void addChatMessage(ChatMessages chatMessages){
		
		SimpleDateFormat sdfinsert = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = sdfinsert.format(new Date());
		chatMessages.setTime(strDate);
		messagesArrayList.add(chatMessages);
		
		chatMessagesAdapter = new ChatMessagesListAdapter(getActivity(), R.layout.chat_message,
				messagesArrayList);

		chatListView.setAdapter(chatMessagesAdapter);
		
	}
	
	void displayPhotoSelect(final String type) {

		TextView camera, gallery, video, tvcancel;
		final Dialog dialog = new Dialog(getActivity());

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.photo_select);

		camera = (TextView) dialog.findViewById(R.id.tvcamera);
		gallery = (TextView) dialog.findViewById(R.id.tvgallery);
		video = (TextView) dialog.findViewById(R.id.tvvideo);
		tvcancel = (TextView) dialog.findViewById(R.id.tvcancel);

		dialog.show();
		videoUrl = null;
		tvcancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();
			}
		});

		camera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				imagebitmap = null;
				if(!type.equals("record")){
				displayimageReply();
				}
				takePictureButtonClicked();

				dialog.dismiss();
			}
		});

		gallery.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();
				imagebitmap = null;
				if(!type.equals("record")){
				displayimageReply();
				}
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		video.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				imagebitmap = null;
				videoUrl = null;
				dialog.dismiss();
				if(!type.equals("record")){
				displayimageReply();
				}
				Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
				// comma-separated MIME types
				mediaChooser.setType("video/*");
				startActivityForResult(mediaChooser, RESULT_LOAD_VIDEO);

			}
		});

	}
	
	private void takePictureButtonClicked() {
		Uri imageUri = Uri.fromFile(getTempFile(getActivity()
				.getApplicationContext()));
		// Intent intent = createIntentForCamera(imageUri);

		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		startActivityForResult(cameraIntent, ACTIVITY_TAKE_PHOTO);
	}

	private File getTempFile(Context context) {
		String fileName = "temp_photo.jpg";
		File path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path.exists()) {
			path.mkdir();
		}
		return new File(path, fileName);
	}

	private Intent createIntentForCamera(Uri imageUri) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		return intent;
	}
	void displayimageReply(){
		


		TextView tvcancel, tvReply;
		final EditText etTitle;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.image_reply, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		ivDispalyImg = (ImageView) promptsView.findViewById(R.id.ivaddimage);

		tvcancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvReply = (TextView) promptsView.findViewById(R.id.tvreply);
		etTitle = (EditText) promptsView.findViewById(R.id.ettextmsg);
		
		tvReply.setText("Send");

		final AlertDialog alertDialog = alertDialogBuilder.create();

		
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

		/*ivaddimage.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					displayPhotoSelect();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});*/

		tvReply.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					String title = null;
					if (imagebitmap != null) {

						String titleStr = ((TextView) etTitle).getText()
								.toString();
						chatMessages=new ChatMessages();
					//	voiceyReply = new VoiceyReply();
						if (titleStr.length() > 0) {
							 
							chatMessages.setChatMessage(titleStr);
						
						} else {
							// title = "Voicey ID " + userCode;
						}
						
					
						chatMessages.setImgBiteMap(imagebitmap);
						chatMessages.setType("out");
						if(videoUrl!=null){
							chatMessages.setVideo(videoUrl);
							
						}
						
						addChatMessage(chatMessages);
						alertDialog.cancel();

						/*File file = new File(Constants.image_folder + "/"
								+ userCode + ".jpeg");

						voiceyReply.setImage_name(userCode);

						if (file.exists())
							file.delete();
						try {
							FileOutputStream out = new FileOutputStream(
									file);
							imagebitmap.compress(
									Bitmap.CompressFormat.JPEG, 90, out);
							out.flush();
							out.close();

						} catch (Exception e) {
							e.printStackTrace();
						}*/
					}

					/*StringBuffer friendBuffer = new StringBuffer();
					if (audioInfo.getCcFriendList().size() > 0) {
						for (Friend f : audioInfo.getCcFriendList()) {

							friendBuffer.append(f.getFriendId() + ",");

						}
					}

					StringBuffer groupBuffer = new StringBuffer();

					if (audioInfo.getCcGroupList().size() > 0) {
						for (Friend f : audioInfo.getCcGroupList()) {

							groupBuffer.append(f.getFriendId() + ",");

						}
					}

					if (!audioInfo.getGroupId().equals("null")
							&& audioInfo.getGroupId() != null
							&& audioInfo.getGroupId().length() > 0) {

						groupBuffer.append(audioInfo.getGroupId() + ",");

					} else {

						friendBuffer.append(audioInfo.getUser_code() + ",");
					}
					friendBuffer.append(audioInfo.getUser_code() + ",");

					// voiceyReply = new VoiceyReply();

					voiceyReply.setTitle(title);

					voiceyReply.setUsercode(userCode);
					voiceyReply.setSharedFriendCode(friendBuffer.toString());
					voiceyReply.setPublic_control("0");
					voiceyReply.setUser_control("1");
					voiceyReply.setUserid(userId);
					voiceyReply.setType("classifield");
					voiceyReply.setId(audioInfo.getId());
					voiceyReply.setSharedGroupCode(groupBuffer.toString());

					if (videoUrl != null) {
						voiceyReply.setVideo_name(videoUrl);
					}
					alertDialog.cancel();
					new ReplayImage().execute();*/

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	
		
	}
	
	public void displayAlert() {

		TextView tvquickshare, tvquickreply,tvUserId;
		RelativeLayout quicksharepannel, rrsavepannel, rrquickreply;
		
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.record_page, null);
		
		Spinner spCategory;
		
		final EditText etTitle;
		final EditText etMood;
		final RelativeLayout llPublic;
		final RelativeLayout llUser;
		final ImageView ivMaximize;
		final ImageView ivMinimize;
		ToggleButton tgmakepublic, tguser;

		ivstart = (ImageButton) promptsView.findViewById(R.id.ivstart);
		ivstop = (ImageView) promptsView.findViewById(R.id.ivstop);
		ivplay = (ImageView) promptsView.findViewById(R.id.ivplay);
		tvTimmer = (TextView) promptsView.findViewById(R.id.currentduration);
		tvmaxTime = (TextView) promptsView.findViewById(R.id.totalduration);
		tvComment = (TextView) promptsView.findViewById(R.id.tvcomment);
		tvUserId = (TextView) promptsView.findViewById(R.id.tvuserid);
		// tvcancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		
		tvquickreply = (TextView) promptsView.findViewById(R.id.tvquickreply);
		
		quicksharepannel = (RelativeLayout) promptsView
				.findViewById(R.id.quicksharepannel);
		rrsavepannel = (RelativeLayout) promptsView
				.findViewById(R.id.rrsavepannel);
		rrquickreply = (RelativeLayout) promptsView
				.findViewById(R.id.rrquickreply);
		imagebitmap = null;

		// tvControl= (TextView) promptsView.findViewById(R.id.tvcontol);
		ivClose = (ImageView) promptsView.findViewById(R.id.ivclose);
		Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
				"verdana.ttf");
		spCategory = (Spinner) promptsView.findViewById(R.id.spCategory);
		// tvheading = (TextView) promptsView.findViewById(R.id.tvheading);

		// tvheading.setTypeface(face);
		spCategory.setVisibility(View.GONE);

		songProgressBar = (SeekBar) promptsView
				.findViewById(R.id.songProgressBar);

		etTitle = (EditText) promptsView.findViewById(R.id.ettitle);
		etMood = (EditText) promptsView.findViewById(R.id.etmood);
		// tvAdd = (EditText) promptsView.findViewById(R.id.etyouradd);
		ivDispalyImg = (ImageView) promptsView.findViewById(R.id.ivaddimage);

		llPublic = (RelativeLayout) promptsView.findViewById(R.id.llpublic);
		llUser = (RelativeLayout) promptsView.findViewById(R.id.lluser);
		ivMaximize = (ImageView) promptsView.findViewById(R.id.ivmaximize);
		tgmakepublic = (ToggleButton) promptsView
				.findViewById(R.id.tgmakepublic);
		tguser = (ToggleButton) promptsView.findViewById(R.id.tganonymous);
		ivMinimize = (ImageView) promptsView.findViewById(R.id.ivminimize);
		tvUserId.setText(userCode);
		etMood.setVisibility(View.GONE);
		llPublic.setVisibility(View.GONE);
		llUser.setVisibility(View.GONE);
		ivMinimize.setVisibility(View.GONE);
		llUser.setVisibility(View.GONE);
		tvComment.setVisibility(View.GONE);
		videoUrl = null;

		
			rrsavepannel.setVisibility(View.GONE);
			quicksharepannel.setVisibility(View.GONE);

		
		// tvAdd.setVisibility(View.GONE);
		// ivAddPhoto.setVisibility(View.GONE);
		// spCategory.setVisibility(View.GONE);
		//popupControl = "classifield";
	//	new getCategoryAsyncTask().execute();
		// tvControl.setText("6 Sec");
		tgmakepublic.setChecked(true);
		tguser.setChecked(true);
		ivstart.setBackgroundResource(R.drawable.record_inactive);
		ivplay.setBackgroundResource(R.drawable.play_inactive);
		ivstop.setBackgroundResource(R.drawable.stop_inactive);

		

		alertDialog = new Dialog(getActivity(), R.style.Dialog_No_Border);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		alertDialog.setContentView(promptsView);
		alertDialog.show();

		

		if (m != null) {

			m.stop();
		}
		customHandler.removeCallbacksAndMessages(null);
		mHandler.removeCallbacksAndMessages(null);
		playBeep("B");

		ivstart.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					if (myAudioRecorder != null) {
						// delay()

						stopRecord();
						// playBeep("S");

					}

					if (m != null) {

						m.stop();
					}
					customHandler.removeCallbacksAndMessages(null);
					mHandler.removeCallbacksAndMessages(null);
					playBeep("B");

				} catch (Exception e) {

				}
			}
		});

		

		ivstop.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				// stopPlay();

				if (myAudioRecorder != null) {
					// delay()

					stopRecord();
					playBeep("S");

				}

				if (m != null) {

					m.stop();
				}
			}
		});

		ivplay.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					play();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		ivDispalyImg.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					displayPhotoSelect("record");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		ivMaximize.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					etMood.setVisibility(View.VISIBLE);
					llPublic.setVisibility(View.VISIBLE);
					llUser.setVisibility(View.VISIBLE);
					ivMinimize.setVisibility(View.VISIBLE);
					ivMaximize.setVisibility(View.GONE);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		ivMinimize.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					etMood.setVisibility(View.GONE);
					llPublic.setVisibility(View.GONE);
					llUser.setVisibility(View.GONE);
					ivMinimize.setVisibility(View.GONE);
					ivMaximize.setVisibility(View.VISIBLE);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		ivClose.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					if (myAudioRecorder != null) {

						myAudioRecorder.release();
					}
					if (m != null) {
						m.stop();
					}
					customHandler.removeCallbacksAndMessages(null);
					mHandler.removeCallbacksAndMessages(null);
					isActivePopup = true;
					alertDialog.cancel();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		/*tguser.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				String usercontrol = (String) tguser.getText();

				if (tguser.isChecked()) {

					tvUserId.setText(userCode);

				} else {
					tvUserId.setText("Annunymous");
				}

			}
		});*/

		
		tvquickreply.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (myAudioRecorder != null) {

					myAudioRecorder.release();
				}
				if (m != null) {
					m.stop();
				}
				customHandler.removeCallbacksAndMessages(null);
				mHandler.removeCallbacksAndMessages(null);
				isActivePopup = true;
				
				String titleStr = ((TextView) etTitle).getText().toString();
				
				chatMessages=new ChatMessages();
				//	voiceyReply = new VoiceyReply();
					if (titleStr.length() > 0) {
						 
						chatMessages.setChatMessage(titleStr);
					
					} else {
						// title = "Voicey ID " + userCode;
					}
					
					File f = new File(Constants.temp_url);

					if (f.exists()) {
					
					int min = 0;
					int max = 100;

					Random r = new Random();
					int i1 = r.nextInt(max - min + 1) + min;
					
					try {
						InputStream in = new FileInputStream(Constants.temp_url);
					
					OutputStream out = new FileOutputStream(Environment
							.getExternalStorageDirectory()
							+ "/"
							+ Constants.app_folder
							+ "/"
							+ i1
							+ ".3gp");
					
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();

					f.delete();
					
					chatMessages.setAudio(Environment
							.getExternalStorageDirectory()
							+ "/"
							+ Constants.app_folder
							+ "/"
							+ i1
							+ ".3gp");
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
					
					
					
					chatMessages.setImgBiteMap(imagebitmap);
					chatMessages.setType("out");
					if(videoUrl!=null){
						chatMessages.setVideo(videoUrl);
						
					}
					
					//
					alertDialog.cancel();
					addChatMessage(chatMessages);
					
				/*audioInfo = new AudioInfo();
				String title = ((TextView) etTitle).getText().toString();
				if (title.length() == 0) {
					// title = "Voicey ID " + userCode;

				}

				audioInfo.setTitle(title);

				audioInfo.setPublic_control("0");
				audioInfo.setUser_control("1");

				if (popupControl.equals("classifield")) {
					// String yourAd = ((TextView) tvAdd).getText().toString();
					// audioInfo.setYourAd(yourAd);

					if (videoUrl != null) {

						audioInfo.setVideoFilePath(videoUrl);

					}

					audioInfo.setImagebitmap(imagebitmap);

					if (imagebitmap != null) {

						File file = new File(Constants.image_folder + "/"
								+ userCode + ".jpeg");
						audioInfo.setImageName(userCode);

						if (file.exists())
							file.delete();
						try {
							FileOutputStream out = new FileOutputStream(file);
							imagebitmap.compress(Bitmap.CompressFormat.JPEG,
									90, out);
							out.flush();
							out.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					audioInfo.setCategoryId(categoryId);
				}

				audioInfo.setUserid(userId);
				audioInfo.setUser_code(userCode);
				audioInfo.setType(popupControl);
				try {
					File f = new File(Constants.temp_url);

					if (f.exists()) {

						InputStream in = new FileInputStream(Constants.temp_url);
						OutputStream out = new FileOutputStream(Environment
								.getExternalStorageDirectory()
								+ "/"
								+ Constants.app_folder
								+ "/"
								+ userCode
								+ ".3gp");

						audioInfo.setFileName(userCode);

						byte[] buf = new byte[1024];
						int len;
						while ((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
						}
						in.close();
						out.close();

						f.delete();

						alertDialog.cancel();

						displayVoiceyFriend();

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

			}
		});

		

	}
	
	void playBeep(final String type) {
		MediaPlayer mPlayer;
		if (type.equals("B")) {
			mPlayer = MediaPlayer.create(getActivity(), R.raw.toest_sound);
		} else if (type.equals("S")) {
			mPlayer = MediaPlayer.create(getActivity(), R.raw.toest_sound);

		}

		else {
			mPlayer = MediaPlayer.create(getActivity(), R.raw.start_audio);

		}

		mPlayer.start();

		mPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				if (type.equals("R")) {

					playBeep("B");

				} else if (type.equals("S")) {

					delaybeforeplay();

				}

				else if (type.equals("B")) {

					startRecord();

				}

			}
		});

	}
	
	void delaybeforeplay() {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {

					play();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// your code here
			}
		}, 1000);

	}
	
	private void startRecord() {
		try {

			File f = new File(Constants.temp_url);

			if (f.exists()) {
				f.delete();
			}

			ivstart.setBackgroundResource(R.drawable.record_active);

			// ivstop.setVisibility(View.VISIBLE);
			// ivstart.setVisibility(View.GONE);
			startTime = SystemClock.uptimeMillis();
			//tvComment.setText("Recording...");

			tvmaxTime.setText("06:00");
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

		ivstart.setBackgroundResource(R.drawable.record_inactive);
		timeSwapBuff += timeInMilliseconds;
		customHandler.removeCallbacks(updateTimerThread);

		// ivstart.setEnabled(false);
		// ivplay.setVisibility(View.VISIBLE);

		// rlbottombutton.setVisibility(View.GONE);
		// songProgressBar.setVisibility(View.GONE);
		// tvTimmer.setVisibility(View.GONE);
		// tvmaxTime.setVisibility(View.GONE);
		// llbotombitton.setVisibility(View.VISIBLE);

		// btn_PlayAudio.setEnabled(true);
		myAudioRecorder.stop();
		myAudioRecorder.release();
		myAudioRecorder = null;
		// Toast.makeText(getApplicationContext(),
		// "Audio recorded successfully",
		// Toast.LENGTH_LONG).show();
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

			int milliseconds = (int) (updatedTime % 60);

			if (mins == 0 && secs <= 6) {
				Double percentage = ((double) secs / maxSec) * 100;
				songProgressBar.setProgress(percentage.intValue());

				Integer secInt = new Integer(secs);
				Integer maxCount = new Integer(Constants.total_audio_time);
				Integer actualSec = maxCount - secInt;

				tvTimmer.setText("" + String.format("%02d", actualSec) + ":"
						+ milliseconds);
				customHandler.postDelayed(this, 50);
			} else {
				tvTimmer.setText("0:00");
				/*
				 * stopRecord(); playBeep("S");
				 * 
				 * try { play();
				 * 
				 * } catch (Exception e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */

				stopRecord();
				playBeep("S");

				// delay();

			}

		}

	};
	
	private void play() throws IllegalArgumentException, SecurityException,
	IllegalStateException, IOException {

m = new MediaPlayer();
m.setDataSource(outputFile);
m.prepare();
m.start();
ivplay.setBackgroundResource(R.drawable.play_active);
ivstop.setBackgroundResource(R.drawable.stop_inactive);
// ivplay.setVisibility(View.GONE);
// ivstop.setVisibility(View.VISIBLE);
//tvComment.setText("Playing...");

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
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = m.getDuration();
			long currentDuration = m.getCurrentPosition();

			int secs = (int) (currentDuration / 1000);
			int maxsec = (int) (totalDuration / 1000);

			int currentmilisec = (int) (currentDuration % 60);
			int totalmilisec = (int) (totalDuration % 60);

			secs = secs % 60;
			maxsec = maxsec % 60;
			Double percentage = ((double) secs / maxsec) * 100;
			songProgressBar.setProgress(percentage.intValue());

			tvTimmer.setText("" + String.format("%02d", secs) + ":"
					+ currentmilisec);

			// Displaying Total Duration time
			// tvTimmer.setText("" + utils.milliSecondsToTimer(totalDuration));
			// Displaying time completed playing
			if (maxsec == 6) {
				totalmilisec = 00;

			}
			tvmaxTime.setText("" + String.format("%02d", maxsec) + ":"
					+ totalmilisec);

			// Updating progress bar

			// Log.d("Progress", ""+progress);

			// Running this thread after 100 milliseconds
			if (secs < maxsec) {
				mHandler.postDelayed(this, 100);
			}
		}
	};



	private void stopPlay() {

		m.stop();

		ivplay.setBackgroundResource(R.drawable.play_inactive);
		ivstop.setBackgroundResource(R.drawable.stop_active);

		//tvComment.setText("Play your voicey");

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		File imgFile;
		switch (requestCode) {
		
		case (RESULT_LOAD_IMAGE):
			if (resultCode == Activity.RESULT_OK) {

				try {

					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getActivity().getContentResolver()
							.query(selectedImage, filePathColumn, null,
									null, null);
					cursor.moveToFirst();

					int columnIndex = cursor
							.getColumnIndex(filePathColumn[0]);

					String picturePath = cursor.getString(columnIndex);
					cursor.close();

					imgFile = new File(picturePath);

					// new LoginActss().execute();

					try {

						// Bundle extras =
						// get the cropped bitmap
						final Bitmap thePic = BitmapFactory
								.decodeFile(picturePath);
						imagebitmap = thePic;
						ivDispalyImg.setImageBitmap(thePic);

						
					}
					
					catch (ActivityNotFoundException anfe) {
						// display an error message
						String errorMessage = "Whoops - your device doesn't support the crop action!";

					}

				} catch (Exception ex) {
					// Toast.makeText(this, ex.getMessage(),
					// Toast.LENGTH_LONG);
				}
			}
			break;
		case (ACTIVITY_TAKE_PHOTO):
			if (resultCode == Activity.RESULT_OK) {

				try {
				
					final Bitmap thePic = (Bitmap) data.getExtras().get(
							"data");
					imagebitmap = thePic;
					ivDispalyImg.setImageBitmap(thePic);
					
				}
				// respond to users whose devices do not support the crop
				// action
				catch (ActivityNotFoundException anfe) {
					// display an error message
					String errorMessage = "Whoops - your device doesn't support the crop action!";

				}
			}
			break;
		
		case (RESULT_LOAD_VIDEO):
			if (resultCode == Activity.RESULT_OK && null != data) {
				// String path=data.getData();
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getActivity().getContentResolver().query(
						selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				videoUrl = picturePath;

				Bitmap bmThumbnail;

				// MICRO_KIND: 96 x 96 thumbnail
				bmThumbnail = ThumbnailUtils.createVideoThumbnail(
						picturePath, Thumbnails.MICRO_KIND);

				imagebitmap = bmThumbnail;
				ivDispalyImg.setImageBitmap(bmThumbnail);

			}
		}
		
		}
	
		
		
		
}
