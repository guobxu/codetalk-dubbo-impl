package me.codetalk.auth.cache;

import me.codetalk.auth.entity.SignupSession;

public interface SignupCache {

	public void setSessionById(SignupSession sess);
	
	public SignupSession getSessionById(String ssid);
	
	public void deleteSessionById(String ssid);
	
}
