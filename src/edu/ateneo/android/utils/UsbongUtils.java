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
package edu.ateneo.android.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;

public class UsbongUtils {		
	public static boolean IS_IN_DEBUG_MODE=false;

	public static String BASE_FILE_PATH = Environment.getExternalStorageDirectory()+"/usbong/";
//	public static String BASE_FILE_PATH = "/sdcard/usbong/";
	private static String timeStamp;
    	
	public static final int LANGUAGE_ENGLISH=0; 
	public static final int LANGUAGE_FILIPINO=1; //uses English only
	
	public static String destinationServerURL;
	
	public static final String debug_username="usbong";
	public static final String debug_password="usbong";
	
    public static void generateTimeStamp() {
		Calendar date = Calendar.getInstance();
		int day = date.get(Calendar.DATE);
		int month = date.get(Calendar.MONTH) +1; //why +1? Because month starts at 0 (i.e. Jan).
		int year = date.get(Calendar.YEAR);
		int hour = date.get(Calendar.HOUR_OF_DAY);
		int min = date.get(Calendar.MINUTE);
		int sec = date.get(Calendar.SECOND);
		int millisec = date.get(Calendar.MILLISECOND);
		
		timeStamp = "" + day +"-"+ month +"-"+ year +"-"+ hour +"hr"+ min +"min"+ sec + "sec";//millisec;
    }

    public static String getTimeStamp() {
		return timeStamp;
    }

	public static void createUsbongFileStructure() throws IOException {
		//code below doesn't seem to work
//		String baseFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "usbong/";
		File directory = new File(BASE_FILE_PATH);
		System.out.println(">>>> Directory: " + directory.getAbsolutePath());
		
		if (!directory.exists() && !directory.mkdirs()) 
    	{
			System.out.println(">>>> Creating file structure for usbong");
    		throw new IOException("Base File Path to file could not be created.");
    	}    			
		System.out.println(">>>> Leaving createUsbongFileStructure");
	}	
	
	public static void createNewOutputFolderStructure() throws IOException {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> createNewOutputFolderStructure()");
		System.out.println("BASE_FILE_PATH + UsbongUtils.getTimeStamp()+/): "+BASE_FILE_PATH + UsbongUtils.getTimeStamp()+"/");
		//code below doesn't seem to work
//		String baseFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "usbong/";
		File directory = new File(BASE_FILE_PATH + UsbongUtils.getTimeStamp()+"/");
		System.out.println(">>>> Directory: " + directory.getAbsolutePath());
		
		if (!directory.exists() && !directory.mkdirs()) 
    	{
			System.out.println(">>>> Creating new output folder structure for usbong");
    		throw new IOException("Base File Path to file could not be created.");
    	}    			
		System.out.println(">>>> Leaving createNewOutputFolderStructure");
	}	

	
    public static String readTextFileInAssetsFolder(Activity a, String filename) {
		//READ A FILE
		//Reference: Jeffrey Jongko, Aug. 31, 2010
    	try {
    		byte[] b = new byte[100];    		
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
    
    //This methods removes ~
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
	
	//Reference: AbakadaUtils.java; public static ArrayList<String> getWords(String filePath)
	public static InputStreamReader getFileFromSDCardAsReader(String filePath) //example of file would be decision trees
	{
		try 
		{  	
			File file = new File(filePath);
			if(!file.exists())
			{
				System.out.println(">>>>>> File " + filePath + " doesn't exist."); //Creating file.
//				file.createNewFile();
				return null;
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
				file.createNewFile();
			}
						
			UsbongFileFilter myFileFilter = new UsbongFileFilter("xml");			
			String[] listOfTrees = file.list(myFileFilter); //file.list();
			int totalTrees = listOfTrees.length;
			
			for(int i=0; i<totalTrees; i++) {
				ret.add(listOfTrees[i].replace(".xml", "")); //remove the ".xml" at the end
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
//		text = text.toLowerCase(); //has problems when text input has symbols ?
    	text = text.replaceAll("\\bANG\\b", "ang");
    	text = text.replaceAll("\\n", "");

    	text = text.replace('h', 'j');

    	text = text.replaceAll("\\bmga\\b", "manga");
		text = text.replaceAll("\\bng\\b", "nang");
		text = text.replaceAll("gi", "ghi");		
		return text;
    }    
    
    public static int getLanguageID(String s) {
    	if (s!=null) {    		
	    	if (s.equals("Filipino")) {
	    		return LANGUAGE_FILIPINO;
	    	}
    	}
    	return LANGUAGE_ENGLISH;
    }
    
    public static Intent performEmailProcess(String filepath, List<String> filePathsList) {
//		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	final Intent emailIntent;
    	if (filePathsList!=null) {
    		emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
    	}
    	else {
    		emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	}
    	try {
			InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(filepath);
			BufferedReader br = new BufferedReader(reader);    		
	    	String currLineString;        	
			currLineString=br.readLine();
			System.out.println(">>>>>>>>>> currLineString: "+currLineString);
			
			//Reference: http://blog.iangclifton.com/2010/05/17/sending-html-email-with-android-intent/
			//last acccessed: 17 Jan. 2012
			emailIntent.setType("text/plain");
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "usbong;"+timeStamp);
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, currLineString); //body
//			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"masarapmabuhay@gmail.com"});//"masarapmabuhay@gmail.com"); 	
			
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
		    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		    
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return emailIntent;
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
        	
		  HttpClient client = new DefaultHttpClient();
		    	    	  
    	  //added by Mike, 10 Dec. 2011
//    	  HttpPost httppost = new HttpPost("http://192.168.1.105/"); 
//		  destinationServerURL="192.168.1.104";
		  HttpPost httppost = new HttpPost("http://"+destinationServerURL+"/");
		  
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
}
