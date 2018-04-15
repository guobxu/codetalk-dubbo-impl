package me.codetalk.auth.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author guobxu
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements Serializable {
	
	@JsonProperty("user_id")
	private Long id;
	@JsonProperty("user_login")
	private String login;
	@JsonProperty("user_name")
	private String name;
	@JsonProperty("user_profile")
	private String profile;
	
	@JsonProperty("user_passwd")
	private String password; 	// 变换后的密码
	
	@JsonProperty("user_signature")
	private String signature;
	@JsonProperty("user_mobile")
	private String mobile;
	@JsonProperty("mobile_verified")
	private Integer mobileVerified;
	@JsonProperty("user_mail")
	private String mail;
	@JsonProperty("mail_verified")
	private Integer mailVerified;
	@JsonProperty("user_disabled")
	private Integer disabled;
	@JsonProperty("disable_reason")
	private String disableReason;
	
	@JsonProperty("attr1")
	private String attr1;
	@JsonProperty("attr2")
	private String attr2;
	@JsonProperty("attr3")
	private String attr3;
	
	@JsonIgnore
	private Long lastUpdate;
	@JsonIgnore
	private Long updateBy;
	
	@JsonProperty("create_date")
	private Long createDate;
	
	@JsonProperty("user_bgcover")
	private String bgcover;
	
	@JsonProperty("user_site")
	private String site;
	
	@JsonProperty("user_location")
	private String location;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getProfile() {
		return profile;
	}
	
	public void setProfile(String profile) {
		this.profile = profile;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public Integer getMobileVerified() {
		return mobileVerified;
	}
	
	public void setMobileVerified(Integer mobileVerified) {
		this.mobileVerified = mobileVerified;
	}
	
	public String getMail() {
		return mail;
	}
	
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	public Integer getMailVerified() {
		return mailVerified;
	}
	
	public void setMailVerified(Integer mailVerified) {
		this.mailVerified = mailVerified;
	}
	
	public Integer getDisabled() {
		return disabled;
	}
	
	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}
	
	public String getDisableReason() {
		return disableReason;
	}

	public void setDisableReason(String disableReason) {
		this.disableReason = disableReason;
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
	
	public Long getUpdateBy() {
		return updateBy;
	}
	
	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}
	
	public Long getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public String getBgcover() {
		return bgcover;
	}

	public void setBgcover(String bgcover) {
		this.bgcover = bgcover;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	
	
	
}
