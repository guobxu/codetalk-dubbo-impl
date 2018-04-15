package me.codetalk.apps.xiaoma.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties ( ignoreUnknown = true )
public class UserWork {

	@JsonProperty("user_work_id")
	private Long id;
	
	@JsonProperty(value = "user_id", access = JsonProperty.Access.WRITE_ONLY)
	private Long userId;
	
	@JsonProperty("work_name")
	private String name;
	@JsonProperty("work_images")
	private String images;
	
	@JsonProperty("work_video")
	private String video;
	
	@JsonProperty("work_desc")
	private String desc;
	@JsonProperty("work_url")
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
	
	public String getImages() {
		return images;
	}
	
	public void setImages(String images) {
		this.images = images;
	}
	
	public String getVideo() {
		return video;
	}
	
	public void setVideo(String video) {
		this.video = video;
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
