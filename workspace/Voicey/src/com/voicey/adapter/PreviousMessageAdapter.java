package com.voicey.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.voicey.activity.R;
import com.voicey.model.AudioInfo;

public class PreviousMessageAdapter extends ArrayAdapter<AudioInfo> {
	
	Context context;
	List<AudioInfo> previousMessageList;
	ViewHolder holder = null;
	AudioInfo audioInfo;
	
	public PreviousMessageAdapter(Context context, int resourceId,
			List<AudioInfo> ProductRegistrationList) {
		super(context, resourceId, ProductRegistrationList);
		this.context = context;
		this.previousMessageList = ProductRegistrationList;
	}
	
	private class ViewHolder {

		TextView tvTitle;
		TextView tvMood;
		TextView tvUserId, tvsend, tvforward;
		TextView tvCount, tvFromvalue, tvsentDate;
		TextView tvclassifield;
	

		

	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		audioInfo = previousMessageList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.previous_message_list, null);
			holder = new ViewHolder();

			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.tvtitle);
			holder.tvMood = (TextView) convertView
					.findViewById(R.id.tvmood);
			holder.tvUserId = (TextView) convertView
					.findViewById(R.id.tvuserId);
			
			holder.tvclassifield = (TextView) convertView
					.findViewById(R.id.tvclassifield);

			holder.tvFromvalue = (TextView) convertView
					.findViewById(R.id.tvfromValue);

			holder.tvsentDate = (TextView) convertView
					.findViewById(R.id.tvdate);
			try {
				holder.tvFromvalue.setText(audioInfo.getFromUserName());
				String inputTimeStamp = audioInfo.getSharedate();

				final String inputFormat = "yyyy-MM-dd HH:mm:ss";
				final String outputFormat = "MMM dd HH:mm";

				String dateValue = TimeStampConverter(inputFormat,
						inputTimeStamp, outputFormat);

				holder.tvsentDate.setText(dateValue);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			// isActivePopup = true;

			holder.tvCount = (TextView) convertView
					.findViewById(R.id.tvcount);
			convertView.setTag(holder);
			
		} else
			holder = (ViewHolder) convertView.getTag();
		
		holder.tvTitle.setText(audioInfo.getTitle());
		holder.tvCount.setText("P " + audioInfo.getCounter());
		holder.tvMood.setText("# " + audioInfo.getSource());

		if (audioInfo.getUser_control().equals("1")) {
			holder.tvUserId.setText(audioInfo.getUser_code());
		} else {
			holder.tvUserId.setText("Anonymous");
		}
		Typeface face = Typeface.createFromAsset(context.getAssets(),
				"ARIAL.TTF");

		holder.tvTitle.setTypeface(face);
		holder.tvMood.setTypeface(face);
		holder.tvUserId.setTypeface(face);
		holder.tvFromvalue.setTypeface(face);
		holder.tvsentDate.setTypeface(face);

		
		
		return convertView;
	}
	
	private String TimeStampConverter(final String inputFormat,
			String inputTimeStamp, final String outputFormat)
			throws ParseException {
		return new SimpleDateFormat(outputFormat)
				.format(new SimpleDateFormat(inputFormat)
						.parse(inputTimeStamp));
	}


}
