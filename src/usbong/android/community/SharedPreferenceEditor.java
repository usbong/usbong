package usbong.android.community;

import android.content.SharedPreferences;
import android.os.Build;

//TODO: For supporting below 1.6 (before dalvik), if only until 2.1 see:
//http://stackoverflow.com/questions/12272397/android-backward-compatibility-but-still-utilise-latest-api-features
public abstract class SharedPreferenceEditor {

	  private static SharedPreferenceEditor sInstance;

	  public static SharedPreferenceEditor getInstance() {
	    if (sInstance == null) {

	      /*
	      * Check the version of the SDK we are running on. Choose an
	      * implementation class designed for that version of the SDK.
	      */
	      int sdkVersion = Build.VERSION.SDK_INT;
	      if (sdkVersion < Build.VERSION_CODES.GINGERBREAD) {
	        sInstance = new CommitSharedPreferenceEditor();
	      } else  {
	        sInstance = new ApplySharedPreferenceEditor();
	      }
	    }
	    return sInstance;
	  }

	  public abstract void save(SharedPreferences.Editor editor);
	}
