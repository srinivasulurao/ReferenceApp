package dealsforsure.in.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import dealsforsure.in.ActivityDetail;
import dealsforsure.in.ActivityDirection;
import dealsforsure.in.ActivityHome;
import dealsforsure.in.ActivityRegistration;
import dealsforsure.in.R;
import dealsforsure.in.ActivityDetail.getPromocodeAsync;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.utils.Utils;

public class AdapterHome extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	private Utils utils;
	SharedPreferences sharedPreferences;
	private String userId, userCode, mCid, mValidUptoDate, mDealId, merchantId,
			pointforDeal;
	JSONObject promoJson;
	HashMap<String, String> item = new HashMap<String, String>();

	// For animation transition
	private int lastPosition = -1;

	// Declare object of userFunctions class
	private UserFunctions userFunction;

	public AdapterHome(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		utils = new Utils(activity);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(activity);

	}

	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;

		// Declare object of UserFuntion class
		userFunction = new UserFunctions();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.adapter_home, null);
			holder = new ViewHolder();

			/*holder.lblTitle = (TextView) convertView
					.findViewById(R.id.lblTitle);*/
		//	holder.tvGet = (TextView) convertView.findViewById(R.id.tvGet);
			// holder.lblStartValue = (TextView)
			// convertView.findViewById(R.id.lblStartValue);
			// holder.lblAfterDiscValue= (TextView)
			// convertView.findViewById(R.id.lblAfterDiscountValue);
			holder.lblOfferDeail = (TextView) convertView
					.findViewById(R.id.lblofferdetail);
			
			/*holder.tvgetcount= (TextView) convertView
					.findViewById(R.id.tvgetcount);
			holder.tvviewcount= (TextView) convertView
					.findViewById(R.id.tvviewcount);
			holder.tvbuycount= (TextView) convertView
					.findViewById(R.id.tvbuycount);
			holder.llstarrate = (LinearLayout) convertView
					.findViewById(R.id.llstarrate);*/
            holder.lblDateEnd = (TextView) convertView
					.findViewById(R.id.lblEndDate);
            holder.lblDayResrtict= (TextView) convertView
					.findViewById(R.id.lblDayResrtict);
			holder.tvStoreName = (TextView) convertView.findViewById(R.id.lblstore);

			holder.imgThumbnail = (ImageView) convertView
					.findViewById(R.id.imgThumbnail);
			/*holder.icMarker = (ImageView) convertView
					.findViewById(R.id.icMarker);*/
			holder.tvAddress = (TextView) convertView
					.findViewById(R.id.lblAddress);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Animation transition
		/*
		 * Animation animation = AnimationUtils.loadAnimation(activity,
		 * (position > lastPosition) ? R.anim.up_from_bottom :
		 * R.anim.down_from_top); convertView.startAnimation(animation);
		 */
		lastPosition = position;

		// Connect views object and views id on xml

		//holder.icMarker.setId(position);
		//holder.tvGet.setId(position);

		item = data.get(position);

		/*holder.icMarker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				item = data.get(v.getId());
				// Double mDblLatitude=Double.parseDouble(p);
				// Double mDblLongitude;

				Intent i = new Intent(activity, ActivityDirection.class);
				i.putExtra(utils.EXTRA_DEST_LAT,
						new Double(item.get(userFunction.key_latitudet)));
				i.putExtra(utils.EXTRA_DEST_LNG,
						new Double(item.get(userFunction.key_longitude)));
				i.putExtra(utils.EXTRA_CATEGORY_MARKER,
						item.get(userFunction.key_category_marker));
				activity.startActivity(i);

			}
		});*/

		/*holder.tvGet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				item = data.get(v.getId());

				userId = sharedPreferences.getString("userId", null);
				userCode = sharedPreferences.getString("userCode", null);

				if (userId != null) {

					mCid = item.get(userFunction.key_c_id);
					mValidUptoDate = item.get(userFunction.key_valid_date);
					mDealId = item.get(userFunction.key_deals_id);
					merchantId = item.get(userFunction.key_merchant_id);
					pointforDeal = item.get(userFunction.key_deal_point);
					displayConforim(pointforDeal);
				} else {

					Intent i = new Intent();
					i.setClass(activity, ActivityRegistration.class);
					i.putExtra("type", "promo");
					activity.startActivity(i);
				}

			}
		});*/

		// Set data to textview and imageview
		// Set image with picasso
		Picasso.with(activity)
				.load(userFunction.URLAdmin + userFunction.folderAdmin
						+ item.get(userFunction.key_deals_image)).fit()
				.centerCrop().tag(activity).into(holder.imgThumbnail);

		// Set marker with picasso
		/*Picasso.with(activity)
				.load(userFunction.URLAdmin + userFunction.folderAdmin
						+ item.get(userFunction.key_category_marker)).fit()
				.centerCrop().tag(activity).into(holder.icMarker);
*/
		//holder.lblTitle.setText(item.get(userFunction.key_deals_title));
		holder.lblOfferDeail.setText(item
				.get(userFunction.key_deals_Offer_detail));
		holder.tvStoreName .setText(item
				.get(userFunction.key_store_name));
		holder.tvAddress.setText(item
				.get(userFunction.key_address));
		
		 holder.lblDayResrtict.setText(item
					.get(userFunction.key_day_restrict));
		/*
		holder.tvviewcount.setText(item
				.get(userFunction.key_view_count));
		
		holder.tvgetcount.setText(item
				.get(userFunction.key_get_count));
		
		holder.tvbuycount.setText(item
				.get(userFunction.key_buy_count));
		
		holder.llstarrate.removeAllViews();*/

		/*Integer countStar = new Integer(item
				.get(userFunction.key_starCount));

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				20, 20);

		for (Integer i = 0; i < countStar; i++) {

			ImageView myImage = new ImageView(activity);
			myImage.setLayoutParams(layoutParams);
			myImage.setImageResource(R.drawable.star);
			holder.llstarrate.addView(myImage);

		}
*/
		
		// holder.lblAfterDiscValue.setText(item.get(userFunction.key_deals_after_disc_value)+Utils.mCurrency+" / ");
		holder.lblDateEnd.setText("  "
				+ item.get(userFunction.key_deals_date_end));
		// holder.lblStartValue.setPaintFlags(holder.lblStartValue.getPaintFlags()
		// | Paint.STRIKE_THRU_TEXT_FLAG);
		return convertView;
	}

	// Method to create instance of views
	static class ViewHolder {
		private ImageView imgThumbnail;
		private TextView  lblDateEnd, lblOfferDeail, tvAddress,tvStoreName,lblDayResrtict;
	

	}

	void displayConforim(String points) {
		TextView tcPromoCode, tvheader;
		ImageView ivClose;
		Button btOK, btncancel;
		LayoutInflater li = LayoutInflater.from(activity);
		View promptsView = li.inflate(R.layout.dialog_promocode_confirm, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				activity);
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
		
		tcPromoCode.setText(points + " points will be deducted to get this deal");

		btOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (utils.isNetworkAvailable()) {
					new getPromocodeAsync().execute();
				} else {

					Toast.makeText(activity, "Error in connection .",
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

	public class getPromocodeAsync extends AsyncTask<Void, Void, Void> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			// Show progress dialog when fetching data from database
			progress = ProgressDialog.show(activity, "",
					activity.getString(R.string.loading_data), true);

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			// Method to get Data from Server
			// getDataFromServer();

			/*promoJson = userFunction.getPromoCode(mCid, userId, userCode,
					mValidUptoDate, mDealId, merchantId, "list");*/

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			try {
				if (promoJson != null) {

					String status = promoJson.getString("status");

					if (status.equals("200")) {

						String promocode = promoJson.getString("promo_code");
						String totalpoints = promoJson
								.getString("total_points");

						sharedPreferences = PreferenceManager
								.getDefaultSharedPreferences(activity);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("userPoint", totalpoints);
						editor.commit();

						if (activity instanceof ActivityHome) {
							((ActivityHome) activity).reloadMenu();
							;
						}

						displaypromocode(promocode);

					}

				}
			} catch (Exception e) {

			}

			if (progress.isShowing()) {
				progress.dismiss();
			}

		}

	}

	void displaypromocode(String promoCode) {
		TextView tcPromoCode;
		ImageView ivClose;
		Button btOK;
		LayoutInflater li = LayoutInflater.from(activity);
		View promptsView = li.inflate(R.layout.dialog_getpromocode, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				activity);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		tcPromoCode = (TextView) promptsView.findViewById(R.id.tvpromocode);
		ivClose = (ImageView) promptsView.findViewById(R.id.ivclose);
		btOK = (Button) promptsView.findViewById(R.id.btnsave);
		tcPromoCode.setText(promoCode);

		btOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				alertDialog.cancel();
			}
		});

		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				alertDialog.cancel();
			}
		});

	}

}