package me.codetalk.api;

import me.codetalk.api.response.BaseResponse;

/**
 * Created by guobxu on 2017/12/23.
 */

public final class ApiUtils {

    public static boolean isSuccess(BaseResponse resp) {
        return resp.getRetCode() == ApiConstants.CODE_SUCCESS;
    }

    public static boolean isFailure(BaseResponse resp) {
        return resp.getRetCode() == ApiConstants.CODE_FAIL;
    }

//    public static AuthApi getAuthApi() {
//        return RetrofitClient.getClient(ApiConstants.BASE_URL).create(AuthApi.class);
//    }

    public static <T> T getRestApi(Class<T> apiInterface) {
        return RetrofitClient.getClient(ApiConstants.BASE_URL).create(apiInterface);
    }


}