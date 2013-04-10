package com.locator.provider;

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

public class ProfileProvider extends ContentProvider {

	private static final String TAG = "ProfileProvider";

	private static final String DATABASE_NAME = "DataProvider.db";

	private static final int DATABASE_VERSION = 1;

	private static final String AUTHORITY = "com.locator.provider.ProfileProvider";

	private static final String PARAMETER_NOTIFY = "notify";

	private SQLiteOpenHelper mOpenHelper;

	private static final String DATABASE_TABLENAME = "all_profiles_data";

	public static final Uri CONTENT_PROFILE_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + DATABASE_TABLENAME);

	public static final String[] PROFILES_PROJECTION = new String[] {
			ProfileProvider.PROFILE_COLUMN_ID,
			ProfileProvider.PROFILE_COLUMN_PROFILE_NAME,
			ProfileProvider.PROFILE_COLUMN_RING_VOLUME,
			ProfileProvider.PROFILE_COLUMN_RINGTONE,
			ProfileProvider.PROFILE_COLUMN_NOTIFICATION,
			ProfileProvider.PROFILE_COLUMN_VIBRATION,
			ProfileProvider.PROFILE_COLUMN_WALLPAPER,
			ProfileProvider.PROFILE_COLUMN_LIVE_WALLPAPER,
			ProfileProvider.PROFILE_COLUMN_LATITUDE,
			ProfileProvider.PROFILE_COLUMN_LOGITUDE,
			ProfileProvider.PROFILE_COLUMN_TIME,
			ProfileProvider.PROFILE_COLUMN_DAYS, 
			ProfileProvider.PROFILE_COLUMN_BRIGHTNESS};

	// Column names of all_profiles_data table
	public static final String PROFILE_COLUMN_ID = "_id";
	public static final String PROFILE_COLUMN_PROFILE_NAME = "profile_name";
	public static final String PROFILE_COLUMN_RING_VOLUME = "ringvol";
	public static final String PROFILE_COLUMN_RINGTONE = "ringtone";
	public static final String PROFILE_COLUMN_NOTIFICATION = "notification";
	public static final String PROFILE_COLUMN_VIBRATION = "vibration";
	public static final String PROFILE_COLUMN_WALLPAPER = "wallpaper";
	public static final String PROFILE_COLUMN_LIVE_WALLPAPER = "livewall";
	public static final String PROFILE_COLUMN_LATITUDE = "latitude";
	public static final String PROFILE_COLUMN_LOGITUDE = "logitude";
	public static final String PROFILE_COLUMN_TIME = "time";
	public static final String PROFILE_COLUMN_DAYS = "days";
	public static final String PROFILE_COLUMN_BRIGHTNESS = "brightness";
	

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
				+ "profile_name text, ringvol text, ringtone text, "
				+ "notification text, vibration text,wallpaper text, "
				+ "livewall text,latitude text,logitude text,time text, "
				+ "days text, brightness text);";

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
			/*
			 * db.execSQL("CREATE TABLE all_profiles_data (" +
			 * "_id INTEGER PRIMARY KEY," + "ringvol TEXT," + "ringtone TEXT," +
			 * "notification TEXT," + "vibration TEXT," + "wallpaper TEXT," +
			 * "livewall TEXT," + "latituide TEXT," + "logitude TEXT," +
			 * "time TEXT," + "days TEXT," + ");");
			 */

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// drop all tables and re create the database
			if (newVersion != DATABASE_VERSION) {
				Log.d(TAG, "Destroying all old data.");
				db.execSQL("DROP TABLE IF EXISTS all_profiles_data");
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
