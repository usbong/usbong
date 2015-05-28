package usbong.android.community;

import com.nostra13.universalimageloader.core.ImageLoader;

import usbong.android.R;
import usbong.android.utils.UsbongUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ScreenshotFragment extends Fragment {
	public static final String IMAGE_URL = "EXTRA_MESSAGE";
	
	public static final ScreenshotFragment newInstance(String imgUrl)
	{
		ScreenshotFragment f = new ScreenshotFragment();
		Bundle bdl = new Bundle(1);
	    bdl.putString(IMAGE_URL, imgUrl);
	    f.setArguments(bdl);
	    return f;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		String imgUrl = getArguments().getString(IMAGE_URL);
		View v = inflater.inflate(R.layout.screenshot_fragment_layout, container, false);
		TextView messageTextView = (TextView)v.findViewById(R.id.textView);
		ImageView iv = (ImageView)v.findViewById(R.id.imageView1);
		messageTextView.setText(imgUrl + "");
		
//		iv.setImageResource(imgUrl);
		ImageLoader.getInstance().displayImage(imgUrl, iv);
		
        return v;
    }
	
}
