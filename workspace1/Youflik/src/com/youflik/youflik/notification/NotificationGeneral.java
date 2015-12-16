package com.youflik.youflik.notification;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.NotificationGeneralModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class NotificationGeneral extends Fragment {
	SwipeRefreshLayout swipeLayout;
	private ListView notificationsListView;
	JSONArray notificationsResponse;
	private TextView noNotifications;
	
	private static int pageCount = 0;
	private String NotificationURL = Util.API + "notification";
	private ArrayList<NotificationGeneralModel> notificationSearch = new ArrayList<NotificationGeneralModel>();
	private ProgressDialog pDialog;
	private static String pagination_Date_String;
	private boolean flag_loading = false;
	private boolean flag_refresh = false;
	private static String refresh_Date_String;
	private NotificationGeneralAdapter adapter;
	private int mLastFirstVisibleItem,mDeleteItem;
	private AlertDialog.Builder mBuilder;
	private static final String DELETE_NOTIFICATION_ITEM_API=Util.API+"delete_notification";
	private AlertDialogManager mAlert = new AlertDialogManager();


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_general_notification, container, false);
		swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
		notificationsListView = (ListView) rootView.findViewById(R.id.Notificationslistview);
		noNotifications = (TextView) rootView.findViewById(R.id.noNotifications);
		return rootView;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		adapter = new NotificationGeneralAdapter(getActivity(), notificationSearch);
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright,android.R.color.holo_green_light, android.R.color.holo_orange_light,android.R.color.holo_red_light);
		notificationSearch.clear();
		pageCount = 0;
		ConnectionDetector conn = new ConnectionDetector(getActivity());
		if(conn.isConnectingToInternet())
		{		new GetNotificationAsyncClass().execute();
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
notificationsListView.setOnItemLongClickListener(new OnItemLongClickListener(){
        	
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
				mDeleteItem = position;
				mBuilder = new AlertDialog.Builder(getActivity());
				mBuilder.setMessage("Are you sure to remove the notification from list?");
				mBuilder.setPositiveButton("YES",new OnClickListener(){
					@Override
				  	public void onClick(DialogInterface dialog, int which) {
						NotificationGeneralModel model = (NotificationGeneralModel) notificationSearch.get(position);
						String notification_id = model.getNotification_id();
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet()){
						new DeleteNotificationListItem().execute(notification_id);
						} else {
							mAlert.showAlertDialog(getActivity(),"Connection Error","Check Your Internet Connection",false);
						}
					    dialog.dismiss();
					}
				});
				mBuilder.setNegativeButton("NO", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog dialog = mBuilder.create();
				dialog.show();
				
				return true;
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

	private class GetNotificationAsyncClass extends AsyncTask<Void, Void, ArrayList<NotificationGeneralModel>>{

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
		protected ArrayList<NotificationGeneralModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray connectionsResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(NotificationURL);
			if(jsonObjectRecived != null){

				try{
					connectionsResponse = jsonObjectRecived.getJSONArray("notifications");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< connectionsResponse.length();i++){
					NotificationGeneralModel notificationData = new NotificationGeneralModel();
					JSONObject notificationDetails;
					try{
						notificationDetails = connectionsResponse.getJSONObject(i);
						notificationData.setNotification_id(notificationDetails.getString("notification_id"));
						notificationData.setNotification_type(notificationDetails.getString("notification_type"));
						notificationData.setTable_name(notificationDetails.getString("table_name"));
						notificationData.setPk_value(notificationDetails.getString("pk_value"));
						notificationData.setTrack_id(notificationDetails.getString("track_id"));
						notificationData.setNotification_send_user_id(notificationDetails.getString("notification_send_user_id"));
						notificationData.setCreated_date(notificationDetails.getString("created_date"));
						pagination_Date_String = notificationDetails.getString("notification_id");
						notificationData.setNotification_send_user_firstname(notificationDetails.getString("notification_send_user_firstname"));
						notificationData.setNotification_send_user_lastname(notificationDetails.getString("notification_send_user_lastname"));
						notificationData.setNotification_message(notificationDetails.getString("notification_message"));
						notificationData.setNotification_send_user_profile_photo_thumb1(notificationDetails.getString("notification_send_user_profile_photo_thumb1"));

						if(!flag_refresh){
							flag_refresh = true;
							refresh_Date_String = notificationDetails.getString("notification_id");
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
		protected void onPostExecute(ArrayList<NotificationGeneralModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result == null){
				noNotifications.setVisibility(View.VISIBLE);
				swipeLayout.setVisibility(View.GONE);
				//Crouton.makeText(getActivity(), getString(R.string.crouton_message_notification), Style.ALERT).show();

			}else if(result.size()==0){
				noNotifications.setVisibility(View.VISIBLE);
				swipeLayout.setVisibility(View.GONE);
				Crouton.makeText(getActivity(), getString(R.string.crouton_message_notification), Style.ALERT).show();

			}        else{
				//	adapter = new NotificationGeneralAdapter(getActivity(), result);
				notificationsListView.setAdapter(adapter);
				if(result.size() < 20){
					flag_loading = true;
				}else{
					flag_loading = false;
				}

			}

		}

	}
	private class GetNotificationRefreshAsyncClass extends AsyncTask<Void, Void, ArrayList<NotificationGeneralModel>>{

		@Override
		protected ArrayList<NotificationGeneralModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray connectionsResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(NotificationURL+"?first_date="+ refresh_Date_String);
			if(jsonObjectRecived != null){

				try{
					connectionsResponse = jsonObjectRecived.getJSONArray("notifications");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< connectionsResponse.length();i++){
					NotificationGeneralModel notificationData = new NotificationGeneralModel();
					JSONObject notificationDetails;
					try{
						notificationDetails = connectionsResponse.getJSONObject(i);
						notificationData.setNotification_id(notificationDetails.getString("notification_id"));
						notificationData.setNotification_type(notificationDetails.getString("notification_type"));
						notificationData.setTable_name(notificationDetails.getString("table_name"));
						notificationData.setPk_value(notificationDetails.getString("pk_value"));
						notificationData.setTrack_id(notificationDetails.getString("track_id"));
						notificationData.setNotification_send_user_id(notificationDetails.getString("notification_send_user_id"));
						notificationData.setCreated_date(notificationDetails.getString("created_date"));
						pagination_Date_String = notificationDetails.getString("notification_id");
						notificationData.setNotification_send_user_firstname(notificationDetails.getString("notification_send_user_firstname"));
						notificationData.setNotification_send_user_lastname(notificationDetails.getString("notification_send_user_lastname"));
						notificationData.setNotification_message(notificationDetails.getString("notification_message"));
						notificationData.setNotification_send_user_profile_photo_thumb1(notificationDetails.getString("notification_send_user_profile_photo_thumb1"));

						if(!flag_refresh){
							refresh_Date_String = notificationDetails.getString("notification_id");
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
		protected void onPostExecute(ArrayList<NotificationGeneralModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			swipeLayout.setRefreshing(false);
			if(result == null){
				noNotifications.setVisibility(View.VISIBLE);
			}else if(result.size()==0){
				noNotifications.setVisibility(View.VISIBLE);
			}        else{
				//adapter = new NotificationGeneralAdapter(getActivity(), result);
				adapter.notifyDataSetChanged();
			}

		}
	}
	private class GetNotificationLoadMoreAsyncClass extends AsyncTask<Void, Void, ArrayList<NotificationGeneralModel>>{

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
		protected ArrayList<NotificationGeneralModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray connectionsResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(NotificationURL+"?last_date="+ pagination_Date_String);
			if(jsonObjectRecived != null){

				try{
					connectionsResponse = jsonObjectRecived.getJSONArray("notifications");
				}catch(JSONException e){
					e.printStackTrace();
				}
				if(connectionsResponse.length() > 0){
					for(int i = 0; i< connectionsResponse.length();i++){
						NotificationGeneralModel notificationData = new NotificationGeneralModel();
						JSONObject notificationDetails;
						try{
							notificationDetails = connectionsResponse.getJSONObject(i);
							notificationDetails = connectionsResponse.getJSONObject(i);
							notificationData.setNotification_id(notificationDetails.getString("notification_id"));
							notificationData.setNotification_type(notificationDetails.getString("notification_type"));
							notificationData.setTable_name(notificationDetails.getString("table_name"));
							notificationData.setPk_value(notificationDetails.getString("pk_value"));
							notificationData.setTrack_id(notificationDetails.getString("track_id"));
							notificationData.setNotification_send_user_id(notificationDetails.getString("notification_send_user_id"));
							notificationData.setCreated_date(notificationDetails.getString("created_date"));
							pagination_Date_String = notificationDetails.getString("notification_id");
							notificationData.setNotification_send_user_firstname(notificationDetails.getString("notification_send_user_firstname"));
							notificationData.setNotification_send_user_lastname(notificationDetails.getString("notification_send_user_lastname"));
							notificationData.setNotification_message(notificationDetails.getString("notification_message"));
							notificationData.setNotification_send_user_profile_photo_thumb1(notificationDetails.getString("notification_send_user_profile_photo_thumb1"));
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
		protected void onPostExecute(ArrayList<NotificationGeneralModel> result) {
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
				//	adapter = new NotificationGeneralAdapter(getActivity(), result);
				adapter.notifyDataSetChanged();
				flag_loading = false;
				pageCount = pageCount +20;
				notificationsListView.setSelection(pageCount);

			}

		}

	}
public class DeleteNotificationListItem extends AsyncTask<String,Void,JSONObject>{

		
		@Override
		protected JSONObject doInBackground(String... params) {
			String id = params[0];
			
			JSONObject sendJsonObject = new JSONObject();
			JSONObject receiveJsonResponse = null;
			try {
				sendJsonObject.put("notification_id",id);
				receiveJsonResponse = HttpPostClient.sendHttpPost(DELETE_NOTIFICATION_ITEM_API, sendJsonObject);
			} catch (JSONException e) { e.printStackTrace();}
			return receiveJsonResponse;
		}
		protected void onPostExecute(JSONObject result){
			super.onPostExecute(result);
			if(result!=null){
				
				try {
					if(result.getString("status").equalsIgnoreCase("1")){
					 Toast.makeText(getActivity(), result.getString("message"),Toast.LENGTH_LONG).show();
					 notificationSearch.remove(mDeleteItem);
					 adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}	
}


	





