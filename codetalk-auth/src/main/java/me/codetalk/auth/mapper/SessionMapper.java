package me.codetalk.auth.mapper;

import org.apache.ibatis.annotations.Param;

import me.codetalk.auth.entity.Session;
import me.codetalk.auth.entity.User;

/**
 * 
 * @author guobxu
 *
 */
public interface SessionMapper {

	public void insertSession(Session userLogin);
	
	public Session selectSession(@Param("userId") Long userId, @Param("accessToken") String accessToken);
	
}
