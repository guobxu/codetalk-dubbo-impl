package me.codetalk.messaging.redis.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisMesgConfig {

	@Resource(name = "mesgRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
//	@Autowired
//	private CommonMesgListener commMesgListener;
	
	@Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        
        container.setConnectionFactory( redisTemplate.getConnectionFactory() );
        
//        ChannelTopic userCreateTopic = new ChannelTopic(Constants.CHN_USER_CREATE),
//        		orderCreateTopic = new ChannelTopic(Constants.CHN_ORDER_CREATE);
//
//        container.addMessageListener( commMesgListener, Arrays.asList(userCreateTopic, orderCreateTopic) );
        
        return container;
    }
	
}
