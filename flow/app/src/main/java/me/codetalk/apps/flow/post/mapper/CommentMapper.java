package me.codetalk.apps.flow.post.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import me.codetalk.apps.flow.post.entity.Comment;
import me.codetalk.apps.flow.post.entity.vo.CommentVO;

public interface CommentMapper {
	
	public int insertComment(Comment comment);
	
	public CommentVO selectCommentById(@Param("cmntId") Long cmntId, @Param("forUpdate") boolean forUpdate);
	
	public List<CommentVO> selectCommentIn(@Param("cmntIdList") List<Long> cmntIdList);

	public void updateComment(Comment comment);
	
	public List<CommentVO> selectThreadCommentsByPost(Long postId);
	
	public List<CommentVO> selectThreadCommentsByComment(Long cmntId);
	
	public Set<Long> selectLikedIn(@Param("userId") Long userId, @Param("cmntIdList") List<Long> cmntIdList);
	
	public void insertCommentLike(@Param("userId") Long userId, @Param("cmntId") Long cmntId);
	
	public void deleteCommentLike(@Param("userId") Long userId, @Param("cmntId") Long cmntId);
	
	public int countCommentReply(Long cmntId);
	
	public int countThreadedReply(Long cmntId);
	
	public List<CommentVO> selectCommentInThread(Long cmntId);
	
	public List<CommentVO> selectCommentsOnPath(@Param("cmntId") Long cmntId, @Param("ancestorList") List<Long> ancestorList);
	
}
