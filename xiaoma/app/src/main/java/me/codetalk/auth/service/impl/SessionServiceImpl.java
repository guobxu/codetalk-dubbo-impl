package me.codetalk.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.auth.cache.SessionCache;
import me.codetalk.auth.entity.Session;
import me.codetalk.auth.service.SessionService;

@Service("sessionService")
public class SessionServiceImpl implements SessionService {

	@Autowired
	private SessionCache sessionCache;
	
	@Override
	@Transactional
	public void createSession(Session sess) {
		sessionCache.setSession(sess);
	}

	@Override
	public Session findSession(Long userId, String accessToken) {
		Session session = sessionCache.getSession(userId, accessToken);
		
		return session;
	}

}
