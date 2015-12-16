package com.texastech.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetSplashScreensInfo {
	
	public boolean success;
	
	@SerializedName("result")
	public List<SplashScreensInfo> screensInfosList;

	public class SplashScreensInfo implements Serializable {
		public String splashScreenId;
		public String conferenceID;
		public String deviceType;
		public String deviceResolution;
		public String icon;
		public String createdOn;
		public String background1;
		public String background2;
		public String backbutton;
		public String titlebar;
		public String titlebartextcolor;
		public String id;
		public String device_type;
		public String resolution;
		public String dt_id;
		public String dr_id;

	}

}
