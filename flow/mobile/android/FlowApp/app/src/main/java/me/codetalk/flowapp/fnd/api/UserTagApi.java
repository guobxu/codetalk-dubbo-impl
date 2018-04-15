package me.codetalk.flowapp.fnd.api;

import io.reactivex.Observable;
import me.codetalk.api.response.BaseResponse;
import me.codetalk.flowapp.fnd.api.request.UserTagAddBody;
import me.codetalk.flowapp.fnd.api.request.UserTagUpdateBody;
import me.codetalk.flowapp.fnd.api.response.TagResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by guobxu on 2018/1/9.
 */

public interface UserTagApi {

    @POST("/flow/fnd/usertag/update")
    Observable<BaseResponse> userTagUpdate(@Body UserTagUpdateBody body);

    @GET("/flow/fnd/usertag")
    Observable<TagResponse> userTag();

    @POST("/flow/fnd/usertag/add")
    Observable<BaseResponse> userTagAdd(@Body UserTagAddBody body);


}
