package me.codetalk.apps.flow.fnd.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TagPosition {
	
	@JsonProperty("t")
	private String text;
	@JsonProperty("s")
	private int start;
	@JsonProperty("e")
	private int end;	// exclusive
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}

	
	
}
