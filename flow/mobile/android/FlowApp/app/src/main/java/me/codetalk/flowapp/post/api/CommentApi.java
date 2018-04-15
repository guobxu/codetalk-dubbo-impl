package me.codetalk.flowapp.post.api;

import io.reactivex.Observable;
import me.codetalk.api.response.MapDataResponse;
import me.codetalk.api.response.ObjectDataResponse;
import me.codetalk.flowapp.post.api.request.CommentCreateBody;
import me.codetalk.flowapp.post.api.request.CommentLikeBody;
import me.codetalk.flowapp.post.api.request.PostCreateBody;
import me.codetalk.flowapp.post.api.request.PostLikeBody;
import me.codetalk.flowapp.post.api.response.CommentThreadListResponse;
import me.codetalk.flowapp.post.api.response.PostListResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by guobxu on 2018/1/9.
 */

public interface CommentApi {

    @POST("/flow/post/comment/create")
    Observable<MapDataResponse> createComment(@Body CommentCreateBody commentCreateBody);

    @POST("/flow/post/comment/like")
    Observable<ObjectDataResponse> commentLike(@Body CommentLikeBody cmntLikeBody);

    @GET("/flow/post/comment")
    Observable<CommentThreadListResponse> commentDetail(@Query("comment_id") Long commentId);


}
