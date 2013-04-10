package com.locator.utility;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.locator.components.ProfileStaticData;
import com.locator.locaionpoller.LocationPoller;
import com.locator.provider.ProfileProvider;
import com.locator.ui.ProfileAddOrUpdate;
import com.locator.ui.ProfileList;
import com.locator.ui.R;

public class Utility {
	private static Utility utility;
	private static final String TAG = "Utility";
	PendingIntent pi = null;
	AlarmManager mgr = null;

	public static Utility getUtility() {
		if (utility == null) {
			utility = new Utility();
		}
		return utility;

	}
	
	public void printLog(int logType, String tag, String message){
		switch(logType){
		case 0:Log.i(tag, message);
			break;
		case 1:Log.d(tag, message);
			break;
		case 2:Log.v(tag, message);
			break;
		case 3:Log.w(tag, message);
			break;
		case 4:Log.e(tag, message);
			break;
		case 5:Log.wtf(tag, message);
			break;
		default:Log.d(tag, "deafult Log");
			break;
		}
		
	}
	

	
	// function to adjust values to accomodate 12 charater length of each pick
		// up time
		public String adjustValues(String str) {
			int strSize = str.length();
			char strA = (char) str.charAt(0);
			char strB = 'K';
			if (strSize > 1) {
				strB = str.charAt(1);
				if ((strA == '0') && (strB == '0')) {
					str = "00";
				}
			} else {
				if (strA == '0') {
					str = "00";
				}
				if (strA != '0') {
					str = "0" + str;
				}
			}
			return str;
		}
	public boolean showDialog(final Context ctx,
			final ProfileStaticData selectedProfileData) {

		AlertDialog.Builder getImageFrom = new AlertDialog.Builder(ctx);
		getImageFrom.setTitle(ctx.getString(R.string.select) + ":");
		final CharSequence[] opsChars = {
				ctx.getResources().getString(R.string.edit_profile),
				ctx.getResources().getString(R.string.delete_profile) };
		getImageFrom.setItems(opsChars,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Log.d(TAG, "onClick : which == 0");
							Intent NPintent = new Intent(ctx,
									ProfileAddOrUpdate.class);
							NPintent.putExtra(Constants.IS_NEW_PROFILE, false);
							NPintent.putExtra(Constants.PROFILE_DATA_KEY,
									selectedProfileData);
							ctx.startActivity(NPintent);
							Constants.isDeleted = false;
						} else if (which == 1) {
							Log.d(TAG, "onClick : which == 1");
							System.out.println("delete");
							Uri myUri = ProfileProvider.CONTENT_PROFILE_URI;
							ctx.getContentResolver().delete(myUri,
									"_id ==" + selectedProfileData.getmPID(),
									null);
							if (isNull(ctx)) {
								stopAlarm(ctx);
								// reset the constant holding the SI no of current set profile
								Constants.sCurrentSetProfileID = null;
							}
							Constants.isDeleted = true;
							((ProfileList) ctx).loadList();
						}

						dialog.dismiss();
					}
				});
		getImageFrom.show();
		return Constants.isDeleted;
	}

	/**
	 * Displays a toast
	 */
	public void displayToast(Context context, String message) {
		if (message != null) {
			if (message.length() <= 20) {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			} else if (message.length() > 20) {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		}
	}

	public void releaseImgBtnMemory(ImageButton imgBtn) {
		if (imgBtn != null) {
			Drawable drw = imgBtn.getBackground();
			if (drw != null) {
				drw.setCallback(null);
				drw = null;
			}
			drw = imgBtn.getDrawable();
			if (drw != null) {
				drw.setCallback(null);
				drw = null;
			}
		}
	}

	public void releaseBtnMemory(Button btn) {
		if (btn != null) {
			Drawable drw = btn.getBackground();
			if (drw != null) {
				drw.setCallback(null);
				drw = null;
			}
		}
	}

	public void releaseChkMemory(CheckBox chk) {
		if (chk != null) {
			Drawable drw = chk.getBackground();
			if (drw != null) {
				drw.setCallback(null);
				drw = null;
			}
		}
	}

	public void releaseEdtBoxMemory(EditText edt) {
		if (edt != null) {
			Drawable drw = edt.getBackground();
			if (drw != null) {
				drw.setCallback(null);
				drw = null;
			}
		}
	}

	public void releaseImgViewMemory(ImageView imgView) {
		if (imgView != null) {
			Drawable drw = imgView.getBackground();
			if (drw != null) {
				drw.setCallback(null);
				drw = null;
			}
			drw = imgView.getDrawable();
			if (drw != null) {
				drw.setCallback(null);
				drw = null;
			}
		}
	}

	public void releaseViewMemory(View vw) {
		if (vw != null) {
			Drawable drw = vw.getBackground();
			if (drw != null) {
				drw.setCallback(null);
				drw = null;
			}
		}
	}

	public void showAlertDialog(Context context, int Message, int title) {
		// Log.d("Util.java", "showAlertDialog Method calling");
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
		alt_bld.setTitle(title);
		alt_bld.setMessage(Message)
				.setCancelable(false)
				.setPositiveButton("Settings",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Action for 'Yes' Button
								
							}
						});
		alt_bld.setNegativeButton("Skip", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		AlertDialog alert = alt_bld.create();
		// Icon for AlertDialog
		alert.setIcon(R.drawable.ic_launcher);
		// Title for AlertDialog
		alert.setTitle("Locator");
		alert.show();
	}

	/**
	 * Check Internet is available or not.
	 * 
	 * @param context
	 * @return
	 */
	public boolean isNetworkAvailable(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = connec.getActiveNetworkInfo();
		if (ni != null && ni.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public void startAlarm(Context ctx, final int period) {

		mgr = (AlarmManager) ctx.getSystemService(ctx.ALARM_SERVICE);

		Intent i = new Intent(ctx, LocationPoller.class);
		pi = PendingIntent.getBroadcast(ctx, 0, i, 0);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime(), period, pi);

		Toast.makeText(ctx,
				"Location polling every " + period / 1000 + " minutes begun",
				Toast.LENGTH_LONG).show();
	}

	private void stopAlarm(Context ctx) {
		System.out.println(" >>>>>>>>>>>>>>> active");
		mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(ctx, LocationPoller.class);
		pi = PendingIntent.getBroadcast(ctx, 0, i, 0);
		mgr.cancel(pi);
	}

	private boolean isNull(Context ctx) {
		Uri myUri = ProfileProvider.CONTENT_PROFILE_URI;
		Cursor cur = ctx.getContentResolver().query(myUri,
				ProfileProvider.PROFILES_PROJECTION, // Which
				// columns
				// to return
				null, // WHERE clause; which rows to return(all rows)
				null, // WHERE clause selection arguments (none)
				null // Order-by clause (ascending by name)
				);

		if (cur.getCount() < 1) {
			return true;
		}

		return false;
	}

	public Boolean checkSDCard(Context mContext) {
		String auxSDCardStatus = Environment.getExternalStorageState();
		System.out.println(" ::::::::::::::::" + auxSDCardStatus);
		if (auxSDCardStatus.equals(Environment.MEDIA_MOUNTED))
			return true;
		else if (auxSDCardStatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			System.out.println(" ::::::::::::::::" + auxSDCardStatus);
			Toast.makeText(
					mContext,
					"Warning, the SDCard it's only in read mode.\nthis does not result in malfunction"
							+ " of the read aplication", Toast.LENGTH_LONG)
					.show();
			return true;
		} else if (auxSDCardStatus.equals(Environment.MEDIA_NOFS)) {
			Toast.makeText(
					mContext,
					"Error, the SDCard can be used, it has not a corret format or "
							+ "is not formated.", Toast.LENGTH_LONG).show();
			return false;
		} else if (auxSDCardStatus.equals(Environment.MEDIA_REMOVED)) {
			Toast.makeText(
					mContext,
					"Error, the SDCard is not found, to use the reader you need "
							+ "insert a SDCard on the device.",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (auxSDCardStatus.equals(Environment.MEDIA_SHARED)) {
			Toast.makeText(
					mContext,
					"Error, the SDCard is not mounted beacuse is using "
							+ "connected by USB. Plug out and try again.",
					Toast.LENGTH_LONG).show();
			return false;
		} else if (auxSDCardStatus.equals(Environment.MEDIA_UNMOUNTABLE)) {
			Toast.makeText(
					mContext,
					"Error, the SDCard cant be mounted.\nThe may be happend when the SDCard is corrupted "
							+ "or crashed.", Toast.LENGTH_LONG).show();
			return false;
		} else if (auxSDCardStatus.equals(Environment.MEDIA_UNMOUNTED)) {
			Toast.makeText(
					mContext,
					"Error, the SDCArd is on the device but is not mounted."
							+ "Mount it before use the app.", Toast.LENGTH_LONG)
							.show();
			return false;
		}

		return true;
	}

	/**
	 * @description: determines best location among given two location based on
	 *               several factors such as accuracy, time, provider
	 * @return: true if location at param2 is best false otherwise
	 * @param1: previous location
	 * @param2: new location value
	 */
	public boolean checkBestLocation(Location previousLocation,
			Location location) {

		if (previousLocation == null) {
			// A new location is always better than no location

			return true;
		}
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - previousLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > Constants.TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -Constants.TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - previousLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				previousLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public int checkFeatures(Context ctxt) {
		int feature = 0;

		PackageManager pm = ctxt.getPackageManager();
		if (((pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) == false)
				&& ((Utility.getUtility().isNetworkAvailable(ctxt)) == false)) {
			feature = 3;
			System.out
					.println(">>>>>>>>>>>>No GPS feature and No Network >>>>>>>>>>>>>>>>>>>>>>>");
		} else if (((pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) == true)
				&& ((Utility.getUtility().isNetworkAvailable(ctxt)) == true)) {
			Log.i(TAG + " checkGPSAndNetwork", " has gps and network ");
			feature = 0;
		} else if (((pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) == true)
				&& ((Utility.getUtility().isNetworkAvailable(ctxt)) == false)) {
			Log.i(TAG + " checkGPSAndNetwork", " has only network feature");
			feature = 1;
		} else if (((pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) == false)
				&& ((Utility.getUtility().isNetworkAvailable(ctxt)) == true)) {
			Log.i(TAG + " checkGPSAndNetwork", " has only gps feature");
			feature = 2;
		}

		return feature;
	}

	public int findAvailabeProvider(int features, Context ctx) {

		LocationManager locationManager = (LocationManager) ctx
				.getSystemService(ctx.LOCATION_SERVICE);
		int provider = 0;
		switch (features) {

		case 0:
			if ((locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER) == true)
					&& (locationManager
							.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true)) {
				provider = 0;
			} else if ((locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER) == true)
					&& (locationManager
							.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == false)) {
				provider = 1;
			} else if ((locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER) == false)
					&& (locationManager
							.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true)) {
				provider = 2;
			} else {
				provider = 3;
			}
			Log.i(TAG + " findAvailabeProvider", " has both ft " + provider);
			break;

		case 1:
			if ((locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER) == true)) {
				provider = 1;
			} else {
				provider = 3;
			}
			Log.i(TAG + " findAvailabeProvider", " has gps ft " + provider);
			break;

		case 2:
			if ((locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true)) {
				provider = 2;
			} else {
				provider = 3;
			}
			Log.i(TAG + " findAvailabeProvider", " has network ft " + provider);
			break;

		case 3:
			provider = 3;
			Log.i(TAG + " findAvailabeProvider", " has no ft " + provider);
			break;

		default:
			Log.i(TAG + " findAvailabeProvider", " default case ");
			provider = 3;
			break;
		}
		return provider;
	}

	public Location getLocationByProvider(String provider, Context ctx) {
		Location location = null;

		LocationManager locationManager = (LocationManager) ctx
				.getSystemService(ctx.LOCATION_SERVICE);
		try {
			location = locationManager.getLastKnownLocation(provider);
		} catch (IllegalArgumentException e) {
			Log.d(TAG, "  getLocationByProvider: Cannot acces Provider "
					+ provider);
		}
		return location;
	}

	public static void getAddressFromLocation(final double latitude,
			final double longitude, final Context context, final Handler handler) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
				String result = null;
				try {
					List<Address> list = geocoder.getFromLocation(latitude,
							longitude, 1);
					if (list != null && list.size() > 0) {
						System.out.println(" list not null or 0 either");
						Address address = list.get(0);
						// sending back first address line and locality
						result = address.getAddressLine(0) + ", "
								+ address.getLocality() + "\n"
								+ address.getCountryName();
					}
					System.out.println(" list either null or 0");
				} catch (IOException e) {
					Log.e(TAG, "Impossible to connect to Geocoder", e);
				} finally {
					Message msg = Message.obtain();
					msg.setTarget(handler);
					if (result != null) {
						msg.what = 1;
						Bundle bundle = new Bundle();
						bundle.putString("address", result);
						msg.setData(bundle);
					} else
						msg.what = 0;
					msg.sendToTarget();
				}
			}
		};
		thread.start();
	}

	public void setBrightness(int brightness, Context mContext) {

		Activity activity = (Activity) mContext;
		WindowManager.LayoutParams layoutParams = activity.getWindow()
				.getAttributes();
		layoutParams.screenBrightness = brightness / 100.0f;
		activity.getWindow().setAttributes(layoutParams);
	}
	
	public static void printProfileData(ProfileStaticData profileData){
		if(!TextUtils.isEmpty(profileData.getmPName())){
			Log.v(TAG, "Profile Name :"+profileData.getmPName());
		}else{
			Log.e(TAG, "Profile Name is NULL");
			
		}
		if (profileData.getmRingVol() != null) {
			Log.v(TAG, "Ringer Volume is not NULL" +profileData.getmRingVol() );
		} else {
			Log.e(TAG, "Ringer Volume is NULL");
		}
		if (profileData.getmRingTone() != null) {
			Log.e(TAG, "Ringer tone is not NULL" + profileData.getmRingTone());
		} else {
			Log.e(TAG, "Ringer tone is NULL");
		}
		if (profileData.getmNot() != null) {
			Log.e(TAG, "Notification is not NULL" + profileData.getmNot());
		} else {
			Log.e(TAG, "Notification is NULL");
		}
		if (profileData.getmVib() != null) {
			Log.e(TAG, "Vibration is NULL" + profileData.getmVib());
		} else {
			Log.e(TAG, "Vibration is NULL");
		}
		if (profileData.getmWall() != null) {
			Log.e(TAG, "Wallpaper is NULL" + profileData.getmWall());
		} else {
			Log.e(TAG, "Wallpaper is NULL");
		}
		if (profileData.getmLivWall() != null) {
			Log.e(TAG, "Live Wallpaper is not NULL"+ profileData.getmLivWall());
		} else {
			Log.e(TAG, "Live Wallpaper is NULL");
		}
		if(profileData.getmLat() != null){
			Log.v(TAG, "Latitude :"+profileData.getmLat());
		}else{
			Log.e(TAG, "Latitude is NULL");
		}
		if(profileData.getmLog() != null){
			Log.v(TAG, "Longitude :"+profileData.getmLog());
		}else{
			Log.e(TAG, "Longitude is NULL");
		}
		if (profileData.getmTime() != null) {
			Log.e(TAG, "Time is not NULL" + profileData.getmTime());
		} else {
			Log.e(TAG, "Time is NULL");
		}
		if(profileData.getmDays() != null){
			Log.v(TAG, "Days :"+profileData.getmDays());
		}else{
			Log.e(TAG, "Days is NULL");
		}
		if (profileData.getmBrightness() != null) {
			Log.e(TAG, " Brightness is Null" + profileData.getmBrightness());
		} else {
			Log.e(TAG, " Brightness is Null");
		}
	}

}
