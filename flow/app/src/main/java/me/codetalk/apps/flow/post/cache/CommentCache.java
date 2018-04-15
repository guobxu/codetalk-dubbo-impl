package me.codetalk.apps.flow.post.cache;

import java.util.List;
import java.util.Map;

import me.codetalk.apps.flow.post.entity.vo.CommentVO;

public interface CommentCache {

	public CommentVO getCommentById(Long cmntId);
	
	public void setComment(CommentVO comment);
	
	public void deleteComment(Long cmntId);
	
	// STAT
	public Long incrCmntReply(Long commentId);
	public Long decrCmntReply(Long commentId);
	
	public Long incrCmntLike(Long commentId);
	public Long decrCmntLike(Long commentId);
	
	public Map<Long, Long> getCmntLike(List<Long> cmntIdList);
	public Map<Long, Long> getCmntReply(List<Long> cmntIdList);
	
}
