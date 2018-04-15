package me.codetalk.apps.flow.article.cache.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.apps.flow.article.cache.UserArticleCache;
import me.codetalk.cache.service.CacheService;
import me.codetalk.stat.StatSupport;

@Component("userArticleCache")
public class UserArticleCacheImpl implements UserArticleCache {

	static final String CACHE_USER_ARTICLE = "USER-ARTICLE-";		// zset

	static final Long TTL_USER_ARTICLE = 24 * 60 * 60L;				// 一天更新一次
	
	static final String CACHE_ARTICLE = "ARTICLE";					// hash
	
	static final String STAT_ARTICLE_RM = "STAT-ARTICLE-RM-";		// 文章从zset中删除次数
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private StatSupport statSupport;
	
	@Override
	public boolean exists(Long userId) {
		String cacheKey = CACHE_USER_ARTICLE + userId;
		
		return cacheService.exists(cacheKey);
	}
	
	@Override
	public Long count(Long userId) {
		String cacheKey = CACHE_USER_ARTICLE + userId;
		
		return cacheService.zLen(cacheKey);
	}
	
	@Override
	public List<Map<String, Object>> getUserArticle(Long userId, Integer count) {
		String cacheKey = CACHE_USER_ARTICLE + userId;
		List<String> articleIdStrs = cacheService.zRevRange(cacheKey, 0, count - 1);
		
		List<Map<String, Object>> articles = new ArrayList<>();
		if(articleIdStrs.isEmpty()) return articles;
		
		List<Object> aobjList = cacheService.hMGet(CACHE_ARTICLE, articleIdStrs);
		for(Object aobj : aobjList) {
			if(aobj != null) {
				articles.add((Map<String, Object>)aobj);
			}
		}
		
		return articles;
	}

	@Override
	public void removeUserArticles(Long userId, List<Long> articleIdList) {
		String cacheKey = CACHE_USER_ARTICLE + userId;
		
		List<String> aidStrList = new ArrayList<>();
		for(Long aid : articleIdList) {
			aidStrList.add(String.valueOf(aid));
		}
		
		cacheService.zRem(cacheKey, aidStrList);
	}

	@Override
	public void setUserArticle(Long userId, List amapList) {
		List<String> articleIdList = new ArrayList<>();
		List<Double> scoreList = new ArrayList<>();
		
		amapList.forEach(obj -> {
			Map<String, Object> amap = (Map<String, Object>)obj;
			String articleId = amap.get("article_id").toString();
			Double articleScore = Double.parseDouble(amap.get("article_score").toString());
			
			articleIdList.add(articleId);
			scoreList.add(articleScore);
		});
		
		// Cache
		// zset
		String zKey = CACHE_USER_ARTICLE + userId;
		cacheService.zAdd(zKey, articleIdList, scoreList);
		cacheService.expire(zKey, TTL_USER_ARTICLE);
		
		// hash
		cacheService.hMSet(CACHE_ARTICLE, articleIdList, amapList);
	}

	@Override
	public List<Long> incrArticleRmStat(Long userId, List<Long> articleIdList) {
		// stat
		List<Long> rmStat = new ArrayList<>();
		
		String statKey = STAT_ARTICLE_RM + userId;
		for(Long articleId : articleIdList) {
			rmStat.add(statSupport.incrStatById(articleId, statKey, 1));
		}
		
		return rmStat;
	}

}
