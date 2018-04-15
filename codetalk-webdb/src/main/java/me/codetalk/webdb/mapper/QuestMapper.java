package me.codetalk.webdb.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import me.codetalk.webdb.entity.Quest;

public interface QuestMapper {

	public void insertQuest(Quest quest);
	
	public void updateQuestIndexed(Long questId);
	
	public Integer selectOneByUuid(@Param("uuid") String uuid);
	
	public Integer selectOneByUrl(@Param("url") String url);
	
	public List<Quest> selectIndex0(@Param("begin") Integer begin, @Param("count") Integer count);
	
}
