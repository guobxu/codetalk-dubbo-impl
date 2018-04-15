package me.codetalk.flowapp.fnd.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

import me.codetalk.api.request.BaseBody;

/**
 * Created by guobxu on 2018/1/17.
 */

public class UserTagUpdateBody extends BaseBody {

    @JsonProperty("tag_list")
    private Set<String> tags;

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
