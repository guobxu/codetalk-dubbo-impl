package me.codetalk.webminer.page;

import java.util.Map;

/**
 * 页面数据实体
 * @author guobxu
 *
 */
public class PageData {

	private String url;
	
	private String site;
	private String resource;

	// 实体属性
	private Map<String, String> attrs;

	@Override
	public String toString() {
		return String.format("Site = %s, Resource = %s, Attrs = %s", 
				site, getResource(), attrs.toString());
	}
	
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, String> attrs) {
		this.attrs = attrs;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	
	
}
