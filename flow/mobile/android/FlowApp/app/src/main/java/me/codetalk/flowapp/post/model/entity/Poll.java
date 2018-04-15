package me.codetalk.flowapp.post.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by guobxu on 2018/1/9.
 */

public class Poll {

    @JsonProperty("poll_id")
    private Long id;
    @JsonProperty("poll_start")
    private Long start;
    @JsonProperty("poll_end")
    private Long end;

    @JsonProperty("poll_options")
    private List<Option> options;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public static class Option {

        @JsonProperty("option_id")
        private Long id;
        @JsonProperty("option_text")
        private String text;
        @JsonProperty("option_votes")
        private Long votes;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Long getVotes() {
            return votes;
        }

        public void setVotes(Long votes) {
            this.votes = votes;
        }

    }

}
