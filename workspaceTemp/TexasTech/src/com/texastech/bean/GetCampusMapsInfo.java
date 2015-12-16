package com.texastech.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetCampusMapsInfo {
	
	@SerializedName("result")
	public List<CampusMapsInfo> campusMapsInfosList;

	public class CampusMapsInfo implements Serializable {
		public String map_id;
		public String campus_id;
		public String map_title;
		public String call_us;
		public String directions;
		public String email_id;
		public String map_link;
		public String latitude;
		public String longitude;
		public String map_image;
		public String Campus_Name;
		
		@Override
		public String toString() {
			return map_title;
		}

	}
}
