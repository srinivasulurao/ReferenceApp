package com.youflik.youflik.fragments;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import com.youflik.youflik.R;
import com.youflik.youflik.commonAdapters.SearchAdapter;
import com.youflik.youflik.commonAdapters.SearchAdapterNoFilter;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.SearchModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.settings.SettingsFragment;
import com.youflik.youflik.thirdPartyProfileView.ThirdPartyUserDetailActivity;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SearchFragment extends Fragment {
	public Menu menuInstance;
	private ListView listconn;
	private ArrayList<SearchModel> connectionssearch = new ArrayList<SearchModel>();
	private EditText conn_search;
	private ImageView search_close;
	StringBuffer response_uname,response_uID;
	String custom_users_name=" ";
	String custom_users_id=" ";
	SearchAdapterNoFilter conn_adapter;
	private ProgressDialog pDialog;
	AlertDialogManager alert = new AlertDialogManager();
	private String MembersURL = Util.API+ "user?search_text=";
	private static int pageCount = 0;
	private static String pagination_Date_String = "";
	private boolean flag_loading = false;
	public Handler searchResults = new Handler();
	SwipeRefreshLayout swipeLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_search, container, false);
		conn_search=(EditText)rootView.findViewById(R.id.searchname);
		search_close=(ImageView)rootView.findViewById(R.id.close_search);
		listconn = (ListView)rootView.findViewById(R.id.list_friends);
		swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container_search);

		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		conn_adapter = new SearchAdapterNoFilter(getActivity(), connectionssearch);
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright,android.R.color.holo_green_light, android.R.color.holo_orange_light,android.R.color.holo_red_light);
		swipeLayout.setEnabled(false);

		// search when click on search in soft keypad
		conn_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {

					if(conn_adapter != null){
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet()){

							if(conn_search.getText().toString().trim().length()>0){
								//connectionssearch.clear();
								pageCount = 0;
								new MemberSearchAsyncClass().execute();
							}
							else
							{
								Toast.makeText(getActivity(), "Add text to search contacts", Toast.LENGTH_SHORT).show();
							}
						}else{
							Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
						}
					}else{
						Toast.makeText(getActivity(), "Connections Not Found", Toast.LENGTH_SHORT).show();
					}


					return true;
				}
				return false;
			}
		});
		// end search

		/*		// Capture Text in EditText
		conn_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {  
				if(conn_adapter != null){
					ConnectionDetector conn = new ConnectionDetector(getActivity());
					if(conn.isConnectingToInternet()){

						if(conn_search.getText().toString().trim().length()>0){
							//connectionssearch.clear();
							pageCount = 0;

							if(conn_search.getText().toString().trim().length()<10 && conn_search.getText().toString().trim().length()>3)
							{
								new MemberSearchAsyncClass().execute();
							}

						}
						else
						{
							Toast.makeText(getActivity(), "Add text to search contacts", Toast.LENGTH_SHORT).show();
						}
					}else{
						Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
					}
				}else{
					Toast.makeText(getActivity(), "Connections Not Found", Toast.LENGTH_SHORT).show();
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

			}
		});*/

		listconn.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
				{
					if(flag_loading == false)
					{
						flag_loading = true;
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet()){
							new MembersLoadMoreAsyncClass().execute();
						}else{
							Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
						}
					}
				}
			}
		});

		search_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				conn_search.setText("");
				conn_search.setHint("search users");
				//connectionssearch.clear();
			}
		});

		listconn.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
				Object object = listconn.getItemAtPosition(position);
				SearchModel connection_data = (SearchModel)  object;
				Intent intent = new Intent(getActivity(),ThirdPartyUserDetailActivity.class);
				intent.putExtra("UserID", connection_data.getUser_id());
				Util.THIRD_PARTY_USER_NAME = connection_data.getFirstname();
				Util.THIRD_PARTY_USER_ID=connection_data.getUser_id();
				startActivity(intent);
			}
		});

	}

	private class MembersLoadMoreAsyncClass extends AsyncTask<Void, Void, ArrayList<SearchModel>>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(getActivity());
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}
		@Override
		protected ArrayList<SearchModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray membersResponse = null;
			JSONObject jsonObjectRecived=null;
			//	jsonObjectRecived = HttpGetClient.sendHttpPost(MembersURL+conn_search.getText().toString().toLowerCase(Locale.getDefault()) + "&last_date=" + pagination_Date_String.replaceAll(" ", "%20"));
			jsonObjectRecived = HttpGetClient.sendHttpPost(MembersURL+conn_search.getText().toString().trim().toLowerCase(Locale.getDefault()).replaceAll(" ", "%20") + "&last_date=" + pagination_Date_String.replaceAll(" ", "%20"));

			if(jsonObjectRecived != null){

				try{
					membersResponse = jsonObjectRecived.getJSONArray("users");
				}catch(JSONException e){
					e.printStackTrace();
				}
				if(membersResponse.length() > 0){
					for(int i = 0; i< membersResponse.length();i++){
						SearchModel membersData = new SearchModel();
						JSONObject membersDetails;
						try{
							membersDetails = membersResponse.getJSONObject(i);
							membersData.setUser_id(membersDetails.getString("user_id"));
							membersData.setFirstname(membersDetails.getString("firstname"));
							membersData.setLastname(membersDetails.getString("lastname"));
							membersData.setUser_profile_photo_path(membersDetails.getString("user_profile_photo_path"));
							membersData.setIs_pending_friend_request(membersDetails.getString("is_pending_friend_request"));
							membersData.setIs_accept_friend_request(membersDetails.getString("is_accept_friend_request"));
							membersData.setIs_friend(membersDetails.getString("is_friend"));
							membersData.setJoined_date(membersDetails.getString("joined_date"));
							pagination_Date_String = membersDetails.getString("joined_date");
							connectionssearch.add(membersData);
						}catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else{
					return null;
				}
				return connectionssearch;
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<SearchModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result == null){
				if(conn_adapter.isEmpty()){
					flag_loading = false;
				}
				flag_loading = true;
				Toast.makeText(getActivity(), "No More Members to Load", Toast.LENGTH_SHORT).show();
			}else if(result.size()==0){
				if(conn_adapter.isEmpty()){
				}
				flag_loading = true;
				Toast.makeText(getActivity(), "No More Members to Load", Toast.LENGTH_SHORT).show();
			}    else{
				//conn_adapter = new SearchAdapter(getActivity(), result);
				conn_adapter.notifyDataSetChanged();
				flag_loading = false;
				pageCount = pageCount +40;
				listconn.setSelection(pageCount);
			}

		}


	}

	private class MemberSearchAsyncClass extends AsyncTask<Void, Void, ArrayList<SearchModel>>{

		@Override
		protected void onPreExecute() {/*
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(getActivity());
				pDialog.setCancelable(true);
				pDialog.show();}
			else{
				pDialog.setCancelable(true);
				pDialog.show();
			}*/
			swipeLayout.setRefreshing(true);	
		}


		@Override
		protected ArrayList<SearchModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray membersResponse = null;
			connectionssearch.clear();
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(MembersURL+conn_search.getText().toString().trim().toLowerCase(Locale.getDefault()).replaceAll(" ", "%20") );
			if(jsonObjectRecived != null){
				try{
					membersResponse = jsonObjectRecived.getJSONArray("users");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< membersResponse.length();i++){
					SearchModel membersData = new SearchModel();
					JSONObject membersDetails;
					try{
						membersDetails = membersResponse.getJSONObject(i);
						membersData.setUser_id(membersDetails.getString("user_id"));
						membersData.setFirstname(membersDetails.getString("firstname"));
						membersData.setLastname(membersDetails.getString("lastname"));
						membersData.setUser_profile_photo_path(membersDetails.getString("user_profile_photo_path"));
						membersData.setIs_pending_friend_request(membersDetails.getString("is_pending_friend_request"));
						membersData.setPending_friend_request_id(membersDetails.getString("pending_friend_request_id"));
						membersData.setIs_accept_friend_request(membersDetails.getString("is_accept_friend_request"));
						membersData.setAccept_friend_request_id(membersDetails.getString("accept_friend_request_id"));
						membersData.setIs_friend(membersDetails.getString("is_friend"));
						membersData.setJoined_date(membersDetails.getString("joined_date"));
						pagination_Date_String = membersDetails.getString("joined_date");
						connectionssearch.add(membersData);
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
		protected void onPostExecute(ArrayList<SearchModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if(result.size()>0)
			{
				listconn.setAdapter(conn_adapter);
				swipeLayout.setRefreshing(false);	

				if(result.size() < 40){
					flag_loading = true;
				}else{
					flag_loading = false;
				}

			}
			else
			{
				try{
					conn_adapter.notifyDataSetChanged();
					swipeLayout.setRefreshing(false);	
					Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();

				}
				catch(Exception e)
				{

				}

			}
		}
	}
}
