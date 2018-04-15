package me.codetalk.auth.service;

import java.util.List;

import me.codetalk.auth.entity.User;
import me.codetalk.auth.exception.AuthServiceException;

/**
 * 
 * @author guobxu
 *
 */
public interface UserService {

	/**
	 * 添加用户
	 * @param login 	登录名
	 * @param passwd	密码
	 * @return 添加成功后的User对象
	 */
	public Long createUser(String login, String passwd) throws AuthServiceException;
	
	/**
	 * 检查登录名是否存在
	 * @param login 不区分大小写
	 * @return
	 */
	public boolean checkLoginExists(String login); 
	
	/**
	 * 根据登录名查找用户
	 * @param login
	 * @return
	 */
	public User findUserByLogin(String login);
	
	/**
	 * 根据ID查找用户
	 * @param userId
	 * @return
	 */
	public User findUserById(Long userId);
	
	public List<User> findUsersByIdList(List<Long> userIdList);
	
	/**
	 * 重置密码
	 * 
	 * @param userId
	 * @param newPwd
	 */
	public void resetPassword(Long userId, String newPwd);
	
	/**
	 * 更新用户信息, 允许更新如下信息:
	 * 用户名 / 签名 / 网站 / 地址 / 背景图 / 头像
	 * @param user
	 */
	public void updateUser(User user) throws AuthServiceException;
	
}











