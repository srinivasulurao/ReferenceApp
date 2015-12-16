package com.voicey.fragment;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.voicey.activity.R;
import com.voicey.adapter.ChatFriendListAdapter;
import com.voicey.model.AudioInfo;
import com.voicey.model.FriendMessages;
import com.voicey.webservices.Webservices;

public class ChatFriendFragment extends Fragment implements OnItemClickListener {
	
	ListView lvChatFriendList;
	Webservices Webservices = new Webservices();
	SharedPreferences sharedPreferences;
	String userCode, userId;
	ChatFriendListAdapter chatFriendListAdapter;
	List<FriendMessages> friendMessageList = new ArrayList<FriendMessages>();
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_chat_friend, container, false);

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
			
			lvChatFriendList = (ListView) v.findViewById(R.id.lvfriendsviewList);
			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			userCode = sharedPreferences.getString("userCode", null);
			userId = sharedPreferences.getString("userId", null);
			
			new GetFriendMessageList().execute();
		
			
		} catch (Exception e) {

			e.printStackTrace();
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
				
				if (result != null && result.length() > 0) {
					JSONArray arr1 = new JSONArray(result);
					friendMessageList.clear();
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
							

							
							friendMessageList.add(friendMessages);

							}
						}
						
						
						chatFriendListAdapter = new ChatFriendListAdapter(
								getActivity(),
								R.layout.chat_friend_detail,
								friendMessageList);
						lvChatFriendList
								.setAdapter(chatFriendListAdapter);
						lvChatFriendList.setOnItemClickListener(ChatFriendFragment.this);
						
							
						
					}
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
		FriendMessages friendMessages=friendMessageList.get(position);
        
		Fragment fragment = new ChatMessageFragment();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
    	Bundle bundle = new Bundle();
    	bundle.putString("friendName", friendMessages.getFriendName());
		fragment.setArguments(bundle);
        
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();	
      
       
       
   }

}
