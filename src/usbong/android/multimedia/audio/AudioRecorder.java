/*
 * Copyright 2012 Michael Syson and Kenneth Llanto
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
package usbong.android.multimedia.audio;

import java.io.File;
import java.io.IOException;

import usbong.android.utils.UsbongUtils;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;

public class AudioRecorder {
	  String myRecordAudioFileName;
	  MediaRecorder recorder;
	  MediaPlayer currMediaPlayer;
	  String path;

	  /**
	   * Creates a new audio recording at the given path (relative to root of SD card).
	   */
	  public AudioRecorder(String path, String myRecordAudioFileName) 
	  {
	    this.path = sanitizePath(path);
	    this.myRecordAudioFileName=myRecordAudioFileName;//this is currUsbongNode
	  }
	  
	  public String getPath() {
		  return path;
	  }

	  public boolean isPlaying() {
		  if (currMediaPlayer!=null) {
			  return currMediaPlayer.isPlaying();
		  }
		  else {
			  return false;
		  }
	  }

	  public void stopPlayback() {
		  if (currMediaPlayer!=null) {
			  currMediaPlayer.stop();
		  }
	  }
	  
	  private String sanitizePath(String path) {
	    if (!path.startsWith("/")) {
	      path = "/" + path;
	    }
	    if (!path.contains(".")) {
	      path += ".3gp";
	    }
	    return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
	  }

	  /**
	   * Starts a new recording.
	   */
	  public void start() throws IOException {
		recorder  = new MediaRecorder();
//	    String state = android.os.Environment.getExternalStorageState();
		
	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

	    File file = new File(UsbongUtils.BASE_FILE_PATH + UsbongUtils.getDateTimeStamp()+"/"+myRecordAudioFileName+".3gp");
       	path =file.getPath();

	    // check if memory card is being used
	    // UNCOMMENT BELOW TO PERFORM MEMORY CARD SAVING
/*
       	if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  
	    {
	    	// instead save on the data directory
//	    	File file = new File(Environment.getDataDirectory().getAbsolutePath()+
//	    			"/data/edu.ateneo.android/test.3gp");
	    			
	        recorder.setOutputFile(path);  	    
	  	}
	    else
	    {
*/	    	
	    	// make sure the directory we plan to store the recording in exists	    
	    	File directory = new File(path).getParentFile();
	    	if (!directory.exists() && !directory.mkdirs()) 
	    	{
	    		throw new IOException("Path to file could not be created.");
	    	}
	    	
		    recorder.setOutputFile(path);
//	    }
	    // UNCOMMENT ABOVE TO PERFORM MEMORY CARD SAVING
	    recorder.prepare();
	    recorder.start();
	  }

	  /**
	   * Stops a recording that has been previously started.
	   */
	  public void stop() throws IOException {
	    recorder.stop();
	    recorder.release();
	  }
	  
	  public boolean hasRecordedData() {
		    File file = new File(path);
		    if(file.exists()) {
		    	return true;
		    }
		    return false;
	  }

	  public void play() throws IOException 
	  {
		    MediaPlayer mp = new MediaPlayer();
		    currMediaPlayer = mp;
		    File file = new File(path);
		    try 
		    {
		    	if(file.exists())
		    	{
		    	  mp.setDataSource(path);
		    	}
		    } catch (IOException e) 
		    {
		    	System.out.println(e.getMessage());
		    }

		    mp.prepare();
		    mp.start();
		    mp.setVolume(1000, 1000);
		    //int x = AudioManager.getStreamMaxVolume(3);
		    // setStreamVolume  (3, 100, 100);
		    // setSpeakerphoneOn  (boolean on)
		    // FROM: http://www.barebonescoder.com/2010/06/android-development-audio-playback-safely/
		    mp.setOnCompletionListener(new OnCompletionListener() {
		    		 
		    	@Override
		    	public void onCompletion(MediaPlayer mp) {
		    	mp.release();
		    	}
	        });		    		 			  
	  }
	  
	  public void playSaved() throws IOException 
	  {
		    MediaPlayer mp = new MediaPlayer();
		    currMediaPlayer = mp;
		    File file = new File(path);
		    try 
		    {
		    	if(file.exists())
		    	{
		    		mp.setDataSource(path);
				    mp.prepare();
				    mp.start();
				    mp.setVolume(1000, 1000);
				    //int x = AudioManager.getStreamMaxVolume(3);
				    // setStreamVolume  (3, 100, 100);
				    // setSpeakerphoneOn  (boolean on)
				    // FROM: http://www.barebonescoder.com/2010/06/android-development-audio-playback-safely/
				    mp.setOnCompletionListener(new OnCompletionListener() {
				    		 
				    @Override
				    public void onCompletion(MediaPlayer mp) {
				    	mp.release();
				    	}
			        });

		    	}
		    } catch (IOException e) 
		    {
		    	System.out.println(e.getMessage());
		    }			  
	  }	  
}