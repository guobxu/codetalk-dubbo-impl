package me.codetalk.flowapp.post.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.request.BaseBody;

/**
 * Created by guobxu on 2018/1/23.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostLikeBody extends BaseBody {

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("action_type")
    private Integer actionType;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }
}
