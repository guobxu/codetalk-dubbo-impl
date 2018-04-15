package me.codetalk.apps.xiaoma.post.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.apps.xiaoma.post.entity.vo.CommentVO;

public class CommentThread {

	@JsonIgnore
	private String id; // uuid
	
	@JsonIgnore
	private Long createDate;
	
	@JsonProperty("comment_list")
	private List<CommentVO> commentList;
	
	public List<CommentVO> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<CommentVO> commentList) {
		this.commentList = commentList;
	}

	public void addComment(CommentVO comment) {
		if(commentList == null) commentList = new ArrayList<>();
		
		commentList.add(comment);
	}
	
	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
