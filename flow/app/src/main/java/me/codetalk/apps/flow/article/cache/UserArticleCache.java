package me.codetalk.apps.flow.article.cache;

import java.util.List;
import java.util.Map;

public interface UserArticleCache {

	public boolean exists(Long userId);
	
	public Long count(Long userId);
	
	public List<Map<String, Object>> getUserArticle(Long userId, Integer count);
	
	public void setUserArticle(Long userId, List<Map<String, Object>> amapList);
	
	public void removeUserArticles(Long userId, List<Long> articleIdList);
	
	public List<Long> incrArticleRmStat(Long userId, List<Long> articleIdList);
	
}
