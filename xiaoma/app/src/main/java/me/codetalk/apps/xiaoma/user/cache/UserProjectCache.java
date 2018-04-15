package me.codetalk.apps.xiaoma.user.cache;

import java.util.List;

import me.codetalk.apps.xiaoma.user.entity.UserProject;

public interface UserProjectCache {

	public List<UserProject> getUserProjects(Long userId);
	
	public void setUserProjects(Long userId, List<UserProject> projects);
	
	public void deleteUserProjects(Long userId);
	
}
