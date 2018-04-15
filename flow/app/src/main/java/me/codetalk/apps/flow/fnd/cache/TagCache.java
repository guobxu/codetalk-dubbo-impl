package me.codetalk.apps.flow.fnd.cache;

import java.util.List;
import java.util.Map;

import me.codetalk.apps.flow.fnd.entity.Tag;

public interface TagCache {

	public void setTopTags(int count, List<Tag> tagList);
	
	public List<Tag> getTopTags(int count);
	
	// 当前时间
	public void incrDayHits(Map<String, Integer> tagHits);
	
	// 当前时间
	public List<Tag> topHitTagByDay(int count);
	
}
