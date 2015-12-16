package com.youflik.youflik.notification;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.NotificationFriendRequestModel;
import com.youflik.youflik.proxy.HttpGetClient;
import com.youflik.youflik.proxy.HttpPutClient;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class NotificationFriendRequestAdapter extends BaseAdapter{

	private ArrayList<NotificationFriendRequestModel> listData;
	private LayoutInflater layoutInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Context prova;
	private ProgressDialog pDialog;
	private String NotificationURL = Util.API + "friend_request";

	public NotificationFriendRequestAdapter(Context context,ArrayList<NotificationFriendRequestModel> listData){
		this.listData = listData;
		//layoutInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		prova = context;

		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		final NotificationFriendRequestModel notificationItem = (NotificationFriendRequestModel) listData.get(position);
		if (layoutInflater == null)
			layoutInflater = (LayoutInflater) prova.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.custom_friend_request_notification, null);
			holder = new ViewHolder();
			holder.textViewName = (TextView) convertView.findViewById(R.id.Notifications_FR_UserName);
			holder.imageViewUserPhoto = (ImageView) convertView.findViewById(R.id.notifications_FR_Image);
			holder.accept=(Button)convertView.findViewById(R.id.accept);
			holder.reject=(Button)convertView.findViewById(R.id.reject);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		//	String str = ("<b>" + notificationItem.getFirstname()) + " </b>" + " " + "<font color=\"#808080\">" + "added you as a friend" + "</font>";
		//	holder.textViewName.setText(Html.fromHtml(str));
		Typeface typeFace=Typeface.createFromAsset(prova.getAssets(),"fonts/BentonSans-Regular.otf");
		//holder.textViewName.setText(notificationItem.getFirstname());
		if(notificationItem.getLastname()==null || notificationItem.getLastname().equalsIgnoreCase("null") || notificationItem.getLastname().equalsIgnoreCase(" "))
		{
			holder.textViewName.setText(notificationItem.getFirstname());
		}else
		{
			holder.textViewName.setText(notificationItem.getFirstname() +" "+ notificationItem.getLastname());
		}

		holder.textViewName.setTypeface(typeFace);
		//anim
		final Animation animation = AnimationUtils.loadAnimation(prova,
				R.anim.fade_out);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				listData.remove(position);
				notifyDataSetChanged();
			}
		});
		//end anim

		holder.accept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ConnectionDetector conn = new ConnectionDetector(prova);
				if(conn.isConnectingToInternet())
				{		
					new NotificationFriendAcceptStatus().execute(notificationItem.getFriend_request_id(),notificationItem.getUser_id(),"1");	
					holder.textViewName.setText((Html.fromHtml(notificationItem.getFirstname())) + " "+ "is now connected with you");
					//notificationItem.setNotification_text("is now connected to you.");
					/*	listData.remove(position);
					notifyDataSetChanged();*/
					//holder.accept.setVisibility(View.GONE);
					//holder.reject.setVisibility(View.GONE);
					v.startAnimation(animation);

				}
				else{
					Toast.makeText(prova, "Please check your internet connection", Toast.LENGTH_SHORT).show();
				}
			}
		});

		holder.reject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ConnectionDetector conn = new ConnectionDetector(prova);
				if(conn.isConnectingToInternet())
				{		
					new NotificationFriendAcceptStatus().execute(notificationItem.getFriend_request_id(),notificationItem.getUser_id(),"0");	
					holder.textViewName.setText((Html.fromHtml(notificationItem.getFirstname())) + " "+ "is not connected with you");
					//notificationItem.setNotification_text("is now connected to you.");
					/*					listData.remove(position);
					notifyDataSetChanged();
					holder.accept.setVisibility(View.GONE);
					holder.reject.setVisibility(View.GONE);*/
					v.startAnimation(animation);
				}
				else{
					Toast.makeText(prova, "Please check your internet connection", Toast.LENGTH_SHORT).show();
				}

			}
		});


		imageLoader.displayImage(notificationItem.getThumb1(), holder.imageViewUserPhoto, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current,
					int total) {

			}
		}
				);


		return convertView;
	}

	static class ViewHolder{
		ImageView imageViewUserPhoto;
		TextView textViewName;
		Button accept,reject;
	}

	private class NotificationFriendAcceptStatus extends AsyncTask<String, Void, Integer>{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			// Showing progress dialog before sending http request
			if(pDialog == null){
				pDialog = Util.createProgressDialog(prova);
				pDialog.setCancelable(false);
				pDialog.show();}
			else{
				pDialog.setCancelable(false);
				pDialog.show();
			}}
		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			// JSON object to hold the information, which is sent to the server
			JSONObject jsonObjSend = new JSONObject();
			String friend_request_id = params[0];
			String friend_req_send_user = params[1];
			String accept_reject_status = params[2];

			try {
				jsonObjSend.put("friend_req_send_user",friend_req_send_user);
				jsonObjSend.put("accept_reject_status",accept_reject_status);
				jsonObjSend.put("is_follow","0");
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}

			JSONObject jsonObjRecv = HttpPutClient.sendHttpPost(Util.API+"friend_request/"+friend_request_id, jsonObjSend);
			return null;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();

		}

	}
}

