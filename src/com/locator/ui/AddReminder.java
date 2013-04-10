package com.locator.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.locator.components.CalanderHelperClass;
import com.locator.components.CalendarEvents;
import com.locator.utility.Constants;
import com.locator.utility.Utility;

public class AddReminder extends Activity implements OnCheckedChangeListener,
		OnClickListener, OnItemSelectedListener {

	private Button mBtnStDt;
	private Button mBtnStTm;
	private Button mBtnEndDt;
	private Button mBtnEndTm;
	private Button mBtnStDtAllDay;
	private Button mBnEtDtAllDay;
	private Button mBtnSelLoc;
	private Button mBtnSubmit;
	private Button mBtnCancle;
	private CheckBox mCheckkBoxAllDay;
	private Spinner mSpinnerTimeZone;
	private Spinner mSpinnerReminders;
	private Spinner mSpinnerRepetitions;
	private EditText mEdtWhere;
	private EditText mEdtWhat;
	private EditText mEdtDesc;
	private RelativeLayout mRelLayTimeZone;

	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinutes;
	private String mStartShedule;
	private String mEndShedule;
	private String mStartDate;
	private String mEndDate;
	private String mStartTime;
	private String mEndTime;
	private boolean mIs24AllDay;
	private String mTimeZone;
	private String mWhere;
	private String mWhat;
	private String mDescription;
	private int mReminders;
	private String mRepititions;

	private long mStartD;
	private long mEndD;

	private static final String TAG = "AddReminder";
	static final int START_DATE_DIALOG_ID = 0;
	static final int END_DATE_DIALOG_ID = 1;
	static final int START_TIME_DIALOG_ID = 2;
	static final int END_TIME_DIALOG_ID = 3;
	static final int START_ALL_DAY_DIALOG_ID = 4;
	static final int END_ALL_DAY_DIALOG_ID = 5;
	private String[] mTZ;
	private ArrayList<String> mTZ1;
	CalendarEvents calQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_reminder);

		mBtnStDt = (Button) findViewById(R.id.btn_start_date);
		mBtnStTm = (Button) findViewById(R.id.btn_start_time);
		mBtnEndDt = (Button) findViewById(R.id.btn_end_date);
		mBtnEndTm = (Button) findViewById(R.id.btn_end_time);
		mBtnSelLoc = (Button) findViewById(R.id.btn_where);
		mBtnStDtAllDay = (Button) findViewById(R.id.btn_start_date_all_day);
		mBnEtDtAllDay = (Button) findViewById(R.id.btn_end_date_all_day);
		mBtnSubmit = (Button)findViewById(R.id.submit);
		mBtnCancle = (Button)findViewById(R.id.cancle);
		
		mSpinnerTimeZone = (Spinner) findViewById(R.id.spinner_timezone);
		mSpinnerReminders = (Spinner) findViewById(R.id.spinner_reminder_time);
		mSpinnerRepetitions = (Spinner) findViewById(R.id.spinner_repitions);
		mCheckkBoxAllDay = (CheckBox) findViewById(R.id.checkBox_allday);
		mEdtWhere = (EditText) findViewById(R.id.editTextAddress);
		mEdtWhat = (EditText) findViewById(R.id.editTextWhat);
		mEdtDesc = (EditText) findViewById(R.id.editTextDescription);
		mRelLayTimeZone = (RelativeLayout)findViewById(R.id.relLay_timezone);

		mBtnStDt.setOnClickListener(this);
		mBtnStTm.setOnClickListener(this);
		mBtnEndDt.setOnClickListener(this);
		mBtnEndTm.setOnClickListener(this);
		mBtnSelLoc.setOnClickListener(this);
		mBtnSubmit.setOnClickListener(this);
		mBtnCancle.setOnClickListener(this);
		mBtnStDtAllDay.setOnClickListener(this);
		mBnEtDtAllDay.setOnClickListener(this);
		mCheckkBoxAllDay.setOnCheckedChangeListener(this);
		mSpinnerTimeZone.setOnItemSelectedListener(this);

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinutes = c.get(Calendar.MINUTE);

		updateDisplay();
		setTimeZonesSpinner();
		setReminderSpinner();
		setRepetitionSpinner();

	}

	private void setTimeZonesSpinner() {
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mTZ = TimeZone.getAvailableIDs();
		mTZ1 = new ArrayList<String>();
		for (int i = 0; i < mTZ.length; i++) {
			if (!(mTZ1.contains(TimeZone.getTimeZone(mTZ[i]).getDisplayName()))) {
				mTZ1.add(TimeZone.getTimeZone(mTZ[i]).getDisplayName());
				System.out.println(" display name"
						+ TimeZone.getTimeZone(mTZ[i]).getDisplayName()
						+ " id of this :" + mTZ[i]);
			}
		}
		for (int i = 0; i < mTZ1.size(); i++) {
			adapter.add(mTZ1.get(i));
		}
		mSpinnerTimeZone.setAdapter(adapter);

		for (int i = 0; i < mTZ1.size(); i++) {
			if (mTZ1.get(i).equals(TimeZone.getDefault().getDisplayName())) {
				mSpinnerTimeZone.setSelection(i);
				System.out.println("display name is >>>>>" + mTZ1.get(i)
						+ " and id is " + mTZ[i]);
			}
		}

	}

	private void setReminderSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.reminder_entries,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerReminders.setAdapter(adapter);
	}

	private void setRepetitionSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.repetition_entries,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerRepetitions.setAdapter(adapter);
	}

	private void getData() {
		Date date = null;
		Date endDate = null;
		String pattern = null;
		java.text.DateFormat sdf = null;
		// get selected timezone id
		int index = (int) mSpinnerTimeZone.getSelectedItemId();
		mTimeZone = mTZ[index];
		System.out.println(" getData time display name is " + mTZ1.get(index)
				+ " and id is " + mTZ[index]);
		// get start date
		if (mIs24AllDay) {
			// get start time
			pattern = "dd/mm/yyyy";
			sdf = new SimpleDateFormat(pattern);
			sdf.setTimeZone(TimeZone.getTimeZone(mTimeZone));
			try {
				date = sdf.parse(mStartShedule);
			} catch (ParseException e) {
				Log.e(TAG, "Exception caught in parsing");
				e.printStackTrace();
			}
			String sDate = sdf.format(date);
			System.out.println(" the start allday date is :" + sDate);
			// get end time
			try {
				endDate = sdf.parse(mEndShedule);
			} catch (ParseException e) {
				Log.e(TAG, "Exception caught in parsing");
				e.printStackTrace();
			}
			String sEDate = sdf.format(endDate);
			System.out.println(" the end allday  date is :" + sEDate);
			System.out.println(" >>end date in millis" + endDate.getTime());
		} else {
			String[] str = mStartDate.split("/");
			String[] str2 = mStartTime.split(":");
			String str3 = Utility.getUtility().adjustValues(str[2])
					+ Utility.getUtility().adjustValues(str[1])
					+ Utility.getUtility().adjustValues(str[0])
					+ Utility.getUtility().adjustValues(str2[0])
					+ Utility.getUtility().adjustValues(str2[1]);
			System.out.println(" pattern start " + str3 + ">>>>>>|" + str[2]
					+ " / " + str[1] + " / " + str[0] + " =" + str2[0] + " : "
					+ str2[1]);
			pattern = "yyyyMMddHHmm";
			sdf = new SimpleDateFormat(pattern);
			sdf.setTimeZone(TimeZone.getTimeZone(mTimeZone));
			try {
				date = sdf.parse(str3);
			} catch (ParseException pe) {
				pe.printStackTrace();
			}
			String sDate = sdf.format(date);
			System.out.println(" the start date is :" + sDate);
			// get end time
			str = mEndDate.split("/");
			str2 = mEndTime.split(":");
			str3 = Utility.getUtility().adjustValues(str[2])
					+ Utility.getUtility().adjustValues(str[1])
					+ Utility.getUtility().adjustValues(str[0])
					+ Utility.getUtility().adjustValues(str2[0])
					+ Utility.getUtility().adjustValues(str2[1]);
			System.out.println(" pattern end " + str3 + ">>>>>>|" + str[2]
					+ " / " + str[1] + " / " + str[0] + " =" + str2[0] + " : "
					+ str2[1]);
			// str3 = str[2]+str[1]+str[0]+str2[0]+str2[1];
			try {
				endDate = sdf.parse(str3);
			} catch (ParseException e) {
				Log.e(TAG, "Exception caught in parsing");
				e.printStackTrace();
			}
			String sEDate = sdf.format(endDate);
			System.out.println(" the end date is :" + sEDate);
			System.out.println(" >>end date in millis" + endDate.getTime());
		}
		mStartD = date.getTime();
		mEndD = endDate.getTime();
		System.out.println(" end date in millis" + mEndD);

		// get Title
		mWhat = mEdtWhat.getText().toString();
		// get address
		mWhere = mEdtWhere.getText().toString();
		// get description
		mDescription = mEdtDesc.getText().toString();

		// get reminder selection
		int remPos = mSpinnerReminders.getSelectedItemPosition();

		// get repetition selection

	}

	private void makeNewCalendarEntry() {
		getData();
		
		int currentApiLevel = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		int remMinutes = getReminderMinutes(mSpinnerReminders.getSelectedItemPosition());
		calQuery = new CalendarEvents(this);
		calQuery.insertEvent(3, mWhat, mDescription, mWhere, mTimeZone, mStartD, mEndD, mIs24AllDay, 1, 0, 0, 1, remMinutes, currentApiLevel);

		System.out.println("what " + mWhat + " des " + mDescription + "where"
				+ mWhere + "start date" + mStartD + " end date" + mEndD);
		ContentValues event = new ContentValues();

		/*event.put(Events.CALENDAR_ID, 1);
		event.put(Events.TITLE, mWhat);
		event.put(Events.DESCRIPTION, mDescription);
		event.put(Events.EVENT_LOCATION, mWhere);
		event.put(Events.EVENT_TIMEZONE, mTimeZone);

		long startTime = System.currentTimeMillis() + 1000 * 60 * 60;
		long endTime = System.currentTimeMillis() + 1000 * 60 * 60 * 2;

		System.out.println("currentTimeMillis" + System.currentTimeMillis());
		System.out.println(" added " + mStartDate);

		event.put("dtstart", mStartD);
		event.put("dtend", mEndD);

		
		 * if(mIs24HourFormat){ event.put("allDay", 1); }else{
		 
		event.put("allDay", 0);
		// }
		event.put("eventStatus", 1);
		//event.put("visibility", 0);
		event.put("transparency", 0);
		event.put("hasAlarm", 1); // 0 for false, 1 for true

		String uriString = getCalendarUriBase();
		Uri uri = Uri.parse(uriString + "events");
		Uri insertedUri = getContentResolver().insert(uri, event);

		// reminder insert
		remMinutes = getReminderMinutes(mSpinnerReminders.getSelectedItemPosition());
		Uri REMINDERS_URI = Uri.parse(uriString + "reminders");
		ContentValues values = new ContentValues();
		values.put("event_id", Long.parseLong(insertedUri.getLastPathSegment()));
		values.put("method", 1);
		values.put("minutes", remMinutes);
		getContentResolver().insert(REMINDERS_URI, values);*/

	//	return insertedUri;
	}

	private int getReminderMinutes(int pos){
		switch(pos){
		case 0:mReminders = 0;
		break;
		case 1:mReminders = 10;
		break;
		case 2:mReminders = 15;
		break;
		case 3:mReminders = 25;
		break;
		case 4:mReminders = 30;
		break;
		case 5:mReminders = 45;
		break;
		case 6:mReminders = 60;
		break;
		case 7:mReminders = 120;
		break;
		case 8:mReminders = 180;
		break;
		case 9:mReminders = 720;
		break;
		case 10:mReminders = 10080;
		break;
		default:mReminders = 0;
		break;
		}	
		return mReminders;
	}
	private String getCalendarUriBase() {
		String calendarUriBase = null;
		Uri calendars = Uri.parse("content://calendar/calendars");
		Cursor cursor = null;
		try {
			cursor = getContentResolver().query(calendars, null, null, null, null);
		} catch (Exception e) {
			Utility.getUtility().printLog(Constants.ERROR, TAG, "exception with "+calendars+" uri");
		}

		if (cursor != null) {
			calendarUriBase = "content://calendar/";
			cursor.close();
		} else {
			calendars = Uri.parse("content://com.android.calendar/calendars");
			try {
				cursor = getContentResolver().query(calendars, null, null, null, null);
			} catch (Exception e) {
				Utility.getUtility().printLog(Constants.ERROR, TAG, "exception with "+calendars+" uri");
			}

			if (cursor != null) {
				calendarUriBase = "content://com.android.calendar/";
			}

		}
		System.out.println(" the documented uri is "+Events.CONTENT_URI);

		System.out.println(" getCalendarUriBase is "+calendarUriBase);
		return calendarUriBase;
	}

	private void updateDisplay() {
		StringBuilder currentDate = new StringBuilder();
		StringBuilder currentTime = new StringBuilder();
		StringBuilder currentTimeExtended = new StringBuilder();
		// Month is 0 based so add 1
		currentDate.append(mDay).append("/").append(mMonth + 1).append("/")
				.append(mYear);

		currentTime.append(mHour).append(":").append(mMinutes);

		currentTimeExtended.append(mHour + 1).append(":").append(mMinutes);

		mBtnStDt.setText(currentDate);
		mBtnStTm.setText(currentTime);
		mBtnEndDt.setText(currentDate);
		mBtnEndTm.setText(currentTimeExtended);

		mBnEtDtAllDay.setText(currentDate);
		mBtnStDtAllDay.setText(currentDate);

		mStartDate = currentDate.toString();
		mEndDate = currentDate.toString();
		mStartTime = currentTime.toString();
		mEndTime = currentTimeExtended.toString();
		mStartShedule = currentDate.toString();
		mEndShedule = currentDate.toString();
		mIs24AllDay = mCheckkBoxAllDay.isChecked();
	}

	private void updateStartDate() {
		StringBuilder startDate = new StringBuilder();
		startDate.append(mDay).append("/").append(mMonth + 1).append("/")
				.append(mYear);

		mBtnStDt.setText(startDate);
		mStartDate = startDate.toString();
	}

	private void upDateEndDate() {
		StringBuilder endDate = new StringBuilder();
		endDate.append(mDay).append("/").append(mMonth + 1).append("/")
				.append(mYear);

		mBtnEndDt.setText(endDate);
		mEndDate = endDate.toString();
	}

	private void upDateStartTime() {
		StringBuilder startTime = new StringBuilder();
		startTime.append(mHour).append(":").append(mMinutes);

		mBtnStTm.setText(startTime);
		mStartTime = startTime.toString();
	}

	private void upDateEndTime() {
		StringBuilder endTime = new StringBuilder();
		endTime.append(mHour).append(":").append(mMinutes);
		mBtnEndTm.setText(endTime);
		mEndTime = endTime.toString();
	}

	private void upDateStartAllDayEvent() {
		StringBuilder StartAllDayEvent = new StringBuilder();
		StartAllDayEvent.append(mDay).append("/").append(mMonth + 1)
				.append("/").append(mYear);

		mBtnStDtAllDay.setText(StartAllDayEvent);
		mStartShedule = StartAllDayEvent.toString();
	}

	private void upDateEndAllDayEvent() {
		StringBuilder EndAllDayEvent = new StringBuilder();
		EndAllDayEvent.append(mDay).append("/").append(mMonth + 1).append("/")
				.append(mYear);
		mBnEtDtAllDay.setText(EndAllDayEvent);
		mEndShedule = EndAllDayEvent.toString();
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateStartDate();
		}
	};

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			upDateEndDate();
		}
	};

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mStartAllDateSetListner = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			upDateStartAllDayEvent();
		}
	};

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mEndALLDateSetListner = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			upDateEndAllDayEvent();
		}
	};

	private TimePickerDialog.OnTimeSetListener mStartTimeSetListner = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinutes = minute;
			upDateStartTime();
		}
	};

	private TimePickerDialog.OnTimeSetListener mEndTimeSetListner = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinutes = minute;
			upDateEndTime();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case START_DATE_DIALOG_ID:
			return new DatePickerDialog(this, mStartDateSetListener, mYear,
					mMonth, mDay);

		case END_DATE_DIALOG_ID:
			return new DatePickerDialog(this, mEndDateSetListener, mYear,
					mMonth, mDay);
		case START_TIME_DIALOG_ID:
			return new TimePickerDialog(this, mStartTimeSetListner, mHour,
					mMinutes, true);

		case END_TIME_DIALOG_ID:
			return new TimePickerDialog(this, mEndTimeSetListner, mHour,
					mMinutes, true);

		case START_ALL_DAY_DIALOG_ID:
			return new DatePickerDialog(this, mStartAllDateSetListner, mYear,
					mMonth, mDay);

		case END_ALL_DAY_DIALOG_ID:
			return new DatePickerDialog(this, mEndALLDateSetListner, mYear,
					mMonth, mDay);
		}
		return null;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mIs24AllDay = isChecked;
		if (isChecked) {
			mBtnStDt.setVisibility(View.INVISIBLE);
			mBtnStTm.setVisibility(View.INVISIBLE);
			mBtnEndDt.setVisibility(View.INVISIBLE);
			mBtnEndTm.setVisibility(View.INVISIBLE);
			mBtnStDtAllDay.setVisibility(View.VISIBLE);
			mBnEtDtAllDay.setVisibility(View.VISIBLE);
			mRelLayTimeZone.setVisibility(View.GONE);
		} else {
			mRelLayTimeZone.setVisibility(View.VISIBLE);
			mBtnStDt.setVisibility(View.VISIBLE);
			mBtnStTm.setVisibility(View.VISIBLE);
			mBtnEndDt.setVisibility(View.VISIBLE);
			mBtnEndTm.setVisibility(View.VISIBLE);
			mBtnStDtAllDay.setVisibility(View.INVISIBLE);
			mBnEtDtAllDay.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onClick(View view) {
		if (view == mBtnStDt) {
			showDialog(START_DATE_DIALOG_ID);
		} else if (view == mBtnEndDt) {
			showDialog(END_DATE_DIALOG_ID);
		} else if (view == mBtnStTm) {
			showDialog(START_TIME_DIALOG_ID);
		} else if (view == mBtnEndTm) {
			showDialog(END_TIME_DIALOG_ID);
		} else if (view == mBtnSelLoc) {
			Intent locintent = new Intent(this, SelectLocation.class);
			locintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(locintent, 0);
		} else if (view == mBtnStDtAllDay) {
			showDialog(START_ALL_DAY_DIALOG_ID);
		} else if (view == mBnEtDtAllDay) {
			showDialog(END_ALL_DAY_DIALOG_ID);
		}else if(view == mBtnSubmit){
			makeNewCalendarEntry();
			Intent intent = new Intent(this, Reminder.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}else if(view == mBtnCancle){
			Intent intent = new Intent(this, Reminder.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((resultCode == 1) && (requestCode == 0)) {
			Location loc = (Location) data.getParcelableExtra("loc");
			if (data.getParcelableExtra("loc") != null) {
				System.out.println(" >>>>>>>>>>>>>>>>onactivty result");
				Utility.getAddressFromLocation(loc.getLatitude(),
						loc.getLongitude(), getApplicationContext(),
						new GeocoderHandler());
				
			}else{
				System.out.println(" null >>>>>>>>>>>>>>>>>>>>>>>");
			}
			

		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int val,
			long arg3) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@SuppressLint("HandlerLeak")
	private class GeocoderHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			String address = null;
			switch (message.what) {
			case 1:
				Bundle bundle = message.getData();
				address = bundle.getString("address");
				if (address == null) {
					address = "address not avialable for this location";
					System.out.println(address);
				} else {
					mEdtWhere.setText(address);
				}
				break;
			default:
				address = null;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, Reminder.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);

	}
}
