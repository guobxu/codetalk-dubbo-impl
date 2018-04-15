package me.codetalk.flowapp.post.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import me.codetalk.api.response.BaseResponse;
import me.codetalk.flowapp.post.model.entity.CommentThread;
import me.codetalk.flowapp.post.model.entity.Post;

/**
 * Created by guobxu on 2018/1/17.
 */

public class CommentThreadListResponse extends BaseResponse {

    @JsonProperty("ret_data")
    List<CommentThread> commentThreads;

    public List<CommentThread> getCommentThreads() {
        return commentThreads;
    }

    public void setCommentThreads(List<CommentThread> commentThreads) {
        this.commentThreads = commentThreads;
    }

}
