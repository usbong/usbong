package usbong.android.community;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import usbong.android.R;
import usbong.android.UsbongDecisionTreeEngineActivity;
import usbong.android.utils.UsbongUtils;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("NewApi")
public class SingleItemViewWithFragment extends ActionBarActivity implements
YouTubePlayer.OnFullscreenListener,
YouTubePlayer.OnInitializedListener,
usbong.android.community.DownloadTreeAsync.AsyncResponse {
	private String youtubeLink = "";
	private DownloadTreeAsync downloadTask;
	private File savedTree;
	private Button download;
	private Button upVote;
	private Button downVote;
	private TextView voteCount; //changed by Mike, 30 May 2015
	
	private MyPageAdapter pageAdapter;
	private static final int RECOVERY_DIALOG_REQUEST = 1;
	private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
			? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
					: ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

	private LinearLayout baseLayout;
	private View otherViews;
	private ViewPager pager;
	private YouTubePlayer player;
	private boolean fullscreen;
	private FitsObject fitsObject = null;
	//For download manager
	private boolean isFileDownloaded = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.singleitemviewwithfragment);
		
//		//For download manager
//		@SuppressWarnings("unused")
//		DownloadManager mgr = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
//	    registerReceiver(onComplete,
//	                     new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//	    registerReceiver(onNotificationClick,
//	                     new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));		
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		View decorView = getWindow().getDecorView();
        // Hide the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
        	int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        	decorView.setSystemUiVisibility(uiOptions);
        }
		//Using UIL
		if(!ImageLoader.getInstance().isInited()) {
			UsbongUtils.initDisplayAndConfigOfUIL(this);
		}
		        
		baseLayout = (LinearLayout) findViewById(R.id.layout);
		otherViews = findViewById(R.id.other_views);
		
		//start of other views
		TextView uploader = (TextView) findViewById(R.id.uploadername);
		TextView fileName = (TextView) findViewById(R.id.filename);
		TextView downloadCount = (TextView) findViewById(R.id.downloadcount);
		TextView description = (TextView) findViewById(R.id.description);
		voteCount = (TextView) findViewById(R.id.voteCount);
		download = (Button) findViewById(R.id.download);
		upVote = (Button) findViewById(R.id.upVote);
		downVote = (Button) findViewById(R.id.downVote);

		if(fitsObject == null) {
			Intent i = getIntent();
			// Get the intent from ListViewAdapter
			fitsObject = i.getExtras().getParcelable(Constants.BUNDLE);
		}
		youtubeLink = UsbongUtils.parseYouTubeLink(fitsObject.getYOUTUBELINK());

		File folder = new File(Environment.getExternalStorageDirectory() + "/usbong");
		if(!folder.exists())
			folder.mkdir();

		savedTree = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/usbong/usbong_trees/"
				+ fitsObject.getFILEPATH());

		if(savedTree.exists()) {
			download.setText("Open Tree");
		} 

		//added by Mike, 31 May 2015
		if (fitsObject.getFILENAME().length()>18) {
			String s = fitsObject.getFILENAME().substring(0, 18)+"...";
			fileName.setText(s);
		}
		else {
			fileName.setText(fitsObject.getFILENAME());
		}
		
		if (fitsObject.getUPLOADER().length()>8) {
			String s = fitsObject.getUPLOADER().substring(0, 8)+"...";
			uploader.setText("Uploader: " + s);
		}
		else {
			uploader.setText("Uploader: " + fitsObject.getUPLOADER());
		}
		
		downloadCount.setText("Download Count: "+fitsObject.getDOWNLOADCOUNT() + "");
		description.setText(fitsObject.getDESCRIPTION());
		voteCount.setText(fitsObject.getRATING() + "");
		ProgressDialog mProgressDialog;

		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Downloading: " + fitsObject.getFILEPATH());
		mProgressDialog.setTitle("Saving tree...");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		downloadTask = new DownloadTreeAsync(this,  mProgressDialog);
		download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(savedTree.exists()) {
					Intent i = new Intent(SingleItemViewWithFragment.this, UsbongDecisionTreeEngineActivity.class);
					i.putExtra(Constants.UTREE_KEY, UsbongUtils.removeExtension(fitsObject.getFILEPATH()));
					startActivity(i);
				} else {
					downloadTask.execute(fitsObject.getFILEPATH());
					downloadTask.delegate = SingleItemViewWithFragment.this;
					
					//For Download manager
//					download.setText("Downloading...");
//				    startDownload(fitsObject.getFILEPATH());
				}
			}
		});

//		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
////				downloadTask.cancel(true);
//			}
//		});
		
		upVote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatabaseAction().execute(fitsObject.getFILEPATH(), Constants.RATING, "UPVOTE");
			}
		});

		downVote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatabaseAction().execute(fitsObject.getFILEPATH(), Constants.RATING, "DOWNVOTE");
			}
		});
		//End handling of other layout views
		
		List<Fragment> fragments = getFragments();
        
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);


        pager = (ViewPager)findViewById(R.id.screenshotsViewPager);
        pager.setAdapter(pageAdapter);
        
      //commented out temporarily by JP, 5 June 2015
//		YouTubePlayerFragment youTubePlayerFragment =
//				(YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.player);
//		youTubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY, this);
		doLayout();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

//		if (onComplete != null && onNotificationClick != null) {
//			unregisterReceiver(onComplete);
//			unregisterReceiver(onNotificationClick);
//		}
	}

	@Override
	public void processFinish(boolean output) {
		if(output)
			download.setText("Open Tree");
	}

    private List<Fragment> getFragments(){
    	List<Fragment> viewPagerContentList = new ArrayList<Fragment>();
    	if(fitsObject.getSCREENSHOT2().length() > 0) {
    		Log.d("SingleItemViewWithFragment", "1:" + fitsObject.getSCREENSHOT2());
    		viewPagerContentList.add(ScreenshotFragment.newInstance(
    				new ScreenshotsInViewPager(Constants.SCREENSHOT2, fitsObject.getSCREENSHOT2())));
    	}
    	if(fitsObject.getSCREENSHOT3().length() > 0) {
    		Log.d("SingleItemViewWithFragment", "2:" + fitsObject.getSCREENSHOT3());
    		viewPagerContentList.add(ScreenshotFragment.newInstance(
    				new ScreenshotsInViewPager(Constants.SCREENSHOT2, fitsObject.getSCREENSHOT3())));
    	} 
    	if(fitsObject.getSCREENSHOT4().length() > 0) {
    		Log.d("SingleItemViewWithFragment", "3:" + fitsObject.getSCREENSHOT4());
    		viewPagerContentList.add(ScreenshotFragment.newInstance(
    				new ScreenshotsInViewPager(Constants.SCREENSHOT2, fitsObject.getSCREENSHOT4())));
    	}    	
    	//Check if youtubelink is null
    	if(fitsObject.getYOUTUBELINK().length() > 0) {
    		Log.d("SingleItemViewWithFragment", "4:" + fitsObject.getYOUTUBELINK());
    		viewPagerContentList.add(ScreenshotFragment.newInstance(
    				new ScreenshotsInViewPager(Constants.YOUTUBELINK, fitsObject.getYOUTUBELINK())));
    	}
    	if(fitsObject.getYOUTUBELINK2().length() > 0) {
    		Log.d("SingleItemViewWithFragment", "5:" + fitsObject.getYOUTUBELINK2());
    		viewPagerContentList.add(ScreenshotFragment.newInstance(
    				new ScreenshotsInViewPager(Constants.YOUTUBELINK, fitsObject.getYOUTUBELINK2())));
    	}
    	return viewPagerContentList;
    }
    
    private class MyPageAdapter extends FragmentPagerAdapter {
    	private List<Fragment> fragments;

        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }
        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }
     
        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
    
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
			boolean wasRestored) {
		this.player = player;
		player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		player.setOnFullscreenListener(this);
		if (!wasRestored) {
			player.cueVideo(youtubeLink);
		}

		int controlFlags = player.getFullscreenControlFlags();
		Log.d("getFullscreen", controlFlags + "");
		setRequestedOrientation(PORTRAIT_ORIENTATION);
		controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
		player.setFullscreenControlFlags(controlFlags);
	}

	private void doLayout() {
		if (fullscreen) {
			otherViews.setVisibility(View.GONE);
		} else {
			otherViews.setVisibility(View.VISIBLE);
			ViewGroup.LayoutParams otherViewsParams = otherViews.getLayoutParams();
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				otherViewsParams.height = MATCH_PARENT;
				baseLayout.setOrientation(LinearLayout.HORIZONTAL);
			} else {
				otherViewsParams.height = 0;
				baseLayout.setOrientation(LinearLayout.VERTICAL);
			}
		}
	}

	@Override
	public void onFullscreen(boolean isFullscreen) {
		fullscreen = isFullscreen;
		doLayout();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		doLayout();
	}

	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider,
			YouTubeInitializationResult errorReason) {
		if (errorReason.isUserRecoverableError()) {
			errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
		} else {
			String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
			Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
		}
	}

	//To handle back pressed when player is full screen/landscape
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("fitsObject", fitsObject);
		savedInstanceState.putBoolean("fullscreen", fullscreen);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		fitsObject = savedInstanceState.getParcelable("fitsObject");
		fullscreen = savedInstanceState.getBoolean("fullscreen");
	}
	
	@Override
	public void onBackPressed() {
		if (fullscreen){
			player.setFullscreen(false);
		} else{
			super.onBackPressed();
		}
	}
	
	//Handle the download button text
	@Override
	public void onResume() {
		super.onResume();
		if(savedTree.exists()) {
			download.setText("Open Tree");
		} else {
			download.setText("Download");
		}
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
//	    MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(R.menu.list_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case android.R.id.home:
//	            NavUtils.navigateUpFromSameTask(this);
	        	onBackPressed();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	//For Download Manager
	//TODO: how to handle failure to connect (probably none)
	public void startDownload(String filePath) {
		String urlToDownload = "http://" + Constants.HOSTNAME + "/usbong/trees/" + filePath;	    	
		final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlToDownload));
		request.setDescription("Downloading: " + filePath);
		request.setTitle("Downloading: " + filePath);
		// in order for this if to run, you must use the android 3.2 to compile your app
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			request.allowScanningByMediaScanner();
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}

		request.setDestinationInExternalPublicDir("/usbong/usbong_trees/", filePath);

		// get download service and enqueue file
		final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request); //Download file

	}


	BroadcastReceiver onComplete = new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent intent) {
			download.setText("Open Tree");
			isFileDownloaded = true;
			Toast.makeText(ctxt, "Done", Toast.LENGTH_LONG).show();
		}
	};

	BroadcastReceiver onNotificationClick = new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent intent) {
			if(isFileDownloaded) {
				Intent i = new Intent(SingleItemViewWithFragment.this, UsbongDecisionTreeEngineActivity.class);
				i.putExtra(Constants.UTREE_KEY, UsbongUtils.removeExtension(fitsObject.getFILEPATH()));
				PendingIntent pI = PendingIntent.getActivity(SingleItemViewWithFragment.this, 0, i, 0);
				Notification n  = new Notification.Builder(SingleItemViewWithFragment.this)
				.setContentTitle("Usbong FITS Download")
				.setContentText("File downloaded")
				.setSmallIcon(R.drawable.usbong_icon)
				.setContentIntent(pI) //TODO change this to usbong app open tree immediately
				.setAutoCancel(true).build();
				n.flags |= Notification.FLAG_AUTO_CANCEL;

				NotificationManager notificationManager =
						(NotificationManager) SingleItemViewWithFragment.this.getSystemService(Context.NOTIFICATION_SERVICE);

				notificationManager.cancel(0);

				notificationManager.notify(0, n);
			}
		}
	};
}