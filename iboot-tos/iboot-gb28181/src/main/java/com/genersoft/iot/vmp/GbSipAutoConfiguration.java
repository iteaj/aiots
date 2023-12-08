package com.genersoft.iot.vmp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@ComponentScan({"com.genersoft.iot.vmp"})
@ServletComponentScan("com.genersoft.iot.vmp.conf")
@MapperScan("com.genersoft.iot.vmp.storager.dao")
@PropertySource("classpath:application-wvp.yml")
public class GbSipAutoConfiguration {

//    @Bean
//    public RedisTemplate<Object, Object> wvpRedisTemplate(RedisConnectionFactory connectionFactory) {
//
//        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
//        // 使用fastJson序列化
//        Jackson2JsonRedisSerializer jsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        // value值的序列化采用fastJsonRedisSerializer
//        redisTemplate.setValueSerializer(jsonRedisSerializer);
//        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
//
//        // key的序列化采用StringRedisSerializer
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setConnectionFactory(connectionFactory);
//        return redisTemplate;
//    }

}
