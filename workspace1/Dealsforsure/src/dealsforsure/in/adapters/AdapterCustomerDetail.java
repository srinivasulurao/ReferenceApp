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
import dealsforsure.in.model.CustomerDetail;

public class AdapterCustomerDetail extends ArrayAdapter<CustomerDetail> {
	
	Context context;
	List<CustomerDetail> customerDetailList ;
	ViewHolder holder = null;
	CustomerDetail customerDetal;
	
	public AdapterCustomerDetail(Context context, int resourceId, List<CustomerDetail> customerDetailList) {
		super(context, resourceId, customerDetailList);
		
		this.context = context;
		this.customerDetailList = customerDetailList;
		
		
	}
	
	private class ViewHolder {
		
		TextView tvStoreName,tvDate,tvAmount;
		
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		customerDetal = customerDetailList.get(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_customer_detail, null);

			holder = new ViewHolder();
			
			holder.tvStoreName = (TextView) convertView
					.findViewById(R.id.tvstorename);
			holder.tvDate = (TextView) convertView
					.findViewById(R.id.tvdate);
			holder.tvAmount = (TextView) convertView
					.findViewById(R.id.tvamount);
			convertView.setTag(holder);
			
		}else
			holder = (ViewHolder) convertView.getTag();
		    holder.tvStoreName.setText(customerDetal.getStoreName());
		    holder.tvDate.setText(customerDetal.getDate());
		    holder.tvAmount.setText(customerDetal.getAmount());

		return convertView;
	}
	
	
	

}
