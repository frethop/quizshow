package com.kabestin.android.quizshow.control;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.kabestin.android.quizshow.R;
import com.kabestin.android.quizshow.utilities.Utilities;
import com.kabestin.android.quizshow.view.ErrorAlert;
import com.kabestin.android.quizshow.view.Quizshow;

public class AutoPlayerDialog 
    implements DialogCreator, OnClickListener, QuizshowRegistrationListener, OptionsItemHandler {
	
	Dialog dialog;
	Activity source;
	
	QuizshowRegistrar registrar;
	
	public AutoPlayerDialog() {
		registrar = new QuizshowRegistrar();
	}

	@Override
	public Dialog handleDialogCreation(Activity source) {
		this.source = source;
		registrar = new QuizshowRegistrar();
		registrar.setRegistrationListener(this);
		
		dialog = new Dialog(source);
		dialog.setContentView(R.layout.auto_player_dialog);
		dialog.setTitle("Register Players");

	    Button button = (Button) dialog.findViewById(R.id.start_stop_registration_button);
	    button.setText("Start!");
		button.setOnClickListener(this);

		return dialog;
	}

	@Override
	public Dialog handleDialogCreation(Activity source, Bundle bun) {
		// TODO Auto-generated method stub
		return handleDialogCreation(source);
	}

	@Override
	public boolean handleOptionsItem(Activity source, MenuItem item) {
		if (item.getTitle().equals("Auto")) {
			item.setIcon(source.getResources().getDrawable(R.drawable.stop_social_group_import));
			registrar.setRegistrationListener(this);
			registrar.startRegistration();
			item.setTitle("Stop");
			InetAddress localHost;
			try {
				localHost = Utilities.getLocalHostLANAddress();
				
				new ErrorAlert(source)
			       .showErrorDialog("IP Address", "Inform players our address is "+localHost.getHostAddress());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			item.setIcon(source.getResources().getDrawable(R.drawable.social_group_import));
			//registrar.stopRegistration();
			item.setTitle("Auto");
		}
		return true;
	}

	@Override
	public void onClick(View v) {
	    Button button = (Button) dialog.findViewById(R.id.start_stop_registration_button);
	    if (button.getText().equals("Start!")) {
	    	button.setText("Stop!");
	    	registrar.startRegistration();
	    } else {
	    	button.setText("Start!");
	    	dialog.dismiss();
	    }
	}
	
	public void registerPlayer (String playerName, String Color) {
		((Quizshow)source).addPlayer(playerName);
	}
	
	public static String getIpAddress(byte[] rawBytes) {
		return ""+rawBytes[0]+"."+rawBytes[1];
    }
}
