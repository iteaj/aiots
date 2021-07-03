package com.iteaj.framework.utils;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.iteaj.framework.FrameworkException;
import com.iteaj.framework.autoconfigure.RedisJacksonAutoConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * redis
 * create time: 2021/7/3
 *  必须等template先初始化
 * @author iteaj
 * @since 1.0
 */
public class RedisUtil {

    private RedisTemplate<String, JsonNode> template;
    private static ObjectMapper mapper = RedisJacksonAutoConfiguration.mapper;

    public RedisUtil(RedisTemplate template) {
        this.template = template;
    }

    public <T> T getBean(String key, Class<T> clazz) {
        final JsonNode jsonNode = template.opsForValue().get(key);
        return toBean(jsonNode, clazz);
    }

    public <T> T getBean(String key, String hashKey, Class<T> clazz) {
        final TreeNode jsonNode = (TreeNode) template.opsForHash().get(key, hashKey);
        return toBean(jsonNode, clazz);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        final JsonNode jsonNode = template.opsForValue().get(key);
        return toList(jsonNode, clazz);
    }

    public <T> Set<T> getSet(String key, Class<T> clazz) {
        final JsonNode jsonNode = template.opsForValue().get(key);
        return toSet(jsonNode, clazz);
    }

    public static <T> T toBean(TreeNode node, Class<T> clazz) throws FrameworkException {
        try {
            if(node != null) {
                return mapper.treeToValue(node, clazz);
            } else {
                throw new FrameworkException("期待值类型[TreeNode]");
            }
        } catch (Exception e) {
            throw new FrameworkException(e.getMessage(), e);
        }
    }

    public static <T> List<T> toList(TreeNode jsonNode, Class<T> clazz) {
        try {
            if(jsonNode instanceof ArrayNode) {
                final CollectionType collectionType = mapper.getTypeFactory()
                        .constructCollectionType(ArrayList.class, clazz);

                return mapper.readValue(mapper.treeAsTokens(jsonNode), collectionType);
            } else {
                throw new FrameworkException("期待值类型[ArrayNode]");
            }

        } catch (Exception e) {
            throw new FrameworkException(e.getMessage(), e);
        }
    }

    public static <T> Set<T> toSet(TreeNode jsonNode, Class<T> clazz) {
        try {
            if(jsonNode instanceof ArrayNode) {
                final CollectionType collectionType = mapper.getTypeFactory()
                        .constructCollectionType(HashSet.class, clazz);

                return mapper.readValue(mapper.treeAsTokens(jsonNode), collectionType);
            } else {
                throw new FrameworkException("期待值类型[ArrayNode]");
            }
        } catch (Exception e) {
            throw new FrameworkException(e.getMessage(), e);
        }
    }
}
