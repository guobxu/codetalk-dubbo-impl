package me.codetalk.apps.xiaoma.user.service;

import java.util.List;

import me.codetalk.apps.xiaoma.user.entity.UserWork;

public interface UserWorkService {

	public List<UserWork> listUserWork(Long userId);
	
	public Long createUserWork(UserWork work);
	
	public void updateUserWork(UserWork work);
	
	public void deleteUserWork(Long id);
	
	
}
