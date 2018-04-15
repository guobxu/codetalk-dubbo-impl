package me.codetalk.webminer.runner;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import me.codetalk.messaging.kafka.IKafkaMesgService;
import me.codetalk.webminer.MinerConstants;
import me.codetalk.webminer.mapper.WebEntityMapper;
import me.codetalk.webminer.pojo.WebEntityVO;

/**
 * Send web entities to kafka broker 
 * 
 * @author guobxu
 *
 */
//@Component
public class KafkaMsgRunner implements CommandLineRunner {

	@Autowired 
	private WebEntityMapper wemapper;
	
	@Autowired
	private IKafkaMesgService messagingService;
	
	@Override
	public void run(String... args) throws Exception {
		for(int i = 0; ; i++) {
			int begin = i * 100, count = 100;
			List<WebEntityVO> entityList = wemapper.selectEntity(begin, count);
			
			if(entityList.isEmpty()) return;
			
			for(WebEntityVO entity : entityList) {
				String topic = getTopic(entity);
				
				messagingService.sendMessage(topic, entity);
			}
		}
	}
	
	private String getTopic(WebEntityVO we) {
		return MinerConstants.KAFKA_TOPIC_PREFIX + we.getSite() + "-" + we.getEntityType();
	}

	
	
}
