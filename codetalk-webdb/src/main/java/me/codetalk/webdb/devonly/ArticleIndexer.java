package me.codetalk.webdb.devonly;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import me.codetalk.util.StringUtils;
import me.codetalk.webdb.elastic.DocArticle;
import me.codetalk.webdb.elastic.repos.ArticleRepository;
import me.codetalk.webdb.entity.Article;
import me.codetalk.webdb.mapper.ArticleMapper;

//@Component
public class ArticleIndexer implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleIndexer.class);
	
	@Autowired
	private ArticleMapper articleMapper;
	
	@Autowired
	private ArticleRepository articleRepo;
	
	@Override
	public void run(String... args) throws Exception {
		int count = 1000;
		for(int i = 0; ;i++) {
			List<Article> articleList = articleMapper.selectIndex0(i * count, count);
			if(articleList.size() == 0) return;
			
			for(Article article: articleList) {
				articleRepo.save(article2Doc(article));
				
				articleMapper.updateArticleIndexed(article.getId());
			}
			
			LOGGER.info("Rows processed = " + i * count);
		}
	}
	
	private DocArticle article2Doc(Article article) {
		DocArticle da = new DocArticle();
		da.setArticleId(article.getId());
		da.setUuid(article.getUuid());
		da.setTitle(article.getTitle());
		da.setSite(article.getSite());
		da.setUrl(article.getUrl());
		da.setSummary(article.getSummary());
		da.setContent(article.getContent());

		// tags
		Set<String> tagSet = new HashSet<>();
		String tags = article.getTags();
		if(!StringUtils.isBlank(tags)) {
			List<String> tagList = StringUtils.splitNoRegex(tags, " ");
			for(String tag : tagList) {
				tagSet.add(tag.toLowerCase());
			}
		}
		da.setTags(tagSet);
		
		da.setCreateDate(article.getCreateDate());
		
		return da;
	}
	
}
