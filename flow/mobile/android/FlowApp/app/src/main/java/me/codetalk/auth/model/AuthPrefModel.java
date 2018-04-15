package me.codetalk.auth.model;

import android.content.Context;
import android.content.SharedPreferences;
import me.codetalk.auth.model.entity.AuthInfo;
import me.codetalk.model.AbstractPrefModel;

/**
 * Created by guobxu on 2017/12/29.
 */

public class AuthPrefModel extends AbstractPrefModel {

    // preferences
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_ACCESS_TOKEN = "access_token";
    public static final String PREF_LOGIN_DATE = "login_date";

    public static final String PREF_USER_LOGIN = "user_login";

    public static final String PREF_TRANSPORT_KEY = "transport_key";

    public static final String PREF_USER_AUTHSTR = "user_authstr";

    private static AuthPrefModel INSTANCE = null;

    private AuthPrefModel(Context appContext) {
        super(appContext);
    }

    public static AuthPrefModel getInstance(Context appContext) {
        if(INSTANCE == null) {
            INSTANCE = new AuthPrefModel(appContext);
        }

        return INSTANCE;
    }

    /**
     * Fetch user info
     *
     * @return
     */
    public AuthInfo loadAuthInfo() {
        AuthInfo authInfo = new AuthInfo();

        authInfo.setUserId(sharedPreferences.getLong(PREF_USER_ID, -1));
        authInfo.setUserLogin(sharedPreferences.getString(PREF_USER_LOGIN, null));
        authInfo.setAccessToken(sharedPreferences.getString(PREF_ACCESS_TOKEN, null));
        authInfo.setLoginDate(sharedPreferences.getLong(PREF_LOGIN_DATE, -1));
        authInfo.setTransportKey(sharedPreferences.getString(PREF_TRANSPORT_KEY, null));

        return authInfo;
    }

    public void setAuthInfo(AuthInfo authInfo) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(PREF_USER_ID, authInfo.getUserId());
        editor.putString(PREF_USER_LOGIN, authInfo.getUserLogin());
        editor.putString(PREF_ACCESS_TOKEN, authInfo.getAccessToken());
        editor.putLong(PREF_LOGIN_DATE, authInfo.getLoginDate());
        editor.putString(PREF_TRANSPORT_KEY, authInfo.getTransportKey());

        editor.apply();
    }

    /**
     * Clear auth info
     *
     */
    public void clearAuthInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(PREF_USER_ID);
        editor.remove(PREF_USER_LOGIN);
        editor.remove(PREF_ACCESS_TOKEN);
        editor.remove(PREF_LOGIN_DATE);
        editor.remove(PREF_TRANSPORT_KEY);
        editor.remove(PREF_USER_AUTHSTR);

        editor.apply();
    }

}
