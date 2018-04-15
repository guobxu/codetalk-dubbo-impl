package me.codetalk.apps.xiaoma.post.cache;

import java.util.List;
import java.util.Map;

import me.codetalk.apps.xiaoma.post.entity.vo.PostVO;

public interface PostCache {

	public PostVO getPostById(Long postId);
	
	public void setPost(PostVO post);
	
	// stat
	public Long incrPostLike(Long postId);
	public Long decrPostLike(Long postId);
	
	public Long incrPostCmnt(Long postId);
	public Long decrPostCmnt(Long postId);
	
	public Map<Long, Long> getPostLike(List<Long> postIdList);
	public Map<Long, Long> getPostCmnt(List<Long> postIdList);
	
}
