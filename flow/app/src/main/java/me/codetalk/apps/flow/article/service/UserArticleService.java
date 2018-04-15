package me.codetalk.apps.flow.article.service;

import java.util.List;
import java.util.Map;

public interface UserArticleService {

	/**
	 * 
	 * @param userId
	 * @param count
	 * @return 
	 * -------- MAP: 
	 * -------- article_id => Long
	 * -------- article_site => String
	 * -------- article_url => String
	 * -------- article_title => String
	 * -------- article_summary => String
	 * -------- article_tags => List<String>
	 * -------- create_date => Long
	 */
	public List<Map<String, Object>> getUserArticles(Long userId, Integer count);
	
	/**
	 * 将文章从zset中移除, 超过指定次数后标记为不感兴趣
	 * 
	 * @param articleIdList
	 */
	public void removeUserArticles(Long userId, List<Long> articleIdList);
	
	
}
