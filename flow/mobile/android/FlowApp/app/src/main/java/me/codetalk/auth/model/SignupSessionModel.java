package me.codetalk.auth.model;

import android.content.Context;
import android.content.SharedPreferences;

import me.codetalk.auth.model.entity.SignupSession;
import me.codetalk.model.AbstractPrefModel;

/**
 * Created by guobxu on 2017/12/29.
 */

public class SignupSessionModel extends AbstractPrefModel {

    // preferences
    protected String PREF_SIGNUP_SID = "signup_sid";
    protected String PREF_SIGNUP_PASSWD_KEY = "signup_passwd_key";
    protected String PREF_SIGNUP_CODE_IMG = "signup_code_img";

    protected String PREF_SIGNUP_CREATE = "signup_create";

    private static SignupSessionModel INSTANCE = null;

    private SignupSessionModel(Context appContext) {
        super(appContext);
    }

    public static SignupSessionModel getInstance(Context appContext) {
        if(INSTANCE == null) {
            INSTANCE = new SignupSessionModel(appContext);
        }

        return INSTANCE;
    }

    public void setSignupSession(SignupSession ssess) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PREF_SIGNUP_SID, ssess.getId());
        editor.putString(PREF_SIGNUP_PASSWD_KEY, ssess.getPasswdKey());
        editor.putString(PREF_SIGNUP_CODE_IMG, ssess.getImgdata());
        editor.putLong(PREF_SIGNUP_CREATE, System.currentTimeMillis());

        editor.apply();
    }

    public void clearSignupSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREF_SIGNUP_SID);
        editor.remove(PREF_SIGNUP_PASSWD_KEY);
        editor.remove(PREF_SIGNUP_CODE_IMG);
        editor.remove(PREF_SIGNUP_CREATE);

        editor.apply();
    }

    public SignupSession loadSignupSession() {
        SignupSession ssess = new SignupSession();
        ssess.setId(sharedPreferences.getString(PREF_SIGNUP_SID, null));
        ssess.setPasswdKey(sharedPreferences.getString(PREF_SIGNUP_PASSWD_KEY, null));
        ssess.setImgdata(sharedPreferences.getString(PREF_SIGNUP_CODE_IMG, null));
        ssess.setCreateDate(sharedPreferences.getLong(PREF_SIGNUP_CREATE, -1));

        return ssess;
    }

}
