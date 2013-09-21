/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//@edited: Michael Syson
//@date created: July 21, 2011
//@last updated: Sept. 21, 2013
//@desc: made public the methods/class
//@ref: Android\android-sdk\samples\android-8\ApiDemos\src\com\example\android\apis\graphics\CameraPreview.java

package com.example.android.apis.graphics;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import usbong.android.utils.UsbongUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
//import android.hardware.Camera.CameraInfo; //android api 9
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener {
	private static final String TAG = "UsbongCameraPreview";

	private SurfaceHolder mHolder;
	private Camera mCamera;
	
    private CameraPreview mContext = this;
	public String myPictureName;
	public String timeStamp;
	
	private static Context myContext;
	private Display myDisplay;
	
	private boolean isPreviewRunning=false;
    
    public CameraPreview(Context context, String myPictureName) {
        super(context);
        myContext = context;
        this.myPictureName = myPictureName;
                
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
/*          
	    new AlertDialog.Builder(CameraPreview.this.getContext()).setTitle("Usbong Tip")
	    	.setMessage("Tap the screen to take a picture!")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {		        
				dialog.dismiss();
			}
		}).show();		
*/	    
	    this.setOnTouchListener(this);
    }

    @Override
	public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        mCamera = Camera.open();

        try {
           mCamera.setPreviewDisplay(holder);
           
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
            // TODO: add more exception handling logic here
        }
    }

    @Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

/*
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
*/    
    //Android SDK; last accessed: 3 Oct 2011
    public static void setCameraDisplayOrientation(Activity activity,
            int cameraId, android.hardware.Camera camera) {
/*//found in Android SDK 9
    	android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
*/        
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        result = (0 + degrees) % 360;
//        result = (360 - result) % 360;  // compensate the mirror

/*
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
*/        
        camera.setDisplayOrientation(result);
    }

    
    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	
    	if (isPreviewRunning) {
    		mCamera.stopPreview();
    	}
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
/*		List<Size> sizes = parameters.getSupportedPreviewSizes();
        Size optimalSize = getOptimalPreviewSize(sizes, w, h);
  */
        
//        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        
        //added by Mike, Aug. 20, 2011
        //http://stackoverflow.com/questions/3841122/android-camera-preview-is-sideways/5110406#5110406; last accessed: Aug. 20, 2011
        myDisplay = ((WindowManager)myContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        System.out.println(">>>>>>myDisplay.getRotation(): "+myDisplay.getRotation());
/*
        if(myDisplay.getRotation() == Surface.ROTATION_0) //0
        {
            parameters.setPreviewSize(optimalSize.height, optimalSize.width);                           
            mCamera.setDisplayOrientation(90);
        }

        if(myDisplay.getRotation() == Surface.ROTATION_90) //1
        {
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);                           
            mCamera.setDisplayOrientation(-90);
        }

        if(myDisplay.getRotation() == Surface.ROTATION_180) //2
        {
            parameters.setPreviewSize(optimalSize.height, optimalSize.width);               
        }

        if(myDisplay.getRotation() == Surface.ROTATION_270) //3
        {
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
            mCamera.setDisplayOrientation(180);
        }
*/        
        mCamera.setParameters(parameters);
        try {
        	mCamera.setPreviewDisplay(holder);  
        	mCamera.startPreview();
        	isPreviewRunning=true;
        }
        catch(Exception e)
        {
            Log.d(TAG, "Cannot start preview", e);    
        }
        mCamera.startPreview();
    }
    
    //added by Mike, July 22, 2011
    @Override
	public boolean onTouch(View arg0, MotionEvent me) {
		switch(me.getAction()) {
		case MotionEvent.ACTION_DOWN:			
			mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
			return true;
		}
		return false;
	}
	
	//Reference: http://p2p.wrox.com/book-professional-android-application-development-isbn-978-0-470-34471-2/72528-article-using-android-camera.html
	//Last accessed on: July 12, 2010
	ShutterCallback shutterCallback = new ShutterCallback() {
	  @Override
	public void onShutter() {
	    // TODO Do something when the shutter closes.
	  }
	};
	 
	PictureCallback rawCallback = new PictureCallback() {
	  @Override
	public void onPictureTaken(byte[] _data, Camera _camera) {
			if (_data != null) {

				//Intent mIntent = new Intent();

				StoreByteImage(mContext, _data, 50,
						"ImageName");
				mCamera.startPreview();

				//setResult(FOTO_MODE, mIntent);
				//finish();
			}

	  }
	};
	 
	PictureCallback jpegCallback = new PictureCallback() {
	@Override
	public void onPictureTaken(final byte[] _data, Camera _camera) {
			if (_data != null) {

				//Intent mIntent = new Intent();
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setCancelable(true);
				builder.setMessage("Do you want to save this image?");
				//builder.setIcon(R.drawable.dialog_question);
				builder.setTitle("Image Saving");
				builder.setInverseBackgroundForced(true);
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				  @Override
				  public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();				    

				    StoreByteImage(mContext, _data, 50,
							"ImageName");		
				    //removed by Mike, Sept. 21, 2013
//					UsbongDecisionTreeEngineActivity.setCurrScreen(UsbongDecisionTreeEngineActivity.PHOTO_CAPTURE_SCREEN);				
					((Activity) getContext()).finish();
				  }
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				  @Override
				  public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();
					mCamera.startPreview();

				  }
				});
				AlertDialog alert = builder.create();
				alert.show();
				
				//setResult(FOTO_MODE, mIntent);
				//finish();
				}
			System.out.println("DITO SA onPictureTaken(...)!");

	  }
	};
	public boolean StoreByteImage(CameraPreview mContext2, byte[] imageData,
			int quality, String expName) {

//	        File sdImageMainDirectory = new File("/sdcard/usbong" + "/" +UsbongUtils.getDateTimeStamp() +"/");
        	File sdImageMainDirectory = new File(UsbongUtils.BASE_FILE_PATH + "/" +UsbongUtils.getDateTimeStamp() +"/");
    	
		// stackoverflow.com/questions/2130932/how-to-create-directory-automatically-on-sd-card
	        if (!sdImageMainDirectory.exists() && !sdImageMainDirectory.mkdirs()) 
	    	{
	    		System.out.println("Path to file could not be created.");
	    	}
	        
	        FileOutputStream fileOutputStream = null;
//			String nameFile;
			try {

				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inSampleSize = 5;
				
				Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,
						imageData.length,options);

				File outputFile= new File(sdImageMainDirectory, myPictureName  +".jpg" );
				
				fileOutputStream = new FileOutputStream(outputFile);
	  	  
				BufferedOutputStream bos = new BufferedOutputStream(
						fileOutputStream);

				myImage.compress(CompressFormat.JPEG, quality, bos);

				bos.flush();
				bos.close();

//				File imageFile = new File("/sdcard/usbong/" +UsbongUtils.getDateTimeStamp() +"/" + myPictureName+".jpg");
				File imageFile = new File(UsbongUtils.BASE_FILE_PATH +UsbongUtils.getDateTimeStamp() +"/" + myPictureName+".jpg");
						    
				if(imageFile.exists())
				{
					System.out.println("FILE EXISTS!");
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}
}
