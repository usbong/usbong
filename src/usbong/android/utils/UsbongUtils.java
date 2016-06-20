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
package usbong.android.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import usbong.android.R;
import usbong.android.UsbongDecisionTreeEngineActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class UsbongUtils {		
	public static boolean IS_IN_DEBUG_MODE=false;
	public static boolean STORE_OUTPUT=true;
	public static boolean IS_IN_AUTO_NARRATE_MODE=true;
	public static boolean IS_IN_AUTO_PLAY_MODE=false;
	public static boolean IS_IN_AUTO_LOOP_MODE=false;
	
	public static boolean hasUnlockedAllLanguages=true;

	public final static String API_KEY = "AIzaSyB5mM_lk_bbdT5nUWQTO6S5FyZ9IgaxqXc"; //added by Mike, 20151120

	public static String DEFAULT_UTREE_TO_LOAD="UsbongDemoTree"; //updated by Mike, 8 April 2016
	public static String BASE_FILE_PATH = Environment.getExternalStorageDirectory()+"/usbong/";
	public static String USBONG_TREES_FILE_PATH = BASE_FILE_PATH + "usbong_trees/";

	//added by Mike, 20160511
	public final static String myPackageName="usbong.android";
	public static String myAppTreeFolder = "/usbong/usbong_trees/";

	//added by Mike, 20160420
	public static boolean hasUnlockedLocalLanguages=false; //except Filipino
	public static boolean hasUnlockedForeignLanguages=false; //except English
	
	//added by Mike, 20160504
	public static Activity myActivityInstance;
	
	//	public static String BASE_FILE_PATH = "/sdcard/usbong/";
	private static String timeStamp;
	private static String dateTimeStamp;

    public static final int MY_DATA_CHECK_CODE=0;
	public static final int EMAIL_SENDING_SUCCESS=99;
	public static final int FROM_MY_YOUTUBE_ACTIVITY = 1; //activity result, added by Mike, 20151124
	public static final int FROM_MY_YOUTUBE_ACTIVITY_TO_MAIN_MENU = 2; //activity result, added by Mike, 20151129
	    	
	public static final int LANGUAGE_ENGLISH=0; 
	public static final int LANGUAGE_FILIPINO=1;
	public static final int LANGUAGE_JAPANESE=2;
	public static final int LANGUAGE_MANDARIN=3; 
	public static final int LANGUAGE_BISAYA=4;
	public static final int LANGUAGE_ILONGGO=5;
	public static final int LANGUAGE_KAPAMPANGAN=6;
	public static final int LANGUAGE_FRENCH=7;
	public static final int LANGUAGE_MANDARIN_SIMPLIFIED=8;
	public static final int LANGUAGE_MANDARIN_TRADITIONAL=9;
	public static final int LANGUAGE_SPANISH=10;
	public static final int LANGUAGE_KOREAN=11;
		
	private static String destinationServerURL;
	
	public static final String debug_username="usbong";
	public static final String debug_password="usbong";
	
	public static AssetManager myAssetManager;
	
	private static final String TAG = "UsbongUtils";
	
	public static final int IS_TEXTVIEW = 0;
	public static final int IS_RADIOBUTTON = 1;
	public static final int IS_CHECKBOX = 2;
	
	public static String myTreeFileName="";
	
	public static String usbongDefaultLanguage="Filipino"; //default is Filipino
//	public static String usbongSetLanguage=usbongDefaultLanguage; //default is Filipino
	private static String currLanguage=usbongDefaultLanguage;
	private static String usbongDefaultLanguageOfXML=usbongDefaultLanguage; //added by Mike, 20160608
	
	public static final boolean USE_UNESCAPE=true; //allows the use of \n (new line) in the decision tree	

	public static ArrayList<String> tokenizedStringList;
	
	//added by Mike, 20151129
	public static final int MY_PROMPT_TITLE = 0;
	public static final int MY_PROMPT_MESSAGE = 1;
	public static final int MY_PROMPT_POSITIVE_BUTTON_TEXT = 2;
	public static final int MY_PROMPT_NEGATIVE_BUTTON_TEXT = 3;
	
//	public static YouTubePlayer myYouTubePlayer;
    private static String myYouTubeVideoId;

    //added by Mike, 20160413
    public static Hashtable<String,Hashtable<String,String>> myHashtableOfWordHints;
	
	//added by Mike, 20160426
    private static IInAppBillingService mService;
    private static ServiceConnection mServiceConn;
    private static Bundle ownedItems;

    public static boolean hasLoadedPurchaseLanguageBundleList=false;
	private static int ownedItemsResponse=-1; //added by Mike, 20160507
    
	//added by Mike, 22 Sept. 2015
	public static String getCurrLanguage(){
		return currLanguage;
	}
	
	//added by Mike, 22 Sept. 2015	
	public static void setCurrLanguage(String s) {
		currLanguage = s;
	}
	
	//added by Mike, Feb. 11, 2013
	public static void setDebugMode(boolean b) {
		IS_IN_DEBUG_MODE=b;
	}

	//added by Mike, Feb. 24, 2014
	public static void setStoreOutput(boolean b) {
		STORE_OUTPUT=b;
	}
/*//commented out by Mike, 24 Sept. 2015; now using: android.util.Patterns.EMAIL_ADDRESS
	//Reference: Andrei Buneyeu's answer in http://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address-on-android;
	//last accessed: 21 Aug. 2012
	public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
	          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	          "\\@" +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	          "(" +
	          "\\." +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	          ")+"
	      );	
*/	
	public static boolean is_a_utree=false;
	
	public static char[] alphanumeric = {'a','b','c','d','e','f','g','h','i','j',
        							 	 'k','l','m','n','o','p','q','r','s','t',
        							 	 'u','v','w','x','y','z','0','1','2','3',
        							 	 '4','5','6','7','8','9'};
	
//	private static final Pattern hintStringTokenizerPattern = Pattern.compile("\W*");
	
	public static boolean checkEmail(String email) {
/*        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
 */
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(); //updated by Mike, 24 Sept. 2015
	}

	//added by Mike, 20160608
	public static boolean isAnAutoPlayException(UsbongDecisionTreeEngineActivity a) {
		//this would cover:
		//		public static final int TEXT_DISPLAY_SCREEN=9;	
		//		public static final int IMAGE_DISPLAY_SCREEN=10;
		//		public static final int TEXT_IMAGE_DISPLAY_SCREEN=11;
		//		public static final int IMAGE_TEXT_DISPLAY_SCREEN=12;
		//		public static final int CLASSIFICATION_SCREEN=13;		
		if ((a.currScreen>=UsbongConstants.TEXT_DISPLAY_SCREEN) && (a.currScreen<=UsbongConstants.CLASSIFICATION_SCREEN)) {
			return true;
		}
		return false;
	}
	
	//-----------------------------------------------------------------
    //setup In-App Billing Service
    //reference: http://developer.android.com/google/play/billing/billing_integrate.html
    //last accessed: 20160123
    //added by Mike, 20160426
    //-----------------------------------------------------------------
	public static void initInAppBillingService(Activity a) {
		//added by Mike, 20160504
		myActivityInstance = a;
		
	    mServiceConn = new ServiceConnection() {
	       @Override
	       public void onServiceDisconnected(ComponentName name) {
	           mService = null;
	       }

	       @Override
	       public void onServiceConnected(ComponentName name,
	          IBinder service) {
	           mService = IInAppBillingService.Stub.asInterface(service);
	       }
	    };
	    
    	//added by Mike, 20160504
	    Intent serviceIntent =
	    	      new Intent("com.android.vending.billing.InAppBillingService.BIND");
	    serviceIntent.setPackage("com.android.vending");
	    myActivityInstance.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

	    try {
	    	//added by Mike, 20160425
	    	if (mService != null) {
            	ownedItems = mService.getPurchases(3, myActivityInstance.getPackageName(), "inapp", null);
/*            	
            	ownedItemsResponse = -1;
            	ownedItemsResponse = ownedItems.getInt("RESPONSE_CODE");
*/
    	    	new Thread(new Runnable() {
    			    public void run() {
    			    	//while (ownedItemsResponse==-1) {
    			    	while (ownedItems==null) {
    			            android.os.SystemClock.sleep(3000); 						    		
    			            Log.d(">>>","sleeping");
    			    	}
    		    		return; //end this background thread
    			    }
    			}).start();
	    	}	    	
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	public static Bundle getInAppOwnedItems() {
		return ownedItems;
	}

	public static IInAppBillingService getInAppMService() {
		return mService;
	}

	public static void unbindInAppService(Activity a) {
		if (mService != null) {
	        a.unbindService(mServiceConn);
	    }
	}
/*	
	public static void checkForInAppOwnedItems(Activity myActivity) {
		if (ownedItems!=null) {
	    	//create a separate thread; don't use the main thread
	    	Handler handler = new Handler();
    		final Runnable r = new Runnable() {
    		    public void run() {
                    try {
                	    int response=-1;
                    	response = ownedItems.getInt("RESPONSE_CODE");
            			
            			if (response == 0) { //SUCCESS
        				   ArrayList<String> ownedSkus =
        				      ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
        				   ArrayList<String>  purchaseDataList =
        				      ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
        				   ArrayList<String>  signatureList =
        				      ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");

        				   for (int i = 0; i < purchaseDataList.size(); ++i) {
        				      String purchaseData = purchaseDataList.get(i);
        				      String signature = signatureList.get(i);
        				      String sku = ownedSkus.get(i);

        			    	  if (sku.contains("local")) {
        				        hasUnlockedLocalLanguages=true;
        			    	  }
        			    	  else { //foreign
        				        hasUnlockedForeignLanguages=true;
        			    	  }
        				   }
        				}
                    } catch (Exception e) {
                    	e.printStackTrace();
                    }
    		    }
    		};
    		handler.postDelayed(r, 1000);
	    }
	    else {
	        //Reference: http://stackoverflow.com/questions/23024831/android-shared-preferences-example
	        //; last accessed: 20150609
	        //answer by Elenasys
	        //added by Mike, 20160425
	        SharedPreferences prefs = myActivity.getSharedPreferences(UsbongConstants.MY_PURCHASED_ITEMS, android.content.Context.MODE_PRIVATE);
	        if (prefs!=null) {
	        	if (prefs.getString(UsbongConstants.ALL_LOCAL_LANGUAGES_PRODUCT_ID, null)!=null) {
		        	hasUnlockedLocalLanguages=true;
	        	}

	        	if (prefs.getString(UsbongConstants.ALL_FOREIGN_LANGUAGES_PRODUCT_ID, null)!=null) {
		        	hasUnlockedForeignLanguages=true;
	        	}
	        }
	    }
	}
*/	
	//edited by Mike, 20160417
	public static void initUsbongConfigFile() {
        try 
        {
        	System.out.println(">>>>>> Creating file structure.");
        	UsbongUtils.createFilePath(BASE_FILE_PATH);
        	
        	//default values
        	UsbongUtils.IS_IN_DEBUG_MODE=false;
        	UsbongUtils.setDestinationServerURL("127.0.0.1");//"192.168.1.105";
    		
        	if (UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config") == null) { 
        		UsbongUtils.IS_IN_DEBUG_MODE=false;    			
				PrintWriter out = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.BASE_FILE_PATH + "usbong.config");    				
				out.println("IS_IN_DEBUG_MODE=OFF");	   
				
				//added by Mike, 20160408
				UsbongUtils.IS_IN_AUTO_NARRATE_MODE=true;							
			    out.println("IS_IN_AUTO_NARRATE_MODE=ON");

	    		UsbongUtils.IS_IN_AUTO_PLAY_MODE=false;						
			    out.println("IS_IN_AUTO_PLAY_MODE=OFF");

			    //added by Mike, 20160417
	    		UsbongUtils.IS_IN_AUTO_LOOP_MODE=false;						
			    out.println("IS_IN_AUTO_LOOP_MODE=OFF");

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
	    			else if (currLineString.equals("IS_IN_AUTO_NARRATE_MODE=ON")) {
						UsbongUtils.IS_IN_AUTO_NARRATE_MODE=true;			
					}
	    			else if (currLineString.equals("IS_IN_AUTO_PLAY_MODE=ON")) {
						UsbongUtils.IS_IN_AUTO_PLAY_MODE=true;			
					}
	    			else if (currLineString.equals("IS_IN_AUTO_LOOP_MODE=ON")) {
						UsbongUtils.IS_IN_AUTO_LOOP_MODE=true;			
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
	}
	
	//updated by Mike, 20160504
	//must comply with JDK 1.6
	public static boolean isLocalLanguage(String s) {
		if (s.equals("Bisaya")) {
			return true;
		}
		else if (s.equals("Ilonggo")) {
			return true;
		}
		else if (s.equals("Kapampangan")) {
			return true;
		}
		return false;
	}
	
	//updated by Mike, 20160504
	//must comply with JDK 1.6
	public static boolean isLanguageIsAnException(String s) {
		if (s.equals("English")) {
			return true;
		}
		else if (s.equals("Filipino")) {
			return true;
		}				
		return false;
	}

	public static boolean isInDebugMode() {
	    try {	    	
			InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config");	
			BufferedReader br = new BufferedReader(reader);    		
	    	String currLineString;        	
	    	while((currLineString=br.readLine())!=null)
	    	{ 	
				if (currLineString.equals("IS_IN_DEBUG_MODE=ON")) {
					return true;
				}
	    	}	        				
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	    return false;
	}

	public static boolean checkIfStoreOutput() {
	    try {	    	
			InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config");	
			BufferedReader br = new BufferedReader(reader);    		
	    	String currLineString;        	
	    	while((currLineString=br.readLine())!=null)
	    	{ 	
				if (currLineString.equals("STORE_OUTPUT=OFF")) {
					return false;
				}
	    	}	        				
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	    return true;
	}

	public static String getDestinationServerURL() {
		return destinationServerURL;
	}
	
	public static void setDestinationServerURL(String s) {
		destinationServerURL = s;
	}	
	
    public static void generateDateTimeStamp() {
		//2015-10-31T14:49:20+08:00
		dateTimeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US).format(new Date());
		Log.d(">>>", "dateTimeStamp: "+dateTimeStamp);
    }

    public static String getDateTimeStamp() {
		return dateTimeStamp;
    }

    public static void generateTimeStamp() {
		Calendar date = Calendar.getInstance();
		int hour = date.get(Calendar.HOUR_OF_DAY);
		int min = date.get(Calendar.MINUTE);
		int sec = date.get(Calendar.SECOND);
		
		timeStamp =  hour +"hr "+ min +"min "+ sec + "sec";
    }
/*
    public static String getTimeStamp() {
		return timeStamp;
    }
*/
    public static String getCurrTimeStamp() {
    	generateTimeStamp();
		return timeStamp;
    }
        
	public static void createFilePath(String filePath) throws IOException {
		File directory = new File(filePath);		
		if (!directory.exists() && !directory.mkdirs()) 
    	{
    		throw new IOException("File Path to file could not be created.");
    	}    			
	}
	
    public static void createUsbongFileStructure() throws IOException {
		//code below doesn't seem to work
//		String baseFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "usbong/";
		File directory = new File(USBONG_TREES_FILE_PATH);
		System.out.println(">>>> Directory: " + directory.getAbsolutePath());
		
		if (!directory.exists() && !directory.mkdirs()) 
    	{
			System.out.println(">>>> Creating file structure for usbong");
    		throw new IOException("Base File Path to file could not be created.");
    	}    			
		System.out.println(">>>> Leaving createUsbongFileStructure");
	}	
	
	public static void createNewOutputFolderStructure() throws IOException {
//		String baseFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "usbong/";
		File directory = new File(BASE_FILE_PATH + UsbongUtils.getDateTimeStamp()+"/");
		System.out.println(">>>> Directory: " + directory.getAbsolutePath());
		
		if (!directory.exists() && !directory.mkdirs()) 
    	{
    		throw new IOException("Base File Path to file could not be created.");
    	}    			
	}	
	
    public static String readTextFileInAssetsFolder(Activity a, String filename) {
		//READ A FILE
		//Reference: Jeffrey Jongko, Aug. 31, 2010
    	try {
//    		byte[] b = new byte[100];    		
    		AssetManager myAssetManager = a.getAssets();
    		InputStream is = myAssetManager.open(filename);

    		BufferedReader br = new BufferedReader(new InputStreamReader(is));
        	String currLineString="";
        	String finalString="";

        	while((currLineString=br.readLine())!=null)
        	{
        		finalString = finalString + currLineString+"\n";
        	}	    
        	is.close();    		

        	return finalString;
    	}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE in readTextFileInAssetsFolder(...).");
    		e.printStackTrace();
    	}    		
    	return null;
    }
/*
    public static void storeAssetsFileIntoSDCard(Activity a, String filename) throws IOException {
    	String destination = USBONG_TREES_FILE_PATH+"usbong_demo_tree.xml";
		File file = new File(destination);
		if(!file.exists()) {
	    	//arg#1 is the destination, arg#2 is the string 
			storeOutputInSDCard(destination, readTextFileInAssetsFolder(a,filename));		
    	}
    }
*/    
    public static void storeAssetsFileIntoSDCard(Activity a, String filename) throws IOException {
    	String destination = USBONG_TREES_FILE_PATH;//+"dash.utree";

    	//delete usbong_demo_tree.xml
    	File file = new File(destination);
    	if (file.exists()) {
        	file.delete();    		
    	}

//    	destination = USBONG_TREES_FILE_PATH+"dash.utree"; //commented out by Mike, 23 April 2015

/* //commented out by Mike, 24 April 2015
    	//replace usbong_demo_tree.xml with usbong_demo_tree.utree
    	file = new File(destination);
    	file.delete();
    	//start anew
//    	if(!file.exists()) {
*/
			
//			System.out.println(">>>>>> File " + destination + " doesn't exist. Creating file.");
			file.mkdirs();

	    	//arg#1 is the destination, arg#2 is the string 
//			storeOutputInSDCard(destination+"/dash.utree", readTextFileInAssetsFolder(a,filename));		
//		storeOutputInSDCard(destination, readTextFileInAssetsFolder(a,filename));		
		copyAssetToSDCard(filename, destination); //added by Mike, 30 April 2015

/*	//commented out by Mike, 24 April 2015		
			file = new File(destination+"/res/");
			file.mkdirs();
			
			copyAssetToSDCard("sample_image.png", destination+"/res/");
			copyAssetToSDCard("bg.png", destination+"/res/");
		//    	}
*/
    }

    public static void storeUsbongAppAssetsFileIntoSDCard(Activity a, String filename) throws IOException {
    	String destination = USBONG_TREES_FILE_PATH+DEFAULT_UTREE_TO_LOAD+".xml";

    	//delete usbong_demo_tree.xml
    	File file = new File(destination);
    	if (file.exists()) {
        	file.delete();    		
    	}

    	destination = USBONG_TREES_FILE_PATH+DEFAULT_UTREE_TO_LOAD+".utree";

    	//replace usbong_demo_tree.xml with usbong_demo_tree.utree
    	file = new File(destination);
    	file.delete();

    	//start anew
//    	if(!file.exists()) {
			
//			System.out.println(">>>>>> File " + destination + " doesn't exist. Creating file.");
			file.mkdirs();

	    	//arg#1 is the destination, arg#2 is the string 
			storeOutputInSDCard(destination+"/"+DEFAULT_UTREE_TO_LOAD+".xml", readTextFileInAssetsFolder(a,filename));		
			
			file = new File(destination+"/res/");
			file.mkdirs();
			
			copyAssetToSDCard("sample_image.png", destination+"/res/");
			copyAssetToSDCard("bg.png", destination+"/res/");
		//    	}
    }

    private static void copyAssetToSDCard(String filename, String filepath) {
        InputStream in = null;
        OutputStream out = null;
        try {
          in = myAssetManager.open(filename);
          out = new FileOutputStream(filepath + filename);
          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;
        } catch(Exception e) {
            Log.e("tag", e.getMessage());
        }       

    }

    //added by Mike, 21 July 2015
    public static Uri getAudioUriFromUTree(String filename, String language) {
/*    	
    	String filePath = UsbongUtils.USBONG_TREES_FILE_PATH + myTreeFileName+".utree/audio/"+language+filename+".mp3";
		File file = new File(filePath);
		if(!file.exists())
		{
			file = new File(UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+myTreeFileName+".utree/audio/"+language+filename+".mp3");

			if(!file.exists()) {						
				return null;
			}
		}
		//if this point is reached, this means that trans file exists		  
		return Uri.fromFile(file);
*/
		return Uri.fromFile(new File(getAudioFilePathFromUTree(filename, language)));
    }

    //added by Mike, 21 July 2015
    public static String getAudioFilePathFromUTree(String filename, String language) {
/*    	
    	String filePath = UsbongUtils.USBONG_TREES_FILE_PATH + myTreeFileName+".utree/audio/"+language+"/"+filename+".mp3";    	    	
		File file = new File(filePath);
		if(!file.exists())
		{
			file = new File(UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+myTreeFileName+".utree/audio/"+"/"+language+filename+".mp3");

			if(!file.exists()) {						
				return null;
			}
		}
		//if this point is reached, this means that trans file exists
		return file.getAbsolutePath();
*/
    	String[] fileExtensions = {".mp3",".wav",".mp4",".ogg",".m4a"};
    	for (int i=0; i<fileExtensions.length; i++) {
        	String filePath = UsbongUtils.USBONG_TREES_FILE_PATH + "temp/" + myTreeFileName+".utree/audio/"+language+"/"+filename+fileExtensions[i];    	    	
        	Log.d(">>>>filePath",filePath);
    		File file = new File(filePath);
    		if(file.exists()) {
    			return file.getAbsolutePath();
    		}
    	}
/*    	
    	for (int i=0; i<fileExtensions.length; i++) {
        	String filePath = UsbongUtils.USBONG_TREES_FILE_PATH + myTreeFileName+".utree/audio/"+language+"/"+filename+fileExtensions[i];
    		File file = new File(filePath);
    		if(file.exists()) {
    			return file.getAbsolutePath();
    		}
    	}    	
*/    	
		return null;    	
    }
    
    //added by Mike, 21 July 2015
    public static String getAudioFilePathForThisScreenIfAvailable(String currUsbongNode) {
		StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
		int totalTokens = st.countTokens();
		int counter = 0;		
		String myStringToken = "";
		
		while (counter<totalTokens-1) { //up to second to the last only
			myStringToken = st.nextToken(); 
			counter++;
			
			//Reference: http://stackoverflow.com/questions/9700115/difference-between-matches-and-equalsignorecase-or-equals-in-string-class;
			//last accessed: 19 July 2015; answer by MByD 
/*
			if (myStringToken.matches("audioName=.*")) {
		        Log.d(">>>>myStringToken.substring(10)",myStringToken.substring(10));
				return myStringToken.substring(10); //why 10? to remove "audioName="
			}
*/			
			if (myStringToken.matches("@audioName=.*")) {
		        Log.d(">>>>myStringToken.substring(11)",myStringToken.substring(11));
				return myStringToken.substring(11); //why 11? to remove "@audioName="
			}			
		}
		return "";
    }

    //added by Mike, 21 July 2015
    public static String getBGAudioFilePathFromUTree(String filename) {
    	String[] fileExtensions = {".mp3",".wav",".mp4",".ogg",".m4a"};
    	for (int i=0; i<fileExtensions.length; i++) {
        	String filePath = UsbongUtils.USBONG_TREES_FILE_PATH + "temp/" + myTreeFileName+".utree/audio/"+filename+fileExtensions[i];    	    	
        	Log.d(">>>>filePath",filePath);
    		File file = new File(filePath);
    		if(file.exists()) {
    			return file.getAbsolutePath();
    		}
    	}
		return null;    	
    }
    
    //added by Mike, 21 July 2015
    public static String getBGAudioFilePathForThisScreenIfAvailable(String currUsbongNode) {
		StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
		int totalTokens = st.countTokens();
		int counter = 0;		
		String myStringToken = "";
		
		while (counter<totalTokens-1) { //up to second to the last only
			myStringToken = st.nextToken(); 
			counter++;

			Log.d(">>>>>",myStringToken);
			
			if (myStringToken.matches("@bgAudioName=.*")) {
		        Log.d(">>>>myStringToken.substring(13)",myStringToken.substring(11));
				return myStringToken.substring(13); //why 13? to remove "@bgAudioName="
			}			
		}
		return "";
    }

/*    
    //from rohith (stackoverflow); 
    //Reference: http://stackoverflow.com/questions/4447477/android-how-to-copy-files-in-assets-to-sdcard;
    //last accessed: 2 Sept 2012
    private static void copyAssets() {
//        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = myAssetManager.list("");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
              in = myAssetManager.open(filename);
              out = new FileOutputStream("/sdcard/" + filename);
              copyFile(in, out);
              in.close();
              in = null;
              out.flush();
              out.close();
              out = null;
            } catch(Exception e) {
                Log.e("tag", e.getMessage());
            }       
        }
    }
*/    
    //from rohith (stackoverflow); 
    //Reference: http://stackoverflow.com/questions/4447477/android-how-to-copy-files-in-assets-to-sdcard;
    //last accessed: 2 Sept 2012
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }
    
    //This methods gets the last string token element using '~' as delimeter
    //example: <task-node name="radioButtons~1~Life is good">
    //becomes "Life is good"
    public static String trimUsbongNodeName(String currUsbongNode) {
		StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
		String myStringToken = st.nextToken();
		while (st.hasMoreTokens()) {
			myStringToken = st.nextToken(); 
		}
		return myStringToken;
    }

    //This methods checks the string for something like "@name=getInput()", 
    //where @name is the variable name (this can be any variable name, 
    //as long as it is preceded by "@").
    //Afterwards, this method processes it accordingly.
    //example: <task-node name="textField~@name=getInput()~What is your name?">
    public static void processStoreVariableMethod(Activity a, String currUsbongNode) {
		if (!currUsbongNode.contains("@")) {
			return;
		}
    	
    	StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
		String myStringToken = st.nextToken();
		while (st.hasMoreTokens()) {
			if (myStringToken.contains("@")) {
				Log.d(">>>>>>","inside myStringToken.contains('@')");
				String[] s = myStringToken.split("=");

				Log.d("s[0]: ",s[0]);
				Log.d("s[1]: ",s[1]);

				((UsbongDecisionTreeEngineActivity)a).setVariableOntoMyUsbongVariableMemory(s[0],s[1]);
			}
			
			myStringToken = st.nextToken(); 
		}
    }    
    
    //This methods gets the second to the last string token element using '~' as delimeter
    //example: <task-node name="textClickableImageDisplay~frame_1~You've just clicked the image!~Life is good">
    //becomes "Life is good"
    public static String getAlertName(String currUsbongNode) {
		StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
		String myStringToken = st.nextToken();
		String myPreviousStringToken="";
		while (st.hasMoreTokens()) {
			myPreviousStringToken = myStringToken;
			myStringToken = st.nextToken(); 
		}
		return myPreviousStringToken;
    }
    
    //This methods gets the name of the image resource
    //example: <task-node name="textImageDisplay~frame_13~Happy Mike">
    //becomes "frame_13"
    //"frame_13" is the name of the image resource, currently located in res/drawable
    //it should always come after the first ~
    public static String getResName(String currUsbongNode) {
		StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
		String myStringToken = st.nextToken();
		myStringToken = st.nextToken(); 
		return myStringToken;
    }

    //This methods gets the name of the YouTube ID
    //example: <task-node name="youtubeVideoWithText~https://www.youtube.com/watch?v=mzB2P7bbmio~Usbong Builder Demo">
    //becomes "mzB2P7bbmio"
    public static String getYouTubeVideoID(String currUsbongNode) {
		StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
		String myStringToken = st.nextToken();
		myStringToken = st.nextToken(); 
		
		StringTokenizer stForActualYouTubeLink = new StringTokenizer(myStringToken, "=");
		String myYouTubeVideoStringToken = stForActualYouTubeLink.nextToken();
		myYouTubeVideoStringToken = stForActualYouTubeLink.nextToken();
		
		return myYouTubeVideoStringToken;
    }
    
    //This methods checks all the tokens for the "optional" keyword
    //from the start of the string up to the second to the last token 
    //with '~' as delimeter
    //(why? because the last token is usually reserved for the text to display) 
    //example: <task-node name="textDisplay~1~optional~Name">
    public static boolean isAnOptionalNode(String currUsbongNode) {
		StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
		int totalTokens = st.countTokens();
		int counter = 0;		
		String myStringToken = "";
		
		while (counter<totalTokens-1) { //up to second to the last only
			myStringToken = st.nextToken(); 
			counter++;
			
			if (myStringToken.equals("optional")) {
				return true;
			}
		}
		return false;
    }

    //added by Mike, 19 July 2015
    public static String getSpecificBGImageStringForThisScreenIfAvailable(String currUsbongNode) {
		StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
		int totalTokens = st.countTokens();
		int counter = 0;		
		String myStringToken = "";
		
		while (counter<totalTokens-1) { //up to second to the last only
			myStringToken = st.nextToken(); 
			counter++;
			
			//Reference: http://stackoverflow.com/questions/9700115/difference-between-matches-and-equalsignorecase-or-equals-in-string-class;
			//last accessed: 19 July 2015; answer by MByD 
			if (myStringToken.matches("@bg=.*")) {
		        Log.d(">>>>myStringToken.substring(4)",myStringToken.substring(4));
				return myStringToken.substring(4); //why 3? to remove "bg="
			}
		}
		return null;
    }

    
    //This methods gets the name of the next node
    //example: <task-node name="textDisplay~You get a full rest.~I choose to go to sleep.">
    //becomes "textDisplay~You get a full rest."
    //"textDisplay~You get a full rest." is the name of the next node
    //if the item in the Radio Button list 
    //with the text "I choose to go to sleep" is ticked
    public static String getLinkFromRadioButton(String itemString) {

    	StringBuffer sb = new StringBuffer("");
		StringTokenizer st = new StringTokenizer(itemString, "~");
    
    	int totalTokens = st.countTokens();
		int counter = 0;		
//		String myStringToken = "";
		
		while (counter<totalTokens-2) { //up to third to the last only			
			sb = sb.append(st.nextToken()+"~");				
			counter++;
		}
	  	sb = sb.append(st.nextToken()); 
		
	  	
	  	Log.d(">>>>>>>>>>>>>>>> getLinkFromRadioButton", sb.toString());
	  	return sb.toString();
    }

    
    //Reference: AbakadaUtils.java; public static ArrayList<String> getWords(String filePath)
	public static ArrayList<String> getTextInput(String filePath)
	{
		List<String> ret = new ArrayList<String>();
//		StringBuffer myReturnValue = new StringBuffer();
		
		try 
		{  	
			File file = new File(filePath);
			if(!file.exists())
			{
				System.out.println(">>>>>> File " + filePath + " doesn't exist. Creating file.");
				file.createNewFile();
			}
			
    		FileInputStream fis = new FileInputStream(filePath);
    		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
    		
        	String currLineString;
        	while((currLineString=br.readLine())!=null)
        	{ 		
        		ret.add(currLineString);
        	}	        	
        	fis.close();        	        	
    	}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE.");
    		e.printStackTrace();
    	}
		
		return (ArrayList<String>) ret;
	}
    
	public static PrintWriter getFileFromSDCardAsWriter(String filePath) {
		try {
			File file = new File(filePath);
			if(!file.exists())
			{
				System.out.println(">>>>>> File " + filePath + " doesn't exist. Creating file.");
				file.createNewFile();
			}
			
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file,false)));
			return out;
		}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE.");
    		e.printStackTrace();
    	}		
    	return null;
	}

	public static InputStream getFileFromSDCardAsInputStream(String filePath) {
		try 
		{  	
			File file = new File(filePath);
			if(!file.exists())
			{
				System.out.println(">>>>>> File " + filePath + " doesn't exist."); //Creating file.
				return null;
			}
						
		      InputStream in = null;
		      try {
		          in = new BufferedInputStream(new FileInputStream(file));
		      }  
		      catch(Exception e) {
		    	  e.printStackTrace();
		      }
		      
		      return in;
		}		
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE.");
    		e.printStackTrace();
    	}		
    	return null;
	}
	
	//Reference: AbakadaUtils.java; public static ArrayList<String> getWords(String filePath)
	public static InputStreamReader getFileFromSDCardAsReader(String filePath) //example of file would be decision trees
	{
		try 
		{  	
		      InputStreamReader reader = new InputStreamReader(getFileFromSDCardAsInputStream(filePath),"UTF-8"); 
        	  return reader;
    	}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE.");
    		e.printStackTrace();
    	}		
    	return null;
	}

	public static void setmyTreeFileName(String myTree) {
		myTreeFileName = myTree;
	}

	public static void clearTempFolder() {
		try 
		{  	
			//first create temp folder
			File file = new File(USBONG_TREES_FILE_PATH+"temp/");
	    	if (file.exists()) {
//	        	file.delete();    		
	    		deleteRecursive(file);//do this to delete contents of the directory
	    	}
	    	file.mkdirs();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Reference: AbakadaUtils.java; public static ArrayList<String> getWords(String filePath)
	public static InputStreamReader getTreeFromSDCardAsReader(String treeFile) //example of file would be decision trees
	{
		setmyTreeFileName(treeFile);
		
		is_a_utree=false; //this variable is not yet used anywhere, Mike, Sept 2, 2012
		try 
		{  	
			//first create temp folder
/*			File file = new File(USBONG_TREES_FILE_PATH+"temp/"); 
			if (file.exists()) {
//	        	file.delete();    		
	    		deleteRecursive(file);//do this to delete contents of the directory
	    	}
	    	file.mkdirs();
*/
			//test if it's a .xml
			File file = new File(UsbongUtils.USBONG_TREES_FILE_PATH + treeFile+".xml");

			if(!file.exists())
			{				
				System.out.println(">>>>>> File " + treeFile + ".xml" + " doesn't exist."); 
//				return null; //don't do this anymore, so that the next check will be performed.
				
				//check if it's a .utree
				String filePath = UsbongUtils.USBONG_TREES_FILE_PATH + treeFile+".utree";
				file = new File(filePath);
				if(!file.exists())
				{
					System.out.println(">>>>>> File " + treeFile + ".utree" + " doesn't exist."); 
					return null;
				}
				else {
					is_a_utree=true;
					//check if it needs to be unzipped
					file = new File(filePath+"/"+treeFile+".xml");
					if(!file.exists())
					{					
						file = new File(UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+treeFile+".utree/"+treeFile+".xml");

						if(!file.exists()) {						
							File f = new File(UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+ treeFile+".utree/");
				            f.mkdirs();
							
							//then unzip file
							unzip(filePath, UsbongUtils.USBONG_TREES_FILE_PATH+"temp/");//+treeFile+".utree/");
						}
						/*
						ArrayList<ZipEntry> myList = unZipFile(filePath);

						file = new File(filePath+"_temp");
				    	if (file.exists()) {
				        	file.delete();    		
				    	}
				    	file.mkdirs();
				    	for (ZipEntry entry : myList) {
				    		entry.
				    	}
*/						
					}
					
				}
						
/*			
				//check if it's a .utree
				file = new File(UsbongUtils.USBONG_TREES_FILE_PATH + treeFile+".utree/"+treeFile+".xml");
				if(!file.exists())
				{
					System.out.println(">>>>>> File " + treeFile + ".utree" + " doesn't exist."); 
					return null;
				}
				else {
					is_a_utree=true;
				}
*/				
			}
			
		      InputStream in = null;
		      InputStreamReader reader;
		      try {
		          in = new BufferedInputStream(new FileInputStream(file));
		      }  
		      catch(Exception e) {
		    	  e.printStackTrace();
		      }
		      reader = new InputStreamReader(in,"UTF-8"); 
        	  return reader;
    	}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE.");
    		e.printStackTrace();
    	}		
    	return null;
	}

	//Reference: AbakadaUtils.java; public static ArrayList<String> addWords(String filePath)
	public static boolean storeOutputInSDCard(String filePath, String output)
	{
		boolean ret = false;
		
		try
		{
//	    	UsbongUtils.createNewOutputFolderStructure();
	
			File file = new File(filePath);
			if(!file.exists())
			{
				System.out.println(">>>>>> File " + filePath + " doesn't exist. Creating file.");
				file.createNewFile();
			}
			
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file,false)));// new BufferedWriter(new FileWriter(filePath, false)));
		    out.println(output);
		    
		    out.close();
			ret = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return ret;
	}
	
	public static ArrayList<String> getTreeArrayList(String filePath)
	{
		List<String> ret = new ArrayList<String>();
		
		try 
		{  	
			File file = new File(filePath);
			if(!file.exists())
			{
				System.out.println(">>>>>> File " + filePath + " doesn't exist. Creating file.");
				//file.createNewFile();
				file.mkdirs();
			}
						
			UsbongFileFilter myFileFilter = new UsbongFileFilter("xml");			
			String[] listOfTrees = file.list(myFileFilter); //file.list();
			int totalTrees = listOfTrees.length;
			
			for(int i=0; i<totalTrees; i++) {
				ret.add(listOfTrees[i].replace(".xml", "")); //remove the ".xml" at the end
			}
			
			UsbongFileFilter myFolderFilter = new UsbongFileFilter(".utree");			
			String[] listOfFolderTrees = file.list(myFolderFilter); 
			int totalFolderTrees = listOfFolderTrees.length;
			
			for(int i=0; i<totalFolderTrees; i++) {
				ret.add(listOfFolderTrees[i].replace(".utree", "")); //remove the ".xml" at the end
			}

    	}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE.");
    		e.printStackTrace();
    	}
		
		return (ArrayList<String>) ret;
	}

	public static ArrayList<String> getAvailableTranslationsArrayList(String treeFile)
	{
		List<String> ret = new ArrayList<String>();
				
		String filePath = UsbongUtils.USBONG_TREES_FILE_PATH + treeFile+".utree/trans/";
		File file = new File(filePath);
		if(!file.exists())
		{
			file = new File(UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+treeFile+".utree/trans/");

			if(!file.exists()) {						
				return null;
			}
		}
		//if this point is reached, this means that the .utree has a 'trans' folder
		Log.d(">>>>","reached this point; has trans folder");

		try 
		{  							
			UsbongFileFilter myFileFilter = new UsbongFileFilter("xml");			
			String[] listOfTrans = file.list(myFileFilter); //file.list();
			int totalTrans = listOfTrans.length;
			
			for(int i=0; i<totalTrans; i++) {
				ret.add(listOfTrans[i].replace(".xml", "")); //remove the ".xml" at the end
				Log.d(">>>>>>listOfTrans[i]:",listOfTrans[i]);
			}			
    	}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE.");
    		e.printStackTrace();
    	}
		
		return (ArrayList<String>) ret;
	}

    //converts the Filipino Text to Spanish Accent-friendly text
	//based on the following rules
	//Reference: http://answers.oreilly.com/topic/217-how-to-match-whole-words-with-a-regular-expression/; last accessed 27 Sept 2011
	//Also, take note that in Android, you must add an extra escape character (e.g. \b becomes \\b)
    public static String convertFilipinoToSpanishAccentFriendlyText(String text) {    	
    	text = text.replaceAll("h", "j");
    	text = text.replaceAll("H", "J");
    	
		//added by Mike, May 30, 2013
    	//if the last character ends with "ng" change it to "g"
        String myStringToken="";
        StringBuffer sb= new StringBuffer("");
                
        String punctuation="";
        
    	StringTokenizer st = new StringTokenizer(text, " ");
		while ((st != null) && (st.hasMoreTokens())) {
    		myStringToken = st.nextToken();

        	if (myStringToken.endsWith(".")) {
        		myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		punctuation=".";
        	}
        	else if (myStringToken.endsWith(",")) {
        		myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		punctuation=",";
        	}
        	else if (myStringToken.endsWith("?")) {
        		myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		punctuation="?";
        	}
        	else if (myStringToken.endsWith("!")) {
        		myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		punctuation="!";
        	}
        	
        	if (myStringToken.equals("ng")){
        		sb.append("nang");
        	}
        	else if (myStringToken.equals("Ng")){
        		sb.append("Nang");
        	}
        	else if (myStringToken.contains("gj")){
        		myStringToken = myStringToken.replaceAll("gj", "gh");
        		sb.append(myStringToken);
        	}
        	else if (myStringToken.contains("ng-")){
        		myStringToken = myStringToken.replaceAll("ng-", "n-");
        		sb.append(myStringToken);
        	}
        	else if (myStringToken.endsWith("ng")){
    			myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		sb.append(myStringToken);
    		}
    		else if (myStringToken.endsWith("NG")){
    			myStringToken = myStringToken.substring(0, myStringToken.length()-1);
        		sb.append(myStringToken);
    		}    	
    		else {
    			sb.append(myStringToken);
    		}
        	sb.append(punctuation+" ");
		}
    	text = sb.toString();
    				    	
//		text = text.toLowerCase(); //has problems when text input has symbols ?
    	text = text.replaceAll("\\bANG\\b", "ang");

    	text = text.replaceAll("\\bmga\\b", "manga");
    	text = text.replaceAll("\\bMga\\b", "Manga");

    	text = text.replaceAll("gi", "ghi");		
		text = text.replaceAll("Gi", "Ghi");		

		return text;
    }    
    
    public static int getLanguageID(String s) {
    	if (s!=null) {    		
	    	if (s.equals("Filipino")) {
	    		return LANGUAGE_FILIPINO;
	    	}
	    	else if (s.equals("Japanese")) {
	    		return LANGUAGE_JAPANESE;
	    	}
	    	else if (s.equals("Mandarin")) {
	    		return LANGUAGE_MANDARIN;
	    	}
	    	else if (s.equals("Bisaya")) {
	    		return LANGUAGE_BISAYA;
	    	}
	    	else if (s.equals("Ilonggo")) {
	    		return LANGUAGE_ILONGGO;
	    	}
	    	else if (s.equals("Kapampangan")) {
	    		return LANGUAGE_KAPAMPANGAN;
	    	}
	    	else if (s.equals("French")) {
	    		return LANGUAGE_FRENCH;
	    	}
	    	else if (s.equals("Spanish")) {
	    		return LANGUAGE_SPANISH;
	    	}
	    	else if (s.equals("Korean")) {
	    		return LANGUAGE_KOREAN;
	    	}
    	}
    	return LANGUAGE_ENGLISH;
    }
    
    public static String getLanguageBasedOnID(int currLanguageBeingUsed) {
    	switch (currLanguageBeingUsed) {
    		case LANGUAGE_FILIPINO:
    			return "Filipino";
    		case LANGUAGE_JAPANESE:
    			return "Japanese";
    		case LANGUAGE_MANDARIN:
    			return "Mandarin";
    		case LANGUAGE_BISAYA:
    			return "Bisaya";
    		case LANGUAGE_ILONGGO:
    			return "Ilonggo";
    		case LANGUAGE_KAPAMPANGAN:
    			return "Kapampangan";
    		case LANGUAGE_FRENCH:
    			return "French";
    		case LANGUAGE_SPANISH:
    			return "Spanish";
    		case LANGUAGE_KOREAN:
    			return "Korean";
    		default:
    			return "English";
    	}
    }

    public static String getDefaultLanguage() {
    	return usbongDefaultLanguage;
    }
    
    public static String getSetLanguage() {
    	//edited by Mike, 20160618
//    	return usbongSetLanguage;
    	return currLanguage;    		
    }

    //added by Mike, 20160608
    public static String getDefaultLanguageOfXML() {
    	return usbongDefaultLanguageOfXML;
    }

    public static void setDefaultLanguage(String s) {
    	usbongDefaultLanguage = s;
    	
    	//edited by Mike, 20160618
    	//usbongSetLanguage = usbongDefaultLanguage;
    	currLanguage = usbongDefaultLanguage;
    }
    
    public static void setLanguage(String s) {
    	//edited by Mike, 20160618
    	//usbongSetLanguage = s;
    	currLanguage = s;
    }

    //added by Mike, 20160608
    public static void setDefaultLanguageOfXML(String s) {
    	usbongDefaultLanguageOfXML = s;
    }

    public static Intent performSendToCloudBasedServiceProcess(String filepath, List<String> filePathsList) {
//		final Intent sendToCloudBasedServiceIntent = new Intent(android.content.Intent.ACTION_SEND);
    	final Intent sendToCloudBasedServiceIntent;
    	if (filePathsList!=null) {
    		sendToCloudBasedServiceIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
    	}
    	else {
    		sendToCloudBasedServiceIntent = new Intent(android.content.Intent.ACTION_SEND);
    	}
    	try {
			InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(filepath);
			BufferedReader br = new BufferedReader(reader);    		
	    	String currLineString;        	
			currLineString=br.readLine();
			System.out.println(">>>>>>>>>> currLineString: "+currLineString);
			
			//Reference: http://blog.iangclifton.com/2010/05/17/sending-html-email-with-android-intent/
			//last acccessed: 17 Jan. 2012
			sendToCloudBasedServiceIntent.setType("text/plain");
			
			sendToCloudBasedServiceIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "usbong;"+UsbongUtils.getDateTimeStamp());
			sendToCloudBasedServiceIntent.putExtra(android.content.Intent.EXTRA_TEXT, currLineString); //body
//			sendToCloudBasedServiceIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"masarapmabuhay@gmail.com"});//"masarapmabuhay@gmail.com"); 	
			
			
			//only add path if it's not already in filePathsLists (i.e. attachmentFilePaths)
			if (!filePathsList.contains(filepath)) {
				filePathsList.add(filepath);
			}
			
			//Reference: http://stackoverflow.com/questions/2264622/android-multiple-email-attachments-using-intent
			//last accessed: 14 March 2012
			//has to be an ArrayList
		    ArrayList<Uri> uris = new ArrayList<Uri>();
		    //convert from paths to Android friendly Parcelable Uri's
		    for (String file : filePathsList)
		    {
		        File fileIn = new File(file);		        
		        if (fileIn.exists()) { //added by Mike, May 13, 2012		        		        
			        Uri u = Uri.fromFile(fileIn);
			        uris.add(u);
			        System.out.println(">>>>>>>>>>>>>>>>>> u: "+u);
		        }
		    }
		    sendToCloudBasedServiceIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		    
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return sendToCloudBasedServiceIntent;
    }
    
    public static void setDestinationServerURLFromConfigFile() {
    	if (UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config") != null) { 
			InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config");
			BufferedReader br = new BufferedReader(reader);    		
        	String currLineString;      
        	
        	try {
	        	while((currLineString=br.readLine())!=null)
	        	{ 		
	        		if (currLineString.contains("DESTINATION_URL=")) {
	    				UsbongUtils.setDestinationServerURL(currLineString.replace("DESTINATION_URL=", ""));
	    				System.out.println(">>>>>>>DestiantionServerURL: "+UsbongUtils.getDestinationServerURL());
	    			}
	        	}	        				
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        	}
		}
    }
    
    public static void performFileUpload(String filepath) {
    		try {
				InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(filepath);
				BufferedReader br = new BufferedReader(reader);    		
		    	String currLineString;        	
				currLineString=br.readLine();
				System.out.println(">>>>>>>>>> currLineString: "+currLineString);
        			
        	//Comment out this for now, later it should be possible to have several rows of entries in one CSV file
/*
        	while((currLineString=br.readLine())!=null)
        	{ 		
        	}
*/
//				if (getDestinationServerURL()==null) {
					setDestinationServerURLFromConfigFile();
//				}
				
				HttpClient client = new DefaultHttpClient();
		    	    	  
    	  //added by Mike, 10 Dec. 2011
//    	  HttpPost httppost = new HttpPost("http://192.168.1.105/"); 
//		  destinationServerURL="192.168.1.104";

		  if (!getDestinationServerURL().startsWith("http://") || !getDestinationServerURL().startsWith("https://")) {
			  setDestinationServerURL("http://"+getDestinationServerURL());
		  }
		  if (!getDestinationServerURL().endsWith("/")) {
			  setDestinationServerURL(destinationServerURL+"/");			  
		  }
		  
//		  HttpPost httppost = new HttpPost("http://"+destinationServerURL+"/");
		  HttpPost httppost = new HttpPost(destinationServerURL);
				  
		  System.out.println(">>>>>>inside performFileUpload;destinationServerURL: "+destinationServerURL);
//		  HttpPost httppost = new HttpPost(destinationServerURL);
				  
    	  //Reference: http://stackoverflow.com/questions/3288823/how-to-add-parameters-in-android-http-post
    	  //last accessed: 11 Dec. 2011
    	  //Add your data  
    	  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
    	  nameValuePairs.add(new BasicNameValuePair("usbong_output", currLineString));  
    	  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
    	  
    	  	//Instantiate a Post HTTP method
	        HttpResponse response=client.execute(httppost);//new HttpPost("http://192.168.1.104/"));
	        InputStream is=response.getEntity().getContent();
	        //Reference: http://stackoverflow.com/questions/2323617/android-httppost-how-to-get-the-result
	        //last accessed: 10 Dec. 2011
	        StringBuffer sb = new StringBuffer();
	        String reply;
	        try {
	            int chr;
	            while ((chr = is.read()) != -1) {
	                sb.append((char) chr);
	            }
	            reply = sb.toString();
	        } finally {
	            is.close();
	        }
	        System.out.println(">>>>> HttpPost reply: "+reply);
    	        //You can convert inputstream to a string with: http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
    	} catch (ClientProtocolException e) {
    	        // TODO Auto-generated catch block
    	        e.printStackTrace();
    	} catch (IOException e) {
    	        // TODO Auto-generated catch block
    	        e.printStackTrace();
    	}
    }    

    public static String getPathOfVideoFile(String myTree, String resFileName) {
    	String path = UsbongUtils.USBONG_TREES_FILE_PATH+myTree+".utree/res/";
    	File file = new File(path);
    	if (!file.exists()) {
    		path = UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+myTree+".utree/res/"+resFileName;
    	}
    	else {
        	path = UsbongUtils.USBONG_TREES_FILE_PATH+myTree+".utree/res/"+resFileName;
    	}
    	
    	File videoFile = new File(path);    	
    	
    	if (videoFile.exists()) {    	
    		return path;		    		
    	}
    	return "null";
//		return imageFile;
    }
    
    public static String getPathOfImageFile(String myTree, String resFileName) {
    	String path = UsbongUtils.USBONG_TREES_FILE_PATH+myTree+".utree/res/";
    	File file = new File(path);
    	if (!file.exists()) {
    		path = UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+myTree+".utree/res/"+resFileName;
    	}
    	else {
        	path = UsbongUtils.USBONG_TREES_FILE_PATH+myTree+".utree/res/"+resFileName;
    	}
 
    	Log.d(">>>>>path", path);
    	File imageFile = new File(path+".png");

		if(!imageFile.exists()) {
			imageFile = new File(path+".jpg");

			//added by Mike, 19 July 2015
			if(!imageFile.exists()) {
				imageFile = new File(path+".jpeg");
				path = path+".jpeg";					
			}
			else {				
				path = path+".jpg";					
		    	Log.d(">>>>>path .jpg", path);
			}						
		}
		else {
			path = path+".png";
		}

    	if (imageFile.exists()) {    		
        	Log.d(">>>>>imageFile.exists!", "exists!");

    		return path;		    		
    	}
    	return "null";
//		return imageFile;
    }
    
    //supports .png, .jpg and .jpeg
    public static boolean setImageDisplay(ImageView myImageView, String myTree, String resFileName) {
    	/*
    	File file = new File(path);
    	if (!file.exists()) {
    		path = UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+myTree+".utree/res/"+resFileName;
    	}
    	else {
        	path = UsbongUtils.USBONG_TREES_FILE_PATH+myTree+".utree/res/"+resFileName;
    	}
    	
    	File imageFile = new File(path+".png");

		if(!imageFile.exists()) {
			imageFile = new File(path+".jpg");
			path = path+".jgp";					
		}
		else {
			path = path+".png";
		}
*/	
    	String path = getPathOfImageFile(myTree, resFileName);
        if(!path.equals("null"))
        {
        	System.out.println(">>>>>>>>>>>>>>>>>> INSIDE!!!");                	
        	Bitmap myBitmap = BitmapFactory.decodeFile(path);
        	if(myBitmap != null)
        	{
        		myImageView.setImageBitmap(myBitmap);
        	}
        	else {
        		return false;
        	}
        	//Read more: http://www.brighthub.com/mobile/google-android/articles/64048.aspx#ixzz0yXLCazcU                	  
        	return true; //success!
        }
        return false; //not successful!
    }

    public static boolean setClickableImageDisplay(ImageButton myImageButton, String myTree, String resFileName) {
    	String path = getPathOfImageFile(myTree, resFileName);
        if(!path.equals("null"))
        {
        	System.out.println(">>>>>>>>>>>>>>>>>> INSIDE!!!");                	
        	Bitmap myBitmap = BitmapFactory.decodeFile(path);
        	if(myBitmap != null)
        	{
        		//Reference: CommonsWare and Doomsknight's answer from:
        		//http://stackoverflow.com/questions/13103484/how-can-i-set-a-bitmap-on-button;
        		//last accessed: 4 Feb 2013
        		myImageButton.setImageBitmap(myBitmap);
        	}
        	else {
        		return false;
        	}
        	//Read more: http://www.brighthub.com/mobile/google-android/articles/64048.aspx#ixzz0yXLCazcU                	  
        	return true; //success!
        }
        return false; //not successful!
    }

    
    //supports .png and .jpg
    public static boolean setBackgroundImage(View myLayout, String myTree, String resFileName) {
    	String path = getPathOfImageFile(myTree, resFileName);
        if(!path.equals("null"))
        {
        	System.out.println(">>>>>>>>>>>>>>>>>> INSIDE!!!");                	
        	        	
        	Bitmap myBitmap = BitmapFactory.decodeFile(path);
        	if(myBitmap != null)
        	{
        		//Answer from Deimos, stackoverflow
        		//Reference: http://stackoverflow.com/questions/7646766/set-linear-layout-background-dynamically;
        		//last accessed: 15 Sept 2012
            	BitmapDrawable background = new BitmapDrawable(myBitmap);        		
            	myLayout.setBackgroundDrawable(background);
//        		myImageView.setImageBitmap(myBitmap);
        	}
        	else {
        		return false;
        	}
        	//Read more: http://www.brighthub.com/mobile/google-android/articles/64048.aspx#ixzz0yXLCazcU                	  
        	return true; //success!
        }
        return false; //not successful!
    }

    
    /*
    //References: qrtt1 from Stackoverflow
    //http://stackoverflow.com/questions/2974798/unzip-file-from-zip-archive-of-multiple-files-on-android-using-zipfile-class;
    //last accessed: 3 Sept 2012
    public static ArrayList<ZipEntry> unZipFile(String zipFilePath) {
    	ZipInputStream in = null;
    	ArrayList<ZipEntry> ret = new ArrayList<ZipEntry>();
    	
    	try {    	    
    	    in = new ZipInputStream(getFileFromSDCardAsInputStream(zipFilePath));
    	    for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {
    	        // handle the zip entry
    	    	System.out.println(">>>>>>>>>>>>>> inside unZipFile");
    	    	System.out.println(">>>>>>>>>>>>>> entry: "+entry);
    	    	ret.add(entry);
    	    }
    	} catch (IOException e) {
    	    Log.e("unZipFile", e.getMessage());
    	} finally {
    	    try {
    	        if (in != null) {
    	            in.close();
    	        }
    	    } catch (IOException ignored) {
    	    }
    	    in = null;
    	}
    	
    	return ret;
    }
*/    
    //from Ben, stackoverflow
    //Reference: http://stackoverflow.com/questions/7485114/how-to-zip-and-unzip-the-files;
    //last accessed: 3 Sept 2012
    public static void unzip(String zipFile, String location) throws IOException {
    	final int BUFFER_SIZE = 8192;//1024; //65536;
    	int size;
        byte[] buffer = new byte[BUFFER_SIZE];
        
        ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));

        try {
        	Log.d(">>>>>>>","1");
            File f = new File(location);
            if (!f.exists()) {
	            if(!f.isDirectory()) {
	                f.mkdirs();
	            	Log.d(">>>>>>>","1.5: f.mkdirs()");
	            }
            }
        	Log.d(">>>>>>>","2");

//            zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            try {
                ZipEntry ze = null;                
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                	Log.d(">>>>>>>","3");
                	Log.d(">>>>>>>","location: "+location);
                	Log.d(">>>>>>>","ze.getName(): "+ze.getName());

                    if (ze.isDirectory()) {
                    	Log.d(">>>>>>>","4.1");

                    	File unzipFile = new File(path);
                    	Log.d(">>>>>>>","5.1");
                    	Log.d(">>>>>>>","path: "+path);

                    	if(!unzipFile.isDirectory()) {
                        	Log.d(">>>>>>>","6.1");
                    		unzipFile.mkdirs();
                        	Log.d(">>>>>>>","7.1");
                        }
                    }
                    else {
                    	Log.d(">>>>>>>","4.2");
                    	File file = new File(path);
                    	file.createNewFile();
                    	
                    	FileOutputStream out = new FileOutputStream(path, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
                            	Log.d(">>>>>>>","4.3");
                            	fout.write(buffer, 0, size);
                            }
                        	Log.d(">>>>>>>","4.4");
                            zin.closeEntry();
                        }
                        finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Unzip exception (Inner Exception)", e);            	
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Unzip exception (Outer Exception)", e);
        }
        finally {
    		zin.close();
        }
    }
    
    //From teedyay, stackoverflow
    //Reference: http://stackoverflow.com/questions/4943629/android-how-to-delete-a-whole-folder-and-content;
    //last accessed: 16 Sept. 2012
    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        //From InformaticOre, stackoverflow
        //Reference: http://stackoverflow.com/questions/11539657/open-failed-ebusy-device-or-resource-busy;
        //last accessed: 10 Sept. 2014
        File to = new File(fileOrDirectory.getAbsolutePath()+System.currentTimeMillis());
        fileOrDirectory.renameTo(to);
        to.delete();
    }

    public static void deleteEmptyOutputFolder(File fileOrDirectory) {
//        Log.d(">>>>deleteEmptyOutputFolder", "1");
    	if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles()) {
//                Log.d(">>>>deleteEmptyOutputFolder", "2");                
                return;
            }
//        Log.d(">>>>deleteEmptyOutputFolder", "3");
        fileOrDirectory.delete();
//        Log.d(">>>>deleteEmptyOutputFolder", "4");        
    }

    //added by Mike, March 26, 2014
    //Reference: http://stackoverflow.com/questions/17145990/how-to-get-substring-based-on-special-characters-in-android;
    //last accessed: March 26, 2014
    public static String processLoadTagsInString(Activity a, String myText) {
		while (myText.contains("<@")) {
			Log.d(">>>>", myText);
			Matcher m = Pattern.compile("<@(.+?)>").matcher(myText);
			String v="";
			while(m.find()) {
			    v = m. group(1);
			    Log.d(">>>> v",v);
				try {
					myText=myText.replaceAll("<@"+v+">", ((UsbongDecisionTreeEngineActivity)a).getVariableFromMyUsbongVariableMemory("@"+v));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}    		
		}
		return myText;
    }    
    
    //added by Mike, Feb. 4, 2013
    //modified by Mike, Oct. 5, 2014
    public static String/*Spanned*/ applyTagsInString(Activity a, String myCurrUsbongNode) {
    	String styledText;

    	if (USE_UNESCAPE) {
//    		Log.d(">>>>>","UNESCAPE=true");
//        	styledText = StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(myCurrUsbongNode));
        	//added by Mike, Feb. 9, 2015
    		styledText = (UsbongUtils.trimUsbongNodeName(myCurrUsbongNode)).replace("\n", "{br}");        	  
        }
        else {
//    		Log.d(">>>>>","UNESCAPE=false");
        	styledText = UsbongUtils.trimUsbongNodeName(myCurrUsbongNode);
        }

//    	Log.d(">>>styledText 1",styledText);
    	styledText = performTranslation(styledText);

//    	Log.d(">>>styledText 2",styledText);
    	styledText = replaceAllCurlyBracesWithGreaterThanLessThanSign(styledText);

//    	Log.d(">>>styledText 3",styledText);

    	//added by Mike, March 26, 2014
    	processStoreVariableMethod(a, myCurrUsbongNode); //does not return anything

    	//added by Mike, March 26, 2014
    	styledText = processLoadTagsInString(a, styledText);

    	//keep the curly braces for <indent>
//    	styledText = styledText.replaceAll("<indent>", "{indent}");//"\u0020\u0020\u0020\u0020\u0020");					
    	styledText = processIndent(styledText);

//    	styledText = applyHintsInString(a, styledText);    	
//    	Log.d(">>>>>styledText",styledText);
/*    	
    	Spanned mySpanned = Html.fromHtml(styledText);	
    	Log.d(">>>>>mySpanned",mySpanned.toString());
    	
    	return mySpanned;
*/
    	return styledText; //do "Html.fromHtml(styledText);" later
    }

	//added by Mike, Sept. 27, 2012
	//answer from Chistopher, stackoverflow
	//Reference: http://stackoverflow.com/questions/2730706/highlighting-text-color-using-html-fromhtml-in-android;
	//last accessed: 19 Sept. 2012
    public static View applyTagsInView(Activity a, View myView, int type, String myCurrUsbongNode) {
//    	String styledText = applyTagsInString(myCurrUsbongNode);
//    	Spanned mySpanned = Html.fromHtml(styledText);
//    	Spanned mySpanned = applyTagsInString(a, myCurrUsbongNode);    	    	
    	String styledText = applyTagsInString(a, myCurrUsbongNode);    	    	

		switch(type) {
			case IS_RADIOBUTTON:
				//modified by Mike, Oct. 5, 2014
				((RadioButton)myView).setText(styledText);
				myView = (RadioButton) UsbongUtils.applyHintsInView(UsbongDecisionTreeEngineActivity.getInstance(), (RadioButton)myView, UsbongUtils.IS_RADIOBUTTON);
				makeLinksFocusable(((RadioButton)myView), IS_RADIOBUTTON);
				break;
			case IS_CHECKBOX:
				//modified by Mike, Oct. 5, 2014
				((CheckBox)myView).setText(styledText);
				myView = (CheckBox) UsbongUtils.applyHintsInView(UsbongDecisionTreeEngineActivity.getInstance(), (CheckBox)myView, UsbongUtils.IS_CHECKBOX);
/*
		    	mySpanned = applyTagsInString(a, ((CheckBox)myView).getText().toString());
				((CheckBox)myView).setText(mySpanned, TextView.BufferType.SPANNABLE);
				((CheckBox)myView).setMovementMethod(LinkMovementMethod.getInstance());
//				((CheckBox)myView).setTextSize((a.getResources().getDimension(R.dimen.textsize)));
*/
				makeLinksFocusable(((CheckBox)myView), IS_CHECKBOX);
				break;				
			default: //case IS_TEXTVIEW:
				//modified by Mike, Oct. 5, 2014
				((TextView)myView).setText(styledText);
				Log.d(">>>> applyTagsInView",((TextView)myView).getText().toString());
				myView = (TextView) UsbongUtils.applyHintsInView(UsbongDecisionTreeEngineActivity.getInstance(), (TextView)myView, UsbongUtils.IS_TEXTVIEW);
				makeLinksFocusable(((TextView)myView), IS_TEXTVIEW);
				break;
		}

		return myView;
    }
    
    //edited by Mike, 20160413
    public static View applyHintsInView(Activity a, View myView, int type) {    	    	
    	Log.d(">>>>", "getCurrLanguage(): "+getCurrLanguage());

    	if((myHashtableOfWordHints==null) || (!myHashtableOfWordHints.containsKey(getCurrLanguage()))) {						
		    switch(type) {
				case IS_RADIOBUTTON:
					((RadioButton)myView).setText(Html.fromHtml((((RadioButton)myView).getText().toString())));					
					return myView;
				case IS_CHECKBOX:
					((CheckBox)myView).setText(Html.fromHtml((((CheckBox)myView).getText().toString())));					
					return myView;
			    default: //case IS_TEXTVIEW:
					((TextView)myView).setText(Html.fromHtml(((TextView)myView).getText().toString()));					
					return myView;
		    }
		}
		//if this point is reached, this means that hint file exists
    	Hashtable<String,String> myHashtableForCurrLang = myHashtableOfWordHints.get(getCurrLanguage());
		
    	Log.d(">>>>", "found Hashtable");
    	
    	if (tokenizedStringList==null) {
    		tokenizedStringList = new ArrayList<String>();
    	}
    	else {
    		tokenizedStringList.clear();
    	}
    	Scanner sc; 
    	StringBuffer output = new StringBuffer();

    	//added by Mike, Oct. 3, 2014
//    	myView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    	
	    switch(type) {
			case IS_RADIOBUTTON:
				sc = new Scanner(((RadioButton)myView).getText().toString());
				((RadioButton)myView).setText("");
		    	break;
			case IS_CHECKBOX:
		    	sc = new Scanner(((CheckBox)myView).getText().toString());
				((CheckBox)myView).setText("");
				
		    	break;				
		    default: //case IS_TEXTVIEW:
		    	sc = new Scanner(((TextView)myView).getText().toString());
/*
		    	Spannable myScannable = ((Spannable)((TextView)myView).getText());
//				sc = new Scanner(Html.toHtml(myScannable));		    	
				sc = new Scanner(myScannable.toString());		    	
*/				
				((TextView)myView).setText("");
		    	break;
	    }

    	//Japanese sentences do not have clear delimiters, such as spaces. 
    	//They do have particles, but how does a computer know if it's a particle 
    	//or part of a phrase?
	    if (getSetLanguage()==getLanguageBasedOnID(LANGUAGE_JAPANESE)) {
//	    	if (sc.hasNext()) {
	    	while (sc.hasNext()) {
	    		ArrayList<String> temp = tokenizeJapaneseString(sc.next());
	    		
	    		for (int i=0; i<temp.size(); i++) {
	    			tokenizedStringList.add(temp.get(i)); //= tokenizeJapaneseString(sc.next());
	    		}
	    	}
	    }
	    else {
	    	//edited by Mike, Oct. 19, 2014
/*
	    	while (sc.hasNext()) {
	    		tokenizedStringList.add(sc.next()+" ");
	    	}		    
*/
	    	StringBuffer temp = new StringBuffer();
	    	while (sc.hasNext()) {
	    		temp.append(sc.next()+" ");

	    		Log.d(">>>temp: ",temp.toString());
	    		
	    		if (temp.toString().startsWith("<a")) {
	    			while (sc.hasNext()&&!temp.toString().trim().endsWith("</a>")) {
	    				temp.append(sc.next()+" ");
	    			}
	    		}	    		
	    		else if (temp.toString().startsWith("<small>")) {
	    			while (sc.hasNext()&&!temp.toString().trim().endsWith("</small>")) {
	    				temp.append(sc.next()+" ");
	    			}
	    		}	    		
	    		else if (temp.toString().startsWith("<big>")) {
	    			while (sc.hasNext()&&!temp.toString().trim().endsWith("</big>")) {
	    				temp.append(sc.next()+" ");
	    			}
	    		}	    		
	    		else if (temp.toString().startsWith("<font>")) {
	    			while (sc.hasNext()&&!temp.toString().trim().endsWith("</font>")) {
	    				temp.append(sc.next()+" ");
	    			}
	    		}	    		
	    		else if (temp.toString().startsWith("<b>")) {
//	    			Log.d(">>nasa loob","<b>");
	    			while (sc.hasNext()&&!temp.toString().trim().endsWith("</b>")) {
//	    			while (sc.hasNext()&&!temp.toString().trim().contains("</b>")) {
	    				temp.append(sc.next()+" ");
	    			}
	    		}	    		
	    		else if (temp.toString().startsWith("<i>")) {
	    			while (sc.hasNext()&&!temp.toString().trim().endsWith("</i>")) {
//	    			while (sc.hasNext()&&!temp.toString().trim().contains("</i>")) {
	    				temp.append(sc.next()+" ");
	    			}
	    		}	    		
	    		else if (temp.toString().startsWith("<u>")) {
	    			while (sc.hasNext()&&!temp.toString().trim().endsWith("</u>")) {
	    				temp.append(sc.next()+" ");
	    			}
	    		}	    		
	    		
//	    		tokenizedStringList.add(temp.toString()+" "); //commented out by Mike, 19 Sept. 2015
	    		tokenizedStringList.add(temp.toString());
	    		temp.delete(0, temp.length());//reset
	    	}		    	    	
	    }
    	
    	for(int i=0; i<tokenizedStringList.size(); i++) {				  
    		Log.d(">>",""+tokenizedStringList.get(i));    	
    	}

		try {
		  boolean foundMatch=false;
		  
		  for(int i=0; i<tokenizedStringList.size(); i++) {				  
			  foundMatch=false;
			  
			  Log.d(">>>>", "tokenizedString: "+tokenizedStringList.get(i).trim().toLowerCase().replaceAll("[^\\w\\s]", ""));
			  
			  String tokenizedString = tokenizedStringList.get(i).trim().toLowerCase().replaceAll("[^\\w\\s]", "");
			  
			  if (myHashtableForCurrLang.containsKey(tokenizedString)) {					  								  
				  final String hintText = myHashtableForCurrLang.get(tokenizedString);
				  final UsbongDecisionTreeEngineActivity finalUdtea = (UsbongDecisionTreeEngineActivity)a;
							  
			      SpannableString link = makeLinkSpan(tokenizedStringList.get(i), new View.OnClickListener() {          
			            @Override
			            public void onClick(View v) {
					    	new AlertDialog.Builder(finalUdtea).setTitle("Word Hint!")
		            		.setMessage(hintText)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
								@Override
								public void onClick(DialogInterface dialog, int which) {	            				
								}
							}).show();
			            }
			      });					
//							      Log.d(">>>","has match: "+tokenizedStringList.get(i));
			      foundMatch=true;

			      switch(type) {
					case IS_RADIOBUTTON:
						((RadioButton)myView).append(link);
						makeLinksFocusable(((RadioButton)myView), IS_RADIOBUTTON);
						continue;
					case IS_CHECKBOX:
						((CheckBox)myView).append(link);
					    makeLinksFocusable(((CheckBox)myView), IS_CHECKBOX); 
						continue;				
					default://case IS_TEXTVIEW:
						((TextView)myView).append(link);
					    makeLinksFocusable(((TextView)myView), IS_TEXTVIEW); 
						continue;
			      }							      							      
			  }
			  if (!foundMatch) {					  
			        Log.d(">>>","i: "+i+" "+tokenizedStringList.get(i));
			        output.append(tokenizedStringList.get(i));
			        Log.d(">>> type",""+type);
			        
		        	switch(type) {
						case IS_RADIOBUTTON:
							((RadioButton)myView).append(Html.fromHtml(tokenizedStringList.get(i)));
							//makeLinksFocusable(((RadioButton)myView), IS_RADIOBUTTON);
							continue;
						case IS_CHECKBOX:
							((CheckBox)myView).append(Html.fromHtml(tokenizedStringList.get(i)));
//							    makeLinksFocusable(((CheckBox)myView), IS_CHECKBOX); 
							continue;				
						default://case IS_TEXTVIEW:
//								Log.d(">>>>>myView before Html.fromHtml...",((TextView)myView).getText().toString());
							((TextView)myView).append(Html.fromHtml(tokenizedStringList.get(i)));

//								((TextView)myView).setText(Html.fromHtml(((TextView)myView).getText().toString()+tokenizedStringList.get(i)));
							
							Log.d(">>>>>myView",((TextView)myView).getText().toString());
//								((CheckBox)myView).setText(mySpanned, TextView.BufferType.SPANNABLE);
//								((TextView)myView).setMovementMethod(LinkMovementMethod.getInstance());
//							    makeLinksFocusable(((TextView)myView), IS_TEXTVIEW); 
							continue;
				   }							      						      
			  }
		  }	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		sc.close(); //added by Mike, 19 Sept. 2015
		return myView;
    }
    
    //added by Mike, Oct. 4, 2014
    public static ArrayList<String> tokenizeJapaneseString(String s) {
    	ArrayList<String> output = new ArrayList<String>();
    	StringBuffer kanjiCommonPhrase = new StringBuffer();
    	StringBuffer kanjiRarePhrase = new StringBuffer();
    	StringBuffer hiraganaPhrase = new StringBuffer();
    	StringBuffer katakanaPhrase = new StringBuffer();
    	StringBuffer japanesePunctuationPhrase = new StringBuffer();
    	StringBuffer fullWidthRomanCharAndHalfWidthKatakanaPhrase = new StringBuffer();
    	StringBuffer othersPhrase = new StringBuffer();    	

		Log.d(">>>>inside: tokenizeJapaneseString",""+s);
    	
    	int value;
    	//go through character by character
    	for(int i=0; i<s.length();) {    		    	
    		value =(int)s.charAt(i);     		
    		Log.d(">>>>",""+s.charAt(i));
  
    		//Reference: http://www.rikai.com/library/kanjitables/kanji_codes.unicode.shtml;
    		//last accessed: 4 Oct. 2014
    		//Reference: http://stackoverflow.com/questions/3826918/how-to-classify-japanese-characters-as-either-kanji-or-kana;
    		//last accessed: 4 Oct. 2014;
    		//answer by Jack, Sept. 30, 2010
        	//KANJI (COMMON)
    		if (value >= 0x4e00 && value <= 0x9faf) {
	    		for (; i<s.length() && value >= 0x4e00 && value <= 0x9faf;) { //while Kanji Char
	    			kanjiCommonPhrase.append(s.charAt(i));
	    			i++;
	    			if (i<s.length()) {
	    				value =(int)s.charAt(i);
	    			}
	    			else {
	    				break;
	    			}
	    		}
//				Log.d(">>>","kanjiPhrase: "+kanjiPhrase);
	    		if (!kanjiCommonPhrase.toString().equals("")) {
	    			output.add(kanjiCommonPhrase.toString());
	    			kanjiCommonPhrase.replace(0,kanjiCommonPhrase.length(),""); 
//	    			Log.d(">>>","here");
	    			continue;
	    		}
    		}
        	//KANJI (RARE)
    		if (value >= 0x3400 && value <= 0x4dbf) {
	    		for (; i<s.length() && value >= 0x3400 && value <= 0x4dbf;) {
	    			kanjiRarePhrase.append(s.charAt(i));
	    			i++;
	    			if (i<s.length()) {
	    				value =(int)s.charAt(i);
	    			}
	    			else {
	    				break;
	    			}
	    		}
//				Log.d(">>>","kanjiPhrase: "+kanjiPhrase);
	    		if (!kanjiRarePhrase.toString().equals("")) {
	    			output.add(kanjiRarePhrase.toString());
	    			kanjiRarePhrase.replace(0,kanjiRarePhrase.length(),""); 
//	    			Log.d(">>>","here");
	    			continue;
	    		}
    		}
    		else if (value >= 0x3040 && value <= 0x309f) {
        		//HIRAGANA
        		for (; i<s.length() && value >= 0x3040 && value <= 0x309f;) { 
        			hiraganaPhrase.append(s.charAt(i));
        			i++;
        			if (i<s.length()) {
        				value =(int)s.charAt(i);
        			}
        			else {
        				break;
        			}
        		}

        		if (!hiraganaPhrase.toString().equals("")) {
        			output.add(hiraganaPhrase.toString());
        			hiraganaPhrase.replace(0,hiraganaPhrase.length(),""); 
        			continue;
        		}    			
    		}
    		else if (value >= 0x30a0 && value <= 0x30ff) {
	    		//KATAKANA
	    		for (; i<s.length() && value >= 0x30a0 && value <= 0x30ff;) { 
	    			katakanaPhrase.append(s.charAt(i));
	    			i++;
	    			if (i<s.length()) {
	    				value =(int)s.charAt(i);
	    			}
	    			else {
	    				break;
	    			}
	    		}
	
	    		if (!katakanaPhrase.toString().equals("")) {
	    			output.add(katakanaPhrase.toString());
	    			katakanaPhrase.replace(0,katakanaPhrase.length(),""); 
	    			continue;
	    		}
    		}  
    		else if (value >= 0x3000 && value <= 0x303f) {
            	//JAPANESE PUNCTUATION
        		for (; i<s.length() && value >= 0x3000 && value <= 0x303f;) { 
        			japanesePunctuationPhrase.append(s.charAt(i));
        			i++;
        			if (i<s.length()) {
        				value =(int)s.charAt(i);
        			}
        			else {
        				break;
        			}
        		}

        		if (!japanesePunctuationPhrase.equals("")) {
        			output.add(japanesePunctuationPhrase.toString());
        			japanesePunctuationPhrase.replace(0,japanesePunctuationPhrase.length(),""); 
        			continue;
        		}    			
    		}
    		else if (value >= 0xff00 && value <= 0xffef) {
            	//FULL-WIDTH ROMAN CHARACTERS and HALF-WIDTH KATAKANA
        		for (;i<s.length() && value >= 0xff00 && value <= 0xffef;) { 
        			fullWidthRomanCharAndHalfWidthKatakanaPhrase.append(s.charAt(i));
        			i++;
        			if (i<s.length()) {
        				value =(int)s.charAt(i);
        			}
        			else {
        				break;
        			}
        		}

        		if (!fullWidthRomanCharAndHalfWidthKatakanaPhrase.toString().equals("")) {
        			output.add(fullWidthRomanCharAndHalfWidthKatakanaPhrase.toString());
        			fullWidthRomanCharAndHalfWidthKatakanaPhrase.replace(0,fullWidthRomanCharAndHalfWidthKatakanaPhrase.length(),""); 
        			continue;
        		}
    		}
    		else {
            	//IF NONE OF THE ABOVE
        		for (;i<s.length() && ((value < 0x3000 || value > 0xffef));) { 
        			othersPhrase.append(s.charAt(i));
        			i++;
        			if (i<s.length()) {
        				value =(int)s.charAt(i);
        			}
        			else {
        				break;
        			}
        		}

        		if (!othersPhrase.toString().equals("")) {
        			output.add(othersPhrase.toString()+" ");
        			othersPhrase.replace(0,othersPhrase.length(),""); 
        			continue;
        		}
    		}    		
    		i++; //do increment here    		
    	}
    	return output;
    }
    
    //added by Mike, Oct. 4, 2014
    public static ArrayList<String> extractKanjiPhrasesFromString(String s) {
    	ArrayList<String> output = new ArrayList<String>();
    	StringBuffer kanjiPhrase = new StringBuffer();
    	
    	//go through character by character
    	for(int i=0; i<s.length();i++) {    		
    		//Reference: http://www.rikai.com/library/kanjitables/kanji_codes.unicode.shtml;
    		//last accessed: 4 Oct. 2014
    		//Reference: http://stackoverflow.com/questions/3826918/how-to-classify-japanese-characters-as-either-kanji-or-kana;
    		//last accessed: 4 Oct. 2014;
    		//answer by Jack, Sept. 30, 2010
    		//while Kanji Char
    		for (int value =(int)s.charAt(i);value >= 0x4e00 && value <= 0x9faf;) { 
    			kanjiPhrase.append(s.charAt(i));
    			i++;
    			value =(int)s.charAt(i);
    		}

    		if (!kanjiPhrase.equals("")) {
    			output.add(kanjiPhrase.toString());
    			kanjiPhrase.replace(0,kanjiPhrase.length(),""); 
    		}
    	}
    	
    	return output;
    }
    
    //Reference: http://stackoverflow.com/questions/14135273/textview-onclick-android;
    //last accessed: 3 Oct. 2014
    //answer by: Naveen, Jan. 3, 2013; originally answered by: user370305, Jan. 3, 2013
    private static void makeLinksFocusable(View v, int type){//TextView tv) {    	
    	MovementMethod m;  
    	switch(type) {
			case IS_TEXTVIEW:
				m = ((TextView)v).getMovementMethod();  
		        if ((m == null) || !(m instanceof LinkMovementMethod)) {  
		            if (((TextView)v).getLinksClickable()) {  
		            	((TextView)v).setMovementMethod(LinkMovementMethod.getInstance());  
		            }  
		        }  
/*				
		    	Spanned mySpanned = Html.fromHtml(((TextView)v).getText().toString());	
				((TextView)v).setText(mySpanned, TextView.BufferType.SPANNABLE);
            	((TextView)v).setMovementMethod(LinkMovementMethod.getInstance());  
*/            	
		    	break;
			case IS_RADIOBUTTON:
		    	m = ((RadioButton)v).getMovementMethod();  
		        if ((m == null) || !(m instanceof LinkMovementMethod)) {  
		            if (((RadioButton)v).getLinksClickable()) {  
		            	((RadioButton)v).setMovementMethod(LinkMovementMethod.getInstance());  
		            }  
		        }  
		    	break;
			case IS_CHECKBOX:
		    	m = ((CheckBox)v).getMovementMethod();  
		        if ((m == null) || !(m instanceof LinkMovementMethod)) {  
		            if (((CheckBox)v).getLinksClickable()) {  
		            	((CheckBox)v).setMovementMethod(LinkMovementMethod.getInstance());  
		            }  
		        }  
		    	break;				
	      }
    }

    //Reference: http://stackoverflow.com/questions/14135273/textview-onclick-android;
    //last accessed: 3 Oct. 2014
    //answer by: Naveen, Jan. 3, 2013; originally answered by: user370305, Jan. 3, 2013
    private static SpannableString makeLinkSpan(CharSequence text, View.OnClickListener listener)                 {
        SpannableString link = new SpannableString(text);
        link.setSpan(new ClickableString(listener), 0, text.length(), 
            SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return link;
    }

    //Reference: http://stackoverflow.com/questions/14135273/textview-onclick-android;
    //last accessed: 3 Oct. 2014
    //answer by: Naveen, Jan. 3, 2013; originally answered by: user370305, Jan. 3, 2013
    private static class ClickableString extends ClickableSpan {  
        private View.OnClickListener mListener;          
//        private TextPaint textpaint; //added by Mike, Oct. 4, 2014
        public ClickableString(View.OnClickListener listener) {              
            mListener = listener;  
        }          
        @Override  
        public void onClick(View v) {  
            mListener.onClick(v);  
        }        
        
        //added by Mike, Oct. 4, 2014
        @Override
        public void updateDrawState(TextPaint ds) {
/*            
        	textpaint = ds;
            ds.setColor(ds.linkColor);

            textpaint.bgColor = Color.GRAY;         
            textpaint.setARGB(255, 255, 255, 255);

            //Remove default underline associated with spans
            ds.setUnderlineText(false);

//            ds.setColor(Color.BLACK);
            ds.setARGB(255,0,0,0);
            ds.setStyle(Style.STROKE);
//            ds.setStrokeCap(Cap.ROUND);
//            ds.setStrokeWidth(6);
            ds.setPathEffect(new DashPathEffect(new float[]{5,5},0));
            ds.setUnderlineText(true);
*/
        	//edited by Mike, 20160620
        	ds.setAlpha(171);//128);//make the hint's color lighter
        }        
    }  
    
	//added by Mike, Sept. 19, 2012
	public static String processIndent(String myText) {
		//answer from Thierry-Dimitri Roy, stackoverflow
		//Reference: http://stackoverflow.com/questions/3611635/java-regex-replaceall-does-not-replace-string;
		//--> must assign new value to myText
		//last accessed: 19 Sept. 2012
//		myText = myText.replaceAll("<indent>", "<font color=\'#f5f2f2\'>ttttt</font>");			
//		myText = myText.replaceAll("\\{indent\\}", "\u0020\u0020\u0020\u0020\u0020");					

		//answer from MylesCLin, linuxquestions
		//Reference: http://www.linuxquestions.org/questions/programming-9/html-how-do-i-insert-empty-spaces-287930/;
		//last accessed: 16 Oct. 2012
		myText = myText.replaceAll("<indent>", "&nbsp&nbsp&nbsp&nbsp&nbsp");					
		return myText;
	}
	
	//added by Mike, Oct. 16, 2012
	public static String replaceAllCurlyBracesWithGreaterThanLessThanSign(String myText) {		
		//use escape character, "\\", 
		//answer from dave, stackoverflow
		//Reference: http://stackoverflow.com/questions/3611560/java-regex-problem-replacing-a-string;
		//last accessed: 16 Oct. 2012
		myText = myText.replaceAll("\\{", "<");	
		myText = myText.replaceAll("\\}", ">");	
		
		return myText;
	}
	
	public static String performTranslation(String origString) {
		String filePath = UsbongUtils.USBONG_TREES_FILE_PATH + myTreeFileName+".utree/trans/"+getSetLanguage()+".xml";
		File file = new File(filePath);
		if(!file.exists())
		{
			file = new File(UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+myTreeFileName+".utree/trans/"+getSetLanguage()+".xml");

			if(!file.exists()) {						
				return origString; //just send the original string
			}
		}
		//if this point is reached, this means that trans file exists

		try {
			  XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		      factory.setNamespaceAware(true);
			  XmlPullParser parser = factory.newPullParser();		 		  
			  
		      InputStream in = null;
		      InputStreamReader reader;
		      try {
		          in = new BufferedInputStream(new FileInputStream(file));
		      }  
		      catch(Exception e) {
		    	  e.printStackTrace();
		      }
		      reader = new InputStreamReader(in,"UTF-8"); 
			  
			  parser.setInput(reader);	
				  
			  //Reference: http://developer.android.com/training/basics/network-ops/xml.html;
			  //last accessed: 24 Oct. 2012
			  while(parser.next() != XmlPullParser.END_DOCUMENT) {
				  if (parser.getEventType() != XmlPullParser.START_TAG) {
			            continue;
			      }
				  
				  if (parser.getName().equals("string")) {					  
					  Log.d(">>>>>parser.getAttributeValue(null, 'name'): ",parser.getAttributeValue(null, "name"));
					  Log.d(">>>>>origString: ",origString);

					  if (parser.getAttributeValue(null, "name").equals(origString)) {
						  if (parser.next() == XmlPullParser.TEXT) {
							  Log.d(">>>>>parser.getText();: ",parser.getText());
							  return parser.getText();
						  }
					  }
				  }
			  }
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return origString; //default; just return the original string
	}
	
	public static void addElementToContainer(Vector<String> usbongAnswerContainer, String s, int usbongAnswerContainerCounter) {
		try {
			usbongAnswerContainer.setElementAt(s, usbongAnswerContainerCounter);
		}
		catch (Exception e) {
			usbongAnswerContainer.addElement(s);																							
		}
	}
	
	//answer by ZeroTek from stackoverflow
	//Reference: http://stackoverflow.com/questions/4275311/how-to-encrypt-and-decrypt-file-in-android;
	//last accessed: Sept. 27, 2013	
	public static byte[] generateKey(String password) throws Exception
	{
	    byte[] keyStart = password.getBytes("UTF-8");

	    KeyGenerator kgen = KeyGenerator.getInstance("AES");
	    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
	    sr.setSeed(keyStart);
	    kgen.init(128, sr);
	    SecretKey skey = kgen.generateKey();
	    return skey.getEncoded();
	}

	//answer by ZeroTek from stackoverflow
	//Reference: http://stackoverflow.com/questions/4275311/how-to-encrypt-and-decrypt-file-in-android;
	//last accessed: Sept. 27, 2013	
    public static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception
    {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    public static String performSimpleFileEncrypt(int key, String fileData) 
    {
    	StringBuffer myFileData = new StringBuffer(fileData.toString());
    	StringBuffer encrypted = new StringBuffer();
    	int myFileDataLength = fileData.length();
    	int myAlphanumericLength = alphanumeric.length;    	
    	boolean isUpperCase=false;
    	
    	for(int i=0; i<myFileDataLength; i++) {
    		if (Character.isUpperCase(myFileData.charAt(i))) {
    			isUpperCase=true;
    		}
    		else {
    			isUpperCase=false;
    		}
    		
    		if (Character.isLetter(myFileData.charAt(i))||Character.isDigit(myFileData.charAt(i))) {    			
	    		for(int k=0; k<myAlphanumericLength; k++) {
	    			if (Character.toLowerCase(myFileData.charAt(i)) == alphanumeric[k]) {
	    				int counter = (k+key)%myAlphanumericLength; //do a plus
	    				if (isUpperCase) {
	    		    		encrypted.append(Character.toUpperCase(alphanumeric[counter]));    					
	    				}
	    				else {
	    		    		encrypted.append(alphanumeric[counter]);    					    					
	    				}
	    			}
	    		}
    		}
    		else {
	    		encrypted.append(fileData.charAt(i));    					    					    			
    		}
    	}
        return encrypted.toString();
    }

    public static String performSimpleFileDecrypt(int key, String fileData)
    {
    	StringBuffer myFileData = new StringBuffer(fileData.toString());
    	StringBuffer decoded = new StringBuffer();
    	int myFileDataLength = fileData.length();
    	int myAlphanumericLength = alphanumeric.length;    	
    	boolean isUpperCase=false;
    	
    	for(int i=0; i<myFileDataLength; i++) {
    		if (Character.isUpperCase(myFileData.charAt(i))) {
    			isUpperCase=true;
    		}
    		else {
    			isUpperCase=false;
    		}
    		
    		if (Character.isLetter(myFileData.charAt(i))||Character.isDigit(myFileData.charAt(i))) {    			
	    		for(int k=0; k<myAlphanumericLength; k++) {
	    			if (Character.toLowerCase(myFileData.charAt(i)) == alphanumeric[k]) {
	    				//Reference: http://stackoverflow.com/questions/11464890/first-char-to-upper-case;
	    				//last accessed: 21 Dec. 2013; answered by Jon
//	    				int counter = ((k-key) % 26 + 26) % 26;
	    				int counter = ((k-key) % myAlphanumericLength + myAlphanumericLength) % myAlphanumericLength;
	    				if (isUpperCase) {
	    		    		decoded.append(Character.toUpperCase(alphanumeric[counter]));    					
	    				}
	    				else {
	    		    		decoded.append(alphanumeric[counter]);    					    					
	    				}
	    			}
	    		}
    		}
    		else {
	    		decoded.append(fileData.charAt(i));    					    					    			
    		}
    	}
        return decoded.toString();
    }
    
	//answer by ZeroTek from stackoverflow
	//Reference: http://stackoverflow.com/questions/4275311/how-to-encrypt-and-decrypt-file-in-android;
	//last accessed: Sept. 27, 2013	
    public static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }
    
    public static String processStringToBeFilenameReady(String s) {
    	s = s.replace("<", "ls");
    	s = s.replace(">", "gs");
    	s = s.replace("\\", "bs");    	
    	s = s.replace("/", "fs");
    	s = s.replace("*", "asterisk");
    	s = s.replace("?", "questionMark");
    	s = s.replace("\"", "quotationMark");
    	s = s.replace("|", "pipe");

    	return s;
    }
    
    //added by Mike, March 26, 2014
    //Reference: http://stackoverflow.com/questions/1383797/java-hashmap-how-to-get-key-from-value
    //last accessed: March 26, 2014; answer by Vitalii Fedorenko
    public static void processUsbongVariableAssignment(Map<String, String> myUsbongVariableMemory, String myStringValue) {    	
    	for (Entry<String, String> entry : myUsbongVariableMemory.entrySet()) {    		
    		Log.d(">>>>>", entry.getKey()+","+entry.getValue());    		
            if ("getInput()".equals(entry.getValue())) {
            	if (myStringValue.length()<=3) {
                	myUsbongVariableMemory.put(entry.getKey(), "");
            		return;
            	}            				
            	//sample myStringValue: "A,mike;"
            	Log.d(">>>>>>", myStringValue.substring(2, myStringValue.length()-1));
            	myUsbongVariableMemory.put(entry.getKey(), myStringValue.substring(2, myStringValue.length()-1));
            	return;
            }
        }    	    	
    }
    
    //added by JP, 26 May 2015
    public static boolean hasNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    
    //added by JP, 26 May 2015
	public static void initDisplayAndConfigOfUIL(Context context) {
		@SuppressWarnings("deprecation")
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.resetViewBeforeLoading(true)
				.showImageForEmptyUri(R.drawable.loading)
				.showImageOnFail(R.drawable.loading)
				.showImageOnLoading(R.drawable.loading)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.memoryCache(new WeakMemoryCache());
		config.defaultDisplayImageOptions(options);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app
		ImageLoader.getInstance().init(config.build());
	}

    //added by JP, 26 May 2015
	public static String parseYouTubeLink(String l) {
		String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|watch\\?v%3D|%2Fvideos%2F|embed%鬯ｩ蛹�ｽｽ�ｯ�ｽ�ｽ�ｽ�ｶ鬮ｯ讖ｸ�ｽ�ｽ�ｽ�ｽ�ｽ�ｯ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ�ｽ2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\n]*";

		Pattern compiledPattern = Pattern.compile(pattern);
	    Matcher matcher = compiledPattern.matcher(l);

	    if(matcher.find()){
	    	return matcher.group();
	    }
	    return "";
	}

    //added by JP, 26 May 2015
    public static String removeExtension(String filePath) {
        // These first few lines the same as Justin's
        File f = new File(filePath);

        // if it's a directory, don't remove the extention
        if (f.isDirectory()) return filePath;

        String name = f.getName();

        // Now we know it's a file - don't need to do any special hidden
        // checking or contains() checking because of:
        final int lastPeriodPos = name.lastIndexOf('.');
        if (lastPeriodPos <= 0)
        {
            // No period after first character - return name as it was passed in
            return filePath;
        }
        else
        {
            // Remove the last period and everything after it
            File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
            return renamed.getPath();
        }
    }
    
    //added by Mike, 20151129
    public static String[] initProcessReturnToMainMenuActivity() {
		String[] myPrompts = new String[4];
    	    	
    	if (getLanguageID(currLanguage)==LANGUAGE_FILIPINO) {
    		myPrompts[MY_PROMPT_TITLE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.alertStringValueFilipino));
    		myPrompts[MY_PROMPT_MESSAGE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.areYouSureYouWantToReturnToMainMenuFilipino));
    		myPrompts[MY_PROMPT_POSITIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.yesStringValueFilipino);
    		myPrompts[MY_PROMPT_NEGATIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.noStringValueFilipino);  
    	}
    	else if (getLanguageID(currLanguage)==LANGUAGE_JAPANESE) {
    		myPrompts[MY_PROMPT_TITLE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.alertStringValueJapanese));				    						    		
    		myPrompts[MY_PROMPT_MESSAGE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.areYouSureYouWantToReturnToMainMenuJapanese));
    		myPrompts[MY_PROMPT_POSITIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.yesStringValueJapanese);
    		myPrompts[MY_PROMPT_NEGATIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.noStringValueJapanese);  
    	}
    	else { //if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
    		myPrompts[MY_PROMPT_TITLE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.alertStringValueEnglish));				    						    		        	    		
    		myPrompts[MY_PROMPT_MESSAGE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.areYouSureYouWantToReturnToMainMenuEnglish));
    		myPrompts[MY_PROMPT_POSITIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.yesStringValueEnglish);
    		myPrompts[MY_PROMPT_NEGATIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.noStringValueEnglish);  
    	}
    	
    	return myPrompts;
    }

    //added by Mike, 20150616
    public static String[] initProcessReturnToTitleScreenActivity() {
		String[] myPrompts = new String[4];
    	    	
    	if (getLanguageID(currLanguage)==LANGUAGE_FILIPINO) {
    		myPrompts[MY_PROMPT_TITLE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.alertStringValueFilipino));
    		myPrompts[MY_PROMPT_MESSAGE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.areYouSureYouWantToReturnToTitleScreenFilipino));
    		myPrompts[MY_PROMPT_POSITIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.yesStringValueFilipino);
    		myPrompts[MY_PROMPT_NEGATIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.noStringValueFilipino);  
    	}
    	else if (getLanguageID(currLanguage)==LANGUAGE_JAPANESE) {
    		myPrompts[MY_PROMPT_TITLE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.alertStringValueJapanese));				    						    		
    		myPrompts[MY_PROMPT_MESSAGE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.areYouSureYouWantToReturnToTitleScreenJapanese));
    		myPrompts[MY_PROMPT_POSITIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.yesStringValueJapanese);
    		myPrompts[MY_PROMPT_NEGATIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.noStringValueJapanese);  
    	}
    	else { //if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
    		myPrompts[MY_PROMPT_TITLE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.alertStringValueEnglish));				    						    		        	    		
    		myPrompts[MY_PROMPT_MESSAGE] = ((String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.areYouSureYouWantToReturnToTitleScreenEnglish));
    		myPrompts[MY_PROMPT_POSITIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.yesStringValueEnglish);
    		myPrompts[MY_PROMPT_NEGATIVE_BUTTON_TEXT] = (String) UsbongDecisionTreeEngineActivity.getInstance().getResources().getText(R.string.noStringValueEnglish);  
    	}
    	
    	return myPrompts;
    }
    
    //added by Mike, 20160414
    //this is like getAvailableTranslationsArrayList(String treeFile)
	public static ArrayList<String> getAvailableHintsArrayList(String treeFile)
	{
		List<String> ret = new ArrayList<String>();
				
		String filePath = UsbongUtils.USBONG_TREES_FILE_PATH + treeFile+".utree/hints/";
		File file = new File(filePath);
		if(!file.exists())
		{
			file = new File(UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+treeFile+".utree/hints/");

			if(!file.exists()) {						
				return null;
			}
		}
		//if this point is reached, this means that the .utree has a 'trans' folder
		Log.d(">>>>","reached this point; has hints folder");

		try 
		{  							
			UsbongFileFilter myFileFilter = new UsbongFileFilter("xml");			
			String[] listOfHints = file.list(myFileFilter); //file.list();
			int totalHints = listOfHints.length;
			
			for(int i=0; i<totalHints; i++) {
				ret.add(listOfHints[i].replace(".xml", "")); //remove the ".xml" at the end
				Log.d(">>>>>>listOfHints[i]:",listOfHints[i]);
			}			
    	}
    	catch(Exception e) {
    		System.out.println("ERROR in reading FILE.");
    		e.printStackTrace();
    	}
		
		return (ArrayList<String>) ret;
	}
    
    //added by Mike, 20160411
    public static void putHintsInHashtable(String myTree) {
    	Log.d(">>>>", "myTree: "+myTree);
    	
    	ArrayList<String> myHintsArrayList = UsbongUtils.getAvailableHintsArrayList(myTree);
        if (myHintsArrayList==null) {
        	Log.d(">>>>", "no hints found");
        	return; //this means that there are no translations/hints for the given myTree
        }
    	
    	final int myHintsArrayListSize = myHintsArrayList.size();
    	Log.d(">>>>", "myHintsArrayListSize: "+myHintsArrayListSize);
        myHashtableOfWordHints = new Hashtable<String, Hashtable<String,String>>();
    	
		for (int i = 0; i < myHintsArrayListSize; i++) {		   
	    	String filePath = UsbongUtils.USBONG_TREES_FILE_PATH + myTreeFileName+".utree/hints/" + myHintsArrayList.get(i) +".xml";
	    	File file = new File(filePath);
			if(!file.exists())
			{
				file = new File(UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+myTreeFileName+".utree/hints/" + myHintsArrayList.get(i) +".xml");

				if(!file.exists()) 
				{						
					continue;
				}
			}
			//if this point is reached, this means that hint file exists
			
	        Hashtable<String,String> wordsHashtable = new Hashtable<String,String>();			
			
			try {			  
				  XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			      factory.setNamespaceAware(true);
				  XmlPullParser parser = factory.newPullParser();		 		  
					  
			      InputStream in = null;
			      InputStreamReader reader;
			      try {
			          in = new BufferedInputStream(new FileInputStream(file));
			      }  
			      catch(Exception e) {
			    	  e.printStackTrace();
			      }
			      reader = new InputStreamReader(in,"UTF-8"); 
				  
				  parser.setInput(reader);	
				  
				  //Reference: http://developer.android.com/training/basics/network-ops/xml.html;
				  //last accessed: 24 Oct. 2012
				  while(parser.next() != XmlPullParser.END_DOCUMENT) {
					  if (parser.getEventType() != XmlPullParser.START_TAG) {
				            continue;
				      }
					  
					  if (parser.getName().equals("string")) {
						  String name = parser.getAttributeValue(null, "name").trim().toLowerCase().replaceAll("[^\\w\\s]", "");
						  if (parser.next() == XmlPullParser.TEXT) {
					    	 wordsHashtable.put(name, parser.getText());
						  }
					  }
				  }
				  
				  Log.d(">>>>", myHintsArrayList.get(i));
			      myHashtableOfWordHints.put(myHintsArrayList.get(i),wordsHashtable); //first parameter is for the language name, the second is the hashtable of the actual words
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}		
    }
}