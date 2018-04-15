package me.codetalk.messaging.kafka.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import me.codetalk.messaging.MesgObj;
import me.codetalk.messaging.kafka.IKafkaMesgService;
import me.codetalk.util.JsonUtils;

//@Service("messagingService")
public class KafkaMesgServiceImpl implements IKafkaMesgService {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMesgServiceImpl.class);
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Override
	public void sendMessage(String topic, Object obj) {
		sendMessage(topic, -1, obj);
	}

	@Override
	public void sendMessage(String topic, int partition, Object obj) {
		MesgObj mesg = new MesgObj();
		
		String key = UUID.randomUUID().toString();
		mesg.setId(key);
		
		mesg.setData(JsonUtils.toMap(obj));
		
		String msgstr = JsonUtils.toJson(mesg);
		LOGGER.info("========================> Send message: " + msgstr);
		if(partition == -1) {
			kafkaTemplate.send(topic, key, msgstr);
		} else {
			kafkaTemplate.send(topic, partition, key, msgstr);
		}
	}
	
	
	
}
