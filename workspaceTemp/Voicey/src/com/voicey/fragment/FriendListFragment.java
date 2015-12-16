package com.voicey.fragment;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

//import com.harman.harmanapplication.businessobjects.ComplaintRegistration;
import com.voicey.activity.R;
import com.voicey.adapter.UserListAdapater;
import com.voicey.model.AudioInfo;
import com.voicey.model.Friend;
import com.voicey.model.GroupInfo;
import com.voicey.model.InviteGroup;
import com.voicey.model.PhoneContacts;
import com.voicey.model.User;
import com.voicey.webservices.Webservices;

public class FriendListFragment extends Fragment implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener,
		OnClickListener {

	TextView tvInvite, tvFriendList, tvRequests, tvSent, tvgroup, grpTitleName,
			invite_to_grp, bak, invite_now, EG_addmembers, TvNote, ed_name,
			cam, gal, close, closedia;
	EditText grpName, EG_editgrpname;
	Button create_grp, save_grp;
	Friend friend;
	List<Friend> shareFriendList = new ArrayList<Friend>();
	List<String> sharefriendId = new ArrayList<String>();
	Webservices Webservices = new Webservices();
	SharedPreferences sharedPreferences;
	String userCode, userName, userPhone, userEmail, groupName, GroupTitle,
			stat;
	FriendListAdapter friendAdapter;
	UserListAdapater userAdapter;
	List<Friend> friendList, requestlist, grouplist, contactslist,
			populatelist, GroupFriendList, allcontact, alluser, tempbuff,
			searchlist;
	Friend contact, Frd;
	List<GroupInfo> grpList;
	List<PhoneContacts> contactList;
	PhoneContacts contacts;
	List<User> userList, selectedList, inviteList;
	List<String> members;
	CharSequence[] items;
	ListView lvFriendList, grpFriendList;
	ListView lvUserList;
	SearchView mainsearchView, searchView, searchViewgrp;
	CheckBox check;
	AlertDialog.Builder alertDialogBuilder, Builder;
	Dialog releaseNote, popup;
	View grpview;
	LayoutInflater li;
	String listString, savegroupname, GrpID, contactstring;
	TextView addfrds;
	LinearLayout addview;
	ImageView next, closedialog, grppic;
	String GroupID, count_type;
	JSONObject Contactlist;
	JSONArray jsonArr;
	int count, grp_pic_count = 0;
	private ListView UserListView;
	private User[] planets;
	private ArrayAdapter<User> SelectuserAdapter;
	InviteGroup inviteGroup;
	String[] codes;
	String searchcontrol = "MAIN";
	String control_pic = "CREATE";
	// Activity request codes
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final int RESULT_LOAD_IMAGE = 1;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_friend_list, container,
				false);

		initilizeUI(v);
		GetCountryZipCode();
		populatelist.clear();
		new MainListLoader().execute();
		return v;
	}

	public String GetCountryZipCode() {
		String CountryID = "AL";
		String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		codes = this.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < codes.length; i++) {
			String[] g = codes[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0].toString().replaceAll("[^0-9]", "");
				break;
			}
		}
		return CountryZipCode;
	}

	public String fetchContacts() {

		String phoneNumber = null;
		String email = null;

		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

		Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;

		ContentResolver contentResolver = getActivity().getContentResolver();

		Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null,
				null);

		try {
			// Loop for every contact in the phone
			if (cursor.getCount() > 0) {
				jsonArr = new JSONArray();
				while (cursor.moveToNext()) {
					contacts = new PhoneContacts();
					contact = new Friend();
					String contact_id = cursor.getString(cursor
							.getColumnIndex(_ID));
					String name = cursor.getString(cursor
							.getColumnIndex(DISPLAY_NAME));

					int hasPhoneNumber = Integer
							.parseInt(cursor.getString(cursor
									.getColumnIndex(HAS_PHONE_NUMBER)));

					if (hasPhoneNumber > 0) {
						Contactlist = new JSONObject();

						// output.append("\n First Name:" + name);
						// contacts.setFirstName(name);
						// contact.setFirstName(name);
						Contactlist.put("NAME", name);
						System.out.println("\n First Name:" + name);
						// Query and loop for every phone number of the contact
						Cursor phoneCursor = contentResolver.query(
								PhoneCONTENT_URI, null, Phone_CONTACT_ID
										+ " = ?", new String[] { contact_id },
								null);

						while (phoneCursor.moveToNext()) {
							phoneNumber = phoneCursor.getString(phoneCursor
									.getColumnIndex(NUMBER));

							Contactlist.put("PHONE_NUM", phoneNumber);
							System.out
									.println("\n Phone number:" + phoneNumber);
						}

						phoneCursor.close();

						// Query and loop for every email of the contact
						Cursor emailCursor = contentResolver.query(
								EmailCONTENT_URI, null, EmailCONTACT_ID
										+ " = ?", new String[] { contact_id },
								null);

						while (emailCursor.moveToNext()) {

							email = emailCursor.getString(emailCursor
									.getColumnIndex(DATA));
							Contactlist.put("EMAIL", email);
							System.out.println("\nEmail:" + email);

						}

						emailCursor.close();
						jsonArr.put(Contactlist);
					}
				}
			}
			writeToFile(jsonArr.toString());
			return jsonArr.toString();
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return "EMPTY";
	}

	private String readFromFile() {

		String ret = "";

		try {
			InputStream inputStream = getActivity().openFileInput(
					"contacts.txt");

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("Contacts", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("Contacts", "Can not read file: " + e.toString());
		}

		return ret;
	}

	private void writeToFile(String data) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					getActivity().openFileOutput("contacts.txt",
							Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	private void initilizeUI(View v) {

		try {
			friendList = new ArrayList<Friend>();
			requestlist = new ArrayList<Friend>();
			grouplist = new ArrayList<Friend>();
			contactslist = new ArrayList<Friend>();
			populatelist = new ArrayList<Friend>();
			GroupFriendList = new ArrayList<Friend>();
			alluser = new ArrayList<Friend>();
			allcontact = new ArrayList<Friend>();
			tempbuff = new ArrayList<Friend>();
			searchlist = new ArrayList<Friend>();
			contactList = new ArrayList<PhoneContacts>();

			tvFriendList = (TextView) v.findViewById(R.id.tvfriendlist);
			tvRequests = (TextView) v.findViewById(R.id.tvrequests);
			tvSent = (TextView) v.findViewById(R.id.tvsent);
			tvgroup = (TextView) v.findViewById(R.id.tvgroup);
			TvNote = (TextView) v.findViewById(R.id.tvnote);
			mainsearchView = (SearchView) v.findViewById(R.id.mainsearch);
			mainsearchView.setIconifiedByDefault(false);
			mainsearchView.setOnQueryTextListener(this);
			mainsearchView.setOnCloseListener(this);
			tvInvite = (TextView) v.findViewById(R.id.tvinvite);
			lvFriendList = (ListView) v.findViewById(R.id.lvfriendlist);

			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			userCode = sharedPreferences.getString("userCode", null);
			userName = sharedPreferences.getString("userName", null);
			userPhone = sharedPreferences.getString("userPhone", null);
			userEmail = sharedPreferences.getString("userEmail", null);
			tvFriendList.setPaintFlags(tvFriendList.getPaintFlags()
					| Paint.UNDERLINE_TEXT_FLAG);

			tvgroup.setOnClickListener(this);
			tvInvite.setOnClickListener(this);
			tvFriendList.setOnClickListener(this);
			tvRequests.setOnClickListener(this);
			tvSent.setOnClickListener(this);

			count_type = "EMPTY";

			String strtext = getArguments().getString("edttext");

			if (strtext.equals("requests")) {
				tvFriendList.setPaintFlags(tvFriendList.getPaintFlags()
						& (~Paint.UNDERLINE_TEXT_FLAG));
				tvSent.setPaintFlags(tvFriendList.getPaintFlags()
						& (~Paint.UNDERLINE_TEXT_FLAG));
				tvRequests.setPaintFlags(tvRequests.getPaintFlags()
						| Paint.UNDERLINE_TEXT_FLAG);
				populatelist.clear();
				new GetRequestList().execute();

			}
			fetchContacts();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvgroup:
			creategroup();
			break;

		case R.id.tvfriendlist:
			populatelist.clear();
			new MainListLoader().execute();
			break;

		case R.id.tvrequests:
			tvFriendList.setPaintFlags(tvFriendList.getPaintFlags()
					& (~Paint.UNDERLINE_TEXT_FLAG));
			tvSent.setPaintFlags(tvFriendList.getPaintFlags()
					& (~Paint.UNDERLINE_TEXT_FLAG));

			tvRequests.setPaintFlags(tvRequests.getPaintFlags()
					| Paint.UNDERLINE_TEXT_FLAG);

			new GetRequestList().execute();
			break;

		case R.id.tvsent:
			tvFriendList.setPaintFlags(tvFriendList.getPaintFlags()
					& (~Paint.UNDERLINE_TEXT_FLAG));
			tvRequests.setPaintFlags(tvFriendList.getPaintFlags()
					& (~Paint.UNDERLINE_TEXT_FLAG));
			tvSent.setPaintFlags(tvSent.getPaintFlags()
					| Paint.UNDERLINE_TEXT_FLAG);
			new GetSentList().execute();
			break;
		case R.id.tvinvite:
			displayInvite();

			break;
		default:
			break;
		}
	}

	/** Holds child views for one row. */
	private static class SelectedUserViewHolder {
		private CheckBox checkBox;
		private TextView textView;

		// public SelectedUserViewHolder() {
		// }

		public SelectedUserViewHolder(TextView textView, CheckBox checkBox) {
			this.checkBox = checkBox;
			this.textView = textView;
		}

		public CheckBox getCheckBox() {
			return checkBox;
		}

		// public void setCheckBox(CheckBox checkBox) {
		// this.checkBox = checkBox;
		// }

		public TextView getTextView() {
			return textView;
		}

		// public void setTextView(TextView textView) {
		// this.textView = textView;
		// }
	}

	/** Custom adapter for displaying an array of Planet objects. */
	private static class SelectUserAdapter extends ArrayAdapter<User> {

		private LayoutInflater inflater;

		public SelectUserAdapter(Context context, List<User> planetList) {
			super(context, R.layout.multi_selection_list, R.id.tv_userName,
					planetList);
			// Cache the LayoutInflate to avoid asking for a new one each time.
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Planet to display
			User planet = (User) this.getItem(position);

			// The child views in each row.
			CheckBox checkBox;
			TextView textView;

			// Create a new row view
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.multi_selection_list,
						null);

				// Find the child views.
				textView = (TextView) convertView
						.findViewById(R.id.tv_userName);
				checkBox = (CheckBox) convertView
						.findViewById(R.id.checked_user);

				// Optimization: Tag the row with it's child views, so we don't
				// have to
				// call findViewById() later when we reuse the row.
				convertView.setTag(new SelectedUserViewHolder(textView,
						checkBox));

				// If CheckBox is toggled, update the planet it is tagged with.
				checkBox.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						User planet = (User) cb.getTag();
						planet.setChecked(cb.isChecked());
					}
				});
			}
			// Reuse existing row view
			else {
				// Because we use a ViewHolder, we avoid having to call
				// findViewById().
				SelectedUserViewHolder viewHolder = (SelectedUserViewHolder) convertView
						.getTag();
				checkBox = viewHolder.getCheckBox();
				textView = viewHolder.getTextView();
			}

			// Tag the CheckBox with the Planet it is displaying, so that we can
			// access the planet in onClick() when the CheckBox is toggled.
			checkBox.setTag(planet);

			// Display planet data
			checkBox.setChecked(planet.isChecked());
			textView.setText(planet.getUserName());

			return convertView;
		}

	}

	public Object onRetainNonConfigurationInstance() {
		return planets;
	}

	public List<Friend> allcontactslist() {
		Friend group;
		contactslist.clear();
		try {
			// fetching contacts
			String allcontacts = readFromFile();
			if (!(allcontacts.equals("EMPTY")) && allcontacts.length() > 0) {
				JSONArray newarr = new JSONArray(allcontacts);
				if (newarr.length() > 0) {
					for (int i = 0; i < newarr.length(); i++) {
						group = new Friend();
						JSONObject jObj = newarr.getJSONObject(i);
						String Fname = URLDecoder.decode(
								jObj.getString("NAME"), "UTF-8");
						group.setFirstName(Fname);
						group.setSearchName(Fname);
						group.setPhoneNumber(jObj.getString("PHONE_NUM"));
						group.setType("contacts");
						contactslist.add(group);
					}
				}
			}

			return contactslist;
		} catch (Exception e) {
			Log.d("contact_List", e.getLocalizedMessage());
		}
		return contactslist;
	}

	private class MainListLoader extends AsyncTask<String, Void, List<Friend>> {
		Dialog dialog;

		@Override
		protected List<Friend> doInBackground(String... urls) {
			publishProgress((Void[]) null);
			try {
				Friend group;
				Friend friend;
				Friend user, contact;

				String grp_list = Webservices.getgroupList(userCode);
				String frd_list = Webservices.getFriendList(userCode);
				String req_list = Webservices.getReceiveRequestList(userCode);
				String allusers_list = Webservices.getAllUsers();

				alluser.clear();
				populatelist.clear();
				friendList.clear();
				requestlist.clear();
				grouplist.clear();
				contactslist.clear();

				// Fetching friends list
				if (frd_list != null && frd_list.length() > 0) {
					JSONArray arr = new JSONArray(frd_list);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							friend = new Friend();
							String friendName = URLDecoder.decode(
									jObj.getString("friend_name"), "UTF-8");
							friend.setFriendId(jObj.getString("added_friend"));
							friend.setFriendName(friendName);
							friend.setSearchName(friendName);
							if (jObj.getString("type").equals("friend")) {
								friend.setPhoneNumber(jObj.getString("phone"));
								friend.setEmailId(jObj.getString("email"));
								friend.setBlockId(jObj.getString("id"));
								friend.setBlockState(jObj
										.getString("banned_status"));
								friend.setType("friend");
								friendList.add(friend);
							}
						}
					}
				}

				// Fetching Friend request list
				if (req_list != null && req_list.length() > 0) {

					JSONArray arr = new JSONArray(req_list);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							friend = new Friend();
							String userName = URLDecoder.decode(
									jObj.getString("name"), "UTF-8");
							friend.setFriendId(jObj.getString("user_id"));
							friend.setFriendName(userName);
							friend.setSearchName(userName);
							friend.setBlockState("0");
							friend.setType("request");
							requestlist.add(friend);
						}
					}
				}

				// Fetching Group list
				if (grp_list != null && grp_list.length() > 0) {
					JSONArray arr = new JSONArray(grp_list);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							group = new Friend();
							String Name = URLDecoder.decode(
									jObj.getString("group_name"), "UTF-8");

							if (jObj.getString("super_user").equals(userCode)) {
								group.setType("grp");
								group.setGroupID(jObj.getString("id"));
								group.setGroupName(Name);
								group.setSearchName(Name);
								group.setBlockState("0");
							} else {
								group.setType("other_grp");
								group.setFriendId(jObj.getString("super_user"));
								group.setGroupID(jObj.getString("id"));
								group.setGroupName(Name);
								group.setSearchName(Name);
								group.setBlockState("0");
							}
							grouplist.add(group);
						}
					}
				}

				contactstring = "";

				if (allusers_list != null && allusers_list.length() > 0) {
					JSONArray arr = new JSONArray(allusers_list);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							user = new Friend();
							String Name = URLDecoder.decode(
									jObj.getString("name"), "UTF-8");
							user.setFirstName(Name);
							user.setSearchName(Name);
							user.setUserId(jObj.getString("user_id"));
							user.setPhoneNumber(jObj.getString("phone"));
							user.setEmailId(jObj.getString("email"));
							user.setBlockState("0");
							alluser.add(user);
						}
					}
				}
				contactslist = allcontactslist();

				for (int i = 0; alluser.size() > i; i++) {

					contact = alluser.get(i);

					for (int j = 0; contactslist.size() > j; j++) {
						user = contactslist.get(j);
						String temp = user.getPhoneNumber().replaceAll(
								"[\\s()-]", "");
						//
						if (temp.equals(contact.getPhoneNumber())
								&& !(contact.getUserId().equals(""))) {
							//
							tempbuff.add(contact);

							contactstring += contact.getUserId() + ",";
							contactslist.remove(j);

						}
					}
				}

				if (friendList.size() != 0)
					contactstring = "";
				for (int k = 0; tempbuff.size() > k; k++) {

					Friend tempfrd = tempbuff.get(k);
					String tempfrdvalue = tempfrd.getUserId();
					for (int l = 0; friendList.size() > l; l++) {
						friend = friendList.get(l);
						if (tempfrdvalue.equals(friend.getFriendId())) {
							tempbuff.remove(k);
						}
					}
				}
				if (tempbuff.size() != 0) {
					contactstring = "";
					for (int c = 0; tempbuff.size() > c; c++) {
						friend = tempbuff.get(c);
						contactstring = friend.getUserId() + ",";
					}
				}

				if (contactstring.equals("")) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getActivity(),
									"No New Friends on Voicey!",
									Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							new AutoFriends().execute();
						}
					});
				}

				populatelist.addAll(friendList);
				populatelist.addAll(requestlist);
				populatelist.addAll(grouplist);
				populatelist.addAll(contactslist);

			} catch (Exception e) {
				Log.d("MAIN_List", e.getLocalizedMessage());
			}

			return populatelist;

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

		protected void onPostExecute(List<Friend> result) {
			dialog.dismiss();
			if (result.size() == 0) {
				TvNote.setVisibility(View.VISIBLE);
				mainsearchView.setVisibility(View.GONE);
			} else
				TvNote.setVisibility(View.GONE);

			searchlist.addAll(result);

			friendAdapter = new FriendListAdapter(getActivity(),
					R.layout.friend_list, result);
			lvFriendList.setAdapter(friendAdapter);
		}
	}

	private class getGrouplistAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getgroupList(userCode);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				Friend group;
				populatelist.clear();
				grouplist.clear();
				if (result != null && result.length() > 0) {
					JSONArray arr = new JSONArray(result);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							group = new Friend();
							String Name = URLDecoder.decode(
									jObj.getString("group_name"), "UTF-8");

							if (jObj.getString("super_user").equals(userCode)) {
								group.setType("grp");
								group.setGroupID(jObj.getString("id"));
								group.setGroupName(Name);
							} else {
								group.setType("other_grp");
								group.setFriendId(jObj.getString("super_user"));
								group.setGroupID(jObj.getString("id"));
								group.setGroupName(Name);
							}
							grouplist.add(group);
						}
					}
				}
				contactslist.clear();
				// Fetching contacts
				String allcontacts = readFromFile();
				if (!(result.equals("EMPTY")) && result.length() > 0) {
					JSONArray newarr = new JSONArray(allcontacts);
					if (newarr.length() > 0) {
						for (int i = 0; i < newarr.length(); i++) {
							group = new Friend();
							JSONObject jObj = newarr.getJSONObject(i);
							String Fname = URLDecoder.decode(
									jObj.getString("NAME"), "UTF-8");
							group.setFirstName(Fname);
							group.setPhoneNumber(jObj.getString("PHONE_NUM"));
							group.setType("contacts");
							contactslist.add(group);
						}
					}
				}
				populatelist.addAll(friendList);
				populatelist.addAll(requestlist);
				populatelist.addAll(grouplist);
				populatelist.addAll(contactslist);
				friendAdapter = new FriendListAdapter(getActivity(),
						R.layout.friend_list, populatelist);
				lvFriendList.setAdapter(friendAdapter);

			} catch (Exception e) {
				Log.d("GrpList", e.getLocalizedMessage());
			}
		}
	}

	class Mapping extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getAllUsers();
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				Friend user, contact;

				alluser.clear();
				allcontact.clear();
				contactstring = "";

				if (result != null && result.length() > 0) {
					JSONArray arr = new JSONArray(result);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							user = new Friend();
							String Name = URLDecoder.decode(
									jObj.getString("name"), "UTF-8");
							user.setFirstName(Name);
							user.setUserId(jObj.getString("user_id"));
							user.setPhoneNumber(jObj.getString("phone"));
							user.setEmailId(jObj.getString("email"));

							alluser.add(user);
						}
					}
				}
				allcontact = allcontactslist();

				for (int i = 0; allcontact.size() > i; i++) {

					contact = allcontact.get(i);

					for (int j = 0; alluser.size() > j; j++) {
						user = alluser.get(j);
						String temp = contact.getPhoneNumber().replaceAll(
								"[\\s()-]", "");
						if (temp.equals(user.getPhoneNumber())
								&& !(user.getUserId().equals(""))) {

							contactstring += user.getUserId() + ",";
							allcontact.remove(i);
						}
					}
				}
				new AutoFriends().execute();
			} catch (Exception e) {
				Log.d("ALL_USER_MAP", e.getLocalizedMessage());
			}
		}

	}

	class AutoFriends extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.addFriendsAuto(userCode, contactstring);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj = new JSONObject(result);

					String status = jObj.getString("status");

					if (status.equals("1")) {

						Toast.makeText(getActivity(), "Contacts Syced!",
								Toast.LENGTH_LONG).show();
						new MainListLoader().execute();
					}

				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	private class getGroupUserlistAsyncTask extends
			AsyncTask<String, Void, List<Friend>> {
		Dialog dialog;

		@Override
		protected List<Friend> doInBackground(String... urls) {
			publishProgress((Void[]) null);

			String result = Webservices.getGroupUserList(GroupID);
			try {
				if (result != null) {
					Friend user;
					if (result != null && result.length() > 0) {

						JSONArray arr = new JSONArray(result);
						if (arr.length() > 0) {
							for (int i = 0; i < arr.length(); i++) {
								JSONObject jObj = arr.getJSONObject(i);
								user = new Friend();
								String Name = URLDecoder.decode(
										jObj.getString("name"), "UTF-8");
								user.setGroupID(jObj.getString("gid"));
								user.setFriendId(jObj.getString("user_id"));
								if (stat.equals("EDIT")) {
									user.setType("rm_frd");
								} else
									user.setType("grp_frd");
								user.setFriendName(Name);

								GroupFriendList.add(user);
							}
						}
					}

					count = GroupFriendList.size();
					return GroupFriendList;
				}

			} catch (Exception e) {
				Log.d("GrpUserList", e.getLocalizedMessage());
			}

			return GroupFriendList;
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

		@Override
		protected void onPostExecute(List<Friend> result) {

			dialog.dismiss();
			if (count_type.equals("COUNT") || control_pic.equals("EDIT")) {
				EditGroup(GroupID);
			} else if (count_type.equals("BODY")) {
				grouphome(GroupID);
			}

			friendAdapter = new FriendListAdapter(getActivity(),
					R.layout.friend_list, result);
			lvFriendList.setAdapter(friendAdapter);
		}

	}

	private class newUserlistAsyncTask extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getInviteUserList(userCode, GroupID);
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

		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				if (result != null) {
					User user;

					if (result != null && result.length() > 0) {

						JSONArray arr = new JSONArray(result);
						if (arr.length() > 0) {
							for (int i = 0; i < arr.length(); i++) {
								JSONObject jObj = arr.getJSONObject(i);
								user = new User();
								String Name = URLDecoder.decode(
										jObj.getString("name"), "UTF-8");
								user.setUserId(jObj.getString("user_id"));
								user.setUserName(Name);

								userList.add(user);
							}
						}
						SelectuserAdapter = new SelectUserAdapter(
								getActivity(), userList);
						UserListView.setAdapter(SelectuserAdapter);

						selectedList = new ArrayList<User>();

						UserListView
								.setOnItemClickListener(new AdapterView.OnItemClickListener() {
									@Override
									public void onItemClick(
											AdapterView<?> parent, View item,
											int position, long id) {
										User planet = SelectuserAdapter
												.getItem(position);
										SelectedUserViewHolder viewHolder = (SelectedUserViewHolder) item
												.getTag();

										if (!(planet.isChecked())) {

											planet.toggleChecked();
											viewHolder.getCheckBox()
													.setChecked(
															planet.isChecked());

											selectedList.add(planet);
										} else {
											planet.toggleChecked();
											viewHolder.getCheckBox()
													.setChecked(
															planet.isChecked());
										}
									}
								});

					}
				}

			} catch (Exception e) {
				Log.d("AddNewUserList",
				// ||e.getLocalizedMessage()
						"NO MESSAGE");
			}
		}
	}

	private class getUserlistAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getUserList(userCode);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result != null) {
					User user;

					if (result != null && result.length() > 0) {

						JSONArray arr = new JSONArray(result);
						if (arr.length() > 0) {
							for (int i = 0; i < arr.length(); i++) {
								JSONObject jObj = arr.getJSONObject(i);
								user = new User();
								String Name = URLDecoder.decode(
										jObj.getString("name"), "UTF-8");
								user.setUserId(jObj.getString("id"));
								user.setUserCode(jObj.getString("user_id"));
								user.setUserName(Name);

								userList.add(user);
							}
						}

						SelectuserAdapter = new SelectUserAdapter(
								getActivity(), userList);
						UserListView.setAdapter(SelectuserAdapter);

						selectedList = new ArrayList<User>();

						UserListView
								.setOnItemClickListener(new AdapterView.OnItemClickListener() {
									@Override
									public void onItemClick(
											AdapterView<?> parent, View item,
											int position, long id) {
										User planet = SelectuserAdapter
												.getItem(position);
										System.out.println("Item Clicked: "
												+ planet);
										// planet.toggleChecked();
										SelectedUserViewHolder viewHolder = (SelectedUserViewHolder) item
												.getTag();

										if (!(planet.isChecked())) {

											planet.toggleChecked();
											viewHolder.getCheckBox()
													.setChecked(
															planet.isChecked());

											selectedList.add(planet);
											System.out.println(selectedList
													.toString());
										} else {
											planet.toggleChecked();
											viewHolder.getCheckBox()
													.setChecked(
															planet.isChecked());

											selectedList.remove(planet);
											System.out.println(selectedList
													.toString());
										}
									}
								});

					}
				}

			} catch (Exception e) {
				Log.d("UserList",
				// ||e.getLocalizedMessage()
						"NO MESSAGE");
			}
		}
	}

	void creategroup() {

		li = LayoutInflater.from(getActivity());
		grpview = li.inflate(R.layout.create_group, null);

		releaseNote = new Dialog(getActivity());
		releaseNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		releaseNote.setContentView(grpview);
		releaseNote.setCancelable(true);
		releaseNote.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		grpName = (EditText) grpview.findViewById(R.id.grp_name);
		addview = (LinearLayout) grpview.findViewById(R.id.add_groupies);
		// next = (ImageView) grpview.findViewById(R.id.add_frd);
		UserListView = (ListView) grpview.findViewById(R.id.friendlist);
		create_grp = (Button) grpview.findViewById(R.id.create_grp);
		closedia = (TextView) grpview.findViewById(R.id.closedialog);
		grppic = (ImageView) grpview.findViewById(R.id.capture);

		userList = new ArrayList<User>();

		new getUserlistAsyncTask().execute();

		// Checking camera availability
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getActivity(),
					"Sorry! Your device doesn't support camera",
					Toast.LENGTH_LONG).show();
		} else {
			getFromSdcard();

			grppic.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					releaseNote.dismiss();
					Chooser();
				}
			});
		}

		create_grp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (grpName.getText().toString().equals("")) {
					Toast.makeText(getActivity(), "Group Name Field Empty",
							Toast.LENGTH_LONG).show();
				} else {
					if (selectedList.size() <= 0) {
						Toast.makeText(getActivity(),
								"Select atleast one member for the group!",
								Toast.LENGTH_LONG).show();
					} else {
						try {
							listString = "";
							groupName = grpName.getText().toString();
							String converttitle = URLEncoder.encode(groupName,
									"UTF-8");
							savegroupname = converttitle;

							for (User s : selectedList) {
								listString += s.getUserCode() + ",";
							}

							new sendRequiest().execute();
							populatelist.clear();
							releaseNote.dismiss();
							new MainListLoader().execute();
							// new GetFriendList().execute();
						} catch (Exception e) {
							Log.d("GroupName", e.getLocalizedMessage());
						}
					}
				}
			}
		});

		closedia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
			}
		});
		releaseNote.show();
	}

	public void getFromSdcard() {
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "Voicey" + File.separator + "group_1.png");
		if (f.exists()) {
			String path = f.getAbsolutePath();
			Bitmap myBitmap = BitmapFactory.decodeFile(path);
			grppic.setImageBitmap(myBitmap);
		} else {
			Resources res = getResources();
			/** from an Activity */
			grppic.setImageDrawable(res.getDrawable(R.drawable.add_img));
		}

	}

	void Chooser() {

		li = LayoutInflater.from(getActivity());
		grpview = li.inflate(R.layout.photo_select, null);

		releaseNote = new Dialog(getActivity());
		releaseNote.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		releaseNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		releaseNote.setContentView(grpview);
		releaseNote.setCancelable(false);
		releaseNote.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		cam = (TextView) grpview.findViewById(R.id.tvcamera);
		gal = (TextView) grpview.findViewById(R.id.tvgallery);
		close = (TextView) grpview.findViewById(R.id.tvcancel);
		TextView tv1 = (TextView) grpview.findViewById(R.id.tvvideo);
		TextView tv2 = (TextView) grpview.findViewById(R.id.textView34);

		tv1.setVisibility(View.GONE);
		tv2.setVisibility(View.GONE);

		cam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent,
						CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
			}
		});

		gal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
				// String path = Environment.getExternalStorageDirectory()
				// + "/images/imagename.jpg";
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
				if (control_pic.equals("EDIT")) {
					GroupFriendList.clear();
					new getGroupUserlistAsyncTask().execute();
				} else {
					control_pic = "CREATE";
					creategroup();
				}
			}
		});
		releaseNote.show();
	}

	void addmembers() {

		li = LayoutInflater.from(getActivity());
		grpview = li.inflate(R.layout.add_members, null);

		releaseNote = new Dialog(getActivity());
		releaseNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		releaseNote.setContentView(grpview);
		releaseNote.setCancelable(true);
		releaseNote.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		addfrds = (TextView) grpview.findViewById(R.id.grpname);
		UserListView = (ListView) grpview.findViewById(R.id.friendlist);
		create_grp = (Button) grpview.findViewById(R.id.add_mem);
		closedialog = (ImageView) grpview.findViewById(R.id.closedialog);

		addfrds.setText(GroupTitle);

		userList = new ArrayList<User>();
		new newUserlistAsyncTask().execute();

		create_grp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (selectedList.size() <= 0) {
					Toast.makeText(getActivity(),
							"Select atleast one member for the group!",
							Toast.LENGTH_LONG).show();
				} else {
					listString = "";
					savegroupname = addfrds.getText().toString();

					for (User s : selectedList) {
						listString += s.getUserId() + ",";
					}

					new addnewmember().execute();
					populatelist.clear();
					releaseNote.dismiss();
					// new GetFriendList().execute();
					new MainListLoader().execute();
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

	void EditGroup(String GID) {

		li = LayoutInflater.from(getActivity());
		grpview = li.inflate(R.layout.edit_group, null);

		releaseNote = new Dialog(getActivity());
		releaseNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		releaseNote.setContentView(grpview);
		releaseNote.setCancelable(true);
		releaseNote.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		EG_addmembers = (TextView) grpview.findViewById(R.id.add_members);
		EG_editgrpname = (EditText) grpview.findViewById(R.id.grp_name);
		ed_name = (TextView) grpview.findViewById(R.id.grpname);
		lvFriendList = (ListView) grpview.findViewById(R.id.friendlist);
		closedia = (TextView) grpview.findViewById(R.id.closedialog);
		grppic = (ImageView) grpview.findViewById(R.id.grp_img);
		save_grp = (Button) grpview.findViewById(R.id.save_grp);
		ed_name.setText(GroupTitle + " (" + count + ")");
		EG_editgrpname.setText(GroupTitle);
		count_type = "EMPTY";

		EG_addmembers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				releaseNote.dismiss();
				GroupFriendList.clear();
				addmembers();
			}
		});

		// Checking camera availability
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getActivity(),
					"Sorry! Your device doesn't support camera",
					Toast.LENGTH_LONG).show();
		} else {
			getFromSdcard();
			grppic.setOnClickListener(new OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(View v) {
					releaseNote.dismiss();
					control_pic = "EDIT";
					Chooser();
				}
			});
		}

		save_grp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String nam = EG_editgrpname.getText().toString();
					String converttitle = URLEncoder.encode(nam, "UTF-8");
					Frd = new Friend();
					if (!nam.equals("")) {
						releaseNote.dismiss();
						GroupFriendList.clear();
						Frd.setGroupName(converttitle);
						Frd.setUserId(userCode);
						Frd.setGroupID(GroupID);
						new editGroup().execute();
					}
				} catch (Exception e) {
					Log.d("editGrp", e.getLocalizedMessage());
				}
			}
		});

		closedia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GroupFriendList.clear();
				releaseNote.dismiss();
			}
		});
		releaseNote.show();
	}

	private class getfriendlisttoinviteAsyncTask extends
			AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getInviteFriendList(userCode, GroupID);
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

		@Override
		protected void onPostExecute(String result) {
			try {
				dialog.dismiss();
				if (result != null) {
					User user;
					userList.clear();
					if (result != null && result.length() > 0) {

						JSONArray arr = new JSONArray(result);
						if (arr.length() > 0) {
							for (int i = 0; i < arr.length(); i++) {
								JSONObject jObj = arr.getJSONObject(i);
								user = new User();
								String Name = URLDecoder.decode(
										jObj.getString("friend_name"), "UTF-8");
								user.setUserId(jObj.getString("added_friend"));
								user.setUserCode(jObj.getString("super_user"));
								user.setGroupId(jObj.getString("gid"));
								user.setUserName(Name);

								userList.add(user);
							}
						}

						SelectuserAdapter = new SelectUserAdapter(
								getActivity(), userList);
						lvFriendList.setAdapter(SelectuserAdapter);

						inviteList = new ArrayList<User>();

						lvFriendList
								.setOnItemClickListener(new AdapterView.OnItemClickListener() {
									@Override
									public void onItemClick(
											AdapterView<?> parent, View item,
											int position, long id) {
										User planet = SelectuserAdapter
												.getItem(position);
										System.out.println("Item Clicked: "
												+ planet);
										// planet.toggleChecked();
										SelectedUserViewHolder viewHolder = (SelectedUserViewHolder) item
												.getTag();

										if (!(planet.isChecked())) {

											planet.toggleChecked();
											viewHolder.getCheckBox()
													.setChecked(
															planet.isChecked());

											inviteList.add(planet);
											System.out.println(inviteList
													.toString());
										} else {
											planet.toggleChecked();
											viewHolder.getCheckBox()
													.setChecked(
															planet.isChecked());

											inviteList.remove(planet);
											System.out.println(inviteList
													.toString());
										}
									}
								});

					}
				}

			} catch (Exception e) {
				Log.d("UserList",
				// ||e.getLocalizedMessage()
						"NO MESSAGE");
			}
		}
	}

	void grouphome(String GID) {

		li = LayoutInflater.from(getActivity());
		grpview = li.inflate(R.layout.group_home, null);

		releaseNote = new Dialog(getActivity());
		releaseNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		releaseNote.setContentView(grpview);
		releaseNote.setCancelable(true);
		releaseNote.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		invite_to_grp = (TextView) grpview.findViewById(R.id.invitetogrp);
		bak = (TextView) grpview.findViewById(R.id.back);
		invite_now = (TextView) grpview.findViewById(R.id.invitenow);
		grpTitleName = (TextView) grpview.findViewById(R.id.grpname);
		lvFriendList = (ListView) grpview.findViewById(R.id.grpfriendlist);
		closedialog = (ImageView) grpview.findViewById(R.id.closedialog);

		grpTitleName.setText(GroupTitle + "  (" + GroupFriendList.size() + ")");
		count_type = "EMPTY";
		// GroupFriendList.clear();
		// new getGroupUserlistAsyncTask().execute();

		bak.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				invite_to_grp.setVisibility(View.VISIBLE);
				bak.setVisibility(View.GONE);
				invite_now.setVisibility(View.GONE);
				count_type = "BODY";
				releaseNote.dismiss();
				GroupFriendList.clear();
				new getGroupUserlistAsyncTask().execute();
			}
		});

		invite_to_grp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				invite_to_grp.setVisibility(View.GONE);
				bak.setVisibility(View.VISIBLE);
				invite_now.setVisibility(View.VISIBLE);
				userList = new ArrayList<User>();
				new getfriendlisttoinviteAsyncTask().execute();
			}
		});

		invite_now.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (inviteList.size() <= 0) {
					Toast.makeText(getActivity(), "No Invites",
							Toast.LENGTH_LONG).show();
				} else {
					listString = "";

					for (User s : inviteList) {
						listString += s.getUserId() + ",";
						GrpID = s.getGroupId();
					}

					inviteGroup = new InviteGroup();

					inviteGroup.setSharedTo(listString);
					inviteGroup.setSharedFrom(userCode);
					inviteGroup.setgId(GrpID);

					releaseNote.dismiss();
					new SendGroupInvite().execute();
				}
			}
		});

		closedialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GroupFriendList.clear();
				releaseNote.dismiss();
			}
		});
		releaseNote.show();
	}

	private class SendGroupInvite extends AsyncTask<String, Void, String> {

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

						Toast.makeText(getActivity(), "Invite Requested",
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
		populatelist.clear();
		new MainListLoader().execute();
		// new GetFriendList().execute();

		tvCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});

		tvSave.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

			}
		});

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

	}

	public boolean onQueryTextChange(String newText) {
		if (searchcontrol.equals("MAIN"))
			SearchResults(newText);
		else if (newText.length() == 0)
			showResults("EMPTY");
		else
			showResults(newText);
		return false;
	}

	public boolean onQueryTextSubmit(String query) {
		if (searchcontrol.equals("MAIN"))
			SearchResults(query);
		else
			showResults(query);

		return false;
	}

	public boolean onClose() {
		SearchResults("");
		showResults("");
		return false;
	}

	private void showResults(String query) {

		String suggtionname = query != null ? query.toString() : "@@@@";

		List<User> orgVoiceyList = new ArrayList<User>();
		List<User> rstVoiceyList = new ArrayList<User>();
		orgVoiceyList.addAll(userList);

		if (query.equals("EMPTY"))
			rstVoiceyList.clear();
		else {
			for (User user : orgVoiceyList) {
				if (user.getUserName().toLowerCase()
						.contains(suggtionname.toString().toLowerCase())
						|| user.getUserCode()
								.toLowerCase()
								.startsWith(
										suggtionname.toString().toLowerCase()))
					rstVoiceyList.add(user);
			}
		}

		userAdapter = new UserListAdapater(getActivity(), R.layout.user_list,
				rstVoiceyList);

		lvUserList.setAdapter(userAdapter);

		lvUserList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				User user = (User) parent.getItemAtPosition(position);
				String username = user.getUserCode();
				friend = new Friend();
				friend.setFriendId(username);
				friend.setUserId(userCode);
				friend.setFriendName(user.getUserName());
				new sendfrdRequiest().execute();

			}

		});

	}

	private void SearchResults(String query) {

		String suggtionname = query != null ? query.toString() : "@@@@";

		List<Friend> orgVoiceyList = new ArrayList<Friend>();
		List<Friend> rstVoiceyList = new ArrayList<Friend>();
		orgVoiceyList.addAll(searchlist);

		for (Friend user : orgVoiceyList) {
			if (user.getSearchName().toLowerCase()
					.startsWith(suggtionname.toString().toLowerCase()))
				rstVoiceyList.add(user);
		}

		friendAdapter = new FriendListAdapter(getActivity(),
				R.layout.friend_list, rstVoiceyList);
		lvFriendList.setAdapter(friendAdapter);
	}

	void displayInvite() {

		TextView tvCancel, tvVoicey, tvWhatsapp, tvPhone;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.invite_popup, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		tvCancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvVoicey = (TextView) promptsView.findViewById(R.id.tvvoicey);
		tvWhatsapp = (TextView) promptsView.findViewById(R.id.tvwhatsapp);
		tvPhone = (TextView) promptsView.findViewById(R.id.tvphone);

		Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
				"verdana.ttf");

		tvPhone.setTypeface(face);
		tvVoicey.setTypeface(face);
		tvWhatsapp.setTypeface(face);

		tvVoicey.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					displayVoiceyFriend();
					alertDialog.cancel();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		tvWhatsapp.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					Intent share = new Intent(Intent.ACTION_SEND);
					share.setType("text/plain");
					share.putExtra(
							Intent.EXTRA_TEXT,
							" My Voiccey ID is "
									+ userCode
									+ " and my Voicey name is "
									+ userName
									+ " Voicey. Say it. Send it. 6 Sec. Download here: \n http://voicey.me/web-services/appinstaller.php ");
					if (isPackageInstalled("com.whatsapp", getActivity())) {
						share.setPackage("com.whatsapp");
						getActivity().startActivity(
								Intent.createChooser(share, "Share Video"));

					} else {

						Toast.makeText(getActivity(),
								"Please Install Whatsapp", Toast.LENGTH_LONG)
								.show();
					}

					alertDialog.cancel();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		tvPhone.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At
																				// least
																				// KitKat
					{
						String defaultSmsPackageName = Telephony.Sms
								.getDefaultSmsPackage(getActivity()); // Need to
																		// change
																		// the
																		// build
																		// to
																		// API
																		// 19

						Intent sendIntent = new Intent(Intent.ACTION_SEND);
						sendIntent.setType("text/plain");

						sendIntent
								.putExtra(
										Intent.EXTRA_TEXT,
										"  My Voiccey ID is "
												+ userCode
												+ " and my Voicey name is "
												+ userName
												+ " Voicey. Say it. Send it. 6 Sec. Download here: \n http://voicey.me/web-services/appinstaller.php ");
						if (defaultSmsPackageName != null)// Can be null in case
															// that there is no
															// default, then the
															// user would be
															// able to choose
															// any app that
															// support this
															// intent.
						{
							sendIntent.setPackage(defaultSmsPackageName);
						}
						startActivity(sendIntent);

					} else // For early versions, do what worked for you before.
					{
						Intent sendIntent = new Intent(Intent.ACTION_VIEW);
						sendIntent.setData(Uri.parse("sms:"));
						sendIntent
								.putExtra(
										"sms_body",
										"  My Voiccey ID is "
												+ userCode
												+ " and my Voicey name is "
												+ userName
												+ " Voicey. Say it. Send it. 6 Sec. Download here: \n http://voicey.me/web-services/appinstaller.php ");

						startActivity(sendIntent);
					}

					alertDialog.cancel();

				} catch (Exception e) {
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

	private boolean isPackageInstalled(String packagename, Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	void displayVoiceyFriend() {

		// final EditText etFriendId;
		// final EditText etFriendName;
		TextView tvCancel, tvSave;
		// ListView lvUserList;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.invite_friend_popup, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		// etFriendId = (EditText) promptsView.findViewById(R.id.etuserid);
		// etFriendName = (EditText) promptsView.findViewById(R.id.etname);
		tvCancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		// tvSave = (TextView) promptsView.findViewById(R.id.tvsave);
		searchView = (SearchView) promptsView.findViewById(R.id.svsearch);
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(this);
		searchView.setOnCloseListener(this);
		searchcontrol = "USER";
		lvUserList = (ListView) promptsView.findViewById(R.id.lvuserlist);
		userList = new ArrayList<User>();
		new getUserAsyncTask().execute();

		tvCancel.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					searchcontrol = "MAIN";
					alertDialog.cancel();

				} catch (Exception e) {
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

	private class getUserAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getUserList(userCode);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result != null) {
					User user;

					if (result != null && result.length() > 0) {

						JSONArray arr = new JSONArray(result);
						if (arr.length() > 0) {
							for (int i = 0; i < arr.length(); i++) {
								JSONObject jObj = arr.getJSONObject(i);
								user = new User();
								String Name = URLDecoder.decode(
										jObj.getString("name"), "UTF-8");
								user.setUserId(jObj.getString("id"));
								user.setUserCode(jObj.getString("user_id"));
								user.setUserName(Name);

								userList.add(user);
							}
						}
						// userAdapter = new UserListAdapater(getActivity(),
						// R.layout.user_list, userList);
						// lvUserList.setAdapter(userAdapter);
						//
						// lvUserList
						// .setOnItemClickListener(new OnItemClickListener() {
						// public void onItemClick(
						// AdapterView<?> parent, View view,
						// int position, long id) {
						//
						// User user = (User) parent
						// .getItemAtPosition(position);
						// String username = user.getUserCode();
						// friend = new Friend();
						// friend.setFriendId(username);
						// friend.setUserId(userCode);
						// friend.setFriendName(user.getUserName());
						//
						// new sendfrdRequiest().execute();
						//
						// }
						//
						// });
					}
				}

			} catch (Exception e) {
				Log.d("GetUser",
				// ||e.getLocalizedMessage()
						"NO MESSAGE");
			}
		}

	}

	private class sendfrdRequiest extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.sendFriendRequest(friend);
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
					Toast.makeText(getActivity(), "Invite sent",
							Toast.LENGTH_LONG).show();
				} else if (status.equals("2")) {
					Toast.makeText(getActivity(), "Invite Already sent",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getActivity(),
							"Error occure while creating request.",
							Toast.LENGTH_LONG).show();

				}

			} catch (Exception e) {
				Log.d("sendFrdReq", e.getLocalizedMessage());
			}
		}
	}

	private class addnewmember extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.addmember(GroupID, listString);
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
					populatelist.clear();
					new MainListLoader().execute();
					// new GetFriendList().execute();

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

	private class sendRequiest extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.creatgroup(userCode, savegroupname, listString);
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

					Toast.makeText(getActivity(),
							"Group created successfully.", Toast.LENGTH_LONG)
							.show();
					populatelist.clear();
					new MainListLoader().execute();
					// new GetFriendList().execute();

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

	/*
	 * private class GetFriendList extends AsyncTask<String, Void, String> {
	 * 
	 * @Override protected String doInBackground(String... urls) {
	 * publishProgress((Void[]) null); return
	 * Webservices.getFriendList(userCode); }
	 * 
	 * @Override protected void onPostExecute(String result) { try {
	 * friendList.clear(); Friend friend; if (result != null && result.length()
	 * > 0) { JSONArray arr = new JSONArray(result); if (arr.length() > 0) { for
	 * (int i = 0; i < arr.length(); i++) { JSONObject jObj =
	 * arr.getJSONObject(i); friend = new Friend(); String friendName =
	 * URLDecoder.decode( jObj.getString("friend_name"), "UTF-8");
	 * friend.setFriendId(jObj.getString("added_friend"));
	 * friend.setFriendName(friendName); if
	 * (jObj.getString("type").equals("friend")) { friend.setType("friend");
	 * friendList.add(friend); } } } } } catch (Exception e) {
	 * Log.d("Friendlist", e.getLocalizedMessage()); } } }
	 */

	private class GetRequestList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getReceiveRequestList(userCode);
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(getActivity());

			dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			dialog.setContentView(R.layout.loading_layout);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			// dialog.show();

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			// dialog.dismiss();
			try {
				// dialog.dismiss();
				requestlist.clear();
				Friend friend;
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							friend = new Friend();
							String userName = URLDecoder.decode(
									jObj.getString("name"), "UTF-8");
							friend.setFriendId(jObj.getString("user_id"));
							friend.setFriendName(userName);
							friend.setType("request");
							requestlist.add(friend);
						}
					}
				}
				populatelist.clear();
				new getGrouplistAsyncTask().execute();
			} catch (Exception e) {
				Log.d("ReqList", e.getLocalizedMessage());
			}
		}

	}

	private class GetSentList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getSentRequestList(userCode);
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
							String userName = URLDecoder.decode(
									jObj.getString("name"), "UTF-8");
							friend.setFriendId(jObj.getString("user_id"));
							friend.setFriendName(userName);
							friend.setType("sent");
							friendList.add(friend);
						}
					}
				}

				friendAdapter = new FriendListAdapter(getActivity(),
						R.layout.friend_list, friendList);

				lvFriendList.setAdapter(friendAdapter);

			} catch (Exception e) {
				Log.d("SentList", e.getLocalizedMessage());
			}
		}

	}

	private class editGroup extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.editGroup(Frd);
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
					populatelist.clear();
					new MainListLoader().execute();
					// new GetFriendList().execute();
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

	public class FriendListAdapter extends ArrayAdapter<Friend> {

		Context context;
		List<Friend> friendList;
		ViewHolder holder = null;
		Friend friends;
		String condition;

		public FriendListAdapter(Context context, int resourceId,
				List<Friend> friendList) {
			super(context, resourceId, friendList);
			this.context = context;
			this.friendList = friendList;
		}

		public int getCount() {
			return friendList.size();
		}

		public Friend getItem(Friend position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {

			TextView tvFriendName;
			TextView tvFriendId;
			RelativeLayout rlBody;
			TextView tvAccept;
			TextView tvAdmin;
			ImageView ivEdit;
			ImageView ivDelete;
			ImageView ivRemove;
			ImageView ivcaller;
			ImageView ivinfo;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			final Friend friend = getItem(position);
			ViewHolder holder = null;
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.friend_list, null);
				holder = new ViewHolder();

				holder.tvFriendName = (TextView) convertView
						.findViewById(R.id.tvfriendName);
				holder.tvFriendId = (TextView) convertView
						.findViewById(R.id.tvfriendid);
				holder.rlBody = (RelativeLayout) convertView
						.findViewById(R.id.rlbody);
				holder.tvAccept = (TextView) convertView
						.findViewById(R.id.tvaccept);
				holder.tvAdmin = (TextView) convertView
						.findViewById(R.id.tvadmin);
				holder.ivEdit = (ImageView) convertView
						.findViewById(R.id.ivedit);
				holder.ivDelete = (ImageView) convertView
						.findViewById(R.id.ivdelete);
				holder.ivcaller = (ImageView) convertView
						.findViewById(R.id.caller);
				holder.ivRemove = (ImageView) convertView
						.findViewById(R.id.remove_user);
				holder.ivinfo = (ImageView) convertView.findViewById(R.id.info);

				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.tvAccept.setId(position);
			holder.tvAdmin.setId(position);
			holder.ivEdit.setId(position);
			holder.ivDelete.setId(position);
			holder.ivRemove.setId(position);
			holder.ivcaller.setId(position);
			holder.tvAccept.setVisibility(View.GONE);
			holder.tvAdmin.setVisibility(View.GONE);
			holder.ivEdit.setVisibility(View.GONE);
			holder.ivDelete.setVisibility(View.GONE);
			holder.ivRemove.setVisibility(View.GONE);
			holder.ivinfo.setVisibility(View.GONE);
			holder.ivcaller.setVisibility(View.GONE);
			if (friend.getType().equals("request")) {
				holder.tvAccept.setText("ACCEPT");
				holder.tvAccept.setVisibility(View.VISIBLE);
				holder.tvFriendName.setText(friend.getFriendName());
				holder.tvFriendId.setText(friend.getFriendId());
			} else if (friend.getType().equals("friend")) {
				if (friend.getBlockState().equals("1")) {
					holder.ivDelete.setImageResource(R.drawable.unblock);
					holder.tvFriendName.setTextColor(getResources().getColor(
							R.color.red));
				} else {
					holder.ivDelete.setImageResource(R.drawable.block);
					holder.tvFriendName.setTextColor(getResources().getColor(
							R.color.gray));
				}
				holder.ivEdit.setVisibility(View.VISIBLE);
				holder.ivDelete.setVisibility(View.VISIBLE);
				holder.ivcaller.setVisibility(View.VISIBLE);
				holder.tvFriendName.setText(friend.getFriendName());
				holder.tvFriendId.setText(friend.getFriendId());
			} else if (friend.getType().equals("grp")) {
				holder.ivDelete.setImageResource(R.drawable.remove);
				holder.ivEdit.setVisibility(View.VISIBLE);
				holder.ivDelete.setVisibility(View.VISIBLE);
				holder.tvFriendName.setText(friend.getGroupName());
				holder.tvFriendId.setText("");
			} else if (friend.getType().equals("other_grp")) {
				holder.ivDelete.setImageResource(R.drawable.remove);
				holder.tvFriendName.setTextColor(getResources().getColor(
						R.color.gray));
				holder.ivDelete.setVisibility(View.VISIBLE);
				holder.ivinfo.setVisibility(View.VISIBLE);
				holder.tvFriendName.setText(friend.getGroupName());
				holder.tvFriendId.setText("");
			} else if (friend.getType().equals("contacts")) {
				holder.tvAccept.setVisibility(View.VISIBLE);
				holder.tvAccept.setText("INVITE");
				holder.tvFriendName.setTextColor(getResources().getColor(
						R.color.gray));
				holder.tvFriendName.setText(friend.getFirstName());
				holder.tvFriendId.setText(friend.getPhoneNumber());
			} else if (friend.getType().equals("grp_frd")) {
				if (position == 0)
					holder.tvAdmin.setVisibility(View.VISIBLE);
				holder.tvFriendName.setText(friend.getFriendName());
				holder.tvFriendId.setText(friend.getFriendId());
			} else if (friend.getType().equals("rm_frd")) {
				if (position == 0)
				holder.tvAdmin.setVisibility(View.VISIBLE);
				holder.ivRemove.setVisibility(View.VISIBLE);
				holder.tvFriendName.setText(friend.getFriendName());
				holder.tvFriendId.setText(friend.getFriendId());
			}

			holder.ivcaller.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					String phone = friend.getPhoneNumber().toString();
					if (phone.length() == 0) {
						Toast.makeText(getActivity(), "Phone Number Invalid",
								Toast.LENGTH_LONG).show();
					} else {
						Intent intent = new Intent(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" + phone));
						startActivity(intent);
					}
				}
			});

			holder.ivRemove.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					friends = friendList.get(v.getId());
					friends.setUserId(friend.getFriendId());
					friends.setGroupID(friend.getGroupID());
					friendList.remove(friends);
					notifyDataSetChanged();
					releaseNote.dismiss();
					new RemoveGrpMember().execute();
				}
			});

			holder.ivinfo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// if (friend.getType().equals("grp")) {
					// GroupID = friend.getGroupID();
					// GroupTitle = friend.getGroupName();
					// stat = "NORMAL";
					// count_type = "BODY";
					// } else
					if (friend.getType().equals("other_grp")) {
						GroupID = friend.getGroupID();
						GroupTitle = friend.getGroupName();
						stat = "NORMAL";
						count_type = "BODY";

						GroupFriendList.clear();
						new getGroupUserlistAsyncTask().execute();

					}
				}
			});

			holder.ivDelete.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					if (friend.getType().equals("grp")) {
						friends = friendList.get(v.getId());
						friends.setUserId(userCode);
						friendList.remove(friends);
						notifyDataSetChanged();
						new deleteGroup().execute();

					} else if (friend.getType().equals("other_grp")) {
						friends = friendList.get(v.getId());
						friends.setUserId(userCode);
						friends.setGroupID(friend.getGroupID());
						friendList.remove(friends);
						notifyDataSetChanged();
						new RemoveGrpMember().execute();
					} else if (friend.getBlockState().equals("1")) {
						friends = friendList.get(v.getId());
						friends.setUserId(userCode);

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								context);

						alertDialogBuilder
								.setMessage(
										"Are you sure you wanna Unblock this friend?")
								.setCancelable(false)
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												friends.setUserId(userCode);
												new deleteFriend().execute();
											}
										})
								.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					} else {
						friends = friendList.get(v.getId());
						friends.setUserId(userCode);

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								context);

						alertDialogBuilder
								.setMessage(
										"Are you sure you wanna block this friend?")
								.setCancelable(false)
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												friends.setUserId(userCode);
												new deleteFriend().execute();
											}
										})
								.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}
				}
			});

			holder.ivEdit.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					if (friend.getType().equals("grp")) {
						GroupID = friend.getGroupID();
						GroupTitle = friend.getGroupName();
						stat = "EDIT";
						count_type = "COUNT";
						GroupFriendList.clear();
						new getGroupUserlistAsyncTask().execute();

					} else {
						friends = friendList.get(v.getId());

						displayAddUser(friends);
					}
				}
			});

			holder.tvAccept.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {
					if (friend.getType().equals("contacts")) {
						Intent sendIntent;
						String Number = friend.getPhoneNumber();
						String messageToSend = "  My Voiccey ID is "
								+ userCode
								+ " and my Voicey name is "
								+ userName
								+ " Voicey. Say it. Send it. 6 Sec. Download here: \n http://voicey.me/web-services/appinstaller.php ";

						try {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
								String defaultSmsPackageName = Telephony.Sms
										.getDefaultSmsPackage(getActivity());

								sendIntent = new Intent(
										Intent.ACTION_SENDTO,
										Uri.parse("smsto:" + Uri.encode(Number)));
								sendIntent.putExtra("sms_body", messageToSend);

								if (defaultSmsPackageName != null) {
									sendIntent
											.setPackage(defaultSmsPackageName);
								}
							} else {
								sendIntent = new Intent(Intent.ACTION_VIEW);
								sendIntent.putExtra("address", Number);
								sendIntent.putExtra("sms_body", messageToSend);
								sendIntent.setType("vnd.android-dir/mms-sms");
							}
							startActivity(sendIntent);
						} catch (Exception e) {
							Toast.makeText(getActivity(),
									"Invite failed, please try again later!",
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					} else {
						friends = friendList.get(v.getId());
						friends.setUserId(userCode);
						new acceptRequest().execute();
					}

				}
			});
			if (position % 3 == 0)
				// holder.rlBody.setBackgroundColor(Color.parseColor("#22b14c"));

				holder.rlBody
						.setBackgroundResource(R.drawable.list_background1);
			else if (position % 3 == 1)
				holder.rlBody
						.setBackgroundResource(R.drawable.list_background3);
			else if (position % 3 == 2)
				holder.rlBody
						.setBackgroundResource(R.drawable.list_background4);

			// holder.tvFriendName.setText(friend.getFriendName());
			// holder.tvFriendId.setText(friend.getFriendId());

			Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
					"verdana.ttf");

			// holder.tvFriendName.setTypeface(face);
			holder.tvFriendId.setTypeface(face);

			return convertView;
		}

		void displayAddUser(final Friend friend) {

			final EditText etUserName, etPhoneNo, etEmail;
			TextView tvSave, tvuserId, tvClose;

			LayoutInflater li = LayoutInflater.from(getActivity());
			View promptsView = li.inflate(R.layout.profile_view, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setView(promptsView);

			final AlertDialog alertDialog = alertDialogBuilder.create();

			etUserName = (EditText) promptsView.findViewById(R.id.etname);
			etPhoneNo = (EditText) promptsView.findViewById(R.id.etphno);
			etEmail = (EditText) promptsView.findViewById(R.id.etmail);
			tvuserId = (TextView) promptsView.findViewById(R.id.tvuseridvaluel);
			tvSave = (TextView) promptsView.findViewById(R.id.tvsave);
			tvClose = (TextView) promptsView.findViewById(R.id.tvclose);
			tvuserId.setText(friend.getFriendId());
			etPhoneNo.setBackgroundColor(Color.WHITE);
			etEmail.setBackgroundColor(Color.WHITE);
			etPhoneNo.setText(friend.getPhoneNumber());
			etEmail.setText(friend.getEmailId());

			if (friends.getType().equals("grp")) {
				etUserName.setText(friend.getGroupName());
			} else if (friends.getType().equals("friend")) {

				etUserName.setText(friend.getFriendName());
			}

			tvClose.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					alertDialog.cancel();
				}
			});

			tvSave.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					try {
						String converttitle = URLEncoder.encode(
								((TextView) etUserName).getText().toString(),
								"UTF-8");
						String userName = converttitle;

						if (userName.length() == 0) {
							// if (friends.getType().equals("friend")) {
							//
							// }
							Toast.makeText(getActivity(),
									"Please enter Friend Name.",
									Toast.LENGTH_LONG).show();

						} else {
							alertDialog.cancel();
							// if (friends.getType().equals("grp")) {
							// friends.setGroupName(userName);
							// friends.setUserId(userCode);
							// friends.setGroupID(friend.getGroupID());
							// new editGroup().execute();
							// } else
							// if (friends.getType().equals("friend")) {
							friends.setFriendName(userName);
							friends.setUserId(userCode);
							friends.setFriendId(friend.getFriendId());
							new editFriend().execute();
							// }
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			// alertDialog.getWindow().setBackgroundDrawable(
			// new ColorDrawable(Color.argb(0, 0, 0, 0)));

			alertDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			alertDialog.show();

		}

		private class editFriend extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.editFriend(friends);
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

						Toast.makeText(getActivity(),
								"Friend Edited successfully.",
								Toast.LENGTH_LONG).show();
						populatelist.clear();
						new MainListLoader().execute();
						// new GetFriendList().execute();

					} else {
						Toast.makeText(getActivity(),
								"Error occure while sending request.",
								Toast.LENGTH_LONG).show();

					}

				} catch (Exception e) {
					Log.d("EditFrd", e.getLocalizedMessage());
				}
			}
		}

		private class RemoveGrpMember extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.RemoveGroupMember(friends);
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

						Toast.makeText(getActivity(),
								"Member Removed successfully.",
								Toast.LENGTH_LONG).show();
						GroupFriendList.clear();
						count_type = "COUNT";
						new getGroupUserlistAsyncTask().execute();

					} else {
						Toast.makeText(getActivity(),
								"Error occure while deletimg.",
								Toast.LENGTH_LONG).show();

					}

				} catch (Exception e) {
					Log.d("RmGrpMem", e.getLocalizedMessage());
				}
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

						Toast.makeText(getActivity(),
								"Group Deleted successfully.",
								Toast.LENGTH_LONG).show();
						populatelist.clear();
						new MainListLoader().execute();
						// new GetFriendList().execute();

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

		private class deleteFriend extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.deleteFriend(friends);
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
						Toast.makeText(getActivity(),
								"Friend Blocked successfully.",
								Toast.LENGTH_LONG).show();
						populatelist.clear();
						new MainListLoader().execute();
					} else if (status.equals("2")) {
						Toast.makeText(getActivity(),
								"Friend UnBlocked successfully.",
								Toast.LENGTH_LONG).show();
						populatelist.clear();
						new MainListLoader().execute();
					} else {
						Toast.makeText(getActivity(),
								"Error occure while deletimg.",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					Log.d("DelFrd", e.getLocalizedMessage());
				}
			}
		}

		private class acceptRequest extends AsyncTask<String, Void, String> {
			Dialog dialog;

			@Override
			protected String doInBackground(String... urls) {
				publishProgress((Void[]) null);
				return Webservices.acceptFriendRequest(friends);
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

					if (status.equals("2")) {
						Toast.makeText(getActivity(), "Friend Already Exist.",
								Toast.LENGTH_LONG).show();
						populatelist.clear();
						new MainListLoader().execute();
						// new GetFriendList().execute();
					}
					if (status.equals("1")) {

						Toast.makeText(getActivity(),
								"Friend Accepted successfully.",
								Toast.LENGTH_LONG).show();

						new getUserAsyncTask().execute();
						populatelist.clear();
						new MainListLoader().execute();
						// new GetFriendList().execute();

					}
					if (status.equals("0")) {
						Toast.makeText(getActivity(),
								"Error occure while sending request.",
								Toast.LENGTH_LONG).show();

					}

				} catch (Exception e) {
					Log.d("AcceptReq", e.getLocalizedMessage());
				}
			}
		}
	}

	public void onBackPressed() {

	}

	/**
	 * Checking device has camera hardware or not
	 * */
	private boolean isDeviceSupportCamera() {
		if (getActivity().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	/**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if the result is capturing Image
		if (requestCode == RESULT_LOAD_IMAGE
				&& resultCode == Activity.RESULT_OK && null != data) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
			grppic.setImageBitmap(bitmap);
			try {
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
				grp_pic_count++;
				File f = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "Voicey" + File.separator + "group_1.png");
				f.createNewFile();
				FileOutputStream fo = new FileOutputStream(f);
				fo.write(bytes.toByteArray());

				fo.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				createFolder();
				Bitmap bmp = (Bitmap) data.getExtras().get("data");
				grp_pic_count++;
				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(
							Environment.getExternalStorageDirectory()
									+ File.separator + "Voicey"
									+ File.separator + "group_1.png");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				BufferedOutputStream bos = new BufferedOutputStream(
						fileOutputStream);

				// choose another format if PNG doesn't suit you
				bmp.compress(CompressFormat.PNG, 100, bos);

				grppic.setImageBitmap(bmp);

				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(getActivity(), "User cancelled image capture",
						Toast.LENGTH_SHORT).show();
			} else {
				// failed to capture image
				Toast.makeText(getActivity(), "Sorry! Failed to capture image",
						Toast.LENGTH_SHORT).show();
			}
		}
		if (control_pic.equals("EDIT")) {
			GroupFriendList.clear();
			new getGroupUserlistAsyncTask().execute();
		} else {
			control_pic = "CREATE";
			creategroup();
		}
	}

	public void createFolder() {
		String RootDir = Environment.getExternalStorageDirectory()
				+ File.separator + "Voicey";
		File RootFile = new File(RootDir);
		RootFile.mkdir();
	}

}
