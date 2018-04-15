package me.codetalk.apps.flow.fnd.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.Constants;
import me.codetalk.apps.flow.fnd.entity.Follow;
import me.codetalk.apps.flow.fnd.entity.FollowStat;
import me.codetalk.apps.flow.fnd.mapper.FollowMapper;
import me.codetalk.apps.flow.fnd.service.FollowService;
import me.codetalk.apps.flow.service.AbstractAppService;
import me.codetalk.auth.aop.ThreadSession;
import me.codetalk.stat.StatSupport;

@Service("followService")
public class FollowServiceImpl extends AbstractAppService implements FollowService {

	@Autowired
	private FollowMapper followMapper;
	
	@Autowired
	private StatSupport statSupport;
	
	@Autowired
	private ThreadSession threadSession;
	
	// STATs
	public static final String STAT_USER_FOLLOW = "STAT-USER-FOLLOW-";		// 关注
	public static final String STAT_USER_FOLLOWED = "STAT-USER-FOLLOWED-";	// 被关注
	
	@Override
	@Transactional
	public FollowStat createFollow(Long userId, Long userFollow) {
		if(null != followMapper.selectOne(userId, userFollow)) return null;
		
		Follow follow = new Follow();
		follow.setUserId(userId);
		follow.setUserFollow(userFollow);
		
		followMapper.insertFollow(follow);
		
		// cache, TODO: fix issue here
		statSupport.incrRangeStatById(userId, STAT_USER_FOLLOW, 1);
		Long followedCount = statSupport.incrRangeStatById(userFollow, STAT_USER_FOLLOWED, 1);
		
		// stat 
		FollowStat stat = new FollowStat();
		stat.setUserId(userId);
		stat.setFollow(statSupport.getRangeStatById(userFollow, STAT_USER_FOLLOW));
		stat.setFollowed(followedCount);
		
		return stat;
	}

	@Override
	@Transactional
	public FollowStat cancelFollow(Long userId, Long userFollow) {
		if( 0 == followMapper.deleteFollow(userId, userFollow) ) return null;
		
		statSupport.incrRangeStatById(userId, STAT_USER_FOLLOW, -1);
		Long followedCount = statSupport.incrRangeStatById(userFollow, STAT_USER_FOLLOWED, -1);
		
		// stat 
		FollowStat stat = new FollowStat();
		stat.setUserId(userId);
		stat.setFollow(statSupport.getRangeStatById(userFollow, STAT_USER_FOLLOW));
		stat.setFollowed(followedCount);
		
		return stat;
	}

	@Override
	public Integer countFollow(Long userId) {
		return followMapper.countFollow(userId);
	}

	@Override
	public Integer countFollowedBy(Long userId) {
		return followMapper.countFollowedBy(userId);
	}

	@Override
	public List<Long> findFollow(Long userId, Integer begin, Integer count) {
		return followMapper.selectFollow(userId, begin, count);
	}

	@Override
	public List<Long> findFollowedBy(Long userId, Integer begin, Integer count) {
		return followMapper.selectFollowedBy(userId, begin, count);
	}

	@Override
	public Follow findFollow(Long userId, Long userFollow) {
		return followMapper.selectOne(userId, userFollow);
	}

	@Override
	public Set<Long> findFollowIn(Long userId, List<Long> userFollowList) {
		return followMapper.selectFollowIn(userId, userFollowList);
	}

	@Override
	public FollowStat getFollowStat(Long userId) {
		Long followCount = statSupport.getRangeStatById(userId, STAT_USER_FOLLOW), 
				followedCount = statSupport.getRangeStatById(userId, STAT_USER_FOLLOWED);
		
		FollowStat stat = new FollowStat();
		stat.setUserId(userId);
		stat.setFollow(followCount);
		stat.setFollowed(followedCount);
		
		return stat;
	}

	@Override
	public Map<Long, FollowStat> getFollowStat(List<Long> userIdList) {
		Map<Long, Long> followMap = statSupport.getRangeStats(userIdList, STAT_USER_FOLLOW), 
				followedMap = statSupport.getRangeStats(userIdList, STAT_USER_FOLLOWED);
		
		Map<Long, FollowStat> statMap = new HashMap<>();
		for(Long userId : userIdList) {
			FollowStat stat = new FollowStat();
			stat.setUserId(userId);
			stat.setFollow(followMap.get(userId));
			stat.setFollowed(followedMap.get(userId));
			
			statMap.put(userId, stat);
		}
		
		return statMap;
	}

	/******************************** Service ********************************/
	static final String URI_USER_FOLLOW = "/flow/fnd/user/follow/create";
	static final String URI_EXIST_FOLLOW = "/flow/fnd/user/follow/exist";
	static final String URI_COUNT_FOLLOW = "/flow/fnd/user/follow/count";
	
	@Override
	@Transactional
	public String doService(String uri, Map<String, Object> params) {
		if(URI_EXIST_FOLLOW.equals(uri)) {
			return existFollow(params);
		} else if(URI_COUNT_FOLLOW.equals(uri)) {
			return countFollow(params);
		} else if(URI_USER_FOLLOW.equals(uri)) {
			return follow(params);
		} 
		
		return null;
	}
	
	private String countFollow(Map<String, Object> params) {
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		Long userFollow = Long.parseLong((String)params.get("user_follow"));
		FollowStat stat = getFollowStat(userFollow);
		
		return successWithData(stat);
	}

	/**
	 * {
		"user_follow": 0L
		}
	 *
	 * @param params
	 * @return
	 */
	private String existFollow(Map<String, Object> params) {
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		Long userId = threadSession.getUserId(), userFollow = Long.parseLong(params.get("user_follow").toString());
		Follow follow = findFollow(userId, userFollow);
		
		return successWithData( follow == null ? 0 : 1 );
	}

	/**
	 * {
		"user_ follow": 0L // 被关注用户
		"action_type":0 // 1 关注 2 取消关注
		}
	 * @param params
	 * @return
	 */
	private String follow(Map<String, Object> params) {
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		Long userId = threadSession.getUserId(), userFollow = Long.parseLong(params.get("user_follow").toString());
		Integer actionType = (Integer)params.get("action_type");
		if(actionType != Constants.ACTION_FOLLOW && actionType != Constants.ACTION_UNFOLLOW) {
			return errorWithKey("sys_err_param");
		}
		
		FollowStat stat = null;
		if(actionType == Constants.ACTION_FOLLOW) {
			stat = createFollow(userId, userFollow);			
		} else {
			stat = cancelFollow(userId, userFollow);
		}
		
		return stat == null ? Constants.RESPONSE_SUCCESS : successWithData(stat);
	}

}
