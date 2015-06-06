package com.kabestin.android.quizshow.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.kabestin.android.quizshow.R;
import com.kabestin.android.quizshow.control.Answer;
import com.kabestin.android.quizshow.view.GamePlay;

public class AnswerPanelAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Activity activityParent;
	private QuizshowDatafile data;
	private int round;
	
	public AnswerPanelAdapter(Activity activity, QuizshowDatafile qsdf) {
		activityParent = activity;
		data = qsdf;
		mInflater = LayoutInflater.from((Context)activity);
		round = 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//create a basic imageview here or inflate a complex layout with
		LinearLayout panelSquare = (LinearLayout)mInflater.inflate(R.layout.answer_panel_square, null, false);
		//DrawableButton panelButton = (DrawableButton) panelSquare.findViewById(R.id.square_button);
		Button panelButton = (Button) panelSquare.findViewById(R.id.square_button);
		
		int row = position/5;
		int col = position % 5;

		if (row == 0) {
			panelButton.setBackgroundColor(Color.YELLOW);
			panelButton.setTypeface(null, Typeface.BOLD);
			panelButton.setText(data.getQSCategoryName(round, col));
		} else {
			panelButton.setBackgroundColor(Color.LTGRAY);
			panelButton.setText(""+(row)*100*(round+1));
			row --;
			
			if (GamePlay.getQuizshowDatafile().isUsed(round, col, row)) 
				panelButton.setVisibility(View.INVISIBLE);
			else {
				panelButton.setTag(position);
				panelButton.setOnClickListener(new Answer(activityParent,
						data.getAnswer(round, col, row),
						data.getQuestion(round, col, row),
						data.isDailyDouble(round, col, row), 
						data.getPlayerList(),
						round, col, row, ((row+1)*100*(round+1))
						));
			}
			
		}

		System.out.println("Round: " + round + ", Row: "+row+", Col: "+col);
	    //final int w = (int) (36 * parent.getResources().getDisplayMetrics().density + 0.5f);
		GridView grid = (GridView) parent.findViewById(R.id.answer_panel);
		int w = grid.getWidth();
		int h = grid.getHeight();
	    panelSquare.setLayoutParams(new GridView.LayoutParams((w-50)/5,(h-50)/6)); //w * 2, w * 2));

		return panelSquare;
    }

    public final int getCount() {
        return 30;
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
