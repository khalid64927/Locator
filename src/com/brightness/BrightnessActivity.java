package com.brightness;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.locator.ui.R;
import com.locator.utility.Constants;

public class BrightnessActivity extends Activity {

	private static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
	private static final int SCREEN_MODE_MANUAL = 0;
	private static final int SCREEN_MODE_AUTO = 1;
	private static final int DEFAULT_BRITHTNESS_LEVEL = 50;
	private int mBrightness; // full brightness level value
	private static final String TAG = "BrightnessActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Intent brightnessIntent = this.getIntent();
		mBrightness = brightnessIntent.getIntExtra(Constants.BRIGHTNESS_LEVEL,
				DEFAULT_BRITHTNESS_LEVEL);
		toggleBrightness();
	}

	private void toggleBrightness() {
		Log.i(TAG, "toggleBrightness");
		System.out
				.println(" brigtness receivesd is >>>>>>>>>>>>" + mBrightness);
		try {
			ContentResolver cr = getContentResolver();
			boolean autoBrightOn = (Settings.System.getInt(cr,
					SCREEN_BRIGHTNESS_MODE, -1) == SCREEN_MODE_AUTO);
			
			if(autoBrightOn)
            {
                    Settings.System.putInt(cr, SCREEN_BRIGHTNESS_MODE, SCREEN_MODE_MANUAL);
                    Toast.makeText(this, "Disabling 'Automatic Brightness'", Toast.LENGTH_SHORT).show();
            }                    
        Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, mBrightness);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = mBrightness / 255.0f;
        getWindow().setAttributes(lp);
    } catch (Exception e) {
        Log.d("Bright", "toggleBrightness: " + e);
    }
    
    final Activity activity = this;
    Thread t = new Thread(){
            public void run()
            {
                    try {
                                    sleep(500);
                            } catch (InterruptedException e) {}
                    activity.finish();
            }
    };
    t.start();
}
}
