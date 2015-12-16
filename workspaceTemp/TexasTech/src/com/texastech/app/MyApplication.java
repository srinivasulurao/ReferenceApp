/**
 * Copyright 2013 Ognyan Bankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.texastech.app;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
/**
 * Application class for the demo. Used to ensure that MyVolley is initialized. {@see MyVolley}
 */

//api_key=
@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=",formKey = ""/*,mailTo = "gour.anandroid@gmail.com"*/)
public class MyApplication extends Application {
	
	private static Context applicationContext;
	
	private static Typeface typeface;
	
	private static ImageLoader imageLoader; 
	
	private static int resolutionId = 0; 
	
	
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        applicationContext = this.getApplicationContext();
        initImageLoader(applicationContext);
    }
    

  
    
    
    public static ImageLoader getLoader(){
    	if(imageLoader==null){
    		imageLoader = ImageLoader.getInstance();
    	}
    	return imageLoader;
    }
    
     
    
    public static Context getContext(){
    	return applicationContext;
    }
    
    
    public static Typeface getTypeface(){
    	if(typeface==null)
    		typeface = Typeface.createFromAsset(applicationContext.getAssets(), "font/----.ttf");
    	
    	return typeface;
    }
    
    
    
    public static void initImageLoader(Context context) {
		int memoryCacheSize;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			int memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
			memoryCacheSize = (memClass / 8) * 1024 * 1024;
		} else {
			memoryCacheSize = 2 * 1024 * 1024;
		}

		final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(5) //Default 3
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(memoryCacheSize)
				.memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize-1000000))
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO) 
				.discCacheSize(100 * 1024 * 1024)
				.threadPoolSize(10)
				.build();

		//ImageLoader.getInstance().init(config);
		getLoader().init(config);
	}

    public static void clearImageCache(){
    	try {
			imageLoader.clearDiskCache();
			imageLoader.clearMemoryCache();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
   
    public static int getDeviceResulotionId(){
    	if(resolutionId!=0)return resolutionId;
    	
    	WindowManager wm = (WindowManager)applicationContext.getSystemService(WINDOW_SERVICE);
    	Display display = wm.getDefaultDisplay();
    	if(display.getWidth()>=720){
    		resolutionId=5;
    	}else if (display.getWidth()>=600) {           
    		resolutionId=5;
		}else if (display.getWidth()>=480) {
    		resolutionId=2;
		}else {
			resolutionId=1;
		}
    	return resolutionId;
    }           
}
