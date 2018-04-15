package me.codetalk.apps.xiaoma.user.cache.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.apps.xiaoma.user.cache.UserProjectCache;
import me.codetalk.apps.xiaoma.user.entity.UserProject;
import me.codetalk.cache.service.CacheService;

@Component("userProjectCache")
public class UserProjectCacheImpl implements UserProjectCache {

	@Autowired
	private CacheService cacheService;
	
	private static final String CACHE_USER_PROJECTS = "USER-PROJECTS-";
	
	@Override
	public List<UserProject> getUserProjects(Long userId) {
		String cacheKey = CACHE_USER_PROJECTS + userId;
		
		return (List<UserProject>)cacheService.get(cacheKey);
	}
	
	@Override
	public void setUserProjects(Long userId, List<UserProject> projects) {
		String cacheKey = CACHE_USER_PROJECTS + userId;
		
		cacheService.set(cacheKey, projects, true);
	}

	@Override
	public void deleteUserProjects(Long userId) {
		String cacheKey = CACHE_USER_PROJECTS + userId;
		
		cacheService.delete(cacheKey);
	}

	

}
