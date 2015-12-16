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

public class AdapterBlog extends ArrayAdapter<Blog> {
	
	Context context;
	List<Blog> blogList;
	ViewHolder holder = null;
	Blog blog;
	
	public AdapterBlog(Context context, int resourceId,
			List<Blog> blogList) {
		super(context, resourceId, blogList);
		this.context = context;
		this.blogList = blogList;

		// imageLoader = new ImageLoader(context);

	}

	/* private view holder class */
	private class ViewHolder {
		TextView tvTitle;
		
	
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		blog = blogList.get(position);

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
			
		
			
			
			holder.tvTitle.setText(blog.getBlogTitle());
			
			return convertView;
	}



}
