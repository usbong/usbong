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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import com.example.android.apis.graphics.CameraPreview;

public class CameraActivity extends Activity 
{
	// ----------------------------------------------------------------------
    private CameraPreview mPreview;
    public String myPictureName;
    public String timeStamp;
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//        					 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //pass and get the value of String myPictureName with the key name in Intent, "myPictureName"
        myPictureName = getIntent().getStringExtra("myPictureName");
        
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, myPictureName);//(this, currentWord, timeStamp);
//        setDisplayOrientation(90);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(mPreview);
    }
    // ----------------------------------------------------------------------
}