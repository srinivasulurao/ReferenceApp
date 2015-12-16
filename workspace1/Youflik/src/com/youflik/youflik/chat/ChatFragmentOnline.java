package com.youflik.youflik.chat;

import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.youflik.youflik.R;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.models.ConversationsModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.utils.Util;

public class ChatFragmentOnline extends Fragment{

	private ListView useronlineListView;
	private TextView noonlinefriends;
	private ArrayList<PresenceModel> presenceList = new ArrayList<PresenceModel>();
	private OnlinePresenceAdapter adapter;
	private Roster roster;
	Collection<RosterEntry> entries;
	private int returnConversationsID;
	DataBaseHandler db;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chat_online, container, false);
		useronlineListView = (ListView) rootView.findViewById(R.id.useronlineListView);
		noonlinefriends = (TextView) rootView.findViewById(R.id.noonlinefriends);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		roster = Util.connection.getRoster();
		entries = roster.getEntries();
		Presence presence;
		db = new DataBaseHandler(getActivity());

		String checkStick = db.checkStickersDate();
		if(checkStick.equalsIgnoreCase("0")){
			new LoadingStickersAsync().execute();
		}else if(checkStick.equalsIgnoreCase("1")){

		}else if(checkStick.equalsIgnoreCase("2")){
			new LoadingStickersAsync().execute();
		}

		for(RosterEntry entry : entries) {
			presence = roster.getPresence(entry.getUser());
			PresenceModel userPresence = new PresenceModel();
			userPresence.setUser(entry.getUser());
			//userPresence.setUserStatus(presence.getStatus());
			//System.out.println("VHATCHAT CHATFVHA"+userPresence.getUser() + " rajesh " + userPresence.getUserName() );

			ConversationsModel convModel = new ConversationsModel();
			convModel = db.checkConversationID(entry.getUser());
			if(convModel!=null)
			{
				userPresence.setUserImagePath(convModel.getWith_user_profilepicurl());
				userPresence.setUserName(convModel.getWith_user_display_name());
			}else{
				userPresence.setUserImagePath("");
				userPresence.setUserName("");
			}
			Presence presenceonline = roster.getPresence(entry.getUser());
			if (presenceonline.isAvailable()) {
				userPresence.setUserStatus("ONLINE");
				presenceList.add(userPresence);
			}else{
				userPresence.setUserStatus("OFFLINE");
			}
		}

		adapter = new OnlinePresenceAdapter(getActivity(), presenceList);

		if(adapter.getCount() > 0 ){
			useronlineListView.setAdapter(adapter);
			useronlineListView.setVisibility(View.VISIBLE);
			noonlinefriends.setVisibility(View.GONE);
		}else{
			useronlineListView.setVisibility(View.GONE);
			noonlinefriends.setVisibility(View.VISIBLE);
		}

		useronlineListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				Object object = useronlineListView.getItemAtPosition(position);
				PresenceModel presenceItem = (PresenceModel) object;

				DataBaseHandler datebasehandler = new DataBaseHandler(getActivity());
				/*String[] from_User_Full_Name = fromName.split("@");
				String from_User_Name = from_User_Full_Name[0];*/
				ConversationsModel checkConversations = new ConversationsModel();
				checkConversations = datebasehandler.checkConversationID(presenceItem.getUser());

				if(checkConversations == null){
					ConversationsModel conversions= new ConversationsModel();
					conversions.setLast_message("");
					conversions.setEnd_time("endtime");
					conversions.setLast_message_direction("in");
					conversions.setLogin_user_display_name(Util.USERNAME);
					conversions.setLogin_user_id(Integer.parseInt(Util.USER_ID));
					conversions.setLogin_user_jid(Util.CHAT_LOGIN_JID);
					conversions.setLogin_user_resource("mobile");
					conversions.setWith_user_display_name(presenceItem.getUser());
					conversions.setWith_user_id(Integer.parseInt("0"));
					conversions.setWith_user_jid(presenceItem.getUser());
					conversions.setWith_user_profilepicurl("message.getFrom()");
					conversions.setWith_user_resource("mobile");
					conversions.setStart_time("starttime");
					conversions.setMessage_iseen("yes");
					conversions.setMessage_isseen_count("0");
					datebasehandler.insertConversions(conversions);

					checkConversations = datebasehandler.checkConversationID(presenceItem.getUser());
					if(checkConversations == null){

					}else{
						returnConversationsID = checkConversations.getConversation_id();
					}
					//
					//getting the latest conversation id
				}else{
					returnConversationsID = checkConversations.getConversation_id();
				}
				String JID = presenceItem.getUser();
				int conId = returnConversationsID;
				Intent intent = new Intent(getActivity(), ChatMessagingActivity.class);
				intent.putExtra("Connversation_ID", conId);
				intent.putExtra("Connversation_JID", JID);
				startActivity(intent);
			}
		});

		/*roster.addRosterListener(new RosterListener() {
			public void entriesDeleted(Collection<String> addresses) {}
			public void entriesUpdated(Collection<String> addresses) {}
			public void entriesAdded(Collection<String> addresses) {}

			public void presenceChanged(Presence presence) {
				System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
				//UpdateRoasters();
				roster = null;
				presenceList.clear();
				entries.clear();

				roster = Util.connection.getRoster();
				entries = roster.getEntries();
				for(RosterEntry entry : entries) {
					presence = roster.getPresence(entry.getUser());
					PresenceModel userPresence = new PresenceModel();
					userPresence.setUser(entry.getUser());
					userPresence.setUserName(presence.getType().name());
					//userPresence.setUserStatus(presence.getStatus());

					Presence presenceonline = roster.getPresence(entry.getUser());
					if (presenceonline.isAvailable()) {
						userPresence.setUserStatus("ONLINE");
						presenceList.add(userPresence);
					}else{
						userPresence.setUserStatus("OFFLINE");
					}
				}
				adapter.notifyDataSetChanged();
			}

		});*/

		/*roster.addRosterListener(new RosterListener() {

			@Override
			public void presenceChanged(Presence presence) {
				// TODO Auto-generated method stub
				System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
				//UpdateRoasters();
				adapter = new OnlinePresenceAdapter(getActivity(), presenceList);
				useronlineListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void entriesUpdated(Collection<String> arg0) {
				// TODO Auto-generated method stub
				System.out.println("entriesUpdated ");
				//UpdateRoasters(getActivity());
			}

			@Override
			public void entriesDeleted(Collection<String> arg0) {
				// TODO Auto-generated method stub
				System.out.println("entriesDeleted ");
				//	UpdateRoasters(getActivity());
			}

			@Override
			public void entriesAdded(Collection<String> arg0) {
				// TODO Auto-generated method stub
				System.out.println("entriesAdded ");
				//UpdateRoasters(getActivity());
			}
		});*/

		/*Roster roster = con.getRoster();
		roster.addRosterListener(new RosterListener() {
		    // Ignored events public void entriesAdded(Collection<String> addresses) {}
		    public void entriesDeleted(Collection<String> addresses) {}
		    public void entriesUpdated(Collection<String> addresses) {}
		    public void presenceChanged(Presence presence) {
		        System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
		    }
		});*/

	}

	public void UpdateRoasters(){

		roster = null;
		presenceList.clear();
		entries.clear();

		roster = Util.connection.getRoster();
		entries = roster.getEntries();
		Presence presence;
		for(RosterEntry entry : entries) {
			presence = roster.getPresence(entry.getUser());
			PresenceModel userPresence = new PresenceModel();
			userPresence.setUser(entry.getUser());
			userPresence.setUserName(presence.getType().name());
			//userPresence.setUserStatus(presence.getStatus());

			Presence presenceonline = roster.getPresence(entry.getUser());
			if (presenceonline.isAvailable()) {
				userPresence.setUserStatus("ONLINE");
				presenceList.add(userPresence);
			}else{
				userPresence.setUserStatus("OFFLINE");
			}
		}
		/*adapter = new OnlinePresenceAdapter(context, presenceList);
		adapter.notifyDataSetChanged();*/
	}




	private class LoadingStickersAsync extends AsyncTask<Void, Void, ArrayList<StickersModel> >{

		@Override
		protected ArrayList<StickersModel>  doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<StickersModel> stickersList = new ArrayList<StickersModel>();
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API + "stickers");
			JSONArray stickersArray;
			try {
				if(jsonObjectRecived!=null){
					if(jsonObjectRecived.getString("error").equalsIgnoreCase("false")){
						stickersArray = jsonObjectRecived.getJSONArray("stickers");
						if(stickersArray.length() > 0){

							for(int i = 0; i < stickersArray.length() ;i++){
								StickersModel sticker = new StickersModel();
								sticker.setStickerUrl(stickersArray.getJSONObject(i).getString("url"));
								System.out.println(stickersArray.getJSONObject(i).getString("url"));
								stickersList.add(sticker);
							}
							return stickersList;
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<StickersModel>  result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			DataBaseHandler db = new DataBaseHandler(getActivity());
			db.AddStickers(result);

		}
	}

}
