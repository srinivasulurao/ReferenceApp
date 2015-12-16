package com.texastech.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetProfileInfo {
	
	@SerializedName("result")
	public List<ProfileInfo> profileInfos;
	
	public class ProfileInfo implements Serializable{
		
		public int id;
		public int username;
		public String message;
		public String createdDate;
		public String modifiedDate;
		public String GUID;
		public String status;
		public String sent;
		public String Password;
		public String ScreenName;
		
		//public String photo;
		public String FirstName;
		public String LastName;
		public String Email;
		public String RN_No;
		public String Role;
		public String Program;
		public String Graduation_Date;
		public String Graduation_Month;
		public String Graduation_Year;
		
		public String MiddleInitial;
		public String User_image_URL;
		public String DOB;
		public String About;
		public String status_type;
		
	}
}
