package com.texastech.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.texastech.app.R;
import com.texastech.bean.GetFacultyInfo.FacultyInfo;

public class FacultyDirAdapter extends MyAdapter{
	
	
	public FacultyDirAdapter(Context context, List list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view==null){
			view = inflater.inflate(R.layout.row_faculty_dir, null);
		}
		
		TextView tvTitle =(TextView)view.findViewById(R.id.tv_lable); 
		
		FacultyInfo info =(FacultyInfo)getItem(position);
		tvTitle.setText(info.FirstName+" "+info.LastName);
		 
		TextView tvHeader = (TextView)view.findViewById(R.id.tv_header);
		if(info.isHeader){
			tvHeader.setVisibility(View.VISIBLE);
			tvHeader.setText(info.LastName.substring(0, 1));
		}else{
			tvHeader.setVisibility(View.GONE);
		}
		view.setTag(info.FirstName+" "+info.LastName);
		return view;
	}

}
