package com.texastech.helper;

import android.content.Context;
import android.telephony.TelephonyManager;

public class DeviceUuidFactory {
	
    
    public static String getUDID(Context context){
        try {
        	final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE )).getDeviceId();
            //UUID uuid = deviceId!=null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
        	//UUID uuid = UUID.randomUUID();
        	MLog.v("deviceId", ""+deviceId);
            return deviceId;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return "";
    }
}
