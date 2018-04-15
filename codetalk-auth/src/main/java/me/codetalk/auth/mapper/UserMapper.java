package me.codetalk.auth.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import me.codetalk.auth.entity.User;

/**
 * 
 * @author guobxu
 *
 */
public interface UserMapper {

	public void insertUser(User user);
	
	/**
	 * 忽略大小写
	 * @param login 小写登录名
	 * @return
	 */
	public Integer selectOneByLogin(@Param("login") String login);
	
	
	public User selectUserByLogin(@Param("login") String login);
	
	public User selectUserById(@Param("userId") Long userId);
	
	public List<User> selectUsersByIdList(@Param("userIdList") List<Long> userIdList);
	
	public void updatePassword(@Param("userId") Long userId, @Param("password") String password);
	
	public void updateUser(@Param("user") User user);
	
}
