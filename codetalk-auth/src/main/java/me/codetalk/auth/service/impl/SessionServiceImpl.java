package me.codetalk.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.auth.cache.SessionCache;
import me.codetalk.auth.entity.Session;
import me.codetalk.auth.mapper.SessionMapper;
import me.codetalk.auth.service.SessionService;

@Service("sessionService")
public class SessionServiceImpl implements SessionService {

	@Autowired
	private SessionCache sessionCache;
	
	@Autowired
	private SessionMapper sessMapper;
	
	@Override
	@Transactional
	public void createSession(Session sess) {
		sessMapper.insertSession(sess);
		
		sessionCache.setSession(sess);
	}

	@Override
	public Session findSession(Long userId, String accessToken) {
		Session session = sessionCache.getSession(userId, accessToken);
		if(session != null) return session;
		
		session = sessMapper.selectSession(userId, accessToken);
		if(session != null) {
			sessionCache.setSession(session);
		}
		
		return session;
	}

}
