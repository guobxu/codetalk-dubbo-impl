package me.codetalk.webdb.elastic.repos;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import me.codetalk.webdb.elastic.DocArticle;

public interface ArticleRepository extends ElasticsearchRepository<DocArticle, String> {

}
