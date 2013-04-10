package com.locator.ui;

import java.util.List;

import android.content.Intent;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.locator.components.LocationUpdate;
import com.locator.components.LocationUpdate.SecretCityLocationListner;
import com.locator.components.ProfileStaticData;
import com.locator.utility.Utility;

public class SelectLocation extends MapActivity implements
		SecretCityLocationListner {

	private MapView mMapView;
	private MapController mMapController;
	private LocationUpdate mLocationUpdate;
	private GeoPoint mGeoPoint;
	List<Overlay> listOfOverlays;
	private MapOverlay mMapOverlay;
	private ProfileStaticData mProfileStaticData;
	private static final String TAG = "SelectLocation";
	private RelativeLayout mAddressLayout;
	private TextView mAddressTextView;
	private Location mLocation;
	
	//==================
	

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.select_location);
		
		Log.i(TAG, "onCreate");
		mMapView = (MapView) findViewById(R.id.mapView);
		mMapController = mMapView.getController();
		mMapView.setBuiltInZoomControls(true);
		mMapController.setZoom(18);
		
		mAddressTextView = (TextView) findViewById(R.id.textView1);
		mAddressLayout = (RelativeLayout)findViewById(R.id.relativeLayout1);
		mLocationUpdate = LocationUpdate.getInstance(this);

		// ---Add a location marker---
		mMapOverlay = new MapOverlay();
		listOfOverlays = mMapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mMapOverlay);

		mMapView.invalidate();

		Intent intent = getIntent();
		if(intent.getSerializableExtra("data")!=null){
			mProfileStaticData = (ProfileStaticData) intent.getSerializableExtra("data");
			System.out.println(" is there");
		}else{
			System.out.println(" is not there");
		}	
		
		getAddress(mMapView);
	}	
	@Override
	protected void onResume() {
		super.onResume();
		mLocationUpdate.registerSecretCityLocationListner(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationUpdate.unregisterSecretCityLocationListner();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	class MapOverlay extends com.google.android.maps.Overlay {
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);
			return true;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			// ---when user lifts his finger---
			if (event.getAction() == event.ACTION_MOVE) {
				getAddress(mapView);				
			}
			return false;
		}
	}

	private class GeocoderHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			String address = null;
			System.out.println(" GeocoderHandler");
			switch (message.what) {			
			case 1:
				Bundle bundle = message.getData();
				address = bundle.getString("address");
				if (address == null) {
					address = "address not avialable for this location";
					mAddressLayout.setVisibility(View.INVISIBLE);
				}
				mAddressTextView.setText(address);
				mAddressLayout.setVisibility(View.VISIBLE);
				break;
			default:
				address = null;
			}
		}
	}
	
	private void getAddress(MapView mapView){
		mGeoPoint = mapView.getMapCenter();
		double latitude = ((double) (mGeoPoint.getLatitudeE6() / 1E6));
		double longitude = ((double) (mGeoPoint.getLongitudeE6() / 1E6));
		if(mLocation!=null){
			mLocation.setLatitude(latitude);
			mLocation.setLongitude(longitude);
		}
		Utility.getAddressFromLocation(latitude, longitude,
				getApplicationContext(), new GeocoderHandler());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// save the location
		
			Intent intent = new Intent();
			intent.putExtra("loc", mLocation);
			intent.putExtra("data", mProfileStaticData);
		//	System.out.println(mLocation.getLatitude()+"--onKeydown-"+mLocation.getLongitude());
			setResult(1, intent);
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void newLocationUpdate(Location loc) {
		mLocation = loc;
		// mPsd.setmLocation(loc);
		if (loc != null) {
			mGeoPoint = new GeoPoint((int)(loc.getLatitude()*1E6), (int)(loc.getLongitude()*1E6));
			mMapController.animateTo(mGeoPoint);
			System.out.println(" newLocationUpdate:  not null");
		} else {
			System.out.println(" newLocationUpdate:   null>>>>>>>>>>>>>>>>");
		}
	}
	/*private static String getAddress(Address address){
		String lAddress = null;
		
		if(address.getFeatureName()!= null){
			lAddress = address.getFeatureName();
		}if(address.getAddressLine(0) != null){
			lAddress = lAddress+"\n"+address.getAddressLine(0);
		}if(address.getLocality() != null){
			lAddress = lAddress+"\n"+address.getLocality();
		}if(address.getSubLocality() != null){
			lAddress = lAddress+"\n"+address.getSubLocality();
		}if(address.getAdminArea()!= null){
			lAddress = lAddress+address.getAdminArea();
		}if(address.getPostalCode()!= null){
			lAddress = lAddress+"\n"+address.getPostalCode();
		}if(address.getCountryName()!= null){
			lAddress = lAddress+"\n"+address.getCountryName();
		}if(address.getCountryCode()!= null){
			lAddress = lAddress+address.getCountryCode();
		}if(address.getPhone()!= null){
			lAddress = lAddress+"\n"+address.getPhone();
		}
		return lAddress;
	}*/
	
}
