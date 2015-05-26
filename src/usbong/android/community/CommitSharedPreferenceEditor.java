package usbong.android.community;

import android.content.SharedPreferences;

public class CommitSharedPreferenceEditor extends SharedPreferenceEditor{
	  public void save(SharedPreferences.Editor editor) {
	    editor.commit();
	  }
	}