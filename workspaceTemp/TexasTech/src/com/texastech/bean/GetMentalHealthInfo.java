package com.texastech.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetMentalHealthInfo {
	
	@SerializedName("result")
	public List<MentalHealthInfo> mentalHealthInfosList;
	
	public class MentalHealthInfo implements Serializable ,Comparable<MentalHealthInfo>{
		public String ID;
		public String Campus;
		public String Address;
		public String Longitude;
		public String Latitude;
		public String Details;
		public String ContactPerson;
		public String Phone;
		public String Email;
		
		public String Details2;
		public String Details3;
		public String SubmittedBy;
		public String pdf_document;
		
		
		@Override
		public String toString() {
			return Campus;
		}
		
		@Override
		public int compareTo(MentalHealthInfo another) {
			//return Title-another.Title;
			return this.Campus.compareTo(another.Campus);
		}

	}	

}
