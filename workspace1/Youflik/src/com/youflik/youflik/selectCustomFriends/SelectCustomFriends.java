package com.youflik.youflik.selectCustomFriends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.youflik.youflik.R;
import com.youflik.youflik.models.FriendsModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectCustomFriends extends ActionBarActivity  {
	public Menu menuInstance;
	private ListView listconn;
	private ArrayList<FriendsModel> connectionssearch = new ArrayList<FriendsModel>();
	private EditText conn_search;
	StringBuffer response_uname,response_uID;
	String custom_users_name=" ";
	String custom_users_id=" ";
	SelectCustomFriendsAdapter conn_adapter;
	private ProgressDialog pDialog;
	AlertDialogManager alert = new AlertDialogManager();
	ArrayList <String> checkedUserName=new ArrayList<String>();
	ArrayList <String> checkedUserID=new ArrayList<String>();
	String received_userID;
	String trimeduid="";
	public static ArrayList<String> myList ;
	TextView msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_select_csv);
		// get the intent
		Bundle bundle = getIntent().getExtras();
		received_userID = bundle.getString("usercheckedid");
		if(received_userID.equalsIgnoreCase(" "))
		{
			myList = new ArrayList<String>(Arrays.asList(received_userID.split(",")));
		}
		else
		{
			trimeduid=removeLastChar(received_userID);
			myList = new ArrayList<String>(Arrays.asList(trimeduid.split(",")));
		}

		//end
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

	}



	private static String removeLastChar(String str) {
		return str.substring(1,str.length()-1);
	}
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		menuInstance = menu;
		getMenuInflater().inflate(R.menu.menu_select_csv, menu);
		return super.onCreateOptionsMenu(menu);

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.members_add: 
			listconn.setEnabled(false);
			if(conn_adapter!=null){
				int size = conn_adapter.getCount();

				if(size>0)
				{
					for(int i=0;i<size;i++){
						FriendsModel rowItem = (FriendsModel) conn_adapter.getItem(i);
						if(rowItem.isSelected()){

							checkedUserName.add(rowItem.getFirstname().trim());
							checkedUserID.add(rowItem.getUser_two().trim());
						}
					}
					custom_users_name=checkedUserName.toString();
					custom_users_id=checkedUserID.toString();

					Intent intent = new Intent();
					intent.putExtra("CustomUserName", custom_users_name);
					intent.putExtra("CustomUserID", custom_users_id);
					setResult(RESULT_OK,intent);
					listconn.setEnabled(true);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "No Connections Found", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(getApplicationContext(), "No Connections Found", Toast.LENGTH_SHORT).show();
			}
			return true;


		case R.id.members_exit: 
			finish();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	private class ConnectionsAsyncClass extends AsyncTask<Void, Void, ArrayList<FriendsModel>>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			pDialog = new ProgressDialog(SelectCustomFriends.this);
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
			}else{
				conn_adapter = new SelectCustomFriendsAdapter(SelectCustomFriends.this, result);
				listconn.setAdapter(conn_adapter);
			}

		}

	}

}
