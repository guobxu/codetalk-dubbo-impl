package me.codetalk.auth.cache;

import me.codetalk.auth.entity.Session;

public interface SessionCache {

	public void setSession(Session sess);
	
	public Session getSession(Long userId, String accessToken);
	
}
