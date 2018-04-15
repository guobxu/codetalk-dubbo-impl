package me.codetalk.auth.service;

import me.codetalk.auth.entity.Session;
import me.codetalk.auth.exception.AuthServiceException;

public interface AppAuthService {

	/**
	 * 通信认证
	 * 
	 * @param userId
	 * @param accessToken
	 * @param authstr
	 * @throws AuthServiceException
	 */
	public Session requestAuth(Long userId, String accessToken, String authStr) throws AuthServiceException;
	
}
