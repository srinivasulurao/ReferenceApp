package com.voicey.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.voicey.activity.ImageLoader;
import com.voicey.activity.R;
import com.voicey.activity.TouchImageView;
import com.voicey.fragment.InboxFragment;
import com.voicey.model.AudioInfo;
import com.voicey.model.Friend;
import com.voicey.model.FriendMessages;
import com.voicey.model.FriendShareAudio;
import com.voicey.model.InviteGroup;
import com.voicey.model.ReplyMessages;
import com.voicey.model.VoiceyReply;
import com.voicey.utils.Constants;
import com.voicey.webservices.Webservices;

public class FriendMessageListAdapter extends ArrayAdapter<FriendMessages> {
	Context context;
	List<FriendMessages> friendMessageList;
	FriendMessages friendMessages;
	AudioInfo audioInfo;
	ViewHolder holder = null;
	Typeface face;
	Fragment inboxFragment;
	String userCode, userId, videoUrl;
	SeekBar songProgressBar, songReplyProgressBar;
	private Handler mHandler = new Handler();
	Boolean isMainReply=false;
	public ImageLoader imageLoader;
	private MediaPlayer mediaPlayer;
	InviteGroup inviteGroup;
	Friend friends;
	List<String> invitefriendId = new ArrayList<String>();
	int playposition = -1;
	int playmainposition = -1;
	int replyplayposition = -1;
	int replymainposition = -1;
	ReplyMessages replyMessages;
	ListView lvFriendList;
	Bitmap imagebitmap;
	ImageView ivaddimage;
	TouchImageView touchImage;
	VoiceyReply voiceyReply;
	private final static int ACTIVITY_TAKE_PHOTO = 1;
	static public final int RESULT_LOAD_IMAGE = 267;
	private static final int RESULT_LOAD_VIDEO = 5;
	List<Friend> friendList;
	List<Friend> shareFriendList = new ArrayList<Friend>();
	List<String> sharefriendId = new ArrayList<String>();
	List<Friend> shareGroupList = new ArrayList<Friend>();
	SharedPreferences sharedPreferences;
	int commentsPosition, expandPosition;
	int j = 0;
	FriendShareAudio friendShareAudio;
	Webservices Webservices = new Webservices();

	public FriendMessageListAdapter(Context context, int resourceId,
			List<FriendMessages> friendMessageList, InboxFragment inboxFragment) {
		super(context, resourceId, friendMessageList);
		this.context = context;
		this.friendMessageList = friendMessageList;
		this.inboxFragment = inboxFragment;
		imageLoader = new ImageLoader(context);

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		userCode = sharedPreferences.getString("userCode", null);
		userId = sharedPreferences.getString("userId", null);

	}

	/* private view holder class */
	private class ViewHolder {
		TextView tvCount, tvFromvalue, tvmessagecount;
		EmojiconTextView tvTitle;
		ImageView ivExpand, ivsend, ivaddimage, ivsmily,ivinvite;
		LinearLayout llfriendmessages;
		EmojiconEditText mEditEmojicon;
		MutableWatcher mWatcher;

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		friendMessages = friendMessageList.get(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.friend_message_list, null);

			holder = new ViewHolder();
			holder.tvFromvalue = (TextView) convertView
					.findViewById(R.id.tvfromValue);
			holder.tvmessagecount = (TextView) convertView
					.findViewById(R.id.tvmessagecount);
			holder.ivExpand = (ImageView) convertView
					.findViewById(R.id.ivexpand);
			holder.llfriendmessages = (LinearLayout) convertView
					.findViewById(R.id.llfriendmessages);
			holder.ivinvite = (ImageView) convertView
					.findViewById(R.id.ivinvite);
			
			holder.mWatcher = new MutableWatcher();
			holder.ivsend = (ImageView) convertView.findViewById(R.id.ivsend);
			holder.ivsmily = (ImageView) convertView.findViewById(R.id.ivsmily);
			holder.ivaddimage = (ImageView) convertView
					.findViewById(R.id.ivaddimage);
			holder.mEditEmojicon = (EmojiconEditText) convertView
					.findViewById(R.id.editEmojicon);
			holder.mWatcher = new MutableWatcher();
			holder.mEditEmojicon.addTextChangedListener(holder.mWatcher);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.ivExpand.setId(position);
		holder.ivinvite.setId(position);
		holder.ivsend.setId(position);
		holder.ivsend.setVisibility(View.GONE);
		holder.ivsend.setTag(holder);
		holder.ivaddimage.setId(position);
		holder.mEditEmojicon.setTag(holder);
		holder.mWatcher.setActive(false);
		holder.mWatcher.setPosition(position);
		holder.mWatcher.setActive(true);
		holder.mWatcher.setViewholder(holder);

		face = Typeface.createFromAsset(context.getAssets(), "ARIAL.TTF");
		holder.llfriendmessages.removeAllViews();
		if (friendMessages.getMessageShow().equals("true")) {
			if (friendMessages.getMessageList().size() > 0) {
				try {
					int pos = -1;
					for (AudioInfo ai : friendMessages.getMessageList()) {
						pos++;

						ImageView ivFremeright, ivFremeLeft, ivimage, ivaudioplay, ivaddimage, ivsend;
						TextView tvTitle, tvsentDate, tvuser, tvcc;
						SeekBar sbAudioPlay;
						EmojiconEditText mEditEmojicon;
						MessageTextWatcher textWatcher;
						RelativeLayout rrcontrolsend;
						LinearLayout llreplymessages;

						LayoutInflater lfPrevious = (LayoutInflater) context
								.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
						final View comment;

						comment = lfPrevious.inflate(R.layout.friend_message,
								null);
						tvTitle = (TextView) comment.findViewById(R.id.tvtitle);
						tvTitle.setText(ai.getTitle());
						tvTitle.setTypeface(face);
						tvsentDate = (TextView) comment
								.findViewById(R.id.tvdate);
						tvsentDate.setTypeface(face);
						tvuser = (TextView) comment
								.findViewById(R.id.tvfromValue);
						tvuser.setTypeface(face);
						tvcc = (TextView) comment.findViewById(R.id.tvcc);

						llreplymessages = (LinearLayout) comment
								.findViewById(R.id.llreplymessages);
						mEditEmojicon = (EmojiconEditText) comment
								.findViewById(R.id.editEmojicon);
						rrcontrolsend = (RelativeLayout) comment
								.findViewById(R.id.rrcontrolsend);
						ivFremeright = (ImageView) comment
								.findViewById(R.id.ivfreme_right);
						ivFremeLeft = (ImageView) comment
								.findViewById(R.id.ivfreme_left);
						ivimage = (ImageView) comment
								.findViewById(R.id.ivimage);
						ivaudioplay = (ImageView) comment
								.findViewById(R.id.ivaudioplay);
						ivaddimage = (ImageView) comment
								.findViewById(R.id.ivaddimage);
						ivsend = (ImageView) comment.findViewById(R.id.ivsend);
						textWatcher = new MessageTextWatcher();

						songProgressBar = (SeekBar) comment
								.findViewById(R.id.songProgressBar);

						songProgressBar.setProgress(0);
						songProgressBar.setMax(100);
						ivsend.setId(pos);
						ivsend.setTag(comment);
						ivaudioplay.setId(pos);
						ivaudioplay.setTag(position);
						ivimage.setId(pos);
						ivimage.setTag(position);
						ivaddimage.setId(pos);
						ivaddimage.setTag(position);

						textWatcher.setActive(false);
						textWatcher.setPosition(pos);
						textWatcher.setActive(true);
						textWatcher.setView(comment);
						// ivaddimage.setVisibility(View.GONE);
						rrcontrolsend.setVisibility(View.GONE);
						mEditEmojicon.addTextChangedListener(textWatcher);

						tvuser.setText(ai.getFromUserName());
						if (ai.getImageName() != null
								&& ai.getImageName().length() > 0) {

							ivimage.setVisibility(View.VISIBLE);
							String Url = Constants.image_url
									+ ai.getImageName();

							if (ai.getVideoFilePath() != null
									&& ai.getVideoFilePath().length() > 0) {
								ivFremeright.setVisibility(View.VISIBLE);
								ivFremeLeft.setVisibility(View.VISIBLE);

							} else {

								ivFremeright.setVisibility(View.GONE);
								ivFremeLeft.setVisibility(View.GONE);
							}

							imageLoader.DisplayImage(Url, ivimage);

						} else {

							ivimage.setVisibility(View.GONE);
							ivFremeright.setVisibility(View.GONE);
							ivFremeLeft.setVisibility(View.GONE);

						}

						if ((ai.getFileName() != null && ai.getFileName()
								.length() > 0)) {
							ivaudioplay.setVisibility(View.VISIBLE);
							songProgressBar.setVisibility(View.VISIBLE);

							if (playmainposition == position
									&& playposition == pos) {

								try {
									if (j == 0) {
										j++;

									} else {

										String url = Constants.audio_url
												+ ai.getFileName();
										mediaPlayer = new MediaPlayer();

										if (mediaPlayer != null) {
											mediaPlayer.stop();
										}
										mediaPlayer.setDataSource(url);

										mediaPlayer.prepare();
										mediaPlayer.start();

										// songProgressBar = sbAudioPlay;
										songProgressBar.setProgress(0);
										songProgressBar.setMax(100);
										// mHandler.removeCallbacksAndMessages(null);
										mHandler.postDelayed(
												updateReplySongProgress, 10);
										mediaPlayer
												.setOnCompletionListener(new OnCompletionListener() {
													public void onCompletion(
															MediaPlayer mp) {

														playmainposition = -1;
														playposition = -1;
														j = 0;
														notifyDataSetChanged();

													}

												});
									}

								} catch (Exception e) {
									e.printStackTrace();
								}

							}

						} else {
							ivaudioplay.setVisibility(View.GONE);
							songProgressBar.setVisibility(View.GONE);

						}

						tvcc.setOnClickListener(new OnClickListener() {

							@SuppressLint("NewApi")
							public void onClick(View v) {

							}
						});

						ivsend.setOnClickListener(new OnClickListener() {

							@SuppressLint("NewApi")
							public void onClick(View v) {
								// friendMessages =
								// friendMessageList.get(v.getId());
								audioInfo = friendMessages.getMessageList()
										.get(v.getId());

								View comment = (View) v.getTag();
								String title = ((TextView) comment
										.findViewById(R.id.editEmojicon))
										.getText().toString();

								if (title.length() == 0) {

									Toast.makeText(context,
											"Enter your message",
											Toast.LENGTH_LONG).show();

								} else if (title.length() > 140) {
									Toast.makeText(
											context,
											"Title should be less then 140 character.",
											Toast.LENGTH_LONG).show();

								} else {

									StringBuffer friendBuffer = new StringBuffer();
									/*
									 * if (audioInfo.getCcFriendList().size() >
									 * 0) { for (Friend f :
									 * audioInfo.getCcFriendList()) {
									 * 
									 * friendBuffer.append(f.getFriendId() +
									 * ",");
									 * 
									 * } }
									 */
									friendBuffer.append(audioInfo
											.getUser_code() + ",");
									voiceyReply = new VoiceyReply();
									voiceyReply.setId(audioInfo.getId());
									voiceyReply.setTitle(title);
									voiceyReply.setUsercode(userCode);
									voiceyReply
											.setSharedFriendCode(friendBuffer
													.toString());
									voiceyReply.setPublic_control("0");
									voiceyReply.setUser_control("1");
									voiceyReply.setUserid(userId);
									voiceyReply.setType("classifield");
									voiceyReply.setSharedGroupCode(audioInfo.getGroupId());

									EmojiconEditText mEditEmojicon = (EmojiconEditText) comment
											.findViewById(R.id.editEmojicon);

									mEditEmojicon.setText(null);
									commentsPosition = v.getId();

									InputMethodManager inputManager = (InputMethodManager) context
											.getSystemService(context.INPUT_METHOD_SERVICE);

									inputManager.hideSoftInputFromWindow(
											((Activity) context)
													.getCurrentFocus()
													.getWindowToken(),
											InputMethodManager.HIDE_NOT_ALWAYS);
									// flSmilys.setVisibility(View.GONE);
									new ReplayText().execute();
								}
							}
						});

						ivaddimage.setOnClickListener(new OnClickListener() {

							@SuppressLint("NewApi")
							public void onClick(View v) {

								friendMessages = friendMessageList
										.get((Integer) v.getTag());
								audioInfo = friendMessages.getMessageList()
										.get(v.getId());
								// displayimageReply(audioInfo);
								displayPhotoSelect();

							}
						});

						ivaudioplay.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								playmainposition = (Integer) v.getTag();
								playposition = v.getId();

								notifyDataSetChanged();

							}
						});

						ivimage.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								friendMessages = friendMessageList
										.get((Integer) v.getTag());
								audioInfo = friendMessages.getMessageList()
										.get(v.getId());

								// displayVoiceyFriend(audioInfo);
								if (audioInfo.getVideoFilePath() != null
										&& audioInfo.getVideoFilePath()
												.length() > 0) {
									File file = new File(Environment
											.getExternalStorageDirectory()
											+ "/"
											+ Constants.app_folder
											+ "/"
											+ audioInfo.getVideoFilePath());

									if (file.exists()) {

										displayImageVideo(audioInfo, "video");
									} else {

										new DownloadVideo().execute();
									}
								} else if (audioInfo.getImageName() != null
										&& audioInfo.getImageName().length() > 0) {

									displayImageVideo(audioInfo, "image");
								}
							}
							// TODO Auto-generated method stub

						});

						String inputTimeStamp = ai.getSharedate();
						final String inputFormat = "yyyy-MM-dd HH:mm:ss";
						final String outputFormat = "MMM dd HH:mm";

						String dateValue;

						dateValue = TimeStampConverter(inputFormat,
								inputTimeStamp, outputFormat);

						tvsentDate.setText(dateValue);

						if (ai.getReplyMessageList().size() > 0) {

							ImageView ivrFremeright, ivrFremeLeft, ivrimage, ivraudioplay;
							TextView tvrTitle, tvrsentDate, tvruser;
							SeekBar sbrAudioPlay;
							int i = 0;

							for (ReplyMessages rm : ai.getReplyMessageList()) {

								try {
									i++;
									// holder.llreplymessages

									LayoutInflater lfrPrevious = (LayoutInflater) context
											.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
									View replyView;

									replyView = lfrPrevious.inflate(
											R.layout.reply_message, null);

									ivrFremeright = (ImageView) replyView
											.findViewById(R.id.ivfreme_right);
									ivrFremeLeft = (ImageView) replyView
											.findViewById(R.id.ivfreme_left);
									ivrimage = (ImageView) replyView
											.findViewById(R.id.ivimage);
									ivraudioplay = (ImageView) replyView
											.findViewById(R.id.ivaudioplay);
									tvruser = (TextView) replyView
											.findViewById(R.id.tvuser);
									songReplyProgressBar = (SeekBar) replyView
											.findViewById(R.id.songProgressBar);

									tvrTitle = (TextView) replyView
											.findViewById(R.id.tvtitle);
									tvrTitle.setText(rm.getReplyMessage());
									tvrTitle.setTypeface(face);
									tvrsentDate = (TextView) replyView
											.findViewById(R.id.tvdate);
									tvrsentDate.setTypeface(face);
									tvruser.setTypeface(face);
									// sbAudioPlay
									songReplyProgressBar.setProgress(0);
									songReplyProgressBar.setMax(100);

									ivraudioplay.setId(i - 1);
									ivraudioplay.setTag(position);
									ivrimage.setId(i - 1);
									ivrimage.setTag(position);

									ivraudioplay
											.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View v) {

													// replymainposition =
													// (Integer) v.getTag();
													// replyplayposition =
													// v.getId();

													notifyDataSetChanged();

												}
											});
									ivrimage.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {

											audioInfo = friendMessages
													.getMessageList().get(
															(Integer) v
																	.getTag());
											replyMessages = audioInfo
													.getReplyMessageList().get(
															v.getId());

											// displayVoiceyFriend(audioInfo);
											if (replyMessages.getVideo() != null
													&& replyMessages.getVideo()
															.length() > 0) {
												File file = new File(
														Environment
																.getExternalStorageDirectory()
																+ "/"
																+ Constants.app_folder
																+ "/"
																+ replyMessages
																		.getVideo());

												if (file.exists()) {
													audioInfo = new AudioInfo();
													audioInfo
															.setId(replyMessages
																	.getId());
													audioInfo
															.setVideoFilePath(replyMessages
																	.getVideo());

													displayImageVideo(
															audioInfo, "video");
												} else {
													audioInfo = new AudioInfo();
													audioInfo
															.setId(replyMessages
																	.getId());
													audioInfo
															.setVideoFilePath(replyMessages
																	.getVideo());
													new DownloadVideo()
															.execute();
												}
											} else if (replyMessages.getImage() != null
													&& replyMessages.getImage()
															.length() > 0) {

												audioInfo = new AudioInfo();
												audioInfo.setId(replyMessages
														.getId());
												audioInfo
														.setImageName(replyMessages
																.getImage());

												displayImageVideo(audioInfo,
														"image");
											}
										}
										// TODO Auto-generated method stub

									});

									if (rm.getReplyMessage() != null
											&& rm.getReplyMessage().length() > 0) {

										tvTitle.setText(rm.getReplyMessage());

									} else {

										tvTitle.setVisibility(View.GONE);
									}
									String rinputTimeStamp = rm.getDate();
									/*
									 * final String inputFormat =
									 * "yyyy-MM-dd HH:mm:ss"; final String
									 * outputFormat = "MMM dd HH:mm";
									 */

									// String dateValue;

									dateValue = TimeStampConverter(inputFormat,
											rinputTimeStamp, outputFormat);

									tvsentDate.setText(dateValue);

									if (rm.getImage() != null
											&& rm.getImage().length() > 0) {
										ivrimage.setVisibility(View.VISIBLE);
										String Url = Constants.image_url
												+ rm.getImage();

										if (rm.getVideo() != null
												&& rm.getVideo().length() > 0) {
											ivrFremeright
													.setVisibility(View.VISIBLE);
											ivrFremeLeft
													.setVisibility(View.VISIBLE);

										} else {

											ivrFremeright
													.setVisibility(View.GONE);
											ivrFremeLeft
													.setVisibility(View.GONE);
										}

										imageLoader.DisplayImage(Url, ivrimage);
									} else {

										ivrimage.setVisibility(View.GONE);
										ivrFremeright.setVisibility(View.GONE);
										ivrFremeLeft.setVisibility(View.GONE);

									}

									if ((rm.getAudio() != null && rm.getAudio()
											.length() > 0)) {
										ivraudioplay
												.setVisibility(View.VISIBLE);
										songReplyProgressBar
												.setVisibility(View.VISIBLE);

										if (replymainposition == position
												&& replyplayposition == i - 1) {

											try {

												if (j == 0) {
													j++;

												} else {

													//
													String url = Constants.audio_url
															+ rm.getAudio();
													mediaPlayer = new MediaPlayer();

													if (mediaPlayer != null) {
														mediaPlayer.stop();
													}
													mediaPlayer
															.setDataSource(url);

													mediaPlayer.prepare();
													mediaPlayer.start();

													// songProgressBar =
													// sbAudioPlay;
													songReplyProgressBar
															.setProgress(0);
													songReplyProgressBar
															.setMax(100);
													// mHandler.removeCallbacksAndMessages(null);
													mHandler.postDelayed(
															updateReplySongProgress,
															10);
													mediaPlayer
															.setOnCompletionListener(new OnCompletionListener() {
																public void onCompletion(
																		MediaPlayer mp) {

																	replymainposition = -1;
																	replyplayposition = -1;
																	j = 0;
																	notifyDataSetChanged();

																}

															});
												}

											} catch (Exception e) {
												e.printStackTrace();
											}

										}
									} else {
										ivraudioplay.setVisibility(View.GONE);
										songReplyProgressBar
												.setVisibility(View.GONE);

									}

									llreplymessages.addView(replyView);

								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						}

						holder.llfriendmessages.addView(comment);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		if (friendMessages.getGroupId() != null
				&& !friendMessages.getGroupId().equals("null")
				&& friendMessages.getGroupId().length() > 0) {
			
			holder.tvFromvalue.setText(friendMessages.getGroupName());

		}else{
			
			holder.tvFromvalue.setText(friendMessages.getFriendName());	
			
		}
		
		holder.tvFromvalue.setTypeface(face);

		//holder.tvFromvalue.setText(friendMessages.getFriendName());
		holder.tvmessagecount.setText(friendMessages.getMessageCount());
		
		
		holder.ivinvite.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				friendMessages = friendMessageList.get(v.getId());

				if (friendMessages.getGroupAdamin() != null
						&& friendMessages.getGroupAdamin().equals(userCode)) {
					displayEditGroup(friendMessages);
				} else {
					displayInviteGroup(friendMessages);
				}
			}
		});


		holder.ivExpand.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				friendMessages = friendMessageList.get(v.getId());
				expandPosition = v.getId();
				if (friendMessages.getMessageShow().equals("false")) {
					friendMessages.setMessageShow("true");
					new GetFriendMessageList().execute();
				} else {

					friendMessages.setMessageShow("false");
					notifyDataSetChanged();
				}
				
				
			}
		});

		holder.ivaddimage.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				friendMessages = friendMessageList.get(v.getId());
				// displayimageReply(audioInfo);
				displayPhotoSelect();

			}
		});
		
		if (friendMessages.getGroupName() != null
				&& friendMessages.getGroupName().length() > 0
				&& !friendMessages.getGroupName().equals("null")) {
			
			holder.ivinvite.setVisibility(View.VISIBLE);
		

		} else {
			holder.ivinvite.setVisibility(View.GONE);
			

		}

		holder.ivsend.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				friendMessages = friendMessageList.get(v.getId());

				holder = (ViewHolder) v.getTag();
				String title = ((TextView) holder.mEditEmojicon).getText()
						.toString();

				if (title.length() == 0) {

					Toast.makeText(context, "Enter your message",
							Toast.LENGTH_LONG).show();

				} else if (title.length() > 140) {
					Toast.makeText(context,
							"Title should be less then 140 character.",
							Toast.LENGTH_LONG).show();

				} else {

					StringBuffer friendBuffer = new StringBuffer();
					/*
					 * if (audioInfo.getCcFriendList().size() > 0) { for (Friend
					 * f : audioInfo.getCcFriendList()) {
					 * 
					 * friendBuffer.append(f.getFriendId() + ",");
					 * 
					 * } }
					 */
					friendBuffer.append(friendMessages.getFriendId() + ",");
					voiceyReply = new VoiceyReply();
					// voiceyReply.setId(audioInfo.getId());
					voiceyReply.setTitle(title);
					voiceyReply.setUsercode(userCode);
					voiceyReply.setSharedFriendCode(friendBuffer.toString());
					voiceyReply.setPublic_control("0");
					voiceyReply.setUser_control("1");
					voiceyReply.setUserid(userId);
					voiceyReply.setType("classifield");
					voiceyReply.setSharedGroupCode(friendMessages.getGroupId());
					
					isMainReply=true;

					holder.mEditEmojicon.setText(null);
					commentsPosition = v.getId();

					InputMethodManager inputManager = (InputMethodManager) context
							.getSystemService(context.INPUT_METHOD_SERVICE);

					inputManager.hideSoftInputFromWindow(((Activity) context)
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					// flSmilys.setVisibility(View.GONE);
					new ReplayText().execute();
				}
			}
		});

		return convertView;
	}

	class MutableWatcher implements TextWatcher {

		private int mPosition;
		private boolean mActive;
		private ViewHolder viewholder;

		void setPosition(int position) {
			mPosition = position;
		}

		void setActive(boolean active) {
			mActive = active;
		}

		public ViewHolder getViewholder() {
			return viewholder;
		}

		public void setViewholder(ViewHolder viewholder) {
			this.viewholder = viewholder;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			//
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (mActive) {
				// flSmilys.setVisibility(View.GONE);
				// final EditText Caption = (EditText) holder.ettextmsg;
				if (s.toString().length() > 0) {

					// audioInfo.setReplayMessage(s.toString());

					// viewholder.ivrecord.setVisibility(View.GONE);
					viewholder.ivsend.setVisibility(View.VISIBLE);
					// viewholder.ivaddimage.setVisibility(View.GONE);
					// viewholder.tvcc.setVisibility(View.VISIBLE);

					// notifyDataSetChanged();
					// productRegistrationList.set(position2, audioInfo);

					// holder.ivrecord.setVisibility(View.GONE);
					// holder.ivaddimage.setVisibility(View.GONE);.

				} else {

					// viewholder.ivrecord.setVisibility(View.VISIBLE);
					viewholder.ivsend.setVisibility(View.GONE);
					// viewholder.ivaddimage.setVisibility(View.VISIBLE);
					// viewholder.tvcc.setVisibility(View.GONE);
				}
				// mUserDetails.set(mPosition, s.toString());
			}
		}
	}

	class MessageTextWatcher implements TextWatcher {

		private int mPosition;
		private boolean mActive;
		private View view;

		void setPosition(int position) {
			mPosition = position;
		}

		void setActive(boolean active) {
			mActive = active;
		}

		public View getView() {
			return view;
		}

		public void setView(View view) {
			this.view = view;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			//
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (mActive) {

				if (s.toString().length() > 0) {

					RelativeLayout rrcontrolsend = (RelativeLayout) view
							.findViewById(R.id.rrcontrolsend);
					rrcontrolsend.setVisibility(View.VISIBLE);

				} else {
					RelativeLayout rrcontrolsend = (RelativeLayout) view
							.findViewById(R.id.rrcontrolsend);
					rrcontrolsend.setVisibility(View.GONE);

				}

			}
		}
	}
	
	void displayInviteGroup(final FriendMessages friendMessages) {

		TextView tvCancel;
		final TextView tvSave;
		TextView tvgroupname, tvinvitegroup, tvgroupmembercount;
		// ListView lvUserList;
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.inbox_group_invite, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		this.invitefriendId.clear();

		final AlertDialog alertDialog = alertDialogBuilder.create();

		// etFriendId = (EditText) promptsView.findViewById(R.id.etuserid);
		// etFriendName = (EditText) promptsView.findViewById(R.id.etname);
		tvCancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvSave = (TextView) promptsView.findViewById(R.id.tvsave);
		tvgroupname = (TextView) promptsView.findViewById(R.id.tvgroupname);
		tvinvitegroup = (TextView) promptsView.findViewById(R.id.tvinvitegroup);
		tvgroupmembercount= (TextView) promptsView.findViewById(R.id.tvgroupmembercount);
		
		tvSave.setVisibility(View.GONE);
		
		lvFriendList = (ListView) promptsView.findViewById(R.id.lvuserlist);
		friendList = new ArrayList<Friend>();
		tvgroupname.setText("Group : " + friendMessages.getGroupName());
		
		tvgroupmembercount.setText("Group member(" + friendMessages.getGroupCount()+") :");
		this.friendMessages = friendMessages;
		new GetGroupMemberList().execute();

		tvinvitegroup.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				tvSave.setVisibility(View.VISIBLE);
				new GetInviteFriendList().execute();

			}
		});

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
					  
					  if(invitefriendId.size()>0){
						  
						  StringBuffer inviteFriendBuff=new StringBuffer();
						  
						  for (String friend : invitefriendId) {

							  inviteFriendBuff.append(friend + ",");

							}
				  
					  inviteGroup=new InviteGroup();
					  
					  inviteGroup.setSharedTo(inviteFriendBuff.toString());
					  inviteGroup.setSharedFrom(userCode);
					  inviteGroup.setgId(friendMessages.getGroupId());
					  
					  new SendGroupInvite().execute();
					  
				
				  
				  alertDialog.cancel();
					  }
				  
				  } catch (Exception e) { // TODO Auto-generated catch block
				  e.printStackTrace(); }
				 
			}
		});

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

	}
	
	void displayEditGroup(final FriendMessages friendMessages) {
		TextView EG_addmembers;
		final EditText EG_editgrpname;
		ImageView closedialog;
		Button save_grp;
		LayoutInflater li = LayoutInflater.from(context);
		View grpview = li.inflate(R.layout.edit_group, null);

		final Dialog releaseNote = new Dialog(context);
		releaseNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		releaseNote.setContentView(grpview);
		releaseNote.setCancelable(true);
		releaseNote.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		EG_addmembers = (TextView) grpview.findViewById(R.id.add_members);
		EG_editgrpname = (EditText) grpview.findViewById(R.id.grp_name);
		lvFriendList = (ListView) grpview.findViewById(R.id.friendlist);
		closedialog = (ImageView) grpview.findViewById(R.id.closedialog);
		save_grp = (Button) grpview.findViewById(R.id.save_grp);

		EG_editgrpname.setText(friendMessages.getGroupName());
		this.friendMessages = friendMessages;
		friendList = new ArrayList<Friend>();
		// GroupFriendList.clear();
		new getGroupUserlistAsyncTask().execute();

		EG_addmembers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			
				  releaseNote.dismiss();
				  dispayAddGroup(friendMessages);
				 
			}
		});

		save_grp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String nam = EG_editgrpname.getText().toString();
				 friends = new Friend();
				if (!nam.equals("")) {
					releaseNote.dismiss();
					// GroupFriendList.clear();
					friends.setGroupName(nam);
					friends.setUserId(userCode);
					friends.setGroupID(friendMessages.getGroupId());
					 new editGroup().execute();
				}
			}
		});

		closedialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				releaseNote.dismiss();
			}
		});
		releaseNote.show();
	}
	
	void dispayAddGroup(final FriendMessages friendMessages) {
		TextView addfrds;
		
		Button create_grp;
		ImageView closedialog;

		LayoutInflater	li = LayoutInflater.from(context);
		View grpview = li.inflate(R.layout.add_members, null);

		final Dialog releaseNote = new Dialog(context);
		releaseNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		releaseNote.setContentView(grpview);
		releaseNote.setCancelable(true);
		releaseNote.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		addfrds = (TextView) grpview.findViewById(R.id.grpname);
		lvFriendList = (ListView) grpview.findViewById(R.id.friendlist);
		create_grp = (Button) grpview.findViewById(R.id.add_mem);
		closedialog = (ImageView) grpview.findViewById(R.id.closedialog);
		invitefriendId.clear();
		//addfrds.setText(GroupTitle);

		//userList = new ArrayList<User>();
		new GetInviteUserList().execute();

		create_grp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				 try {
					  
					  if(invitefriendId.size()>0){
						  
						  StringBuffer inviteFriendBuff=new StringBuffer();
						  
						  for (String friend : invitefriendId) {

							  inviteFriendBuff.append(friend + ",");

							}
				  
					  inviteGroup=new InviteGroup();
					  
					  inviteGroup.setSharedTo(inviteFriendBuff.toString());
					  inviteGroup.setSharedFrom(userCode);
					  inviteGroup.setgId(friendMessages.getGroupId());
					  
					  new Addnewmember().execute();
					  
				
				  
					  releaseNote.dismiss();
					  }
				  
				  } catch (Exception e) { // TODO Auto-generated catch block
				  e.printStackTrace(); }
				 
			}
		});

		closedialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
			}
		});
		releaseNote.show();
	}

	void displayPhotoSelect() {

		TextView camera, gallery, video, tvcancel;
		final Dialog dialog = new Dialog(context);

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

				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("ActivityResult", "FriendMessage");
				editor.commit();

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				inboxFragment.startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		video.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				imagebitmap = null;
				videoUrl = null;
				dialog.dismiss();
				displayimageReply();

				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString("ActivityResult", "FriendMessage");
				editor.commit();

				Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
				// comma-separated MIME types
				mediaChooser.setType("video/*");
				inboxFragment.startActivityForResult(mediaChooser,
						RESULT_LOAD_VIDEO);

			}
		});

	}

	private void takePictureButtonClicked() {
		Uri imageUri = Uri
				.fromFile(getTempFile(context.getApplicationContext()));
		// Intent intent = createIntentForCamera(imageUri);

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("ActivityResult", "FriendMessage");
		editor.commit();

		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		inboxFragment.startActivityForResult(cameraIntent, ACTIVITY_TAKE_PHOTO);
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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("MyAdapter", "onActivityResult");

		File imgFile;
		switch (requestCode) {
		case (RESULT_LOAD_IMAGE):
			if (resultCode == Activity.RESULT_OK) {

				try {

					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = context.getContentResolver().query(
							selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

					String picturePath = cursor.getString(columnIndex);
					cursor.close();

					imgFile = new File(picturePath);

					try {

						final Bitmap thePic = BitmapFactory
								.decodeFile(picturePath);
						imagebitmap = thePic;
						ivaddimage.setImageBitmap(thePic);

					}

					catch (ActivityNotFoundException anfe) {

						String errorMessage = "Whoops - your device doesn't support the crop action!";

					}

				} catch (Exception ex) {
					// Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
				}
			}
			break;

		case (ACTIVITY_TAKE_PHOTO):
			if (resultCode == Activity.RESULT_OK) {

				try {
					// call the standard crop action intent (the user device may
					// not
					// support it)

					// Bundle extras = data.getExtras();
					// get the cropped bitmap
					final Bitmap thePic = (Bitmap) data.getExtras().get("data");
					imagebitmap = thePic;
					ivaddimage.setImageBitmap(thePic);
					/*
					 * Intent cropIntent = new Intent(
					 * "com.android.camera.action.CROP"); // indicate image type
					 * and Uri cropIntent.setDataAndType(Uri
					 * .fromFile(getTempFile(getActivity()
					 * .getApplicationContext())), "image/*"); // set crop
					 * properties cropIntent.putExtra("crop", "true"); //
					 * indicate aspect of desired crop
					 * cropIntent.putExtra("aspectX", 1);
					 * cropIntent.putExtra("aspectY", 1); // indicate output X
					 * and Y cropIntent.putExtra("outputX", 256);
					 * cropIntent.putExtra("outputY", 256); // retrieve data on
					 * return cropIntent.putExtra("return-data", true); // start
					 * the activity - we handle returning in // onActivityResult
					 * startActivityForResult(cropIntent, CEMARA_PIC_CROP);
					 */
				}
				// respond to users whose devices do not support the crop action
				catch (ActivityNotFoundException anfe) {
					// display an error message
					String errorMessage = "Whoops - your device doesn't support the crop action!";

				}
			}
			break;
		/*
		 * case (CEMARA_PIC_CROP): if (resultCode == Activity.RESULT_OK && null
		 * != data) { Bundle extras = data.getExtras(); // get the cropped
		 * bitmap final Bitmap thePic = extras.getParcelable("data");
		 * imagebitmap = thePic; ivaddimage.setImageBitmap(thePic);
		 * 
		 * break;
		 * 
		 * }
		 */
		case (RESULT_LOAD_VIDEO):
			if (resultCode == Activity.RESULT_OK && null != data) {
				// String path=data.getData();
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = context.getContentResolver().query(
						selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				videoUrl = picturePath;

				// Uri selectedImage = data.getData();
				// Bundle extras = data.getExtras();
				// get the cropped bitmap
				Bitmap bmThumbnail;

				// MICRO_KIND: 96 x 96 thumbnail
				bmThumbnail = ThumbnailUtils.createVideoThumbnail(picturePath,
						Thumbnails.MICRO_KIND);

				// final Bitmap thePic = extras.getParcelable("data");
				// imagebitmap = thePic;
				// ivAddPhoto.setImageBitmap(bmThumbnail);
				imagebitmap = bmThumbnail;
				ivaddimage.setImageBitmap(bmThumbnail);

			}

		}
	}

	void displayimageReply() {

		TextView tvcancel, tvReply;
		final EditText etTitle;
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.image_reply, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		ivaddimage = (ImageView) promptsView.findViewById(R.id.ivaddimage);

		tvcancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvReply = (TextView) promptsView.findViewById(R.id.tvreply);
		etTitle = (EditText) promptsView.findViewById(R.id.ettextmsg);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		/*
		 * alertDialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(Color.argb(0, 0, 0, 0)));
		 * 
		 * alertDialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(android.graphics.Color.TRANSPARENT));
		 */
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

		ivaddimage.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					displayPhotoSelect();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		tvReply.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					String title = null;
					if (imagebitmap != null) {

						String titleStr = ((TextView) etTitle).getText()
								.toString();

						voiceyReply = new VoiceyReply();
						if (titleStr.length() > 0) {
							title = titleStr;
						} else {
							// title = "Voicey ID " + userCode;
						}

						File file = new File(Constants.image_folder + "/"
								+ userCode + ".jpeg");

						voiceyReply.setImage_name(userCode);

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

					StringBuffer friendBuffer = new StringBuffer();
					/*
					 * if (audioInfo.getCcFriendList().size() > 0) { for (Friend
					 * f : audioInfo.getCcFriendList()) {
					 * 
					 * friendBuffer.append(f.getFriendId() + ",");
					 * 
					 * } }
					 */
					friendBuffer.append(friendMessages.getFriendId() + ",");

					// voiceyReply = new VoiceyReply();

					voiceyReply.setTitle(title);

					voiceyReply.setUsercode(userCode);
					voiceyReply.setSharedFriendCode(friendBuffer.toString());
					voiceyReply.setPublic_control("0");
					voiceyReply.setUser_control("1");
					voiceyReply.setUserid(userId);
					voiceyReply.setType("classifield");
					voiceyReply.setId(audioInfo.getId());
					if (videoUrl != null) {
						voiceyReply.setVideo_name(videoUrl);
					}
					alertDialog.cancel();
					new ReplayImage().execute();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	void displayImageVideo(final AudioInfo audioInfo, final String type) {
		TextView tvCancel, tvSave;
		VideoView myVideoView;
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.image_video_display, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
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

		touchImage = (TouchImageView) promptsView.findViewById(R.id.imgtough);

		if (type.equals("video")) {
			touchImage.setVisibility(View.GONE);
			String SrcPath = Environment.getExternalStorageDirectory() + "/"
					+ Constants.app_folder + "/" + audioInfo.getVideoFilePath();

			myVideoView.setVideoPath(SrcPath);
			myVideoView.setMediaController(new MediaController(context));
			myVideoView.requestFocus();
			myVideoView.start();

		} else if (type.equals("image")) {

			myVideoView.setVisibility(View.GONE);

			if (audioInfo.getImageName() != null
					&& audioInfo.getImageName().length() > 0) {

				Bitmap largeIcon = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.loading_img);
				// touchImage.setImageBitmap(largeIcon);
				new ImageDownloader().execute();

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
					shareFriendList.clear();

					commentsPosition = Integer.parseInt(audioInfo.getId());

					if (type.equals("image")) {

						displayVoiceyFriend(audioInfo, "image");
					} else if (type.equals("video")) {

						displayVoiceyFriend(audioInfo, "video");

					}

					alertDialog.cancel();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	Runnable updateReplySongProgress = new Runnable() {
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
	
	
	private class GetInviteFriendList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getInviteFriendList(userCode,
					friendMessages.getGroupId());
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

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				dialog.dismiss();
				friendList.clear();
				// List<String> selItemList = new ArrayList<String>();

				Friend friend;
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							friend = new Friend();

							String friendName = URLDecoder.decode(
									jObj.getString("friend_name"), "UTF-8");
							friend.setFriendId(jObj.getString("added_friend"));

							String convertedfriendName = URLDecoder.decode(
									friendName, "UTF-8");
							friend.setFriendName(convertedfriendName);
							friend.setType("groupinvite");
							friendList.add(friend);
						}
					}
				}

				FriendListAdapter friendAdapter = new FriendListAdapter(
						context, R.layout.friend_list, friendList);

				lvFriendList.setAdapter(friendAdapter);

				// lvFriendList.setAdapter(friendAdapter);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}
	
	private class SendGroupInvite extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			// publishProgress((Void[]) null);
			return Webservices.sendGroupInvite(inviteGroup);
		}

		protected void onProgressUpdate(Void... progress) {

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			// dialog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj = new JSONObject(result);

					String status = jObj.getString("status");

					if (status.equals("1")) {
					

						Toast.makeText(context,
								"send reply successfully.", Toast.LENGTH_LONG)
								.show();

						playBeep();

					} else {
						Toast.makeText(context,
								"Error occure while Share file.",
								Toast.LENGTH_LONG).show();

					}
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
	}
	
	private class GetGroupMemberList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getGroupUserList(friendMessages.getGroupId());
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

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				dialog.dismiss();
				friendList.clear();
				// List<String> selItemList = new ArrayList<String>();

				Friend friend;
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							friend = new Friend();

							String friendName = URLDecoder.decode(
									jObj.getString("name"), "UTF-8");
							friend.setFriendId(jObj.getString("user_id"));

							String convertedfriendName = URLDecoder.decode(
									friendName, "UTF-8");
							friend.setFriendName(convertedfriendName);
							friend.setType("groupmember");
							friendList.add(friend);
						}
					}
				}

				FriendListAdapter friendAdapter = new FriendListAdapter(
						context, R.layout.friend_list, friendList);

				lvFriendList.setAdapter(friendAdapter);

				// lvFriendList.setAdapter(friendAdapter);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}
	
	private class editGroup extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.editGroup(friends);
		}

		protected void onProgressUpdate(Void... progress) {
		}

		protected void onPostExecute(String result) {
			try {
				JSONObject jObj = new JSONObject(result);

				String status = jObj.getString("status");

				if (status.equals("1")) {
					Toast.makeText(context,
							"Group Name Edited successfully.",
							Toast.LENGTH_LONG).show();
					
					
				} else {
					Toast.makeText(context,
							"Error occure while sending request.",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				Log.d("GrpName Edit", e.getLocalizedMessage());
			}
		}
	}
	
	private class GetInviteUserList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getInviteUserList(userCode,
					friendMessages.getGroupId());
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

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				dialog.dismiss();
				friendList.clear();
				// List<String> selItemList = new ArrayList<String>();

				Friend friend;
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							friend = new Friend();

							String friendName = URLDecoder.decode(
									jObj.getString("name"), "UTF-8");
							friend.setFriendId(jObj.getString("user_id"));

							String convertedfriendName = URLDecoder.decode(
									friendName, "UTF-8");
							friend.setFriendName(convertedfriendName);
							friend.setType("groupinvite");
							friendList.add(friend);
						}
					}
				}

				FriendListAdapter friendAdapter = new FriendListAdapter(
						context, R.layout.friend_list, friendList);

				lvFriendList.setAdapter(friendAdapter);

				// lvFriendList.setAdapter(friendAdapter);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}
	
	private class Addnewmember extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.addmember(inviteGroup.getgId(), inviteGroup.getSharedTo());
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

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {

				JSONObject jObj = new JSONObject(result);

				String status = jObj.getString("status");

				if (status.equals("1")) {

					Toast.makeText(context, "Members Added",
							Toast.LENGTH_LONG).show();
					
					new GetFriendList().execute();

				} else {
					Toast.makeText(context,
							"Error occure while creating request.",
							Toast.LENGTH_LONG).show();

				}

			} catch (Exception e) {
				Log.d("SendReq", e.getLocalizedMessage());
			}
		}
	}
	
private class getGroupUserlistAsyncTask extends
	AsyncTask<String, Void, String> {

@Override
protected String doInBackground(String... urls) {
	publishProgress((Void[]) null);
	return Webservices.getGroupUserList(friendMessages.getGroupId());
}

@Override
protected void onPostExecute(String result) {
	try {
		if (result != null) {
			Friend user;

			if (result != null && result.length() > 0) {
				friendList.clear();
				JSONArray arr = new JSONArray(result);
				if (arr.length() > 0) {
					for (int i = 0; i < arr.length(); i++) {
						JSONObject jObj = arr.getJSONObject(i);
						user = new Friend();
						String Name = URLDecoder.decode(
								jObj.getString("name"), "UTF-8");
						user.setGroupID(jObj.getString("gid"));
						user.setFriendId(jObj.getString("user_id"));

						user.setType("groupedit");
						user.setFriendName(Name);

						friendList.add(user);
					}
				}

				FriendListAdapter friendAdapter = new FriendListAdapter(
						context, R.layout.friend_list, friendList);
				lvFriendList.setAdapter(friendAdapter);
			}
		}

	} catch (Exception e) {
		Log.d("UserList", e.getLocalizedMessage());
	}
}

}
	

	private class DownloadVideo extends AsyncTask<String, Void, AudioInfo> {
		Dialog dialog;

		@Override
		protected AudioInfo doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.downloadVideo(audioInfo);
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

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(AudioInfo audioInfo) {

			dialog.dismiss();

			displayImageVideo(audioInfo, "video");

		}
	}

	void displayVoiceyFriend(final AudioInfo audioInfo, final String type) {

		TextView tvCancel, tvSave;
		// ListView lvUserList;
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.share_frienslist, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		// etFriendId = (EditText) promptsView.findViewById(R.id.etuserid);
		// etFriendName = (EditText) promptsView.findViewById(R.id.etname);
		tvCancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvSave = (TextView) promptsView.findViewById(R.id.tvsave);

		lvFriendList = (ListView) promptsView.findViewById(R.id.lvuserlist);
		friendList = new ArrayList<Friend>();
		new GetFriendList().execute();

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
					StringBuffer friendBuffer = new StringBuffer();
					if (shareFriendList.size() > 0) {
						for (Friend f : shareFriendList) {

							friendBuffer.append(f.getFriendId() + ",");

						}
						if (type.equals("voicey")) {

							friendShareAudio = new FriendShareAudio();
							friendShareAudio.setUserCode(userCode);
							friendShareAudio.setVoiceyId(audioInfo.getId());

							friendShareAudio.setFriendlistStr(friendBuffer
									.toString());
							new ShareFriendAudio().execute();

						} else if (type.equals("image")) {

							voiceyReply = new VoiceyReply();

							voiceyReply.setTitle("image");

							voiceyReply.setUsercode(userCode);
							voiceyReply.setSharedFriendCode(friendBuffer
									.toString());
							voiceyReply.setPublic_control("0");
							voiceyReply.setUser_control("1");
							voiceyReply.setUserid(userId);
							voiceyReply.setType("classifield");
							voiceyReply.setImage_name(audioInfo.getImageName());

							alertDialog.cancel();
							new ForwardImage().execute();

						} else if (type.equals("video")) {

							voiceyReply = new VoiceyReply();

							voiceyReply.setTitle("image");

							voiceyReply.setUsercode(userCode);
							voiceyReply.setSharedFriendCode(friendBuffer
									.toString());
							voiceyReply.setPublic_control("0");
							voiceyReply.setUser_control("1");
							voiceyReply.setUserid(userId);
							voiceyReply.setType("classifield");
							voiceyReply.setImage_name(audioInfo.getImageName());
							voiceyReply.setVideo_name(audioInfo
									.getVideoFilePath());
							alertDialog.cancel();
							new ForwardVideo().execute();
						}
					}

					alertDialog.cancel();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

	}

	class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

		protected Bitmap doInBackground(String... urls) {
			Bitmap bmp = null;
			try {
				URL url = new URL(Constants.image_url
						+ audioInfo.getImageName());
				bmp = BitmapFactory.decodeStream(url.openConnection()
						.getInputStream());
				// ivimage.setImageBitmap(bmp);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
			}
			return bmp;
		}

		protected void onPostExecute(Bitmap result) {

			if (result != null) {
				touchImage.setImageBitmap(result);
			}
		}
	}

	private class GetFriendList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getFriendList(userCode);
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

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				dialog.dismiss();
				friendList.clear();
				// List<String> selItemList = new ArrayList<String>();

				Friend friend;
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							friend = new Friend();

							String friendName = URLDecoder.decode(
									jObj.getString("friend_name"), "UTF-8");
							friend.setFriendId(jObj.getString("added_friend"));
							friend.setType(jObj.getString("type"));
							String convertedfriendName = URLDecoder.decode(
									friendName, "UTF-8");
							friend.setFriendName(convertedfriendName);
							// friend.setType("friend");
							friendList.add(friend);
						}
					}
				}

				FriendListAdapter friendAdapter = new FriendListAdapter(
						context, R.layout.friend_list, friendList);

				lvFriendList.setAdapter(friendAdapter);

				// lvFriendList.setAdapter(friendAdapter);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	private class ForwardVideo extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			// publishProgress((Void[]) null);
			return Webservices.forwardVideo(voiceyReply);
		}

		protected void onProgressUpdate(Void... progress) {

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			// dialog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj = new JSONObject(result);

					String status = jObj.getString("status");

					if (status.equals("1")) {

						Toast.makeText(context, "send reply successfully.",
								Toast.LENGTH_LONG).show();

					} else {
						Toast.makeText(context,
								"Error occure while Share file.",
								Toast.LENGTH_LONG).show();

					}
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
	}

	public class FriendListAdapter extends ArrayAdapter<Friend> {

		Context context;
		List<Friend> friendList;
		ViewHolder holder = null;
		Friend friendobj;

		public FriendListAdapter(Context context, int resourceId,
				List<Friend> friendList) {
			super(context, resourceId, friendList);
			this.context = context;
			this.friendList = friendList;
		}

		private class ViewHolder {

			TextView tvUserName;
			TextView tvUserId;
			ImageView ivRemove;
			RelativeLayout rlBody;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			Friend friend = getItem(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.user_list, null);
				holder = new ViewHolder();

				holder.tvUserName = (TextView) convertView
						.findViewById(R.id.tvuserName);
				holder.tvUserId = (TextView) convertView
						.findViewById(R.id.tvuserid);
				holder.rlBody = (RelativeLayout) convertView
						.findViewById(R.id.rlbody);
				holder.ivRemove = (ImageView) convertView
						.findViewById(R.id.remove_user);
				
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			holder.rlBody.setId(position);
			holder.rlBody.setTag(holder);
			holder.ivRemove.setId(position);
			if (sharefriendId.contains(friend.getFriendId())) {
				holder.rlBody.setBackgroundResource(R.color.black);

			} else {
				if (position % 4 == 0)
					// holder.rlBody.setBackgroundColor(Color.parseColor("#22b14c"));

					holder.rlBody
							.setBackgroundResource(R.drawable.list_background1);
				else if (position % 4 == 1)
					holder.rlBody
							.setBackgroundResource(R.drawable.list_background2);
				else if (position % 4 == 2)
					holder.rlBody
							.setBackgroundResource(R.drawable.list_background3);
				else if (position % 4 == 3)
					holder.rlBody
							.setBackgroundResource(R.drawable.list_background4);
			}
			
			if (friend.getType().equals("friend")
					|| (friend.getType().equals("group"))
					|| friend.getType().equals("groupinvite")) {

			holder.rlBody.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					friendobj = friendList.get(v.getId());
					int id = v.getId();
					holder = (ViewHolder) v.getTag();
					holder.rlBody.setBackgroundResource(R.color.black);

					if (friendobj.getType().equals("friend")) {
						if (sharefriendId.contains(friendobj.getFriendId())) {
							shareFriendList.remove(friendobj);
							sharefriendId.remove(friendobj.getFriendId());
							if (id % 4 == 0)
								// holder.rlBody.setBackgroundColor(Color.parseColor("#22b14c"));

								holder.rlBody
										.setBackgroundResource(R.drawable.list_background1);
							else if (id % 4 == 1)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background2);
							else if (id % 4 == 2)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background3);
							else if (id % 4 == 3)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background4);

						} else {
							holder.rlBody.setBackgroundResource(R.color.black);
							shareFriendList.add(friendobj);
							sharefriendId.add(friendobj.getFriendId());
						}

					}else if (friendobj.getType().equals("groupinvite")) {

						if (invitefriendId.contains(friendobj.getFriendId())) {

							invitefriendId.remove(friendobj.getFriendId());
							if (id % 4 == 0)
								// holder.rlBody.setBackgroundColor(Color.parseColor("#22b14c"));

								holder.rlBody
										.setBackgroundResource(R.drawable.list_background1);
							else if (id % 4 == 1)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background2);
							else if (id % 4 == 2)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background3);
							else if (id % 4 == 3)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background4);

						
					}else {
						holder.rlBody
						.setBackgroundResource(R.color.black);

				invitefriendId.add(friendobj.getFriendId());
			}}else {
						if (sharefriendId.contains(friendobj.getFriendId())) {

							shareGroupList.remove(friendobj);
							sharefriendId.remove(friendobj.getFriendId());
							if (id % 4 == 0)
								// holder.rlBody.setBackgroundColor(Color.parseColor("#22b14c"));

								holder.rlBody
										.setBackgroundResource(R.drawable.list_background1);
							else if (id % 4 == 1)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background2);
							else if (id % 4 == 2)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background3);
							else if (id % 4 == 3)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background4);

						}else {
							holder.rlBody.setBackgroundResource(R.color.black);
							shareGroupList.add(friendobj);
							sharefriendId.add(friendobj.getFriendId());
						} 

					}

				}
			});
			}
			
			if (friend.getType().equals("groupedit")) {

				holder.ivRemove.setVisibility(View.VISIBLE);

			} else {

				holder.ivRemove.setVisibility(View.GONE);

			}
			
			holder.ivRemove.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					friends = friendList.get(v.getId());
					friends.setUserId(userCode);
					friendList.remove(friends);
					notifyDataSetChanged();

					new deleteGroup().execute();
					// TODO Auto-generated method stub
					
				}
			});

			holder.tvUserName.setText(friend.getFriendName());
			holder.tvUserId.setText(friend.getFriendId());

			return convertView;
		}

	
	
	}
	
	
	private class deleteGroup extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.deleteGroup(friends);
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

		protected void onPostExecute(String result) {
			dialog.dismiss();

			try {

				JSONObject jObj = new JSONObject(result);

				String status = jObj.getString("status");

				if (status.equals("1")) {

					Toast.makeText(context,
							"Group Deleted successfully.",
							Toast.LENGTH_LONG).show();
				
					

				} else {
					Toast.makeText(context,
							"Error occure while deletimg.",
							Toast.LENGTH_LONG).show();

				}

			} catch (Exception e) {
				Log.d("DelGrp", e.getLocalizedMessage());
			}
		}
	}

	private class ShareFriendAudio extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.shareFriendAudio(friendShareAudio);
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

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				dialog.dismiss();

				// List<String> selItemList = new ArrayList<String>();

				Friend friend;
				if (result != null && result.length() > 0) {

					JSONObject jObj = new JSONObject(result);

					String status = jObj.getString("status");

					if (status.equals("1")) {

						Toast.makeText(context, "File share successfully.",
								Toast.LENGTH_LONG).show();

					} else {
						Toast.makeText(context,
								"Error occure while Share file.",
								Toast.LENGTH_LONG).show();

					}

				}

				// lvFriendList.setAdapter(friendAdapter);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	private class ReplayImage extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.replayImage(voiceyReply);
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

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj = new JSONObject(result);

					String status = jObj.getString("status");

					if (status.equals("1")) {

						/*
						 * List<ReplyMessages> replyMessageList = new
						 * ArrayList<ReplyMessages>();
						 * 
						 * AudioInfo ai = productRegistrationList
						 * .get(commentsPosition); ReplyMessages rm = new
						 * ReplyMessages();
						 * rm.setReplyMessage(voiceyReply.getTitle());
						 * rm.setImage(voiceyReply.getImage_name());
						 * rm.setVideo(voiceyReply.getVideo_name()); Calendar c
						 * = Calendar.getInstance();
						 * 
						 * long time_val = c.getTimeInMillis(); String
						 * formatted_date = (DateFormat.format(
						 * "yyyy-MM-dd hh:mm:ss", time_val)).toString();
						 * rm.setDate(formatted_date); replyMessageList.add(rm);
						 * replyMessageList.addAll(ai.getReplyMessageList());
						 * ai.getReplyMessageList().clear();
						 * ai.getReplyMessageList().addAll(replyMessageList);
						 * 
						 * productRegistrationList.set(commentsPosition, ai);
						 * adapter.notifyDataSetChanged();
						 */

						// new GetShareAudioList().execute();

						Toast.makeText(context, "send reply successfully.",
								Toast.LENGTH_LONG).show();

					} else {
						Toast.makeText(context,
								"Error occure while Share file.",
								Toast.LENGTH_LONG).show();

					}
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
	}

	private class ReplayText extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			// publishProgress((Void[]) null);
			return Webservices.replayText(voiceyReply);
		}

		protected void onProgressUpdate(Void... progress) {

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			// dialog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj = new JSONObject(result);

					String status = jObj.getString("status");

					if (status.equals("1")) {
						
						if(isMainReply){
							isMainReply=false;
							
						}else{
						List<ReplyMessages> replyMessageList = new ArrayList<ReplyMessages>();

						FriendMessages fm = friendMessageList
								.get(expandPosition);
						audioInfo = fm.getMessageList().get(commentsPosition);
						ReplyMessages rm = new ReplyMessages();
						rm.setReplyMessage(voiceyReply.getTitle());
						Calendar c = Calendar.getInstance();

						long time_val = c.getTimeInMillis();
						String formatted_date = (DateFormat.format(
								"yyyy-MM-dd hh:mm:ss", time_val)).toString();
						rm.setDate(formatted_date);
						replyMessageList.add(rm);
						replyMessageList
								.addAll(audioInfo.getReplyMessageList());
						audioInfo.getReplyMessageList().clear();
						audioInfo.getReplyMessageList()
								.addAll(replyMessageList);

						fm.getMessageList().set(commentsPosition, audioInfo);
						friendMessageList.set(expandPosition, fm);
						notifyDataSetChanged();
						}

						Toast.makeText(context, "send reply successfully.",
								Toast.LENGTH_LONG).show();

						playBeep();

					} else {
						Toast.makeText(context,
								"Error occure while Share file.",
								Toast.LENGTH_LONG).show();

					}
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
	}

	void playBeep() {
		MediaPlayer mPlayer;
		mPlayer = MediaPlayer.create(context, R.raw.toest_sound);
		mPlayer.start();
	}

	private class ForwardImage extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			// publishProgress((Void[]) null);
			return Webservices.forwardImage(voiceyReply);
		}

		protected void onProgressUpdate(Void... progress) {

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			// dialog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj = new JSONObject(result);

					String status = jObj.getString("status");

					if (status.equals("1")) {

						Toast.makeText(context, "send reply successfully.",
								Toast.LENGTH_LONG).show();

					} else {
						Toast.makeText(context,
								"Error occure while Share file.",
								Toast.LENGTH_LONG).show();

					}
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
	}
	
	
	private class GetFriendMessageList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getFriendMessageDetailList(userCode,friendMessages.getFriendId(),friendMessages.getGroupId());
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

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				
				AudioInfo audioInfo;
				friendMessages.getMessageList().clear();
				
				if (result != null && result.length() > 0) {
					JSONArray arr = new JSONArray(result);

					/*arr1.length();
					for (int m = 0; m < arr1.length(); m++) {
						JSONArray arr = arr1.getJSONArray(m);*/
						// JSONArray arr1= arr.getJSONObject(a)(0);
						if (arr.length() > 0) {
							for (int i = 0; i < arr.length(); i++) {
								JSONObject messageObj  = arr.getJSONObject(i);
							
										String title = URLDecoder.decode(
												messageObj.getString("title"),
												"UTF-8");

										String fromname = URLDecoder.decode(
												messageObj
														.getString("fromname"),
												"UTF-8");

										audioInfo = new AudioInfo();
										audioInfo.setTitle(title);
										audioInfo.setFromUserName(fromname);
										audioInfo.setSharedate(messageObj
												.getString("date_time"));
										audioInfo.setImageName(messageObj
												.getString("image"));
										audioInfo.setVideoFilePath(messageObj
												.getString("video"));
										audioInfo.setFileName(messageObj
												.getString("audio"));
										audioInfo.setUser_code(messageObj
												.getString("fromid"));
										audioInfo.setGroupId(messageObj
												.getString("group_id"));

										audioInfo.setId(messageObj
												.getString("id"));
										JSONArray replyjson = new JSONArray(
												messageObj
														.getString("reply_to"));
										if (replyjson.length() > 0) {
											for (int k = 0; k < replyjson
													.length(); k++) {
												JSONObject replyjObj = replyjson
														.getJSONObject(k);

												replyMessages = new ReplyMessages();

												String replytitle = URLDecoder
														.decode(replyjObj
																.getString("title"),
																"UTF-8");

												replyMessages
														.setReplyMessage(replytitle);
												replyMessages
														.setDate(replyjObj
																.getString("date_time"));

												
												  replyMessages.setId(replyjObj
												 .getString("id"));
												 

												replyMessages
														.setImage(replyjObj
																.getString("image"));
												replyMessages
														.setAudio(replyjObj
																.getString("audio"));
												replyMessages
														.setVideo(replyjObj
																.getString("video"));
												audioInfo.getReplyMessageList()
														.add(replyMessages);
											}

										}

										friendMessages.getMessageList().add(
												audioInfo);

									}
								}

								
								friendMessageList.set(expandPosition, friendMessages);
							
								notifyDataSetChanged();

							}
					
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
	}

	private String TimeStampConverter(final String inputFormat,
			String inputTimeStamp, final String outputFormat)
			throws ParseException {
		return new SimpleDateFormat(outputFormat).format(new SimpleDateFormat(
				inputFormat).parse(inputTimeStamp));
	}

}
