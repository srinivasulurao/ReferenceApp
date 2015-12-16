package com.texastech.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetLoginScreenInfo {
	
	@SerializedName("result")
	public List<LoginScreenInfo> loginScreenInfosList;
	
	public class LoginScreenInfo implements Serializable{
		public String id;
		public String username;
		public String message;
		public String createdDate;
		public String modifiedDate;
		public String GUID;
		public String status;
		public String sent;
	}
}
