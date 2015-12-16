/**
 * @author {Anand Gour}
 * Date : Jul 9, 2013
 * Time : 4:59:33 PM
 */
package com.texastech.helper;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;


/**
 * @author {Anand Gour}
 * Date : Jul 17, 2013
 * Time : 4:57:04 PM
 */
public class UnbindView {
	
	/**
	 * Removes the reference to the activity from every view in a view hierarchy (listeners, images etc.).
	 * This method should be called in the onDestroy() method of each activity
	 * @param viewID normally the id of the root layout
	 * 
	 * see http://code.google.com/p/android/issues/detail?id=8488
	 */
	public static void unbindReferences(View view) {
		try {
			if (view!=null) {
				unbindViewReferences(view);
			    if (view instanceof ViewGroup) unbindViewGroupReferences((ViewGroup) view);
			}
			System.gc();
		}
		catch (Throwable e) {
			// whatever exception is thrown just ignore it because a crash is always worse than this method not doing what it's supposed to do
		}
	}

	private static void unbindViewGroupReferences(ViewGroup viewGroup) {
	    	int nrOfChildren = viewGroup.getChildCount();
	    	for (int i=0; i<nrOfChildren; i++){
	    		View view = viewGroup.getChildAt(i);
	        	unbindViewReferences(view);
			if (view instanceof ViewGroup) unbindViewGroupReferences((ViewGroup) view);
	    	}
	    	try {
	    		viewGroup.removeAllViews();
	    	}
	    	catch (Throwable mayHappen) {
	        	// AdapterViews, ListViews and potentially other ViewGroups don't support the removeAllViews operation
	        }
	}

	private static void unbindViewReferences(View view) {
		// set all listeners to null (not every view and not every API level supports the methods)
		try {view.setOnClickListener(null);} catch (Throwable mayHappen) {};
		try {view.setOnCreateContextMenuListener(null);} catch (Throwable mayHappen) {};
		try {view.setOnFocusChangeListener(null);} catch (Throwable mayHappen) {};
		try {view.setOnKeyListener(null);} catch (Throwable mayHappen) {};
		try {view.setOnLongClickListener(null);} catch (Throwable mayHappen) {};
		try {view.setOnClickListener(null);} catch (Throwable mayHappen) {};

		// set background to null
		Drawable d = view.getBackground(); 
		if (d!=null) d.setCallback(null);
		if (view instanceof ImageView) {
			ImageView imageView = (ImageView) view;
			d = imageView.getDrawable();
			if (d!=null) d.setCallback(null);
			imageView.setImageDrawable(null);
			imageView.setBackgroundDrawable(null);
		}

		// destroy webview
		if (view instanceof WebView) {
			((WebView) view).destroyDrawingCache();
			((WebView) view).destroy();
		}
	}

}
