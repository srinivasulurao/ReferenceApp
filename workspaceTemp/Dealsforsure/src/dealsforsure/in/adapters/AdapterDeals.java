package dealsforsure.in.adapters;

import java.util.List;

import org.json.JSONObject;

import dealsforsure.in.ActivityAddDeals;
import dealsforsure.in.ActivityDetail;
import dealsforsure.in.R;
import dealsforsure.in.ActivityDetail.getPromocodeAsync;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.Deals;
import dealsforsure.in.model.Store;
import dealsforsure.in.utils.Utils;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AdapterDeals extends ArrayAdapter<Deals> {

	Context context;
	List<Deals> dealsList;
	ViewHolder holder = null;
	Deals deals;
	Typeface face;
	Integer selectPosition;
	private JSONObject json;
	private UserFunctions userFunction;
	private Utils utils;
	Boolean isShowDisplay = false;
	String deviceId, tokenKey;
	SharedPreferences sharedPreferences;

	String cId, updateStatus;

	public AdapterDeals(Context context, int resourceId, List<Deals> dealsList) {
		super(context, resourceId, dealsList);
		this.context = context;
		this.dealsList = dealsList;
		userFunction = new UserFunctions();
		utils = new Utils(context);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);
		// imageLoader = new ImageLoader(context);

	}

	/* private view holder class */
	private class ViewHolder {
		TextView tvDealName;
		Switch swstatus;
		ImageView ivdelete;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		deals = dealsList.get(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_deal, null);

			holder = new ViewHolder();
			holder.tvDealName = (TextView) convertView
					.findViewById(R.id.tvdealname);

			holder.swstatus = (Switch) convertView.findViewById(R.id.swstatus);
			holder.ivdelete = (ImageView) convertView
					.findViewById(R.id.ivdelete);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.tvDealName.setTypeface(face);

		holder.tvDealName.setText(deals.getDealsName());

		if (deals.getStatus().equals("0")) {

			holder.swstatus.setChecked(true);
		} else {
			holder.swstatus.setChecked(false);
		}
		holder.swstatus.setId(position);

		holder.ivdelete.setId(position);

		holder.ivdelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectPosition = v.getId();

				deals = dealsList.get(selectPosition);
				cId = deals.getCid();
				displayConforim(null, "delete");

			}
		});

		holder.swstatus.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (!isShowDisplay) {

					selectPosition = v.getId();
					deals = dealsList.get(selectPosition);
					isShowDisplay = true;
					cId = deals.getCid();
					displayConforim(deals.getStatus(), "status");
				}
				return false;

			}

		});

		return convertView;
	}

	void displayConforim(final String status, final String type) {
		TextView tcPromoCode, tvheader;
		ImageView ivClose;
		Button btOK, btncancel;
		String message;
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.dialog_promocode_confirm, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
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
		if (type.equals("delete")) {

			message = "Do you want to delete this deal";

		} else {

			if (status.equals("0")) {
				message = "Do you want to disable this deal";
			} else {
				message = "Do you want to enable this deal";
			}
		}

		tcPromoCode.setText(message);

		btOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (type.equals("delete")) {

					if (utils.isNetworkAvailable()) {
						new deleteDealsync().execute();
					} else {

						Toast.makeText(context, "Error in connection .",
								Toast.LENGTH_LONG).show();

					}

				} else {

					if (status.equals("0")) {

						deals.setStatus("1");

						updateStatus = "1";

					} else {
						deals.setStatus("0");
						updateStatus = "0";

					}
					dealsList.set(selectPosition, deals);
					if (utils.isNetworkAvailable()) {
						new saveStatusChangeAsync().execute();
					} else {

						Toast.makeText(context, "Error in connection .",
								Toast.LENGTH_LONG).show();

					}

					isShowDisplay = false;
				}
				notifyDataSetChanged();

				alertDialog.cancel();
			}
		});

		btncancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				notifyDataSetChanged();
				isShowDisplay = false;
				alertDialog.cancel();
			}
		});

	}

	public class saveStatusChangeAsync extends AsyncTask<Void, Void, Void> {

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

			json = userFunction.saveStatusChange(updateStatus, cId, deviceId,
					tokenKey);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			try {
				if (json != null) {

					String status = json.getString("status");

					if (status.equals("200")) {

						Toast.makeText(context, "Status Changed successfully",
								Toast.LENGTH_LONG).show();

					} else {

						String errormessage = json.getString("message");

						Toast.makeText(context, errormessage, Toast.LENGTH_LONG)
								.show();
					}

				}
			} catch (Exception e) {

			}

			if (pDialog.isShowing())
				pDialog.dismiss();
		}

	}

	public class deleteDealsync extends AsyncTask<Void, Void, Void> {

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

			json = userFunction.deleteDeal("1", cId, deviceId, tokenKey);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			try {
				if (pDialog.isShowing())
					pDialog.dismiss();
				if (json != null) {

					String status = json.getString("status");

					if (status.equals("200")) {

						// store=storeList.get(selectPosition);
						int i = selectPosition;
						dealsList.remove(i);
						notifyDataSetChanged();
						Toast.makeText(context, "deal deleted successfully",
								Toast.LENGTH_LONG).show();

					} else {

						String errormessage = json.getString("message");

						Toast.makeText(context, errormessage, Toast.LENGTH_LONG)
								.show();
					}

				}
			} catch (Exception e) {

			}

		}

	}

}
