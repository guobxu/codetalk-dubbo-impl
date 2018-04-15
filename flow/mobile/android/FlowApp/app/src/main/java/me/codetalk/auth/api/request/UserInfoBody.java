package me.codetalk.auth.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.request.BaseBody;

/**
 * Created by guobxu on 2018/1/5.
 */

public class UserInfoBody extends BaseBody {

    @JsonProperty("user_bgcover")
    private String bgcover;
    @JsonProperty("user_profile")
    private String profile;
    @JsonProperty("user_name")
    private String name;
    @JsonProperty("user_signature")
    private String signature;
    @JsonProperty("user_location")
    private String location;
    @JsonProperty("user_site")
    private String site;

    public String getBgcover() {
        return bgcover;
    }

    public void setBgcover(String bgcover) {
        this.bgcover = bgcover;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
