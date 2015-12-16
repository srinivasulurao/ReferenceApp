package com.texastech.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetStatus {
	
	public boolean success;
	
	@SerializedName("result")
	public List<Status> status;
	
	public static class Status{
		
		public String status_id;
		
		public String status_type;
		
		@Override
		public String toString() {
			return status_type;
		}
	}

}
