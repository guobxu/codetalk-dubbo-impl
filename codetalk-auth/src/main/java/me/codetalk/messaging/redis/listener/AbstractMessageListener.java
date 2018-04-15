package me.codetalk.messaging.redis.listener;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import me.codetalk.cache.service.CacheService;
import me.codetalk.messaging.MesgObj;

public abstract class AbstractMessageListener implements MessageListener {

	@Resource(name = "mesgRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	protected CacheService cacheService;
	
    protected MesgObj mesgToObj(Message mesg) {
    	Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
    	
    	return (MesgObj)serializer.deserialize(mesg.getBody());
    }
	
}
