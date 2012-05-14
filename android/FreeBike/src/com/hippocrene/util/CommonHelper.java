package com.hippocrene.util;

import android.app.ProgressDialog;
import android.content.Context;


public class CommonHelper {
	private static ProgressDialog mProgress;
	
	// show the progress bar.
	public static void showProgress(Context context, CharSequence message)
	{
		mProgress = new ProgressDialog(context);
		mProgress.setMessage(message);
		mProgress.setIndeterminate(false);
		mProgress.setCancelable(false);
		mProgress.show();
	}
	
	public static void closeProgress()
    {
    	try
    	{
	    	if( mProgress != null )
	    	{
	    		mProgress.dismiss();
	    		mProgress = null;
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }   
}
