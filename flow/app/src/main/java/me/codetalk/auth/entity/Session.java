package me.codetalk.auth.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Session implements Serializable {

	private Long id;
	private Long userId;
	private String userLogin;
	private String accessToken;
	private String transportKey;
	
	private Integer pfType;	// 1 Web 2 Android 3 iOS
	
	private String attr1;
	private String attr2;
	private String attr3;
	
	private Long lastUpdate;
	private Long createDate;
	private Long endDate;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String getTransportKey() {
		return transportKey;
	}
	
	public void setTransportKey(String transportKey) {
		this.transportKey = transportKey;
	}
	
	public String getAttr1() {
		return attr1;
	}
	
	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}
	
	public String getAttr2() {
		return attr2;
	}
	
	public void setAttr2(String attr2) {
		this.attr2 = attr2;
	}
	
	public String getAttr3() {
		return attr3;
	}
	
	public void setAttr3(String attr3) {
		this.attr3 = attr3;
	}
	
	public Long getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public Long getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public Integer getPfType() {
		return pfType;
	}

	public void setPfType(Integer pfType) {
		this.pfType = pfType;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	
	
	
	
}
