package me.codetalk.flowapp.post.api;

import io.reactivex.Observable;
import me.codetalk.api.response.ObjectDataResponse;
import me.codetalk.flowapp.post.api.request.PostCreateBody;
import me.codetalk.flowapp.post.api.request.PostLikeBody;
import me.codetalk.flowapp.post.api.response.PostListResponse;
import me.codetalk.flowapp.post.api.response.PostResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by guobxu on 2018/1/9.
 */

public interface PostApi {

    @POST("/flow/post/create")
    Observable<ObjectDataResponse> createPost(@Body PostCreateBody postCreateBody);

    @GET("/flow/post/search")
    Observable<PostListResponse> search(@Query("q") String q,
                                        @Query("tags") String tags,
                                        @Query("begin_date") Long beginDate,
                                        @Query("end_date") Long endDate,
                                        @Query("begin") Integer begin,
                                        @Query("count") Integer count);

    @POST("/flow/post/like")
    Observable<ObjectDataResponse> postLike(@Body PostLikeBody postLikeBody);

    @GET("/flow/post/listbyuser")
    Observable<PostListResponse> listByUser(@Query("user_id_get") Long userId,
                                            @Query("begin") Integer begin,
                                            @Query("count") Integer count);

    @GET("/flow/post/likebyuser")
    Observable<PostListResponse> likeByUser(@Query("user_id_get") Long userId,
                                            @Query("begin") Integer begin,
                                            @Query("count") Integer count);

    @GET("/flow/post")
    Observable<PostResponse> postDetail(@Query("post_id") Long postId,
                                        @Query("post_type") Integer postType);

}
