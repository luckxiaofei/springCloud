package com.neo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fei
 */
@Configuration
@EnableAutoConfiguration
@RestController
public class SpringCloudConsulApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConsulApplication.class, args);
    }
}
