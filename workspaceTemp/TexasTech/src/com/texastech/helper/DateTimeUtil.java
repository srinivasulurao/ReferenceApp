/**
 * 
 */
package com.texastech.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

 
public class DateTimeUtil {
	
	public static String getCurrentDate(){
		Date date = new Date();  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");  
		MLog.v("current date", ""+sdf.format(date));
		return sdf.format(date);  
	}
	
	
	public static String getCurrentDate(String format){
		Date date = new Date();  
		SimpleDateFormat sdf = new SimpleDateFormat(format);  
		MLog.v("current date", ""+sdf.format(date));
		return sdf.format(date);  
	}
	
	
	/*public static String getDateInFromat(String time_s){
		String strDate="";
		try{
	        // *** note that it's "yyyy-MM-dd hh:mm:ss" not "yyyy-mm-dd hh:mm:ss"  
	        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
	        Date date = dt.parse(time_s.substring(0, time_s.indexOf("T")));
	        
	        // *** same for the format String below
	        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	        strDate = dt1.format(date);
	        System.out.println(dt1.format(date));
	        
		}catch(Exception e){
			e.printStackTrace();
		} 
		return strDate;
	}*/
	
	 
	public static String getDateTimeInFromat(String time_s){
		String strDate="";
		try{
	        // *** note that it's "yyyy-MM-dd hh:mm:ss" not "yyyy-mm-dd hh:mm:ss"  
	        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date date = dt.parse(time_s);
	
	        // *** same for the format String below
	        SimpleDateFormat dt1 = new SimpleDateFormat("MMM dd,yyyy HH:mm:a", Locale.US);
	        strDate = dt1.format(date);
	        System.out.println(dt1.format(date));
	        
		}catch(Exception e){
			e.printStackTrace();
		} 
		return strDate;
	}
 
	
	public static String getDateFormat(String dateStr){
		try {
			DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			fromFormat.setLenient(false);
			DateFormat toFormat = new SimpleDateFormat("MMM dd,yyyy");
			toFormat.setLenient(false);
			Date date = fromFormat.parse(dateStr);
			dateStr =toFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateStr;
	}
}
