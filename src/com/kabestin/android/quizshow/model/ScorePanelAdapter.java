package com.kabestin.android.quizshow.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kabestin.android.quizshow.R;
import com.kabestin.android.quizshow.control.FixNameScoreAction;
import com.kabestin.android.quizshow.view.GamePlay;

public class ScorePanelAdapter extends BaseAdapter 
{
	private LayoutInflater mInflater;
	private HashMap<String, Integer> scores;
	private ArrayList<String> names;
	TextView line;
	TextView score;
	Activity parent;
	
	public ScorePanelAdapter(Activity context) {
		parent = context;
		mInflater = LayoutInflater.from(context);
		scores = new HashMap<String, Integer>();
		names = new ArrayList<String>();
	}
	
	public void setNames(ArrayList<String> list)
	{
		names = list;
		for (int i=0; i<names.size(); i++) {
			scores.put(list.get(i), 0);
		}
	}
	
	public void setScore(String name, int score)
	{
		scores.put(name, score);
	}
	
	public void addName(String name) 
	{
		names.add(name);
		scores.put(name, 0);
	}
	
	public int getCount()
	{
		return ((GamePlay)parent).numberOfPlayers();
	}
	
	public String getItem(int i) {
		return names.get(i);
	}
	
	public long getItemId(int i) {
		return i;
	}
	
	public View getView(int arg0, View arg1, ViewGroup arg2)
	{
		View v = arg1;
		
		if ( (v == null) || (v.getTag() == null) ) {
			v = mInflater.inflate(R.layout.score_panel_item, null);
			line = (TextView) v.findViewById(R.id.score_name);
			score = (TextView) v.findViewById(R.id.score_value);
		} else {
			line = (TextView)v.getTag();
		}
		
		String name = ((GamePlay)parent).getPlayerName(arg0);
		line.setText(name+": ");
		line.setTextSize(20);
		int sc = ((GamePlay)parent).getPlayerScore(name);
		score.setText(""+sc);
		score.setTextSize(20);
		
		v.setTag(line);
		v.setOnLongClickListener(new FixNameScoreAction(parent, names.get(arg0)));
		v.setContentDescription("score_name");

		return v;
	}
	
	
}

