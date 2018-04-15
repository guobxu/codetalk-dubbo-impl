package me.codetalk.apps.xiaoma.post.elastic.repos;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import me.codetalk.apps.xiaoma.post.elastic.DocPost;

public interface PostRepository extends ElasticsearchRepository<DocPost, String> {

}
