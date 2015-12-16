package com.youflik.youflik.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.MainActivity;
import com.youflik.youflik.R;
import com.youflik.youflik.SigninActivity;
import com.youflik.youflik.models.SearchModel;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.proxy.HttpPutClient;
import com.youflik.youflik.utils.Util;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BlockAdapter extends BaseAdapter{

	public ArrayList<SearchModel> listData;
	//private ArrayList<SearchModel> arraylist;
	private LayoutInflater layoutInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Context prova;
	String[] sections;
	private boolean state;
	Activity contex_a;
	private ProgressDialog pDialog;
	private String block_userID;
	private JSONObject jsonObjRecv,jsonObjRecv_b;
	private  boolean delete_flag=false;

	public BlockAdapter(Activity contex_a,ArrayList<SearchModel> listData){
		this.listData = listData;
	//	this.arraylist = new ArrayList<SearchModel>();
	//	this.arraylist.addAll(listData);
		//layoutInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		this.contex_a = contex_a;
		prova = contex_a;

		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
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
		final SearchModel connectionsItem = (SearchModel) listData.get(position);
		if (layoutInflater == null)
			layoutInflater = (LayoutInflater) prova.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.custom_block_user, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.block_user_name);
			holder.image = (ImageView) convertView.findViewById(R.id.block_user_image);
			holder.block=(Button)convertView.findViewById(R.id.block_button);
			convertView.setTag(holder);		

		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		// addind Font 
		Typeface typeFace=Typeface.createFromAsset(prova.getAssets(),"fonts/BentonSans-Regular.otf");
		if(connectionsItem.getLastname()==null || connectionsItem.getLastname().equalsIgnoreCase("null") || connectionsItem.getLastname().equalsIgnoreCase(" "))
		{
			holder.name.setText(connectionsItem.getFirstname());
		}else
		{
			holder.name.setText(connectionsItem.getFirstname() +" "+ connectionsItem.getLastname());
		}

		holder.name.setTypeface(typeFace);
		holder.block.setTypeface(typeFace);

		String imageUrl = connectionsItem.getUser_profile_photo_path();

		imageLoader.displayImage(imageUrl, holder.image, options, new SimpleImageLoadingListener() {
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
		if(Util.block_flag==true)
		{
			holder.block.setText("Unblock");
			holder.block.setBackgroundResource(R.drawable.round_button_green);
			holder.block.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new UnblockAsync().execute(connectionsItem.getUser_id(),connectionsItem.getBlock_id());
					listData.remove(position);
					notifyDataSetChanged();
				}
			});
		}
		else
		{
			holder.block.setText("Block");
			holder.block.setBackgroundResource(R.drawable.round_button_red);

			holder.block.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new BlockAsync().execute(connectionsItem.getUser_id());
					//	if(delete_flag==true)
					//{
					listData.remove(position);
					notifyDataSetChanged();
					//}

				}
			});
		}


		return convertView;
	} 

	static class ViewHolder{
		ImageView image;
		TextView name;
		Button block;

	}

	private class BlockAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected void onPreExecute() {
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
			JSONObject jsonObjSend = new JSONObject();
			block_userID= params[0];
			try {
				jsonObjSend.put("blocked_user_id",block_userID);
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
			jsonObjRecv = HttpPostClient.sendHttpPost(Util.API+"block", jsonObjSend);
			return 1;
		}

		@SuppressLint("NewApi") @Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if(HttpPostClient.statuscode == 500){
				//Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverError), Style.ALERT).show();
			}if(HttpPostClient.statuscode == 504){
				//Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
			}
			if(HttpPostClient.statuscode == 200){
				//Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
				try {
					if(jsonObjRecv.getString("message").equalsIgnoreCase("Successfully blocked"))
					{
						Toast.makeText(prova, "Sucessfully Blocked", Toast.LENGTH_SHORT).show();
						//delete_flag=true;

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}


	}
	private class UnblockAsync extends AsyncTask<String, Void, Integer>{
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
			String blocked_user_id = params[0];
			String blockID = params[1];

			try {
				jsonObjSend.put("blocked_user_id",blocked_user_id );
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}

			jsonObjRecv_b = HttpPutClient.sendHttpPost(Util.API+"block/"+blockID, jsonObjSend);
			return null;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(HttpPostClient.statuscode == 500){
				//Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverError), Style.ALERT).show();
			}if(HttpPostClient.statuscode == 504){
				//Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
			}
			if(HttpPostClient.statuscode == 200){
				//Crouton.makeText(SigninActivity.this, getString(R.string.crouton_message_serverGatewayError), Style.ALERT).show();
				try {
					if(jsonObjRecv_b.getString("message").equalsIgnoreCase("Successfully unblock"))
					{
						Toast.makeText(prova, "Successfully unblock", Toast.LENGTH_SHORT).show();
						//delete_flag=true;

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}




