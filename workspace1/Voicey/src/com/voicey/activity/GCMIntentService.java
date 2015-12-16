package com.voicey.activity;

import static  com.voicey.activity.CommonUtilities.SENDER_ID;
import static  com.voicey.activity.CommonUtilities.displayMessage;

import java.net.URLDecoder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
       // Log.d("NAME", MainActivity.name);
        ServerUtilities.register(context, registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
    	try {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");
        String convertedMessage=URLDecoder.decode(message,"UTF-8");
        String type = intent.getExtras().getString("Type");
        String audioName=null;
        if(type.equals("QuickShareAuto")){
        	
       audioName = intent.getExtras().getString("Audio");
        }
        displayMessage(context, message);
        // notifies user
        generateNotification(context, convertedMessage,type,audioName);
    	} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message,null,null);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message,String type,String audioName) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
       
        
        Intent notificationIntent = new Intent(context, HomeActivity.class);
        
        notificationIntent.removeExtra("Navigation");
        if(type.equals("Request")){
        	notificationIntent.putExtra("Navigation", "Request");
        	
        }else if(type.equals("Share")){
        	notificationIntent.putExtra("Navigation", "inbox");
        	
        }else if(type.equals("Accept")){
        	notificationIntent.putExtra("Navigation", "accept");
        	
        }else if(type.equals("QuickShare")){
        	notificationIntent.putExtra("Navigation", "inbox");
        	
        }
        else if(type.equals("QuickShareAuto")){
        	notificationIntent.putExtra("Navigation", "inbox");
        	
        }
        // set intent so it does not start a new activity
      /* notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);*/
        PendingIntent intent =PendingIntent.getActivity(context, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification); 
        SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
        String muteControl = sharedPreferences.getString("muteControl",
				null);
		if (muteControl == null || muteControl.equals("0")) {
        if(audioName!=null){
        try {
        MediaPlayer mediaPlayer;
    	mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource("http://voicey.me/web-services/audio/"+audioName);
        mediaPlayer.prepare();
		mediaPlayer.start();
		
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
        }
        
       /* 
        Intent notificationIntent = new Intent(getApplicationContext(), viewmessage.class);
        notificationIntent.putExtra("NotificationMessage", notificationMessage);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),notificationIndex,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(getApplicationContext(), notificationTitle, notificationMessage, pendingNotificationIntent);*/

    }

}
