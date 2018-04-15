package me.codetalk.flowapp.post.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.ApiConstants;
import me.codetalk.api.response.BaseResponse;
import me.codetalk.flowapp.post.model.entity.Post;

/**
 * Created by guobxu on 2018/1/9.
 */

public class PostCreateResponse extends BaseResponse {

    public static final PostCreateResponse ERROR = errorInstance();

    @JsonProperty("ret_data")
    private Post post;

    private static PostCreateResponse errorInstance() {
        PostCreateResponse err = new PostCreateResponse();
        err.setRetCode(ApiConstants.CODE_ERROR);

        return err;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
