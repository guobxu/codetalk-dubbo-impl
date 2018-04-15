package me.codetalk.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.ApiConstants;
import me.codetalk.api.response.BaseResponse;

/**
 * Created by guobxu on 2017/12/15.
 */

public class ObjectDataResponse extends BaseResponse {

    public static final ObjectDataResponse ERROR = errorInstance();

    @JsonProperty("ret_data")
    private Object retData; // object or list

    private static ObjectDataResponse errorInstance() {
        ObjectDataResponse err = new ObjectDataResponse();
        err.setRetCode(ApiConstants.CODE_ERROR);

        return err;
    }

    public Object getRetData() {
        return retData;
    }

    public void setRetData(Object retData) {
        this.retData = retData;
    }

}
