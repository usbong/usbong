package usbong.android.community;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import usbong.android.R;
import usbong.android.utils.UsbongUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class FitsListDisplay extends ActionBarActivity {
	private final String TAG = "usbong.usbongcommunitydraft.FitsListDisplay";
	private SwipeRefreshLayout swipeContainer;
	private ListView listView;
	private ProgressDialog dialog;
	private ListViewAdapter adapter;
	private ArrayList<FitsObject> fitObjects = new ArrayList<FitsObject>();
	private SharedPreferences editor;
	private TextView error;
	
	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		
		super.onCreate(savedInstanceState);
		// Get the view from gridview_main.xml
		
		setContentView(R.layout.fitgriddisplay_main);
        String appname = getResources().getString(R.string.app_name);
        editor = getSharedPreferences(appname, Context.MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
        	int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        	decorView.setSystemUiVisibility(uiOptions);
        }
		listView = (ListView) findViewById(R.id.listview);
		error = (TextView) findViewById(R.id.errorMessage);
		swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
		
		swipeContainer.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if(UsbongUtils.hasNetworkConnection(FitsListDisplay.this)) {
					new GetFitsListAsync().execute();
				} else {
	        		error.setVisibility(View.VISIBLE);
	        		error.setText("Please connect to the internet first.");
	        		swipeContainer.setRefreshing(false);
				}
			}
		});
		
		// Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);
/*
		// Execute RemoteDataTask AsyncTask
        //TODO:Add expiration of current data so that a refresh is done automatically 
        if((editor.getString(Constants.JSON_KEY, "").length() == 0) && UsbongUtils.hasNetworkConnection(this)) {
*/
        if(UsbongUtils.hasNetworkConnection(this)) {
        	error.setVisibility(View.GONE);
        	new GetFitsListAsync().execute();
        } else {
/*        	if(editor.getString(Constants.JSON_KEY, "").length() != 0) {
 */
        	if(editor.contains(Constants.JSON_KEY)) {
        		error.setVisibility(View.VISIBLE);
        		error.setText("Warning: Currently in offline mode.");
            	ParseJSONToFitsArray(editor.getString(Constants.JSON_KEY, ""));
        	} else {
        		error.setVisibility(View.VISIBLE);
        		error.setText("Please connect to the internet first.");
        	}
        }    	
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
//		savedInstanceState.putString("jsonString", jsonString);
		// save index and top position
/*		
		int index = listView.getScrollX();
		editor.edit().putInt("index", index).apply();
*/		
		int currentListViewPosition = listView.getScrollX();
		editor.edit().putInt("index", currentListViewPosition).apply();
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		int index = editor.getInt("index", 0);
		listView.setScrollX(index);
/*		
		if(UsbongUtils.hasNetworkConnection(FitsListDisplay.this)) {
    		error.setVisibility(View.GONE);
		} else {
    		error.setVisibility(View.VISIBLE);
    		error.setText("Warning: Currently in offline mode.");
		}
*/
		if(UsbongUtils.hasNetworkConnection(this)) {
        	error.setVisibility(View.GONE);
        } else {
        	error.setText("Warning: Currently in offline mode.");
        	ParseJSONToFitsArray(editor.getString(Constants.JSON_KEY, ""));
        }
	}
	
	public class GetFitsListAsync extends AsyncTask<Void, Void, String> {
		private final String TAG = "usbong.usbongcommunitydraft.GetFitsListAsync";
		private URL url;
		private HttpURLConnection conn;
		private String response = "";
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        dialog = new ProgressDialog(FitsListDisplay.this);
	        dialog.setTitle("Planting trees...");
//	        dialog.setMessage("Please wait..."); //edited by Mike, 9 July 2015
	        dialog.setMessage("This takes only a short while.");
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(false);
	        dialog.setCanceledOnTouchOutside(false);
	        dialog.show();
	    }
	    
		@Override
		protected String doInBackground(Void... inputs) {
	        try {
				url = new URL(Constants.FITS_LIST_SERVER);
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000);
				conn.setConnectTimeout(15000);
	
		        if(conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
		            throw new RuntimeException("Failed : HTTP error code : "
		                + conn.getResponseCode());
		        } else {
		        	Log.d(TAG, "getsListSuccessfully");
			        BufferedReader br = new BufferedReader(new InputStreamReader(
			                (conn.getInputStream())));
			        String line;
			        while ((line=br.readLine()) != null) {
		                response+=line;
		            }
		        }
		        conn.disconnect();        
			}catch(IOException e) {
				Log.w(TAG, "HTTP3:" + e );
				response = e.getMessage();
			}catch(Exception e) {
				Log.w(TAG, "HTTP4:" + e );
				response = e.getMessage();
			}
	        if(UsbongUtils.IS_IN_DEBUG_MODE)
	        	Log.d(TAG, "Response: " + response);
	        return response;
	    }
		
	    @Override
	    protected void onPostExecute(String result){
	    	SharedPreferenceEditor.getInstance().save(editor.edit().putString(Constants.JSON_KEY, result));
	    	ParseJSONToFitsArray(result);
	    	dialog.dismiss();
	    	
	    }
	}
	
	private void ParseJSONToFitsArray(String result) {
		Log.d(TAG, "ParseJSONToFitsArray");
		ArrayList<FitsObject> fObjs = new ArrayList<FitsObject>();
    	
    	try {
			JSONArray a = new JSONArray(result);
			for(int i = 0; i < a.length(); ++i) {
				JSONObject utree = (JSONObject) a.get(i);
				if(UsbongUtils.IS_IN_DEBUG_MODE) {
					Log.d(TAG, utree.getString(Constants.FILENAME));
					Log.d(TAG, utree.getString(Constants.FILEPATH));
					Log.d(TAG, utree.getString(Constants.RATING));
					Log.d(TAG, utree.getString(Constants.UPLOADER));
					Log.d(TAG, utree.getString(Constants.DESCRIPTION));
					Log.d(TAG, utree.getString(Constants.ICON));
					Log.d(TAG, utree.getString(Constants.YOUTUBELINK));
					Log.d(TAG, utree.getString(Constants.DATEUPLOADED));
					Log.d(TAG, utree.getString(Constants.DOWNLOADCOUNT));
				}
				FitsObject fO = new FitsObject(utree);
				fObjs.add(fO);
			}
			fitObjects.clear();
			fitObjects.addAll(fObjs);
			adapter = new ListViewAdapter(FitsListDisplay.this, fitObjects);
			listView.setAdapter(adapter);
			error.setVisibility(View.GONE);
			swipeContainer.setRefreshing(false);
    	} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.list_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_search:
	            Toast.makeText(this, "Pressed search", Toast.LENGTH_SHORT).show();
	            return true;
	        case R.id.action_settings:
	        	Toast.makeText(this, "Pressed settings", Toast.LENGTH_SHORT).show();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
