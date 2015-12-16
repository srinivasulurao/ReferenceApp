package com.aotd.map.helper;

import java.io.Serializable;
import java.util.ArrayList;

public class RoadModel implements Serializable{

	   public String mName; 
	   
       public String mDescription; 
	   
       public int mColor; 
	   
       public int mWidth; 
	   
       public double[][] mRoute = new double[][] {}; 
	   
       public PointsModel[] mPoints = new PointsModel[] {}; 
       
       public ArrayList<Double> lat_arr = new ArrayList<Double>();
       
       public ArrayList<Double> long_arr = new ArrayList<Double>();
       
	   
}
	 
 
	