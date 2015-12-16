package foodzu.com.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import foodzu.com.R;
import foodzu.com.models.Products;

public class Cart_CheckoutAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<Products> data;
	private static LayoutInflater inflater = null;
	int value = 0;

	// Declare object of userFunctions class
	// private UserFunctions userFunction;

	public Cart_CheckoutAdapter(Activity a, ArrayList<Products> d) {
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
			convertView = inflater
					.inflate(R.layout.adapter_cart, parent, false);
			holder = new ViewHolder();

			holder.prod_name = (TextView) convertView
					.findViewById(R.id.Prod_name);
			holder.prod_qty = (TextView) convertView
					.findViewById(R.id.Prod_qty);
			holder.prod_cost = (TextView) convertView
					.findViewById(R.id.Prod_cost);
			holder.final_cost = (TextView) convertView
					.findViewById(R.id.Prod_cost_price);
			holder.imgThumbnail = (ImageView) convertView
					.findViewById(R.id.imgThumbnail);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Double value = Double.valueOf(data.get(position)
				.getfinal_price())
				* Double.valueOf(data.get(position)
						.getitem_qty_count());
		holder.prod_name.setText(data.get(position).getitem_name());
		holder.prod_qty.setText(data.get(position).getpd_wieght());
		holder.prod_cost.setText("\u20B9  "
				+ data.get(position).getfinal_price() + "  X  "
				+ data.get(position).getitem_qty_count());
		holder.final_cost.setText("= \u20B9  " + Double.valueOf(value));

		if (data.get(position).getitem_image() == null
				|| data.get(position).getitem_image().equals(""))
			holder.imgThumbnail.setImageResource(R.drawable.no_image);
		else
			Picasso.with(activity)
					.load(data.get(position).getitem_image()).fit()
					.centerCrop().tag(activity).into(holder.imgThumbnail);
		return convertView;
	}

	// Method to create instance of views
	static class ViewHolder {
		private ImageView imgThumbnail;
		private TextView prod_name, prod_qty, prod_cost, final_cost;
		ImageLoader imgLoader;
	}

}