package usbong.android.community;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import usbong.android.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.widget.Toast;

@SuppressLint("NewApi")
public class DownloadTreeAsync extends AsyncTask<String, Integer, String> {
	public AsyncResponse delegate = null;

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog mProgressDialog;

    public DownloadTreeAsync(Context context, ProgressDialog progressDialogue) {
        this.context = context;
        this.mProgressDialog = progressDialogue;
    }
    
    public DownloadTreeAsync(Context context) {
        this.context = context;
        this.mProgressDialog = null;
    }

    @SuppressWarnings("resource")
	@Override
    protected String doInBackground(String... sUrl) {
    	String filePath = sUrl[0];
    	String urlToDownload = "http://" + Constants.HOSTNAME + "/usbong/trees/" + filePath;
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlToDownload);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
             
            output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()
            		+ "/usbong/usbong_trees/"
            		+ filePath);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
            
            new IterateDownload().execute(filePath);
            
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user 
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
             getClass().getName());
        mWakeLock.acquire();
        if(mProgressDialog != null)
        	mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        if(mProgressDialog != null) {
	        mProgressDialog.setIndeterminate(false);
	        mProgressDialog.setMax(100);
	        mProgressDialog.setProgress(progress[0]);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        if(mProgressDialog != null)
        	mProgressDialog.dismiss();
        PendingIntent nullIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        if (result != null) {
        	
            Notification n  = new Notification.Builder(context)
		        .setContentTitle("Usbong FITS Download")
		        .setContentText("Download error: "+result)
		        .setSmallIcon(R.drawable.loading)
		        .setContentIntent(nullIntent) //TODO change this to usbong app open tree immediately
		        .setAutoCancel(true).build();
            n.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            
            notificationManager.cancel(0);
            
            notificationManager.notify(0, n);
            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            delegate.processFinish(false);
        } else {
            Notification n  = new Notification.Builder(context)
		        .setContentTitle("Usbong FITS Download")
		        .setContentText("File downloaded")
		        .setSmallIcon(R.drawable.loading)
		        .setContentIntent(nullIntent) //TODO change this to usbong app open tree immediately
		        .setAutoCancel(true).build();
            n.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            
            notificationManager.cancel(0);
            
            notificationManager.notify(0, n);
            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
            delegate.processFinish(true);
        }
    }
    
	public interface AsyncResponse {
	    void processFinish(boolean output);
	}
}