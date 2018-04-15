package me.codetalk.webdb.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import me.codetalk.webdb.entity.Article;

public interface ArticleMapper {

	public void insertArticle(Article article);
	
	public void updateArticleIndexed(Long articleId);
	
	public Integer selectOneByUuid(@Param("uuid") String uuid);
	
	public Integer selectOneByUrl(@Param("url") String url);
	
	public List<Article> selectLatest(@Param("begin") Integer begin, @Param("count") Integer count);
	
	// Dev Only
	public List<Article> selectIndex0(@Param("begin") Integer begin, @Param("count") Integer count);
	
	
}
