package dealsforsure.in.adapters;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import dealsforsure.in.R;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.Store;
import dealsforsure.in.utils.Utils;



public class AdapterStore extends ArrayAdapter<Store> {
	
	Context context;
	List<Store> storeList;
	ViewHolder holder = null;
	Store store;
	Typeface face;
	Integer selectPosition;
	private JSONObject json;
	private UserFunctions userFunction;
	private Utils utils;
	String deviceId,tokenKey;
	SharedPreferences sharedPreferences;
	
	public AdapterStore(Context context, int resourceId,
			List<Store> storeList) {
		super(context, resourceId, storeList);
		this.context = context;
		this.storeList = storeList;
		userFunction = new UserFunctions();
		utils = new Utils(context);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);
		//imageLoader = new ImageLoader(context);

	

	}

	/* private view holder class */
	private class ViewHolder {
		TextView  tvStoreName;
		ImageView ivdelete;

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		store = storeList.get(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_store, null);

			holder = new ViewHolder();
			holder.tvStoreName = (TextView) convertView
					.findViewById(R.id.tvStore);
			holder.ivdelete= (ImageView) convertView
					.findViewById(R.id.ivdelete);
			
			convertView.setTag(holder);
		} else
			
		holder = (ViewHolder) convertView.getTag();
			
		holder.ivdelete.setId(position);
		
		holder.ivdelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectPosition=v.getId();
				store=storeList.get(selectPosition);
				displayConforim();
				
			}
		});
			
		holder = (ViewHolder) convertView.getTag();
		
		holder.tvStoreName.setTypeface(face);

		holder.tvStoreName.setText(store.getStoreName());
		
		
		return convertView;
	}
	
	void displayConforim() {
		TextView tcPromoCode, tvheader;
		ImageView ivClose;
		Button btOK, btncancel;
		String message;
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.dialog_promocode_confirm, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		tcPromoCode = (TextView) promptsView.findViewById(R.id.tvpromocode);
		tvheader = (TextView) promptsView.findViewById(R.id.tvheader);
		// ivClose = (ImageView) promptsView.findViewById(R.id.ivclose);
		btOK = (Button) promptsView.findViewById(R.id.btnset);
		btncancel = (Button) promptsView.findViewById(R.id.btncancel);
		
		
			message="Do you want to delete this store";
	
		tcPromoCode.setText(message);

		btOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				//notifyDataSetChanged();
				
				 if(utils.isNetworkAvailable()) {
					new deleteStoresync().execute();
				} else {

					Toast.makeText(context, "Error in connection .",
							Toast.LENGTH_LONG).show();

				}
				
				 
				alertDialog.cancel();
			}
		});

		btncancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				
				alertDialog.cancel();
			}
		});

	}
	
	public class deleteStoresync extends AsyncTask<Void, Void, Void> {

		ProgressDialog pDialog;

		@Override
        protected void onPreExecute() {
        	
        	pDialog = Utils.createProgressDialog(context);
    		pDialog.setCancelable(false);
    		pDialog.show();

        }

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			// Method to get Data from Server
			// getDataFromServer();

			json = userFunction.deleteStore(store.getStoreId(),deviceId,tokenKey);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			try {
				if (json != null) {
					if(pDialog.isShowing()) pDialog.dismiss();

					String status = json.getString("status");

					if (status.equals("200")) {
						
						//store=storeList.get(selectPosition);
						int i=selectPosition;
						storeList.remove(i);
						notifyDataSetChanged();
						Toast.makeText(context, "store deleted successfully",
								Toast.LENGTH_LONG).show();

					} else {

						String errormessage = json.getString("message");

						Toast.makeText(context, errormessage,
								Toast.LENGTH_LONG).show();
					}

				}
			} catch (Exception e) {

			}

			

		}

	}


}
