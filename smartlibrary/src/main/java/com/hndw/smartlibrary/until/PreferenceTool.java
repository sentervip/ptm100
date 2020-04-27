
package com.hndw.smartlibrary.until;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * @author ljh create 2019-3-20
 */

public class PreferenceTool {
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private static PreferenceTool tool;
    private final String preferName = "smart_kit";
    public PreferenceTool(Context context) {
        preferences = context.getSharedPreferences(preferName, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static PreferenceTool getInstance(Context context) {
        if (tool == null) {
            synchronized (PreferenceTool.class) {
                tool = new PreferenceTool(context);
            }
        }
        return tool;
    }


    public void editString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public String getStringValue(String key) {
        return preferences.getString(key, "");
    }

    public void editBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public boolean getBooleanValue(String key) {
        return preferences.getBoolean(key, false);
    }

    public void editLong(String key, Long value) {
        editor.putLong(key, value).commit();
    }

    public long getLongValue(String key) {
        return preferences.getLong(key, 0);
    }

    public void editFloat(String key, float value) {
        editor.putFloat(key, value).commit();
    }

    public float getFloatValue(String key) {
        return preferences.getFloat(key, 0.0f);
    }

    public void editInt(String key, int value) {
        editor.putInt(key, value).commit();
    }

    public int getIntValue(String key) {
        return preferences.getInt(key, 0);
    }

    public void eidtLanguage(String value) {
        editor.putString("default_language", value).commit();
    }

    public String getDefaultLanguage() {
        return preferences.getString("default_language", "zh");
    }
}
