package com.aotd.activities;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aotd.helpers.DownLoadAcceptPDFAsync;
import com.aotd.utils.Utils;

public class DownLoadAcceptPDF {

	private Context context;
	private ArrayList<String> marrSelectedOrders = new ArrayList<String>();
	private static ArrayList<String> tempMarrSelectedOrders = new ArrayList<String>();

	private Handler handler;

	private int itemCount = 0;

	
	public DownLoadAcceptPDF(Context context, Handler handler, ArrayList<String> marrSelectedOrders){
		
		this.context 		= 	context;
		this.handler 		= 	handler;
		this.marrSelectedOrders = marrSelectedOrders;
		for(int i=0;i < marrSelectedOrders.size();i++){
			tempMarrSelectedOrders.add(marrSelectedOrders.get(i));
		}
		
		Log.e("", "kkk constructor.... "+marrSelectedOrders+tempMarrSelectedOrders);

		downloadPDFFromServer();
	}
	
	private void downloadPDFFromServer(){

		/**
		 * down load pdf
		 */
		Log.e("", "kkk down load pdf.... "+marrSelectedOrders+tempMarrSelectedOrders);
		if(marrSelectedOrders != null && marrSelectedOrders.size()>0){
			
			Log.i("", "kkk order number: "+marrSelectedOrders.get(itemCount).toString().trim());

			String pdfUrl = String.format(Utils.PDF_DOWNLOAD_URL,marrSelectedOrders.get(itemCount).toString().trim());
			Log.i("", "kkk downloadPDFFromServer url: "+pdfUrl);

			new DownLoadAcceptPDFAsync(context, new DownLoadAcceptPDFHandler()).execute(pdfUrl,marrSelectedOrders.get(itemCount).toString().trim());
		}
		
	}
	
	
	class DownLoadAcceptPDFHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			
			Log.e("", "kkk DownLoadAcceptPDFHandler.... "+marrSelectedOrders+tempMarrSelectedOrders);

			
				try{
					marrSelectedOrders.remove(0);
					if(marrSelectedOrders != null && marrSelectedOrders.size()>0){
						Log.e("", "kkk DownLoadAcceptPDFHandler....if "+marrSelectedOrders+tempMarrSelectedOrders);
						downloadPDFFromServer();
					}else{
						Log.e("", "kkk DownLoadAcceptPDFHandler....else "+marrSelectedOrders+tempMarrSelectedOrders);

						Message messageToParent = new Message();
						Bundle messageData = new Bundle();
						messageToParent.what = 1;		
						messageData.putStringArrayList("marrSelectedOrders", tempMarrSelectedOrders);
						messageToParent.setData(messageData);
						handler.sendMessage(messageToParent);
						//handler.sendEmptyMessage(1);
					}
				}catch (Exception e) {
					e.printStackTrace();
					Log.e("", "kkk DownLoadAcceptPDFHandler....catch "+marrSelectedOrders+tempMarrSelectedOrders);

					Message messageToParent = new Message();
					Bundle messageData = new Bundle();
					messageToParent.what = 1;		
					messageData.putStringArrayList("marrSelectedOrders", tempMarrSelectedOrders);
					messageToParent.setData(messageData);
					handler.sendMessage(messageToParent);
				}
			
				
		}
	}
	
}
