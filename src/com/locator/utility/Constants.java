package com.locator.utility;

public class Constants {

	// general settings screen constants
	public static final int CAMERA_PIC_REQUEST = 0;
	public static final int SELECT_PICTURE = 1;
	public static final int AUDIO_FILE_REQUEST = 2;
	public static final int SELECT_AUDIO_FILE = 3;
	
	public static final int INFO = 0;
	public static final int DATA = 1;
	public static final int VERBEROS = 2;
	public static final int WARNING = 3;
	public static final int ERROR = 4;
	public static final int WHAT_A_TERRIBLE_FAILURE = 5;
	
	//intent keys
	public static final String PROFILE_DATA_KEY = "profile_data";
	public static final String IS_NEW_PROFILE = "is_new_profile";
	public static final String NAVIGATION_FLAG = "navigation_flag";
	public static final String BRIGHTNESS_LEVEL = "brightness_level";
	public static final String PROX_ALERT_INTENT = "com.khalid.locator.proximityAlert";
	public static final double DISTANCE_RANGE = 200.0000;
	public static final int INTERNAL_MAX_BRIGHTNESS_VALUE = 255;
	public static final int LOG_DISTANCE_RANGE = 500;
	public static boolean sNavigation = true;
	public static boolean isDeleted  = false;
	public static boolean mIsNewProfile = false;
	public static final int TWO_MINUTES = 2 * 60 * 1000;
	public static final int PERIOD = 60000; // alarm frequency 1 minutes
	public static String sCurrentSetProfileID;
	
	public static final int s_MONDAY = 01;
	public static final int s_TUESDAY = 02;
	public static final int s_WEDNESDAY = 04;
	public static final int s_THURSDAY = 8;
	public static final int s_FRIDAY = 16;
	public static final int s_SATURDAY = 32;
	public static final int s_SUNDAY = 64;
	public static final int s_ALL = 128;
	
}
