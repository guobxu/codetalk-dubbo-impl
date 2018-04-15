package me.codetalk.webdb.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 外部问题
 * @author guobxu
 *
 */
@JsonInclude(Include.NON_NULL)
public class Quest {

	@JsonProperty("quest_id")
	private Long id;
	
	@JsonProperty("quest_uuid")
	private String uuid;
	
	@JsonProperty("quest_site")
	private String site;
	@JsonProperty("quest_url")
	private String url;
	@JsonProperty("quest_title")
	private String title;
	@JsonProperty("quest_content")
	private String content;
	@JsonProperty("quest_answer")
	private String answer;
	@JsonProperty("quest_accept")
	private Integer answerAccept;	// 0 否 1 是
	
	@JsonProperty("quest_tags")
	private String tags;
	@JsonProperty("quest_votes")
	private Integer votes;
	
	@JsonIgnore
	private Integer indexed;		// 0 否 1 是
	
	@JsonProperty("create_date")
	private Long createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Integer getVotes() {
		return votes;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}

	public Integer getAnswerAccept() {
		return answerAccept;
	}

	public void setAnswerAccept(Integer answerAccept) {
		this.answerAccept = answerAccept;
	}

	public Integer getIndexed() {
		return indexed;
	}

	public void setIndexed(Integer indexed) {
		this.indexed = indexed;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	
	
}

















