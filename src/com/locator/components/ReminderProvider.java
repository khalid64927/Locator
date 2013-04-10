package com.locator.components;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ReminderProvider extends ContentProvider {

	private static final String TAG = "ReminderProvider";

	private static final String DATABASE_NAME = "DataProvider.db";

	private static final int DATABASE_VERSION = 1;

	private static final String AUTHORITY = "com.locator.provider.ReminderProvider";

	private static final String PARAMETER_NOTIFY = "notify";

	private SQLiteOpenHelper mOpenHelper;

	private static final String DATABASE_TABLENAME = "all_reminder_data";

	public static final Uri CONTENT_REMINDER_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + DATABASE_TABLENAME);

	public static final String[] REMINDERS_PROJECTION = new String[] {
			ReminderProvider.REMINDER_COLUMN_ID,
			ReminderProvider.REMINDER_COLUMN_NAME,
			ReminderProvider.REMINDER_COLUMN_START_DATE,
			ReminderProvider.REMINDER_COLUMN_START_TIME,
			ReminderProvider.REMINDER_COLUMN_END_DATE,
			ReminderProvider.REMINDER_COLUMN_END_TIME,
			ReminderProvider.REMINDER_COLUMN_IS_ALL_DAY,
			ReminderProvider.REMINDER_COLUMN_TIMEZONE,
			ReminderProvider.REMINDER_COLUMN_WHERE,
			ReminderProvider.REMINDER_COLUMN_DESCRIPTION,
			ReminderProvider.REMINDER_COLUMN_REMINDERS,
			ReminderProvider.REMINDER_COLUMN_REPETITION};

	// Column names of all_REMINDERs_data table
	public static final String REMINDER_COLUMN_ID = "_id";
	public static final String REMINDER_COLUMN_NAME = "reminder_name";
	public static final String REMINDER_COLUMN_START_DATE = "start_date";
	public static final String REMINDER_COLUMN_START_TIME = "start_time";
	public static final String REMINDER_COLUMN_END_DATE = "end_date";
	public static final String REMINDER_COLUMN_END_TIME = "end_time";
	public static final String REMINDER_COLUMN_IS_ALL_DAY = "all_day";
	public static final String REMINDER_COLUMN_TIMEZONE = "timezone";
	public static final String REMINDER_COLUMN_WHERE = "location";
	public static final String REMINDER_COLUMN_DESCRIPTION = "description";
	public static final String REMINDER_COLUMN_REMINDERS = "reminders";
	public static final String REMINDER_COLUMN_REPETITION = "repetition";
	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = db.delete(args.mTable, args.mWhere, args.mArgs);
		if (count > 0)
			sendNotify(uri);

		return count;

	}

	private void sendNotify(Uri uri) {
		String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
		if (notify == null || "true".equals(notify)) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
	}

	@Override
	public String getType(Uri uri) {
		SqlArguments args = new SqlArguments(uri, null, null);
		if (TextUtils.isEmpty(args.mWhere)) {
			return "vnd.android.cursor.dir/" + args.mTable;
		} else {
			return "vnd.android.cursor.item/" + args.mTable;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SqlArguments args = new SqlArguments(uri);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final long rowId = db.insert(args.mTable, null, values);
		if (rowId <= 0)
			return null;

		uri = ContentUris.withAppendedId(uri, rowId);
		sendNotify(uri);

		return uri;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(args.mTable);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Cursor result = qb.query(db, projection, args.mWhere, args.mArgs, null,
				null, sortOrder);
		result.setNotificationUri(getContext().getContentResolver(), uri);

		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = db.update(args.mTable, values, args.mWhere, args.mArgs);
		if (count > 0)
			sendNotify(uri);

		return count;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_CREATE = "create table "
				+ DATABASE_TABLENAME
				+ " (_id integer primary key autoincrement, "
				+ "reminder_name text, start_date text, start_time text, "
				+ "end_date text, end_time text,all_day text, "
				+ "timezone text,location text,description text,reminders text, "
				+ "repetition text);";

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
		}
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// drop all tables and re create the database
			if (newVersion != DATABASE_VERSION) {
				Log.d(TAG, "Destroying all old data.");
				db.execSQL("DROP TABLE IF EXISTS all_reminders_data");
				onCreate(db);
			}

		}

	}

	private static class SqlArguments {
		public final String mTable;
		public final String mWhere;
		public final String[] mArgs;

		SqlArguments(Uri url, String where, String[] args) {
			if (url.getPathSegments().size() == 1) {
				this.mTable = url.getPathSegments().get(0);
				this.mWhere = where;
				this.mArgs = args;
			} else if (url.getPathSegments().size() != 2) {
				throw new IllegalArgumentException("Invalid URI: " + url);
			} else if (!TextUtils.isEmpty(where)) {
				throw new UnsupportedOperationException(
						"WHERE clause not supported: " + url);
			} else {
				this.mTable = url.getPathSegments().get(0);
				this.mWhere = "_id=" + ContentUris.parseId(url);
				this.mArgs = null;
			}
		}

		SqlArguments(Uri url) {
			if (url.getPathSegments().size() == 1) {
				mTable = url.getPathSegments().get(0);
				mWhere = null;
				mArgs = null;
			} else {
				throw new IllegalArgumentException("Invalid URI: " + url);
			}
		}
	}
}
