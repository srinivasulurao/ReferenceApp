package com.youflik.youflik.services;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.youflik.youflik.R;
import com.youflik.youflik.SplashScreen;
import com.youflik.youflik.broadcastreceivers.YouflikGcmBroadcastReceiver;

public class YouflikGcmIntentService extends IntentService {

	public static String TAG;
	String title ,subTitle;
	NotificationCompat.Builder builder;

	public YouflikGcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
		String jsonstring = extras.getString("jk_noti");
		System.out.println("EXTRAS "+extras.toString());
		System.out.println("jsonstring "+jsonstring.toString());
		try {
			JSONObject jsonObject = new JSONObject(jsonstring);
			title = jsonObject.getString("title");
			subTitle = jsonObject.getString("text");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that GCM
			 * will be extended in the future with new message types, just ignore
			 * any message types you're not interested in, or that you don't
			 * recognize.
			 */
			if (GoogleCloudMessaging.
					MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification("Send error: " + messageType);
			} else if (GoogleCloudMessaging.
					MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server: " +
						messageType);
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.
					MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				// Post notification of received message.
				sendNotification("Received: " + messageType);
				Log.i(TAG, "Received: " + messageType);
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		YouflikGcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.

	private void sendNotification(String msg) {

		int NotificationNewID = 0;
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("New "+title)
		.setContentText(subTitle);
		// Creates an explicit intent for an Activity in your app



		Intent resultIntent = new Intent(this, SplashScreen.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		// Adds the back stack for the Intent (but not the Intent itself)

		//stackBuilder.addParentStack(MainSplashScreen.class);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
						);

		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);
		mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
		//Vibration
		mBuilder.setVibrate(new long[] { 300, 300, 300, 300, 300 });

		//LED
		mBuilder.setLights(Color.RED, 300, 300);
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.

		mNotificationManager.notify(NotificationNewID, mBuilder.build());

		/*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable
				.ic_launcher).setContentTitle("JayaKarnataka").setContentText("Hello World!");

		Intent resultIntent = new Intent(this, MainSplashScreen.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainSplashScreen.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);

		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());*/


		/*// Instantiate a Builder object.
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		// Creates an Intent for the Activity
		Intent notifyIntent =
		        new Intent(new ComponentName(this,MainSplashScreen.class));
		// Sets the Activity to start in a new, empty task
		notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
		// Creates the PendingIntent
		// Puts the PendingIntent into the notification builder
		builder.setContentIntent(notifyIntent);
		// Notifications are issued by sending them to the
		// NotificationManager system service.
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Builds an anonymous Notification object from the builder, and
		// passes it to the NotificationManager
		mNotificationManager.notify(NOTIFICATION_ID, builder.build());


		 */


		//==============================================

		/*
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("Jayakarnataka Notifications")
		.setContentText(goTo);
		// Creates an explicit intent for an Activity in your app\
		Intent resultIntent;
		System.out.println("toGo" + msg);
		if(goTo.contains("notification")){
			resultIntent = new Intent(this, MainSplashScreen.class);

		}else{
		resultIntent = new Intent(this, MainSplashScreen.class);
		}
		resultIntent = new Intent(this, MainSplashScreen.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		Intent i = new Intent(this,MainSplashScreen.class);
		stackBuilder.addNextIntentWithParentStack(i);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);

		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
						);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mBuilder.setAutoCancel(true);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());*/

		//=========================================

		/*mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);


		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("jayakarnataka")
		.setStyle(new NotificationCompat.BigTextStyle()
		.bigText(msg))
		.setContentText(msg);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.ic_launcher, msg, when);


		Intent notificationIntent = new Intent(this, MainSplashScreen.class);

		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
				Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent =
				PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "Jayakarnataka", msg, intent);

		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		//mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, notification);

		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());*/
	}
}