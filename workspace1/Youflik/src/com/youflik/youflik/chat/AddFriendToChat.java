package com.youflik.youflik.chat;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.youflik.youflik.R;
import com.youflik.youflik.database.DataBaseHandler;
import com.youflik.youflik.models.ConversationsModel;
import com.youflik.youflik.models.FriendsModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

public class AddFriendToChat extends ActionBarActivity  {
	private ListView listconn;
	private ArrayList<FriendsModel> connectionssearch = new ArrayList<FriendsModel>();
	private EditText conn_search;
	StringBuffer response_uname,response_uID;
	String custom_users_name=" ";
	String custom_users_id=" ";
	AddFriendChatAdapter conn_adapter;
	private ProgressDialog pDialog;
	AlertDialogManager alert = new AlertDialogManager();
	ArrayList <String> checkedUserName=new ArrayList<String>();
	ArrayList <String> checkedUserID=new ArrayList<String>();
	String trimeduid="";
	public static ArrayList<String> myList ;
	TextView msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_select_csv);
		// get the intent
		listconn = (ListView)findViewById(R.id.list_connections);
		conn_search=(EditText)findViewById(R.id.connection_search);
		msg=(TextView)findViewById(R.id.noconn);
		connectionssearch.clear();
		new ConnectionsAsyncClass().execute();

		// Capture Text in EditText
		conn_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {  
				// TODO Auto-generated method stub
				if(conn_adapter != null){
					msg.setVisibility(View.GONE);
					String text = conn_search.getText().toString().toLowerCase(Locale.getDefault());
					conn_adapter.filter(text);
				}else{
					Toast.makeText(getApplicationContext(), "Connections Not Found", Toast.LENGTH_SHORT).show();
					msg.setVisibility(View.VISIBLE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
		});

		listconn.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				int returnConversationsID = 0;
				Object object=  listconn.getItemAtPosition(position);
				FriendsModel friend  = (FriendsModel) object;

				DataBaseHandler datebasehandler = new DataBaseHandler(AddFriendToChat.this);
				/*String[] from_User_Full_Name = fromName.split("@");
				String from_User_Name = from_User_Full_Name[0];*/
				ConversationsModel checkConversations = new ConversationsModel();
				checkConversations = datebasehandler.checkConversationID(friend.getJid());

				if(checkConversations == null){
					ConversationsModel conversions= new ConversationsModel();
					conversions.setLast_message("");
					conversions.setEnd_time("endtime");
					conversions.setLast_message_direction("in");
					conversions.setLogin_user_display_name(Util.USERNAME);
					conversions.setLogin_user_id(Integer.parseInt(Util.USER_ID));
					conversions.setLogin_user_jid(Util.CHAT_LOGIN_JID);
					conversions.setLogin_user_resource("mobile");
					conversions.setWith_user_display_name(friend.getFirstname());
					conversions.setWith_user_id(Integer.parseInt(friend.getUser_two()));
					conversions.setWith_user_jid(friend.getJid());
					conversions.setWith_user_profilepicurl(friend.getUser_profile_photo());
					conversions.setWith_user_resource("mobile");
					conversions.setStart_time("starttime");
					conversions.setMessage_iseen("yes");
					conversions.setMessage_isseen_count("0");
					datebasehandler.insertConversions(conversions);

					checkConversations = datebasehandler.checkConversationID(friend.getJid());
					if(checkConversations == null){

					}else{
						returnConversationsID = checkConversations.getConversation_id();
					}
					//
					//getting the latest conversation id
				}else{
					returnConversationsID = checkConversations.getConversation_id();
				}
				Intent intent = new Intent(AddFriendToChat.this, ChatMessagingActivity.class);
				intent.putExtra("Connversation_ID", returnConversationsID);
				intent.putExtra("Connversation_JID", friend.getJid());
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}

	private class ConnectionsAsyncClass extends AsyncTask<Void, Void, ArrayList<FriendsModel>>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			pDialog = new ProgressDialog(AddFriendToChat.this);
			pDialog.setMessage("Loading ...");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected ArrayList<FriendsModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray connectionsResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"get_all_friends");
			if(jsonObjectRecived != null){

				try{
					connectionsResponse = jsonObjectRecived.getJSONArray("connections");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< connectionsResponse.length();i++){
					FriendsModel connectionData = new FriendsModel();
					JSONObject connectionsDetails;
					try{
						connectionsDetails = connectionsResponse.getJSONObject(i);
						connectionData.setUser_two(connectionsDetails.getString("user_two"));
						connectionData.setFirstname(connectionsDetails.getString("firstname"));
						connectionData.setLastname(connectionsDetails.getString("lastname"));
						connectionData.setThumb3(connectionsDetails.getString("thumb3"));
						connectionData.setJid(connectionsDetails.getString("jid"));
						connectionssearch.add(connectionData);
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return connectionssearch;
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<FriendsModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result == null){
			}else if(result.size()==0){
				System.out.println("No Connections For You");
			}else{
				conn_adapter = new AddFriendChatAdapter(AddFriendToChat.this, result);
				System.out.println(conn_adapter);
				listconn.setAdapter(conn_adapter);
			}

		}

	}

}
