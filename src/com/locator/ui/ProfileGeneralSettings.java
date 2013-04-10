package com.locator.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.locator.components.ProfileStaticData;
import com.locator.utility.Constants;
import com.locator.utility.Utility;

public class ProfileGeneralSettings extends Activity implements
		OnClickListener, OnCheckedChangeListener, OnSeekBarChangeListener{

	private CheckBox mChkNot;
	private CheckBox mChkVib;
	private CheckBox mChkRgTn;
	private CheckBox mChkWall;
	private SeekBar mSbRvLvl;
	private SeekBar mSbBrightness;
	private ImageView mIvWall;
	private TextView mTvWallpaper;
	private TextView mTvRingtone;
	private Button mBtnWall, mBtnRgtn;
	private AudioManager mAM;
	private int mRingerVol;
	private Uri mPhotoUri;
	private Uri mAudioUri;
	private ProfileStaticData mPsd;
	private Bitmap mBmap = null;
	private boolean mIsNewProfile;
	private int mColumnIndex;
	private String mFilePath;
	private String mImageName;
	
	private String mSelWall;
	private String mSelRT;
	private String mBrightness;
	private String mNotification;
	private String mVibrate;
	private static final String TAG = "ProfileGeneralSettings";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_settings);

		mChkNot = (CheckBox) findViewById(R.id.checkBox1);
		mChkVib = (CheckBox) findViewById(R.id.checkBox2);
		// mChkRgv = (CheckBox) findViewById(R.id.checkBox3);
		mChkWall = (CheckBox) findViewById(R.id.checkBox5);
		mChkRgTn = (CheckBox) findViewById(R.id.checkBox4);
		mBtnWall = (Button) findViewById(R.id.button1);
		mBtnRgtn = (Button) findViewById(R.id.button2);
		mSbRvLvl = (SeekBar) findViewById(R.id.seekBar1);
		mSbBrightness = (SeekBar) findViewById(R.id.seekBarBrightness);
		mTvWallpaper = (TextView) findViewById(R.id.textView1);
		mTvRingtone = (TextView) findViewById(R.id.textView2);
		mIvWall = (ImageView) findViewById(R.id.imageView1);
		mIvWall.setImageResource(R.drawable.ic_launcher);
		mSbBrightness.setOnSeekBarChangeListener(this);

		mSbRvLvl.setOnSeekBarChangeListener(this);
		mBtnWall.setOnClickListener(this);
		mBtnRgtn.setOnClickListener(this);
		mChkWall.setOnCheckedChangeListener(this);
		mChkRgTn.setOnCheckedChangeListener(this);
		mChkNot.setOnCheckedChangeListener(this);
		mChkVib.setOnCheckedChangeListener(this);

		mAM = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		setButtonsW(mChkWall.isChecked());
		setButtonsR(mChkRgTn.isChecked());

		Bundle bd = getIntent().getExtras();
		mIsNewProfile = bd.getBoolean(Constants.IS_NEW_PROFILE);
		System.out.println(" the paseed value is >>>>>>>>>" + mIsNewProfile);
		mPsd = (ProfileStaticData) bd
				.getSerializable(Constants.PROFILE_DATA_KEY);

	}

	@Override
	public void onClick(View v) {
		if (v == mBtnRgtn) {
			if (mChkRgTn.isChecked()) {
				AlertDialog.Builder getImageFrom = new AlertDialog.Builder(this);
				getImageFrom.setTitle("Select:");
				final CharSequence[] opsChars = {
						getResources().getString(R.string.str_take_pic),
						getResources().getString(R.string.str_choose_gallery) };
				getImageFrom.setItems(opsChars,
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									Intent intent = new Intent(
											MediaStore.Audio.Media.RECORD_SOUND_ACTION);
									startActivityForResult(intent,
											Constants.AUDIO_FILE_REQUEST);
								} else if (which == 1) {
									System.out.println("Inside gallery click");
									Intent intent = new Intent();
									intent.setType("audio/*");
									intent.setAction(Intent.ACTION_GET_CONTENT);
									startActivityForResult(
											Intent.createChooser(
													intent,
													getResources()
															.getString(
																	R.string.str_choose_gallery)),
											Constants.SELECT_AUDIO_FILE);
								}
								dialog.dismiss();
							}
						});
				getImageFrom.show();
			}
		}
		if (v == mBtnWall) {
			if (mChkWall.isChecked()) {
				AlertDialog.Builder getImageFrom = new AlertDialog.Builder(this);
				getImageFrom.setTitle("Select:");
				final CharSequence[] opsChars = {
						getResources().getString(R.string.str_take_pic),
						getResources().getString(R.string.str_choose_gallery) };
				getImageFrom.setItems(opsChars,
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									Intent intent = new Intent(
											"android.media.action.IMAGE_CAPTURE");
									startActivityForResult(intent,
											Constants.CAMERA_PIC_REQUEST);
								} else if (which == 1) {
									System.out.println("Inside gallery click");
									Intent intent = new Intent();
									intent.setType("image/*");
									intent.setAction(Intent.ACTION_GET_CONTENT);
									startActivityForResult(
											Intent.createChooser(
													intent,
													getResources()
															.getString(
																	R.string.str_choose_gallery)),
											Constants.SELECT_PICTURE);
								}
								dialog.dismiss();
							}
						});
				getImageFrom.show();
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("on Activity result");
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println("Request code : " + requestCode);
		if (requestCode == Constants.AUDIO_FILE_REQUEST
				&& resultCode == RESULT_OK) {

			if (data != null) {
				mAudioUri = data.getData();
				if (mAudioUri != null) {
					String[] mFilePathColumn = { MediaStore.Audio.Media.DATA,
							MediaStore.Audio.Media.TITLE };
					Cursor cursor = getContentResolver().query(mAudioUri,
							mFilePathColumn, null, null, null);
					cursor.moveToFirst();
					mColumnIndex = cursor.getColumnIndex(mFilePathColumn[0]);
					mFilePath = cursor.getString(mColumnIndex);
					System.out.println(" the audio uri is ???????????????"
							+ mAudioUri);

					Ringtone r = RingtoneManager.getRingtone(this, mAudioUri);
					System.out
							.println(" the audio file created is >>>>>>>>>>>>>>>>>"
									+ r.getTitle(this));
					mTvRingtone.setText(r.getTitle(this));
					mSelRT = mAudioUri.toString();
					mPsd.setmRingTone(mSelRT);
					cursor.close();
				}
			}

		} else if (requestCode == Constants.SELECT_AUDIO_FILE
				&& resultCode == RESULT_OK) {
			if (data != null) {
				mAudioUri = data.getData();
				if (mAudioUri != null) {

					String[] mFilePathColumn = { MediaStore.Audio.Media.DATA,
							MediaStore.Audio.Media.TITLE };
					Cursor cursor = getContentResolver().query(mAudioUri,
							mFilePathColumn, null, null, null);
					cursor.moveToFirst();
					mColumnIndex = cursor.getColumnIndex(mFilePathColumn[0]);
					mFilePath = cursor.getString(mColumnIndex);

					// RingtoneManager.setActualDefaultRingtoneUri(this,
					// RingtoneManager.TYPE_NOTIFICATION, mAudioUri);

					Ringtone r = RingtoneManager.getRingtone(this, mAudioUri);
					System.out.println(" the audio uri is ???????????????"
							+ mAudioUri);
					System.out.println(" the filepath uri is ???????????????"
							+ mFilePath);
					System.out
							.println(" the audio file selected is >>>>>>>>>>>>>>>>>"
									+ r.getTitle(this));
					mTvRingtone.setText(r.getTitle(this));
					mSelRT = mAudioUri.toString();
					mPsd.setmRingTone(mSelRT);
					cursor.close();

				}
			}
		} else if (requestCode == Constants.CAMERA_PIC_REQUEST
				&& resultCode == RESULT_OK) {

			mPhotoUri = data.getData();
			if (mPhotoUri != null) {
				try {
					String[] mFilePathColumn = { MediaStore.Images.Media.DATA,
							MediaStore.Images.Media.TITLE };
					Cursor cursor = getContentResolver().query(mPhotoUri,
							mFilePathColumn, null, null, null);

					cursor.moveToFirst();
					mColumnIndex = cursor.getColumnIndex(mFilePathColumn[0]);
					System.out
							.println(" the column index value is>>>>>>>>>>>>>>>>> "
									+ mColumnIndex
									+ "and size of mFilePathColumn string array is "
									+ mFilePathColumn.length);
					mFilePath = cursor.getString(mColumnIndex);
					mImageName = cursor.getString(cursor
							.getColumnIndex(mFilePathColumn[1]));
					System.out.println(" the title of the image is >>"
							+ mImageName);
					cursor.close();

					mBmap = BitmapFactory.decodeFile(mFilePath);
					mBmap = Bitmap.createScaledBitmap(mBmap, 420, 800, false);
					// set this image view source as this bitmap
					mIvWall.setImageBitmap(mBmap);
					mTvWallpaper.setText(mImageName);
					mSelWall = mPhotoUri.toString();
					mPsd.setmWall(mSelWall);
					cursor.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (requestCode == Constants.SELECT_PICTURE
				&& resultCode == RESULT_OK) {
			mPhotoUri = data.getData();
			if (mPhotoUri != null) {
				try {
					String[] mFilePathColumn = { MediaStore.Images.Media.DATA,
							MediaStore.Images.Media.TITLE };
					System.out.println(" the image file path>>>>>>>>>>>>>>>>>"
							+ mFilePathColumn);
					Cursor cursor = getContentResolver().query(mPhotoUri,
							mFilePathColumn, null, null, null);
					cursor.moveToFirst();
					mColumnIndex = cursor.getColumnIndex(mFilePathColumn[0]);
					mFilePath = cursor.getString(mColumnIndex);
					mImageName = cursor.getString(cursor
							.getColumnIndex(mFilePathColumn[1]));
					cursor.close();
					
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 8;
					mBmap = BitmapFactory.decodeFile(mFilePath, options);
					mIvWall.setImageBitmap(mBmap);
					mTvWallpaper.setText(mImageName);
					mSelWall = mPhotoUri.toString();
					mPsd.setmWall(mSelWall);
					cursor.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			System.out.println(">>>>>>>>>>>onkey down");
			saveProfileSettings();
			Intent intent = new Intent(this, ProfileAddOrUpdate.class);
			intent.putExtra(Constants.IS_NEW_PROFILE, mIsNewProfile);
			intent.putExtra(Constants.NAVIGATION_FLAG, false);
			intent.putExtra(Constants.PROFILE_DATA_KEY, mPsd);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			System.out.println(" onKey down end");
			finish();

		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == mChkWall) {
			setButtonsW(isChecked);
			if (!isChecked) {
				mSelWall = null;
			}
		} else if (buttonView == mChkRgTn) {
			setButtonsR(isChecked);
			if (!isChecked) {
				mSelRT = null;
			}
		} else if (buttonView == mChkNot) {
			if (mChkNot.isChecked()) {
				mNotification = "true";
			} else {
				mNotification = null;
			}
		} else if (buttonView == mChkVib) {
			if (mChkVib.isChecked()) {
				mVibrate = "true";
			} else {
				mVibrate = null;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mPsd.getmNot() != null) {
			System.out.println(" the notify is " + mPsd.getmNot());
			mChkNot.setChecked(true);
		} else {
			mChkNot.setChecked(false);
			System.out.println(" notify is null");
		}
		if (mPsd.getmVib() != null) {
			if (mPsd.getmVib().equals("true")) {
				System.out.println(" the vib is " + mPsd.getmVib());
				mChkVib.setChecked(true);
			} else {
				mChkVib.setChecked(false);
				System.out.println(" the vib is " + mPsd.getmVib());
			}
		} else {
			System.out.println(" vib is null");
		}
		if (mPsd.getmRingVol() != null) {
			System.out.println(" the RgVl is " + mPsd.getmRingVol());
			int ringVolume = (Integer.parseInt(mPsd.getmRingVol()) * (mSbRvLvl
					.getMax() / mAM
					.getStreamMaxVolume(AudioManager.STREAM_RING)));
			System.out.println(" the progress is >>>>>>>>" + ringVolume);
			mSbRvLvl.setProgress(ringVolume);
		} else {
			System.out.println(" RgVl is null");
		}
		if (mPsd.getmWall() != null) {
			System.out.println(" wall not null" + mPsd.getmWall());

			mPhotoUri = Uri.parse(mPsd.getmWall());
			String[] mFilePathColumn = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media.TITLE };
			Cursor cursor = getContentResolver().query(mPhotoUri,
					mFilePathColumn, null, null, null);

			cursor.moveToFirst();
			mColumnIndex = cursor.getColumnIndex(mFilePathColumn[0]);
			Constants.isDeleted = false;
			try {
				mFilePath = cursor.getString(mColumnIndex);
			} catch (CursorIndexOutOfBoundsException ciobe) {
				Utility.getUtility().printLog(Constants.WARNING, TAG, "Image may have been deleted");
				Constants.isDeleted = true;
				mPsd.setmWall(null);
				mSelWall = null;
				mChkWall.setChecked(false);
				setButtonsW(false);
				cursor.close();
			}
			if (!Constants.isDeleted) {
				mChkWall.setChecked(true);
				setButtonsW(true);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 8;
				mBmap = BitmapFactory.decodeFile(mFilePath, options);
				mBmap = Bitmap.createScaledBitmap(mBmap, 420, 800, false);
				mIvWall.setImageBitmap(mBmap);
				mSelWall = mPhotoUri.toString();
				cursor.close();
			}
			

		} else {
			mChkWall.setChecked(false);
			setButtonsW(false);
			Utility.getUtility().printLog(Constants.INFO, TAG, "wallpaper is not set");
		}
		if (mPsd.getmRingTone() != null) {
			System.out.println(" rgTn is not null" + mPsd.getmRingTone());

			mAudioUri = Uri.parse(mPsd.getmRingTone());
			String[] mFilePathColumn = { MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Media.TITLE };
			Cursor cursor = getContentResolver().query(mAudioUri,
					mFilePathColumn, null, null, null);

			cursor.moveToFirst();
			mColumnIndex = cursor.getColumnIndex(mFilePathColumn[0]);
			try {
				mFilePath = cursor.getString(mColumnIndex);
				Ringtone r = RingtoneManager.getRingtone(this, mAudioUri);
				mTvRingtone.setText(r.getTitle(this));
			} catch (CursorIndexOutOfBoundsException ciobe) {
				Constants.isDeleted = true;
				mSelRT = null;
				System.out.println(" audio file was deleted------------");
				cursor.close();
			}
			if (!Constants.isDeleted) {
				mChkRgTn.setChecked(true);
				setButtonsR(true);
				mSelRT = mAudioUri.toString();
				Ringtone r = RingtoneManager.getRingtone(this, mAudioUri);
				mTvRingtone.setText(r.getTitle(this));
				cursor.close();
			}
		} else {
			System.out.println(" RgTn is null");

		}

		if (mPsd.getmBrightness() != null) {
			System.out.println(" brightness is not null "
					+ mPsd.getmBrightness());
			int brightness = Integer.parseInt(mPsd.getmBrightness());
			brightness = (int) (brightness*(mSbBrightness.getMax()/(float)Constants.INTERNAL_MAX_BRIGHTNESS_VALUE));
			System.out.println(" bright in resume "+brightness);
			mSbBrightness.setProgress(brightness);
		} else {
			System.out.println(" brightness is null");
		}

	}
	
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	private void setButtonsW(boolean isChecked) {
		if (isChecked) {
			mBtnWall.setEnabled(isChecked);
			mTvWallpaper.setVisibility(View.VISIBLE);
			// mIvWall.setImageResource(R.drawable.ic_launcher);
			mIvWall.setVisibility(View.VISIBLE);
		} else {
			mBtnWall.setEnabled(isChecked);
			mTvWallpaper.setVisibility(View.INVISIBLE);
			// mIvWall.setImageResource(R.drawable.ic_launcher);
			mIvWall.setVisibility(View.INVISIBLE);
		}
	}

	private void setButtonsR(boolean isChecked) {
		if (isChecked) {
			mBtnRgtn.setEnabled(isChecked);
			mTvRingtone.setVisibility(View.VISIBLE);
		} else {
			mBtnRgtn.setEnabled(isChecked);
			mTvRingtone.setVisibility(View.INVISIBLE);
		}
	}
	private void saveProfileSettings(){
		Utility.getUtility().printLog(Constants.INFO, TAG, "saveProfileSettings");
		Utility.getUtility().printLog(Constants.INFO, TAG, "Notiy :"+mNotification+"Vib :"+mVibrate+"Brightness :"+mBrightness+"Ring vol :"+mRingerVol+"Wallpaper :"+mSelWall+"ringtone :"+mSelRT);
		mPsd.setmNot(mNotification);
		mPsd.setmVib(mVibrate);
		mPsd.setmBrightness(mBrightness);
		mPsd.setmRingVol(String.valueOf(mRingerVol));
		mPsd.setmWall(mSelWall);
		mPsd.setmRingTone(mSelRT);
	}

	@Override
	protected void onPause() {
		super.onPause();	
		saveProfileSettings();
		Utility.getUtility().releaseViewMemory(mIvWall);
		Utility.getUtility().releaseViewMemory(mChkNot);
		Utility.getUtility().releaseViewMemory(mChkVib);
		Utility.getUtility().releaseViewMemory(mChkWall);
		Utility.getUtility().releaseViewMemory(mChkRgTn);
		Utility.getUtility().releaseViewMemory(mBtnRgtn);
		Utility.getUtility().releaseViewMemory(mBtnWall);
		Utility.getUtility().releaseViewMemory(mSbBrightness);
		Utility.getUtility().releaseViewMemory(mSbRvLvl);
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {	
		if(arg0 == mSbBrightness){
			float brightness = ((Constants.INTERNAL_MAX_BRIGHTNESS_VALUE/(float)mSbBrightness.getMax())* mSbBrightness.getProgress());
			int intBrightness = (int) brightness;
			mBrightness = String.valueOf(intBrightness);			
		}else{
			mRingerVol = (mSbRvLvl.getProgress() / (mSbRvLvl.getMax() / mAM
					.getStreamMaxVolume(AudioManager.STREAM_RING)));					
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
