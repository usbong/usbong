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
package usbong.android;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import usbong.android.utils.UsbongConstants;
import usbong.android.utils.UsbongUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;

public class SettingsActivity extends Activity {
	private CheckBox myDebugModeCheckBox;
	private CheckBox myAutoNarrateModeCheckBox;
	private CheckBox myAutoPlayModeCheckBox;
	private CheckBox myAutoLoopModeCheckBox;
	private CheckBox myDestinationURLCheckBox;
	private EditText myDestinationURLEditText;
	private CheckBox myStoreOutputCheckBox;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.settings_screen);	    
		myDebugModeCheckBox = (CheckBox)findViewById(R.id.settings_debug_mode_checkbox);		
		myDestinationURLCheckBox = (CheckBox)findViewById(R.id.settings_destination_url_checkbox);		
		myDestinationURLEditText = (EditText)findViewById(R.id.settings_edittext);		
				
		myStoreOutputCheckBox = (CheckBox)findViewById(R.id.settings_store_output_checkbox);
		
		//hide virtual keyboard, instead of auto-focusing on the first edittext
		myDestinationURLEditText.setInputType(0);	
		myDestinationURLEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
					myDestinationURLEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
		        }
		    }
		});

		//added by Mike, 20160410
		myAutoNarrateModeCheckBox = (CheckBox)findViewById(R.id.settings_auto_narrate_mode_checkbox);		
		myAutoNarrateModeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
		            if (myAutoPlayModeCheckBox.isChecked()) {
		            	myAutoNarrateModeCheckBox.setChecked(true);
		            }
		        } 
			}
		});
		
		myAutoPlayModeCheckBox = (CheckBox)findViewById(R.id.settings_auto_play_mode_checkbox);		
		myAutoPlayModeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
	            	myAutoNarrateModeCheckBox.setChecked(true);
		        } 
			}
		});
		
		//added by Mike, 20160410
		myAutoLoopModeCheckBox = (CheckBox)findViewById(R.id.settings_auto_loop_mode_checkbox);		
	
	    try {	    	
			InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config");	
			BufferedReader br = new BufferedReader(reader);    		
	    	String currLineString;        	
			myStoreOutputCheckBox.setChecked(true);				

	    	while((currLineString=br.readLine())!=null)
	    	{ 	
				if (currLineString.equals("IS_IN_DEBUG_MODE=ON")) {
					myDebugModeCheckBox.setChecked(true);				
				}
				
				//added by Mike, 20160410
				if (currLineString.equals("IS_IN_AUTO_NARRATE_MODE=ON")) {
					myAutoNarrateModeCheckBox.setChecked(true);				
				}
				if (currLineString.equals("IS_IN_AUTO_PLAY_MODE=ON")) {
					myAutoPlayModeCheckBox.setChecked(true);				
				}
				if (currLineString.equals("IS_IN_AUTO_LOOP_MODE=ON")) {
					myAutoLoopModeCheckBox.setChecked(true);				
				}
				
				if (currLineString.equals("STORE_OUTPUT=OFF")) {
					myStoreOutputCheckBox.setChecked(false);				
				}
				
				if (currLineString.equals("DESTINATION_URL_CHECKBOX=ON")) {
					myDestinationURLCheckBox.setChecked(true);				
				}								
				if (currLineString.contains("DESTINATION_URL=")) {
					if (myDestinationURLCheckBox.isChecked()) {
						myDestinationURLEditText.setEnabled(true);
//						myDestinationURLEditText.setFocusable(true);
//						myDestinationURLEditText.setFocusableInTouchMode(true);
//						myDestinationURLEditText.requestFocus();
					}
					else {
						myDestinationURLEditText.setEnabled(false);
						myDestinationURLEditText.setFocusable(false);
						myDestinationURLEditText.setFocusableInTouchMode(false);						
					}
					myDestinationURLEditText.setText(currLineString.subSequence("DESTINATION_URL=".length(), currLineString.length()));
				}
	    	}	        				
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
    	String pressTheMenuButtonToSaveStringValue = (String) getResources().getText(R.string.pressTheMenuButtonToSaveStringValue);
    	String alertString = (String) getResources().getText(R.string.alertStringValueEnglish);

    	new AlertDialog.Builder(SettingsActivity.this).setTitle(alertString)
		.setMessage(pressTheMenuButtonToSaveStringValue)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();	        		        	    	
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_settings_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case(R.id.save_settings):
				PrintWriter out = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.BASE_FILE_PATH + "usbong.config");
				if (myDebugModeCheckBox.isChecked()) {
		    		out.println("IS_IN_DEBUG_MODE=ON");
		    		UsbongUtils.setDebugMode(true);
				}
				else {
		    		out.println("IS_IN_DEBUG_MODE=OFF");					
		    		UsbongUtils.setDebugMode(false);
				}

				if (myAutoNarrateModeCheckBox.isChecked()) {
		    		out.println("IS_IN_AUTO_NARRATE_MODE=ON");
		    		UsbongUtils.IS_IN_AUTO_NARRATE_MODE=true;							
				}					
				else {
		    		out.println("IS_IN_AUTO_NARRATE_MODE=OFF");
		    		UsbongUtils.IS_IN_AUTO_NARRATE_MODE=false;							
				}					
				
				if (myAutoPlayModeCheckBox.isChecked()) {
		    		out.println("IS_IN_AUTO_PLAY_MODE=ON");
		    		UsbongUtils.IS_IN_AUTO_PLAY_MODE=true;		
				}
				else {
		    		out.println("IS_IN_AUTO_PLAY_MODE=OFF");
		    		UsbongUtils.IS_IN_AUTO_PLAY_MODE=false;							
				}

				if (myAutoLoopModeCheckBox.isChecked()) {
		    		out.println("IS_IN_AUTO_LOOP_MODE=ON");
		    		UsbongUtils.IS_IN_AUTO_LOOP_MODE=true;							
				}					
				else {
		    		out.println("IS_IN_AUTO_LOOP_MODE=OFF");
		    		UsbongUtils.IS_IN_AUTO_LOOP_MODE=false;							
				}					

				if (myStoreOutputCheckBox.isChecked()) {
		    		out.println("STORE_OUTPUT=ON");
		    		UsbongUtils.setStoreOutput(true);
				}
				else {
		    		out.println("STORE_OUTPUT=OFF");					
		    		UsbongUtils.setStoreOutput(false);
				}

				
				if (myDestinationURLCheckBox.isChecked()) {
					out.println("DESTINATION_URL_CHECKBOX=ON");
//		    		out.println("DESTINATION_URL="+myDestinationURLEditText.getText().toString());
				}				
				else {
					out.println("DESTINATION_URL_CHECKBOX=OFF");					
//		    		out.println("DESTINATION_URL=");
				}
				out.println("DESTINATION_URL="+myDestinationURLEditText.getText().toString());
			    out.close();

			    UsbongUtils.setDestinationServerURL(myDestinationURLEditText.getText().toString());
			    return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
    
	public void onCheckBoxSettingsDebugModeClicked(View v){
    	if (((CheckBox) v).isChecked()){
    		System.out.println("ON!");
//    		myDebugModeCheckBox.setChecked(true);
    	}
    	else {
    		System.out.println("OFF!");
//    		myDebugModeCheckBox.setChecked(false);
    	}    	
	}

	public void onCheckBoxSettingsDoNotStoreOutputClicked(View v){
    	if (((CheckBox) v).isChecked()){
    		System.out.println("ON!");
//    		myDebugModeCheckBox.setChecked(true);
    	}
    	else {
    		System.out.println("OFF!");
//    		myDebugModeCheckBox.setChecked(false);
    	}    	
	}

	public void onCheckBoxSettingsDestinationURLClicked(View v) {
    	if (((CheckBox) v).isChecked()){
			myDestinationURLEditText.setEnabled(true);
			myDestinationURLEditText.setFocusable(true);
//			myDestinationURLEditText.setSelected(true);
			myDestinationURLEditText.setFocusableInTouchMode(true);
			myDestinationURLEditText.requestFocus();
			myDestinationURLEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
    	}
    	else {
			myDestinationURLEditText.setEnabled(false);
			myDestinationURLEditText.setFocusable(false);
//			myDestinationURLEditText.setSelected(false);
			myDestinationURLEditText.setFocusableInTouchMode(false);
			myDestinationURLEditText.setInputType(0);
    	}    			
	}
}