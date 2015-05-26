package usbong.android.community;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ApplySharedPreferenceEditor extends SharedPreferenceEditor {
	  public void save(SharedPreferences.Editor editor) {
	    editor.apply();
	  }
	}