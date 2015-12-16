package com.youflik.youflik.timefeed;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.google.gson.Gson;
import com.youflik.youflik.R;
import com.youflik.youflik.SigninActivity;
import com.youflik.youflik.commonAdapters.FeedAdapter;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.MyPojo;
import com.youflik.youflik.models.NotificationGeneralModel;
import com.youflik.youflik.models.TimeFeedModel;
import com.youflik.youflik.models.Timefeeds;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.utils.ListUtility;
import com.youflik.youflik.utils.Util;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class TimefeedFragment extends Fragment {
	SwipeRefreshLayout swipeLayout;
	private ListView feedListView;
	JSONArray feedResponse;
	private TextView noFeeds;
	private ImageView reload;
	private int mLastFirstVisibleItem;
	private static int pageCount = 0;
	private String FeedURL = Util.API + "post?filter=post,photo,video";
	private String VideoFeedURL = Util.API + "post?filter=video";
	private String PhotosFeedURL = Util.API + "post?filter=photo";
	private ArrayList<Timefeeds> feedSearch = new ArrayList<Timefeeds>();
	private ProgressDialog pDialog;
	private static String pagination_Date_String;
	private boolean flag_loading = false;
	private boolean flag_refresh = false;
	private static String refresh_Date_String;
	private FeedAdapter adapter;
	public static ImageView filter;
	private Dialog mDialogFilterPost;
	MyPojo feed;
	JSONObject jsonObjectRecived,jsonObjectRecived_loadMore,jsonObjectRecived_refresh,jsonObjectRecived_cache;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_timefeed, container, false);
		swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container_feed);
		feedListView = (ListView) rootView.findViewById(R.id.feedListview);
		noFeeds = (TextView) rootView.findViewById(R.id.noFeed);
		filter=(ImageView)rootView.findViewById(R.id.filter_post);
		reload=(ImageView)rootView.findViewById(R.id.click_to_load_image);
		return rootView;
	}


	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		adapter = new FeedAdapter(getActivity(),feedSearch );
		swipeLayout.setColorScheme(android.R.color.holo_blue_bright,android.R.color.holo_green_light, android.R.color.holo_orange_light,android.R.color.holo_red_light);
		feedSearch.clear();
		pageCount = 0;
		ConnectionDetector conn = new ConnectionDetector(getActivity());
		if(conn.isConnectingToInternet())
		{		
			new GetFeedAsyncClass().execute();		
		}
		else{
			Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
			new GetFeedAsyncClassCache().execute();
		}

		feedListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				boolean enable = false;
				if(feedListView != null && feedListView.getChildCount() > 0){
					boolean firstItemVisible = feedListView.getFirstVisiblePosition() == 0;
					boolean topOfFirstItemVisible = feedListView.getChildAt(0).getTop() == 0;
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
							new GetFeedLoadMoreAsyncClass().execute();
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
					swipeLayout.setRefreshing(false);
					new GetFeedRefreshAsyncClass().execute();
				}else{
					Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
				}
			}
		});

		filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialogFilterPost = new Dialog(getActivity());
				//	mDialogFilterPost.requestWindowFeature(Window.FEATURE_NO_TITLE);
				mDialogFilterPost.setTitle("Select Filters");
				mDialogFilterPost.setContentView(R.layout.filter_post);
				mDialogFilterPost.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

				final Button All;
				final Button Videos;
				final Button Photos;

				All = (Button) mDialogFilterPost.findViewById(R.id.all_types);
				Videos = (Button)mDialogFilterPost.findViewById(R.id.type_video);
				Photos = (Button)mDialogFilterPost.findViewById(R.id.type_photo);

				mDialogFilterPost.show();
				All.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Util.feedFilter="All";
						All.setBackgroundColor(Color.argb(250, 202, 252, 231));
						Videos.setBackgroundColor(color.white);
						Photos.setBackgroundColor(color.white);
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet()){
							feedSearch.clear();
							pageCount = 0;
							new GetFeedAsyncClass().execute();		
							mDialogFilterPost.dismiss();
						} else {
						}
					}
				});

				Videos.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Util.feedFilter="Videos";
						Videos.setBackgroundColor(Color.argb(250, 202, 252, 231));
						All.setBackgroundColor(color.white);
						Photos.setBackgroundColor(color.white);
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet()){
							feedSearch.clear();
							pageCount = 0;
							new GetFeedAsyncClass().execute();		
							mDialogFilterPost.dismiss();
						} else {
						}
					}
				});

				Photos.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Util.feedFilter="Photos";
						Photos.setBackgroundColor(Color.argb(250, 202, 252, 231));
						All.setBackgroundColor(color.white);
						Videos.setBackgroundColor(color.white);
						ConnectionDetector conn = new ConnectionDetector(getActivity());
						if(conn.isConnectingToInternet()){
							feedSearch.clear();
							pageCount = 0;
							new GetFeedAsyncClass().execute();		
							mDialogFilterPost.dismiss();
						} else {
						}
					}
				});

			}
		});

		reload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ConnectionDetector conn = new ConnectionDetector(getActivity());
				if(conn.isConnectingToInternet())
				{		
					new GetFeedAsyncClass().execute();		
				}
				else{
					Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
				}
			}
		});

		noFeeds.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ConnectionDetector conn = new ConnectionDetector(getActivity());
				if(conn.isConnectingToInternet())
				{		
					new GetFeedAsyncClass().execute();		
				}
				else{
					Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
				}
			}
		});
	}

	private class GetFeedAsyncClass extends AsyncTask<Void, Void, JSONObject>{

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
		protected JSONObject doInBackground(Void... params) {
			JSONArray feedResponse = null;
			if(Util.feedFilter.equalsIgnoreCase("All"))
			{
				jsonObjectRecived = HttpGetClient.sendHttpPost(FeedURL);
			}
			else if(Util.feedFilter.equalsIgnoreCase("Videos"))
			{
				jsonObjectRecived = HttpGetClient.sendHttpPost(VideoFeedURL);

			}
			else if(Util.feedFilter.equalsIgnoreCase("Photos"))
			{
				jsonObjectRecived = HttpGetClient.sendHttpPost(PhotosFeedURL);

			}
			else
			{
				jsonObjectRecived = HttpGetClient.sendHttpPost(FeedURL);

			}

			//cache
			Util.Timefeed_Json=jsonObjectRecived;

			if(jsonObjectRecived != null){
				feed=new Gson().fromJson(jsonObjectRecived.toString(), MyPojo.class);
				for(int i=0 ; i < feed.getTimefeeds().length ; i++)
				{
					Timefeeds timefeedmodel = new Timefeeds();
					timefeedmodel = feed.getTimefeeds()[i];
					pagination_Date_String=feed.getTimefeeds()[i].getTrack_time();
					feedSearch.add(timefeedmodel);
					if(!flag_refresh){
						flag_refresh = true;
						refresh_Date_String =feed.getTimefeeds()[i].getTrack_time();
					}
				}
			}
			return jsonObjectRecived;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			if(swipeLayout.isRefreshing())
			{
				swipeLayout.setRefreshing(false);
			}
			pDialog.dismiss();
			if(HttpGetClient.statuscode == 500)
			{
				Crouton.makeText(getActivity(), getString(R.string.crouton_message_serverError), Style.ALERT).show();
				reload.setVisibility(View.VISIBLE);
			}
			else if(HttpGetClient.statuscode == 504)
			{
				Crouton.makeText(getActivity(), getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
				reload.setVisibility(View.VISIBLE);
			}
			else if(HttpGetClient.statuscode==404)
			{
				Crouton.makeText(getActivity(), getString(R.string.crouton_message_auth), Style.ALERT).show();
				reload.setVisibility(View.VISIBLE);

			}
			else if(HttpGetClient.statuscode == 200)
			{
				try {
					if(result.getJSONArray("timefeeds")== null)
					{
						noFeeds.setVisibility(View.VISIBLE);
						swipeLayout.setVisibility(View.GONE);
						reload.setVisibility(View.VISIBLE);

					} 
					else if(result.getJSONArray("timefeeds").length()<=0)
					{
						noFeeds.setVisibility(View.VISIBLE);
						swipeLayout.setVisibility(View.VISIBLE);
						reload.setVisibility(View.GONE);
					}      
					else
					{
						noFeeds.setVisibility(View.GONE);
						reload.setVisibility(View.GONE);
						feedListView.setAdapter(adapter);		
						//ListUtility.setListViewHeightBasedOnChildren(feedListView);
						if(feedSearch.size() < 20)
						{
							flag_loading = true;
						} else{
							flag_loading = false;
						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private class GetFeedRefreshAsyncClass extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			JSONArray feedResponse = null;
			//JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(FeedURL+"&first_date="+ refresh_Date_String.replaceAll(" ", "%20"));
			if(Util.feedFilter.equalsIgnoreCase("All"))
			{
				jsonObjectRecived_refresh = HttpGetClient.sendHttpPost(FeedURL+"&first_date="+ refresh_Date_String.replaceAll(" ", "%20"));
			}
			else if(Util.feedFilter.equalsIgnoreCase("Videos"))
			{
				jsonObjectRecived_refresh = HttpGetClient.sendHttpPost(VideoFeedURL+"&first_date="+ refresh_Date_String.replaceAll(" ", "%20"));
			}
			else if(Util.feedFilter.equalsIgnoreCase("Photos"))
			{
				jsonObjectRecived_refresh = HttpGetClient.sendHttpPost(PhotosFeedURL+"&first_date="+ refresh_Date_String.replaceAll(" ", "%20"));
			}
			else
			{
				jsonObjectRecived_refresh = HttpGetClient.sendHttpPost(FeedURL+"&first_date="+ refresh_Date_String.replaceAll(" ", "%20"));
			}
			if(jsonObjectRecived_refresh != null){
				feed=new Gson().fromJson(jsonObjectRecived_refresh.toString(), MyPojo.class);
				for(int i=0 ; i < feed.getTimefeeds().length ; i++)
				{ 
					Timefeeds timefeedmodel = new Timefeeds();
					timefeedmodel = feed.getTimefeeds()[i];
					feedSearch.add(timefeedmodel);
					if(!flag_refresh){
						flag_refresh = true;
						refresh_Date_String =feed.getTimefeeds()[i].getTrack_time();
					}
					feedSearch.add(0,timefeedmodel);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			swipeLayout.setRefreshing(false);
			if(HttpGetClient.statuscode == 500)
			{
				Crouton.makeText(getActivity(), getString(R.string.crouton_message_serverError), Style.ALERT).show();
			}
			else if(HttpGetClient.statuscode == 504)
			{
				Crouton.makeText(getActivity(), getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
			}
			else if(HttpGetClient.statuscode==401)
			{
				Crouton.makeText(getActivity(), getString(R.string.crouton_message_auth), Style.ALERT).show();
			}
			else if(HttpGetClient.statuscode == 200)
			{
				if(feedSearch == null)
				{
					Toast.makeText(getActivity(), "No more feeds to load", Toast.LENGTH_SHORT).show();
				} 
				else if(feedSearch.size()==0)
				{
					Toast.makeText(getActivity(), "No more feeds to load", Toast.LENGTH_SHORT).show();
				}      
				else
				{
					adapter.notifyDataSetChanged();
					//ListUtility.setListViewHeightBasedOnChildren(feedListView);
				}
			}
		}
	}

	private class GetFeedLoadMoreAsyncClass extends AsyncTask<Void, Void, ArrayList<Timefeeds>>{

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
		protected ArrayList<Timefeeds> doInBackground(Void... params) {
			JSONArray feedResponse = null;
			//JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(FeedURL+"&last_date="+ pagination_Date_String.replaceAll(" ", "%20"));
			if(Util.feedFilter.equalsIgnoreCase("All"))
			{
				jsonObjectRecived_loadMore = HttpGetClient.sendHttpPost(FeedURL+"&last_date="+ pagination_Date_String.replaceAll(" ", "%20"));
			}
			else if(Util.feedFilter.equalsIgnoreCase("Videos"))
			{
				jsonObjectRecived_loadMore = HttpGetClient.sendHttpPost(VideoFeedURL+"&last_date="+ pagination_Date_String.replaceAll(" ", "%20"));
			}
			else if(Util.feedFilter.equalsIgnoreCase("Photos"))
			{
				jsonObjectRecived_loadMore = HttpGetClient.sendHttpPost(PhotosFeedURL+"&last_date="+ pagination_Date_String.replaceAll(" ", "%20"));
			}
			else
			{
				jsonObjectRecived_loadMore = HttpGetClient.sendHttpPost(FeedURL+"&last_date="+ pagination_Date_String.replaceAll(" ", "%20"));
			}
			if(jsonObjectRecived_loadMore != null){
				feed=new Gson().fromJson(jsonObjectRecived_loadMore.toString(), MyPojo.class);
				if(feed.getTimefeeds().length > 0){
					for(int i=0 ; i < feed.getTimefeeds().length ; i++)
					{
						Timefeeds timefeedmodel = new Timefeeds();
						timefeedmodel = feed.getTimefeeds()[i];
						pagination_Date_String=feed.getTimefeeds()[i].getTrack_time();
						feedSearch.add(timefeedmodel);

					}
					return feedSearch;
				}
				else
				{
					return null;
				}
			}
			else
			{
				return null;
			}



		}

		@Override
		protected void onPostExecute(ArrayList<Timefeeds> result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if(HttpGetClient.statuscode == 500)
			{
				Crouton.makeText(getActivity(), getString(R.string.crouton_message_serverError), Style.ALERT).show();
			}
			else if(HttpGetClient.statuscode == 504)
			{
				Crouton.makeText(getActivity(), getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
			}
			else if(HttpGetClient.statuscode==401)
			{
				Crouton.makeText(getActivity(), getString(R.string.crouton_message_auth), Style.ALERT).show();
			}
			else if(HttpGetClient.statuscode == 200)
			{
				if(result == null)
				{
					if(adapter.isEmpty()){
						flag_loading = false;
					}
					flag_loading = true;
				} 
				else if(result.size()==0)
				{
					if(adapter.isEmpty()){
					}
					flag_loading = true;
				}      
				else
				{
					adapter.notifyDataSetChanged();
					flag_loading = false;
					pageCount = pageCount +20;
					feedListView.setSelection(pageCount-2);
				}
			}
		}

	}
	private class GetFeedAsyncClassCache extends AsyncTask<Void, Void, Void>{

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
		protected Void doInBackground(Void... params) {
			JSONArray feedResponse = null;
			jsonObjectRecived_cache = Util.Timefeed_Json;
			//cache
			Util.Timefeed_Json=jsonObjectRecived_cache;

			if(jsonObjectRecived_cache != null){
				feed=new Gson().fromJson(jsonObjectRecived_cache.toString(), MyPojo.class);
				for(int i=0 ; i < feed.getTimefeeds().length ; i++)
				{
					Timefeeds timefeedmodel = new Timefeeds();
					timefeedmodel = feed.getTimefeeds()[i];
					pagination_Date_String=feed.getTimefeeds()[i].getTrack_time();
					feedSearch.add(timefeedmodel);
					if(!flag_refresh){
						flag_refresh = true;
						refresh_Date_String =feed.getTimefeeds()[i].getTrack_time();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(swipeLayout.isRefreshing())
			{
				swipeLayout.setRefreshing(false);
			}
			pDialog.dismiss();
			if(feedSearch == null)
			{
				noFeeds.setVisibility(View.VISIBLE);
				swipeLayout.setVisibility(View.GONE);
			} 
			else if(feedSearch.size()==0)
			{
				noFeeds.setVisibility(View.VISIBLE);
				swipeLayout.setVisibility(View.GONE);

			}      
			else
			{
				feedListView.setAdapter(adapter);		
				//ListUtility.setListViewHeightBasedOnChildren(feedListView);
				if(feedSearch.size() < 20)
				{
					flag_loading = true;
				} else{
					flag_loading = false;
				}

			}
		}

	}

}
