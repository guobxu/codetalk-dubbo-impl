package me.codetalk.flowapp.auth.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.codetalk.api.request.BaseBody;

/**
 * Created by guobxu on 2018/1/13.
 */

public class AppLoginBody extends BaseBody {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("auth_str")
    private String authStr;

    @JsonProperty("svc_ticket_str")
    private String svcTicket;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSvcTicket() {
        return svcTicket;
    }

    public void setSvcTicket(String svcTicket) {
        this.svcTicket = svcTicket;
    }

    public String getAuthStr() {
        return authStr;
    }

    public void setAuthStr(String authStr) {
        this.authStr = authStr;
    }

}
