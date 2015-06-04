package usbong.android.community;

import java.util.List;

import usbong.android.R;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ScreenshotFragment extends Fragment {
	public static final String IMAGE_URL = "EXTRA_MESSAGE";

	public static final ScreenshotFragment newInstance(ScreenshotsInViewPager ss)
	{
		ScreenshotFragment f = new ScreenshotFragment();
		Bundle bdl = new Bundle(1);
		bdl.putParcelable(Constants.BUNDLE, ss);
		f.setArguments(bdl);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		final ScreenshotsInViewPager ss = getArguments().getParcelable(Constants.BUNDLE);
		View v = inflater.inflate(R.layout.screenshot_fragment_layout, container, false);
		ImageView iv = (ImageView)v.findViewById(R.id.screenshot);
		ImageButton ib = (ImageButton)v.findViewById(R.id.screenshot);
		if(ss.getKEY() == Constants.SCREENSHOT2) {
			ib.setVisibility(View.GONE);
			ImageLoader.getInstance().displayImage(ss.getVAL(), iv);			
		} else {
			//TODO: or switch this up with the youtubefragment.java
			ib.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage("http://img.youtube.com/vi/" + ss.getVAL() + "/hqdefault.jpg", iv);
			ib.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = null;
					intent = YouTubeStandalonePlayer.createVideoIntent(
							getActivity(), Constants.YOUTUBE_API_KEY, ss.getVAL(), 0, true, true);
					startActivity(intent);
				}
			});
		}

		return v;
	}
}
