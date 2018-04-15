package me.codetalk.apps.flow.post.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.apps.flow.post.entity.Comment;

public class CommentVO extends Comment {

	@JsonProperty("user_login")
	private String userLogin;
	@JsonProperty("user_name")
	private String userName;
	@JsonProperty("user_profile")
	private String userProfile;
	
	@JsonProperty("comment_liked")
	private Integer liked;
	
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
	
	
	
}
