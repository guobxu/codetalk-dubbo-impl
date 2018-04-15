package me.codetalk.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import me.codetalk.api.ApiConstants;

/**
 * Created by guobxu on 2017/12/15.
 */

public class MapDataResponse extends BaseResponse {

    public static final MapDataResponse ERROR = errorInstance();

    @JsonProperty("ret_data")
    private Map<String, Object> retData; // object or list

    private static MapDataResponse errorInstance() {
        MapDataResponse err = new MapDataResponse();
        err.setRetCode(ApiConstants.CODE_ERROR);

        return err;
    }

    public Map<String, Object> getRetData() {
        return retData;
    }

    public void setRetData(Map<String, Object> retData) {
        this.retData = retData;
    }

}
