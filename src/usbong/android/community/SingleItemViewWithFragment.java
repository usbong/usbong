package usbong.android.community;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import java.util.ArrayList;
import java.util.List;

import usbong.android.R;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

@SuppressLint("NewApi")
public class SingleItemViewWithFragment extends FragmentActivity implements
YouTubePlayer.OnFullscreenListener,
YouTubePlayer.OnInitializedListener {
	private MyPageAdapter pageAdapter;
	private static final int RECOVERY_DIALOG_REQUEST = 1;
	private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
			? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
					: ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

	private LinearLayout baseLayout;
	private View otherViews;
	private ScrollView s;
	private ViewPager pager;
	
	private boolean fullscreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.singleitemviewwithfragment);
		
		List<Fragment> fragments = getFragments();
        
		s = (ScrollView) findViewById(R.id.sv);
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        pager = (ViewPager)findViewById(R.id.screenshotsViewPager);
        pager.setAdapter(pageAdapter);
        
//        pager.setOnTouchListener(new View.OnTouchListener() {
//
//            int dragthreshold = 30;
//            int downX;
//            int downY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    downX = (int) event.getRawX();
//                    downY = (int) event.getRawY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    int distanceX = Math.abs((int) event.getRawX() - downX);
//                    int distanceY = Math.abs((int) event.getRawY() - downY);
//
//                    if (distanceY > distanceX && distanceY > dragthreshold) {
//                    	pager.getParent().requestDisallowInterceptTouchEvent(false);
//                        s.getParent().requestDisallowInterceptTouchEvent(true);
//                    } else if (distanceX > distanceY && distanceX > dragthreshold) {
//                    	pager.getParent().requestDisallowInterceptTouchEvent(true);
//                        s.getParent().requestDisallowInterceptTouchEvent(false);
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                    s.getParent().requestDisallowInterceptTouchEvent(false);
//                    pager.getParent().requestDisallowInterceptTouchEvent(false);
//                    break;
//                }
//                return false;
//            }
//        });
        
		baseLayout = (LinearLayout) findViewById(R.id.layout);
		otherViews = findViewById(R.id.other_views);
		YouTubePlayerFragment youTubePlayerFragment =
				(YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.player);
		youTubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY, this);
		doLayout();
	}

    private List<Fragment> getFragments(){
    	List<Fragment> fList = new ArrayList<Fragment>();
    	
    	fList.add(ScreenshotFragment.newInstance(R.drawable.usbong_icon));
    	fList.add(ScreenshotFragment.newInstance(R.drawable.ic_action_search));
    	fList.add(ScreenshotFragment.newInstance(R.drawable.up));
    	
    	return fList;
    }

    private List<Fragment> getFragments2(){
    	List<Fragment> fList = new ArrayList<Fragment>();
    	
    	fList.add(YoutubeFragment.newInstance("fragment1"));
    	fList.add(YoutubeFragment.newInstance("fragment2"));
    	fList.add(YoutubeFragment.newInstance("fragment3"));
    	
    	return fList;
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
		player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		player.setOnFullscreenListener(this);
		if (!wasRestored) {
			player.cueVideo("nCgQDjiotG0");
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

}