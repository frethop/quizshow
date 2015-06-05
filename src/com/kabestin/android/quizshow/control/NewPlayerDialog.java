package com.kabestin.android.quizshow.control;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kabestin.android.quizshow.R;
import com.kabestin.android.quizshow.view.Quizshow;

public class NewPlayerDialog implements DialogCreator, OnClickListener, OptionsItemHandler {
	
	Dialog dialog;
	Activity source;

	@Override
	public Dialog handleDialogCreation(Activity source) {
		this.source = source;
		dialog = new Dialog(source);
		
		dialog.setContentView(R.layout.new_player_dialog);
		dialog.setTitle("New Player");

	    Button button = (Button) dialog.findViewById(R.id.create_new_player_button);
		button.setOnClickListener(this);
		button = (Button) dialog.findViewById(R.id.cancel_new_player_button);
		button.setOnClickListener(new cancelDialogAction(dialog));	

		return dialog;
	}

	@Override
	public Dialog handleDialogCreation(Activity source, Bundle bun) {
		// TODO Auto-generated method stub
		return handleDialogCreation(source);
	}

	@Override
	public boolean handleOptionsItem(Activity source, MenuItem item) {
		source.showDialog(R.id.action_player_manual_add);
		return false;
	}

	@Override
	public void onClick(View v) {
		TextView et = (TextView) dialog.findViewById(R.id.new_player_name);
		String name = et.getText().toString();
		if (et != null) ((Quizshow)source).addPlayer(name);
		dialog.dismiss();
	}

}
