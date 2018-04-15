package me.codetalk.webdb.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.codetalk.messaging.MesgObj;
import me.codetalk.util.JsonUtils;
import me.codetalk.util.StringUtils;
import me.codetalk.webdb.elastic.DocQuest;
import me.codetalk.webdb.elastic.repos.QuestRepository;
import me.codetalk.webdb.entity.Quest;
import me.codetalk.webdb.mapper.QuestMapper;
import me.codetalk.webdb.service.QuestService;

@Service("questService")
public class QuestServiceImpl implements QuestService {

	private static final Logger LOGGER = LoggerFactory.getLogger(QuestServiceImpl.class); 
	
	@Autowired
	private QuestMapper eqmapper;
	
	@Autowired
	private QuestRepository questRepo;
	
	@Override
	@Transactional
	public void addQuest(Quest quest) {
		LOGGER.info("Add quest, uuid=" + quest.getUuid());
		
//		String quid = quest.getUuid();
		String url = quest.getUrl();
		if(eqmapper.selectOneByUrl(url) != null) {
			LOGGER.info("Quest with [url = " + url + "] already exists!");
			
			return;
		}
		
		quest.setIndexed(1);
		quest.setCreateDate(System.currentTimeMillis());
		eqmapper.insertQuest(quest);

		questRepo.save(quest2Doc(quest));
	}

	// KAFKA
	
//	@KafkaListener(topics = "webminer-stackoverflow-quest", groupId = "webdb-quest-stackoverflow-quest-migrate")
	public void soQuestMigrate(String msgstr) {
		LOGGER.info("In soQuestMigrate...Receive mesg data = " + msgstr);
		
		MesgObj mesg = (MesgObj)JsonUtils.fromJsonObj(msgstr, MesgObj.class);
		Map<String, Object> data = (Map<String, Object>)mesg.getData();
		
		// attr list -> map
		List attrs = (List)data.get("attrs");
		Map<String, String> attrsMap = new HashMap<>();
		for(Object attrObj : attrs) {
			Map<String, Object> attrMap = (Map<String, Object>)attrObj;
			
			String key = (String)attrMap.get("key"), val = (String)attrMap.get("val");
			// ignore quest without answer
			if("quest_top_reply".equals(key) && StringUtils.isNull(val)) return;
			
			// ignore quest with vote < 0
			if("quest_votes".equals(key) && Integer.parseInt(val) < 0) return;
			
			attrsMap.put(key, val);
		}

		Quest quest = new Quest();
		quest.setUuid(mesg.getId());
		quest.setSite((String)data.get("site"));
		quest.setUrl((String)data.get("pageUrl"));
		quest.setTitle(attrsMap.get("quest_title"));
		quest.setContent(attrsMap.get("quest_content"));
		
		String questAns = attrsMap.get("quest_answer");
		quest.setAnswer(questAns == null ? attrsMap.get("quest_top_reply") : questAns);
		
		quest.setAnswerAccept("accepted".equals(attrsMap.get("quest_accepted")) ? 1 : 0);
		
		quest.setTags(attrsMap.get("quest_tags"));
		
		quest.setVotes(Integer.parseInt(attrsMap.get("quest_votes")));
		
		addQuest(quest);
	}
	
	/**
	 * 转换为Doc Quest作为索引
	 * 
	 * @param eq
	 * @return
	 */
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





























