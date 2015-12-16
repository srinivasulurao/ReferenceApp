package com.example.fivestarchickenapp.adapter;

import java.util.List;



import com.example.fivestarchickenapp.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterExamCategory extends ArrayAdapter<String> {

	Context context;
	List<String> categoryList;
	ViewHolder holder = null;
	String CategoryName;
	
	public AdapterExamCategory(Context context, int resourceId,
			List<String> categoryList) {
		super(context, resourceId, categoryList);
		this.context = context;
		this.categoryList = categoryList;

		// imageLoader = new ImageLoader(context);

	}

	/* private view holder class */
	private class ViewHolder {
		TextView tvCategory;
		

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		CategoryName = categoryList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_exam_category, null);

			holder = new ViewHolder();
		
			holder.tvCategory = (TextView) convertView
					.findViewById(R.id.tvcategory);
			convertView.setTag(holder);
		} else

			holder = (ViewHolder) convertView.getTag();
		holder.tvCategory.setText(CategoryName);
		

		// holder.rlMain.setBackgroundColor(context.getResources().getColor(R.color.text_link));

		return convertView;
	}

}
