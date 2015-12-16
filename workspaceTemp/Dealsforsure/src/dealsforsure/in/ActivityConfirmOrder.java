package dealsforsure.in;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import dealsforsure.in.ActivityAddDeals.CategorySelectedListener;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.Address;
import dealsforsure.in.model.Category;
import dealsforsure.in.utils.Utils;

public class ActivityConfirmOrder extends ActionBarActivity {
	
	Address address;
	 Utils utils;
	 JSONObject regjson;
	 UserFunctions userFunction;
	String tokenKey,deviceId,cid;
	String userId;
	SharedPreferences sharedPreferences;
	Spinner spquantity;
	String price;
	ImageView ivAddressEdit;
	RelativeLayout rlBody;
	Boolean isEditAddress=false;
	private ActionBar actionbar;
	Integer selectQuantity,totalAmout;
	
	Button btConfirmOrder;
	
	TextView tvfullname,tvaddress,tvordertotal;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_order);
		
		utils = new Utils(ActivityConfirmOrder.this);
		userFunction = new UserFunctions();

		tvfullname = (TextView) findViewById(R.id.tvfullname);
		tvaddress = (TextView) findViewById(R.id.tvaddress);
		tvordertotal= (TextView) findViewById(R.id.tvordertotal);
		ivAddressEdit=(ImageView)findViewById(R.id.ivedit);
		rlBody=(RelativeLayout)findViewById(R.id.rlbody);
		btConfirmOrder=(Button)findViewById(R.id.btconfirmorder);
		rlBody.setVisibility(View.GONE);
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		Bundle b = getIntent().getExtras();
		price = b.getString("dealCost");
		cid= b.getString("cid");

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityConfirmOrder.this);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);
		userId = sharedPreferences.getString("userId", null);

		if (utils.isNetworkAvailable()) {
			new getAddress().execute();
		} else {
			Toast.makeText(getBaseContext(), "Error in connection .",
					Toast.LENGTH_LONG).show();
		}
		tvordertotal.setText(price+" Coins");
		spquantity=(Spinner) findViewById(R.id.spquantity);
		Integer[] items = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, items);
		spquantity.setAdapter(adapter);
		
		spquantity
		.setOnItemSelectedListener(new quantitySelectedListener());
		
		ivAddressEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				isEditAddress=true;
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				DialogFragmentAddress frag = new DialogFragmentAddress();
				frag.show(ft, "txn_tag");
				
			}
		});
		
		btConfirmOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				if (utils.isNetworkAvailable()) {
					new saveOrder().execute();
				} else {
					Toast.makeText(getBaseContext(), "Error in connection .",
							Toast.LENGTH_LONG).show();
				}
				
				
			}
		});
	
		/*FragmentTransaction ft = getFragmentManager()
				.beginTransaction();
		DialogFragmentAddress frag = new DialogFragmentAddress();
		frag.show(ft, "txn_tag");*/

	}
	public class quantitySelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {

			selectQuantity = (Integer) spquantity.getSelectedItem();

			totalAmout = selectQuantity * new Integer(price);

			tvordertotal.setText(totalAmout + " Coins");

			//categoryId = category.getCategoryId();

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	}
	
	public void loadAddress(Address address){
		
		tvfullname.setText(address.getFullName());
		
	StringBuffer strBuff=new StringBuffer();	
	strBuff.append(address.getAddress1() + "\n");
	if(address.getAddress2()!=null&&address.getAddress2().length()>0){
		strBuff.append(address.getAddress2() + "\n");
		
	}
	
	strBuff.append(address.getCity() + "\n" + address.getState() + "\n"
			+ address.getPinCode() + "\n" + address.getPhoneNumber());
		
		tvaddress.setText(strBuff.toString());
		
		
	}

	public class DialogFragmentAddress extends DialogFragment {

		Button btsave,btcancel;
		EditText etFullName,etAddress1,etAddress2,etPinCode,etCity,etState,etPhoneNumber;
		String fullName,address1,address2,pinCode,city,state,phoneNumber;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setStyle(DialogFragment.STYLE_NORMAL, R.style.Full_Screen);
		}

		@Override
		public void onStart() {
			super.onStart();
			Dialog d = getDialog();
			if (d != null) {
				int width = ViewGroup.LayoutParams.MATCH_PARENT;
				int height = ViewGroup.LayoutParams.MATCH_PARENT;
				d.getWindow().setLayout(width, height);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate(R.layout.dialog_address, container,
					false);
			
			btsave = (Button) root.findViewById(R.id.btnsave);
			btcancel = (Button) root.findViewById(R.id.btncancel);
			etFullName = (EditText) root.findViewById(R.id.etfullname);
			etAddress1 = (EditText) root.findViewById(R.id.etaddress1);
			etAddress2 = (EditText) root.findViewById(R.id.etaddress2);
			etPinCode = (EditText) root.findViewById(R.id.etpincode);
			etCity = (EditText) root.findViewById(R.id.etcity);
			etState = (EditText) root.findViewById(R.id.etstate);
			etPhoneNumber = (EditText) root.findViewById(R.id.etphonenumber);
			
			if(isEditAddress){
				
				etFullName.setText(address.getFullName());	
				etAddress1.setText(address.getAddress1());
				etAddress2.setText(address.getAddress2());
				etPinCode.setText(address.getPinCode());
				etCity.setText(address.getCity());
				etState.setText(address.getState());
				etPhoneNumber.setText(address.getPhoneNumber());
				
			}
			
			btsave.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					fullName= ((TextView) etFullName).getText().toString().trim();
					address1= ((TextView) etAddress1).getText().toString().trim();
					address2= ((TextView) etAddress2).getText().toString().trim();
					pinCode= ((TextView) etPinCode).getText().toString().trim();
					city= ((TextView) etCity).getText().toString().trim();
					state= ((TextView) etState).getText().toString().trim();
					phoneNumber= ((TextView) etPhoneNumber).getText().toString().trim();
					
					

					if (fullName==null||fullName.length() == 0) {
						Toast.makeText(getBaseContext(),
								"Please enter Full Name .", Toast.LENGTH_LONG)
								.show();

					}else if(address1==null||address1.length() == 0){
						
						Toast.makeText(getBaseContext(),
								"Please enter Address .", Toast.LENGTH_LONG)
								.show();
						
					}else if(pinCode==null||pinCode.length() == 0){
						
						Toast.makeText(getBaseContext(),
								"Please enter Pin Code .", Toast.LENGTH_LONG)
								.show();
						
					}else if(city==null||city.length() == 0){
						
						Toast.makeText(getBaseContext(),
								"Please enter City .", Toast.LENGTH_LONG)
								.show();
						
					}else if(state==null||state.length() == 0){
						
						Toast.makeText(getBaseContext(),
								"Please enter State .", Toast.LENGTH_LONG)
								.show();
						
					}else if(phoneNumber==null||phoneNumber.length() == 0){
						
						Toast.makeText(getBaseContext(),
								"Please enter Phone Number .", Toast.LENGTH_LONG)
								.show();
						
					}else{
					
					address=new Address();
					address.setFullName(URLEncoder.encode(fullName));
					address.setAddress1(URLEncoder.encode(address1));
					address.setAddress2(URLEncoder.encode(address2));
					address.setPinCode(pinCode);
					address.setCity(URLEncoder.encode(city));
					address.setState(URLEncoder.encode(state));
					address.setPhoneNumber(phoneNumber);
					address.setUserId(userId);
					
					DialogFragmentAddress.this.dismiss();
					
						if (utils.isNetworkAvailable()) {
							new SaveAddress().execute();
						} else {
							Toast.makeText(getBaseContext(),
									"Error in connection .", Toast.LENGTH_LONG)
									.show();
						}
					}
					
					
				}
					
					
				});
			btcancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					DialogFragmentAddress.this.dismiss();
				}
			});

			return root;
		}
	}
	
	private class SaveAddress extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = Utils.createProgressDialog(ActivityConfirmOrder.this);
    		pDialog.setCancelable(false);
    		pDialog.show();
		}

		protected Void doInBackground(Void... unused) {
			regjson = userFunction.saveAddress(address,deviceId,tokenKey);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				if(pDialog.isShowing()) pDialog.dismiss();

				if (regjson != null) {

					JSONArray dataRegisterArray;

					String status = regjson.getString("status");

					if (status.equals("200")) {
						
						//DialogFragmentAddress.dismiss();
						
						address.setFullName(regjson.getString("name"));
						 address.setAddress1(regjson.getString("address1"));
						 address.setAddress2(regjson.getString("address2"));
						 address.setCity(regjson.getString("city"));
						 address.setPhoneNumber(regjson.getString("phone"));
						 address.setPinCode(regjson.getString("pincode"));
						 address.setState(regjson.getString("state"));
						 address.setAddressId(regjson.getString("add_id"));
						 rlBody.setVisibility(View.VISIBLE);
						 loadAddress(address);


					} else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityConfirmOrder.this, errormessage,
								Toast.LENGTH_LONG).show();
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	private class getAddress extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = Utils.createProgressDialog(ActivityConfirmOrder.this);
    		pDialog.setCancelable(false);
    		pDialog.show();
		}

		protected Void doInBackground(Void... unused) {
			regjson = userFunction.getAddress(userId,deviceId,tokenKey);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				if(pDialog.isShowing()) pDialog.dismiss();

				if (regjson != null) {

					JSONArray dataRegisterArray;

					String status = regjson.getString("status");

					if (status.equals("200")) {
						
					
						
						address=new Address();
						
						 JSONObject resultjson=regjson.getJSONObject("result");
						 
							String addressPresent = resultjson.getString("content");
							if(addressPresent.equals("1")){
						 
						 address.setFullName(resultjson.getString("name"));
						 address.setAddress1(resultjson.getString("address1"));
						 address.setAddress2(resultjson.getString("address2"));
						 address.setCity(resultjson.getString("city"));
						 address.setPhoneNumber(resultjson.getString("phone"));
						 address.setPinCode(resultjson.getString("pincode"));
						 address.setState(resultjson.getString("state"));
						 address.setAddressId(resultjson.getString("add_id"));
							rlBody.setVisibility(View.VISIBLE);
						 loadAddress(address);
						}else{
							
							FragmentTransaction ft = getFragmentManager()
									.beginTransaction();
							DialogFragmentAddress frag = new DialogFragmentAddress();
							frag.show(ft, "txn_tag");
							
							
						}
						
						//DialogFragmentAddress.dismiss();


					} else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityConfirmOrder.this, errormessage,
								Toast.LENGTH_LONG).show();
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	private class saveOrder extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = Utils.createProgressDialog(ActivityConfirmOrder.this);
    		pDialog.setCancelable(false);
    		pDialog.show();
		}

		protected Void doInBackground(Void... unused) {
			regjson = userFunction.saveOrders(cid,address.getAddressId(),selectQuantity.toString(),totalAmout.toString(),userId,deviceId,tokenKey);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				if(pDialog.isShowing()) pDialog.dismiss();

				if (regjson != null) {

					JSONArray dataRegisterArray;

					String status = regjson.getString("status");

					if (status.equals("200")) {
						
						String totalpoints = regjson
								.getString("user_points");

						sharedPreferences = PreferenceManager
								.getDefaultSharedPreferences(ActivityConfirmOrder.this);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("userPoint", totalpoints);
						editor.commit();
						
						Toast.makeText(ActivityConfirmOrder.this, "Your order placed successfully",
								Toast.LENGTH_LONG).show();
						
						finish();
						
					}else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityConfirmOrder.this, errormessage,
								Toast.LENGTH_LONG).show();
					}
				}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Previous page or exit
			finish();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	}

