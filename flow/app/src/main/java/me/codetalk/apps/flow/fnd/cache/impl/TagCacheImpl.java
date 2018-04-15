package me.codetalk.apps.flow.fnd.cache.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.codetalk.apps.flow.fnd.cache.TagCache;
import me.codetalk.apps.flow.fnd.entity.Tag;
import me.codetalk.cache.service.CacheService;
import me.codetalk.util.DateUtils;

@Component("tagCache")
public class TagCacheImpl implements TagCache {

	@Autowired
	private CacheService cacheService;
	
	private static final String CACHE_TOP_TAG = "TOP-TAGS-";
	
	private static final Long TTL_TOP_TAG = 10 * 60L; // 10分钟
	
	private static final String CACHE_TAG = "TAG-";
	
	@Override
	public void setTopTags(int count, List<Tag> tags) {
		String cacheKey = CACHE_TOP_TAG + count;
		
		cacheService.set(cacheKey, tags, TTL_TOP_TAG);
	}
	
	@Override
	public List<Tag> getTopTags(int count) {
		String cacheKey = CACHE_TOP_TAG + count;
		
		return (List<Tag>)cacheService.get(cacheKey);
	}

	@Override
	public void incrDayHits(Map<String, Integer> tagHits) {
		String tagCacheKey = CACHE_TAG + DateUtils.formatToDay(System.currentTimeMillis());
		
		tagHits.forEach((tagText, hits) -> {
			cacheService.zIncrBy(tagCacheKey, tagText, hits);
		});
	}

	@Override
	public List<Tag> topHitTagByDay(int count) {
		String tagCacheKey = CACHE_TAG + DateUtils.formatToDay(System.currentTimeMillis());
		Map<String, Double> tagAndHits = cacheService.zRevRangeWithScore(tagCacheKey, 0L, count - 1L);
		
		List<Tag> tagList = new ArrayList<>();
		tagAndHits.forEach((tagText, hits) -> {
			Tag tag = new Tag();
			tag.setText(tagText);
			tag.setHits(hits.longValue());
			
			tagList.add(tag);
		});
		
		return tagList;
	}

}
