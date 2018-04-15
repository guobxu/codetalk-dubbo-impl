package me.codetalk.webminer.main;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {
        "me.codetalk.webminer.runner",
        "me.codetalk.cache.config",
//        "me.codetalk.messaging.kafka.impl",
        "me.codetalk.messaging.redis.sender",
})
@MapperScan(value = {"me.codetalk.webminer.mapper"})
public class MinerMain {

	private static Logger LOGGER = LoggerFactory.getLogger(MinerMain.class);
	
	public static void main(String[] args) throws Exception {
        SpringApplication.run(MinerMain.class, args);

        LOGGER.debug("Miner server started...");

        System.in.read();
    }
	
}
