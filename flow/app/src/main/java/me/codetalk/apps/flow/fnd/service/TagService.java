package me.codetalk.apps.flow.fnd.service;

import java.util.List;

import me.codetalk.apps.flow.fnd.entity.Tag;

public interface TagService {

	public List<Tag> findTopTags(int count);
	
	public List<Tag> findTopTagLike(String q, int count);
	
	public List<Tag> topHitTagByDay(int count);
	
}
