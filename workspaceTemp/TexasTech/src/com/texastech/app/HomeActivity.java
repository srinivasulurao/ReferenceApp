package com.texastech.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;

import com.texastech.activity.ContactsActivity;
import com.texastech.activity.EventsActivity;
import com.texastech.activity.FacultyDirectoryActivity;
import com.texastech.activity.MentalHealthResourcesActivity;
import com.texastech.activity.NotesActivity;
import com.texastech.activity.PushNotificationActivity;
import com.texastech.activity.SocialMediaActivity;
import com.texastech.activity.StudentResourcesActivity;
import com.texastech.activity.VirtualToursActivity;
import com.texastech.adapter.NavListAdapter;
import com.texastech.bean.GetHomeScreenInfo;
import com.texastech.bean.GetHomeScreenInfo.HomeScreenInfo;
import com.texastech.helper.AlertManager;
import com.texastech.helper.AppConstant;
import com.texastech.helper.AppUtil;
import com.texastech.helper.FileUtil;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class HomeActivity extends BaseActivity implements HttpTaskListener{

	@InjectView(R.id.pane)
	SlidingPaneLayout mPane;
	
	@InjectView(R.id.drawer_list)
	ListView listView;
	
	@Override
	public void setTitle(TextView tv) {
		
	}

	@Override
	protected void initXmlView() {
		ImageView ivHeaderBack = (ImageView)findViewById(R.id.header_background_nav_list);
		MyApplication.getLoader().displayImage(IMAGE_URL+settingPre.getTitleBarUrl(), ivHeaderBack, AppUtil.getImageSetting());
		
		listView.setOnItemClickListener(navListener);
		
		List<HomeScreenInfo> screenInfos = new ArrayList<HomeScreenInfo>();
		String response = FileUtil.readFile(getApplicationContext(), Action.GET_HOME_SCREEN+".txt");
		GetHomeScreenInfo getHomeScreenInfo = gson.fromJson(response, GetHomeScreenInfo.class);
		for (HomeScreenInfo screenInfo : getHomeScreenInfo.homeScreensList) {
			if(screenInfo.Active.equals("1"))screenInfos.add(screenInfo);
		}
		Collections.sort(screenInfos);
		//----------------------------------//
		MenuFragment fragment = MenuFragment.instance(screenInfos);
		getSupportFragmentManager().beginTransaction()
		.add(R.id.contain_fragment, fragment, MenuFragment.TAG)
		.commit(); 
		
		listView.setAdapter(new NavListAdapter(getApplicationContext(),screenInfos,R.layout.row_navigation_list));
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_home);
	}
 
	
	
	
	
	public OnItemClickListener navListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			HomeScreenInfo screenInfo = (HomeScreenInfo)arg0.getItemAtPosition(arg2);
			String title = screenInfo.Title;
			
			switch (Integer.parseInt(screenInfo.TypeID)) {
				case 1: // Contacts
					pushMenuActivity(ContactsActivity.class, title);
				break;
				// -------------------//
				case 2:// Events
					pushMenuActivity(EventsActivity.class, title);
				break;
				// -------------------//
				case 3:// Faculty Directory
					pushMenuActivity(FacultyDirectoryActivity.class, title);
				break;
				// -------------------//
				case 4:{// Campuses                        
					//pushMenuActivity(CampusesActivity.class, title);
					Intent intent = new Intent(getApplicationContext(), WebActivity.class);
					intent.putExtra("URL", "http://appddiction.way.wf/m/");
					intent.putExtra("TITLE", title);
					startActivity(intent);
				}	
				break;
				// -------------------//
				case 5:// Social Media Integration
					pushMenuActivity(SocialMediaActivity.class, title);
				break;
				// -------------------//
				case 6:// Virtual Tours
					pushMenuActivity(VirtualToursActivity.class, title);
				break;
				// -------------------//
				case 7:// Student Health Resources
					pushMenuActivity(StudentResourcesActivity.class, title);
				break;
				//-------------------//
				case 8://Mental Resources 
					pushMenuActivity(MentalHealthResourcesActivity.class, title);
				break;
				//-------------------//
				case 9://Notes
					pushMenuActivity(NotesActivity.class, title);
				break;
				//-------------------//
				case 10:{//Profile
					pushMenuActivity(SignUpActivity.class, title);
				}	
				break;
				//-------------------//
				case 11://Log Out
					AppConstant.loginUser=null;
					finish();
				break;
				//-------------------//
				case -1://push notification
					pushMenuActivity(PushNotificationActivity.class, title);
				break;
			}//end of switch
			
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					closeDrawer();
				}
			}, 200);
		}
	};
	
	
	public void openDrawer(){
		try {
			if(mPane.isOpen())
				mPane.closePane();
			
			else
				mPane.openPane();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeDrawer(){
		try {
			mPane.closePane();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() { 
		AlertManager.showExitAppAlert(this);
	}
	
	
	public void pushMenuActivity(Class class1, String title){
		Intent intent = new Intent(getApplicationContext(), class1);
		intent.putExtra("TITLE", title);
		startActivity(intent);
	}

	@Override
	public void sendHttpRequest(Action ac, String... param) {
		showProgressBar();
		RestClient client = new RestClient(ac, this);
		client.get();
	}

	@Override
	public void onSuccess(Action ac, String response) {
		if(!isActivityVisible)return;
		try {
			GetHomeScreenInfo getHomeScreenInfo = gson.fromJson(response, GetHomeScreenInfo.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		dismissProgressBar();
	}

	@Override
	public void onFaliure(Action ac, String error) {
		if(!isActivityVisible)return;
		showMessage("Alert!", error);
		dismissProgressBar();
	}
}
