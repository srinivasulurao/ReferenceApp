package foodzu.com.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import foodzu.com.OrderDetailsActivity;
import foodzu.com.R;
import foodzu.com.models.Data_Models;
import foodzu.com.models.Orders_Model;

public class OldOrdersAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<Orders_Model> data;
	private static LayoutInflater inflater = null;
	int value = 0;

	public OldOrdersAdapter(Activity a, ArrayList<Orders_Model> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.adapter_myorders, parent,
					false);
			holder = new ViewHolder();

			holder.order_id = (TextView) convertView
					.findViewById(R.id.order_id);
			holder.order_date = (TextView) convertView
					.findViewById(R.id.order_date);
			holder.order_cost = (TextView) convertView
					.findViewById(R.id.order_cost);
			holder.order_view = (TextView) convertView
					.findViewById(R.id.order_view);
			holder.full_order = (LinearLayout) convertView
					.findViewById(R.id.order_detailview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Picasso.with(activity).load(data.get(position).getitem_image()).fit()
		// .centerCrop().tag(activity).into(holder.imgThumbnail);
		holder.order_id.setText(data.get(position).getOrder_ID());
		holder.order_date.setText(data.get(position).getOrder_Date());
		holder.order_cost.setText("\u20B9  "
				+ Double.valueOf(data.get(position).getOrder_Total_Amount()));
		holder.order_view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new Data_Models().setProduct_data(data.get(position));
				activity.startActivity(new Intent(activity, OrderDetailsActivity.class));
			}
		});

		return convertView;
	}

	static class ViewHolder {
		// private ImageView imgThumbnail;
		LinearLayout full_order;
		private TextView order_id, order_date, order_cost, order_view;
	}

}