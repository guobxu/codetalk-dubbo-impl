package me.codetalk.auth.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.ApiConstants;
import me.codetalk.api.response.BaseResponse;
import me.codetalk.auth.model.entity.SignupSession;

/**
 * Created by guobxu on 2017/12/23.
 */

public class SignupSessionResponse extends BaseResponse {

    public static final SignupSessionResponse ERROR = errorInstance();

    @JsonProperty("ret_data")
    private SignupSession signupSession;

    private static SignupSessionResponse errorInstance() {
        SignupSessionResponse err = new SignupSessionResponse();
        err.setRetCode(ApiConstants.CODE_ERROR);

        return err;
    }

    public SignupSession getSignupSession() {
        return signupSession;
    }

    public void setSignupSession(SignupSession signupSession) {
        this.signupSession = signupSession;
    }

}
