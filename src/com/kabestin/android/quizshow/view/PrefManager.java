package com.kabestin.android.quizshow.view;

import java.util.Date;

import android.content.Context;
import android.preference.PreferenceManager;

public class PrefManager {

	private static final String HOME = "home";
	static Context parentContext = null;

	public PrefManager() {
	}

	static public void setContext(Context parent) {
		parentContext = parent;
	}

	// date of schedule
	static public void saveDateString(String dateKey, String lastDate) {
		PreferenceManager.getDefaultSharedPreferences(parentContext).edit()
				.putString(dateKey, lastDate).commit();
	}

	static public String getDateString(String dateKey) {
		return PreferenceManager.getDefaultSharedPreferences(parentContext)
				.getString(dateKey, "");
	}

	static public void saveDate(String dateKey, Date lastDate) {
		String str = lastDate.toString();
		PreferenceManager.getDefaultSharedPreferences(parentContext).edit()
				.putString(dateKey, str).commit();
	}

	static public Date getDate(String dateKey) {
		String str = PreferenceManager.getDefaultSharedPreferences(
				parentContext).getString(dateKey, "");
		if (str == null)
			return null;
		else if (str.length() == 0)
			return null;
		else
			return new Date(str);
	}
	
	static public boolean getBoolean(String key) 
	{
		return PreferenceManager.getDefaultSharedPreferences(parentContext)
				.getBoolean(key, false);
	}

	// anything else ... as a String
	static public void put(String key, String value) {
		PreferenceManager.getDefaultSharedPreferences(parentContext).edit()
				.putString(key, value).commit();
	}

	static public String get(String key, String defaultValue) {
		String str = PreferenceManager.getDefaultSharedPreferences(parentContext).getString(key, null);
		if (str == null) str = defaultValue;
		return str;
	}

}


