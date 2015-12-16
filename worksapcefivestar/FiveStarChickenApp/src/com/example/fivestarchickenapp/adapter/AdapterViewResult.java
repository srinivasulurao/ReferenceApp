package com.example.fivestarchickenapp.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fivestarchickenapp.R;

public class AdapterViewResult extends ArrayAdapter<String> {

	Context context;
	List<String> categoryList;
	ViewHolder holder = null;
	String CategoryName;
	
	public AdapterViewResult(Context context, int resourceId,
			List<String> categoryList) {
		super(context, resourceId, categoryList);
		this.context = context;
		this.categoryList = categoryList;

		// imageLoader = new ImageLoader(context);

	}

	/* private view holder class */
	private class ViewHolder {
		TextView tvTitle;
		

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		CategoryName = categoryList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_view_result, null);

			holder = new ViewHolder();
		
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.tvtitle);
			convertView.setTag(holder);
		} else

			holder = (ViewHolder) convertView.getTag();
		holder.tvTitle.setText(CategoryName);
		

		// holder.rlMain.setBackgroundColor(context.getResources().getColor(R.color.text_link));

		return convertView;
	}

}
