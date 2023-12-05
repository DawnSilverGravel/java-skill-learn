package com.silvergravel;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author DawnStar
 * Date: 2023/10/7
 */
@SpringBootApplication
public class RocketMqApplication {
    public static void main(String[] args) {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(RocketMqApplication.class);
        ConfigurableApplicationContext context = springApplicationBuilder.web(WebApplicationType.NONE).run(args);
    }
}

