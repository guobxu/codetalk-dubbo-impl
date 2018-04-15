package me.codetalk.apps.xiaoma.user.cache.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.apps.xiaoma.user.cache.UserWorkCache;
import me.codetalk.apps.xiaoma.user.entity.UserWork;
import me.codetalk.cache.service.CacheService;

@Component("userWorkCache")
public class UserWorkCacheImpl implements UserWorkCache {

	@Autowired
	private CacheService cacheService;
	
	private static final String CACHE_USER_WORKS = "USER-WORKS-";
	
	@Override
	public List<UserWork> getUserWorks(Long userId) {
		String cacheKey = CACHE_USER_WORKS + userId;
		
		return (List<UserWork>)cacheService.get(cacheKey);
	}

	@Override
	public void setUserWorks(Long userId, List<UserWork> works) {
		String cacheKey = CACHE_USER_WORKS + userId;
		
		cacheService.set(cacheKey, works, true);
	}

	@Override
	public void deleteUserWorks(Long userId) {
		String cacheKey = CACHE_USER_WORKS + userId;
		
		cacheService.delete(cacheKey);
	}

}
