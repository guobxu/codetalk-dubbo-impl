package me.codetalk.auth.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.request.BaseBody;

/**
 * Created by guobxu on 2017/12/23.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignupBody extends BaseBody {

    @JsonProperty("signup_sid")
    private String signupSessionId;
    @JsonProperty("user_login")
    private String userLogin;
    @JsonProperty("passwd_str")
    private String passwd;
    @JsonProperty("signup_code")
    private String signupCode;

    public String getSignupSessionId() {
        return signupSessionId;
    }

    public void setSignupSessionId(String signupSessionId) {
        this.signupSessionId = signupSessionId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getSignupCode() {
        return signupCode;
    }

    public void setSignupCode(String signupCode) {
        this.signupCode = signupCode;
    }
}
