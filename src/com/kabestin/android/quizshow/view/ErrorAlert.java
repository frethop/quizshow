//------------------------------------------------------------------------
//
// Taken from http://www.java2s.com/Code/Android/UI/ShowerrorAlertDialog.htm
//
//------------------------------------------------------------------------

package com.kabestin.android.quizshow.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;

/**
 * @author Amando Jose Quinto II The class that shows the error dialog box.
 */

public class ErrorAlert implements OnKeyListener {

	private final Context mContext;
	private AlertDialog aDialog;

	  public ErrorAlert(final Context context) {
	    mContext = context;
	  }

	  public void showErrorDialog(final String title, final String message) {
	    aDialog = new AlertDialog.Builder(mContext).setMessage(message)//.setTitle(title)
	        .setNeutralButton("Close", new OnClickListener() {
	          public void onClick(final DialogInterface dialog, final int which) {
	            //Prevent to finish activity, if user clicks about.
	            if (title.startsWith("Fatal")) {
	              ((Activity) mContext).finish();
	            }
	            
	          }
	        }).create();
	    aDialog.setOnKeyListener(this);
	    aDialog.show();
	  }
	  
	  public void showErrorDialog(Exception e) {
		  showErrorDialog("Unexpected Error", e.getMessage());
	  }

	  public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK){
	      //disable the back button
	    }
	    return true;
	  }
	  
	  public void setTitle(String title)
	  {
		  try {
			aDialog.setTitle(title);
		} catch (Exception e) {
			e.printStackTrace();
		}
	  }
	  
	  public void setMessage(String message)
	  {
		  try {
			aDialog.setMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	  }

}
