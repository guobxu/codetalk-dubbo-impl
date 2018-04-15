package me.codetalk.auth.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.request.BaseBody;

/**
 * Created by guobxu on 2017/12/23.
 */

public class LoginBody extends BaseBody {

    @JsonProperty("app_id")
    private String appId;
    @JsonProperty("user_login")
    private String userLogin;
    @JsonProperty("login_auth_str")
    private String loginAuthStr;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getLoginAuthStr() {
        return loginAuthStr;
    }

    public void setLoginAuthStr(String loginAuthStr) {
        this.loginAuthStr = loginAuthStr;
    }
}
