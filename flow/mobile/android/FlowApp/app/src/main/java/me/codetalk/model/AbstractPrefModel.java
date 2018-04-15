package me.codetalk.model;

import android.content.Context;
import android.content.SharedPreferences;

import me.codetalk.flowapp.AppConstants;

/**
 * Created by guobxu on 2017/12/29.
 */

public abstract class AbstractPrefModel {

    protected Context appContext;
    protected SharedPreferences sharedPreferences;

    public AbstractPrefModel(Context appContext) {
        this.appContext = appContext;

        sharedPreferences = appContext.getSharedPreferences(AppConstants.APP_ID, Context.MODE_PRIVATE);
    }

}
