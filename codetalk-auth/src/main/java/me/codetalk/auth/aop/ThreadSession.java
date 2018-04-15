package me.codetalk.auth.aop;

import org.springframework.stereotype.Component;

import me.codetalk.auth.entity.Session;

@Component
public class ThreadSession {

	private ThreadLocal<Session> sessionStore = new ThreadLocal<>();
	
	public boolean authorized() {
		return sessionStore.get() != null;
	}
	
	public Session get() {
		return sessionStore.get();
	}
	
	public void set(Session session) {
		sessionStore.set(session);
	}
	
	public void clear() {
		sessionStore.set(null);
	}
	
	public Long getUserId() {
		Session sess = sessionStore.get();
		
		return sess == null ? null : sess.getUserId();
	}
	
	public String getUserLogin() {
		Session sess = sessionStore.get();
		
		return sess == null ? null : sess.getUserLogin();
	}
	
	public String getTransportKey() {
		Session sess = sessionStore.get();
		
		return sess == null ? null : sess.getTransportKey();
	}
	
}
