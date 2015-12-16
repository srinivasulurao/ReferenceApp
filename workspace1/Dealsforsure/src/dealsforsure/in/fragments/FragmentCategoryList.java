package dealsforsure.in.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dealsforsure.in.ActivityHome;
import dealsforsure.in.R;
import dealsforsure.in.adapters.AdapterHome;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.loadmore.PagingListView;
import dealsforsure.in.utils.Utils;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
public class FragmentCategoryList extends Fragment implements OnClickListener {
	
	// Create interface for ListFragment
	private OnDataListSelectedListener mCallback;
	
	private ArrayList<HashMap<String, String>> mItems;
	private ProgressDialog pDialog;
	 SharedPreferences sharedPreferences;
    // Declare object of userFunctions and Utils class
	private UserFunctions userFunction;
	private Utils utils;
	
	// Create instance of list and ListAdapter

	private AdapterHome la;
	private PagingListView list;

	// Declare view objects
	private TextView lblNoResult, lblAlert;
	private Button btnRetry; 
	private LinearLayout lytRetry;
	
    // flag for current page
	private JSONObject json;
	private int mCurrentPage = 0;
	private int mPreviousPage;
    
	// create array variables to store data
    private String[] mDealsId;
    private String[] mTitle;
    private String[] mDateEnd;
    private String[] mAfterDiscValue;
    private String[] mStartValue;
    private String[] mImg;
    private String[] mIcMarker;
    private String[] mCId;
    private String[] merchantId;
    private String[] mValidUptoDate;
    private String[] mDealPoint;
    private String[] mLatitude;
    private String[] mLongitude;
    private String[] mOfferDetail;
    private String[] mStoreName;
    private String[] mAddress;
    private String[] mDayRestrict;
    private String[] mReviewCount;
    private String[] mViewCount;
    private String[] mGetCount;
    private String[] mBuyCount;
	
	
	private int intLengthData;
	// Declare variable 
	private String mCategoryId;
	
	// To handle parameter loadmore gone or visible(1 = visible ; 0 = gone)
	private int paramLoadmore=0;
	
	// Declare OnListSelected interface
	public interface OnDataListSelectedListener{
		public void onListSelected(String idSelected);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View v = inflater.inflate(R.layout.fragment_home, container, false);
		
		list 	 	= (PagingListView) v.findViewById(R.id.list);
		lblNoResult	= (TextView) v.findViewById(R.id.lblNoResult);
		lblAlert	= (TextView) v.findViewById(R.id.lblAlert);
		lytRetry 	= (LinearLayout) v.findViewById(R.id.lytRetry);
		btnRetry 	= (Button) v.findViewById(R.id.btnRetry);

		btnRetry.setOnClickListener(this);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		// Declare object of userFunctions class
		userFunction= new UserFunctions();
		utils 		= new Utils(getActivity());
		mItems 		= new ArrayList<HashMap<String, String>>();

		// Get Bundle data
		Bundle bundle = this.getArguments();
		mCategoryId   = bundle.getString(utils.EXTRA_CATEGORY_ID);    

		if(utils.isNetworkAvailable()){	
			new loadFirstListView().execute();
		} else {
			lblNoResult.setVisibility(View.GONE);
    		lytRetry.setVisibility(View.VISIBLE);
    		lblAlert.setText(R.string.no_connection);
		}	
				        
		// Listener to get selected id when list item clicked
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, String> item = new HashMap<String, String>();
		        item = mItems.get(position);
				
				// Pass id to onListSelected method on HomeActivity
				mCallback.onListSelected(item.get(userFunction.key_c_id));

				// Set the item as checked to be highlighted when in two-pane layout
				list.setItemChecked(position, true);
			}
		});
		
		// Set loadmore
		list.setHasMoreItems(true);
		list.setPagingableListener(new PagingListView.Pagingable() {
			@Override
			public void onLoadMoreItems() {
				if(utils.isNetworkAvailable()){	
					json = null;
					if(paramLoadmore==1){
						new loadMoreListView().execute();
					} else {
						list.onFinishLoading(false, null);
					}
	                
				} else {
					Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return v;
	}
	
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // The callback interface. If not, it throws an exception.
        try {
            mCallback = (OnDataListSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
	
	// Load first 10 videos
	private class loadFirstListView extends AsyncTask<Void, Void, Void> {
		 
		Dialog dialog;
        @Override
        protected void onPreExecute() {

			/*dialog = new Dialog(getActivity());

			dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			dialog.setContentView(R.layout.loading_layout);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			dialog.show();*/
        	
        	dialog = Utils.createProgressDialog(getActivity());
        	dialog.setCancelable(false);
        	dialog.show();
        }
        protected Void doInBackground(Void... unused) {
        	getDataFromServer();
        	return (null);
        }
 
        protected void onPostExecute(Void unused) {
            
        	if(isAdded()){
        		
	            if(mItems.size() != 0){
	            	lytRetry.setVisibility(View.GONE);
	            	lblNoResult.setVisibility(View.GONE);
	            	list.setVisibility(View.VISIBLE);
	            	
	            	// Getting adapter
	            	la = new AdapterHome(getActivity(), mItems);
		            
	            	if(list.getAdapter() == null){
		            	list.setAdapter(la);
		            }
	            	
	            } else {
	            	if(json != null){
						lblNoResult.setVisibility(View.VISIBLE);
	            		lytRetry.setVisibility(View.GONE);
	            		
		            } else {
						lblNoResult.setVisibility(View.GONE);
	            		lytRetry.setVisibility(View.VISIBLE);
	            		lblAlert.setText(R.string.error_server);
	            	}
	            }
        	}
        
        	// Closing progress dialog
        	dialog.dismiss();
        }
    }
	
	// Load more videos
    private class loadMoreListView extends AsyncTask<Void, Void, Void> {
    	
        @Override
        protected void onPreExecute() {

        }
 
        protected Void doInBackground(Void... unused) {
        	
			// Store previous value of current page
			mPreviousPage = mCurrentPage;
            // Increment current page
			mCurrentPage += FragmentHome.paramValueItemPerPage;
			getDataFromServer();
            return (null);
        }
 
        protected void onPostExecute(Void unused) {
 
            if(json != null){
            	// Get listview current position - used to maintain scroll position
	            int currentPosition = list.getFirstVisiblePosition();
	
            	lytRetry.setVisibility(View.GONE);
	            // Appending new data to mItems ArrayList
	            la = new AdapterHome(
	                    getActivity(),
	                    mItems);
	            
	            if(list.getAdapter() == null){
	            	list.setAdapter(la);
	            }
	            
	            // Setting new scroll position
	            list.setSelectionFromTop(currentPosition + 1, 0);
	            list.onFinishLoading(true, mItems);

            } else {
            		mCurrentPage = mPreviousPage;
            		Toast.makeText(getActivity(), R.string.error_server, Toast.LENGTH_SHORT).show();
            }
            // Closing progress dialog
           
        }
    }
	
	public void getDataFromServer(){
	       
        try {
        	String tokenKey = sharedPreferences.getString("tokenKey", null);
        	String deviceId = sharedPreferences.getString("deviceId", null);
        	String userLat = sharedPreferences.getString("userLat", null);
        	String userLong = sharedPreferences.getString("userLong", null);
        	
        	json = userFunction.dealByCategory(mCategoryId,userLat,userLong, mCurrentPage,tokenKey,deviceId);
        	
            if(json != null){
            	JSONArray dataDealsArray  = json.getJSONArray(userFunction.array_latest_deals);
            	
            	// Get Count_Total from server
            	int mCountTotal = Integer.valueOf(json.getString(userFunction.key_total_data));

            	// Because it array, it start from 0 not 1, so mCountTotal - 1
            	mCountTotal-=1;
            	
            	/* Conditional if mCountTotal equal or more than mCurrentPage it means 
            	all data from server is already load */
            	if(((mCountTotal-=FragmentHome.paramValueItemPerPage)<mCurrentPage)){
            		paramLoadmore=0;
            	} else {
            		paramLoadmore=1;
            	}
            	
	            intLengthData = dataDealsArray.length();
	            mDealsId 		= new String[intLengthData];
	            mCId            = new String[intLengthData];
	            mTitle 			= new String[intLengthData];
	            mDateEnd 		= new String[intLengthData];
	            mOfferDetail = new String[intLengthData];
	            //mStartValue 	= new String[intLengthData];
	            mImg 			= new String[intLengthData];
	            mIcMarker 		= new String[intLengthData];
	            merchantId      = new String[intLengthData];
	            mValidUptoDate  = new String[intLengthData];
	            mDealPoint      = new String[intLengthData];
	            mLatitude       = new String[intLengthData];
	            mLongitude       = new String[intLengthData];
	            mStoreName        = new String[intLengthData];
	            mAddress        = new String[intLengthData];
	            mReviewCount = new String[intLengthData];
				mViewCount = new String[intLengthData];
				mGetCount = new String[intLengthData];
				mBuyCount = new String[intLengthData];
				mDayRestrict= new String[intLengthData];
	            
	            for (int i = 0; i < intLengthData; i++) {
	            	// Store data from server to variable
	            
	            	JSONObject dealsObject = dataDealsArray.getJSONObject(i);
	            	HashMap<String, String> map = new HashMap<String, String>();
	            	mDealsId[i] = dealsObject
							.getString(userFunction.key_deals_id);
					mCId[i] = dealsObject.getString(userFunction.key_c_id);
					mTitle[i] = dealsObject
							.getString(userFunction.key_deals_title);
					mDateEnd[i] = dealsObject
							.getString(userFunction.key_deals_date_end);
					mOfferDetail[i] = dealsObject
							.getString(userFunction.key_deals_Offer_detail);
					// mStartValue[i] =
					// dealsObject.getString(userFunction.key_deals_start_value);
					mImg[i] = dealsObject
							.getString(userFunction.key_deals_image);
					mIcMarker[i] = dealsObject
							.getString(userFunction.key_category_marker);
					merchantId[i] = dealsObject
							.getString(userFunction.key_merchant_id);
					mValidUptoDate[i] = dealsObject
							.getString(userFunction.key_valid_date);
					mDealPoint[i] = dealsObject
							.getString(userFunction.key_deal_point);
					mLatitude[i] = dealsObject
							.getString(userFunction.key_latitudet);
					mLongitude[i] = dealsObject
							.getString(userFunction.key_longitude);
					mStoreName[i]= dealsObject
							.getString(userFunction.key_store_name);
					mAddress[i]= dealsObject
							.getString(userFunction.key_address);
					
					mReviewCount[i] = dealsObject
							.getString(userFunction.key_starCount);
					mViewCount[i] = dealsObject
							.getString(userFunction.key_view_count);
					mGetCount[i] = dealsObject
							.getString(userFunction.key_get_count);
					mBuyCount[i] = dealsObject
							.getString(userFunction.key_buy_count);
					
					 mDayRestrict[i] = dealsObject
								.getString(userFunction.key_day_restrict);
					map.put(userFunction.key_deals_id, mDealsId[i]); // id not
																		// using
																		// any
																		// where
					map.put(userFunction.key_c_id, mCId[i]);
					map.put(userFunction.key_deals_title, mTitle[i]);
					map.put(userFunction.key_deals_date_end, mDateEnd[i]);
					
					 map.put(userFunction.key_deals_Offer_detail,
					 mOfferDetail[i]);
					
					map.put(userFunction.key_deals_image, mImg[i]);
					map.put(userFunction.key_category_marker, mIcMarker[i]);
					
					map.put(userFunction.key_merchant_id, merchantId[i]);
					map.put(userFunction.key_valid_date, mValidUptoDate[i]);
					map.put(userFunction.key_deal_point, mDealPoint[i]);
					map.put(userFunction.key_latitudet, mLatitude[i]);
					map.put(userFunction.key_longitude, mLongitude[i]);
					map.put(userFunction.key_store_name, mStoreName[i]);
					map.put(userFunction.key_address, mAddress[i]);
					map.put(userFunction.key_starCount, mReviewCount[i]);
					map.put(userFunction.key_view_count, mViewCount[i]);
					map.put(userFunction.key_get_count, mGetCount[i]);
					map.put(userFunction.key_buy_count, mBuyCount[i]);
					map.put(userFunction.key_day_restrict, mDayRestrict[i]);
		         // Adding HashList to ArrayList
		            mItems.add(map);
		            
	           
	            }
            }
                                
        } catch (JSONException e) {
            // TODO Auto-generated catch block
        	Log.i("FragmentHome", "getDataFromServer: "+e);
        }      
    }

    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	list.setAdapter(null);
    	super.onDestroy();
    	
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnRetry:
			if(utils.isNetworkAvailable()){	
				json = null;
				new loadFirstListView().execute();
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
}
