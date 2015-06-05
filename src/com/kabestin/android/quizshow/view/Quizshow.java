package com.kabestin.android.quizshow.view;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.kabestin.android.quizshow.R;
import com.kabestin.android.quizshow.control.ActivityResultHandler;
import com.kabestin.android.quizshow.control.AutoPlayerDialog;
import com.kabestin.android.quizshow.control.DialogCreator;
import com.kabestin.android.quizshow.control.NewPlayerDialog;
import com.kabestin.android.quizshow.control.OpenQuizshowFile;
import com.kabestin.android.quizshow.control.OptionsItemHandler;
import com.kabestin.android.quizshow.model.QuizshowDatafile;
import com.kabestin.android.quizshow.model.SetupPlayerListAdapter;
import com.kabestin.android.quizshow.utilities.Constants;

public class Quizshow extends Activity {
	
	HashMap<Integer, Class> optionItemHandlers, activityResultHandlers, dialogCreators;
	
	QuizshowDatafile data;
	
	Uri dataFileUri;
	
	Activity parent;
	SetupPlayerListAdapter adapter;
	
	// game data
	int rounds;
	ArrayList<String> playerNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy); 
		
		parent = this;
		
		playerNames = new ArrayList<String>();
    	data = new QuizshowDatafile();
		
		ListView playerList = (ListView)findViewById(R.id.setup_player_list);
		adapter = new SetupPlayerListAdapter(this);
		playerList.setAdapter(adapter);
		Button playButton = (Button) findViewById(R.id.start_button);
		playButton.setOnClickListener(new StartGamePlay());

		//*** Set up option, activity, and dialog handlers		
		optionItemHandlers = new HashMap<Integer, Class>();
		optionItemHandlers.put(R.id.action_file, OpenQuizshowFile.class);
		optionItemHandlers.put(R.id.action_player_manual_add, NewPlayerDialog.class);
		optionItemHandlers.put(R.id.action_player_auto_add, AutoPlayerDialog.class);

		activityResultHandlers = new HashMap<Integer, Class>();
		activityResultHandlers.put(Constants.QUIZSHOW_DATA, OpenQuizshowFile.class);

		dialogCreators = new HashMap<Integer, Class>();
		dialogCreators.put(R.id.action_player_manual_add, NewPlayerDialog.class);
		dialogCreators.put(R.id.action_player_auto_add, AutoPlayerDialog.class);
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quizshow, menu);
		return true;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
	    super.onPrepareDialog(id, dialog);
	    
	    if (id == R.id.action_player_manual_add) {
    		EditText et = (EditText) dialog.findViewById(R.id.new_player_name);
    		et.setText("");
	    }
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
			option = null;
		}
		
        return super.onOptionsItemSelected(item);
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
    	}
	    	
   }
    
    @SuppressLint("NewApi")
	public void setDataFileUri(Uri fileUri)
    {
    	dataFileUri = fileUri;
    	
    	InputStream dataStream;
		try {
			
			// Get the File path from the Uri
            String path = FileUtils.getPath(this, fileUri);
            
            // Alternatively, use FileUtils.getFile(Context, Uri)
            if (path != null && FileUtils.isLocal(path)) {
                File file = new File(path);
			
                dataStream = new BufferedInputStream(new FileInputStream(file));
                data.readFile(dataStream, fileUri);
            } else {
            	throw new Exception("Path is wrong");
            }
            
            // Reconfigure the screen to reflect the new data file contents
            RelativeLayout noDataLayout = (RelativeLayout) findViewById(R.id.nodata_display);
            noDataLayout.setVisibility(View.GONE);
            
            RelativeLayout yesDataLayout = (RelativeLayout) findViewById(R.id.data_display);
            yesDataLayout.setVisibility(View.VISIBLE);
            TextView view = (TextView) findViewById(R.id.qs_title);
            view.setText(data.getQSName());
            view = (TextView) findViewById(R.id.qs_rounds);
            view.setText(""+data.getQSRounds());
            view = (TextView) findViewById(R.id.qs_description);
            String str = "Round 1: "+ data.numberOfRows(0) + " rows / " + data.numberOfColumns(0) +" columns"
            		+"\nRound 2: "+ data.numberOfRows(1) + " rows / " + data.numberOfColumns(1) +" columns";
            view.setText(str);
            view = (TextView) findViewById(R.id.qs_verification);
            if (data.getErrorMessage() == null) {
	            str = data.verifyData();
	            view.setText(Html.fromHtml(str));
            } else {
            	view.setText(data.getErrorMessage());
            	view.setTextColor(Color.RED);
            }
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void revealSetupPlayerList()
    {
        RelativeLayout noPlayerLayout = (RelativeLayout) findViewById(R.id.noplayers_display);
        noPlayerLayout.setVisibility(View.GONE);
        
        RelativeLayout yesPlayerLayout = (RelativeLayout) findViewById(R.id.players_display);
        yesPlayerLayout.setVisibility(View.VISIBLE);

    }
    	
    public void addPlayer(String name) 
    {
    	revealSetupPlayerList();
    	playerNames.add(name);
    	data.addPlayer(name);
    	adapter.notifyDataSetChanged();
    }
  
    public ArrayList<String> getPlayerList() 
    {
    	return playerNames;
    }
    
    //-------------------------------------------------------------------------------------------
    private class StartGamePlay implements OnClickListener {
    	
    	@Override
    	public void onClick(View v) {
    		if (data.getQSDFilename() == null) {
    			new ErrorAlert(parent).
 			   		showErrorDialog("No Data", "Please provide a Quizshow data file before playing.");
    		} else if (data.getErrorMessage() != null) {
    			new ErrorAlert(parent).
			   		showErrorDialog("No Data", "Please provide a correct Quizshow data file before playing.");
    		} else if (data.numberOfPlayers() == 0) {
    			new ErrorAlert(parent).
    			   showErrorDialog("No Players", "Please create players for the game before playing.");
    		} else {
	        	Bundle bundle = new Bundle();
	        	bundle.putStringArrayList("playerNames", playerNames);
	        	bundle.putSerializable("dataFile", (Serializable)data);
	        	Intent gameplayIntent = new Intent((Context)parent, GamePlay.class);
	        	gameplayIntent.putExtras(bundle);
	        	startActivityForResult(gameplayIntent, Constants.PLAY_THE_GAME);
    		}
    	}
    }
	
}
