package com.youflik.youflik.thirdPartyProfileView;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.etsy.android.grid.StaggeredGridView;
import com.youflik.youflik.R;
import com.youflik.youflik.ImageDetailPager.ImageDetail;
import com.youflik.youflik.commonAdapters.UserGalleryPhotoAdapter;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.PhotosModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;
import com.youflik.youflik.utils.Util.Extra;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ThirdPartyFragmentUserPhotos extends Fragment {

	private StaggeredGridView photoGridView;
	private TextView noPhotos;
	private String UserGalleryPhotosURL = Util.API+ "photo?user_id=";
	private ArrayList<PhotosModel> galleryPhotoSearch = new ArrayList<PhotosModel>();
	private ProgressDialog pDialog;
	private UserGalleryPhotoAdapter adapter;
	private static int pageCount = 0;
	private static String pagination_Date_String = "";
	private boolean flag_loading = false;
	AlertDialogManager alert = new AlertDialogManager();
	private String friedID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_user_photos, container, false);
		photoGridView = (StaggeredGridView) view.findViewById(R.id.grid_view_user_photos);
		noPhotos = (TextView) view.findViewById(R.id.Nophotos);
		ThirdPartyUserDetailActivity thirdParty = (ThirdPartyUserDetailActivity) getActivity();
		friedID = thirdParty.FriendID;
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		adapter = new UserGalleryPhotoAdapter(getActivity(), galleryPhotoSearch);
		galleryPhotoSearch.clear();
		pagination_Date_String = "";
		ConnectionDetector conn = new ConnectionDetector(getActivity());
		if(conn.isConnectingToInternet()){
			new GalleryPhotoAsync().execute();
		}else{
			Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
		}


		photoGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				startImagePagerActivity(position);
			}
		});

		photoGridView.setOnScrollListener(new OnScrollListener() {
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
							new GalleryPhotoLoadMoreAsync().execute();
						}else{
							Crouton.makeText(getActivity(), getString(R.string.crouton_message), Style.ALERT).show();
						}

					}
				}
			}
		});


	}

		private void startImagePagerActivity(int position) {
		Intent intent = new Intent(getActivity(), ImageDetail.class);
		intent.putExtra(Extra.IMAGE_POSITION, position);
		intent.putExtra("Activity", "ThirdParty");
		intent.putExtra("FriendID", friedID);
		startActivity(intent); 
	}
	 
	private class GalleryPhotoAsync extends AsyncTask<Void, Void, ArrayList<PhotosModel>>{

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
		protected ArrayList<PhotosModel> doInBackground(
				Void... params) {
			// TODO Auto-generated method stub
			JSONArray userGalleryPhotosResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(UserGalleryPhotosURL+friedID);

			if(jsonObjectRecived != null){

				try{
					userGalleryPhotosResponse = jsonObjectRecived.getJSONArray("photos");
				}catch(JSONException e){
					e.printStackTrace();
				}
				for(int i = 0; i< userGalleryPhotosResponse.length();i++){
					PhotosModel photosData = new PhotosModel();
					JSONObject photoDetails;
					try{
						photoDetails = userGalleryPhotosResponse.getJSONObject(i);
						photosData.setPhoto_id(photoDetails.getString("photo_id"));
						photosData.setPhoto_name(photoDetails.getString("photo_name"));
						photosData.setPhoto_desc(photoDetails.getString("photo_desc"));
						photosData.setPhoto_taken_date(photoDetails.getString("photo_taken_date"));
						photosData.setLoc_id(photoDetails.getString("loc_id"));
						photosData.setOriginal_dimensions(photoDetails.getString("original_dimensions"));
						photosData.setUploaded_date(photoDetails.getString("uploaded_date"));
						pagination_Date_String = photoDetails.getString("uploaded_date");
						photosData.setActual_photo_path_thumb3(photoDetails.getString("actual_photo_path_thumb3"));
						galleryPhotoSearch.add(photosData);
					}catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return galleryPhotoSearch;
			}else{
				return null;
			}
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(ArrayList<PhotosModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();

			if(result == null){
				noPhotos.setVisibility(View.VISIBLE);
				Crouton.makeText(getActivity(), getString(R.string.crouton_gallery_photo), Style.ALERT).show();
			}else if(result.size()==0){
				noPhotos.setVisibility(View.VISIBLE);
			}  else{
			//	adapter = new UserGalleryPhotoAdapter(getActivity(), result);
				photoGridView.setAdapter(adapter);
				if(result.size() < 20){
					flag_loading = true;
				}else{
					flag_loading = false;
				}
			}
		}
	}

	private class GalleryPhotoLoadMoreAsync extends AsyncTask<Void, Void, ArrayList<PhotosModel>>{


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
		protected ArrayList<PhotosModel> doInBackground(
				Void... params) {
			// TODO Auto-generated method stub
			JSONArray userGalleryPhotosResponse = null;
			JSONObject jsonObjectRecived = HttpGetClient.sendHttpPost(UserGalleryPhotosURL+friedID + "&last_date=" + pagination_Date_String.replaceAll(" ", "%20"));

			if(jsonObjectRecived != null){
				try{
					userGalleryPhotosResponse = jsonObjectRecived.getJSONArray("photos");
				}catch(JSONException e){
					e.printStackTrace();
				}
				if(userGalleryPhotosResponse.length() > 0){
					for(int i = 0; i< userGalleryPhotosResponse.length();i++){
						PhotosModel photosData = new PhotosModel();
						JSONObject photoDetails;
						try{
							photoDetails = userGalleryPhotosResponse.getJSONObject(i);
							photosData.setPhoto_id(photoDetails.getString("photo_id"));
							photosData.setPhoto_name(photoDetails.getString("photo_name"));
							photosData.setPhoto_desc(photoDetails.getString("photo_desc"));
							photosData.setPhoto_taken_date(photoDetails.getString("photo_taken_date"));
							photosData.setLoc_id(photoDetails.getString("loc_id"));
							photosData.setOriginal_dimensions(photoDetails.getString("original_dimensions"));
							photosData.setUploaded_date(photoDetails.getString("uploaded_date"));
							pagination_Date_String = photoDetails.getString("uploaded_date");
							photosData.setActual_photo_path_thumb3(photoDetails.getString("actual_photo_path_thumb3"));

							galleryPhotoSearch.add(photosData);
						}catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					return galleryPhotoSearch;
				}else{
					return null;
				}
			}else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<PhotosModel> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result == null){
				if(adapter.isEmpty()){
					noPhotos.setVisibility(View.VISIBLE);
					flag_loading = false;
				}
				flag_loading = true;
				Crouton.makeText(getActivity(), getString(R.string.crouton_gallery_photo), Style.ALERT).show();
			}else if(result.size()==0){
				if(adapter.isEmpty()){
					noPhotos.setVisibility(View.VISIBLE);
				}
				flag_loading = true;
				Toast.makeText(getActivity(), "No More Photos to Load", Toast.LENGTH_SHORT).show();
			}  else{
				//adapter = new UserGalleryPhotoAdapter(getActivity(), result);
				adapter.notifyDataSetChanged();
				flag_loading = false;
				pageCount = pageCount +20;
				photoGridView.setSelection(pageCount);
			}

		}

	}
}
