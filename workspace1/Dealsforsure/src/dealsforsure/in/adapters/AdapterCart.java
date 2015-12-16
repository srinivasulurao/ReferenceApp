package dealsforsure.in.adapters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import dealsforsure.in.R;
import dealsforsure.in.adapters.AdapterStore.deleteStoresync;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.Cart;
import dealsforsure.in.utils.Utils;

public class AdapterCart extends ArrayAdapter<Cart>{
	Context context;
	ViewHolder holder = null;
	private UserFunctions userFunction;
	List<Cart> myCartList;
	Cart cart;
	private Utils utils;
	SharedPreferences sharedPreferences ;
	Integer selectedPosition;
	
	public AdapterCart(Context context, int resourceId,
			List<Cart> myCartList) {
		super(context, resourceId, myCartList);
		this.context = context;
		this.myCartList = myCartList;
		userFunction = new UserFunctions();
		utils = new Utils(context);
		sharedPreferences= PreferenceManager
				.getDefaultSharedPreferences(context);
		
		
		//imageLoader = new ImageLoader(context);

	

	}
	private class ViewHolder {
		TextView  tvTitle,lblPriceValue,lblTotal;
		ImageView ivImage;
		EditText etQuantity;
		ImageView ivdelete;
		
		
	}
	public View getView(final int position, View convertView, ViewGroup parent) {
		cart = myCartList.get(position);
		
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_cart, null);

			holder = new ViewHolder();
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.tvTitle);
			holder.ivImage= (ImageView) convertView.findViewById(R.id.icmarker);
			holder.lblPriceValue= (TextView) convertView.findViewById(R.id.lblPriceValue);
			holder.etQuantity= (EditText) convertView.findViewById(R.id.etquantity);
			holder.lblTotal= (TextView) convertView.findViewById(R.id.lbltotalValue);
			holder.ivdelete= (ImageView) convertView
					.findViewById(R.id.ivdelete);
		
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		holder.etQuantity.setTag(position);
        holder.ivdelete.setId(position);
		
		holder.ivdelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectedPosition=v.getId();
				cart=myCartList.get(selectedPosition);
				displayConforim();
				
				
			}
		});
			
		holder.tvTitle.setText(cart.getTitle());
		holder.lblPriceValue.setText(cart.getPrice()+Utils.mCurrency);
		holder.etQuantity.setText(cart.getQuantity());
		Integer total=new Integer(cart.getPrice())*new Integer(cart.getQuantity());
		
		holder.lblTotal.setText(total.toString()+Utils.mCurrency);
		holder.etQuantity.clearFocus();
		holder.etQuantity.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(hasFocus){
			       // Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
			    }else {
			    	
			    	selectedPosition=new Integer(v.getTag().toString());
			    	String quantity=((TextView)holder.etQuantity).getText().toString();
			    	
			    	if(myCartList.size()>selectedPosition){
			    	
			    	cart=myCartList.get(selectedPosition);
			    	
			    	if(!cart.getQuantity().equals(quantity)){
			    	
			    		cart.setQuantity(quantity);
			    		holder.etQuantity.clearFocus();
			    		myCartList.set(selectedPosition,cart);
			    		notifyDataSetChanged();
			    		
			    	}
			    	}
			    	
			    	
			       // Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
			    }
			   }
			});
		
		 Picasso.with(context)
	        .load(userFunction.URLAdmin+userFunction.folderAdmin+cart.getImageUrl())
	        .fit().centerCrop()
	        .tag(context)
	        .into(holder.ivImage);
		return convertView;
	}

		
	void displayConforim() {
		TextView tcPromoCode, tvheader;
		ImageView ivClose;
		Button btOK, btncancel;
		String message;
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.dialog_promocode_confirm, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		tcPromoCode = (TextView) promptsView.findViewById(R.id.tvpromocode);
		tvheader = (TextView) promptsView.findViewById(R.id.tvheader);
		// ivClose = (ImageView) promptsView.findViewById(R.id.ivclose);
		btOK = (Button) promptsView.findViewById(R.id.btnset);
		btncancel = (Button) promptsView.findViewById(R.id.btncancel);
		
		
			message="Do you want to remove from your cart";
	
		tcPromoCode.setText(message);

		btOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				int i=selectedPosition;
				cart=myCartList.get(selectedPosition);
				
			
				
				SharedPreferences.Editor editor = sharedPreferences.edit();
				
				Set<String> cartItemIdset = sharedPreferences.getStringSet("cartItemId",
						new HashSet<String>());
				
				cartItemIdset.remove(cart.getCid());

				Set<String> cartItemSet = new HashSet<String>();

				cartItemSet = sharedPreferences.getStringSet("cartItem", new HashSet<String>());
				
				

				Gson gson = new Gson();

				String json = gson.toJson(cart);

				cartItemSet.remove(json);
				
				editor.remove("cartItemId");
				editor.remove("cartItem");

				editor.putStringSet("cartItemId", cartItemIdset);

				editor.putStringSet("cartItem", cartItemSet);

				// 
				editor.apply();
				editor.commit();

				myCartList.remove(i);
				notifyDataSetChanged();
				
				/* if(utils.isNetworkAvailable()) {
					
				} else {

					Toast.makeText(context, "Error in connection .",
							Toast.LENGTH_LONG).show();

				}
				*/
				 
				alertDialog.cancel();
			}
		});

		btncancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				alertDialog.cancel();
			}
		});

	}
		
		
		
		
		
		
	
	

}
