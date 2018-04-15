package me.codetalk.messaging.redis.config;

import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import me.codetalk.messaging.redis.listener.impl.ArticleMesgListener;
import me.codetalk.messaging.redis.listener.impl.QuestMesgListener;
import me.codetalk.webdb.Constants;

@Configuration
public class RedisMesgConfig {

	@Resource(name = "mesgRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private QuestMesgListener questMesgListener;
	
	@Autowired
	private ArticleMesgListener articleMesgListener;
	
	@Bean
    RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        
        container.setConnectionFactory( redisTemplate.getConnectionFactory() );
        
        // Quest 
        ChannelTopic soQuestTopic = new ChannelTopic(Constants.CHN_QUEST_SO);
        container.addMessageListener( questMesgListener, Arrays.asList(new ChannelTopic[] {
        		soQuestTopic
        }));

        // Article
        container.addMessageListener( articleMesgListener, Arrays.asList(new ChannelTopic[] {
        		new ChannelTopic(Constants.CHN_ARTICLE_DZONE),
        		new ChannelTopic(Constants.CHN_ARTICLE_JCG),
        		new ChannelTopic(Constants.CHN_ARTICLE_BAELDUNG),
        		new ChannelTopic(Constants.CHN_ARTICLE_MKYONG),
        		new ChannelTopic(Constants.CHN_ARTICLE_INFOQ),
        		new ChannelTopic(Constants.CHN_ARTICLE_TNS),
        		new ChannelTopic(Constants.CHN_ARTICLE_JVNS),
        		new ChannelTopic(Constants.CHN_ARTICLE_MARTIN),
        		new ChannelTopic(Constants.CHN_ARTICLE_TUTSPLUS),
        }));
        
        return container;
    }
	
}
