package me.codetalk.auth.cache.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import me.codetalk.auth.cache.SignupCache;
import me.codetalk.auth.entity.SignupSession;
import me.codetalk.cache.service.CacheService;

@Component("signupCache")
public class SignupCacheImpl implements SignupCache {

	// 注册会话缓存前缀
	private static final String CACHE_SSESS_PREFIX = "SSESS-";
	
	@Value("${auth.signup.session-timeout}")
	private int sessTimeout; // 注册会话过期时间(分钟)
	
	@Autowired
	private CacheService cacheService;
	
	@Override
	public void setSessionById(SignupSession ssess) {
		cacheService.set(CACHE_SSESS_PREFIX + ssess.getId(), ssess, sessTimeout * 60);
	}

	@Override
	public SignupSession getSessionById(String ssid) {
		return (SignupSession)cacheService.get(CACHE_SSESS_PREFIX + ssid);
	}

	@Override
	public void deleteSessionById(String ssid) {
		cacheService.delete(CACHE_SSESS_PREFIX + ssid);
	}

}
