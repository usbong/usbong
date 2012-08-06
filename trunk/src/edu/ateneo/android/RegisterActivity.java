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
package edu.ateneo.android;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);
        
        //EditText declarations
        final EditText etFirstName = (EditText)findViewById(R.id.reg_first_name);
        final EditText etLastName = (EditText)findViewById(R.id.reg_last_name);
        final EditText etEmail = (EditText)findViewById(R.id.reg_email);
        final EditText etUsername = (EditText)findViewById(R.id.reg_username);
        final EditText etPassword = (EditText)findViewById(R.id.reg_password);
        
        //Button declarations
        Button btnSubmit = (Button)findViewById(R.id.register_button);
        
        btnSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost();
				try {
					httpPost.setURI(new URI("http://usbong3.appspot.com/sign_up"));
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("firstname", etFirstName.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("lastname", etLastName.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("email", etEmail.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("username", etUsername.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("password", etPassword.getText().toString()));
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					try {
						HttpResponse response = httpClient.execute(httpPost);
						if(EntityUtils.toString(response.getEntity()).equals("True")) {
							Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
						}
					}
					catch(Exception a) {
						Toast.makeText(getApplicationContext(), a.toString(), Toast.LENGTH_SHORT).show();
					}
				}
				catch(Exception e) {
					Toast.makeText(getApplicationContext(), "awsad", Toast.LENGTH_SHORT).show();
				}
			}
		});
    }
}