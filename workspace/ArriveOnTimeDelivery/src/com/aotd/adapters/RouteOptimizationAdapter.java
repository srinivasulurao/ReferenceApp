package com.aotd.adapters;

import java.util.ArrayList;

import com.aotd.activities.DeliveryOrderInfoDelivery;
import com.aotd.activities.DeliveryOrderInfoPickUp;
import com.aotd.activities.DispatchPresentTabActivity;
import com.aotd.activities.R;
import com.aotd.activities.RoundTripActivity;
import com.aotd.model.DispatchAllListModel;
import com.aotd.utils.Utils;
import com.vinscan.barcode.FinishListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class RouteOptimizationAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<DispatchAllListModel> mDriverOrdersList;
	private LayoutInflater mInflater;

	public RouteOptimizationAdapter(Context ctx, ArrayList<DispatchAllListModel> driverOrdersList) {
		context = ctx;
		mDriverOrdersList = driverOrdersList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		return mDriverOrdersList.size();
	}

	@Override
	public DispatchAllListModel getItem(int position) {
		return mDriverOrdersList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		System.out.println("kkk Position " + position);
			
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.aotd_route_optimization_row, null);
			
			viewHolder.btnOrederNumber = (Button) convertView.findViewById(R.id.btn_oreder_number);
			viewHolder.txtOrderPriority = (TextView) convertView.findViewById(R.id.txt_order_priority);
			
			viewHolder.txtPUCompanyName = (TextView) convertView.findViewById(R.id.txt_pu_company_name);			
			viewHolder.txtPUCompanyAddress = (TextView) convertView.findViewById(R.id.txt_pu_company_address);
			
			viewHolder.txtDLCompanyName = (TextView) convertView.findViewById(R.id.txt_dl_company_name);
			viewHolder.txtDLCompanyAddress = (TextView) convertView.findViewById(R.id.txt_dl_company_address);


			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.btnOrederNumber.setText(mDriverOrdersList.get(position).getOrder_id());
		viewHolder.txtOrderPriority.setText((position+1)+"");

		
		viewHolder.txtPUCompanyName.setText("P/U : "+mDriverOrdersList.get(position).getCompany());
		String puAddress = "Address : "+mDriverOrdersList.get(position).getAddress()+"\n"+ "Suite : "+mDriverOrdersList.get(position).getSuit()+"\n"+"City : "+mDriverOrdersList.get(position).getCity()+"\n"+"State : "+mDriverOrdersList.get(position).getState()+"\n"+"Zip : "+mDriverOrdersList.get(position).getZip();
		 
		viewHolder.txtPUCompanyAddress.setText(puAddress);

		String dlAddress = "Address : "+mDriverOrdersList.get(position).getDladdress()+"\n"+ "Suite : "+mDriverOrdersList.get(position).getDlsuit()+"\n"+"City : "+mDriverOrdersList.get(position).getDlcity()+"\n"+"State : "+mDriverOrdersList.get(position).getDlstate()+"\n"+"Zip : "+mDriverOrdersList.get(position).getDlzip();
		viewHolder.txtDLCompanyName.setText("D/L : "+mDriverOrdersList.get(position).getDlcompany());
		viewHolder.txtDLCompanyAddress.setText(dlAddress);

		
		if(mDriverOrdersList.get(position).getOrderColor().trim().equalsIgnoreCase("green"))
		{				 
			if(mDriverOrdersList.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDriverOrdersList.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
//				viewHolder.btnOrederNumber.setBackgroundResource(R.drawable.btn_bg_pickedup_green_roundtrip);
				viewHolder.btnOrederNumber.setTag("1");
			}		 
			else{
//				viewHolder.btnOrederNumber.setBackgroundResource(R.drawable.btn_bg_pickedup_green);
				viewHolder.btnOrederNumber.setTag("0");
			}	 
		}	 
		else if(mDriverOrdersList.get(position).getOrderColor().trim().equalsIgnoreCase("red")){
			if(mDriverOrdersList.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDriverOrdersList.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
//				viewHolder.btnOrederNumber.setBackgroundResource(R.drawable.btn_bg_pickedup_red_roundtrip);
				viewHolder.btnOrederNumber.setTag("1");
			}		 
			else{
//				viewHolder.btnOrederNumber.setBackgroundResource(R.drawable.btn_bg_pickedup_red);
				viewHolder.btnOrederNumber.setTag("0");
			}
		}	 
		else if(mDriverOrdersList.get(position).getOrderColor().trim().equalsIgnoreCase("orange")){
			if(mDriverOrdersList.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDriverOrdersList.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
//				viewHolder.btnOrederNumber.setBackgroundResource(R.drawable.btn_bg_pickedup_orange_roundtrip);
				viewHolder.btnOrederNumber.setTag("1");
			}	 
			else{
//				viewHolder.btnOrederNumber.setBackgroundResource(R.drawable.btn_bg_pickedup_orange);
				viewHolder.btnOrederNumber.setTag("0");
			}
		}
		else if(mDriverOrdersList.get(position).getOrderColor().trim().equalsIgnoreCase("blue") ){
			if(mDriverOrdersList.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDriverOrdersList.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
//				viewHolder.btnOrederNumber.setBackgroundResource(R.drawable.btn_past_delivered_roundtrip);
				viewHolder.btnOrederNumber.setTag("1");
			}	 
			else{
//				viewHolder.btnOrederNumber.setBackgroundResource(R.drawable.btn_past_delivered);
				viewHolder.btnOrederNumber.setTag("0");
			}
		}
		else if(mDriverOrdersList.get(position).getOrderColor().trim().equalsIgnoreCase("white") ){				 
//			viewHolder.btnOrederNumber.setBackgroundResource(R.drawable.btn_bg_openorder);
			viewHolder.btnOrederNumber.setTag("0");
		}
		
		if(!mDriverOrdersList.get(position).getSignDelivery().trim().equalsIgnoreCase("") && !mDriverOrdersList.get(position).getSignRoundTrip().trim().equalsIgnoreCase("")) 
		{	
			viewHolder.btnOrederNumber.setEnabled(false);
		}else{
			viewHolder.btnOrederNumber.setEnabled(true);
			
		}
		
		viewHolder.btnOrederNumber.setOnClickListener(new OnClickListener() 
		{
			
			public void onClick(View v) {
				
				Log.i("", "kkk calling"+ mDriverOrdersList.get(position).getOrderStatus());
				if(mDriverOrdersList.get(position).getOrderStatus().toString().equalsIgnoreCase("Open Order")){
					
					Intent intent=new Intent(context,DeliveryOrderInfoPickUp.class);
					intent.putExtra("orderNumber", mDriverOrdersList.get(position).getOrder_id().toString());	
					intent.putExtra("openorder", "true");
					context.startActivity(intent);
					((Activity)context).finish();
					
				}else if(mDriverOrdersList.get(position).getOrderStatus().toString().startsWith("Picked")){
					
					Intent intent=new Intent(context,DeliveryOrderInfoDelivery.class);
					intent.putExtra("orderNumber",  mDriverOrdersList.get(position).getOrder_id().toString());	
					intent.putExtra("openorder", "false");
					intent.putExtra("condition", "updateSingDelivered");
					Utils.isRoundTrip = mDriverOrdersList.get(position).getIsRoundTrip();
					context.startActivity(intent);
					
				}else if(mDriverOrdersList.get(position).getOrderStatus().toString().startsWith("Delivered")){
					
					//condition 1 
					String roundtrip = viewHolder.btnOrederNumber.getTag().toString(); 
					if(!mDriverOrdersList.get(position).getSignDelivery().trim().equalsIgnoreCase("")   && roundtrip.equalsIgnoreCase("1") && mDriverOrdersList.get(position).getPCRoundTrip().trim().equalsIgnoreCase("1")){
						
						Intent intent=new Intent(context,DeliveryOrderInfoDelivery.class);
						intent.putExtra("orderNumber",  mDriverOrdersList.get(position).getOrder_id().toString());	
						intent.putExtra("openorder", "false");
						intent.putExtra("roundtrip", roundtrip);
						intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
						context.startActivity(intent);
				
					}else if(!mDriverOrdersList.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")  && mDriverOrdersList.get(position).getPCRoundTrip().trim().equalsIgnoreCase("0")){			
					
						Intent intent=new Intent(context,RoundTripActivity.class);
						intent.putExtra("orderNumber",  mDriverOrdersList.get(position).getOrder_id().toString());	
						intent.putExtra("openorder", "false");
						intent.putExtra("roundtrip", roundtrip);
						intent.putExtra("condition", "UpdateRoundTripPickup");
						context.startActivity(intent);
					
					}else if(!mDriverOrdersList.get(position).getSignDelivery().trim().equalsIgnoreCase("") && roundtrip.equalsIgnoreCase("0")){									
						
						roundTripAlertDialog(mDriverOrdersList.get(position).getOrder_id().toString(), roundtrip);
						
					}else if(!mDriverOrdersList.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")){			
					
						Intent intent=new Intent(context,DeliveryOrderInfoDelivery.class);
						intent.putExtra("orderNumber",  mDriverOrdersList.get(position).getOrder_id().toString());	
						intent.putExtra("openorder", "false");
						intent.putExtra("roundtrip", roundtrip);
						intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
						context.startActivity(intent);
						
					}
				}	
			}
		});
		
		return convertView;
	}
	
	private class ViewHolder{

		private Button btnOrederNumber;

		private TextView txtOrderPriority;

		private TextView txtPUCompanyName;
		
		private TextView txtPUCompanyAddress;

		private TextView txtDLCompanyName;

		private TextView txtDLCompanyAddress;


	}

private void roundTripAlertDialog(String mOrderNum, String roundtrip){
		
		final String ornum = mOrderNum;
		final String rtrip = roundtrip;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("AOTD");
		builder.setMessage("Order already delivered do you wish to choose RonudTrip manually.")
		       .setCancelable(false)					       
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   
		        	
		        		
		           }
		       })
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	
		               
		        	   Intent intent=new Intent(context,RoundTripActivity.class);
						intent.putExtra("orderNumber", ornum);	
						intent.putExtra("openorder", "false");
						intent.putExtra("roundtrip", rtrip);
						intent.putExtra("condition", "UpdateRoundTripPickupByDriver");
						context.startActivity(intent);
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
}
