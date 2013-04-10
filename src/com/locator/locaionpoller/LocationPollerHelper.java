package com.locator.locaionpoller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.brightness.BrightnessActivity;
import com.locator.components.ProfileStaticData;
import com.locator.provider.ProfileProvider;
import com.locator.utility.Constants;
import com.locator.utility.Utility;

public class LocationPollerHelper {

	private Context mContext;
	private ProfileStaticData mPsd;
	private static final String TAG = "LocationPollerHelper";
	private Uri mAudioUri;
	private Uri mPhotoUri;
	private int mColumnIndex;
	private String mFilePath;
	private AudioManager mAM;
	private Bitmap mBmap;

	public LocationPollerHelper(Context ctx) {
		this.mContext = ctx;
	}

	public void checkFormStoredLocation(Location loc) {
		System.out.println(" inside  checkFormStoredLocation");
		ArrayList<Double> al = new ArrayList<Double>();
		al.add(loc.getLatitude());
		al.add(loc.getLongitude());
		Uri myUri = ProfileProvider.CONTENT_PROFILE_URI;
		Cursor cur = mContext.getContentResolver().query(myUri,
				ProfileProvider.PROFILES_PROJECTION, // Which
				// columns
				// to return
				null, // WHERE clause; which rows to return(all rows)
				null, // WHERE clause selection arguments (none)
				null // Order-by clause (ascending by name)
				);
		mPsd = new ProfileStaticData();
		if (cur.moveToFirst()) {
			String latitude = null;
			String logitude = null;
			float[] results = new float[3];
			do {
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
				mPsd.setmLat(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_LATITUDE)));
				mPsd.setmLog(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_LOGITUDE)));
				mPsd.setmLivWall(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_LIVE_WALLPAPER)));
				mPsd.setmWall(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_WALLPAPER)));
				mPsd.setmDays(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_DAYS)));
				mPsd.setmTime(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_TIME)));
				mPsd.setmBrightness(cur.getString(cur
						.getColumnIndex(ProfileProvider.PROFILE_COLUMN_BRIGHTNESS)));

				latitude = cur
						.getString(cur
								.getColumnIndex(ProfileProvider.PROFILE_COLUMN_LATITUDE));
				logitude = cur
						.getString(cur
								.getColumnIndex(ProfileProvider.PROFILE_COLUMN_LOGITUDE));
				Utility.getUtility().printLog(Constants.DATA, TAG, "Cur_Lat :"+loc.getLatitude()+" Cur_Log"+loc.getLongitude()+"\n"
						+"Sto_Lat :"+latitude+"Sto_Long :"+logitude);
				try {
					android.location.Location.distanceBetween(
							(al.get(0) / 1E6), (al.get(1) / 1E6),
							(Double.valueOf(latitude) / 1E6),
							(Double.valueOf(logitude) / 1E6), results);
				} catch (IllegalArgumentException iae) {
					Log.e(TAG, "distance could not be found");
					return;
				}
				double distance = (results[0]*1E6);
				System.out.println(" inside checkFormStoredLocation result:"
						+ distance);
				if (distance <= Constants.DISTANCE_RANGE) {

					System.out
							.println(" checkFormStoredLocation distance is <= range ");
					if (!isSetForToday(mPsd)) {
						// it is not set for today
						System.out
								.println(" checkFormStoredLocation is not set for today");
						return;
					}
					System.out
							.println(" checkFormStoredLocation is  set for today");
					// check if it profile is already set
					if ((Constants.sCurrentSetProfileID != null)) {
						if (!Constants.sCurrentSetProfileID.equals(mPsd
								.getmPID())) {
							System.out
									.println(" checkFormStoredLocation diferent profile was set before");
							System.out.println(" set Profile");
							setProfile(mPsd);
						}
					} else {
						System.out
								.println(" checkFormStoredLocation is  not already set ");
						// set profile if not set before
						setProfile(mPsd);
					}

				} else {
					System.out
							.println(" checkFormStoredLocation distance is not <= range ");
				}

			} while (cur.moveToNext());
		}
		cur.close();
	}

	private void setProfile(ProfileStaticData mPsd) {
		Log.i(TAG, "setProfile");
		
		Constants.sCurrentSetProfileID = mPsd.getmPID();

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String str = sdf.format(new Date());
		System.out.println("Current time:" + str);
		System.out.println("profile time:" + mPsd.getmTime());

		// set ringtone
		mAudioUri = Uri.parse(mPsd.getmRingTone());
		String[] mFilePathColumn = { MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.TITLE };
		Cursor cursor = mContext.getContentResolver().query(mAudioUri,
				mFilePathColumn, null, null, null);
		cursor.moveToFirst();
		mColumnIndex = cursor.getColumnIndex(mFilePathColumn[0]);
		Constants.isDeleted = false;
		try {
			mFilePath = cursor.getString(mColumnIndex);
		} catch (CursorIndexOutOfBoundsException ciobe) {
			System.out.println(" audio deleted------------");
			Constants.isDeleted = true;
		}
		if (!Constants.isDeleted) {
			RingtoneManager.setActualDefaultRingtoneUri(mContext,
					RingtoneManager.TYPE_RINGTONE, mAudioUri);
		}

		// set notification
		if (mPsd.getmNot() != null) {
			mAM = (AudioManager) mContext
					.getSystemService(Context.AUDIO_SERVICE);
			mAM.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
					AudioManager.VIBRATE_SETTING_ON);
		}
		// set vibrations
		if (mPsd.getmVib() != null) {

			mAM = (AudioManager) mContext
					.getSystemService(Context.AUDIO_SERVICE);
			mAM.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
					AudioManager.VIBRATE_SETTING_ON);
		}
		// set Wallpaper
		if (mPsd.getmWall() != null) {
			mPhotoUri = Uri.parse(mPsd.getmWall());
			String[] mFilePathColumn_images = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media.TITLE };
			Cursor cur = mContext.getContentResolver().query(mPhotoUri,
					mFilePathColumn_images, null, null, null);

			cur.moveToFirst();
			mColumnIndex = cur.getColumnIndex(mFilePathColumn[0]);
			Constants.isDeleted = false;
			try {
				mFilePath = cur.getString(mColumnIndex);
			} catch (CursorIndexOutOfBoundsException ciobe) {
				System.out.println(" image deleted------------");
				Constants.isDeleted = true;
			}
			if (!Constants.isDeleted) {
				
				mBmap = BitmapFactory.decodeFile(mFilePath);
				WallpaperManager wM = WallpaperManager.getInstance(mContext);
				try {
					wM.setBitmap(mBmap);

				} catch (IOException e) {
					e.printStackTrace();
					System.out.println(" error in setting wall paper");
				}
			}

		}

		if (mPsd.getmRingVol() != null) {
			int mRingerVolume = Integer.parseInt(mPsd.getmRingVol());
			if (mRingerVolume != 0) {
				System.out.println("the current seek bar level is :"
						+ mRingerVolume);
				mAM = (AudioManager) mContext
						.getSystemService(Context.AUDIO_SERVICE);
				mAM.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				// no notification is required hence 0 is set in place of flags
				mAM.setStreamVolume(AudioManager.STREAM_RING, mRingerVolume, 0);
			} else {
				mAM.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
		} else {
			System.out.println(" no preference for ringer volume level set");
		}

		// set brightness level
		if (mPsd.getmBrightness() != null) {
			System.out.println(" the brightness level is >>>>>>>>>>>>>>>>"
					+ mPsd.getmBrightness());
			setBrightness(Integer.parseInt(mPsd.getmBrightness()));
		} else {
			System.out.println(" the brightness is null>>>>>>>>>>>>>>>>");
		}

	}

	private void setBrightness(int brightness) {
		Log.i(TAG, "setBrightness");
		Intent intent = new Intent(mContext, BrightnessActivity.class);
		intent.putExtra(Constants.BRIGHTNESS_LEVEL, brightness);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this is important
		mContext.startActivity(intent);
	}

	public boolean isSetForToday(ProfileStaticData mPsd) {
		boolean isSet = false;
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK);
		System.out.println(" day of the week" + day);
		int setDays = Integer.parseInt(mPsd.getmDays());
		System.out.println(" stored day is " + setDays);
		switch (day) {

		case 1:
			int sunday = setDays & Constants.s_SUNDAY;
			if (sunday == Constants.s_SUNDAY) {
				isSet = true;
			}
			break;
		case 2:
			int monday = setDays & Constants.s_MONDAY;
			if (monday == Constants.s_MONDAY) {
				isSet = true;
			}
			break;
		case 3:
			int tuesday = setDays & Constants.s_TUESDAY;
			if (tuesday == Constants.s_TUESDAY) {
				isSet = true;
			}
			break;
		case 4:
			int wednesday = setDays & Constants.s_WEDNESDAY;
			if (wednesday == Constants.s_WEDNESDAY) {
				isSet = true;
			}
			break;
		case 5:
			int thursday = setDays & Constants.s_THURSDAY;
			if (thursday == Constants.s_THURSDAY) {
				isSet = true;
			}

			break;

		case 6:
			int friday = setDays & Constants.s_FRIDAY;
			if (friday == Constants.s_FRIDAY) {
				isSet = true;
			}
			break;
		case 7:
			int saturday = setDays & Constants.s_SATURDAY;
			if (saturday == Constants.s_SATURDAY) {
				isSet = true;
			}
			break;

		default:// day could not found
			isSet = false;
			break;

		}

		if (!isSet) {
			System.out.println("day not set for today>>>>>>>>>>>>");
			// if not set for today then return false avoiding time checking
			return isSet;
		}
		System.out.println("day set for today>>>>>>>>>>>>");

		// it is set for today check if whether set for current time
		String time = mPsd.getmTime();
		String currentTime = "" + cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
		Date date = null;
		Date date2 = null;
		try {
			if(time == null){
				System.out.println(" is null>>>>>>>>>>>>>>>>>>>>.");
			}else{
				System.out.println(" is not null");
			}
			date = sdf.parse(time);
			date2 = sdf2.parse(currentTime);
		} catch (ParseException e) {
			isSet = false;
			Log.e(TAG, "Exception is parsing date");
			e.printStackTrace();
			return isSet;
		}
		// Outputs -1 as date1 is before date2
		// System.out.println(date1.compareTo(date2));

		// Outputs 1 as date1 is after date1
		// System.out.println(date2.compareTo(date1));

		// date2 = sdf.parse("19:28");
		// Outputs 0 as the dates are now equal

		int num = date2.compareTo(date);
		if ((num == 1) || (num == 0)) {
			isSet = true;
			System.out.println("time set for today>>>>>>>>>>>>");
		} else {
			System.out.println("time not set for today>>>>>>>>>>>>");
			isSet = false;
		}
		return isSet;
	}

	public void writeToFile(Location loc, Location currentBestLocation) {
		// check this new location is atleast 500 meters from previous location
		// only then log this values
		if (!checkDistance(loc, currentBestLocation)) {
			System.out.println(" it is false returned >>>>>>>>>>>>>>>>>>>>> ");
			return;
			
		}
		// reset the current best location
		LocationPollerService.currentBestLocation = loc;

		if (!Utility.getUtility().checkSDCard(mContext)) {
			// sd card unavailable
			return;
		}
		Utility.getAddressFromLocation(loc.getLatitude(), loc.getLongitude(),
				mContext, new GeocoderHandler());

	}

	/**
	 * @description: checks if distance between new location fix(param1) and
	 *               previous location(currentBestLocation) is >= 500 meters
	 * @return: true if both location distance vary by atleast 500 meters false
	 *          otherwise
	 * @param1: new location
	 */
	private boolean checkDistance(Location loc, Location currentBestLocation) {
		Log.i(TAG, "checkDistance");
		boolean isTrue = false;
		if (currentBestLocation == null) {
			// return true as there is no previous location
			System.out.println(" checkDistance : first location co ordinate");
			return isTrue = true;
		}

		double startLatitude = currentBestLocation.getLatitude() / 1E6;
		double startLongitude = currentBestLocation.getLongitude() / 1E6;
		double endLatitude = loc.getLatitude() / 1E6;
		double endLongitude = loc.getLongitude() / 1E6;
		if (currentBestLocation != null) {
			float[] results = new float[3];
			;
			try {
				android.location.Location.distanceBetween(startLatitude,
						startLongitude, endLatitude, endLongitude, results);
				if ((results[0]* 1E6) >= Constants.LOG_DISTANCE_RANGE) {
					isTrue = true;
				} else {
					isTrue = false;
				}
			} catch (IllegalArgumentException e) {
				Log.e(TAG,
						"Exception when determining the distance between prevoius and current location value");
				// return true to avoid writing in file
				return false;
			}
		}
		return isTrue;
	}

	private class GeocoderHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			String address;
			switch (message.what) {
			case 1:
				Bundle bundle = message.getData();
				address = bundle.getString("address");
				File logFile = null;
				File directory = new File(
						Environment.getExternalStorageDirectory(), "/Locator");
				boolean exist = directory.exists();
				if (!exist) {
					System.out
							.println(" it does not exist");
					directory.mkdirs();
				}
				logFile = new File(Environment.getExternalStorageDirectory(),
						"/Locator/LocationLog.txt");
				try {
					if (!logFile.exists()) {
						logFile.createNewFile();
					}
					BufferedWriter out = new BufferedWriter(new FileWriter(
							logFile, true));

					out.write(new Date().toString());
					out.write("\n");
					out.write(address);
					out.write("\n");
					out.write("\n");
					out.close();
				} catch (IOException e) {
					Log.e(getClass().getName(),
							"Exception appending to log file", e);
				}
				break;
			default:
				address = null;
			}
		}
	}

}
