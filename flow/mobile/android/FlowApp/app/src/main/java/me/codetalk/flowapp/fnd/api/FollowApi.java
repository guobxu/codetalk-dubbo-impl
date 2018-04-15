package me.codetalk.flowapp.fnd.api;

import io.reactivex.Observable;
import me.codetalk.api.response.ObjectDataResponse;
import me.codetalk.flowapp.fnd.api.request.FollowBody;
import me.codetalk.flowapp.fnd.api.response.FollowResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by guobxu on 2018/1/24.
 */

public interface FollowApi {

    @POST("/flow/fnd/user/follow")
    Observable<FollowResponse> follow(@Body FollowBody followBody);

    @GET("/flow/fnd/user/follow/exist")
    Observable<ObjectDataResponse> existFollow(@Query("user_follow") Long userFollow);

    @GET("/flow/fnd/user/follow/count")
    Observable<FollowResponse> count(@Query("user_follow") Long userId);

}
