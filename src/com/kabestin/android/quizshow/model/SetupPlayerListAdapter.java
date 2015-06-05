package com.kabestin.android.quizshow.model;

import java.text.DateFormat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kabestin.android.quizshow.R;
import com.kabestin.android.quizshow.view.Quizshow;

public class SetupPlayerListAdapter extends BaseAdapter 
{
	private LayoutInflater mInflater;
	Activity parent;
	   
	public SetupPlayerListAdapter(Activity parent) {
		this.parent = parent;
		mInflater = LayoutInflater.from(parent);
	}
	
	public int getCount()
	{
		return ((Quizshow)parent).getPlayerList().size();
	}
	    	
	public String getItem(int i) {
		return ((Quizshow)parent).getPlayerList().get(i);
	}
	
	public long getItemId(int i) {
		return i;
	}
	
	public View getView(int arg0, View arg1, ViewGroup arg2)
	{
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT);
		String fmt;
		final ViewHolder holder;
		View v = arg1;
		
		if ( (v == null) || (v.getTag() == null) ) {
			v = mInflater.inflate(R.layout.setup_player_list_row, null);
			holder = new ViewHolder();
			holder.mName = (TextView)v.findViewById(R.id.setup_player_list_name);
		} else {
			holder = (ViewHolder)v.getTag();
		}
		
		holder.mPosition = arg0;
		holder.mName.setText(((Quizshow)parent).getPlayerList().get(arg0));
		
		v.setTag(holder);
		//v.setContentDescription("class_list");    		
		return v;
	}
    	
	    
//==========================================================================================
// Private classes to help implement functionality

	private class ViewHolder {
		int mPosition;
		TextView mName;
	}
	    

}
