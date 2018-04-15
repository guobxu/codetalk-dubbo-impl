package me.codetalk.messaging.redis.listener;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import me.codetalk.cache.service.CacheService;
import me.codetalk.messaging.MesgObj;

public abstract class AbstractMessageListener implements MessageListener {

	protected static ThreadLocal<SimpleDateFormat> DATE_FORMATTER = new ThreadLocal<>();
	
	protected static long LOCK_EXPIRE_SECONDS = 60 * 60L;
	
	@Resource(name = "mesgRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	protected CacheService cacheService;
	
    protected MesgObj mesgToObj(Message mesg) {
    	Jackson2JsonRedisSerializer serializer = (Jackson2JsonRedisSerializer)redisTemplate.getValueSerializer();
    	
    	return (MesgObj)serializer.deserialize(mesg.getBody());
    }
    
    protected String getDate() {
    	SimpleDateFormat dateFormatter = getDateFormatter();
    	
    	return dateFormatter.format(new Date());
    }

    private SimpleDateFormat getDateFormatter() {
    	SimpleDateFormat dateFormatter = DATE_FORMATTER.get();
		if (dateFormatter == null) {
			dateFormatter = new SimpleDateFormat("yyyyMMdd");
			DATE_FORMATTER.set(dateFormatter);
		}

		return dateFormatter;
    }
    
}
