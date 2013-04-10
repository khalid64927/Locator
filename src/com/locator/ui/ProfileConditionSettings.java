package com.locator.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

import com.locator.components.ProfileStaticData;
import com.locator.utility.Constants;
import com.locator.utility.Utility;

public class ProfileConditionSettings extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	private CheckBox mChkbx_loc;
	private CheckBox mChkbx_time;
	private CheckBox mChkbx_mon;
	private CheckBox mChkbx_tue;
	private CheckBox mChkbx_wed;
	private CheckBox mChkbx_thu;
	private CheckBox mChkbx_fri;
	private CheckBox mChkbx_sat;
	private CheckBox mChkbx_sun;
	private Button mBtn_select;
	private TimePicker mTp;
	private boolean mM, mT, mW, mTh, mFr, mSa, mSu;

	private static final int s_MONDAY = 01;
	private static final int s_TUESDAY = 02;
	private static final int s_WEDNESDAY = 04;
	private static final int s_THURSDAY = 8;
	private static final int s_FRIDAY = 16;
	private static final int s_SATURDAY = 32;
	private static final int s_SUNDAY = 64;
	private static final int s_ALL = 128;
	private ProfileStaticData mPsd;
	private boolean mIsNewProfile;

	private TextView mTxt_locPts;
	private static final boolean SET24HOURFORMAT = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_condition);

		mChkbx_loc = (CheckBox) findViewById(R.id.location);
		mChkbx_time = (CheckBox) findViewById(R.id.time);
		
		mChkbx_mon = (CheckBox) findViewById(R.id.monday);
		mChkbx_tue = (CheckBox) findViewById(R.id.teusday);
		mChkbx_wed = (CheckBox) findViewById(R.id.wednesday);
		mChkbx_thu = (CheckBox) findViewById(R.id.thursday);
		mChkbx_fri = (CheckBox) findViewById(R.id.friday);
		mChkbx_sat = (CheckBox) findViewById(R.id.saturday);
		mChkbx_sun = (CheckBox) findViewById(R.id.sunday);
		mTp = (TimePicker) findViewById(R.id.timePicker1);
		mBtn_select = (Button) findViewById(R.id.location_points);
		mTxt_locPts = (TextView) findViewById(R.id.txt_locpts);

		mTp.setIs24HourView(SET24HOURFORMAT);
		mBtn_select.setOnClickListener(this);
		System.out.println(" on create called >>>>>>>>>>>>>>>>>");
		mChkbx_mon.setOnCheckedChangeListener(this);
		mChkbx_tue.setOnCheckedChangeListener(this);
		mChkbx_wed.setOnCheckedChangeListener(this);
		mChkbx_thu.setOnCheckedChangeListener(this);
		mChkbx_fri.setOnCheckedChangeListener(this);
		mChkbx_sat.setOnCheckedChangeListener(this);
		mChkbx_sun.setOnCheckedChangeListener(this);
		
		Bundle bd = getIntent().getExtras();
		mIsNewProfile = bd.getBoolean(Constants.IS_NEW_PROFILE);
		mPsd = (ProfileStaticData) bd.getSerializable(Constants.PROFILE_DATA_KEY);
		System.out.println(" the pname is "+mPsd.getmPName());
	}

	private String getTime() {
		return mTp.getCurrentHour() + ":" + mTp.getCurrentMinute();
	}

	private int getDay() {
		int days = 0;

		if ((mM == true) && (mT == true) && (mW == true) && (mTh == true)
				&& (mFr == true)&& (mSa == true) && (mSu == true)) {
			return s_ALL;
		} else {
			if (mM == true) {
				days = days + s_MONDAY;
				Log.i("mon", ":" + days);
			}
			if (mT == true) {
				days = days + s_TUESDAY;
				Log.i("tue", ":" + days);
			}
			if (mW == true) {
				days = days + s_WEDNESDAY;
				Log.i("wed", ":" + days);
			}
			if (mTh == true) {
				days = days + s_THURSDAY;
				Log.i("thu", ":" + days);
			}
			if (mFr == true) {
				days = days + s_FRIDAY;
				Log.i("fri", ":" + days);
			}
			if (mSa == true) {
				days = days + s_SATURDAY;
				Log.i("sat", ":" + days);
			}
			if (mSu == true) {
				days = days + s_SUNDAY;
				Log.i("sun", ":" + days);
			}
		}
		return days;
	}
	private void setCheckBoxStst(int days){
		if(days == s_ALL){
			mM = mT= mW = mTh = mFr = mSa = mSu = true;
			mChkbx_mon.setChecked(true);
			mChkbx_tue.setChecked(true);
			mChkbx_wed.setChecked(true);
			mChkbx_thu.setChecked(true);
			mChkbx_fri.setChecked(true);
			mChkbx_sat.setChecked(true);
			mChkbx_sun.setChecked(true);
		}else if(days == s_MONDAY){
			mChkbx_mon.setChecked(true);			
		}else if(days == s_TUESDAY){
			mChkbx_tue.setChecked(true);
		}else if(days == s_WEDNESDAY){
			mChkbx_wed.setChecked(true);		
		}else if(days == s_THURSDAY){
			mChkbx_thu.setChecked(true);			
		}else if(days == s_FRIDAY){
			mChkbx_fri.setChecked(true);
		}else if(days == s_SATURDAY){
			mChkbx_sat.setChecked(true);
		}else if(days == s_SUNDAY){
			mChkbx_sun.setChecked(true);
		}else{
			sortDay(days);
		}	
	}
	private void sortDay(int value)
	{
		int days = value;
		int var1 = days & 01;		
		int var2 = days & 02;
		int var3 = days & 04;
		int var4 = days & 8;
		int var5 = days & 16;
		int var6 = days & 32;
		int var7 = days & 64;
		
		if(var7 == 64)
		{
			//tempString = this.getResources().getString(R.string.sun);
			mChkbx_sun.setChecked(true);

		}
		if(var6 == 32)
		{
			//tempString = this.getResources().getString(R.string.sat)+" "+tempString;
			mChkbx_sat.setChecked(true);
		}
		if(var5 == 16)
		{
			//tempString = this.getResources().getString(R.string.fri)+" "+tempString;
			mChkbx_fri.setChecked(true);
		}
		if(var4 == 8)
		{
			//tempString = this.getResources().getString(R.string.thu)+" "+tempString;
			mChkbx_thu.setChecked(true);

		}
		if(var3 == 04)
		{
			//tempString = this.getResources().getString(R.string.wed)+" "+tempString;
			mChkbx_wed.setChecked(true);

		}
		if(var2 == 02)
		{
			//tempString = this.getResources().getString(R.string.tue)+" "+tempString;
			mChkbx_tue.setChecked(true);
		}
		if((var1 == 01) )
		{
			//tempString = this.getResources().getString(R.string.mon)+" "+tempString;
			mChkbx_mon.setChecked(true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {			
			mPsd.setmDays(String.valueOf(getDay()));
			Intent intent = new Intent(this, ProfileAddOrUpdate.class);
			intent.putExtra( Constants.IS_NEW_PROFILE,mIsNewProfile);
			intent.putExtra( Constants.NAVIGATION_FLAG,false);
			intent.putExtra(Constants.PROFILE_DATA_KEY, mPsd);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	
	@Override
	public void onClick(View v) {
		if (v == mBtn_select) {
			System.out.println(" onClick called >>>>>>>>>>>>>>>>>"+getTime());
			//System.out.println(" onClick called >>>>>>>>>>>>>>>>>"+String.valueOf(getDay()));
			mPsd.setmTime(getTime());
	///		mPsd.setmDays(
		//			String.valueOf(getDay()));
			Intent intent = new Intent(this, SelectLocation.class);
			intent.putExtra("data", mPsd);
			startActivityForResult(intent, 0);
			/*Bundle bd = new Bundle();
			bd.putBoolean(Constants.IS_NEW_PROFILE, true);
			bd.putSerializable(Constants.PROFILE_DATA_KEY, mPsd);
			intent.putExtras(bd);
			startActivity(intent);*/
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if((requestCode == 0)&&(resultCode == 1)){
		//	Location loc = (Location) data.getSerializableExtra("loc");
			Location loc = null;
			if(data.getSerializableExtra("data") == null){
				System.out.println(" data is null");
				mPsd = new ProfileStaticData();
			}else{
				System.out.println(" data not null");	
				mPsd = (ProfileStaticData) data.getSerializableExtra("data");
			}
			if(data.getParcelableExtra("loc") == null){
				System.out.println(" is null in condition");
			}else{
				System.out.println(" condition not null");
				loc = data.getParcelableExtra("loc");
				mPsd.setmLat(String.valueOf((loc.getLatitude())));
				mPsd.setmLog(String.valueOf((loc.getLongitude())));
				
				System.out.println(loc.getLatitude()+"---------------khalid----------"+loc.getLongitude());
				
				mTxt_locPts.setText("Latitude :"
						+ mPsd.getmLat()+"\n"
						+ ", Longitudeg:"
						+ mPsd.getmLog());
			}
			
			
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		System.out.println(" on destroy called >>>>>>>>>>>>>>>>>");
	}

	@Override
	protected void onStart() {
		super.onStart();
	//	System.out.println(" onStart called >>>>>>>>>>>>>>>>>");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		System.out.println(" onRestart called >>>>>>>>>>>>>>>>>");

	}

	@Override
	protected void onStop() {
		super.onStop();
//		System.out.println(" onStop called >>>>>>>>>>>>>>>>>");
	}

	@Override
	protected void onPause() {
		super.onPause();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Utility.printProfileData(mPsd);
		if(mPsd.getmDays() != null){
			System.out.println(" the lat is "+mPsd.getmDays());
			setCheckBoxStst(Integer.parseInt(mPsd.getmDays()));
		}		
		if (mPsd.getmLat() == null) {
			mTxt_locPts.setText("Lat : " + " Log: ");
		} else {
			mTxt_locPts.setText("Lat :"
					+ mPsd.getmLat()
					+ ", Log:"
					+ mPsd.getmLog());
		}
		if(mPsd.getmTime() != null){
			String time = mPsd.getmTime();
			String[] times = time.split(":");
			System.out.println("hour===="+times[0]);
			System.out.println("min===="+times[1]);
			mTp.setCurrentHour(Integer.parseInt(times[0]));
			mTp.setCurrentMinute(Integer.parseInt(times[1]));
		}else{
			System.out.println(">>>>getmTime is null>>>>>>>>>");
		}		
	}
	
	private void chkDay(int day,boolean val){
		
		if (day == s_MONDAY) {
			mM = val;
		} else if (day == s_TUESDAY) {
			mT = val;
		} else if (day == s_WEDNESDAY) {
			mW = val;
		} else if (day == s_THURSDAY) {
			mTh = val;
		} else if (day == s_FRIDAY) {
			mFr = val;
		} else if (day == s_SATURDAY) {
			mSa = val;
		} else if (day == s_SUNDAY) {
			mSu = val;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		 if(buttonView == mChkbx_mon){
			 chkDay(s_MONDAY,isChecked); 
		  }else if(buttonView == mChkbx_tue){
			  chkDay(s_TUESDAY,isChecked);
		  }else if(buttonView == mChkbx_wed){ 
			  chkDay(s_WEDNESDAY,isChecked); 
		  }else if(buttonView == mChkbx_thu){ 
			  chkDay(s_THURSDAY,isChecked); 
		  }else if(buttonView == mChkbx_fri){ 
			  chkDay(s_FRIDAY,isChecked); 
		  }else if(buttonView == mChkbx_sat){ 
			  chkDay(s_SATURDAY,isChecked); 
		  }else if(buttonView == mChkbx_sun){ 
			  chkDay(s_SUNDAY,isChecked); 
		  }	 

		}
	
}


