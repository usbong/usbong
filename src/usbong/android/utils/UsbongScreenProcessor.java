/*
 * Copyright 2012-2013 Michael Syson
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
package usbong.android.utils;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringEscapeUtils;

import usbong.android.R;
import usbong.android.UsbongDecisionTreeEngineActivity;
import usbong.android.utils.FedorMyLocation.LocationResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

/*
 * This class was created to reduce the length (i.e. number of lines of code) 
 * in UsbongDecisionTreeEngineAcitivity. As its name implies, this class
 * handles the processing of the Usbong screens.
 */
public class UsbongScreenProcessor
{    
	private UsbongDecisionTreeEngineActivity udtea;
	private LocationResult locationResult;
	
	public static String myLatitude;
	public static String myLongitude;
		
	public TextView myLongitudeTextView;
	public TextView myLatitudeTextView;
	
	public ProgressBar myLoadingProgressBar;
	
	public boolean hasGottenGPSLocation;
	
	public UsbongScreenProcessor(){		
	}
	
	public UsbongScreenProcessor(Activity a) {
        udtea = (UsbongDecisionTreeEngineActivity) a;	
//        this.setInstance((UsbongDecisionTreeEngineActivity) a);	
	}
	
	public LocationResult getLocationResult() {
		return locationResult;
	}
	
    /*
     * Initialize processor
     */
    public void init()
    {
		//Reference: http://www.anddev.org/tinytut_-_get_resources_by_name__getidentifier_-t460.html; last accessed 14 Sept 2011
        Resources myRes = udtea.getResources();
        Drawable myDrawableImage;
        
        //added by Mike, Feb. 13, 2013
        udtea.isAnOptionalNode = UsbongUtils.isAnOptionalNode(udtea.currUsbongNode);

        String myStringToken="";
//		if (usedBackButton) {
        
//        System.out.println(">>>>>> udtea.currAnswer: "+udtea.currAnswer);
        
    		StringTokenizer st = new StringTokenizer(udtea.currAnswer, ",");
    		if ((st != null) && (st.hasMoreTokens())) {
	    		myStringToken = st.nextToken();
	    		udtea.currAnswer = udtea.currAnswer.replace(myStringToken+",", "");
    		}	    		
	    		
    		StringTokenizer st_two = new StringTokenizer(udtea.currAnswer, ";");
	    		
	    	if (st_two!=null) {
    			if (udtea.currAnswer.length()>1) {
    				myStringToken = st_two.nextToken(); //get next element (i.e. 1 in "Y,1;")	    			
    			}
    			else {
    				myStringToken="";
    			}	    			
    		}

		if (udtea.currScreen == udtea.MULTIPLE_RADIO_BUTTONS_SCREEN) {
			udtea.setContentView(R.layout.multiple_radio_buttons_screen);
			udtea.initBackNextButtons();
			TextView myMultipleRadioButtonsScreenTextView = (TextView)udtea.findViewById(R.id.radio_buttons_textview);
			myMultipleRadioButtonsScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myMultipleRadioButtonsScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			RadioGroup radioGroup = (RadioGroup)udtea.findViewById(R.id.multiple_radio_buttons_radiogroup);
			int totalRadioButtonsInContainer = udtea.radioButtonsContainer.size();
			for (int i=0; i<totalRadioButtonsInContainer; i++) {
			    View radioButtonView = new RadioButton(udtea.getBaseContext());
			    RadioButton radioButton = (RadioButton) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), radioButtonView, UsbongUtils.IS_RADIOBUTTON, udtea.radioButtonsContainer.elementAt(i).toString());
			    radioButton.setTextSize(20);
			    radioButton.setId(i);
			    radioButton.setTextColor(Color.parseColor("#4a452a"));			        

			    int myStringTokenInt;
			    try {
			    	myStringTokenInt = Integer.parseInt(myStringToken);
			    }		            
			    catch (NumberFormatException e) {//if myStringToken is not an int;
			    	myStringTokenInt=-1;
				}
			    
			    if ((!myStringToken.equals("")) && (i == myStringTokenInt)) {
			    	radioButton.setChecked(true);
			    }
			    else {
			        radioButton.setChecked(false);
			    }
			    
			    radioGroup.addView(radioButton);
			}
		} else if (udtea.currScreen == udtea.MULTIPLE_RADIO_BUTTONS_WITH_ANSWER_SCREEN) {
			udtea.setContentView(R.layout.multiple_radio_buttons_screen);
			udtea.initBackNextButtons();
			String myMultipleRadioButtonsWithAnswerScreenStringToken = "";
			//    			Log.d(">>>>>>>>udtea.currUsbongNode", udtea.currUsbongNode);
			udtea.currUsbongNodeWithoutAnswer=udtea.currUsbongNode.replace("Answer=", "~");
			StringTokenizer myMultipleRadioButtonsWithAnswerScreenStringTokenizer = new StringTokenizer(udtea.currUsbongNodeWithoutAnswer, "~");
			if (myMultipleRadioButtonsWithAnswerScreenStringTokenizer != null) {
				myMultipleRadioButtonsWithAnswerScreenStringToken = myMultipleRadioButtonsWithAnswerScreenStringTokenizer.nextToken();
				
				while (myMultipleRadioButtonsWithAnswerScreenStringTokenizer.hasMoreTokens()) { //get last element (i.e. 0 in "radioButtonsWithAnswer~You see your teacher approaching you. What do you do?Answer=0")
					myMultipleRadioButtonsWithAnswerScreenStringToken = myMultipleRadioButtonsWithAnswerScreenStringTokenizer.nextToken(); 
				}
			}
			udtea.myMultipleRadioButtonsWithAnswerScreenAnswer=myMultipleRadioButtonsWithAnswerScreenStringToken.toString();
//    			Log.d(">>>>>>>>udtea.myMultipleRadioButtonsWithAnswerScreenAnswer", udtea.myMultipleRadioButtonsWithAnswerScreenAnswer);
			udtea.currUsbongNodeWithoutAnswer=udtea.currUsbongNodeWithoutAnswer.substring(0, udtea.currUsbongNodeWithoutAnswer.length()-udtea.myMultipleRadioButtonsWithAnswerScreenAnswer.length()-1); //do a -1 for the last tilde    			
//    			Log.d(">>>>>>>>udtea.currUsbongNodeWithoutAnswer", udtea.currUsbongNodeWithoutAnswer);
			TextView myMultipleRadioButtonsWithAnswerScreenTextView = (TextView)udtea.findViewById(R.id.radio_buttons_textview);
			myMultipleRadioButtonsWithAnswerScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myMultipleRadioButtonsWithAnswerScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNodeWithoutAnswer);
			RadioGroup myMultipleRadioButtonsWithAnswerRadioGroup = (RadioGroup)udtea.findViewById(R.id.multiple_radio_buttons_radiogroup);
			int myMultipleRadioButtonsWithAnswerTotalRadioButtonsInContainer = udtea.radioButtonsContainer.size();
			for (int i=0; i<myMultipleRadioButtonsWithAnswerTotalRadioButtonsInContainer; i++) {
			    View radioButtonView = new RadioButton(udtea.getBaseContext());
			    RadioButton radioButton = (RadioButton) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), radioButtonView, UsbongUtils.IS_RADIOBUTTON, udtea.radioButtonsContainer.elementAt(i).toString());
			    radioButton.setTextSize(20);
			    radioButton.setId(i);
			    radioButton.setTextColor(Color.parseColor("#4a452a"));			        

			    if ((!myStringToken.equals("")) && (i == Integer.parseInt(myStringToken))) {
			    	radioButton.setChecked(true);
			    }
			    else {
			        radioButton.setChecked(false);
			    }
			    
			    myMultipleRadioButtonsWithAnswerRadioGroup.addView(radioButton);
			}
		} else if (udtea.currScreen == udtea.LINK_SCREEN) {
			//use same contentView as multiple_radio_buttons_screen
			udtea.setContentView(R.layout.multiple_radio_buttons_screen);
			udtea.initBackNextButtons();
			TextView myLinkScreenTextView = (TextView)udtea.findViewById(R.id.radio_buttons_textview);
			myLinkScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myLinkScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);

			RadioGroup myLinkScreenRadioGroup = (RadioGroup)udtea.findViewById(R.id.multiple_radio_buttons_radiogroup);
			int myLinkScreenTotalRadioButtonsInContainer = udtea.radioButtonsContainer.size();
			for (int i=0; i<myLinkScreenTotalRadioButtonsInContainer; i++) {
			    View radioButtonView = new RadioButton(udtea.getBaseContext());
			    RadioButton radioButton = (RadioButton) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), radioButtonView, UsbongUtils.IS_RADIOBUTTON, UsbongUtils.trimUsbongNodeName(udtea.radioButtonsContainer.elementAt(i).toString()));

				Log.d(">>>>>radioButton",radioButton.getText().toString());

//		            radioButton.setChecked(false);
			    radioButton.setTextSize(20);
			    radioButton.setId(i);
			    radioButton.setTextColor(Color.parseColor("#4a452a"));			        

			    if ((!myStringToken.equals("")) && (i == Integer.parseInt(myStringToken))) {
			    	radioButton.setChecked(true);
			    }
			    else {
			        radioButton.setChecked(false);
			    }
			    
			    myLinkScreenRadioGroup.addView(radioButton);
			}
		} else if (udtea.currScreen == udtea.MULTIPLE_CHECKBOXES_SCREEN) {
			udtea.setContentView(R.layout.multiple_checkboxes_screen);
			udtea.initBackNextButtons();
			TextView myMultipleCheckBoxesScreenTextView = (TextView)udtea.findViewById(R.id.checkboxes_textview);
			myMultipleCheckBoxesScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myMultipleCheckBoxesScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			LinearLayout myMultipleCheckboxesLinearLayout = (LinearLayout)udtea.findViewById(R.id.multiple_checkboxes_linearlayout);
			int totalCheckBoxesInContainer = udtea.checkBoxesContainer.size();
			StringTokenizer myMultipleCheckboxStringTokenizer = new StringTokenizer(myStringToken, ",");
			Vector<String> myCheckedAnswers = new Vector<String>();	    		
//	    		int counter=0;	    		
			while (myMultipleCheckboxStringTokenizer.countTokens()>0) {
				String myMultipleCheckboxStringToken = myMultipleCheckboxStringTokenizer.nextToken();
				if (myMultipleCheckboxStringToken != null) {
					myCheckedAnswers.add(myMultipleCheckboxStringToken);	    				
				}
				else {
					break;
				}
//	    			counter++;
			}
			for (int i=0; i<totalCheckBoxesInContainer; i++) {
			    CheckBox checkBox = new CheckBox(udtea.getBaseContext());
//		            checkBox.setText(StringEscapeUtils.unescapeJava(udtea.checkBoxesContainer.elementAt(i).toString()));
			    checkBox = (CheckBox) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), checkBox, UsbongUtils.IS_CHECKBOX, StringEscapeUtils.unescapeJava(udtea.checkBoxesContainer.elementAt(i).toString()));
			        
			    for (int k=0; k<myCheckedAnswers.size(); k++) {
			    	try {
			        	if (i==Integer.parseInt(myCheckedAnswers.elementAt(k))) {
			        		checkBox.setChecked(true);
			        	}
			    	}
			    	catch (NumberFormatException e) {//if myCheckedAnswers.elementAt(k) is not an int;
			    		continue;
			    	}
			    }
			    
			    checkBox.setTextSize(20);
			    checkBox.setTextColor(Color.parseColor("#4a452a"));			        
			    myMultipleCheckboxesLinearLayout.addView(checkBox);
			}
		} else if (udtea.currScreen == udtea.AUDIO_RECORD_SCREEN) {
			udtea.setContentView(R.layout.audio_recorder_screen);
			udtea.initRecordAudioScreen();
			udtea.initBackNextButtons();
			TextView myAudioRecorderTextView = (TextView)udtea.findViewById(R.id.audio_recorder_textview);
			myAudioRecorderTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myAudioRecorderTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			Button recordButton = (Button)udtea.findViewById(R.id.record_button);
			Button stopButton = (Button)udtea.findViewById(R.id.stop_button);
			Button playButton = (Button)udtea.findViewById(R.id.play_button);
			if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
				recordButton.setText((String) udtea.getResources().getText(R.string.UsbongRecordTextViewFILIPINO));				    		
				stopButton.setText((String) udtea.getResources().getText(R.string.UsbongStopTextViewFILIPINO));				    		
				playButton.setText((String) udtea.getResources().getText(R.string.UsbongPlayTextViewFILIPINO));				    		
			}
			else if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
				recordButton.setText((String) udtea.getResources().getText(R.string.UsbongRecordTextViewJAPANESE));				    		
				stopButton.setText((String) udtea.getResources().getText(R.string.UsbongStopTextViewJAPANESE));				    		
				playButton.setText((String) udtea.getResources().getText(R.string.UsbongPlayTextViewJAPANESE));				    		
			}
			else { //if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
				recordButton.setText((String) udtea.getResources().getText(R.string.UsbongRecordTextViewENGLISH));				    		
				stopButton.setText((String) udtea.getResources().getText(R.string.UsbongStopTextViewENGLISH));				    		
				playButton.setText((String) udtea.getResources().getText(R.string.UsbongPlayTextViewENGLISH));				    		
			}
		} else if (udtea.currScreen == udtea.PHOTO_CAPTURE_SCREEN) {
			udtea.setContentView(R.layout.photo_capture_screen);
			if (!udtea.performedCapturePhoto) {
			  udtea.initTakePhotoScreen();
			}
			udtea.initBackNextButtons();
			TextView myPhotoCaptureScreenTextView = (TextView)udtea.findViewById(R.id.photo_capture_textview);
			myPhotoCaptureScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myPhotoCaptureScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			Button photoCaptureButton = (Button)udtea.findViewById(R.id.photo_capture_button);
			if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
				photoCaptureButton.setText((String) udtea.getResources().getText(R.string.UsbongTakePhotoTextViewFILIPINO));				    		
			}
			else if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
				photoCaptureButton.setText((String) udtea.getResources().getText(R.string.UsbongTakePhotoTextViewJAPANESE));				    		
			}
			else { //if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
				photoCaptureButton.setText((String) udtea.getResources().getText(R.string.UsbongTakePhotoTextViewENGLISH));				    		
			}
		} else if (udtea.currScreen == udtea.PAINT_SCREEN) {
			udtea.setContentView(R.layout.paint_screen);
			if (!udtea.performedRunPaint) {
			  udtea.initPaintScreen();
			}
			udtea.initBackNextButtons();
			TextView myPaintScreenTextView = (TextView)udtea.findViewById(R.id.paint_textview);
			myPaintScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myPaintScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			Button paintButton = (Button)udtea.findViewById(R.id.paint_button);
			if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
				paintButton.setText((String) udtea.getResources().getText(R.string.UsbongRunPaintTextViewFILIPINO));				    		
			}
			else if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
				paintButton.setText((String) udtea.getResources().getText(R.string.UsbongRunPaintTextViewJAPANESE));				    		
			}
			else { //if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
				paintButton.setText((String) udtea.getResources().getText(R.string.UsbongRunPaintTextViewENGLISH));				    		
			}
		} else if (udtea.currScreen == udtea.QR_CODE_READER_SCREEN) {
			udtea.setContentView(R.layout.qr_code_reader_screen);
			if (!udtea.performedGetQRCode) {
			  udtea.initQRCodeReaderScreen();
			}
			udtea.initBackNextButtons();
			TextView myQRCodeReaderScreenTextView = (TextView)udtea.findViewById(R.id.qr_code_reader_textview);
			myQRCodeReaderScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myQRCodeReaderScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			Button qrCodeReaderButton = (Button)udtea.findViewById(R.id.qr_code_reader_button);
			if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
				qrCodeReaderButton .setText((String) udtea.getResources().getText(R.string.UsbongQRCodeReaderTextViewFILIPINO));				    		
			}
			else if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
				qrCodeReaderButton .setText((String) udtea.getResources().getText(R.string.UsbongQRCodeReaderTextViewJAPANESE));				    		
			}
			else { //if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
				qrCodeReaderButton .setText((String) udtea.getResources().getText(R.string.UsbongQRCodeReaderTextViewENGLISH));				    		
			}
		} else if (udtea.currScreen == udtea.TEXTFIELD_SCREEN) {
			udtea.setContentView(R.layout.textfield_screen);
			udtea.initBackNextButtons();
			TextView myTextFieldScreenTextView = (TextView)udtea.findViewById(R.id.textfield_textview);
			myTextFieldScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextFieldScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			EditText myTextFieldScreenEditText = (EditText)udtea.findViewById(R.id.textfield_edittext);
			myTextFieldScreenEditText.setText(myStringToken);
		} else if (udtea.currScreen == udtea.TEXTFIELD_WITH_ANSWER_SCREEN) {
			udtea.setContentView(R.layout.textfield_screen);
			udtea.initBackNextButtons();
			String myTextFieldWithAnswerScreenStringToken = "";
			//    			Log.d(">>>>>>>>udtea.currUsbongNode", udtea.currUsbongNode);
			udtea.currUsbongNodeWithoutAnswer=udtea.currUsbongNode.replace("Answer=", "~");
			StringTokenizer myTextFieldWithAnswerScreenStringTokenizer = new StringTokenizer(udtea.currUsbongNodeWithoutAnswer, "~");
			if (myTextFieldWithAnswerScreenStringTokenizer != null) {
				myTextFieldWithAnswerScreenStringToken = myTextFieldWithAnswerScreenStringTokenizer.nextToken();
				
				while (myTextFieldWithAnswerScreenStringTokenizer.hasMoreTokens()) { //get last element (i.e. Mike in "textFieldWithAnswer~Who is the founder of Usbong (nickname)?Answer=Mike")
					myTextFieldWithAnswerScreenStringToken = myTextFieldWithAnswerScreenStringTokenizer.nextToken(); 
				}
			}
			udtea.myTextFieldWithAnswerScreenAnswer=myTextFieldWithAnswerScreenStringToken.toString();
			udtea.currUsbongNodeWithoutAnswer=udtea.currUsbongNodeWithoutAnswer.substring(0, udtea.currUsbongNodeWithoutAnswer.length()-udtea.myTextFieldWithAnswerScreenAnswer.length()-1); //do a -1 for the last tilde    			
			TextView myTextFieldWithAnswerScreenTextView = (TextView)udtea.findViewById(R.id.textfield_textview);
			myTextFieldWithAnswerScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextFieldWithAnswerScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNodeWithoutAnswer);
			EditText myTextFieldScreenWithAnswerEditText = (EditText)udtea.findViewById(R.id.textfield_edittext);
			myTextFieldScreenWithAnswerEditText.setText(myStringToken);
		} else if (udtea.currScreen == udtea.TEXTAREA_SCREEN) {
			udtea.setContentView(R.layout.textarea_screen);
			udtea.initBackNextButtons();
			TextView myTextAreaScreenTextView = (TextView)udtea.findViewById(R.id.textarea_textview);
			myTextAreaScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextAreaScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			EditText myTextAreaScreenEditText = (EditText)udtea.findViewById(R.id.textarea_edittext);
			myTextAreaScreenEditText.setText(myStringToken);
		} else if (udtea.currScreen == udtea.TEXTAREA_WITH_ANSWER_SCREEN) {
			udtea.setContentView(R.layout.textarea_screen);
			udtea.initBackNextButtons();
			String myTextAreaWithAnswerScreenStringToken = "";
			//    			Log.d(">>>>>>>>udtea.currUsbongNode", udtea.currUsbongNode);
			udtea.currUsbongNodeWithoutAnswer=udtea.currUsbongNode.replace("Answer=", "~");
			StringTokenizer myTextAreaWithAnswerScreenStringTokenizer = new StringTokenizer(udtea.currUsbongNodeWithoutAnswer, "~");
			if (myTextAreaWithAnswerScreenStringTokenizer != null) {
				myTextAreaWithAnswerScreenStringToken = myTextAreaWithAnswerScreenStringTokenizer.nextToken();
				
				while (myTextAreaWithAnswerScreenStringTokenizer.hasMoreTokens()) { //get last element (i.e. Mike in "textFieldWithAnswer~Who is the founder of Usbong (nickname)?Answer=Mike")
					myTextAreaWithAnswerScreenStringToken = myTextAreaWithAnswerScreenStringTokenizer.nextToken(); 
				}
			}
			udtea.myTextAreaWithAnswerScreenAnswer=myTextAreaWithAnswerScreenStringToken.toString();
			udtea.currUsbongNodeWithoutAnswer=udtea.currUsbongNodeWithoutAnswer.substring(0, udtea.currUsbongNodeWithoutAnswer.length()-udtea.myTextAreaWithAnswerScreenAnswer.length()-1); //do a -1 for the last tilde    			
			TextView myTextAreaWithAnswerScreenTextView = (TextView)udtea.findViewById(R.id.textarea_textview);
			myTextAreaWithAnswerScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextAreaWithAnswerScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNodeWithoutAnswer);
			EditText myTextAreaScreenWithAnswerEditText = (EditText)udtea.findViewById(R.id.textarea_edittext);
			myTextAreaScreenWithAnswerEditText.setText(myStringToken);
		} else if (udtea.currScreen == udtea.TEXTFIELD_WITH_UNIT_SCREEN) {
			udtea.setContentView(R.layout.textfield_with_unit_screen);
			udtea.initBackNextButtons();
			TextView myTextFieldWithUnitScreenTextView = (TextView)udtea.findViewById(R.id.textfield_textview);
			myTextFieldWithUnitScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextFieldWithUnitScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			EditText myEditText = (EditText)udtea.findViewById(R.id.textfield_edittext);
			myEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
			myEditText.setText(myStringToken);
			TextView myUnitScreenTextView = (TextView)udtea.findViewById(R.id.textfieldunit_textview);
			myUnitScreenTextView.setText(udtea.textFieldUnit);
		} else if (udtea.currScreen == udtea.TEXTFIELD_NUMERICAL_SCREEN) {
			udtea.setContentView(R.layout.textfield_screen);
			udtea.initBackNextButtons();
			TextView myTextFieldNumericalScreenTextView = (TextView)udtea.findViewById(R.id.textfield_textview);
			myTextFieldNumericalScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextFieldNumericalScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			EditText myTextFieldNumericalScreenEditText = (EditText)udtea.findViewById(R.id.textfield_edittext);
			myTextFieldNumericalScreenEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
			myTextFieldNumericalScreenEditText.setText(myStringToken);
		} else if (udtea.currScreen == udtea.CLASSIFICATION_SCREEN) {
			udtea.setContentView(R.layout.classification_screen);
			udtea.initBackNextButtons();
			TextView myClassificationScreenTextView = (TextView)udtea.findViewById(R.id.classification_textview);
			myClassificationScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myClassificationScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			LinearLayout myClassificationLinearLayout = (LinearLayout)udtea.findViewById(R.id.classification_linearlayout);
			int totalClassificationsInContainer = udtea.classificationContainer.size();
			for (int i=0; i<totalClassificationsInContainer; i++) {
			    TextView myTextView = new TextView(udtea.getBaseContext());
			    //consider removing this code below; not needed; Mike, May 23, 2013
			    myTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);

				int bulletCount = i+1;
			    if (UsbongUtils.USE_UNESCAPE) {
			    	myTextView.setText(bulletCount+") "+StringEscapeUtils.unescapeJava(udtea.classificationContainer.elementAt(i).toString()));
			    }
			    else {
			    	myTextView.setText(bulletCount+") "+UsbongUtils.trimUsbongNodeName(udtea.classificationContainer.elementAt(i).toString()));		        	
			    }
			    
			    //add 5 so that the text does not touch the left border
			    myTextView.setPadding(udtea.padding_in_px, 0, 0, 0);
			    myTextView.setTextSize(24);
//			        myTextView.setTextColor(Color.WHITE);
			    myTextView.setTextColor(Color.parseColor("#4a452a"));			        
			    myClassificationLinearLayout.addView(myTextView);
			}
		} else if (udtea.currScreen == udtea.DCAT_SUMMARY_SCREEN) {
			udtea.setContentView(R.layout.dcat_summary_screen);
			udtea.initBackNextButtons();
			TextView myDCATSummaryScreenTextView = (TextView)udtea.findViewById(R.id.dcat_summary_textview);
			myDCATSummaryScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myDCATSummaryScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			udtea.myDcatSummaryStringBuffer= new StringBuffer();
			String weightsString = "1.9;2.1;2.6;1.8;2.4;1.8;.7;1.0;1.6;2.6;6.9;5.7;3.3;2.2;3.3;3.3;2;2;1.7;1.9;3.9;1.3;2.5;.8";
			StringTokenizer myWeightsStringTokenizer = new StringTokenizer(weightsString, ";");
			String myWeightString = myWeightsStringTokenizer.nextToken();
			//				
//				while (st.hasMoreTokens()) {
//					myStringToken = st.nextToken(); 
//				}
//
			double myWeightedScoreInt=0;
			double myNegotiatedWeightedScoreInt=0;
			double[][] dcatSum = new double[8][4];
			final int sumWeightedRatingIndex=0;
			final int sumWeightedScoreIndex=1;
			final int sumNegotiatedRatingIndex=2;
			final int sumNegotiatedScoreIndex=3;
			int currStandard=0;//standard 1
			//				boolean hasReachedNegotiated=false;
			boolean hasReachedStandardTotal=false;
			LinearLayout myDCATSummaryLinearLayout = (LinearLayout)udtea.findViewById(R.id.dcat_summary_linearlayout);
			int totalElementsInDCATSummaryBasedOnUsbongNodeContainer = udtea.usbongNodeContainer.size();
			//		        for (int i=0; i<totalElementsInDCATSummaryBasedOnUsbongNodeContainer.usbongNodeContainer; i++) {		        	
			for (int i=0; i<totalElementsInDCATSummaryBasedOnUsbongNodeContainer; i++) {		        	

				TextView myTextView = new TextView(udtea.getBaseContext());	            	
			    myTextView.setPadding(udtea.padding_in_px, 0, 0, 0); //add 5 so that the text does not touch the left border
			    myTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
			    myTextView.setTextColor(Color.parseColor("#4a452a"));			        

			    //the only way to check if the element is already the last item in the standard
			    //is if the next element in the node container has "STANDARD", but not the first standard
				if ((i+1>=totalElementsInDCATSummaryBasedOnUsbongNodeContainer) || 
				   (i+1<totalElementsInDCATSummaryBasedOnUsbongNodeContainer) &&
						((udtea.usbongNodeContainer.elementAt(i+1).toString().contains("STANDARD")))&&
						(!(udtea.usbongNodeContainer.elementAt(i+1).toString().contains("STANDARD ONE")))
				   )
				{	
					int tempCurrStandard=currStandard+1; //do a +1 since currStandard begins at 0

			        TextView myIssuesTextView = new TextView(udtea.getBaseContext());			            

					//added by Mike, May 31, 2013
					if (!udtea.usbongAnswerContainer.elementAt(i).toString().contains("dcat_end,")) {

			    		String s = udtea.usbongAnswerContainer.elementAt(i).toString().replace(";", "");
			    		s = s.replace("A,", "");
			    		if (!s.equals("")) {
			    			myIssuesTextView  = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myIssuesTextView, UsbongUtils.IS_TEXTVIEW, "ISSUES: "+s+"{br}");
			    		}
			    		else {
			    			myIssuesTextView  = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myIssuesTextView, UsbongUtils.IS_TEXTVIEW, "ISSUES: none{br}");
			    		}

			            myIssuesTextView.setPadding(udtea.padding_in_px, 0, 0, 0); //add 5 so that the text does not touch the left border
			            myIssuesTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
			            myIssuesTextView.setTextColor(Color.parseColor("#4a452a"));			        
			            myDCATSummaryLinearLayout.addView(myIssuesTextView);
						udtea.myDcatSummaryStringBuffer.append(myIssuesTextView.getText().toString()+"\n");
					}
					
					if (myWeightsStringTokenizer.hasMoreElements()) {
			    		//get the next weight
						myWeightString = myWeightsStringTokenizer.nextToken();
					}

			        myTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextView, UsbongUtils.IS_TEXTVIEW, 
			        		"//--------------------"+
							" STANDARD "+tempCurrStandard+" (TOTAL){br}"+ 
							"Total (Rating): "+String.format("%.2f",dcatSum[currStandard][sumWeightedRatingIndex]) +"{br}"+
			        		"Total (Weighted Score): "+String.format("%.2f",dcatSum[currStandard][sumWeightedScoreIndex])+"{br}"+
							"Total (Negotiated Rating): "+String.format("%.2f",dcatSum[currStandard][sumNegotiatedRatingIndex])+"{br}"+
			        		"Total (Negotiated WS): "+String.format("%.2f",dcatSum[currStandard][sumNegotiatedScoreIndex])+"{br}"+
			        		"//--------------------"
							);		
			        hasReachedStandardTotal=true;
					currStandard++;
				}

				if (hasReachedStandardTotal) {
					hasReachedStandardTotal=false;
				}		        	
				else if (udtea.usbongNodeContainer.elementAt(i).toString().contains("ISSUES")){
					String s = udtea.usbongAnswerContainer.elementAt(i).toString().replace(";", "");
					s = s.replace("A,", "");
					if (!s.equals("")) {
			    		myTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextView, UsbongUtils.IS_TEXTVIEW, "ISSUES: "+s+"{br}");
					}
					else {
			    		myTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextView, UsbongUtils.IS_TEXTVIEW, "ISSUES: none{br}");
					}
					
					if (myWeightsStringTokenizer.hasMoreElements()) {
			    		//get the next weight
						myWeightString = myWeightsStringTokenizer.nextToken();
					}
				}
				else if (udtea.usbongNodeContainer.elementAt(i).toString().contains("Weighted")){
			        TextView myWeightedTextView = new TextView(udtea.getBaseContext());
			        myWeightedTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myWeightedTextView, UsbongUtils.IS_TEXTVIEW, udtea.usbongNodeContainer.elementAt(i).toString().replace("{br}(Weighted Score)",""));
			        myWeightedTextView.setPadding(udtea.padding_in_px, 0, 0, 0); //add 5 so that the text does not touch the left border
			        myWeightedTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
			        myWeightedTextView.setTextColor(Color.parseColor("#4a452a"));			        
			        
			        myDCATSummaryLinearLayout.addView(myWeightedTextView);
					udtea.myDcatSummaryStringBuffer.append(myWeightedTextView.getText().toString()+"\n");

			        int weightedAnswer;
			        //added by Mike, July 8, 2013
			        try {
			        	weightedAnswer = Integer.parseInt(udtea.usbongAnswerContainer.elementAt(i).toString().replace(";", ""));
			        }
			        catch (Exception e) { //if there's no answer selected
			        	weightedAnswer=0;
			        }
			        if (weightedAnswer<=0) {
						weightedAnswer=0;
					}

					//the weight is in double
					myWeightedScoreInt = weightedAnswer * Double.parseDouble(myWeightString);
					if (myWeightedScoreInt<=0) {
						myWeightedScoreInt=0;
						myTextView.setBackgroundColor(Color.YELLOW);
					}
					
					dcatSum[currStandard][sumWeightedRatingIndex]+=weightedAnswer;
					dcatSum[currStandard][sumWeightedScoreIndex]+=myWeightedScoreInt;		        		
					
					myTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextView, UsbongUtils.IS_TEXTVIEW, 
							"Weighted: " +myWeightedScoreInt);
				}
				else if (udtea.usbongNodeContainer.elementAt(i).toString().contains("Negotiated")){
			        //added by Mike, July 8, 2013
					int negotiatedAnswer;
					try {
			    		negotiatedAnswer = Integer.parseInt(udtea.usbongAnswerContainer.elementAt(i).toString().replace(";", ""));
			        }
			        catch (Exception e) { //if there's no answer selected
			        	negotiatedAnswer=0;
			        }
					if (negotiatedAnswer<=0) {
						negotiatedAnswer=0;
					}
					
					//the weight is in double
					myNegotiatedWeightedScoreInt =  negotiatedAnswer * Double.parseDouble(myWeightString);
					if (myNegotiatedWeightedScoreInt<=0) {
						myNegotiatedWeightedScoreInt=0;
						myTextView.setBackgroundColor(Color.YELLOW);
					}

					dcatSum[currStandard][sumNegotiatedRatingIndex]+=negotiatedAnswer;
					dcatSum[currStandard][sumNegotiatedScoreIndex]+=myNegotiatedWeightedScoreInt;		        		

					myTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextView, UsbongUtils.IS_TEXTVIEW, 
							"Negotiated: " + myNegotiatedWeightedScoreInt);		        				        				        		
//		        		hasReachedNegotiated=true;
				}
				else {
			        myTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextView, UsbongUtils.IS_TEXTVIEW, udtea.usbongNodeContainer.elementAt(i).toString()+"{br}");
				}

//	        		if (!hasReachedStandardTotal) {
					myDCATSummaryLinearLayout.addView(myTextView);
					udtea.myDcatSummaryStringBuffer.append(myTextView.getText().toString()+"\n");
					Log.d(">>>>>myTextView.getText().toString()",myTextView.getText().toString());
//	        		}
//	        		else {
//	        			hasReachedStandardTotal=false;
//	        		}
			}
		} else if (udtea.currScreen == udtea.DATE_SCREEN) {
			udtea.setContentView(R.layout.date_screen);
			udtea.initBackNextButtons();
			TextView myDateScreenTextView = (TextView)udtea.findViewById(R.id.date_textview);
			myDateScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myDateScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			//Reference: http://code.google.com/p/android/issues/detail?id=2037
			//last accessed: 21 Aug. 2012
			Configuration userConfig = new Configuration();
			Settings.System.getConfiguration( udtea.getContentResolver(), userConfig );
			Calendar date = Calendar.getInstance( userConfig.locale);
			//Reference: http://www.androidpeople.com/android-spinner-default-value;
			//last accessed: 21 Aug. 2012		        
			//month-------------------------------
			int month = date.get(Calendar.MONTH); //first month of the year is 0
			Spinner dateMonthSpinner = (Spinner) udtea.findViewById(R.id.date_month_spinner);
			udtea.monthAdapter  = ArrayAdapter.createFromResource(
					((Activity)udtea), R.array.months_array, android.R.layout.simple_spinner_item);
//		        udtea.monthAdapter  = ArrayAdapter.createFromResource(
//                this, R.array.months_array, R.layout.date_textview);
			udtea.monthAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			dateMonthSpinner.setAdapter(udtea.monthAdapter );
			dateMonthSpinner.setSelection(month);
//		        System.out.println(">>>>>>>>>>>>>> month"+month);
//		        Log.d(">>>>>>myStringToken",myStringToken);
			for (int i=0; i<udtea.monthAdapter .getCount(); i++) {
//		        	Log.d(">>>>>>udtea.monthAdapter ",udtea.monthAdapter .getItem(i).toString());
				
				if (myStringToken.contains(udtea.monthAdapter .getItem(i).toString())) {
					dateMonthSpinner.setSelection(i);
					
					//added by Mike, March 4, 2013
					myStringToken = myStringToken.replace(udtea.monthAdapter .getItem(i).toString(), "");
				}
			}		        		        
			//-------------------------------------
			//day----------------------------------
			//Reference: http://docs.oracle.com/javase/1.5.0/docs/api/java/util/Calendar.html#MONTH
			//last accessed: 21 Aug 2012
			int day = date.get(Calendar.DAY_OF_MONTH); //first day of the month is 1
			day = day - 1; //do this to offset, when retrieving the day in strings.xml
			Spinner dateDaySpinner = (Spinner) udtea.findViewById(R.id.date_day_spinner);
			udtea.dayAdapter = ArrayAdapter.createFromResource(
			        ((Activity)udtea), R.array.day_array, android.R.layout.simple_spinner_item);
			udtea.dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			dateDaySpinner.setAdapter(udtea.dayAdapter);
			dateDaySpinner.setSelection(day);
//		        System.out.println(">>>>>>>>>>>>>> day"+day);
			//		        Log.d(">>>>>myStringToken",myStringToken);
//		        System.out.println(">>>>>>>> myStringToken"+myStringToken);
			StringTokenizer myDateStringTokenizer = new StringTokenizer(myStringToken, ",");
			String myDayStringToken="";
			if (!myStringToken.equals("")) {
				myDayStringToken = myDateStringTokenizer.nextToken();	
			}
			for (int i=0; i<udtea.dayAdapter.getCount(); i++) {		        	
				if (myDayStringToken.contains(udtea.dayAdapter.getItem(i).toString())) {
					dateDaySpinner.setSelection(i);
					
					myStringToken = myStringToken.replace(udtea.dayAdapter.getItem(i).toString()+",", "");
//		        		System.out.println(">>>>>>>>>>>myStringToken: "+myStringToken);
				}
			}
			//-------------------------------------				
			//year---------------------------------
			int year = date.get(Calendar.YEAR);
			EditText myDateYearEditText = (EditText)udtea.findViewById(R.id.date_edittext);
			myDateYearEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
			//added by Mike, March 4, 2013
			if (myStringToken.equals("")) {
				myDateYearEditText.setText(""+year);		        
			}
			else {
				myDateYearEditText.setText(myStringToken);
			}
		} else if (udtea.currScreen == udtea.TEXT_DISPLAY_SCREEN) {
			udtea.setContentView(R.layout.text_display_screen);
			udtea.initBackNextButtons();
			TextView myTextDisplayScreenTextView = (TextView)udtea.findViewById(R.id.text_display_textview);
			myTextDisplayScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextDisplayScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			
//			Log.d(">>>>>","inside udtea.currScreen == udtea.TEXT_DISPLAY_SCREEN");
//			myTextDisplayScreenTextView = (TextView) UsbongUtils.applyHintsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextDisplayScreenTextView, UsbongUtils.IS_TEXTVIEW);
//			Log.d(">>>>>","after myTextDisplayScreenTextView");
			
		} else if (udtea.currScreen == udtea.TIMESTAMP_DISPLAY_SCREEN) {
			udtea.setContentView(R.layout.timestamp_display_screen);
			udtea.initBackNextButtons();
			TextView myTimeDisplayScreenTextView = (TextView)udtea.findViewById(R.id.time_display_textview);
			udtea.timestampString = UsbongUtils.getCurrTimeStamp();
			myTimeDisplayScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTimeDisplayScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode+"{br}"+udtea.timestampString);

		} else if (udtea.currScreen == udtea.SIMPLE_ENCRYPT_SCREEN) {
			udtea.setContentView(R.layout.simple_encrypt_screen);
			udtea.initBackNextButtons();
			TextView myEncryptScreenTextView = (TextView)udtea.findViewById(R.id.encrypt_textview);
			myEncryptScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myEncryptScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);

			String message ="";
			if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
				message = (String) udtea.getResources().getText(R.string.UsbongEncryptAlertMessageFILIPINO);
			}
			else if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
				 message = (String) udtea.getResources().getText(R.string.UsbongEncryptAlertMessageJAPANESE);				    		
			}
			else { //if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
				message = (String) udtea.getResources().getText(R.string.UsbongEncryptAlertMessageENGLISH);				    		
			}

	    	new AlertDialog.Builder(udtea).setTitle("Hey!")
	    	.setMessage(message)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {	            				
				}
			}).show();
						
		} else if (udtea.currScreen == udtea.IMAGE_DISPLAY_SCREEN) {
			udtea.setContentView(R.layout.image_display_screen);
			udtea.initBackNextButtons();
			ImageView myImageDisplayScreenImageView = (ImageView)udtea.findViewById(R.id.special_imageview);
			//		        if (!UsbongUtils.setImageDisplay(myImageDisplayScreenImageView, myTree+".utree/res/" +UsbongUtils.getResName(udtea.currUsbongNode))) {
			if (!UsbongUtils.setImageDisplay(myImageDisplayScreenImageView, udtea.myTree, UsbongUtils.getResName(udtea.currUsbongNode))) {
			//Reference: http://www.anddev.org/tinytut_-_get_resources_by_name__getidentifier_-t460.html; last accessed 14 Sept 2011
//			        Resources myRes = getResources();
			    myDrawableImage = myRes.getDrawable(myRes.getIdentifier("no_image", "drawable", udtea.myPackageName));
			    myImageDisplayScreenImageView.setImageDrawable(myDrawableImage);		        		        	
			}
		} else if (udtea.currScreen == udtea.CLICKABLE_IMAGE_DISPLAY_SCREEN) {
			udtea.setContentView(R.layout.clickable_image_display_screen);
			udtea.initBackNextButtons();
			ImageButton myClickableImageDisplayScreenImageButton = (ImageButton)udtea.findViewById(R.id.clickable_image_display_imagebutton);
			if (!UsbongUtils.setClickableImageDisplay(myClickableImageDisplayScreenImageButton, udtea.myTree, UsbongUtils.getResName(udtea.currUsbongNode))) {
			//Reference: http://www.anddev.org/tinytut_-_get_resources_by_name__getidentifier_-t460.html; last accessed 14 Sept 2011
//			        Resources myRes = getResources();
			    myDrawableImage = myRes.getDrawable(myRes.getIdentifier("no_image", "drawable", udtea.myPackageName));
			    myClickableImageDisplayScreenImageButton.setBackgroundDrawable(myDrawableImage);		        		        	
			}
			myClickableImageDisplayScreenImageButton.setOnClickListener(new OnClickListener() {
			    @Override
			    public void onClick(View v) {
//	                	myMessage = UsbongUtils.applyTagsInString(udtea.currUsbongNode).toString();	    				
			    	
			    	TextView tv = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(UsbongDecisionTreeEngineActivity.getInstance()), UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			    	if (tv.toString().equals("")) {
			    		tv.setText("No message.");
			    	}
	    	    	tv.setTextSize((UsbongDecisionTreeEngineActivity.getInstance().getResources().getDimension(R.dimen.textsize)));	    	    	

			    	new AlertDialog.Builder(udtea).setTitle("Hey!")
//	            		.setMessage(myMessage)
					.setView(tv)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {	            				
						}
					}).show();
			    }
			});
		} else if (udtea.currScreen == udtea.TEXT_CLICKABLE_IMAGE_DISPLAY_SCREEN) {
			udtea.setContentView(R.layout.text_clickable_image_display_screen);
			udtea.initBackNextButtons();
			TextView myTextClickableImageDisplayTextView = (TextView)udtea.findViewById(R.id.text_clickable_image_display_textview);
			myTextClickableImageDisplayTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextClickableImageDisplayTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			ImageButton myTextClickableImageDisplayScreenImageButton = (ImageButton)udtea.findViewById(R.id.clickable_image_display_imagebutton);
			if (!UsbongUtils.setClickableImageDisplay(myTextClickableImageDisplayScreenImageButton, udtea.myTree, UsbongUtils.getResName(udtea.currUsbongNode))) {
			//Reference: http://www.anddev.org/tinytut_-_get_resources_by_name__getidentifier_-t460.html; last accessed 14 Sept 2011
//			        Resources myRes = getResources();
			    myDrawableImage = myRes.getDrawable(myRes.getIdentifier("no_image", "drawable", udtea.myPackageName));
			    myTextClickableImageDisplayScreenImageButton.setBackgroundDrawable(myDrawableImage);		        		        	
			}
			myTextClickableImageDisplayScreenImageButton.setOnClickListener(new OnClickListener() {
			    @Override
			    public void onClick(View v) {
//	                	myMessage = UsbongUtils.applyTagsInString(udtea.currUsbongNode).toString();	    				
			    	
			    	TextView tv = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(udtea), UsbongUtils.IS_TEXTVIEW, UsbongUtils.getAlertName(udtea.currUsbongNode));
			    	if (tv.toString().equals("")) {
			    		tv.setText("No message.");
			    	}
	    	    	tv.setTextSize((UsbongDecisionTreeEngineActivity.getInstance().getResources().getDimension(R.dimen.textsize)));

			    	new AlertDialog.Builder(udtea).setTitle("Hey!")
//	            		.setMessage(myMessage)
					.setView(tv)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {	            				
						}
					}).show();
			    }
			});
		} else if (udtea.currScreen == udtea.CLICKABLE_IMAGE_TEXT_DISPLAY_SCREEN) {
			udtea.setContentView(R.layout.clickable_image_text_display_screen);
			udtea.initBackNextButtons();
			TextView myClickableImageTextDisplayTextView = (TextView)udtea.findViewById(R.id.clickable_image_text_display_textview);
			myClickableImageTextDisplayTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myClickableImageTextDisplayTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			ImageButton myClickableImageTextDisplayScreenImageButton = (ImageButton)udtea.findViewById(R.id.clickable_image_display_imagebutton);
			if (!UsbongUtils.setClickableImageDisplay(myClickableImageTextDisplayScreenImageButton, udtea.myTree, UsbongUtils.getResName(udtea.currUsbongNode))) {
			//Reference: http://www.anddev.org/tinytut_-_get_resources_by_name__getidentifier_-t460.html; last accessed 14 Sept 2011
//			        Resources myRes = getResources();
			    myDrawableImage = myRes.getDrawable(myRes.getIdentifier("no_image", "drawable", udtea.myPackageName));
			    myClickableImageTextDisplayScreenImageButton.setBackgroundDrawable(myDrawableImage);
			}
			myClickableImageTextDisplayScreenImageButton.setOnClickListener(new OnClickListener() {
			    @Override
			    public void onClick(View v) {
//	                	myMessage = UsbongUtils.applyTagsInString(udtea.currUsbongNode).toString();	    				
			    	
			    	TextView tv = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(udtea), UsbongUtils.IS_TEXTVIEW, UsbongUtils.getAlertName(udtea.currUsbongNode));
			    	if (tv.toString().equals("")) {
			    		tv.setText("No message.");
			    	}
	    	    	tv.setTextSize((UsbongDecisionTreeEngineActivity.getInstance().getResources().getDimension(R.dimen.textsize)));

			    	new AlertDialog.Builder(udtea).setTitle("Hey!")
//	            		.setMessage(myMessage)
					.setView(tv)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {	            				
						}
					}).show();
			    }
			});
		} else if (udtea.currScreen == udtea.VIDEO_FROM_FILE_SCREEN) {
			udtea.setContentView(R.layout.video_from_file_screen);
			udtea.initBackNextButtons();
			VideoView myVideoFromFileScreenVideoView = (VideoView)udtea.findViewById(R.id.video_from_file_videoview);
			myVideoFromFileScreenVideoView.setVideoPath(UsbongUtils.getPathOfVideoFile(udtea.myTree, UsbongUtils.getResName(udtea.currUsbongNode)));
			//added by Mike, Sept. 9, 2013
			myVideoFromFileScreenVideoView.setMediaController(new MediaController(((Activity)udtea)));
			myVideoFromFileScreenVideoView.start();
		} else if (udtea.currScreen == udtea.VIDEO_FROM_FILE_WITH_TEXT_SCREEN) {
			udtea.setContentView(R.layout.video_from_file_with_text_screen);
			udtea.initBackNextButtons();
			TextView myVideoFromFileWithTextTextView = (TextView)udtea.findViewById(R.id.video_from_file_with_text_textview);
			myVideoFromFileWithTextTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myVideoFromFileWithTextTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			VideoView myVideoFromFileWithTextScreenVideoView = (VideoView)udtea.findViewById(R.id.video_from_file_with_text_videoview);
			myVideoFromFileWithTextScreenVideoView.setVideoPath(UsbongUtils.getPathOfVideoFile(udtea.myTree, UsbongUtils.getResName(udtea.currUsbongNode)));
			myVideoFromFileWithTextScreenVideoView.setMediaController(new MediaController(((Activity)udtea)));
			myVideoFromFileWithTextScreenVideoView.start();
		} else if (udtea.currScreen == udtea.TEXT_IMAGE_DISPLAY_SCREEN) {
			udtea.setContentView(R.layout.text_image_display_screen);
			udtea.initBackNextButtons();
			TextView myTextImageDisplayTextView = (TextView)udtea.findViewById(R.id.text_image_display_textview);
			myTextImageDisplayTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextImageDisplayTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			ImageView myTextImageDisplayImageView = (ImageView)udtea.findViewById(R.id.image_display_imageview);
			//		        if (!UsbongUtils.setImageDisplay(myTextImageDisplayImageView, myTree+".utree/res/" +UsbongUtils.getResName(udtea.currUsbongNode))) {
			if (!UsbongUtils.setImageDisplay(myTextImageDisplayImageView, udtea.myTree, UsbongUtils.getResName(udtea.currUsbongNode))) {
			//Reference: http://www.anddev.org/tinytut_-_get_resources_by_name__getidentifier_-t460.html; last accessed 14 Sept 2011
//			        Resources myRes = getResources();
			    myDrawableImage = myRes.getDrawable(myRes.getIdentifier("no_image", "drawable", udtea.myPackageName));
			    myTextImageDisplayImageView.setImageDrawable(myDrawableImage);		        		        	
			}
		} else if (udtea.currScreen == udtea.IMAGE_TEXT_DISPLAY_SCREEN) {
			udtea.setContentView(R.layout.image_text_display_screen);
			udtea.initBackNextButtons();
			TextView myImageTextDisplayTextView = (TextView)udtea.findViewById(R.id.image_text_display_textview);
			myImageTextDisplayTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myImageTextDisplayTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			ImageView myImageTextDisplayImageView = (ImageView)udtea.findViewById(R.id.image_display_imageview);

			if (!UsbongUtils.setImageDisplay(myImageTextDisplayImageView, udtea.myTree, UsbongUtils.getResName(udtea.currUsbongNode))) {
			//Reference: http://www.anddev.org/tinytut_-_get_resources_by_name__getidentifier_-t460.html; last accessed 14 Sept 2011
//			        Resources myRes = getResources();
			    myDrawableImage = myRes.getDrawable(myRes.getIdentifier("no_image", "drawable", udtea.myPackageName));
			    myImageTextDisplayImageView.setImageDrawable(myDrawableImage);		        		        	
			}
		} else if (udtea.currScreen == udtea.GPS_LOCATION_SCREEN) {
			udtea.setContentView(R.layout.gps_location_screen);
			udtea.initBackNextButtons();
			TextView myGPSLocationTextView = (TextView)udtea.findViewById(R.id.gps_location_textview);
			myGPSLocationTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myGPSLocationTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
//			TextView myLongitudeTextView = (TextView)udtea.findViewById(R.id.longitude_textview);
//			TextView myLatitudeTextView = (TextView)udtea.findViewById(R.id.latitude_textview);
			hasGottenGPSLocation=false;
			
			locationResult = new LocationResult(){
				@Override
			    public void gotLocation(Location location){
			        //Got the location!
			    		System.out.println(">>>>>>>>>>>>>>>>>location: "+location);
			    		if (udtea.currScreen==udtea.GPS_LOCATION_SCREEN) {
				        	if (location!=null) {
				        		myLongitude = location.getLongitude()+"";
				        		myLatitude = location.getLatitude()+"";
	
				    			myLongitudeTextView = (TextView)udtea.findViewById(R.id.longitude_textview);
				    			myLatitudeTextView = (TextView)udtea.findViewById(R.id.latitude_textview);
				    			
				    			hasGottenGPSLocation=true;
				    			
				    			udtea.runOnUiThread(new Runnable() {
				                    @Override
				                    public void run() {
						    			myLongitudeTextView.setText("long: "+myLongitude);
						    			myLatitudeTextView.setText("lat: "+myLatitude);
				                    }
				                });
				        	}
				        	else {
				        		Toast.makeText(UsbongDecisionTreeEngineActivity.getInstance(), "Error getting location. Please make sure you are not inside a building.", Toast.LENGTH_SHORT).show();
				        	}
			    		}
			    		else {
			    			hasGottenGPSLocation=true; //to stop the cycling progress bar
			    		}
			    }				
			};
//			myLoadingProgressBar =  new ProgressBar(udtea);
//			myLoadingProgressBar.setIndeterminate(false);
//			myLoadingProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);			
			
			udtea.myLocation = new FedorMyLocation();
			udtea.myLocation.getLocation(udtea, locationResult);		        
			
			myLoadingProgressBar = (ProgressBar) udtea.findViewById(R.id.progressBar);
			new ProgressTask().execute();			
			
		} else if (udtea.currScreen == udtea.YES_NO_DECISION_SCREEN) {
			udtea.setContentView(R.layout.yes_no_decision_screen);
			udtea.initBackNextButtons();
			TextView myYesNoDecisionScreenTextView = (TextView)udtea.findViewById(R.id.yes_no_decision_textview);
			myYesNoDecisionScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myYesNoDecisionScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			RadioButton myYesRadioButton = (RadioButton)udtea.findViewById(R.id.yes_radiobutton);
			myYesRadioButton.setText(udtea.yesStringValue);
			myYesRadioButton.setTextSize(20);
			RadioButton myNoRadioButton = (RadioButton)udtea.findViewById(R.id.no_radiobutton);
			myNoRadioButton.setText(udtea.noStringValue);
			myNoRadioButton.setTextSize(20);
			if (myStringToken.equals("N")) {
				myNoRadioButton.setChecked(true);
			}
			else if ((myStringToken.equals("Y"))){
				myYesRadioButton.setChecked(true);		        	
			}
		} else if (udtea.currScreen == udtea.SEND_TO_CLOUD_BASED_SERVICE_SCREEN) {
			udtea.setContentView(R.layout.yes_no_decision_screen);
			udtea.initBackNextButtons();
			TextView mySendToCloudBasedServiceScreenTextView = (TextView)udtea.findViewById(R.id.yes_no_decision_textview);
			mySendToCloudBasedServiceScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), mySendToCloudBasedServiceScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			RadioButton mySendToCloudBasedServiceScreenYesRadioButton = (RadioButton)udtea.findViewById(R.id.yes_radiobutton);
			mySendToCloudBasedServiceScreenYesRadioButton.setText(udtea.yesStringValue);
			mySendToCloudBasedServiceScreenYesRadioButton.setTextSize(20);
			RadioButton mySendToCloudBasedServiceScreenNoRadioButton = (RadioButton)udtea.findViewById(R.id.no_radiobutton);
			mySendToCloudBasedServiceScreenNoRadioButton.setText(udtea.noStringValue);
			mySendToCloudBasedServiceScreenNoRadioButton.setTextSize(20);
			if (myStringToken.equals("N")) {
				mySendToCloudBasedServiceScreenNoRadioButton.setChecked(true);
			}
			else if ((myStringToken.equals("Y"))){
				mySendToCloudBasedServiceScreenYesRadioButton.setChecked(true);		        	
			}
		} else if (udtea.currScreen == udtea.SEND_TO_WEBSERVER_SCREEN) {
			udtea.setContentView(R.layout.send_to_webserver_screen);
			udtea.initBackNextButtons();
			TextView mySendToWebserverScreenTextView = (TextView)udtea.findViewById(R.id.send_to_webserver_textview);
			mySendToWebserverScreenTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), mySendToWebserverScreenTextView, UsbongUtils.IS_TEXTVIEW, udtea.currUsbongNode);
			TextView myWebserverURLScreenTextView = (TextView)udtea.findViewById(R.id.webserver_url_textview);
			
			if (!UsbongUtils.getDestinationServerURL().toString().equals("")) {
				myWebserverURLScreenTextView.setText("["+UsbongUtils.getDestinationServerURL()+"]");				
			}
			else {
				myWebserverURLScreenTextView.setText("[Warning: No URL specified in Settings.]");								
			}

			
			RadioButton mySendToWebserverYesRadioButton = (RadioButton)udtea.findViewById(R.id.yes_radiobutton);
			mySendToWebserverYesRadioButton.setText(udtea.yesStringValue);
			mySendToWebserverYesRadioButton.setTextSize(20);
			RadioButton mySendToWebserverNoRadioButton = (RadioButton)udtea.findViewById(R.id.no_radiobutton);
			mySendToWebserverNoRadioButton.setText(udtea.noStringValue);
			mySendToWebserverNoRadioButton.setTextSize(20);
			if (myStringToken.equals("N")) {
				mySendToWebserverNoRadioButton.setChecked(true);
			}
			else if ((myStringToken.equals("Y"))){
				mySendToWebserverYesRadioButton.setChecked(true);		        	
			}
		} else if (udtea.currScreen == udtea.END_STATE_SCREEN) {
			udtea.setContentView(R.layout.end_state_screen);
			TextView endStateTextView = (TextView)udtea.findViewById(R.id.end_state_textview);
			if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
				endStateTextView.setText((String) udtea.getResources().getText(R.string.UsbongEndStateTextViewFILIPINO));				    		
			}
			else if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
				endStateTextView.setText((String) udtea.getResources().getText(R.string.UsbongEndStateTextViewJAPANESE));				    						    		
			}
			else if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_MANDARIN) {
				endStateTextView.setText((String) udtea.getResources().getText(R.string.UsbongEndStateTextViewMANDARIN));				    						    		
			}
			else { //if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
				endStateTextView.setText((String) udtea.getResources().getText(R.string.UsbongEndStateTextViewENGLISH));				    						    		
			}
			//add Bisaya, Ilonggo, and Kapampangan
			udtea.initBackNextButtons();
		}
		View myLayout= udtea.findViewById(R.id.parent_layout_id);
        if (!UsbongUtils.setBackgroundImage(myLayout, udtea.myTree, "bg")) {
    		myLayout.setBackgroundResource(R.drawable.bg);//default bg
        }
        
		if ((!udtea.usedBackButton) && (!udtea.hasReturnedFromAnotherActivity)){
			udtea.usbongNodeContainer.addElement(udtea.currUsbongNode);
			udtea.usbongNodeContainerCounter++;
		}
		else {
			udtea.usedBackButton=false;
			udtea.hasReturnedFromAnotherActivity=false;
		}
    }    
    
	private class ProgressTask extends AsyncTask <Void,Void,Void>{
	    @Override
	    protected void onPreExecute(){
//	    	myLoadingProgressBar.show();
	    	myLoadingProgressBar.setVisibility(View.VISIBLE);
	    }

	    @Override
	    protected Void doInBackground(Void... arg0) {   
			while(!hasGottenGPSLocation);
	    	
	    	return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
//	    	myLoadingProgressBar.dismiss();
	    	myLoadingProgressBar.setVisibility(View.GONE);
	    }
	}
}