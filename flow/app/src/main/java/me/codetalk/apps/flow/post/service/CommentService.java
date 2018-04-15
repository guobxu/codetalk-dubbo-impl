package me.codetalk.apps.flow.post.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import me.codetalk.apps.flow.post.entity.Comment;
import me.codetalk.apps.flow.post.entity.CommentThread;
import me.codetalk.apps.flow.post.entity.vo.CommentVO;

public interface CommentService {

	public Long createComment(Comment comment);
	
	public void updateComment(CommentVO comment);
	
	public CommentVO findCommentById(Long cmntId);
	
	public CommentVO lockCommentById(Long cmntId);
	
	public Map<Long, CommentVO> findCommentAsMap(Long... cmntIds);
	
	public List<CommentThread> findCommentThreadsByPost(Long postId);
	
	public List<CommentThread> findCommentThreadsByComment(Long cmntId);
	
	public Set<Long> findLikedIn(Long userId, List<Long> cmntIdList);
	
	public Long likeComment(Long userId, Long cmntId);

	public Long unlikeComment(Long userId, Long cmntId);
	
	public int countThreadedReply(Long cmntId);
	
	public boolean hasReply(Long cmntId);
	
	public boolean hasThreadedReply(Long cmntId);
	
	/** 
	 * 查找与cmntId所在路径的回复(包括自己), 按level升序
	 * @param cmntId
	 * @return null if cmntId is absent
	 */
	public List<CommentVO> findCommentOnPath(Long cmntId);
	
}
