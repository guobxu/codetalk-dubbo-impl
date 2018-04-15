package me.codetalk.auth.model.entity;

import me.codetalk.auth.AuthConstants;
import me.codetalk.auth.AuthUtils;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.util.StringUtils;

/**
 * Created by guobxu on 2017/12/29.
 */

public class AuthInfo {

    private Long userId = -1L;              // user id
    private String userLogin;               // user login
    private String accessToken;             // access token
    private Long loginDate = -1L;           // login date

    private String transportKey;            // transport key

    private Long authMills;                 // Timemillis when Auth Str generated

    private String auth;                    // userId:token:authstr -> base64

    private String authStr;                 // Base64(AES("UserLogin +时间戳", KEY("32Byte传输密钥")))

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Long loginDate) {
        this.loginDate = loginDate;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getTransportKey() {
        return transportKey;
    }

    public void setTransportKey(String transportKey) {
        this.transportKey = transportKey;
    }

    public String getAuth() {
        if(auth != null && System.currentTimeMillis() - authMills <= AuthConstants.AUTH_STR_EXPIRE) {
            return auth;
        }

        if(userId == -1) return null;

        this.authMills = System.currentTimeMillis();
        try {
            this.authStr = AuthUtils.cipherAuthStr(userLogin, authMills, transportKey);
        } catch(Exception ex) {
            ex.printStackTrace();

            return null;
        }

        this.auth = StringUtils.base64(userId + ":" + accessToken + ":" + authStr);

        return this.auth;
    }

    public String getAuthStr() {
        return this.authStr;
    }

}