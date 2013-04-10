package com.locator.components;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.locator.ui.R;
import com.locator.utility.Utility;

/**
 * @author Md Khalid Hamid
 *
 */

public class LocationUpdate {
	
	private static final String TAG = "LocationUpdate";
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 5000; // in Milliseconds
	private static final int TWO_MINUTES = 2 * 60 * 1000;
	private static final int GPS_AND_NETWORK = 0;
	private static final int GPS = 1;
	private static final int NETWORK = 2;
	protected static LocationManager locationManager;
	// instance variable to save network state
	private static int mSelect_provider = 4;
	private static LocationUpdate instance;
	private static Location currentBestLocation;	
	private MyLocationListener locationListner;
	private MySecondLocationListener secondLocationListner;
	private String networkProvider;
	private String GPSProvider;
	private String mContext;
	private Location mGPSLocation;
	private Location mNetworkLocation;
	private SecretCityLocationListner mLocLtr = null;
	private Context mCtx;
	
	private LocationUpdate(Context ctx){
		this.mCtx = ctx;
	}
	 	
	public static synchronized LocationUpdate getInstance(Context ctx){
		if(instance == null){
			instance = new LocationUpdate(ctx);
		}
		return instance;
	}
		
	/**
	 * starts the location query algorithm
	 */
	@SuppressWarnings("static-access")
	private void getLocationUpdate(){	
		Utility.getUtility().displayToast(mCtx, mCtx.getString(R.string.loc_waiting));
		Log.i(TAG, "inside getLocationUpdate ");
        mContext = mCtx.LOCATION_SERVICE;        
        locationManager = (LocationManager)mCtx.getSystemService(mContext);  
        GPSProvider = LocationManager.GPS_PROVIDER;
        networkProvider = LocationManager.NETWORK_PROVIDER; 
        locationListner = new MyLocationListener();
        secondLocationListner = new MySecondLocationListener();
        checkGPSAndNetworkFeatures();     
        getBestInitialLocation();
        
        System.out.println(" the set listners are "+mSelect_provider);
        if(mSelect_provider == 0){
        	locationManager.requestLocationUpdates(GPSProvider, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, locationListner);
        	locationManager.requestLocationUpdates(networkProvider, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, secondLocationListner);
        }else if((mSelect_provider == 1)){
        	locationManager.requestLocationUpdates(GPSProvider, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, locationListner);	        
        }else if(mSelect_provider == 2) {
        	locationManager.requestLocationUpdates(networkProvider, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, secondLocationListner);     
        }              
        
	}	
	
	/**
	 * determines available features on the devices such as network and GPS by setting mSelect_provider instance variable
	 *  ---------------
	 * Feature state
	 * ---------------
	 * GPS and Network = 0
	 * Only GPS = 1
	 * Only Network = 2
	 * None = 3  
	 * @return null;
	 *
	 */
	@SuppressWarnings({ "static-access" })
	private void checkGPSAndNetworkFeatures(){
		PackageManager pm = mCtx.getPackageManager();							
		if(((pm.hasSystemFeature(pm.FEATURE_LOCATION_GPS)) == false)&&((Utility.getUtility().isNetworkAvailable(mCtx)) == false)){
			mSelect_provider = 3;
		//	Log.i(TAG+" checkGPSAndNetwork", "has no gps and network feature ");
		}else if(((pm.hasSystemFeature(pm.FEATURE_LOCATION_GPS)) == true)&&((Utility.getUtility().isNetworkAvailable(mCtx)) == true)){
		//	Log.i(TAG+" checkGPSAndNetwork", " has gps and network ");
			findAvailabeProvider(GPS_AND_NETWORK);
		}else if(((pm.hasSystemFeature(pm.FEATURE_LOCATION_GPS)) == true)&&((Utility.getUtility().isNetworkAvailable(mCtx)) == false)){
		//	Log.i(TAG+" checkGPSAndNetwork", " has only GPS feature");
			findAvailabeProvider(GPS);
		}else if(((pm.hasSystemFeature(pm.FEATURE_LOCATION_GPS)) == false)&&((Utility.getUtility().isNetworkAvailable(mCtx)) == true)){
		//	Log.i(TAG+" checkGPSAndNetwork", " has only Network feature");
			findAvailabeProvider(NETWORK);
		}
	}
	
	/**
	 * gets best initial location fix and save them in respective (mNetworkLocation , mGPSLocation)instance variables and triggers response accordingly
	 * using interface  
	 * @return null;
	 */
	private void getBestInitialLocation(){	
		Log.d(TAG," getBestInitialLocation: The availbale provider is "+mSelect_provider);
	switch(mSelect_provider){
	case 0: mGPSLocation = getLocationByProvider(GPSProvider);
			mNetworkLocation = getLocationByProvider(networkProvider);
			if((mNetworkLocation == null)&&(mGPSLocation == null)){
				currentBestLocation = null;
				System.out.println(" initial location could not be found!, Please wait until location is determined");
		}else if((mGPSLocation != null)&&(mNetworkLocation != null)){		
				if(checkBestLocation(mGPSLocation, mNetworkLocation)){
					currentBestLocation = mGPSLocation;
				}else{
					currentBestLocation = mNetworkLocation;
				}	
				System.out.println(" getBestInitialLocation when both ft ava and enabled then >>>>>>>>>:"+currentBestLocation.getLatitude());
			}else if(mNetworkLocation != null){
					currentBestLocation = mNetworkLocation;
					}else{
						currentBestLocation = mGPSLocation;
					}			
			break;
	case 1: mGPSLocation = getLocationByProvider(GPSProvider);
			if(mGPSLocation != null){
				System.out.println(" getBest GPS not null");
			}else{
				System.out.println(" getBest GPS null");
			}
			currentBestLocation = mGPSLocation;
			break;
	case 2: mNetworkLocation = getLocationByProvider(networkProvider);
			currentBestLocation = mNetworkLocation;
			break;
	case 3:Log.d(TAG, "getBestInitialLocation : Location Unavailable");
			break;
	default:Log.d(TAG, "select_parovider not set---------------->");
			break;
	}		
		System.out.println(" sending from getBestInitialLocation");
		if(currentBestLocation == null){
			startWait();
		}else{
			sendUpdatetoInterface(currentBestLocation);
		}
		
	}
	
	/**
	 * determines if location providers are ON or OFF state and sets the instance variable mSelect_providers accordingly 
	 * 
	 */
	private void findAvailabeProvider(int features){
		Log.i(TAG+" findAvailabeProvider", " the available feature(s) are/is "+features);
		switch(features){
		
		case 0:if((locationManager.isProviderEnabled(GPSProvider) == true)&&(locationManager.isProviderEnabled(networkProvider) == true)){
				mSelect_provider = 0;
				}else if((locationManager.isProviderEnabled(GPSProvider) == true)&&(locationManager.isProviderEnabled(networkProvider) == false)){
					mSelect_provider = 1;
				}else if ((locationManager.isProviderEnabled(GPSProvider) == false)&& (locationManager.isProviderEnabled(networkProvider) == true)){
					mSelect_provider = 2;
				}else{
					mSelect_provider = 3;
				}
			//	Log.i(TAG+" findAvailabeProvider", " has both ft "+mSelect_provider);
				break;
		
		case 1: if((locationManager.isProviderEnabled(GPSProvider) == true)){
				mSelect_provider = 1;
				}else{
					mSelect_provider = 3;
				}
		//		Log.i(TAG+" findAvailabeProvider", " has gps ft "+mSelect_provider);
				break;
			
		case 2: if ((locationManager.isProviderEnabled(networkProvider) == true)){
				mSelect_provider = 2;
				}else{
					mSelect_provider = 3;
				}
		//		Log.i(TAG+" findAvailabeProvider", " has network ft "+mSelect_provider);
				break;
			
		case 3:	mSelect_provider = 3;
		//		Log.i(TAG+" findAvailabeProvider", " has no ft "+mSelect_provider);
				break;
			
		default:// Log.i(TAG+" findAvailabeProvider", " default case ");
				break;
		}
				if(mSelect_provider == 3){
					Utility.getUtility().displayToast(mCtx, mCtx.getString(R.string.provider_alert));
				}else if(mSelect_provider == 2){
					if(features == 0){
						// displaying toast only if it has gps feature and was off
						Utility.getUtility().displayToast(mCtx, mCtx.getString(R.string.gps_alert));
					}					
				}else if(mSelect_provider == 1){
					Utility.getUtility().displayToast(mCtx, mCtx.getString(R.string.network_alert));
				}
		
	}
		
	
	private boolean checkBestLocation(Location previousLocation, Location location){
		
		 if (previousLocation == null) {
		        // A new location is always better than no location		 
		        return true;
		    }
		    // Check whether the new location fix is newer or older
		    long timeDelta = location.getTime() - previousLocation.getTime();
		    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		    boolean isNewer = timeDelta > 0;

		    // If it's been more than two minutes since the current location, use the new location
		    // because the user has likely moved
		    if (isSignificantlyNewer) {
		        return true;
		    // If the new location is more than two minutes older, it must be worse
		    } else if (isSignificantlyOlder) {
		        return false;
		    }

		    // Check whether the new location fix is more or less accurate
		    int accuracyDelta = (int) (location.getAccuracy() - previousLocation.getAccuracy());
		    boolean isLessAccurate = accuracyDelta > 0;
		    boolean isMoreAccurate = accuracyDelta < 0;
		    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		    // Check if the old and new location are from the same provider
		    boolean isFromSameProvider = isSameProvider(location.getProvider(),
		    		previousLocation.getProvider());

		    // Determine location quality using a combination of timeliness and accuracy
		    if (isMoreAccurate) {
		        return true;
		    } else if (isNewer && !isLessAccurate) {
		        return true;
		    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
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
	
	private void sendUpdatetoInterface(Location loc){
	//	System.out.println("sendUpdatetoInterface:   the current best location>>>>>>>>>>>>>>>"+loc.getLatitude()+"log :"+loc.getLongitude());
		if(loc == null){
			System.out.println("sendUpdatetoInterface : null");
		}else{
			mLocLtr.newLocationUpdate(loc);
		}
		}
	
	
	private Location getLocationByProvider(String provider){
		Location location = null;
		/* LocationManager locationManager = (LocationManager) mCtx
	 	            .getSystemService(mContext);*/
	 	    try {	 	        
	 	            location = locationManager.getLastKnownLocation(provider);
	 	    } catch (IllegalArgumentException e) {
	 	        Log.d(TAG, "  getLocationByProvider: Cannot acces Provider " + provider);
	 	    }	
		return location;
	}
	
	 private class MyLocationListener implements LocationListener {

	        public void onLocationChanged(Location location) {
	            String message = String.format(
	                    "New Location (GPS_Listner) \n Longitude: %1$s \n Latitude: %2$s",
	                    location.getLongitude(), location.getLatitude()	                 
	            );
	         
	            	currentBestLocation = location;
	            	sendUpdatetoInterface(location);
	            Utility.getUtility().displayToast(mCtx, message);
	        }

	        public void onStatusChanged(String s, int i, Bundle b) {
	        	Utility.getUtility().displayToast(mCtx, "GPS Provider status changed");
	        }

	        public void onProviderDisabled(String s) {
	        	Utility.getUtility().displayToast(mCtx,"GPS Provider disabled by the user. GPS turned off");
	        }

	        public void onProviderEnabled(String s) {
	        	Utility.getUtility().displayToast(mCtx,"GPS Provider enabled by the user. GPS turned on");
	        }

	    }
	 
	 
	 private class MySecondLocationListener implements LocationListener {

	        public void onLocationChanged(Location location) {
	            String message = String.format(
	                    "New Location (network_Listner) \n Longitude: %1$s \n Latitude: %2$s",
	                    location.getLongitude(), location.getLatitude()	                 
	            );
	            if(checkBestLocation(location, currentBestLocation)){
	            	currentBestLocation = location;
	            	sendUpdatetoInterface(location);
	            }
	           /* mGPSLocation = location;
	            updateCurrentLocation(mSelect_provider);*/
	            Toast.makeText(mCtx, message, Toast.LENGTH_LONG).show();
	        }

	        public void onStatusChanged(String s, int i, Bundle b) {
	            Toast.makeText(mCtx, "network Provider status changed",
	                    Toast.LENGTH_LONG).show();
	        }

	        public void onProviderDisabled(String s) {
	            Toast.makeText(mCtx,
	                    "network Provider disabled by the user. network turned off",
	                    Toast.LENGTH_LONG).show();
	        }

	        public void onProviderEnabled(String s) {
	            Toast.makeText(mCtx,
	                    "network Provider enabled by the user. network turned on",
	                    Toast.LENGTH_LONG).show();
	        }

	    }
	 
		private void stopThread() {
			System.out.println(" unrgistering>>>>>>>>>>>>>>>>>>>>>"+mSelect_provider);
			if(mSelect_provider == 0){
				locationManager.removeUpdates(locationListner);
				locationManager.removeUpdates(secondLocationListner);
			}else if(mSelect_provider == 1){
				locationManager.removeUpdates(locationListner);
			}else if(mSelect_provider == 2){
				locationManager.removeUpdates(secondLocationListner);
			}
					
			Log.d(TAG,"stopThread: stopping thread");
		}
		
		public void registerSecretCityLocationListner(SecretCityLocationListner scLocltr){
			mLocLtr = scLocltr;
			getLocationUpdate();
		}
		
		public void unregisterSecretCityLocationListner(){
			stopThread();
		}
		
		
		/**
		 * @author Mohammed Khalid Hamid
		 * @description Provides Location updates as and when available
		 *
		 */
		public interface SecretCityLocationListner {
			
			/**
			 *@description This function provides location updates
			 *@param Location : latest location update/null if location cannot be found 
			 *
			 */
			public void newLocationUpdate(Location loc);
		}
	 
	
		/**
		 * start waiting for location fix if initial location fix is unavailable
		 * @return null
		 */
		private void startWait(){
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					stopWait();
					
				}
			}, 5000);
		}
		
		
		/**
		 * stop waiting for location fix which has been triggered with startWaitThread
		 * @return null
		 */
		private void stopWait(){
			System.out.println("waiting stopped");
			if(currentBestLocation == null){
				Utility.getUtility().displayToast(mCtx, mCtx.getString(R.string.location_unavailable));
			}
		}

}

