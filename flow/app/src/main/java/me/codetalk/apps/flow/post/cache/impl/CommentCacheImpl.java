package me.codetalk.apps.flow.post.cache.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.apps.flow.post.cache.CommentCache;
import me.codetalk.apps.flow.post.entity.vo.CommentVO;
import me.codetalk.cache.service.CacheService;
import me.codetalk.stat.StatSupport;

@Component("commentCache")
public class CommentCacheImpl implements CommentCache {

	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private StatSupport statSupport;
	
	public static final String STAT_COMMENT_LIKE = "STAT-COMMENT-LIKE-";
	public static final String STAT_COMMENT_CMNT = "STAT-COMMENT-CMNT-";
	
	static final Long TTL_CMNT = 30 * 60L;	// 评论缓存过期时间
	
	static final String CACHE_CMNT = "COMMENT-";
	
	@Override
	public CommentVO getCommentById(Long cmntId) {
		String cacheKey = CACHE_CMNT + cmntId;
		
		return (CommentVO)cacheService.get(cacheKey);
	}

	@Override
	public void setComment(CommentVO comment) {
		String cacheKey = CACHE_CMNT + comment.getId();
		
		cacheService.set(cacheKey, comment, TTL_CMNT);
	}

	@Override
	public void deleteComment(Long cmntId) {
		String cacheKey = CACHE_CMNT + cmntId;
		
		cacheService.delete(cacheKey);
	}

	/**************************** STAT ****************************/
	
	@Override
	public Long incrCmntReply(Long commentId) {
		return statSupport.incrRangeStatById(commentId, STAT_COMMENT_CMNT, 1);
	}
	
	@Override
	public Long decrCmntReply(Long commentId) {
		return statSupport.incrRangeStatById(commentId, STAT_COMMENT_CMNT, -1);
	}

	@Override
	public Long incrCmntLike(Long commentId) {
		return statSupport.incrRangeStatById(commentId, STAT_COMMENT_LIKE, 1);
	}

	@Override
	public Long decrCmntLike(Long commentId) {
		return statSupport.incrRangeStatById(commentId, STAT_COMMENT_LIKE, -1);
	}
	
	@Override
	public Map<Long, Long> getCmntLike(List<Long> cmntIdList) {
		return statSupport.getRangeStats(cmntIdList, STAT_COMMENT_LIKE);
	}

	@Override
	public Map<Long, Long> getCmntReply(List<Long> cmntIdList) {
		return statSupport.getRangeStats(cmntIdList, STAT_COMMENT_CMNT);
	}

	
	

}













