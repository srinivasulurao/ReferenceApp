package dealsforsure.in.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import dealsforsure.in.ActivityDetail;
import dealsforsure.in.ActivityProfile;
import dealsforsure.in.R;
import dealsforsure.in.ActivityAddDeals.MyDialogFragment;
import dealsforsure.in.adapters.AdapterAddress;
import dealsforsure.in.adapters.AdapterHome;
import dealsforsure.in.adapters.AdapterSearchSuggestion;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.loadmore.PagingListView;
import dealsforsure.in.model.AddressObj;
import dealsforsure.in.model.Deals;
import dealsforsure.in.model.SuggestionKeyword;
import dealsforsure.in.utils.Utils;
public class FragmentHome extends Fragment implements OnClickListener,SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	
	// Create interface for ListFragment
	private OnDataListSelectedListener mCallback;
	
	private ArrayList<HashMap<String, String>> mItems;
	private ProgressDialog pDialog;

    // Declare object of userFunctions and Utils class
	private UserFunctions userFunction;
	private Utils utils;
	
	// Create instance of list and ListAdapter

	private AdapterHome la;
	private PagingListView list;
	
	AdapterSearchSuggestion adapterSearchSuggestion;
	

	// Declare view objects
	private TextView lblNoResult, lblAlert,lblsearch,tvclose;
	private Button btnRetry; 
	private LinearLayout lytRetry;
	RelativeLayout llsearch;
    // flag for current page
	private JSONObject json, jsonCurrency;
	private int mCurrentPage = 0;
	private int mPreviousPage;
	ListView lvSuggestions;
	
	String loadType="normal";
	
	SearchView svDeal;
	// create array variables to store data
    private String[] mDealsId;
    private String[] mCId;
    private String[] mTitle;
    private String[] mDateEnd;
    private String[] mOfferDetail;
   // private String[] mAfterDiscValue;
   // private String[] mStartValue;
    private String[] mImg;
    private String[] mIcMarker;
    private String[] merchantId;
    private String[] mValidUptoDate;
    private String[] mDealPoint;
    private String[] mLatitude;
    private String[] mLongitude;
    private String[] mAddress;
    private String[] mStoreName;
    private String[] mReviewCount;
    private String[] mViewCount;
    private String[] mGetCount;
    private String[] mBuyCount;
    private String[] mDayRestrict;
	private int intLengthData;
	String tokenKey,deviceId,userId;
	 SharedPreferences sharedPreferences;
	 
	 List<SuggestionKeyword> suggestionList=new ArrayList<SuggestionKeyword>();
	 
	 SuggestionKeyword suggestionKeyword;
	
	// Using in menu who have grid to know how much item per page
	public static int paramValueItemPerPage=6;
	
	// To handle parameter loadmore gone or visible(1 = visible ; 0 = gone)
	private int paramLoadmore=0;
	
	private int wPixDefault, hPixDefault;
	public static int hPixTarget, wPixTarget;
	private DisplayMetrics dm;
	
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
		llsearch =(RelativeLayout)v.findViewById(R.id.llsearch);
		lblsearch= (TextView) v.findViewById(R.id.lblsearch);
		tvclose= (TextView) v.findViewById(R.id.tvclose);
		btnRetry.setOnClickListener(this);
		
		// Declare object of userFunctions class
		userFunction= new UserFunctions();
		utils 		= new Utils(getActivity());
		mItems 		= new ArrayList<HashMap<String, String>>();
		
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		
		
		
	    tokenKey = sharedPreferences.getString("tokenKey", null);
	    deviceId = sharedPreferences.getString("deviceId", null);
		userId = sharedPreferences.getString("userId", null);
	    tvclose.setVisibility(View.GONE);
		// Setting value item per page
		//setItemPerPage();

		if(utils.isNetworkAvailable()){	
			new loadFirstListView().execute();
		} else {
			lblNoResult.setVisibility(View.GONE);
    		lytRetry.setVisibility(View.VISIBLE);
    		lblAlert.setText(R.string.no_connection);
		}	
		
		llsearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getFragmentManager()
						.beginTransaction();
				SearchDialogFragment frag = new SearchDialogFragment() ;
				frag.show(ft, "txn_tag");
				
			}
		});
		
		tvclose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 tvclose.setVisibility(View.GONE);
					lblsearch.setText(getResources().getString(R.string.search_text));
					list.setHasMoreItems(true);
				if(utils.isNetworkAvailable()){	
					new loadFirstListView().execute();
				} else {
					lblNoResult.setVisibility(View.GONE);
		    		lytRetry.setVisibility(View.VISIBLE);
		    		lblAlert.setText(R.string.no_connection);
				}	
				 
				
				
			}
		});
				        
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
        	
        	mItems.clear();
        	loadType="normal";
        	mCurrentPage=0;
        	//list.setAdapter(null);
        	getDataFromServer("normal");
        	return null;
        }
 
        protected void onPostExecute(Void unused) {
            
        	if(isAdded()){
        		
	            if(mItems.size() != 0){
	            	lytRetry.setVisibility(View.GONE);
	            	lblNoResult.setVisibility(View.GONE);
	            	list.setVisibility(View.VISIBLE);
	            	
	            	// Getting adapter
	            	la = new AdapterHome(getActivity(), mItems);
		            
	            	//if(list.getAdapter() == null){
		            	list.setAdapter(la);
		            //}
	            	
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
	
	private class loadSearchListView extends AsyncTask<Void, Void, Void> {
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
        	mItems.clear();
        	loadType="search";
        	mCurrentPage=0;
        	getDataFromServer("search");
        	return null;
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
			mCurrentPage += paramValueItemPerPage;
			getDataFromServer( loadType);
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
           // if(pDialog.isShowing()) pDialog.dismiss();
            	
			
        }
    }
	
	public void getDataFromServer(String type){
	       
        try {
        
        	String userLat = sharedPreferences.getString("userLat", null);
        	String userLong = sharedPreferences.getString("userLong", null);
        	
        	String deviceId = Secure.getString(getActivity().getContentResolver(),
                     Secure.ANDROID_ID);
        	
        	
    		
    		
        	jsonCurrency = userFunction.currency();
        	
        	if(type.equals("search")){
        		
			json = userFunction.searchDeals(mCurrentPage,userLat,userLong,deviceId,tokenKey,suggestionKeyword);
        	}else{
        		
        		json = userFunction.latestDeals(mCurrentPage,userLat,userLong,deviceId,userId);
        		
        	}
			
            if(json != null){
            	JSONArray dataDealsArray;
            	
            	dataDealsArray = json.getJSONArray(userFunction.array_latest_deals);
            	
            	// Get Count_Total from server
            	int mCountTotal = Integer.valueOf(json.getString(userFunction.key_total_data));

            	// Because it array, it start from 0 not 1, so mCountTotal - 1
            	mCountTotal-=1;
            	
            	/* Conditional if mCountTotal equal or more than mCurrentPage it means 
            	all data from server is already load */
            	if(((mCountTotal-=paramValueItemPerPage)<mCurrentPage)){
            		paramLoadmore=0;
            	} else {
            		paramLoadmore=1;
            	}
				SharedPreferences.Editor editor = sharedPreferences.edit();
				String tokenKey = json.getString("token_key").trim();

				if (tokenKey != null && tokenKey.length() > 0) {

					editor.putString("tokenKey", tokenKey);
				}
				editor.putString("deviceId", deviceId);

				String userPoints = json.getString("user_points");
				String merchantPoints = json.getString("merchant_points");

				if (userPoints != null && userPoints.length() > 0) {
					editor.putString("userPoint", userPoints);

				}
				if (merchantPoints != null && merchantPoints.length() > 0) {

					editor.putString("merchentPoint", merchantPoints);

				}

				editor.commit();
				
				
            	
				intLengthData = dataDealsArray.length();
				mDealsId = new String[intLengthData];
				mCId = new String[intLengthData];
				mTitle = new String[intLengthData];
				mDateEnd = new String[intLengthData];
				mOfferDetail = new String[intLengthData];
				// mStartValue = new String[intLengthData];
				mImg = new String[intLengthData];
				mIcMarker = new String[intLengthData];
				merchantId = new String[intLengthData];
				mValidUptoDate = new String[intLengthData];
				mDealPoint = new String[intLengthData];
				mLatitude = new String[intLengthData];
				mLongitude = new String[intLengthData];
				mAddress = new String[intLengthData];
				mStoreName = new String[intLengthData];
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
					
					mAddress[i]= dealsObject
							.getString(userFunction.key_address);
					mStoreName[i]= dealsObject
							.getString(userFunction.key_store_name);
					
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
					// map.put(userFunction.key_deals_start_value,
					// mStartValue[i]);
					map.put(userFunction.key_deals_image, mImg[i]);
					map.put(userFunction.key_category_marker, mIcMarker[i]);
					
					map.put(userFunction.key_merchant_id, merchantId[i]);
					map.put(userFunction.key_valid_date, mValidUptoDate[i]);
					map.put(userFunction.key_deal_point, mDealPoint[i]);
					map.put(userFunction.key_latitudet, mLatitude[i]);
					map.put(userFunction.key_longitude, mLongitude[i]);
					map.put(userFunction.key_address, mAddress[i]);
					map.put(userFunction.key_store_name, mStoreName[i]);
					map.put(userFunction.key_starCount, mReviewCount[i]);
					map.put(userFunction.key_view_count, mViewCount[i]);
					map.put(userFunction.key_get_count, mGetCount[i]);
					map.put(userFunction.key_buy_count, mBuyCount[i]);
					map.put(userFunction.key_day_restrict, mDayRestrict[i]);

		         // Adding HashList to ArrayList
		            mItems.add(map);
	            }
            }
            if(jsonCurrency !=null){
            	JSONArray currencyArray = jsonCurrency.getJSONArray(userFunction.array_currency);
	            JSONObject currencyObject 	= currencyArray.getJSONObject(0);
            	Utils.mCurrency	= " "+currencyObject.getString(userFunction.key_currency_code);
            }
                       
                               
        } catch (JSONException e) {
            // TODO Auto-generated catch block
        	Log.i("FragmentHome", "getDataFromServer: "+e);
        }      
    }
	
	
public class SearchDialogFragment extends DialogFragment {
		
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
			View root = inflater.inflate(R.layout.dialog_search, container,
					false);
			
			lvSuggestions = (ListView) root.findViewById(R.id.lvsuggestionlist);

			svDeal = (SearchView) root
					.findViewById(R.id.svsearch);
			svDeal.setIconifiedByDefault(false);
			svDeal.setOnQueryTextListener(FragmentHome.this);
			svDeal.setOnCloseListener(FragmentHome.this);
			
			suggestionList.clear();
			
			adapterSearchSuggestion = new AdapterSearchSuggestion(getActivity(),
					R.layout.adapter_address, suggestionList);

			lvSuggestions.setAdapter(adapterSearchSuggestion);

			lvSuggestions.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					suggestionKeyword=(SuggestionKeyword) parent.getItemAtPosition(position);

					lblsearch.setText(suggestionKeyword.getSuggestion());
					 tvclose.setVisibility(View.VISIBLE);
					SearchDialogFragment.this.dismiss();
					new loadSearchListView().execute();

				}

			});
			
			return root;
		}
}
	
	// Setting value item per page
	private void setItemPerPage(){
		/* Specify value item per page depend on screen size
 		 If paramValueItemPerPage not yet setting */
		paramValueItemPerPage = utils.loadPreferences(utils.ITEM_PAGE_LIST);
 		if(paramValueItemPerPage==Integer.valueOf(utils.VALUE_DEFAULT)){
 			// Getting size of display for setting image hieght
 	     	
 			dm = new DisplayMetrics();
 			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
 			wPixDefault = dm.widthPixels;
 			hPixDefault = dm.heightPixels;
 			
 			utils.savePreferences(utils.PARAM_WIDTH_PIX,wPixDefault);
    		utils.savePreferences(utils.PARAM_HIGHT_PIX,hPixDefault);
	    		
 			// Get screen size in inches
 			double x = Math.pow(wPixDefault/dm.xdpi,2);
 		    double y = Math.pow(hPixDefault/dm.ydpi,2);
 		    double screenInches = Math.sqrt(x+y);
 		   
 			// Scren size under 4"
			if((screenInches<utils.mMedium)){
				utils.savePreferences(utils.ITEM_PAGE_LIST, utils.mItemInMedium);
				paramValueItemPerPage=utils.mItemInMedium;
				
			// Scren size under 5"
			} else if((screenInches<utils.mLarge)){
				utils.savePreferences(utils.ITEM_PAGE_LIST, utils.mItemInLarge);
				paramValueItemPerPage=utils.mItemInLarge;
				
			// Scren size under or equal 7"	
			} else if((screenInches<=utils.mXlarge)){
				utils.savePreferences(utils.ITEM_PAGE_LIST, utils.mItemInXlarge);
				paramValueItemPerPage=utils.mItemInXlarge;
				
			// Scren size more than 7"	
			} else {
				utils.savePreferences(utils.ITEM_PAGE_LIST, utils.mItemInXXlarge);
				paramValueItemPerPage=utils.mItemInXXlarge;
			}			
		}
 		
 		/* To manage private Button load more, paramValueItemPerPage + 1 because amount data per page is paramValueItemPerPage - 2, 
         * so private Button loadmore appear because it certainly have other data to display.*/
 		paramValueItemPerPage+=1;
	}
	
	
	public boolean onQueryTextChange(String newText) {
		showResults(newText);
		return false;
	}

	public boolean onQueryTextSubmit(String query) {
		showResults(query);
		return false;
	}

	public boolean onClose() {
		showResults("");
		return false;
	}

	private void showResults(String query) {

		String suggtionname = query != null ? query.toString() : "@@@@";

		new SuggestionListAsy().execute(suggtionname);

	}
	
	private class SuggestionListAsy extends AsyncTask<String, Void, JSONObject> {
	
		
		@Override
        protected void onPreExecute() {
        	
        	
        }

		@Override
		protected JSONObject doInBackground(String... place) {
			
			String suggestion=place[0];
			JSONObject regjson = userFunction.getsuggestionList(suggestion,deviceId,tokenKey);

			// Store previous value of current page

			return regjson;
		}
		@Override
		protected void onPostExecute(JSONObject regjson) {
			try {
				
				if (regjson != null) {
					SuggestionKeyword suggestionKeyword;
					JSONObject suggestionObject;

					String status = regjson.getString("status");

					if (status.equals("200")) {

						JSONArray dataRegisterArray;
						suggestionList.clear();
						dataRegisterArray = regjson.getJSONArray("result");
						for (int i = 0; i < dataRegisterArray.length(); i++) {

							suggestionObject = dataRegisterArray.getJSONObject(i);
							
							suggestionKeyword=new SuggestionKeyword();
							
							suggestionKeyword.setId(suggestionObject.getString("id"));
							suggestionKeyword.setTableId(suggestionObject.getString("table_id"));
							suggestionKeyword.setSuggestion(suggestionObject.getString("keyword"));
							suggestionList.add(suggestionKeyword);
							
						}
						
						adapterSearchSuggestion.notifyDataSetChanged();
					}

				}
				
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
