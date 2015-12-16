package dealsforsure.in.fragments;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import dealsforsure.in.ActivityPromocodeDeatil;
import dealsforsure.in.R;
import dealsforsure.in.adapters.AdapterMyDeal;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.MyDeals;
import dealsforsure.in.utils.Utils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentCompletedDeals extends Fragment {

	public FragmentCompletedDeals() {
	}

	JSONObject regjson;
	UserFunctions userFunction;
	String userId, userName, Type, userCode,deviceId,tokenKey;;
	SharedPreferences sharedPreferences;
	private RatingBar ratingBar;
	ListView lvMydeals;
	private Utils utils;
	List<MyDeals> myDealList = new ArrayList<MyDeals>();
	MyDeals myDeals;
	AdapterMyDeal adapterMyDeal;
	String comment,statRate,cid;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_completed_deals,
				container, false);

		userFunction = new UserFunctions();

		lvMydeals = (ListView) rootView.findViewById(R.id.lvmydeals);

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		userId = sharedPreferences.getString("userId", null);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);
		utils = new Utils(getActivity());

		if (utils.isNetworkAvailable()) {
			new getMydealsAsy().execute();
		} else {

			Toast.makeText(getActivity(), "Error in connection .",
					Toast.LENGTH_LONG).show();

		}
		
		adapterMyDeal = new AdapterMyDeal(getActivity(),
				R.layout.adapter_mydeal, myDealList, "valid");

		lvMydeals.setAdapter(adapterMyDeal);
		
		lvMydeals.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				myDeals = (MyDeals) parent.getItemAtPosition(position);

				// tvAddress.setText(address.getAddressName());

				Intent i = new Intent(getActivity(),
						ActivityPromocodeDeatil.class);
				i.putExtra("cid", myDeals.getCid());
				i.putExtra("promocode", myDeals.getPromoCode());
				i.putExtra("type", "deal");
				startActivity(i);

				// alertDialog.cancel();

			}

		});

		return rootView;
	}

	private class getMydealsAsy extends AsyncTask<Void, Void, Void> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}

		protected Void doInBackground(Void... unused) {
			regjson = userFunction.getMyDealDetail(userId,"used_promocode",tokenKey,deviceId);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				myDealList.clear();
				//progDailog.dismiss();
				if (regjson != null) {

					// JSONArray dataRegisterArray;

					// dataRegisterArray = regjson.getJSONArray("result");

					// JSONObject dealsObject =
					// dataRegisterArray.getJSONObject(0);
					JSONObject dealObject;
					String status = regjson.getString("status");

					if (status.equals("200")) {

						JSONArray dataRegisterArray;

						dataRegisterArray = regjson.getJSONArray("result");
						for (int i = 0; i < dataRegisterArray.length(); i++) {
							dealObject = dataRegisterArray.getJSONObject(i);
							myDeals=new MyDeals();
							
							myDeals.setDealTitle(dealObject.getString("title"));
							myDeals.setPromoCode(dealObject.getString("promo_code"));
							myDeals.setImageUrl(dealObject.getString("image"));
							myDeals.setOfferDetail(dealObject.getString("discount_type"));
							myDeals.setPurchaseDate(dealObject.getString("purchased_date"));
							myDeals.setExpDate(dealObject.getString("end_date"));
							myDeals.setCid(dealObject.getString("cid"));
							myDealList.add(myDeals);

						}

						adapterMyDeal = new AdapterMyDeal(getActivity(),
								R.layout.adapter_mydeal, myDealList,"completed");

						lvMydeals.setAdapter(adapterMyDeal);
						
						
						lvMydeals.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {

								myDeals=(MyDeals) parent.getItemAtPosition(position);
								
								cid=myDeals.getCid();

								displayAddComments();

							}

						});
						
						
						

					} else {

						/*String errormessage = regjson.getString("message");

						Toast.makeText(getActivity(), errormessage,
								Toast.LENGTH_LONG).show();*/
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	void displayAddComments() {

		ImageView ivClose;
		Button btSave;
		final EditText etComments;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.dialog_write_comments, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		
		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		ivClose = (ImageView) promptsView.findViewById(R.id.ivclose);
		btSave = (Button) promptsView.findViewById(R.id.btnsave);
		etComments = (EditText) promptsView.findViewById(R.id.etcomments);
		ratingBar = (RatingBar)promptsView. findViewById(R.id.ratingBar);
		
		btSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String comments = ((TextView) etComments).getText().toString();

				if (comments.length() == 0) {
					Toast.makeText(getActivity(), "Please enter Comments .",
							Toast.LENGTH_LONG).show();

				} else {
					comment=URLEncoder.encode(comments);
					
					statRate=String.valueOf(ratingBar.getRating());
					if (utils.isNetworkAvailable()) {
						new SaveComments().execute();
					} else {

					}
					alertDialog.cancel();

				}
				
				

				
			}
		});

		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				alertDialog.cancel();
			}
		});

	}
	
	private class SaveComments extends AsyncTask<Void, Void, Void> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = Utils.createProgressDialog(getActivity());
			progDailog.setCancelable(false);
			progDailog.show();
		}

		protected Void doInBackground(Void... unused) {
			regjson = userFunction.saveComments(cid,userId,comment,statRate,tokenKey,deviceId);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				progDailog.dismiss();

				if (regjson != null) {

					JSONArray dataRegisterArray;

					/*dataRegisterArray = regjson.getJSONArray("result");

					JSONObject dealsObject = dataRegisterArray.getJSONObject(0);*/

					String status = regjson.getString("status");

					if (status.equals("200")) {
						
						Toast.makeText(getActivity(), "Review saves successfully",
								Toast.LENGTH_LONG).show();
						
						} else {

						String errormessage = regjson.getString("message");

						Toast.makeText(getActivity(), errormessage,
								Toast.LENGTH_LONG).show();
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}


}
