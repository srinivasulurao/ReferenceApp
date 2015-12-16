package dealsforsure.in;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import dealsforsure.in.R;
import dealsforsure.in.ads.Ads;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.Cart;
import dealsforsure.in.utils.Utils;

@SuppressLint("ResourceAsColor")
public class ActivityDetail extends ActionBarActivity implements
		OnClickListener {

	// Create an instance of ActionBar
	private ActionBar actionbar;
    ImageView ivCart;
	// Declare object of AdView class
	private AdView adView;
	SharedPreferences sharedPreferences;
	// Declare object of userFunction, JSONObject, and Utils class
	private UserFunctions userFunction;
	private JSONObject json, promoJson;
	private Utils utils;
	private String userId, userCode;
	// Declare variables to store data
	private String mGetDealId;
	private String mCompany;
	private String mTitle;
	private String mAddress;
	private String mDesc;
	private String mDealPoint;
	private String mofferDetail;
	Cart cart;
	Gson gson;
	/*
	 * private String mAfterDiscount; private String mStartValue; private String
	 * mDiscount; private String mSave;
	 */
	private String mDateStart;
	private String mDateEnd;
	private String mUrl;
	private String mImgDeal;
	private String mIcMarker;
	private String mCid;
	private String mValidUptoDate;
	private String mDealId;
	private String merchantId;
	private String mImgDeal2;
	private String mImgDeal3;
	private String mImgDeal4;
	private Integer mImageCount;
	private String mDayRestrict;
	private String mdescription;
	private String mviewCount;
	private String mstarCount;
	private String mprice;
	private String mtype;
	
	private String mviewitCount;
	private String mgetCount;
	private String mbuyCount;

	private Double mDblLatitude;
	private Double mDblLongitude;

	final String mimeType = "text/html";
	final String encoding = "UTF-8";

	// Declare view objects
	private TextView lblCompany, lblTitle, lblAddress, lblDateStart, lblDateEnd, lbldealpoint,lblcomments,
			lblofferdeatil,tvviewcount,tvgetcount,tvbuycount,tvdescription,lblDayResrtict;
	//private ImageView imgThumbnail;
	private LinearLayout lytMedia,llStarRate;
	private RelativeLayout lytDetail, lytDesc;
	private Button btnGet;
	private WebView webDesc;
    ViewPager viewPager ;
    CirclePageIndicator circleIndicator;
	private ProgressBar prgStepLoading;
	ImageView icMarker;
	String tokenKey,deviceId;
	// Declare view objects
	private TextView lblNoResult, lblAlert,tvCartBadge;
	private Button btnRetry;
	private LinearLayout lytRetry;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.aactionbar_detail, menu);
		
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		// Declare object of userFunction and utils class
		userFunction = new UserFunctions();
		utils = new Utils(this);

		// Get intent Data from ActivityHome, ActivityPlaceAroundYou,
		// ActivityPlaceList OR ActivitySearch
		Intent i = getIntent();
		mGetDealId = i.getStringExtra(utils.EXTRA_DEAL_ID);

		// Get ActionBar and set back private Button on actionbar
		//actionbar = getSupportActionBar();
		actionbar = getSupportActionBar();
     	//actionbar.setTitle("vvvvvvvv");
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.actionbar_detail, null);
		
		TextView tvTitle= (TextView) mCustomView.findViewById(R.id.tvtitle);
		tvTitle.setText("Details");
		/*tvCartBadge=(TextView)mCustomView.findViewById(R.id.tab_badge);
		ivCart=(ImageView)mCustomView.findViewById(R.id.ivcart);
		
		ivCart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Intent i =new Intent();
				i.setClass(ActivityDetail.this, ActivityCart.class);
			
				startActivity(i);
				
				
			}
		});
		
		
		tvCartBadge.setVisibility(View.GONE);*/
		gson = new Gson();
		//tvTitle.setText("My Own Title");
		actionbar.setCustomView(mCustomView);
		actionbar.setDisplayShowCustomEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityDetail.this);
		userId = sharedPreferences.getString("userId", null);
		userCode = sharedPreferences.getString("userCode", null);
	    tokenKey = sharedPreferences.getString("tokenKey", null);
	    deviceId = sharedPreferences.getString("deviceId", null);
	   // updateCartBadge();
	    
		// Connect view objects and xml ids
		lblCompany = (TextView) findViewById(R.id.lblCompany);
		lblTitle = (TextView) findViewById(R.id.lblTitle);
		lblAddress = (TextView) findViewById(R.id.lblAddress);
		//lblAfterDiscount = (TextView) findViewById(R.id.lblAfterDiscount);
		//lblStartValue = (TextView) findViewById(R.id.lblStartValue);
		//lblDiscount = (TextView) findViewById(R.id.lblDiscountValue);
		// lblSave = (TextView) findViewById(R.id.lblSaveValue);
		lblDateStart = (TextView) findViewById(R.id.lblStartDateValue);
		lblDateEnd = (TextView) findViewById(R.id.lblEndDateValue);
		lbldealpoint = (TextView) findViewById(R.id.lbldealpoint);
		lblofferdeatil = (TextView) findViewById(R.id.lblofferdeatil);
		lytMedia = (LinearLayout) findViewById(R.id.lytMedia);
		lytDetail = (RelativeLayout) findViewById(R.id.lytDetail);
		btnGet = (Button) findViewById(R.id.btnGet);
		icMarker=(ImageView)findViewById(R.id.icMarker);
		//imgThumbnail = (ImageView) findViewById(R.id.imgThumbnail);
		adView = (AdView) this.findViewById(R.id.adView);
		// webDesc = (WebView) findViewById(R.id.webDesc);
		// prgStepLoading = (ProgressBar) findViewById(R.id.prgLoading);
		lblNoResult = (TextView) findViewById(R.id.lblNoResult);
		lblAlert = (TextView) findViewById(R.id.lblAlert);
		// lytDesc = (RelativeLayout) findViewById(R.id.lytDesc);
		lytRetry = (LinearLayout) findViewById(R.id.lytRetry);
		btnRetry = (Button) findViewById(R.id.btnRetry);
	   
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		circleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		tvviewcount= (TextView) findViewById(R.id.tvviewcount);
		tvgetcount= (TextView) findViewById(R.id.tvgetcount);
		tvbuycount= (TextView) findViewById(R.id.tvbuycount);
		llStarRate=(LinearLayout)findViewById(R.id.llstarrate);
		lblcomments= (TextView) findViewById(R.id.lblcomments);
		tvdescription=(TextView) findViewById(R.id.tvdescription);
		lblDayResrtict=(TextView) findViewById(R.id.lblDayResrtict);
		btnRetry.setOnClickListener(this);
		btnGet.setOnClickListener(this);

		// Change cover image width and height
		/*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				utils.loadPreferences(utils.PARAM_WIDTH_PIX),
				(((utils.loadPreferences(utils.PARAM_WIDTH_PIX)) * 9) / 16));

		lytMedia.setLayoutParams(lp);*/
		new getDataAsync().execute();

		/*
		 * CHECK_PLAY_SERV = 1 means Google Play services version on the device
		 * supports the version of the client library you are using
		 */
		if (utils.loadPreferences(utils.CHECK_PLAY_SERV) == 1) {
			// Check the connection
			if (utils.isNetworkAvailable()) {

				// Call asynctask class

				// Condition for admob (0=gone, 1=visible)
				if (utils.paramAdmob == 1) {

					adView.setVisibility(View.VISIBLE);
					// load ads
					Ads.loadAds(adView);
				}
			} else {
				lblNoResult.setVisibility(View.GONE);
				lytRetry.setVisibility(View.VISIBLE);
				lblAlert.setText(R.string.no_connection);
			}
		}
		
		icMarker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Intent i;
				i = new Intent(ActivityDetail.this, ActivityDirection.class);
				i.putExtra(utils.EXTRA_DEST_LAT, mDblLatitude);
				i.putExtra(utils.EXTRA_DEST_LNG, mDblLongitude);
				i.putExtra(utils.EXTRA_CATEGORY_MARKER, mIcMarker);
				startActivity(i);
				
			}
		});
		
		lblcomments.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Intent i =new Intent();
				i.setClass(ActivityDetail.this, ActivityReview.class);
				i.putExtra("cid", mCid);
				startActivity(i);
				
			}
		});

	}
	
	public void updateCartBadge(){
		
	
		
	Set<String> cartItemIdset = sharedPreferences.getStringSet("cartItemId",
				new HashSet<String>());
		
		if(cartItemIdset.size()>0){
			
			Integer cartCount=cartItemIdset.size();
			tvCartBadge.setVisibility(View.VISIBLE);
			tvCartBadge.setText(cartCount.toString());
			
		}else{
			tvCartBadge.setVisibility(View.GONE);
			
		}
		
	}

	public class getPromocodeAsync extends AsyncTask<Void, Void, Void> {


ProgressDialog pDialog;

 @Override
        protected void onPreExecute() {
        	
        	pDialog = Utils.createProgressDialog(ActivityDetail.this);
    		pDialog.setCancelable(false);
    		pDialog.show();

        }

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			// Method to get Data from Server
			// getDataFromServer();

			promoJson = userFunction.getPromoCode(mCid, userId, userCode,
					mValidUptoDate, mDealId, merchantId,"detail",tokenKey,deviceId);

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
								.getDefaultSharedPreferences(ActivityDetail.this);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("userPoint", totalpoints);
						editor.commit();

						displaypromocode(promocode);

					}

				}
			} catch (Exception e) {

			}

			if(pDialog.isShowing()) pDialog.dismiss();

		}

	}

	// AsyncTask to Get Data from Server and put it on View Object
	public class getDataAsync extends AsyncTask<Void, Void, Void> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			// Show progress dialog when fetching data from database
			/*progress = ProgressDialog.show(ActivityDetail.this, "",
					getString(R.string.loading_data), true);*/
			progress = Utils.createProgressDialog(ActivityDetail.this);
			progress.setCancelable(false);
			progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			// Method to get Data from Server
			getDataFromServer();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

			if (json != null) {
				// Display Data
				lytDetail.setVisibility(View.VISIBLE);
				lytRetry.setVisibility(View.GONE);
				 lblCompany.setText(mCompany+",");
				lblTitle.setText(mTitle);
				lblAddress.setText(mAddress);
				/*
				 * lblAfterDiscount.setText(mAfterDiscount+Utils.mCurrency);
				 * lblStartValue.setText(mStartValue+Utils.mCurrency);
				 * lblDiscount.setText(mDiscount+"%");
				 */
				
				lblDateStart.setText(" " + mDateStart);
				lblDateEnd.setText(" " + mDateEnd);
				
				lblofferdeatil.setText(mofferDetail);
				
				lblcomments.setText("View Reviews"+"("+mviewCount+")");
				tvgetcount.setText(mgetCount);
				tvbuycount.setText(mbuyCount);
				tvviewcount.setText(mviewitCount);
				tvdescription.setText(mdescription);
				lblDayResrtict.setText(mDayRestrict);
				
				if(mtype.equals("coupen")){
					lbldealpoint.setText(mDealPoint);
					btnGet.setText("Get It");
					
					// lblSave.setText(mSave+Utils.mCurrency);
					
				
				}else{
					
					btnGet.setText("Buy It");
					//lblSave.setText("Price :");
					lbldealpoint.setText(mprice);
					
				}
				
				Integer countStar = new Integer(mstarCount);

				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						20, 20);

				for (Integer i = 0; i < countStar; i++) {

					ImageView myImage = new ImageView(ActivityDetail.this);
					myImage.setLayoutParams(layoutParams);
					myImage.setImageResource(R.drawable.star);
					llStarRate.addView(myImage);

				}

				
				final ImagePagerAdapter adapter = new ImagePagerAdapter();
		        viewPager.setAdapter(adapter);
		        
		       
		        circleIndicator.setViewPager(viewPager);
		        
		        final float density = getResources().getDisplayMetrics().density;
		       
		      
		        circleIndicator.setPageColor(0x880000FF);
		        circleIndicator.setFillColor(0xFF888888);
		        circleIndicator.setStrokeColor(0xFF000000);
		        

				// Load data from url
				// webViewStep();

				// Set Image thumbnail from Server with picasso
				/*Picasso.with(getApplicationContext())
						.load(userFunction.URLAdmin +userFunction.folderAdmin+ mImgDeal).fit()
						.centerCrop().tag(getApplicationContext())
						.into(imgThumbnail);*/
		        
		        Picasso.with(ActivityDetail.this)
				.load(userFunction.URLAdmin + userFunction.folderAdmin
						+ mIcMarker).fit()
				.centerCrop().tag(ActivityDetail.this).into(icMarker);

			} else {
				lytDetail.setVisibility(View.GONE);
				lytRetry.setVisibility(View.VISIBLE);
				Toast.makeText(ActivityDetail.this,
						getString(R.string.no_connection), Toast.LENGTH_SHORT)
						.show();

			}
			if (progress.isShowing()) {
				progress.dismiss();
			}

		}

	}
	
	
	private class ImagePagerAdapter extends PagerAdapter {
       

        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }

        @Override
        public int getCount() {
            return mImageCount;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final Context context = ActivityDetail.this;
            final ImageView imageView = new ImageView(context);
            final int padding = context.getResources().getDimensionPixelSize(
                    R.dimen.padding_medium);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
           // imageView.setImageResource(this.mImages[position]);
            if(position==0){
            Picasso.with(getApplicationContext())
			.load(userFunction.URLAdmin +userFunction.folderAdmin+ mImgDeal).fit()
			.centerCrop().tag(getApplicationContext())
			.into(imageView);
			} else if (position == 1) {
				Picasso.with(getApplicationContext())
				.load(userFunction.URLAdmin +userFunction.folderAdmin+ mImgDeal2).fit()
				.centerCrop().tag(getApplicationContext())
				.into(imageView);

			} else if (position == 2) {
				Picasso.with(getApplicationContext())
				.load(userFunction.URLAdmin +userFunction.folderAdmin+ mImgDeal3).fit()
				.centerCrop().tag(getApplicationContext())
				.into(imageView);

			} else if (position == 3) {
				Picasso.with(getApplicationContext())
				.load(userFunction.URLAdmin +userFunction.folderAdmin+ mImgDeal4).fit()
				.centerCrop().tag(getApplicationContext())
				.into(imageView);

			}
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view == ((ImageView) object);
        }
    }
	
	

	// Method to get Data from Server
	public void getDataFromServer() {

		try {
			
			
			json = userFunction.dealDetail(mGetDealId,userId,tokenKey,deviceId);
			if (json != null) {
				JSONArray dataDealsArray = json
						.getJSONArray(userFunction.array_deal_detail);

				JSONObject dealsObject = dataDealsArray.getJSONObject(0);

				// Store Data to Variables
				mCompany = dealsObject
						.getString(userFunction.key_deals_company);
				mTitle = dealsObject.getString(userFunction.key_deals_title);
				mAddress = dealsObject
						.getString(userFunction.key_deals_address);
				/*
				 * mAfterDiscount =
				 * dealsObject.getString(userFunction.key_deals_after_disc_value
				 * ); mStartValue =
				 * dealsObject.getString(userFunction.key_deals_start_value);
				 * mDiscount =
				 * dealsObject.getString(userFunction.key_deals_disc); mSave =
				 * dealsObject.getString(userFunction.key_deals_save);
				 */
				mImgDeal = dealsObject.getString(userFunction.key_deals_image);
				mIcMarker = dealsObject
						.getString(userFunction.key_category_marker);
				mDblLatitude = dealsObject
						.getDouble(userFunction.key_deals_lat);
				mDblLongitude = dealsObject
						.getDouble(userFunction.key_deals_lng);
				mDateStart = dealsObject
						.getString(userFunction.key_deals_date_start);
				mDateEnd = dealsObject
						.getString(userFunction.key_deals_date_end);
				mDesc = dealsObject.getString(userFunction.key_deals_desc);
				mDealPoint = dealsObject.getString(userFunction.key_deal_point);
				mofferDetail = dealsObject
						.getString(userFunction.key_offer_detail);

				// mUrl = dealsObject.getString(userFunction.key_deals_url);
				mImgDeal = dealsObject.getString(userFunction.key_deals_image);
				mIcMarker = dealsObject
						.getString(userFunction.key_category_marker);
				mDblLatitude = dealsObject
						.getDouble(userFunction.key_deals_lat);
				mDblLongitude = dealsObject
						.getDouble(userFunction.key_deals_lng);
				mCid = dealsObject.getString(userFunction.key_c_id);
				mValidUptoDate = dealsObject
						.getString(userFunction.key_valid_date);
				mDealId = dealsObject.getString(userFunction.key_deals_id);
				merchantId = dealsObject
						.getString(userFunction.key_merchant_id);
				mviewCount=json.getString(userFunction.key_commentsCount);
				mstarCount=json.getString(userFunction.key_starCount);
				 mtype=dealsObject.getString(userFunction.key_dealtype);
				mviewitCount=json.getString(userFunction.key_view_count);
				mgetCount=json.getString(userFunction.key_get_count);
				mbuyCount=json.getString(userFunction.key_buy_count);
				mImgDeal2=dealsObject.getString(userFunction.key_deals_image2);
				mImgDeal3=dealsObject.getString(userFunction.key_deals_image3);
				mImgDeal4=dealsObject.getString(userFunction.key_deals_image4);
				mImageCount=dealsObject.getInt(userFunction.key_image_count);
				mDayRestrict=dealsObject.getString(userFunction.key_day_restrict);
			    mdescription=dealsObject.getString(userFunction.key_description);
			    mprice=dealsObject.getString(userFunction.key_price);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.i("ActivityDetailPlace", "getDataFromServer: " + e);
		}
	}

	// Listener for option menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		Intent i;
		switch (item.getItemId()) {
		case android.R.id.home:
			// Previous page or exit
			finish();
			
			return true;
			
		case R.id.mapIcon:
		
			Intent is;
			is = new Intent(ActivityDetail.this, ActivityDirection.class);
			is.putExtra(utils.EXTRA_DEST_LAT, mDblLatitude);
			is.putExtra(utils.EXTRA_DEST_LNG, mDblLongitude);
			is.putExtra(utils.EXTRA_CATEGORY_MARKER, mIcMarker);
			startActivity(is);
			break;
			
			
		/*case R.id.abDirection:
			// Call ActivityPlaceAroundYou
			i = new Intent(this, ActivityDirection.class);
			i.putExtra(utils.EXTRA_DEST_LAT, mDblLatitude);
			i.putExtra(utils.EXTRA_DEST_LNG, mDblLongitude);
			i.putExtra(utils.EXTRA_CATEGORY_MARKER, mIcMarker);
			startActivity(i);
			
			break;*/

		/*case R.id.abShare:
			i = new Intent(this, ActivityShare.class);
			i.putExtra(utils.EXTRA_DEAL_ID, mGetDealId);
			i.putExtra(utils.EXTRA_DEAL_TITLE, mTitle);
			i.putExtra(utils.EXTRA_DEAL_DESC, mDesc);
			i.putExtra(utils.EXTRA_DEAL_IMG, mImgDeal);
			startActivity(i);
			
			break;*/

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	// Method handle load webview
	/*
	 * private void webViewStep(){ lytDesc.setVisibility(View.VISIBLE);
	 * webDesc.loadUrl(userFunction.varLoadURL+mGetDealId); final Activity act =
	 * ActivityDetail.this; webDesc.setWebChromeClient(new WebChromeClient(){
	 * public void onProgressChanged(WebView webview, int progress){
	 * 
	 * act.setProgress(progress*100); prgStepLoading.setProgress(progress);
	 * 
	 * }
	 * 
	 * });
	 * 
	 * webDesc.setWebViewClient(new WebViewClient() {
	 * 
	 * @Override public void onPageStarted( WebView view, String url, Bitmap
	 * favicon ) {
	 * 
	 * super.onPageStarted(webDesc, url, favicon );
	 * prgStepLoading.setVisibility(View.VISIBLE);
	 * 
	 * }
	 * 
	 * @Override public void onPageFinished( WebView view, String url ) {
	 * 
	 * super.onPageFinished( webDesc, url );
	 * 
	 * prgStepLoading.setProgress(0); prgStepLoading.setVisibility(View.GONE);
	 * 
	 * } public void onReceivedError(WebView view, int errorCode, String
	 * description, String failingUrl) {
	 * 
	 * view.stopLoading(); // may not be needed
	 * view.loadData(utils.timeoutMessageHtml, "text/html", "utf-8"); }
	 * 
	 * }); }
	 */

	// Listener for on click
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGet:

			userId = sharedPreferences.getString("userId", null);
			userCode = sharedPreferences.getString("userCode", null);
			if (userId != null) {
				if(mtype.equals("coupen")){
				displayConforim(mDealPoint);
				}else{
					
					Intent i = new Intent();
					i.setClass(ActivityDetail.this, ActivityConfirmOrder.class);
					i.putExtra("dealCost", mprice);
					i.putExtra("cid", mCid);
					startActivity(i);
					
					//addToCart();
					
				}
			} else {

				Intent i = new Intent();
				i.setClass(ActivityDetail.this, ActivityRegistrationDetail.class);
				i.putExtra("type", "promo");
				startActivity(i);
			}

			// displaypromocode();
			break;
		/*
		 * Intent i; // Open ActivityBrowser i = new Intent(this,
		 * ActivityBrowser.class); i.putExtra(utils.EXTRA_DEAL_URL, mUrl);
		 * i.putExtra(utils.EXTRA_DEAL_TITLE, mTitle); startActivity(i);
		 * overridePendingTransition (R.anim.open_next, R.anim.close_main);
		 */

		case R.id.btnRetry:
			// Retry to get Data
			if (utils.isNetworkAvailable()) {
				json = null;
				new getDataAsync().execute();
			} else {
				lblNoResult.setVisibility(View.GONE);
				lytRetry.setVisibility(View.VISIBLE);
				lblAlert.setText(R.string.no_connection);
			}
			break;

		default:
			break;
		}
	}
	
	void addToCart(){
		
		Set<String> cartItemIdset = new HashSet<String>();
		
		cartItemIdset = sharedPreferences.getStringSet("cartItemId",
				new HashSet<String>());
		
		if((cartItemIdset.size()>0)&&(cartItemIdset.contains(mCid))){
			Toast.makeText(getApplicationContext(), "Item already added to cart", Toast.LENGTH_LONG).show();
			}else{
				cart=new Cart();
				cart.setCid(mCid);
				cart.setPrice(mprice);
				cart.setQuantity("1");
				cart.setTitle(mTitle);
				cart.setImageUrl(mImgDeal);
				//cart.setPrice(mprice);
				Set<String> cartItemset = sharedPreferences.getStringSet("cartItem",
						new HashSet<String>());
				
				String cartString = gson.toJson(cart);
				cartItemset.add(cartString);
				
				cartItemIdset.add(mCid);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.remove("cartItemId");
				editor.remove("cartItem");
				editor.putStringSet("cartItemId", cartItemIdset);
				editor.putStringSet("cartItem", cartItemset);
				editor.apply();
			}
			
		
		
		/*Integer cartCount = sharedPreferences.getInt("cartCount", 0);
		cartCount=cartCount+1;
		SharedPreferences.Editor editor = sharedPreferences
				.edit();
		editor.putInt("cartCount", cartCount);
		editor.commit();*/
		//updateCartBadge();
		
		
		
		
	}

	void displayConforim(String points) {
		TextView tcPromoCode, tvheader;
		ImageView ivClose;
		Button btOK, btncancel;
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.dialog_promocode_confirm, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

					Toast.makeText(getBaseContext(), "Error in connection .",
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

	void displaypromocode(String promoCode) {
		TextView tcPromoCode;
		ImageView ivClose;
		Button btOK;
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.dialog_getpromocode, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}

}