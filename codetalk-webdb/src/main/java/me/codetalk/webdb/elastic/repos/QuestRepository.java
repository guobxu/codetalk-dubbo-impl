package me.codetalk.webdb.elastic.repos;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import me.codetalk.webdb.elastic.DocQuest;

public interface QuestRepository extends ElasticsearchRepository<DocQuest, String> {

}
