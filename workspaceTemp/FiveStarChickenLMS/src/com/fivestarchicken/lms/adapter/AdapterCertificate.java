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
import com.fivestarchicken.lms.adapter.AdapterEmployee.ViewHolder;
import com.fivestarchicken.lms.model.Blog;
import com.fivestarchicken.lms.model.Certificate;

public class AdapterCertificate extends ArrayAdapter<Certificate> {
	
	Context context;
	List<Certificate> certificateList;
	ViewHolder holder = null;
	Certificate certificate;
	
	public AdapterCertificate(Context context, int resourceId,
			List<Certificate> certificateList) {
		super(context, resourceId, certificateList);
		this.context = context;
		this.certificateList = certificateList;

		// imageLoader = new ImageLoader(context);

	}

	/* private view holder class */
	private class ViewHolder {
		TextView tvTitle;
		
	
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		certificate = certificateList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_blog, null);

			holder = new ViewHolder();
		
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.tvtitle);
			
			convertView.setTag(holder);
		}else
			holder = (ViewHolder) convertView.getTag();
			
			
			holder.tvTitle.setText(certificate.getStarValue()+" Star");
			
			return convertView;
	}


}
