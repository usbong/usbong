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

import java.io.File;
import java.io.FilenameFilter;

//Reference: http://www.java-samples.com/showtutorial.php?tutorialid=384; last accessed: 11 Nov. 2011
public class UsbongFileFilter implements FilenameFilter {
	String myExtension;
	public UsbongFileFilter(String ext)
	{
		this.myExtension = ext;
	}    	
	@Override
	public boolean accept(File dir, String filename) {    		    		
		return filename.endsWith(myExtension);
	}
}