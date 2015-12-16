package foodzu.com.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import foodzu.com.R;
import foodzu.com.models.AddressObj;

public class AdapterAddress extends ArrayAdapter<AddressObj> {
	
	Context context;
	List<AddressObj> addressList;
	ViewHolder holder = null;
	AddressObj address;
	Typeface face;
	
	
	public AdapterAddress(Context context, int resourceId,
			List<AddressObj> addressList) {
		super(context, resourceId, addressList);
		this.context = context;
		this.addressList = addressList;
		
		//imageLoader = new ImageLoader(context);

	

	}

	/* private view holder class */
	private class ViewHolder {
		TextView  tvAddressValue;
	

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		address = addressList.get(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_address, null);

			holder = new ViewHolder();
			holder.tvAddressValue = (TextView) convertView
					.findViewById(R.id.tvaddress);
			
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		holder.tvAddressValue.setTypeface(face);
		
		holder.tvAddressValue.setText(address.getAddressName());

		
		
		
		return convertView;
	}


}
