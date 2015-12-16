package dealsforsure.in.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import dealsforsure.in.R;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.MyDeals;

public class AdapterOrder extends ArrayAdapter<MyDeals> {
	
	Context context;
	List<MyDeals> myDealsList;
	ViewHolder holder = null;
	MyDeals myDeals;
	Typeface face;
	
	private UserFunctions userFunction;
	
	public AdapterOrder(Context context, int resourceId,
			List<MyDeals> myDealsList) {
		super(context, resourceId, myDealsList);
		this.context = context;
		this.myDealsList = myDealsList;
		
		//imageLoader = new ImageLoader(context);

	

	}

	/* private view holder class */
	private class ViewHolder {
		TextView  tvDealTitle;
		TextView  tvprice;
		TextView  tvpurchasedate;
		TextView  tvofferDeatil;
		ImageView ivImage;
		RelativeLayout rlMain;
	

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		myDeals = myDealsList.get(position);
		
		userFunction = new UserFunctions();
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_order, null);

			holder = new ViewHolder();
			holder.tvDealTitle = (TextView) convertView
					.findViewById(R.id.lblNameCategory);
			holder.tvprice= (TextView) convertView.findViewById(R.id.tvprice);
			holder.ivImage= (ImageView) convertView.findViewById(R.id.icmarker);
			holder.rlMain= (RelativeLayout) convertView.findViewById(R.id.rlmain);
			
			holder.tvpurchasedate= (TextView) convertView.findViewById(R.id.tvpurchasedate);
			holder.tvofferDeatil= (TextView) convertView.findViewById(R.id.lblofferDeatil);
			
			convertView.setTag(holder);
		} else
			
			
			holder = (ViewHolder) convertView.getTag();
		
		//holder.rlMain.setBackgroundColor(context.getResources().getColor(R.color.text_link));
		
	
		
			
			holder.rlMain.setBackgroundColor(Color.parseColor("#D5EA83"));
			
		
			
		
		
		
		 Picasso.with(context)
	        .load(userFunction.URLAdmin+userFunction.folderAdmin+myDeals.getImageUrl())
	        .fit().centerCrop()
	        .tag(context)
	        .into(holder.ivImage);
		

		holder.tvDealTitle.setText(myDeals.getDealTitle());
		holder.tvprice.setText(myDeals.getAmount());
		
		holder.tvpurchasedate.setText(myDeals.getPurchaseDate());
		holder.tvofferDeatil.setText(myDeals.getOfferDetail());
		
		return convertView;
	}


}
