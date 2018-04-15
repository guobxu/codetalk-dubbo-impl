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
//	private PostMesgListener postMesgListener;
	
//	@Autowired
//	private CommentMesgListener cmntMesgListener;
	
//	@Autowired
//	private UserArticleMesgListener uaMesgListener;
	
	@Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        
        container.setConnectionFactory( redisTemplate.getConnectionFactory() );
        
        // POST
//        ChannelTopic postCreateTopic = new ChannelTopic(AppConstants.MESG_POST_CREATE),
//        		cmntCreateTopic = new ChannelTopic(AppConstants.MESG_COMMENT_CREATE);
        
        // ARTICLE
//        ChannelTopic uaRemoveTopic = new ChannelTopic(AppConstants.MESG_ARTICLE_RM);

//        container.addMessageListener( postMesgListener, Arrays.asList( postCreateTopic ) );
//        container.addMessageListener( cmntMesgListener, Arrays.asList( cmntCreateTopic ) );
        
//        container.addMessageListener( uaMesgListener, Arrays.asList( uaRemoveTopic ) );
        
        return container;
    }
	
}
