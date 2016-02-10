package usbong.android.utils;

import usbong.android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PurchaseLanguageBundleListAdapter extends BaseAdapter
{
	Activity myActivity;
	private String[][] languageBundleList;
	
	public PurchaseLanguageBundleListAdapter(Activity a)
	{
		myActivity = a;
		languageBundleList = new String[3][2];

		languageBundleList[0][0] = "Local Languages";
		languageBundleList[1][0] = "Foreign Languages";
		languageBundleList[2][0] = "All Languages";

		languageBundleList[0][1] = "US$0.99";
		languageBundleList[1][1] = "US$1.99";
		languageBundleList[2][1] = "US$2.99";
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return languageBundleList.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return languageBundleList[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = myActivity.getLayoutInflater();
		View view = null;
		try {
			if (convertView==null)
			{
				view = inflater.inflate(R.layout.purchase_language_bundle_list_selection, null);
			}
			else
			{
				view = convertView;
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		/*//commented out by Mike, 10 June 2015			
		ImageView image = (ImageView) view.findViewById(R.id.imageView1);
*//*
		ImageView banner_image = (ImageView) view.findViewById(R.id.banner_imageView);
*/
		
		TextView nameOfBundleText = (TextView) view.findViewById(R.id.textView1);
		TextView priceOfBundleText = (TextView) view.findViewById(R.id.textView2);		

		nameOfBundleText.setText(languageBundleList[position][0]);
		priceOfBundleText.setText(languageBundleList[position][1]);
		
		return view;
	}
}