package me.codetalk.apps.flow.stat.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.codetalk.apps.flow.stat.cache.IUserStatCache;
import me.codetalk.apps.flow.stat.service.IUserStatService;

@Service("userStatService")
public class UserStatServiceImpl implements IUserStatService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserStatServiceImpl.class);
	
	@Autowired
	private IUserStatCache userStatCache;
	
	@Override
	public void incrUserSignup(String date) {
		userStatCache.incrUserSignup(date);
	}

}
