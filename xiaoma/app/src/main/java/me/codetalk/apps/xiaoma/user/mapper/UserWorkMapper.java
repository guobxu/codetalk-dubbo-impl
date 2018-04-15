package me.codetalk.apps.xiaoma.user.mapper;

import java.util.List;

import me.codetalk.apps.xiaoma.user.entity.UserWork;

public interface UserWorkMapper {

	public List<UserWork> selectUserWorks(Long userId);
	
	public void insertUserWork(UserWork work);
	
	public void updateUserWork(UserWork work);
	
	public void deleteUserWork(Long id);
	
}
