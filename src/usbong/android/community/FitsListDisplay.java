package usbong.android.community;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import usbong.android.R;
import usbong.android.utils.UsbongUtils;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class FitsListDisplay extends ActionBarActivity {
	private final String TAG = "usbong.usbongcommunitydraft.FitsGridDisplay";
	private SwipeRefreshLayout swipeContainer;
	private ListView listView;
	private ProgressDialog dialog;
	private ListViewAdapter adapter;
	private ArrayList<FitsObject> fitObjects = new ArrayList<FitsObject>();
	private SharedPreferences editor;
	private TextView error;
//	private String jsonString = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from gridview_main.xml
		
		setContentView(R.layout.fitgriddisplay_main);
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

        String appname = getResources().getString(R.string.app_name);
        editor = getSharedPreferences(appname, Context.MODE_PRIVATE);

        
        
		// Execute RemoteDataTask AsyncTask
        if((editor.getString(Constants.JSON_KEY, "").length() == 0) && UsbongUtils.hasNetworkConnection(this)) {
        	error.setVisibility(View.GONE);
        	new GetFitsListAsync().execute();
        } else {
        	if(editor.getString(Constants.JSON_KEY, "").length() != 0) {
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
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
//		jsonString = savedInstanceState.getString("jsonString");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(UsbongUtils.hasNetworkConnection(FitsListDisplay.this)) {
    		error.setVisibility(View.GONE);
		} else {
    		error.setVisibility(View.VISIBLE);
    		error.setText("Warning: Currently in offline mode.");
		}
	}
	
	public class GetFitsListAsync extends AsyncTask<Void, Void, String> {
		private final String TAG = "usbong.usbongcommunitydraft.GetFitsListAsync";
	    private final DefaultHttpClient httpclient = new DefaultHttpClient();
	    private final HttpParams params = httpclient.getParams();
	    private String content =  null;
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        dialog = new ProgressDialog(FitsListDisplay.this);
	        dialog.setTitle("Planting trees...");
	        dialog.setMessage("Please wait...");
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(false);
	        dialog.setCanceledOnTouchOutside(false);
	        dialog.show();
	    }
	    
		@Override
		protected String doInBackground(Void... inputs) {
	        try {
	            HttpConnectionParams.setConnectionTimeout(params, Constants.REGISTRATION_TIMEOUT);
	            HttpConnectionParams.setSoTimeout(params, Constants.WAIT_TIMEOUT);
	            HttpGet request = new HttpGet();

	            //raw HTTP post
	            request.setHeader("Accept", "application/json");
	            request.setHeader("Content-type", "application/json");
	            request.setURI(new URI(Constants.FITS_LIST_SERVER));
	            HttpResponse response = httpclient.execute(request);
	            StatusLine statusLine = response.getStatusLine();

	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                content = out.toString();
	            } else{
	                //Closes the connection.
	            	Log.d(TAG, "Error at status code");
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	        	Log.w(TAG, "HTTP2:" + e );
	            content = e.getMessage();
	            cancel(true);
	        } catch (IOException e) {
	        	Log.w(TAG, "HTTP3:" + e );
	            content = e.getMessage();
	            cancel(true);
	        }catch (Exception e) {
	        	Log.w(TAG, "HTTP4:" + e );
	            content = e.getMessage();
	            cancel(true);
	        }
	        Log.d(TAG, "Response: " + content);
	        return content;
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
				Log.d(TAG, utree.getString(Constants.FILENAME));
				Log.d(TAG, utree.getString(Constants.FILEPATH));
				Log.d(TAG, utree.getString(Constants.RATING));
				Log.d(TAG, utree.getString(Constants.UPLOADER));
				Log.d(TAG, utree.getString(Constants.DESCRIPTION));
				Log.d(TAG, utree.getString(Constants.ICON));
				Log.d(TAG, utree.getString(Constants.YOUTUBELINK));
				Log.d(TAG, utree.getString(Constants.DATEUPLOADED));
				Log.d(TAG, utree.getString(Constants.DOWNLOADCOUNT));
				FitsObject fO = new FitsObject(utree);
				fObjs.add(fO);
			}
			fitObjects.clear();
			fitObjects.addAll(fObjs);
			listView = (ListView) findViewById(R.id.listview);
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
}
