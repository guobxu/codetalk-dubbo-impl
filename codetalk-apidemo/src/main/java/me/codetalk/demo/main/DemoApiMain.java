package me.codetalk.demo.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {
	"me.codetalk.demo.service",
	"me.codetalk.demo.controller"
})
public class DemoApiMain {

	public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApiMain.class, args);
    }
	
}
