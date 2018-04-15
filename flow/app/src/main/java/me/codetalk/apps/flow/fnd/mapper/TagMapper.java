package me.codetalk.apps.flow.fnd.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import me.codetalk.apps.flow.fnd.entity.Tag;

public interface TagMapper {
	
	public void insertTag(Tag tag);
	
	public void updateTagHits(@Param("text") String text, @Param("hits") Long hits);
	
	public Tag selectTag(@Param("text") String text);
	
	public List<Tag> selectTopTagLike(@Param("q") String q, @Param("count") int count);
	
	public List<Tag> selectTopTag(@Param("count") int count);
	
}
