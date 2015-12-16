package com.fivestarchicken.lms.fragments;

import java.util.ArrayList;
import java.util.List;

import com.fivestarchicken.lms.ActivityBlogDetail;
import com.fivestarchicken.lms.ActivityExamDeatail;
import com.fivestarchicken.lms.ActivityExamModule;
import com.fivestarchicken.lms.ActivityResultDetail;
import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.adapter.AdapterBlog;
import com.fivestarchicken.lms.adapter.AdapterExamCategory;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.model.Blog;
import com.fivestarchicken.lms.model.BlogDetail;
import com.fivestarchicken.lms.model.Exam;
import com.fivestarchicken.lms.model.ExamModule;
import com.google.gson.Gson;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentBlog  extends Fragment {
	
	ListView lvBlog;
	AdapterBlog adapterBlog;
	List<Blog> blogList = new ArrayList<Blog>();
	private DbAdapter dh;
	Gson gson;
	SharedPreferences sharedPreferences;
	String languageType;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_blog, container, false);

		initilizeUI(v);
		
		return v;
	}
	
	private void initilizeUI(View v) {

		try {
			lvBlog = (ListView) v.findViewById(R.id.lvblog);

			this.dh = new DbAdapter(getActivity());
			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
		    languageType = sharedPreferences.getString("languageType", null);
			blogList=dh.getBlogList(languageType);
			
			adapterBlog = new AdapterBlog(getActivity(),
					R.layout.adapter_blog, blogList);
			
			lvBlog.setAdapter(adapterBlog);
			  gson = new Gson();
			lvBlog.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Blog blog = (Blog) parent.getItemAtPosition(position);
					String blogStr = gson.toJson(blog);

					Intent i = new Intent(getActivity(),
							ActivityBlogDetail.class);

					i.putExtra("blogStr", blogStr);
					startActivity(i);
					

					
				}
			});
			

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
