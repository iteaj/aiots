package com.iteaj.admin.service;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.iteaj.framework.AbstractServiceApplication;
import com.iteaj.framework.api.AuthorityManager;
import com.iteaj.framework.api.AuthorizingService;
import com.iteaj.framework.observer.event.AuthorityEvent;
import com.iteaj.framework.observer.handle.AuthEventHandle;
import com.iteaj.framework.observer.handle.AuthorityEventHandle;
import com.iteaj.framework.observer.listener.AuthorityManagerListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * create time: 2019/12/2
 *  Admin业务模块
 * @author iteaj
 * @since 1.0
 */
@MapperScan(basePackages = {"com.iteaj.admin.dao"})
@SpringBootApplication(scanBasePackages = "com.iteaj.**")
public class AdminServiceApplication implements AbstractServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bind.type}"))
    private AuthorizingService authorizingService;

    /**
     * 在应用上下文刷新完成时, 注册{@link AuthorityManagerListener}监听器
     * @see AuthorityManager
     * @see AuthEventHandle
     * @see AuthorityEventHandle
     * @see AuthorityEvent 菜单管理事件
     * @see com.iteaj.framework.observer.event.AuthEvent 认证事件
     * @param event
     */
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        AuthorityManager authorityManager = event
//                .getApplicationContext().getBean(AuthorityManager.class);
//        if(null == authorityManager)
//            throw new BeanNotOfRequiredTypeException("找不到菜单管理器", AuthorityManager.class, null);
//
//        // 获取由各个应用注册的认证事件处理器
//        Map<String, AuthEventHandle> authListenerHandles = event
//                .getApplicationContext().getBeansOfType(AuthEventHandle.class);
//
//        // 获取由各个应用注册的权限事件处理器
//        Map<String, AuthorityEventHandle> menusListenerHandles = event
//                .getApplicationContext().getBeansOfType(AuthorityEventHandle.class);
//
//        // 创建权限管理监听器
//        AuthorityManagerListener listener = new AuthorityManagerListener(authorityManager
//                , authListenerHandles.values(), menusListenerHandles.values());
//
//        // 注册监听器
//        EventUtils.getEventBus().register(listener);
//    }
}
