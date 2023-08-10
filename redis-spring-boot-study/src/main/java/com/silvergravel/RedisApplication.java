package com.silvergravel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author DawnStar
 */
@SpringBootApplication
public class RedisApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(RedisApplication.class)
                        .web(WebApplicationType.NONE)
                        .run(args);
        RedisTemplate<String, Object> redisTemplate =
                context.getBean("redisTemplate", RedisTemplate.class);
        RedisTemplate<String, Object> redisTemplate1 =
                context.getBean("redisTemplate1", RedisTemplate.class);
        redisTemplate.opsForValue().set("11", "2123");
        redisTemplate.boundValueOps("22").set("2124");

        redisTemplate1.opsForValue().set("33", "2123");
        redisTemplate1.boundValueOps("44").set("2124");
        // 其他同理...
    }
}