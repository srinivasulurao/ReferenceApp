

package dealsforsure.in;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import dealsforsure.in.R;
import dealsforsure.in.ads.Ads;
import dealsforsure.in.fragments.FragmentCategoryList;
import dealsforsure.in.utils.Utils;

public class ActivityCategoryList extends ActionBarActivity implements 
	FragmentCategoryList.OnDataListSelectedListener{
	
	// Create an instance of ActionBar
	private ActionBar actionbar;
	
	// Declare object of AdView class
	private AdView adView;
	
	// Declare object of Utils class
	private Utils utils;
	
	// Declare variable to store data
	private String mCategoryId, mCategoryName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		// Declare object of Utils class
		utils = new Utils(this);
		
		// connect view objects and xml ids
		adView = (AdView)this.findViewById(R.id.adView);
		
		// Get intent Data from ActivityCategory
        Intent i = getIntent();
        mCategoryId   = i.getStringExtra(utils.EXTRA_CATEGORY_ID);
        mCategoryName = i.getStringExtra(utils.EXTRA_CATEGORY_NAME);
       
     	// Get ActionBar and set back private Button on actionbar
 		actionbar = getSupportActionBar();
 		actionbar.setDisplayHomeAsUpEnabled(true);	
 		actionbar.setTitle(mCategoryName);

        /* In case this activity was started with special instructions from an Intent,
        Pass the Intent's extras to the fragment as arguments */
       Bundle bundle = new Bundle();
       bundle.putString(utils.EXTRA_CATEGORY_ID, mCategoryId);
       FragmentCategoryList fragObjList = new FragmentCategoryList();
       fragObjList.setArguments(bundle);

       // Add the fragment to the 'fragment_container' FrameLayout
       getSupportFragmentManager().beginTransaction()
       	.add(R.id.frame_content, fragObjList).commit();
     	
		/* CHECK_PLAY_SERV = 1 means Google Play services version on the device 
	    supports the version of the client library you are using */
		if(utils.loadPreferences(utils.CHECK_PLAY_SERV)==1){
			
			// Check the connection
			if(utils.isNetworkAvailable()){
	            // Condition for admob (0=gone, 1=visible)
	            if(utils.paramAdmob==1){
	            	
	            	adView.setVisibility(View.VISIBLE);
	            	// load ads
		            Ads.loadAds(adView);
	            }
			} else {
				Toast.makeText(this, getString(R.string.internet_alert), Toast.LENGTH_SHORT).show();
			}
		}
	
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

	// Listener For list selected
	@Override
	public void onListSelected(String mIdSelected) {
		// TODO Auto-generated method stub
		
		// Call ActivityDetailPlace
		Intent i = new Intent(this, ActivityDetail.class);
		i.putExtra(utils.EXTRA_DEAL_ID, mIdSelected);
		startActivity(i);
		overridePendingTransition (R.anim.open_next, R.anim.close_main);
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition (R.anim.open_main, R.anim.close_next);
		
	}
	
}
