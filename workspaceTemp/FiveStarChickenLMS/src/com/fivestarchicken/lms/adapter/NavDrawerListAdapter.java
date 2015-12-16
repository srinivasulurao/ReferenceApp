package com.fivestarchicken.lms.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.model.NavDrawerItem;

public class NavDrawerListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private int selectedIndex;
	
	
	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems,int selectedIndex){
		this.context = context;
		this.navDrawerItems = navDrawerItems;
		this.selectedIndex=selectedIndex;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }
         
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        RelativeLayout rlMain=(RelativeLayout)convertView.findViewById(R.id.rlmain);
        //TextView txtCount = (TextView) convertView.findViewById(R.id.counter);
         
        imgIcon.setImageResource(navDrawerItems.get(position).getIcon());        
        txtTitle.setText(navDrawerItems.get(position).getTitle());
        
        if(position==selectedIndex){
        	
        	rlMain.setBackgroundDrawable(context. getResources().getDrawable(R.drawable.side_menu_bg_pressed) );	
        }else{
        	
        	rlMain.setBackgroundDrawable(context. getResources().getDrawable(R.drawable.side_menu_bg_normal) );
        	
        }
        
        // displaying count
        // check whether it set visible or not
       
        
        return convertView;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	

}
