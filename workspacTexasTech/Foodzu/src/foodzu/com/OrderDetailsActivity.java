package foodzu.com;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import foodzu.com.models.Data_Models;
import foodzu.com.models.Ordered_Products;
import foodzu.com.models.Orders_Model;

public class OrderDetailsActivity extends Activity {

	ArrayList<Ordered_Products> Product_Arraylist;
	Orders_Model Orderlist;
	ListView Product_list;
	TextView order_id, order_date, order_cost;
	OrderDetailsAdapter OD_Adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mydetailorder);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Product_Arraylist = new ArrayList<Ordered_Products>();

		if (new Data_Models().getProduct_data() != null)
			Orderlist = new Data_Models().getProduct_data();
		
		

		Product_list = (ListView) findViewById(R.id.order_details_list);
		order_id = (TextView) findViewById(R.id.order_id);
		order_date = (TextView) findViewById(R.id.order_date);
		order_cost = (TextView) findViewById(R.id.order_cost);
		
		order_id.setText(Orderlist.getOrder_ID().toString());
		order_date.setText(Orderlist.getOrder_Date().toString());
		order_cost.setText(Orderlist.getOrder_Total_Amount().toString());
		OD_Adapter = new OrderDetailsAdapter(OrderDetailsActivity.this,
				Orderlist.getProducts_data());
		Product_list.setAdapter(OD_Adapter);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	public class OrderDetailsAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<Ordered_Products> data;
		private LayoutInflater inflater = null;
		int value = 0;

		public OrderDetailsAdapter(Activity a, ArrayList<Ordered_Products> d) {
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
				convertView = inflater.inflate(R.layout.adapter_orderdetails, parent,
						false);
				holder = new ViewHolder();

				holder.prod_name = (TextView) convertView
						.findViewById(R.id.prod_name);
				holder.prod_qty = (TextView) convertView
						.findViewById(R.id.prod_qty);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// Picasso.with(activity).load(data.get(position).getitem_image()).fit()
			// .centerCrop().tag(activity).into(holder.imgThumbnail);
			holder.prod_name.setText(data.get(position).getPrd_Name());
			holder.prod_qty.setText(data.get(position).getPrd_Qty());
//			holder.order_cost.setText("\u20B9  "
//					+ Double.valueOf(data.get(position).getOrder_Total_Amount()));
			notifyDataSetChanged();
			return convertView;
		}

		class ViewHolder {
			// private ImageView imgThumbnail;
			private TextView prod_name, prod_qty;
		}

	}
}
