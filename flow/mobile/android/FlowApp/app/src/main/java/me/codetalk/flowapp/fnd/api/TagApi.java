package me.codetalk.flowapp.fnd.api;

import io.reactivex.Observable;
import me.codetalk.flowapp.fnd.api.response.TagResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by guobxu on 2018/1/9.
 */

public interface TagApi {

    @GET("/flow/fnd/tag/list")
    Observable<TagResponse> tagList(@Query("q") String q, @Query("count") int count);

    @GET("/flow/fnd/tag/topbyday")
    Observable<TagResponse> topByDay(@Query("count") int count);

}
