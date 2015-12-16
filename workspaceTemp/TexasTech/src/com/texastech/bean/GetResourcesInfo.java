package com.texastech.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetResourcesInfo {

	@SerializedName("result")
	public List<ResourcesInfo> resourcesInfosList;

	public class ResourcesInfo implements Serializable {
		public String ID;
		public String Title;
		public String TeaserSentence;
		public String Latitude;
		public String Longitude;
		public String Address;
		public String DateTime;
		public String SubmittedBy;
		public String UploadURL;

		@Override
		public String toString() {
			return TeaserSentence;
		}
		
		 
	}

}
