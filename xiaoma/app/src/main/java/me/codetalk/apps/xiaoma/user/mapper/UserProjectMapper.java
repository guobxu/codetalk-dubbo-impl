package me.codetalk.apps.xiaoma.user.mapper;

import java.util.List;

import me.codetalk.apps.xiaoma.user.entity.UserProject;

public interface UserProjectMapper {

	public List<UserProject> selectUserProjects(Long userId);
	
	public void insertUserProject(UserProject project);
	
	public void updateUserProject(UserProject project);
	
	public void deleteUserProject(Long id);
	
}
