package com.shop.config;

import com.shop.core.config.CoreApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Created by 36kr on 16/1/28.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    @Override
    protected final SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(WebappConfiguration.class, CoreApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(WebappConfiguration.class, args);
    }
}
