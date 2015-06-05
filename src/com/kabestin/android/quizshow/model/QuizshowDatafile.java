package com.kabestin.android.quizshow.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import android.net.Uri;

public class QuizshowDatafile extends DefaultHandler implements Serializable {

	String filename;
	String dataFileUri = null;
	BufferedReader queueFile;
	org.w3c.dom.Document doc;
	InputSource input;
	String info;
	int rounds;
	boolean modified;
	String errorMessage;
	StringBuilder str;
	int round, catpos, rowpos;
	String answerText, questionText, text;

	String qsName;
	String cattitles[][];
	String questions[][][];
	String answers[][][];
	Boolean dailyDouble[][][];
	String audio[][][];
	String finalTitle;
	String finalAnswer;
	String finalQuestion;
	boolean rowempty[][], columnempty[][];
	boolean used[][][];
	ArrayList<String> player;

	public QuizshowDatafile() {
		int i, j, k;

		dataFileUri = null;
		qsName = null;
		cattitles = new String[3][5];
		questions = new String[3][5][5];
		answers = new String[3][5][5];
		dailyDouble = new Boolean[3][5][5];
		audio = new String[3][5][5];
		rowempty = new boolean[3][5];
		columnempty = new boolean[3][5];
		used = new boolean[3][5][5];
		for (i = 0; i < 2; i++)
			for (j = 0; j < 5; j++) {
				rowempty[i][j] = true;
				columnempty[i][j] = true;

				for (k = 0; k < 5; k++) {
					dailyDouble[i][j][k] = false;
					audio[i][j][k] = "";
					used[i][j][k] = false;
				}
			}
		player = new ArrayList<String>();
		errorMessage = null;
	}

	public QuizshowDatafile(InputStream stream, Uri aFileUri) {
		int i, j, k;

		dataFileUri = aFileUri.toString();
		qsName = null;
		cattitles = new String[3][5];
		questions = new String[3][5][5];
		answers = new String[3][5][5];
		dailyDouble = new Boolean[3][5][5];
		audio = new String[3][5][5];
		rowempty = new boolean[3][5];
		columnempty = new boolean[3][5];
		used = new boolean[3][5][5];
		for (i = 0; i < 2; i++)
			for (j = 0; j < 5; j++) {
				rowempty[i][j] = true;
				columnempty[i][j] = true;

				for (k = 0; k < 5; k++) {
					dailyDouble[i][j][k] = false;
					audio[i][j][k] = "";
					used[i][j][k] = false;
				}
			}
		player = new ArrayList<String>();
		errorMessage = null;
		if (aFileUri != null) readFile(stream, aFileUri);
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	
	public void startDocument() throws SAXException {		
        str = new StringBuilder();
	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		String dt;
		DateFormat df  = DateFormat.getDateInstance();
		
		if (localName.length() == 0) localName = qName;
		
		System.out.println(qName);
		
		// Start the file with a <quizshow> tag
		// Example: <quizshow name="2 6 0  Q u i z  S h o w"> ... </quizshow>
		//System.out.println(localName);
		if (localName.equalsIgnoreCase("quizshow")) {
			qsName = atts.getValue("name");
		
		// Parsing a category	  
		//	<category round="0" position="1" title="You Nits"> ... </category>  
		} else if (localName.equalsIgnoreCase("category")) {
			info = atts.getValue("round");
			round = new Integer(info).intValue();
			if (round > rounds)
				rounds = round;
			info = atts.getValue("position");
			catpos = new Integer(info).intValue();
			info = atts.getValue("title");
			cattitles[round][catpos] = info;

		} else if (localName.equalsIgnoreCase("answer")) { 
			info = atts.getValue("position");
			rowpos = new Integer(info).intValue();
			rowempty[round][rowpos] = false;
			columnempty[round][catpos] = false;
			if (atts.getValue("dailydouble") != null) {
				info = atts.getValue("dailydouble");
				dailyDouble[round][catpos][rowpos] = info.equals("true");
			}
			if (atts.getValue("audiofile") != null) {
				info = atts.getValue("audiofile");
				audio[round][catpos][rowpos] = info;
			}

		} else if (localName.equalsIgnoreCase("question")) { 
			info = atts.getValue("position");
			rowpos = new Integer(info).intValue();
		}
	}

    public void characters(char[] ch, int start, int length) throws SAXException
    {
        String value = new String(ch, start, length).trim();
 
        if (value.length() == 0)
        {
            return; // ignore white space
        } else {
        	text = value;
        }

        
    }
    
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.length() == 0) localName = qName;
		//System.out.println("</"+localName+">");
		if (localName.equalsIgnoreCase("answer")) {
			answers[round][catpos][rowpos] = text;
		} else if (localName.equalsIgnoreCase("question")) { 
			questions[round][catpos][rowpos] = text;
		}
	}

    

	public void readFile(InputStream dataStream, Uri aFileUri) {
		int i, round, num, aq, catpos, rowpos;
		Vector children;

		rounds = -1;
		dataFileUri = aFileUri.toString();

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = null;
		try {
			sp = spf.newSAXParser();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		}
		
        try {
        	
    		sp.parse(dataStream, this);

        } catch (SAXParseException e) {
        	e.printStackTrace();
        	errorMessage = e.getMessage();        	
        } catch (Exception e2) {
            //throw new RuntimeException(e);
        	e2.printStackTrace();
        	errorMessage = e2.getLocalizedMessage();
        }

	}
	
	public String getErrorMessage()
	{
		return errorMessage;
	}


	private String readline(FileInputStream fis) throws IOException {
		boolean done = false;
		String str = "";
		int ch;

		while (!done) {
			ch = fis.read();
			if (ch < 0 || (char) ch == '\n') {
				done = true;
			} else {
				str = str + (char) ch;
			}
		}
		str = str.trim();

		return str;
	}

	public void importFile(String aFilename) {
		FileInputStream fis;
		String line;
		int rnd, c, r, i, j, pos;

		try {
			fis = new FileInputStream(aFilename);

			qsName = readline(fis);

			for (rnd = 0; rnd < 2; rnd++) {
				rounds = rnd + 1;
				for (c = 0; c < 5; c++)
					cattitles[rnd][c] = readline(fis);

				for (c = 0; c < 5; c++) {
					columnempty[rnd][c] = false;
					for (r = 0; r < 5; r++) {
						rowempty[rnd][r] = false;
						line = readline(fis);
						if (line.startsWith("&")) {
							dailyDouble[rnd][c][r] = true;
							line = line.substring(1);
						}
						if (line.startsWith("!")) {
							pos = line.indexOf('!', 1);
							audio[rnd][c][r] = line.substring(1, pos);
							line = line.substring(pos + 1);
						}
						answers[rnd][c][r] = line;
						questions[rnd][c][r] = readline(fis);
					}
				}
			}

			cattitles[2][0] = readline(fis);
			answers[2][0][0] = readline(fis);
			questions[2][0][0] = readline(fis);
			rounds = 3;
			modified = false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getQSDFilename() {
		return dataFileUri;
	}

	public String getQSName() {
		return qsName;
	}

	public void setQSName(String aName) {
		qsName = aName;
		modified = true;
	}

	public String getQSCategoryName(int aRound, int aPos) {
		return cattitles[aRound][aPos];
	}

	public void setQSCategoryName(int aRound, int aPos, String aName) {
		cattitles[aRound][aPos] = aName;
		if (aRound > rounds - 1)
			rounds = aRound + 1;
		modified = true;
	}

	public int getQSRounds() {
		return rounds;
	}

	public String getAnswer(int aRound, int i, int j) {
		String str = answers[aRound][i][j];
		if (str == null) {
			return "";
		} else {
			str = str.replaceAll("#-", "<");
			return str.replaceAll("-#", ">");
		}
	}

	public String getQuestion(int aRound, int i, int j) {
		String str = questions[aRound][i][j];
		if (str == null) {
			return "";
		} else {
			str = str.replaceAll("#-", "<");
			return str.replaceAll("-#", ">");
		}
	}

	public void setAnswer(int aRound, int i, int j, String aText) {
		String str;

		str = aText.replaceAll("<", "#-");
		str = str.replaceAll(">", "-#");
//		str = aText.replaceAll("<", "\\<");
//		str = str.replaceAll(">", "\\>");
		answers[aRound][i][j] = str;

		if (aRound > rounds - 1)
			rounds = aRound + 1;
		columnempty[aRound][i] = false;
		rowempty[aRound][j] = false;
		modified = true;
	}

	public void setQuestion(int aRound, int i, int j, String aText) {
		String str;

		str = aText.replaceAll("<", "#-");
		str = str.replaceAll(">", "-#");
//		str = aText.replaceAll("<", "\\<");
//		str = str.replaceAll(">", "\\>");
		questions[aRound][i][j] = str;

		if (aRound > rounds - 1)
			rounds = aRound + 1;
		columnempty[aRound][i] = false;
		rowempty[aRound][j] = false;
		modified = true;
	}

	public boolean isValidQuestion(int aRound, int i, int j) {
		if (questions[aRound][i][j] == null)
			return false;
		if (questions[aRound][i][j].length() == 0)
			return false;
		return true;
	}

	public String getFinalAnswer() {
		return answers[2][0][0];
	}

	public void setFinalAnswer(String aAnswer) {
		answers[2][0][0] = aAnswer;
		rounds = 3;
		modified = true;
	}

	public boolean isValidAnswer(int aRound, int i, int j) {
		if (answers[aRound][i][j] == null)
			return false;
		if (answers[aRound][i][j].length() == 0
				&& audio[aRound][i][j].length() == 0)
			return false;
		return true;
	}

	public String getFinalQuestion() {
		return questions[2][0][0];
	}

	public void setFinalQuestion(String aQuestion) {
		questions[2][0][0] = aQuestion;
		rounds = 3;
		modified = true;
	}

	public String getFinalTitle() {
		return cattitles[2][0];
	}

	public void setFinalTitle(String aTitle) {
		cattitles[2][0] = aTitle;
		rounds = 3;
		modified = true;
	}
	
	public void addPlayer(String name)
	{
		player.add(name);
	}
	
	public int numberOfPlayers()
	{
		return player.size();
	}
	
	public String getPlayer(int index) {
		return player.get(index);
	}
	
	public ArrayList<String> getPlayerList()
	{
		return player;
	}

	public Boolean isDailyDouble(int aRound, int aCategory, int aRow) {
		return dailyDouble[aRound][aCategory][aRow];
	}

	public void setDailyDouble(int aRound, int aCategory, int aRow,
			Boolean aBool) {
		dailyDouble[aRound][aCategory][aRow] = aBool;
		if (aRound > rounds)
			rounds = aRound + 1;
		modified = true;
	}

	public String audioFile(int aRound, int aCategory, int aRow) {
		return audio[aRound][aCategory][aRow];
	}

	public void setAudioFile(int aRound, int aCategory, int aRow,
			String aFilename) {
		audio[aRound][aCategory][aRow] = aFilename;
		if (aRound > rounds)
			rounds = aRound + 1;
		modified = true;
	}

	public boolean isRowEmpty(int aRound, int aRow) {
		return rowempty[aRound][aRow];
	}

	public boolean isColumnEmpty(int aRound, int aColumn) {
		return columnempty[aRound][aColumn];
	}

	public boolean isModified() {
		return modified;
	}
	
	public int numberOfRows(int round) {
		int num = 5;
		for (int i=0; i<4; i++) {
			if (isRowEmpty(round, i)) num--;
		}
		return num;
	}

	public int numberOfColumns(int round) {
		int num = 5;
		for (int i=0; i<4; i++) {
			if (isColumnEmpty(round, i)) num--;
		}
		return num;
	}
	
	public boolean isUsed(int aRound, int aCategory, int aRow) {
		return used[aRound][aCategory][aRow];
	}
	
	public void setUsed(int aRound, int aCategory, int aRow) {
		used[aRound][aCategory][aRow] = true;
	}

	public String verifyData() {
		String analysis, htmlAnalysis;
		Boolean correctData;
		int dds[];
		int rnd, r, c;

		analysis = 
		htmlAnalysis = "<html><body>";
		analysis += "<b>Game data integrity...</b><p>";

		// Check for general/standard items
		if (qsName == null)
			analysis += "<b>Quizshow has no name</b>";
		else if (qsName.length() == 0)
			analysis += "<b>Quizshow has no name</b><i>" + qsName + "</i>";
		else
			analysis += "<b>Quizshow name is </b><i>" + qsName + "</i>";
		analysis += "<p>";
		if (rounds == 3) {
			analysis += "Game has 2 rounds and a final round.";
		} else {
			analysis += "<font color=red>Game has " + rounds + " rounds, but no final round.</font>";
		}
		analysis += "<p>";

		correctData = false;
		dds = new int[2];
		dds[0] = dds[1] = 0;
		analysis += "<ul>";
		for (rnd = 0; rnd < rounds; rnd++) {
			if (rnd == 0)
				correctData = true;
			if (rnd > 1) break;
			for (c = 0; c < 5; c++) {
				if (cattitles[rnd][c] == null) {
					analysis += "<li> <font color=red>Category #" + (c + 1)
							+ " for round " + (rnd + 1)
							+ " has no title</font>";
					correctData = false;
				}
				for (r = 0; r < 5; r++) {
					if (answers[rnd][c][r] == null) {
						analysis += "<li> <font color=red><b>Round "
								+ (rnd + 1) + ", ";
						if (cattitles[rnd][c] == null) {
							analysis += "#" + (c + 1) + ", ";
						} else {
							analysis += "category \"" + cattitles[rnd][c]
									+ "\", ";
						}
						analysis += " row " + (r + 1)
								+ "</b> has no answer</font>";
						correctData = false;
					}
					if (questions[rnd][c][r] == null) {
						analysis += "<li> <font color=red><b>Round "
								+ (rnd + 1) + ", ";
						if (cattitles[rnd][c] == null) {
							analysis += "#" + (c + 1) + ", ";
						} else {
							analysis += "category \"" + cattitles[rnd][c]
									+ "\", ";
						}
						analysis += " row " + (r + 1)
								+ "</b> has no question</font>";
						correctData = false;
					}
					if (dailyDouble[rnd][c][r])
						dds[rnd]++;
					// check to see if the audio file exists
				}
			}
		}
		if (correctData && rounds == 3)
			analysis += "<li>The game has data for all rounds, categories, and rows.";
		analysis += "<p>";
		for (rnd = 0; rnd < 2; rnd++) {
			if (dds[rnd] == 0) {
				analysis += "<li><font color=red> There are no daily doubles for round "
						+ (rnd + 1) + ".";
			} else if (dds[rnd] == 1) {
				analysis += "<li><font color=red> There is only 1 daily double for round "
						+ (rnd + 1) + ".";
			} else if (dds[rnd] != 2) {
				analysis += "<li><font color=red> There are " + dds[rnd]
						+ " daily doubles for round " + (rnd + 1) + ".";
			} else {
				analysis += "<li> There are 2 daily doubles for round "
						+ (rnd + 1) + ".";
			}
		}
		analysis += "</ul>";
		analysis += "</body></html>";

		return analysis;
	}
}
