package me.codetalk.apps.xiaoma.user.cache;

import java.util.List;

import me.codetalk.apps.xiaoma.user.entity.UserWork;

public interface UserWorkCache {

	public List<UserWork> getUserWorks(Long userId);
	
	public void setUserWorks(Long userId, List<UserWork> works);
	
	public void deleteUserWorks(Long userId);
	
}
