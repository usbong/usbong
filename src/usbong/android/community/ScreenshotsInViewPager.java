package usbong.android.community;

import android.os.Parcel;
import android.os.Parcelable;

public class ScreenshotsInViewPager implements Parcelable {
	private String KEY = "";
	private String VAL = "";
	
	ScreenshotsInViewPager(String KEY, String VAL) {
		this.KEY = KEY;
		this.VAL = VAL;
	}

	public String getKEY() {
		return KEY;
	}

	public String getVAL() {
		return VAL;
	}	
	
	public static final Parcelable.Creator<ScreenshotsInViewPager> CREATOR = new Parcelable.Creator<ScreenshotsInViewPager>() {
		public ScreenshotsInViewPager createFromParcel(Parcel in) {
			return new ScreenshotsInViewPager(in);
		}
		
		public ScreenshotsInViewPager[] newArray(int size) {
			return new ScreenshotsInViewPager[size];
		}
	};
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(KEY);
		dest.writeString(VAL);
	}
	
	private ScreenshotsInViewPager(Parcel in) {
		KEY = in.readString();
		VAL = in.readString();
	}
}
