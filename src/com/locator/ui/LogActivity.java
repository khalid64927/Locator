package com.locator.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

public class LogActivity extends Activity{

	private TextView logText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log);
		
		logText = (TextView) findViewById(R.id.textView1);
		File log = new File(Environment.getExternalStorageDirectory(), "/Locator/locationlog.txt");
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(log));
			String line = null;
			while((line = br.readLine()) != null){
				sb.append(line);
				sb.append("\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logText.setText(sb);
		
	}
	
	
	
}
