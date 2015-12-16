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
import com.fivestarchicken.lms.model.ExamModule;

public class AdapterExamModules extends ArrayAdapter<ExamModule> {
	
	Context context;
	List<ExamModule> examModuleList;
	ViewHolder holder = null;
	ExamModule examModule;
	
	public AdapterExamModules(Context context, int resourceId,
			List<ExamModule> examList) {
		super(context, resourceId, examList);
		this.context = context;
		this.examModuleList = examList;

		// imageLoader = new ImageLoader(context);

	}

	/* private view holder class */
	private class ViewHolder {
		TextView tvCategory,tvTotalQuestions,tvDuration,tvResult;
		

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		examModule = examModuleList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_exam_module, null);

			holder = new ViewHolder();
		
			holder.tvCategory = (TextView) convertView
					.findViewById(R.id.tvcategory);
			
			holder.tvTotalQuestions = (TextView) convertView
					.findViewById(R.id.tvquestion);
			
			holder.tvResult=(TextView) convertView
					.findViewById(R.id.tvresult);
			
			holder.tvDuration = (TextView) convertView
					.findViewById(R.id.tvduration);
			
			convertView.setTag(holder);
		} else

		holder = (ViewHolder) convertView.getTag();
		holder.tvCategory.setText(examModule.getModuleName());
		holder.tvTotalQuestions.setText(context.getResources().getString(R.string.total_question)+"  :  "+examModule.getTotalQuestions());
		holder.tvDuration.setText(context.getResources().getString(R.string.duration)+"  :  "+examModule.getDuration());
		holder.tvResult.setVisibility(View.GONE);
		
		/*if(examModule.getIsPassed().equals("0")){
			
			holder.tvResult.setVisibility(View.GONE);
		}else{
			
			holder.tvResult.setVisibility(View.VISIBLE);
		}*/

		// holder.rlMain.setBackgroundColor(context.getResources().getColor(R.color.text_link));

		return convertView;
	}

}
