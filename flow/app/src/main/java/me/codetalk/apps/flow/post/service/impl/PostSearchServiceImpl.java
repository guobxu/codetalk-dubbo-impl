package me.codetalk.apps.flow.post.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import me.codetalk.apps.flow.AppConstants;
import me.codetalk.apps.flow.post.elastic.repos.PostRepository;
import me.codetalk.apps.flow.post.service.PostSearchService;
import me.codetalk.util.CollectionUtils;

@Service("postSearchService")
public class PostSearchServiceImpl implements PostSearchService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PostSearchServiceImpl.class);
	
	private static final float BOOST_USER = 5;
	private static final float BOOST_TAG = 3;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Autowired
	private PostRepository postRepos;
	
	@Override
	public List<Long> searchByTags(List<String> tags, Long beginDate, Long endDate, Integer page, Integer size) {
		List<String> lowered = new ArrayList<>();
		for(String tag : tags) lowered.add(tag.toLowerCase());
		
		// search criteria
		BoolQueryBuilder criteriaQueryBuilder = QueryBuilders.boolQuery()
				.should(QueryBuilders.termsQuery("post_tags", lowered)).boost(BOOST_TAG)				// by tag
				.should(QueryBuilders.matchQuery("post_content", CollectionUtils.concat(tags, " ")));	// by content
		
		if(beginDate != null) {
			criteriaQueryBuilder = criteriaQueryBuilder.must(QueryBuilders.rangeQuery("create_date").gte(beginDate));
		} 
		if(endDate != null) {
			criteriaQueryBuilder = criteriaQueryBuilder.must(QueryBuilders.rangeQuery("create_date").lt(endDate));
		}
		criteriaQueryBuilder.minimumShouldMatch("1"); // reference: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-minimum-should-match.html
		
		// Query Builder
		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
				.withIndices(AppConstants.INDEX_FLOWAPP)
				.withTypes(AppConstants.INDEX_TYPE_POST)
				.withQuery(criteriaQueryBuilder)
				.withSourceFilter(new FetchSourceFilter(new String[]{"post_id"}, null))
				.withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
				.withSort(SortBuilders.fieldSort("create_date").order(SortOrder.DESC));
		
		if(page != null && size != null) {
			queryBuilder = queryBuilder.withPageable(new PageRequest(page, size));
		}
		
		// Search Query 
		SearchQuery searchQuery = queryBuilder.build();
		
		return queryForPostIdList(searchQuery);
	}

	@Override
	public List<Long> searchByKeyword(String q, Long beginDate, Long endDate, Integer page, Integer size) {
		// search criteria
		BoolQueryBuilder criteriaQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("post_content", q));	// by content only
		
		if(beginDate != null) {
			criteriaQueryBuilder = criteriaQueryBuilder.must(QueryBuilders.rangeQuery("create_date").gte(beginDate));
		} 
		if(endDate != null) {
			criteriaQueryBuilder = criteriaQueryBuilder.must(QueryBuilders.rangeQuery("create_date").lt(endDate));
		}
		
		// Query Builder
		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
				.withIndices(AppConstants.INDEX_FLOWAPP)
				.withTypes(AppConstants.INDEX_TYPE_POST)
				.withQuery(criteriaQueryBuilder)	
				.withSourceFilter(new FetchSourceFilter(new String[]{"post_id"}, null))
				.withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
				.withSort(SortBuilders.fieldSort("create_date").order(SortOrder.DESC));
		
		if(page != null && size != null) {
			queryBuilder = queryBuilder.withPageable(new PageRequest(page, size));
		}
		
		// Search Query 
		SearchQuery searchQuery = queryBuilder.build();
		
		return queryForPostIdList(searchQuery);
	}

	@Override
	public List<Long> searchByKeywordAndTags(String q, List<String> tags, Long beginDate, Long endDate, Integer page, Integer size) {
		List<String> lowered = new ArrayList<>();
		for(String tag : tags) lowered.add(tag.toLowerCase());
		
		// search criteria
		BoolQueryBuilder criteriaQueryBuilder = QueryBuilders.boolQuery()
				.should(QueryBuilders.termsQuery("post_tags", lowered)).boost(BOOST_TAG)				// by tag
				.should(QueryBuilders.matchQuery("post_content", q))									// by content
				.should(QueryBuilders.matchQuery("post_content", CollectionUtils.concat(tags, " ")));	// by content
		
		if(beginDate != null) {
			criteriaQueryBuilder = criteriaQueryBuilder.must(QueryBuilders.rangeQuery("create_date").gte(beginDate));
		} 
		if(endDate != null) {
			criteriaQueryBuilder = criteriaQueryBuilder.must(QueryBuilders.rangeQuery("create_date").lt(endDate));
		}
		criteriaQueryBuilder.minimumShouldMatch("1");
		
		// Query Builder
		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
				.withIndices(AppConstants.INDEX_FLOWAPP)
				.withTypes(AppConstants.INDEX_TYPE_POST)
				.withQuery(criteriaQueryBuilder)
				.withSourceFilter(new FetchSourceFilter(new String[]{"post_id"}, null))
				.withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
				.withSort(SortBuilders.fieldSort("create_date").order(SortOrder.DESC));
		
		if(page != null && size != null) {
			queryBuilder = queryBuilder.withPageable(new PageRequest(page, size));
		}
		
		// Search Query 
		SearchQuery searchQuery = queryBuilder.build();
		
		return queryForPostIdList(searchQuery);
	}
	
	@Override
	public List<Long> searchByUserAndTag(List<String> users, List<String> tags, Long beginDate, Long endDate, Integer page, Integer size) {
		List<String> lowered = new ArrayList<>();
		for(String tag : tags) lowered.add(tag.toLowerCase());
		
		// search criteria
		BoolQueryBuilder criteriaQueryBuilder = QueryBuilders.boolQuery()
				.should(QueryBuilders.termsQuery("create_by", users)).boost(BOOST_USER)						// by user
				.should(QueryBuilders.termsQuery("post_tags", lowered)).boost(BOOST_TAG)					// by tag
				.should(QueryBuilders.matchQuery("post_content", CollectionUtils.concat(tags, " ")));		// by content
		
		if(beginDate != null) {
			criteriaQueryBuilder = criteriaQueryBuilder.must(QueryBuilders.rangeQuery("create_date").gte(beginDate));
		} 
		if(endDate != null) {
			criteriaQueryBuilder = criteriaQueryBuilder.must(QueryBuilders.rangeQuery("create_date").lt(endDate));
		}
		criteriaQueryBuilder.minimumShouldMatch("1");
		
		// Query Builder
		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
				.withIndices(AppConstants.INDEX_FLOWAPP)
				.withTypes(AppConstants.INDEX_TYPE_POST)
				.withQuery(criteriaQueryBuilder)
				.withSourceFilter(new FetchSourceFilter(new String[]{"post_id"}, null))
				.withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
				.withSort(SortBuilders.fieldSort("create_date").order(SortOrder.DESC));
		
		if(page != null && size != null) {
			queryBuilder = queryBuilder.withPageable(new PageRequest(page, size));
		}
		
		// Search Query
		SearchQuery searchQuery = queryBuilder.build();
		
		return queryForPostIdList(searchQuery);
	}
	
	private List<Long> queryForPostIdList(SearchQuery searchQuery) {
		List<Long> postIdList = new ArrayList<>();
		
		List<Map> recs = esTemplate.queryForList(searchQuery, Map.class);
		for(Map rec : recs) {
			postIdList.add(Long.parseLong(rec.get("post_id").toString()));
		}
		
		return postIdList;
	}

	
}
