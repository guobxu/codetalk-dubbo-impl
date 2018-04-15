package me.codetalk.flowapp.fnd.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import me.codetalk.api.response.BaseResponse;
import me.codetalk.flowapp.fnd.model.entity.Tag;

/**
 * Created by guobxu on 2018/1/17.
 */

public class TagResponse extends BaseResponse {

    @JsonProperty("ret_data")
    List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
