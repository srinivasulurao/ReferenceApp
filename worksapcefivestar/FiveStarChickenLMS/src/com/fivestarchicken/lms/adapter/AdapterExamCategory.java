package com.fivestarchicken.lms.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.model.Exam;

public class AdapterExamCategory extends ArrayAdapter<Exam> {

	Context context;
	List<Exam> examList;
	ViewHolder holder = null;
	Exam exam;
	
	public AdapterExamCategory(Context context, int resourceId,
			List<Exam> examList) {
		super(context, resourceId, examList);
		this.context = context;
		this.examList = examList;

		// imageLoader = new ImageLoader(context);

	}

	/* private view holder class */
	private class ViewHolder {
		TextView tvCategory;
		TextView tvModuleCount;
		

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		exam = examList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_exam_category, null);

			holder = new ViewHolder();
		
			holder.tvCategory = (TextView) convertView
					.findViewById(R.id.tvcategory);
			holder.tvModuleCount= (TextView) convertView
					.findViewById(R.id.tvmodulecount);
			
			convertView.setTag(holder);
		} else

			holder = (ViewHolder) convertView.getTag();
		holder.tvCategory.setText(exam.getTitle());
		holder.tvModuleCount.setText(exam.getModuleCount());
		holder.tvModuleCount.setVisibility(View.GONE);
		// holder.rlMain.setBackgroundColor(context.getResources().getColor(R.color.text_link));

		return convertView;
	}

}
