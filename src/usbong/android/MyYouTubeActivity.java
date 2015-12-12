package usbong.android;

import java.util.ArrayList;

import usbong.android.utils.UsbongConstants;
import usbong.android.utils.UsbongUtils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class MyYouTubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
	public static final String API_KEY = UsbongUtils.API_KEY;
	private String myYouTubeVideoID;
//	private int currScreen;
	private String currScreen;
	private String currUsbongNode;
	private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer player;
    
    private TextView myTextView;
    	
	private Button backButton;
	private Button nextButton;	
	
	private UdteaObject myUdteaObject;
	private static MediaPlayer myMediaPlayer; //added by Mike, 20151208

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);    
                
        //added by Mike, 20151208
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);        

        //myMediaPlayer is not yet used
		//added by Mike, 20151208
		myMediaPlayer = new MediaPlayer();
		myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); //added by Mike, 22 July 2015
		myMediaPlayer.setVolume(1.0f, 1.0f);

    	Bundle bundle = getIntent().getExtras();
    	myYouTubeVideoID = bundle.getString("youtubeID");
//    	Log.d(">>>>myYouTubeVideoID",myYouTubeVideoID);
//    	currScreen = bundle.getInt("currScreen");
    	currScreen = bundle.getString("currScreen");
    	
    	Log.d(">>>currScreen",currScreen);
    	Log.d(">>>UsbongConstants.YOUTUBE_VIDEO_WITH_TEXT_SCREEN",""+UsbongConstants.YOUTUBE_VIDEO_WITH_TEXT_SCREEN);
    	
    	if (currScreen.equals(UsbongConstants.YOUTUBE_VIDEO_WITH_TEXT_SCREEN+"")) { //make the Usbong Constant a String
    		setContentView(R.layout.youtube_video_with_text_screen);    		
    		myTextView = (TextView) findViewById(R.id.youtube_with_text_textview);    		
        	currUsbongNode = bundle.getString("currUsbongNode");    		
        	myTextView.setText(((TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextView, UsbongUtils.IS_TEXTVIEW, currUsbongNode)).getText());
    	}
    	else {
    		setContentView(R.layout.youtube_video_screen);    		
    	}

		myUdteaObject = bundle.getParcelable(UsbongConstants.BUNDLE);
    	
    	youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
		youTubePlayerView.initialize(API_KEY, this);				
		
	    initBackNextButtons();
    }

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		// TODO Auto-generated method stub
		Toast.makeText(this, arg1.toString(), Toast.LENGTH_LONG).show();		
	}     
      
	@Override
	public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
		/** add listeners to YouTubePlayer instance **/
		this.player = player;
		this.player.setPlayerStateChangeListener(playerStateChangeListener);
		this.player.setPlaybackEventListener(playbackEventListener);
		
		/** Start buffering **/
		if (!wasRestored) {
			this.player.cueVideo(myYouTubeVideoID);
		}
		
		Log.d(">>>>","inside onInitSuccess...");
	}    
	
	private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {
		@Override
		public void onBuffering(boolean arg0) {
		}

		@Override
		public void onPaused() {
		}

		@Override
		public void onPlaying() {
		}

		@Override
		public void onSeekTo(int arg0) {
		}

		@Override
		public void onStopped() {
		}
	};

	private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {
		@Override
		public void onAdStarted() {
		}
		
		@Override
		public void onLoaded(String arg0) {
		}

		@Override
		public void onLoading() {
		}

		@Override
		public void onVideoEnded() {
		}

		@Override
		public void onVideoStarted() {
		}

		@Override
		public void onError(ErrorReason arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
    public void initBackNextButtons()
    {
    	initBackButton();
    	initNextButton();
    }
    
    public void initBackButton() {
    	backButton = (Button)findViewById(R.id.back_button);
    	backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				finish();
		    	Intent returnToUDTEAIntent = new Intent().setClass(MyYouTubeActivity.this, UsbongDecisionTreeEngineActivity.class);
		    	returnToUDTEAIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		    	returnToUDTEAIntent.putExtra("buttonPressed","back");
		    	returnToUDTEAIntent.putExtra("currScreen",currScreen);
		    	
				Bundle myUdteaObjectBundle = new Bundle();
				myUdteaObjectBundle.putParcelable(UsbongConstants.BUNDLE, myUdteaObject);
				returnToUDTEAIntent.putExtras(myUdteaObjectBundle);
		    	
				startActivityForResult(returnToUDTEAIntent, UsbongUtils.FROM_MY_YOUTUBE_ACTIVITY);
			}
    	});
    }
    
    public void initNextButton() {
    	nextButton = (Button)findViewById(R.id.next_button);
    	nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				finish();
		    	Intent returnToUDTEAIntent = new Intent().setClass(MyYouTubeActivity.this, UsbongDecisionTreeEngineActivity.class);
		    	returnToUDTEAIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		    	returnToUDTEAIntent.putExtra("buttonPressed","next");
		    	returnToUDTEAIntent.putExtra("currScreen",currScreen);

				Bundle myUdteaObjectBundle = new Bundle();
				myUdteaObjectBundle.putParcelable(UsbongConstants.BUNDLE, myUdteaObject);
				returnToUDTEAIntent.putExtras(myUdteaObjectBundle);
		    	
		    	startActivityForResult(returnToUDTEAIntent, UsbongUtils.FROM_MY_YOUTUBE_ACTIVITY);
			}
    	});
    }    
    
    //added by Mike, 20151129
    @Override
    public void onBackPressed() {
		String[] myPrompts = UsbongUtils.initProcessReturnToMainMenuActivity();
    	
    	AlertDialog.Builder prompt = new AlertDialog.Builder(MyYouTubeActivity.this);
		prompt.setTitle(myPrompts[UsbongUtils.MY_PROMPT_TITLE]);
		prompt.setMessage(myPrompts[UsbongUtils.MY_PROMPT_MESSAGE]); 
		prompt.setPositiveButton(myPrompts[UsbongUtils.MY_PROMPT_POSITIVE_BUTTON_TEXT], new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(">>>>>onBackPressed(); UsbongUtils.FROM_MY_YOUTUBE_ACTIVITY_TO_MAIN_MENU: ", UsbongUtils.FROM_MY_YOUTUBE_ACTIVITY_TO_MAIN_MENU+"");
	    		//return to main activity
				Intent toUDTEAIntent = new Intent(MyYouTubeActivity.this, UsbongDecisionTreeEngineActivity.class);
				toUDTEAIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				toUDTEAIntent.putExtra(UsbongConstants.FROM_MY_YOUTUBE_ACTIVITY_TO_MAIN_MENU,UsbongConstants.FROM_MY_YOUTUBE_ACTIVITY_TO_MAIN_MENU);
				startActivityForResult(toUDTEAIntent, UsbongUtils.FROM_MY_YOUTUBE_ACTIVITY_TO_MAIN_MENU);
			}
		});
		prompt.setNegativeButton(myPrompts[UsbongUtils.MY_PROMPT_NEGATIVE_BUTTON_TEXT], new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		prompt.show();
    }
}