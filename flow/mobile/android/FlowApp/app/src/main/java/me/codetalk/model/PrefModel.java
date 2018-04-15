package me.codetalk.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by guobxu on 2017/12/29.
 */

public class PrefModel extends AbstractPrefModel {

    public static PrefModel INSTANCE = null;

    private PrefModel(Context appContext) {
        super(appContext);
    }

    public static PrefModel getInstance(Context appContext) {
        if(INSTANCE == null) {
            INSTANCE = new PrefModel(appContext);
        }

        return INSTANCE;
    }

    public void putString(String key, String val) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, val);
        editor.apply();
    }

    public void putInteger(String key, int val) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, val);
        editor.apply();
    }

    public void putLong(String key, long val) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(key, val);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public Integer getInteger(String key) {
        return sharedPreferences.contains(key) ? sharedPreferences.getInt(key, -1) : null;
    }

    public Long getLong(String key) {
        return sharedPreferences.contains(key) ? sharedPreferences.getLong(key, -1) : null;
    }

}
