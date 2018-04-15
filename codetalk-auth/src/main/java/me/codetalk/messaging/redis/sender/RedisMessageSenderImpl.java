package me.codetalk.messaging.redis.sender;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import me.codetalk.messaging.IMessageSender;
import me.codetalk.messaging.MesgObj;
import me.codetalk.messaging.MessagingUtil;

@Component("mesgSender")
public class RedisMessageSenderImpl implements IMessageSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageSenderImpl.class);
	
	@Resource(name = "mesgRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	@Value("${mesg.sender.core-pool-size}")
	private int corePoolSize;
	
	@Value("${mesg.sender.max-pool-size}")
	private int maxPoolSize;
	
	@Value("${mesg.sender.keep-alive-millis}")
	private int keepAliveMillis;
	
	// thread executor
	private ThreadPoolExecutor executor = null;
	
	@PostConstruct
	public void postConstruct() {
		executor = new ThreadPoolExecutor(corePoolSize,		// core pool size 
										  maxPoolSize, 		// max pool size
										  keepAliveMillis, 			// keepAliveTime
										  TimeUnit.MILLISECONDS, 	// TimeUnit
										  new LinkedBlockingQueue<Runnable>());	// workQueue
	}
	
	@Override
	public void sendMessage(String target, MesgObj msgobj) {
		sendMessage(target, msgobj, true);
	}

	@Override
	public void sendMessage(String target, Map<String, Object> params) {
		sendMessage(target, MessagingUtil.convertAsMesgObj(target, params));
	}
	
	@Override
	public void sendMessage(String target, MesgObj msgobj, boolean isAsync) {
		if( !isAsync ) {
			logBeforeSend(target, msgobj);
			redisTemplate.convertAndSend(target, msgobj);
		} else {
			executor.execute(() -> {
				logBeforeSend(target, msgobj);
				redisTemplate.convertAndSend(target, msgobj);
			});
		}
	}
	
	private void logBeforeSend(String target, MesgObj msgobj) {
		LOGGER.info("Send message: target = [" + target + "], msgobj = [" + msgobj + "]");
	}

}
