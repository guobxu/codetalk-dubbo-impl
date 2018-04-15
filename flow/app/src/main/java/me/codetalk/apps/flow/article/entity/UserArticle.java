package me.codetalk.apps.flow.article.entity;

public class UserArticle {

	private Long userId;
	private Long articleId;
	private Integer articleStatus;
	private Double readPercent;
	
	private Long createDate;
	private Long lastUpdate;
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getArticleId() {
		return articleId;
	}
	
	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
	
	public Integer getArticleStatus() {
		return articleStatus;
	}
	
	public void setArticleStatus(Integer articleStatus) {
		this.articleStatus = articleStatus;
	}
	
	public Double getReadPercent() {
		return readPercent;
	}
	
	public void setReadPercent(Double readPercent) {
		this.readPercent = readPercent;
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
