package usbong.android.utils;

import java.util.Vector;

public class UsbongConstants {
    private UsbongConstants() {

    }
    
    //SCREEN types constants
	public static final int YES_NO_DECISION_SCREEN=0;	
	public static final int MULTIPLE_RADIO_BUTTONS_SCREEN=1;	
	public static final int MULTIPLE_CHECKBOXES_SCREEN=2;	
	public static final int AUDIO_RECORD_SCREEN=3;
	public static final int PHOTO_CAPTURE_SCREEN=4;	
	public static final int TEXTFIELD_SCREEN=5;	
	public static final int TEXTFIELD_WITH_UNIT_SCREEN=6;	
	public static final int TEXTFIELD_NUMERICAL_SCREEN=7;
	public static final int TEXTAREA_SCREEN=8;
	public static final int TEXT_DISPLAY_SCREEN=9;	
	public static final int IMAGE_DISPLAY_SCREEN=10;
	public static final int TEXT_IMAGE_DISPLAY_SCREEN=11;
	public static final int IMAGE_TEXT_DISPLAY_SCREEN=12;
	public static final int CLASSIFICATION_SCREEN=13;		
	public static final int DATE_SCREEN=14;	
	public static final int TIMESTAMP_DISPLAY_SCREEN=15;		
	public static final int GPS_LOCATION_SCREEN=16;		
	public static final int VIDEO_FROM_FILE_SCREEN=17;	
	public static final int VIDEO_FROM_FILE_WITH_TEXT_SCREEN=18;	
	public static final int LINK_SCREEN=19;			
	public static final int SEND_TO_WEBSERVER_SCREEN=20;		
	public static final int SEND_TO_CLOUD_BASED_SERVICE_SCREEN=21;	
	public static final int PAINT_SCREEN=22;
	public static final int QR_CODE_READER_SCREEN=23;
	public static final int CLICKABLE_IMAGE_DISPLAY_SCREEN=24;
	public static final int TEXT_CLICKABLE_IMAGE_DISPLAY_SCREEN=25;
	public static final int CLICKABLE_IMAGE_TEXT_DISPLAY_SCREEN=26;
	public static final int DCAT_SUMMARY_SCREEN=27;			
	public static final int MULTIPLE_RADIO_BUTTONS_WITH_ANSWER_SCREEN=28;	
	public static final int TEXTFIELD_WITH_ANSWER_SCREEN=29;	
	public static final int TEXTAREA_WITH_ANSWER_SCREEN=30;	
	public static final int SIMPLE_ENCRYPT_SCREEN=31;	
	public static final int YOUTUBE_VIDEO_SCREEN=32;	
	public static final int YOUTUBE_VIDEO_WITH_TEXT_SCREEN=33;	
	
	public static final int END_STATE_SCREEN=34;		

    
    //JSON constants
	public static String MY_TREE = "MY_TREE";
	public static String CURR_USBONG_NODE = "CURR_USBONG_NODE";
	public static String NEXT_USBONG_NODE_IF_YES = "NEXT_USBONG_NODE_IF_YES";
	public static String NEXT_USBONG_NODE_IF_NO = "NEXT_USBONG_NODE_IF_NO";
	public static String USBONG_ANSWER_CONTAINER_COUNTER = "USBONG_ANSWER_CONTAINER_COUNTER";
	public static String USBONG_NODE_CONTAINER_COUNTER = "USBONG_NODE_CONTAINER_COUNTER";
	public static String DECISION_TRACKER_CONTAINER = "DECISION_TRACKER_CONTAINER";
	public static String DECISION_TRACKER_CONTAINER_SIZE = "DECISION_TRACKER_CONTAINER_SIZE";
	public static String USBONG_ANSWER_CONTAINER = "USBONG_ANSWER_CONTAINER";
	public static String USBONG_ANSWER_CONTAINER_SIZE = "USBONG_ANSWER_CONTAINER_SIZE";
	public static String USBONG_NODE_CONTAINER = "USBONG_NODE_CONTAINER";
	public static String USBONG_NODE_CONTAINER_SIZE = "USBONG_NODE_CONTAINER_SIZE";
/*
	//API KEY
    public static final String YOUTUBE_API_KEY = "AIzaSyCQ-Awkvj5nq5j5_9GqCKwxDzEsxjVfEIc";
*/    
    //BUNDLE KEY
    public static final String BUNDLE = "UDTEA_BUNDLE";
    public static final String JSON_KEY = "UDTEA_JSON_KEY";
    public static final String UTREE_KEY = "UTREE_KEY";
    
    public static final String FROM_MY_YOUTUBE_ACTIVITY_TO_MAIN_MENU = "FROM_MY_YOUTUBE_ACTIVITY_TO_MAIN_MENU";
    
    //SETTINGS 
    public static final int AUTO_NARRATE = 0;
    public static final int AUTO_PLAY = 1;
    public static final int AUTO_LOOP = 2;
    public static final String AUTO_NARRATE_STRING = " Auto-Narrate";
    public static final String AUTO_PLAY_STRING = " Auto-Play";    
    public static final String AUTO_LOOP_STRING = " Auto-Loop";    
    
    public static final String MY_UTTERANCE_ID = "myUsbongTTSId";
}
