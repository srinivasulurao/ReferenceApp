package com.texastech.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetHomeScreenInfo {
	
	@SerializedName("result")
	public List<HomeScreenInfo> homeScreensList;
	
	public static class HomeScreenInfo implements Serializable , Comparable{
		public String ID;
		public String TypeID;
		public String Title;
		public String IconIphone;
		public String IconAndroid;
		public String IconTablet;
		public int SortOrder;
		public String Active;
		
		@Override
		public int compareTo(Object another) {
			int compareage=Integer.parseInt(((HomeScreenInfo)another).TypeID);
	        /* For Ascending order*/
	        return Integer.parseInt(TypeID)-compareage;
		}
	}
}
