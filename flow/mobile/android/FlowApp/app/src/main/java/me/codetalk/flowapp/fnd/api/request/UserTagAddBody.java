package me.codetalk.flowapp.fnd.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import me.codetalk.api.request.BaseBody;

/**
 * Created by guobxu on 2018/1/17.
 */

public class UserTagAddBody extends BaseBody {

    @JsonProperty("tag_text")
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
