package dealsforsure.in.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import dealsforsure.in.ActivityPromocodeDeatil;
import dealsforsure.in.R;
import dealsforsure.in.adapters.AdapterMyDeal;
import dealsforsure.in.adapters.AdapterOrder;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.MyDeals;
import dealsforsure.in.utils.Utils;

public class FragmentOrder extends Fragment {
	
	public FragmentOrder(){}
	
	JSONObject regjson;
	UserFunctions userFunction;
	String userId, userName, Type,userCode,deviceId,tokenKey;
	SharedPreferences sharedPreferences;

	ListView lvMydeals;
	private Utils utils;
	List<MyDeals> myDealList=new ArrayList<MyDeals>();
	MyDeals myDeals;
	AdapterOrder adapterOrder;

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_expired_deals, container, false);
      
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
		
		adapterOrder = new AdapterOrder(getActivity(),
				R.layout.adapter_mydeal, myDealList);

		lvMydeals.setAdapter(adapterOrder);
		
		lvMydeals.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				myDeals = (MyDeals) parent.getItemAtPosition(position);

				// tvAddress.setText(address.getAddressName());

				Intent i = new Intent(getActivity(),
						ActivityPromocodeDeatil.class);
				i.putExtra("type", "order");
				i.putExtra("oid", myDeals.getOid());
				//i.putExtra("promocode", myDeals.getPromoCode());

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
			regjson = userFunction.getOrderList(userId,tokenKey,deviceId);

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
							
							myDeals.setImageUrl(dealObject.getString("image"));
							myDeals.setOfferDetail(dealObject.getString("discount_type"));
							myDeals.setPurchaseDate(dealObject.getString("order_date"));
							myDeals.setAmount(dealObject.getString("amount"));
							myDeals.setCid(dealObject.getString("cid"));
							myDeals.setOid(dealObject.getString("oid"));
							myDealList.add(myDeals);

						}

						adapterOrder.notifyDataSetChanged();
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

}
