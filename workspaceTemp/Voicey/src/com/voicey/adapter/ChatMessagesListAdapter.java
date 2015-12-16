package com.voicey.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.rockerhieu.emojicon.EmojiconTextView;
import com.voicey.activity.R;
import com.voicey.activity.TouchImageView;
import com.voicey.model.AudioInfo;
import com.voicey.model.ChatMessages;
import com.voicey.utils.Constants;

public class ChatMessagesListAdapter extends ArrayAdapter<ChatMessages> {
	Context context;
	ViewHolder holder = null;
	List<ChatMessages> chatMessageList;
	ChatMessages chatMessages;
	TouchImageView touchImage;
	private MediaPlayer mediaPlayer;
	int playposition = -1;
	SeekBar songProgressBar;
	private Handler mHandler = new Handler();
	
	public ChatMessagesListAdapter(Context context, int resourceId,
			List<ChatMessages> chatMessageList) {
		super(context, resourceId, chatMessageList);
		this.context = context;
		this.chatMessageList = chatMessageList;
		
	}
	private class ViewHolder {
		TextView  tvFromvalue;
		public EmojiconTextView message_left,message_right;
		public ImageView messageImage_left,messageImage_right,ivfreme_left,ivfreme_right,ivaudioplay;
		public LinearLayout contentBody_left;
		public LinearLayout contentBody_right,llbuttonleft;
		SeekBar songProgressBar;
	

	}

	
	public View getView(final int position, View convertView, ViewGroup parent) {
		chatMessages = chatMessageList.get(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.chat_message, null);
            holder = new ViewHolder();
			holder.message_left = (EmojiconTextView) convertView
					.findViewById(R.id.chat_compact_message_left);
			holder.message_right = (EmojiconTextView) convertView
					.findViewById(R.id.chat_compact_message_right);
			holder.contentBody_left = (LinearLayout) convertView
					.findViewById(R.id.contentBody_left);
			holder.llbuttonleft = (LinearLayout) convertView
					.findViewById(R.id.llbuttonleft);
			holder.messageImage_right= (ImageView) convertView
					.findViewById(R.id.chat_compact_image_right);
			holder.ivfreme_left= (ImageView) convertView
					.findViewById(R.id.ivfreme_left);
			holder.ivfreme_right= (ImageView) convertView
					.findViewById(R.id.ivfreme_right);
			holder.ivaudioplay= (ImageView) convertView
					.findViewById(R.id.ivaudioplay);
			holder.songProgressBar= (SeekBar) convertView
					.findViewById(R.id.songProgressBar);
			holder.contentBody_right = (LinearLayout) convertView
					.findViewById(R.id.contentBody_right);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		holder.contentBody_right.setId(position);
		holder.messageImage_right.setId(position);
		holder.ivaudioplay.setId(position);
		holder.ivaudioplay.setTag("play");
		   
		
		if(chatMessages.getType().equals("IN")){
			
			holder.contentBody_right.setVisibility(View.GONE);
			holder.contentBody_left.setVisibility(View.VISIBLE);
			holder.message_left.setText(chatMessages.getChatMessage());
			
		}else{
			
			if(chatMessages.getChatMessage()!=null&&chatMessages.getChatMessage().length()>0){
				holder.message_right.setText(chatMessages.getChatMessage());
				holder.message_right.setVisibility(View.VISIBLE);
			}else{
				
				holder.message_right.setVisibility(View.GONE);
			}
			holder.message_right.setText(chatMessages.getChatMessage());
			holder.contentBody_right.setVisibility(View.VISIBLE);
			holder.contentBody_left.setVisibility(View.GONE);
			
			if(chatMessages.getImgBiteMap()!=null){
				holder.messageImage_right.setVisibility(View.VISIBLE);
				holder.messageImage_right.setImageBitmap(chatMessages.getImgBiteMap());
				if(chatMessages.getVideo()!=null){
					holder.ivfreme_right.setVisibility(View.VISIBLE);
					holder.ivfreme_left.setVisibility(View.VISIBLE);
				}else{
					holder.ivfreme_right.setVisibility(View.GONE);
					holder.ivfreme_left.setVisibility(View.GONE);
				}
			}else{
				holder.messageImage_right.setVisibility(View.GONE);
				holder.ivfreme_right.setVisibility(View.GONE);
				holder.ivfreme_left.setVisibility(View.GONE);
			}
			
		}
		
		if(chatMessages.getIsDispalyDetail().equals("0")){
			
			 holder.llbuttonleft.setVisibility(View.GONE);
		}else{
			holder.llbuttonleft.setVisibility(View.VISIBLE);
		}
		
		if(chatMessages.getAudio()!=null){
			
			holder.ivaudioplay.setVisibility(View.VISIBLE);
			holder.songProgressBar.setVisibility(View.VISIBLE);
			
		}else{
			
			holder.ivaudioplay.setVisibility(View.GONE);
			holder.songProgressBar.setVisibility(View.GONE);
			
			
		}
		
		/*if (playposition == position) {

			try {
				
				holder.ivaudioplay.setImageResource(R.drawable.record_inactive);

				
					String url = chatMessages.getAudio();
					mediaPlayer = new MediaPlayer();
					

					if (mediaPlayer != null) {
						mediaPlayer.stop();
					}
					mediaPlayer.setDataSource(url);

					mediaPlayer.prepare();
					mediaPlayer.start();

					songProgressBar = holder.songProgressBar;
					songProgressBar.setProgress(0);
					songProgressBar.setMax(100);
					// mHandler.removeCallbacksAndMessages(null);
					mHandler.postDelayed(updateSongProgress, 10);
					mediaPlayer
							.setOnCompletionListener(new OnCompletionListener() {
								public void onCompletion(MediaPlayer mp) {

									// songProgressBar.setProgress(0);
									// songProgressBar.setMax(100);
									playposition = -1;
								//	i = 0;
									notifyDataSetChanged();
									holder.ivaudioplay.setImageResource(R.drawable.play);

									// audioInfo.setPlayAudio("NO");
									// productRegistrationList.set(position,
									// audioInfo);

								}

							});
				//}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			//holder.ivaudioplay.setImageResource(R.drawable.play);

		}*/
		
		holder.messageImage_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				chatMessages = chatMessageList.get(v.getId());
				
				if(chatMessages.getVideo()!=null){
					
					displayImageVideo(chatMessages,"video");
				}else{
					
					displayImageVideo(chatMessages,"image");
				}
				
				// TODO Auto-generated method stub
				
			}
		});
		
		holder.contentBody_right.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				
				chatMessages = chatMessageList.get(view.getId());
				
				if(chatMessages.getIsDispalyDetail().equals("0")){
					
					chatMessages.setIsDispalyDetail("1");
					
				}else{
					
					chatMessages.setIsDispalyDetail("0");
				}
				
				
				notifyDataSetChanged();
				
				return true;
			}        	
        });
		   
		holder.ivaudioplay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				
				if(((ImageView)v).getTag().toString().equals("play")){
				chatMessages = chatMessageList.get(v.getId());
				playposition = v.getId();
				
				//holder.ivaudioplay.setImageResource(R.drawable.record_inactive);

				try {
				String url = chatMessages.getAudio();
				mediaPlayer = new MediaPlayer();
				

				if (mediaPlayer != null) {
					mediaPlayer.stop();
				}
				mediaPlayer.setDataSource(url);
				
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                     @Override
                     public void onPrepared(MediaPlayer mp) {
                         mp.start();
                         songProgressBar = holder.songProgressBar;
         				songProgressBar.setProgress(0);
         				songProgressBar.setMax(100);
         				// mHandler.removeCallbacksAndMessages(null);
         				mHandler.postDelayed(updateSongProgress, 10);
                         ((ImageView)v).setTag("pause");
                         ((ImageView)v).setImageResource(R.drawable.record_inactive);
                     }
                 });
				mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                     @Override
                     public void onCompletion(MediaPlayer mp) {
                    	// mediaPlayer.release();
                    	// mediaPlayer = null;
                    	 ((ImageView)v).setTag("play");
                    	 ((ImageView)v).setImageResource(R.drawable.play);
                     }
                 });
				

				/*mediaPlayer.prepare();
				mediaPlayer.start();

				songProgressBar = holder.songProgressBar;
				songProgressBar.setProgress(0);
				songProgressBar.setMax(100);
				// mHandler.removeCallbacksAndMessages(null);
				mHandler.postDelayed(updateSongProgress, 10);
				mediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {
							public void onCompletion(MediaPlayer mp) {

								// songProgressBar.setProgress(0);
								// songProgressBar.setMax(100);
								playposition = -1;
							
							}

						});*/
				
			
				
				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
			}else{
			mediaPlayer.pause();;
			
				 ((ImageView)v).setTag("play");
            	 ((ImageView)v).setImageResource(R.drawable.play);
             }
			 
			}

		});
		
		
		
		
		
		
		
		return convertView;
	}
	
	Runnable updateSongProgress = new Runnable() {
		public void run() {

			long totalDuration = mediaPlayer.getDuration();
			long currentDuration = mediaPlayer.getCurrentPosition();

			int secs = (int) (currentDuration / 1000);
			int maxsec = (int) (totalDuration / 1000);

			Double percentage = ((double) secs / maxsec) * 100;

			songProgressBar.setProgress(percentage.intValue());
			if (secs < maxsec) {
				mHandler.postDelayed(this, 10);
			}
		}
	};
	
	void displayImageVideo(final ChatMessages cm, final String type) {
		TextView tvCancel, tvSave;
		VideoView myVideoView;
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.image_video_display, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context,
				android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);

		// Dialog dialog=new
		// Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();

		// final AlertDialog alertDialog = alertDialogBuilder.create();
		tvCancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvSave = (TextView) promptsView.findViewById(R.id.tvsave);
		myVideoView = (VideoView) promptsView.findViewById(R.id.VideoView);

		touchImage = (TouchImageView) promptsView
				.findViewById(R.id.imgtough);

		if (type.equals("video")) {
			touchImage.setVisibility(View.GONE);
			String SrcPath = cm.getVideo();

			myVideoView.setVideoPath(SrcPath);
			myVideoView.setMediaController(new MediaController(
					context));
			myVideoView.requestFocus();
			myVideoView.start();

		} else if (type.equals("image")) {

			myVideoView.setVisibility(View.GONE);

			if (cm.getImgBiteMap() != null) {
				
				
				touchImage.setImageBitmap(cm.getImgBiteMap());

				/*Bitmap largeIcon = BitmapFactory.decodeResource(
						getResources(), R.drawable.loading_img);
				// touchImage.setImageBitmap(largeIcon);
				new ImageDownloader().execute();*/

			}

		}

		tvCancel.setOnClickListener(new OnClickListener() {

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

		tvSave.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					/*shareFriendList.clear();
					shareGroupList.clear();
					commentsPosition = Integer.parseInt(audioInfo.getId());

					if (type.equals("image")) {

						displayVoiceyFriend(audioInfo, "image");
					} else if (type.equals("video")) {

						displayVoiceyFriend(audioInfo, "video");

					}

					alertDialog.cancel();*/

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}
	
	
	
	

}
