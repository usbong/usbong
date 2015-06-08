package usbong.android.community;

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
    private String SCREENSHOT2;
	private String SCREENSHOT3;
    private String SCREENSHOT4;
    private String DATEUPLOADED;
    private int DOWNLOADCOUNT;
    FitsObject(JSONObject jO) throws JSONException {
    	FILENAME = jO.optString(Constants.FILENAME);
    	FILEPATH = jO.optString(Constants.FILEPATH);
    	RATING = jO.optInt(Constants.RATING);
    	UPLOADER = jO.optString(Constants.UPLOADER);
    	DESCRIPTION = jO.optString(Constants.DESCRIPTION);
    	ICON = jO.optString(Constants.ICON);
//    	YOUTUBELINK = jO.optString(Constants.YOUTUBELINK);
//    	YOUTUBELINK2 = jO.optString(Constants.YOUTUBELINK2);
    	//Checking if there are values in these. (non required entries)
    	if(!jO.optString(Constants.YOUTUBELINK).equals("null")) {
    		YOUTUBELINK = jO.optString(Constants.YOUTUBELINK);	
    	} else {
    		YOUTUBELINK = "";
    	}
    	
    	if(!jO.optString(Constants.YOUTUBELINK2).equals("null")) {
    		YOUTUBELINK2 = jO.optString(Constants.YOUTUBELINK2);	
    	} else {
    		YOUTUBELINK2 = "";
    	}
    	
    	DATEUPLOADED = jO.getString(Constants.DATEUPLOADED);
    	DOWNLOADCOUNT = jO.getInt(Constants.DOWNLOADCOUNT);
    	
    	if(!jO.optString(Constants.SCREENSHOT2).equals("null")) {
    		SCREENSHOT2 = jO.optString(Constants.SCREENSHOT2);
    	} else {
    		SCREENSHOT2 = "";
    	}
    	if(!jO.optString(Constants.SCREENSHOT3).equals("null")) {
    		SCREENSHOT3 = jO.optString(Constants.SCREENSHOT3);
    	} else {
    		SCREENSHOT3 = "";
    	}
    	if(!jO.optString(Constants.SCREENSHOT4).equals("null")) {
    		SCREENSHOT4 = jO.optString(Constants.SCREENSHOT4);
    	} else {
    		SCREENSHOT4 = "";
    	}
    }
    
    public String getSCREENSHOT2() {
		return SCREENSHOT2;
	}

	public String getSCREENSHOT3() {
		return SCREENSHOT3;
	}

	public String getSCREENSHOT4() {
		return SCREENSHOT4;
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
		dest.writeString(YOUTUBELINK2);
		dest.writeString(SCREENSHOT2);
		dest.writeString(SCREENSHOT3);
		dest.writeString(SCREENSHOT4);
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
		YOUTUBELINK2 = in.readString();
		SCREENSHOT2 = in.readString();
		SCREENSHOT3 = in.readString();
		SCREENSHOT4 = in.readString();
		DATEUPLOADED = in.readString();
		DOWNLOADCOUNT = in.readInt();
	}
}
