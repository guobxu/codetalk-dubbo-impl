package me.codetalk.webdb.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Article {

	@JsonProperty("article_id")
	private Long id;
	@JsonProperty("article_uuid")
	private String uuid;
	
	@JsonProperty("article_site")
	private String site;
	
	@JsonProperty("article_url")
	private String url;
	@JsonProperty("article_title")
	private String title;
	@JsonProperty("article_summary")
	private String summary;
	@JsonProperty("article_content")
	private String content;
	@JsonProperty("article_tags")
	private String tags;
	
	@JsonIgnore
	private Integer indexed;
	
	@JsonProperty("create_date")
	private Long createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Integer getIndexed() {
		return indexed;
	}

	public void setIndexed(Integer indexed) {
		this.indexed = indexed;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	

	
}
