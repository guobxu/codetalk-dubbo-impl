package me.codetalk.flowapp.fnd.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.request.BaseBody;

/**
 * Created by guobxu on 2018/1/24.
 */

public class FollowBody extends BaseBody {

    @JsonProperty("user_follow")
    private Long userFollow;
    @JsonProperty("action_type")
    private Integer action;

    public Long getUserFollow() {
        return userFollow;
    }

    public void setUserFollow(Long userFollow) {
        this.userFollow = userFollow;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }
}
