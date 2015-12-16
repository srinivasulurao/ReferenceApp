package com.texastech.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetSocialMediaInfo {
	
	@SerializedName("result")
	public List<SocialMediaInfo> socialMediaInfosList;
	
	public class SocialMediaInfo implements Serializable{
		public String ID;
		public String Title;
		public String URL;
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return Title;
		}
	}
}
