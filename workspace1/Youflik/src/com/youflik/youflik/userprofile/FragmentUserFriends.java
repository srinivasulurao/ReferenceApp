package com.youflik.youflik.userprofile;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.youflik.youflik.R;
import com.youflik.youflik.commonAdapters.ConnectionAdapter;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.FriendsModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.thirdPartyProfileView.ThirdPartyUserDetailActivity;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FragmentUserFriends extends Fragment {

	private GridView FriendsGridView;
	private ArrayList<FriendsModel> connectionssearch = new ArrayList<FriendsModel>();
	private static int pageCount = 0;
	private static String pagination_Date_String = "";
	private boolean flag_loading = false;
	private ProgressDialog pDialog;
	private ConnectionAdapter adapter;
	private TextView NoConnections; 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_user_friends, container, false);
		FriendsGridView = (GridView) rootView.findViewById(R.id.friendsgridview);
		NoConnections = (TextView) rootView.findViewById(R.id.NoFriends);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		connectionssearch.clear();
		pageCount = 0;
		adapter = new ConnectionAdapter(getActivity(), connectionssearch);
		ConnectionDetector conn = new ConnectionDetector(getActivity());
		if(conn.isConnectingToInternet()){
			new ConnectionsAsyncClass().execute();

		}else{
			Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
		}


		FriendsGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Object object = FriendsGridView.getItemAtPosition(position);
				FriendsModel connection_data = (FriendsModel)  object;
				if(connection_data.getUser_two().equalsIgnoreCase(Util.USER_ID))
				{
					if(Util.mediaPlayer.isPlaying()){
						Util.mediaPlayer.reset();
					}
					else {
						Util.mediaPlayer.reset();
					}
					Intent intent = new Intent(getActivity(),UserDetailActivity.class);
					/*	intent.putExtra("UserID", connection_data.getUser_two());
					Util.THIRD_PARTY_USER_NAME = connection_data.getFirstname();
					Util.THIRD_PARTY_USER_ID=connection_data.getUser_two();*/
					startActivity(intent);
				}
				else
				{
					if(Util.mediaPlayer.isPlaying()){
						Util.mediaPlayer.reset();
					}
					else {
						Util.mediaPlayer.reset();
					}
					Intent intent = new Intent(getActivity(),ThirdPartyUserDetailActivity.class);
					intent.putExtra("UserID", connection_data.getUser_two());
					Util.THIRD_PARTY_USER_NAME = connection_data.getFirstname();
					Util.THIRD_PARTY_USER_ID=connection_data.getUser_two();
					startActivity(intent);
				}
			}

		});

		FriendsGridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {	

				if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
				{
					if(flag_loading == false)
					{
						flag_loading = true;
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet()){
							new ConnectionsLoadMoreAsyncClass().execute();
						}else{
							Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
						}
					}
				}

			}
		});


	}

	private class ConnectionsAsyncClass extends AsyncTask<Void, Void, ArrayList<FriendsModel>>{

		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(getActivity());
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected ArrayList<FriendsModel> doInBackground(Void... params) {
			JSONArray connectionsResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"connection?user_id="+Util.USER_ID);
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
						connectionData.setConnection_id(connectionsDetails.getString("connection_id"));
						connectionData.setUser_two(connectionsDetails.getString("user_two"));
						connectionData.setThumb3(connectionsDetails.getString("thumb3"));
						connectionData.setCreated_date(connectionsDetails.getString("created_date"));
						connectionData.setFirstname(connectionsDetails.getString("firstname"));
						connectionData.setLastname(connectionsDetails.getString("lastname")); 
						pagination_Date_String = connectionsDetails.getString("created_date");
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
				NoConnections.setVisibility(View.VISIBLE);
				NoConnections.setText("You haven't connected to anyone yet!");
			}else if(result.size()==0){
				NoConnections.setVisibility(View.VISIBLE);
				NoConnections.setText("You haven't connected to anyone yet!");
			}  else{
				//adapter = new ConnectionAdapter(getActivity(), result);
				FriendsGridView.setAdapter(adapter);
				if(result.size() < 40){
					flag_loading = true;
				}else{
					flag_loading = false;
				}
			}
		}
	}


	private class ConnectionsLoadMoreAsyncClass extends AsyncTask<Void, Void, ArrayList<FriendsModel>>{

		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(getActivity());
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected ArrayList<FriendsModel> doInBackground(Void... params) {
			JSONArray connectionsResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"connection?user_id="+Util.USER_ID +"&last_date=" + pagination_Date_String.replaceAll(" ", "%20"));
			if(jsonObjectRecived != null){

				try{
					connectionsResponse = jsonObjectRecived.getJSONArray("connections");
				}catch(JSONException e){
					e.printStackTrace();
				}
				if(connectionsResponse.length() > 0){
					for(int i = 0; i< connectionsResponse.length();i++){
						FriendsModel connectionData = new FriendsModel();
						JSONObject connectionsDetails;
						try{
							connectionsDetails = connectionsResponse.getJSONObject(i);
							connectionData.setConnection_id(connectionsDetails.getString("connection_id"));
							connectionData.setUser_two(connectionsDetails.getString("user_two"));
							connectionData.setThumb3(connectionsDetails.getString("thumb3"));
							connectionData.setFirstname(connectionsDetails.getString("firstname"));
							connectionData.setLastname(connectionsDetails.getString("lastname")); 
							connectionData.setCreated_date(connectionsDetails.getString("created_date"));
							pagination_Date_String = connectionsDetails.getString("created_date");
							connectionssearch.add(connectionData);
						}catch (JSONException e) {
							e.printStackTrace();
						}
					}
					return connectionssearch;
				}else{
					return null;
				}
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<FriendsModel> result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result == null){
				if(adapter.isEmpty()){
					NoConnections.setVisibility(View.VISIBLE);
					flag_loading = false;
				}
				flag_loading = true;
				Toast.makeText(getActivity(), "No more connections", Toast.LENGTH_SHORT).show();
			} else if(result.size()==0){
				if(adapter.isEmpty()){
					NoConnections.setVisibility(View.VISIBLE);
				}
				flag_loading = true;
				Toast.makeText(getActivity(), "No more connections", Toast.LENGTH_SHORT).show();
			}      else{
				adapter = new ConnectionAdapter(getActivity(), result);
				adapter.notifyDataSetChanged();
				flag_loading = false;
				pageCount = pageCount +40;
				FriendsGridView.setSelection(pageCount);
			}
		}
	}
}