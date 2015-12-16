package com.voicey.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.voicey.activity.R;
import com.voicey.adapter.ChatMessagesListAdapter;
import com.voicey.model.ChatMessages;
import com.voicey.utils.Constants;


public class ChatMessageFragment extends Fragment { 
	
	
	ImageView ivsend,ivrecord,ivaddimage,ivDispalyImg;
	EditText etMessageText;
	TextView tvFriendName;
	public ChatMessagesListAdapter chatMessagesAdapter;
	Bitmap imagebitmap;
	ListView chatListView;
	String videoUrl;
	private final static int ACTIVITY_TAKE_PHOTO = 1;
	static public final int RESULT_LOAD_IMAGE = 267;
	private static final int RESULT_LOAD_VIDEO = 5;
	
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
			etMessageText=(EditText)v.findViewById(R.id.etmessageText);
			chatListView=(ListView)v.findViewById(R.id.chatlistview);
			ivrecord=(ImageView)v.findViewById(R.id.ivrecord);
			ivaddimage=(ImageView)v.findViewById(R.id.ivaddimage);
			tvFriendName=(TextView)v.findViewById(R.id.tvFriendName);
			ivsend.setVisibility(View.GONE);
			
			String strtext = getArguments().getString("friendName");
			tvFriendName.setText(strtext);
			
			ivaddimage.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					
					displayPhotoSelect();

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
					
				
					
				}
			});
		

			
		
		} catch (Exception e) {

		e.printStackTrace();
	}

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
	
	void displayPhotoSelect() {

		TextView camera, gallery, video, tvcancel;
		final Dialog dialog = new Dialog(getActivity());

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.photo_select);

		camera = (TextView) dialog.findViewById(R.id.tvcamera);
		gallery = (TextView) dialog.findViewById(R.id.tvgallery);
		video = (TextView) dialog.findViewById(R.id.tvvideo);
		tvcancel = (TextView) dialog.findViewById(R.id.tvcancel);

		dialog.show();

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
				displayimageReply();
				takePictureButtonClicked();

				dialog.dismiss();
			}
		});

		gallery.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();
				imagebitmap = null;
				displayimageReply();
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
				displayimageReply();
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
