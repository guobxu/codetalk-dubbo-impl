package me.codetalk.flowapp.post.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by guobxu on 2018/1/9.
 */

public class Post {

    @JsonProperty("post_id")
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("post_liked")
    private Integer liked;

    @JsonProperty("user_followed")
    private Integer followed;

    @JsonProperty("count_follow")
    private Long countFollow; 			// 关注数

    @JsonProperty("count_followed")
    private Long countFollowed; 		// 被关注数

    @JsonProperty("user_login")
    private String userLogin;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("user_profile")
    private String userProfile;

    @JsonProperty("post_type")
    private Integer type; // 1 基本 2 投票

    @JsonProperty("post_content")
    private String content;

    @JsonProperty("post_imgs")
    private String images;

    @JsonProperty("poll_data")
    private Poll poll;

    @JsonProperty("comment_count")
    private Long commentCount;

    @JsonProperty("like_count")
    private Long likeCount;

    @JsonProperty("post_tags")
    private String tags;

    @JsonProperty("create_date")
    private Long createDate;

    @JsonProperty("comment_threads")
    private List<CommentThread> commentThreads;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getLiked() {
        return liked;
    }

    public void setLiked(Integer liked) {
        this.liked = liked;
    }

    public Integer getFollowed() {
        return followed;
    }

    public void setFollowed(Integer followed) {
        this.followed = followed;
    }

    public Long getCountFollow() {
        return countFollow;
    }

    public void setCountFollow(Long countFollow) {
        this.countFollow = countFollow;
    }

    public Long getCountFollowed() {
        return countFollowed;
    }

    public void setCountFollowed(Long countFollowed) {
        this.countFollowed = countFollowed;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }


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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public List<CommentThread> getCommentThreads() {
        return commentThreads;
    }

    public void setCommentThreads(List<CommentThread> commentThreads) {
        this.commentThreads = commentThreads;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(obj instanceof Post) {
            Post post = (Post)obj;

            return id.equals(post.id);
        }

        return false;
    }

}
