package com.voicey.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.media.MediaPlayer.OnCompletionListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.provider.Telephony;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.voicey.activity.HomeActivity;
import com.voicey.activity.ImageLoader;
import com.voicey.activity.R;
import com.voicey.activity.TouchImageView;
import com.voicey.adapter.FriendMessageListAdapter;
import com.voicey.model.AudioInfo;
import com.voicey.model.DownloadStatus;
import com.voicey.model.Friend;
import com.voicey.model.FriendMessages;
import com.voicey.model.FriendShareAudio;
import com.voicey.model.InviteGroup;
import com.voicey.model.Remender;
import com.voicey.model.ReplyMessages;
import com.voicey.model.VoiceyReply;
import com.voicey.utils.Constants;
import com.voicey.webservices.Webservices;

public class InboxFragment extends Fragment implements OnClickListener,
		SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	ListView lvItemList, ivFriendMessageList;
	List<AudioInfo> audioInfoList;
	private AudioInfo audioInfo;
	AudioListAdapter adapter;
	InviteGroup inviteGroup;
	Friend friends;
	String userCode, userId, type, timerId, isTodoDelete,isdeleteLastMeg,deleteGroupId;
	SearchView searchView;
	SharedPreferences sharedPreferences;
	Webservices Webservices = new Webservices();
	VoiceyReply voiceyReply;
	Bitmap imagebitmap;
	ImageView ivaddimage;
	ListView lvFriendList;
	String ringtoneUrl, videoUrl;
	String friendId;
	TextView tvshowall,tvgroupmembercount;
	List<Friend> friendList;
	FriendShareAudio friendShareAudio;
	List<AudioInfo> messageAudioInfoList;
	String todoControl = "todo";
	FriendMessageListAdapter friendMessageListadapter;
	List<Friend> shareFriendList = new ArrayList<Friend>();
	List<Friend> shareGroupList = new ArrayList<Friend>();
	ListView lvmessagelist;
	int expandPosition, commentsPosition;
	SeekBar songProgressBar, sbaudioplay, songReplyProgressBar,
			songPreMessProgressBar;
	ImageView ivimage;
	List<AudioInfo> productRegistrationList;
	FrameLayout flSmilys;
	ReplyMessages replyMessages;
	TouchImageView touchImage;
	Remender remender;

	List<String> sharefriendId = new ArrayList<String>();
	List<String> invitefriendId = new ArrayList<String>();

	private final static int ACTIVITY_TAKE_PHOTO = 1;
	static public final int RESULT_LOAD_IMAGE = 267;
	static public final int PIC_CROP = 4044;
	static public final int CEMARA_PIC_CROP = 9999;
	private static final int RESULT_LOAD_VIDEO = 5;
	private int mediaFileLengthInMilliseconds;
	static public final int CONTACT_CHOOSER_ACTIVITY_CODE = 73729;
	private final Handler handler = new Handler();
	private FragmentActivity myContext;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_inbox, container, false);

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

			lvItemList = (ListView) v.findViewById(R.id.lvcustomlist);
			ivFriendMessageList = (ListView) v
					.findViewById(R.id.lvfriendsviewList);
			lvItemList.setItemsCanFocus(true);
			ivFriendMessageList.setItemsCanFocus(true);
			searchView = (SearchView) v.findViewById(R.id.svtitle);
			int id = searchView.getContext().getResources()
					.getIdentifier("android:id/search_src_text", null, null);
			TextView textView = (TextView) searchView.findViewById(id);
			textView.setTextColor(Color.BLACK);
			searchView.setIconifiedByDefault(false);
			searchView.setOnQueryTextListener(this);
			searchView.setOnCloseListener(this);
			myContext = (FragmentActivity) super.getActivity();

			getActivity().getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			userCode = sharedPreferences.getString("userCode", null);
			userId = sharedPreferences.getString("userId", null);
			audioInfoList = new ArrayList<AudioInfo>();
			type = "normal";
			new GetShareAudioList().execute();
			final ImageView ivtodolist = (ImageView) v
					.findViewById(R.id.ivtodolist);
			ImageView ivgroup = (ImageView) v.findViewById(R.id.ivgroup);

			tvshowall = (TextView) v.findViewById(R.id.tvshowall);
			tvshowall.setVisibility(View.GONE);
			ivFriendMessageList.setVisibility(View.GONE);

			flSmilys = (FrameLayout) v.findViewById(R.id.emojicons);

			ivtodolist.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					if (todoControl.equals("todo")) {
						type = "todo";
						ivtodolist.setImageResource(R.drawable.showall_icon);
						todoControl = "showall";
						// ivtodolist
					} else {
						todoControl = "todo";
						ivtodolist.setImageResource(R.drawable.todo_icon);
						type = "normal";
					}
					new GetShareAudioList().execute();
				}
			});

			ivgroup.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					new GetFriendMessageList().execute();

				}
			});

			tvshowall.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					type = "orderby";
					new GetShareAudioList().execute();
				}
			});

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void updatesmily(Emojicon emojicon) {

		AudioInfo ai = productRegistrationList.get(commentsPosition);
		ai.setEmojicon(emojicon);

		productRegistrationList.set(commentsPosition, ai);
		adapter.notifyDataSetChanged();

	}

	private void setEmojiconFragment(boolean useSystemDefault) {

		myContext
				.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.emojicons,
						EmojiconsFragment.newInstance(useSystemDefault))
				.commit();
	}

	public boolean onQueryTextChange(String newText) {
		showResults(newText);
		return false;
	}

	public boolean onQueryTextSubmit(String query) {
		showResults(query);
		return false;
	}

	public boolean onClose() {
		showResults("");
		return false;
	}

	private void showResults(String query) {

		String suggtionname = query != null ? query.toString() : "@@@@";

		List<AudioInfo> orgVoiceyList = new ArrayList<AudioInfo>();
		List<AudioInfo> rstVoiceyList = new ArrayList<AudioInfo>();
		orgVoiceyList.addAll(audioInfoList);

		if (suggtionname != null) {

			if (orgVoiceyList != null && orgVoiceyList.size() > 0) {
				for (AudioInfo ai : orgVoiceyList) {
					if (ai.getTitle().toLowerCase()
							.contains(suggtionname.toString().toLowerCase())||ai.getComments().toLowerCase()
							.contains(suggtionname.toString().toLowerCase())||ai.getFromUserName().toLowerCase()
							.contains(suggtionname.toString().toLowerCase())||ai.getGroupName().toLowerCase()
							.contains(suggtionname.toString().toLowerCase())){
						rstVoiceyList.add(ai);
				}else{
					for (ReplyMessages rm : ai.getReplyMessageList()) {
						
						if (rm.getReplyMessage().toLowerCase()
								.contains(suggtionname.toString().toLowerCase())){
							rstVoiceyList.add(ai);
							
						}
						
						
					}
					
					
				}
					
					
					
				}
			}

			adapter = new AudioListAdapter(getActivity(), R.layout.list_page,
					rstVoiceyList);

			lvItemList.setAdapter(adapter);

		}
	}

	public class AudioListAdapter extends ArrayAdapter<AudioInfo> implements
			EmojiconGridFragment.OnEmojiconClickedListener,
			EmojiconsFragment.OnEmojiconBackspaceClickedListener {

		Context context;
		AudioInfo audioInfo;
		ViewHolder holder = null;
		private MediaPlayer mediaPlayer;
		// private int mediaFileLengthInMilliseconds;
		int playposition = -1;
		int replyplayposition = -1;
		int replymainposition = -1;

		int previousplayposition = -1;
		int previousmainposition = -1;

		int i = 0, j = 0, k = 0;
		Webservices Webservices = new Webservices();
		private final Handler handler = new Handler();
		String audioId, audioUrl, shareId, comments;
		private Handler mHandler = new Handler();
		private Boolean isActivePopup = true;
		Typeface face;
		public ImageLoader imageLoader;

		public AudioListAdapter(Context context, int resourceId,
				List<AudioInfo> ProductRegistrationList) {
			super(context, resourceId, ProductRegistrationList);
			this.context = context;
			productRegistrationList = ProductRegistrationList;
			imageLoader = new ImageLoader(context);
		}

		/* private view holder class */
		private class ViewHolder {

			EmojiconTextView tvTitle;
			TextView tvMood;
			TextView tvUserId, tvforward, tvupmore, tvmorecancel, tvtodo,
					tvreminder, tvsettime, tvsnoozealert;
			TextView tvCount, tvFromvalue, tvsentDate, tvmemo,tvforwardname;
			RelativeLayout rlBody, rrsend;
			ImageView ivTrash, ivaudioplay;
			ImageView ivrecord, ivaddimage, ivDisImage, ivrightframe, ivsend,
					ivsmily, ivleftframe, ivinvite;
			SeekBar sbAudioPlay;
			ImageView ivExpand, ivtodo;
			TextView tvclassifield,tvgroupadd;
			// EditText ettextmsg;
			EmojiconEditText mEditEmojicon;
			LinearLayout llbutton, lldetail, llpreviousmessage,
					llmessagefooter, llreplymessages;
			TextView tvcc, tvcomments, tvgroupname;
			MutableWatcher mWatcher;
			FrameLayout smilyLayout;

			// SeekBar songProgressBar;

		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			audioInfo = productRegistrationList.get(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.inbox_list, null);
				holder = new ViewHolder();

				holder.tvTitle = (EmojiconTextView) convertView
						.findViewById(R.id.tvtitle);
				holder.tvMood = (TextView) convertView
						.findViewById(R.id.tvmood);
				holder.tvUserId = (TextView) convertView
						.findViewById(R.id.tvuserId);

				holder.ivinvite = (ImageView) convertView
						.findViewById(R.id.ivinvite);

				holder.tvsettime = (TextView) convertView
						.findViewById(R.id.tvsettime);

				holder.ivTrash = (ImageView) convertView
						.findViewById(R.id.ivtrash);
				holder.tvforward = (TextView) convertView
						.findViewById(R.id.tvforward);
				holder.mWatcher = new MutableWatcher();
				holder.tvtodo = (TextView) convertView
						.findViewById(R.id.tvtodo);
				holder.tvreminder = (TextView) convertView
						.findViewById(R.id.tvreminder);

				holder.tvmemo = (TextView) convertView
						.findViewById(R.id.tvmemo);

				holder.smilyLayout = (FrameLayout) convertView
						.findViewById(R.id.emojicons);

				holder.tvcomments = (TextView) convertView
						.findViewById(R.id.tvcomments);

				holder.tvclassifield = (TextView) convertView
						.findViewById(R.id.tvclassifield);

				holder.tvFromvalue = (TextView) convertView
						.findViewById(R.id.tvfromValue);

				holder.tvsentDate = (TextView) convertView
						.findViewById(R.id.tvdate);

				holder.ivsend = (ImageView) convertView
						.findViewById(R.id.ivsend);

				holder.sbAudioPlay = (SeekBar) convertView
						.findViewById(R.id.songProgressBar);

				holder.tvsnoozealert = (TextView) convertView
						.findViewById(R.id.tvsnoozealert);

				holder.tvgroupname = (TextView) convertView
						.findViewById(R.id.tvgroupname);

				// isActivePopup = true;
				holder.tvgroupadd = (TextView) convertView
						.findViewById(R.id.tvgroupadd);
				holder.tvCount = (TextView) convertView
						.findViewById(R.id.tvcount);

				holder.rlBody = (RelativeLayout) convertView
						.findViewById(R.id.rlbody);
				holder.ivsmily = (ImageView) convertView
						.findViewById(R.id.ivsmily);

				holder.ivrecord = (ImageView) convertView
						.findViewById(R.id.ivrecord);
				holder.ivaddimage = (ImageView) convertView
						.findViewById(R.id.ivaddimage);

				holder.ivExpand = (ImageView) convertView
						.findViewById(R.id.ivexpand);

				holder.tvupmore = (TextView) convertView
						.findViewById(R.id.tvupmore);
				holder.tvmorecancel = (TextView) convertView
						.findViewById(R.id.tvmorecancel);

				holder.mEditEmojicon = (EmojiconEditText) convertView
						.findViewById(R.id.editEmojicon);

				holder.rrsend = (RelativeLayout) convertView
						.findViewById(R.id.rrsend);

				holder.llbutton = (LinearLayout) convertView
						.findViewById(R.id.llbutton);

				holder.llmessagefooter = (LinearLayout) convertView
						.findViewById(R.id.llmessagefooter);

				holder.lldetail = (LinearLayout) convertView
						.findViewById(R.id.lldetail);

				holder.llpreviousmessage = (LinearLayout) convertView
						.findViewById(R.id.llpreviousmessage);

				holder.llreplymessages = (LinearLayout) convertView
						.findViewById(R.id.llreplymessages);

				holder.ivrightframe = (ImageView) convertView
						.findViewById(R.id.ivfreme_right);
				holder.ivleftframe = (ImageView) convertView
						.findViewById(R.id.ivfreme_left);
				holder.ivtodo = (ImageView) convertView
						.findViewById(R.id.ivtodoactive);
				holder.tvforwardname= (TextView) convertView
						.findViewById(R.id.tvforwardname);

				holder.ivaudioplay = (ImageView) convertView
						.findViewById(R.id.ivaudioplay);

				holder.mEditEmojicon.addTextChangedListener(holder.mWatcher);

				holder.tvcc = (TextView) convertView.findViewById(R.id.tvcc);
				holder.ivDisImage = (ImageView) convertView
						.findViewById(R.id.ivimage);

				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.tvclassifield.setVisibility(View.GONE);
			face = Typeface.createFromAsset(context.getAssets(), "ARIAL.TTF");
			holder.llpreviousmessage.removeAllViews();
			holder.llreplymessages.removeAllViews();

			try {
				if (audioInfo.getPreviousMessageList().size() > 0) {

					TextView valueTV = new TextView(getActivity());
					valueTV.setText("Previous messages");
					valueTV.setId(5);
					valueTV.setTypeface(face);
					valueTV.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					holder.llpreviousmessage.addView(valueTV);

					int maxPosition;
					if (audioInfo.getPreviousMessageList().size() > audioInfo
							.getPreviousMessageCount()) {

						maxPosition = audioInfo.getPreviousMessageCount();
						holder.llmessagefooter.setVisibility(View.VISIBLE);
					} else {

						maxPosition = audioInfo.getPreviousMessageList().size();
						holder.llmessagefooter.setVisibility(View.GONE);
					}
					// setEmojiconFragment(false);

					for (int pos = 0; pos < maxPosition; pos++) {
						AudioInfo ai = audioInfo.getPreviousMessageList().get(
								pos);
						LayoutInflater lfPrevious = (LayoutInflater) context
								.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
						View comment;
						TextView tvTitle, tvsentDate, tvFromvalue;
						ImageView ivrFremeright, ivrFremeLeft, ivrimage, ivaudioplay;
						RelativeLayout rlbody;
						if (!ai.getUser_code().equals(userCode)) {
							comment = lfPrevious.inflate(
									R.layout.previous_message_list, null);
						} else {
							comment = lfPrevious.inflate(
									R.layout.previous_message_self, null);

						}

						tvTitle = (TextView) comment.findViewById(R.id.tvtitle);
						tvTitle.setText(ai.getTitle());
						tvsentDate = (TextView) comment
								.findViewById(R.id.tvdate);
						tvFromvalue = (TextView) comment
								.findViewById(R.id.tvfromValue);

						rlbody = (RelativeLayout) comment
								.findViewById(R.id.rlbodymain);

						ivrFremeright = (ImageView) comment
								.findViewById(R.id.ivfreme_right);
						ivrFremeLeft = (ImageView) comment
								.findViewById(R.id.ivfreme_left);
						ivrimage = (ImageView) comment
								.findViewById(R.id.ivimage);
						ivaudioplay = (ImageView) comment
								.findViewById(R.id.ivaudioplay);
						songPreMessProgressBar = (SeekBar) comment
								.findViewById(R.id.songProgressBar);

						ivaudioplay.setId(pos);
						ivaudioplay.setTag(position);
						ivrimage.setId(pos);
						ivrimage.setTag(position);

						songPreMessProgressBar.setProgress(0);
						songPreMessProgressBar.setMax(100);

						ivaudioplay.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								previousmainposition = (Integer) v.getTag();
								previousplayposition = v.getId();

								notifyDataSetChanged();

							}
						});

						ivrimage.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								audioInfo = productRegistrationList
										.get((Integer) v.getTag());
								audioInfo = audioInfo.getPreviousMessageList()
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

						if (ai.getImageName() != null
								&& ai.getImageName().length() > 0) {
							ivrimage.setVisibility(View.VISIBLE);
							String Url = Constants.image_url
									+ ai.getImageName();

							if (ai.getVideoFilePath() != null
									&& ai.getVideoFilePath().length() > 0) {
								ivrFremeright.setVisibility(View.VISIBLE);
								ivrFremeLeft.setVisibility(View.VISIBLE);

							} else {

								ivrFremeright.setVisibility(View.GONE);
								ivrFremeLeft.setVisibility(View.GONE);
							}

							imageLoader.DisplayImage(Url, ivrimage);
						} else {

							ivrimage.setVisibility(View.GONE);
							ivrFremeright.setVisibility(View.GONE);
							ivrFremeLeft.setVisibility(View.GONE);

						}

						if ((ai.getFileName() != null && ai.getFileName()
								.length() > 0)) {
							ivaudioplay.setVisibility(View.VISIBLE);
							songPreMessProgressBar.setVisibility(View.VISIBLE);

							if (previousmainposition == position
									&& previousplayposition == pos) {

								try {

									if (k == 0) {
										k++;

									} else {

										//
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
										songPreMessProgressBar.setProgress(0);
										songPreMessProgressBar.setMax(100);
										// mHandler.removeCallbacksAndMessages(null);
										mHandler.postDelayed(
												updatePreviousSongProgress, 10);
										mediaPlayer
												.setOnCompletionListener(new OnCompletionListener() {
													public void onCompletion(
															MediaPlayer mp) {

														previousmainposition = -1;
														previousplayposition = -1;
														k = 0;
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
							songPreMessProgressBar.setVisibility(View.GONE);

						}

						String inputTimeStamp = ai.getSharedate();

						final String inputFormat = "yyyy-MM-dd HH:mm:ss";
						final String outputFormat = "MMM dd HH:mm";

						String dateValue = TimeStampConverter(inputFormat,
								inputTimeStamp, outputFormat);
						tvsentDate.setText(dateValue);
						tvFromvalue.setText(ai.getFromUserName());

						tvsentDate.setTypeface(face);
						tvFromvalue.setTypeface(face);

						holder.llpreviousmessage.addView(comment);

					}

				}

				else {

					holder.llmessagefooter.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*	*/
			//

			if (audioInfo.getReplyMessageList().size() > 0) {
				/*
				 * TextView valueTV = new TextView(getActivity());
				 * valueTV.setText("You replied on:"); valueTV.setId(5);
				 * valueTV.setTypeface(face); valueTV.setLayoutParams(new
				 * LayoutParams( LayoutParams.FILL_PARENT,
				 * LayoutParams.WRAP_CONTENT));
				 * holder.llreplymessages.addView(valueTV);
				 */

				ImageView ivrFremeright, ivrFremeLeft, ivrimage, ivaudioplay;
				TextView tvTitle, tvsentDate, tvuser;
				SeekBar sbAudioPlay;
				int i = 0;

				for (ReplyMessages rm : audioInfo.getReplyMessageList()) {

					try {
						i++;
						// holder.llreplymessages

						LayoutInflater lfPrevious = (LayoutInflater) context
								.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
						View comment;

						comment = lfPrevious.inflate(R.layout.reply_message,
								null);

						ivrFremeright = (ImageView) comment
								.findViewById(R.id.ivfreme_right);
						ivrFremeLeft = (ImageView) comment
								.findViewById(R.id.ivfreme_left);
						ivrimage = (ImageView) comment
								.findViewById(R.id.ivimage);
						ivaudioplay = (ImageView) comment
								.findViewById(R.id.ivaudioplay);
						tvuser = (TextView) comment.findViewById(R.id.tvuser);
						songReplyProgressBar = (SeekBar) comment
								.findViewById(R.id.songProgressBar);

						tvTitle = (TextView) comment.findViewById(R.id.tvtitle);
						tvTitle.setText(rm.getReplyMessage());
						tvTitle.setTypeface(face);
						tvsentDate = (TextView) comment
								.findViewById(R.id.tvdate);
						tvsentDate.setTypeface(face);
						tvuser.setTypeface(face);
						// sbAudioPlay
						songReplyProgressBar.setProgress(0);
						songReplyProgressBar.setMax(100);

						ivaudioplay.setId(i - 1);
						ivaudioplay.setTag(position);
						ivrimage.setId(i - 1);
						ivrimage.setTag(position);

						ivaudioplay.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								replymainposition = (Integer) v.getTag();
								replyplayposition = v.getId();

								notifyDataSetChanged();

							}
						});
						ivrimage.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								audioInfo = productRegistrationList
										.get((Integer) v.getTag());
								replyMessages = audioInfo.getReplyMessageList()
										.get(v.getId());

								// displayVoiceyFriend(audioInfo);
								if (replyMessages.getVideo() != null
										&& replyMessages.getVideo().length() > 0) {
									File file = new File(Environment
											.getExternalStorageDirectory()
											+ "/"
											+ Constants.app_folder
											+ "/"
											+ replyMessages.getVideo());

									if (file.exists()) {
										audioInfo = new AudioInfo();
										audioInfo.setId(replyMessages.getId());
										audioInfo
												.setVideoFilePath(replyMessages
														.getVideo());

										displayImageVideo(audioInfo, "video");
									} else {
										audioInfo = new AudioInfo();
										audioInfo.setId(replyMessages.getId());
										audioInfo
												.setVideoFilePath(replyMessages
														.getVideo());
										new DownloadVideo().execute();
									}
								} else if (replyMessages.getImage() != null
										&& replyMessages.getImage().length() > 0) {

									audioInfo = new AudioInfo();
									audioInfo.setId(replyMessages.getId());
									audioInfo.setImageName(replyMessages
											.getImage());

									displayImageVideo(audioInfo, "image");
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
						String inputTimeStamp = rm.getDate();
						final String inputFormat = "yyyy-MM-dd HH:mm:ss";
						final String outputFormat = "MMM dd HH:mm";

						String dateValue;

						dateValue = TimeStampConverter(inputFormat,
								inputTimeStamp, outputFormat);

						tvsentDate.setText(dateValue);

						if (rm.getImage() != null && rm.getImage().length() > 0) {
							ivrimage.setVisibility(View.VISIBLE);
							String Url = Constants.image_url + rm.getImage();

							if (rm.getVideo() != null
									&& rm.getVideo().length() > 0) {
								ivrFremeright.setVisibility(View.VISIBLE);
								ivrFremeLeft.setVisibility(View.VISIBLE);

							} else {

								ivrFremeright.setVisibility(View.GONE);
								ivrFremeLeft.setVisibility(View.GONE);
							}

							imageLoader.DisplayImage(Url, ivrimage);
						} else {

							ivrimage.setVisibility(View.GONE);
							ivrFremeright.setVisibility(View.GONE);
							ivrFremeLeft.setVisibility(View.GONE);

						}

						if ((rm.getAudio() != null && rm.getAudio().length() > 0)) {
							ivaudioplay.setVisibility(View.VISIBLE);
							songReplyProgressBar.setVisibility(View.VISIBLE);

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
										mediaPlayer.setDataSource(url);

										mediaPlayer.prepare();
										mediaPlayer.start();

										// songProgressBar = sbAudioPlay;
										songReplyProgressBar.setProgress(0);
										songReplyProgressBar.setMax(100);
										// mHandler.removeCallbacksAndMessages(null);
										mHandler.postDelayed(
												updateReplySongProgress, 10);
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
							ivaudioplay.setVisibility(View.GONE);
							songReplyProgressBar.setVisibility(View.GONE);

						}

						holder.llreplymessages.addView(comment);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

			if (audioInfo.getCategoryName() != null
					&& !audioInfo.getCategoryName().equals("Type (Optional)")) {
				holder.tvclassifield.setVisibility(View.VISIBLE);
				holder.tvclassifield.setText(audioInfo.getCategoryName());

			}

			if (audioInfo.getAlertTime() != null
					&& audioInfo.getAlertTime().length() > 0
					&& !audioInfo.getAlertTime().equals("null")) {
				holder.tvsettime.setVisibility(View.VISIBLE);

				String inputTimeStamp = audioInfo.getAlertTime();

				final String inputFormat = "yyyy-MM-dd HH:mm:ss";
				final String outputFormat = "MMM dd HH:mm";

				String dateValue = null;
				try {
					dateValue = TimeStampConverter(inputFormat, inputTimeStamp,
							outputFormat);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				holder.tvsettime.setTextColor(getResources().getColor(
						R.color.green));
				holder.tvsettime.setText(dateValue);
			} else {
				holder.tvsettime.setVisibility(View.GONE);
			}

			if (audioInfo.getIsexpand() == 1) {
				holder.lldetail.setVisibility(View.VISIBLE);
				// holder.llbutton.setVisibility(View.VISIBLE);
				holder.llpreviousmessage.setVisibility(View.VISIBLE);
			} else {
				// holder.llbutton.setVisibility(View.GONE);
				holder.lldetail.setVisibility(View.GONE);
				holder.llpreviousmessage.setVisibility(View.GONE);
				holder.llmessagefooter.setVisibility(View.GONE);
			}

			try {
				holder.tvFromvalue.setText(audioInfo.getFromUserName());
				holder.tvFromvalue.setTypeface(face);

				String inputTimeStamp = audioInfo.getSharedate();

				final String inputFormat = "yyyy-MM-dd HH:mm:ss";
				final String outputFormat = "MMM dd HH:mm";

				String dateValue = TimeStampConverter(inputFormat,
						inputTimeStamp, outputFormat);

				holder.tvsentDate.setText(dateValue);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (audioInfo.getTitle() != null
					&& audioInfo.getTitle().length() > 0) {
				holder.tvTitle.setVisibility(View.VISIBLE);
				holder.tvTitle.setText(audioInfo.getTitle());
			} else {

				holder.tvTitle.setVisibility(View.GONE);
			}
			holder.tvCount.setText("P " + audioInfo.getCounter());

			holder.tvTitle.setId(position);
			holder.tvTitle.setTag(holder);
			holder.tvUserId.setId(position);
			holder.ivTrash.setId(position);
			holder.tvforward.setId(position);
			holder.ivaudioplay.setId(position);
			/*
			 * holder.rrtitlebody.setTag(holder);
			 * holder.rrtitlebody.setId(position);
			 */
			holder.tvupmore.setId(position);
			holder.tvmorecancel.setId(position);
			holder.ivExpand.setId(position);
			holder.ivsend.setId(position);
			holder.ivsend.setTag(holder);
			holder.mEditEmojicon.setId(position);
			holder.ivaddimage.setId(position);
			holder.ivrecord.setId(position);
			holder.tvcc.setId(position);
			holder.tvmemo.setId(position);
			holder.tvreminder.setId(position);
			holder.tvtodo.setId(position);
			holder.tvFromvalue.setId(position);
			holder.ivsmily.setId(position);
			holder.ivinvite.setId(position);
			holder.tvgroupadd.setId(position);

			// holder.ettextmsg.setText(audioInfo.getReplayMessage());
			holder.mEditEmojicon.setTag(holder);
			holder.mWatcher.setActive(false);
			holder.mWatcher.setPosition(position);
			holder.mWatcher.setActive(true);
			holder.mWatcher.setViewholder(holder);
			holder.ivsend.setVisibility(View.GONE);
			holder.tvcc.setVisibility(View.GONE);

			holder.ivrightframe.setVisibility(View.GONE);
			holder.ivleftframe.setVisibility(View.GONE);

			if (audioInfo.getIsSnoozed().equals("0")) {
				holder.tvsnoozealert.setVisibility(View.GONE);
			} else {
				holder.tvsnoozealert.setVisibility(View.VISIBLE);

			}

			holder.ivDisImage.setId(position);

			if (audioInfo.getEmojicon() != null) {

				holder.ivrecord.setVisibility(View.GONE);
				holder.ivsend.setVisibility(View.VISIBLE);
				holder.ivaddimage.setVisibility(View.GONE);
				holder.tvcc.setVisibility(View.VISIBLE);

				EmojiconsFragment.input(holder.mEditEmojicon,
						audioInfo.getEmojicon());

			} else {

				holder.mEditEmojicon.setText("");
				holder.ivrecord.setVisibility(View.VISIBLE);
				holder.ivsend.setVisibility(View.GONE);
				holder.ivaddimage.setVisibility(View.VISIBLE);
				holder.tvcc.setVisibility(View.GONE);

			}

			if ((audioInfo.getFileName() != null && audioInfo.getFileName()
					.length() > 0)) {
				holder.ivaudioplay.setVisibility(View.VISIBLE);
				holder.sbAudioPlay.setVisibility(View.VISIBLE);
			} else {
				holder.ivaudioplay.setVisibility(View.GONE);
				holder.sbAudioPlay.setVisibility(View.GONE);

			}

			holder.sbAudioPlay.setProgress(0);
			holder.sbAudioPlay.setMax(100);

			if (playposition == position) {

				try {

					if (i == 0) {
						i++;

					} else {
						//
						String url = Constants.audio_url
								+ audioInfo.getFileName();
						mediaPlayer = new MediaPlayer();
						// playposition=-1;
						// holder.ivaudioplay.seti
						/*
						 * holder.ivaudioplay
						 * .setImageResource(R.drawable.record_inactive);
						 */

						if (mediaPlayer != null) {
							mediaPlayer.stop();
						}
						mediaPlayer.setDataSource(url);

						mediaPlayer.prepare();
						mediaPlayer.start();

						songProgressBar = holder.sbAudioPlay;
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
										i = 0;
										notifyDataSetChanged();

										// audioInfo.setPlayAudio("NO");
										// productRegistrationList.set(position,
										// audioInfo);

									}

								});
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				holder.ivaudioplay.setImageResource(R.drawable.play);

			}

			if (audioInfo.getImageName() != null
					&& audioInfo.getImageName().length() > 0) {
				holder.ivDisImage.setVisibility(View.VISIBLE);
				String Url = Constants.image_url + audioInfo.getImageName();

				if (audioInfo.getVideoFilePath() != null
						&& audioInfo.getVideoFilePath().length() > 0) {
					holder.ivrightframe.setVisibility(View.VISIBLE);
					holder.ivleftframe.setVisibility(View.VISIBLE);

				}

				imageLoader.DisplayImage(Url, holder.ivDisImage);
			} else {

				holder.ivDisImage.setVisibility(View.GONE);

			}

			holder.ivsmily.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					InputMethodManager imm = (InputMethodManager) context
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

					flSmilys.setVisibility(View.VISIBLE);
					commentsPosition = v.getId();
					setEmojiconFragment(false);

				}
			});

			holder.ivaudioplay.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					audioInfo = productRegistrationList.get(v.getId());
					playposition = v.getId();
					productRegistrationList.set(v.getId(), audioInfo);
					notifyDataSetChanged();

				}
			});

			holder.ivinvite.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					audioInfo = productRegistrationList.get(v.getId());
					commentsPosition = v.getId();
					if (audioInfo.getGroupAdamin() != null
							&& audioInfo.getGroupAdamin().equals(userCode)) {
						displayEditGroup(audioInfo);
					} else {
						displayInviteGroup(audioInfo);
					}
				}
			});

			holder.tvtodo.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					audioInfo = productRegistrationList.get(v.getId());
					shareId = audioInfo.getShareId();
					commentsPosition = v.getId();

					new updateTodo().execute();

				}
			});

			holder.tvmemo.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					audioInfo = productRegistrationList.get(v.getId());

					commentsPosition = v.getId();
					displayComments(audioInfo);

				}
			});

			holder.tvforward.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					audioInfo = productRegistrationList.get(v.getId());
					displayVoiceyFriend(audioInfo, "voicey");

				}
			});

			holder.ivDisImage.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					audioInfo = productRegistrationList.get(v.getId());
					// displayVoiceyFriend(audioInfo);
					if (audioInfo.getVideoFilePath() != null
							&& audioInfo.getVideoFilePath().length() > 0) {
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
			});

			holder.tvreminder.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					audioInfo = productRegistrationList.get(v.getId());
					commentsPosition = v.getId();
					displayRemender(audioInfo, audioInfo.getAlertTime());
				}
			});

			holder.tvmorecancel.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					audioInfo = productRegistrationList.get(v.getId());
					audioInfo.setIsexpand(0);
					productRegistrationList.set(v.getId(), audioInfo);
					notifyDataSetChanged();

				}
			});

			holder.ivExpand.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					audioInfo = productRegistrationList.get(v.getId());
					// displayPreviousMessages(audioInfo);
					expandPosition = v.getId();
					friendId = audioInfo.getUser_code();
					shareId = audioInfo.getShareId();

					if (audioInfo.getIsexpand() == 0) {
						audioInfo.setIsexpand(1);
						new GetPreviousMessage().execute();
					} else {

						audioInfo.setIsexpand(0);
						productRegistrationList.set(v.getId(), audioInfo);
						notifyDataSetChanged();
					}

					//
					//

				}
			});

			holder.tvcc.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					shareFriendList.clear();
					shareGroupList.clear();
					sharefriendId.clear();
					audioInfo = productRegistrationList.get(v.getId());
					if (audioInfo.getCcFriendList().size() > 0) {

						for (Friend f : audioInfo.getCcFriendList()) {
							sharefriendId.add(f.getFriendId());

						}
						shareFriendList.addAll(audioInfo.getCcFriendList());
					}
					displayVoiceyFriendCC(audioInfo);
				}
			});

			holder.ivrecord.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					audioInfo = productRegistrationList.get(v.getId());
					StringBuffer friendBuffer = new StringBuffer();
					if (audioInfo.getCcFriendList().size() > 0) {
						for (Friend f : audioInfo.getCcFriendList()) {

							friendBuffer.append(f.getFriendId() + ",");

						}
					}
					friendBuffer.append(audioInfo.getUser_code() + ",");

					((HomeActivity) getActivity()).displayAlert(
							friendBuffer.toString(), audioInfo.getId());
					// displayPhotoSelect();

				}
			});

			holder.ivaddimage.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					audioInfo = productRegistrationList.get(v.getId());
					// displayimageReply(audioInfo);
					displayPhotoSelect();

				}
			});

			holder.tvupmore.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					audioInfo = productRegistrationList.get(v.getId());
					audioInfo.setPreviousMessageCount(audioInfo
							.getPreviousMessageCount() + 5);

					productRegistrationList.set(v.getId(), audioInfo);
					notifyDataSetChanged();

				}
			});

			holder.ivsend.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					audioInfo = productRegistrationList.get(v.getId());
					holder = (ViewHolder) v.getTag();

					String title = ((TextView) holder.mEditEmojicon).getText()
							.toString();

					if (title.length() == 0) {
						Toast.makeText(getActivity().getBaseContext(),
								"Enter your message", Toast.LENGTH_LONG).show();

					} else if (title.length() > 140) {
						Toast.makeText(getActivity().getBaseContext(),
								"Title should be less then 140 character.",
								Toast.LENGTH_LONG).show();

					} else {

						StringBuffer friendBuffer = new StringBuffer();
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
						voiceyReply = new VoiceyReply();
						voiceyReply.setId(audioInfo.getId());
						voiceyReply.setTitle(title);
						voiceyReply.setUsercode(userCode);
						voiceyReply.setSharedFriendCode(friendBuffer.toString());
						voiceyReply.setPublic_control("0");
						voiceyReply.setUser_control("1");
						voiceyReply.setUserid(userId);
						voiceyReply.setType("classifield");
						voiceyReply.setSharedGroupCode(groupBuffer.toString());
						
						holder.mEditEmojicon.setText(null);
						commentsPosition = v.getId();

						InputMethodManager inputManager = (InputMethodManager) getActivity()
								.getSystemService(
										getActivity().INPUT_METHOD_SERVICE);

						inputManager.hideSoftInputFromWindow(getActivity()
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
						flSmilys.setVisibility(View.GONE);
						new ReplayText().execute();
					}
				}
			});

			holder.tvFromvalue.setOnClickListener(new OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(View v) {
					audioInfo = productRegistrationList.get(v.getId());
					List<AudioInfo> copyAudioList = new ArrayList<AudioInfo>();
					copyAudioList.addAll(productRegistrationList);
					// List<AudioInfo> copyAudioList = new
					// ArrayList<AudioInfo>();
					for (AudioInfo ai : copyAudioList) {
						if (!audioInfo.getUser_code().equals(ai.getUser_code())) {

							productRegistrationList.remove(ai);

						}
					}
					tvshowall.setVisibility(View.VISIBLE);
					Typeface face = Typeface.createFromAsset(
							context.getAssets(), "ARIAL.TTF");

					tvshowall.setTextColor(getResources()
							.getColor(R.color.blue));

					tvshowall.setTypeface(face);

					tvshowall.setPaintFlags(holder.tvUserId.getPaintFlags()
							| Paint.UNDERLINE_TEXT_FLAG);
					notifyDataSetChanged();

				}
			});
			
			holder.tvgroupadd.setOnClickListener(new OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(View v) {
					audioInfo = productRegistrationList.get(v.getId());
					commentsPosition=v.getId();
				
					
					new AddInviteMember().execute();

				}
			});

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

			/*
			 * holder.rrtitlebody.setOnClickListener(new OnClickListener() {
			 * 
			 * @SuppressLint("NewApi") public void onClick(View v) {
			 * 
			 * if (isActivePopup) { isActivePopup = false; audioInfo =
			 * productRegistrationList.get(v.getId()); holder = (ViewHolder)
			 * v.getTag();
			 * 
			 * if ((audioInfo.getImageName() != null && audioInfo
			 * .getImageName().length() > 0) || (audioInfo.getFileName() != null
			 * && audioInfo .getFileName().length() > 0)) {
			 * //displayAlert(audioInfo, v.getId(), holder);
			 * 
			 * Integer count = new Integer(audioInfo.getCounter()); count =
			 * count + 1; audioInfo.setCounter(count.toString());
			 * holder.tvCount.setText("P " + audioInfo.getCounter()); }
			 * 
			 * holder.rlBody.setBackgroundColor(Color .parseColor("#e1f2fa"));
			 * 
			 * 
			 * }
			 * 
			 * } });
			 */

			if (audioInfo.getGroupAdd() != null
					&&! audioInfo.getGroupAdd().equals("null")&&audioInfo.getGroupAdd().length()>0) {
			
			holder.tvgroupadd.setVisibility(View.VISIBLE);
			}else{
				
				holder.tvgroupadd.setVisibility(View.GONE);
			}
			
			
			if (audioInfo.getTimerId() == null
					|| audioInfo.getTimerId().equals("null")) {

				holder.tvreminder.setTextColor(getResources().getColor(
						R.color.inbox_text));

			} else {

				holder.tvreminder.setTextColor(getResources().getColor(
						R.color.green));

			}

			if (audioInfo.getComments() != null
					&& audioInfo.getComments().length() > 0) {
				holder.tvcomments.setVisibility(View.VISIBLE);
				holder.tvcomments.setText(audioInfo.getComments());
				holder.tvmemo.setTextColor(getResources().getColor(
						R.color.green));

			} else {
				holder.tvcomments.setVisibility(View.GONE);
				holder.tvmemo.setTextColor(getResources().getColor(
						R.color.inbox_text));
			}
			holder.tvMood.setText("# " + audioInfo.getSource());

			if (audioInfo.getUser_control().equals("1")) {
				holder.tvUserId.setText(audioInfo.getUser_code());
			} else {
				holder.tvUserId.setText("Anonymous");
			}
			Typeface face = Typeface.createFromAsset(context.getAssets(),
					"ARIAL.TTF");

			holder.tvFromvalue.setTextColor(getResources().getColor(
					R.color.blue));

			holder.tvTitle.setTypeface(face);
			holder.tvMood.setTypeface(face);
			holder.tvUserId.setTypeface(face);
			holder.tvFromvalue.setTypeface(face);
			holder.tvsentDate.setTypeface(face);
			holder.tvtodo.setTypeface(face);
			holder.tvreminder.setTypeface(face);
			holder.tvforward.setTypeface(face);
			holder.tvsnoozealert.setTypeface(face);
			holder.tvgroupname.setTypeface(face);
			if(audioInfo.getForwardUserName()!=null&&audioInfo.getForwardUserName().length()>0){
				holder.tvforwardname.setVisibility(View.VISIBLE);
			holder.tvforwardname.setText("FW from "+audioInfo.getForwardUserName()+": ");
			}else{
				
				holder.tvforwardname.setVisibility(View.GONE);
			}
			
			holder.tvforwardname.setTypeface(face);

			if (audioInfo.getGroupName() != null
					&& audioInfo.getGroupName().length() > 0
					&& !audioInfo.getGroupName().equals("null")&&(audioInfo.getGroupAdd() == null
					|| audioInfo.getGroupAdd().equals("null")||audioInfo.getGroupAdd().length()<=0)) {
				holder.tvgroupname.setVisibility(View.VISIBLE);
				holder.ivinvite.setVisibility(View.VISIBLE);
				holder.tvgroupname.setText(audioInfo.getGroupName());
				
				if (audioInfo.getGroupAdamin() != null
						&& audioInfo.getGroupAdamin().equals(userCode)) {
					
					holder.ivinvite.setImageResource(R.drawable.edit);
					
				}else{
					
					holder.ivinvite.setImageResource(R.drawable.i);
				}

			} else {
				holder.ivinvite.setVisibility(View.GONE);
				holder.tvgroupname.setVisibility(View.GONE);

			}

			if (audioInfo.getTodo() != null && audioInfo.getTodo().equals("1")) {
				holder.ivtodo.setImageResource(R.drawable.tick_box_active);
				holder.tvtodo.setTextColor(getResources().getColor(
						R.color.green));

				// holder.tvtodo.setTypeface(null, face.BOLD);

			} else {

				holder.ivtodo.setImageResource(R.drawable.tick_box);
				holder.tvtodo.setTextColor(getResources().getColor(
						R.color.inbox_text));

				// holder.tvtodo.setTypeface( face.DEFAULT_BOLD);

			}

			holder.tvUserId.setPaintFlags(holder.tvUserId.getPaintFlags()
					| Paint.UNDERLINE_TEXT_FLAG);

			holder.tvFromvalue.setPaintFlags(holder.tvUserId.getPaintFlags()
					| Paint.UNDERLINE_TEXT_FLAG);

			holder.ivTrash.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					audioInfo = productRegistrationList.get(v.getId());
					audioId = audioInfo.getId();

					// new VoiceyDelete().execute();
					displayDeleteConform(audioInfo);

				}
			});

			/*
			 * holder.ivnotify.setOnClickListener(new OnClickListener() {
			 * 
			 * @SuppressLint("NewApi") public void onClick(View v) {
			 * 
			 * audioInfo = productRegistrationList.get(v.getId());
			 * 
			 * notificationMail(audioInfo);
			 * 
			 * } });
			 */

			/*
			 * holder.ivringtone.setOnClickListener(new OnClickListener() {
			 * 
			 * @SuppressLint("NewApi") public void onClick(View v) { audioInfo =
			 * productRegistrationList.get(v.getId());
			 * 
			 * displayRingtoneAlert(audioInfo);
			 * 
			 * } });
			 */
			//
			// URL

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

		Runnable updateReplySongProgress = new Runnable() {
			public void run() {

				long totalDuration = mediaPlayer.getDuration();
				long currentDuration = mediaPlayer.getCurrentPosition();

				int secs = (int) (currentDuration / 1000);
				int maxsec = (int) (totalDuration / 1000);

				Double percentage = ((double) secs / maxsec) * 100;

				songReplyProgressBar.setProgress(percentage.intValue());
				if (secs < maxsec) {
					mHandler.postDelayed(this, 10);
				}
			}
		};

		Runnable updatePreviousSongProgress = new Runnable() {
			public void run() {

				long totalDuration = mediaPlayer.getDuration();
				long currentDuration = mediaPlayer.getCurrentPosition();

				int secs = (int) (currentDuration / 1000);
				int maxsec = (int) (totalDuration / 1000);

				Double percentage = ((double) secs / maxsec) * 100;

				songPreMessProgressBar.setProgress(percentage.intValue());
				if (secs < maxsec) {
					mHandler.postDelayed(this, 10);
				}
			}
		};
		
		private class AddInviteMember extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.addInvitemember(audioInfo);
			}

			protected void onProgressUpdate(Void... progress) {

				dialog = new Dialog(getActivity());

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

						Toast.makeText(getActivity(), "Members Added",
								Toast.LENGTH_LONG).show();
						
						AudioInfo ai = productRegistrationList.get(commentsPosition);
						ai.setGroupAdd("null");
						
						productRegistrationList.set(commentsPosition, ai);
						notifyDataSetChanged();
						
					//	new GetFriendList().execute();

					} else {
						Toast.makeText(getActivity(),
								"Error occure while creating request.",
								Toast.LENGTH_LONG).show();

					}

				} catch (Exception e) {
					Log.d("SendReq", e.getLocalizedMessage());
				}
			}
		}
		

		void displayComments(final AudioInfo audioInfo) {
			TextView tvCancel, tvsave;
			final EditText etTitle;
			LayoutInflater li = LayoutInflater.from(getActivity());
			View promptsView = li.inflate(R.layout.comments_popup, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setView(promptsView);

			final AlertDialog alertDialog = alertDialogBuilder.create();

			alertDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.argb(0, 0, 0, 0)));

			alertDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			alertDialog.show();

			tvCancel = (TextView) promptsView.findViewById(R.id.tvcancel);
			tvsave = (TextView) promptsView.findViewById(R.id.tvsave);
			etTitle = (EditText) promptsView.findViewById(R.id.ettextmsg);
			etTitle.setText(audioInfo.getComments());
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

			tvsave.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					try {

						String titleStr = ((TextView) etTitle).getText()
								.toString();

						if (titleStr.length() > 0) {

							comments = titleStr;
							shareId = audioInfo.getShareId();
							alertDialog.cancel();
							new saveComments().execute();
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

		}

		TextView tvdisplaydate, tvdisplaytime;

		void displayRemender(final AudioInfo audioInfo, String settime) {
			final DatePicker datePicker;
			final TimePicker timePicker;
			TextView tvCancel, tvset, tvdelete;

			LayoutInflater li = LayoutInflater.from(getActivity());
			View promptsView = li.inflate(R.layout.reminder, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setView(promptsView);

			final AlertDialog alertDialog = alertDialogBuilder.create();

			alertDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.argb(0, 0, 0, 0)));

			alertDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			alertDialog.show();

			tvCancel = (TextView) promptsView.findViewById(R.id.tvcancel);
			tvset = (TextView) promptsView.findViewById(R.id.tvset);
			tvdelete = (TextView) promptsView.findViewById(R.id.tvdelete);

			if (audioInfo.getTimerId() == null
					|| audioInfo.getTimerId().equals("null")) {

				tvdelete.setVisibility(View.GONE);

			}

			tvdelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					timerId = audioInfo.getTimerId();
					alertDialog.cancel();
					new deleteRemender().execute();

				}
			});
			tvdisplaydate = (TextView) promptsView
					.findViewById(R.id.tvdisplaydate);
			tvdisplaytime = (TextView) promptsView
					.findViewById(R.id.tvdisplaytime);

			datePicker = (DatePicker) promptsView
					.findViewById(R.id.datePicker1);
			timePicker = (TimePicker) promptsView
					.findViewById(R.id.timePicker1);
			Calendar c = Calendar.getInstance();
			if (settime == null) {

			} else {
				try {

					String inputTimeStamp = settime;
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd kk:mm:ss", Locale.US);
					// final String inputFormat = "yyyy-MM-dd HH:mm:ss";

					c.setTime(sdf.parse(settime));

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			// Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int min = c.get(Calendar.MINUTE);

			long time_val = c.getTimeInMillis();

			String dayName = c.getDisplayName(Calendar.DAY_OF_WEEK,
					Calendar.SHORT, Locale.getDefault());

			String formatted_date = (DateFormat.format("MMM dd,yyyy", time_val))
					.toString();

			String myFormat = "hh:mm a";
			// your own format
			SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
			String formated_time = sdf.format(c.getTime());

			tvdisplaydate.setText(dayName + ": " + formatted_date);
			tvdisplaytime.setText(formated_time);

			datePicker.init(year, month, day, dateSetListener);
			// timePicker.
			/*
			 * }else{
			 * 
			 * }
			 */

			timePicker.setCurrentHour(hour);
			timePicker.setCurrentMinute(min);

			timePicker
					.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

						public void onTimeChanged(TimePicker view,
								int hourOfDay, int minute) {
							// updateDisplay(hourOfDay, minute);

							/*
							 * Time tme = new Time(hr,min,0);//seconds by
							 * default set to zero Format formatter; formatter =
							 * new SimpleDateFormat("h:mm a");
							 */

							Calendar datetime = Calendar.getInstance();
							datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
							datetime.set(Calendar.MINUTE, minute);

							String myFormat = "hh:mm a"; // your own format
							SimpleDateFormat sdf = new SimpleDateFormat(
									myFormat, Locale.US);
							String formated_time = sdf.format(datetime
									.getTime());

							tvdisplaytime.setText(formated_time);
						}
					});

			tvset.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					int month = datePicker.getMonth();
					int year = datePicker.getYear();
					int day = datePicker.getDayOfMonth();
					int selectedHour = timePicker.getCurrentHour();
					int selectedMinute = timePicker.getCurrentMinute();

					Calendar calNow = Calendar.getInstance();
					Calendar calSet = (Calendar) calNow.clone();

					calSet.set(Calendar.DATE, day);
					calSet.set(Calendar.MONTH, month);
					calSet.set(Calendar.YEAR, year);
					calSet.set(Calendar.HOUR_OF_DAY, selectedHour);
					calSet.set(Calendar.MINUTE, selectedMinute);
					long time_val = calSet.getTimeInMillis();

					String formatted_date = (DateFormat.format(
							"yyyy-MM-dd kk:mm:ss", time_val)).toString();

					remender = new Remender();
					remender.setUserId(userCode);
					remender.setShareId(audioInfo.getShareId());

					remender.setAlertTime(formatted_date);
					alertDialog.cancel();
					new saveRemender().execute();

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

		}

		DatePicker.OnDateChangedListener dateSetListener = new DatePicker.OnDateChangedListener() {

			public void onDateChanged(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				Calendar calNow = Calendar.getInstance();
				Calendar calSet = (Calendar) calNow.clone();

				calSet.set(Calendar.DATE, dayOfMonth);
				calSet.set(Calendar.MONTH, monthOfYear);
				calSet.set(Calendar.YEAR, year);

				long time_val = calSet.getTimeInMillis();

				String dayName = calSet.getDisplayName(Calendar.DAY_OF_WEEK,
						Calendar.SHORT, Locale.getDefault());

				String formatted_date = (DateFormat.format("MMM dd,yyyy",
						time_val)).toString();

				tvdisplaydate.setText(dayName + " :" + formatted_date);

			}
		};

		void displayImageVideo(final AudioInfo audioInfo, final String type) {
			TextView tvCancel, tvSave;
			VideoView myVideoView;
			LayoutInflater li = LayoutInflater.from(getActivity());
			View promptsView = li.inflate(R.layout.image_video_display, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity(),
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
				String SrcPath = Environment.getExternalStorageDirectory()
						+ "/" + Constants.app_folder + "/"
						+ audioInfo.getVideoFilePath();

				myVideoView.setVideoPath(SrcPath);
				myVideoView.setMediaController(new MediaController(
						getActivity()));
				myVideoView.requestFocus();
				myVideoView.start();

			} else if (type.equals("image")) {

				myVideoView.setVisibility(View.GONE);

				if (audioInfo.getImageName() != null
						&& audioInfo.getImageName().length() > 0) {

					Bitmap largeIcon = BitmapFactory.decodeResource(
							getResources(), R.drawable.loading_img);
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
						shareGroupList.clear();
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

						audioInfo.setReplayMessage(s.toString());

						viewholder.ivrecord.setVisibility(View.GONE);
						viewholder.ivsend.setVisibility(View.VISIBLE);
						viewholder.ivaddimage.setVisibility(View.GONE);
						viewholder.tvcc.setVisibility(View.VISIBLE);

						// notifyDataSetChanged();
						// productRegistrationList.set(position2, audioInfo);

						// holder.ivrecord.setVisibility(View.GONE);
						// holder.ivaddimage.setVisibility(View.GONE);.

					} else {

						viewholder.ivrecord.setVisibility(View.VISIBLE);
						viewholder.ivsend.setVisibility(View.GONE);
						viewholder.ivaddimage.setVisibility(View.VISIBLE);
						viewholder.tvcc.setVisibility(View.GONE);
					}
					// mUserDetails.set(mPosition, s.toString());
				}
			}
		}

		private class updateTodo extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.updateTodo(shareId);
			}

			protected void onProgressUpdate(Void... progress) {

				dialog = new Dialog(getActivity());

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
				AudioInfo ai = productRegistrationList.get(commentsPosition);
				if (ai.getTodo().equals("0")) {

					ai.setTodo("1");

				} else {
					ai.setTodo("0");

				}

				productRegistrationList.set(commentsPosition, ai);
				notifyDataSetChanged();

				// displayImageVideo(audioInfo, "video");

			}
		}

		private class saveComments extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.saveComments(comments, shareId);
			}

			protected void onProgressUpdate(Void... progress) {

				dialog = new Dialog(getActivity());

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
				AudioInfo ai = productRegistrationList.get(commentsPosition);
				ai.setComments(comments);
				ai.setTodo("1");
				productRegistrationList.set(commentsPosition, ai);
				notifyDataSetChanged();

				// displayImageVideo(audioInfo, "video");

			}
		}

		private class deleteRemender extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.deleteRemender(timerId);
			}

			protected void onProgressUpdate(Void... progress) {

				dialog = new Dialog(getActivity());

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
				AudioInfo ai = productRegistrationList.get(commentsPosition);
				ai.setTimerId(null);

				productRegistrationList.set(commentsPosition, ai);
				notifyDataSetChanged();

				// displayImageVideo(audioInfo, "video");

			}
		}

		private class saveRemender extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.saveRemender(remender);
			}

			protected void onProgressUpdate(Void... progress) {

				dialog = new Dialog(getActivity());

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

				JSONObject jObj;
				try {
					jObj = new JSONObject(result);

					String timerId = jObj.getString("timer_id");

					AudioInfo ai = productRegistrationList
							.get(commentsPosition);
					ai.setTimerId(timerId);
					ai.setAlertTime(remender.getAlertTime());
					ai.setTodo("1");
					productRegistrationList.set(commentsPosition, ai);
					notifyDataSetChanged();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// displayImageVideo(audioInfo, "video");

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

				dialog = new Dialog(getActivity());

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

		private class GetPreviousMessage extends
				AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.getpreviousMessageList(userCode, friendId,
						shareId);
			}

			protected void onProgressUpdate(Void... progress) {

				dialog = new Dialog(getActivity());

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
					messageAudioInfoList = new ArrayList<AudioInfo>();
					AudioInfo ai;
					if (result != null && result.length() > 0) {

						JSONArray arr = new JSONArray(result);
						// JSONArray arr1= arr.getJSONObject(a)(0);
						if (arr.length() > 0) {
							for (int i = 0; i < arr.length(); i++) {
								JSONObject jObj = arr.getJSONObject(i);
								ai = new AudioInfo();

								String title = URLDecoder.decode(
										jObj.getString("title"), "UTF-8");
								String fromUserName = URLDecoder.decode(
										jObj.getString("fromname"), "UTF-8");

								ai.setTitle(title);
								ai.setSource(jObj.getString("source"));
								ai.setPublic_control(jObj
										.getString("public_control"));
								ai.setUser_control(jObj
										.getString("user_control"));
								ai.setCounter(jObj.getString("counter"));
								ai.setFileName(jObj.getString("audio"));
								ai.setUser_code(jObj.getString("user_id"));
								ai.setType(jObj.getString("cat_type"));

								ai.setFromUserName(fromUserName);
								ai.setFromUserId(jObj.getString("fromid"));
								;
								ai.setSharedate(jObj.getString("date_time"));
								ai.setShareId(jObj.getString("shared_id"));
								if (ai.getType().equals("classifield")) {

									ai.setYourAd(jObj.getString("your_ad"));
									ai.setImageName(jObj.getString("image"));
									ai.setCategoryName(jObj
											.getString("category_name"));
									ai.setVideoFilePath(jObj.getString("video"));

								}
								ai.setId(jObj.getString("id"));
								ai.setPosition(i);
								ai.setIsDeleteRequired(false);
								messageAudioInfoList.add(ai);

							}
						}

						audioInfo.setPreviousMessageList(messageAudioInfoList);
						productRegistrationList.set(expandPosition, audioInfo);
						notifyDataSetChanged();

						// lvmessagelist.setAdapter(previousMessageAdapter);

					}

				} catch (Exception e) {
					Log.d("InputStream", e.getLocalizedMessage());
				}
			}

		}

		void displayDeleteConform(final AudioInfo audioInfo) {

			TextView tvCancel, tvSave, tvdeletemsg, tvdeleteusermsg, tvdeleteall;
			LinearLayout lldeletetodoactive, llcopychatactive,lldeletelastactive;
			ImageView ivcopychatactive;
			final ImageView ivdeletetodoactive,ivdeletelastactive;
			// ListView lvUserList;
			LayoutInflater li = LayoutInflater.from(getActivity());
			View promptsView = li.inflate(R.layout.confirm_delete, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setView(promptsView);

			final AlertDialog alertDialog = alertDialogBuilder.create();

			tvCancel = (TextView) promptsView.findViewById(R.id.tvcancel);
			tvSave = (TextView) promptsView.findViewById(R.id.tvsave);

			tvdeletemsg = (TextView) promptsView.findViewById(R.id.tvdeletemsg);
			tvdeleteusermsg = (TextView) promptsView
					.findViewById(R.id.tvdeleteusermsg);
			tvdeleteall = (TextView) promptsView.findViewById(R.id.tvdeleteall);
			lldeletetodoactive = (LinearLayout) promptsView
					.findViewById(R.id.lldeletetodoactive);
			llcopychatactive = (LinearLayout) promptsView
					.findViewById(R.id.llcopychatactive);
			
			lldeletelastactive = (LinearLayout) promptsView
					.findViewById(R.id.lldeletelastactive);
			ivdeletetodoactive = (ImageView) promptsView
					.findViewById(R.id.ivdeletetodoactive);
			
			ivdeletelastactive= (ImageView) promptsView
					.findViewById(R.id.ivdeletelastactive);

			Typeface face = Typeface.createFromAsset(context.getAssets(),
					"ARIAL.TTF");
		
			tvdeleteusermsg.setTypeface(face);
			isTodoDelete = "0";
			isdeleteLastMeg="1";
			deleteGroupId=null;
			
			if (audioInfo.getGroupName() != null
					&& audioInfo.getGroupName().length() > 0
					&& !audioInfo.getGroupName().equals("null")){
				tvdeleteusermsg.setText("All from " + audioInfo.getGroupName());
				deleteGroupId=audioInfo.getGroupId();
						
					}else{
						
						tvdeleteusermsg.setText("All from " + audioInfo.getFromUserName());
						
					}
					
					
			lldeletelastactive.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					try {
						if (isdeleteLastMeg.equals("0")) {

							ivdeletelastactive
									.setImageResource(R.drawable.tick_box_active);
							isdeleteLastMeg = "1";
						} else {

							ivdeletelastactive
									.setImageResource(R.drawable.uncheckbox);
							isdeleteLastMeg = "0";

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});		

			lldeletetodoactive.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					try {
						if (isTodoDelete.equals("0")) {

							ivdeletetodoactive
									.setImageResource(R.drawable.tick_box_active);
							isTodoDelete = "1";
						} else {

							ivdeletetodoactive
									.setImageResource(R.drawable.uncheckbox);
							isTodoDelete = "0";

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			tvdeletemsg.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					try {

						shareId = audioInfo.getShareId();
						productRegistrationList.remove(audioInfo);
						notifyDataSetChanged();
						new VoiceyDelete().execute();

						// }

						alertDialog.cancel();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			tvdeleteusermsg.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					try {

						friendId = audioInfo.getUser_code();
						alertDialog.cancel();

						new UserMessageDelete().execute();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			tvdeleteall.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					try {
						alertDialog.cancel();
						new AllMessageDelete().execute();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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

			/*
			 * tvSave.setOnClickListener(new OnClickListener() {
			 * 
			 * @SuppressLint("NewApi") public void onClick(View v) {
			 * 
			 * try { shareId = audioInfo.getShareId();
			 * productRegistrationList.remove(audioInfo);
			 * notifyDataSetChanged(); new VoiceyDelete().execute();
			 * 
			 * // }
			 * 
			 * alertDialog.cancel();
			 * 
			 * } catch (Exception e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } } });
			 */

			alertDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.argb(0, 0, 0, 0)));

			alertDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			alertDialog.show();

		}

		private String TimeStampConverter(final String inputFormat,
				String inputTimeStamp, final String outputFormat)
				throws ParseException {
			return new SimpleDateFormat(outputFormat)
					.format(new SimpleDateFormat(inputFormat)
							.parse(inputTimeStamp));
		}

		/*
		 * void displayAlert(AudioInfo audioInfo, final Integer position, final
		 * ViewHolder holder) { // SeekBar songProgressBar; TextView tvcancel;
		 * TextView tvtitle, tvMore;
		 * 
		 * try { LayoutInflater li = LayoutInflater.from(context); View
		 * promptsView = li.inflate(R.layout.play_audio, null); mediaPlayer =
		 * new MediaPlayer(); AlertDialog.Builder alertDialogBuilder = new
		 * AlertDialog.Builder( context);
		 * alertDialogBuilder.setCancelable(false);
		 * alertDialogBuilder.setView(promptsView); tvcancel = (TextView)
		 * promptsView.findViewById(R.id.tvcancel); tvtitle = (TextView)
		 * promptsView.findViewById(R.id.tvtitle); tvMore = (TextView)
		 * promptsView.findViewById(R.id.tvmore); ivimage = (ImageView)
		 * promptsView.findViewById(R.id.ivimage); Typeface face =
		 * Typeface.createFromAsset(context.getAssets(), "verdana.ttf");
		 * 
		 * tvtitle.setTypeface(face); tvcancel.setTypeface(face); if
		 * (audioInfo.getType() != null &&
		 * audioInfo.getType().equals("classifield")) {
		 * 
		 * if (audioInfo.getImageName() != null &&
		 * audioInfo.getImageName().length() > 0) { new
		 * ImageDownloader().execute(); } else {
		 * 
		 * ivimage.setVisibility(View.GONE); }
		 * 
		 * tvMore.setText(audioInfo.getYourAd()); tvMore.setTypeface(face); // /
		 * ivimage.setImageBitmap(audioInfo.getImagebitmap());
		 * 
		 * } else {
		 * 
		 * ivimage.setVisibility(View.GONE);
		 * 
		 * }
		 * 
		 * songProgressBar = (SeekBar) promptsView
		 * .findViewById(R.id.songProgressBar);
		 * 
		 * final AlertDialog alertDialog = alertDialogBuilder.create();
		 * 
		 * alertDialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(Color.argb(0, 0, 0, 0)));
		 * 
		 * alertDialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(android.graphics.Color.TRANSPARENT));
		 * alertDialog.show(); tvtitle.setText(audioInfo.getTitle());
		 * tvcancel.setOnClickListener(new OnClickListener() {
		 * 
		 * @SuppressLint("NewApi") public void onClick(View v) {
		 * 
		 * try {
		 * 
		 * mediaPlayer.stop();
		 * 
		 * isActivePopup = true; alertDialog.cancel();
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } } }); if ((audioInfo.getFileName() != null &&
		 * audioInfo.getFileName() .length() > 0)) { audioId =
		 * audioInfo.getId(); new AudioCountInc().execute();
		 * 
		 * String url = Constants.audio_url + audioInfo.getFileName();
		 * 
		 * if (mediaPlayer != null) { mediaPlayer.stop(); }
		 * mediaPlayer.setDataSource(url); // mediaPlayer = new MediaPlayer();
		 * //
		 * mediaPlayer.setDataSource(Environment.getExternalStorageDirectory()
		 * // + "/"+ Constants.app_folder + "/" + "abc" + ".3gp");
		 * mediaPlayer.prepare(); mediaPlayer.start();
		 * songProgressBar.setProgress(0); songProgressBar.setMax(100); //
		 * mediaPlayer.setDataSource(url); // mediaPlayer.setDataSource(); //
		 * mediaPlayer.prepare();
		 * 
		 * // mediaFileLengthInMilliseconds = // mediaPlayer.getDuration();
		 * 
		 * // mediaPlayer.start();
		 * 
		 * updateProgressBar(); mediaPlayer .setOnCompletionListener(new
		 * OnCompletionListener() {
		 * 
		 * @Override public void onCompletion(MediaPlayer mp) {
		 * mediaPlayer.start(); songProgressBar.setProgress(0);
		 * songProgressBar.setMax(100);
		 * 
		 * // Updating progress bar updateProgressBar();
		 * 
		 * } });
		 * 
		 * } else {
		 * 
		 * songProgressBar.setVisibility(View.GONE); } } catch (Exception e) {
		 * e.printStackTrace(); } }
		 */

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

		void displayPreviousMessages(final AudioInfo audioInfo) {
			TextView tvcancel;

			LayoutInflater li = LayoutInflater.from(context);
			View promptsView = li.inflate(R.layout.previous_message, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setView(promptsView);
			tvcancel = (TextView) promptsView.findViewById(R.id.tvcancel);
			friendId = audioInfo.getUser_code();
			// tvReply = (TextView) promptsView.findViewById(R.id.tvreply);
			lvmessagelist = (ListView) promptsView
					.findViewById(R.id.lvmessagelist);
			new GetPreviousMessage().execute();

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
					displayimageReply(audioInfo);
					takePictureButtonClicked();

					dialog.dismiss();
				}
			});

			gallery.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					dialog.dismiss();
					imagebitmap = null;
					displayimageReply(audioInfo);
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
					displayimageReply(audioInfo);
					Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
					// comma-separated MIME types
					mediaChooser.setType("video/*");
					startActivityForResult(mediaChooser, RESULT_LOAD_VIDEO);

				}
			});

		}

		void displayimageReply(final AudioInfo audioInfo) {

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
								FileOutputStream out = new FileOutputStream(
										file);
								imagebitmap.compress(
										Bitmap.CompressFormat.JPEG, 90, out);
								out.flush();
								out.close();

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						StringBuffer friendBuffer = new StringBuffer();
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
						new ReplayImage().execute();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

		}

		/*
		 * public void notificationMail(AudioInfo audioInfo) {
		 * 
		 * String message = " Voicey Title:" + audioInfo.getTitle() +
		 * " , Report by:" + userCode;
		 * 
		 * Intent email = new Intent(Intent.ACTION_SEND);
		 * email.putExtra(Intent.EXTRA_EMAIL, new String[] {
		 * Constants.notifican_mail }); // email.putExtra(Intent.EXTRA_CC, new
		 * String[]{ to}); // email.putExtra(Intent.EXTRA_BCC, new
		 * String[]{to}); email.putExtra(Intent.EXTRA_SUBJECT,
		 * Constants.notifican_mail_subject); email.putExtra(Intent.EXTRA_TEXT,
		 * message);
		 * 
		 * // need this to prompts email client only
		 * email.setType("message/rfc822");
		 * 
		 * context.startActivity(Intent.createChooser(email,
		 * "Choose an Email client :"));
		 * 
		 * }
		 */

		/*
		 * void displayShareAlert(final AudioInfo audioInfo) {
		 * 
		 * TextView tvcancel; TextView tvfriend, tvWhatsapp, tvmessage;
		 * LayoutInflater li = LayoutInflater.from(context); View promptsView =
		 * li.inflate(R.layout.share_popup, null);
		 * 
		 * AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
		 * context); alertDialogBuilder.setCancelable(false);
		 * alertDialogBuilder.setView(promptsView); tvfriend = (TextView)
		 * promptsView.findViewById(R.id.tvfriend); tvWhatsapp = (TextView)
		 * promptsView.findViewById(R.id.tvwhatsapp); tvmessage = (TextView)
		 * promptsView.findViewById(R.id.tvmessage); tvcancel = (TextView)
		 * promptsView.findViewById(R.id.tvcancel); Typeface face =
		 * Typeface.createFromAsset(context.getAssets(), "verdana.ttf");
		 * 
		 * tvfriend.setTypeface(face); tvWhatsapp.setTypeface(face);
		 * tvmessage.setTypeface(face); tvcancel.setTypeface(face);
		 * 
		 * final AlertDialog alertDialog = alertDialogBuilder.create();
		 * 
		 * alertDialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(Color.argb(0, 0, 0, 0)));
		 * 
		 * alertDialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(android.graphics.Color.TRANSPARENT));
		 * alertDialog.show();
		 * 
		 * tvcancel.setOnClickListener(new OnClickListener() {
		 * 
		 * @SuppressLint("NewApi") public void onClick(View v) {
		 * 
		 * try {
		 * 
		 * alertDialog.cancel();
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } } });
		 * 
		 * tvfriend.setOnClickListener(new OnClickListener() {
		 * 
		 * @SuppressLint("NewApi") public void onClick(View v) {
		 * 
		 * try {
		 * 
		 * alertDialog.cancel(); displayVoiceyFriend(audioInfo);
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } } });
		 * 
		 * tvWhatsapp.setOnClickListener(new OnClickListener() {
		 * 
		 * @SuppressLint("NewApi") public void onClick(View v) {
		 * 
		 * try {
		 * 
		 * alertDialog.cancel(); File f = new File(Environment
		 * .getExternalStorageDirectory() + "/Voicey/" + audioInfo.getTitle() +
		 * ".3gp");
		 * 
		 * if (f.exists()) {
		 * 
		 * shareAudioWhatsApp(Environment .getExternalStorageDirectory() +
		 * "/Voicey/" + audioInfo.getTitle() + ".3gp");
		 * 
		 * 
		 * shareallthing(Environment.getExternalStorageDirectory () + "/Voicey/"
		 * + audioInfo.getTitle() + ".3gp");
		 * 
		 * } else {
		 * 
		 * File file = new File(Environment .getExternalStorageDirectory() +
		 * "/Voicey/" + audioInfo.getFileName());
		 * 
		 * if (file.exists()) {
		 * 
		 * shareAudioWhatsApp(Environment .getExternalStorageDirectory() +
		 * "/Voicey/" + audioInfo.getFileName()); } new
		 * voiceyDownload().execute(); }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } } });
		 * 
		 * tvmessage.setOnClickListener(new OnClickListener() {
		 * 
		 * @SuppressLint("NewApi") public void onClick(View v) {
		 * 
		 * try {
		 * 
		 * alertDialog.cancel();
		 * 
		 * File f = new File(Environment .getExternalStorageDirectory() +
		 * "/Voicey/" + audioInfo.getTitle() + ".3gp");
		 * 
		 * if (f.exists()) {
		 * 
		 * shareAudioMMS(Environment .getExternalStorageDirectory() + "/Voicey/"
		 * + audioInfo.getTitle() + ".3gp");
		 * 
		 * 
		 * shareallthing(Environment.getExternalStorageDirectory () + "/Voicey/"
		 * + audioInfo.getTitle() + ".3gp");
		 * 
		 * } else { File file = new File(Environment
		 * .getExternalStorageDirectory() + "/Voicey/" +
		 * audioInfo.getFileName());
		 * 
		 * if (file.exists()) {
		 * 
		 * shareAudioMMS(Environment .getExternalStorageDirectory() + "/Voicey/"
		 * + audioInfo.getFileName()); } else { new
		 * voiceMMSyDownload().execute(); } }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * } });
		 * 
		 * }
		 */

		private class voiceMMSyDownload extends
				AsyncTask<String, Void, DownloadStatus> {
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

				if (downloadStatus.getStatus().equals("1")) {

					shareAudioMMS(downloadStatus.getUrl());

				} else {

					Toast.makeText(context,
							"Error occurred while downloading ",
							Toast.LENGTH_LONG).show();
				}

			}
		}

		private class voiceyDownload extends
				AsyncTask<String, Void, DownloadStatus> {
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

				if (downloadStatus.getStatus().equals("1")) {

					shareAudioWhatsApp(downloadStatus.getUrl());

				} else {

					Toast.makeText(context,
							"Error occurred while downloading ",
							Toast.LENGTH_LONG).show();
				}

			}
		}

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
				return Webservices.shareDelete(shareId);
			}

			protected void onPostExecute(String result) {

			}
		}

		private class UserMessageDelete extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.userMessageDelete(friendId, userCode,
						isTodoDelete,isdeleteLastMeg,deleteGroupId);
			}

			protected void onPostExecute(String result) {
				try {
					if (result != null && result.length() > 0) {

						JSONObject jObj = new JSONObject(result);

						String status = jObj.getString("status");

						if (status.equals("1")) {

							Toast.makeText(getActivity(),
									"delete successfully.", Toast.LENGTH_LONG)
									.show();

							playBeep();
							new GetShareAudioList().execute();

						} else {
							Toast.makeText(getActivity(),
									"Error occure while Share file.",
									Toast.LENGTH_LONG).show();

						}
					}
				} catch (Exception e) {

				}

			}
		}

		private class AllMessageDelete extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.allMessageDelete(userCode, isTodoDelete);
			}

			protected void onPostExecute(String result) {
				try {
					if (result != null && result.length() > 0) {

						JSONObject jObj = new JSONObject(result);

						String status = jObj.getString("status");

						if (status.equals("1")) {

							Toast.makeText(getActivity(),
									"delete successfully.", Toast.LENGTH_LONG)
									.show();

							playBeep();
							new GetShareAudioList().execute();

						} else {
							Toast.makeText(getActivity(),
									"Error occure while Share file.",
									Toast.LENGTH_LONG).show();

						}
					}
				} catch (Exception e) {

				}

			}
		}

		public void shareAudioWhatsApp(String url) {

			File name = new File(url);

			if (name.exists()) {
				Intent share = new Intent(Intent.ACTION_SEND);
				// share.setType("image/jpeg");
				share.setType("audio/3gp");
				/*
				 * share.putExtra(Intent.EXTRA_STREAM, Uri.parse(
				 * Environment.getExternalStorageDirectory()+
				 * File.separator+"temporary_file.jpg"));
				 */

				share.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
				if (isPackageInstalled("com.whatsapp", context)) {
					share.setPackage("com.whatsapp");
					context.startActivity(Intent.createChooser(share,
							"Share Video"));

				} else {

					Toast.makeText(context, "Please Install Whatsapp",
							Toast.LENGTH_LONG).show();
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

		@Override
		public void onEmojiconClicked(Emojicon emojicon) {
			Toast.makeText(context, "in adapter", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onEmojiconBackspaceClicked(View v) {
			// EmojiconsFragment.backspace(mEditEmojicon);
		}

	}

	@SuppressLint("NewApi")
	public void shareAudioMMS(String url) {

		File name = new File(url);

		if (name.exists()) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At
																		// least
																		// KitKat
			{
				String defaultSmsPackageName = Telephony.Sms
						.getDefaultSmsPackage(getActivity());
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setType("video/3gp");
				sendIntent.putExtra("sms_body",
						" Voicey.  Say it. Send it. 6 Sec");
				// final File file1 = new
				// File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Downloadtest.3gp");
				Uri uri = Uri.fromFile(name);

				sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
				if (defaultSmsPackageName != null)// Can be null in case
													// that there is no
													// default, then the
													// user would be able to
													// choose any app that
													// support this intent.
				{
					sendIntent.setPackage(defaultSmsPackageName);
				}

			} else {

				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setClassName("com.android.mms",
						"com.android.mms.ui.ComposeMessageActivity");

				// sendIntent.setData(Uri.parse("mms:"));

				sendIntent.putExtra("sms_body",
						" Voicey.  Say it. Send it. 6 Sec");
				// final File file1 = new
				// File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Downloadtest.3gp");
				Uri uri = Uri.fromFile(name);
				// sendIntent.setPackage("com.android.mms");
				sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
				sendIntent.setType("video/3gp");
				startActivity(Intent.createChooser(sendIntent, "Send file"));
			}
		}

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

						Toast.makeText(getActivity(),
								"send reply successfully.", Toast.LENGTH_LONG)
								.show();

					} else {
						Toast.makeText(getActivity(),
								"Error occure while Share file.",
								Toast.LENGTH_LONG).show();

					}
				}

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

						Toast.makeText(getActivity(),
								"send reply successfully.", Toast.LENGTH_LONG)
								.show();

					} else {
						Toast.makeText(getActivity(),
								"Error occure while Share file.",
								Toast.LENGTH_LONG).show();

					}
				}

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
					

						Toast.makeText(getActivity(),
								"send reply successfully.", Toast.LENGTH_LONG)
								.show();

						playBeep();

					} else {
						Toast.makeText(getActivity(),
								"Error occure while Share file.",
								Toast.LENGTH_LONG).show();

					}
				}

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

			dialog = new Dialog(getActivity());

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

						new GetShareAudioList().execute();

						Toast.makeText(getActivity(),
								"send reply successfully.", Toast.LENGTH_LONG)
								.show();

					} else {
						Toast.makeText(getActivity(),
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
						List<ReplyMessages> replyMessageList = new ArrayList<ReplyMessages>();

						AudioInfo ai = productRegistrationList
								.get(commentsPosition);
						ReplyMessages rm = new ReplyMessages();
						rm.setReplyMessage(voiceyReply.getTitle());
						Calendar c = Calendar.getInstance();

						long time_val = c.getTimeInMillis();
						String formatted_date = (DateFormat.format(
								"yyyy-MM-dd hh:mm:ss", time_val)).toString();
						rm.setDate(formatted_date);
						replyMessageList.add(rm);
						replyMessageList.addAll(ai.getReplyMessageList());
						ai.getReplyMessageList().clear();
						ai.getReplyMessageList().addAll(replyMessageList);

						productRegistrationList.set(commentsPosition, ai);
						adapter.notifyDataSetChanged();

						Toast.makeText(getActivity(),
								"send reply successfully.", Toast.LENGTH_LONG)
								.show();

						playBeep();

					} else {
						Toast.makeText(getActivity(),
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
		mPlayer = MediaPlayer.create(getActivity(), R.raw.toest_sound);
		mPlayer.start();
	}

	private class GetShareAudioList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getShareAudioList(userCode, type);
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(getActivity());

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
				audioInfoList.clear();
				AudioInfo audioInfo;
				ReplyMessages replyMessages;
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							audioInfo = new AudioInfo();
							String title = URLDecoder.decode(
									jObj.getString("title"), "UTF-8");
							String fromUserName = URLDecoder.decode(
									jObj.getString("fromname"), "UTF-8");
							audioInfo.setTitle(title);
							audioInfo.setSource(jObj.getString("source"));
							audioInfo.setPublic_control(jObj
									.getString("public_control"));
							audioInfo.setUser_control(jObj
									.getString("user_control"));
							audioInfo.setCounter(jObj.getString("counter"));
							audioInfo.setFileName(jObj.getString("audio"));
							audioInfo.setUser_code(jObj.getString("user_id"));
							audioInfo.setType(jObj.getString("cat_type"));
							audioInfo.setGroupAdd(jObj.getString("suggest_friend"));
							audioInfo.setIsSnoozed(jObj.getString("snooze"));
							audioInfo.setForwardUserId(jObj.getString("forward_user"));
							String forwardName = URLDecoder.decode(
									jObj.getString("forward_user_name"), "UTF-8");
							audioInfo.setForwardUserName(forwardName);
							audioInfo.setGroupId(jObj.getString("group_id"));
							String groupName = URLDecoder.decode(
									jObj.getString("group_name"), "UTF-8");
							audioInfo
									.setGroupName(groupName);

							audioInfo.setFromUserName(fromUserName);
							audioInfo.setFromUserId(jObj.getString("fromid"));
							;
							audioInfo.setSharedate(jObj.getString("date_time"));
							audioInfo.setShareId(jObj.getString("shared_id"));
							
					 audioInfo.setGroupAdamin(jObj.getString("group_admin"
							 ));
							  audioInfo.setGroupCount(jObj.getString("group_count"
							  ));
							 
							if (audioInfo.getType().equals("classifield")) {

								audioInfo.setYourAd(jObj.getString("your_ad"));
								audioInfo.setImageName(jObj.getString("image"));
								audioInfo.setCategoryName(jObj
										.getString("category_name"));

								audioInfo.setVideoFilePath(jObj
										.getString("video"));
							}

							JSONArray replyjson = new JSONArray(
									jObj.getString("reply_to"));
							if (replyjson.length() > 0) {
								for (int j = 0; j < replyjson.length(); j++) {
									JSONObject replyjObj = replyjson
											.getJSONObject(j);

									replyMessages = new ReplyMessages();

									String replytitle = URLDecoder.decode(
											replyjObj.getString("title"),
											"UTF-8");

									replyMessages.setReplyMessage(replytitle);
									replyMessages.setDate(replyjObj
											.getString("date_time"));

									replyMessages.setId(replyjObj
											.getString("id"));

									replyMessages.setImage(replyjObj
											.getString("image"));
									replyMessages.setAudio(replyjObj
											.getString("audio"));
									replyMessages.setVideo(replyjObj
											.getString("video"));
									audioInfo.getReplyMessageList().add(
											replyMessages);
								}

							}

							audioInfo.setTodo(jObj.getString("to_do"));
							String comments = URLDecoder.decode(
									jObj.getString("comment"), "UTF-8");
							audioInfo.setComments(comments);
							audioInfo.setId(jObj.getString("id"));
							audioInfo.setPosition(i);
							audioInfo.setIsDeleteRequired(false);
							audioInfo.setTimerId(jObj.getString("timer_id"));
							audioInfo
									.setAlertTime(jObj.getString("alert_time"));
							audioInfoList.add(audioInfo);

						}
					}

					adapter = new AudioListAdapter(getActivity(),
							R.layout.list_page, audioInfoList);

					lvItemList.setAdapter(adapter);

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
			return Webservices.getFriendMessageList(userCode);
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(getActivity());

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
				FriendMessages friendMessages;
				AudioInfo audioInfo;
				List<FriendMessages> friendMessageList = new ArrayList<FriendMessages>();
				if (result != null && result.length() > 0) {
					JSONArray arr1 = new JSONArray(result);

					arr1.length();
					for (int m = 0; m < arr1.length(); m++) {
						JSONArray arr = arr1.getJSONArray(m);
						// JSONArray arr1= arr.getJSONObject(a)(0);
						if (arr.length() > 0) {
							for (int i = 0; i < arr.length(); i++) {
								JSONObject jObj = arr.getJSONObject(i);
								friendMessages = new FriendMessages();
								String fromUserName = URLDecoder.decode(
										jObj.getString("name"), "UTF-8");
								friendMessages.setFriendName(fromUserName);
								friendMessages.setMessageCount(jObj
										.getString("total_count"));
								friendMessages.setFriendId(jObj
										.getString("shared_from"));

								friendMessages.setGroupId(jObj
										.getString("group_id"));
								String groupname = URLDecoder.decode(
										jObj.getString("group_name"), "UTF-8");
								friendMessages.setGroupName(groupname);
								friendMessages.setGroupAdamin(jObj.getString("group_admin"
										 ));
								friendMessages.setGroupCount(jObj.getString("group_count"
										  ));
							

								/*JSONArray messagejson = new JSONArray(
										jObj.getString("group_by"));

								if (messagejson.length() > 0) {
									for (int j = 0; j < messagejson.length(); j++) {
										JSONObject messageObj = messagejson
												.getJSONObject(j);
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

												
												 * replyMessages.setId(replyjObj
												 * .getString("id"));
												 

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

								
								*/
							
							friendMessageList.add(friendMessages);

							}
						}
							ivFriendMessageList.setVisibility(View.VISIBLE);
							friendMessageListadapter = new FriendMessageListAdapter(
									getActivity(),
									R.layout.friend_message_list,
									friendMessageList, InboxFragment.this);
							ivFriendMessageList
									.setAdapter(friendMessageListadapter);
							lvItemList.setVisibility(View.GONE);
						
					}
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
	}

	void displayInviteGroup(final AudioInfo audioInfo) {

		TextView tvCancel;
		final TextView tvSave;
		TextView tvgroupname, tvinvitegroup, tvgroupmembercount;
		// ListView lvUserList;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.inbox_group_invite, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
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
		
		
		
		lvFriendList = (ListView) promptsView.findViewById(R.id.lvuserlist);
		friendList = new ArrayList<Friend>();
		
		Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
				"ARIAL.TTF");
		
		tvgroupname.setText("Group : " + audioInfo.getGroupName());
		tvgroupname.setTypeface(face);
		
		tvgroupmembercount.setText("Group member(" + audioInfo.getGroupCount()+") :");
		this.audioInfo = audioInfo;
		new GetGroupMemberList().execute();
		tvSave.setVisibility(View.GONE);

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
					  inviteGroup.setgId(audioInfo.getGroupId());
					  
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

	void displayVoiceyFriendCC(final AudioInfo audioInfo) {

		TextView tvCancel, tvSave;
		// ListView lvUserList;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.share_frienslist, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
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

					if (shareFriendList.size() > 0) {

						audioInfo.getCcFriendList().addAll(shareFriendList);

					}

					if (shareGroupList.size() > 0) {

						audioInfo.getCcGroupList().addAll(shareGroupList);

					}

					// }

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

	void displayEditGroup(final AudioInfo audioInfo) {
		TextView EG_addmembers;
		final EditText EG_editgrpname;
		ImageView closedialog;
		Button save_grp;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View grpview = li.inflate(R.layout.edit_group_inbox, null);

		final Dialog releaseNote = new Dialog(getActivity());
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
		tvgroupmembercount= (TextView) grpview.findViewById(R.id.tvgroupmembercount);

		tvgroupmembercount.setText("Group member(" + audioInfo.getGroupCount()+") :");
		
		Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
				"ARIAL.TTF");
		
		

		EG_editgrpname.setText(audioInfo.getGroupName());
		EG_editgrpname.setTypeface(face);
		this.audioInfo = audioInfo;
		friendList = new ArrayList<Friend>();
		// GroupFriendList.clear();
		new getGroupUserlistAsyncTask().execute();

		EG_addmembers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			
				  releaseNote.dismiss();
				  dispayAddGroup(audioInfo);
				 
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
					friends.setGroupID(audioInfo.getGroupId());
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
	
	void dispayAddGroup(final AudioInfo audioInfo) {
		TextView addfrds;
		
		Button create_grp;
		ImageView closedialog;

		LayoutInflater	li = LayoutInflater.from(getActivity());
		View grpview = li.inflate(R.layout.add_members, null);

		final Dialog releaseNote = new Dialog(getActivity());
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
		addfrds.setText(audioInfo.getGroupName());

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
					  inviteGroup.setgId(audioInfo.getGroupId());
					  
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

	void displayVoiceyFriend(final AudioInfo audioInfo, final String type) {

		TextView tvCancel, tvSave;
		// ListView lvUserList;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.share_frienslist, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
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
					StringBuffer groupBuffer = new StringBuffer();

					if (shareFriendList.size() > 0 || shareGroupList.size() > 0) {
						for (Friend f : shareFriendList) {

							friendBuffer.append(f.getFriendId() + ",");

						}

						for (Friend f : shareGroupList) {

							groupBuffer.append(f.getFriendId() + ",");

						}
						if (type.equals("voicey")) {

							friendShareAudio = new FriendShareAudio();
							friendShareAudio.setUserCode(userCode);
							friendShareAudio.setVoiceyId(audioInfo.getId());
							friendShareAudio.setSharedGroupCode(groupBuffer
									.toString());
							friendShareAudio.setFriendlistStr(friendBuffer
									.toString());
							friendShareAudio.setForwardUser(audioInfo.getUser_code());
							new ShareFriendAudio().execute();

						} else if (type.equals("image")) {

							voiceyReply = new VoiceyReply();

							voiceyReply.setTitle("image");

							voiceyReply.setUsercode(userCode);
							voiceyReply.setSharedFriendCode(friendBuffer
									.toString());
							voiceyReply.setSharedGroupCode(groupBuffer
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
							voiceyReply.setSharedGroupCode(groupBuffer
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

	private class getGroupUserlistAsyncTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getGroupUserList(audioInfo.getGroupId());
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
								getActivity(), R.layout.friend_list, friendList);
						lvFriendList.setAdapter(friendAdapter);
					}
				}

			} catch (Exception e) {
				Log.d("UserList", e.getLocalizedMessage());
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

			dialog = new Dialog(getActivity());

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

						Toast.makeText(getActivity(),
								"File share successfully.", Toast.LENGTH_LONG)
								.show();

					} else {
						Toast.makeText(getActivity(),
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

	private class GetFriendList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getFriendList(userCode);
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(getActivity());

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
							String status=jObj.getString("banned_status");
							if(!status.equals("1")){
							// friend.setType("friend");
							friendList.add(friend);
							}
						}
					}
				}

				FriendListAdapter friendAdapter = new FriendListAdapter(
						getActivity(), R.layout.friend_list, friendList);

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

			dialog = new Dialog(getActivity());

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

					Toast.makeText(getActivity(), "Members Added",
							Toast.LENGTH_LONG).show();
					
				//	new GetFriendList().execute();

				} else {
					Toast.makeText(getActivity(),
							"Error occure while creating request.",
							Toast.LENGTH_LONG).show();

				}

			} catch (Exception e) {
				Log.d("SendReq", e.getLocalizedMessage());
			}
		}
	}


	private class GetInviteFriendList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getInviteFriendList(userCode,
					audioInfo.getGroupId());
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(getActivity());

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
						getActivity(), R.layout.friend_list, friendList);

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
					Toast.makeText(getActivity(),
							"Group Name Edited successfully.",
							Toast.LENGTH_LONG).show();
					
					
				} else {
					Toast.makeText(getActivity(),
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
					audioInfo.getGroupId());
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(getActivity());

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
						getActivity(), R.layout.friend_list, friendList);

				lvFriendList.setAdapter(friendAdapter);

				// lvFriendList.setAdapter(friendAdapter);

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
			return Webservices.getGroupUserList(audioInfo.getGroupId());
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(getActivity());

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
						getActivity(), R.layout.friend_list, friendList);

				lvFriendList.setAdapter(friendAdapter);

				// lvFriendList.setAdapter(friendAdapter);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}
	
	private class deleteGroup extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.RemoveGroupMember(friends);
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(getActivity());

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

					Toast.makeText(getActivity(),
							"Group Deleted successfully.",
							Toast.LENGTH_LONG).show();
				
					

				} else {
					Toast.makeText(getActivity(),
							"Error occure while deletimg.",
							Toast.LENGTH_LONG).show();

				}

			} catch (Exception e) {
				Log.d("DelGrp", e.getLocalizedMessage());
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
								holder.rlBody
										.setBackgroundResource(R.color.black);
								shareFriendList.add(friendobj);
								sharefriendId.add(friendobj.getFriendId());
							}
						} else if (friendobj.getType().equals("groupinvite")) {

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

							} else {
								holder.rlBody
										.setBackgroundResource(R.color.black);

								invitefriendId.add(friendobj.getFriendId());
							}

						}

						else {

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

							} else {
								holder.rlBody
										.setBackgroundResource(R.color.black);
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
					friends.setUserId(friends.getFriendId());
					friendList.remove(friends);
					notifyDataSetChanged();
					
					AudioInfo ai = productRegistrationList.get(commentsPosition);
					Integer.parseInt(ai.getGroupCount());
					Integer count=Integer.parseInt(ai.getGroupCount())-1;
					ai.setGroupCount(count.toString());
					tvgroupmembercount.setText("Group member(" + count+") :");

					productRegistrationList.set(commentsPosition, ai);
					adapter.notifyDataSetChanged();
					
					adapter.notifyDataSetChanged();
					

					new deleteGroup().execute();
					// TODO Auto-generated method stub
					
				}
			});
			

			holder.tvUserName.setText(friend.getFriendName());
			holder.tvUserId.setText(friend.getFriendId());

			return convertView;
		}

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String activityResult = sharedPreferences.getString("ActivityResult",
				null);
		if (activityResult != null && activityResult.equals("FriendMessage")) {
			friendMessageListadapter.onActivityResult(requestCode, resultCode,
					data);

			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.remove("ActivityResult");
			editor.commit();

		} else {
			File imgFile;
			switch (requestCode) {
			case (CONTACT_CHOOSER_ACTIVITY_CODE):
				if (resultCode == Activity.RESULT_OK) {

					try {

						String filepath = ringtoneUrl;
						File ringtoneFile = new File(filepath);
						Uri contactData = data.getData();
						String contactId = contactData.getLastPathSegment();
						String[] PROJECTION = new String[] {
								ContactsContract.Contacts._ID,
								ContactsContract.Contacts.DISPLAY_NAME,
								ContactsContract.Contacts.HAS_PHONE_NUMBER, };
						Cursor localCursor = getActivity().getContentResolver()
								.query(contactData, PROJECTION, null, null,
										null);
						localCursor.moveToFirst();
						// --> use moveToFirst instead of this:
						// localCursor.move(Integer.valueOf(contactId));
						// /*CONTACT
						// ID NUMBER*/

						String contactID = localCursor.getString(localCursor
								.getColumnIndexOrThrow("_id"));
						String contactDisplayName = localCursor
								.getString(localCursor
										.getColumnIndexOrThrow("display_name"));

						Uri localUri = Uri.withAppendedPath(
								ContactsContract.Contacts.CONTENT_URI,
								contactID);
						localCursor.close();
						ContentValues localContentValues = new ContentValues();

						localContentValues
								.put(ContactsContract.Data.RAW_CONTACT_ID,
										contactId);
						localContentValues.put(
								ContactsContract.Data.CUSTOM_RINGTONE,
								ringtoneFile.getAbsolutePath());
						getActivity().getContentResolver().update(localUri,
								localContentValues, null, null);

						Toast.makeText(getActivity(),
								"Ringtone Changed for " + contactDisplayName,
								Toast.LENGTH_LONG).show();

					} catch (Exception ex) {
						// Toast.makeText(this, ex.getMessage(),
						// Toast.LENGTH_LONG);
					}
				}
				break;

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
							ivaddimage.setImageBitmap(thePic);

							/*
							 * // call the standard crop action intent (the user
							 * device // may not // support it) Intent
							 * cropIntent = new Intent(
							 * "com.android.camera.action.CROP"); // indicate
							 * image type and Uri
							 * cropIntent.setDataAndType(Uri.fromFile(imgFile),
							 * "image/*"); // set crop properties
							 * cropIntent.putExtra("crop", "true"); // indicate
							 * aspect of desired crop
							 * cropIntent.putExtra("aspectX", 1);
							 * cropIntent.putExtra("aspectY", 1); // indicate
							 * output X and Y cropIntent.putExtra("outputX",
							 * 256); cropIntent.putExtra("outputY", 256); //
							 * retrieve data on return
							 * cropIntent.putExtra("return-data", true); //
							 * start the activity - we handle returning in //
							 * onActivityResult
							 * startActivityForResult(cropIntent, PIC_CROP);
							 */
						}
						// respond to users whose devices do not support the
						// crop
						// action
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
			case (PIC_CROP):
				if (resultCode == Activity.RESULT_OK && null != data) {

					Bundle extras = data.getExtras();
					// get the cropped bitmap
					final Bitmap thePic = extras.getParcelable("data");
					imagebitmap = thePic;
					ivaddimage.setImageBitmap(thePic);

				}
				break;

			case (ACTIVITY_TAKE_PHOTO):
				if (resultCode == Activity.RESULT_OK) {

					try {
						// call the standard crop action intent (the user device
						// may
						// not
						// support it)

						// Bundle extras = data.getExtras();
						// get the cropped bitmap
						final Bitmap thePic = (Bitmap) data.getExtras().get(
								"data");
						imagebitmap = thePic;
						ivaddimage.setImageBitmap(thePic);
						/*
						 * Intent cropIntent = new Intent(
						 * "com.android.camera.action.CROP"); // indicate image
						 * type and Uri cropIntent.setDataAndType(Uri
						 * .fromFile(getTempFile(getActivity()
						 * .getApplicationContext())), "image/*"); // set crop
						 * properties cropIntent.putExtra("crop", "true"); //
						 * indicate aspect of desired crop
						 * cropIntent.putExtra("aspectX", 1);
						 * cropIntent.putExtra("aspectY", 1); // indicate output
						 * X and Y cropIntent.putExtra("outputX", 256);
						 * cropIntent.putExtra("outputY", 256); // retrieve data
						 * on return cropIntent.putExtra("return-data", true);
						 * // start the activity - we handle returning in //
						 * onActivityResult startActivityForResult(cropIntent,
						 * CEMARA_PIC_CROP);
						 */
					}
					// respond to users whose devices do not support the crop
					// action
					catch (ActivityNotFoundException anfe) {
						// display an error message
						String errorMessage = "Whoops - your device doesn't support the crop action!";

					}
				}
				break;
			/*
			 * case (CEMARA_PIC_CROP): if (resultCode == Activity.RESULT_OK &&
			 * null != data) { Bundle extras = data.getExtras(); // get the
			 * cropped bitmap final Bitmap thePic =
			 * extras.getParcelable("data"); imagebitmap = thePic;
			 * ivaddimage.setImageBitmap(thePic);
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

					Cursor cursor = getActivity().getContentResolver().query(
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
					bmThumbnail = ThumbnailUtils.createVideoThumbnail(
							picturePath, Thumbnails.MICRO_KIND);

					// final Bitmap thePic = extras.getParcelable("data");
					// imagebitmap = thePic;
					// ivAddPhoto.setImageBitmap(bmThumbnail);
					imagebitmap = bmThumbnail;
					ivaddimage.setImageBitmap(bmThumbnail);

				}
			}
		}

	}

}
