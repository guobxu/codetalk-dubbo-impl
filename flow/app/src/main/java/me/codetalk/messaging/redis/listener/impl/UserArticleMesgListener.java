package me.codetalk.messaging.redis.listener.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.apps.flow.AppConstants;
import me.codetalk.apps.flow.article.cache.UserArticleCache;
import me.codetalk.apps.flow.article.entity.UserArticle;
import me.codetalk.apps.flow.article.mapper.UserArticleMapper;
import me.codetalk.messaging.MesgObj;
import me.codetalk.messaging.redis.listener.AbstractTagMesgListener;

@Component("userArticleMesgListener")
public class UserArticleMesgListener extends AbstractTagMesgListener {

	static final int ARTICLE_RM_THRESHOLD = 3;		// 超过此阈值标记为不感兴趣
	
	@Autowired
	private UserArticleCache userArticleCache;
	
	@Autowired
	private UserArticleMapper uaMapper;;
	
	@Override
	@Transactional
	public void onMessage(Message message, byte[] pattern) {
		MesgObj msgobj = mesgToObj(message);
		
		String cacheKey = this.getClass().getName() + "-" + msgobj.getId();
		if(!lockService.lock(cacheKey)) return;
		
		String type = msgobj.getType();
		if(AppConstants.MESG_ARTICLE_RM.equals(type)) {
			handleUserArticleRemove(msgobj);
		}	
	}

	/**
	 * 
	 * @param msgobj
	 */
	private void handleUserArticleRemove(MesgObj msgobj) {
		Map<String, Object> mapData = (Map<String, Object>)msgobj.getData();
		Long userId = (Long)mapData.get("user_id");
		List<Long> articleIdList = (List<Long>)mapData.get("article_list");
		
		// stat
		List<Long> rmStat = userArticleCache.incrArticleRmStat(userId, articleIdList);
		for(int i = 0; i < articleIdList.size(); i++) {
			if(rmStat.get(i) < ARTICLE_RM_THRESHOLD) continue;
			
			Long articleId = articleIdList.get(i);
			UserArticle ua = uaMapper.selectUserArticle(userId, articleId);
			if(ua == null) {
				ua = new UserArticle();
				ua.setUserId(userId);
				ua.setArticleId(articleId);
				ua.setArticleStatus(AppConstants.ARTICLE_NOT_FAVOR);
				
				Long currentTime = System.currentTimeMillis();
				ua.setCreateDate(currentTime);
				ua.setLastUpdate(currentTime);
				
				uaMapper.insertUserArticle(ua);
			} else {
				// 如果status = 正在阅读, 是否需要处理
			}
		}
	}
	
}
