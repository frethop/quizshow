package com.kabestin.android.quizshow.control;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.kabestin.android.quizshow.R;
import com.kabestin.android.quizshow.view.GamePlay;

public class FixNameScoreAction implements DialogCreator, OnLongClickListener, OnClickListener {

	Dialog dialog;
	Activity source;
	String playerName;

	public FixNameScoreAction() {
	}

	public FixNameScoreAction(Activity source, String name) {
		this.source = source;
		playerName = name;
	}

	@Override
	public Dialog handleDialogCreation(Activity source) {
		return null;
	}

	@Override
	public Dialog handleDialogCreation(Activity source, Bundle bun) {
		// TODO Auto-generated method stub
		return handleDialogCreation(source);
	}

	@Override
	public boolean onLongClick(View v) {
		dialog = new Dialog(source);
		
		dialog.setContentView(R.layout.fix_name_score_dialog);
		dialog.setTitle("Fix Player");

		EditText et = (EditText) dialog.findViewById(R.id.player_name);
		et.setText(playerName);
		et = (EditText) dialog.findViewById(R.id.player_score);
		et.setText(""+ ((GamePlay)source).getPlayerScore(playerName));
	    Button button = (Button) dialog.findViewById(R.id.edit_player_button);
		button.setOnClickListener(this);
		button = (Button) dialog.findViewById(R.id.cancel_player_button);
		button.setOnClickListener(new cancelDialogAction(dialog));
		
		dialog.show();

		return true;
	}

	@Override
	public void onClick(View v) {
		EditText et = (EditText) dialog.findViewById(R.id.player_name);
		String newName = et.getText().toString();
		if (! newName.equals(playerName)) ((GamePlay)source).changePlayerName(playerName, newName);
		et = (EditText) dialog.findViewById(R.id.player_score);
		int pScore = (new Integer(et.getText().toString())).intValue();
		((GamePlay)source).setPlayerScore(newName, pScore);
		((GamePlay)source).redisplayScores();
		dialog.dismiss();
	}

}
