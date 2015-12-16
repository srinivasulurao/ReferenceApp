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
import dealsforsure.in.model.Category;
import dealsforsure.in.model.Store;

public class AdapterDroupDownStore extends ArrayAdapter<Store> {
 
    Context context;
 
    public AdapterDroupDownStore(Context context, int resourceId,
            List<Store> storelist) {
        super(context, resourceId, storelist);
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
    	Store store = getItem(position);
    	 LayoutInflater mInflater = (LayoutInflater) context
                 .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View row = mInflater.inflate(R.layout.spinner_content, parent, false);
		TextView label = (TextView) row.findViewById(R.id.tvname);
		label.setText(store.getStoreName());
		
		
		return row;
	}

}
