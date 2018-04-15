package me.codetalk.apps.flow.fnd.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Follow {

	@JsonProperty("user_id")
	private Long userId;
	@JsonProperty("user_follow")
	private Long userFollow;
	
	@JsonProperty("create_date")
	private Long createDate;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserFollow() {
		return userFollow;
	}

	public void setUserFollow(Long userFollow) {
		this.userFollow = userFollow;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}
	
	
	
}
