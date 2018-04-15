package me.codetalk.apps.flow.fnd.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.auth.entity.User;

public class UserVO extends User {

	@JsonProperty("user_followed")
	private Integer followed;

	public Integer getFollowed() {
		return followed;
	}

	public void setFollowed(Integer followed) {
		this.followed = followed;
	}
	
	
}
