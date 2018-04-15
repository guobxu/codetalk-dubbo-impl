package me.codetalk.apps.xiaoma.user.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import me.codetalk.apps.xiaoma.user.entity.Follow;
import me.codetalk.apps.xiaoma.user.entity.FollowStat;

public interface FollowService {

	/**
	 * 
	 * @param userId
	 * @param userFollow
	 * @return 如果已经存在, 返回null; 否则返回userFollow的关注数据
	 */
	public FollowStat createFollow(Long userId, Long userFollow);
	
	/**
	 * 
	 * @param userId
	 * @param userFollow
	 * @return 如果不存在, 返回null; 否则返回userFollow的关注数据
	 */
	public FollowStat cancelFollow(Long userId, Long userFollow);
	
	public Follow findFollow(Long userId, Long userFollow);
	
	// 关注人数
	public Integer countFollow(Long userId);
	// 被关注人数
	public Integer countFollowedBy(Long userId);
	
	// 关注人列表
	public List<Long> findFollow(Long userId, Integer begin, Integer count);
	// 被关注人列表
	public List<Long> findFollowedBy(Long userId, Integer begin, Integer count);
	
	public Set<Long> findFollowIn(Long userId, List<Long> userFollowList);
	
	public FollowStat getFollowStat(Long userId);
	
	public Map<Long, FollowStat> getFollowStat(List<Long> userIdList);
	
}
