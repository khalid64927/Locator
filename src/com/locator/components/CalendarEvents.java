package com.locator.components;

import com.locator.utility.Constants;
import com.locator.utility.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Events;

public class CalendarEvents {
	private Context mCtx;
	private static final String TAG = "CalendarEvents";

	public CalendarEvents(Context ctx) {
		this.mCtx = ctx;

	}

	public void insertEvent(int cal_ID, String eventTitle,
			String eventDescription, String eventLocation,
			String eventTimezone, long eventStart, long eventEnd,
			boolean isAllDay, int eventStatus, int transparency,
			int hasAlarm, int method, int minute, int apiLevel) {
		
		
			
		System.out.println("what " + eventTitle + " des " + eventDescription + "where"
				+ eventLocation + "start date" + eventStart + " end date" + eventEnd);
		ContentValues event = new ContentValues();

		event.put(Events.DTSTART, eventStart);
		event.put(Events.DTEND, eventEnd);
		event.put(Events.CALENDAR_ID, cal_ID);
		event.put(Events.TITLE, eventTitle);
		event.put(Events.DESCRIPTION, eventDescription);
		event.put(Events.EVENT_LOCATION, eventLocation);
		event.put(Events.EVENT_TIMEZONE, eventTimezone);

	/*	long startTime = System.currentTimeMillis() + 1000 * 60 * 60;
		long endTime = System.currentTimeMillis() + 1000 * 60 * 60 * 2;*/

		System.out.println("currentTimeMillis" + System.currentTimeMillis());
		System.out.println(" added " + eventStart);

		

		if(isAllDay){ 
			event.put(Events.ALL_DAY, 1); 
			}else{
			event.put(Events.ALL_DAY, 0);
		}
	//	event.put(Events.STATUS, 1);
		//event.put("visibility", 0);
	//	event.put("transparency", 0);
	//	event.put("hasAlarm", 1); // 0 for false, 1 for true
		System.out.println(" getCalendarUriBase is "+getCalendarUriBase(apiLevel));

		String uriString = getCalendarUriBase(apiLevel);
		System.out.println(" the api level "+apiLevel);
		Uri eventUri = null;
		String eventUriString = uriString;
		if(apiLevel < 14){
			eventUriString = eventUriString+"events";
		}
		eventUri = Uri.parse(eventUriString);
		Uri insertedUri = mCtx.getContentResolver().insert(eventUri, event);
		System.out.println(" inserted event uri "+insertedUri);

		// reminder insert
		Uri reminderUri = null;
		String reminderUriString = uriString;
		if(apiLevel < 14){
			reminderUriString = reminderUriString+"reminders";
		}
		reminderUri = Uri.parse(uriString);
		ContentValues values = new ContentValues();
		values.put("event_id", Long.parseLong(insertedUri.getLastPathSegment()));
		values.put("method", method);
		values.put("minutes", minute);
		mCtx.getContentResolver().insert(reminderUri, values);
		

	}
	
	private String getCalendarUriBase(int apiLevel) {
		String calendarUriBase = null;
		Uri calendars = Uri.parse("content://calendar/calendars");
		Cursor cursor = null;
		if(apiLevel >= 14){
			calendarUriBase = ""+Events.CONTENT_URI;
			return calendarUriBase;
		}
		
		try {
			cursor = mCtx.getContentResolver().query(calendars, null, null, null, null);
		} catch (Exception e) {
			Utility.getUtility().printLog(Constants.ERROR, TAG, "exception with "+calendars+" uri");
		}

		if (cursor != null) {
			calendarUriBase = "content://calendar/";
		} else {
			calendars = Uri.parse("content://com.android.calendar/calendars");
			try {
				cursor = mCtx.getContentResolver().query(calendars, null, null, null, null);
			} catch (Exception e) {
				Utility.getUtility().printLog(Constants.ERROR, TAG, "exception with "+calendars+" uri");
			}

			if (cursor != null) {
				calendarUriBase = "content://com.android.calendar/";
			}
			cursor.close();
		}
		System.out.println(" the documented uri is "+Events.CONTENT_URI);

		System.out.println(" getCalendarUriBase is "+calendarUriBase);
		return calendarUriBase;
	}

}
