package me.codetalk.lock.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.codetalk.cache.service.CacheService;
import me.codetalk.lock.service.LockService;

@Service("redisLockService")
public class RedisLockServiceImpl implements LockService {

	private static final String DEFAULT_LOCK_VALUE = "X"; // 默认值
	
	@Autowired
	private CacheService cacheService;
	
	@Override
	public boolean lock(Object resource) {
		return lock(resource, DEFAULT_TIMEOUT);
	}

	@Override
	public boolean lock(Object resource, long seconds) {
		String key = (String)resource;
		
		boolean b = cacheService.setNX(key, DEFAULT_LOCK_VALUE);
		if(b) {
			cacheService.expire(key, seconds);
		}
		
		return b;
	}

}
