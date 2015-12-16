package dealsforsure.in.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dealsforsure.in.R;
import dealsforsure.in.model.Customer;

public class AdapterMyCustomer extends ArrayAdapter<Customer> {
	Context context;
	List<Customer> customerList ;
	ViewHolder holder = null;
	Customer customer;
	
	public AdapterMyCustomer(Context context, int resourceId, List<Customer> customerList) {
		super(context, resourceId, customerList);
		
		this.context = context;
		this.customerList = customerList;
		
		
	}
	
	private class ViewHolder {
		
		TextView tvCustomerName,tvPhone,tvVisitCount,tvPrice;
		
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		customer = customerList.get(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_my_customer, null);

			holder = new ViewHolder();
			
			holder.tvCustomerName = (TextView) convertView
					.findViewById(R.id.tvcustomername);
			holder.tvPhone = (TextView) convertView
					.findViewById(R.id.tvphone);
			holder.tvVisitCount = (TextView) convertView
					.findViewById(R.id.tvvisitcount);
			holder.tvPrice= (TextView) convertView
					.findViewById(R.id.tvprice);
			
			convertView.setTag(holder);
			
		}else
			holder = (ViewHolder) convertView.getTag();
		
		holder.tvCustomerName.setText(customer.getName());
		holder.tvPhone.setText(customer.getPhone());
		holder.tvVisitCount.setText(customer.getVisitCount());
		holder.tvPrice.setText(customer.getTotalAmount());
		return convertView;
	}
}
