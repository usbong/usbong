package usbong.android.community;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import usbong.android.R;
import usbong.android.UsbongDecisionTreeEngineActivity;
import usbong.android.utils.UsbongUtils;
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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

@SuppressLint("NewApi")
public class DownloadTreeAsync extends AsyncTask<String, Integer, String> {
	private static final String TAG = "usbong.android.community.DownloadTreeAsync";
	public AsyncResponse delegate = null;

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog mProgressDialog;
    private String savedPathAfterDownload = "";
    private String filePath = "";
    
    public DownloadTreeAsync(Context context, ProgressDialog progressDialogue) {
        this.context = context;
        this.mProgressDialog = progressDialogue;
        this.filePath = "";
    }
    
    public DownloadTreeAsync(Context context) {
        this.context = context;
        this.mProgressDialog = null;
        this.filePath = "";
    }

    @SuppressWarnings("resource")
	@Override
    protected String doInBackground(String... sUrl) {
    	filePath = sUrl[0];
    	String urlToDownload = "http://" + Constants.HOSTNAME + "/usbong/trees/" + filePath;
    	Log.d(TAG, urlToDownload);
    	InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlToDownload);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
			connection.setConnectTimeout(15000);
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
             
            savedPathAfterDownload = Environment.getExternalStorageDirectory().getPath()
            		+ "/usbong/usbong_trees/"
            		+ filePath;
            output = new FileOutputStream(savedPathAfterDownload);

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
            
            new DatabaseAction().execute(filePath, Constants.DOWNLOADCOUNT, "DOWNLOAD");
            
        } catch (Exception e) {
        	cancel(true);
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            	cancel(true);
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
    	Toast toast;
    	View view;
        mWakeLock.release();
        if(mProgressDialog != null)
        	mProgressDialog.dismiss();
        PendingIntent nullIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        if (result != null) {
        	File file = new File(savedPathAfterDownload);
        	file.delete();
            Notification n  = new Notification.Builder(context)
		        .setContentTitle("Usbong FITS Download")
		        .setContentText("Download error: "+result)
		        .setSmallIcon(R.drawable.usbong_icon)
		        .setContentIntent(nullIntent) //TODO change this to usbong app open tree immediately
		        .setAutoCancel(true).build();
            n.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            
            notificationManager.cancel(0);
            
            notificationManager.notify(0, n);
            toast = Toast.makeText(context,"Download error: " + result, Toast.LENGTH_LONG);
            view = toast.getView();
            view.setBackgroundResource(R.drawable.alternatetoastbox);
            toast.setView(view);
			toast.show();
            delegate.processFinish(false);
        } else {
        	Log.d(TAG, "intent: " + filePath);
			Intent i = new Intent(context, UsbongDecisionTreeEngineActivity.class);
			i.putExtra(Constants.UTREE_KEY, UsbongUtils.removeExtension(filePath));
			PendingIntent pI = PendingIntent.getActivity(context, 0, i, 0);
            Notification n  = new Notification.Builder(context)
		        .setContentTitle("Usbong FITS Download")
		        .setContentText("File downloaded")
		        .setSmallIcon(R.drawable.usbong_icon)
		        .setContentIntent(pI) //TODO change this to usbong app open tree immediately
		        .setAutoCancel(true).build();
            n.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            
            notificationManager.cancel(0);
            
            notificationManager.notify(0, n);
            toast = Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT);
            view = toast.getView();
            view.setBackgroundResource(R.drawable.alternatetoastbox);
            toast.setView(view);
			toast.show();
            delegate.processFinish(true);
        }
    }
    
	public interface AsyncResponse {
	    void processFinish(boolean output);
	}
}