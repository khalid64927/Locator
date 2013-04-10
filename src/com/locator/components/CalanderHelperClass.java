package com.locator.components;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class CalanderHelperClass {

	private Context mContext;
	private static final String DEBUG_TAG = "CalanderHelperClass";
	
	public CalanderHelperClass(Context mContext) {
	this.mContext = mContext;
	}
	
	public Uri MakeNewCalendarEntry(int calId) {
        ContentValues event = new ContentValues();

        event.put("calendar_id", calId);
        event.put("title", "Today's Event [TEST]");
        event.put("description", "2 Hour Presentation");
        event.put("eventLocation", "Online");

        long startTime = System.currentTimeMillis() + 1000 * 60 * 60;
        long endTime = System.currentTimeMillis() + 1000 * 60 * 60 * 2;

        event.put("dtstart", startTime);
        event.put("dtend", endTime);

        event.put("allDay", 0); // 0 for false, 1 for true
        event.put("eventStatus", 1);
        event.put("visibility", 0);
        event.put("transparency", 0);
        event.put("hasAlarm", 0); // 0 for false, 1 for true

        Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");

        Uri insertedUri = mContext.getContentResolver().insert(eventsUri, event);
        return insertedUri;
    }

	public Uri MakeNewCalendarEntry2(int calId) {
        ContentValues event = new ContentValues();

        event.put("calendar_id", calId);
        event.put("title", "Birthday [TEST]");
        event.put("description", "All Day Event");
        event.put("eventLocation", "Worldwide");

        long startTime = System.currentTimeMillis() + 1000 * 60 * 60 * 24;

        event.put("dtstart", startTime);
        event.put("dtend", startTime);

        event.put("allDay", 1); // 0 for false, 1 for true
        event.put("eventStatus", 1);
        event.put("visibility", 0);
        event.put("transparency", 0);
        event.put("hasAlarm", 0); // 0 for false, 1 for true

        Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");
        Uri insertedUri = mContext.getContentResolver().insert(eventsUri, event);
        return insertedUri;

    }

	public int UpdateCalendarEntry(int entryID) {
        int iNumRowsUpdated = 0;

        ContentValues event = new ContentValues();

        event.put("title", "Changed Event Title");
        event.put("hasAlarm", 1); // 0 for false, 1 for true

        Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);

        iNumRowsUpdated = mContext.getContentResolver().update(eventUri, event, null,
                null);

        Log.i(DEBUG_TAG, "Updated " + iNumRowsUpdated + " calendar entry.");

        return iNumRowsUpdated;
    }

	public int DeleteCalendarEntry(int entryID) {
        int iNumRowsDeleted = 0;

        Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
        iNumRowsDeleted = mContext.getContentResolver().delete(eventUri, null, null);

        Log.i(DEBUG_TAG, "Deleted " + iNumRowsDeleted + " calendar entry.");

        return iNumRowsDeleted;
    }
    
    /*
     * Determines if it's a pre 2.1 or a 2.2 calendar Uri, and returns the Uri
     */
    private String getCalendarUriBase() {
        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = mContext.getContentResolver().query(calendars, null, null, null, null);
        } catch (Exception e) {
            // eat
        }

        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = mContext.getContentResolver().query(calendars, null, null, null, null);
            } catch (Exception e) {
                // eat
            }

            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }

        }

        return calendarUriBase;
    }
}
