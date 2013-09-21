package usbong.android.features.node;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import usbong.android.R;
import usbong.android.multimedia.graphics.MyCanvas;
import usbong.android.utils.UsbongUtils;

public class PaintActivity extends Activity
{
	private MyCanvas myCanvas;
	public String myPaintName;
/*
	private String currentWord;
	private String timeStamp;
*/
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        myCanvas = new MyCanvas(this);        
        setContentView(myCanvas);
        
        myPaintName=this.getIntent().getStringExtra("myPaintName");
/*        
        currentWord = GameActivity.getInstance().getChosenWordToBeGuessed();//getIntent().getStringExtra("currentWord");
        timeStamp = GameActivity.timeStamp;
*/
        new AlertDialog.Builder(PaintActivity.this).setTitle("Usbong Tip")
//			.setMessage(currentWord)
        	.setMessage("Touch the screen with your stylus or finger and start drawing! When you're done, hit the menu button and save your work.")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.canvas_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
//		currentWord = "test";
//		FileOutputStream fileOutputStream = null;
//		String path = "/sdcard/abakada/" + timeStamp + "/" + currentWord + "1.jpg";
//		File sdImageMainDirectory = new File(path);
//		if(sdImageMainDirectory.exists())
//		{
//			System.out.println("FILE EXISTS!");
//		}
//    	if (!sdImageMainDirectory.exists() && !sdImageMainDirectory.mkdirs()) 
//    	{
//    		System.out.println("Path to file could not be created.");
//    	}
		switch(item.getItemId())
		{
			case(R.id.clear_all):
				myCanvas.clearCanvas();
				return true;
			case(R.id.save_canvas):
					try
					{	
						//----------------------------
						Bitmap myImage = myCanvas.getMyBitmap();

//				        File sdImageMainDirectory = new File("/sdcard/usbong" + "/" +UsbongUtils.getDateTimeStamp() +"/");
				        File sdImageMainDirectory = new File(UsbongUtils.BASE_FILE_PATH + "/" +UsbongUtils.getDateTimeStamp() +"/");
/*
						
						/*				        File sdImageMainDirectory = new File("/sdcard/abakada/" +timeStamp  +"/");
*/
				        sdImageMainDirectory.mkdirs();
				        
				        FileOutputStream fileOutputStream = null;
//						String nameFile;
	
						File outputFile= new File(sdImageMainDirectory, myPaintName  +".jpg" );
/*						File outputFile = new File(sdImageMainDirectory, currentWord +"1.jpg");	
*/
						fileOutputStream = new FileOutputStream(outputFile);
				  	  
						BufferedOutputStream bos = new BufferedOutputStream(
							fileOutputStream);

							myImage.compress(CompressFormat.JPEG, 90, bos);

//						------------------------------
//						Bitmap myImage = myCanvas.getMyBitmap();
//						
//						String filepath = sdImageMainDirectory.toString();
//						fileOutputStream = new FileOutputStream(filepath);
//	  	  
//						BufferedOutputStream bos = new BufferedOutputStream(
//								fileOutputStream, MODE_WORLD_READABLE);
//
//						myImage.compress(CompressFormat.JPEG, 50, bos);
						bos.flush();
						bos.close();

//						File imageFile = new File("/sdcard/usbong/" +UsbongUtils.getDateTimeStamp() +"/" + myPaintName+".jpg");
						File imageFile = new File(UsbongUtils.BASE_FILE_PATH +UsbongUtils.getDateTimeStamp() +"/" + myPaintName+".jpg");
						/*
						File imageFile = new File("/sdcard/abakada/" + timeStamp+ "/"+ currentWord + "1.jpg");
*/			    
						if(imageFile.exists())
						{
							System.out.println("FILE EXISTS!");
						}
						finish();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/*
	 * GETTERS AND SETTERS
	 */
	public MyCanvas getMyCanvas() {
		return myCanvas;
	}

	public void setMyCanvas(MyCanvas myCanvas) {
		this.myCanvas = myCanvas;
	}
}
