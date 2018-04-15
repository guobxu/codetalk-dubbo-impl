package me.codetalk.apps.xiaoma.post.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import me.codetalk.apps.xiaoma.post.entity.Post;
import me.codetalk.apps.xiaoma.post.entity.vo.PostVO;

public interface PostMapper {

	public void insertPost(Post post);

	public PostVO selectPostById(@Param("postId") Long postId);
	
	public List<PostVO> selectPostByIdIn(List<Long> postIdList);
	
	public PostVO selectPollPostById(@Param("postId") Long postId);
	
	public void updatePostTags(@Param("postId") Long postId, @Param("tags") String tags);
	
	public void updatePost(@Param("post") Post post);
	
	public List<String> selectPostTagBetween(@Param("start") Long start, @Param("end") Long end, 
										@Param("begin") int begin, @Param("count") int count);
	
	public List<PostVO> selectLatestPost(@Param("begin") int begin, @Param("count") int count);
	
	// POST like
	public void insertPostLike(@Param("userId") Long userId, @Param("postId") Long postId);
	
	public void deletePostLike(@Param("userId") Long userId, @Param("postId") Long postId);
	
	public Set<Long> selectLikedIn(@Param("userId") Long userId, @Param("postIdList") List<Long> postIdList);
	
	public List<PostVO> selectPostByUser(@Param("userId") Long userId, @Param("begin") Integer begin, @Param("count") Integer count);
	
	public List<PostVO> selectPostLikeByUser(@Param("userId") Long userId, @Param("begin") Integer begin, @Param("count") Integer count);
	
	/********************* devonly *********************/
	public List<PostVO> selectAllPosts();
	
}
