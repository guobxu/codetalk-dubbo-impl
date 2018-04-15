package me.codetalk.auth.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.ApiConstants;
import me.codetalk.api.response.BaseResponse;

/**
 * Created by guobxu on 2017/12/27.
 */

public class LoginResponse extends BaseResponse {

    public static final LoginResponse ERROR = errorInstance();

    @JsonProperty("ret_data")
    private LoginAuthRet loginAuthRet;

    private static LoginResponse errorInstance() {
        LoginResponse err = new LoginResponse();
        err.setRetCode(ApiConstants.CODE_ERROR);

        return err;
    }

    public LoginAuthRet getLoginAuthRet() {
        return loginAuthRet;
    }

    public void setLoginAuthRet(LoginAuthRet loginAuthRet) {
        this.loginAuthRet = loginAuthRet;
    }

}
