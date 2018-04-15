package me.codetalk.webdb.service;

import java.util.List;

public interface WebdbService {

	/**
	 * Search by Keyword
	 * @param q
	 * @param page
	 * @param size
	 * @return 
	 * -------- JSON: 
	 * -------- article_id => Long
	 * -------- article_site => String
	 * -------- article_url => String
	 * -------- article_title => String
	 * -------- article_summary => String
	 * -------- article_tags => List<String>
	 * -------- create_date => Long
	 */
	public String searchArticleByKeyword(String q, List<Long> excludeArticles, Integer page, Integer size);
	
	public String searchArticleByTags(List<String> tags, List<Long> excludeArticles, Integer page, Integer size);
	
	public String findLatestArticles(Integer count);
	
}
