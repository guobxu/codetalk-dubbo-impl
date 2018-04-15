package me.codetalk.webdb.service;

import java.util.List;

import me.codetalk.webdb.entity.Article;

public interface ArticleService {

	public void createArticle(Article article);
	
	public void createArticle(Article article, boolean doIndex);
	
	public List<Article> findLatest(Integer count);
	
}
