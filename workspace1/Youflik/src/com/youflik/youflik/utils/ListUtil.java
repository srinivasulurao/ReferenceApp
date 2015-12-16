package com.youflik.youflik.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListUtil {
	public static void updateListViewHeight(ListView myListView) {
	     ListAdapter myListAdapter = myListView.getAdapter();
	     if (myListAdapter == null) {            
	              return;
	     }
	    //get listview height
	    int totalHeight = 0;
	    int adapterCount = myListAdapter.getCount();
	    for (int size = 0; size < adapterCount ; size++) {
	        View listItem = myListAdapter.getView(size, null, myListView);
	        listItem.measure(0, 0);
	        totalHeight += listItem.getMeasuredHeight();
	    }
	    //Change Height of ListView 
	    ViewGroup.LayoutParams params = myListView.getLayoutParams();
	    params.height = totalHeight + (myListView.getDividerHeight() * (adapterCount-1));
	    myListView.setLayoutParams(params);
	}
}
