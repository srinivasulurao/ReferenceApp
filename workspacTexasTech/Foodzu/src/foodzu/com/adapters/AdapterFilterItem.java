package foodzu.com.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import foodzu.com.HomeActivity;
import foodzu.com.R;
import foodzu.com.models.FilterCategory;
import foodzu.com.models.FilterItem;

public class AdapterFilterItem extends ArrayAdapter<FilterItem> {

	Context context;
	List<FilterItem> filterItemList;
	ViewHolder holder = null;
	FilterItem filterItem;
	
	
	    

	public AdapterFilterItem(Context context, int resourceId,
			List<FilterItem> filterItemList) {
		super(context, resourceId, filterItemList);
		this.context = (Activity) context;
		this.filterItemList = filterItemList;
		

	}

	private class ViewHolder {

		TextView tvItemName;
		ImageView ivSelect;
		RelativeLayout rlBody;
		

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		filterItem = filterItemList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_filteritem, null);

			holder = new ViewHolder();

			holder.tvItemName = (TextView) convertView
					.findViewById(R.id.tvitemname);

			holder.ivSelect = (ImageView) convertView
					.findViewById(R.id.ivselect);
			holder.rlBody= (RelativeLayout) convertView
					.findViewById(R.id.rlbody);
			
			convertView.setTag(holder);
		} else

			holder = (ViewHolder) convertView.getTag();
		holder.rlBody.setId(position);
		holder.tvItemName.setText(filterItem.getItemName());
		
		
		if(filterItem.getType().equals("1")){

		if (((HomeActivity)context).getBrandSelactList().containsKey(filterItem.getId())) {
			holder.ivSelect.setImageResource(R.drawable.tick_select);
		} else {

			holder.ivSelect.setImageResource(R.drawable.tick_unselect);
		}
		}else{
			
			if (((HomeActivity)context).getPriceSelactList().containsKey(filterItem.getId())) {
				holder.ivSelect.setImageResource(R.drawable.tick_select);
			} else {

				holder.ivSelect.setImageResource(R.drawable.tick_unselect);
			}	
			
		}
		/*holder.rlBody.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				filterItem = filterItemList.get(v.getId());
				
				if(filterItem.getType().equals("1")){
				
				if (((HomeActivity) context).getBrandSelactList().containsKey(
						filterItem.getId())) {

					((HomeActivity) context).getBrandSelactList().remove(
							filterItem.getId());

				} else {

					((HomeActivity) context).getBrandSelactList().put(
							filterItem.getId(), filterItem);

				}
				}else{
					
					if (((HomeActivity) context).getPriceSelactList().containsKey(
							filterItem.getId())) {

						((HomeActivity) context).getPriceSelactList().remove(
								filterItem.getId());

					} else {

						((HomeActivity) context).getPriceSelactList().put(
								filterItem.getId(), filterItem);

					}
					
				}

				if (filterItem.getIsSelected().equals("0")) {
					;
					
				} else {
					
					
					if (((HomeActivity)context).getBrandSelactList().containsKey(filterItem.getId())) {
						
						((HomeActivity)context).getBrandSelactList().remove(filterItem.getId());
						
					}
					
				}

				filterItemList.set(v.getId(), filterItem);
				notifyDataSetChanged();

			}
		});*/

		return convertView;
	}

	
	
	

}
