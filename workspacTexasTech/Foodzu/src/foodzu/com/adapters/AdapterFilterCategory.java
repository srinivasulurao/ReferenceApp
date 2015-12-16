package foodzu.com.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import foodzu.com.FilterFragment;
import foodzu.com.HomeActivity;
import foodzu.com.R;
import foodzu.com.models.FilterCategory;

public class AdapterFilterCategory extends ArrayAdapter<FilterCategory> {

	Context context;
	List<FilterCategory> filterCategoryList;
	ViewHolder holder = null;
	FilterCategory filterCategory;
	FilterFragment filterFragment;
	Integer selectCount;
	
	public AdapterFilterCategory(Context context, int resourceId,
			List<FilterCategory> filterCategoryList,FilterFragment filterFragment) {
		super(context, resourceId, filterCategoryList);
		this.context = context;
		this.filterCategoryList = filterCategoryList;
		this.filterFragment=filterFragment;

	}

	private class ViewHolder {
	
		TextView tvCategoryName,tvSelectCount;
		
		//RelativeLayout rlBody;

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		filterCategory = filterCategoryList.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_filtercategory,
					null);

			holder = new ViewHolder();

			holder.tvCategoryName = (TextView) convertView
					.findViewById(R.id.tvcategory_name);
			holder.tvSelectCount= (TextView) convertView
					.findViewById(R.id.tvSelectCount);
			
			

			convertView.setTag(holder);
		} else

			holder = (ViewHolder) convertView.getTag();
		
		if(position==0){
			selectCount=((HomeActivity) context).getBrandSelactList().size();
		}else{
			
			 selectCount=((HomeActivity) context).getPriceSelactList().size();
		}
			
			if(selectCount>0){
				holder.tvSelectCount.setVisibility(View.VISIBLE);
				holder.tvSelectCount.setText(selectCount.toString());
				
			}else{
				
				holder.tvSelectCount.setVisibility(View.GONE);
			}
			
		
		
		
		
		if (filterFragment.getSelectPosition() == position) {
			holder.tvCategoryName.setBackgroundResource(R.color.grey_light);
			holder.tvCategoryName.setTextColor(context.getResources().getColor(R.color.black));
		} else {
			holder.tvCategoryName.setBackgroundResource(R.color.grey);
			holder.tvCategoryName.setTextColor(context.getResources().getColor(R.color.White));
		}
		holder.tvCategoryName.setText(filterCategory.getCategoryName());

		return convertView;
	}

}
