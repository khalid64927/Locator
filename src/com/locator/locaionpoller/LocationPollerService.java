/***
  Copyright (c) 2010 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.locator.locaionpoller;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.locator.utility.Constants;
import com.locator.utility.Utility;

/**
 * Service providing the guts of the location polling engine. Uses a WakeLock to
 * ensure the CPU stays on while the location lookup is going on. Handles both
 * successful and timeout conditions.
 * 
 * Those wishing to leverage this service should do so via the LocationPoller
 * class.
 */
public class LocationPollerService extends Service {
	private static final String LOCK_NAME_STATIC = "com.commonsware.cwac.locpoll.LocationPoller";
	private static final int DEFAULT_TIMEOUT = 60000; // one minutes
	private static int TIMEOUT = DEFAULT_TIMEOUT;
	private static final String TAG = "LocationPollerService";
	private static volatile PowerManager.WakeLock lockStatic = null;
	private LocationManager locMgr = null;
	private static int feature;
	private static int availableProvider;
	private static Location mGPSLocation;
	private static Location mNetworkLocation;
	private static Context mCtx;

	public static Location currentBestLocation;
	private LocationPollerHelper locpollhelper;

	/**
	 * Lazy-initializes the WakeLock when we first use it. We use a partial
	 * WakeLock since we only need the CPU on, not the screen.
	 */
	synchronized private static PowerManager.WakeLock getLock(Context context) {
		if (lockStatic == null) {
			PowerManager mgr = (PowerManager) context.getApplicationContext()
					.getSystemService(Context.POWER_SERVICE);

			lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					LOCK_NAME_STATIC);
			lockStatic.setReferenceCounted(true);
		}

		return (lockStatic);
	}

	/**
	 * Called by LocationPoller to trigger a poll for the location. Acquires the
	 * WakeLock, then starts the service using the supplied Intent (setting the
	 * component so routing always goes to the service).
	 */
	public static void requestLocation(Context ctxt, Intent i) {
		mCtx = ctxt;
		Log.i(TAG, "requestLocation");
		feature = Utility.getUtility().checkFeatures(ctxt);
		availableProvider = Utility.getUtility().findAvailabeProvider(feature,
				mCtx);
		mGPSLocation = Utility.getUtility().getLocationByProvider(
				LocationManager.GPS_PROVIDER, mCtx);
		mNetworkLocation = Utility.getUtility().getLocationByProvider(
				LocationManager.NETWORK_PROVIDER, mCtx);

		if ((feature == 3)
				|| (availableProvider == 3)
				|| ((mGPSLocation == null) && (mNetworkLocation == null) && (currentBestLocation == null))) {
			System.out
					.println(" no feature is available to get location  >>>>>>>>>>>>");
			return;
		} else {
			System.out
					.println(" requestLocation if provider and intent != null >>>>>>>>>>>>>>>>>> ");
			getLock(ctxt.getApplicationContext()).acquire();

			i.setClass(ctxt, LocationPollerService.class);

			ctxt.startService(i);
		}
	}

	/**
	 * Obtain the LocationManager on startup
	 */
	@Override
	public void onCreate() {
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	/**
	 * No-op implementation as required by superclass
	 */
	@Override
	public IBinder onBind(Intent i) {
		return (null);
	}

	/**
	 * Validates the required extras (EXTRA_PROVIDER and EXTRA_INTENT). If
	 * valid, updates the Intent to be broadcast with the application's own
	 * package (required to keep the broadcast within this application, so we do
	 * not leak security information). Then, forks a PollerThread to do the
	 * actual location lookup.
	 * 
	 * @return START_REDELIVER_INTENT to ensure we get the last request again
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println(" onStartCommands >>>>>>>>>>>>>>>>>> ");
		PowerManager.WakeLock lock = getLock(this.getApplicationContext());

		if (!lock.isHeld() || (flags & START_FLAG_REDELIVERY) != 0) {
			lock.acquire();
		}

		new PollerThread(lock, locMgr).start();

		return (START_REDELIVER_INTENT);
	}

	/**
	 * A WakefulThread subclass that knows how to look up the current location,
	 * plus handle the timeout scenario.
	 */
	private class PollerThread extends WakefulThread {
		private LocationManager locMgr = null;
		private MyPollerLocationListner2 polLocList2;
		private Runnable onTimeout = null;
		private LocationListener listner = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location location) {
				handler.removeCallbacks(onTimeout);
				if (Utility.getUtility().checkBestLocation(currentBestLocation,
						location)) {
					// currentBestLocation = location;
					System.out.println(" listner and gps better than current");
					newLocation(location);
				}
				quit();

			}
		};
		private Handler handler = new Handler();

		PollerThread(PowerManager.WakeLock lock, LocationManager locMgr) {
			super(lock, "LocationPoller-PollerThread");
			this.locMgr = locMgr;

		}

		/**
		 * Called before the Handler loop begins. Registers a timeout, so we do
		 * not wait forever for a location. When a timeout occurs, broadcast an
		 * Intent containing an error extra, then terminate the thread. Also,
		 * requests a location update from the LocationManager.
		 */
		@Override
		protected void onPreExecute() {
			polLocList2 = new MyPollerLocationListner2();
			System.out.println(" onPreExecute feature " + feature
					+ " availableProvider" + availableProvider);
			if ((mNetworkLocation == null) && (mGPSLocation == null)) {
				Utility.getUtility().printLog(Constants.INFO, TAG, "Last known location could not be found");
			}else if (availableProvider == 0) {
				// check is both location are not null
				if ((mGPSLocation != null) && (mNetworkLocation != null)) {
					if (Utility.getUtility().checkBestLocation(mGPSLocation,
							mNetworkLocation)) {
						// network location is better
						if (Utility.getUtility().checkBestLocation(
								currentBestLocation, mNetworkLocation)) {
							// network loc
							// currentBestLocation = mNetworkLocation;
							System.out
									.println(" 0 and net better than current");
							newLocation(mNetworkLocation);

						}
					} else {
						// gps loc is better
						if (Utility.getUtility().checkBestLocation(
								currentBestLocation, mGPSLocation)) {
							// gps is better
							// currentBestLocation = mGPSLocation;
							System.out
									.println(" 0 and gps better than current");
							newLocation(mGPSLocation);
						}
					}
				}
			} else if (availableProvider == 1) {
				if (Utility.getUtility().checkBestLocation(currentBestLocation,
						mGPSLocation)) {
					// currentBestLocation = mGPSLocation;
					System.out.println(" 1 and gps better than current");
					newLocation(mGPSLocation);
				}

			} else if (availableProvider == 2) {
				if (Utility.getUtility().checkBestLocation(currentBestLocation,
						mNetworkLocation)) {

					/*
					 * if(currentBestLocation == null){ System.out.println(
					 * ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> currentBestLocation is null"
					 * ); }else{ showLocation(currentBestLocation,
					 * mNetworkLocation); }
					 */
					// currentBestLocation = mNetworkLocation;
					System.out.println(" 2 and net better than current");
					newLocation(mNetworkLocation);
				}

			}
			onTimeout = new Runnable() {
				public void run() {
					System.out
							.println(" run >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");

					quit();
				}
			};

			handler.postDelayed(onTimeout, TIMEOUT);

			try {
				if (availableProvider == 1) {
					locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
							5000, 1, listner);
				} else if (availableProvider == 2) {
					locMgr.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 5000, 1,
							polLocList2);
				} else if (availableProvider == 0) {
					locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
							5000, 1, listner);
					locMgr.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 5000, 1,
							polLocList2);
				}
			} catch (IllegalArgumentException e) {
				// see http://code.google.com/p/android/issues/detail?id=21237
				Log.w(getClass().getSimpleName(),
						"Exception requesting updates -- may be emulator issue",
						e);
				quit();
			}
		}

		/**
		 * Called when the Handler loop ends. Removes the location listener.
		 */
		@Override
		protected void onPostExecute() {
			if (availableProvider == 1) {
				locMgr.removeUpdates(listner);
			} else if (availableProvider == 2) {
				locMgr.removeUpdates(polLocList2);
			} else if (availableProvider == 0) {
				locMgr.removeUpdates(listner);
				locMgr.removeUpdates(polLocList2);
			}

			super.onPostExecute();
		}

		/**
		 * Called when the WakeLock is completely unlocked. Stops the service,
		 * so everything shuts down.
		 */
		@Override
		protected void onUnlocked() {
			stopSelf();
		}

		private class MyPollerLocationListner2 implements LocationListener {

			@Override
			public void onLocationChanged(Location location) {
				handler.removeCallbacks(onTimeout);
				/*
				 * if(currentBestLocation == null){ System.out.println(
				 * ">>>>>>>>>>>>>>>>>>>>>>>>MyPollerLocationListner2>>>>>>>>>>>>>>>> currentBestLocation is null"
				 * ); }else{ showLocation(currentBestLocation, location); }
				 */
				if (Utility.getUtility().checkBestLocation(currentBestLocation,
						location)) {
					// currentBestLocation = location;
					System.out
							.println(" MyPollerLocationListner2 and gps better than current");
					newLocation(location);
				}
				quit();
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

		}
	}

	private void newLocation(Location loc) {
		if (loc == null) {
			System.out.println("location is unavailable >>>>>>>>>>>>");
			return;
		}
		locpollhelper = new LocationPollerHelper(this);
		locpollhelper.checkFormStoredLocation(loc);
		locpollhelper.writeToFile(loc, currentBestLocation);

	}

}
