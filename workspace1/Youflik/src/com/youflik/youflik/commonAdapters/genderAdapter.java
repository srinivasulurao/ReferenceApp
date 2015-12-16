package com.youflik.youflik.commonAdapters;

import java.util.ArrayList;

import com.youflik.youflik.R;
import com.youflik.youflik.models.GenderModel;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class genderAdapter extends BaseAdapter{

	private ArrayList<GenderModel> listData;
	private LayoutInflater layoutInflater;
	private Context prova;

	public genderAdapter(Context context,ArrayList<GenderModel> listData){
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
		prova = context;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null){
			convertView = layoutInflater.inflate(R.layout.custom_gender, null);
			holder = new ViewHolder();
			holder.textViewGender= (TextView) convertView.findViewById(R.id.customgender);
				convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		GenderModel gender_Item = (GenderModel) listData.get(position);

		Typeface typeFace=Typeface.createFromAsset(prova.getAssets(),"fonts/BentonSans-Regular.otf");
		// addind Font 

			holder.textViewGender.setText(gender_Item.getGendername());	
			holder.textViewGender.setTypeface(typeFace);
		
	
		
		return convertView;
	}

	static class ViewHolder{
	
		TextView textViewGender;
	

	}

}

