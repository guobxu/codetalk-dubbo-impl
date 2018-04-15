package me.codetalk.apps.flow.fnd.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tag {

	@JsonProperty("tag_text")
	private String text;
	@JsonProperty("tag_hits")
	private Long hits;
	
	@JsonProperty("create_date")
	private Long createDate;
	@JsonProperty("last_update")
	private Long lastUpdate;
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Long getHits() {
		return hits;
	}
	
	public void setHits(Long hits) {
		this.hits = hits;
	}
	
	public Long getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}
	
	public Long getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
	
}
