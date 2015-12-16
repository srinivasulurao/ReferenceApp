package com.texastech.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetEvetntInfo {
	
	//private static DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public boolean success;
	
	@SerializedName("result")
	public List<EvetntInfo> evetntInfosList;

	public class EvetntInfo implements Serializable,Comparable<EvetntInfo>{
		
		public String ID;
		public String Date;
		public String Title;
		public String Description;
		public String Address;
		public String Longitude;
		public String Latitude;
		public String Icon;
		public String UploadURL;
		
		public boolean isHeader= false; 

		@Override
		public int compareTo(EvetntInfo another) { 
			try {
				DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return f.parse(Date).compareTo(f.parse(another.Date));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return 0;
		}
	}
}
