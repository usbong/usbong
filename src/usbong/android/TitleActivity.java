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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TitleActivity extends Activity 
{	
	private Button startButton;
	private Intent startIntent;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
        init();
    }
    
    /*
     * Initialize this activity
     */
    public void init()
    {
    	startButton = (Button)findViewById(R.id.start_button);
		startIntent = new Intent().setClass(this, UsbongMainActivity.class);
		
    	startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				startActivity(startIntent);
			}
    	});
    }
    
    public void initGUI()
    {  	
    }
}