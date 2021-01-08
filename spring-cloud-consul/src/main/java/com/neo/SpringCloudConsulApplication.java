package com.neo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author fei
 */
@SpringBootApplication
@ComponentScan(value = "com.neo")
public class SpringCloudConsulApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConsulApplication.class, args);
    }
}
