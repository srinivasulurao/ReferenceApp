package com.youflik.youflik.userprofile;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.etsy.android.grid.StaggeredGridView;
import com.youflik.youflik.R;
import com.youflik.youflik.ImageDetailPager.ImageDetail;
import com.youflik.youflik.ImageDetailPager.videoDetail;
import com.youflik.youflik.commonAdapters.UserGalleryPhotoAdapter;
import com.youflik.youflik.commonAdapters.UserGalleryVideosAdapter;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.VideosModel;
import com.youflik.youflik.models.VideosModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;
import com.youflik.youflik.utils.Util.Extra;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FragmentUserVideos extends Fragment {

	private StaggeredGridView videoGridView;
	private TextView noVideos;
	private String UserGalleryVideosURL = Util.API+ "video?user_id=";
	private ArrayList<VideosModel> galleryVideoSearch = new ArrayList<VideosModel>();
	private ProgressDialog pDialog;
	private UserGalleryVideosAdapter adapter;
	private static int pageCount = 0;
	private static String pagination_Date_String = "";
	private boolean flag_loading = false;
	AlertDialogManager alert = new AlertDialogManager();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_videos, container, false);
		videoGridView = (StaggeredGridView) view.findViewById(R.id.grid_view_user_videos);
		noVideos = (TextView) view.findViewById(R.id.Novideos);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		adapter = new UserGalleryVideosAdapter(getActivity(), galleryVideoSearch);
		galleryVideoSearch.clear();
		pagination_Date_String = "";
		ConnectionDetector conn = new ConnectionDetector(getActivity());
		if(conn.isConnectingToInternet()){
			new GalleryVideoAsync().execute();
		}else{
			Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
		}


		videoGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				startImagePagerActivity(position);
				/*				Object object = videoGridView.getItemAtPosition(position);
				VideosModel connection_data = (VideosModel)  object;
				String extension = MimeTypeMap.getFileExtensionFromUrl(connection_data.getVideo_path());
				String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
				Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
				mediaIntent.setDataAndType(Uri.parse(connection_data.getVideo_path()), mimeType);
				startActivity(mediaIntent);*/
			}
		});

		videoGridView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
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
							new GalleryVideoLoadMoreAsync().execute();
						}else{
							Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
						}

					}
				}
			}
		});


	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(getActivity(), videoDetail.class);
		//intent.putExtra(Extra.IMAGES, imageUrls);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		intent.putExtra("Activity", "User");
		startActivity(intent); 
	}
	private class GalleryVideoAsync extends AsyncTask<Void, Void, ArrayList<VideosModel>>{

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
		protected ArrayList<VideosModel> doInBackground(
				Void... params) {
			// TODO Auto-generated method stub
			JSONArray userGalleryVideosResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(UserGalleryVideosURL+Util.USER_ID);

			if(jsonObjectRecived != null){

				try{
					userGalleryVideosResponse = jsonObjectRecived.getJSONArray("videos");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< userGalleryVideosResponse.length();i++){
					VideosModel videosData = new VideosModel();
					JSONObject videosDetails;
					try{
						videosDetails = userGalleryVideosResponse.getJSONObject(i);
						videosData.setVideo_id(videosDetails.getString("video_id"));
						videosData.setVideo_title(videosDetails.getString("video_title"));
						videosData.setCreated_date(videosDetails.getString("created_date"));	
						pagination_Date_String = videosDetails.getString("created_date");
						videosData.setSelected_video_poster(videosDetails.getString("selected_video_poster"));					
						videosData.setVideo_play_count(videosDetails.getString("video_play_count"));	
						videosData.setVideo_like_count(videosDetails.getString("video_like_count"));	
						videosData.setVideo_path(videosDetails.getString("video_path"));
						videosData.setVideo_comment_count(videosDetails.getString("video_comment_count"));	
						videosData.setVideo_share_count(videosDetails.getString("video_share_count"));	
						galleryVideoSearch.add(videosData);
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return galleryVideoSearch;
			}else{
				return null;
			}
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(ArrayList<VideosModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();

			if(result == null){
				noVideos.setVisibility(View.VISIBLE);
				Crouton.makeText(getActivity(), getString(R.string.crouton_gallery_photo), Style.ALERT).show();
			}else if(result.size()==0){
				noVideos.setVisibility(View.VISIBLE);
			}  else{
				//	adapter = new UserGalleryPhotoAdapter(getActivity(), result);
				videoGridView.setAdapter(adapter);
				if(result.size() < 20){
					flag_loading = true;
				}else{
					flag_loading = false;
				}
			}
		}
	}

	private class GalleryVideoLoadMoreAsync extends AsyncTask<Void, Void, ArrayList<VideosModel>>{


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
		protected ArrayList<VideosModel> doInBackground(
				Void... params) {
			// TODO Auto-generated method stub
			JSONArray userGalleryVideosResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(UserGalleryVideosURL+Util.USER_ID + "&last_date=" + pagination_Date_String.replaceAll(" ", "%20"));

			if(jsonObjectRecived != null){
				try{
					userGalleryVideosResponse = jsonObjectRecived.getJSONArray("videos");
				}catch(JSONException e){
					e.printStackTrace();
				}
				if(userGalleryVideosResponse.length() > 0){
					for(int i = 0; i< userGalleryVideosResponse.length();i++){
						VideosModel videosData = new VideosModel();
						JSONObject videosDetails;
						try{
							videosDetails = userGalleryVideosResponse.getJSONObject(i);
							videosData.setVideo_id(videosDetails.getString("video_id"));
							videosData.setVideo_title(videosDetails.getString("video_title"));
							videosData.setCreated_date(videosDetails.getString("created_date"));	
							videosData.setVideo_path(videosDetails.getString("video_path"));
							pagination_Date_String = videosDetails.getString("created_date");
							videosData.setSelected_video_poster(videosDetails.getString("selected_video_poster"));					
							videosData.setVideo_play_count(videosDetails.getString("video_play_count"));	
							videosData.setVideo_like_count(videosDetails.getString("video_like_count"));	
							videosData.setVideo_comment_count(videosDetails.getString("video_comment_count"));	
							videosData.setVideo_share_count(videosDetails.getString("video_share_count"));	
							galleryVideoSearch.add(videosData);
						}catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					return galleryVideoSearch;
				}else{
					return null;
				}
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<VideosModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result == null){
				if(adapter.isEmpty()){
					noVideos.setVisibility(View.VISIBLE);
					flag_loading = false;
				}
				flag_loading = true;
				Crouton.makeText(getActivity(), getString(R.string.crouton_gallery_photo), Style.ALERT).show();
			}else if(result.size()==0){
				if(adapter.isEmpty()){
					noVideos.setVisibility(View.VISIBLE);
				}
				flag_loading = true;
				Toast.makeText(getActivity(), "No More Videos to Load", Toast.LENGTH_SHORT).show();
			}  else{
				//adapter = new UserGalleryPhotoAdapter(getActivity(), result);
				adapter.notifyDataSetChanged();
				flag_loading = false;
				pageCount = pageCount +20;
				videoGridView.setSelection(pageCount);
			}

		}

	}
}