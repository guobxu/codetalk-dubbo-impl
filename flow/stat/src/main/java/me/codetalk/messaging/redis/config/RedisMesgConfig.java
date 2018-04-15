package me.codetalk.messaging.redis.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import me.codetalk.apps.flow.stat.StatConstants;
import me.codetalk.messaging.redis.listener.impl.PageViewMesgListener;
import me.codetalk.messaging.redis.listener.impl.UserMesgListener;

@Configuration
public class RedisMesgConfig {

	@Resource(name = "mesgRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private PageViewMesgListener pvMesgListener;
	
//	@Autowired
//	private FileUploadMesgListener fileUploadListener;
	
	@Autowired
	private UserMesgListener userMesgListener;
	
	@Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        
        container.setConnectionFactory( redisTemplate.getConnectionFactory() );
        
        // Page View
        ChannelTopic pvTopic = new ChannelTopic(StatConstants.MESG_PAGE_VIEW);
        container.addMessageListener( pvMesgListener, pvTopic );

        // File Upload
//        ChannelTopic fuTopic = new ChannelTopic(StatConstants.CHN_FILE_UP);
//        container.addMessageListener( fileUploadListener, fuTopic );
        
        // User Create
        ChannelTopic userCreateTopic = new ChannelTopic(StatConstants.MESG_USER_SIGNUP);
        container.addMessageListener( userMesgListener, userCreateTopic );
        
        return container;
    }
	
}
