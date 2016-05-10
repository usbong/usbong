package usbong.android.utils;

import java.util.ArrayList;

import org.json.JSONObject;

import usbong.android.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;

public class PurchaseLanguageBundleListAdapter extends BaseAdapter
{
	private int languageBundleListSize=2; //default is 2
    private ArrayList<String> defaultSkuList;
	private Activity myActivity;
	public String[][] languageBundleList;
    private IInAppBillingService mService; //added by Mike, 20160426	
	private Bundle buyIntentBundle; //added by Mike, 20160426	
	private PendingIntent pendingBuyIntent; //added by Mike, 20160506
	public Bundle myOwnedItems;
	
	private TextView nameOfBundleText;
	private TextView priceOfBundleText;
	private Button buyButton;
	
	private int myPos;
		
	public PurchaseLanguageBundleListAdapter(Activity a, Bundle ownedItems, IInAppBillingService mS)
	{
		myActivity = a;
		mService = mS;
		
		myOwnedItems=ownedItems;
		
	    languageBundleList = new String[languageBundleListSize][2];
		languageBundleList[0][0] = "All Local Languages";
		languageBundleList[1][0] = "All Foreign Languages";
		languageBundleList[0][1] = UsbongConstants.DEFAULT_PRICE;
		languageBundleList[1][1] = UsbongConstants.DEFAULT_PRICE;

	    defaultSkuList = new ArrayList<String> ();
	    defaultSkuList.add(UsbongConstants.ALL_LOCAL_LANGUAGES_PRODUCT_ID);
	    defaultSkuList.add(UsbongConstants.ALL_FOREIGN_LANGUAGES_PRODUCT_ID);	    
	    
	    //added by Mike, 20160510
	    if (UsbongUtils.hasUnlockedAllLanguages) {
			languageBundleList[0][1] = "Owned";
			languageBundleList[1][1] = "Owned";
/*	//do these in UsbongDecisionTreeEngineActivity
			UsbongUtils.hasUnlockedLocalLanguages=true;
			UsbongUtils.hasUnlockedForeignLanguages=true;
*/			
	    }
	    else {
			//added by Mike, 20160425
		    if (ownedItems!=null) {
		    	Log.d(">>>","ownedItems NOT null");
				new MyPLBLABackgroundTask().execute();    
	/*	    	
		    	int response = -1;
				response = myOwnedItemsGetResponseCode();
				if (response == 0) { //SUCCESS
					updateLanguageBundleList();
				}	
	*/			
		    }
		    else {
		        //Reference: http://stackoverflow.com/questions/23024831/android-shared-preferences-example
		        //; last accessed: 20150609
		        //answer by Elenasys
		        //added by Mike, 20160425
		        SharedPreferences prefs = myActivity.getSharedPreferences(UsbongConstants.MY_PURCHASED_ITEMS, android.content.Context.MODE_PRIVATE);
		        if (prefs!=null) {
					languageBundleList[0][1] = prefs.getString(UsbongConstants.ALL_LOCAL_LANGUAGES_PRODUCT_ID, UsbongConstants.DEFAULT_PRICE);
					languageBundleList[1][1] = prefs.getString(UsbongConstants.ALL_FOREIGN_LANGUAGES_PRODUCT_ID, UsbongConstants.DEFAULT_PRICE);
					
			    	if (languageBundleList[0][1].contains("Owned")) {
		    			UsbongUtils.hasUnlockedLocalLanguages=true;
			    	}
			    	if (languageBundleList[1][1].contains("Owned")) { //foreign
		    			UsbongUtils.hasUnlockedForeignLanguages=true;
			    	}
		        }
			    UsbongUtils.hasLoadedPurchaseLanguageBundleList=true;
		    }		
	    }
	    // if continuationToken != null, call getPurchases again
	    // and pass in the token to retrieve more items
	}

	public void updateLanguageBundleList() {
	   ArrayList<String> ownedSkus =
		      myOwnedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
		   ArrayList<String>  purchaseDataList =
			  myOwnedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
		   ArrayList<String>  signatureList =
			  myOwnedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
/*//not used
		   String continuationToken =
		      ownedItems.getString("INAPP_CONTINUATION_TOKEN");
*/
		   for (int i = 0; i < purchaseDataList.size(); ++i) {
		      String purchaseData = purchaseDataList.get(i);
		      String signature = signatureList.get(i);
		      String sku = ownedSkus.get(i);

//consume product items
		      try {
			      JSONObject o = new JSONObject(purchaseData);
			      String purchaseToken = o.optString("token", o.optString("purchaseToken"));		    	  
			      //Consume purchaseToken
			      mService.consumePurchase(3, myActivity.getPackageName(), purchaseToken);
		      }
		      catch (Exception e) {
		    	  e.printStackTrace();
		      }
		      
	    	  if (sku.contains("local")) {
    			languageBundleList[0][1] = "Owned";
    			UsbongUtils.hasUnlockedLocalLanguages=true;
	    	  }
	    	  else { //foreign
	    		languageBundleList[1][1] = "Owned";			    		  
    			UsbongUtils.hasUnlockedForeignLanguages=true;
	    	  }	    	  
		   }
	}
	
	//added by Mike, 29 Sept. 2015
    //Reference: http://stackoverflow.com/questions/13017122/how-to-show-progressdialog-across-launching-a-new-activity;
    //last accessed: 29 Sept. 2015; answer by: Slartibartfast, 23 Oct. 2012
    class MyPLBLABackgroundTask extends AsyncTask<String, Integer, Boolean> {
		private int response=-1;
		
    	@Override
		protected void onPreExecute() {
			Log.d(">>>>","onPreExecute()");
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			Log.d(">>>>","onPostExecute()");
			if (response == 0) { //SUCCESS
				updateLanguageBundleList();
			}			
		    UsbongUtils.hasLoadedPurchaseLanguageBundleList=true;
		}
		
		@Override
		protected Boolean doInBackground(String... params) {		
			Log.d(">>>>","doInBackground()");
			response = myOwnedItemsGetResponseCode();

		    //Do all your slow tasks here but don't set anything on UI
		    //ALL UI activities on the main thread 		
		    return true;		
		}		
	}
    
	//added by Mike, 29 Sept. 2015
    //Reference: http://stackoverflow.com/questions/13017122/how-to-show-progressdialog-across-launching-a-new-activity;
    //last accessed: 29 Sept. 2015; answer by: Slartibartfast, 23 Oct. 2012
    class BuyBackgroundTask extends AsyncTask<String, Integer, Boolean> {	
    	@Override
		protected void onPreExecute() {
			Log.d(">>>>","onPreExecute()");
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			Log.d(">>>>","onPostExecute()");
		    if (mService!=null) {
				try {					
					if (pendingBuyIntent!=null) {
						myActivity.startIntentSenderForResult(pendingBuyIntent.getIntentSender(),
								   1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
								   Integer.valueOf(0));	
					}
					else {
						//go back to the main thread
			            Handler mainHandler = new Handler(myActivity.getBaseContext().getMainLooper());
			            Runnable myRunnable = new Runnable() {
			            	@Override
			            	public void run() {
						    	new AlertDialog.Builder(myActivity).setTitle("Connection Failure!")
			            		.setMessage("Unable to connect to Google Play. Please make sure that you are connected to the internet.")
								.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
									@Override
									public void onClick(DialogInterface dialog, int which) {	            				
									}
								}).show();							
			            	}
			            };
			            mainHandler.post(myRunnable);
					}				
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		    }
		}
		
		@Override
		protected Boolean doInBackground(String... params) {		
			Log.d(">>>>","doInBackground()");
			processBuy();

		    //Do all your slow tasks here but don't set anything on UI
		    //ALL UI activities on the main thread 		
		    return true;		
		}		
	}

    public int myOwnedItemsGetResponseCode() {
		return myOwnedItems.getInt("RESPONSE_CODE");    	
    }

    public void processBuy() {
		final int pos = myPos;		
    	try {
		    if (mService==null) {
		    	UsbongUtils.initInAppBillingService(myActivity);
		    	mService = UsbongUtils.getInAppMService();
		    }
		    
		    UsbongUtils.generateDateTimeStamp();
		    if (mService!=null) {
                try {
					buyIntentBundle = mService.getBuyIntent(3, myActivity.getPackageName(),
							  defaultSkuList.get(pos), "inapp", UsbongUtils.getDateTimeStamp());				
					pendingBuyIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                } catch (Exception e) {
                	e.printStackTrace();
                }
		    }
		    else {				    					    	
		    	new AlertDialog.Builder(myActivity).setTitle("Connection Failure")
        		.setMessage("Unable to connect to Google Play. Please make sure that you are connected to the internet.")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {	            				
					}
				}).show();
		    }
		  } catch (Exception e) {
				e.printStackTrace();
		  }		
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

	public void setLocalLanguageToOwned() {
		languageBundleList[0][1] = "Owned";	
	}

	public void setForeignLanguageToOwned() {
		languageBundleList[1][1] = "Owned";	
	}

	@SuppressLint("DefaultLocale")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {		
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
		
		nameOfBundleText = (TextView) view.findViewById(R.id.textView1);
		priceOfBundleText = (TextView) view.findViewById(R.id.textView2);		
		buyButton = (Button) view.findViewById(R.id.buyButton);
		
		nameOfBundleText.setText(languageBundleList[position][0]);
		priceOfBundleText.setText(languageBundleList[position][1]);
		
		if (languageBundleList[position][1].equals("Owned")) {
			buyButton.setFocusable(false);
			buyButton.setVisibility(Button.INVISIBLE);
		}
		else {
			buyButton.setFocusable(true);
			buyButton.setVisibility(Button.VISIBLE);			
		}
		
		final int pos = position;		
		buyButton.setOnClickListener(new OnClickListener() {           
			  @Override
			  public void onClick(View v) 
			  {
				  myPos = pos;		
				  new BuyBackgroundTask().execute();				  		
//				  Log.d(">>>>","pressed!"+defaultSkuList.get(pos));
//				  Log.d(">>>>","pressed!"+languageBundleList[pos][0]);
			  }    
		});		
		
		return view;
	}
}