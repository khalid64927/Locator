package com.locator.ui;

import java.util.HashMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.locator.components.ProfileStaticData;
import com.locator.provider.ProfileProvider;
import com.locator.utility.Constants;
import com.locator.utility.Utility;

public class ProfileAddOrUpdate extends Activity implements OnClickListener {
	private Button mBtn_cond, mBtn_sett, mBtn_ok;
	private EditText mEdt_Profile_name;
	private TextView mtvScreenName;
	private static final String TAG = "Profile";
	private int insertedSerialNumber;
	private ProfileStaticData mPsd;

	private boolean mNavigationFlag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_add_or_update);
		mtvScreenName = (TextView) findViewById(R.id.screen_name);
		mBtn_cond = (Button) findViewById(R.id.cond_btn);
		mBtn_ok = (Button) findViewById(R.id.ok);
		mBtn_sett = (Button) findViewById(R.id.settings_btn);
		mEdt_Profile_name = (EditText) findViewById(R.id.editText1);
		mBtn_cond.setOnClickListener(this);
		mBtn_ok.setOnClickListener(this);
		mBtn_sett.setOnClickListener(this);

		Bundle bd = getIntent().getExtras();
		Constants.mIsNewProfile = bd.getBoolean(Constants.IS_NEW_PROFILE);
		mNavigationFlag = bd.getBoolean(Constants.NAVIGATION_FLAG);
		System.out.println(" the paseed value is >>>>>>>>>" + Constants.mIsNewProfile);

		if (Constants.mIsNewProfile) {
			mtvScreenName.setText(R.string.create_profile);
			System.out.println(" is new profile");
		} else {
			mtvScreenName.setText(R.string.update_profile);
			System.out.println(" update profile");
		}

		if (mNavigationFlag) {
			mPsd = new ProfileStaticData();
			System.out.println(" coming from main screen");
		} else {
			mPsd = (ProfileStaticData) bd
					.getSerializable(Constants.PROFILE_DATA_KEY);
			System.out.println(" coming from subscreens");
		}
	}

	private void startLocService() {
		Utility.getUtility().startAlarm(this, Constants.PERIOD);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Utility.getUtility().releaseViewMemory(mBtn_cond);
		Utility.getUtility().releaseViewMemory(mBtn_ok);
		Utility.getUtility().releaseViewMemory(mBtn_sett);
		Utility.getUtility().releaseViewMemory(mEdt_Profile_name);
		Utility.getUtility().releaseViewMemory(mtvScreenName);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Utility.printProfileData(mPsd);
		if (!TextUtils.isEmpty(mPsd.getmPName())) {
			System.out.println(" Pn is not null" + mPsd.getmPName());
			mEdt_Profile_name.setText(mPsd.getmPName());
		}
		
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private boolean storeData() {
		ContentValues values = new ContentValues();

		// System.out.println("not null ==============days========="+mPsd.getmDays());
		if (!TextUtils.isEmpty(mPsd.getmPName())) {
			values.put(ProfileProvider.PROFILE_COLUMN_PROFILE_NAME,
					mPsd.getmPName());
		} else {
			Utility.getUtility().displayToast(this,
					getString(R.string.conf_profile_name));
			return false;
		}
		if (mPsd.getmRingVol() != null) {
			values.put(ProfileProvider.PROFILE_COLUMN_RING_VOLUME,
					mPsd.getmRingVol());
		} else {
			values.put(ProfileProvider.PROFILE_COLUMN_RING_VOLUME,
					mPsd.getmRingVol());
		}
		if (mPsd.getmRingTone() != null) {
			values.put(ProfileProvider.PROFILE_COLUMN_RINGTONE,
					mPsd.getmRingTone());
		} else {
			Utility.getUtility().displayToast(this,
					getString(R.string.conf_rgtn));
			return false;
		}
		if (mPsd.getmNot() != null) {
			values.put(ProfileProvider.PROFILE_COLUMN_NOTIFICATION,
					mPsd.getmNot());
		} else {
			values.put(ProfileProvider.PROFILE_COLUMN_NOTIFICATION,
					mPsd.getmNot());
			System.out.println(" vibrations not set>>>>>>>>>>>>>>");
		}
		if (mPsd.getmVib() != null) {
			values.put(ProfileProvider.PROFILE_COLUMN_VIBRATION, mPsd.getmVib());
		} else {
			values.put(ProfileProvider.PROFILE_COLUMN_VIBRATION, mPsd.getmVib());
			System.out.println(" vibrations not set>>>>>>>>>>>>>>");
		}
		if (mPsd.getmWall() != null) {
			values.put(ProfileProvider.PROFILE_COLUMN_WALLPAPER,
					mPsd.getmWall());
		} else {
			values.put(ProfileProvider.PROFILE_COLUMN_WALLPAPER,
					mPsd.getmWall());
			System.out.println(" wallpaper not set>>>>>>>>>>>>>>");
		}
		if (mPsd.getmLivWall() != null) {
			values.put(ProfileProvider.PROFILE_COLUMN_LIVE_WALLPAPER,
					mPsd.getmLivWall());
		} else {
			values.put(ProfileProvider.PROFILE_COLUMN_LIVE_WALLPAPER,
					mPsd.getmLivWall());
			System.out.println(" live wallpaper not set>>>>>>>>>>>>>>");
		}
		if (mPsd.getmLat() != null) {
			values.put(ProfileProvider.PROFILE_COLUMN_LATITUDE, mPsd.getmLat());
		} else {
			Utility.getUtility().displayToast(this,
					getString(R.string.conf_loc));
			return false;
		}
		if (mPsd.getmLog() != null) {
			values.put(ProfileProvider.PROFILE_COLUMN_LOGITUDE, mPsd.getmLog());
		} else {
			Utility.getUtility().displayToast(this,
					getString(R.string.conf_loc));
			return false;
		}
		if (mPsd.getmTime() != null) {
			values.put(ProfileProvider.PROFILE_COLUMN_TIME, mPsd.getmTime());
		} else {
			values.put(ProfileProvider.PROFILE_COLUMN_TIME, mPsd.getmTime());
			System.out.println(" time not set");
		}
		if (mPsd.getmDays() != null) {
			values.put(ProfileProvider.PROFILE_COLUMN_DAYS, mPsd.getmDays());
		} else {
			values.put(ProfileProvider.PROFILE_COLUMN_DAYS, mPsd.getmDays());
			System.out.println(" days not set>>>>>>>>>>>>>>>>>>>>");
		}
		if (mPsd.getmBrightness() != null) {
			values.put(ProfileProvider.PROFILE_COLUMN_BRIGHTNESS,
					mPsd.getmBrightness());
		} else {
			values.put(ProfileProvider.PROFILE_COLUMN_BRIGHTNESS,
					mPsd.getmDays());
			System.out.println(" brightness not set>>>>>>>>>>>>>>>>>>>>");
		}
		Uri uri = null;
		if (Constants.mIsNewProfile) {
			System.out.println(" insert >>>>>>>>>>>>>>");
			uri = getContentResolver().insert(ProfileProvider.CONTENT_PROFILE_URI,
					values);
			//System.out.println(" the SI :"+uri.getLastPathSegment());
			mPsd.setmPID(uri.getLastPathSegment());
		} else {
			System.out.println(" update>>>>>>>>>>>>>>");
			getContentResolver().update(ProfileProvider.CONTENT_PROFILE_URI,
					values, "_id ==" + mPsd.getmPID(), null);
		}

		return true;

	}
	private void setProximityAlert(boolean isUpdate){
		double latitude = Double.parseDouble(mPsd.getmLat());
		double longitude = Double.parseDouble(mPsd.getmLog());;
		float radius = 5.0f;
		long expiration = 10000;
		Intent intent = new Intent(Constants.PROX_ALERT_INTENT);
		PendingIntent pendingIntent = null;
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if(isUpdate){
			pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(mPsd.getmPID()), intent, 0);
			locationManager.removeProximityAlert(pendingIntent);
			locationManager.addProximityAlert(latitude, longitude, radius, expiration, pendingIntent);	
		}else{
			pendingIntent = PendingIntent.getBroadcast(this, insertedSerialNumber, intent, 0);
			locationManager.addProximityAlert(latitude, longitude, radius, expiration, pendingIntent);	
		}
				
	}

	private void displayRecords() {
		Uri myUri = ProfileProvider.CONTENT_PROFILE_URI;
		Cursor cur = getContentResolver().query(myUri, ProfileProvider.PROFILES_PROJECTION, // Which columns to return
				null, // WHERE clause; which rows to return(all rows)
				null, // WHERE clause selection arguments (none)
				null // Order-by clause (ascending by name)
		);
		HashMap<String, String> hm = new HashMap<String, String>();
		if (cur.moveToFirst()) {
			String id = null;
			String time = null;
			String name = null;
			do {
				// Get the field values
				id = cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_ID));
				time = cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_TIME));
				name = cur
						.getString(cur
								.getColumnIndex(ProfileProvider.PROFILE_COLUMN_PROFILE_NAME));
				hm.put(cur
						.getString(cur
								.getColumnIndex(ProfileProvider.PROFILE_COLUMN_LATITUDE)),
						cur.getString(cur
								.getColumnIndex(ProfileProvider.PROFILE_COLUMN_LOGITUDE)));
				Log.d(TAG, ">>>>>>>>>>>>the time for ID:" + id
						+ "is >>>>>>>>>>>" + time
						+ " and name of the profile is " + name);
				// Toast.makeText(this, id + " " + time,
				// Toast.LENGTH_LONG).show();
			} while (cur.moveToNext());
		}
		cur.close();

	}

	@Override
	public void onClick(View v) {
		mPsd.setmPName(mEdt_Profile_name.getText().toString());
		System.out.println(" the entered profile name is >>>>>>>>>>>"
				+ mEdt_Profile_name.getText().toString());
		if (v == mBtn_cond) {
			Intent intent = new Intent(this, ProfileConditionSettings.class);
			Bundle bd = new Bundle();
			bd.putBoolean(Constants.IS_NEW_PROFILE, Constants.mIsNewProfile);
			bd.putBoolean(Constants.NAVIGATION_FLAG, Constants.sNavigation);
			bd.putSerializable(Constants.PROFILE_DATA_KEY, mPsd);
			intent.putExtras(bd);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		} else if (v == mBtn_sett) {
			Intent intent = new Intent(this, ProfileGeneralSettings.class);
			Bundle bd = new Bundle();
			bd.putBoolean(Constants.IS_NEW_PROFILE, Constants.mIsNewProfile);
			bd.putBoolean(Constants.NAVIGATION_FLAG, Constants.sNavigation);
			bd.putSerializable(Constants.PROFILE_DATA_KEY, mPsd);
			intent.putExtras(bd);
			startActivity(intent);
		} else if (v == mBtn_ok) {
			if (storeData()) {
				 displayRecords();
				// set the alarm only when first new profile is created
				if (isFirstRecord() && Constants.mIsNewProfile) {
					System.out.println(" it is the first record>>>>>>>>");
					startLocService();
				}
			//	setProximityAlert(Constants.mIsNewProfile);
				Intent intent = new Intent(this, ProfileList.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();

			}

		}
	}

	private boolean isFirstRecord() {
		Uri myUri = ProfileProvider.CONTENT_PROFILE_URI;
		Cursor cur = getContentResolver().query(myUri, ProfileProvider.PROFILES_PROJECTION, // Which columns to return
				null, // WHERE clause; which rows to return(all rows)
				null, // WHERE clause selection arguments (none)
				null // Order-by clause (ascending by name)
		);
		if (cur.getCount() == 1) {
			return true;
		}
		cur.close();
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, ProfileList.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
