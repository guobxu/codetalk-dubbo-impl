package me.codetalk.apps.flow.fnd.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import me.codetalk.apps.flow.fnd.entity.UserTag;
import me.codetalk.apps.flow.fnd.entity.vo.UserTagVO;

public interface UserTagMapper {
	
	public void insertUserTag(List<UserTag> userTags);
	
	public void deleteUserTag(@Param("userId") Long userId);
	
	public List<UserTagVO> selectUserTag(@Param("userId") Long userId);
	
}
