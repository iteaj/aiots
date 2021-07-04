package com.iteaj.framework.autoconfigure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteaj.framework.utils.JsonUtil;
import com.iteaj.framework.utils.RedisUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * create time: 2021/7/3
 *  redis缓存基于jackson序列化实现
 * @author iteaj
 * @since 1.0
 */
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
public class RedisJacksonAutoConfiguration {

    public static ObjectMapper mapper = JsonUtil.mapper;

    static {

    }

    @Bean({"jacksonRedisTemplate"})
    public RedisTemplate jacksonRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // key使用String序列化
        redisTemplate.setKeySerializer(redisTemplate.getStringSerializer());
        redisTemplate.setHashKeySerializer(redisTemplate.getStringSerializer());

        // value 使用Jackson序列化
        Jackson2JsonRedisSerializer<JsonNode> valueSerializer =
                new Jackson2JsonRedisSerializer<>(JsonNode.class);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);

        return redisTemplate;
    }

    @Bean
    public RedisUtil redisUtil(RedisTemplate redisTemplate) {
        return new RedisUtil(redisTemplate);
    }
}
