package me.codetalk.flowapp.post.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment {

	@JsonProperty("comment_id")
	private Long id;
	@JsonProperty("user_id")
	private Long userId;
	@JsonProperty("user_login")
	private String userLogin;
	@JsonProperty("user_name")
	private String userName;
	@JsonProperty("user_profile")
	private String userProfile;

	@JsonProperty("comment_liked")
	private Integer liked;

	@JsonProperty("post_id")
	private Long postId;

	@JsonProperty("comment_reply")
	private Long commentReply;
	
	@JsonProperty("comment_content")
	private String content;

	@JsonProperty("comment_tags")
	private String tags;

	@JsonProperty("comment_imgs")
	private String images;
	
	// 以下四个字段异步
	@JsonProperty("comment_thread")
	private String thread;
	@JsonProperty("comment_mentions")
	private String mentions;
	@JsonProperty("comment_level")
	private Integer level;
	@JsonIgnore
	private String ancestors; // ancestor comments
	
	@JsonIgnore
	private Integer deleteMark;
	@JsonIgnore
	private String deleteReason;

	@JsonIgnore
	private Long lastUpdate;
	@JsonIgnore
	private Long updateBy;
	
	@JsonProperty("create_date")
	private Long createDate;

	@JsonProperty("comment_count")
	private Long commentCount;
	
	@JsonProperty("like_count")
	private Long likeCount;
	
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

	public Integer getLiked() {
		return liked;
	}

	public void setLiked(Integer liked) {
		this.liked = liked;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
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

	public String getMentions() {
		return mentions;
	}

	public void setMentions(String mentions) {
		this.mentions = mentions;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getAncestors() {
		return ancestors;
	}

	public void setAncestors(String ancestors) {
		this.ancestors = ancestors;
	}

	public Integer getDeleteMark() {
		return deleteMark;
	}

	public void setDeleteMark(Integer deleteMark) {
		this.deleteMark = deleteMark;
	}

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}

	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Long getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
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
	
	public Long getCommentReply() {
		return commentReply;
	}

	public void setCommentReply(Long commentReply) {
		this.commentReply = commentReply;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
}
