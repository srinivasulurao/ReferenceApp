/**
 * 
 */
package com.texastech.helper;

import android.content.Context;
import android.os.Debug;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

 
public class MemoryAnalyzer {
	
	public static void freeMemory(){
	    try {
			System.runFinalization();
			Runtime.getRuntime().gc();
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static String getHeapSize(){
		long  MEGABYTE = 1024L * 1024L;
		Long l = Debug.getNativeHeapSize();
		return (l/MEGABYTE)+"MB";
	}
	
	
	
	/** 
	 * Call when close the app.
	 * @param context
	 */
	public static void clearCache(Context context){
		try {
			 CookieSyncManager.createInstance(context);         
			 CookieManager cookieManager = CookieManager.getInstance();        
			 cookieManager.removeAllCookie();
			 //context.deleteDatabase("webview.db");
			 //context.deleteDatabase("webviewCache.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
