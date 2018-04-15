package me.codetalk.apps.flow.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import me.codetalk.Constants;

@SpringBootApplication
@ComponentScan(basePackages = {
        "me.codetalk.mesg",
        "me.codetalk.cache.config",
        "me.codetalk.cache.service",
        "me.codetalk.lock.service",
        "me.codetalk.messaging.redis.config",
        "me.codetalk.messaging.redis.sender",
        "me.codetalk.messaging.redis.listener",
        "me.codetalk.cipher",
        "me.codetalk.auth.aop",
        "me.codetalk.auth.cache",
        "me.codetalk.auth.service",
        "me.codetalk.stat.redis",
        "me.codetalk.apps.flow.service",
        "me.codetalk.apps.flow.*.cache",
        "me.codetalk.apps.flow.*.service",
        "me.codetalk.apps.flow.devonly",
})
@MapperScan(value = {"me.codetalk.apps.flow.*.mapper"})
@ImportResource(locations = {
		"classpath:dubbo-config.xml",
})
@EnableElasticsearchRepositories(basePackages = {
		"me.codetalk.apps.flow.*.elastic.repos",
})
@EnableScheduling
public class AppMain {

	public static void main(String[] args) throws Exception {
        SpringApplication.run(AppMain.class, args);
        
        System.in.read();
    }
    
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:locale/messages");
//        messageSource.setCacheSeconds(3600); //refresh cache once per hour
        messageSource.setDefaultEncoding(Constants.ENCODING_UTF8);
        
        return messageSource;
    }
	
}
