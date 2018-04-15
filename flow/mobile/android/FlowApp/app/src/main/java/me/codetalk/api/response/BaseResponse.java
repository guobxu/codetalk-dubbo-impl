package me.codetalk.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.ApiConstants;

/**
 * Created by guobxu on 2017/12/15.
 */

public class BaseResponse {

    public static final BaseResponse ERROR = errorInstance();

    @JsonProperty("ret_code")
    private Integer retCode;

    @JsonProperty("ret_msg")
    private String retMsg;

    private static BaseResponse errorInstance() {
        BaseResponse err = new BaseResponse();
        err.setRetCode(ApiConstants.CODE_ERROR);

        return err;
    }

    public Integer getRetCode() {
        return retCode;
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }
}
