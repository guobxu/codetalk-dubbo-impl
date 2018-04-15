package me.codetalk.apps.xiaoma.post.elastic;

import java.io.Serializable;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.apps.xiaoma.AppConstants;

@JsonInclude(Include.NON_NULL)
@Document(indexName = AppConstants.INDEX_XMAPP, type = AppConstants.INDEX_TYPE_POST)
public class DocPost implements Serializable {

	@Id
	@JsonIgnore
	private String id;
	
	@JsonProperty("post_id")
	private Long postId;
	@JsonProperty("post_type")
	private Integer postType;
	@JsonProperty("post_content")
	private String postContent;

	@JsonProperty("post_tags")
	private Set<String> postTags;
	@JsonProperty("create_by")
	private String createBy; 		// userId
	@JsonProperty("create_date")
	private Long createDate;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Integer getPostType() {
		return postType;
	}

	public void setPostType(Integer postType) {
		this.postType = postType;
	}
	
	public Set<String> getPostTags() {
		return postTags;
	}

	public void setPostTags(Set<String> postTags) {
		this.postTags = postTags;
	}

	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}

	public String getCreateBy() {
		return createBy;
	}
	
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	
	public Long getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}
	
	
	
}
