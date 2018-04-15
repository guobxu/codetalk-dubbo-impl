package me.codetalk.apps.flow.stat.cache.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.apps.flow.stat.cache.IUserStatCache;
import me.codetalk.stat.StatSupport;

@Component("userStatCache")
public class UserStatCacheImpl implements IUserStatCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserStatCacheImpl.class);
	
	public static final String STAT_USER_SIGNUP = "STAT-USER-SIGNUP";			// 用户注册
	
	@Autowired
	private StatSupport statCache;
	
	@Override
	public void incrUserSignup(String date) {
		statCache.incrStatById(date, STAT_USER_SIGNUP, 1);
	}

}
