package me.codetalk.apps.flow.post.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by guobxu on 2018/1/25.
 */

public class Mention {

    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("user_login")
    private String userLogin;
    @JsonProperty("user_profile")
    private String userProfile;

    public Mention() {}

    public Mention(Long userId, String userLogin, String userProfile) {
        this.userId = userId;
        this.userLogin = userLogin;
        this.userProfile = userProfile;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(this == obj) return true;

        if(obj instanceof Mention) {
        	Mention mention = (Mention)obj;

            return userId.equals(mention.userId);
        }

        return false;
    }
    
}
