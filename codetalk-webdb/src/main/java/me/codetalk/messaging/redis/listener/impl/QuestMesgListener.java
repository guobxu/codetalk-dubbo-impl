package me.codetalk.messaging.redis.listener.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import me.codetalk.cache.service.ICacheService;
import me.codetalk.messaging.MesgObj;
import me.codetalk.messaging.redis.listener.AbstractMessageListener;
import me.codetalk.util.StringUtils;
import me.codetalk.webdb.Constants;
import me.codetalk.webdb.entity.Quest;
import me.codetalk.webdb.service.QuestService;

@Component("questMesgListener")
public class QuestMesgListener extends AbstractMessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(QuestMesgListener.class);
	
	@Autowired
	private ICacheService cacheService;
	
	@Autowired
	private QuestService questService;
	
	public void onMessage(Message message, byte[] pattern) {
		MesgObj msgobj = mesgToObj(message);
		
		String cacheKey = this.getClass().getName() + "-" + msgobj.getId();
		if(!cacheService.setNX(cacheKey, "X")) return;
		cacheService.expire(cacheKey, LOCK_EXPIRE_SECONDS);
		
		String type = msgobj.getType();
		if(Constants.CHN_QUEST_SO.equals(type)) {
			soQuestMigrate(msgobj);
		}
	}
	
	public void soQuestMigrate(MesgObj msgobj) {
		LOGGER.info("In soQuestMigrate...Receive mesg data = " + msgobj);
		
		Map<String, Object> data = (Map<String, Object>)msgobj.getData();
		
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
		quest.setUuid(msgobj.getId());
		quest.setSite((String)data.get("site"));
		quest.setUrl((String)data.get("pageUrl"));
		quest.setTitle(attrsMap.get("quest_title"));
		quest.setContent(attrsMap.get("quest_content"));
		
		String questAns = attrsMap.get("quest_answer");
		quest.setAnswer(questAns == null ? attrsMap.get("quest_top_reply") : questAns);
		
		quest.setAnswerAccept("accepted".equals(attrsMap.get("quest_accepted")) ? 1 : 0);
		
		quest.setTags(attrsMap.get("quest_tags"));
		
		quest.setVotes(Integer.parseInt(attrsMap.get("quest_votes")));
		
		questService.addQuest(quest);
	}

}
