package com.locator.ui;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Reminder extends Activity implements OnClickListener, OnItemClickListener {

	private Button mAdd;
	private ListView mEventsList;
	private TextView mNoReminders;
	private ArrayAdapter<String> mArrAdapter;
	private ArrayList<String> mEvents;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminder);

		mAdd = (Button) findViewById(R.id.add);
		mEventsList = (ListView)findViewById(R.id.listView1);
		mNoReminders = (TextView)findViewById(R.id.textView1);
		mEventsList.setOnItemClickListener(this);
		mAdd.setOnClickListener(this);

		mEvents = new ArrayList<String>();
		String uriString = "content://com.android.calendar/calendars";
	//	Log.i("INFO", "Reading content from " + uriString);
	//	readContent(uriString);
		uriString = "content://com.android.calendar/events";
		Log.i("INFO", "Reading content from " + uriString);
		readContent(uriString);
		
		
		if(mEvents.size()<1){
			mNoReminders.setVisibility(View.VISIBLE);
			mEventsList.setVisibility(View.INVISIBLE);
		}else{
			mNoReminders.setVisibility(View.INVISIBLE);
			mEventsList.setVisibility(View.VISIBLE);
		}
		mArrAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mEvents);
		mEventsList.setAdapter(mArrAdapter);

	}

	private void readContent(String uriString) {

		Uri uri = Uri.parse(uriString);
		Cursor cursor = this.getContentResolver().query(uri, null, null, null,
				null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			String columnNames[] = cursor.getColumnNames();
			String value = "";
			String colNamesString = "";
			
			do {
				value = "";

				for (String colName : columnNames) {
					if(colName.equals("title")){
						mEvents.add(cursor.getString(cursor.getColumnIndex(colName)));
					}
					/*value += colName + " = ";
					value += cursor.getString(cursor.getColumnIndex(colName))
							+ " ||";*/
				}

				Log.e("INFO : ", value);
			} while (cursor.moveToNext());

		}
		cursor.close();

	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == mAdd) {

			/*Calendar cal = Calendar.getInstance();
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			intent.putExtra("beginTime", cal.getTimeInMillis());
			intent.putExtra("allDay", false);
			intent.putExtra("rrule", "FREQ=DAILY");
			intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
			intent.putExtra("title", "A Test Event from android app");
			startActivity(intent);*/

			
			 Intent intent = new Intent(this, AddReminder.class);
			 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 startActivity(intent);
			 finish();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}
