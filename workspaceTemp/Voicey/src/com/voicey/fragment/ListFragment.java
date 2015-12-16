package com.voicey.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.voicey.activity.HomeActivity;
import com.voicey.activity.R;
import com.voicey.model.AudioInfo;
import com.voicey.model.DownloadStatus;
import com.voicey.model.Friend;
import com.voicey.model.FriendShareAudio;
import com.voicey.model.VoiceyReply;
import com.voicey.utils.Constants;
import com.voicey.webservices.Webservices;

public class ListFragment extends Fragment implements OnClickListener,
		SearchView.OnQueryTextListener, SearchView.OnCloseListener,
		OnItemClickListener {

	SearchView search;
	Spinner sptype, spsort;
	ListView lvItemList;
	List<AudioInfo> audioInfoList;
	private AudioInfo audioInfo;
	Bitmap imagebitmap;
	AudioListAdapter adapter;
	String userCode, userId, userListRefresh;
	Webservices Webservices = new Webservices();
	SharedPreferences sharedPreferences;
	Boolean isRefreshRequired, isSortRequired;
	SearchView searchView;
	TextView tvAutoPlay;
	VoiceyReply voiceyReply;
	String sortValue, typeValue, playValue;
	//static public final int PIC_CROP = 4044;
	String ringtoneUrl;
	private MediaPlayer mediaPlayer;
	private MediaPlayer autoPlayer;
	List<Friend> friendList;
	List<Friend> shareFriendList = new ArrayList<Friend>();
	FriendShareAudio friendShareAudio;
	ArrayList<String> autoUrlList = new ArrayList<String>();
	Integer autoSongCount;
	ListView lvFriendList;
	ImageView ivaddimage;
	private final static int ACTIVITY_TAKE_PHOTO = 1;
	static public final int RESULT_LOAD_IMAGE = 267;
	static public final int PIC_CROP = 4044;
	static public final int CEMARA_PIC_CROP = 9999;
	
	private int mediaFileLengthInMilliseconds;
	static public final int CONTACT_CHOOSER_ACTIVITY_CODE = 73729;
	private final Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, container, false);

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

			// search = (SearchView) v.findViewById(R.id.svtitle);
			sptype = (Spinner) v.findViewById(R.id.sptype);
			spsort = (Spinner) v.findViewById(R.id.spsort);
			lvItemList = (ListView) v.findViewById(R.id.lvcustomlist);
			tvAutoPlay = (TextView) v.findViewById(R.id.tvplayall);
			List<String> list = new ArrayList<String>();
			list.add("Public");
			//list.add("Inbox");
			list.add("My");
			list.add("Favorites");

			List<String> sortlist = new ArrayList<String>();
			sortlist.add("Recent");
			sortlist.add("Title");
			sortlist.add("Plays");
			sortlist.add("User");

			searchView = (SearchView) v.findViewById(R.id.svtitle);
			int id = searchView.getContext().getResources()
					.getIdentifier("android:id/search_src_text", null, null);
			TextView textView = (TextView) searchView.findViewById(id);
			textView.setTextColor(Color.BLACK);
			searchView.setIconifiedByDefault(false);
			searchView.setOnQueryTextListener(this);
			searchView.setOnCloseListener(this);

			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			userCode = sharedPreferences.getString("userCode", null);
			// userName = sharedPreferences.getString("userName", null);

			ArrayAdapter<String> adp1 = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, list);
			adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sptype.setAdapter(adp1);

			String strtext = getArguments().getString("edttext");

			if (strtext.equals("inbox")) {

				sptype.setSelection(1);
			}

			ArrayAdapter<String> adsort = new ArrayAdapter<String>(
					getActivity(), android.R.layout.simple_list_item_1,
					sortlist);
			adsort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spsort.setAdapter(adsort);

			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			userId = sharedPreferences.getString("userId", null);
			userListRefresh = sharedPreferences.getString("islistsyncrequired",
					null);

			isRefreshRequired = true;
			audioInfoList = new ArrayList<AudioInfo>();

			isSortRequired = false;
			playValue = "Auto Play";
			
			getActivity().getWindow().setSoftInputMode(WindowManager.
                    LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			tvAutoPlay.setText(playValue);

			tvAutoPlay.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					try {
						String muteControl = sharedPreferences.getString("muteControl",
								null);
						if (muteControl == null || muteControl.equals("0")) {
						
						if (playValue.equals("Auto Play")) {
							autoSongCount = 0;
							playValue = "Stop";

							tvAutoPlay.setText(playValue);

							if (audioInfoList.size() > 0) {

								for (AudioInfo ai : audioInfoList) {

									if (ai.getFileName() != null) {
										autoUrlList.add(Constants.audio_url
												+ ai.getFileName());

									}

								}
								String url = autoUrlList.get(autoSongCount);

								autoPlayer = new MediaPlayer();
								autoPlayer = new MediaPlayer();
								Uri file = Uri.parse(url);
								autoPlayer.setDataSource(getActivity(), file);
								autoPlayer.prepare();
								autoPlayer.start();
								autoPlayer
										.setOnCompletionListener(new OnCompletionListener() {

											@Override
											public void onCompletion(
													MediaPlayer mp) {
												try {
													autoSongCount = autoSongCount + 1;
													String url = autoUrlList
															.get(autoSongCount);
													autoPlayer.stop();
													autoPlayer.reset();
													Uri file = Uri.parse(url);
													autoPlayer
															.setDataSource(
																	getActivity(),
																	file);
													autoPlayer.prepare();
													autoPlayer.start();

												} catch (Exception e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
										});

							}
							// autoPlayer=new MediaPlayer();

						} else {

							playValue = "Auto Play";
							tvAutoPlay.setText(playValue);
							autoPlayer.stop();
						}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

			sptype.setOnItemSelectedListener(new TypeSelectedListener());
			spsort.setOnItemSelectedListener(new SortSelectedListener());

		} catch (Exception e) {

			e.printStackTrace();
		}

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

		if (suggtionname != null)

			if (orgVoiceyList != null && orgVoiceyList.size() > 0) {
				for (AudioInfo ai : orgVoiceyList) {
					if (ai.getTitle().toLowerCase()
							.startsWith(suggtionname.toString().toLowerCase()))
						rstVoiceyList.add(ai);
				}
			}

		adapter = new AudioListAdapter(getActivity(), R.layout.list_page,
				rstVoiceyList);

		lvItemList.setAdapter(adapter);

	}

	public class TypeSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {

			if (position == 0) {
				typeValue = "Public";
				if (isRefreshRequired) {

					new GetAudioList().execute();

				} else {
					SharedPreferences.Editor editor = sharedPreferences.edit();

					editor.putString("islistsyncrequired", "YES");
					editor.commit();

					adapter = new AudioListAdapter(getActivity(),
							R.layout.list_page, audioInfoList);

					lvItemList.setAdapter(adapter);

					isRefreshRequired = true;

				}

			}/* else if (position == 1) {
				typeValue = "Inbox";
				new GetShareAudioList().execute();

			}*/ else if (position == 1) {
				typeValue = "MY";
				new GetSelfAudioList().execute();

			} else if (position == 2) {
				typeValue = "Favorites";
				new GetSelfShareAudioList().execute();

			}

		}

		public void onNothingSelected(AdapterView<?> arg0) {

		}
	}

	public class SortSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {

			if (position == 0) {
				if (isSortRequired) {
					sortValue = "Recent";
					if (typeValue.equals("MY")) {
						new GetSelfAudioList().execute();
						//
					} else {
						new GetAudioList().execute();
					}

				} else {
					isSortRequired = true;

				}

			} else if (position == 1) {
				sortValue = "Title";
				if (typeValue.equals("Public")) {
					new SortAudioList().execute();
				} else if (typeValue.equals("MY")) {
					new SortSelfAudioList().execute();
				}

			} else if (position == 2) {
				if (typeValue.equals("Public")) {
					sortValue = "Plays";
					new SortAudioList().execute();
				} else if (typeValue.equals("MY")) {
					new SortSelfAudioList().execute();
				}

			} else if (position == 3) {
				sortValue = "User";
				if (typeValue.equals("Public")) {
					new SortAudioList().execute();
				} else if (typeValue.equals("MY")) {

				}

			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {

		}
	}

	private class GetSelfAudioList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getSelfAudioList(userId);
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
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							audioInfo = new AudioInfo();
							audioInfo.setTitle(jObj.getString("title"));
							audioInfo.setSource(jObj.getString("source"));
							audioInfo.setPublic_control(jObj
									.getString("public_control"));
							audioInfo.setUser_control(jObj
									.getString("user_control"));
							audioInfo.setCounter(jObj.getString("counter"));
							audioInfo.setFileName(jObj.getString("audio"));
							audioInfo.setUser_code(jObj.getString("user_id"));
							audioInfo.setType(jObj.getString("cat_type"));
							if (audioInfo.getType().equals("classifield")) {

								audioInfo.setYourAd(jObj.getString("your_ad"));
								audioInfo.setImageName(jObj.getString("image"));
								audioInfo.setCategoryName(jObj
										.getString("category_name"));

							}
							audioInfo.setId(jObj.getString("id"));
							audioInfo.setPosition(i);
							audioInfo.setIsDeleteRequired(true);
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

	

	private class GetSelfShareAudioList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getSelfShareAudioList(userCode);
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
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							audioInfo = new AudioInfo();
							audioInfo.setTitle(jObj.getString("title"));
							audioInfo.setSource(jObj.getString("source"));
							audioInfo.setPublic_control(jObj
									.getString("public_control"));
							audioInfo.setUser_control(jObj
									.getString("user_control"));
							audioInfo.setCounter(jObj.getString("counter"));
							audioInfo.setFileName(jObj.getString("audio"));
							audioInfo.setUser_code(jObj.getString("user_id"));
							audioInfo.setType(jObj.getString("cat_type"));
							if (audioInfo.getType().equals("classifield")) {

								audioInfo.setYourAd(jObj.getString("your_ad"));
								audioInfo.setImageName(jObj.getString("image"));
								audioInfo.setCategoryName(jObj
										.getString("category_name"));
							}
							audioInfo.setId(jObj.getString("id"));
							audioInfo.setPosition(i);
							audioInfo.setIsDeleteRequired(false);
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

	private class GetAudioList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getAudioList();
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
			try {
				dialog.dismiss();
				audioInfoList.clear();
				// List<String> selItemList = new ArrayList<String>();
				Set<String> audioItemset = new HashSet<String>();
				AudioInfo audioInfo;
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							audioInfo = new AudioInfo();
							
							String title=URLDecoder.decode(jObj.getString("title"),"UTF-8");
							audioInfo.setTitle(title);
							audioInfo.setSource(jObj.getString("source"));
							audioInfo.setPublic_control(jObj
									.getString("public_control"));
							audioInfo.setUser_control(jObj
									.getString("user_control"));
							audioInfo.setId(jObj.getString("id"));
							audioInfo.setCounter(jObj.getString("counter"));
							audioInfo.setFileName(jObj.getString("audio"));
							audioInfo.setUser_code(jObj.getString("user_id"));
							audioInfo.setPosition(i);
							audioInfo.setType(jObj.getString("cat_type"));
							if (audioInfo.getType() != null
									&& audioInfo.getType()
											.equals("classifield")) {

								audioInfo.setYourAd(jObj.getString("your_ad"));
								audioInfo.setImageName(jObj.getString("image"));
								audioInfo.setCategoryName(jObj
										.getString("category_name"));
								/*
								 * byte[] decodedString =
								 * Base64.decode(imageData, Base64.DEFAULT);
								 * Bitmap decodedByte =
								 * BitmapFactory.decodeByteArray(decodedString,
								 * 0,decodedString.length);
								 * audioInfo.setImagebitmap(decodedByte);
								 */

							}
							audioInfo.setIsDeleteRequired(false);
							audioInfoList.add(audioInfo);
						}
					}
				}

				adapter = new AudioListAdapter(getActivity(),
						R.layout.list_page, audioInfoList);

				lvItemList.setAdapter(adapter);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	private class SortAudioList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getSortAudioList(sortValue);
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
			try {
				dialog.dismiss();
				audioInfoList.clear();
				// List<String> selItemList = new ArrayList<String>();
				Set<String> audioItemset = new HashSet<String>();
				AudioInfo audioInfo;
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							audioInfo = new AudioInfo();
							audioInfo.setTitle(jObj.getString("title"));
							audioInfo.setSource(jObj.getString("source"));
							audioInfo.setPublic_control(jObj
									.getString("public_control"));
							audioInfo.setUser_control(jObj
									.getString("user_control"));
							audioInfo.setId(jObj.getString("id"));
							audioInfo.setCounter(jObj.getString("counter"));
							audioInfo.setFileName(jObj.getString("audio"));
							audioInfo.setUser_code(jObj.getString("user_id"));
							audioInfo.setPosition(i);
							audioInfo.setType(jObj.getString("cat_type"));
							if (audioInfo.getType().equals("classifield")) {

								audioInfo.setYourAd(jObj.getString("your_ad"));
								audioInfo.setImageName(jObj.getString("image"));
								audioInfo.setCategoryName(jObj
										.getString("category_name"));

							}
							audioInfo.setIsDeleteRequired(false);
							audioInfoList.add(audioInfo);
						}
					}
				}

				adapter = new AudioListAdapter(getActivity(),
						R.layout.list_page, audioInfoList);

				lvItemList.setAdapter(adapter);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	private class SortSelfAudioList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getSortSelfAudioList(sortValue, userId);
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
			try {
				dialog.dismiss();
				audioInfoList.clear();
				// List<String> selItemList = new ArrayList<String>();
				Set<String> audioItemset = new HashSet<String>();
				AudioInfo audioInfo;
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							audioInfo = new AudioInfo();
							audioInfo.setTitle(jObj.getString("title"));
							audioInfo.setSource(jObj.getString("source"));
							audioInfo.setPublic_control(jObj
									.getString("public_control"));
							audioInfo.setId(jObj.getString("id"));
							audioInfo.setUser_control(jObj
									.getString("user_control"));
							audioInfo.setCounter(jObj.getString("counter"));
							audioInfo.setFileName(jObj.getString("audio"));
							audioInfo.setUser_code(jObj.getString("user_id"));
							audioInfo.setIsDeleteRequired(true);
							audioInfo.setPosition(i);
							audioInfo.setType(jObj.getString("cat_type"));
							if (audioInfo.getType().equals("classifield")) {

								audioInfo.setYourAd(jObj.getString("your_ad"));
								audioInfo.setImageName(jObj.getString("image"));
								audioInfo.setCategoryName(jObj
										.getString("category_name"));

							}
							audioInfoList.add(audioInfo);
						}
					}
				}

				adapter = new AudioListAdapter(getActivity(),
						R.layout.list_page, audioInfoList);

				lvItemList.setAdapter(adapter);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

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
		private Boolean isActivePopup = true;
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
			TextView tvUserId,tvsend;
			TextView tvCount, tvFromvalue, tvsentDate;
			RelativeLayout rlBody,rrsend;
			ImageView ivTrash;
			ImageView ivshare,ivrecord,ivaddimage;
			ImageView ivringtone;
			ImageView ivnotify;
			TextView tvclassifield;
			EditText ettextmsg;

			// SeekBar songProgressBar;

		}

		public View getView(int position, View convertView, ViewGroup parent) {

			audioInfo = productRegistrationList.get(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_page, null);
				holder = new ViewHolder();

				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tvtitle);
				holder.tvMood = (TextView) convertView
						.findViewById(R.id.tvmood);
				holder.tvUserId = (TextView) convertView
						.findViewById(R.id.tvuserId);
				holder.ivTrash = (ImageView) convertView
						.findViewById(R.id.ivtrash);
				holder.ivshare = (ImageView) convertView
						.findViewById(R.id.ivshare);
				holder.ivringtone = (ImageView) convertView
						.findViewById(R.id.ivringtone);
				holder.ivnotify = (ImageView) convertView
						.findViewById(R.id.ivnotify);
				holder.tvclassifield = (TextView) convertView
						.findViewById(R.id.tvclassifield);

				holder.tvFromvalue = (TextView) convertView
						.findViewById(R.id.tvfromValue);

				holder.tvsentDate = (TextView) convertView
						.findViewById(R.id.tvdate);
				
				holder.tvsend = (TextView) convertView
						.findViewById(R.id.tvsend);
				
				// isActivePopup = true;

				holder.tvCount = (TextView) convertView
						.findViewById(R.id.tvcount);

				holder.rlBody = (RelativeLayout) convertView
						.findViewById(R.id.rlbody);
				
				holder.ivrecord = (ImageView) convertView
						.findViewById(R.id.ivrecord);
				holder.ivaddimage = (ImageView) convertView
						.findViewById(R.id.ivaddimage);
				
				holder.ettextmsg = (EditText) convertView
						.findViewById(R.id.ettextmsg);
				holder.rrsend = (RelativeLayout) convertView
						.findViewById(R.id.rrsend);
				

				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.tvclassifield.setVisibility(View.GONE);
			if (audioInfo.getCategoryName() != null && !audioInfo.getCategoryName().equals("Type (Optional)")) {
				holder.tvclassifield.setVisibility(View.VISIBLE);
				holder.tvclassifield.setText(audioInfo.getCategoryName());

			}

			if (typeValue.equals("Inbox")) {
				 try {
				holder.tvFromvalue.setText(audioInfo.getFromUserName());
				String inputTimeStamp =audioInfo.getSharedate();

		        final String inputFormat = "yyyy-MM-dd HH:mm:ss";
		        final String outputFormat = "MMM dd HH:mm";

		       
			String dateValue=TimeStampConverter(inputFormat, inputTimeStamp,
					        outputFormat);
				

				holder.tvsentDate.setText(dateValue);
				
				
				
				 } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			} else {
				holder.tvFromvalue.setVisibility(View.GONE);
				holder.tvsentDate.setVisibility(View.GONE);
				
				holder.ivrecord.setVisibility(View.GONE);
				holder.ivaddimage.setVisibility(View.GONE);
				holder.ettextmsg.setVisibility(View.GONE);
				holder.rrsend.setVisibility(View.GONE);
				
			}
			holder.tvTitle.setText(audioInfo.getTitle());
			holder.tvCount.setText("P "+ audioInfo.getCounter());

			holder.tvTitle.setId(position);
			holder.tvTitle.setTag(holder);
			holder.tvUserId.setId(position);
			holder.ivTrash.setId(position);
			holder.ivshare.setId(position);
			holder.ivnotify.setId(position);
			holder.ivringtone.setId(position);
			
			holder.tvsend.setId(position);
			holder.tvsend.setTag(holder);
			
			holder.ivaddimage.setId(position);
			holder.ivrecord.setId(position);
			
			
			holder.ettextmsg.requestFocus();
			
			holder.ivrecord.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					audioInfo = productRegistrationList.get(v.getId());
					((HomeActivity)getActivity()).displayAlert(audioInfo.getUser_code(),null);
						//displayPhotoSelect();

					
				}
			});
			
			holder.ivaddimage.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					audioInfo = productRegistrationList.get(v.getId());
					displayimageReply(audioInfo);
						//displayPhotoSelect();

					
				}
			});

			
			holder.tvsend.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					audioInfo = productRegistrationList.get(v.getId());
					holder = (ViewHolder) v.getTag();	
					
					String title = ((TextView) holder.ettextmsg).getText().toString();
					
					if (title.length() == 0) {
						Toast.makeText(getActivity().getBaseContext(), "Please enter Name it.",
								Toast.LENGTH_LONG).show();

					} else if (title.length() > 140) {
						Toast.makeText(getActivity().getBaseContext(),
								"Title should be less then 15 character.",
								Toast.LENGTH_LONG).show();

					}
					voiceyReply=new VoiceyReply();
					
					voiceyReply.setTitle(title);
					voiceyReply.setUsercode(userCode);
					voiceyReply.setSharedFriendCode(audioInfo.getUser_code());
					voiceyReply.setPublic_control("0");
					voiceyReply.setUser_control("1");
					voiceyReply.setUserid(userId);
					voiceyReply.setType("classifield");
					
					new ReplayText().execute();
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

			holder.tvTitle.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					if (isActivePopup) {
						isActivePopup = false;
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

					}

				}
			});
			if(audioInfo.getSource()!=null&&!audioInfo.getSource().equals("null")){

			holder.tvMood.setText("# " + audioInfo.getSource());
			}

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

			if (audioInfo.getIsDeleteRequired() != null
					&& audioInfo.getIsDeleteRequired()) {

				holder.ivTrash.setVisibility(View.VISIBLE);
			} else {

				holder.ivTrash.setVisibility(View.GONE);

			}

			holder.ivshare.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					audioInfo = productRegistrationList.get(v.getId());

					displayShareAlert(audioInfo);

					/*
					 * File f = new
					 * File(Environment.getExternalStorageDirectory() +
					 * "/Voicey/" + audioInfo.getTitle() + ".3gp");
					 * 
					 * if (f.exists()) {
					 * 
					 * shareAudioWhatsApp(Environment.
					 * getExternalStorageDirectory() + "/Voicey/" +
					 * audioInfo.getTitle() + ".3gp");
					 * 
					 * 
					 * shareallthing(Environment.getExternalStorageDirectory() +
					 * "/Voicey/" + audioInfo.getTitle() + ".3gp"); } else { new
					 * voiceyDownload().execute(); }
					 */

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

				}
			});
			//
			// URL

			return convertView;
		}
		
		

		
		
		 private  String TimeStampConverter(final String inputFormat,
		            String inputTimeStamp, final String outputFormat)
		            throws ParseException {
		        return new SimpleDateFormat(outputFormat).format(new SimpleDateFormat(
		                inputFormat).parse(inputTimeStamp));
		    }

		SeekBar songProgressBar;
		ImageView ivimage;

		void displayAlert(AudioInfo audioInfo, final Integer position,
				final ViewHolder holder) {
			// SeekBar songProgressBar;
			TextView tvcancel;
			TextView tvtitle, tvMore;

			try {
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.play_audio, null);
				mediaPlayer = new MediaPlayer();
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setView(promptsView);
				tvcancel = (TextView) promptsView.findViewById(R.id.tvcancel);
				tvtitle = (TextView) promptsView.findViewById(R.id.tvtitle);
				tvMore = (TextView) promptsView.findViewById(R.id.tvmore);
				ivimage = (ImageView) promptsView.findViewById(R.id.ivimage);
				Typeface face = Typeface.createFromAsset(context.getAssets(),
						"verdana.ttf");

				tvtitle.setTypeface(face);
				tvcancel.setTypeface(face);
				if (audioInfo.getType() != null
						&& audioInfo.getType().equals("classifield")) {

					if (audioInfo.getImageName() != null&&audioInfo.getImageName().length()>0) {
						new ImageDownloader().execute();
					} else {

						ivimage.setVisibility(View.GONE);
					}

					tvMore.setText(audioInfo.getYourAd());
					tvMore.setTypeface(face);
					// / ivimage.setImageBitmap(audioInfo.getImagebitmap());

				} else {

					ivimage.setVisibility(View.GONE);

				}

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

							mediaPlayer.stop();

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

				String url = Constants.audio_url + audioInfo.getFileName();

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
					ivimage.setImageBitmap(result);
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
				return Webservices.voiceyDelete(audioId);
			}

			protected void onPostExecute(String result) {

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

		private class ringtoneDownload extends
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

					setRingTone(downloadStatus.getUrl(),
							downloadStatus.getTitle());

				} else {

					Toast.makeText(context,
							"Error occurred while downloading ",
							Toast.LENGTH_LONG).show();
				}

			}
		}

		private class ringtoneContactDownload extends
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

					ringtoneUrl = Environment.getExternalStorageDirectory()
							+ "/Voicey/" + audioInfo.getFileName() + ".3gp";
					setRingToneContact();

				} else {

					Toast.makeText(context,
							"Error occurred while downloading ",
							Toast.LENGTH_LONG).show();
				}

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
					//sendIntent.setPackage("com.android.mms");
					sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
					sendIntent.setType("video/3gp");
					startActivity(Intent.createChooser(sendIntent, "Send file"));
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

		public void notificationMail(AudioInfo audioInfo) {

			String message = " Voicey Title:" + audioInfo.getTitle()
					+ " , Report by:" + userCode;

			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL,
					new String[] { Constants.notifican_mail });
			// email.putExtra(Intent.EXTRA_CC, new String[]{ to});
			// email.putExtra(Intent.EXTRA_BCC, new String[]{to});
			email.putExtra(Intent.EXTRA_SUBJECT,
					Constants.notifican_mail_subject);
			email.putExtra(Intent.EXTRA_TEXT, message);

			// need this to prompts email client only
			email.setType("message/rfc822");

			context.startActivity(Intent.createChooser(email,
					"Choose an Email client :"));

		}

		/*
		 * public void shareallthing(String url) {
		 * 
		 * String path = url;
		 * 
		 * Intent share = new Intent(Intent.ACTION_SEND);
		 * share.setType("video/*"); share.putExtra(Intent.EXTRA_STREAM,
		 * Uri.parse("file:///" + path));
		 * context.startActivity(Intent.createChooser(share,
		 * "Share Sound File")); }
		 */
		
		void displayimageReply(final AudioInfo audioInfo){
			
			
			TextView tvcancel,tvReply;
			LayoutInflater li = LayoutInflater.from(context);
			View promptsView = li.inflate(R.layout.image_reply, null);
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setView(promptsView);
			ivaddimage = (ImageView) promptsView.findViewById(R.id.ivaddimage);
			
			tvcancel = (TextView) promptsView.findViewById(R.id.tvcancel);
			tvReply = (TextView) promptsView.findViewById(R.id.tvreply);
			
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
						
						if (imagebitmap != null) {

							File file = new File(Constants.image_folder + "/"
									+ userCode + ".jpeg");

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

						voiceyReply=new VoiceyReply();
						
						voiceyReply.setTitle(userCode);
						voiceyReply.setUsercode(userCode);
						voiceyReply.setSharedFriendCode(audioInfo.getUser_code());
						voiceyReply.setPublic_control("0");
						voiceyReply.setUser_control("1");
						voiceyReply.setUserid(userId);
						voiceyReply.setType("classifield");
						alertDialog.cancel();
						new ReplayImage().execute();
						
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			

			
			
		}
		void displayShareAlert(final AudioInfo audioInfo) {

			TextView tvcancel;
			TextView tvfriend, tvWhatsapp, tvmessage;
			LayoutInflater li = LayoutInflater.from(context);
			View promptsView = li.inflate(R.layout.share_popup, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setView(promptsView);
			tvfriend = (TextView) promptsView.findViewById(R.id.tvfriend);
			tvWhatsapp = (TextView) promptsView.findViewById(R.id.tvwhatsapp);
			tvmessage = (TextView) promptsView.findViewById(R.id.tvmessage);
			tvcancel = (TextView) promptsView.findViewById(R.id.tvcancel);
			Typeface face = Typeface.createFromAsset(context.getAssets(),
					"verdana.ttf");

			tvfriend.setTypeface(face);
			tvWhatsapp.setTypeface(face);
			tvmessage.setTypeface(face);
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

			tvfriend.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					try {

						alertDialog.cancel();
						displayVoiceyFriend(audioInfo);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			tvWhatsapp.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					try {

						alertDialog.cancel();
						File f = new File(Environment
								.getExternalStorageDirectory()
								+ "/Voicey/"
								+ audioInfo.getTitle() + ".3gp");

						if (f.exists()) {

							shareAudioWhatsApp(Environment
									.getExternalStorageDirectory()
									+ "/Voicey/"
									+ audioInfo.getTitle() + ".3gp");

							/*
							 * shareallthing(Environment.getExternalStorageDirectory
							 * () + "/Voicey/" + audioInfo.getTitle() + ".3gp");
							 */
						} else {

							File file = new File(Environment
									.getExternalStorageDirectory()
									+ "/Voicey/"
									+ audioInfo.getFileName());

							if (file.exists()) {

								shareAudioWhatsApp(Environment
										.getExternalStorageDirectory()
										+ "/Voicey/" + audioInfo.getFileName());
							}
							new voiceyDownload().execute();
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			tvmessage.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					try {

						alertDialog.cancel();

						File f = new File(Environment
								.getExternalStorageDirectory()
								+ "/Voicey/"
								+ audioInfo.getTitle() + ".3gp");

						if (f.exists()) {

							shareAudioMMS(Environment
									.getExternalStorageDirectory()
									+ "/Voicey/"
									+ audioInfo.getTitle() + ".3gp");

							/*
							 * shareallthing(Environment.getExternalStorageDirectory
							 * () + "/Voicey/" + audioInfo.getTitle() + ".3gp");
							 */
						} else {
							File file = new File(Environment
									.getExternalStorageDirectory()
									+ "/Voicey/"
									+ audioInfo.getFileName());

							if (file.exists()) {

								shareAudioMMS(Environment
										.getExternalStorageDirectory()
										+ "/Voicey/" + audioInfo.getFileName());
							} else {
								new voiceMMSyDownload().execute();
							}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

		}

		void displayVoiceyFriend(final AudioInfo audioInfo) {

			
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

						friendShareAudio = new FriendShareAudio();
						friendShareAudio.setUserCode(userCode);
						friendShareAudio.setVoiceyId(audioInfo.getId());
						StringBuffer friendBuffer = new StringBuffer();
						if (shareFriendList.size() > 0) {
							for (Friend f : shareFriendList) {

								friendBuffer.append(f.getFriendId() + ",");

							}
							friendShareAudio.setFriendlistStr(friendBuffer
									.toString());
							new ShareFriendAudio().execute();

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
		
		private class ReplayImage extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				//publishProgress((Void[]) null);
				return Webservices.replayImage(voiceyReply);
			}

			protected void onProgressUpdate(Void... progress) {

				
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(String result) {
				//dialog.dismiss();
				try {
					if (result != null && result.length() > 0) {

						JSONObject jObj = new JSONObject(result);

						String status = jObj.getString("status");

						if (status.equals("1")) {

							Toast.makeText(getActivity(),
									"send reply successfully.",
									Toast.LENGTH_LONG).show();

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
				//publishProgress((Void[]) null);
				return Webservices.replayText(voiceyReply);
			}

			protected void onProgressUpdate(Void... progress) {

				
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(String result) {
				//dialog.dismiss();
				try {
					if (result != null && result.length() > 0) {

						JSONObject jObj = new JSONObject(result);

						String status = jObj.getString("status");

						if (status.equals("1")) {

							Toast.makeText(getActivity(),
									"send reply successfully.",
									Toast.LENGTH_LONG).show();

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
									"File share successfully.",
									Toast.LENGTH_LONG).show();

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
								String friendName=URLDecoder.decode(jObj.getString("friend_name"),"UTF-8");
								friend.setFriendId(jObj
										.getString("added_friend"));
								friend.setFriendName(friendName);
								friend.setType("friend");
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
					convertView.setTag(holder);
				} else
					holder = (ViewHolder) convertView.getTag();

				holder.rlBody.setId(position);
				holder.rlBody.setTag(holder);
				shareFriendList.clear();
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

				holder.rlBody.setOnClickListener(new OnClickListener() {

					@SuppressLint("NewApi")
					public void onClick(View v) {

						friendobj = friendList.get(v.getId());
						int id = v.getId();
						holder = (ViewHolder) v.getTag();
						holder.rlBody.setBackgroundResource(R.color.black);
						if (shareFriendList.contains(friendobj)) {
							shareFriendList.remove(friendobj);

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
						}

					}
				});

				holder.tvUserName.setText(friend.getFriendName());
				holder.tvUserId.setText(friend.getFriendId());

				return convertView;
			}

		}

		void displayRingtoneAlert(final AudioInfo audioInfo) {
			TextView tvcancel;
			TextView tvGenral, tvspecific;
			LayoutInflater li = LayoutInflater.from(context);
			View promptsView = li.inflate(R.layout.ringtone_popup, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
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
						alertDialog.cancel();
						File f = new File(Environment
								.getExternalStorageDirectory()
								+ "/Voicey/"
								+ audioInfo.getTitle() + ".3gp");

						if (f.exists()) {

							ringtoneUrl = Environment
									.getExternalStorageDirectory()
									+ "/Voicey/"
									+ audioInfo.getTitle() + ".3gp";
							setRingToneContact();
						} else {
							File file = new File(Environment
									.getExternalStorageDirectory()
									+ "/Voicey/"
									+ audioInfo.getFileName());

							if (file.exists()) {
								ringtoneUrl = Environment
										.getExternalStorageDirectory()
										+ "/Voicey/" + audioInfo.getFileName();
								setRingToneContact();

							} else {

								new ringtoneContactDownload().execute();

							}
						}
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
						File f = new File(Environment
								.getExternalStorageDirectory()
								+ "/Voicey/"
								+ audioInfo.getTitle() + ".3gp");

						if (f.exists()) {

							setRingTone(
									Environment.getExternalStorageDirectory()
											+ "/Voicey/" + audioInfo.getTitle()
											+ ".3gp", audioInfo.getTitle());
						} else {
							File file = new File(Environment
									.getExternalStorageDirectory()
									+ "/Voicey/"
									+ audioInfo.getFileName());

							if (file.exists()) {
								setRingTone(
										Environment
												.getExternalStorageDirectory()
												+ "/Voicey/"
												+ audioInfo.getFileName(),
										audioInfo.getTitle());

							} else {

								new ringtoneDownload().execute();

							}

						}

						// alertDialog.cancel();

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

		}

		public void setRingToneContact() {

			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
			startActivityForResult(intent, CONTACT_CHOOSER_ACTIVITY_CODE);

		}

		public void setRingTone(String url, String title) {
			try {
				String filepath = url;
				File ringtoneFile = new File(filepath);
				if (ringtoneFile.exists()) {

					ContentValues content = new ContentValues();
					content.put(MediaStore.MediaColumns.DATA,
							ringtoneFile.getAbsolutePath());
					content.put(MediaStore.MediaColumns.TITLE, title);
					content.put(MediaStore.MediaColumns.SIZE, 215454);
					content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
					content.put(MediaStore.Audio.Media.ARTIST, "artist");
					content.put(MediaStore.Audio.Media.DURATION, 230);
					content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
					content.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
					content.put(MediaStore.Audio.Media.IS_ALARM, false);
					content.put(MediaStore.Audio.Media.IS_MUSIC, false);

					Uri uri = MediaStore.Audio.Media
							.getContentUriForPath(ringtoneFile
									.getAbsolutePath());
					Uri newUri = context.getContentResolver().insert(uri,
							content);
					// ringtoneUri = newUri;

					RingtoneManager.setActualDefaultRingtoneUri(context,
							RingtoneManager.TYPE_RINGTONE, newUri);
					Toast.makeText(context, "Ringtone Changed successfully",
							Toast.LENGTH_LONG).show();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
	
	void displayPhotoSelect() {

		TextView camera, gallery;
		final Dialog dialog = new Dialog(getActivity());

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.photo_select);

		camera = (TextView) dialog.findViewById(R.id.tvcamera);
		gallery = (TextView) dialog.findViewById(R.id.tvgallery);

		dialog.show();

		camera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				takePictureButtonClicked();
				dialog.dismiss();
			}
		});

		gallery.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

	}

	private void takePictureButtonClicked() {
		Uri imageUri = Uri.fromFile(getTempFile(getActivity().getApplicationContext()));
		Intent intent = createIntentForCamera(imageUri);
	    startActivityForResult(intent, ACTIVITY_TAKE_PHOTO);
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
							.query(contactData, PROJECTION, null, null, null);
					localCursor.moveToFirst();
					// --> use moveToFirst instead of this:
					// localCursor.move(Integer.valueOf(contactId)); /*CONTACT
					// ID NUMBER*/

					String contactID = localCursor.getString(localCursor
							.getColumnIndexOrThrow("_id"));
					String contactDisplayName = localCursor
							.getString(localCursor
									.getColumnIndexOrThrow("display_name"));

					Uri localUri = Uri.withAppendedPath(
							ContactsContract.Contacts.CONTENT_URI, contactID);
					localCursor.close();
					ContentValues localContentValues = new ContentValues();

					localContentValues.put(
							ContactsContract.Data.RAW_CONTACT_ID, contactId);
					localContentValues.put(
							ContactsContract.Data.CUSTOM_RINGTONE,
							ringtoneFile.getAbsolutePath());
					getActivity().getContentResolver().update(localUri,
							localContentValues, null, null);

					Toast.makeText(getActivity(),
							"Ringtone Changed for " + contactDisplayName,
							Toast.LENGTH_LONG).show();

				} catch (Exception ex) {
					// Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
				}
			}
			break;
			
		case (RESULT_LOAD_IMAGE):
			if (resultCode == Activity.RESULT_OK) {

				try {
					
					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getActivity().getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

					String picturePath = cursor.getString(columnIndex);
					cursor.close();

					imgFile = new File(picturePath);

					// new LoginActss().execute();

					try {
						// call the standard crop action intent (the user device may not
						// support it)
						Intent cropIntent = new Intent("com.android.camera.action.CROP");
						// indicate image type and Uri
						cropIntent.setDataAndType(Uri.fromFile(imgFile), "image/*");
						// set crop properties
						cropIntent.putExtra("crop", "true");
						// indicate aspect of desired crop
						cropIntent.putExtra("aspectX", 1);
						cropIntent.putExtra("aspectY", 1);
						// indicate output X and Y
						cropIntent.putExtra("outputX", 256);
						cropIntent.putExtra("outputY", 256);
						// retrieve data on return
						cropIntent.putExtra("return-data", true);
						// start the activity - we handle returning in onActivityResult
						startActivityForResult(cropIntent, PIC_CROP);
					}
					// respond to users whose devices do not support the crop action
					catch (ActivityNotFoundException anfe) {
						// display an error message
						String errorMessage = "Whoops - your device doesn't support the crop action!";
						
						
					}
					
				} catch (Exception ex) {
					// Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
				}
				}
		break;
		case (PIC_CROP):
			if (resultCode == Activity.RESULT_OK&& null != data) {
				
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
					// call the standard crop action intent (the user device may not
					// support it)
					Intent cropIntent = new Intent("com.android.camera.action.CROP");
					// indicate image type and Uri
					cropIntent.setDataAndType(
							Uri.fromFile(getTempFile(getActivity().getApplicationContext())),
							"image/*");
					// set crop properties
					cropIntent.putExtra("crop", "true");
					// indicate aspect of desired crop
					cropIntent.putExtra("aspectX", 1);
					cropIntent.putExtra("aspectY", 1);
					// indicate output X and Y
					cropIntent.putExtra("outputX", 256);
					cropIntent.putExtra("outputY", 256);
					// retrieve data on return
					cropIntent.putExtra("return-data", true);
					// start the activity - we handle returning in onActivityResult
					startActivityForResult(cropIntent, CEMARA_PIC_CROP);
				}
				// respond to users whose devices do not support the crop action
				catch (ActivityNotFoundException anfe) {
					// display an error message
					String errorMessage = "Whoops - your device doesn't support the crop action!";
					
				}
			}
			break;
		case (CEMARA_PIC_CROP):
			  if (resultCode == Activity.RESULT_OK && null != data) {
				Bundle extras = data.getExtras();
				// get the cropped bitmap
				final Bitmap thePic = extras.getParcelable("data");
				imagebitmap = thePic;
				ivaddimage.setImageBitmap(thePic);

		
				break;
		
			  }
		}

	}
	

}
