package dealsforsure.in.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.squareup.picasso.Picasso;

import dealsforsure.in.R;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.Review;

public class AdapterReview extends ArrayAdapter<Review> {

	Context context;
	List<Review> reviewList;
	ViewHolder holder = null;
	Review review;
	Typeface face;

	public AdapterReview(Context context, int resourceId,
			List<Review> reviewList) {
		super(context, resourceId, reviewList);
		this.context = context;
		this.reviewList = reviewList;

		// imageLoader = new ImageLoader(context);

	}

	/* private view holder class */
	private class ViewHolder {
		TextView lblcomments;
		TextView lblsendby;
		TextView lbldate;
		LinearLayout llstarrate;

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		review = reviewList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_review, null);

			holder = new ViewHolder();
			holder.lblcomments = (TextView) convertView
					.findViewById(R.id.lblcomments);
			holder.lblsendby = (TextView) convertView
					.findViewById(R.id.lblsendby);
			holder.lbldate = (TextView) convertView.findViewById(R.id.lbldate);
			holder.llstarrate = (LinearLayout) convertView
					.findViewById(R.id.llstarrate);

			convertView.setTag(holder);
		} else

			holder = (ViewHolder) convertView.getTag();

		holder.llstarrate.removeAllViews();

		Integer countStar = new Integer(review.getStarRate());

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				20, 20);

		for (Integer i = 0; i < countStar; i++) {

			ImageView myImage = new ImageView(context);
			myImage.setLayoutParams(layoutParams);
			myImage.setImageResource(R.drawable.star);
			holder.llstarrate.addView(myImage);

		}

		holder.lblcomments.setText(review.getComments());
		holder.lblsendby.setText("By " + review.getUserName());
		holder.lbldate.setText(review.getDate());

		// holder.rlMain.setBackgroundColor(context.getResources().getColor(R.color.text_link));

		return convertView;
	}

}
