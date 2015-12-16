package foodzu.com.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import foodzu.com.NavigationDrawerFragment;
import foodzu.com.R;

public class AdapterMenu extends BaseAdapter {

	private Activity activity;
	int chosenOne = -1;
	SharedPreferences sharedPreferences;
	int i = 0;

	static int[] imagelist = { R.drawable.home_icon, R.drawable.sweet_grn,
			R.drawable.bakery_grn, R.drawable.choco_grn,
			R.drawable.dryfruit_grn, R.drawable.health_food_grn,
			R.drawable.herbal_grn, R.drawable.savories_grn,
			R.drawable.others_grn, R.drawable.grocery_grn, R.drawable.non_food_grn };

	public AdapterMenu(Activity act) {
		this.activity = act;
	}

	public int getCount() {
		return NavigationDrawerFragment.categorylist.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.adapter_cat_menu, parent,
					false);
			holder = new ViewHolder();

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtCounter = (TextView) convertView
				.findViewById(R.id.txtCounter);
		holder.txtCategory = (TextView) convertView
				.findViewById(R.id.txtCategory);
		// holder.txtCategory.setTextColor(Color.parseColor("#4CBB17"));
		holder.imgMenu = (ImageView) convertView.findViewById(R.id.ic_img);
		holder.txtCategory.setText(NavigationDrawerFragment.categorylist
				.get(position));
//		holder.imgMenu.setImageResource(imagelist[position]);
		return convertView;
	}

	static class ViewHolder {
		TextView txtCategory, txtCounter;
		ImageView imgMenu;
	}

}