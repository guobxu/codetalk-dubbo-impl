package me.codetalk.apps.flow.post.service;

import java.util.List;
import java.util.Set;

import me.codetalk.apps.flow.post.entity.CommentThread;
import me.codetalk.apps.flow.post.entity.Post;
import me.codetalk.apps.flow.post.entity.vo.PostVO;

public interface PostService {

	public Long createPost(Post post);
	
	public PostVO findPostById(Long postId);
	
	public List<PostVO> findPostByUser(Long userId, Integer begin, Integer count);
	
	public List<PostVO> findPostLikeByUser(Long userId, Integer begin, Integer count);
	
	public List<PostVO> findPostById(List<Long> postIdList);
	
	public PostVO findPollPostById(Long postId);
	
	public void updatePostTags(Long postId, String tags);
	
	public void updatePost(Post post);
	
	public List<PostVO> findLatestPost(int begin, int count);
	
	// 返回时间端内的tag, 按时间正序
	public List<String> findPostTagBetween(Long start, Long end, int begin, int count);
	
	public Long likePost(Long userId, Long postId);
	
	public Long unlikePost(Long userId, Long postId);
	
	public Set<Long> findLikedIn(Long userId, List<Long> postIdList);
	
	/**
	 * 
	 * @param tags
	 * @param page
	 * @param size
	 * @param beginDate 包含, 允许null
	 * @param endDate 不包含, 允许null
	 * @return 如果没有搜索到结果, 返回空
	 */
	public List<PostVO> searchByTags(List<String> tags, Long beginDate, Long endDate, Integer page, Integer size);

	public List<PostVO> searchByKeyword(String q, Long beginDate, Long endDate, Integer page, Integer size);
	
	public List<PostVO> searchByKeywordAndTags(String q, List<String> tags, Long beginDate, Long endDate, Integer page, Integer size);
	
	public List<PostVO> searchByUserId(Long userId, Long beginDate, Long endDate, Integer page, Integer size);
	
}
