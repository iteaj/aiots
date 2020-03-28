package com.iteaj.framework.spring.boot;

import com.iteaj.framework.observer.handle.AuthEventHandle;
import com.iteaj.framework.shiro.OrderShiroFilterChainDefinition;
import com.iteaj.framework.spring.boot.config.FrameworkProperties;
import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 注：只用于后台管理项目的子模块如：dev、static、admin
 * 继承此类的子类, 一定记得标注{@link org.springframework.context.annotation.Configuration}
 */
public abstract class AbstractWebConfiguration {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 用来注册认证事件处理器注意:<br>
     *     1. 使用@Bean时必须指定名称{@link Bean#name()}
     * @see com.iteaj.framework.observer.event.AuthEvent
     * @see com.iteaj.framework.consts.AuthEventType#LOGOUT
     * @see com.iteaj.framework.consts.AuthEventType#LOGIN
     * @return
     */
    protected AuthEventHandle registerAuthEventHandle() {return null;}

    /**
     * 覆写此方法后需要用{@link Bean#name()} 注解, 且必须起一个不用其他模块的名称<br>
     *     e.g: {@link com.iteaj.framework.spring.boot.config.ShiroConfiguration#primaryShiroFilterChainDefinition()}
     * @see org.springframework.core.annotation.Order 使用此注解标注 可以设置Shiro的拦截顺序
     * @see OrderShiroFilterChainDefinition
     * @see com.iteaj.framework.spring.boot.config.ShiroConfiguration#shiroFilterFactoryBean(SecurityManager, FrameworkProperties, List, List)
     * @return
     */
    protected OrderShiroFilterChainDefinition moduleFilterChainDefinition() {
        return null;
    }
}
