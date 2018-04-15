package me.codetalk.flowapp.fnd.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FollowStat {

	@JsonProperty("user_id")
	private Long userId;
	@JsonProperty("count_follow")
	private Long follow; 	// 关注数
	@JsonProperty("count_followed")
	private Long followed; 	// 被关注数
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getFollow() {
		return follow;
	}
	
	public void setFollow(Long follow) {
		this.follow = follow;
	}
	
	public Long getFollowed() {
		return followed;
	}
	
	public void setFollowed(Long followed) {
		this.followed = followed;
	}
	
	
	
}
