package dealsforsure.in.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dealsforsure.in.R;
import dealsforsure.in.model.SuggestionKeyword;

public class AdapterSearchSuggestion extends ArrayAdapter<SuggestionKeyword> {
	
	Context context;
	List<SuggestionKeyword> suggestionList;
	ViewHolder holder = null;
	SuggestionKeyword suggestionKeyword;
	Typeface face;
	
	
	public AdapterSearchSuggestion(Context context, int resourceId,
			List<SuggestionKeyword> suggestionList) {
		super(context, resourceId, suggestionList);
		this.context = context;
		this.suggestionList = suggestionList;
		
		//imageLoader = new ImageLoader(context);

	

	}

	/* private view holder class */
	private class ViewHolder {
		TextView  tvAddressValue;
	

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		suggestionKeyword = suggestionList.get(position);
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
		
		holder.tvAddressValue.setText(suggestionKeyword.getSuggestion());

		
		
		
		return convertView;
	}


}
