package me.codetalk.webminer.page.impl;

import me.codetalk.webminer.page.Page;

public abstract class AbstractPage implements Page {

	protected String url;
	
	protected AbstractPage(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
}
