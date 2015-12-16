package com.texastech.bean;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetProgram {
	
	public boolean success;
	
	@SerializedName("result")
	public List<Program> programs;
	
	public static class Program{
		
		public String ID;
		
		public String Title;
		
		@Override
		public String toString() {
			return Title;
		}
	}

}
