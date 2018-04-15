package me.codetalk.apps.xiaoma.user.service;

import java.util.List;

import me.codetalk.apps.xiaoma.user.entity.UserProject;

public interface UserProjectService {

	public List<UserProject> listUserProject(Long userId);
	
	public Long createUserProject(UserProject project);
	
	public void updateUserProject(UserProject project);
	
	public void deleteUserProject(Long id);
	
	
}
