package com.texastech.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import com.texastech.bean.CalenderEvent;

public class CalendarUtility {

	public static List<CalenderEvent> readCalendarEvent(Context context) {
		List<CalenderEvent> calenderEvents = new ArrayList<CalenderEvent>();
		try {
			Cursor cursor = context.getContentResolver().query(
					Uri.parse("content://com.android.calendar/events"),
					new String[] { "_id", "title", "description", "dtstart",
							"dtend", "eventLocation" }, null, null, null);
			cursor.moveToFirst();
			// fetching calendars name
			String CNames[] = new String[cursor.getCount()];
			for (int i = 0; i < CNames.length; i++) {
				CalenderEvent event = new CalenderEvent();
				event.calenderId = cursor.getInt(0);
				event.nameOfEvent = cursor.getString(1);
				event.description = cursor.getString(2);
				event.startDate = cursor.getString(3);
				event.endDate = cursor.getString(4);
				calenderEvents.add(event);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return calenderEvents;
	}

	// -----------------DeleteEvent----------//
	private int DeleteCalendarEntry(Activity context, int entryID) {
		int iNumRowsDeleted = 0;
		Uri eventsUri = Uri.parse(getCalendarUriBase(context) + "events");
		Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
		iNumRowsDeleted = context.getContentResolver().delete(eventUri, null,
				null);
		Log.i("DEBUG_TAG", "Deleted " + iNumRowsDeleted + " calendar entry.");
		return iNumRowsDeleted;
	}

	// -----------------UpdateEvent----------//
	private int UpdateCalendarEntry(Activity context, int entryID) {
		int iNumRowsUpdated = 0;

		ContentValues event = new ContentValues();

		event.put("title", "Changed Event Title");
		event.put("hasAlarm", 1); // 0 for false, 1 for true

		Uri eventsUri = Uri.parse(getCalendarUriBase(context) + "events");
		Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);

		iNumRowsUpdated = context.getContentResolver().update(eventUri, event,
				null, null);

		Log.i("DEBUG_TAG", "Updated " + iNumRowsUpdated + " calendar entry.");

		return iNumRowsUpdated;
	}

	
	
	
	//--------------------------------------------------------//
	private String getCalendarUriBase(Activity context) {
		String calendarUriBase = null;
		Uri calendars = Uri.parse("content://calendar/calendars");
		Cursor managedCursor = null;
		try {
			managedCursor = context.managedQuery(calendars, null, null, null,
					null);
		} catch (Exception e) {
			// eat
		}
		if (managedCursor != null) {
			calendarUriBase = "content://calendar/";
		} else {
			calendars = Uri.parse("content://com.android.calendar/calendars");
			try {
				managedCursor = context.managedQuery(calendars, null, null,
						null, null);
			} catch (Exception e) {
				// eat
			}
			if (managedCursor != null) {
				calendarUriBase = "content://com.android.calendar/";
			}
		}
		return calendarUriBase;
	}

	
	
	@SuppressLint("NewApi") 
	public static void addEvent(Context ctx, String title, Calendar start) {
		TimeZone timeZone = TimeZone.getDefault();
		/*Uri eventsUri;
		
		if (android.os.Build.VERSION.SDK_INT <= 7) {
			eventsUri = Uri.parse("content://calendar/events");
			remainderUri = Uri.parse("content://calendar/reminders");
			cursor = MainActivity.this.getContentResolver().query(
					Uri.parse("content://calendar/calendars"),
					new String[] { "_id", "displayName" }, null, null, null);
		}
 
		else if (android.os.Build.VERSION.SDK_INT <= 14) {
			eventsUri = Uri.parse("content://com.android.calendar/events");
			remainderUri = Uri
					.parse("content://com.android.calendar/reminders");
			cursor = MainActivity.this.getContentResolver().query(
					Uri.parse("content://com.android.calendar/calendars"),
					new String[] { "_id", "displayName" }, null, null, null);
		}
 
		else {
			eventsUri = Uri.parse("content://com.android.calendar/events");
			remainderUri = Uri
					.parse("content://com.android.calendar/reminders");
			cursor = MainActivity.this.getContentResolver().query(
					Uri.parse("content://com.android.calendar/calendars"),
					new String[] { "_id", "calendar_displayName" }, null, null,
					null);
		}*/
		
		
        ContentResolver contentResolver = ctx.getContentResolver();
        ContentValues calEvent = new ContentValues();
        calEvent.put(CalendarContract.Events.CALENDAR_ID, 1); // XXX pick)
        calEvent.put(CalendarContract.Events.TITLE, title);
        calEvent.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());
        
        calEvent.put(CalendarContract.Events.DTEND, start.getTimeInMillis());
        calEvent.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        
        //Uri uri = contentResolver.insert(eventsUri, calEvent);
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, calEvent);
        
        // The returned Uri contains the content-retriever URI for 
        // the newly-inserted event, including its id
        int id = Integer.parseInt(uri.getLastPathSegment());               
        //Toast.makeText(ctx, "Created Calendar Event " + id,Toast.LENGTH_SHORT).show();
    }
	
	
	//-------------------------------------------------//
	public static String getDate(long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"dd/MM/yyyy hh:mm:ss a");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
}

// delecte event
// DeleteCalendarEntry(list.get(0).calenderId);

