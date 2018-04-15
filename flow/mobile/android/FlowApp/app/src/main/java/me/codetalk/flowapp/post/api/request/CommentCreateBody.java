package me.codetalk.flowapp.post.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import me.codetalk.api.request.BaseBody;

/**
 * Created by guobxu on 2018/1/9.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentCreateBody extends BaseBody {

    @JsonProperty("post_id_reply")
    private Long postId;

    @JsonProperty("comment_id_reply")
    private Long commentId;

    @JsonProperty("comment_content")
    private String content;

    @JsonProperty("comment_imgs")
    private String images;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
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
}
