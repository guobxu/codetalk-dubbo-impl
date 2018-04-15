package me.codetalk.apps.flow.fnd.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.apps.flow.fnd.entity.UserTag;

public class UserTagVO extends UserTag {
	
	@JsonProperty("tag_hits")
	private Long hits;

	public Long getHits() {
		return hits;
	}

	public void setHits(Long hits) {
		this.hits = hits;
	}
	
}
