package usbong.android.community;

import usbong.android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

//TODO: Need to hack usbong decision tree engine act to give me api to send users to the 
//needed tree when pressing "open"
public class UsbongTreeDisplay extends Activity {
		@SuppressWarnings("unused")
		private final static String TAG = "usbong.usbongcommunitydraft.UsbongTreeDisplay";
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.tree_display);
			Intent i = getIntent();
			String treePath = i.getStringExtra(Constants.UTREE_KEY);
			TextView treeText = (TextView) findViewById(R.id.treeName);
			treeText.setText(treePath);
		}
}
