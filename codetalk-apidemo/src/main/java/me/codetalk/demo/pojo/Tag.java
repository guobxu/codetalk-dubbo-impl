package me.codetalk.demo.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tag {

	@JsonProperty("tag_id")
	private Integer id;
	@JsonProperty("tag_text")
	private String text;
	
	public Tag(Integer id, String text) {
		this.id = id;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	
}
