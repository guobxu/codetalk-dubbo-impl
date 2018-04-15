package me.codetalk.apps.flow.post.cache.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.apps.flow.post.cache.PostCache;
import me.codetalk.apps.flow.post.entity.vo.PostVO;
import me.codetalk.cache.service.CacheService;
import me.codetalk.stat.StatSupport;

@Component("postCache")
public class PostCacheImpl implements PostCache {

	// STATs
	public static final String STAT_POST_LIKE = "STAT-POST-LIKE-";
	public static final String STAT_POST_CMNT = "STAT-POST-CMNT-";
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private StatSupport statSupport;
	
	private static final String CACHE_POST = "POST-";
	
	@Override
	public PostVO getPostById(Long postId) {
		String cacheKey = CACHE_POST + postId;
		
		return (PostVO)cacheService.get(cacheKey);
	}

	@Override
	public void setPost(PostVO post) {
		String cacheKey = CACHE_POST + post.getId();
		
		cacheService.set(cacheKey, post, CacheService.DEFAULT_CACHE_EXPIRE);
	}

	/************************** STAT **************************/
	
	@Override
	public Long incrPostLike(Long postId) {
		return statSupport.incrRangeStatById(postId, STAT_POST_LIKE, 1);
	}

	@Override
	public Long decrPostLike(Long postId) {
		return statSupport.incrRangeStatById(postId, STAT_POST_LIKE, -1);
	}

	@Override
	public Long incrPostCmnt(Long postId) {
		return statSupport.incrRangeStatById(postId, STAT_POST_CMNT, 1);
	}

	@Override
	public Long decrPostCmnt(Long postId) {
		return statSupport.incrRangeStatById(postId, STAT_POST_CMNT, -1);
	}

	@Override
	public Map<Long, Long> getPostLike(List<Long> postIdList) {
		return statSupport.getRangeStats(postIdList, STAT_POST_LIKE);
	}

	@Override
	public Map<Long, Long> getPostCmnt(List<Long> postIdList) {
		return statSupport.getRangeStats(postIdList, STAT_POST_CMNT);
	}

}
