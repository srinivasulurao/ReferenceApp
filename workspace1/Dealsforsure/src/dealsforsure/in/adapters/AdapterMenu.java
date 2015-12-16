package dealsforsure.in.adapters;

import dealsforsure.in.ActivityProfile;
import dealsforsure.in.R;
import dealsforsure.in.fragments.FragmentNavigationDrawer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
public class AdapterMenu extends BaseAdapter {

		private Activity activity;	
		 SharedPreferences sharedPreferences;
		
		public AdapterMenu(Activity act) {
			this.activity = act;

		}
		
		public int getCount() {
			// TODO Auto-generated method stub
			return FragmentNavigationDrawer.listMenu.length;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.adapter_menu, null);
				holder = new ViewHolder();
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.txtCounter= (TextView) convertView.findViewById(R.id.txtCounter);
			holder.txtCategory = (TextView) convertView.findViewById(R.id.txtCategory);
			holder.imgMenu = (ImageView) convertView.findViewById(R.id.ic_img);
			holder.txtCategory.setText(FragmentNavigationDrawer.listMenu[position]);
			
			
			
			if(FragmentNavigationDrawer.listMenu[position].equals("Profile")){
				holder.txtCounter.setVisibility(View.VISIBLE);
				sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(activity);
				
				String userPoints= sharedPreferences.getString("userPoint", null);
				holder.txtCounter.setText(userPoints);
				
				
			}else{
				holder.txtCounter.setVisibility(View.GONE);
			}
			
			holder.imgMenu.setBackgroundResource(FragmentNavigationDrawer.imageMenu[position]);
			
			
			
			return convertView;
		}
		
		static class ViewHolder {
			TextView txtCategory,txtCounter;
			ImageView	imgMenu;
		}
		
		
	}