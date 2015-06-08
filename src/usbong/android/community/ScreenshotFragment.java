package usbong.android.community;

import java.util.List;

import usbong.android.R;
import usbong.android.utils.UsbongUtils;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
	private static final String TAG = "ScreenshotFragment";
	public static final String IMAGE_URL = "EXTRA_MESSAGE";
	  private static final int REQ_START_STANDALONE_PLAYER = 1;
	  private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

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
		ImageButton ib = (ImageButton)v.findViewById(R.id.playVideoButton);
		if(ss.getKEY() == Constants.SCREENSHOT2) {
			ib.setVisibility(View.GONE);
			ImageLoader.getInstance().displayImage(ss.getVAL(), iv);			
		} else {
			if(ss.getVAL() != null) {
				final String videoId = UsbongUtils.parseYouTubeLink(ss.getVAL());
				//TODO: or switch this up with the youtubefragment.java
				Log.d(TAG, "PASSES ELSE SCREENSHOT");
				Log.d(TAG, videoId + "");
				ib.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage("http://img.youtube.com/vi/" + videoId + "/hqdefault.jpg", iv);
				ib.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = null;
						intent = YouTubeStandalonePlayer.createVideoIntent(
								getActivity(), Constants.YOUTUBE_API_KEY, videoId, 0, true, false);						
					    if (intent != null) {
					        if (canResolveIntent(intent)) {
					          startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
					        } else {
					          // Could not resolve the intent - must need to install or update the YouTube API service.
					          YouTubeInitializationResult.SERVICE_MISSING
					              .getErrorDialog(getActivity(), REQ_RESOLVE_SERVICE_MISSING).show();
					        }
					      }
					}
				});
			}
		}

		return v;
	}
	
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != android.app.Activity.RESULT_OK) {
	      YouTubeInitializationResult errorReason =
	          YouTubeStandalonePlayer.getReturnedInitializationResult(data);
	      if (errorReason.isUserRecoverableError()) {
	        errorReason.getErrorDialog(getActivity(), 0).show();
	      } else {
	        String errorMessage =
	            String.format(getString(R.string.error_player), errorReason.toString());
	        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
	      }
	    }
	  }
	
	  private boolean canResolveIntent(Intent intent) {
		    List<ResolveInfo> resolveInfo = getActivity().getPackageManager().queryIntentActivities(intent, 0);
		    return resolveInfo != null && !resolveInfo.isEmpty();
		  }
}
