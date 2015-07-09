/*
 * Copyright 2012 Jomel Araniego and Michael Syson
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import usbong.android.utils.UsbongUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends Activity {
    private Intent gotoUsbongMainActivityIntent;
	private HttpClient httpClient;
	private HttpPost httpPost;
	private ProgressDialog myProgressDialog;
	
	private static Activity myActivityInstance;

	//added by Mike, 9 June 2015
    public static final String MY_SAVED_USERNAME_AND_PASSWORD= "MySavedUsernameAndPassword";

/*
	private Handler handler;
*/	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_screen);
/*        
        handler = new Handler(Looper.getMainLooper());
*/        
        myActivityInstance = this;
        try 
        {
        	System.out.println(">>>>>> Creating file structure.");
        	UsbongUtils.createUsbongFileStructure();
        	
        	//default values
        	UsbongUtils.IS_IN_DEBUG_MODE=false;
        	UsbongUtils.setDestinationServerURL("127.0.0.1");//"192.168.1.105";
    		
        	if (UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config") == null) { 
        		UsbongUtils.IS_IN_DEBUG_MODE=false;    			
				PrintWriter out = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.BASE_FILE_PATH + "usbong.config");    				
				out.println("IS_IN_DEBUG_MODE=OFF");	    			
				out.close();
    		}
    		else {
				InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config");
				BufferedReader br = new BufferedReader(reader);    		
	        	String currLineString;        	
	        	while((currLineString=br.readLine())!=null)
	        	{ 		
	    			if (currLineString.equals("IS_IN_DEBUG_MODE=ON")) {
	    				UsbongUtils.IS_IN_DEBUG_MODE=true;				
	    			}
	    			else if (currLineString.contains("DESTINATION_URL=")) {
	    				UsbongUtils.setDestinationServerURL(currLineString.replace("DESTINATION_URL=", ""));
	    				System.out.println(">>>>>>>DestiantionServerURL: "+UsbongUtils.getDestinationServerURL());
	    			}
	    			/*
	    			else {
	    				UsbongUtils.IS_IN_DEBUG_MODE=false;		
	    			}*/			
	        	}	        				
    		}
        }
        catch (Exception e) {
        	System.out.println("ERROR creating usbong file structure! ");
        	e.printStackTrace();
        }
        
		gotoUsbongMainActivityIntent = new Intent().setClass(this, UsbongMainActivity.class);

/*		//commented out by Mike, 9 June 2015
        //EditText declarations
        final EditText etUsername = (EditText)findViewById(R.id.signin_username);
        final EditText etPassword = (EditText)findViewById(R.id.signin_password);
*/

        //EditText declarations
        EditText etUsernameNonFinal = (EditText)findViewById(R.id.signin_username);
        EditText etPasswordNonFinal = (EditText)findViewById(R.id.signin_password);

        //Reference: http://stackoverflow.com/questions/23024831/android-shared-preferences-example
        //; last accessed: 9 June 2015
        //answer by Elenasys
        //added by Mike, 9 June 2015
        SharedPreferences prefs = getSharedPreferences(MY_SAVED_USERNAME_AND_PASSWORD, MODE_PRIVATE);
          if (prefs!=null) {
        	System.out.println(">>>>inside!");
        	System.out.println(">>>>prefs.getString: "+prefs.getString("username",""));

          etUsernameNonFinal.setText(prefs.getString("username", ""));//"" is the default value.
          etPasswordNonFinal.setText(prefs.getString("password", "")); //"" is the default value.
        }

        final EditText etUsername = etUsernameNonFinal;
        final EditText etPassword = etPasswordNonFinal;
        
        //Button declarations
        Button btnSubmit = (Button)findViewById(R.id.signin_button);
        
        TextView tvSignUp = (TextView)findViewById(R.id.signup);
        
        tvSignUp.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
			}
		});
        
        btnSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				//Reference: http://stackoverflow.com/questions/3075506/http-connection-timeout-on-android-not-working
				//last accessed: June 7, 2012
				HttpParams httpParameters = new BasicHttpParams();
				// Set the timeout in milliseconds until a connection is established.
				int timeoutConnection = 3000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT) 
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 3000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				
				httpClient = new DefaultHttpClient(httpParameters);
				httpPost = new HttpPost();
				
				try {
					httpPost.setURI(new URI("http://usbong3.appspot.com/json"));
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			    if (etUsername.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Please enter a username.", Toast.LENGTH_LONG).show();											    	
			    }
			    else if (etPassword.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Please enter a password.", Toast.LENGTH_LONG).show();											    	
			    }
				//check if using debug username and passwords
			    else if ((UsbongUtils.IS_IN_DEBUG_MODE) &&
				    (etUsername.getText().toString().equals(UsbongUtils.debug_username) && 
				    (etPassword.getText().toString().equals(UsbongUtils.debug_password)))) {
					finish();
					startActivity(gotoUsbongMainActivityIntent);					
				}
				else {														
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("username", etUsername.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("password", etPassword.getText().toString()));				
					
					try {
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						try {
							
							//as suggested by JP Talusan
							//added by Mike, 25 May 2015
							myProgressDialog = ProgressDialog.show(myActivityInstance, "Signing in...",
									  "This takes only a short while.", true);
							
							//Reference: http://stackoverflow.com/questions/6343166/android-os-networkonmainthreadexception;
							//last accessed: 9 Jan. 2014; answer by Dr.Luiji
							Thread thread = new Thread(new Runnable(){
							    @Override
							    public void run() {							    								    	
/*							    	handler.post(new Runnable() { // This thread runs in the UI
					                    @Override
					                    public void run() {					                    
*/									        try {
									        	//Your code goes here
												HttpResponse response = httpClient.execute(httpPost);
//												Toast.makeText(getApplicationContext(), EntityUtils.toString(response.getEntity()), Toast.LENGTH_LONG).show();
												String usbongStringResponse = EntityUtils.toString(response.getEntity());
												System.out.println("EntityUtils.toString(response.getEntity()): "+usbongStringResponse);
																								
												if (usbongStringResponse.equals("True")) {
											        //Reference: http://stackoverflow.com/questions/23024831/android-shared-preferences-example
											        //; last accessed: 9 June 2015
											        //answer by Elenasys
											        //added by Mike, 9 June 2015
											        SharedPreferences.Editor editor = getSharedPreferences(MY_SAVED_USERNAME_AND_PASSWORD, MODE_PRIVATE).edit();
											        editor.putString("username", etUsername.getText().toString());
											        editor.putString("password", etPassword.getText().toString());
											        editor.commit();
										        	System.out.println(">>>>Dito!");
													
													myProgressDialog.dismiss();
													finish();
													startActivity(gotoUsbongMainActivityIntent);
												}
												else {
													myProgressDialog.dismiss();
													//Reference: http://stackoverflow.com/questions/3134683/android-toast-in-a-thread;
													//last accessed: 9 Jan. 2014; answer by Lauri Lehtinen
													myActivityInstance.runOnUiThread(new Runnable() {
													    public void run() {
													    	Toast.makeText(getApplicationContext(), "Incorrect username or password.", Toast.LENGTH_LONG).show();
													    }
													});
//													Toast.makeText(getApplicationContext(), "Incorrect username or password.", Toast.LENGTH_LONG).show();								
												}
									        } catch (Exception e) {
									        	//added by Mike, 25 May 2015
												myProgressDialog.dismiss();
									            e.printStackTrace();
												myActivityInstance.runOnUiThread(new Runnable() {
												    public void run() {
											            Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
												    }
												});
									        }
/*				                    	}
					                });
*/					                
							    }
							});

							thread.start(); 							
						}
						catch(Exception a) {
//							Toast.makeText(getApplicationContext(), "Incorrect username or password.", Toast.LENGTH_LONG).show();								
							Toast.makeText(getApplicationContext(), a.toString(), Toast.LENGTH_SHORT).show();
						}
					}
					catch(Exception e) {
						Toast.makeText(getApplicationContext(), "Usbong Error:", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
    }
}