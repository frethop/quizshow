package com.kabestin.android.quizshow.control;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.kabestin.android.quizshow.R;
import com.kabestin.android.quizshow.model.DrawableButton;
import com.kabestin.android.quizshow.view.GamePlay;

public class AnswerPlayerPanelAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Activity activityParent;
	private ArrayList<String> playerList;
	private int round;
	private int dollars;
	
	public AnswerPlayerPanelAdapter(Activity activity, ArrayList<String> pList, int amount) {
		activityParent = activity;
		playerList = pList;
		mInflater = LayoutInflater.from((Context)activity);
		round = 0;
		dollars = amount;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		//create a basic imageview here or inflate a complex layout with
		LinearLayout panelSquare = (LinearLayout)mInflater.inflate(R.layout.answer_display_player_item, null, false);

		DrawableButton correct = (DrawableButton) panelSquare.findViewById(R.id.correct_button);
		DrawableButton wrong = (DrawableButton) panelSquare.findViewById(R.id.wrong_button);
				correct.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	int t = GamePlay.getPlayerScore(playerList.get(position));
	            GamePlay.setPlayerScore(playerList.get(position), 
	            		GamePlay.getPlayerScore(playerList.get(position))+dollars);
	            activityParent.finish();
	        }
	    });

		correct.setText(playerList.get(position));
		//Drawable d = activityParent.getResources().getDrawable( R.drawable.correct );
		correct.setBackgroundColor(Color.GREEN);
		wrong.setText(playerList.get(position));
		//d = activityParent.getResources().getDrawable( R.drawable.wrong );
		wrong.setBackgroundColor(Color.RED);
		
		return panelSquare;
    }

    public final int getCount() {
        return playerList.size();
    }

    public final PanelItem getItem(int position) {
        return null;
    }

    public final long getItemId(int position) {
        return position;
    }
    
    public void setRound(int round) 
    {
    	this.round = round;
    }
    
    public void setPoints(int points) {
    	dollars = points;
    }
    
    private class PanelItem {
    	public String name;
    	public int row, column;
    	
    	public PanelItem (String n, int r, int c)
    	{
    		name = n;
    		row = r;
    		column = c;
    	}
    }
    
    
	
}
