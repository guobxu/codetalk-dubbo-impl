package me.codetalk.apps.flow.fnd.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import me.codetalk.apps.flow.fnd.entity.Follow;

public interface FollowMapper {

	public void insertFollow(Follow follow);
	
	public int deleteFollow(@Param("userId") Long userId, @Param("userFollow") Long userFollow);

	// 关注的人数
	public Integer countFollow(Long userId);
	// 被关注人数
	public Integer countFollowedBy(Long userId);
	
	// 关注人列表
	public List<Long> selectFollow(@Param("userId") Long userId, @Param("begin") Integer begin, @Param("count") Integer count);
	// 被关注人列表
	public List<Long> selectFollowedBy(@Param("userId") Long userId, @Param("begin") Integer begin, @Param("count") Integer count);

	public Follow selectOne(@Param("userId") Long userId, @Param("userFollow") Long userFollow);
	
	public Set<Long> selectFollowIn(@Param("userId") Long userId, @Param("userFollowList") List<Long> userFollowList);
	
}
