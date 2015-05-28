package usbong.android.community;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class DatabaseAction extends AsyncTask<String, Void, String> {
	private static String TAG = "usbong.community.IterateDownload";
	private String filePath = "";
	private String columnName = "";
	private String action = "";
	private URL url;
	private HttpURLConnection conn;
				
	public DatabaseAction() {
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	protected String doInBackground(String... data) {
		//TODO: check if data is null
		filePath = data[0];
		columnName = data[1];
		action = data[2];
		String response = "";
		
		try {
			url = new URL(Constants.FITS_ITERATE_DOWNLOAD);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			JSONObject j = new JSONObject();
			j.put(Constants.FILEPATH, filePath);
			j.put(Constants.COLUMN, columnName);
			j.put(Constants.ACTION, action);
			
			byte[] postData = j.toString().getBytes();
			
			OutputStream out = conn.getOutputStream();

		    out.write(postData);
		    out.close();

	        if(conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
	            throw new RuntimeException("Failed : HTTP error code : "
	                + conn.getResponseCode());
	        } else {
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
		return response;
	}
	
	@Override
	protected void onPostExecute(String result){
		Log.d(TAG, "Response:" + result);        
	}
}
