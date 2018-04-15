package me.codetalk.flowapp.fnd.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by guobxu on 2018/1/17.
 */

public class Tag {

    @JsonProperty("tag_text")
    private String text;

    @JsonProperty("tag_hits")
    private Long hits;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getHits() {
        return hits;
    }

    public void setHits(Long hits) {
        this.hits = hits;
    }
}
