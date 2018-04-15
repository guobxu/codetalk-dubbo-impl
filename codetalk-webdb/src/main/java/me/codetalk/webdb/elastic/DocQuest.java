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
@Document(indexName = Constants.INDEX_WEBDB, type= Constants.INDEX_TYPE_QUEST)
public class DocQuest implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("quest_id")
	private Long questId;
	
	@Id
	@JsonProperty("quest_uuid")
	private String uuid;
	
	@JsonProperty("quest_title")
	private String title;
	
	@JsonProperty("quest_content")
	private String content;
	
	@JsonProperty("quest_tags")
	private Set<String> tags;
	
	@JsonProperty("quest_votes")
	private Integer votes;
	
	@JsonProperty("quest_site")
	private String site;
	
	@JsonProperty("quest_url")
	private String url;
	
	@JsonProperty("quest_accepted")
	private Integer accepted;
	
	@JsonProperty("create_date")
	private Long createDate;
	
	public DocQuest() {}
	
	public Long getQuestId() {
		return questId;
	}

	public void setQuestId(Long questId) {
		this.questId = questId;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getVotes() {
		return votes;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}

	public Integer getAccepted() {
		return accepted;
	}

	public void setAccepted(Integer accepted) {
		this.accepted = accepted;
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

	
	
}
