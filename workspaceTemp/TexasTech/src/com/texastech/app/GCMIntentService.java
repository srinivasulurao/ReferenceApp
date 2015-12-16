/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.texastech.app;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.texastech.helper.MLog;
import com.texastech.helper.PushRegistration;
import com.texastech.httputil.HttpConst;
/**
 * class, overriding the callback methods which are called by
 * GCMBroadcastReceiver {@link IntentService} responsible for handling GCM
 * messages.
 * @author {Anand Gour}  
 */
public class GCMIntentService extends GCMBaseIntentService implements HttpConst{

	// an integer value for notity id
	private static int nofityCount = 0;

	@SuppressWarnings("hiding")
	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(senderId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message) {
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(R.drawable.app_icon_sml,context.getString(R.string.app_name), when);
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, HomeActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(nofityCount, notification);
		nofityCount++;
	}

	/**
	 * Called when the device tries to register or unregister, but GCM returned
	 * an error.
	 */
	@Override
	protected void onError(Context arg0, String arg1) {

	}

	/**
	 * Called when your server sends a message to GCM, and GCM delivers it to
	 * the device. If the message has a payload, its contents are available as
	 * extras in the intent.
	 */
	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		MLog.d("GCM", "RECIEVED A MESSAGE :"+arg1.getStringExtra("message"));
		Toast.makeText(getApplicationContext(), "RECIEVED A MESSAGE", Toast.LENGTH_LONG).show();
		generateNotification(arg0, arg1.getStringExtra("message"));
	}

	/**
	 * Called after a registration intent is received, passes the registration
	 * ID assigned by GCM to that device/application pair as parameter.
	 */
	@Override
	protected void onRegistered(Context arg0, String arg1) {
		MLog.d("GCM", "Registration id : " + arg1);
		//new PushRegistration(arg0);
		Intent intent = new Intent(getApplicationContext(), PushRegistration.class);
		startService(intent);
	}

	/**
	 * Called after the device has been unregistered from GCM. Typically, you
	 * should send the regid to the server so it unregisters the device.
	 */
	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		
	}
}
