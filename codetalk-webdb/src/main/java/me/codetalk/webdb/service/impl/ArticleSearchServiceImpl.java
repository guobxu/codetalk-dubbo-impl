package me.codetalk.webdb.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import me.codetalk.util.CollectionUtils;
import me.codetalk.util.JsonUtils;
import me.codetalk.webdb.Constants;
import me.codetalk.webdb.elastic.DocArticle;
import me.codetalk.webdb.service.ArticleSearchService;

@Service("articleSearchService")
public class ArticleSearchServiceImpl implements ArticleSearchService {

	private static final float BOOST_TITLE = 3;
	private static final float BOOST_TAG = 5;
	
	static final String TAG_BETTER_RANK = "givebetterrank"; 
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	@Override
	public Map<DocArticle, Float> searchArticleByKeyword(String q, List<Long> excludeArticles, Integer page, Integer size) {
		BoolQueryBuilder criteriaQueryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.boolQuery()
						.should(QueryBuilders.matchQuery("article_title", q)).boost(BOOST_TITLE)			//  by title
						.should(QueryBuilders.matchQuery("article_content", q)))							// by content
				.should(QueryBuilders.termQuery("article_tags", TAG_BETTER_RANK)); 							
					
		if(!CollectionUtils.isEmpty(excludeArticles)) {
			criteriaQueryBuilder = criteriaQueryBuilder.mustNot(QueryBuilders.termsQuery("article_id", excludeArticles));
		}
		
		// Query Builder
		NativeSearchQueryBuilder queryBuilder = createQueryBuilder(criteriaQueryBuilder, page, size);
		
		// Search Query 
		SearchQuery searchQuery = queryBuilder.build();
		
		return queryForListWithScore(searchQuery);
	}

	@Override
	public Map<DocArticle, Float> searchArticleByTags(List<String> tags, List<Long> excludeArticles, Integer page, Integer size) {
		List<String> lowered = new ArrayList<>();
		for(String tag : tags) lowered.add(tag.toLowerCase());
		
		// search criteria
		String tagstr = CollectionUtils.concat(tags, " ");
		BoolQueryBuilder criteriaQueryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.boolQuery()
						.should(QueryBuilders.termsQuery("article_tags", lowered)).boost(BOOST_TAG)				// by tag
						.should(QueryBuilders.matchQuery("article_title", tagstr)).boost(BOOST_TITLE)			//  by title
						.should(QueryBuilders.matchQuery("article_content", tagstr)))							// by content
				.should(QueryBuilders.termQuery("article_tags", TAG_BETTER_RANK)); 							
					
		if(!CollectionUtils.isEmpty(excludeArticles)) {
			criteriaQueryBuilder = criteriaQueryBuilder.mustNot(QueryBuilders.termsQuery("article_id", excludeArticles));
		}
		
		// Query Builder
		NativeSearchQueryBuilder queryBuilder = createQueryBuilder(criteriaQueryBuilder, page, size);
		
		// Search Query
		SearchQuery searchQuery = queryBuilder.build();
		
		return queryForListWithScore(searchQuery);
	}
	
	// Reference: 
	// https://stackoverflow.com/questions/44496243/score-of-each-hit-with-spring-searchquery-elasticsearch
	private Map<DocArticle, Float> queryForListWithScore(SearchQuery searchQuery) {
		
		return esTemplate.query(searchQuery, new ResultsExtractor<Map<DocArticle, Float>>() {

			@Override
			public Map<DocArticle, Float> extract(SearchResponse response) {
				Map<DocArticle, Float> articleListWithScore = new HashMap<>();
				
		        SearchHit[] hits = response.getHits().getHits();
		        for (SearchHit hit : hits) {
		        	DocArticle article = (DocArticle)JsonUtils.fromJsonObj(hit.sourceAsString(), DocArticle.class);
		        	
		        	articleListWithScore.put(article, hit.getScore());
		        }
		        
		        return articleListWithScore;
			}
			
		});
		
	}
	
	private NativeSearchQueryBuilder createQueryBuilder(QueryBuilder criteriaQueryBuilder, Integer page, Integer size) {
		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
				.withIndices(Constants.INDEX_WEBDB)
				.withTypes(Constants.INDEX_TYPE_ARTICLE)
				.withQuery(criteriaQueryBuilder)
				.withSourceFilter(new FetchSourceFilter(new String[]{
						"article_id", "article_uuid", "article_site", "article_url", "article_title", 
						"article_tags", "create_date"
				}, null))
				.withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
				.withSort(SortBuilders.fieldSort("create_date").order(SortOrder.DESC));
		
		if(page != null && size != null) {
			queryBuilder = queryBuilder.withPageable(new PageRequest(page, size));
		}
		
		return queryBuilder;
	}

}
