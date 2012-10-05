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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringEscapeUtils;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class UsbongUtils {		
	public static boolean IS_IN_DEBUG_MODE=false;

	public static String BASE_FILE_PATH = Environment.getExternalStorageDirectory()+"/usbong/";
	public static String USBONG_TREES_FILE_PATH = BASE_FILE_PATH + "usbong_trees/";
	//	public static String BASE_FILE_PATH = "/sdcard/usbong/";
	private static String timeStamp;
    	
	public static final int LANGUAGE_ENGLISH=0; 
	public static final int LANGUAGE_FILIPINO=1; //uses English only
	
	public static String destinationServerURL;
	
	public static final String debug_username="usbong";
	public static final String debug_password="usbong";
	
	public static AssetManager myAssetManager;
	
	private static final String TAG = "UsbongUtils";
	
	public static final int IS_TEXTVIEW = 0;
	public static final int IS_RADIOBUTTON = 1;
	public static final int IS_CHECKBOX = 2;
	
	public static final boolean USE_UNESCAPE=true; //allows the use of \n (new line) in the decision tree
	
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
	
	public static boolean is_a_utree=false;
	
	public static boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
	
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
		File directory = new File(BASE_FILE_PATH + UsbongUtils.getTimeStamp()+"/");
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
    	String destination = USBONG_TREES_FILE_PATH+"usbong_demo_tree.xml";

    	//delete usbong_demo_tree.xml
    	File file = new File(destination);
    	if (file.exists()) {
        	file.delete();    		
    	}

    	destination = USBONG_TREES_FILE_PATH+"usbong_demo_tree.utree";

    	//replace usbong_demo_tree.xml with usbong_demo_tree.utree
    	file = new File(destination);
    	file.delete();

    	//start anew
//    	if(!file.exists()) {
			
//			System.out.println(">>>>>> File " + destination + " doesn't exist. Creating file.");
			file.mkdirs();

	    	//arg#1 is the destination, arg#2 is the string 
			storeOutputInSDCard(destination+"/usbong_demo_tree.xml", readTextFileInAssetsFolder(a,filename));		
			
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

	
	//Reference: AbakadaUtils.java; public static ArrayList<String> getWords(String filePath)
	public static InputStreamReader getTreeFromSDCardAsReader(String treeFile) //example of file would be decision trees
	{
		is_a_utree=false; //this variable is not yet used anywhere, Mike, Sept 2, 2012
		try 
		{  	
			//first create temp folder
			File file = new File(USBONG_TREES_FILE_PATH+"temp/");
	    	if (file.exists()) {
//	        	file.delete();    		
	    		deleteRecursive(file);//do this to delete contents of the directory
	    	}
	    	file.mkdirs();

			//test if it's a .xml
			file = new File(UsbongUtils.USBONG_TREES_FILE_PATH + treeFile+".xml");
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
							File f = new File(UsbongUtils.USBONG_TREES_FILE_PATH+"temp/"+ treeFile+".utree");
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
	    	UsbongUtils.createNewOutputFolderStructure();

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
	
    //converts the Filipino Text to Spanish Accent-friendly text
	//based on the following rules
	//Reference: http://answers.oreilly.com/topic/217-how-to-match-whole-words-with-a-regular-expression/; last accessed 27 Sept 2011
	//Also, take note that in Android, you must add an extra escape character (e.g. \b becomes \\b)
    public static String convertFilipinoToSpanishAccentFriendlyText(String text) {    	
//		text = text.toLowerCase(); //has problems when text input has symbols ?

    	text = text.replace('h', 'j');
    	text = text.replace('H', 'J');

    	text = text.replaceAll("\\bmga\\b", "manga");
    	text = text.replaceAll("\\bMga\\b", "Manga");
    	
		text = text.replaceAll("\\bng\\b", "nang");
		text = text.replaceAll("\\bNg\\b", "Nang");

		text = text.replaceAll("gi", "ghi");		
		text = text.replaceAll("Gi", "Ghi");		

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
    	
    	File imageFile = new File(path+".png");

		if(!imageFile.exists()) {
			imageFile = new File(path+".jpg");
			path = path+".jpg";					
		}
		else {
			path = path+".png";
		}

    	if (imageFile.exists()) {    		
    		return path;		    		
    	}
    	return "null";
//		return imageFile;
    }
    
    //supports .png and .jpg
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

        try {
//        	Log.d(">>>>>>>","1");
            File f = new File(location);
            if(!f.isDirectory()) {
                f.mkdirs();
//            	Log.d(">>>>>>>","1.5: f.mkdirs()");
            }
//        	Log.d(">>>>>>>","2");

            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
//                	Log.d(">>>>>>>","3");
//                	Log.d(">>>>>>>","location: "+location);
//                	Log.d(">>>>>>>","ze.getName(): "+ze.getName());

                    if (ze.isDirectory()) {
//                    	Log.d(">>>>>>>","4.1");

                    	File unzipFile = new File(path);
//                    	Log.d(">>>>>>>","5.1");
//                    	Log.d(">>>>>>>","path: "+path);

                    	if(!unzipFile.isDirectory()) {
//                        	Log.d(">>>>>>>","6.1");
                    		unzipFile.mkdirs();
//                        	Log.d(">>>>>>>","7.1");
                        }
                    }
                    else {
//                    	Log.d(">>>>>>>","4.2");
                    	FileOutputStream out = new FileOutputStream(path, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
//                            	Log.d(">>>>>>>","4.3");
                            	fout.write(buffer, 0, size);
                            }
//                        	Log.d(">>>>>>>","4.4");
                            zin.closeEntry();
                        }
                        finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            }
            finally {
                zin.close();
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Unzip exception", e);
        }
    }
    
    //From teedyay, stackoverflow
    //Reference: http://stackoverflow.com/questions/4943629/android-how-to-delete-a-whole-folder-and-content;
    //last accessed: 16 Sept. 2012
    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

	//added by Mike, Sept. 27, 2012
	//answer from Chistopher, stackoverflow
	//Reference: http://stackoverflow.com/questions/2730706/highlighting-text-color-using-html-fromhtml-in-android;
	//last accessed: 19 Sept. 2012
    public static View applyTagsInView(View myView, int type, String myCurrUsbongNode) {
    	String styledText;

    	if (USE_UNESCAPE) {
        	styledText = StringEscapeUtils.unescapeJava(UsbongUtils.trimUsbongNodeName(myCurrUsbongNode));
        }
        else {
        	styledText = UsbongUtils.trimUsbongNodeName(myCurrUsbongNode);
        }
    	
		styledText = UsbongUtils.processIndent(styledText);

		switch(type) {
			case IS_TEXTVIEW:
				((TextView)myView).setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
				break;
			case IS_RADIOBUTTON:
				((RadioButton)myView).setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
				break;
			case IS_CHECKBOX:
				((CheckBox)myView).setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
				break;				
		}
		
		return myView;
    }

    
    public static TextView applyTagsInTextView(TextView tv, String myText) {
		String styledText = myText;
		styledText = UsbongUtils.processIndent(styledText);
		
		//added by Mike, Sept. 27, 2012
		//answer from Chistopher, stackoverflow
		//Reference: http://stackoverflow.com/questions/2730706/highlighting-text-color-using-html-fromhtml-in-android;
		//last accessed: 19 Sept. 2012
		tv.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

		return tv;
    }

    public static RadioButton applyTagsInRadioButton(RadioButton rb, String myText) {
		String styledText = myText;
		styledText = UsbongUtils.processIndent(styledText);
		
		//added by Mike, Sept. 27, 2012
		//answer from Chistopher, stackoverflow
		//Reference: http://stackoverflow.com/questions/2730706/highlighting-text-color-using-html-fromhtml-in-android;
		//last accessed: 19 Sept. 2012
		rb.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

		return rb;
    }

    public static CheckBox applyTagsInCheckBox(CheckBox cb, String myText) {
		String styledText = myText;
		styledText = UsbongUtils.processIndent(styledText);
		
		//added by Mike, Sept. 27, 2012
		//answer from Chistopher, stackoverflow
		//Reference: http://stackoverflow.com/questions/2730706/highlighting-text-color-using-html-fromhtml-in-android;
		//last accessed: 19 Sept. 2012
		cb.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

		return cb;
    }

    
	//added by Mike, Sept. 19, 2012
	public static String processIndent(String myText) {
		//answer from Thierry-Dimitri Roy, stackoverflow
		//Reference: http://stackoverflow.com/questions/3611635/java-regex-replaceall-does-not-replace-string;
		//--> must assign new value to myText
		//last accessed: 19 Sept. 2012
		myText = myText.replaceAll("<indent>", "<font color=\'#f5f2f2\'>ttttt</font>");			
		return myText;
	}

}
