package com.kabestin.android.quizshow.control;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public interface DialogCreator {
	
	public Dialog handleDialogCreation(Activity source);
	public Dialog handleDialogCreation(Activity source, Bundle bun);
	
    public class cancelDialogAction implements OnClickListener {
   	Dialog parentDialog;
   	public cancelDialogAction(Dialog dialog) {
   		parentDialog = dialog;
   	}
       public void onClick(View v) {
       	parentDialog.dismiss();
       }
   }


}
