package me.codetalk.auth.main;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@SpringBootApplication
@ComponentScan(basePackages = {
        "me.codetalk.mesg",
        "me.codetalk.cache.config",
        "me.codetalk.messaging.redis.config",
        "me.codetalk.messaging.redis.sender",
        "me.codetalk.messaging.redis.listener",
		"me.codetalk.cache.service",
		"me.codetalk.cipher.service",
		"me.codetalk.auth.cache",
        "me.codetalk.auth.service",
        "me.codetalk.auth.aop",
})
@MapperScan(value = {"me.codetalk.*.mapper"})
@ImportResource(locations = {
		"classpath:dubbo-config.xml",
})
public class AuthMain {

    private static Logger LOGGER = LoggerFactory.getLogger(AuthMain.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AuthMain.class, args);

        LOGGER.debug("Auth server started...");

        System.in.read();
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:locale/messages");
        messageSource.setCacheSeconds(3600); //refresh cache once per hour
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
