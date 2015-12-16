package com.texastech.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class GetLoginInfo {
	
	@SerializedName("result")
	public LoginInfo loginInfo;
	
	public class LoginInfo implements Serializable{
		
		public String About;
		public String AccountTypeID;
		public String Active;
		public String DOB;
		public String Email;
		public String FirstName;
		public String Graduation_Date;
		public String Graduation_Month;
		public String Graduation_Year;
		
		@SerializedName("ID")
		public int id;
		
		public String LastName;
		public String MiddleInitial;
		public String Password;
		public String Program;
		public String RN_No;
		public String ScreenName;
		
		@SerializedName("status_id")
		public String status;
		
		/*public String Role;
		public int username;
		public String message;
		public String createdDate;
		public String modifiedDate;
		public String GUID;
		public String sent;
		public String status_type;*/
		
		public String User_image_URL;
	}
}
