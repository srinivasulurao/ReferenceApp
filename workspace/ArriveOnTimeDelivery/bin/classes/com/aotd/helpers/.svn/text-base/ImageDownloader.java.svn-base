package com.aotd.helpers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;



public class ImageDownloader  {
    private static final String LOG_TAG = "ImageDownloader";

    public enum Mode { NO_ASYNC_TASK, NO_DOWNLOADED_DRAWABLE, CORRECT }
    private Mode mode = Mode.CORRECT;
    String extStorageDirectory = "";
    Context tempCon;
    /**
     * Download the specified image from the Internet and binds it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously
     * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     *
     * @param url The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public ImageDownloader(Context context){
    	tempCon = context;
    	extStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/aotd";
    	File bMarketDirectory=new File(extStorageDirectory);
    	bMarketDirectory.mkdirs();
    }
    
    private Bitmap decodeFile(File f){
    	   try {
    	       //Decode image size
    	       BitmapFactory.Options o = new BitmapFactory.Options();
    	       o.inJustDecodeBounds = true;
    	       BitmapFactory.decodeStream(new FileInputStream(f),null,o);

    	       //The new size we want to scale to
    	       final int REQUIRED_SIZE=70;

    	       //Find the correct scale value. It should be the power of 2.
    	       int scale=1;
    	       while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
    	           scale*=2;

    	       //Decode with inSampleSize
    	       BitmapFactory.Options o2 = new BitmapFactory.Options();
    	       o2.inSampleSize=scale;
    	       return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    	   } catch (FileNotFoundException e) {}
    	   return null;
    	}
    
    public void download(String url, ImageView imageView) {
        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) {
        	String filename = url.substring(url.lastIndexOf("/") + 1);
        	/*if(hasExternalStoragePublicPicture(filename)){
        		Log.v("Image Download","The file found in sdcard "+extStorageDirectory+"/"+filename);
        		boolean errorBitmap=true;
        		
        		try{
        			
//        			decodeFile(extStorageDirectory+"/"+filename);
        			
//        			bitmap = BitmapFactory.decodeFile(extStorageDirectory+"/"+filename);
        			
        			bitmap = decodeFile(new File(extStorageDirectory+"/"+filename));
        			imageView.setImageBitmap(bitmap);
        	        imageView.setVisibility(View.VISIBLE);
        			addBitmapToCache(url, bitmap);
        		}catch(Exception ex){
        			errorBitmap=false;
        		}
        		if(errorBitmap==false)
        			forceDownload(url, imageView);
        		
        	}else{
        		forceDownload(url, imageView);
        	}*/
    		forceDownload(url, imageView);
        	
        } else {
            cancelPotentialDownload(url, imageView);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasExternalStoragePublicPicture(String name) {
		//File sdImageMainDirectory = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.directory));
		File file = new File(extStorageDirectory, name);
		return file.exists();
	}
    /*
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
       private void forceDownload(String url, ImageView view) {
          forceDownload(url, view, null);
       }
     */

    /**
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
     */
    private void forceDownload(String url, ImageView imageView) {
        // State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
        if (url == null) {
            imageView.setImageDrawable(null);
            return;
        }

        if (cancelPotentialDownload(url, imageView)) {
            switch (mode) {
                case NO_ASYNC_TASK:
                    Bitmap bitmap = downloadBitmap(url);
                    addBitmapToCache(url, bitmap);
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);

                    break;

                case NO_DOWNLOADED_DRAWABLE:
                    //imageView.setMinimumHeight(156);
                    BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
                    task.execute(url);
                    break;

                case CORRECT:
                	//Log.v("Image Download","Starting to download");
                    task = new BitmapDownloaderTask(imageView);
                    DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                    imageView.setImageDrawable(downloadedDrawable);
                    //imageView.setMinimumHeight(156);
                    task.execute(url);
                    break;
            }
        }
    }

    /**
     * Returns true if the current download has been canceled or if there was no download in
     * progress on this image view.
     * Returns false if the download in progress deals with the same url. The download is not
     * stopped in that case.
     */
    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    Bitmap downloadBitmap(String url) {
        final int IO_BUFFER_SIZE = 4 * 1024;

        // AndroidHttpClient is not allowed to be used from the main thread
        final HttpClient client = (mode == Mode.NO_ASYNC_TASK) ? new DefaultHttpClient() :
            AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    
                    // return BitmapFactory.decodeStream(inputStream);
                    // Bug on slow connections, fixed in future release.
                    return createScaledBitmapFromStream(inputStream, 100, 100);
//                    return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            getRequest.abort();
            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            getRequest.abort();
            Log.w(LOG_TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
            getRequest.abort();
            Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }
    
    
    
    
    /**
     * 
     * to decrease the bitmap size
     * 
     *
     */
    
    
    protected Bitmap createScaledBitmapFromStream( InputStream s, int minimumDesiredBitmapWidth, int minimumDesiredBitmapHeight ) {

    	
    	   final BufferedInputStream is = new BufferedInputStream(s, 32*1024);
    	   try {
    	       final Options decodeBitmapOptions = new Options();
    	       // For further memory savings, you may want to consider using this option
    	       // decodeBitmapOptions.inPreferredConfig = Config.RGB_565; // Uses 2-bytes instead of default 4 per pixel

    	       if( minimumDesiredBitmapWidth >0 && minimumDesiredBitmapHeight >0 ) {
    	           final Options decodeBoundsOptions = new Options();
    	           decodeBoundsOptions.inJustDecodeBounds = true;
    	           is.mark(32*1024); // 32k is probably overkill, but 8k is insufficient for some jpgs
    	           BitmapFactory.decodeStream(is,null,decodeBoundsOptions);
    	           is.reset();

    	           final int originalWidth = decodeBoundsOptions.outWidth;
    	           final int originalHeight = decodeBoundsOptions.outHeight;

    	           // inSampleSize prefers multiples of 2, but we prefer to prioritize memory savings
    	           decodeBitmapOptions.inSampleSize= Math.max(1,Math.min(originalWidth / minimumDesiredBitmapWidth, originalHeight / minimumDesiredBitmapHeight));

    	       }

    	       return BitmapFactory.decodeStream(is,null,decodeBitmapOptions);

    	   } catch( IOException e ) {
    	       throw new RuntimeException(e); // this shouldn't happen
    	   } finally {
    	       try {
    	           is.close();
    	       } catch( IOException ignored ) {}
    	   }

    	}

    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    /**
     * The actual AsyncTask that will asynchronously download the image.
     */
    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            return downloadBitmap(url);
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }else{
            	String filename = url.substring(url.lastIndexOf("/") + 1);
            	Log.v("Image Download","Save the file to sd card" + filename);
            	saveToSDCard(bitmap,filename);
            }

            addBitmapToCache(url, bitmap);
           
            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                // Change bitmap only if this process is still associated with it
                // Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
                if ((this == bitmapDownloaderTask) || (mode != Mode.CORRECT)) {
                    //Log.v("Image Download","Async Completed Downloading");
                	imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    /**
     * A fake Drawable that will be attached to the imageView while the download is in progress.
     *
     * <p>Contains a reference to the actual download task, so that a download task can be stopped
     * if a new binding is required, and makes sure that only the last started download process can
     * bind its result, independently of the download finish order.</p>
     */
    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.BLACK);
            bitmapDownloaderTaskReference =
                new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        clearCache();
    }

    
    /*
     * Cache-related fields and methods.
     * 
     * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
     * Garbage Collector.
     */
    
    private static final int HARD_CACHE_CAPACITY = 10;
    private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, Bitmap> sHardBitmapCache =
        new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
            if (size() > HARD_CACHE_CAPACITY) {
                // Entries push-out of hard reference cache are transferred to soft reference cache
                sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                return true;
            } else
                return false;
        }
    };

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
        new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);

    private final Handler purgeHandler = new Handler();

    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    /**
     * Adds this bitmap to the cache.
     * @param bitmap The newly downloaded bitmap.
     */
    private void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    /**
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    public Bitmap getBitmapFromCache(String url) {
        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(url);
            if (bitmap != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, bitmap);
                return bitmap;
            }
        }

        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                // Bitmap found in soft cache
                return bitmap;
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(url);
            }
        }

        return null;
    }
 
    /**
     * Clears the image cache used internally to improve performance. Note that for memory
     * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
     */
    public void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    private void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }
    
    public void saveToSDCard(Bitmap bitmap, String filename) {
		//boolean mExternalStorageAvailable = false;
		//boolean mExternalStorageWriteable = false;
		//String state = Environment.getExternalStorageState();
		//if (Environment.MEDIA_MOUNTED.equals(state)) {
			//mExternalStorageAvailable = mExternalStorageWriteable = true;
			//Log.v("SDCard", "SD Card is available for read and write "
			//		+ mExternalStorageAvailable + mExternalStorageWriteable);
    	try {
    		
    		saveFile(bitmap, filename);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		//} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		///	mExternalStorageAvailable = true;
		//	mExternalStorageWriteable = false;
		//	Log.v("SDCard", "SD Card is available for read "
		//			+ mExternalStorageAvailable);
		//} else {
		//	mExternalStorageAvailable = mExternalStorageWriteable = false;
		//	Log.v("SDCard", "Please insert a SD Card to save your Video "
		//			+ mExternalStorageAvailable + mExternalStorageWriteable);
		//}
	}

	private void saveFile(Bitmap bitmap, String filename) {
		//String filename = name;
		//File sdImageMainDirectory = new File(Environment
		//		.getExternalStorageDirectory(), getResources().getString(
		//		R.string.directory));
		//sdImageMainDirectory.mkdirs();
		try {
			
			File file = new File(extStorageDirectory, filename);
			
			OutputStream outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outStream);
			outStream.flush();
			outStream.close();
		
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
