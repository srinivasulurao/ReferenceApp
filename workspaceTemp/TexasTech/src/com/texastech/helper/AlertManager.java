package com.texastech.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.texastech.activity.EventsActivity;

/**
 * Utility class for helper methods
 */
public class AlertManager {
	/**
	 * Display a simple alert dialog with the given text and title.
	 * @param ctx 		: 	Android context in which the dialog should be displayed
	 * @param title		:	Alert dialog title
	 * @param message	:	Alert dialog message
	 */
	public static void showMessage(Context ctx, String title , String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		//builder.setIcon(R.drawable.sml_icon);
		builder.setMessage(message)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   
		           }
		       });
		
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	
	
	public static void showSuccessMessage(final Activity ctx, String title , String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(title);
		//builder.setIcon(R.drawable.sml_icon);
		builder.setMessage(message)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   ctx.finish();
		           }
		       });
		       
	
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	 
	
	
	
	/**
	 * Function to show exit application alert dialog On pressing OK button will
	 * close the application
	 * @param ctx 		: 	Android context in which the dialog should be displayed
	 * */
	public static void showExitAppAlert(final Activity activity) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
		alertDialog.setTitle("Exit !");
		alertDialog.setMessage("Are you sure you want to close this application ?");
		// On pressing Settings button
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		// Showing Alert Message
		alertDialog.show();
	}
	
	
	
	
	
	public static void showCalanderAlert(final EventsActivity activity) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
		alertDialog.setTitle("Calendar");
		
		alertDialog.setCancelable(false);
		alertDialog.setMessage("\"TxTechNurse\" Would Like to Access Your Calendar");
		// On pressing Settings button
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						EventsActivity.allowCalendar=true;
						activity.setEventsOnCal();
						dialog.cancel();
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Don't Allow",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						EventsActivity.allowCalendar=false;
						activity.setEventsOnCal();
						dialog.cancel();
					}
				});
		// Showing Alert Message
		alertDialog.show();
	}
}
