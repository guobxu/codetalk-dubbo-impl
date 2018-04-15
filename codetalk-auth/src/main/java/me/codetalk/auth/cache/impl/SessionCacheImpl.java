package me.codetalk.auth.cache.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.auth.cache.SessionCache;
import me.codetalk.auth.entity.Session;
import me.codetalk.cache.service.CacheService;

@Component("sessionCache")
public class SessionCacheImpl implements SessionCache {

	// 用户会话缓存前缀 
	private static final String CACHE_SESSION_PREFIX = "SESSION-";
	
	// ttl
	private static final long TTL_SESSION = 2 * 60 * 60L;
	
	@Autowired
	private CacheService cacheService;
	
	@Override
	public void setSession(Session sess) {
		String key = CACHE_SESSION_PREFIX + sess.getUserId() + "-" + sess.getAccessToken();
		cacheService.set(key, sess, TTL_SESSION);
	}

	@Override
	public Session getSession(Long userId, String accessToken) {
		String key = CACHE_SESSION_PREFIX + userId + "-" + accessToken;
		
		return (Session)cacheService.get(key);
	}

}
