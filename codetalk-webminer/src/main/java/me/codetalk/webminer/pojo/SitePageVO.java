package me.codetalk.webminer.pojo;

import java.util.List;

public class SitePageVO extends SitePage {

	private String siteName;
	
	private String entityType;
	
	private List<SiteEntityAttr> entityAttrs;

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public List<SiteEntityAttr> getEntityAttrs() {
		return entityAttrs;
	}

	public void setEntityAttrs(List<SiteEntityAttr> entityAttrs) {
		this.entityAttrs = entityAttrs;
	}
	
	
	
}
