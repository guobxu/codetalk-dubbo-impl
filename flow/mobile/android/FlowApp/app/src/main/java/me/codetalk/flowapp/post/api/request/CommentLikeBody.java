package me.codetalk.flowapp.post.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.request.BaseBody;

/**
 * Created by guobxu on 2018/1/23.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentLikeBody extends BaseBody {

    @JsonProperty("comment_id")
    private Long commentId;

    @JsonProperty("action_type")
    private Integer actionType;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }
}
