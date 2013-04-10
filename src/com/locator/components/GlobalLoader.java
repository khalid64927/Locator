/*package com.locator.components;

import com.locator.utility.Constants;
import com.locator.utility.Utility;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public class GlobalLoader implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private Context mContext;
	private Uri mUri;
	private String[] mProjection;
	private String mSelection;
	private String[] mSelectionArgs;
	private String mSortOrder;
	private static final String TAG = "GlobalLoader";
	
	public GlobalLoader(Context mContext,Uri mUri,String[] mProjection,String mSelection,String[] mSelectionArgs,String mSortOrder) {
		this.mContext = mContext;
		this.mUri = mUri;
		this.mProjection = mProjection;
		this.mSelection = mSelection;
		this.mSelectionArgs = mSelectionArgs;
		this.mSortOrder = mSortOrder;
				
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Utility.getUtility().printLog(Constants.INFO, TAG, "onCreateLoader");
		CursorLoader curLoader = new CursorLoader(mContext, mUri, mProjection, mSelection, mSelectionArgs, mSortOrder); 
				//new CursorLoader(mContext, mUri, mProjection, mSelection, mSelectionArgs, mSortOrder);
		//return new CursorLoader(mContext, mUri, mProjection, mSelection, mSelectionArgs, mSortOrder);
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

}
*/