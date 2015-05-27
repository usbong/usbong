package usbong.android.community;

import usbong.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ScreenshotFragment extends Fragment {
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	
	public static final ScreenshotFragment newInstance(int resId)
	{
		ScreenshotFragment f = new ScreenshotFragment();
		Bundle bdl = new Bundle(1);
	    bdl.putInt(EXTRA_MESSAGE, resId);
	    f.setArguments(bdl);
	    return f;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		int message = getArguments().getInt(EXTRA_MESSAGE);
		View v = inflater.inflate(R.layout.screenshot_fragment_layout, container, false);
		TextView messageTextView = (TextView)v.findViewById(R.id.textView);
		ImageView iv = (ImageView)v.findViewById(R.id.imageView1);
		messageTextView.setText(message + "");
		iv.setImageResource(message);
		
        return v;
    }
	
}
