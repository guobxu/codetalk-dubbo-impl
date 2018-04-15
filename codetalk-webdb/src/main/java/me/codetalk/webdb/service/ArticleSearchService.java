package me.codetalk.webdb.service;

import java.util.List;
import java.util.Map;

import me.codetalk.webdb.elastic.DocArticle;

public interface ArticleSearchService {

	/**
	 * 
	 * @param q
	 * @param page
	 * @param size
	 * @return NO order
	 */
	public Map<DocArticle, Float> searchArticleByKeyword(String q, List<Long> excludeArticles, Integer page, Integer size);
	
	public Map<DocArticle, Float> searchArticleByTags(List<String> tags, List<Long> excludeArticles, Integer page, Integer size);
	
}
