package com.kabestin.android.quizshow.view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.kabestin.android.quizshow.R;
import com.kabestin.android.quizshow.control.ActivityResultHandler;
import com.kabestin.android.quizshow.control.DialogCreator;
import com.kabestin.android.quizshow.control.FixNameScoreAction;
import com.kabestin.android.quizshow.control.OpenQuizshowFile;
import com.kabestin.android.quizshow.control.OptionsItemHandler;
import com.kabestin.android.quizshow.model.AnswerPanelAdapter;
import com.kabestin.android.quizshow.model.QuizshowDatafile;
import com.kabestin.android.quizshow.model.ScorePanelAdapter;
import com.kabestin.android.quizshow.utilities.Constants;

public class GamePlay extends Activity {
	
	HashMap<Integer, Class> optionItemHandlers, activityResultHandlers, dialogCreators;
	
	AnswerPanelAdapter answerAdapter;
	static ScorePanelAdapter scoreAdapter;
	ArrayList<String> playerNames;
	static HashMap<String, Integer> scores;
	static QuizshowDatafile data;
	
	Uri dataFileUri;
	
	// game data
	int rounds;
	int currentRound = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quizshow);
		
		playerNames = getIntent().getStringArrayListExtra("playerNames");
		data = (QuizshowDatafile) getIntent().getSerializableExtra("dataFile");
		scores = new HashMap<String, Integer>();
		for (String s : playerNames) {
			scores.put(s, 0);
		}

	    ActionBar ab = getActionBar();
	    ab.setTitle(data.getQSName());
	    ab.setSubtitle("Round 1"); 
		
		GridView answerGrid = (GridView) findViewById(R.id.answer_panel);
		answerAdapter = new AnswerPanelAdapter(this, data);
		answerGrid.setAdapter(answerAdapter);	    
	    answerGrid.setOnItemClickListener(new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	            System.out.println("Position: "+arg2+" / Row: "+arg3);
	        }
	    });
	    
	    ListView scoreList = (ListView) findViewById(R.id.score_panel);
	    scoreAdapter = new ScorePanelAdapter(this);
	    scoreAdapter.setNames(playerNames);
	    scoreList.setAdapter(scoreAdapter);
	    
		//*** Set up option, activity, and dialog handlers		
		optionItemHandlers = new HashMap<Integer, Class>();
		//optionItemHandlers.put(R.id.action_file, OpenQuizshowFile.class);

		activityResultHandlers = new HashMap<Integer, Class>();
		activityResultHandlers.put(Constants.QUIZSHOW_DATA, OpenQuizshowFile.class);

		dialogCreators = new HashMap<Integer, Class>();
		dialogCreators.put(R.id.fix_name_score_dialog, FixNameScoreAction.class);
		
		MediaPlayer mp = new MediaPlayer();
        try {

            AssetFileDescriptor afd;
            afd = getAssets().openFd("boardfill.mp3");
            mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gameplay, menu);
		return true;
	}
	
    // Create dialogs as needed.
    protected Dialog onCreateDialog(int directive, Bundle bun) 
    {
    	Dialog dialog = new Dialog(this);

    	// Find the handler in the optionsHandler table and invoke it
    	Class dialogCreator = (Class)dialogCreators.get(directive);
    	DialogCreator creator = null;
		try {
			creator = (DialogCreator)dialogCreator.newInstance();
    		dialog =  creator.handleDialogCreation(this, bun); 
		} catch (Exception e) {
			e.printStackTrace();
			dialog = null;
		}
		if (dialog != null) return dialog; 
		
	    return dialog;
    }
    
    // Create dialogs as needed.
    protected Dialog onCreateDialog(int directive) 
    {
    	Dialog dialog = new Dialog(this);

    	// Find the handler in the optionsHandler table and invoke it
    	Class dialogCreator = (Class)dialogCreators.get(directive);
    	DialogCreator creator = null;
		try {
			creator = (DialogCreator)dialogCreator.newInstance();
    		dialog =  creator.handleDialogCreation(this); 
		} catch (Exception e) {
			e.printStackTrace();
			dialog = null;
		}
		if (dialog != null) return dialog;
		
	    return dialog;
    }
    
    // Implement reactions to options selected from the opening menu.
    public boolean onOptionsItemSelected(MenuItem item) {
    	   	
    	// Find the handler in the optionsHandler table and invoke it
    	Class optionsHandler = (Class)optionItemHandlers.get(item.getItemId());
    	OptionsItemHandler option = null;
		try {
			option = (OptionsItemHandler)optionsHandler.newInstance();
    		return option.handleOptionsItem(this, item);
		} catch (Exception e) {
			e.printStackTrace();
			option = null;
		}
		//adapter.notifyDataSetChanged();
		
		if (item.getItemId() == R.id.action_next_round) {
			currentRound++;
		    ActionBar ab = getActionBar();
		    ab.setSubtitle("Round "+(currentRound+1));
		    if (currentRound == 1) {
		    	item.setEnabled(false);
		    }
		    answerAdapter.setRound(currentRound);
		    answerAdapter.notifyDataSetChanged();
		}
    	       
        return super.onOptionsItemSelected(item);
    }
    
    public void redisplayScores() {
    	scoreAdapter.notifyDataSetChanged();
    }
    
    public void redisplayAnswers() {
    	answerAdapter.notifyDataSetChanged();
    }
    
    // All sorts of activities begin in this class and complete their work be returning.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    	
    	// If there is an error, simply return
    	if (resultCode != Activity.RESULT_OK) return;
    	
    	if (intent != null) {
    		
	    	// Find the handler in the activityResultHandlers table and invoke it
	    	Class activityResultHandler = (Class)activityResultHandlers.get(requestCode);
	    	ActivityResultHandler handler = null;
			try {
				handler = (ActivityResultHandler)activityResultHandler.newInstance();
				if (handler != null) {
					handler.handleActivityResult(this, intent);
				}
	    		return;
			} catch (Exception e) {
				e.printStackTrace();
				handler = null;
			}
			redisplayAnswers();
			redisplayScores();
    	}
	    	
   }
    
    @SuppressLint("NewApi")
	public void setDataFileUri(Uri fileUri)
    {
    	dataFileUri = fileUri;
    	
    	data = new QuizshowDatafile();
    	InputStream dataStream;
		try {
			
			// Get the File path from the Uri
            String path = FileUtils.getPath(this, fileUri);
            
            // Alternatively, use FileUtils.getFile(Context, Uri)
            if (path != null && FileUtils.isLocal(path)) {
                File file = new File(path);
			
                dataStream = new BufferedInputStream(new FileInputStream(file));
                data.readFile(dataStream, fileUri);
                rounds = data.getQSRounds();
            } else {
            	throw new Exception("Path is wrong");
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    static public QuizshowDatafile getQuizshowDatafile() {
    	return data;
    }

    //------------------------------------------------------------------------------
    
    public String getPlayerName(int position)
    {
    	return position<playerNames.size()?playerNames.get(position):null;
    }
    
    public void changePlayerName(String oldName, String newName) 
    {
    	int i;
    	
    	for (i=0; i<playerNames.size(); i++)
    		if (playerNames.get(i).equals(oldName)) break;
    	if (i<playerNames.size()) {
    		playerNames.set(i, newName);
	    	int score = scores.get(oldName);
	    	scores.remove(oldName);
	    	scores.put(newName, score);
    	}
    }
    
    public int numberOfPlayers()
    {
    	return playerNames.size();
    }
    
    public static int getPlayerScore(String name)
    {
    	return scores.get(name);
    }
    
    public static void setPlayerScore(String name, int score)
    {
    	scores.put(name, score);
    	scoreAdapter.setScore(name, score);
    	scoreAdapter.notifyDataSetChanged();
    }

	
	
}
