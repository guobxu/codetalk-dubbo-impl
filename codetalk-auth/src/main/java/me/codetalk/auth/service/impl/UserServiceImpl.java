package me.codetalk.auth.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import me.codetalk.auth.cache.UserCache;
import me.codetalk.auth.entity.User;
import me.codetalk.auth.exception.AuthServiceException;
import me.codetalk.auth.mapper.UserMapper;
import me.codetalk.auth.service.UserService;
import me.codetalk.mesg.KeyedMessages;

/**
 * 
 * @author guobxu
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private KeyedMessages km;
	
	@Autowired
	private UserCache userCache;
	
	@Transactional
	public Long createUser(String login, String passwd) throws AuthServiceException {
		if(checkLoginExists(login)) {
			throw new AuthServiceException( km.get("auth_err_dup_login") );
		}
		
		User user = new User();
		user.setLogin(login);
		user.setName(login); // 默认与login相同
		user.setPassword(passwd);
		user.setCreateDate(System.currentTimeMillis());
		
		userMapper.insertUser(user);

		// set cache
		userCache.setUser(user);
		
		return user.getId();
	}
	
	@Override
	public boolean checkLoginExists(String login) {
		return userMapper.selectOneByLogin(login) == null ? false : true;
	}

	@Override
	public User findUserByLogin(String login) {
		User user = userCache.getUserByLogin(login);
		if(user != null) return user;
		
		user = userMapper.selectUserByLogin(login);
		if(user != null) {
			userCache.setUser(user);
		}
		
		return user;
	}
	
	@Override
	public User findUserById(Long userId) {
		User user = userCache.getUserById(userId);
		if(user != null) return user;
		
		user = userMapper.selectUserById(userId);
		if(user != null) {
			userCache.setUser(user);
		}
		
		return user;
	}
	
	@Override
	public List<User> findUsersByIdList(List<Long> userIdList) {
		return userMapper.selectUsersByIdList(userIdList);
	}

	@Override
	public void resetPassword(Long userId, String newPwd) {
		userMapper.updatePassword(userId, newPwd);
	}

	@Override
	@Transactional
	public void updateUser(User user) throws AuthServiceException {
		String name = user.getName();
		if(name != null && name.trim().length() == 0) {
			throw new AuthServiceException(km.get("auth_err_name_empty"));
		}
		
		userMapper.updateUser(user);
		
		userCache.clearUser(user);
	}

	

}

















