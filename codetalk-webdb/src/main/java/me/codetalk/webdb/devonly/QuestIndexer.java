package me.codetalk.webdb.devonly;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import me.codetalk.util.StringUtils;
import me.codetalk.webdb.elastic.DocQuest;
import me.codetalk.webdb.elastic.repos.QuestRepository;
import me.codetalk.webdb.entity.Quest;
import me.codetalk.webdb.mapper.QuestMapper;

//@Component
public class QuestIndexer implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(QuestIndexer.class);
	
	@Autowired
	private QuestMapper questMapper;
	
	@Autowired
	private QuestRepository questRepo;
	
	@Override
	public void run(String... args) throws Exception {
		int count = 1000;
		for(int i = 0; ;i++) {
			List<Quest> questList = questMapper.selectIndex0(i * count, count);
			if(questList.size() == 0) return;
			
			for(Quest quest : questList) {
				questRepo.save(quest2Doc(quest));
				
				questMapper.updateQuestIndexed(quest.getId());
			}
			
			LOGGER.info("Rows processed = " + i * count);
		}
		
	}
	
	private DocQuest quest2Doc(Quest eq) {
		DocQuest dq = new DocQuest();
		dq.setQuestId(eq.getId());
		dq.setUuid(eq.getUuid());
		dq.setTitle(eq.getTitle());
		dq.setContent(eq.getContent());
		
		Set<String> tagSet = new HashSet<>();
		String tags = eq.getTags();
		if(!StringUtils.isBlank(tags)) {
			List<String> tagList = StringUtils.splitNoRegex(tags, " ");
			for(String tag : tagList) {
				tagSet.add(tag.toLowerCase());
			}
		}
		dq.setTags(tagSet);
		dq.setVotes(eq.getVotes());
		dq.setSite(eq.getSite());
		dq.setUrl(eq.getUrl());
		dq.setAccepted(eq.getAnswerAccept());
		dq.setCreateDate(eq.getCreateDate());
		
		return dq;
	}

}
