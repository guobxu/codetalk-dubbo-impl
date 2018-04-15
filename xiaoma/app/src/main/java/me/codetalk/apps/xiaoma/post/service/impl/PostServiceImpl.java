package me.codetalk.apps.xiaoma.post.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.Constants;
import me.codetalk.apps.xiaoma.AppConstants;
import me.codetalk.apps.xiaoma.post.cache.PostCache;
import me.codetalk.apps.xiaoma.post.entity.Post;
import me.codetalk.apps.xiaoma.post.entity.vo.PostVO;
import me.codetalk.apps.xiaoma.post.mapper.PostMapper;
import me.codetalk.apps.xiaoma.post.service.CommentService;
import me.codetalk.apps.xiaoma.post.service.PostSearchService;
import me.codetalk.apps.xiaoma.post.service.PostService;
import me.codetalk.apps.xiaoma.service.AbstractAppService;
import me.codetalk.apps.xiaoma.user.entity.FollowStat;
import me.codetalk.apps.xiaoma.user.service.FollowService;
import me.codetalk.auth.aop.ThreadSession;
import me.codetalk.auth.entity.User;
import me.codetalk.auth.service.AuthService;
import me.codetalk.messaging.IMessageSender;
import me.codetalk.util.CollectionUtils;
import me.codetalk.util.MapUtils;
import me.codetalk.util.StringUtils;

@Service("postService")
public class PostServiceImpl extends AbstractAppService implements PostService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostServiceImpl.class);
	
	@Autowired
	private PostMapper postMapper;
	
	@Autowired
	private IMessageSender mesgSender;
	
	@Autowired
	private PostSearchService postSearchService;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private FollowService followService;
	
	@Autowired
	private PostCache postCache;
	
	@Autowired
	private CommentService cmntService;
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public Long createPost(Post post) {
		postMapper.insertPost(post);
		
		Long postId = post.getId();
		
		// mesg
		mesgSender.sendMessage(AppConstants.MESG_POST_CREATE, post);
		
		return postId;
	}

	@Override
	public PostVO findPostById(Long postId) {
		PostVO post = postCache.getPostById(postId);
		if(post == null) {
			post = postMapper.selectPostById(postId);
			postCache.setPost(post);
		}

		enrichPosts(post);
		
		return post;
	}
	
	@Override
	public PostVO findPollPostById(Long postId) {
		PostVO post = postCache.getPostById(postId);
		if(post == null) {
			post = postMapper.selectPollPostById(postId);
			postCache.setPost(post);
		}

		enrichPosts(post);
		
		return post;
	}

	@Override
	public void updatePostTags(Long postId, String tags) {
		postMapper.updatePostTags(postId, tags);
	}

	@Override
	public List<String> findPostTagBetween(Long start, Long end, int begin, int count) {
		return postMapper.selectPostTagBetween(start, end, begin, count);
	}
	
	@Override
	public List<PostVO> findPostById(List<Long> postIdList) {
		List<PostVO> posts =  postMapper.selectPostByIdIn(postIdList);
		if(!posts.isEmpty()) {
			enrichPosts(posts);
		}
		
		return posts;
	}
	
	@Override
	public List<PostVO> findLatestPost(int begin, int count) {
		List<PostVO> posts = postMapper.selectLatestPost(begin, count);
		if(!posts.isEmpty()) {
			enrichPosts(posts);
		}
		
		return posts;
	}

	@Override
	public List<PostVO> searchByTags(List<String> tags, Long beginDate, Long endDate, Integer page, Integer size) {
		List<Long> postIdList = postSearchService.searchByTags(tags, beginDate, endDate, page, size);
		
		return postListById(postIdList);
	}

	@Override
	public List<PostVO> searchByKeyword(String q, Long beginDate, Long endDate, Integer page, Integer size) {
		List<Long> postIdList = postSearchService.searchByKeyword(q, beginDate, endDate, page, size);
		
		return postListById(postIdList);
	}
	
	@Override
	public List<PostVO> searchByKeywordAndTags(String q, List<String> tags, Long beginDate, Long endDate, Integer page, Integer size) {
		List<Long> postIdList = postSearchService.searchByKeywordAndTags(q, tags, beginDate, endDate, page, size);
		
		return postListById(postIdList);
	}

	private List<PostVO> postListById(List<Long> postIdList) {
		if(postIdList.isEmpty()) return new ArrayList<>();
		
		List<PostVO> postList = findPostById(postIdList);
		if(!postList.isEmpty()) {
			enrichPosts(postList);
		}
		
		return sortPostList(postIdList, postList);
	}
	
	private List<PostVO> sortPostList(List<Long> postIdList, List<PostVO> postList) {
		List<PostVO> sorted = new ArrayList<>();
		
		for(Long postId : postIdList) {
			for(PostVO post : postList) {
				if(post.getId().equals(postId)) {
					sorted.add(post);
					break;
				}
			}
		}
		
		return sorted;
	}

	@Override
	public void updatePost(Post post) {
		postMapper.updatePost(post);
	}

	@Override
	@Transactional
	public Long likePost(Long userId, Long postId) {
		postMapper.insertPostLike(userId, postId);
		
		return postCache.incrPostLike(postId);
	}

	@Override
	@Transactional
	public Long unlikePost(Long userId, Long postId) {
		postMapper.deletePostLike(userId, postId);
		
		return postCache.decrPostLike(postId);
	}
	
	public Set<Long> findLikedIn(Long userId, List<Long> postIdList) {
		return postMapper.selectLikedIn(userId, postIdList);
	}

	@Override
	public List<PostVO> findPostByUser(Long userId, Integer begin, Integer count) {
		List<PostVO> posts =  postMapper.selectPostByUser(userId, begin, count);
		if(!posts.isEmpty()) {
			enrichPosts(posts);
		}
		
		return posts;
	}
	
	@Override
	public List<PostVO> findPostLikeByUser(Long userId, Integer begin, Integer count) {
		List<PostVO> posts =  postMapper.selectPostLikeByUser(userId, begin, count);
		if(!posts.isEmpty()) {
			enrichPosts(posts);
		}
		
		return posts;
	}
	
	private void enrichPosts(PostVO... posts) {
		enrichPosts(Arrays.asList(posts));
	}
	
	private void enrichPosts(List<PostVO> posts) {
		List<Long> postIdList = new ArrayList<>();
		Set<Long> postUserSet = new HashSet<>();
		for(PostVO post : posts) {
			postIdList.add(post.getId());
			postUserSet.add(post.getUserId());
		}
		
		// user info
		Map<Long, User> userMap = authService.getUsersAsMap(CollectionUtils.toList(postUserSet));
		for(PostVO post : posts) {
			User user = userMap.get(post.getUserId());
			if(user == null) continue;
			
			post.setUserLogin(user.getLogin());
			post.setUserName(user.getName());
			post.setUserProfile(user.getProfile());
		}
		
		// like count
		Map<Long, Long> likeStatMap = postCache.getPostLike(postIdList),
				cmntStatMap = postCache.getPostCmnt(postIdList);
		
		for(PostVO post : posts) {
			Long postId = post.getId();
			
			post.setLikeCount(likeStatMap.get(postId));
			post.setCommentCount(cmntStatMap.get(postId));
		}
		
		Long userId = threadSession.getUserId();
		if(userId == null || posts.isEmpty()) return;
		
		// like data
		Set<Long> postLiked = findLikedIn(userId, postIdList);
		// follow or not
		Set<Long> userFollowed = followService.findFollowIn(userId, CollectionUtils.toList(postUserSet));
		// follow stats
		Map<Long, FollowStat> fstatMap = followService.getFollowStat(CollectionUtils.toList(postUserSet));
		
		for(PostVO post : posts) {
			post.setLiked(postLiked.contains(post.getId()) ? 1 : 0);
			post.setFollowed(userFollowed.contains(post.getUserId()) ? 1 : 0);
			
			FollowStat stat = fstatMap.get(post.getUserId());
			post.setCountFollow(stat.getFollow());
			post.setCountFollowed(stat.getFollowed());
		}
		
	}
	
	/*********************************** Service ***********************************/
	static final String URI_POST_DTL = "/xiaoma/api/post";
	static final String URI_POST_CREATE = "/xiaoma/api/post/create";
	static final String URI_POST_SEARCH = "/xiaoma/api/post/search";
	
	static final String URI_POST_LISTBYUSER = "/xiaoma/api/post/listbyuser";
	static final String URI_POST_LIKEBYUSER = "/xiaoma/api/post/likebyuser";
	
	static final String URI_POST_LIKE = "/xiaoma/api/post/like";

	@Transactional
	public String doService(String uri, Map<String, Object> params) {
		if(URI_POST_SEARCH.equals(uri)) {
			return search(params);
		} else if(URI_POST_DTL.equals(uri)) {
			return postDetail(params);
		} else if(URI_POST_LIKE.equals(uri)) {
			return postLike(params);
		} else if(URI_POST_CREATE.equals(uri)) {
			return postCreate(params);
		} else if(URI_POST_LISTBYUSER.equals(uri)) {
			return postListByUser(params);
		} else if(URI_POST_LIKEBYUSER.equals(uri)) {
			return postLikeByUser(params);
		} 
		
		return null;
	}
	
	private String postDetail(Map<String, Object> params) {
		Long postId = Long.parseLong((String)params.get("post_id"));
		
		PostVO post = findPostById(postId);
		
		post.setCommentThreads(cmntService.findCommentThreadsByPost(postId));
//		enrichPosts(post);
		
		return successWithData(post);
	}

	/**
	 * Params:
	 * -------------
	 * "pf_type" = 0 
	   "user_id" = 0L // 用户ID
	   "access_token" = "xx" // token
	   q = "xx" // 搜索关键字, 可选
	   tags="tag1,tag2,tag3..." // 标签列表, 可选
	   begin=0 // begin必须是count的整数倍数
	   count=0 // 条数
	 *
	 * @param params
	 * @return
	 */
	private String search(Map<String, Object> params) {
		LOGGER.info("In search... Params = " + params);
		
		String q = StringUtils.toString(params.get("q"), true).trim(),
				tags = StringUtils.toString(params.get("tags"), true).trim();
		
		boolean nullq = StringUtils.isNull(q), nulltags = StringUtils.isNull(tags);
		
		String beginDateStr = (String)params.get("begin_date"), endDateStr = (String)params.get("end_date");
		Long beginDate = ( StringUtils.isNull(beginDateStr) ? null : Long.parseLong(beginDateStr) ),
				endDate = ( StringUtils.isNull(endDateStr) ? null : Long.parseLong(endDateStr) );
		
		// page & size
		String beginStr = (String)params.get("begin"), countStr = (String)params.get("count");

		Integer page = null, size = null, begin = null, count = null;
		if(StringUtils.isNotNull(beginStr) && StringUtils.isNotNull(countStr)) {
			begin = Integer.parseInt(beginStr);
			count = Integer.parseInt(countStr); 
			page = begin / count;
			size = count;
		}
		
		List<PostVO> posts = null;
		if(!nullq && !nulltags) {
			posts = searchByKeywordAndTags(q, StringUtils.splitNoRegex(tags, ","), beginDate, endDate, page, size);
		} else if(!nullq) {
			posts = searchByKeyword(q, beginDate, endDate, page, size);
		} else if(!nulltags) {
			posts = searchByTags(StringUtils.splitNoRegex(tags, ","), beginDate, endDate, page, size);
		} else {
//			if(threadSession.authorized()) {
//				posts = searchByUserId(threadSession.getUserId(), beginDate, endDate, page, size);
//			} else {
//				posts = findLatestPost(begin, count);
//			}
		}
		
		return successWithData(posts);
	}

	/**
	 * {
		"pf_type": 0 
		"user_id": 0L // 用户ID
		"access_token": "xx" // token
		"post_type": 0 // 帖子类别1基本2 投票
		"post_content": "xx" // 帖子内容; 最长300
		"post_imgs": "['xx1', 'xx2'...]" // 帖子图片最多三张, 可选
		"poll_setup": { // 投票设置, 可选
			"poll_options": ["xx1", "xx2"...] // 投票选项; 最长50
			"poll_duration": 0L // 投票时间长度
		}
		}
	 *
	 * @param params
	 * @return
	 */
	private String postCreate(Map<String, Object> params) {
		LOGGER.info("In postCreate... Params = " + params);
		
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		Post newPost = new Post();
		newPost.setUserId(threadSession.getUserId());
		int type = Integer.parseInt(params.get("post_type").toString());
		newPost.setType(type);
		newPost.setContent(params.get("post_content").toString());
		
		Object postImgObj = params.get("post_imgs");
		if(postImgObj != null) newPost.setImages(postImgObj.toString());
		
		long createDate = System.currentTimeMillis();
		newPost.setCreateDate(createDate);
		
		Long postId = createPost(newPost);
		
		return successWithData(MapUtils.toMap("post_id", postId));
	}
	
	/**
	 * Params:
	 * {
	 *	"post_id": 0L // 帖子id, 可选
	 *	"comment_id": 0L // 评论id, 可选
	 *	"action_type":0 // 1 点赞 2 取消
	 *	}
	 *
	 * @param params
	 * @return
	 */
	private String postLike(Map<String, Object> params) {
		if(!threadSession.authorized()) {
			return errorWithCodeKey(Constants.CODE_ERROR_RELOGIN, "auth_err_session_notfound");
		}
		
		Integer actionType = (Integer)params.get("action_type"); // 1 点赞 2 取消
		if(actionType != Constants.ACTION_LIKE && actionType != Constants.ACTION_UNLIKE) {
			return errorWithKey("sys_err_param");
		}
		
		Long postId = Long.parseLong(params.get("post_id").toString());
		
		Long likes = null;
		if(actionType == Constants.ACTION_LIKE) {
			likes = likePost(threadSession.getUserId(), postId);				
		} else {
			likes = unlikePost(threadSession.getUserId(), postId);
		}
		
		return successWithData(MapUtils.toMap("likes", likes));
	}
	
	/**
	 * Params:
	 * user_id_get=0L // 获取此用户的收藏
		begin=0 // 起始
		count=0 // 条数
	 *
	 * @param params
	 * @return
	 */
	private String postLikeByUser(Map<String, Object> params) {
		Long postUserId = Long.parseLong((String)params.get("user_id_get"));
		Integer begin = Integer.parseInt((String)params.get("begin")),
				count = Integer.parseInt((String)params.get("count"));
		
		List<PostVO> posts = findPostLikeByUser(postUserId, begin, count);
		
		return successWithData(posts);
	}

	/**
	 * Params:
	 * post_user_id=0L // 获取此用户的帖子列表
		begin=0 // 起始
		count=0 // 条数
 	 *
	 * @param uri
	 * @return
	 */
	private String postListByUser(Map<String, Object> params) {
		Long postUserId = Long.parseLong((String)params.get("user_id_get"));
		Integer begin = Integer.parseInt((String)params.get("begin")),
				count = Integer.parseInt((String)params.get("count"));
		
		List<PostVO> posts = findPostByUser(postUserId, begin, count);
		
		return successWithData(posts);
	}

	
}














