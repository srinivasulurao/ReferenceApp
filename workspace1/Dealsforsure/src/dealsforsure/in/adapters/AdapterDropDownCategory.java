package dealsforsure.in.adapters;

import java.util.List;

import dealsforsure.in.R;
import dealsforsure.in.model.Category;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



public class AdapterDropDownCategory extends ArrayAdapter<Category> {
 
    Context context;
 
    public AdapterDropDownCategory(Context context, int resourceId,
            List<Category> categorylist) {
        super(context, resourceId, categorylist);
        this.context = context;
    }
    
    @Override
	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {

		return getCustomView(position, convertView, parent);
	}
    
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}


    
    /*private view holder class*/
    private class ViewHolder {
     
      
       
    }
 
    public View getCustomView(int position, View convertView,
			ViewGroup parent) {
    	Category category = getItem(position);
    	 LayoutInflater mInflater = (LayoutInflater) context
                 .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View row = mInflater.inflate(R.layout.spinner_content, parent, false);
		TextView label = (TextView) row.findViewById(R.id.tvname);
		label.setText(category.getCategoryName());
		
		
		return row;
	}


}
