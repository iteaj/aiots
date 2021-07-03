package com.iteaj.redis.shiro;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iteaj.framework.autoconfigure.RedisJacksonAutoConfiguration;
import com.iteaj.framework.autoconfigure.SessionCacheProperties;
import com.iteaj.framework.shiro.online.ShiroOnlineSession;
import lombok.SneakyThrows;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.ThreadContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * create time: 2021/7/3
 *
 * @author iteaj
 * @since 1.0
 */
public class ShiroRedisSessionCacheManager implements CacheManager, InitializingBean {

    @Autowired
    private RedisTemplate jacksonRedisTemplate;
    @Autowired
    private SessionCacheProperties config;

    private ValueOperations valueOperations;
    private static final String SessionKeyPrefix = "IFSCF:";
    private static final String AuthorizationKeyPrefix = "IFAOCF:";
    private static final String AuthenticationKeyPrefix = "IFAECF:";

    private Map<String, Cache> cacheMap = new HashMap<>();
    private ObjectMapper mapper = RedisJacksonAutoConfiguration.mapper;

    @Override
    public Cache<String, Cache> getCache(String s) throws CacheException {
        return cacheMap.get(s);
    }

    protected class RedisSessionCache implements Cache<String, Session>{

        @SneakyThrows
        @Override
        public Session get(String s) throws CacheException {
            // 首先先从线程上下文获取session, 将session和线程绑定
            Session session = (Session) ThreadContext.get(s);
            if(session != null) {
                return session;
            }

            final Object o = valueOperations.get(SessionKeyPrefix+s);
            if(o instanceof JsonNode) {
                session = mapper.treeToValue((TreeNode) o, ShiroOnlineSession.class);
                ThreadContext.put(s, session);
            }

            return session;
        }

        @Override
        public Session put(String s, Session session) throws CacheException {
            valueOperations.set(SessionKeyPrefix+s, session, config.getExpire(), TimeUnit.SECONDS);
            ThreadContext.put(s, session);
            return session;
        }

        @Override
        public Session remove(String s) throws CacheException {
            final Session session = this.get(SessionKeyPrefix+s);
            if(session != null) {
                jacksonRedisTemplate.delete(SessionKeyPrefix+s);
            }

            return session;
        }

        @Override
        public void clear() throws CacheException {
            final Set<String> keys = keys();
            if(!keys().isEmpty()) {
                jacksonRedisTemplate.delete(keys);
            }
        }

        @Override
        public int size() {
            return keys().size();
        }

        @Override
        public Set<String> keys() {
            final Set<String> keys = jacksonRedisTemplate
                    .keys(SessionKeyPrefix + "*");

            if(!CollectionUtils.isEmpty(keys)) {
                return keys.stream().map(key -> key
                        .split(":")[1]).collect(Collectors.toSet());
            }

            return Collections.emptySet();
        }

        @Override
        public Collection<Session> values() {
            return Collections.emptyList();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.valueOperations = jacksonRedisTemplate.opsForValue();
        cacheMap.put(config.getName(), new RedisSessionCache());
    }
}
