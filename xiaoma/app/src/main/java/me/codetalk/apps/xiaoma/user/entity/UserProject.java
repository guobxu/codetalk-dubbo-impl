package me.codetalk.apps.xiaoma.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties ( ignoreUnknown = true )
public class UserProject {

	@JsonProperty("user_project_id")
	private Long id;
	
	@JsonProperty(value = "user_id", access = JsonProperty.Access.WRITE_ONLY)
	private Long userId;
	
	@JsonProperty("project_name")
	private String name;
	@JsonProperty("project_company")
	private String company;
	@JsonProperty("project_resp")
	private String resp;
	
	@JsonProperty("project_start")
	private Long start;
	@JsonProperty("project_end")
	private Long end;
	
	@JsonProperty("project_desc")
	private String desc;
	@JsonProperty("project_url")
	private String url;
	
	@JsonProperty("create_date")
	private Long createDate;
	@JsonProperty("last_update")
	private Long lastUpdate;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCompany() {
		return company;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}
	
	public String getResp() {
		return resp;
	}
	
	public void setResp(String resp) {
		this.resp = resp;
	}
	
	public Long getStart() {
		return start;
	}
	
	public void setStart(Long start) {
		this.start = start;
	}
	
	public Long getEnd() {
		return end;
	}
	
	public void setEnd(Long end) {
		this.end = end;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
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
