package com.kabestin.android.quizshow.view;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.kabestin.android.quizshow.R;
import com.kabestin.android.quizshow.control.AnswerPlayerPanelAdapter;

public class AnswerActivity extends Activity {
	
	ArrayList<String> playerList;
	String answer, question;
	int round, points;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // From: http://stackoverflow.com/questions/11425020/actionbar-in-a-dialogfragment
        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        LayoutParams params = this.getWindow().getAttributes(); 
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        this.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params); 

        // This sets the window size, while working around the IllegalStateException thrown by ActionBarView
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
        this.getWindow().setLayout((int)(width*0.8),(int)(height*0.8));
        
        setContentView(R.layout.answer_display_dialog);
        
        answer = getIntent().getStringExtra("answer");
        question = getIntent().getStringExtra("question");
        playerList = getIntent().getStringArrayListExtra("players");
        round = getIntent().getIntExtra("round", 0);
        points = getIntent().getIntExtra("dollars", 0);
        
	    ActionBar ab = getActionBar();
	    ab.setTitle("Round "+round);
	    ab.setSubtitle(""+points+" points"); 
        
		WebView view = (WebView) findViewById(R.id.answer_text);
		String answerPlus = "<font size=+3>"+answer+"</font>";
		view.loadData(answerPlus, "text/html", null);
		
//		final String[] numbers = new String[playerList.size()];
//		int i=0;
//		for (String s : playerList) numbers[i++] = s;
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_list_item_1, numbers);
		AnswerPlayerPanelAdapter adapter = new AnswerPlayerPanelAdapter(this, playerList, points);
		GridView gridView = (GridView) findViewById(R.id.answer_scores);
		gridView.setAdapter(adapter);
		
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.answer, menu);
		return true;
	}
	
    // Implement reactions to options selected from the opening menu.
    public boolean onOptionsItemSelected(MenuItem item) {
    	   	
//    	// Find the handler in the optionsHandler table and invoke it
//    	Class optionsHandler = (Class)optionItemHandlers.get(item.getItemId());
//    	OptionsItemHandler option = null;
//		try {
//			option = (OptionsItemHandler)optionsHandler.newInstance();
//    		return option.handleOptionsItem(this);
//		} catch (Exception e) {
//			e.printStackTrace();
//			option = null;
//		}
//		//adapter.notifyDataSetChanged();
    	
    	if (item.getItemId() == R.id.action_close) {
    		finish();
    	} else if (item.getItemId() == R.id.action_show_answer) {
    		WebView view = (WebView) findViewById(R.id.answer_text);
    		String questionPlus = "<font size=+3>"+question+"</font>";
    		view.loadData(questionPlus, "text/html", null);
    	}
        return super.onOptionsItemSelected(item);
    }
    
 
	
	
}
