package com.youflik.youflik.songs;

import java.util.ArrayList;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.youflik.youflik.R;
import com.youflik.youflik.commonAdapters.SongsAdapter;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.SongsModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.utils.Util;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class ProfileSongs extends Activity {
	private ProgressDialog pDialog;
	private EditText song_search;
	private ListView listsong;
	private ArrayList<SongsModel> songSearch = new ArrayList<SongsModel>();
	private SongsAdapter adapter;
	private static String pagination_Date_String;
	private static String pagination_Date;
	private boolean flag_loading = false;
	private static int pageCount = 0;
	private static boolean paginationFlag=false;
	private static String song_id;
	private JSONObject	jsonObj;
	private ImageView search_close;
	private String song_intent_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.songs_list);
		Typeface typeFace=Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/BentonSans-Regular.otf");
		song_search=(EditText)findViewById(R.id.searchsongname);
		listsong = (ListView)findViewById(R.id.Songslistview);
		search_close=(ImageView)findViewById(R.id.close_search_songs);
		adapter = new SongsAdapter(getApplicationContext(), songSearch);
		// By Default
		ConnectionDetector conn = new ConnectionDetector(ProfileSongs.this);
		if(conn.isConnectingToInternet()){
			songSearch.clear();
			pageCount = 0;
			new SongsAsyncClass().execute();

		}else{
			Crouton.makeText(ProfileSongs.this, getString(R.string.crouton_message), Style.ALERT).show();
		}
		//End Default
		
		// search when click on search in soft keypad
		song_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				    @Override
				    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				        	  
							// TODO Auto-generated method stub
							if(adapter != null){
								ConnectionDetector conn = new ConnectionDetector(ProfileSongs.this);
								if(conn.isConnectingToInternet()){

									if(song_search.getText().toString().trim().length()>0){
										songSearch.clear();
										pageCount = 0;
										new SongsSearchAsyncClass().execute();
									}
									else
									{
										Toast.makeText(ProfileSongs.this, "Add text to search songs", Toast.LENGTH_SHORT).show();
									}
								}else{
									Crouton.makeText(ProfileSongs.this, getString(R.string.crouton_message), Style.ALERT).show();
								}
							}else{
								Toast.makeText(ProfileSongs.this, "Songs Not Found", Toast.LENGTH_SHORT).show();
							}

						
				        }
				        return false;
				    }
				});
				// end search
/*		song_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {  
				// TODO Auto-generated method stub
				if(adapter != null){
					ConnectionDetector conn = new ConnectionDetector(ProfileSongs.this);
					if(conn.isConnectingToInternet()){

						if(song_search.getText().toString().trim().length()>0){
							songSearch.clear();
							pageCount = 0;
							new SongsSearchAsyncClass().execute();
						}
						else
						{
							Toast.makeText(ProfileSongs.this, "Add text to search songs", Toast.LENGTH_SHORT).show();
						}
					}else{
						Crouton.makeText(ProfileSongs.this, getString(R.string.crouton_message), Style.ALERT).show();
					}
				}else{
					Toast.makeText(ProfileSongs.this, "Songs Not Found", Toast.LENGTH_SHORT).show();
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
		});*/
		listsong.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Object object = listsong.getItemAtPosition(position);
				SongsModel songs_data = (SongsModel)  object;
				song_id=songs_data.getSong_id();
				song_intent_name=songs_data.getSong_title();
				new UpdateSongAsync().execute();

			}
		});
		listsong.setOnScrollListener(new OnScrollListener() {

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
						ConnectionDetector conn = new ConnectionDetector(ProfileSongs.this);
						if(conn.isConnectingToInternet()){
							new SongsSearchLoadMoreAsyncClass().execute();
						}else{
							Crouton.makeText(ProfileSongs.this, getString(R.string.crouton_message), Style.ALERT).show();
						}
					}
				}
			}
		});
		search_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				song_search.setText("");
				song_search.setHint("search songs");
			//	songSearch.clear();
			}
		});
	}
	private class SongsSearchLoadMoreAsyncClass extends AsyncTask<Void, Void, ArrayList<SongsModel>>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(ProfileSongs.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected ArrayList<SongsModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray songsResponse = null;
			JSONObject jsonObjectRecived = null ;
			if(paginationFlag==false)
			{
				jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"song?search_text="+song_search.getText().toString().trim().toLowerCase(Locale.getDefault()).replaceAll(" ", "%20")+ "&last_date=" + pagination_Date_String.replaceAll(" ", "%20"));
			}
			else
			{
				jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"song"+ "?last_date=" + pagination_Date.replaceAll(" ", "%20"));

			}
			if(jsonObjectRecived != null){

				try{
					songsResponse = jsonObjectRecived.getJSONArray("songs");
				}catch(JSONException e){
					e.printStackTrace();
				}
				if(songsResponse.length() > 0){
					for(int i = 0; i< songsResponse.length();i++){
						SongsModel songsData = new SongsModel();
						JSONObject songsDetails;
						try{
							songsDetails = songsResponse.getJSONObject(i);
							songsData.setSong_id(songsDetails.getString("song_id"));
							songsData.setSong_title(songsDetails.getString("song_title"));
							songsData.setSong_desc(songsDetails.getString("song_desc"));
							songsData.setSong_label(songsDetails.getString("song_label"));
							songsData.setSong_time(songsDetails.getString("song_time"));
							songsData.setActual_song_path(songsDetails.getString("actual_song_path"));
							songsData.setUploaded_date(songsDetails.getString("uploaded_date"));
							if(paginationFlag==false)
							{
								pagination_Date_String = songsDetails.getString("uploaded_date");
							}
							else
							{
								pagination_Date = songsDetails.getString("uploaded_date");
							}
							songsData.setSong_cover_photo_path(songsDetails.getString("song_cover_photo_path"));
							songsData.setSong_owner_id(songsDetails.getString("song_owner_id"));

							songSearch.add(songsData);
						}catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					return songSearch;
				}else{
					return null;
				}
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<SongsModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result == null){
				if(adapter.isEmpty()){
					//noNotifications.setVisibility(View.VISIBLE);
					flag_loading = false;
				}
				flag_loading = true;
			}else if(result.size()==0){
				if(adapter.isEmpty()){
					//noNotifications.setVisibility(View.VISIBLE);
				}
				flag_loading = true;
			}      else{
				adapter.notifyDataSetChanged();
				flag_loading = false;
				pageCount = pageCount +20;
				listsong.setSelection(pageCount);

			}

		}

	}

	private class SongsSearchAsyncClass extends AsyncTask<Void, Void, ArrayList<SongsModel>>{

/*		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(ProfileSongs.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}*/

		@Override
		protected ArrayList<SongsModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray songsResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"song?search_text="+song_search.getText().toString().trim().toLowerCase(Locale.getDefault()).replaceAll(" ", "%20"));
			if(jsonObjectRecived != null){

				try{
					songsResponse = jsonObjectRecived.getJSONArray("songs");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< songsResponse.length();i++){
					SongsModel songsData = new SongsModel();
					JSONObject songsDetails;
					try{
						songsDetails = songsResponse.getJSONObject(i);
						songsData.setSong_id(songsDetails.getString("song_id"));
						songsData.setSong_title(songsDetails.getString("song_title"));
						songsData.setSong_desc(songsDetails.getString("song_desc"));
						songsData.setSong_label(songsDetails.getString("song_label"));
						songsData.setSong_time(songsDetails.getString("song_time"));
						songsData.setActual_song_path(songsDetails.getString("actual_song_path"));
						songsData.setUploaded_date(songsDetails.getString("uploaded_date"));
						pagination_Date_String = songsDetails.getString("uploaded_date");
						songsData.setSong_cover_photo_path(songsDetails.getString("song_cover_photo_path"));
						songsData.setSong_owner_id(songsDetails.getString("song_owner_id"));

						songSearch.add(songsData);
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return songSearch;
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<SongsModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// pDialog.dismiss();
			if(result == null){
				//noNotifications.setVisibility(View.VISIBLE);

			}else if(result.size()==0){
				//noNotifications.setVisibility(View.VISIBLE);

			}        else{
				paginationFlag=false;
				listsong.setAdapter(adapter);
				if(result.size() < 20){
					flag_loading = true;
				}else{
					flag_loading = false;
				}

			}

		}

	}
	private class SongsAsyncClass extends AsyncTask<Void, Void, ArrayList<SongsModel>>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(ProfileSongs.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected ArrayList<SongsModel> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONArray songsResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(Util.API+"song");
			if(jsonObjectRecived != null){

				try{
					songsResponse = jsonObjectRecived.getJSONArray("songs");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< songsResponse.length();i++){
					SongsModel songsData = new SongsModel();
					JSONObject songsDetails;
					try{
						songsDetails = songsResponse.getJSONObject(i);
						songsData.setSong_id(songsDetails.getString("song_id"));
						songsData.setSong_title(songsDetails.getString("song_title"));
						songsData.setSong_desc(songsDetails.getString("song_desc"));
						songsData.setSong_label(songsDetails.getString("song_label"));
						songsData.setSong_time(songsDetails.getString("song_time"));
						songsData.setActual_song_path(songsDetails.getString("actual_song_path"));
						songsData.setUploaded_date(songsDetails.getString("uploaded_date"));
						pagination_Date = songsDetails.getString("uploaded_date");
						songsData.setSong_cover_photo_path(songsDetails.getString("song_cover_photo_path"));
						songsData.setSong_owner_id(songsDetails.getString("song_owner_id"));

						songSearch.add(songsData);
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return songSearch;
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<SongsModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result == null){
				//noNotifications.setVisibility(View.VISIBLE);

			}else if(result.size()==0){
				//noNotifications.setVisibility(View.VISIBLE);

			}        else{
				paginationFlag=true;
				listsong.setAdapter(adapter);
				if(result.size() < 20){
					flag_loading = true;
				}else{
					flag_loading = false;
				}

			}

		}

	}
	private class UpdateSongAsync extends AsyncTask<Void, Void, Integer>{

		@Override
		protected void onPreExecute() {
			if(pDialog == null){
				pDialog = Util.createProgressDialog(ProfileSongs.this);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}

		@Override
		protected Integer doInBackground(Void... params) {
			JSONObject jsonObjSend = new JSONObject();

			try {
				jsonObjSend.put("profile_song_id",song_id);
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}

			jsonObj= HttpPostClient.sendHttpPost(Util.API+"user_profile_song", jsonObjSend);

			return 1;
		}

		@SuppressLint("NewApi") @Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if(HttpPostClient.statuscode == 500){
				Crouton.makeText(ProfileSongs.this, getString(R.string.crouton_message_serverError), Style.ALERT).show();
			}if(HttpPostClient.statuscode == 504){
				Crouton.makeText(ProfileSongs.this, getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
			}
			else{
				if(jsonObj!=null)
				{ 
					if(jsonObj.has("errorMessages") )
					{
						Toast.makeText(ProfileSongs.this,"Error Occured ! ",Toast.LENGTH_LONG).show();	
					}
					else
					{
						Intent intent = new Intent();
						intent.putExtra("SongTitle", song_intent_name);
						setResult(RESULT_OK,intent);
						ProfileSongs.this.finish();
					}
				}

			}
		}


	}
}
