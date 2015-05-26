package usbong.android.community;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class IterateDownload extends AsyncTask<String, Void, String> {
	private static String TAG = "usbong.community.IterateDownload";
	private String content =  null;
	private StringEntity se;
	private String filePath;
	private HttpPost httpPost;
				
	public IterateDownload() {
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	protected String doInBackground(String... data) {
		//TODO: check if data is null
		filePath = data[0];
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpParams params = httpclient.getParams();
	    HttpConnectionParams.setConnectionTimeout(params, Constants.REGISTRATION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, Constants.WAIT_TIMEOUT);
		
		try {
			httpPost = new HttpPost(Constants.FITS_ITERATE_DOWNLOAD);
			
			JSONObject j = new JSONObject();
			j.put(Constants.FILEPATH, filePath);
			
			se = new StringEntity(j.toString());		
			se.setContentType("application/json");			
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setEntity(se);			
			HttpResponse response = httpclient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				content = out.toString();
			} else{
				Log.d(TAG, statusLine.getStatusCode() + "");
				//Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			Log.w(TAG, "HTTP2:" + e );
			content = e.getMessage();
		} catch (IOException e) {
			Log.w(TAG, "HTTP3:" + e );
			content = e.getMessage();
		}catch (Exception e) {
			Log.w(TAG, "HTTP4:" + e );
			content = e.getMessage();
		}
		return content;
	}
	
	@Override
	protected void onPostExecute(String result){
		Log.d(TAG, "Response:" + content);        
	}
}
