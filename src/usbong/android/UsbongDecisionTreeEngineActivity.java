/*
 * Copyright 2011-2016 Usbong Social Systems, Inc.
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
/*
 * @authors: Michael B. Syson, JP Talusan
 */
package usbong.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import usbong.android.community.Constants;
import usbong.android.features.node.PaintActivity;
import usbong.android.features.node.QRCodeReaderActivity;
import usbong.android.multimedia.audio.AudioRecorder;
import usbong.android.utils.FedorMyLocation;
import usbong.android.utils.PurchaseLanguageBundleListAdapter;
import usbong.android.utils.UsbongConstants;
import usbong.android.utils.UsbongScreenProcessor;
import usbong.android.utils.UsbongUtils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

//@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class UsbongDecisionTreeEngineActivity extends /*YouTubeBaseActivity*/AppCompatActivity implements TextToSpeech.OnInitListener {
//	private static final boolean UsbongUtils.USE_UNESCAPE=true; //allows the use of \n (new line) in the decision tree

//	private static boolean USE_ENG_ONLY=true; //uses English only	
//	private static boolean UsbongUtils.IS_IN_DEBUG_MODE=false;

	public final String myPackageName="usbong.android";
	
	public int currLanguageBeingUsed;
		
	public int currScreen=UsbongConstants.TEXTFIELD_SCREEN;
	
	public static final int PLEASE_CHOOSE_AN_ANSWER_ALERT_TYPE=0;
	public static final int PLEASE_ANSWER_FIELD_ALERT_TYPE=1;

	private Button backButton;
	private Button nextButton;	
	
	private Button stopButton;
	private Button recordButton;
	private Button playButton;
	
	private static AudioRecorder currAudioRecorder;
	private static MediaPlayer myMediaPlayer;
	private static MediaPlayer myBGMediaPlayer; //added by Mike, 25 Sept. 2015
	
	private Button paintButton;
	private Button photoCaptureButton;
	private ImageView myImageView;

	private Button qrCodeReaderButton;

	public static Intent photoCaptureIntent;
	public static Intent paintIntent;
	public static Intent qrCodeReaderIntent;
	public static Intent youTubeIntent;
	
	private String myPictureName="default"; //change this later in the code
	private String myPaintName="default"; //change this later in the code
	private String myQRCodeReaderName="default"; //change this later in the code

//	private boolean hasReachedEndOfAllDecisionTrees;
//	private boolean isFirstQuestionForDecisionTree;

	public boolean usedBackButton;
	
	public boolean performedCapturePhoto;
	public boolean performedRunPaint;
	public boolean performedGetQRCode;
	
	public String currUsbongNode="";
	public String currUsbongAudioString=""; //added by Mike, 21 July 2015
	public String currUsbongBGAudioString=""; //added by Mike, 25 Sept. 2015
	private String nextUsbongNodeIfYes;
	private String nextUsbongNodeIfNo;

	public String currUsbongNodeWithoutAnswer="";
	
	public String textFieldUnit="";
		
	public int usbongNodeContainerCounter=-1;//because I do a ++, so that the first element would be at 0;
	private int requiredTotalCheckedBoxes;
	public Vector<String> usbongNodeContainer;
	public Vector<String> classificationContainer;
	public Vector<String> radioButtonsContainer;
	public Vector<String> usbongAnswerContainer;
	public Vector<String> checkBoxesContainer;
	private Vector<String> decisionTrackerContainer; //added by Mike, Feb. 2, 2013

	public String noStringValue;
	public String yesStringValue;

//	private String myTreeDirectory="usbong_trees/";
	public String myTree="no tree selected.";//"input.xml";
	private String myOutputDirectory=UsbongUtils.getDateTimeStamp()+"/"; //add the ".csv" after appending the timestamp //output.csv
	
	private static UsbongDecisionTreeEngineActivity instance;
    private static TextToSpeech mTts;
    private HashMap<String, String> mTtsParams;

	public ListView treesListView;
	
	private CustomDataAdapter mCustomAdapter;
	private ArrayList<String> listOfTreesArrayList;

	public ArrayAdapter<CharSequence> monthAdapter;
	public ArrayAdapter<CharSequence> dayAdapter;
	
	private List<String> attachmentFilePaths;
	
	public FedorMyLocation myLocation;
	
	private boolean isInTreeLoader;
	
	private static String myQRCodeContent;
    public boolean hasReturnedFromAnotherActivity; //camera, paint, email, etc
	private static boolean wasNextButtonPressed;
	private static boolean hasUpdatedDecisionTrackerContainer;
	
	public boolean isAnOptionalNode;
	public String currAnswer;
	public int usbongAnswerContainerCounter;
	
    private int padding_in_dp = 5;  // 5 dps
    public int padding_in_px;
    
    public String myMultipleRadioButtonsWithAnswerScreenAnswer;
    public String myTextFieldWithAnswerScreenAnswer;
    public String myTextAreaWithAnswerScreenAnswer;
    public String timestampString;
    
    public StringBuffer myDcatSummaryStringBuffer;
    
    private UsbongScreenProcessor myUsbongScreenProcessor;

	private Map<String, String> myUsbongVariableMemory;
    
	protected InputStreamReader isr;

	private ProgressDialog myProgressDialog;

	private int currSelectedItemForSetLanguage=0;
	
    private YouTubePlayerFragment myYouTubePlayerFragment;
	public static YouTubePlayer myYouTubePlayer;
	private UdteaObject myUdteaObject;
	
	//edited by Mike, 20160417
    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;
    
	//edited by Mike, 20160417
    public static ArrayList<Integer> selectedSettingsItems;
    public static boolean[] selectedSettingsItemsInBoolean;
    private AlertDialog inAppSettingsDialog;
    
    //added by Mike, 20160415
    private boolean isAutoLoopedTree;
    
    //added by Mike, 20160420
    private static AlertDialog.Builder purchaseLanguagesListDialog;
    private static PurchaseLanguageBundleListAdapter myPurchaseLanguageBundleListAdapter;
//	private static DialogInterface purchaseLanguagesListDialogInterface;
	private static AlertDialog purchaseLanguageListAlertDialog;
    private static AlertDialog.Builder setLanguageDialog;
	private static AlertDialog mySetLanguageAlertDialog;

	private ArrayAdapter<String> arrayAdapter; //added by Mike, 20160507
		
//	@SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);		

        super.onCreate(savedInstanceState);    
        
        Log.d(">>>>", "insideOnCreate(...)");
                
        instance=this;
        
        //added by Mike, 20160510
        if (UsbongUtils.hasUnlockedAllLanguages) {
        	UsbongUtils.hasUnlockedLocalLanguages=true;
        	UsbongUtils.hasUnlockedForeignLanguages=true;        	
        }
        
        //added by Mike, 20160410
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        UsbongUtils.myAssetManager = getAssets();
        
        //added by Mike, 22 Sept. 2015
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);        

        currUsbongNode=""; //added by Mike, 20151126
        isAutoLoopedTree=false; //added by Mike, 20160415
        
        //added by Mike, 29 Sept. 2015
		myProgressDialog = ProgressDialog.show(instance, "Loading...",
				  "This takes only a short while.", true, false);				  
		new MyBackgroundTask().execute();   	    	       
    }
    
    public void init() {
        //if return is null, then currScreen=0
//      currScreen=Integer.parseInt(getIntent().getStringExtra("currScreen")); 
      //modified by JPT, May 25, 2015        
      try {
	        if(getIntent().getStringExtra("currScreen") != null) {
	        	currScreen=Integer.parseInt(getIntent().getStringExtra("currScreen")); 
	        }
      }
      catch(java.lang.ClassCastException e) { //if currScreen is an int not a String
//	        if(getIntent().getIntExtra("currScreen", currScreen) != -1) {
	        	currScreen=getIntent().getIntExtra("currScreen", currScreen); 
//	        }        	
      }
      
//  	currScreen=getIntent().getIntExtra("currScreen", currScreen); 

        
        //default..
        currLanguageBeingUsed=UsbongUtils.LANGUAGE_ENGLISH;
		UsbongUtils.setCurrLanguage("English"); //added by Mike, 22 Sept. 2015

        //==================================================================
        //text-to-speech stuff
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, UsbongUtils.MY_DATA_CHECK_CODE);

        mTts = new TextToSpeech(this,this);
		mTts.setLanguage(new Locale("en", "US"));//default
        //==================================================================
        
		myMediaPlayer = new MediaPlayer();
		myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); //added by Mike, 22 July 2015
		myMediaPlayer.setVolume(1.0f, 1.0f);

		//added by Mike, 25 Sept. 2015
		myBGMediaPlayer = new MediaPlayer();
		myBGMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		myBGMediaPlayer.setVolume(0.5f, 0.5f);
		
		//added by Mike, 22 July 2015
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		
    	usbongNodeContainer = new Vector<String>();
    	classificationContainer = new Vector<String>();
    	radioButtonsContainer = new Vector<String>();
    	usbongAnswerContainer = new Vector<String>();
    	checkBoxesContainer = new Vector<String>();
    	decisionTrackerContainer = new Vector<String>();

    	usedBackButton=false;
    	currAnswer="";    	    	
    	
    	try{    		
    		//commented out by Mike, 4 Oct. 2015
  			UsbongUtils.createUsbongFileStructure();
  			
  			//added by Mike, 20160417
  			UsbongUtils.initUsbongConfigFile();
 
    		//create the usbong_demo_tree and store it in sdcard/usbong/usbong_trees
    		UsbongUtils.storeAssetsFileIntoSDCard(this,"usbong_demo_tree.xml");
//    		UsbongUtils.storeAssetsFileIntoSDCard(this,"usbong_demo_tree.utree");
    	}
    	catch(IOException ioe) {
    		ioe.printStackTrace();
    	}
    	
    	//Reference: http://stackoverflow.com/questions/2793004/java-lista-addalllistb-fires-nullpointerexception
    	//Last accessed: 14 March 2012
    	attachmentFilePaths = new ArrayList<String>();            	
//		attachmentFilePaths.clear();
//		System.out.println(">>>>>>>>> attachmentFilePaths.clear!");
		currAudioRecorder = null;
		
		myQRCodeContent="";
	    hasReturnedFromAnotherActivity=false; //camera, paint, email, etc

	    //added by Mike, March 4, 2013
	    usbongAnswerContainerCounter=0;
/*	    	    
	    ArrayList<String> skuList = new ArrayList<String> ();
	    skuList.add(UsbongConstants.ALL_LOCAL_LANGUAGES_PRODUCT_ID);
	    skuList.add(UsbongConstants.ALL_FOREIGN_LANGUAGES_PRODUCT_ID);
	    Bundle querySkus = new Bundle();
	    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
*/	    
	    
/*//commented out by Mike, 20161121	    
	    new AsyncTask<String, Integer, Boolean>() {
			@Override
			protected void onPostExecute(Boolean result) {
			    //added by Mike, 20160421
				//init purchase languages list
		        purchaseLanguagesListDialog = new AlertDialog.Builder(getInstance());
		        myPurchaseLanguageBundleListAdapter = new PurchaseLanguageBundleListAdapter(getInstance(), UsbongUtils.getInAppOwnedItems(), UsbongUtils.getInAppMService());
		        purchaseLanguagesListDialog.setTitle("Purchase");	
				purchaseLanguagesListDialog.setSingleChoiceItems(myPurchaseLanguageBundleListAdapter,0,
				        new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int which) {								            	
	//			                Log.i("Selected Item : ", (String) myPurchaseLanguageBundleListAdapter.getItem(which));
				                dialog.dismiss();	//edited by Mike, 20160508	            	
	//			            	purchaseLanguagesListDialogInterface = dialog;
				            }
				        });				            	
		    	purchaseLanguagesListDialog.setNegativeButton("Cancel",
				        new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int which) {
				                dialog.dismiss();
				            }
				        });  		
			}

			@Override
			protected Boolean doInBackground(String... params) {
			    try {
			    	UsbongUtils.initInAppBillingService(getInstance());
			    }
			    catch (Exception e) {
			    	e.printStackTrace();
			    }
				return true;		
			}	
	    }.execute();
*/	    	        	
        //reference: Labeeb P's answer from stackoverflow;
        //http://stackoverflow.com/questions/4275797/view-setpadding-accepts-only-in-px-is-there-anyway-to-setpadding-in-dp;
        //last accessed: 23 May 2013
        final float scale = getResources().getDisplayMetrics().density;
        padding_in_px = (int) (padding_in_dp * scale + 0.5f);
	    
        //added by Mike, 25 June 2013
        UsbongUtils.setDebugMode(UsbongUtils.isInDebugMode());
        
        //added by Mike, 25 Feb. 2014
//        UsbongUtils.setStoreOutput(UsbongUtils.checkIfStoreOutput());

        //commented out by Mike, 20161122
//        UsbongUtils.setStoreOutput(false); //don't store output, added by Mike, 27 Sept. 2015
        
        myUsbongScreenProcessor = new UsbongScreenProcessor(UsbongDecisionTreeEngineActivity.getInstance());
        myUsbongVariableMemory = new HashMap<String, String>();

        //added by Mike, March 26, 2014
		try {
			Log.d(">>>>", ""+UsbongUtils.STORE_OUTPUT);
			if (UsbongUtils.STORE_OUTPUT) { //added by Mike, 27 Sept. 2015
				UsbongUtils.createNewOutputFolderStructure();				
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}		

		//added by JPT, Jul 13, 2015
		Intent intent = getIntent();
 	   	String action = intent.getAction();
 	   	String type = intent.getType();
 	    
 	   if(getIntent().getStringExtra(Constants.UTREE_KEY) != null) {
 		   Log.d(">>>>>>","if(getIntent().getStringExtra(Constants.UTREE_KEY) != null) {");
 		   Log.d("DecisionTree", getIntent().getStringExtra(Constants.UTREE_KEY));
		   
 		   myUdteaObject = new UdteaObject(); //added by Mike, 20151212 		   
 		   initParser(getIntent().getStringExtra(Constants.UTREE_KEY));
 	   }
 	   else if(Intent.ACTION_SEND.equals(action) && type != null) {
 		   Log.d(">>>>>>","inside else if(Intent.ACTION_SEND.equals(action) && type != null) {");
 		   myUdteaObject = new UdteaObject(); //added by Mike, 20151212 		   

 	    	if(getIntent().getStringExtra(Constants.UTREE_KEY) != null) {
 	    		Log.d("DecisionTree", getIntent().getStringExtra(Constants.UTREE_KEY));
 	    		initParser(getIntent().getStringExtra(Constants.UTREE_KEY));
 	    	} else if(Intent.ACTION_SEND.equals(action) && type != null) {
	 			if ("application/utree".equals(type)) {
	 				Log.d("DecisionTree", getIntent().getStringExtra(Constants.UTREE_KEY));
	 				initParser(getIntent().getStringExtra(Constants.UTREE_KEY));
	 			}
 	    	}
 	    }
 	    //added by Mike, 20151126
 	    else if(getIntent().getParcelableExtra(UsbongConstants.BUNDLE) != null) {
 	    	myUdteaObject = getIntent().getParcelableExtra((UsbongConstants.BUNDLE));
 	    	Log.d(">>>>>","inside UsbongConstants.Bundle");
 	    	
 	    	myTree = myUdteaObject.getMyTree();
 	    	currUsbongNode = myUdteaObject.getCurrUsbongNode();
	 	    Log.d(">>>>currUsbongNode",currUsbongNode); 	    		

	    	nextUsbongNodeIfYes = myUdteaObject.getNextUsbongNodeIfYes();
	    	nextUsbongNodeIfNo = myUdteaObject.getNextUsbongNodeIfYes();

// 	    	usbongAnswerContainerCounter = myUdteaObject.getUsbongAnswerContainerCounter();
// 	    	usbongNodeContainerCounter = myUdteaObject.getUsbongNodeContainerCounter();
 	    	decisionTrackerContainer = myUdteaObject.getDecisionTrackerContainer(); 	    	
 	    	usbongAnswerContainer = myUdteaObject.getUsbongAnswerContainer();
 	    	usbongNodeContainer = myUdteaObject.getUsbongNodeContainer();

 	    	usbongAnswerContainerCounter = usbongAnswerContainer.size()-1;
 	    	usbongNodeContainerCounter = usbongNodeContainer.size()-1;
 	    	
	 	    Log.d(">>>>",usbongNodeContainerCounter+""); 	    		

 	    	for (int i=0; i<usbongNodeContainer.size(); i++) {
 	 	    	Log.d(">>>>",usbongNodeContainer.elementAt(i)); 	    		
 	    	}
 	    	 	    	
        	Bundle bundle = getIntent().getExtras();
        	String buttonPressed = bundle.getString("buttonPressed");
        	
        	Log.d(">>>> buttonPressed",buttonPressed);
        	
        	if (buttonPressed.equals("back")) {
        		processBackButtonPressed();
        	}
        	else {
        		processNextButtonPressed();
        	}	    	
//			initParser(myTree);
 	    }
 	    //added by Mike, 20151130
 	    else if(getIntent().getStringExtra(UsbongConstants.FROM_MY_YOUTUBE_ACTIVITY_TO_MAIN_MENU) != null) {
            processReturnToMainMenuActivityYes();
    	}    		
    	else {			   		  
  		   Log.d(">>>>>>","else");

    		myUdteaObject = new UdteaObject(); //added by Mike, 20151126
	    	
            Handler mainHandler = new Handler(getInstance().getBaseContext().getMainLooper());
            Runnable myRunnable = new Runnable() {
            	@Override
            	public void run() {
            		initTreeLoader();
            	}
            };
            mainHandler.post(myRunnable);
	    }
/*        
				//added by Mike, 30 April 2015
				isInTreeLoader=false;		
				myTree = UsbongUtils.DEFAULT_UTREE_TO_LOAD; //edited by Mike, 20160418

				UsbongUtils.clearTempFolder();
*/		        
    }

    
    //added by Mike, 29 Sept. 2015
    //Reference: http://stackoverflow.com/questions/13017122/how-to-show-progressdialog-across-launching-a-new-activity;
    //last accessed: 29 Sept. 2015; answer by: Slartibartfast, 23 Oct. 2012
    class MyBackgroundTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			Log.d(">>>>","onPreExectue()");
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			Log.d(">>>>","onPostExecute()");
	    	new Thread(new Runnable() {
			    public void run() {
/*			    	
			    	while (!UsbongUtils.hasLoadedPurchaseLanguageBundleList) {
			            android.os.SystemClock.sleep(3000); 						    		
			            Log.d(">>>","sleeping");
			    	}
*/			    	
		            Log.d(">>>","DONE!");
		            Handler mainHandler = new Handler(getInstance().getBaseContext().getMainLooper());
		            Runnable myRunnable = new Runnable() {
		            	@Override
		            	public void run() {
						    instance.initTreeLoader(); //edited by Mike, 20160510
 						    if (instance.myProgressDialog != null) {
						        instance.myProgressDialog.dismiss();
						    }				            		
		            	}
		            };
		            mainHandler.post(myRunnable);
		    		return; //end this background thread
			    }
			}).start();
/*
		    initParser();
		    if (instance.myProgressDialog != null) {
		        instance.myProgressDialog.dismiss();
		    }		
*/		    
		}
		
		@Override
		protected Boolean doInBackground(String... params) {		
			Log.d(">>>>","doInBackground()");
			init();
		    //Do all your slow tasks here but don't set anything on UI
		    //ALL UI activities on the main thread 		
		    return true;
		
		}		
	}
    
/*    
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
*/
    public class MyOnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent,
            View view, int pos, long id) {
/*
        	Toast.makeText(parent.getContext(), "The month is " +
              parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
*/              
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
        
    }
    
	public void initTreeLoader()
	{
		setContentView(R.layout.tree_list_interface);				

		isInTreeLoader=true;
		
		listOfTreesArrayList = UsbongUtils.getTreeArrayList(UsbongUtils.USBONG_TREES_FILE_PATH);
		
		mCustomAdapter = new CustomDataAdapter(this, R.layout.tree_loader, listOfTreesArrayList);
		//Reference: http://stackoverflow.com/questions/8908549/sorting-of-listview-by-name-of-the-product-using-custom-adaptor;
		//last accessed: 2 Jan. 2014; answer by Alex Lockwood
		mCustomAdapter.sort(new Comparator<String>() {
		    public int compare(String arg0, String arg1) {
		        return arg0.compareTo(arg1);
		    }
		});
		
		treesListView = (ListView)findViewById(R.id.tree_list_view);
		treesListView.setLongClickable(true);
		treesListView.setAdapter(mCustomAdapter);

    	String pleaseMakeSureThatXMLTreeExistsString = (String) getResources().getText(R.string.pleaseMakeSureThatXMLTreeExistsString);
    	String alertString = (String) getResources().getText(R.string.alertStringValueEnglish);

		if (listOfTreesArrayList.isEmpty()){
        	new AlertDialog.Builder(UsbongDecisionTreeEngineActivity.this).setTitle(alertString)
			.setMessage(pleaseMakeSureThatXMLTreeExistsString)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {
		    		finish();    
					Intent toUsbongMainActivityIntent = new Intent(UsbongDecisionTreeEngineActivity.this, UsbongMainActivity.class);
					toUsbongMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
					startActivity(toUsbongMainActivityIntent);
				}
			}).show();	        		        	
		  }		
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Log.d(">>>>", "onCreateOptionsMenu");
		
		if (!isInTreeLoader) {
			Log.d(">>>>", "inside !inInTreeLoader");
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.speak_and_set_language_menu, menu);
			return super.onCreateOptionsMenu(menu); //added by Mike, 22 Sept. 2015
//			return true;
		}/*
		else {
			Log.d(">>>>", "this.menu=menu;");
			UsbongDecisionTreeEngineActivity.menu = menu;
		}*/
		return false;
	}
			
	public void initSetLanguage() {
//		UsbongUtils.checkForInAppOwnedItems(getInstance());
		
//		final Dialog dialog = new Dialog(this);
		setLanguageDialog = new AlertDialog.Builder(this);
		// Get the layout inflater
//    	LayoutInflater inflater = this.getLayoutInflater();
		setLanguageDialog.setTitle("Select Language");

		arrayAdapter = new ArrayAdapter<String>(
		        this,
		        android.R.layout.simple_list_item_single_choice) {
//			private AlertDialog myAlertDialog;
			@Override
		    public boolean isEnabled(int position) {
				if (UsbongUtils.isLanguageIsAnException(this.getItem(position).toString())) {
					return true;
				}

				if (!UsbongUtils.hasUnlockedLocalLanguages) {
					if (UsbongUtils.isLocalLanguage(this.getItem(position).toString())) {
						if ((purchaseLanguageListAlertDialog==null) || (!purchaseLanguageListAlertDialog.isShowing())) {
							//added by Mike, 20160508
							mySetLanguageAlertDialog.dismiss();
//							myAlertDialog = purchaseLanguagesListDialog.show();						
							purchaseLanguageListAlertDialog = purchaseLanguagesListDialog.show();						
						}
						return false;
					}
				}
				
				if (!UsbongUtils.hasUnlockedForeignLanguages) {
					//if it is not a local language, then it is a foreign language
					if (!UsbongUtils.isLocalLanguage(this.getItem(position).toString())) {
						if ((purchaseLanguageListAlertDialog==null) || (!purchaseLanguageListAlertDialog.isShowing())) {
							//added by Mike, 20160508
							mySetLanguageAlertDialog.dismiss();
//							myAlertDialog = purchaseLanguagesListDialog.show();
							purchaseLanguageListAlertDialog = purchaseLanguagesListDialog.show();						
						}
						return false;
					}
				}
		    	return true;
		    }
		};				
		
        ArrayList<String> myTransArrayList = UsbongUtils.getAvailableTranslationsArrayList(myTree);

		        if (myTransArrayList==null) {
		        	myTransArrayList = new ArrayList<String>();
		        }
		        //add the language setting of the xml tree to the list
		        myTransArrayList.add(0, UsbongUtils.getDefaultLanguage());
		        final int myTransArrayListSize = myTransArrayList.size();	        
/*		        
				for (int i = 0; i < myTransArrayListSize; i++) {
				    arrayAdapter.add(myTransArrayList.get(i));				    
				}
*/
        //make sure is empty every time 
        //added by Mike, 20160427
        arrayAdapter.clear();
        
		for (int i = 0; i < myTransArrayListSize; i++) {
			if (UsbongUtils.isLanguageIsAnException(myTransArrayList.get(i))) {
				arrayAdapter.add(myTransArrayList.get(i));				    
				continue;
			}
			else {
				if (!UsbongUtils.hasUnlockedLocalLanguages) {
					if (UsbongUtils.isLocalLanguage(myTransArrayList.get(i))) {
						arrayAdapter.add(myTransArrayList.get(i)+" (Locked)");				    								
						continue;
					}
				}
				else {
					if (UsbongUtils.isLocalLanguage(myTransArrayList.get(i))) {
						arrayAdapter.add(myTransArrayList.get(i));				    								
						continue;
					}							
				}
				
				if (!UsbongUtils.hasUnlockedForeignLanguages) {
					if (!UsbongUtils.isLocalLanguage(myTransArrayList.get(i))) {
						arrayAdapter.add(myTransArrayList.get(i)+" (Locked)");				    								
						continue;							
					}
				}
				else {
					if (!UsbongUtils.isLocalLanguage(myTransArrayList.get(i))) {
						arrayAdapter.add(myTransArrayList.get(i));				    								
						continue;							
					}							
				}
			}
		}
		
		//20160420				
		// Unlock Languages button
		setLanguageDialog.setPositiveButton("Unlock Languages",
		        new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
						purchaseLanguageListAlertDialog = purchaseLanguagesListDialog.show();
		            	dialog.dismiss();
		            }
		        });
		// cancel button
		setLanguageDialog.setNegativeButton("Cancel",
		        new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		                dialog.dismiss();
		            }
		        });
		setLanguageDialog.setSingleChoiceItems(arrayAdapter,currSelectedItemForSetLanguage,//setAdapter(arrayAdapter,
		        new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		                Log.i("Selected Item : ", arrayAdapter.getItem(which));
		                currSelectedItemForSetLanguage = which;
		                
						UsbongUtils.setLanguage(arrayAdapter.getItem(currSelectedItemForSetLanguage));

						currLanguageBeingUsed = UsbongUtils.getLanguageID(UsbongUtils.getSetLanguage());
						UsbongUtils.setCurrLanguage(UsbongUtils.getSetLanguage()); //added by Mike, 22 Sept. 2015
						
						//added by Mike, 4 June 2015
						//remove the current element in the node container and start anew
						//so that when end-user presses back, the previous screen will appear,
						//and not cause the same screen to reappear.
						if (!usbongNodeContainer.isEmpty()) {
							usbongNodeContainer.removeElementAt(usbongNodeContainerCounter);                            
			                usbongNodeContainerCounter--;
						}						
						initParser();
		                dialog.dismiss();
		            }
		        });
		mySetLanguageAlertDialog = setLanguageDialog.show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{		
		if (mTts.isSpeaking()) {
			mTts.stop();
		}
		
		StringBuffer sb = new StringBuffer();
		switch(item.getItemId())
		{
			case(R.id.set_language):	
				initSetLanguage();
				//refresh the menu options
				supportInvalidateOptionsMenu(); //added by Mike, 20160507
				invalidateOptionsMenu();
				return true;
			case(R.id.speak):
				processSpeak(sb);
				return true;
			case(R.id.settings):
			//Reference: http://stackoverflow.com/questions/16954196/alertdialog-with-checkbox-in-android;
			//last accessed: 20160408; answer by: kamal; edited by: Empty2K12
			final CharSequence[] items = {UsbongConstants.AUTO_NARRATE_STRING, UsbongConstants.AUTO_PLAY_STRING, UsbongConstants.AUTO_LOOP_STRING};
			// arraylist to keep the selected items
			selectedSettingsItems=new ArrayList<Integer>();
			
			//check saved settings
			if (UsbongUtils.IS_IN_AUTO_NARRATE_MODE) {					
				selectedSettingsItems.add(UsbongConstants.AUTO_NARRATE);			
			}
			if (UsbongUtils.IS_IN_AUTO_PLAY_MODE) {
				selectedSettingsItems.add(UsbongConstants.AUTO_PLAY);	
				selectedSettingsItems.add(UsbongConstants.AUTO_NARRATE); //if AUTO_PLAY is checked, AUTO_NARRATE should also be checked
	    	}	        				
			if (UsbongUtils.IS_IN_AUTO_LOOP_MODE) {					
				selectedSettingsItems.add(UsbongConstants.AUTO_LOOP);			
			}
		    
		    selectedSettingsItemsInBoolean = new boolean[items.length];
		    for(int k=0; k<items.length; k++) {
	    		selectedSettingsItemsInBoolean[k] = false;			    		
		    }
		    for(int i=0; i<selectedSettingsItems.size(); i++) {
	    		selectedSettingsItemsInBoolean[selectedSettingsItems.get(i)] = true;
		    }
		    		    
			inAppSettingsDialog = new AlertDialog.Builder(this)
			.setTitle("Settings")
			.setMultiChoiceItems(items, selectedSettingsItemsInBoolean, new DialogInterface.OnMultiChoiceClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
			    	Log.d(">>>","onClick");

				    	if (isChecked) {
				            // If the user checked the item, add it to the selected items
				            selectedSettingsItems.add(indexSelected);
				            if ((indexSelected==UsbongConstants.AUTO_PLAY) 
					        		&& !selectedSettingsItems.contains(UsbongConstants.AUTO_NARRATE)) {
				                final ListView list = inAppSettingsDialog.getListView();
				                list.setItemChecked(UsbongConstants.AUTO_NARRATE, true);
				            }				           
				        } else if (selectedSettingsItems.contains(indexSelected)) {
				        	if ((indexSelected==UsbongConstants.AUTO_NARRATE) 
				        		&& selectedSettingsItems.contains(UsbongConstants.AUTO_PLAY)) {
				                final ListView list = inAppSettingsDialog.getListView();
				                list.setItemChecked(indexSelected, false);
				        	}
				        	else {        	
					            // Else, if the item is already in the array, remove it
					            selectedSettingsItems.remove(Integer.valueOf(indexSelected));
				        	}
				        }
				        
				        //updated selectedSettingsItemsInBoolean
					    for(int k=0; k<items.length; k++) {
				    		selectedSettingsItemsInBoolean[k] = false;			    		
					    }
					    for(int i=0; i<selectedSettingsItems.size(); i++) {
				    		selectedSettingsItemsInBoolean[selectedSettingsItems.get(i)] = true;
					    }
				    }
				}).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int id) {
				    	 try {	    	
				 			InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config");	
				 			BufferedReader br = new BufferedReader(reader);    		
				 	    	String currLineString;        	

			 	    	//write first to a temporary file
						PrintWriter out = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.BASE_FILE_PATH + "usbong.config" +"TEMP");

				 	    	while((currLineString=br.readLine())!=null)
				 	    	{ 	
				 	    		Log.d(">>>", "currLineString: "+currLineString);
								if ((currLineString.contains("IS_IN_AUTO_NARRATE_MODE="))
								|| (currLineString.contains("IS_IN_AUTO_PLAY_MODE="))
								|| (currLineString.contains("IS_IN_AUTO_LOOP_MODE="))) {
									continue;
								}	
								else {
									out.println(currLineString);			 	    		
								}
				 	    	}	        				

							for (int i=0; i<items.length; i++) {
								Log.d(">>>>", i+"");
								if (selectedSettingsItemsInBoolean[i]==true) {
									if (i==UsbongConstants.AUTO_NARRATE) {
							    		out.println("IS_IN_AUTO_NARRATE_MODE=ON");
							    		UsbongUtils.IS_IN_AUTO_NARRATE_MODE=true;							
									}								
									else if (i==UsbongConstants.AUTO_PLAY) {
							    		out.println("IS_IN_AUTO_PLAY_MODE=ON");
							    		UsbongUtils.IS_IN_AUTO_PLAY_MODE=true;						
	/*						    		
							    		//if auto_play is ON, auto_narrate is also ON
							    		//however, it is possible to have auto_play OFF,
							    		//while auto_narrate is ON
							    		out.println("IS_IN_AUTO_NARRATE_MODE=ON");
							    		UsbongUtils.IS_IN_AUTO_NARRATE_MODE=true;
	*/						    									
									}	
									else if (i==UsbongConstants.AUTO_LOOP) {
							    		out.println("IS_IN_AUTO_LOOP_MODE=ON");
							    		UsbongUtils.IS_IN_AUTO_LOOP_MODE=true;						
									}
								}
								else {
									if (i==UsbongConstants.AUTO_NARRATE) {
							    		out.println("IS_IN_AUTO_NARRATE_MODE=OFF");
							    		UsbongUtils.IS_IN_AUTO_NARRATE_MODE=false;															
									}							
									else if (i==UsbongConstants.AUTO_PLAY) {
							    		out.println("IS_IN_AUTO_PLAY_MODE=OFF");
							    		UsbongUtils.IS_IN_AUTO_PLAY_MODE=false;	
									}
									else if (i==UsbongConstants.AUTO_LOOP) {
							    		out.println("IS_IN_AUTO_LOOP_MODE=OFF");
							    		UsbongUtils.IS_IN_AUTO_LOOP_MODE=false;	
									}
								}				
							}					
					    	out.close(); //remember to close
					    	
					    	//copy temp file to actual usbong.config file
				 			InputStreamReader reader2 = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config"+"TEMP");	
				 			BufferedReader br2 = new BufferedReader(reader2);    		
				 	    	String currLineString2;        	

			 	    	//write to actual usbong.config file
						PrintWriter out2 = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.BASE_FILE_PATH + "usbong.config");

				 	    	while((currLineString2=br2.readLine())!=null)
				 	    	{ 	
								out2.println(currLineString2);			 	    		
				 	    	}			 	    	
				 	    	out2.close();
				 	    	
				 	    	UsbongUtils.deleteRecursive(new File(UsbongUtils.BASE_FILE_PATH + "usbong.config"+"TEMP"));
				 		}
				 		catch(Exception e) {
				 			e.printStackTrace();
				 		}			 		
				    }
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int id) {
				        //  Your code when user clicked on Cancel
				    }
				}).create();
				inAppSettingsDialog.show();
	/*			
			    	new AlertDialog.Builder(UsbongDecisionTreeEngineActivity.this).setTitle("Settings")
					.setMessage("Automatic voice-over narration:")
//					.setView(requiredFieldAlertStringTextView)
			    	.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							UsbongUtils.isInAutoVoiceOverNarration=true;
						}
			    	})
				    .setNegativeButton("Turn Off", new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							UsbongUtils.isInAutoVoiceOverNarration=false;
						}
					}).show();
	*/				
				return true;
			case(R.id.about):
				new AlertDialog.Builder(UsbongDecisionTreeEngineActivity.this).setTitle("About")
				.setMessage(UsbongUtils.readTextFileInAssetsFolder(UsbongDecisionTreeEngineActivity.this,"credits.txt")) //don't add a '/', otherwise the file would not be found
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();				
				return true;
			case android.R.id.home: //added by Mike, 22 Sept. 2015
	        	processReturnToMainMenuActivity();
		        return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	//added by Mike, 24 Sept. 2015
	@Override
	public void onStop() {
	    if (mTts != null) {
	        mTts.stop();
	    }
	    super.onStop();
	}
	
	//added by Mike, 25 Sept. 2015
	public void processPlayBGMusic() {
		try {
			String newCurrUsbongBGAudioString = UsbongUtils.getBGAudioFilePathForThisScreenIfAvailable(currUsbongNode);
			Log.d(">>>>newCurrUsbongBGAudioString: ",""+newCurrUsbongBGAudioString);
			Log.d(">>>>currUsbongBGAudioString: ",""+currUsbongBGAudioString);

			if (currUsbongBGAudioString==newCurrUsbongBGAudioString) {
				return;
			}
			else {
				Log.d(">>>>", "inside currUsbongBGAudioString!=newCurrUsbongBGAudioString");
				currUsbongBGAudioString = newCurrUsbongBGAudioString;
//				myBGMediaPlayer.stop();

				String filePath=UsbongUtils.getBGAudioFilePathFromUTree(currUsbongBGAudioString);
		//			Log.d(">>>>filePath: ",filePath);
				if (filePath!=null) {
					Log.d(">>>>", "inside filePath!=null");
					Log.d(">>>>filePath: ",filePath);
					if (myBGMediaPlayer.isPlaying()) {
						myBGMediaPlayer.stop();
					}
					myBGMediaPlayer.reset();
					//edited by Mike, 20151201
//					myBGMediaPlayer.setDataSource(filePath);
					FileInputStream fis = new FileInputStream(new File(filePath));
					myBGMediaPlayer.setDataSource(fis.getFD());
					fis.close();

					myBGMediaPlayer.prepare();
		//				myMediaPlayer.setVolume(1.0f, 1.0f);
					myBGMediaPlayer.setLooping(true);
					myBGMediaPlayer.start();
		//				myMediaPlayer.seekTo(0);
				}			
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void processSpeak(StringBuffer sb) {
		if (mTts.isSpeaking()) { //commented out by Mike, 24 Sept. 2015
			mTts.stop();
		}

//		Log.d(">>>>currScreen",currScreen+"");
		switch(currScreen) {
			//edit later, Mike, Sept. 26, 2013
			case UsbongConstants.SIMPLE_ENCRYPT_SCREEN:
				break;
			//edit later, Mike, May 23, 2013
			case UsbongConstants.DCAT_SUMMARY_SCREEN:
				break;
				
	    	case UsbongConstants.LINK_SCREEN:
	    	case UsbongConstants.MULTIPLE_RADIO_BUTTONS_SCREEN:
	    	case UsbongConstants.MULTIPLE_RADIO_BUTTONS_WITH_ANSWER_SCREEN:
		        sb.append(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(this), UsbongUtils.IS_TEXTVIEW, currUsbongNode)).getText().toString()+". ");

		        int totalRadioButtonsInContainer = radioButtonsContainer.size();
		        for (int i=0; i<totalRadioButtonsInContainer; i++) {
			        sb.append(((RadioButton) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new RadioButton(this), UsbongUtils.IS_RADIOBUTTON, radioButtonsContainer.elementAt(i))).getText().toString()+". ");
		        }		     		        
				break;
	    	case UsbongConstants.MULTIPLE_CHECKBOXES_SCREEN:
		        sb.append(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(this), UsbongUtils.IS_TEXTVIEW, currUsbongNode)).getText().toString()+". ");

		        int totalCheckBoxesInContainer = checkBoxesContainer.size();
		        for (int i=0; i<totalCheckBoxesInContainer; i++) {
			        sb.append(((CheckBox) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new CheckBox(this), UsbongUtils.IS_CHECKBOX, checkBoxesContainer.elementAt(i))).getText().toString()+". ");
		        }		     		        
		        break;
	    	case UsbongConstants.AUDIO_RECORD_SCREEN:
		        sb.append(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(this), UsbongUtils.IS_TEXTVIEW, currUsbongNode)).getText().toString()+". ");

		        Button recordButton = (Button)findViewById(R.id.record_button);
		        Button stopButton = (Button)findViewById(R.id.stop_button);
		        Button playButton = (Button)findViewById(R.id.play_button);

		        sb.append(recordButton.getText()+". ");
		        sb.append(stopButton.getText()+". ");
		        sb.append(playButton.getText()+". ");
		        break;
			case UsbongConstants.PAINT_SCREEN:
	    		sb.append(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(this), UsbongUtils.IS_TEXTVIEW, currUsbongNode)).getText().toString()+". ");

	    		Button paintButton = (Button)findViewById(R.id.paint_button);
		        sb.append(paintButton.getText()+". ");
	    		break;
			case UsbongConstants.PHOTO_CAPTURE_SCREEN:
	    		sb.append(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(this), UsbongUtils.IS_TEXTVIEW, currUsbongNode)).getText().toString()+". ");

	    		Button photoCaptureButton = (Button)findViewById(R.id.photo_capture_button);
		        sb.append(photoCaptureButton.getText()+". ");
	    		break;
			case UsbongConstants.TEXTFIELD_SCREEN:
			case UsbongConstants.TEXTFIELD_WITH_UNIT_SCREEN:
			case UsbongConstants.TEXTFIELD_NUMERICAL_SCREEN:
			case UsbongConstants.TEXTAREA_SCREEN:
				sb.append(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(this), UsbongUtils.IS_TEXTVIEW, currUsbongNode)).getText().toString()+". ");
		        break;    	
			case UsbongConstants.TEXTFIELD_WITH_ANSWER_SCREEN:						
			case UsbongConstants.TEXTAREA_WITH_ANSWER_SCREEN:						
				sb.append(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(this), UsbongUtils.IS_TEXTVIEW, currUsbongNodeWithoutAnswer)).getText().toString()+". ");
		        break;    	
			case UsbongConstants.CLASSIFICATION_SCREEN:
		        sb.append(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(this), UsbongUtils.IS_TEXTVIEW, currUsbongNode)).getText().toString()+". ");

		        int totalClassificationsInContainer = classificationContainer.size();
		        for (int i=0; i<totalClassificationsInContainer; i++) {
			        sb.append(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(this), UsbongUtils.IS_TEXTVIEW, classificationContainer.elementAt(i))).getText().toString()+". ");
		        }		     		        
		        break;    	
			case UsbongConstants.DATE_SCREEN:				       
			case UsbongConstants.TEXT_DISPLAY_SCREEN:
			case UsbongConstants.TEXT_IMAGE_DISPLAY_SCREEN:
			case UsbongConstants.IMAGE_TEXT_DISPLAY_SCREEN:
			case UsbongConstants.CLICKABLE_IMAGE_TEXT_DISPLAY_SCREEN:				       
			case UsbongConstants.TEXT_CLICKABLE_IMAGE_DISPLAY_SCREEN:				       
			case UsbongConstants.GPS_LOCATION_SCREEN:
			case UsbongConstants.QR_CODE_READER_SCREEN:
			case UsbongConstants.TIMESTAMP_DISPLAY_SCREEN:						
			case UsbongConstants.VIDEO_FROM_FILE_WITH_TEXT_SCREEN:							
			case UsbongConstants.YOUTUBE_VIDEO_WITH_TEXT_SCREEN:							
				sb.append(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(this), UsbongUtils.IS_TEXTVIEW, currUsbongNode)).getText().toString()+". ");
//		        Log.d(">>>>sb",sb.toString());
		        break;
			case UsbongConstants.CLICKABLE_IMAGE_DISPLAY_SCREEN:				       
			case UsbongConstants.IMAGE_DISPLAY_SCREEN:
			case UsbongConstants.VIDEO_FROM_FILE_SCREEN:							
			case UsbongConstants.YOUTUBE_VIDEO_SCREEN:											
		        break;    	
			case UsbongConstants.YES_NO_DECISION_SCREEN:
			case UsbongConstants.SEND_TO_WEBSERVER_SCREEN:
			case UsbongConstants.SEND_TO_CLOUD_BASED_SERVICE_SCREEN:
		        sb.append(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(this), UsbongUtils.IS_TEXTVIEW, currUsbongNode)).getText().toString()+". ");
		        sb.append(yesStringValue+". ");
		        sb.append(noStringValue+". ");
		        break;    	
/*						
			case UsbongConstants.PAINT_SCREEN:
		    	if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
					sb.append((String) getResources().getText(R.string.UsbongPaintScreenTextViewFILIPINO));
		    	}
		    	else if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
					sb.append((String) getResources().getText(R.string.UsbongPaintScreenTextViewJAPANESE));				    						    		
		    	}
		    	else { //if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
					sb.append((String) getResources().getText(R.string.UsbongPaintScreenTextViewENGLISH));				    						    		
		    	}
		    	break;    		
*/				    	
/*			//commented out by Mike, 20160213	        
			case UsbongConstants.END_STATE_SCREEN:
		    	if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
					sb.append((String) getResources().getText(R.string.UsbongEndStateTextViewFILIPINO));				    		
		    	}
		    	else if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
					sb.append((String) getResources().getText(R.string.UsbongEndStateTextViewJAPANESE));				    						    		
		    	}
		    	else { //if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
					sb.append((String) getResources().getText(R.string.UsbongEndStateTextViewENGLISH));				    						    		
		    	}
		    	break;    		
*/		    	
		}
		//edited by Mike, 21 July 2015
		try {
			
			currUsbongAudioString = UsbongUtils.getAudioFilePathForThisScreenIfAvailable(currUsbongNode);
			
			Log.d(">>>>currUsbongAudioString: ",""+currUsbongAudioString);
			Log.d(">>>>currLanguageBeingUsed: ",UsbongUtils.getLanguageBasedOnID(currLanguageBeingUsed));

			//added by Mike, 2 Oct. 2015
			//exception for Mandarin
			//make simplified and traditional refer to the same audio folder
			if ((currLanguageBeingUsed==UsbongUtils.LANGUAGE_MANDARIN_SIMPLIFIED) ||
					(currLanguageBeingUsed==UsbongUtils.LANGUAGE_MANDARIN_TRADITIONAL)) {
				currLanguageBeingUsed=UsbongUtils.LANGUAGE_MANDARIN;
			}
			
			String filePath=UsbongUtils.getAudioFilePathFromUTree(currUsbongAudioString, UsbongUtils.getLanguageBasedOnID(currLanguageBeingUsed));
//			Log.d(">>>>filePath: ",filePath);
			if (filePath!=null) {
				Log.d(">>>>", "inside filePath!=null");
				Log.d(">>>>filePath: ",filePath);
				if (myMediaPlayer.isPlaying()) {
					myMediaPlayer.stop();
				}
				myMediaPlayer.reset();
				//edited by Mike, 20151201
//				myMediaPlayer.setDataSource(filePath);
				FileInputStream fis = new FileInputStream(new File(filePath));
				myMediaPlayer.setDataSource(fis.getFD());
				fis.close();

				myMediaPlayer.prepare();
//				myMediaPlayer.setVolume(1.0f, 1.0f);
				myMediaPlayer.start();
//				myMediaPlayer.seekTo(0);
				
				//added by Mike, 20160408
				myMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
		            public void onCompletion(MediaPlayer mp) {
		            	//added by Mike, 20160608
		            	if ((UsbongUtils.IS_IN_AUTO_PLAY_MODE) &&
		            			(UsbongUtils.isAnAutoPlayException(instance))){
		            		processNextButtonPressed();
		            	}
		            }
		        });
			}
			else {
				//it's either com.svox.pico (default) or com.svox.classic (Japanese, etc)  
//commented out by Mike, 11 Oct. 2015
//				mTts.setEngineByPackageName("com.svox.pico"); //note: this method is already deprecated
				
				mTtsParams = new HashMap<String, String>();
				mTtsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UsbongConstants.MY_UTTERANCE_ID);
				
				switch (currLanguageBeingUsed) {
					case UsbongUtils.LANGUAGE_FILIPINO:				    
						mTts.setLanguage(new Locale("spa", "ESP"));
						if (Build.VERSION.RELEASE.startsWith("5")) { 
							mTts.speak(UsbongUtils.convertFilipinoToSpanishAccentFriendlyText(sb.toString()), TextToSpeech.QUEUE_FLUSH, null, UsbongConstants.MY_UTTERANCE_ID); //QUEUE_ADD			
						}
						else {
							mTts.speak(UsbongUtils.convertFilipinoToSpanishAccentFriendlyText(sb.toString()), TextToSpeech.QUEUE_FLUSH, mTtsParams); //QUEUE_ADD										
						}
						break;
					case UsbongUtils.LANGUAGE_JAPANESE:
//						commented out by Mike, 11 Oct. 2015
//						mTts.setEngineByPackageName("com.svox.classic"); //note: this method is already deprecated
						mTts.setLanguage(new Locale("ja", "JP"));
						if (Build.VERSION.RELEASE.startsWith("5")) { 
							mTts.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, null, UsbongConstants.MY_UTTERANCE_ID); //QUEUE_ADD			
							
						}
						else {
							mTts.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, mTtsParams); //QUEUE_ADD			
						}
						break;						
					/*case UsbongUtils.LANGUAGE_ENGLISH:*/
					default:
						Log.d(">>>>sb.toString()",sb.toString());
						mTts.setLanguage(new Locale("en", "US"));
						if (Build.VERSION.RELEASE.startsWith("5")) { 
							mTts.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, null, UsbongConstants.MY_UTTERANCE_ID); //QUEUE_ADD			
						}
						else {
							mTts.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, mTtsParams); //QUEUE_ADD										
						}
						break;
/*						
					default:
						mTts.setLanguage(new Locale("en", "US"));
						mTts.speak(sb.toString(), TextToSpeech.QUEUE_ADD, null); //QUEUE_FLUSH			
						break;
*/						
				}
				
				//added by Mike, 20160410
				mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
					@Override
					public void onDone(String utteranceId) {
		            	if (utteranceId.equals(UsbongConstants.MY_UTTERANCE_ID)) {
		            		//added by Mike, 20160608
		            		if ((UsbongUtils.IS_IN_AUTO_PLAY_MODE) &&
			            			(UsbongUtils.isAnAutoPlayException(instance))){
			            		instance.runOnUiThread(new Runnable() {
			            		     @Override
			            		     public void run() {
						            	processNextButtonPressed();
			            		    }
			            		});			            		
			            	}						
		            	}
					}

					@Override
					@Deprecated
					public void onError(String utteranceId) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStart(String utteranceId) {
						// TODO Auto-generated method stub						
					}
		        });
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}					
	}
	
	@Override
	public void onInit(int status) {
		//answer from Eternal Learner, stackoverflow
		//Reference: http://stackoverflow.com/questions/9473070/texttospeech-setenginebypackagename-doesnt-set-anything;
		//last accessed: Nov. 7, 2012
		//mTts.getDefaultEngine() and mTts.setEngineByPackageName(...) can only be called only when onInit(...) is reached		
/*		
		String myEngine = mTts.getDefaultEngine();
        System.out.println(">>>>>>>>>>>>>>> myEngine: "+myEngine);

        //it's either com.svox.pico (default) or com.svox.classic (Japanese, etc)        
        mTts.setEngineByPackageName(myEngine); //note: this method is already deprecated
*/
	}

	@Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
    	Log.d(">>>>>requestCode", ""+requestCode);

    	if (requestCode == UsbongUtils.MY_DATA_CHECK_CODE) {
    		/*
        	if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
*/
        	if (mTts==null) {
                Intent installIntent = new Intent();
                installIntent.setAction(
                TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);        		        	
            }
//			commented out by Mike, 11 Oct. 2015
//        	mTts = new TextToSpeech(this, this);        		
        }
    	/*
    	 * //considering commenting this out, Mike, 20161122
    	 */
        else if (requestCode==UsbongUtils.EMAIL_SENDING_SUCCESS) {
    		finish();    		
			Intent toUsbongMainActivityIntent = new Intent(UsbongDecisionTreeEngineActivity.this, UsbongMainActivity.class);
			toUsbongMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(toUsbongMainActivityIntent);
    		if (mTts!=null) {
    			mTts.shutdown();
    		}
        }
        /*//requestCode seems to be always 0
        else if (requestCode==UsbongUtils.FROM_MY_YOUTUBE_ACTIVITY_TO_MAIN_MENU) {
        	Log.d(">>>>>", "requestCode==UsbongUtils.FROM_MY_YOUTUBE_ACTIVITY_TO_MAIN_MENU");
        	processReturnToMainMenuActivityYes();
        }*/
    }

    @Override
	public void onDestroy() {
    	super.onDestroy(); //added by Mike, 7 Aug. 2015
		if (mTts!=null) {
			mTts.shutdown();
		}
		if (myMediaPlayer!=null) {
			myMediaPlayer.release();			
		}
		if (myBGMediaPlayer!=null) {
			myBGMediaPlayer.release();			
		}
		//added by Mike, 20160123
		if (mService != null) {
	        unbindService(mServiceConn);
	    }
	}
	
    public static UsbongDecisionTreeEngineActivity getInstance() {
    	return instance;
    }
    
    public static void setMyIntent(Intent i) {
      getInstance().setIntent(i);
    }
    
    public void setCurrScreen(int cs) {
    	currScreen=cs;
    }
    
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (getParent()!=null) { 
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	        	processReturnToMainMenuActivity();
	        	return false;
	        }
    	}
        return super.onKeyDown(keyCode, event);
    }
    */
    
    //added by Mike, Feb. 2, 2013
    @Override
	public void onBackPressed() {
    	processReturnToMainMenuActivity();  
    }
    
    @Override
    public void onRestart() 
    {
        super.onRestart();        

    	Log.d(">>>>>>", " onRestart()");
    	//added by Mike, Dec. 24, 2012
//    	Log.d(">>>>>> onActivityResult", "inside!!!");
        hasReturnedFromAnotherActivity=true; //camera, paint, email, etc

        switch(currScreen) {
        	case UsbongConstants.PHOTO_CAPTURE_SCREEN:
        		initTakePhotoScreen();
        		break;
        	case UsbongConstants.YOUTUBE_VIDEO_SCREEN:
        	case UsbongConstants.YOUTUBE_VIDEO_WITH_TEXT_SCREEN:
/*        		
            	Bundle bundle = getIntent().getExtras();
            	String buttonPressed = bundle.getString("buttonPressed");
            	currScreen = bundle.getInt("currScreen");

     	    	myUdteaObject = getIntent().getParcelableExtra((UsbongConstants.BUNDLE));
     	    	
     	    	Log.d(">>>>>","inside UsbongConstants.Bundle");
     	    	
     	    	myTree = myUdteaObject.getMyTree();
     	    	currUsbongNode = myUdteaObject.getCurrUsbongNode();
     	    	usbongAnswerContainerCounter = myUdteaObject.getUsbongAnswerContainerCounter();
     	    	usbongNodeContainerCounter = myUdteaObject.getUsbongNodeContainerCounter();
     	    	decisionTrackerContainer = myUdteaObject.getDecisionTrackerContainer(); 	    	
     	    	usbongAnswerContainer = myUdteaObject.getUsbongAnswerContainer();
     	    	usbongNodeContainer = myUdteaObject.getUsbongNodeContainer();

            	if (buttonPressed.equals("back")) {
            		backButton.performClick();
            	}
            	else {
            		nextButton.performClick();            		
            	}
            	return;
*/            	
        		break;
        		//added by Mike, 20161122
        	case UsbongConstants.SEND_TO_CLOUD_BASED_SERVICE_SCREEN:
        		return;
        }
        initParser(); 
    }

    @Override
    public void onPause() {
    	super.onPause();
    	if (myLocation!=null) {
    	  myLocation.cancelTimer();    	
    	}
    }
    
	public void parseYesNoAnswers(XmlPullParser parser) {
		try {
			if (parser.getName().equals("transition")) {				

				//check if the first transition's name is "No"
//				  if (parser.getAttributeValue(1).toString().equals(noStringValue)) {

				//if the edge or arrow doesn't have a label (e.g. Yes, No, Any), make it an "Any" as default
				 //added by Mike, Aug. 13, 2012
				if (parser.getAttributeValue(null, "name") == null) {
					  nextUsbongNodeIfYes = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();
					  nextUsbongNodeIfNo = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();						  							  							  					
				}
				 //added by Mike, March 8, 2012
				 //add the || just in case the language is Filipino, but we used "Yes/No" for the transitions
				 else if (parser.getAttributeValue(null,"name").equals(noStringValue)
						  || parser.getAttributeValue(null,"name").equals("No")) {
					  nextUsbongNodeIfNo = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();
					  //do two nextTag()'s, because <transition> has a closing tag
					  parser.nextTag();
					  parser.nextTag();						  
					  nextUsbongNodeIfYes = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();
				  }
//				  else if (parser.getAttributeValue(1).toString().equals(yesStringValue)) { // if it is "Yes"
				  else if (parser.getAttributeValue(null,"name").equals(yesStringValue)
				  		  || parser.getAttributeValue(null,"name").equals("Yes")){
				  	  nextUsbongNodeIfYes = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();
					  //do two nextTag()'s, because <transition> has a closing tag
					  parser.nextTag();
					  parser.nextTag();						  
					  nextUsbongNodeIfNo = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();						  							  
				  }
				  else { // if it is "Any"
					  nextUsbongNodeIfYes = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();
					  nextUsbongNodeIfNo = /*parser.getAttributeValue(null,"to").toString();*/parser.getAttributeValue(0).toString();						  							  							  
				  }				  
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	//added by Mike, 20151126
	public void storeDataToUdteaObject() {
//		Log.d(">>>>>", "storeDataToUdteaObject()");
		myUdteaObject.setMyTree(myTree); 
    	myUdteaObject.setCurrUsbongNode(currUsbongNode);
    	Log.d(">>>>>currUsbongNode", currUsbongNode);

    	myUdteaObject.setNextUsbongNodeIfYes(nextUsbongNodeIfYes);
    	myUdteaObject.setNextUsbongNodeIfNo(nextUsbongNodeIfNo);

    	myUdteaObject.setUsbongAnswerContainerCounter(usbongAnswerContainerCounter);
    	myUdteaObject.setUsbongNodeContainerCounter(usbongNodeContainerCounter);
    	myUdteaObject.setDecisionTrackerContainer(decisionTrackerContainer); 	    	
    	myUdteaObject.setUsbongAnswerContainer(usbongAnswerContainer);
    	myUdteaObject.setUsbongNodeContainer(usbongNodeContainer);
	}
    
	//added by Mike, 24 May 2015
	//@param: s is the name of the .utree file
	public void initParser(String s) {
		isInTreeLoader=false;		
		invalidateOptionsMenu(); //should be after isInTreeLoader=false; added by Mike, 24 Sept. 2015
		myTree = s;
		
		//added by Mike, 20160417
		if (mTts!=null) {
			mTts.stop(); 			
		} 
		//added by Mike, 20160417
		if (myMediaPlayer!=null) {
			myMediaPlayer.stop();
		} 

		UsbongUtils.clearTempFolder();		
		currUsbongNode=""; //added by Mike, 20160415
		
        initParser();
	}
	
	//Reference: 
	//http://wiki.forum.nokia.com/index.php/How_to_parse_an_XML_file_in_Java_ME_with_kXML ;Last accessed on: June 2,2010
	//http://kxml.sourceforge.net/kxml2/ ;Last accessed on: June 2,2010    
    //http://developer.android.com/reference/org/xmlpull/v1/XmlPullParser.html; last accessed on: Aug. 23, 2011
	public void initParser() {
//		hasReachedEndOfAllDecisionTrees=false;		
//			decisionTrackerContainer.addElement(usbongAnswerContainer.lastElement());
		Log.d(">>>>>", "initParser");
		
		if (wasNextButtonPressed){
			Log.d(">>>>>", "wasNextButtonPressed");
			
			if (!hasUpdatedDecisionTrackerContainer) {
				decisionTrackerContainer.addElement(usbongAnswerContainer.elementAt(usbongAnswerContainerCounter-1));
				hasUpdatedDecisionTrackerContainer=true;
			}
			
//			System.out.println(">>>>>>>>>> wasNextButtonPressed");
//			System.out.println(">>>>>>>>>> usbongAnswerContainerCounter: "+usbongAnswerContainerCounter);

			for (int i=0; i<usbongAnswerContainer.size(); i++) {
				System.out.println(i+": "+usbongAnswerContainer.elementAt(i));				
			}			
			
			if (usbongAnswerContainer.size()!=0) {
				UsbongUtils.processUsbongVariableAssignment(myUsbongVariableMemory, usbongAnswerContainer.elementAt(usbongAnswerContainer.size()-1));
			}
			
			if ((!usbongAnswerContainer.isEmpty()) && (usbongAnswerContainerCounter < usbongAnswerContainer.size())) {
				currAnswer = usbongAnswerContainer.elementAt(usbongAnswerContainerCounter);
//				System.out.println(">>>> loob currAnswer: "+currAnswer);				
			}
			else {
				currAnswer="";
			}
			
			wasNextButtonPressed=false;
		}
		Log.d(">>>>", "end of wasNextButtonPressed");
		
		try {
		  XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	      factory.setNamespaceAware(true);
		  XmlPullParser parser = factory.newPullParser();		 		  
		  
		  //if *.xml is blank
//		  if (UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + myTreeDirectory + myTree + ".xml") == null) { 
//		  UsbongUtils.getTreeFromSDCardAsReader(/*UsbongUtils.USBONG_TREES_FILE_PATH + */myTree));// + ".xml")		  
//		  if (!hasRetrievedTree) {
		  //edited by Mike, Sept. 10, 2014
//		  if (isr==null) {
//			  System.out.println("isr = null;");
			  isr = UsbongUtils.getTreeFromSDCardAsReader(myTree);

			  if (isr==null) {
				  Toast.makeText(getApplicationContext(), "Error loading: "+myTree, Toast.LENGTH_LONG).show();  
				  return;
			  }
//		  }
/*		  else {
			  System.out.println("isr not = null;"+isr.toString());
		  }
*/		  
//			  hasRetrievedTree=true;
//		  }		  
		  
		  parser.setInput(isr);	

			
		  //load hints (if there are any) and put them in a hashtable
		  //do this after isr = UsbongUtils.getTreeFromSDCardAsReader(myTree);
		  //because UsbongUtils.getTreeFromSDCardAsReader(myTree) unzips the .utree file into the temp folder
		  //added by Mike, 20160413
		  UsbongUtils.putHintsInHashtable(myTree);
		  
		  while(parser.nextTag() != XmlPullParser.END_DOCUMENT) {
			  //if this tag does not have an attribute; e.g. END_TAG
			  if (parser.getAttributeCount()==-1) {
				  continue;
			  }
			  //if this is the first process-definition tag
			  else if (parser.getAttributeCount()>1){ 
				  if ((currUsbongNode.equals("")) && (parser.getName().equals("process-definition"))) {
					  if (!isAutoLoopedTree) {
						  //@todo: remove this id thing, immediately use the String; otherwise it'll be cumbersome to keep on adding language ids
						  currLanguageBeingUsed=UsbongUtils.getLanguageID(parser.getAttributeValue(null, "lang"));
						  UsbongUtils.setDefaultLanguage(UsbongUtils.getLanguageBasedOnID(currLanguageBeingUsed));
						  UsbongUtils.setCurrLanguage(parser.getAttributeValue(null, "lang")); //added by Mike, 22 Sept. 2015
					  }
//					  System.out.println("currLanguageBeingUsed: "+currLanguageBeingUsed);
					  	
					  //added by Mike, Feb. 2, 2013
					  decisionTrackerContainer.removeAllElements();					  
				  }
				  continue;
			  }
			  else if (parser.getAttributeCount()==1) {
				  //if currUsbongNode is still blank
				  //getName() is not the name attribute, but the name of the tag (e.g. start-state, decision-node, task-node)
				  if ((currUsbongNode.equals("")) && (parser.getName().equals("start-state"))) {
					  //the next tag would be transition
					  //Example:
				      //<start-state name="start-state1">
					  //  <transition to="Is there tender swelling behind the ear?"></transition>
					  //</start-state>

					  //do two next()'s to skip the "\n", and proceed to <transition>...
//					  parser.next();
//					  parser.next();
					  //or do one nextTag();
					  parser.nextTag();
					  currUsbongNode=parser.getAttributeValue(0).toString();
					  
//					  isFirstQuestionForDecisionTree=true;

					  //added by Mike, Dec. 24, 2012
					  //reset myQRCodeContent to blank
					  myQRCodeContent="";
					  continue;
				  }
				  //edited by Mike, 21 July 2015
				  if ((!currUsbongNode.equals("")) && /*(parser.getAttributeValue(null,"name")!=null) && ((parser.getAttributeValue(null,"name").toString().equals(currUsbongNode))*/(parser.getAttributeValue(0).toString().equals(currUsbongNode))
						  && !(parser.getName().equals("transition"))) { //make sure that the tag is not a transition node 
				      if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
				    	noStringValue = (String) getResources().getText(R.string.noStringValueFilipino);
				    	yesStringValue = (String) getResources().getText(R.string.yesStringValueFilipino);
				      }
				      else if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
				    	noStringValue = (String) getResources().getText(R.string.noStringValueJapanese); //noStringValue
				    	yesStringValue = (String) getResources().getText(R.string.yesStringValueJapanese); //yesStringValue    		
					  }						  
				      else if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_MANDARIN) {
				    	noStringValue = (String) getResources().getText(R.string.noStringValueMandarin); //noStringValue
				    	yesStringValue = (String) getResources().getText(R.string.yesStringValueMandarin); //yesStringValue    		
					  }						  
				      else { //if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
				    	noStringValue = (String) getResources().getText(R.string.noStringValueEnglish); //noStringValue
				    	yesStringValue = (String) getResources().getText(R.string.yesStringValueEnglish); //yesStringValue    		
					  }						  

					  //if this is a decision tag and the next tag is transition...
					  //Example:
					  //<decision name="Is there tender swelling behind the ear?">
					  //  <transition to="Is there pus draining from the ear?" name="No"></transition>
					  //  <transition to="MASTOIDITIS" name="Yes"></transition>
					  //</decision>
					  if (parser.getName().equals("decision")) {//transition")) {//(parser.getAttributeCount()>1) {
						  currScreen=UsbongConstants.YES_NO_DECISION_SCREEN;
						  parser.nextTag(); //go to the next tag
						  parseYesNoAnswers(parser);
					  }
					  else if (parser.getName().equals("end-state")) { 
 						  currScreen=UsbongConstants.END_STATE_SCREEN;
 						  processNextButtonPressed();
 						  return;
					  }
					  else if (parser.getName().equals("task-node")) { 
						    StringTokenizer st = new StringTokenizer(currUsbongNode, "~");
							String myStringToken = st.nextToken();							

							if (myStringToken.equals(currUsbongNode)) {//if this is the task-node for classification and treatment/management plan								
								//<task-node name="SOME DEHYDRATION">
								//	<task name="Give fluid, zinc supplements and food for some dehydration (Plan B)"></task>
								//	<task name="If child also has a severe classification: 1) Refer URGENTLY to hospital with mother giving frequent sips of ORS on the way, 2) Advise the mother to continue breastfeeding"></task>
								//	<task name="Advise mother when to return immediately"></task>
								//	<task name="Follow-up in 5 days if not improving"></task>
								//	<transition to="end-state1" name="to-end"></transition>
								//</task-node>
								parser.nextTag(); //go to task tag

								currScreen = UsbongConstants.CLASSIFICATION_SCREEN;
								classificationContainer.removeAllElements();
								while(!parser.getName().equals("transition")) {
									  classificationContainer.addElement(parser.getAttributeValue(0).toString());
									  //do two nextTag()'s, because <task> has a closing tag
									  parser.nextTag();
									  parser.nextTag();		
								}
								nextUsbongNodeIfYes = parser.getAttributeValue(0).toString();
								nextUsbongNodeIfNo = parser.getAttributeValue(0).toString();						  							  							  
							}
							else { //this is a task-node that has "~"								
								if (myStringToken.equals("radioButtons")) {
									//<task-node name="radioButtons~1~Life is good">
									//	<task name="Strongly Disagree"></task>
									//	<task name="Moderately Disagree"></task>
									//	<task name="Slightly Disagree"></task>
									//	<task name="Slightly Agree"></task>
									//	<task name="Moderately Agree"></task>
									//	<task name="Strongly Agree"></task>
									//	<transition to="radioButtons~1~I don't think that the world is a good place" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to task tag

									//radioButtons by definition requires only 1 ticked button in the group
									//requiredTotalCheckedBoxes = Integer.parseInt(st.nextToken());
/*																		
									String t=st.nextToken();
									//do this so that we are sure that the token that we are going to check is the last token
									while(st.hasMoreTokens()) {
										t=st.nextToken();
									}
*/									
									currScreen=UsbongConstants.MULTIPLE_RADIO_BUTTONS_SCREEN;
									
									radioButtonsContainer.removeAllElements();
									while(!parser.getName().equals("transition")) {
										  radioButtonsContainer.addElement(parser.getAttributeValue(0).toString());
										  
										  //do two nextTag()'s, because <task> has a closing tag
										  parser.nextTag();
										  parser.nextTag();		
									}
									parseYesNoAnswers(parser);
								}
								if (myStringToken.equals("radioButtonsWithAnswer")) {
									//<task-node name="radioButtonsWithAnswer~You see your teacher approaching you. What do you do?Answer=0">
									//	<task name="Greet him."></task>
									//	<task name="Run away."></task>
									//	<task name="Ignore him."></task>
									//	<transition to="textDisplay~Hello!" name="Yes"></transition>
									//	<transition to="textDisplay~You feel sad that you didn't greet him." name="No"></transition>
									//</task-node>
									parser.nextTag(); //go to task tag
									//radioButtons by definition requires only 1 ticked button in the group
									currScreen=UsbongConstants.MULTIPLE_RADIO_BUTTONS_WITH_ANSWER_SCREEN;
									
									radioButtonsContainer.removeAllElements();
									while(!parser.getName().equals("transition")) {
										  radioButtonsContainer.addElement(parser.getAttributeValue(0).toString());
										  
										  //do two nextTag()'s, because <task> has a closing tag
										  parser.nextTag();
										  parser.nextTag();		
									}
									parseYesNoAnswers(parser);
								}								
								else if (myStringToken.equals("link")) {
									//<task-node name="link~1~What will you do?">
									//	<task name="textDisplay~You ate the fruit.~I will eat the fruit."></task>
									//	<task name="textDisplay~You looked for another solution.~I will look for another solution"></task>
									//	<transition to="textDisplay~You ate the fruit.~I will eat the fruit." name="Any"></transition>
									//</task-node>
									//the transition is the default
									parser.nextTag(); //go to task tag

									//radioButtons by definition requires only 1 ticked button in the group
									//requiredTotalCheckedBoxes = Integer.parseInt(st.nextToken());
/*																		
									String t=st.nextToken();
									//do this so that we are sure that the token that we are going to check is the last token
									while(st.hasMoreTokens()) {
										t=st.nextToken();
									}
*/									
									currScreen=UsbongConstants.LINK_SCREEN;
									
									radioButtonsContainer.removeAllElements();
									while(!parser.getName().equals("transition")) {
										  radioButtonsContainer.addElement(parser.getAttributeValue(0).toString());
										  Log.d(">>>>>parser.getAttributeValue(0).toString()",parser.getAttributeValue(0).toString());
										  //do two nextTag()'s, because <task> has a closing tag
										  parser.nextTag();
										  parser.nextTag();		
									}
									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("checkList")) {
									//<task-node name="checkList~2~Check for SEVERE DEHYDRATION. Has at least two (2) of the following signs:">
									//	<task name="Is lethargic or unconscious."></task>
									//	<task name="Has sunken eyes."></task>
									//	<task name="Is not able to drink or is drinking poorly."></task>
									//	<task name="Skin pinch goes back VERY slowly (longer than 2 seconds)."></task>
									//	<transition to="SEVERE DEHYDRATION" name="Yes"></transition>
									//	<transition to="checkList-Check for SOME DEHYDRATION. Has at least two (2) of the following signs:" name="No"></transition>
									//</task-node>
									parser.nextTag(); //go to task tag
									requiredTotalCheckedBoxes = Integer.parseInt(st.nextToken());
/*																		
									String t=st.nextToken();
									//do this so that we are sure that the token that we are going to check is the last token
									while(st.hasMoreTokens()) {
										t=st.nextToken();
									}
*/									
/*									
									//added by Mike, Aug. 20, 2010; st.nextToken()
									if (t.equals(GENERAL_DANGER_SIGNS_CHECKBOX_QUESTION)) { //Does the question pertain to general danger signs?
										isAGeneralDangerSignsCheckBox=true;
									}
									//added by Mike, Sept. 17, 2010
									else if (t.equals(SOME_DEHYDRATION_CHECKBOX_QUESTION)) {
										isSomeDehydrationCheckBox=true;
									}
									else if (t.equals(SEVERE_DEHYDRATION_CHECKBOX_QUESTION)) {
										isSevereDehydrationCheckBox=true;
									}
*/									
									currScreen=UsbongConstants.MULTIPLE_CHECKBOXES_SCREEN;

									checkBoxesContainer.removeAllElements();
									while(!parser.getName().equals("transition")) {
										  checkBoxesContainer.addElement(parser.getAttributeValue(0).toString());
										  
										  //do two nextTag()'s, because <task> has a closing tag
										  parser.nextTag();
										  parser.nextTag();		
									}
									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("sendToWebServer")) { 								
								  currScreen=UsbongConstants.SEND_TO_WEBSERVER_SCREEN;
								  parser.nextTag(); //go to the next tag
								  parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("sendToCloudBasedService")) { 								
								  currScreen=UsbongConstants.SEND_TO_CLOUD_BASED_SERVICE_SCREEN;
								  parser.nextTag(); //go to the next tag
								  parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("dcatSummary")) { 								
								  currScreen=UsbongConstants.DCAT_SUMMARY_SCREEN;
								  parser.nextTag(); //go to the next tag
								  parseYesNoAnswers(parser);
								}									
								else if (myStringToken.equals("qrCodeReader")) { 								
									//<task-node name="qrCodeReader~Scan Patient's QR Code ID?">
									//  <transition to="textField~Family Name:" name="Any"></transition>
									//</task-node>
									currScreen=UsbongConstants.QR_CODE_READER_SCREEN;
									parser.nextTag(); //go to the next tag
									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("textField")) { 
									//<task-node name="textField~For how many days?">
									//  <transition to="Does the child have wheezing? (child must be calm)" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.TEXTFIELD_SCREEN;
									
									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("textFieldWithAnswer")) { 
									//<task-node name="textFieldWithAnswer~Who is the founder of Usbong (nickname)?Answer=Mike">
									//  <transition to="textDisplay~Correct!" name="Yes"></transition>
									//  <transition to="textDisplay~Incorrect!" name="No"></transition>		
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.TEXTFIELD_WITH_ANSWER_SCREEN;
									
									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("textFieldWithUnit")) { 
									//<task-node name="textFieldWithUnit~Days~For how many days?">
									//  <transition to="Does the child have wheezing? (child must be calm)" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									textFieldUnit = st.nextToken();									
									currScreen=UsbongConstants.TEXTFIELD_WITH_UNIT_SCREEN;
									
									parseYesNoAnswers(parser);
								}																								
								else if (myStringToken.equals("textFieldNumerical")) { 
									//<task-node name="textFieldNumerical~PatientID">
									//  <transition to="Does the child have wheezing? (child must be calm)" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.TEXTFIELD_NUMERICAL_SCREEN;
									
									parseYesNoAnswers(parser);
								}																								
								else if (myStringToken.equals("textArea")) { 
									//<task-node name="textArea~Comments">
									//  <transition to="Does the child have wheezing? (child must be calm)" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.TEXTAREA_SCREEN;
									
									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("textAreaWithAnswer")) { 
									//<task-node name="textAreaWithAnswer~Who is the founder of Usbong (nickname)?Answer=Mike">
									//  <transition to="textDisplay~Correct!" name="Yes"></transition>
									//  <transition to="textDisplay~Incorrect!" name="No"></transition>		
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.TEXTAREA_WITH_ANSWER_SCREEN;
									
									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("date")) { //special?
									//<task-node name="date~Birthday">
									//  <transition to="textField~Address" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.DATE_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("timestampDisplay")) { //special?
									//<task-node name="timestampDisplay~Timecheck">
									//  <transition to="textDisplay~Comments" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.TIMESTAMP_DISPLAY_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if ((myStringToken.equals("special")) || (myStringToken.equals("textDisplay"))) { //special?
									//<task-node name="special~Give a trial of rapid acting inhaled bronchodilator for up to 3 times 15-20 minutes apart. Count the breaths and look for chest indrawing again, and then classify.">
									//  <transition to="Does the child have wheezing? (child must be calm)" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.TEXT_DISPLAY_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if ((myStringToken.equals("specialImage")) || (myStringToken.equals("imageDisplay"))) { //special?
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.IMAGE_DISPLAY_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("clickableImageDisplay")) { 
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.CLICKABLE_IMAGE_DISPLAY_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("textImageDisplay")) { //special?
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.TEXT_IMAGE_DISPLAY_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("imageTextDisplay")) { //special?
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.IMAGE_TEXT_DISPLAY_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("textClickableImageDisplay")) { 
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.TEXT_CLICKABLE_IMAGE_DISPLAY_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("clickableImageTextDisplay")) { 
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.CLICKABLE_IMAGE_TEXT_DISPLAY_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("videoFromFile")) { 
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.VIDEO_FROM_FILE_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("videoFromFileWithText")) { 
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.VIDEO_FROM_FILE_WITH_TEXT_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("youtubeVideo")) { 
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.YOUTUBE_VIDEO_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("youtubeVideoWithText")) { 
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.YOUTUBE_VIDEO_WITH_TEXT_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("gps")) { //special?
									//<task-node name="gps~My Location">
									//  <transition to="Does the child have wheezing? (child must be calm)" name="Any"></transition>
									//</task-node>
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.GPS_LOCATION_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("photoCapture")) { 
									
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.PHOTO_CAPTURE_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("audioRecord")) { 
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.AUDIO_RECORD_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("paint")) { 
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.PAINT_SCREEN;

									parseYesNoAnswers(parser);
								}
								else if (myStringToken.equals("simpleEncrypt")) { 
									parser.nextTag(); //go to transition tag
									currScreen=UsbongConstants.SIMPLE_ENCRYPT_SCREEN;

									parseYesNoAnswers(parser);
								}
							}
					  }/*
					  else { //this is a currIMCICaseList number
						usbongNodeContainerCounter++;
						currUsbongNode=(String)usbongNodeContainer.elementAt(usbongNodeContainerCounter);
						continue;
					  }*/
					  break;
				  }
				  //TODO dosage guide/table
				  //It would be great if someone adds the above TODO as well.
			  }
		  }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
/*		
		if ((!usedBackButton) && (!hasReturnedFromAnotherActivity)){
			usbongNodeContainer.addElement(currUsbongNode);
			usbongNodeContainerCounter++;
		}
		else {
			usedBackButton=false;
			hasReturnedFromAnotherActivity=false;
		}
*/		
		
		//added by Mike, 20151126
		storeDataToUdteaObject();

		initUsbongScreen();
	}

	public void initUsbongScreen() {		
		Log.d(">>>>","inside: initUsbongScreen()");		
		
		myUsbongScreenProcessor.init();
		if (UsbongUtils.IS_IN_AUTO_NARRATE_MODE) { //added by Mike, 24 Sept. 2015
			processSpeak(new StringBuffer());
		}
		processPlayBGMusic(); //added by Mike, 25 Sept. 2015
	}
   
    public void initBackNextButtons()
    {
    	initBackButton();
    	initNextButton();
    }

    public void initBackButton()
    {
    	backButton = (Button)findViewById(R.id.back_button);
    	backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processBackButtonPressed();
			}
		});    
    }
    
    public void processBackButtonPressed() {
    	try {
			if (mTts.isSpeaking()) {
				mTts.stop();
			}
			//added by Mike, 21 July 2015
			if (myMediaPlayer.isPlaying()) {
				myMediaPlayer.stop();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		usedBackButton=true;
		
		decisionTrackerContainer.addElement("B;");
		
		if (!usbongAnswerContainer.isEmpty()) {
/*					
			currAnswer = usbongAnswerContainer.lastElement();
			usbongAnswerContainer.removeElementAt(usbongAnswerContainer.size()-1);					
*/
			usbongAnswerContainerCounter--;
			
			if (usbongAnswerContainerCounter<0) {
				usbongAnswerContainerCounter=0;
			}
			
			currAnswer = usbongAnswerContainer.elementAt(usbongAnswerContainerCounter);
		}

		if (!usbongNodeContainer.isEmpty()) {
			usbongNodeContainer.removeElementAt(usbongNodeContainerCounter);                            
            usbongNodeContainerCounter--;
            Log.d(">>>>>usbongNodeContainerCounter", ""+usbongNodeContainerCounter);
		}

        if (usbongNodeContainerCounter>=0) {
        	currUsbongNode=(String)usbongNodeContainer.elementAt(usbongNodeContainerCounter);
            Log.d(">>>>>currUsbongNode", currUsbongNode);
        }
        else { 
        	processReturnToMainMenuActivity();
/*                	initParser();
        	return;
*/
        }
    	initParser();
	}

    public void initNextButton()
    {
    	nextButton = (Button)findViewById(R.id.next_button);
    	nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processNextButtonPressed();
			}
    	});
    }
    
    public void processNextButtonPressed() {
		wasNextButtonPressed=true;
		hasUpdatedDecisionTrackerContainer=false;
		
		try {
			if ((mTts!=null) && (mTts.isSpeaking())) {
				mTts.stop();
			}
			//edited by Mike, 20160415
			if ((myMediaPlayer!=null) && (myMediaPlayer.isPlaying())) {
				myMediaPlayer.stop();
			}
	
			if (currAudioRecorder!=null) {
				try {					
					//if stop button is pressable
					if (stopButton.isEnabled()) { 
						currAudioRecorder.stop();
					}
					if (currAudioRecorder.isPlaying()){
						currAudioRecorder.stopPlayback();
					}					
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				String path = currAudioRecorder.getPath();
	//			System.out.println(">>>>>>>>>>>>>>>>>>>currAudioRecorder: "+currAudioRecorder);
				if (!attachmentFilePaths.contains(path)) {
					attachmentFilePaths.add(path);
	//				System.out.println(">>>>>>>>>>>>>>>>adding path: "+path);
				}							
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
    	//UsbongConstants.END_STATE_SCREEN = last screen
    	if (currScreen==UsbongConstants.END_STATE_SCREEN) {    		
    		int usbongAnswerContainerSize = usbongAnswerContainer.size();
    		StringBuffer outputStringBuffer = new StringBuffer();
    		for(int i=0; i<usbongAnswerContainerSize;i++) {
    			outputStringBuffer.append(usbongAnswerContainer.elementAt(i));
    		}

    		myOutputDirectory=UsbongUtils.getDateTimeStamp()+"/";
    		if (UsbongUtils.STORE_OUTPUT) {
    			try {
    				UsbongUtils.createNewOutputFolderStructure();
    			}
    			catch(Exception e) {
    				e.printStackTrace();
    			}
    			UsbongUtils.storeOutputInSDCard(UsbongUtils.BASE_FILE_PATH + myOutputDirectory + UsbongUtils.getDateTimeStamp() + ".csv", outputStringBuffer.toString());
    		}
    		else {
    			UsbongUtils.deleteRecursive(new File(UsbongUtils.BASE_FILE_PATH + myOutputDirectory));
    		}

    		//wasNextButtonPressed=false; //no need to make this true, because this is the last node
			hasUpdatedDecisionTrackerContainer=true;
			
    		/*
    		//send to server
    		UsbongUtils.performFileUpload(UsbongUtils.BASE_FILE_PATH + myOutputDirectory + UsbongUtils.getTimeStamp() + ".csv");
    		 
    		//send to email
    		Intent emailIntent = UsbongUtils.performEmailProcess(UsbongUtils.BASE_FILE_PATH + myOutputDirectory + UsbongUtils.getTimeStamp() + ".csv", attachmentFilePaths);
    		emailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//    		emailIntent.addFlags(RESULT_OK);
    		startActivityForResult(Intent.createChooser(emailIntent, "Email:"),EMAIL_SENDING_SUCCESS);
*/
			
    		//added by Mike, Sept. 10, 2014
    		UsbongUtils.clearTempFolder();

    		//added by Mike, 20160415
    		if (UsbongUtils.IS_IN_AUTO_LOOP_MODE) {
    			isAutoLoopedTree=true;
    			initParser(myTree);
    			return;
    		}
    		else {
    			finish();
    		}
    	}
    	else {			
    		if (currScreen==UsbongConstants.YES_NO_DECISION_SCREEN) {
		        RadioButton myYesRadioButton = (RadioButton)findViewById(R.id.yes_radiobutton);
		        RadioButton myNoRadioButton = (RadioButton)findViewById(R.id.no_radiobutton);

		        if (myYesRadioButton.isChecked()) {
					currUsbongNode = nextUsbongNodeIfYes;
					
					UsbongUtils.addElementToContainer(usbongAnswerContainer, "Y;", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;

					initParser();								        	
		        }
		        else if (myNoRadioButton.isChecked()) {
					currUsbongNode = nextUsbongNodeIfNo; 
//					usbongAnswerContainer.addElement("N;");															
					UsbongUtils.addElementToContainer(usbongAnswerContainer, "N;", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;
					
					initParser();								        					        	
		        }
		        else { //if no radio button was checked				        	
	        		if (!UsbongUtils.IS_IN_DEBUG_MODE) {
	    				if (!isAnOptionalNode) {
		    				showRequiredFieldAlert(PLEASE_CHOOSE_AN_ANSWER_ALERT_TYPE);
	    					wasNextButtonPressed=false;
	    					hasUpdatedDecisionTrackerContainer=true;
	    					return;
		        		}
	        		}
		    		currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
		    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A;", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;

		    		initParser();				
		        }
    		}	
    		else if (currScreen==UsbongConstants.SEND_TO_WEBSERVER_SCREEN) {
		        RadioButton myYesRadioButton = (RadioButton)findViewById(R.id.yes_radiobutton);
		        RadioButton myNoRadioButton = (RadioButton)findViewById(R.id.no_radiobutton);

		        if (myYesRadioButton.isChecked()) {
					currUsbongNode = nextUsbongNodeIfYes; 
//					usbongAnswerContainer.addElement("Y;");			
					UsbongUtils.addElementToContainer(usbongAnswerContainer, "Y;", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;
					
					decisionTrackerContainer.addElement(usbongAnswerContainer.lastElement());
//					wasNextButtonPressed=false; //no need to make this true, because "Y;" has already been added to decisionTrackerContainer
					hasUpdatedDecisionTrackerContainer=true;
					
		    		//edited by Mike, March 4, 2013
		    		//"save" the output into the SDCard as "output.txt"
//		    		int usbongAnswerContainerSize = usbongAnswerContainer.size();
		    		int usbongAnswerContainerSize = usbongAnswerContainerCounter;
		    					    		
		    		StringBuffer outputStringBuffer = new StringBuffer();
		    		for(int i=0; i<usbongAnswerContainerSize;i++) {
		    			outputStringBuffer.append(usbongAnswerContainer.elementAt(i));
		    		}

		        	myOutputDirectory=UsbongUtils.getDateTimeStamp()+"/";
	    			try {
	    				UsbongUtils.createNewOutputFolderStructure();
	    			}
	    			catch(Exception e) {
	    				e.printStackTrace();
	    			}				        	
		        	UsbongUtils.storeOutputInSDCard(UsbongUtils.BASE_FILE_PATH + myOutputDirectory + UsbongUtils.getDateTimeStamp() + ".csv", outputStringBuffer.toString());

		    		//send to server
		    		UsbongUtils.performFileUpload(UsbongUtils.BASE_FILE_PATH + myOutputDirectory + UsbongUtils.getDateTimeStamp() + ".csv");
		        }
		        else if (myNoRadioButton.isChecked()) {
					currUsbongNode = nextUsbongNodeIfNo; 
//					usbongAnswerContainer.addElement("N;");															
					UsbongUtils.addElementToContainer(usbongAnswerContainer, "N;", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;
		        }
		        else { //if no radio button was checked				        	
	        		if (!UsbongUtils.IS_IN_DEBUG_MODE) {
	    				if (!isAnOptionalNode) {
		    				showRequiredFieldAlert(PLEASE_CHOOSE_AN_ANSWER_ALERT_TYPE);
	    					wasNextButtonPressed=false;
	    					hasUpdatedDecisionTrackerContainer=true;
	    					return;
		        		}
	        		}
//	        		else {
		    		currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
		    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A;", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;
//		    		initParser();				
		        }
				initParser();				
    		}	
    		else if (currScreen==UsbongConstants.SEND_TO_CLOUD_BASED_SERVICE_SCREEN) {
		        RadioButton myYesRadioButton = (RadioButton)findViewById(R.id.yes_radiobutton);
		        RadioButton myNoRadioButton = (RadioButton)findViewById(R.id.no_radiobutton);

		        if (myYesRadioButton.isChecked()) {
		        	//added by Mike, 20161121
		    	    new AsyncTask<String, Integer, Boolean>() {
		    			@Override
		    			protected void onPostExecute(Boolean result) {
				    		//send to cloud-based service
				    		Intent sendToCloudBasedServiceIntent = UsbongUtils.performSendToCloudBasedServiceProcess(UsbongUtils.BASE_FILE_PATH + myOutputDirectory + UsbongUtils.getDateTimeStamp() + ".csv", attachmentFilePaths);

				    		//answer from Llango J, stackoverflow
				    		//Reference: http://stackoverflow.com/questions/7479883/problem-with-sending-email-goes-back-to-previous-activity;
				    		//last accessed: 22 Oct. 2012
				    		getInstance().startActivity(Intent.createChooser(sendToCloudBasedServiceIntent, "Send to Cloud-based Service:"));
				    		getInstance().initParser();				
		    			}

		    			@Override
		    			protected Boolean doInBackground(String... params) {
				        	currUsbongNode = nextUsbongNodeIfYes; 
//							usbongAnswerContainer.addElement("Y;");															
				    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "Y;", usbongAnswerContainerCounter);
							usbongAnswerContainerCounter++;

							decisionTrackerContainer.addElement(usbongAnswerContainer.lastElement());
//							wasNextButtonPressed=false; //no need to make this true, because "Y;" has already been added to decisionTrackerContainer
							hasUpdatedDecisionTrackerContainer=true;
							
							StringBuffer sb = new StringBuffer();
							for (int i=0; i<decisionTrackerContainer.size();i++) {
								sb.append(decisionTrackerContainer.elementAt(i));
							}
							Log.d(">>>>>>>>>>>>>decisionTrackerContainer", sb.toString());								
							
				    		//edited by Mike, March 4, 2013
				    		//"save" the output into the SDCard as "output.txt"
//				    		int usbongAnswerContainerSize = usbongAnswerContainer.size();
				    		int usbongAnswerContainerSize = usbongAnswerContainerCounter;

				    		StringBuffer outputStringBuffer = new StringBuffer();
				    		for(int i=0; i<usbongAnswerContainerSize; i++) {
				    			outputStringBuffer.append(usbongAnswerContainer.elementAt(i));
				    		}

				        	myOutputDirectory=UsbongUtils.getDateTimeStamp()+"/";
			    			try {
			    				UsbongUtils.createNewOutputFolderStructure();
			    			}
			    			catch(Exception e) {
			    				e.printStackTrace();
			    			}				        	
				        	UsbongUtils.storeOutputInSDCard(UsbongUtils.BASE_FILE_PATH + myOutputDirectory + UsbongUtils.getDateTimeStamp() + ".csv", outputStringBuffer.toString());
		    				return true;		
		    			}	
		    	    }.execute();
		        }
		        else if (myNoRadioButton.isChecked()) {
					currUsbongNode = nextUsbongNodeIfNo; 
//					usbongAnswerContainer.addElement("N;");															
		    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "N;", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;
		    		initParser();				
		        }
		        else { //if no radio button was checked				        	
	        		if (!UsbongUtils.IS_IN_DEBUG_MODE) {
	    				if (!isAnOptionalNode) {
		    				showRequiredFieldAlert(PLEASE_CHOOSE_AN_ANSWER_ALERT_TYPE);
	    					wasNextButtonPressed=false;
	    					hasUpdatedDecisionTrackerContainer=true;
	    					return;
		        		}
	        		}
//	        		else {
		    		currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
		    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A;", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;
		    		initParser();				
		        }

		        //commented out by Mike, 20161122
//		        initParser();
    		}	
    		else if (currScreen==UsbongConstants.MULTIPLE_CHECKBOXES_SCREEN) {
//	    		requiredTotalCheckedBoxes	
		        LinearLayout myMultipleCheckboxesLinearLayout = (LinearLayout)findViewById(R.id.multiple_checkboxes_linearlayout);
		        StringBuffer sb = new StringBuffer();
		        int totalCheckedBoxes=0;
		        int totalCheckBoxChildren = myMultipleCheckboxesLinearLayout.getChildCount();
		        //begin with i=1, because i=0 is for checkboxes_textview
		        for(int i=1; i<totalCheckBoxChildren; i++) {
		        	if (((CheckBox)myMultipleCheckboxesLinearLayout.getChildAt(i)).isChecked()) {
		        		totalCheckedBoxes++;
		        		sb.append(","+(i-1)); //do a (i-1) so that i starts with 0 for the checkboxes (excluding checkboxes_textview) to maintain consistency with the other components
		        	}
		        }
		        
		        if (totalCheckedBoxes>=requiredTotalCheckedBoxes) {
					currUsbongNode = nextUsbongNodeIfYes; 	
	        		sb.insert(0,"Y"); //insert in front of stringBuffer
					sb.append(";");
		        }
		        else {
					currUsbongNode = nextUsbongNodeIfNo; 				        					        	
					sb.delete(0,sb.length());
					sb.append("N,;"); //make sure to add the comma
		        }				        
//    			usbongAnswerContainer.addElement(sb.toString());
	    		UsbongUtils.addElementToContainer(usbongAnswerContainer, sb.toString(), usbongAnswerContainerCounter);
				usbongAnswerContainerCounter++;

    			initParser();
    		}	
    		else if (currScreen==UsbongConstants.MULTIPLE_RADIO_BUTTONS_SCREEN) {
//				currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
    			RadioGroup myRadioGroup = (RadioGroup)findViewById(R.id.multiple_radio_buttons_radiogroup);				        				        		    			

//    			if (UsbongUtils.IS_IN_DEBUG_MODE==false) {
	    			if (myRadioGroup.getCheckedRadioButtonId()==-1) { //no radio button checked
		        		if (!UsbongUtils.IS_IN_DEBUG_MODE) {
		    				if (!isAnOptionalNode) {
			    				showRequiredFieldAlert(PLEASE_CHOOSE_AN_ANSWER_ALERT_TYPE);
		    					wasNextButtonPressed=false;
		    					hasUpdatedDecisionTrackerContainer=true;
//				        		Log.d(">>>>","BEFORE return: currUsbongNode"+currUsbongNode+"; nextUsbongNodeIfYes"+nextUsbongNodeIfYes);
		    					return;
			        		}
		        		}				        						        		
//		        		else {
//		        		Log.d(">>>>","after return: currUsbongNode"+currUsbongNode+"; nextUsbongNodeIfYes"+nextUsbongNodeIfYes);
			    		currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
			    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A;", usbongAnswerContainerCounter);
						usbongAnswerContainerCounter++;

			    		initParser();				
//		        		}
	    			}
	    			else {
		    			currUsbongNode = nextUsbongNodeIfYes; //added by Mike, 31 Oct. 2015
//		    			usbongAnswerContainer.addElement(myRadioGroup.getCheckedRadioButtonId()+";");
			    		UsbongUtils.addElementToContainer(usbongAnswerContainer, myRadioGroup.getCheckedRadioButtonId()+";", usbongAnswerContainerCounter);
						usbongAnswerContainerCounter++;

		    			initParser();
	    			}
//    			}
/*		    			
    			else {
//	    			usbongAnswerContainer.addElement(myRadioGroup.getCheckedRadioButtonId()+";");
		    		UsbongUtils.addElementToContainer(usbongAnswerContainer, myRadioGroup.getCheckedRadioButtonId()+";", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;
	    			
	    			initParser();		    				
    			}
*/		    			
    		}
    		else if (currScreen==UsbongConstants.MULTIPLE_RADIO_BUTTONS_WITH_ANSWER_SCREEN) {
//				currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
    			RadioGroup myRadioGroup = (RadioGroup)findViewById(R.id.multiple_radio_buttons_radiogroup);				        				        		    			

/*		    			if (UsbongUtils.IS_IN_DEBUG_MODE==false) {
*/
	    			if (myRadioGroup.getCheckedRadioButtonId()==-1) { //no radio button checked			    							    				
		        		if (!UsbongUtils.IS_IN_DEBUG_MODE) {
		        			if (!isAnOptionalNode) {
			    				showRequiredFieldAlert(PLEASE_CHOOSE_AN_ANSWER_ALERT_TYPE);
		    					wasNextButtonPressed=false;
		    					hasUpdatedDecisionTrackerContainer=true;
		    					return;
		        			}
		        		}
//		        		else {
	        			currUsbongNode = nextUsbongNodeIfYes; //choose Yes if "Any"
			    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A,"+myRadioGroup.getCheckedRadioButtonId()+";", usbongAnswerContainerCounter);
						usbongAnswerContainerCounter++;

			    		initParser();				
//		        		}
	    			}
	    			else {			    				
	        			if (myMultipleRadioButtonsWithAnswerScreenAnswer.equals(""+myRadioGroup.getCheckedRadioButtonId())) {
							currUsbongNode = nextUsbongNodeIfYes; 	
				    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "Y,"+myRadioGroup.getCheckedRadioButtonId()+";", usbongAnswerContainerCounter);
				        }
				        else {
							currUsbongNode = nextUsbongNodeIfNo; 				        					        	
				    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "N,"+myRadioGroup.getCheckedRadioButtonId()+";", usbongAnswerContainerCounter);
				        }				        

						usbongAnswerContainerCounter++;
		    			initParser();
	    			}
    		}
    		else if (currScreen==UsbongConstants.LINK_SCREEN) {		    			
    			RadioGroup myRadioGroup = (RadioGroup)findViewById(R.id.multiple_radio_buttons_radiogroup);				        				        		    					    			
//    			Log.d(">>>>>>>>>>currUsbongNode",currUsbongNode);
	    			if (myRadioGroup.getCheckedRadioButtonId()==-1) { //no radio button checked
//		    			if (!UsbongUtils.IS_IN_DEBUG_MODE) {
		    				if (!isAnOptionalNode) {
			    				showRequiredFieldAlert(PLEASE_CHOOSE_AN_ANSWER_ALERT_TYPE);
		    					wasNextButtonPressed=false;
		    					hasUpdatedDecisionTrackerContainer=true;				    					
		    					return;
			        		}
//		    			}
//		        		else {
			    			//below line of code, added by Mike, 31 Oct. 2015
			    			currUsbongNode = UsbongUtils.getLinkFromRadioButton(nextUsbongNodeIfYes); //nextUsbongNodeIfNo will also do, since this is "Any" 
//				    		currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
				    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A;", usbongAnswerContainerCounter);
							usbongAnswerContainerCounter++;
							
							initParser();				
//		        		}
	    			}
	    			else {
		    			try {
		    				currUsbongNode = UsbongUtils.getLinkFromRadioButton(radioButtonsContainer.elementAt(myRadioGroup.getCheckedRadioButtonId()));		    				
		    			}
		    			catch(Exception e) {
		    				//if the user hasn't ticked any radio button yet
		    				//put the currUsbongNode to default
//			    			currUsbongNode = UsbongUtils.getLinkFromRadioButton(nextUsbongNodeIfYes); //nextUsbongNodeIfNo will also do, since this is "Any"
			    			//of course, showPleaseAnswerAlert() will be called			    			  
		    				//edited by Mike, 20160613
		    				//don't change the currUsbongNode anymore if no radio button has been ticked    				
		    			}		    			

//		    			usbongAnswerContainer.addElement(myRadioGroup.getCheckedRadioButtonId()+";");
			    		UsbongUtils.addElementToContainer(usbongAnswerContainer, myRadioGroup.getCheckedRadioButtonId()+";", usbongAnswerContainerCounter);
						usbongAnswerContainerCounter++;

		    			initParser();
	    			}
/*		    			}
    			else {
	    			usbongAnswerContainer.addElement(myRadioGroup.getCheckedRadioButtonId()+";");
			        initParser();		    				
    			}
*/		    			
    		}
    		else if ((currScreen==UsbongConstants.TEXTFIELD_SCREEN) 
    				|| (currScreen==UsbongConstants.TEXTFIELD_WITH_UNIT_SCREEN)
    				|| (currScreen==UsbongConstants.TEXTFIELD_NUMERICAL_SCREEN)) {
//    			currUsbongNode = nextUsbongNodeIfYes; //= nextIMCIQuestionIfNo will also do
		        EditText myTextFieldScreenEditText = (EditText)findViewById(R.id.textfield_edittext);

//		        if (UsbongUtils.IS_IN_DEBUG_MODE==false) {
			        //if it's blank
	    			if (myTextFieldScreenEditText.getText().toString().trim().equals("")) {
				        if (!UsbongUtils.IS_IN_DEBUG_MODE) {
		    				if (!isAnOptionalNode) {
			    				showRequiredFieldAlert(PLEASE_ANSWER_FIELD_ALERT_TYPE);
		    					wasNextButtonPressed=false;
		    					hasUpdatedDecisionTrackerContainer=true;
		    					return;
			        		}
				        }
//		        		else {
				    		currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
//							usbongAnswerContainer.addElement("A;");															
				    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A,;", usbongAnswerContainerCounter);
							usbongAnswerContainerCounter++;
				    		
				    		initParser();				
//		        		}
	    			}
					else {
		    			currUsbongNode = nextUsbongNodeIfYes; //added by Mike, 31 Oct. 2015
//						usbongAnswerContainer.addElement("A,"+myTextFieldScreenEditText.getText()+";");							
			    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A,"+myTextFieldScreenEditText.getText()+";", usbongAnswerContainerCounter);
						usbongAnswerContainerCounter++;
						
						initParser();
					}
    		}
    		else if (currScreen==UsbongConstants.TEXTFIELD_WITH_ANSWER_SCREEN) {
//    			currUsbongNode = nextUsbongNodeIfYes; 
		        EditText myTextFieldScreenEditText = (EditText)findViewById(R.id.textfield_edittext);
			        //if it's blank
	    			if (myTextFieldScreenEditText.getText().toString().trim().equals("")) {
		        		if (!UsbongUtils.IS_IN_DEBUG_MODE) {
		        			if (!isAnOptionalNode) {
			    				showRequiredFieldAlert(PLEASE_ANSWER_FIELD_ALERT_TYPE);
		    					wasNextButtonPressed=false;
		    					hasUpdatedDecisionTrackerContainer=true;
		    					return;
		        			}
		        		}
			    		currUsbongNode = nextUsbongNodeIfYes; //choose Yes if "Any"
			    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A,"+myTextFieldScreenEditText.getText().toString().trim()+";", usbongAnswerContainerCounter);
						usbongAnswerContainerCounter++;
			    		
			    		initParser();				
	    			}
					else {
						//added by Mike, Jan. 27, 2014
						Vector<String> myPossibleAnswers = new Vector<String>();
						StringTokenizer myPossibleAnswersStringTokenizer = new StringTokenizer(myTextFieldWithAnswerScreenAnswer, "||");
						if (myPossibleAnswersStringTokenizer != null) {
							while (myPossibleAnswersStringTokenizer.hasMoreTokens()) { //get last element (i.e. Mike in "textFieldWithAnswer~Who is the founder of Usbong (nickname)?Answer=Mike")
								myPossibleAnswers.add(myPossibleAnswersStringTokenizer.nextToken()); 
							}
						}
						int size = myPossibleAnswers.size();
						for(int i=0; i<size; i++) {
		        			if (myPossibleAnswers.elementAt(i).equals(myTextFieldScreenEditText.getText().toString().trim())) {
								currUsbongNode = nextUsbongNodeIfYes; 	
					    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "Y,"+myTextFieldScreenEditText.getText().toString().trim()+";", usbongAnswerContainerCounter);
					    		break;
					        }	

		        			if (i==size-1) { //if this is the last element in the vector
								currUsbongNode = nextUsbongNodeIfNo; 				        					        	
					    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "N,"+myTextFieldScreenEditText.getText().toString().trim()+";", usbongAnswerContainerCounter);
		        			}
						}								
/*								
	        			if (myTextFieldWithAnswerScreenAnswer.equals(myTextFieldScreenEditText.getText().toString().trim())) {
							currUsbongNode = nextUsbongNodeIfYes; 	
				    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "Y,"+myTextFieldScreenEditText.getText().toString().trim()+";", usbongAnswerContainerCounter);
				        }
				        else {
							currUsbongNode = nextUsbongNodeIfNo; 				        					        	
				    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "N,"+myTextFieldScreenEditText.getText().toString().trim()+";", usbongAnswerContainerCounter);
				        }				        
*/
						usbongAnswerContainerCounter++;
		    			initParser();
					}
    		}
    		else if ((currScreen==UsbongConstants.TEXTAREA_SCREEN)) {
//    			currUsbongNode = nextUsbongNodeIfYes; //= nextIMCIQuestionIfNo will also do
		        EditText myTextAreaScreenEditText = (EditText)findViewById(R.id.textarea_edittext);

//		        if (UsbongUtils.IS_IN_DEBUG_MODE==false) {
			        //if it's blank
	    			if (myTextAreaScreenEditText.getText().toString().trim().equals("")) {
				        if (!UsbongUtils.IS_IN_DEBUG_MODE) {							        	
			        		if (!isAnOptionalNode) {
			    				showRequiredFieldAlert(PLEASE_ANSWER_FIELD_ALERT_TYPE);
		    					wasNextButtonPressed=false;
		    					hasUpdatedDecisionTrackerContainer=true;
		    					return;
			        		}
				        }
//		        		else {
				    		currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
				    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A,;", usbongAnswerContainerCounter);
							usbongAnswerContainerCounter++;
							
							initParser();				
//		        		}
	    			}
					else {
		    			currUsbongNode = nextUsbongNodeIfYes; //added by Mike, 31 Oct. 2015
//						usbongAnswerContainer.addElement("A,"+myTextAreaScreenEditText.getText()+";");							
			    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A,"+myTextAreaScreenEditText.getText()+";", usbongAnswerContainerCounter);
						usbongAnswerContainerCounter++;
						
						initParser();
					}
    		}
    		else if (currScreen==UsbongConstants.TEXTAREA_WITH_ANSWER_SCREEN) {
//    			currUsbongNode = nextUsbongNodeIfYes; 
		        EditText myTextAreaScreenEditText = (EditText)findViewById(R.id.textarea_edittext);
			        //if it's blank
	    			if (myTextAreaScreenEditText.getText().toString().trim().equals("")) {
		        		if (!UsbongUtils.IS_IN_DEBUG_MODE) {
		        			if (!isAnOptionalNode) {
			    				showRequiredFieldAlert(PLEASE_ANSWER_FIELD_ALERT_TYPE);
		    					wasNextButtonPressed=false;
		    					hasUpdatedDecisionTrackerContainer=true;
		    					return;
		        			}
		        		}
			    		currUsbongNode = nextUsbongNodeIfYes; //choose Yes if "Any"
			    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A,"+myTextAreaScreenEditText.getText().toString().trim()+";", usbongAnswerContainerCounter);
						usbongAnswerContainerCounter++;
			    		
			    		initParser();				
	    			}
					else {
/*								
	        			if (myTextAreaWithAnswerScreenAnswer.equals(myTextAreaScreenEditText.getText().toString().trim())) {
							currUsbongNode = nextUsbongNodeIfYes; 	
				    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "Y,"+myTextAreaScreenEditText.getText().toString().trim()+";", usbongAnswerContainerCounter);
				        }
				        else {
							currUsbongNode = nextUsbongNodeIfNo; 				        					        	
				    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "N,"+myTextAreaScreenEditText.getText().toString().trim()+";", usbongAnswerContainerCounter);
				        }				        
*/
						//added by Mike, Jan. 27, 2014
						Vector<String> myPossibleAnswers = new Vector<String>();
						StringTokenizer myPossibleAnswersStringTokenizer = new StringTokenizer(myTextAreaWithAnswerScreenAnswer, "||");
						if (myPossibleAnswersStringTokenizer != null) {
							while (myPossibleAnswersStringTokenizer.hasMoreTokens()) { //get last element (i.e. Mike in "textAreaWithAnswer~Who is the founder of Usbong (nickname)?Answer=Mike||mike")
								myPossibleAnswers.add(myPossibleAnswersStringTokenizer.nextToken().replace("{br}", "\n")); 								
							}
						}
						int size = myPossibleAnswers.size();
						for(int i=0; i<size; i++) {
							Log.d(">>>>>>myPossibleAnswers: ",myPossibleAnswers.elementAt(i));
							Log.d(">>>>>>myTextAreaScreenEditText.getText().toString().trim(): ",myTextAreaScreenEditText.getText().toString().trim());
							if (myPossibleAnswers.elementAt(i).equals(myTextAreaScreenEditText.getText().toString().trim())) {
								currUsbongNode = nextUsbongNodeIfYes; 	
					    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "Y,"+myTextAreaScreenEditText.getText().toString().trim()+";", usbongAnswerContainerCounter);
					    		break;
					        }	

		        			if (i==size-1) { //if this is the last element in the vector
								currUsbongNode = nextUsbongNodeIfNo; 				        					        	
					    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "N,"+myTextAreaScreenEditText.getText().toString().trim()+";", usbongAnswerContainerCounter);
		        			}
						}								
						usbongAnswerContainerCounter++;
		    			initParser();
					}
    		}
    		else if (currScreen==UsbongConstants.GPS_LOCATION_SCREEN) {
    			currUsbongNode = nextUsbongNodeIfYes; //= nextIMCIQuestionIfNo will also do
				TextView myLongitudeTextView = (TextView)findViewById(R.id.longitude_textview);
	            TextView myLatitudeTextView = (TextView)findViewById(R.id.latitude_textview);

//				usbongAnswerContainer.addElement(myLongitudeTextView.getText()+","+myLatitudeTextView.getText()+";");							
	    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A,"+myLongitudeTextView.getText()+","+myLatitudeTextView.getText()+";", usbongAnswerContainerCounter);
				usbongAnswerContainerCounter++;

	            initParser();				        	

    		}
    		else if (currScreen==UsbongConstants.SIMPLE_ENCRYPT_SCREEN) {
				EditText myPinEditText = (EditText)findViewById(R.id.pin_edittext);

    			if (myPinEditText.getText().toString().length()!=4) {
    				String message ="";
    				if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
    					message = (String) getResources().getText(R.string.Usbong4DigitsPinAlertMessageFILIPINO);
    				}
    				else if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
    					 message = (String) getResources().getText(R.string.Usbong4DigitsPinAlertMessageJAPANESE);				    		
    				}
    				else { //if (udtea.currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
    					message = (String) getResources().getText(R.string.Usbong4DigitsPinAlertMessageENGLISH);				    		
    				}
    				
    				new AlertDialog.Builder(UsbongDecisionTreeEngineActivity.this).setTitle("Hey!")
	    	    	.setMessage(message)
	    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
	    				@Override
	    				public void onClick(DialogInterface dialog, int which) {	            				
	    				}
	    			}).show();		    				
    			}
    			else {
					int yourKey = Integer.parseInt(myPinEditText.getText().toString());
	    			currUsbongNode = nextUsbongNodeIfYes; //= nextIMCIQuestionIfNo will also do

		    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A;", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;
						
//						Log.d(">>>>>>>start encode","encode");
					for(int i=0; i<usbongAnswerContainerCounter; i++) {							
						try {
							usbongAnswerContainer.set(i,UsbongUtils.performSimpleFileEncrypt(yourKey, usbongAnswerContainer.elementAt(i)));
//								Log.d(">>>>>>"+i,""+usbongAnswerContainer.get(i));
//								Log.d(">>>decoded"+i,""+UsbongUtils.performSimpleFileDecode(yourKey, usbongAnswerContainer.get(i)));
						}
						catch(Exception e) {
							e.printStackTrace();
						}			
					}
																
		            initParser();				        	
    			}
    		}		    		
    		else if (currScreen==UsbongConstants.DATE_SCREEN) {
    			currUsbongNode = nextUsbongNodeIfYes;
    			//added by Mike, 13 Oct. 2015
    			DatePicker myDatePicker = (DatePicker) findViewById(R.id.date_picker);		    			
	    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A,"+myDatePicker.getMonth() +
 						 myDatePicker.getDayOfMonth() + "," +
 						myDatePicker.getYear()+";", usbongAnswerContainerCounter);

/*		    			
		    	Spinner dateMonthSpinner = (Spinner) findViewById(R.id.date_month_spinner);
		        Spinner dateDaySpinner = (Spinner) findViewById(R.id.date_day_spinner);
		        EditText myDateYearEditText = (EditText)findViewById(R.id.date_edittext);
//    			usbongAnswerContainer.addElement("A,"+monthAdapter.getItem(dateMonthSpinner.getSelectedItemPosition()).toString() +
//						 						 dayAdapter.getItem(dateDaySpinner.getSelectedItemPosition()).toString() + "," +
//						 						 myDateYearEditText.getText().toString()+";");		    					
	    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A,"+monthAdapter.getItem(dateMonthSpinner.getSelectedItemPosition()).toString() +
						 						 dayAdapter.getItem(dateDaySpinner.getSelectedItemPosition()).toString() + "," +
						 						 myDateYearEditText.getText().toString()+";", usbongAnswerContainerCounter);
*/		    			
				usbongAnswerContainerCounter++;

//    			System.out.println(">>>>>>>>>>>>>Date screen: "+usbongAnswerContainer.lastElement());
    			initParser();				        	
    		}		    		
    		else if (currScreen==UsbongConstants.TIMESTAMP_DISPLAY_SCREEN) {
    			currUsbongNode = nextUsbongNodeIfYes;
	    		UsbongUtils.addElementToContainer(usbongAnswerContainer, timestampString+";", usbongAnswerContainerCounter);
				usbongAnswerContainerCounter++;

    			initParser();				        	
    		}		    				    		
    		else if (currScreen==UsbongConstants.QR_CODE_READER_SCREEN) {
    			currUsbongNode = nextUsbongNodeIfYes; //= nextIMCIQuestionIfNo will also do

    			if (!myQRCodeContent.equals("")) {
//    				usbongAnswerContainer.addElement("Y,"+myQRCodeContent+";");							
		    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "Y,"+myQRCodeContent+";", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;
    			}
    			else {
//    				usbongAnswerContainer.addElement("N;");									    				
		    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "N;", usbongAnswerContainerCounter);
					usbongAnswerContainerCounter++;
    			}
    			initParser();				        	
    		}
    		else if ((currScreen==UsbongConstants.DCAT_SUMMARY_SCREEN)) {
    			currUsbongNode = nextUsbongNodeIfYes; //= nextIMCIQuestionIfNo will also do
/*
		        LinearLayout myDCATSummaryLinearLayout = (LinearLayout)findViewById(R.id.dcat_summary_linearlayout);
		        int total = myDCATSummaryLinearLayout.getChildCount();

				StringBuffer dcatSummary= new StringBuffer();
		        for (int i=0; i<total; i++) {
		        	dcatSummary.append(((TextView) myDCATSummaryLinearLayout.getChildAt(i)).getText().toString());
		        }
*/				        
//    			UsbongUtils.addElementToContainer(usbongAnswerContainer, "dcat_end;", usbongAnswerContainerCounter);
    			UsbongUtils.addElementToContainer(usbongAnswerContainer, "dcat_end,"+myDcatSummaryStringBuffer.toString()+";", usbongAnswerContainerCounter);
		        usbongAnswerContainerCounter++;
				
				initParser();		    		
			}
    		else { //TODO: do this for now		    		
    			currUsbongNode = nextUsbongNodeIfYes; //nextUsbongNodeIfNo will also do, since this is "Any"
//				usbongAnswerContainer.addElement("A;");															
	    		UsbongUtils.addElementToContainer(usbongAnswerContainer, "A;", usbongAnswerContainerCounter);
				usbongAnswerContainerCounter++;

				initParser();				
    		}		    		
    	}
	}
    
    public void initRecordAudioScreen() {    	    	
        String timeStamp = UsbongUtils.getDateTimeStamp();
//        final AudioRecorder recorder = new AudioRecorder("/usbong/" + timeStamp,currUsbongNode);
//        currAudioRecorder = recorder;        
        
        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setEnabled(false); 
        recordButton = (Button) findViewById(R.id.record_button);
        playButton = (Button) findViewById(R.id.play_button);
        playButton.setEnabled(false);         	

        /*
        if ((currAudioRecorder!=null) && (!currAudioRecorder.hasRecordedData())) {
            playButton.setEnabled(false);         	
        }
        else {
        	currAudioRecorder = new AudioRecorder("/usbong/" + timeStamp,currUsbongNode);
        }
*/        
    	currAudioRecorder = new AudioRecorder("/usbong/" + timeStamp,currUsbongNode + UsbongUtils.getDateTimeStamp()); //updated by Mike, 20161122

        // add a click listener to the button
        recordButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                	currAudioRecorder.start();
					stopButton.setEnabled(true);
					recordButton.setEnabled(false);
			        playButton.setEnabled(false);
				} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        });    
 

	    // add a click listener to the button
        stopButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	try {
            		currAudioRecorder.stop();
					recordButton.setEnabled(true); 
					playButton.setEnabled(true);
					stopButton.setEnabled(false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });    

        // add a click listener to the button
        playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	try {
            		currAudioRecorder.play();
					recordButton.setEnabled(true); 
					//play.setEnabled(false);
					stopButton.setEnabled(false);
            	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });    
	    	
	    new AlertDialog.Builder(UsbongDecisionTreeEngineActivity.this).setTitle("Usbong Tip")
     	.setMessage("Press the Record button to start recording. Press Stop to stop the recording. And press Play to hear what you've recorded!") // When you're done, hit the menu button and save your work.
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();		
    }
    
    public void initTakePhotoScreen()
    {
//    	myPictureName=currUsbongNode; //make the name of the picture the name of the currUsbongNode
    	//updated by Mike, 20161122		
    	myPictureName=UsbongUtils.processStringToBeFilenameReady(currUsbongNode) + UsbongUtils.getDateTimeStamp(); //make the name of the picture the name of the currUsbongNode
    	
//		String path = "/sdcard/usbong/"+ UsbongUtils.getTimeStamp() +"/"+ myPictureName +".jpg";
		String path = UsbongUtils.BASE_FILE_PATH + UsbongUtils.getDateTimeStamp()+"/"+ myPictureName +".jpg";
		//only add path if it's not already in attachmentFilePaths
		if (!attachmentFilePaths.contains(path)) {
			attachmentFilePaths.add(path);
		}
		
    	setContentView(R.layout.photo_capture_screen);
    	myImageView = (ImageView) findViewById(R.id.CameraImage);

    	File imageFile = new File(path);
        
        if(imageFile.exists())
        {
        	Bitmap myBitmap = BitmapFactory.decodeFile(path);
        	if(myBitmap != null)
        	{
        		myImageView.setImageBitmap(myBitmap);
        	}
        	//Read more: http://www.brighthub.com/mobile/google-android/articles/64048.aspx#ixzz0yXLCazcU                	  
        }
        else
        {        	
        }
    	photoCaptureButton = (Button)findViewById(R.id.photo_capture_button);
		photoCaptureIntent = new Intent().setClass(this, CameraActivity.class);
		photoCaptureIntent.putExtra("myPictureName",myPictureName);
		photoCaptureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(UsbongDecisionTreeEngineActivity.photoCaptureIntent);
			}
    	});

    }

    public void initPaintScreen()
    {
//    	myPaintName=currUsbongNode; //make the name of the picture the name of the currUsbongNode
    	//updated by Mike, 20161122
    	myPaintName=UsbongUtils.processStringToBeFilenameReady(currUsbongNode) + UsbongUtils.getDateTimeStamp(); //make the name of the picture the name of the currUsbongNode
    	    	
    	Log.d(">>>>>>myPaintName:",myPaintName);
    	
//		String path = "/sdcard/usbong/"+ UsbongUtils.getTimeStamp() +"/"+ myPictureName +".jpg";
		String path = UsbongUtils.BASE_FILE_PATH + UsbongUtils.getDateTimeStamp()+"/"+ myPaintName +".jpg";	

		//only add path if it's not already in attachmentFilePaths
		if (!attachmentFilePaths.contains(path)) {
			attachmentFilePaths.add(path);
		}

    	setContentView(R.layout.paint_screen);
    	myImageView = (ImageView) findViewById(R.id.PaintImage);

    	File imageFile = new File(path);
        if(imageFile.exists())
        {
        	Bitmap myBitmap = BitmapFactory.decodeFile(path);
        	if(myBitmap != null)
        	{
        		myImageView.setImageBitmap(myBitmap);
        	}
        	//Read more: http://www.brighthub.com/mobile/google-android/articles/64048.aspx#ixzz0yXLCazcU                	  
        }
        else
        {        	
        }
        
    	paintButton = (Button)findViewById(R.id.paint_button);
    	paintIntent = new Intent().setClass(this, PaintActivity.class);
		paintIntent.putExtra("myPaintName",myPaintName);
		paintButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(UsbongDecisionTreeEngineActivity.paintIntent);
			}
    	});
    }
    
    public void initQRCodeReaderScreen() {
    	myQRCodeReaderName=currUsbongNode; 
    	setContentView(R.layout.qr_code_reader_screen);

    	TextView qrCodeReaderResultTextView = (TextView)findViewById(R.id.qr_code_reader_result_textview);
    	TextView qrCodeReaderContentTextView = (TextView)findViewById(R.id.qr_code_reader_content_textview);
    	
    	if (!myQRCodeContent.equals("")) {
	    	if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
	    		qrCodeReaderResultTextView.setText((String) getResources().getText(R.string.UsbongQRCodeReaderContentResultFILIPINO));
	    	}
	    	else if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
	    		qrCodeReaderResultTextView.setText((String) getResources().getText(R.string.UsbongQRCodeReaderContentResultJAPANESE));				    						    		
	    	}
	    	else { //if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
	    		qrCodeReaderResultTextView.setText((String) getResources().getText(R.string.UsbongQRCodeReaderContentResultENGLISH));				    						    		
	    	}

//    		qrCodeReaderResultTextView.setText("QR Code content successfully saved!");
    		qrCodeReaderContentTextView.setText("--\n"+myQRCodeContent);
    	}
    	else {
    		if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
	    		qrCodeReaderResultTextView.setText((String) getResources().getText(R.string.UsbongQRCodeReaderContentResultNoneFILIPINO));
	    	}
	    	else if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
	    		qrCodeReaderResultTextView.setText((String) getResources().getText(R.string.UsbongQRCodeReaderContentResultNoneJAPANESE));				    						    		
	    	}
	    	else { //if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_ENGLISH) {
	    		qrCodeReaderResultTextView.setText((String) getResources().getText(R.string.UsbongQRCodeReaderContentResultNoneENGLISH));				    						    		
	    	}
//    		qrCodeReaderResultTextView.setText("No QR Code content has been saved yet!");    		
    	}

    	qrCodeReaderButton = (Button)findViewById(R.id.qr_code_reader_button);
    	qrCodeReaderIntent = new Intent().setClass(this, QRCodeReaderActivity.class);
    	qrCodeReaderIntent.putExtra("myQRCodeReaderName",myQRCodeReaderName);
    	qrCodeReaderButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(UsbongDecisionTreeEngineActivity.qrCodeReaderIntent);
			}
    	});    	
    }
    
    public void initYouTubeScreen()
    {
    	youTubeIntent = new Intent().setClass(this, MyYouTubeActivity.class);
		youTubeIntent.putExtra("youtubeID",UsbongUtils.getYouTubeVideoID(currUsbongNode));
		youTubeIntent.putExtra("currScreen",currScreen+"");

//		if (currScreen == UsbongConstants.YOUTUBE_VIDEO_WITH_TEXT_SCREEN) {
			youTubeIntent.putExtra("currUsbongNode",currUsbongNode);			
//		}
		
		Bundle myUdteaObjectBundle = new Bundle();
		myUdteaObjectBundle.putParcelable(UsbongConstants.BUNDLE, myUdteaObject);
		youTubeIntent.putExtras(myUdteaObjectBundle);

		startActivity(UsbongDecisionTreeEngineActivity.youTubeIntent);
    }
    
    public static void setQRCodeContent(String s) {
    	myQRCodeContent = s;
    }

    public void decrementCurrScreen() {
    	currScreen--;
    	initUsbongScreen();
    }

    //this is not used at the moment (Sept 5, 2011)
    public void incrementCurrScreen() {
    	currScreen++;
    	initUsbongScreen();
    }

    //edited by Mike, 20151129
    private void processReturnToMainMenuActivity() {
		String[] myPrompts = UsbongUtils.initProcessReturnToMainMenuActivity();
		    		
    	//added by Mike, Feb. 2, 2013;
		//edited by Mike, 20151120
    	AlertDialog.Builder prompt = new AlertDialog.Builder(UsbongDecisionTreeEngineActivity.this);
		prompt.setTitle(myPrompts[UsbongUtils.MY_PROMPT_TITLE]);
		prompt.setMessage(myPrompts[UsbongUtils.MY_PROMPT_MESSAGE]); 
		prompt.setPositiveButton(myPrompts[UsbongUtils.MY_PROMPT_POSITIVE_BUTTON_TEXT], new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				processReturnToMainMenuActivityYes();
			}
		});
		prompt.setNegativeButton(myPrompts[UsbongUtils.MY_PROMPT_NEGATIVE_BUTTON_TEXT], new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		prompt.show();		
    }
    
    private void processReturnToMainMenuActivityYes() {
		decisionTrackerContainer.removeAllElements();

		Log.d(">>>>UsbongUtils.STORE_OUTPUT",""+UsbongUtils.STORE_OUTPUT);
		myOutputDirectory=UsbongUtils.getDateTimeStamp()+"/";
		if (UsbongUtils.STORE_OUTPUT) {
			UsbongUtils.deleteEmptyOutputFolder(new File(UsbongUtils.BASE_FILE_PATH + myOutputDirectory));
		}
		else {
			UsbongUtils.deleteRecursive(new File(UsbongUtils.BASE_FILE_PATH + myOutputDirectory));
		}

		//added by Mike, Sept. 10, 2014
		UsbongUtils.clearTempFolder();

		//added by Mike, 21 July 2015
		if (myMediaPlayer!=null) {myMediaPlayer.stop();}
		if (mTts!=null) {mTts.stop();}
		if (myBGMediaPlayer!=null) {myBGMediaPlayer.stop();} //added by Mike, 25 Sept. 2015
		
		//return to main activity
		finish();    
		Intent toUsbongMainActivityIntent = new Intent(UsbongDecisionTreeEngineActivity.this, UsbongMainActivity.class);
		toUsbongMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(toUsbongMainActivityIntent);
    }
    
	private class CustomDataAdapter extends ArrayAdapter<String>
	{
		private ArrayList<String> items;
		
		public CustomDataAdapter(Context context, int textViewResourceId, ArrayList<String> items) {
            super(context, textViewResourceId, items);
            this.items = items;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.tree_loader, null);
                }
                final String o = items.get(position);
                if (o != null) {
                	TextView dataCurrentTextView = (TextView)v.findViewById(R.id.tree_item);
                	dataCurrentTextView.setText(o.toString());
                	dataCurrentTextView.setOnClickListener(new OnClickListener() {
            			@Override
            			public void onClick(View v) {
/*//commented out by Mike, 24 May 2015            				
            				isInTreeLoader=false;
            				
            				myTree = o.toString();
  
            				UsbongUtils.clearTempFolder();
//            				isr=null; //set inputStreamReader to null; i.e. new tree
            		        initParser();
*/            		        
            				initParser(o.toString());
            			}
                	});
/*                	
                	Button selectButton = (Button)v.findViewById(R.id.select_tree_button);
                	selectButton.setOnClickListener(new OnClickListener() {
            			@Override
            			public void onClick(View v) {
            				myTree = o.toString();
            		        initParser();
            			}
                	});
*/                	
                }
                return v;
        }
	}
	
	public void showRequiredFieldAlert(int type) {
		String requiredFieldAlertString="";
		String alertString = "";
		
		switch(type) {
			case PLEASE_CHOOSE_AN_ANSWER_ALERT_TYPE:
		        if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
		        	requiredFieldAlertString = (String) getResources().getText(R.string.pleaseChooseAnAnswerStringValueFilipino);
		        }
		        else if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
		        	requiredFieldAlertString = (String) getResources().getText(R.string.pleaseChooseAnAnswerStringValueJapanese);
		        }
		        else {
		        	requiredFieldAlertString = (String) getResources().getText(R.string.pleaseChooseAnAnswerStringValue);		        	
		        }
		        break;
			case PLEASE_ANSWER_FIELD_ALERT_TYPE:
		        if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
		        	requiredFieldAlertString = (String) getResources().getText(R.string.pleaseAnswerFieldStringValueFilipino);
		        }
		        else if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
		        	requiredFieldAlertString = (String) getResources().getText(R.string.pleaseAnswerFieldStringValueJapanese);
		        }
		        else {
		        	requiredFieldAlertString = (String) getResources().getText(R.string.pleaseAnswerFieldStringValue);		        	
		        }
				break;
		}
		requiredFieldAlertString = "{big}"+requiredFieldAlertString+"{/big}";

        if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_FILIPINO) {
    		alertString = (String) getResources().getText(R.string.alertStringValueFilipino);
        }
        else if (currLanguageBeingUsed==UsbongUtils.LANGUAGE_JAPANESE) {
    		alertString = (String) getResources().getText(R.string.alertStringValueJapanese);
        }
        else {
    		alertString = (String) getResources().getText(R.string.alertStringValueEnglish);
        }
    	
    	TextView requiredFieldAlertStringTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(UsbongDecisionTreeEngineActivity.getInstance()), UsbongUtils.IS_TEXTVIEW, requiredFieldAlertString);
    	TextView alertStringTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), new TextView(UsbongDecisionTreeEngineActivity.getInstance()), UsbongUtils.IS_TEXTVIEW, alertString);
    	
    	new AlertDialog.Builder(UsbongDecisionTreeEngineActivity.this).setTitle(alertStringTextView.getText().toString())
//		.setMessage(requiredFieldAlertStringTextView.toString())
		.setView(requiredFieldAlertStringTextView)
    	.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}	
	
	public String getVariableFromMyUsbongVariableMemory(String key) {		
		if (myUsbongVariableMemory.containsKey(key)) {
			return myUsbongVariableMemory.get(key);
		}	
		return "variable_not_found";
	}
	
	public void setVariableOntoMyUsbongVariableMemory(String varName, String varValue) {
//		if (!myUsbongVariableMemory.containsKey(varName)) {
			myUsbongVariableMemory.put(varName, varValue);			
//		}
	}
}