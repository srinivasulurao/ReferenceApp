package foodzu.com.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import foodzu.com.NavigationDrawerFragment;
import foodzu.com.R;

@SuppressLint("InflateParams")
public class SubCategoryMenu extends BaseAdapter {

	private Activity activity;
	SharedPreferences sharedPreferences;
	int i = 0;

	public SubCategoryMenu(Activity act) {
		this.activity = act;

	}

	public int getCount() {
		return NavigationDrawerFragment.subcategorylist.size();
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
			convertView = inflater.inflate(R.layout.adapter_subcat_menu, null);
			holder = new ViewHolder();

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txtCounter = (TextView) convertView
				.findViewById(R.id.txtCounter);
		holder.txtCategory = (TextView) convertView
				.findViewById(R.id.txtCategory);
		holder.imgMenu = (ImageView) convertView.findViewById(R.id.ic_img);
		holder.txtCategory.setText(NavigationDrawerFragment.subcategorylist
				.get(position));

		holder.txtCategory.setTextColor(Color.WHITE);
		/*
		 * int sdk = android.os.Build.VERSION.SDK_INT; if (sdk <
		 * android.os.Build.VERSION_CODES.JELLY_BEAN) {
		 * convertView.setBackgroundDrawable(activity.getResources()
		 * .getDrawable(R.drawable.green_bg)); } else {
		 * convertView.setBackground(activity.getResources().getDrawable(
		 * R.drawable.green_bg)); }
		 */
		// convertView.setBackgroundColor(Color.parseColor("#4CBB17"));

		return convertView;
	}

	static class ViewHolder {
		TextView txtCategory, txtCounter;
		ImageView imgMenu;
	}

}