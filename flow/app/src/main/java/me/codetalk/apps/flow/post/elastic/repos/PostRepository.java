package me.codetalk.apps.flow.post.elastic.repos;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import me.codetalk.apps.flow.post.elastic.DocPost;

public interface PostRepository extends ElasticsearchRepository<DocPost, String> {

}
