package me.codetalk.messaging.redis.listener.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.apps.flow.AppConstants;
import me.codetalk.apps.flow.fnd.entity.TagPosition;
import me.codetalk.apps.flow.post.entity.Mention;
import me.codetalk.apps.flow.post.entity.vo.CommentVO;
import me.codetalk.apps.flow.post.entity.vo.PostVO;
import me.codetalk.apps.flow.post.service.CommentService;
import me.codetalk.apps.flow.post.service.PostService;
import me.codetalk.auth.entity.User;
import me.codetalk.auth.service.AuthService;
import me.codetalk.messaging.MesgObj;
import me.codetalk.messaging.redis.listener.AbstractTagMesgListener;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.StringUtils;

@Component("commentMesgListenr")
public class CommentMesgListener extends AbstractTagMesgListener {

	@Autowired
	private PostService postService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private AuthService authService;
	
	@Override
	@Transactional
	public void onMessage(Message message, byte[] pattern) {
		MesgObj msgobj = mesgToObj(message);
		
		String cacheKey = this.getClass().getName() + "-" + msgobj.getId();
		if(!lockService.lock(cacheKey)) return;
		
		String type = msgobj.getType();
		if(AppConstants.MESG_COMMENT_CREATE.equals(type)) {
			handleCommentCreate(msgobj);
		}	
	}

	/**
	 * 发表评论消息
	 * 
	 * @param msgobj
	 */
	private void handleCommentCreate(MesgObj msgobj) {
		Map<String, Object> cmntData = (Map<String, Object>)msgobj.getData();
		Long userId = (Long)cmntData.get("user_id"),
				cmntId = (Long)cmntData.get("comment_id"), 
				postId = (Long)cmntData.get("post_id"),
				cmntReply = (Long)cmntData.get("comment_reply");
		String content = (String)cmntData.get("comment_content");
		
		if(cmntReply != null) {
			handleCommentReply(cmntId, cmntReply, content);
		} else if(postId != null) {
			handlePostReply(cmntId, postId, content);
		}
	}

	private void handlePostReply(Long cmntId, Long postId, String content) {
		PostVO post = postService.findPostById(postId);
		CommentVO comment = commentService.findCommentById(cmntId);
		
		// thread
		comment.setThread(StringUtils.uuid());
		
		// mention list
		List<Mention> mentionList = new ArrayList<>();
		Long postUserId = post.getUserId(), cmntUserId = comment.getUserId();
		Map<Long, User> userMap = authService.getUsersAsMap(postUserId, cmntUserId);
		User postUser = userMap.get(postUserId), cmntUser = userMap.get(cmntUserId);
		
		Mention cmntUserMention = new Mention(cmntUserId, cmntUser.getLogin(), cmntUser.getProfile()), 
				postUserMention = new Mention(postUserId, postUser.getLogin(), postUser.getProfile());
		mentionList.add(cmntUserMention);
		if(!mentionList.contains(postUserMention)) {
			mentionList.add(postUserMention);
		}
		comment.setMentions(JsonUtils.toJson(mentionList));
		
		comment.setLevel(1);
		
		List<TagPosition> tagPosList = findTagPositions(content);
		comment.setTags(JsonUtils.toJson(tagPosList));
		
		commentService.updateComment(comment);
	}

	private void handleCommentReply(Long cmntId, Long cmntReplyId, String content) {
		CommentVO comment = commentService.findCommentById(cmntId), 
				cmntReply = commentService.lockCommentById(cmntReplyId);
		
		comment.setPostId(cmntReply.getPostId());
		
		// thread 
		// 发表评论后, 首先判断父节点是否存在thread id(tid), 如果有则判断当前节点是否为最先的回复, 如果是则设置为父节点tid, 否则生成新的tid
		if(commentService.hasThreadedReply(cmntReplyId)) {
			comment.setThread(StringUtils.uuid());
		} else {
			comment.setThread(cmntReply.getThread());
		}
		
		// mention list TODO: max 10
		List<Mention> mentionList = (List<Mention>)JsonUtils.fromJsonArray(cmntReply.getMentions(), Mention.class);
		Long cmntUserId = comment.getUserId();
		User cmntUser = authService.getUser(cmntUserId);
		
		Mention cmntUserMention = new Mention(cmntUserId, cmntUser.getLogin(), cmntUser.getProfile());
		mentionList.remove(cmntUserMention);
		mentionList.add(cmntUserMention);
		comment.setMentions(JsonUtils.toJson(mentionList));
		
		comment.setLevel(cmntReply.getLevel() + 1);
		
		List<TagPosition> tagPosList = findTagPositions(content);
		comment.setTags(JsonUtils.toJson(tagPosList));
		
		// ancestors
		String ancestors = cmntReply.getAncestors();
		List<Long> ancestorList  = new ArrayList<>();
		if(ancestors != null) {
			ancestorList.addAll(JsonUtils.fromJsonArray(ancestors, Long.class));
		}
		ancestorList.add(0, cmntReply.getId());
		comment.setAncestors(JsonUtils.toJson(ancestorList));
		
		commentService.updateComment(comment);
	}

}
