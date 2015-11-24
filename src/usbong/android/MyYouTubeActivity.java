package usbong.android;

import usbong.android.utils.UsbongUtils;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer player;
    	
	private Button backButton;
	private Button nextButton;	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube_video_screen);

	    initBackNextButtons();

    	Bundle bundle = getIntent().getExtras();
    	myYouTubeVideoID = bundle.getString("youtubeID");
//    	Log.d(">>>>myYouTubeVideoID",myYouTubeVideoID);
//    	currScreen = bundle.getInt("currScreen");
    	currScreen = bundle.getString("currScreen");
    	
    	youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
		youTubePlayerView.initialize(API_KEY, this);		
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
//		    	returnToUDTEAIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		    	returnToUDTEAIntent.putExtra("buttonPressed","back");
		    	returnToUDTEAIntent.putExtra("currScreen",currScreen);
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
//		    	returnToUDTEAIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		    	returnToUDTEAIntent.putExtra("buttonPressed","next");
		    	returnToUDTEAIntent.putExtra("currScreen",currScreen);
		    	startActivityForResult(returnToUDTEAIntent, UsbongUtils.FROM_MY_YOUTUBE_ACTIVITY);
			}
    	});
    }    
}