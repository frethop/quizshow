package com.kabestin.android.quizshow.control;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.kabestin.android.quizshow.model.QuizshowDatafile;
import com.kabestin.android.quizshow.utilities.Constants;
import com.kabestin.android.quizshow.view.AnswerActivity;
import com.kabestin.android.quizshow.view.GamePlay;

public class Answer implements OnClickListener {
	
	String answer, question;
	boolean isDailyDouble;
	int row, column;
	QuizshowDatafile dataFile;
	int round, dollars;
	
	Activity source;
	
	ArrayList<String> playerList;
	
	public Answer(Activity source, String aAnswer, String aQuestion, boolean isDailyDouble, ArrayList<String> players,
			int round, int column, int row, int dollars) 
	{
		this.source = source;
		
		answer = aAnswer;
		question = aQuestion;
		this.isDailyDouble = isDailyDouble;
		this.row = row;
		this.column = column;
		//dataFile = qsdf;
		this.round = round;
		this.dollars = dollars;
		
		playerList = players;
	}
		
	@Override
	public void onClick(View v) {
		v.setVisibility(View.INVISIBLE);
		((GamePlay)source).getQuizshowDatafile().setUsed(round, column, row);
		
		AnswerActivity aa = new AnswerActivity();
    	Bundle bundle = new Bundle();
    	bundle.putString("answer", answer);
    	bundle.putString("question", question);
    	bundle.putStringArrayList("players", playerList);
    	bundle.putBoolean("dailydouble", isDailyDouble);
    	bundle.putInt("row", row);
    	bundle.putInt("column",  column);
    	bundle.putInt("round", round);
    	bundle.putInt("dollars",  dollars);
    	Intent answerIntent = new Intent((Context)source, AnswerActivity.class);
    	answerIntent.putExtras(bundle);
    	source.startActivityForResult(answerIntent, Constants.DISPLAY_ANSWER); 
		
	}

}
