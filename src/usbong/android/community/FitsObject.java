package usbong.android.community;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class FitsObject implements Parcelable {
	private String FILENAME;
	private String FILEPATH;
    private int RATING;
    private String UPLOADER;
    private String DESCRIPTION;
    private String ICON;
    private String YOUTUBELINK;
    private String YOUTUBELINK2;
    private String DATEUPLOADED;
    private int DOWNLOADCOUNT;
    private boolean isYoutubeLink1Ok = true;
	private boolean isYoutubeLink2Ok = true;
    private ArrayList<String> screenshotArray = new ArrayList<String>();
    
    FitsObject(JSONObject jO) throws JSONException {
    	FILENAME = jO.optString(Constants.FILENAME);
    	FILEPATH = jO.optString(Constants.FILEPATH);
    	RATING = jO.optInt(Constants.RATING);
    	UPLOADER = jO.optString(Constants.UPLOADER);
    	DESCRIPTION = jO.optString(Constants.DESCRIPTION);
    	ICON = jO.optString(Constants.ICON);
    	
    	//Checking if there are values in these. (non required entries)
    	if(jO.optString(Constants.YOUTUBELINK) != null) {
    		YOUTUBELINK = jO.optString(Constants.YOUTUBELINK);	
    	} else {
    		YOUTUBELINK = "";
    		isYoutubeLink1Ok = false;
    	}
    	
    	if(jO.optString(Constants.YOUTUBELINK2) != null) {
    		YOUTUBELINK2 = jO.optString(Constants.YOUTUBELINK2);	
    	} else {
    		YOUTUBELINK2 = "";
    		isYoutubeLink2Ok = false;
    	}
    	
    	DATEUPLOADED = jO.getString(Constants.DATEUPLOADED);
    	DOWNLOADCOUNT = jO.getInt(Constants.DOWNLOADCOUNT);
    	
    	if(jO.optString(Constants.SCREENSHOT2).length() > 0) {
        	screenshotArray.add(jO.optString(Constants.SCREENSHOT2));    		
    	} else {
    		screenshotArray.add("");
    	}
    	if(jO.optString(Constants.SCREENSHOT3).length() > 0) {
        	screenshotArray.add(jO.optString(Constants.SCREENSHOT3));    		
    	} else {
    		screenshotArray.add("");
    	}
    	if(jO.optString(Constants.SCREENSHOT4).length() > 0) {
        	screenshotArray.add(jO.optString(Constants.SCREENSHOT4));    		
    	} else {
    		screenshotArray.add("");
    	}
    }
    
    public boolean isYoutubeLink1Ok() {
		return isYoutubeLink1Ok;
	}

	public boolean isYoutubeLink2Ok() {
		return isYoutubeLink2Ok;
	}

        
    public String getFILENAME() {
		return FILENAME;
	}

	public String getFILEPATH() {
		return FILEPATH;
	}

	public int getRATING() {
		return RATING;
	}

	public String getUPLOADER() {
		return UPLOADER;
	}

	public String getDESCRIPTION() {
		return DESCRIPTION;
	}

	public String getICON() {
		return ICON;
	}

	public String getYOUTUBELINK() {
		return YOUTUBELINK;
	}

	public String getYOUTUBELINK2() {
		return YOUTUBELINK2;
	}
	
	public String getDATEUPLOADED() {
		return DATEUPLOADED;
	}

	public int getDOWNLOADCOUNT() {
		return DOWNLOADCOUNT;
	}
	
	public ArrayList<String> getScreenshotArray() {
		return screenshotArray;
	}

	public static final Parcelable.Creator<FitsObject> CREATOR = new Parcelable.Creator<FitsObject>() {
		public FitsObject createFromParcel(Parcel in) {
			return new FitsObject(in);
		}
		
		public FitsObject[] newArray(int size) {
			return new FitsObject[size];
		}
	};
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(FILENAME);
		dest.writeString(FILEPATH);
		dest.writeInt(RATING);
		dest.writeString(UPLOADER);
		dest.writeString(DESCRIPTION);
		dest.writeString(ICON);
		dest.writeString(YOUTUBELINK);
		dest.writeString(DATEUPLOADED);
		dest.writeInt(DOWNLOADCOUNT);
	}
	
	private FitsObject(Parcel in) {
		FILENAME = in.readString();
		FILEPATH = in.readString();
		RATING = in.readInt();
		UPLOADER = in.readString();
		DESCRIPTION = in.readString();
		ICON = in.readString();
		YOUTUBELINK = in.readString();
		DATEUPLOADED = in.readString();
		DOWNLOADCOUNT = in.readInt();
	}
}
