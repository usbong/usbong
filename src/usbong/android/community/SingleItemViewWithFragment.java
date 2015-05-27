package usbong.android.community;

import java.util.ArrayList;
import java.util.List;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import usbong.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

//http://stackoverflow.com/questions/15484126/using-the-youtube-api-within-a-fragment
public class SingleItemViewWithFragment extends FragmentActivity {
	private MyPageAdapter pageAdapter;
	private MyPageAdapter pageAdapter2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singleitemviewwithfragment);

        List<Fragment> fragments = getFragments();
        
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager)findViewById(R.id.screenshotsViewPager);
        pager.setAdapter(pageAdapter);
        
        List<Fragment> fragments2 = getFragments2();
        
        pageAdapter2 = new MyPageAdapter(getSupportFragmentManager(), fragments2);
        ViewPager pager2 = (ViewPager)findViewById(R.id.youtubeViewPager);
        pager2.setAdapter(pageAdapter2);
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
}