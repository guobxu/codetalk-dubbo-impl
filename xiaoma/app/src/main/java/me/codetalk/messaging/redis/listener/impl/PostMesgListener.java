package me.codetalk.messaging.redis.listener.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.apps.xiaoma.AppConstants;
import me.codetalk.apps.xiaoma.post.cache.TagCache;
import me.codetalk.apps.xiaoma.post.elastic.DocPost;
import me.codetalk.apps.xiaoma.post.elastic.repos.PostRepository;
import me.codetalk.apps.xiaoma.post.entity.Post;
import me.codetalk.apps.xiaoma.post.entity.TagPosition;
import me.codetalk.apps.xiaoma.post.service.PostService;
import me.codetalk.auth.entity.User;
import me.codetalk.messaging.MesgObj;
import me.codetalk.messaging.redis.listener.AbstractTagMesgListener;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.StringUtils;

@Component("postMesgListener")
public class PostMesgListener extends AbstractTagMesgListener {

	@Autowired
	private PostRepository postRepos;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private TagCache tagCache;
	
	@Override
	@Transactional
	public void onMessage(Message message, byte[] pattern) {
		MesgObj msgobj = mesgToObj(message);
		
		String cacheKey = this.getClass().getName() + "-" + msgobj.getId();
		if(!lockService.lock(cacheKey)) return;
		
		String type = msgobj.getType();
		if(AppConstants.MESG_POST_CREATE.equals(type)) {
			handlePostCreate(msgobj);
		}	
	}

	/**
	 * 创建post消息
	 * 
	 * @param msgobj
	 */
	private void handlePostCreate(MesgObj msgobj) {
		Map<String, Object> postData = (Map<String, Object>)msgobj.getData();
		Long postId = (Long)postData.get("post_id");
		String content = (String)postData.get("post_content");
		
		List<TagPosition> tags = findTagPositions(content);

		// 更新post
		Long userId = Long.parseLong(postData.get("user_id").toString());
		User user = authService.getUser(userId);
		
		Post postForUpdate = new Post();
		postForUpdate.setId(postId);
		postForUpdate.setTags(JsonUtils.toJson(tags));
		
		postService.updatePost(postForUpdate);
		
		// index
		Set<String> tagSet = new HashSet<>();
		for(TagPosition postTag : tags) {
			tagSet.add(postTag.getText().toLowerCase()); // 搜索: 不区分大小写
		}
		
		postRepos.save(createDocPost(postData, tagSet));
		
		// Tag Board
		Map<String, Integer> tagHits = new HashMap<String, Integer>();
		for(TagPosition tag : tags) {
			String tagText = tag.getText();
			
			Integer hits = tagHits.get(tagText);
			tagHits.put(tagText, hits == null ? 1 : 1 + hits);
		}
		
		tagCache.incrDayHits(tagHits);
	}

	// "#[^#\\s]{1,}"
	private static Object[] processSearchText(String text) {
		StringBuffer qbuf = new StringBuffer();
		Set<String> tags = new HashSet<>();
		
		int ts = -1; // tag start
		for(int i = 0, len = text.length(); i < len; i++) {
			char c = text.charAt(i);
			
			if(ts == -1) {	// not in tag
				if(c == '#') {
					if(i == len -1) continue;
					
					if(!isTagChar(text.charAt(i + 1))) {
						continue;
					}
					
					if(i == 0 || !isTagChar(text.charAt(i - 1))) {
						ts = i; // mark tag start
					}
				} else { 
					qbuf.append(StringUtils.isPuncChar(c) ? ' ' : c);
				}
			} else {
				if(!isTagChar(c)) {
					tags.add(text.substring(ts + 1, i).toLowerCase()); // add tag
					ts = -1; // mark no tag
					qbuf.append(' ');
				} else if(i == len - 1) {
					tags.add(text.substring(ts + 1).toLowerCase()); // add tag
				}
			}
		}
		
		return new Object[] {qbuf.toString(), tags};
	}
	
	private static boolean isTagChar(char c) {
		return !Character.isWhitespace(c) && !StringUtils.isPuncChar(c);
	}
	
	// 根据post创建DocPost
	private DocPost createDocPost(Map<String, Object> postData, Set<String> tags) {
		DocPost docPost = new DocPost();
		docPost.setId(StringUtils.uuid());
		docPost.setPostId((Long)postData.get("post_id"));
		docPost.setPostContent((String)postData.get("post_content"));
		docPost.setPostTags(tags);
		docPost.setPostType((Integer)postData.get("post_type"));
		docPost.setCreateBy(postData.get("user_id").toString());
		docPost.setCreateDate(System.currentTimeMillis());
		
		return docPost;
	}
	
	public static void main(String[] args) {
//		String s = "你好 #你好 ##你好 #你 好  #你好#安妮 #换行\n#你好";
//		Matcher m = POST_HASHTAG_PTRN.matcher(s);
//		while(m.find()) {
//			System.out.println(m.start() + "-" + m.end() + ": " + m.group());
//		}
		
		String s = "#你好 #java database ruby #python #learning### #abc#";
		Object[] objs = processSearchText(s);
		System.out.println("Keyword: " + objs[0]);
		System.out.println("Tags: " + objs[1]);
	}
	
}
