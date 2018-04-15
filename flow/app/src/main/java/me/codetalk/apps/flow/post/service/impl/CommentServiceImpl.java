package me.codetalk.apps.flow.post.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.Constants;
import me.codetalk.apps.flow.AppConstants;
import me.codetalk.apps.flow.post.cache.CommentCache;
import me.codetalk.apps.flow.post.cache.PostCache;
import me.codetalk.apps.flow.post.entity.Comment;
import me.codetalk.apps.flow.post.entity.CommentThread;
import me.codetalk.apps.flow.post.entity.vo.CommentVO;
import me.codetalk.apps.flow.post.entity.vo.PostVO;
import me.codetalk.apps.flow.post.mapper.CommentMapper;
import me.codetalk.apps.flow.post.service.CommentService;
import me.codetalk.apps.flow.post.service.PostService;
import me.codetalk.apps.flow.service.AbstractAppService;
import me.codetalk.auth.aop.ThreadSession;
import me.codetalk.auth.entity.User;
import me.codetalk.auth.service.AuthService;
import me.codetalk.messaging.IMessageSender;
import me.codetalk.util.CollectionUtils;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.MapUtils;
import me.codetalk.util.StringUtils;

@Service("commentService")
public class CommentServiceImpl extends AbstractAppService implements CommentService {

	@Autowired
	private CommentMapper commentMapper;
	
	@Autowired
	private ThreadSession threadSession;
	
	@Autowired
	private IMessageSender mesgSender;
	
	@Autowired
	private CommentCache cmntCache;
	
	@Autowired
	private PostCache postCache;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private AuthService authService;
	
	@Override
	@Transactional
	public Long createComment(Comment comment) {
		commentMapper.insertComment(comment);
		
		Long cmntIdReply = comment.getCommentReply();
		if(cmntIdReply != null) {
			cmntCache.incrCmntReply(cmntIdReply);
		} else {
			postCache.incrPostCmnt(comment.getPostId());
		}
		
		// mesg
		mesgSender.sendMessage(AppConstants.MESG_COMMENT_CREATE, comment);
		
		return comment.getId();
	}
	
	@Override
	@Transactional
	public void updateComment(CommentVO comment) {
		commentMapper.updateComment(comment);
		
		cmntCache.setComment(comment);
	}
	
	@Override
	@Transactional
	public Long likeComment(Long userId, Long cmntId) {
		commentMapper.insertCommentLike(userId, cmntId);
		
		return cmntCache.incrCmntLike(cmntId);
	}

	@Override
	@Transactional
	public Long unlikeComment(Long userId, Long cmntId) {
		commentMapper.deleteCommentLike(userId, cmntId);
		
		return cmntCache.decrCmntLike(cmntId);
	}
	
	@Override
	public CommentVO findCommentById(Long cmntId) {
		CommentVO cmnt = cmntCache.getCommentById(cmntId);
		if(cmnt != null) return cmnt;
		
		cmnt = commentMapper.selectCommentById(cmntId, false);
		if(cmnt != null && cmnt.getThread() != null) {
			cmntCache.setComment(cmnt);
		}
		
		return cmnt;
	}
	
	@Override
	public boolean hasReply(Long cmntId) {
		int replyCount = commentMapper.countCommentReply(cmntId);
		
		return replyCount > 0;
	}
	
	@Override
	public int countThreadedReply(Long cmntId) {
		return commentMapper.countThreadedReply(cmntId);
	}
	
	@Override
	public boolean hasThreadedReply(Long cmntId) {
		int replyCount = commentMapper.countThreadedReply(cmntId);
		
		return replyCount > 0;
	}
	
	@Override
	public Map<Long, CommentVO> findCommentAsMap(Long... cmntIds) {
		Map<Long, CommentVO> cmntMap = new HashMap<>();
		
		List<CommentVO> comments = commentMapper.selectCommentIn(Arrays.asList(cmntIds));
		for(CommentVO comment : comments) {
			cmntMap.put(comment.getId(), comment);
		}
		
		return cmntMap;
	}
	
	@Override
	public Set<Long> findLikedIn(Long userId, List<Long> cmntIdList) {
		return commentMapper.selectLikedIn(userId, cmntIdList);
	}
	
	@Override
	public CommentVO lockCommentById(Long cmntId) {
		return commentMapper.selectCommentById(cmntId, true);
	}
	
	@Override
	public List<CommentThread> findCommentThreadsByPost(Long postId) {
		List<CommentThread> cmntThreads = new ArrayList<>();
		
		List<CommentVO> comments = commentMapper.selectThreadCommentsByPost(postId);
		if(comments.isEmpty()) return cmntThreads;
		
		enrichComments(comments);
		
		commentListToThreads(comments, cmntThreads);
		
		return cmntThreads;
	}
	
	@Override
	public List<CommentThread> findCommentThreadsByComment(Long cmntId) {
		List<CommentThread> cmntThreads = new ArrayList<>();
		
		List<CommentVO> comments = commentMapper.selectThreadCommentsByComment(cmntId);
		if(comments.isEmpty()) return cmntThreads;
		
		enrichComments(comments);
		
		commentListToThreads(comments, cmntThreads);
		
		return cmntThreads;
	}
	
	private void commentListToThreads(List<CommentVO> comments, List<CommentThread> cmntThreads) {
		CommentThread thread = null;
		for(CommentVO comment : comments) {
			String threadId = comment.getThread();
			if(thread == null || !threadId.equals(thread.getId())) {
				if(thread != null) cmntThreads.add(thread);
				
				thread = new CommentThread();
				thread.setId(threadId);
				thread.setCreateDate(comment.getCreateDate());
			}
			
			thread.addComment(comment);
		}
		cmntThreads.add(thread);
		
		cmntThreads.sort((t1, t2) -> {
			return t1.getCreateDate() > t2.getCreateDate() ? 1 : -1;
		});
	}
	
	@Override
	public List<CommentVO> findCommentOnPath(Long cmntId) {
		List<CommentVO> pathComments = commentMapper.selectCommentInThread(cmntId);
		if(pathComments.isEmpty()) return null;
		
		CommentVO currentCmnt = pathComments.get(0);
		
		String ancestors = currentCmnt.getAncestors();
		if(ancestors != null) {
			List<Long> cmntIdList = JsonUtils.fromJsonArray(ancestors, Long.class);
			List<CommentVO> ancestorCmnts = commentMapper.selectCommentIn(cmntIdList);
			Collections.sort(ancestorCmnts, (c1, c2) -> {
				return c1.getLevel() - c2.getLevel();
			});
			
			pathComments.addAll(0, ancestorCmnts);
		}
		
		return pathComments;
	}
	
	private void enrichComments(List<CommentVO> comments) {
		List<Long> cmntIdList = new ArrayList<>();
		Set<Long> cmntUserSet = new HashSet<>();
		for(CommentVO comment : comments) {
			cmntIdList.add(comment.getId());
			cmntUserSet.add(comment.getUserId());
		}
		
		// user info
		Map<Long, User> userMap = authService.getUsersAsMap(CollectionUtils.toList(cmntUserSet));
		for(CommentVO comment : comments) {
			User user = userMap.get(comment.getUserId());
			if(user == null) continue;
			
			comment.setUserLogin(user.getLogin());
			comment.setUserName(user.getName());
			comment.setUserProfile(user.getProfile());
		}
		
		// like count
		Map<Long, Long> likeStatMap = cmntCache.getCmntLike(cmntIdList),
				cmntStatMap = cmntCache.getCmntReply(cmntIdList);
		
		for(CommentVO comment : comments) {
			Long cmntId = comment.getId();
			
			comment.setLikeCount(likeStatMap.get(cmntId));
			comment.setCommentCount(cmntStatMap.get(cmntId));
		}
		
		Long userId = threadSession.getUserId();
		if(userId == null || comments.isEmpty()) return;
		
		// like data
		Set<Long> cmntLiked = findLikedIn(userId, cmntIdList);
		
		for(CommentVO comment : comments) {
			comment.setLiked(cmntLiked.contains(comment.getId()) ? 1 : 0);
		}
		
	}
	
	/******************************* Service *******************************/
	static final String URI_COMMENT_CREATE = "/flow/post/comment/create";
	static final String URI_COMMENT_LIKE = "/flow/post/comment/like";
	static final String URI_COMMENT_DTL = "/flow/post/comment";

	@Override
	@Transactional
	public String doService(String uri, Map<String, Object> params) {
		if(URI_COMMENT_LIKE.equals(uri)) {
			return commentLike(params);
		} else if(URI_COMMENT_DTL.equals(uri)) {
			return commentDetail(params);
		} else if(URI_COMMENT_CREATE.equals(uri)) {
			return commentCreate(params);
		}
		
		return null;
	}
	
	/**
	 * Params:
	 * "pf_type" = 0 
	 *	comment_id = 0L // 帖子id
	 *
	 * @param params
	 * @return
	 */
	private String commentDetail(Map<String, Object> params) {
		Long cmntId = Long.parseLong((String)params.get("comment_id"));
		List<CommentThread> cmntThreads = findCommentThreadsByComment(cmntId);
		
		return successWithData(cmntThreads);
	}

	/**
	 * Params:
	 * {
	 *	"comment_id": 0L // 评论id, 可选
	 *	"action_type":0 // 1 点赞 2 取消
	 *	}
	 *
	 * @param params
	 * @return
	 */
	private String commentLike(Map<String, Object> params) {
		if(!threadSession.authorized()) {
			return errSessionMsg();
		}
		
		Integer actionType = (Integer)params.get("action_type"); // 1 点赞 2 取消
		if(actionType != Constants.ACTION_LIKE && actionType != Constants.ACTION_UNLIKE) {
			return errParamMsg();
		}
		
		Long cmntId = Long.parseLong(params.get("comment_id").toString());
		
		Long likes = null;
		if(actionType == Constants.ACTION_LIKE) {
			likes = likeComment(threadSession.getUserId(), cmntId);				
		} else {
			likes = unlikeComment(threadSession.getUserId(), cmntId);
		}
		
		return successWithData(MapUtils.toMap("likes", likes));
	}
	
	/**
	 * Params: 
	 * {
		"post_id_reply": 0L // 帖子id, 可选
		"comment_id_reply":0L // 评论id, 可选 
		"comment_content": "xx" // 评论内容; 最长300
		"comment_imgs": ["xx1"...] // 评论图片, 最多一张
		}
	 *
	 * @param params
	 */
	private String commentCreate(Map<String, Object> params) {
		if(!threadSession.authorized()) {
			return errSessionMsg();
		}
		
		Object postIdObj = params.get("post_id_reply"), cmntIdObj = params.get("comment_id_reply");
		Long postIdReply = postIdObj == null ? null : Long.parseLong(postIdObj.toString()), 
				cmntIdReply = cmntIdObj == null ? null : Long.parseLong(cmntIdObj.toString());
		
		String content = (String)params.get("comment_content"), 
				images = (String)params.get("comment_imgs");
		
		if((postIdReply == null && cmntIdReply == null) || StringUtils.isBlank(content)) {
			return errParamMsg();
		}
		
		if(cmntIdReply != null) {
			return commentReplyCreate(cmntIdReply, content, images);
		} else {
			return postCommentCreate(postIdReply, content, images);
		}
	}

	private String postCommentCreate(Long postIdReply, String content, String images) {
		Comment comment = new Comment();
		comment.setUserId(threadSession.getUserId());
		comment.setPostId(postIdReply);
		comment.setContent(content);
		comment.setImages(images);
		comment.setCreateDate(System.currentTimeMillis());
		
		Long cmntId = createComment(comment);
		
		return successWithData(MapUtils.toMap("comment_id", cmntId));
	}
	
	private String commentReplyCreate(Long cmntIdReply, String content, String images) {
		Comment comment = new Comment();
		comment.setUserId(threadSession.getUserId());
		comment.setCommentReply(cmntIdReply);
		comment.setContent(content);
		comment.setImages(images);
		comment.setCreateDate(System.currentTimeMillis());
		
		Long cmntId = createComment(comment);
		
		return successWithData(MapUtils.toMap("comment_id", cmntId));
	}

	



	



	

	
	
}
