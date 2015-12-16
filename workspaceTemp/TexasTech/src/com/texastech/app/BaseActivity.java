package com.texastech.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.texastech.helper.AlertManager;
import com.texastech.helper.Anim;
import com.texastech.helper.AppConstant;
import com.texastech.helper.MLog;
import com.texastech.helper.MemoryAnalyzer;
import com.texastech.helper.SettingPreference;
import com.texastech.helper.UnbindView;
import com.texastech.httputil.HttpConst;

 
public abstract class BaseActivity extends FragmentActivity implements HttpConst{
	
	public ImageLoader imageLoader; 
	
	@Override
	public void setContentView(int layoutResID) {                
		super.setContentView(layoutResID);
		ButterKnife.inject(this);
		TextView tv= (TextView)findViewById(R.id.tv_header);
		if(tv!=null){
			tv.setTextColor(Color.parseColor(settingPre.getTitleColor()));
			setTitle(tv);
		}
		
		/*ImageView ivHeaderBack = (ImageView)findViewById(R.id.header_background);
		if(ivHeaderBack!=null){
			MyApplication.getLoader().displayImage(IMAGE_URL+settingPre.getTitleBarUrl(), ivHeaderBack, AppUtil.getImageSetting());
		}*/
		
		/*ImageButton btnBack = (ImageButton)findViewById(R.id.btn_back);
		if(btnBack!=null){
			MyApplication.getLoader().displayImage(IMAGE_URL+settingPre.getBackButtonUrl(), btnBack, AppUtil.getImageSetting());
		}*/
		initXmlView();
	}
	
	
	public abstract void setTitle(TextView tv);
	
	protected abstract void initXmlView();
	
	
	public ProgressDialog dialog;
	protected Context context;
	private Handler handler  = new Handler();
	
	public Gson gson = new Gson();
	
	/** Check whether your activity is running in front or background */
	public boolean isActivityVisible = true;
	
	public SettingPreference settingPre;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		imageLoader = MyApplication.getLoader();
		context = BaseActivity.this;
		settingPre = new SettingPreference(context);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		// prepare for a progress bar dialog
		dialog = new ProgressDialog(context);
		dialog.setMessage("Please wait....");
		dialog.setCancelable(true);
		//dialog.setIndeterminate(true);
	}
	
	public void hideSofeKeyboard(EditText et){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
	
	
	public void toastMessage(String message){
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
	
	public void pushActivity(Class class1, boolean isFinish){
		startActivity(new Intent(getApplicationContext(), class1));
		if(isFinish)finish();
	}
	
	
	public boolean isValidInput(EditText et){
		return !TextUtils.isEmpty(et.getText().toString().trim());
	}
	
	
	public final void setAppFont(ViewGroup mContainer, Typeface mFont){
	    if (mContainer == null || mFont == null) return;
	    final int mCount = mContainer.getChildCount();
	    // Loop through all of the children.
	    for (int i = 0; i < mCount; ++i){
	        final View mChild = mContainer.getChildAt(i);
	        
	        if (mChild instanceof TextView){
	            // Set the font if it is a TextView.
	            ((TextView) mChild).setTypeface(mFont);  
	        }
	        else if (mChild instanceof EditText){
	            ((EditText) mChild).setTypeface(mFont); 
	        }
	        else if (mChild instanceof ViewGroup){
	            // Recursively attempt another ViewGroup.
	            setAppFont((ViewGroup) mChild, mFont);
	        }
	     }
	}

	
	/**
	 * start the progress dialog if not showing.
	 */
	public void showProgressBar(){
		try {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if(dialog !=null && !dialog.isShowing())
						dialog.show();
				}
			} ,0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * dismiss the progress dialog if showing.
	 */
	public void dismissProgressBar(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if(dialog !=null && dialog.isShowing()){
						dialog.dismiss();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	/**
	 * 
	 * @param view
	 */
	public void viewAnimation(View view){
		Anim.runAlphaAnimation(this, view);
	}
	
	/**
	 * Show alert message in case if your application is running under low memory.
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		MemoryAnalyzer.freeMemory();
	}
 

	
	
	/**
	 * Display a simple alert dialog with the given text and title.
	 * @param title
	 * @param message
	 */
	public void showMessage(String title , String message){
		AlertManager.showMessage(context, title, message);
	}
 
	
	
	/**
	 * Called when this activity is no longer visible.
	 * Calculate the current heap size of running application.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		MLog.v(getLocalClassName()+" HeapSize :- ", MemoryAnalyzer.getHeapSize());
	}
	
	
	
	/** Set your current running activity is in front .
	 * This method is called after onStart() method and if your activity is the 
	 * foreground activity on the screen.
	 * **/
	@Override 
	protected void onResume() {
		super.onResume();
		isActivityVisible = true;
		MLog.v(getLocalClassName(), "onResume");
	}

	
	/** Set your current running activity is in background.
	 * This method is called when your activity is just about to call another activity so that the 
	 * current activity has to be paused and the new activity has to be resumed. Here the previous 
	 * activity is not stopped but it loss the foreground visibility means it goes as background activity. 
	 * **/
	@Override
	protected void onPause() {
		super.onPause();
		isActivityVisible = false;
		MLog.v(getLocalClassName(), "onPause");
	}
	
	
	/**
	 * Back key event of header layout , define in header.xml file.
	 * @param v
	 */
	public void onBackEvent(View v){
		Anim.runAlphaAnimation(this, v);
		finish();
	}
	
	
	/**
	 * Unbind xml layout view for memory managment(out of memory bitmap exceeds VM budget)
	 * Remove the callback for the cached drawables or we leak the previous screen on orientation change.
	 * @param view	:	parent view of xml layout.
	 */
    protected void unbindDrawables(View view) { 
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}
    
    
    /**
     * This method is called when your current activity has the last chance 
     * to do any processing before it is destroyed.
     */
    @Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			UnbindView.unbindReferences(findViewById(R.id.parent_layout));
			MemoryAnalyzer.freeMemory();
			MLog.v(getLocalClassName(), "onDestroy");
			ButterKnife.reset(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    
     
    @Override
    protected void onRestart() {
    	super.onRestart();
    	if(AppConstant.isUserLogout){
    		if(this instanceof HomeActivity)
    			AppConstant.isUserLogout = false;
    		else finish();
    	}
    }
    
    public int getDimenValue(int value){
    	return (int) (getResources().getDimension(value) / getResources().getDisplayMetrics().density);
    }
    
    
    public int getResourceId(String name){
		MLog.v("File Name", ""+name);
		int resId = this.getResources().getIdentifier(name, "drawable", getPackageName());
		return resId;
	}
}
