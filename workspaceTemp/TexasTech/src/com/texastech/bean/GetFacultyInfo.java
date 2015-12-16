package com.texastech.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetFacultyInfo {
	
	public boolean success;
	
	@SerializedName("result")
	public List<FacultyInfo> contactsInfosList;

	public class FacultyInfo implements Serializable ,Comparable<FacultyInfo>{

		public String ID;
		public String Campus;
		public String Title;
		public String Rank;
		public String FirstName;
		public String LastName;
		public String Phone;
		public String Email;
		public String URL;
		public String Image;
		public String campus_id;
		public String campus_name;
		
		public boolean isHeader = false; 
		
		/*@Override
		public String toString() {
			return LastName;
		}*/

		@Override
		public int compareTo(FacultyInfo another) {
			//return Title-another.Title;
			return this.LastName.compareTo(another.LastName);
		}
	}
}
