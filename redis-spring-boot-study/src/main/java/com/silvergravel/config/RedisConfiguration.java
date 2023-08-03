package com.silvergravel.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @author DawnStar
 * Date: 2023/8/3
 */
@Configuration
public class RedisConfiguration {

    @Resource
    private RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }



    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(1);
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        // 数据库0
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // key序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        // 字符
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate1() {
        // 数据库1
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置默认序列化
        redisTemplate.setDefaultSerializer(RedisSerializer.string());
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }



}
