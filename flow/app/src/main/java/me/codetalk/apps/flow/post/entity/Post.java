package me.codetalk.apps.flow.post.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post {

	@JsonProperty("post_id")
	private Long id;
	
	@JsonProperty("user_id")
	private Long userId;
	
	
	@JsonProperty("post_content")
	private String content;
	@JsonProperty("post_type")
	private Integer type;
	@JsonProperty("post_imgs")
	private String images;
	@JsonProperty("post_tags")
	private String tags;

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
	
	@JsonProperty("poll_data")
	private Poll poll;
	
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
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getType() {
		return type;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}
	
	public String getImages() {
		return images;
	}
	
	public void setImages(String images) {
		this.images = images;
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
	
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	
}
