package me.codetalk.webdb.elastic;

import java.io.Serializable;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.webdb.Constants;

@JsonInclude(Include.NON_NULL)
@Document(indexName = Constants.INDEX_WEBDB, type= Constants.INDEX_TYPE_ARTICLE)
public class DocArticle implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("article_id")
	private Long articleId;
	
	@Id
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
	private Set<String> tags;
	
	@JsonProperty("create_date")
	private Long createDate;
	
	public DocArticle() {}

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

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}
	
	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		
		if(obj instanceof DocArticle) {
			DocArticle article = (DocArticle)obj;
			
			return this.uuid.equals(article.uuid);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
	
}
