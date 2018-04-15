package me.codetalk.auth.cache;

import me.codetalk.auth.entity.User;

public interface UserCache {

	// 生成user id
	public Long nextUserId();
	
	public void setUser(User user);
	
	public User getUserByLogin(String login);
	
	public User getUserById(Long id);
	
	public void clearUser(User user);
	
}
