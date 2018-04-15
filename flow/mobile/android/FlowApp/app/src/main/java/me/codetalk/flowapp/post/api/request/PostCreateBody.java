package me.codetalk.flowapp.post.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import me.codetalk.api.request.BaseBody;

/**
 * Created by guobxu on 2018/1/9.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostCreateBody extends BaseBody {

    @JsonProperty("post_type")
    private Integer type; // 1 基本 2 投票

    @JsonProperty("post_content")
    private String content;

    @JsonProperty("post_imgs")
    private String images;

    @JsonProperty("poll_data")
    private Map<String, Object> pollData;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Map<String, Object> getPollData() {
        return pollData;
    }

    public void setPollData(Map<String, Object> pollData) {
        this.pollData = pollData;
    }

}
