package me.codetalk.webminer.pojo;

import java.sql.Timestamp;

public class WebEntity {

	private Long id;
	private Integer siteId;
	private Integer entityTypeId;
	private String pageUrl;
	private Integer indexed;
	
	private Timestamp createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public Integer getIndexed() {
		return indexed;
	}

	public void setIndexed(Integer indexed) {
		this.indexed = indexed;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Integer getEntityTypeId() {
		return entityTypeId;
	}

	public void setEntityTypeId(Integer entityTypeId) {
		this.entityTypeId = entityTypeId;
	}
	
	
	
	
}
