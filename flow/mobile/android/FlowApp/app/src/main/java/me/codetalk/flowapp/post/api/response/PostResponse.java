package me.codetalk.flowapp.post.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import me.codetalk.api.response.BaseResponse;
import me.codetalk.flowapp.post.model.entity.Post;

/**
 * Created by guobxu on 2018/1/17.
 */

public class PostResponse extends BaseResponse {

    @JsonProperty("ret_data")
    private Post post;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
