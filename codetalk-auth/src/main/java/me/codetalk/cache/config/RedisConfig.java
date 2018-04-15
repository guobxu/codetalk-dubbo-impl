package me.codetalk.cache.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

	/************************** Redis **************************/
	@Value("${auth.redis.host}")
	private String host;
	
	@Value("${auth.redis.port}")
	private int port;
	
	@Value("${auth.redis.pool.max-total}")
	private int maxTotal;
	@Value("${auth.redis.pool.max-idle}")
	private int maxIdle;
	@Value("${auth.redis.pool.min-idle}")
	private int minIdle;
	
	/************************** Redis - Mesg **************************/
	@Value("${mesg.redis.host}")
	private String mesgHost;
	
	@Value("${mesg.redis.port}")
	private int mesgPort;
	
	@Value("${mesg.redis.pool.max-total}")
	private int mesgMaxTotal;
	@Value("${mesg.redis.pool.max-idle}")
	private int mesgMaxIdle;
	@Value("${mesg.redis.pool.min-idle}")
	private int mesgMinIdle;
	
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        JedisPoolConfig poolConfig=new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);
        
        return jedisConnectionFactory;
    }

    @Bean
    public RedisConnectionFactory mesgRedisConnectionFactory(){
        JedisPoolConfig poolConfig=new JedisPoolConfig();
        poolConfig.setMaxTotal(mesgMaxTotal);
        poolConfig.setMaxIdle(mesgMaxIdle);
        poolConfig.setMinIdle(mesgMinIdle);
        
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);
        jedisConnectionFactory.setHostName(mesgHost);
        jedisConnectionFactory.setPort(mesgPort);
        
        return jedisConnectionFactory;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, String> redisTemplate() throws Exception {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        setSerializer(redisTemplate);
        
        return redisTemplate;
    }

    @Bean(name = "mesgRedisTemplate")
    public RedisTemplate<String, String> mesgRedisTemplate() throws Exception {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(mesgRedisConnectionFactory());
        setSerializer(redisTemplate);
        
        return redisTemplate;
    }

    private void setSerializer(RedisTemplate<String, String> template) {
    	Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
    }

}