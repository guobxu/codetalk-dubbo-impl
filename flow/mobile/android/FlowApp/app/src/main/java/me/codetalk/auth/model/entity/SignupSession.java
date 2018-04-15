package me.codetalk.auth.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by guobxu on 2017/12/23.
 */

public class SignupSession {

    @JsonProperty("signup_sid")
    private String id;
    @JsonProperty("signup_passwd_key")
    private String passwdKey;
    @JsonProperty("signup_code_img")
    private String imgdata;

    @JsonIgnore
    private Long createDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasswdKey() {
        return passwdKey;
    }

    public void setPasswdKey(String passwdKey) {
        this.passwdKey = passwdKey;
    }

    public String getImgdata() {
        return imgdata;
    }

    public void setImgdata(String imgdata) {
        this.imgdata = imgdata;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

}
