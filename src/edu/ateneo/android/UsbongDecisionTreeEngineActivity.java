/*
 * Copyright 2012 Michael Syson
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.ateneo.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import edu.ateneo.android.multimedia.audio.AudioRecorder;
import edu.ateneo.android.utils.UsbongUtils;

public class UsbongDecisionTreeEngineActivity extends Activity implements TextToSpeech.OnInitListener{
	private static final boolean USE_UNESCAPE=true; //allows the use of \n (new line) in the decision tree
//	private static boolean USE_ENG_ONLY=true; //uses English only
	
	private static boolean IS_IN_DEBUG_MODE=false;

	private int currLanguageBeingUsed;
	
	public static final int YES_NO_DECISION_SCREEN=0;	
	public static final int MULTIPLE_RADIO_BUTTONS_SCREEN=1;	
	public static final int MULTIPLE_CHECKBOXES_SCREEN=2;	
	public static final int AUDIO_RECORD_SCREEN=3;
	public static final int PHOTO_CAPTURE_SCREEN=4;	
	public static final int TEXTFIELD_SCREEN=5;	
	public static final int TEXTFIELD_WITH_UNIT_SCREEN=6;	
	public static final int SPECIAL_SCREEN=7;	
	public static final int SPECIAL_IMAGE_SCREEN=8;	
	public static final int CLASSIFICATION_SCREEN=9;		
	public static final int END_STATE_SCREEN=10;	
	
	private static int currScreen=TEXTFIELD_SCREEN;
	
	private Button backButton;
	private Button nextButton;	
	
	private Button stopButton;
	private Button recordButton;
	private Button playButton;
	
	private Button photoCaptureButton;
	private ImageView myImageView;
	
	public static Intent photoCaptureIntent;
	
	private static String myPictureName="default"; //change this later in the code

	private boolean hasReachedEndOfAllDecisionTrees;
	private boolean isFirstQuestionForDecisionTree;

	private boolean usedBackButton;
	
	private boolean performedCapturePhoto;

	private String currUsbongNode="";
	private String nextUsbongNodeIfYes;
	private String nextUsbongNodeIfNo;
	
	private String textFieldUnit="";
		
	private int usbongNodeContainerCounter=-1;//because I do a ++, so that the first element would be at 0;
	private int requiredTotalCheckedBoxes;
	private Vector<String> usbongNodeContainer;
	private Vector<String> classificationContainer;
	private Vector<String> radioButtonsContainer;
	private Vector<String> usbongAnswerContainer;
	private Vector<String> checkBoxesContainer;

	private String noStringValue;
	private String yesStringValue;

	private String myTreeDirectory="usbong_trees/";
	private String myTree="input.xml";
	private String myOutputDirectory="usbong_trees/output"; //add the ".csv" after appending the timestampe //output.csv
	
	private static UsbongDecisionTreeEngineActivity instance;
    private static TextToSpeech mTts;
    private int MY_DATA_CHECK_CODE=0;
	private static final int EMAIL_SENDING_SUCCESS=99;

	public ListView treesListView;
	
	private CustomDataAdapter mCustomAdapter;
	private ArrayList<String> listOfTreesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
                
        instance=this;

        //if return is null, then currScreen=0
        currScreen=Integer.parseInt(getIntent().getStringExtra("currScreen")); 

        //default..
        currLanguageBeingUsed=UsbongUtils.LANGUAGE_ENGLISH;
        
        //==================================================================
        //text-to-speech stuff
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        mTts = new TextToSpeech(this,this);
		mTts.setLanguage(new Locale("eng", "EN"));//default
        //==================================================================

        
    	usbongNodeContainer = new Vector<String>();
    	classificationContainer = new Vector<String>();
    	radioButtonsContainer = new Vector<String>();
    	usbongAnswerContainer = new Vector<String>();
    	checkBoxesContainer = new Vector<String>();

    	usedBackButton=false;
    	
    	//default values
    	IS_IN_DEBUG_MODE=false;
    	UsbongUtils.destinationServerURL="127.0.0.1";//"192.168.1.105";
		
    	if (UsbongUtils.getFileFromSDCard(UsbongUtils.BASE_FILE_PATH + "usbong.config") == null) { 
			IS_IN_DEBUG_MODE=false;
		}
		else {
			StringBuffer sb = new StringBuffer();    		
			try {
				InputStreamReader reader = UsbongUtils.getFileFromSDCard(UsbongUtils.BASE_FILE_PATH + "usbong.config");
				BufferedReader br = new BufferedReader(reader);    		
	        	String currLineString;        	
	        	while((currLineString=br.readLine())!=null)
	        	{ 		
	    			if (currLineString.equals("DEBUG_MODE=ON")) {
	    				IS_IN_DEBUG_MODE=true;				
	    			}
	    			else if (currLineString.contains("DESTINATION_URL=")) {
	    				UsbongUtils.destinationServerURL= currLineString.replace("DESTINATION_URL=", "");
	    			}
	    			/*
	    			else {
	    				IS_IN_DEBUG_MODE=false;		
	    			}*/			
	        	}	        				
			}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    	}
		}
    	
        initTreeLoader();
    }

	public void initTreeLoader()
	{
		setContentView(R.layout.tree_list_interface);
		
		listOfTreesArrayList = UsbongUtils.getTreeArrayList(UsbongUtils.BASE_FILE_PATH + myTreeDirectory);
		
		mCustomAdapter = new CustomDataAdapter(this, R.layout.tree_loader, listOfTreesArrayList);
		
		treesListView = (ListView)findViewById(R.id.tree_list_view);
		treesListView.setLongClickable(true);
		treesListView.setAdapter(mCustomAdapter);
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.text_to_speech_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (mTts.isSpeaking()) {
			mTts.stop();
		}
		
		StringBuffer sb = new StringBuffer();
		switch(item.getItemId())
		{
			case(R.id.speak):
				switch(currScreen) {
			    	case MULTIPLE_RADIO_BUTTONS_SCREEN:
				        if (USE_UNESCAPE) {
				        	sb.append(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode))+". ");
				        }
				        else {
				        	sb.append(UsbongUtils.trimUsbongNodeName(currUsbongNode)+". ");		        	
				        }
				        int totalRadioButtonsInContainer = radioButtonsContainer.size();
				        for (int i=0; i<totalRadioButtonsInContainer; i++) {
							if (USE_UNESCAPE) {
					            sb.append(StringEscapeUtils.unescapeJava(radioButtonsContainer.elementAt(i)));
					        }
					        else {
					        	sb.append(radioButtonsContainer.elementAt(i).toString());			        	
					        }
				        }		     		        
						break;
			    	case MULTIPLE_CHECKBOXES_SCREEN:
				        if (USE_UNESCAPE) {
				        	sb.append(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode))+". ");
				        }
				        else {
				        	sb.append(UsbongUtils.trimUsbongNodeName(currUsbongNode)+". ");		        	
				        }
				        int totalCheckBoxesInContainer = checkBoxesContainer.size();
				        for (int i=0; i<totalCheckBoxesInContainer; i++) {
							if (USE_UNESCAPE) {
					            sb.append(StringEscapeUtils.unescapeJava(checkBoxesContainer.elementAt(i)));
					        }
					        else {
					        	sb.append(checkBoxesContainer.elementAt(i).toString());			        	
					        }
				        }		     		        
				        break;
			    	case AUDIO_RECORD_SCREEN:
						if (USE_UNESCAPE) {
				        	sb.append(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode))+". ");
				        }
				        else {
				        	sb.append(UsbongUtils.trimUsbongNodeName(currUsbongNode)+". ");		        	
				        }
				        Button recordButton = (Button)findViewById(R.id.record_button);
				        Button stopButton = (Button)findViewById(R.id.stop_button);
				        Button playButton = (Button)findViewById(R.id.play_button);

				        sb.append(recordButton.getText()+". ");
				        sb.append(stopButton.getText()+". ");
				        sb.append(playButton.getText()+". ");
				        break;
			        case PHOTO_CAPTURE_SCREEN:
						if (USE_UNESCAPE) {
				        	sb.append(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode))+". ");
				        }
				        else {
				        	sb.append(UsbongUtils.trimUsbongNodeName(currUsbongNode)+". ");		        	
				        }
			        	break;
					case TEXTFIELD_SCREEN:
					case TEXTFIELD_WITH_UNIT_SCREEN:
						if (USE_UNESCAPE) {
				        	sb.append(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode))+". ");
				        }
				        else {
				        	sb.append(UsbongUtils.trimUsbongNodeName(currUsbongNode)+". ");		        	
				        }
				        break;    	
					case CLASSIFICATION_SCREEN:
				        if (USE_UNESCAPE) {
				        	sb.append(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode))+". ");
				        }
				        else {
				        	sb.append(UsbongUtils.trimUsbongNodeName(currUsbongNode)+". ");		        	
				        }
				        int totalClassificationsInContainer = classificationContainer.size();
				        for (int i=0; i<totalClassificationsInContainer; i++) {
							if (USE_UNESCAPE) {
					            sb.append(StringEscapeUtils.unescapeJava(classificationContainer.elementAt(i)));
					        }
					        else {
					        	sb.append(classificationContainer.elementAt(i).toString());			        	
					        }
				        }		     		        
				        break;    	
					case SPECIAL_SCREEN:
				        if (USE_UNESCAPE) {
				        	sb.append(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode))+". ");
				        }
				        else {
				        	sb.append(UsbongUtils.trimUsbongNodeName(currUsbongNode)+". ");		        	
				        }
					case SPECIAL_IMAGE_SCREEN:
				        break;    	
					case YES_NO_DECISION_SCREEN:
				        if (USE_UNESCAPE) {
				        	sb.append(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode))+". ");
				        }
				        else {
				        	sb.append(UsbongUtils.trimUsbongNodeName(currUsbongNode)+". ");		        	
				        }
				        sb.append(yesStringValue);
				        sb.append(noStringValue);
				        break;    	
					case END_STATE_SCREEN:
				    	if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
							sb.append((String) getResources().getText(R.string.UsbongEndStateTextView_FILIPINO));				    		
				    	}
				    	else { //if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
							sb.append((String) getResources().getText(R.string.UsbongEndStateTextView_ENGLISH));				    						    		
				    	}
				        break;    		
				}
				switch (currLanguageBeingUsed) {
					case UsbongUtils.LANGUAGE_FILIPINO:
						mTts.setLanguage(new Locale("spa", "ESP"));
						mTts.speak(UsbongUtils.convertFilipinoToSpanishAccentFriendlyText(sb.toString()), TextToSpeech.QUEUE_ADD, null); //QUEUE_FLUSH			
						break;
					case UsbongUtils.LANGUAGE_ENGLISH:
						mTts.setLanguage(new Locale("eng", "EN"));
						mTts.speak(sb.toString(), TextToSpeech.QUEUE_ADD, null); //QUEUE_FLUSH			
						break;
					default:
						mTts.setLanguage(new Locale("eng", "EN"));
						mTts.speak(sb.toString(), TextToSpeech.QUEUE_ADD, null); //QUEUE_FLUSH			
						break;
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onInit(int status) {
	}

    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
        else if (requestCode==EMAIL_SENDING_SUCCESS) {
    		finish();    		
			Intent toUsbongMainActivityIntent = new Intent(UsbongDecisionTreeEngineActivity.this, UsbongMainActivity.class);
			toUsbongMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(toUsbongMainActivityIntent);
    		if (mTts!=null) {
    			mTts.shutdown();
    		}
        }
    }
/*    
	public void onDestroy() {
		if (mTts!=null) {
			mTts.shutdown();
		}
	}
*/	
    public static UsbongDecisionTreeEngineActivity getInstance() {
    	return instance;
    }
    
    public static void setMyIntent(Intent i) {
      getInstance().setIntent(i);
    }
    
    public static void setCurrScreen(int cs) {
    	currScreen=cs;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
/*
    	if (getParent()!=null) { //this means that this is a child activity in our TabActivity (i.e. MediaActivity)
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	        	GameActivity.getInstance().decrementCurrScreen();
	            return super.onKeyDown(keyCode, event);
	        }
    	}
*/    	
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onRestart() 
    {
        super.onRestart();
        switch(currScreen) {
        	case PHOTO_CAPTURE_SCREEN:
        		initTakePhotoScreen();
        		break;
        }
        initParser(); 
    }
    
	public void parseYesNoAnswers(XmlPullParser parser) {
		try {
			if (parser.getName().equals("transition")) {				

				//check if the first transition's name is "No"
//				  if (parser.getAttributeValue(1).toString().equals(noStringValue)) {
				  if (parser.getAttributeValue(null,"name").equals(noStringValue)) {
					  nextUsbongNodeIfNo = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();
					  //do two nextTag()'s, because <transition> has a closing tag
					  parser.nextTag();
					  parser.nextTag();						  
					  nextUsbongNodeIfYes = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();
				  }
//				  else if (parser.getAttributeValue(1).toString().equals(yesStringValue)) { // if it is "Yes"
				  else if (parser.getAttributeValue(null,"name").equals(yesStringValue)) {
				  	  nextUsbongNodeIfYes = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();
					  //do two nextTag()'s, because <transition> has a closing tag
					  parser.nextTag();
					  parser.nextTag();						  
					  nextUsbongNodeIfNo = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();						  							  
				  }
				  else { // if it is "Any"
					  nextUsbongNodeIfYes = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();
					  nextUsbongNodeIfNo = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();						  							  							  
				  }				  
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
	}
    
	//Reference: 
	//http://wiki.forum.nokia.com/index.php/How_to_parse_an_XML_file_in_Java_ME_with_kXML ;Last accessed on: June 2,2010
	//http://kxml.sourceforge.net/kxml2/ ;Last accessed on: June 2,2010    
    //http://developer.android.com/reference/org/xmlpull/v1/XmlPullParser.html; last accessed on: Aug. 23, 2011
	public void initParser() {
		hasReachedEndOfAllDecisionTrees=false;
    	String pleaseMakeSureThatInputTxtExistsString = (String) getResources().getText(R.string.pleaseMakeSureThatInputTxtExistsString);
    	String alertString = (String) getResources().getText(R.string.alertStringValue);

		try {
		  XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	      factory.setNamespaceAware(true);
		  XmlPullParser parser = factory.newPullParser();		 		  
		  
		  //if *.xml is blank
		  if (UsbongUtils.getFileFromSDCard(UsbongUtils.BASE_FILE_PATH + myTreeDirectory + myTree + ".xml") == null) { 
	        	new AlertDialog.Builder(UsbongDecisionTreeEngineActivity.this).setTitle(alertString)
				.setMessage(pleaseMakeSureThatInputTxtExistsString)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
			    		finish();    
						Intent toUsbongMainActivityIntent = new Intent(UsbongDecisionTreeEngineActivity.this, UsbongMainActivity.class);
						toUsbongMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
						startActivity(toUsbongMainActivityIntent);
					}
				}).show();	        		        	
	        	return;
		  }
		  else {
			  parser.setInput(UsbongUtils.getFileFromSDCard(UsbongUtils.BASE_FILE_PATH + myTreeDirectory + myTree + ".xml"));	
		  }
		  
		  while(parser.nextTag() != XmlPullParser.END_DOCUMENT) {
			  //if this tag does not have an attribute; e.g. END_TAG
			  if (parser.getAttributeCount()==-1) {
				  continue;
			  }
			  //if this is the first process-definition tag
			  else if (parser.getAttributeCount()>1) { 
				  if ((currUsbongNode.equals("")) && (parser.getName().equals("process-definition"))) {
					  currLanguageBeingUsed=UsbongUtils.getLanguageID(parser.getAttributeValue(null, "lang"));
					  System.out.println("currLanguageBeingUsed: "+currLanguageBeingUsed);				  
				  }
				  continue;
			  }
			  else if (parser.getAttributeCount()==1) {
				  //if currUsbongNode is still blank
				  //getName() is not the name attribute, but the name of the tag (e.g. start-state, decision-node, task-node)
				  if ((currUsbongNode.equals("")) && (parser.getName().equals("start-state"))) {
					  //the next tag would be transition
					  //Example:
				      //<start-state name="start-state1">
					  //  <transition to="Is there tender swelling behind the ear?"></transition>
					  //</start-state>

					  //do two next()'s to skip the "\n", and proceed to <transition>...
//					  parser.next();
//					  parser.next();
					  //or do one nextTag();
					  parser.nextTag();
					  currUsbongNode=parser.getAttributeValue(0).toString();
					  
					  isFirstQuestionForDecisionTree=true;
					  continue;
				  }
				  if ((!currUsbongNode.equals("")) && (parser.getAttributeValue(0).toString().equals(currUsbongNode)) &&
						  !(parser.getName().equals("transition"))) { //make sure that the tag is not a transition node 
				      if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
				    	noStringValue = (String) getResources().getText(R.string.noStringValueFilipino);
				    	yesStringValue = (String) getResources().getText(R.string.yesStringValueFilipino);
				      }
				      else { //if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
				    	noStringValue = (String) getResources().getText(R.string.noStringValueEnglish); //noStringValue
				    	yesStringValue = (String) getResources().getText(R.string.yesStringValueEnglish); //yesStringValue    		
					  }						  

					  //if this is a decision tag and the next tag is transition...
					  //Example:
					  //<decision name="Is there tender swelling behind the ear?">
					  //  <transition to="Is there pus draining from the ear?" name="No"></transition>
					  //  <transition to="MASTOIDITIS" name="Yes"></transition>
					  //</decision>
					  if (parser.getName().equals("decision")) {//transition")) {//(parser.getAttributeCount()>1) {
						  currScreen=YES_NO_DECISION_SCREEN;
						  parser.nextTag(); //go to the next tag
						  parseYesNoAnswers(parser);
					  }
					  else if (parser.getName().equals("end-state")) { 
						  //temporarily do this
						  currScreen=END_STATE_SCREEN;
					  }
					  else if (parser.getName().equals("task-node")) { 
						  	StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
							String myStringToken = st.nextToken();
							if (myStringToken.equals(currUsbongNode)) {//if this is the task-node for classification and treatment/management plan								
								//<task-node name="SOME DEHYDRATION">
								//	<task name="Give fluid, zinc supplements and food for some dehydration (Plan B)"></task>
								//	<task name="If child also has a severe classification: 1) Refer URGENTLY to hospital with mother giving frequent sips of ORS on the way, 2) Advise the mother to continue breastfeeding"></task>
								//	<task name="Advise mother when to return immediately"></task>
								//	<task name="Follow-up in 5 days if not improving"></task>
								//	<transition to="end-state1" name="to-end"></transition>
								//</task-node>
								parser.nextTag(); //go to task tag
								currScreen = CLASSIFICATION_SCREEN;
								classificationContainer.removeAllElements();
								while(!parser.getName().equals("transition")) {
									  classificationContainer.addElement(parser.getAttributeValue(0).toString());
									  //do two nextTag()'s, because <task> has a closing tag
									  parser.nextTag();
									  parser.nextTag();		
								}
								nextUsbongNodeIfYes = parser.getAttributeValue(0).toString();
								nextUsbongNodeIfNo = parser.getAttributeValue(0).toString();						  							  							  
							}
							else { //this is a task-node that has "~"								
								if (myStringToken.equals("radioButtons")) {
									//<task-node name="radioButtons~1~Life is good">
									//	<task name="Strongly Disagree"></task>
									//	<task name="Moderately Disagree"></task>
									//	<task name="Slightly Disagree"></task>
									//	<task name="Slightly Agree"></task>
									//	<task name="Moderately Agree"></task>
									//	<task name="Strongly Agree"></task>
									//	<transition to="radioButtons~1~I don't think that the world is a good place" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to task tag

									//radioButtons by definition requires only 1 ticked button in the group
									//requiredTotalCheckedBoxes = Integer.parseInt(st.nextToken());
																		
									String t=st.nextToken();
									//do this so that we are sure that the token that we are going to check is the last token
									while(st.hasMoreTokens()) {
										t=st.nextToken();
									}
									currScreen=MULTIPLE_RADIO_BUTTONS_SCREEN;
									
									radioButtonsContainer.removeAllElements();
									while(!parser.getName().equals("transition")) {
										  radioButtonsContainer.addElement(parser.getAttributeValue(0).toString());
										  
										  //do two nextTag()'s, because <task> has a closing tag
										  parser.nextTag();
										  parser.nextTag();		
									}
									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("checkList")) {
									//<task-node name="checkList~2~Check for SEVERE DEHYDRATION. Has at least two (2) of the following signs:">
									//	<task name="Is lethargic or unconscious."></task>
									//	<task name="Has sunken eyes."></task>
									//	<task name="Is not able to drink or is drinking poorly."></task>
									//	<task name="Skin pinch goes back VERY slowly (longer than 2 seconds)."></task>
									//	<transition to="SEVERE DEHYDRATION" name="Yes"></transition>
									//	<transition to="checkList-Check for SOME DEHYDRATION. Has at least two (2) of the following signs:" name="No"></transition>
									//</task-node>
									parser.nextTag(); //go to task tag
									requiredTotalCheckedBoxes = Integer.parseInt(st.nextToken());
																		
									String t=st.nextToken();
									//do this so that we are sure that the token that we are going to check is the last token
									while(st.hasMoreTokens()) {
										t=st.nextToken();
									}
/*									
									//added by Mike, Aug. 20, 2010; st.nextToken()
									if (t.equals(GENERAL_DANGER_SIGNS_CHECKBOX_QUESTION)) { //Does the question pertain to general danger signs?
										isAGeneralDangerSignsCheckBox=true;
									}
									//added by Mike, Sept. 17, 2010
									else if (t.equals(SOME_DEHYDRATION_CHECKBOX_QUESTION)) {
										isSomeDehydrationCheckBox=true;
									}
									else if (t.equals(SEVERE_DEHYDRATION_CHECKBOX_QUESTION)) {
										isSevereDehydrationCheckBox=true;
									}
*/									
									currScreen=MULTIPLE_CHECKBOXES_SCREEN;

									checkBoxesContainer.removeAllElements();
									while(!parser.getName().equals("transition")) {
										  checkBoxesContainer.addElement(parser.getAttributeValue(0).toString());
										  
										  //do two nextTag()'s, because <task> has a closing tag
										  parser.nextTag();
										  parser.nextTag();		
									}
									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("textField")) { 
									//<task-node name="textField~For how many days?">
									//  <transition to="Does the child have wheezing? (child must be calm)" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=TEXTFIELD_SCREEN;
									
									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("textFieldWithUnit")) { 
									//<task-node name="textField~Days~For how many days?">
									//  <transition to="Does the child have wheezing? (child must be calm)" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									textFieldUnit = st.nextToken();									
									currScreen=TEXTFIELD_WITH_UNIT_SCREEN;
									
									parseYesNoAnswers(parser);
								}																								
								else if (myStringToken.equals("special")) { //special?
									//<task-node name="special~Give a trial of rapid acting inhaled bronchodilator for up to 3 times 15-20 minutes apart. Count the breaths and look for chest indrawing again, and then classify.">
									//  <transition to="Does the child have wheezing? (child must be calm)" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=SPECIAL_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("specialImage")) { //special?
									//<task-node name="special~Give a trial of rapid acting inhaled bronchodilator for up to 3 times 15-20 minutes apart. Count the breaths and look for chest indrawing again, and then classify.">
									//  <transition to="Does the child have wheezing? (child must be calm)" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=SPECIAL_IMAGE_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("photoCapture")) { 
									parser.nextTag(); //go to transition tag
									currScreen=PHOTO_CAPTURE_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("audioRecord")) { 
									parser.nextTag(); //go to transition tag
									currScreen=AUDIO_RECORD_SCREEN;

									parseYesNoAnswers(parser);
								}

							}
					  }
					  else { //this is a currIMCICaseList number
						usbongNodeContainerCounter++;
						currUsbongNode=(String)usbongNodeContainer.elementAt(usbongNodeContainerCounter);
						continue;
					  }
					  break;
				  }
				  //TODO dosage guide/table
				  //It would be great if someone adds the above TODO as well.
			  }
		  }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if (!usedBackButton) {
			usbongNodeContainer.addElement(currUsbongNode);
			usbongNodeContainerCounter++;
		}
		else {
			usedBackButton=false;
		}
		initUsbongScreen();
	}

	public void initUsbongScreen() {		
		switch(currScreen) {
	    	case MULTIPLE_RADIO_BUTTONS_SCREEN:
		    	setContentView(R.layout.multiple_radio_buttons_screen);
		        initBackNextButtons();
		        TextView myMultipleRadioButtonsScreenTextView = (TextView)findViewById(R.id.radio_buttons_textview);
		        if (USE_UNESCAPE) {
		        	myMultipleRadioButtonsScreenTextView.setText(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode)));
		        }
		        else {
		        	myMultipleRadioButtonsScreenTextView.setText(UsbongUtils.trimUsbongNodeName(currUsbongNode));		        	
		        }
		        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.multiple_radio_buttons_radiogroup);
		        int totalRadioButtonsInContainer = radioButtonsContainer.size();
		        for (int i=0; i<totalRadioButtonsInContainer; i++) {
		            RadioButton radioButton = new RadioButton(getBaseContext());
			        if (USE_UNESCAPE) {
			        	radioButton.setText(StringEscapeUtils.unescapeJava(radioButtonsContainer.elementAt(i).toString()));
			        }
			        else {
			        	radioButton.setText(radioButtonsContainer.elementAt(i).toString());			        	
			        }
			        radioButton.setChecked(false);
		            radioButton.setTextSize(20);
		            radioButton.setId(i);
/*		            
		            radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub
								
							}});
*/		            
		            radioGroup.addView(radioButton);
		        }		     		        
				break;
	    	case MULTIPLE_CHECKBOXES_SCREEN:
		    	setContentView(R.layout.multiple_checkboxes_screen);
		        initBackNextButtons();
		        TextView myMultipleCheckBoxesScreenTextView = (TextView)findViewById(R.id.checkboxes_textview);
		        if (USE_UNESCAPE) {
		        	myMultipleCheckBoxesScreenTextView.setText(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode)));
		        }
		        else {
		        	myMultipleCheckBoxesScreenTextView.setText(UsbongUtils.trimUsbongNodeName(currUsbongNode));		        	
		        }
		        LinearLayout myMultipleCheckboxesLinearLayout = (LinearLayout)findViewById(R.id.multiple_checkboxes_linearlayout);
		        int totalCheckBoxesInContainer = checkBoxesContainer.size();
		        for (int i=0; i<totalCheckBoxesInContainer; i++) {
		            CheckBox checkBox = new CheckBox(getBaseContext());
		            checkBox.setText(StringEscapeUtils.unescapeJava(checkBoxesContainer.elementAt(i).toString()));
		            checkBox.setChecked(false);
		            checkBox.setTextSize(20);
		            myMultipleCheckboxesLinearLayout.addView(checkBox);
		        }		     		        
		        break;
	    	case AUDIO_RECORD_SCREEN:
		        setContentView(R.layout.audio_recorder_screen);
		        initRecordAudioScreen();
		        initBackNextButtons();

		        TextView myAudioRecorderTextView = (TextView)findViewById(R.id.audio_recorder_textview);
		        if (USE_UNESCAPE) {
		        	myAudioRecorderTextView.setText(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode)));
		        }
		        else {
		        	myAudioRecorderTextView.setText(UsbongUtils.trimUsbongNodeName(currUsbongNode));		        	
		        }
		        break;
	        case PHOTO_CAPTURE_SCREEN:
		    	setContentView(R.layout.photo_capture_screen);
		    	if (!performedCapturePhoto) {
 		    	  initTakePhotoScreen();
		    	}
		    	initBackNextButtons();

		        TextView myPhotoCaptureScreenTextView = (TextView)findViewById(R.id.photo_capture_textview);
		        if (USE_UNESCAPE) {
		        	myPhotoCaptureScreenTextView.setText(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode)));
		        }
		        else {
		        	myPhotoCaptureScreenTextView.setText(UsbongUtils.trimUsbongNodeName(currUsbongNode));		        	
		        }		    	
		    	break;		    	
			case TEXTFIELD_SCREEN:
		    	setContentView(R.layout.textfield_screen);
		        initBackNextButtons();

		        TextView myTextFieldScreenTextView = (TextView)findViewById(R.id.textfield_textview);
		        if (USE_UNESCAPE) {
		        	myTextFieldScreenTextView.setText(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode)));
		        }
		        else {
		        	myTextFieldScreenTextView.setText(UsbongUtils.trimUsbongNodeName(currUsbongNode));		        	
		        }
		        break;    	
			case TEXTFIELD_WITH_UNIT_SCREEN:
		    	setContentView(R.layout.textfield_with_unit_screen);
		        initBackNextButtons();

		        TextView myTextFieldWithUnitScreenTextView = (TextView)findViewById(R.id.textfield_textview);
		        if (USE_UNESCAPE) {
		        	myTextFieldWithUnitScreenTextView.setText(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode)));
		        }
		        else {
		        	myTextFieldWithUnitScreenTextView.setText(UsbongUtils.trimUsbongNodeName(currUsbongNode));		        	
		        }
		        
		        TextView myEditText = (TextView)findViewById(R.id.textfield_edittext);
		        myEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

		        TextView myUnitScreenTextView = (TextView)findViewById(R.id.textfieldunit_textview);
		        myUnitScreenTextView.setText(textFieldUnit);		        		        
		        break;    	
			case CLASSIFICATION_SCREEN:
		    	setContentView(R.layout.classification_screen);
		        initBackNextButtons();
		        TextView myClassificationScreenTextView = (TextView)findViewById(R.id.classification_textview);
		        if (USE_UNESCAPE) {
		        	myClassificationScreenTextView.setText(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode)));
		        }
		        else {
		        	myClassificationScreenTextView.setText(UsbongUtils.trimUsbongNodeName(currUsbongNode));		        	
		        }
		        
		        LinearLayout myClassificationLinearLayout = (LinearLayout)findViewById(R.id.classification_linearlayout);
		        int totalClassificationsInContainer = classificationContainer.size();
		        for (int i=0; i<totalClassificationsInContainer; i++) {
		            TextView myTextView = new TextView(getBaseContext());
		        	int bulletCount = i+1;
		            if (USE_UNESCAPE) {
			        	myTextView.setText(bulletCount+") "+StringEscapeUtils.unescapeJava(classificationContainer.elementAt(i).toString()));
			        }
			        else {
			        	myTextView.setText(bulletCount+") "+UsbongUtils.trimUsbongNodeName(classificationContainer.elementAt(i).toString()));		        	
			        }
			        myTextView.setTextSize(24);
			        myTextView.setTextColor(Color.WHITE);
		            myClassificationLinearLayout.addView(myTextView);
		        }		     		        
		        break;    	

			case SPECIAL_SCREEN:
		    	setContentView(R.layout.special_screen);
		        initBackNextButtons();

		        TextView mySpecialScreenTextView = (TextView)findViewById(R.id.special_textview);
		        if (USE_UNESCAPE) {
		        	mySpecialScreenTextView.setText(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode)));
		        }
		        else {
		        	mySpecialScreenTextView.setText(UsbongUtils.trimUsbongNodeName(currUsbongNode));		        	
		        }
		        break;    	
			case SPECIAL_IMAGE_SCREEN:
		    	setContentView(R.layout.special_image_screen);
		        initBackNextButtons();
		        ImageView mySpecialScreenImageView = (ImageView)findViewById(R.id.special_imageview);		       
		        
		        //Reference: http://www.anddev.org/tinytut_-_get_resources_by_name__getidentifier_-t460.html; last accessed 14 Sept 2011
		        Resources myRes = getResources();
		        Drawable myDrawableImage = myRes.getDrawable(myRes.getIdentifier(UsbongUtils.trimUsbongNodeName(currUsbongNode), "drawable", "edu.ateneo.android"));
		        mySpecialScreenImageView.setImageDrawable(myDrawableImage);		        
		        break;    	
			case YES_NO_DECISION_SCREEN:
		    	setContentView(R.layout.yes_no_decision_screen);
		        initBackNextButtons();

		        TextView myYesNoDecisionScreenTextView = (TextView)findViewById(R.id.yes_no_decision_textview);
		        if (USE_UNESCAPE) {
		        	myYesNoDecisionScreenTextView.setText(StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(currUsbongNode)));
		        }
		        else {
		        	myYesNoDecisionScreenTextView.setText(UsbongUtils.trimUsbongNodeName(currUsbongNode));		        	
		        }		    	
		        RadioButton myYesRadioButton = (RadioButton)findViewById(R.id.yes_radiobutton);
		        myYesRadioButton.setText(yesStringValue);
		        RadioButton myNoRadioButton = (RadioButton)findViewById(R.id.no_radiobutton);		        
		        myNoRadioButton.setText(noStringValue);
		        break;    	
			case END_STATE_SCREEN:
		    	setContentView(R.layout.end_state_screen);

		        TextView endStateTextView = (TextView)findViewById(R.id.end_state_textview);
		    	if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
		    		endStateTextView.setText((String) getResources().getText(R.string.UsbongEndStateTextView_FILIPINO));				    		
		    	}
		    	else { //if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
		    		endStateTextView.setText((String) getResources().getText(R.string.UsbongEndStateTextView_ENGLISH));				    						    		
		    	}
		    	initBackNextButtons();
		        break;    	

    	}

	}
   
    public void initBackNextButtons()
    {
    	initBackButton();
    	initNextButton();
    }

    public void initBackButton()
    {
    	backButton = (Button)findViewById(R.id.back_button);
    	backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTts.isSpeaking()) {
					mTts.stop();
				}

				usedBackButton=true;
				
				if (!usbongAnswerContainer.isEmpty()) {
					usbongAnswerContainer.removeElementAt(usbongAnswerContainer.size()-1);
				}
				
                usbongNodeContainer.removeElementAt(usbongNodeContainerCounter);                            
                usbongNodeContainerCounter--;
                if (usbongNodeContainerCounter>=0) {
                        currUsbongNode=(String)usbongNodeContainer.elementAt(usbongNodeContainerCounter);
                }
                else { 
                	//return to main activity
		    		finish();    
					Intent toUsbongMainActivityIntent = new Intent(UsbongDecisionTreeEngineActivity.this, UsbongMainActivity.class);
					toUsbongMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
					startActivity(toUsbongMainActivityIntent);
                }
                initParser();
                return;                
			}
    	});    
    }

    public void initNextButton()
    {
    	nextButton = (Button)findViewById(R.id.next_button);
    	nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTts.isSpeaking()) {
					mTts.stop();
				}

		    	//END_STATE_SCREEN = last screen
		    	if (currScreen==END_STATE_SCREEN) {
		    		//"save" the output into the SDCard as "output.txt"
		    		int usbongAnswerContainerSize = usbongAnswerContainer.size();
		    		StringBuffer outputStringBuffer = new StringBuffer();
		    		for(int i=0; i<usbongAnswerContainerSize;i++) {
		    			outputStringBuffer.append(usbongAnswerContainer.elementAt(i));
		    		}
		    		
		    		System.out.println(">>>>>>>>>>>>> outputStringBuffer: " + outputStringBuffer.toString());
		    		UsbongUtils.storeOutputInSDCard(UsbongUtils.BASE_FILE_PATH + myOutputDirectory + UsbongUtils.getTimeStamp() + ".csv", outputStringBuffer.toString());
		    		//send to server
		    		UsbongUtils.performFileUpload(UsbongUtils.BASE_FILE_PATH + myOutputDirectory + UsbongUtils.getTimeStamp() + ".csv");

		    		//send to email
		    		Intent emailIntent = UsbongUtils.performEmailProcess(UsbongUtils.BASE_FILE_PATH + myOutputDirectory + UsbongUtils.getTimeStamp() + ".csv");
		    		emailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		    		emailIntent.addFlags(RESULT_OK);
		    		startActivityForResult(Intent.createChooser(emailIntent, "Email:"),EMAIL_SENDING_SUCCESS);

/*
		    		if (emailIntent.getFlags()==RESULT_OK) {
			    		finish();    		
						Intent toUsbongMainActivityIntent = new Intent(UsbongDecisionTreeEngineActivity.this, UsbongMainActivity.class);
						toUsbongMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
						startActivity(toUsbongMainActivityIntent);					
		    		}
*/		    		
		    	}
		    	else {			
		    		if (currScreen==YES_NO_DECISION_SCREEN) {
				        RadioButton myYesRadioButton = (RadioButton)findViewById(R.id.yes_radiobutton);
				        RadioButton myNoRadioButton = (RadioButton)findViewById(R.id.no_radiobutton);

				        if (myYesRadioButton.isChecked()) {
							currUsbongNode = nextUsbongNodeIfYes;
							usbongAnswerContainer.addElement("Yes;");															
							initParser();								        	
				        }
				        else if (myNoRadioButton.isChecked()) {
							currUsbongNode = nextUsbongNodeIfNo; 
							usbongAnswerContainer.addElement("No;");															
							initParser();								        					        	
				        }
				        else { //if no radio button was checked				        	
			    				showPleaseAnswerAlert();
				        }
		    		}	
		    		else if (currScreen==MULTIPLE_CHECKBOXES_SCREEN) {
//			    		requiredTotalCheckedBoxes	
				        LinearLayout myMultipleCheckboxesLinearLayout = (LinearLayout)findViewById(R.id.multiple_checkboxes_linearlayout);
				        StringBuffer sb = new StringBuffer();
				        int totalCheckedBoxes=0;
				        int totalCheckBoxChildren = myMultipleCheckboxesLinearLayout.getChildCount();
				        //begin with i=1, because i=0 is for checkboxes_textview
				        for(int i=1; i<totalCheckBoxChildren; i++) {
				        	if (((CheckBox)myMultipleCheckboxesLinearLayout.getChildAt(i)).isChecked()) {
				        		totalCheckedBoxes++;
				        		sb.append(","+(i-1)); //do a (i-1) so that i starts with 0 for the checkboxes (excluding checkboxes_textview) to maintain consistency with the other components
				        	}
				        }
				        
				        if (totalCheckedBoxes>=requiredTotalCheckedBoxes) {
							currUsbongNode = nextUsbongNodeIfYes; 	
			        		sb.insert(0,"Y"); //insert in front of stringBuffer
							sb.append(";");
				        }
				        else {
							currUsbongNode = nextUsbongNodeIfNo; 				        					        	
							sb.delete(0,sb.length());
							sb.append("N;");
				        }				        
		    			usbongAnswerContainer.addElement(sb.toString());
				        initParser();
		    		}
		    		
		    		else if (currScreen==MULTIPLE_RADIO_BUTTONS_SCREEN) {
						currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
		    			RadioGroup myRadioGroup = (RadioGroup)findViewById(R.id.multiple_radio_buttons_radiogroup);				        				        		    			

		    			if (IS_IN_DEBUG_MODE==false) {
			    			if (myRadioGroup.getCheckedRadioButtonId()==-1) { //no radio button checked
			    				showPleaseAnswerAlert();
			    			}
			    			else {
				    			usbongAnswerContainer.addElement(myRadioGroup.getCheckedRadioButtonId()+";");
						        initParser();
			    			}
		    			}
		    			else {
			    			usbongAnswerContainer.addElement(myRadioGroup.getCheckedRadioButtonId()+";");
					        initParser();		    				
		    			}
		    		}
		    		else if ((currScreen==TEXTFIELD_SCREEN) 
		    				|| (currScreen==TEXTFIELD_WITH_UNIT_SCREEN)) {
		    			currUsbongNode = nextUsbongNodeIfYes; //= nextIMCIQuestionIfNo will also do
				        TextView myTextFieldScreenEditText = (TextView)findViewById(R.id.textfield_edittext);

				        if (IS_IN_DEBUG_MODE==false) {
					        //if it's blank
			    			if (myTextFieldScreenEditText.getText().toString().trim().equals("")) {
			    				showPleaseAnswerAlert();
	
							}
							else {
								usbongAnswerContainer.addElement(myTextFieldScreenEditText.getText()+";");							
								initParser();
							}
				        }
				        else {
							usbongAnswerContainer.addElement(myTextFieldScreenEditText.getText()+";");							
							initParser();				        	
				        }
		    		}
		    		else { //TODO: do this for now
						currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
						usbongAnswerContainer.addElement("Any;");															
						initParser();				
		    		}
		    	}
			}
    	});
    }
    
    public void initRecordAudioScreen() {
        String timeStamp = UsbongUtils.getTimeStamp();
        
        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setEnabled(false); 
        recordButton = (Button) findViewById(R.id.record_button);
        playButton = (Button) findViewById(R.id.play_button);
        playButton.setEnabled(false); 
        final AudioRecorder recorder = new AudioRecorder("/usbong/" + timeStamp);

        // add a click listener to the button
        recordButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
					recorder.start();
					stopButton.setEnabled(true);
					recordButton.setEnabled(false);
			        playButton.setEnabled(false);
				} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        });    
 

	    // add a click listener to the button
        stopButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	try {
					recorder.stop();
					recordButton.setEnabled(true); 
					playButton.setEnabled(true);
					stopButton.setEnabled(false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });    

        // add a click listener to the button
        playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	try {
					recorder.play();
					recordButton.setEnabled(true); 
					//play.setEnabled(false);
					stopButton.setEnabled(false);
            	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });    
	    	
	    new AlertDialog.Builder(UsbongDecisionTreeEngineActivity.this).setTitle("Usbong Tip")
     	.setMessage("Press the Record button to start recording. Press Stop to stop the recording. And press Play to hear what you've recorded!") // When you're done, hit the menu button and save your work.
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();		
    }
    
    public void initTakePhotoScreen()
    {
    	myPictureName=currUsbongNode; //make the name of the picture the name of the currUsbongNode

		String path = "/sdcard/usbong/"+ UsbongUtils.getTimeStamp() +"/"+ myPictureName +".jpg";
    	setContentView(R.layout.photo_capture_screen);
    	myImageView = (ImageView) findViewById(R.id.CameraImage);

    	File imageFile = new File(path);
        
        if(imageFile.exists())
        {
        	Bitmap myBitmap = BitmapFactory.decodeFile(path);
        	if(myBitmap != null)
        	{
        		myImageView.setImageBitmap(myBitmap);
        	}
        	//Read more: http://www.brighthub.com/mobile/google-android/articles/64048.aspx#ixzz0yXLCazcU                	  
        }
        else
        {        	
        }
    	photoCaptureButton = (Button)findViewById(R.id.photo_capture_button);
		photoCaptureIntent = new Intent().setClass(this, CameraActivity.class);
		photoCaptureIntent.putExtra("myPictureName",myPictureName);
		photoCaptureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(UsbongDecisionTreeEngineActivity.photoCaptureIntent);
			}
    	});

    }


    public void decrementCurrScreen() {
    	currScreen--;
    	initUsbongScreen();
    }

    //this is not used at the moment (Sept 5, 2011)
    public void incrementCurrScreen() {
    	currScreen++;
    	initUsbongScreen();
    }
    
	private class CustomDataAdapter extends ArrayAdapter<String>
	{
		private ArrayList<String> items;
		
		public CustomDataAdapter(Context context, int textViewResourceId, ArrayList<String> items) {
            super(context, textViewResourceId, items);
            this.items = items;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.tree_loader, null);
                }
                final String o = items.get(position);
                if (o != null) {
                	TextView dataCurrentTextView = (TextView)v.findViewById(R.id.tree_item);
                	dataCurrentTextView.setText(o.toString());
                	
                	Button selectButton = (Button)v.findViewById(R.id.select_tree_button);
                	selectButton.setOnClickListener(new OnClickListener() {
            			@Override
            			public void onClick(View v) {
            				myTree = o.toString();
            		        initParser();
            			}
                	});
                }
                return v;
        }
	}
	
	public void showPleaseAnswerAlert() {
		String pleaseChooseAnAnswerString = (String) getResources().getText(R.string.pleaseChooseAnAnswerStringValue);
    	String alertString = (String) getResources().getText(R.string.alertStringValue);

    	if (USE_UNESCAPE) {
    		pleaseChooseAnAnswerString = StringEscapeUtils.unescapeJava(pleaseChooseAnAnswerString);
    		alertString = StringEscapeUtils.unescapeJava(alertString);
    	}
    	
    	new AlertDialog.Builder(UsbongDecisionTreeEngineActivity.this).setTitle(alertString)
		.setMessage(pleaseChooseAnAnswerString)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}
}