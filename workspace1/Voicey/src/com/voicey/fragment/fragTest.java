package com.voicey.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.voicey.activity.R;

public class fragTest extends Fragment {
	
	ListView lv;
	LvAdapter adpter;
	ArrayList<Integer> list=new ArrayList<Integer>();
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.testmain, container, false);

		initilizeUI(v);

		return v;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		}
	}

	private void initilizeUI(View v) {

		lv=(ListView)v.findViewById(R.id.listView1);
		lv.setItemsCanFocus(true);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		for(int i=0;i<30;i++){
			list.add(i);
		}
		
		 adpter=new LvAdapter();
		lv.setAdapter(adpter);
		

		}
	
	public class LvAdapter extends BaseAdapter{

		LayoutInflater mInflater;
		public LvAdapter(){
			 mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			final ViewHolder holder;
	     	convertView=null;
	    if (convertView == null) {
	        holder = new ViewHolder();

	        convertView = mInflater.inflate(R.layout.testsub, null); // this is your cell
	        
	        holder.caption = (EditText) convertView
	                .findViewById(R.id.editText12);
	        holder.caption.setTag(position); 
	        holder.caption.setText(list.get(position).toString());
	        convertView.setTag(holder);
	        
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
	     	int tag_position=(Integer) holder.caption.getTag();
	    	holder.caption.setId(tag_position);
			
			 holder.caption.addTextChangedListener(new TextWatcher() {

		           @Override
		           public void onTextChanged(CharSequence s, int start, int before,
		                   int count) {
		                     final int position2 = holder.caption.getId();
		                     final EditText Caption = (EditText) holder.caption;
		                    /* if(Caption.getText().toString().length()>0){
		                    	 list.set(position2,Integer.parseInt(Caption.getText().toString()));
		                     }else{
		                    	 //Toast.makeText(getApplicationContext(), "Please enter some value", Toast.LENGTH_SHORT).show();
		                     }*/
		                     
		                 }

		           @Override
		           public void beforeTextChanged(CharSequence s, int start, int count,
		                   int after) {
		               // TODO Auto-generated method stub
		           }

		           @Override
		           public void afterTextChanged(Editable s) {
		        	   
		           }

		       });
			 
	        
	    return convertView;
	    }
		
	}



	public class ViewHolder {
	    EditText caption;
	}

}
