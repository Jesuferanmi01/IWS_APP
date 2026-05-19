package com.understandingjava.iws_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class IwsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(IwsAppApplication.class, args);
    }

}
