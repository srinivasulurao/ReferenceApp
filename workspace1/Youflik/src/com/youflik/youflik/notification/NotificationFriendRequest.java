package com.youflik.youflik.notification;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.NotificationFriendRequestModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class NotificationFriendRequest extends Fragment {
	SwipeRefreshLayout swipeLayout;
	private ListView notificationsListView;
	JSONArray notificationsResponse;
	private TextView noNotifications;
	private int mLastFirstVisibleItem;
	private static int pageCount = 0;
	private String NotificationURL = Util.API + "friend_request";
	private ArrayList<NotificationFriendRequestModel> notificationSearch = new ArrayList<NotificationFriendRequestModel>();
	private ProgressDialog pDialog;
	private static String pagination_Date_String;
	private boolean flag_loading = false;
	private boolean flag_refresh = false;
	private static String refresh_Date_String;
	private NotificationFriendRequestAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_friend_request_notificatoin, container, false);
		swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container_fr);
		notificationsListView = (ListView) rootView.findViewById(R.id.FriendRequestListview);
		noNotifications = (TextView) rootView.findViewById(R.id.noFriendRequest);
		return rootView;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		adapter = new NotificationFriendRequestAdapter(getActivity(), notificationSearch);
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright,android.R.color.holo_green_light, android.R.color.holo_orange_light,android.R.color.holo_red_light);
		notificationSearch.clear();
		pageCount = 0;
		ConnectionDetector conn = new ConnectionDetector(getActivity());
		if(conn.isConnectingToInternet())
		{		
			new GetNotificationAsyncClass().execute();		
		}
		else{
			Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
		}

		notificationsListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				/*				int topRowVerticalPosition = 
						(notificationsListView == null || notificationsListView.getChildCount() == 0) ? 
								0 : notificationsListView.getChildAt(0).getTop();
				swipeLayout.setEnabled(topRowVerticalPosition >= 0);	*/
				boolean enable = false;
				if(notificationsListView != null && notificationsListView.getChildCount() > 0){
					boolean firstItemVisible = notificationsListView.getFirstVisiblePosition() == 0;
					boolean topOfFirstItemVisible = notificationsListView.getChildAt(0).getTop() == 0;
					enable = firstItemVisible && topOfFirstItemVisible;
				}
				swipeLayout.setEnabled(enable);	


				if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
				{
					if(flag_loading == false)
					{
						flag_loading = true;
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet()){
							new GetNotificationLoadMoreAsyncClass().execute();
						}else{
							Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
						}
					}
				}



			}
		});

		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				swipeLayout.setRefreshing(true);
				flag_refresh = false;
				ConnectionDetector conn = new ConnectionDetector(getActivity());
				if(conn.isConnectingToInternet()){
					new GetNotificationRefreshAsyncClass().execute();
				}else{
					Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
				}
			}
		});
	}

	private class GetNotificationAsyncClass extends AsyncTask<Void, Void, ArrayList<NotificationFriendRequestModel>>{

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
		protected ArrayList<NotificationFriendRequestModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray connectionsResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(NotificationURL);
			if(jsonObjectRecived != null){

				try{
					connectionsResponse = jsonObjectRecived.getJSONArray("friend_requests");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< connectionsResponse.length();i++){
					NotificationFriendRequestModel notificationData = new NotificationFriendRequestModel();
					JSONObject notificationDetails;
					try{
						notificationDetails = connectionsResponse.getJSONObject(i);
						notificationData.setFriend_request_id(notificationDetails.getString("friend_request_id"));
						notificationData.setUser_id(notificationDetails.getString("user_id"));
						notificationData.setFirstname(notificationDetails.getString("firstname"));
						notificationData.setLastname(notificationDetails.getString("lastname"));
						notificationData.setUser_profile_photo(notificationDetails.getString("user_profile_photo"));
						notificationData.setThumb1(notificationDetails.getString("thumb1"));
						notificationData.setCreated_date(notificationDetails.getString("created_date"));
						pagination_Date_String = notificationDetails.getString("created_date");

						if(!flag_refresh){
							flag_refresh = true;
							refresh_Date_String = notificationDetails.getString("created_date");
						}
						notificationSearch.add(notificationData);
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return notificationSearch;
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<NotificationFriendRequestModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(swipeLayout.isRefreshing())
			{
				swipeLayout.setRefreshing(false);
			}
			pDialog.dismiss();
			if(result == null){
				noNotifications.setVisibility(View.VISIBLE);
				swipeLayout.setVisibility(View.GONE);
				//Crouton.makeText(getActivity(), getString(R.string.crouton_message_friend_request), Style.ALERT).show();
			}else if(result.size()==0){
				noNotifications.setVisibility(View.VISIBLE);
				swipeLayout.setVisibility(View.GONE);
				//Crouton.makeText(getActivity(), getString(R.string.crouton_message_friend_request), Style.ALERT).show();

			}        else{
				//adapter = new NotificationFriendRequestAdapter(getActivity(), result);
				notificationsListView.setAdapter(adapter);
				if(result.size() < 20){
					flag_loading = true;
				}else{
					flag_loading = false;
				}

			}

		}

	}
	private class GetNotificationRefreshAsyncClass extends AsyncTask<Void, Void, ArrayList<NotificationFriendRequestModel>>{

		@Override
		protected ArrayList<NotificationFriendRequestModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray connectionsResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(NotificationURL+"?first_date="+ refresh_Date_String.replaceAll(" ", "%20"));
			if(jsonObjectRecived != null){

				try{
					connectionsResponse = jsonObjectRecived.getJSONArray("friend_requests");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< connectionsResponse.length();i++){
					NotificationFriendRequestModel notificationData = new NotificationFriendRequestModel();
					JSONObject notificationDetails;
					try{
						notificationDetails = connectionsResponse.getJSONObject(i);
						notificationData.setFriend_request_id(notificationDetails.getString("friend_request_id"));
						notificationData.setUser_id(notificationDetails.getString("user_id"));
						notificationData.setFirstname(notificationDetails.getString("firstname"));
						notificationData.setThumb1(notificationDetails.getString("thumb1"));
						notificationData.setLastname(notificationDetails.getString("lastname"));
						notificationData.setUser_profile_photo(notificationDetails.getString("user_profile_photo"));
						notificationData.setCreated_date(notificationDetails.getString("created_date"));

						if(!flag_refresh){
							refresh_Date_String = notificationDetails.getString("created_date");
						}
						notificationSearch.add(0,notificationData);
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				flag_refresh = true;
				return notificationSearch;
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<NotificationFriendRequestModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			swipeLayout.setRefreshing(false);
			if(result == null){
				noNotifications.setVisibility(View.VISIBLE);
			}else if(result.size()==0){
				noNotifications.setVisibility(View.VISIBLE);
			}        else{
				//	adapter = new NotificationFriendRequestAdapter(getActivity(), result);
				adapter.notifyDataSetChanged();

			}

		}
	}
	private class GetNotificationLoadMoreAsyncClass extends AsyncTask<Void, Void, ArrayList<NotificationFriendRequestModel>>{

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
		protected ArrayList<NotificationFriendRequestModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray connectionsResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(NotificationURL+"?last_date="+ pagination_Date_String.replaceAll(" ", "%20"));
			if(jsonObjectRecived != null){

				try{
					connectionsResponse = jsonObjectRecived.getJSONArray("friend_requests");
				}catch(JSONException e){
					e.printStackTrace();
				}
				if(connectionsResponse.length() > 0){
					for(int i = 0; i< connectionsResponse.length();i++){
						NotificationFriendRequestModel notificationData = new NotificationFriendRequestModel();
						JSONObject notificationDetails;
						try{
							notificationDetails = connectionsResponse.getJSONObject(i);
							notificationData.setFriend_request_id(notificationDetails.getString("friend_request_id"));
							notificationData.setUser_id(notificationDetails.getString("user_id"));
							notificationData.setFirstname(notificationDetails.getString("firstname"));
							notificationData.setLastname(notificationDetails.getString("lastname"));
							notificationData.setUser_profile_photo(notificationDetails.getString("user_profile_photo"));
							notificationData.setCreated_date(notificationDetails.getString("created_date"));
							pagination_Date_String = notificationDetails.getString("created_date");
							notificationSearch.add(notificationData);
						}catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					return notificationSearch;
				}else{
					return null;
				}
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<NotificationFriendRequestModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result == null){
				if(adapter.isEmpty()){
					noNotifications.setVisibility(View.VISIBLE);
					flag_loading = false;
				}
				flag_loading = true;
			}else if(result.size()==0){
				if(adapter.isEmpty()){
					noNotifications.setVisibility(View.VISIBLE);
				}
				flag_loading = true;
			}      else{
				//adapter = new NotificationFriendRequestAdapter(getActivity(), result);
				adapter.notifyDataSetChanged();
				flag_loading = false;
				pageCount = pageCount +20;
				notificationsListView.setSelection(pageCount);

			}

		}

	}
}
