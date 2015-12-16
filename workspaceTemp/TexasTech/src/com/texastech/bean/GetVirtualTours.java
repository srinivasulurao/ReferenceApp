package com.texastech.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetVirtualTours {
	
	public boolean success;
	
	@SerializedName("result")
	public List<VirtualTour> virtualTours;
	
	public class VirtualTour implements Serializable{
		public String ID;
		public String album_name;
		public String cover_image;
		
		public List<String> album_images;
		
		@Override
		public String toString() {
			return album_name;
		}
	}
}
