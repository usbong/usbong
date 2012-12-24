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
import java.util.Date;

import usbong.android.utils.UsbongUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/*
 * This is Usbong's Main Menu activity. 
 */
public class UsbongMainActivity extends Activity 
{
	
	private static final int MAIN_MENU_SCREEN=0;
	private static final int GAME_GROUP_A_TITLE_SCREEN=4;

	private static final int GAME_GROUP_B_TITLE_SCREEN=7;
	private static final int GAME_GROUP_B_MAIN_SCREEN=8; //MAIN should go before WORD

	private static int currScreen=MAIN_MENU_SCREEN;
	
	private Button newEntryButton;
	private Button instructionsButton;
	private Button aboutButton;
	private Button settingsButton;
	private Button exitButton;
	
	private Button backButton;
	private Button nextButton;	
		
	private static UsbongMainActivity instance;
				
	public static String timeStamp;
	
	private static String teamName="";
	private static Date startTime;	
	
	protected UsbongDecisionTreeEngineActivity myUsbongDecisionTreeEngineActivity;
	protected SettingsActivity mySettingsActivity;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
//    	if (instance==null) { //comment this out, since the app seems to retain the instance even after we do a finish to GameActivity to close the app...
	        setContentView(R.layout.main);	        
	        instance = this;
	    	startTime = new Date();
	    	
	        reset();
	        initMainMenuScreen();
    }
    
    public static UsbongMainActivity getInstance() {
    	return instance;
    }
    
    /*
     * Initialize this activity
     */
    public void init()
    {    	
    }
    
    public void initMainMenuScreen()
    {
    	currScreen=MAIN_MENU_SCREEN;
    	
    	newEntryButton = (Button)findViewById(R.id.newEntry_button);
    	instructionsButton = (Button)findViewById(R.id.instructions_button);
    	aboutButton = (Button)findViewById(R.id.about_button);
    	settingsButton = (Button)findViewById(R.id.settings_button);
    	exitButton = (Button)findViewById(R.id.exit_button);    	

    	newEntryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				myUsbongDecisionTreeEngineActivity = new UsbongDecisionTreeEngineActivity();
				reset(); //generate new timestamp
				Intent toUsbongDecisionTreeEngineActivityIntent = new Intent().setClass(UsbongMainActivity.getInstance(), UsbongDecisionTreeEngineActivity.class);
				toUsbongDecisionTreeEngineActivityIntent.putExtra("currScreen","0"); //make currScreen=0; meaning very first screen				
				startActivity(toUsbongDecisionTreeEngineActivityIntent);				
			}
    	});


    	instructionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder prompt = new AlertDialog.Builder(UsbongMainActivity.this);
				prompt.setTitle("Instructions");
				prompt.setMessage(UsbongUtils.readTextFileInAssetsFolder(UsbongMainActivity.this,"instructions.txt")); //don't add a '/', otherwise the file would not be found
				prompt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				prompt.show();
			}
    	});

    	aboutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder prompt = new AlertDialog.Builder(UsbongMainActivity.this);
				prompt.setTitle("About");
				prompt.setMessage(UsbongUtils.readTextFileInAssetsFolder(UsbongMainActivity.this,"credits.txt")); //don't add a '/', otherwise the file would not be found
				prompt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				prompt.show();
			}
    	});
    	
    	settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
//				mySettingsActivity = new SettingsActivity();
				
				Intent toSettingsActivityIntent = new Intent().setClass(UsbongMainActivity.getInstance(), SettingsActivity.class);
				startActivity(toSettingsActivityIntent);				
			}
    	});

    	exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(UsbongMainActivity.this).setTitle("Exiting application...")
				.setMessage("Are you sure you want to exit application?")
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						finish();
					}
				}).show();
			}
    	});
    }
/*  
    public String getTeamName() {
    	return teamName;
    }
*/    
/*    
    public void initBackNextButtons()
    {
    	initBackButton();
    	initNextButton();
    }

    public void initBackPlayButtons()
    {
    	initBackButton();
    }
*/    
/*
    public void initBackButton()
    {
    	backButton = (Button)findViewById(R.id.back_button);
    	backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				decrementCurrScreen();
			}
    	});    
    }

    public void initNextButton()
    {
    	nextButton = (Button)findViewById(R.id.next_button);

    	nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				boolean dontIncrement = false;
				if (!dontIncrement)
				{
					incrementCurrScreen();
				}
			}
    	});
    }
*/        
/*    
    public Date getStartTime() {
    	return startTime;
    }
*/    
    public void reset() {
    	UsbongUtils.generateTimeStamp(); //create a new timestamp for this "New Entry"
    }
/*        
    public void decrementCurrScreen() {
    	currScreen--;
    	manageScreens();
    }

    public void incrementCurrScreen() {
    	currScreen++;
    	manageScreens();
    }
*/
/*    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	switch(currScreen) {
    			case GAME_GROUP_A_TITLE_SCREEN:
        		case GAME_GROUP_B_TITLE_SCREEN:
    				new AlertDialog.Builder(UsbongMainActivity.this).setTitle("Quitting application...")
    				.setMessage("Are you sure you want to quit application?")
    				.setNegativeButton("No", new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    					}
    				})
    				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {					
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						finish();
    						Intent mainMenu = new Intent(UsbongMainActivity.this, UsbongMainActivity.getInstance().getClass());
    						mainMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
    						startActivity(mainMenu);
    					}
    				}).show();
        			break;
        		default:
                	decrementCurrScreen();
                	break;
        	}
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }
*/    
/*    
    public void manageScreens() {
    	System.out.println("CURR SCREEN: "+currScreen);
    	if (currScreen<0) {
    		currScreen=MAIN_MENU_SCREEN;
    	}

    	if (currScreen>GAME_GROUP_B_MAIN_SCREEN) {
    		currScreen=GAME_GROUP_B_MAIN_SCREEN;
    	}

    	switch(currScreen) {
			case MAIN_MENU_SCREEN:
		    	setContentView(R.layout.main);
		    	initMainMenuScreen();
		    	reset();
		        break;    	

//			case GAME_SETTINGS_TEAM_NAME_SCREEN:
//    	    	setContentView(R.layout.game_settings_team_name);
//    	        initBackNextButtons();
//    			break;
    	}
    }
*/    
    private void showStatusDialog(String status)
	{
		AlertDialog.Builder prompt = new AlertDialog.Builder(UsbongMainActivity.this);
		prompt.setTitle("Notification");
		prompt.setMessage(status);
		prompt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		prompt.show();
	}
}