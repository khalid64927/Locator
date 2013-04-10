package com.locator.ui;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.locator.components.ProfileStaticData;
import com.locator.provider.ProfileProvider;
import com.locator.utility.Constants;
import com.locator.utility.Utility;

public class ProfileList extends Activity implements OnItemLongClickListener {
	private ListView mProfilesList;
	private TextView mHelperText;
	private static final String TAG = "ProfilesActivity";
	private ProfileStaticData mPsd;
	private ArrayList<String> mProfile_list;
	private ArrayList<ProfileStaticData> mPsdList;
	public ArrayAdapter<String> aa;
	private boolean mIsListEmpty;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_list);
		mProfilesList = (ListView) findViewById(R.id.recipients_ListView);
		mHelperText = (TextView) findViewById(R.id.helperText);
		loadList();
		mProfilesList.setOnItemLongClickListener(this);
		if (mIsListEmpty) {
			mHelperText.setText(getString(R.string.no_profles));
			mHelperText.setVisibility(View.VISIBLE);
		} else {
			mHelperText.setVisibility(View.INVISIBLE);
		}			
	}

	public void loadList() {
		System.out.println(" load listloadList");
		aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, displayProfilesFromDB());
		mProfilesList.setAdapter(aa);
/*		
		String uriString = "content://com.android.calendar/calendars";
		  Log.i("INFO", "Reading content from " + uriString);
		  readContent(uriString);
		  uriString = "content://com.android.calendar/events";
		  Log.i("INFO", "Reading content from " + uriString);
		  readContent(uriString);*/

	}
	
/*	private void readContent(String uriString) {

		  Uri uri = Uri.parse(uriString);
		  Cursor cursor = this.getContentResolver().query(uri, null, null,
		    null, null);
		  if (cursor != null && cursor.getCount() > 0) {
		   cursor.moveToFirst();
		   String columnNames[] = cursor.getColumnNames();
		   String value = "";
		   String colNamesString = "";
		   do {
		    value = "";

		    for (String colName : columnNames) {
		     value += colName + " = ";
		     value += cursor.getString(cursor.getColumnIndex(colName))
		       + " ||";
		    }

		    Log.e("INFO : ", value);
		   } while (cursor.moveToNext());

		  }

		 }*/


	private ArrayList<String> displayProfilesFromDB() {
		mPsdList = new ArrayList<ProfileStaticData>();
		mProfile_list = new ArrayList<String>();

		Uri myUri = ProfileProvider.CONTENT_PROFILE_URI;
		Cursor cur = getContentResolver().query(myUri, ProfileProvider.PROFILES_PROJECTION, // Which
																				// columns
																				// to
																				// return
				null, // WHERE clause; which rows to return(all rows)
				null, // WHERE clause selection arguments (none)
				null // Order-by clause (ascending by name)
		);

		if (cur.getCount() < 1) {
			Log.i(TAG, "No profiles");
			mIsListEmpty = true;
			return mProfile_list;
		}
		mIsListEmpty = false;
		Log.i(TAG, "No of Profile" + cur.getCount());
		if (cur.moveToFirst()) {
			String id = null;
			String time = null;
			do {
				mPsd = new ProfileStaticData();
				// Get the field values
				mPsd.setmPID(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_ID)));
				mPsd.setmPName(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_PROFILE_NAME)));
				mPsd.setmRingVol(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_RING_VOLUME)));
				mPsd.setmRingTone(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_RINGTONE)));
				mPsd.setmNot(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_NOTIFICATION)));
				mPsd.setmVib(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_VIBRATION)));
				mPsd.setmWall(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_WALLPAPER)));
				mPsd.setmLivWall(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_LIVE_WALLPAPER)));
				mPsd.setmLat(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_LATITUDE)));
				mPsd.setmLog(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_LOGITUDE)));
				mPsd.setmTime(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_TIME)));
				mPsd.setmDays(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_DAYS)));
				mPsdList.add(mPsd);
				mProfile_list.add(mPsd.getmPName());
				id = cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_ID));
				time = cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_TIME));
				Log.d(TAG, "ID:" + id + " Time:" + time);
			} while (cur.moveToNext());
		}
		cur.close();
		// testFunction(mPsdList);

		return mProfile_list;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int itemNo,
			long arg3) {

		Log.d(TAG, "Selected profile" + itemNo);
		mPsd = mPsdList.get(itemNo);
		Constants.isDeleted = Utility.getUtility().showDialog(this,
				mPsdList.get(itemNo));
		System.out.println("the returned value is >>>>>>>>"
				+ Constants.isDeleted);

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflator = getMenuInflater();
		inflator.inflate(R.menu.profile_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_profile:
			Intent intent = new Intent(this, ProfileAddOrUpdate.class);
			intent.putExtra(Constants.IS_NEW_PROFILE, true);
			intent.putExtra(Constants.NAVIGATION_FLAG, true);
			intent.putExtra(Constants.PROFILE_DATA_KEY, mPsd);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			Log.d(TAG, " New_profile called");
			break;
		case R.id.reminder:
			Log.d(TAG, " Reminder called");
			Intent remintent = new Intent(this, Reminder.class);
			startActivity(remintent);
			break;
		case R.id.log:
			Log.d(TAG, " Log called");
			if (isTextFileReadable()) {
				Intent logintent = new Intent(this, LogActivity.class);
				logintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(logintent);
			}

			break;
		default:
			Log.d(TAG, " default case");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean isTextFileReadable() {
		boolean isReadable = false;

		File log2 = new File(Environment.getExternalStorageDirectory(),
				"/Locator/LocationLog.txt");
		try {
			if (!log2.exists()) {
				isReadable = false;
				return isReadable;
			}
		} catch (Exception e) {
			System.out.println(" exception in isTextFileReadable");
			isReadable = false;
			return isReadable;
		}

		if (!Utility.getUtility().checkSDCard(this)) {
			isReadable = false;
			return isReadable;
		}

		isReadable = true;
		return isReadable;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.ACTION_DOWN) {
			finish();
		}

		return super.onKeyDown(keyCode, event);

	}
}