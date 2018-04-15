package me.codetalk.flowapp.auth.api;

import io.reactivex.Observable;
import me.codetalk.api.response.BaseResponse;
import me.codetalk.flowapp.auth.api.request.AppLoginBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by guobxu on 2018/1/13.
 */

public interface AppAuthApi {

    @POST("/flow/login")
    Observable<BaseResponse> appLogin(@Body AppLoginBody appLoginBody);

}
