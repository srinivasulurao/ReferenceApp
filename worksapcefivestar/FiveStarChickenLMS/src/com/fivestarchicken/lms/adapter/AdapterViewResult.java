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
import com.fivestarchicken.lms.model.Result;
import com.fivestarchicken.lms.utils.Commons;

public class AdapterViewResult extends ArrayAdapter<Result> {

	Context context;
	List<Result> resultList;
	ViewHolder holder = null;
	Result result;
	
	public AdapterViewResult(Context context, int resourceId,
			List<Result> resultList) {
		super(context, resourceId, resultList);
		this.context = context;
		this.resultList = resultList;

		// imageLoader = new ImageLoader(context);

	}

	/* private view holder class */
	private class ViewHolder {
		TextView tvPercentage,tvModuleName,tvStatus;
		

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		result = resultList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_view_result, null);

			holder = new ViewHolder();
		
			holder.tvPercentage = (TextView) convertView
					.findViewById(R.id.tvpercentage);
			holder.tvModuleName=(TextView)convertView
					.findViewById(R.id.tvmodule);
			holder.tvStatus=(TextView)convertView
					.findViewById(R.id.tvstatus);
			
			convertView.setTag(holder);
		} else

		holder = (ViewHolder) convertView.getTag();
		holder.tvPercentage.setText(result.getPercentage()+ " %");
		holder.tvModuleName.setText(result.getModuleName());
		holder.tvStatus.setText(result.getStatus());
		
		if(result.getStatus().equals(Commons.statusFail)){
			
			holder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.failbackground));
			
		}else if(result.getStatus().equals(Commons.statusPass)){
		
			holder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.passbackground));
			
			
		}else if(result.getStatus().equals(Commons.statusProcessing)){
			
			holder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.processingbackground));
		}

		// holder.rlMain.setBackgroundColor(context.getResources().getColor(R.color.text_link));

		return convertView;
	}

}
