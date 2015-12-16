package com.fivestarchicken.lms.adapter;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import  com.fivestarchicken.lms.libs.ImageDownloaderTask;
import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.model.User;
import com.fivestarchicken.lms.utils.Commons;

public class AdapterEmployee extends ArrayAdapter<User> {

	Context context;
	List<User> employeeList;
	ViewHolder holder = null;
	User employee;
	
	public AdapterEmployee(Context context, int resourceId,
			List<User> employeeList) {
		super(context, resourceId, employeeList);
		this.context = context;
		this.employeeList = employeeList;

	

	}

	private class ViewHolder {
		ImageView ivEmployeePhoto;
		TextView ivEmployeeName;
		LinearLayout llStarRate;
		

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		employee = employeeList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_employee, null);

			holder = new ViewHolder();
		
			holder.ivEmployeePhoto = (ImageView) convertView
					.findViewById(R.id.ivemployeephoto);
			holder.ivEmployeeName= (TextView) convertView
					.findViewById(R.id.tvemployeename);
			holder.llStarRate= (LinearLayout) convertView
					.findViewById(R.id.llrate);
			
			convertView.setTag(holder);
		} else

			holder = (ViewHolder) convertView.getTag();
		holder.ivEmployeeName.setText(employee.getUserName());
		//holder.ivEmployeePhoto.setText(exam.getModuleCount());
		
		if (employee.getProfileImage() != null
				&& employee.getProfileImage().length() > 0) {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/" + Commons.appFolder + "/" + employee.getProfileImage());

			if (file.exists()) {

				Bitmap myBitmap = BitmapFactory.decodeFile(file
						.getAbsolutePath());

				holder.ivEmployeePhoto.setImageBitmap(myBitmap);

			} else {
				if (Commons.isNetworkAvailable(context)) {
					 new ImageDownloaderTask(holder.ivEmployeePhoto).execute(employee.getProfileImage());
				}

			}
		}
		
		holder.llStarRate.removeAllViews();
		
		Integer countStar = new Integer(employee.getStarRate());
		
		Integer blankStarCount=Commons.MAX_STAR-countStar;
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				50, 50);
		
		for (Integer i = 0; i < countStar; i++) {

			ImageView myImage = new ImageView(context);
			myImage.setLayoutParams(layoutParams);
			myImage.setImageResource(R.drawable.star_on);
			holder.llStarRate.addView(myImage);

		}
		
		for (Integer i = 0; i < blankStarCount; i++) {

			ImageView myImage = new ImageView(context);
			myImage.setLayoutParams(layoutParams);
			myImage.setImageResource(R.drawable.star_off);
			holder.llStarRate.addView(myImage);

		}

		// holder.rlMain.setBackgroundColor(context.getResources().getColor(R.color.text_link));

		return convertView;
	}

}
