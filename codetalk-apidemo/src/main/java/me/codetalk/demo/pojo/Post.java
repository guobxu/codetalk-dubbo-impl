package me.codetalk.demo.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Post {

	@JsonProperty("post_id")
	private Long id;
	@JsonProperty("post_content")
	private String content;
	@JsonProperty("post_author")
	private String author;
	@JsonProperty("create_date")
	private Long createDate;
	
	@JsonProperty("post_tags")
	private List<Tag> tags;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
	public static class Builder {
		
		private Post post;
		
		public Builder() {
			post = new Post();
		}
		
		public Builder setId(Long id) {
			post.id = id;
			
			return this;
		}
		
		public Builder setContent(String content) {
			post.content = content;
			
			return this;
		}
		
		public Builder setAuthor(String author) {
			post.author = author;
			
			return this;
		}
		
		public Builder setCreateDate(Long createDate) {
			post.createDate = createDate;
			
			return this;
		}
		
		public Builder setTags(List<Tag> tags) {
			post.tags = tags;
			
			return this;
		}
		
		public Post build() {
			return post;
		}
		
	}
	
}
