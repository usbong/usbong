package usbong.android.community;

import java.io.File;
import java.util.ArrayList;

import usbong.android.R;
import usbong.android.UsbongDecisionTreeEngineActivity;
import usbong.android.community.DownloadTreeAsync.AsyncResponse;
import usbong.android.utils.UsbongUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
/*
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
*/
import com.nostra13.universalimageloader.core.ImageLoader;

//ODO: Add a way to display (callback) if download has been completed.
//http://stackoverflow.com/questions/8937817/downloadmanager-action-download-complete-broadcast-receiver-receiving-same-downl
public class ListViewAdapter extends BaseAdapter implements AsyncResponse {
	private static final String TAG = "usbong.usbongcommunitydraft.ListViewAdapter";
	Context context;
	LayoutInflater inflater;
	//NonLibraryImageLoader imageLoader;
	private ArrayList<FitsObject> fitsObjects = new ArrayList<FitsObject>();
	private DownloadTreeAsync downloadTask;
		
	public ListViewAdapter(Context context, ArrayList<FitsObject> fitsObjects) {
		this.context = context;
		this.fitsObjects = fitsObjects;
		inflater = LayoutInflater.from(context);
		//imageLoader = new NonLibraryImageLoader(context);
	}
	
	public class ViewHolder {
		ImageView icon;
		TextView uploader;
		TextView fileName;
		Button overflow;
		ImageView rating;
		ImageView voteImage;
		TextView voteCount;
		TextView downloadCount;
	}
	
	@Override
	public int getCount() {
		return fitsObjects.size();
	}

	@Override
	public Object getItem(int position) {
		return fitsObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		final String url;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.listview_item, parent, false);
			// Locate the ImageView in gridview_item.xml
			holder.icon = (ImageView) view.findViewById(R.id.icon);
			holder.uploader = (TextView) view.findViewById(R.id.uploadername);
			holder.fileName = (TextView) view.findViewById(R.id.fileName);
			holder.overflow = (Button) view.findViewById(R.id.overflow);
//			holder.rating = (ImageView) view.findViewById(R.id.ratingImage);
/*
			holder.voteImage = (ImageView) view.findViewById(R.id.voteImage);
			holder.voteCount = (TextView) view.findViewById(R.id.voteCount);
*/			
			holder.downloadCount = (TextView) view.findViewById(R.id.downloadCount);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		// Load image into GridView
		
		url = "http://img.youtube.com/vi/" + UsbongUtils.parseYouTubeLink(fitsObjects.get(position).getYOUTUBELINK()) + "/hqdefault.jpg";
	
		//		Glide.with(context)
		//			.load(url)
		//			.diskCacheStrategy(DiskCacheStrategy.ALL)
		//			.placeholder(R.drawable.usbong_logo)
		//			.into(holder.icon);
				
		if(!ImageLoader.getInstance().isInited()) {
			UsbongUtils.initDisplayAndConfigOfUIL(context);
		}
		ImageLoader.getInstance().displayImage(url, holder.icon);
		//END TEST

//		holder.uploader.setText(fitsObjects.get(position).getUPLOADER());
		//added by Mike, 31 May 2015
		if (fitsObjects.get(position).getUPLOADER().length()>25) {
			String s = fitsObjects.get(position).getUPLOADER().substring(0, 25)+"...";
			holder.uploader.setText(s);
		}
		else {
			holder.uploader.setText(fitsObjects.get(position).getUPLOADER());
		}

		if (fitsObjects.get(position).getFILENAME().length()>18) {
			String s = fitsObjects.get(position).getFILENAME().substring(0, 18)+"...";
			holder.fileName.setText(s);
		}
		else {
			holder.fileName.setText(fitsObjects.get(position).getFILENAME());
		}


/*		//commented out by Mike, 30 May 2015
		switch(fitsObjects.get(position).getRATING()) {
		default:
		case 0:
		case 1:
			holder.rating.setImageResource(R.drawable.one);
			break;
		case 2:
			holder.rating.setImageResource(R.drawable.two);
			break;
		case 3:
			holder.rating.setImageResource(R.drawable.three);
			break;
		case 4:
			holder.rating.setImageResource(R.drawable.four);
			break;
		case 5:
			holder.rating.setImageResource(R.drawable.five);
			break;
		}
*/		
		//added by Mike, 30 May 2015
		holder.downloadCount.setText("Download Count: "+fitsObjects.get(position).getDOWNLOADCOUNT());

		// Capture GridView item click
		view.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				// Send single item click data to SingleItemView Class
//				Intent intent = new Intent(context, SingleItemView.class);
				Intent intent = new Intent(context, SingleItemViewWithFragment.class);
				Bundle fitsObjectBundle = new Bundle();
				fitsObjectBundle.putParcelable(Constants.BUNDLE, fitsObjects.get(position));
				intent.putExtras(fitsObjectBundle);
				context.startActivity(intent);
			}
		});
		
		holder.overflow.setOnTouchListener(new View.OnTouchListener() {
			File savedTree;
			CharSequence[] items = new String[1];
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG, "event.getAction()" + event.getAction());
		        switch (event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		        	Log.d(TAG, "ACTION_DOWN");
		        	return false;
		        case MotionEvent.ACTION_UP:
		        	Log.d(TAG, "ACTION_UP");
		        	File folder = new File(Environment.getExternalStorageDirectory() + "/usbong");
		        	if(!folder.exists())
		        		folder.mkdir();
		        	
            		savedTree = new File(Environment.getExternalStorageDirectory().getPath()
                    		+ "/usbong/usbong_trees/"
                    		+ fitsObjects.get(position).getFILEPATH());
            		
            		if(savedTree.exists()) {
            			items[0] = "Open Tree";
            		} else {
            			items[0] = "Download";
            		}
            		Log.d(TAG, "item: " + items[0]);
                	
            		final ProgressDialog mProgressDialog;

            		// instantiate it within the onCreate method
            		mProgressDialog = new ProgressDialog(context);
            		mProgressDialog.setMessage("Downloading: " + fitsObjects.get(position).getFILEPATH());
            		mProgressDialog.setTitle("Saving trees...");
            		mProgressDialog.setIndeterminate(true);
            		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            		mProgressDialog.setCancelable(false);
            		mProgressDialog.setCanceledOnTouchOutside(false);
            		
		            AlertDialog.Builder builder = new AlertDialog.Builder(context);
		            builder.setItems(items, new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int item) {		     

		            		
		    				if(savedTree.exists()) {
		    	            	Intent i = new Intent(context, UsbongDecisionTreeEngineActivity.class);
		    	            	i.putExtra(Constants.UTREE_KEY, UsbongUtils.removeExtension(fitsObjects.get(position).getFILEPATH()));
		    	            	context.startActivity(i);
		    				} else {
		    					downloadTask = new DownloadTreeAsync(context, mProgressDialog);
		    					downloadTask.execute(fitsObjects.get(position).getFILEPATH());
		    					downloadTask.delegate = ListViewAdapter.this;
		    				}

		                }
		            });		            
    				
    				mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
    				    @Override
    				    public void onCancel(DialogInterface dialog) {
    				        downloadTask.cancel(true);
    				    }
    				});

		             AlertDialog dialog = builder.create();
		             dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      
			         dialog.show();
		        	return false;
		        }
		        return true;
			}
		});
		return view;
	}

	@Override
	public void processFinish(boolean output) {

	}

}
