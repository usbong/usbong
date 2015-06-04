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
    private ArrayList<String> screenshotArray = new ArrayList<String>();
    
    FitsObject(JSONObject jO) throws JSONException {
    	FILENAME = jO.getString(Constants.FILENAME);
    	FILEPATH = jO.getString(Constants.FILEPATH);
    	RATING = jO.getInt(Constants.RATING);
    	UPLOADER = jO.getString(Constants.UPLOADER);
    	DESCRIPTION = jO.getString(Constants.DESCRIPTION);
    	ICON = jO.getString(Constants.ICON);
    	YOUTUBELINK = jO.getString(Constants.YOUTUBELINK);
    	YOUTUBELINK2 = jO.getString(Constants.YOUTUBELINK2);
    	DATEUPLOADED = jO.getString(Constants.DATEUPLOADED);
    	DOWNLOADCOUNT = jO.getInt(Constants.DOWNLOADCOUNT);
    	
    	screenshotArray.add(jO.getString(Constants.SCREENSHOT2));
    	screenshotArray.add(jO.getString(Constants.SCREENSHOT2));
    	screenshotArray.add(jO.getString(Constants.SCREENSHOT2));
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
