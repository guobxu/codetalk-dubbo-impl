package me.codetalk.flowapp.fnd.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.response.BaseResponse;
import me.codetalk.flowapp.fnd.model.entity.FollowStat;

/**
 * Created by guobxu on 2018/1/24.
 */

public class FollowResponse extends BaseResponse{

    @JsonProperty("ret_data")
    private FollowStat stat;

    public FollowStat getStat() {
        return stat;
    }

    public void setStat(FollowStat stat) {
        this.stat = stat;
    }

}
