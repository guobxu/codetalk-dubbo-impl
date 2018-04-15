package me.codetalk.auth.service;

import me.codetalk.auth.entity.Session;

public interface SessionService {

	public void createSession(Session sess);
	
	public Session findSession(Long userId, String accessToken);
	
}
