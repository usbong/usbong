package usbong.android.community;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import usbong.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//http://stackoverflow.com/questions/20302110/struggling-with-youtube-player-support-fragment
//http://stackoverflow.com/questions/14115008/exception-when-using-youtubeplayerfragment-with-fragmentactivity-and-actionbarsh
//http://stackoverflow.com/questions/19848142/how-to-load-youtubeplayer-using-youtubeplayerfragment-inside-another-fragment

//Multiple youtube views (overlay
//http://stackoverflow.com/questions/14014087/views-overlayed-above-youtubeplayerfragment-or-youtubeplayerview-in-the-layout-h
public class YoutubeFragment extends Fragment implements
YouTubePlayer.OnInitializedListener {
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	private static final int RECOVERY_DIALOG_REQUEST = 1;
//	private YouTubePlayerSupportFragment mYoutubePlayerFragment;
	
	public static final YoutubeFragment newInstance(String test)
	{
		YoutubeFragment f = new YoutubeFragment();
		Bundle bdl = new Bundle(1);
	    bdl.putString(EXTRA_MESSAGE, test);
	    f.setArguments(bdl);
	    return f;
	}
	
	//TODO:only initalizing once and not playing again?
//	http://stackoverflow.com/questions/21135478/youtubeview-after-initializing-once-not-initializing-again-using-youtube-api
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		String message = getArguments().getString(EXTRA_MESSAGE);
		View v = inflater.inflate(R.layout.youtube_fragment_layout, container, false);
		TextView messageTextView = (TextView)v.findViewById(R.id.textView);
//		ImageView iv = (ImageView)v.findViewById(R.id.imageView1);
		messageTextView.setText(message);
//		iv.setImageResource(message);
		
		YouTubePlayerSupportFragment mYoutubePlayerFragment = new YouTubePlayerSupportFragment();
	    mYoutubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY, this);
	    FragmentManager fragmentManager = getFragmentManager();
	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	    fragmentTransaction.replace(R.id.youtube_fragment, mYoutubePlayerFragment);
	    fragmentTransaction.commit();
		
        return v;
    }

    
	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider,
			YouTubeInitializationResult errorReason) {
		if (errorReason.isUserRecoverableError()) {
			errorReason.getErrorDialog(this.getActivity(), RECOVERY_DIALOG_REQUEST).show();
		} else {
			String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
			Toast.makeText(this.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
			boolean wasRestored) {
		if (!wasRestored) {
			player.cueVideo("nCgQDjiotG0");
		}
	}
}
