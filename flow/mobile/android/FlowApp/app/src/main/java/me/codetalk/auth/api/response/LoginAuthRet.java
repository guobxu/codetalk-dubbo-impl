package me.codetalk.auth.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by guobxu on 2017/12/27.
 */

public class LoginAuthRet {

    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("auth_ret_str")
    private String authRet;

    @JsonProperty("svc_ticket_str")
    private String svcTicket;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAuthRet() {
        return authRet;
    }

    public void setAuthRet(String authRet) {
        this.authRet = authRet;
    }

    public String getSvcTicket() {
        return svcTicket;
    }

    public void setSvcTicket(String svcTicket) {
        this.svcTicket = svcTicket;
    }

}
