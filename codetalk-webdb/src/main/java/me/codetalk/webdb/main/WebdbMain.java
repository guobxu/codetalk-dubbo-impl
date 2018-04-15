package me.codetalk.webdb.main;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {
		"me.codetalk.cache.config",
		"me.codetalk.cache.service",
        "me.codetalk.webdb.service",
        "me.codetalk.messaging.redis.config",
        "me.codetalk.messaging.redis.listener",
        "me.codetalk.webdb.devonly",
})
@MapperScan(value = {"me.codetalk.webdb.mapper"})
@EnableElasticsearchRepositories(basePackages = {
		"me.codetalk.webdb.elastic.repos",
})
@ImportResource(locations = {
		"classpath:dubbo-config.xml",
})
public class WebdbMain {

	private static Logger LOGGER = LoggerFactory.getLogger(WebdbMain.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebdbMain.class, args);

        LOGGER.debug("WebdbMain started...");

        System.in.read();
    }
	
}
