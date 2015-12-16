package com.aotd.service;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.aotd.helpers.AOTDDataBase;
import com.aotd.parsers.DispatchCountParser;
import com.aotd.utils.Utils;

public class DispatchOrderService extends Service{
	
	private final int DISPATCH_COUNT_UPDATE = 3;
	public static final int SUCCESS = 1;
	public static final int ERROR = 0;
	private final String DISPATCH_COUNT_URL = Utils.DISPATCH_COUNT_URL;
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	private  long UPDATE_INTERVAL = 10000;
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_TO_CLIENT = 3;
	
	private Timer timer = new Timer();
	private String list = "";
	private ArrayList<Messenger> mClients = new ArrayList<Messenger>();	
	private Message msg;
	private Bundle msgData;
	String fromUserID = null;
	SharedPreferences loginprefs;
	
	
	class IncomingHandler extends Handler { 
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				//sLog.v("size@@@@@@@@@@@@@@@@@@@@@",""+mClients.size());
				fromUserID = msg.getData().getString("fromuserid");
				break;
				
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
				
			default: 
				super.handleMessage(msg);
			}
		}
	}
	
	public void onCreate() {
		
		super.onCreate();
		pollForUpdates();
		
		loginprefs	= this.getSharedPreferences("loginprefs", 0);	
		SharedPreferences.Editor prefsEditor = loginprefs.edit();
		prefsEditor.putInt("orderIdsCount",0);
		prefsEditor.commit();	
	}
	
	private void pollForUpdates() {
		
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// Imagine here a freaking cool web access ;-)
				if(fromUserID != null){
					messagesHandler.sendEmptyMessage(DISPATCH_COUNT_UPDATE);
					UPDATE_INTERVAL = 20000;				
				}else{
					UPDATE_INTERVAL = 20000;
				}
			}
		}, 0, UPDATE_INTERVAL);
		
		Log.i(getClass().getSimpleName(), "Dispatach Order service started.");
		
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		if (timer != null) {
			timer.cancel();
		}
		
		Log.i(getClass().getSimpleName(), "Timer stopped.");
		
	}
	
	// We return the binder class upon a call of bindService
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}
	
	private void sendMessageToUI(String tag) {
		
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				// Send data as an Integer
				msg = new Message();
				msgData = new Bundle();
				if(tag.equalsIgnoreCase("response")) 
					msg.what = MSG_TO_CLIENT;
				else
					msg.what = ERROR;
				
				msgData.putSerializable(tag,  list);
				msg.setData(msgData);
				
				mClients.get(i).send(msg);			
				
			} catch (RemoteException e) {
				// The client is dead. Remove it from the list; we are going
				// through the list from back to front so this is safe to do
				// inside the loop.
				mClients.remove(i);
			}
		}
	}
	
	private void deleteOrder(String driverId, String orderIds){
		
		AOTDDataBase _AOTDDataBase = new AOTDDataBase(this);
		_AOTDDataBase.deleteOredrs(driverId, orderIds);
	}
	
	
	private void deleteDPDateOredr(String driverId){
		
		AOTDDataBase _AOTDDataBase = new AOTDDataBase(this);
		_AOTDDataBase.deleteDPDateOredrs(driverId);
	}
	
	
	private Handler messagesHandler = new Handler() {
		
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String url = String.format(DISPATCH_COUNT_URL,Utils.ROLENAME,fromUserID);
			switch (msg.what) {
			case DISPATCH_COUNT_UPDATE:
				new DispatchCountParser(url, this).start();
				break;
				
			case SUCCESS:
				
				String response =  msg.getData().getString("response");	
				String orderIds =  msg.getData().getString("orderIds");	
				
				list = response;
				int count = Integer.parseInt(response);
				
				
				if(!orderIds.equalsIgnoreCase("")){ 
					
					if(	count < loginprefs.getInt("orderIdsCount",0)){
						
						SharedPreferences.Editor prefsEditor = loginprefs.edit();
						prefsEditor.putInt("orderIdsCount",Integer.parseInt(response));
						prefsEditor.commit();					
						deleteOrder(fromUserID, orderIds);
						
					}else{
						
						SharedPreferences.Editor prefsEditor = loginprefs.edit();
						prefsEditor.putInt("orderIdsCount",Integer.parseInt(response));
						prefsEditor.commit();						
						System.out.println("No orders to delete");
					}
					
					
				}else{
					
					System.out.println("orders count "+loginprefs.getInt("orderIdsCount",0));
					
					if(loginprefs.getInt("orderIdsCount",0) != 0){
						
						SharedPreferences.Editor prefsEditor = loginprefs.edit();
						prefsEditor.putInt("orderIdsCount",Integer.parseInt(response));
						prefsEditor.commit();
						deleteDPDateOredr(fromUserID);
						
					}
				}
				
				
				sendMessageToUI("response");
				break;
				
			case ERROR:
				String response1 =  msg.getData().getString("error");				 
				list = response1;
				sendMessageToUI("error");
				break;
			}
		}
	};
}
