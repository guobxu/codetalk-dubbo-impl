package me.codetalk.proxy.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import me.codetalk.Constants;

@SpringBootApplication
@ComponentScan(value = {
	"me.codetalk.mesg",
	"me.codetalk.cache.config",
	"me.codetalk.param.checker",
	"me.codetalk.storage.fdfs.service",
	"me.codetalk.messaging.redis.sender",
	"me.codetalk.proxy",
	"me.codetalk.auth.proxy",
	"me.codetalk.apps.*.proxy",
})
@ImportResource("classpath:dubbo-config.xml")
public class ProxyMain {

	public static void main(String[] args) throws Exception {
//    	System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
    	
        SpringApplication.run(ProxyMain.class, args);
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
