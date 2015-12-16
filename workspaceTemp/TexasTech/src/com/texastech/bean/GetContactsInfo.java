package com.texastech.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetContactsInfo {
	
	public boolean success;
	
	@SerializedName("result")
	public List<ContactsInfo> contactsInfosList;

	public class ContactsInfo implements Serializable ,Comparable<ContactsInfo>{

		public String ID;
		public String Campus;
		public String Department;
		public String Title;
		public String Name;
		public String Phone;
		public String Email;
		public String URL;
		public String Image;
		public String campus_id;
		public String campus_name;
		
		public boolean isHeader = false; 
		
		@Override
		public String toString() {
			return Title;
		}

		@Override
		public int compareTo(ContactsInfo another) {
			//return Title-another.Title;
			return this.Title.compareTo(another.Title);
		}
	}
}
