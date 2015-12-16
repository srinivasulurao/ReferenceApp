package com.fivestarchicken.lms.adapter;

import java.util.List;

import com.fivestarchicken.lms.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class AdapterDropDown extends ArrayAdapter<String> {
 
    Context context;
 
    public AdapterDropDown(Context context, int resourceId,
            List<String> spinnerlist) {
        super(context, resourceId, spinnerlist);
        this.context = context;
    }
    
    @Override
	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {

		return getCustomView(position, convertView, parent);
	}
    
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}


    
    /*private view holder class*/
    private class ViewHolder {
     
      
       
    }
 
    public View getCustomView(int position, View convertView,
			ViewGroup parent) {
    	String suggestValue = getItem(position);
    	 LayoutInflater mInflater = (LayoutInflater) context
                 .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View row = mInflater.inflate(R.layout.spinner_content, parent, false);
		TextView label = (TextView) row.findViewById(R.id.tvname);
		label.setText(suggestValue);
		
		
		return row;
	}


}
