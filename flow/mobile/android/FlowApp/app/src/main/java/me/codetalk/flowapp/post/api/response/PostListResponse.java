package me.codetalk.flowapp.post.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import me.codetalk.api.response.BaseResponse;
import me.codetalk.flowapp.post.model.entity.Post;

/**
 * Created by guobxu on 2018/1/17.
 */

public class PostListResponse extends BaseResponse {

    @JsonProperty("ret_data")
    List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
