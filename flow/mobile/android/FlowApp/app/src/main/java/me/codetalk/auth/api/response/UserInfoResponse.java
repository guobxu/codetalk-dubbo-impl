package me.codetalk.auth.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.ApiConstants;
import me.codetalk.api.response.BaseResponse;
import me.codetalk.auth.model.entity.UserInfo;

/**
 * Created by guobxu on 2018/1/2.
 */

public class UserInfoResponse extends BaseResponse {

    public static final UserInfoResponse ERROR = errorInstance();

    @JsonProperty("ret_data")
    private UserInfo userInfo;

    private static UserInfoResponse errorInstance() {
        UserInfoResponse err = new UserInfoResponse();
        err.setRetCode(ApiConstants.CODE_ERROR);

        return err;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
