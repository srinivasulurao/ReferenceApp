package dealsforsure.in;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import dealsforsure.in.ActivityAddDeals.MyDialogFragment;
import dealsforsure.in.adapters.AdapterCustomerDetail;
import dealsforsure.in.adapters.AdapterDeals;
import dealsforsure.in.adapters.AdapterDropDownCategory;
import dealsforsure.in.adapters.AdapterDroupDownStore;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.Category;
import dealsforsure.in.model.CustomerDetail;
import dealsforsure.in.model.Deals;
import dealsforsure.in.model.Store;
import dealsforsure.in.utils.Utils;

public class ActivityCustomerDetail extends ActionBarActivity {
	
	private ActionBar actionbar;
	private UserFunctions userFunction;
	private Utils utils;

	ListView lvCustomerDetail;
	String deviceId, tokenKey, userId, customerId;
	SharedPreferences sharedPreferences;
	AdapterCustomerDetail adapterCustomerDeatl;
	List<CustomerDetail> customerList = new ArrayList<CustomerDetail>();
	CustomerDetail customerDetail;
	JSONObject regjson;
	TextView tvCreatedeal;
	ImageView ivDealsImage, ivstartCalenderPicker, ivendCalenderPicker,
			ivDealsImage2, ivDealsImage3, ivDealsImage4;
	TextView tvStartDate, tvendDate, tvSelectOffer;
	static public final int RESULT_LOAD_IMAGE = 267;
	List<Store> storeList = new ArrayList<Store>();
	private Spinner spStore;
	private JSONObject json;
	Deals deals;
	Integer imageCount = 0;
	String  storeId, dealImagePath, dealImagePath2, dealImagePath3,
			dealImagePath4;
	
	

	int pos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actvity_customer_datail);
		lvCustomerDetail=(ListView)findViewById(R.id.lvcustomerdetail);
		tvCreatedeal=(TextView)findViewById(R.id.tvcreatedeal);
		
		
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		userFunction = new UserFunctions();
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityCustomerDetail.this);
		userId = sharedPreferences.getString("userId", null);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);
		Bundle b = getIntent().getExtras();
		customerId = b.getString("customerId");
		utils = new Utils(ActivityCustomerDetail.this);
		
		
		
		
		
		adapterCustomerDeatl = new AdapterCustomerDetail(ActivityCustomerDetail.this,
				R.layout.adapter_mydeal, customerList);

		lvCustomerDetail.setAdapter(adapterCustomerDeatl);
		
		tvCreatedeal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				MyDialogFragment frag = new MyDialogFragment();
				frag.show(ft, "txn_tag");
				
			}
		});
		
		
		if (utils.isNetworkAvailable()) {
			new getCustomerDetailList().execute();
		} else {

			Toast.makeText(getBaseContext(), "Error in connection .",
					Toast.LENGTH_LONG).show();

		}
	}
	
	
	private class getCustomerDetailList extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;
		
		 @Override
	        protected void onPreExecute() {
	        	
	        	pDialog = Utils.createProgressDialog(ActivityCustomerDetail.this);
	    		pDialog.setCancelable(false);
	    		pDialog.show();

	        }


		protected Void doInBackground(Void... unused) {
			regjson = userFunction.getCustomerDetailList(userId,customerId,deviceId,tokenKey);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				// progDailog.dismiss();
				if(pDialog.isShowing()) pDialog.dismiss();
				customerList.clear();
				if (regjson != null) {

					JSONObject custObject;

					String status = regjson.getString("status");

					if (status.equals("200")) {

						JSONArray dataRegisterArray;

						dataRegisterArray = regjson.getJSONArray("result");
						for (int i = 0; i < dataRegisterArray.length(); i++) {

							custObject = dataRegisterArray.getJSONObject(i);

							customerDetail = new CustomerDetail();
							customerDetail.setStoreName(custObject.getString("store_name"));
							customerDetail.setDate(custObject.getString("used_date"));
							customerDetail.setAmount(custObject.getString("amount"));
							
							customerList.add(customerDetail);

						}

						adapterCustomerDeatl.notifyDataSetChanged();

						// storeList

					} else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityCustomerDetail.this, errormessage,
								Toast.LENGTH_LONG).show();
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	
	public class MyDialogFragment extends DialogFragment {
		
		String type;

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
			View root = inflater.inflate(R.layout.dialog_adddeals, container,
					false);


			Button btsave = (Button) root.findViewById(R.id.btnsave);
			Button btcancel = (Button) root.findViewById(R.id.btncancel);
			RadioGroup radGrp = (RadioGroup)root. findViewById(R.id.rgtype);
			
			final TextView tvquantity,lblcategory,lbltype;
			final TextView tvprice;
			
			final EditText etprice;
			final EditText etquantity;

			final EditText ettitle = (EditText) root.findViewById(R.id.ettitle);
			final EditText etdescription = (EditText) root
					.findViewById(R.id.etdescription);

			final EditText etdayrestrict = (EditText) root
					.findViewById(R.id.etdayrestrict);
			lbltype = (TextView) root.findViewById(R.id.lbltype);
			lblcategory = (TextView) root.findViewById(R.id.lblcategory);
			tvquantity = (TextView) root.findViewById(R.id.tvquantity);
			tvprice = (TextView) root.findViewById(R.id.tvprice);
			etprice= (EditText) root.findViewById(R.id.etprice);
			etquantity= (EditText) root.findViewById(R.id.etquantity);
			
			tvquantity.setVisibility(View.GONE);
        	tvprice.setVisibility(View.GONE);
        	etprice.setVisibility(View.GONE);
        	etquantity.setVisibility(View.GONE);
        	radGrp.setVisibility(View.GONE);
        	lbltype.setVisibility(View.GONE);
        	lblcategory.setText("Store");
        	
        	type="coupen";
			
			radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			      public void onCheckedChanged(RadioGroup arg0, int id) {
			        switch (id) {
			        case -1:
			         
			          break;
			        case R.id.rbcoupen:
			        	tvquantity.setVisibility(View.GONE);
			        	tvprice.setVisibility(View.GONE);
			        	etprice.setVisibility(View.GONE);
			        	etquantity.setVisibility(View.GONE);
			        	type="coupen";
			        	
			          break;
			        case R.id.rbitem:
			        	type="item";
			        	tvquantity.setVisibility(View.VISIBLE);
			        	tvprice.setVisibility(View.VISIBLE);
			        	etprice.setVisibility(View.VISIBLE);
			        	etquantity.setVisibility(View.VISIBLE);
			        
			          break;
			          
			        }
			      }
			      });

			spStore = (Spinner) root.findViewById(R.id.spcategory);
			ivDealsImage = (ImageView) root.findViewById(R.id.ivaddimage);

			ivDealsImage2 = (ImageView) root.findViewById(R.id.ivaddimage2);
			ivDealsImage3 = (ImageView) root.findViewById(R.id.ivaddimage3);
			ivDealsImage4 = (ImageView) root.findViewById(R.id.ivaddimage4);

			ivstartCalenderPicker = (ImageView) root
					.findViewById(R.id.ivstartcalender);
			ivendCalenderPicker = (ImageView) root
					.findViewById(R.id.ivendcalender);
			// tvStartDate=(TextView)root.findViewById(R.id.ivendcalender);
			tvStartDate = (TextView) root.findViewById(R.id.tvstartdate);
			tvendDate = (TextView) root.findViewById(R.id.tvenddate);

			tvSelectOffer = (TextView) root.findViewById(R.id.tvselectoffer);

			// imagebitmap = null;
			dealImagePath = null;
			dealImagePath2 = null;
			dealImagePath3 = null;
			dealImagePath4 = null;
			storeId = null;

			ivstartCalenderPicker.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					displayDatePicker("start");

				}
			});

			ivendCalenderPicker.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					
					if(tvStartDate.getText().toString().trim().equals(""))
					{
						Toast.makeText(getApplicationContext(), "Please select starting date first!!!", Toast.LENGTH_LONG).show();
					}
					else
					{
					
					displayDatePicker("end");
					}
				}
			});

			new getStoreAsyncTask().execute();

			ivDealsImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					imageCount = 1;
					Intent i = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

					startActivityForResult(i, RESULT_LOAD_IMAGE);

				}
			});
			ivDealsImage2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					imageCount = 2;
					Intent i = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

					startActivityForResult(i, RESULT_LOAD_IMAGE);

				}
			});
			ivDealsImage3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					imageCount = 3;
					Intent i = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

					startActivityForResult(i, RESULT_LOAD_IMAGE);

				}
			});
			ivDealsImage4.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					imageCount = 4;
					Intent i = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

					startActivityForResult(i, RESULT_LOAD_IMAGE);

				}
			});

			tvSelectOffer.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					displaySelectOffers();
				}
			});

			btcancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					MyDialogFragment.this.dismiss();
				}
			});

			btsave.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					String title = ((TextView) ettitle).getText().toString()
							.trim();

					String description = ((TextView) etdescription).getText()
							.toString().trim();
					String startDate = ((TextView) tvStartDate).getText()
							.toString().trim();
					String endDate = ((TextView) tvendDate).getText()
							.toString().trim();
					String selectedOffers = ((TextView) tvSelectOffer)
							.getText().toString().trim();

					String dayRestrict = ((TextView) etdayrestrict).getText()
							.toString().trim();
					
					String price = ((TextView) etprice).getText()
							.toString().trim();
					
					String quantity = ((TextView) etquantity).getText()
							.toString().trim();

					if (title.length() == 0) {
						Toast.makeText(getBaseContext(),
								"Please enter Title .", Toast.LENGTH_LONG)
								.show();

					} else if (selectedOffers.length() == 0
							|| selectedOffers.equals("Select any offers")) {

						Toast.makeText(getBaseContext(),
								"Please  Select Offers .", Toast.LENGTH_LONG)
								.show();

					} else if (startDate.length() == 0) {

						Toast.makeText(getBaseContext(),
								"Please enter Start Date .", Toast.LENGTH_LONG)
								.show();

					} else if (endDate.length() == 0) {

						Toast.makeText(getBaseContext(),
								"Please enter End Date .", Toast.LENGTH_LONG)
								.show();

					} else if (dealImagePath == null
							|| dealImagePath.length() == 0) {

						Toast.makeText(getBaseContext(),
								"Please select Deal image .", Toast.LENGTH_LONG)
								.show();

					} else if (storeId == null || storeId.equals("0")) {

						Toast.makeText(getBaseContext(),
								"Please select Store .", Toast.LENGTH_LONG)
								.show();

					}else if(type.equals("item")&&((price== null)||price.length() == 0)){
						
						Toast.makeText(getBaseContext(),
								"Please select Price .", Toast.LENGTH_LONG)
								.show();
					
					}else if(type.equals("item")&&(quantity== null||quantity.length() == 0)){
						
						Toast.makeText(getBaseContext(),
								"Please select Quantity .", Toast.LENGTH_LONG)
								.show();
					
					}

					else {
						
						

						deals = new Deals();
						deals.setDealsName(URLEncoder.encode(title));
						//deals.setCategoryId(categoryId);
						deals.setDescription(URLEncoder.encode(description));
						deals.setStartDate(URLEncoder.encode(startDate));
						deals.setEndDate(URLEncoder.encode(endDate));
						deals.setStoreId(storeId);
						deals.setOfferDetail(URLEncoder.encode(selectedOffers));
						deals.setDayRestrict(dayRestrict);
						deals.setType(type);
						deals.setQuantity(quantity);
						deals.setPrice(price);
						deals.setSpecialDeal("1");
						deals.setCustomerId(customerId);
						
						if (dealImagePath != null) {

							deals.setImagePath(dealImagePath);

						}
						if (dealImagePath2 != null) {

							deals.setImagePath2(dealImagePath2);

						}
						if (dealImagePath3 != null) {

							deals.setImagePath3(dealImagePath3);

						}
						if (dealImagePath4 != null) {

							deals.setImagePath4(dealImagePath4);

						}
						deals.setImageCount(imageCount);
						deals.setUserId(userId);

						if (utils.isNetworkAvailable()) {
							new SaveDeals().execute();
						} else {

						}

						MyDialogFragment.this.dismiss();

						/*
						 * dealsList.add(deals); adapterDeals= new
						 * AdapterDeals(ActivityAddDeals.this,
						 * R.layout.adapter_store, dealsList);
						 * 
						 * lvdeals.setAdapter(adapterDeals);
						 */
					}

				}
			});

			return root;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			switch (requestCode) {
			case (RESULT_LOAD_IMAGE):
				if (resultCode == Activity.RESULT_OK) {

					try {

						Uri selectedImage = data.getData();

						String[] filePathColumn = { MediaStore.Images.Media.DATA };

						Cursor cursor = ActivityCustomerDetail.this
								.getContentResolver().query(selectedImage,
										filePathColumn, null, null, null);
						cursor.moveToFirst();

						int columnIndex = cursor
								.getColumnIndex(filePathColumn[0]);

						String picturePath = cursor.getString(columnIndex);
						cursor.close();
						if (imageCount == 1) {
							
							
					             File file = new File(picturePath);
					             Long length = file.length();
					             length = length/1024;
					             int retval =  length.compareTo(new Long(100));
					             
					             if(retval > 0) { 
					            	

										Toast.makeText(getBaseContext(),
												"Please select image less then 100 KB", Toast.LENGTH_LONG)
												.show();
					            	 
					             }else{
					        

							dealImagePath = picturePath;
							final Bitmap thePic = BitmapFactory
									.decodeFile(picturePath);
							// imagebitmap = thePic;
							ivDealsImage.setImageBitmap(thePic);
							ivDealsImage2.setVisibility(View.VISIBLE);
					             }

						} else if (imageCount == 2) {
							
							   File file = new File(picturePath);
					             Long length = file.length();
					             length = length/1024;
					             int retval =  length.compareTo(new Long(100));
					             
					             if(retval > 0) { 
					            	

										Toast.makeText(getBaseContext(),
												"Please select image less then 100 KB", Toast.LENGTH_LONG)
												.show();
					            	 
					             }else{

							dealImagePath2 = picturePath;

							final Bitmap thePic = BitmapFactory
									.decodeFile(picturePath);
							// imagebitmap = thePic;

							ivDealsImage2.setImageBitmap(thePic);
							ivDealsImage3.setVisibility(View.VISIBLE);
					             }

						} else if (imageCount == 3) {
							
							   File file = new File(picturePath);
					             Long length = file.length();
					             length = length/1024;
					             int retval =  length.compareTo(new Long(100));
					             
					             if(retval > 0) { 
					            	

										Toast.makeText(getBaseContext(),
												"Please select image less then 100 KB", Toast.LENGTH_LONG)
												.show();
					            	 
					             }else{

							dealImagePath3 = picturePath;

							final Bitmap thePic = BitmapFactory
									.decodeFile(picturePath);
							
							ivDealsImage3.setImageBitmap(thePic);

							
							ivDealsImage4.setVisibility(View.VISIBLE);
					             }

						} else if (imageCount == 4) {
							
							   File file = new File(picturePath);
					             Long length = file.length();
					             length = length/1024;
					             int retval =  length.compareTo(new Long(100));
					             
					             if(retval > 0) { 
					            	

										Toast.makeText(getBaseContext(),
												"Please select image less then 100 KB", Toast.LENGTH_LONG)
												.show();
					            	 
					             }else{

							dealImagePath4 = picturePath;

							final Bitmap thePic = BitmapFactory
									.decodeFile(picturePath);
							// imagebitmap = thePic;
							ivDealsImage4.setImageBitmap(thePic);
							
					             }

							

						}

					} catch (Exception e) {

					}
				}
			}

		}

	}

	private class SaveDeals extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = Utils.createProgressDialog(ActivityCustomerDetail.this);
    		pDialog.setCancelable(false);
    		pDialog.show();
		}

		protected Void doInBackground(Void... unused) {
			regjson = userFunction.saveSpecialDeal(deals,deviceId,tokenKey);

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

						/*deals = new Deals();
						deals.setDealsName(regjson.getString("title"));
						deals.setCid(regjson.getString("cid"));
						deals.setStatus("0");
						dealsList.add(deals);
						adapterDeals.notifyDataSetChanged();*/
						
						Toast.makeText(ActivityCustomerDetail.this, "Deal saved successfully",
								Toast.LENGTH_LONG).show();

					} else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityCustomerDetail.this, errormessage,
								Toast.LENGTH_LONG).show();
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	void displaySelectOffers() {

		TextView tvCancel, tvset;
		final RadioGroup rgselection;
		String offerText;
		final EditText et1option1;
		final EditText et1option2;
		final EditText et2option1;
		final EditText et3option1;
		final EditText et3option2;
		final EditText et4option1;
		final EditText et5option1;
		final EditText et5option2;
		final EditText et6option1;
		final EditText et6option2;
		final EditText et7option1;
		final EditText et7option2;
		final EditText et8option1;

		LayoutInflater li = LayoutInflater.from(ActivityCustomerDetail.this);
		View promptsView = li.inflate(R.layout.dialog_select_offers, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ActivityCustomerDetail.this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		tvCancel = (TextView) promptsView.findViewById(R.id.btncancel);
		tvset = (TextView) promptsView.findViewById(R.id.btnset);
		rgselection = (RadioGroup) promptsView.findViewById(R.id.radioGroup1);
		et1option1 = (EditText) promptsView.findViewById(R.id.et1option1);
		et1option2 = (EditText) promptsView.findViewById(R.id.et1option2);
		et2option1 = (EditText) promptsView.findViewById(R.id.et2option1);
		et3option1 = (EditText) promptsView.findViewById(R.id.et3option1);
		et3option2 = (EditText) promptsView.findViewById(R.id.et3option2);
		et4option1 = (EditText) promptsView.findViewById(R.id.et4option1);
		et5option1 = (EditText) promptsView.findViewById(R.id.et5option1);
		et5option2 = (EditText) promptsView.findViewById(R.id.et5option2);
		et6option1 = (EditText) promptsView.findViewById(R.id.et6option1);
		et6option2 = (EditText) promptsView.findViewById(R.id.et6option2);
		et7option1 = (EditText) promptsView.findViewById(R.id.et7option1);
		et7option2 = (EditText) promptsView.findViewById(R.id.et7option2);
		et8option1 = (EditText) promptsView.findViewById(R.id.et8option1);

		rgselection.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				RadioButton checkedRadioButton = (RadioButton) rgselection
						.findViewById(checkedId);
				pos = rgselection.indexOfChild(checkedRadioButton);

				// pos=group.indexOfChild(findViewById(group.getCheckedRadioButtonId()));

			}
		});
		// TODO Auto-generated method stub

		tvset.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				if (pos == 0) {
					String text1 = ((TextView) et1option1).getText().toString()
							.trim();
					String text2 = ((TextView) et1option2).getText().toString()
							.trim();

					if (text1.length() == 0 || text2.length() == 0) {

						Toast.makeText(getBaseContext(),
								"Please enter selected offer fields.",
								Toast.LENGTH_LONG).show();

					} else {
						alertDialog.cancel();
						tvSelectOffer.setText("Buy " + text1 + " Get " + text2
								+ " Free");
					}

				} else if (pos == 1) {

					String text1 = ((TextView) et2option1).getText().toString()
							.trim();

					if (text1.length() == 0) {

						Toast.makeText(getBaseContext(),
								"Please enter selected offer fields.",
								Toast.LENGTH_LONG).show();

					} else {
						alertDialog.cancel();
						tvSelectOffer.setText("Flat " + text1
								+ " % off on your bill");
					}

				} else if (pos == 2) {

					String text1 = ((TextView) et3option1).getText().toString()
							.trim();
					String text2 = ((TextView) et3option2).getText().toString()
							.trim();

					if (text1.length() == 0 || text2.length() == 0) {

						Toast.makeText(getBaseContext(),
								"Please enter selected offer fields.",
								Toast.LENGTH_LONG).show();

					} else {
						alertDialog.cancel();
						tvSelectOffer.setText("Get " + text1
								+ " % off when you spend Rs " + text2);
					}

				} else if (pos == 3) {

					String text1 = ((TextView) et4option1).getText().toString()
							.trim();

					if (text1.length() == 0) {

						Toast.makeText(getBaseContext(),
								"Please enter selected offer fields.",
								Toast.LENGTH_LONG).show();

					} else {
						alertDialog.cancel();

						tvSelectOffer.setText("Flat Rs " + text1 + " off ");
					}

				} else if (pos == 4) {

					String text1 = ((TextView) et5option1).getText().toString()
							.trim();
					String text2 = ((TextView) et5option2).getText().toString()
							.trim();
					if (text1.length() == 0 || text2.length() == 0) {

						Toast.makeText(getBaseContext(),
								"Please enter selected offer fields.",
								Toast.LENGTH_LONG).show();

					} else {
						alertDialog.cancel();
						tvSelectOffer.setText("Get Rs " + text1
								+ " off when you spend Rs " + text2);
					}

				} else if (pos == 5) {
					String text1 = ((TextView) et6option1).getText().toString()
							.trim();
					String text2 = ((TextView) et6option2).getText().toString()
							.trim();

					if (text1.length() == 0 || text2.length() == 0) {

						Toast.makeText(getBaseContext(),
								"Please enter selected offer fields.",
								Toast.LENGTH_LONG).show();

					} else {
						alertDialog.cancel();
						tvSelectOffer.setText(text1 + " starting at just Rs  "
								+ text2);
					}

				} else if (pos == 6) {
					String text1 = ((TextView) et7option1).getText().toString()
							.trim();
					String text2 = ((TextView) et7option2).getText().toString()
							.trim();
					if (text1.length() == 0 || text2.length() == 0) {

						Toast.makeText(getBaseContext(),
								"Please enter selected offer fields.",
								Toast.LENGTH_LONG).show();

					} else {
						alertDialog.cancel();
						tvSelectOffer.setText("Buy at Rs " + text1
								+ " Actual Price is Rs " + text2);
					}

				} else if (pos == 7) {

					String text1 = ((TextView) et8option1).getText().toString()
							.trim();

					if (text1.length() == 0) {

						Toast.makeText(getBaseContext(),
								"Please enter selected offer fields.",
								Toast.LENGTH_LONG).show();

					} else {
						alertDialog.cancel();

						tvSelectOffer.setText(text1);
					}

				}

			}
		});

		tvCancel.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					alertDialog.cancel();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	void displayDatePicker(final String type) {
		final DatePicker datePicker;
		final TimePicker timePicker;

		TextView tvCancel, tvset;

		LayoutInflater li = LayoutInflater.from(ActivityCustomerDetail.this);
		View promptsView = li.inflate(R.layout.dialog_datepicker, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ActivityCustomerDetail.this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		timePicker = (TimePicker) promptsView.findViewById(R.id.timePicker1);

		tvCancel = (TextView) promptsView.findViewById(R.id.btncancel);
		tvset = (TextView) promptsView.findViewById(R.id.btnset);

		datePicker = (DatePicker) promptsView.findViewById(R.id.datePicker1);
		
		datePicker.setMinDate(System.currentTimeMillis() - 1000);

		timePicker.setCurrentHour(0);
		timePicker.setCurrentMinute(0);

		tvset.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				int month = datePicker.getMonth();
				int year = datePicker.getYear();
				int day = datePicker.getDayOfMonth();
				int selectedHour = timePicker.getCurrentHour();
				int selectedMinute = timePicker.getCurrentMinute();

				Calendar calNow = Calendar.getInstance();
				Calendar calSet = (Calendar) calNow.clone();

				calSet.set(Calendar.DATE, day);
				calSet.set(Calendar.MONTH, month);
				calSet.set(Calendar.YEAR, year);
				calSet.set(Calendar.HOUR_OF_DAY, selectedHour);
				calSet.set(Calendar.MINUTE, selectedMinute);

				long time_val = calSet.getTimeInMillis();

				String formatted_date = (DateFormat.format(
						"yyyy-MM-dd kk:mm:ss", time_val)).toString();

				if (type.equals("start")) {
					
					
					String formatted_date2 = (DateFormat.format(
							"yyyy-MM-dd kk:mm:ss", System.currentTimeMillis())).toString();
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        	Date date1 = null,date2 = null;
					try {
						date1 = sdf.parse(formatted_date);
						date2 = sdf.parse(formatted_date2);
					} catch (java.text.ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        	
		 
		        	if(date1.compareTo(date2)>0||date1.compareTo(date2)==0){

		        		  tvStartDate.setText(formatted_date);
		        		  tvendDate.setText("");
		        		  alertDialog.cancel();
		        	}else if(date1.compareTo(date2)<0){
		        		
		        		  Toast.makeText(getApplicationContext(), "You can't set Past Date as Starting Date!!!", Toast.LENGTH_LONG).show();
		        		  
		        	}
				

				} else {
					
					if(tvStartDate.getText().toString().trim().equals(""))
					{
						Toast.makeText(getApplicationContext(), "Please select starting date first!!!", Toast.LENGTH_LONG).show();
					}
					else
					{			
					Date dmsd=null;
					String dtStart = tvStartDate.getText().toString();  
					SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");  
					try {  
						dmsd = format.parse(dtStart);  
					  //  System.out.println(date);  
					} catch (ParseException e) {  
					    // TODO Auto-generated catch block  
					    e.printStackTrace();  
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					/*String formatted_date2 = (DateFormat.format(
							"yyyy-MM-dd kk:mm:ss", dmsd.getDate())).toString();*/
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
					formatter.setLenient(false);


					String oldTime = dtStart;
					long oldMillis = 0;
					Date oldDate;
					try {
						oldDate = formatter.parse(oldTime);
						oldMillis = oldDate.getTime();
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					String formatted_date2 = (DateFormat.format(
							"yyyy-MM-dd kk:mm:ss", oldMillis)).toString();
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        	Date date1 = null,date2 = null;
					try {
						date1 = sdf.parse(formatted_date);
						date2 = sdf.parse(formatted_date2);
						
					} catch (java.text.ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        	
		 
		        	if(date1.compareTo(date2)>0||date1.compareTo(date2)==0){

		        		tvendDate.setText(formatted_date);
		        		 
		        		 alertDialog.cancel();
		        	}else if(date1.compareTo(date2)<0){
		        		
		        		  Toast.makeText(getApplicationContext(), "You can't set Date before the Starting Date as End Date!!!", Toast.LENGTH_LONG).show();
		        		  
		        	}
					
					}
				
				}
				

			}
		});

		tvCancel.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					alertDialog.cancel();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	private class getStoreAsyncTask extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			Store store;
			try {
				storeList.clear();
				store = new Store();
				store.setStoreId("0");
				store.setStoreName("--Select--");
				storeList.add(store);
				
				json = userFunction.getStoreList(userId, deviceId, tokenKey);
				if (json != null) {
					JSONArray dataDealsArray = json
							.getJSONArray("result");

					for (int i = 0; i < dataDealsArray.length(); i++) {
						JSONObject dealObject = dataDealsArray.getJSONObject(i);

						store = new Store();
						store.setStoreId(dealObject
								.getString("sid"));
						store.setStoreName(dealObject
								.getString("store_name"));
						storeList.add(store);
					}

				}
			} catch (Exception e) {

			}

			return null;
		}

		protected void onProgressUpdate(Void... progress) {

			/*
			 * dialog = new Dialog(HomeActivity.this);
			 * 
			 * dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
			 * ; dialog.setContentView(R.layout.loading_layout);
			 * dialog.setCancelable(false);
			 * dialog.getWindow().setBackgroundDrawableResource(
			 * android.R.color.transparent); dialog.show();
			 */

		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// dialog.dismiss();
			try {
				spStore.setAdapter(new AdapterDroupDownStore(
						ActivityCustomerDetail.this, R.layout.spinner_content,
						storeList));

				spStore
						.setOnItemSelectedListener(new StoreSelectedListener());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public class StoreSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {

			Store store = (Store) spStore.getSelectedItem();

			storeId = store.getStoreId();

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}

	// Listener for option menu
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
