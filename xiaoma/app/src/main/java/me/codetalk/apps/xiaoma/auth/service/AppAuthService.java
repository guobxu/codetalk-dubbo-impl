package me.codetalk.apps.xiaoma.auth.service;

import me.codetalk.auth.entity.Session;
import me.codetalk.auth.exception.AuthServiceException;

public interface AppAuthService {

	/**
	 * 服务登录
	 * 
	 * @param userId
	 * @param accessToken
	 * @param svcTicket
	 * @throws AuthServiceException login failure if throw
	 */
	public void appLogin(Long userId, String authStr, String svcTicket) throws AuthServiceException;
	
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
