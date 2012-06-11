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
import java.io.InputStreamReader;
import java.io.PrintWriter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import edu.ateneo.android.utils.UsbongUtils;

public class SettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.settings_screen);	    
		CheckBox myDebugModeCheckBox = (CheckBox)findViewById(R.id.settings_debug_mode_checkbox);
	    
	    try {	    	
			InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config");	
			BufferedReader br = new BufferedReader(reader);    		
	    	String currLineString;        	
	    	while((currLineString=br.readLine())!=null)
	    	{ 	
				if (currLineString.equals("IS_IN_DEBUG_MODE=ON")) {
					myDebugModeCheckBox.setChecked(true);				
				}
	    	}	        				
		}
		catch(Exception e) {
			e.printStackTrace();
		}
    }
    
	public void onCheckBoxSettingsDebugModeClicked(View v){
    	if (((CheckBox) v).isChecked()){
    		System.out.println("ON!");
			PrintWriter out = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.BASE_FILE_PATH + "usbong.config");    				
    		out.println("IS_IN_DEBUG_MODE=ON");
		    out.close();
/*
    		try {
				InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config");
				PrintWriter out = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.BASE_FILE_PATH + "usbong.config");    				

				BufferedReader br = new BufferedReader(reader);    		
	        	String currLineString;        	
	        	while((currLineString=br.readLine())!=null)
	        	{ 	
	        		System.out.println(">>>>>>>>>>>>currLineString"+currLineString);
	    			if (currLineString.contains("DEBUG_MODE")) {
	    				currLineString = "IS_IN_DEBUG_MODE=ON";				
	    			}
    				out.println(currLineString);
    				System.out.println(">>>>>>>>>>> ON!"+out.toString());
	        	}	        
	        	out.println("testing");
			    out.close();
			}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    	}
*/	    	
    	}
    	else {
    		System.out.println("OFF!");
			PrintWriter out = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.BASE_FILE_PATH + "usbong.config");    				
    		out.println("IS_IN_DEBUG_MODE=OFF");
		    out.close();
/*
    		try {
				PrintWriter out = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.BASE_FILE_PATH + "usbong.config");    				
				InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config");

				BufferedReader br = new BufferedReader(reader);    		
	        	String currLineString;        	
	        	while((currLineString=br.readLine())!=null)
	        	{ 	
	    			if (currLineString.contains("DEBUG_MODE")) {
	    				currLineString = "IS_IN_DEBUG_MODE=OFF";				
	    			}
    				out.println(currLineString);
    				System.out.println(">>>>>>>>>>> OFF!"+out.toString());
	        	}	        				
			    out.close();
			}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    	}
*/	    	
    	}    	
	}
}