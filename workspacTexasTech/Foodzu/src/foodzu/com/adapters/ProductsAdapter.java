package foodzu.com.adapters;
/*package foodzu.in.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import foodzu.in.HomeActivity;
import foodzu.in.LoginActivity;
import foodzu.in.ProductDetailsActivity;
import foodzu.in.R;
import foodzu.in.models.Products;

public class ProductsAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	// private Utils utils;
	SharedPreferences sharedPreferences;
	// private String userId, userCode, mCid, mValidUptoDate, mDealId,
	// merchantId,
	// pointforDeal;
	JSONObject promoJson;
	HashMap<String, String> item = new HashMap<String, String>();
	private Context mContext;
	// For animation transition
	int lastPosition = -1;
	int value = 0;

	// Declare object of userFunctions class
	// private UserFunctions userFunction;

	public ProductsAdapter(Activity a, ArrayList<HashMap<String, String>> d,
			Context context) {
		activity = a;
		data = d;
		this.mContext = context;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// utils = new Utils(activity);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(activity);

	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.adapter_home, parent, false);
			holder = new ViewHolder();

			
			 * holder.lblOfferDeail = (TextView) convertView
			 * .findViewById(R.id.lblofferdetail);
			 
			holder.view = (RelativeLayout) convertView
					.findViewById(R.id.wrap_rl);
			holder.lblDateEnd = (TextView) convertView
					.findViewById(R.id.lblEndDate);
			holder.lblDayResrtict = (TextView) convertView
					.findViewById(R.id.lblDayResrtict);
			holder.tvStoreName = (TextView) convertView
					.findViewById(R.id.lblstore);

			holder.imgThumbnail = (ImageView) convertView
					.findViewById(R.id.imgThumbnail);
			holder.plus = (ImageView) convertView.findViewById(R.id.plus);
			holder.minus = (ImageView) convertView.findViewById(R.id.minus);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			holder.tvAddress = (TextView) convertView
					.findViewById(R.id.lblAddress);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

//		lastPosition = position;
		item = data.get(position);

		holder.plus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				value = Integer.parseInt(holder.count.getText().toString());
				if (value >= 0) {
					holder.count.setText(Integer.toString(value + 1));
					((HomeActivity) mContext).buttombarAction("true");
				} else
					((HomeActivity) mContext).buttombarAction("false");
			}
		});

		holder.minus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				value = Integer.parseInt(holder.count.getText().toString());
				if (value > 0) {
					holder.count.setText(Integer.toString(value - 1));
					((HomeActivity) mContext).buttombarAction("true");
				} else
					((HomeActivity) mContext).buttombarAction("false");
			}
		});
		
		holder.view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(activity, ProductDetailsActivity.class);	
				item = data.get(position);
				i.putExtra("item_name", item.get("item_name"));
				i.putExtra("prod_img", "http://cdn.zunu.in/product_images/icon.jpg");
				i.putExtra("actual_price", item.get("actual_price"));
				i.putExtra("count", Integer.parseInt(holder.count.getText().toString()));
				mContext.startActivity(i);
			}
		});

		
		 * holder.lblOfferDeail.setText(item .get("final_price")+" Rs");
		 
		holder.tvStoreName.setText(item.get("item_name"));
		holder.tvAddress.setText("\u20B9  " + item.get("actual_price"));

		Picasso.with(activity)
				.load("http://cdn.zunu.in/product_images/icon.jpg").fit()
				.centerCrop().tag(activity).into(holder.imgThumbnail);
		holder.lblDateEnd.setText("  " + item.get(""));

		return convertView;
	}

	// Method to create instance of views
	static class ViewHolder {
		private ImageView imgThumbnail, plus, minus;
		private TextView lblDateEnd, lblOfferDeail, tvAddress, tvStoreName,
				lblDayResrtict, count;
		RelativeLayout view;
	}

	
	 * void displayConforim(String points) { TextView tcPromoCode, tvheader;
	 * ImageView ivClose; Button btOK, btncancel; LayoutInflater li =
	 * LayoutInflater.from(activity); View promptsView =
	 * li.inflate(R.layout.dialog_promocode_confirm, null);
	 * 
	 * AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
	 * activity); alertDialogBuilder.setCancelable(false);
	 * alertDialogBuilder.setView(promptsView); final AlertDialog alertDialog =
	 * alertDialogBuilder.create();
	 * alertDialog.getWindow().setBackgroundDrawable( new
	 * ColorDrawable(Color.argb(0, 0, 0, 0)));
	 * 
	 * alertDialog.getWindow().setBackgroundDrawable( new
	 * ColorDrawable(android.graphics.Color.TRANSPARENT)); alertDialog.show();
	 * 
	 * tcPromoCode = (TextView) promptsView.findViewById(R.id.tvpromocode);
	 * tvheader = (TextView) promptsView.findViewById(R.id.tvheader); // ivClose
	 * = (ImageView) promptsView.findViewById(R.id.ivclose); btOK = (Button)
	 * promptsView.findViewById(R.id.btnset); btncancel = (Button)
	 * promptsView.findViewById(R.id.btncancel);
	 * 
	 * tcPromoCode.setText(points +
	 * " points will be deducted to get this deal");
	 * 
	 * btOK.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { if (utils.isNetworkAvailable()) {
	 * new getPromocodeAsync().execute(); } else {
	 * 
	 * Toast.makeText(activity, "Error in connection .",
	 * Toast.LENGTH_LONG).show();
	 * 
	 * } alertDialog.cancel(); } });
	 * 
	 * btncancel.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * alertDialog.cancel(); } });
	 * 
	 * }
	 * 
	 * public class getPromocodeAsync extends AsyncTask<Void, Void, Void> {
	 * 
	 * ProgressDialog progress;
	 * 
	 * @Override protected void onPreExecute() {
	 * 
	 * // Show progress dialog when fetching data from database progress =
	 * ProgressDialog.show(activity, "",
	 * activity.getString(R.string.loading_data), true);
	 * 
	 * }
	 * 
	 * @Override protected Void doInBackground(Void... params) {
	 * 
	 * // Method to get Data from Server // getDataFromServer();
	 * 
	 * promoJson = userFunction.getPromoCode(mCid, userId, userCode,
	 * mValidUptoDate, mDealId, merchantId, "list");
	 * 
	 * return null; }
	 * 
	 * @Override protected void onPostExecute(Void result) { try { if (promoJson
	 * != null) {
	 * 
	 * String status = promoJson.getString("status");
	 * 
	 * if (status.equals("200")) {
	 * 
	 * String promocode = promoJson.getString("promo_code"); String totalpoints
	 * = promoJson .getString("total_points");
	 * 
	 * sharedPreferences = PreferenceManager
	 * .getDefaultSharedPreferences(activity); SharedPreferences.Editor editor =
	 * sharedPreferences .edit(); editor.putString("userPoint", totalpoints);
	 * editor.commit();
	 * 
	 * if (activity instanceof ActivityHome) { ((ActivityHome)
	 * activity).reloadMenu(); ; }
	 * 
	 * displaypromocode(promocode);
	 * 
	 * }
	 * 
	 * } } catch (Exception e) {
	 * 
	 * }
	 * 
	 * if (progress.isShowing()) { progress.dismiss(); }
	 * 
	 * }
	 * 
	 * }
	 

	
	 * void displaypromocode(String promoCode) { TextView tcPromoCode; ImageView
	 * ivClose; Button btOK; LayoutInflater li = LayoutInflater.from(activity);
	 * View promptsView = li.inflate(R.layout.dialog_getpromocode, null);
	 * 
	 * AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
	 * activity); alertDialogBuilder.setCancelable(false);
	 * alertDialogBuilder.setView(promptsView); final AlertDialog alertDialog =
	 * alertDialogBuilder.create();
	 * alertDialog.getWindow().setBackgroundDrawable( new
	 * ColorDrawable(Color.argb(0, 0, 0, 0)));
	 * 
	 * alertDialog.getWindow().setBackgroundDrawable( new
	 * ColorDrawable(android.graphics.Color.TRANSPARENT)); alertDialog.show();
	 * 
	 * tcPromoCode = (TextView) promptsView.findViewById(R.id.tvpromocode);
	 * ivClose = (ImageView) promptsView.findViewById(R.id.ivclose); btOK =
	 * (Button) promptsView.findViewById(R.id.btnsave);
	 * tcPromoCode.setText(promoCode);
	 * 
	 * btOK.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * alertDialog.cancel(); } });
	 * 
	 * ivClose.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * alertDialog.cancel(); } });
	 * 
	 * }
	 

}*/