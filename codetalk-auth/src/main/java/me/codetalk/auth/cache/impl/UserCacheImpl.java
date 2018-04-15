package me.codetalk.auth.cache.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.codetalk.auth.cache.UserCache;
import me.codetalk.auth.entity.User;
import me.codetalk.cache.service.CacheService;

@Service("userCacheService")
public class UserCacheImpl implements UserCache {

	@Autowired
	private CacheService cacheService;
	
	// user id key
	private static final String KEY_USER_ID = "KEY-USER-ID";
	
	// cache prefixes
	private static final String CACHE_USER_PREFIX = "USER-";
	
	// ttl
	private static final long TTL_USER = 60 * 60L;
	
	@Override
	public Long nextUserId() {
		Long userId = cacheService.incr(KEY_USER_ID);
		
		return userId;
	}
	
	@Override
	public void setUser(User user) {
		cacheService.set(CACHE_USER_PREFIX + user.getId(), user, TTL_USER);
		cacheService.set(CACHE_USER_PREFIX + user.getLogin(), user, TTL_USER);
	}

	@Override
	public User getUserByLogin(String login) {
		return (User)cacheService.get(CACHE_USER_PREFIX + login);
	}

	@Override
	public User getUserById(Long id) {
		return (User)cacheService.get(CACHE_USER_PREFIX + id);
	}

	@Override
	public void clearUser(User user) {
		String[] keys = new String[] {
				CACHE_USER_PREFIX + user.getId(), 
				CACHE_USER_PREFIX + user.getLogin()
		};
		
		cacheService.delete(keys);
	}

}
