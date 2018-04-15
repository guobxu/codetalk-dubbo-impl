package me.codetalk.webdb.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.codetalk.util.JsonUtils;
import me.codetalk.webdb.elastic.DocArticle;
import me.codetalk.webdb.entity.Article;
import me.codetalk.webdb.service.ArticleSearchService;
import me.codetalk.webdb.service.ArticleService;
import me.codetalk.webdb.service.WebdbService;

@Service("webdbService")
public class WebdbServiceImpl implements WebdbService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebdbServiceImpl.class);
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private ArticleSearchService articleSearchService;
	
	@Override
	public String searchArticleByKeyword(String q, List<Long> excludeArticles, Integer page, Integer size) {
		Map<DocArticle, Float> articleListWithScore = articleSearchService.searchArticleByKeyword(q, excludeArticles, page, size);
		
		List<Map<String, Object>> articleMapList = convertAsMap(articleListWithScore);
		
		return JsonUtils.toJson(articleMapList);
	}

	@Override
	public String searchArticleByTags(List<String> tags, List<Long> excludeArticles, Integer page, Integer size) {
		Map<DocArticle, Float> articleListWithScore = articleSearchService.searchArticleByTags(tags, excludeArticles, page, size);
		
		List<Map<String, Object>> articleMapList = convertAsMap(articleListWithScore);
		
		return JsonUtils.toJson(articleMapList);
	}
	
	private List<Map<String, Object>> convertAsMap(Map<DocArticle, Float> articleListWithScore) {
		List<Map<String, Object>> articleMapList = new ArrayList<>();
		articleListWithScore.forEach((article, score) -> {
			Map<String, Object> amap = JsonUtils.toMap(article);
			amap.put("article_score", score);
			
			articleMapList.add(amap);
		});
		
		return articleMapList;
	}

	@Override
	public String findLatestArticles(Integer count) {
		List<Article> articleList = articleService.findLatest(count);
		List<Map<String, Object>> articleMapList = new ArrayList<>();
		for(Article article : articleList) {
			Map<String, Object> amap = JsonUtils.toMap(article);
			amap.put("article_score", 0.0f);
			
			articleMapList.add(amap);
		}
		
		return JsonUtils.toJson(articleMapList);
	}

}
