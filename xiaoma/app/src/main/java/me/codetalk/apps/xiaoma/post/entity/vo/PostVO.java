package me.codetalk.apps.xiaoma.post.entity.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.apps.xiaoma.post.entity.CommentThread;
import me.codetalk.apps.xiaoma.post.entity.Post;

public class PostVO extends Post {

	@JsonProperty("user_login")
	private String userLogin;
	@JsonProperty("user_name")
	private String userName;
	@JsonProperty("user_profile")
	private String userProfile;
	
	@JsonProperty("post_liked")
	private Integer liked; // 是否点赞 0 否 1 是 
	
	@JsonProperty("user_followed")
	private Integer followed; // 是否关注 0 否 1 是
	
	@JsonProperty("count_follow")
	private Long countFollow; 			// 关注数
	@JsonProperty("count_followed")
	private Long countFollowed; 		// 被关注数
	
	@JsonProperty("comment_threads")
	private List<CommentThread> commentThreads;
	
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
	
	public List<CommentThread> getCommentThreads() {
		return commentThreads;
	}

	public void setCommentThreads(List<CommentThread> commentThreads) {
		this.commentThreads = commentThreads;
	}
	
	
}
