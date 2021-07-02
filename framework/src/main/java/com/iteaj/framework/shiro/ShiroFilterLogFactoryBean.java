package com.iteaj.framework.shiro;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.spi.admin.AccessLogger;
import com.iteaj.framework.spi.admin.LoggerMenu;
import com.iteaj.framework.spi.admin.LoggerService;
import com.iteaj.framework.web.WebUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * create time: 2021/6/29
 *
 * @author iteaj
 * @since 1.0
 */
public class ShiroFilterLogFactoryBean extends ShiroFilterFactoryBean {

    @Autowired(required = false)
    private LoggerService loggerService;

    @Override
    protected AbstractShiroFilter createInstance() throws Exception {
        SecurityManager securityManager = getSecurityManager();
        if (securityManager == null) {
            String msg = "SecurityManager property must be set.";
            throw new BeanInitializationException(msg);
        }

        if (!(securityManager instanceof WebSecurityManager)) {
            String msg = "The security manager does not implement the WebSecurityManager interface.";
            throw new BeanInitializationException(msg);
        }

        FilterChainManager manager = createFilterChainManager();

        //Expose the constructed FilterChainManager by first wrapping it in a
        // FilterChainResolver implementation. The AbstractShiroFilter implementations
        // do not know about FilterChainManagers - only resolvers:
        PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
        chainResolver.setFilterChainManager(manager);

        return new SpringShiroFilter((WebSecurityManager) securityManager, chainResolver);
    }

    private final class SpringShiroFilter extends AbstractShiroFilter {

        protected SpringShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
            super();
            if (webSecurityManager == null) {
                throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
            }
            setSecurityManager(webSecurityManager);

            if (resolver != null) {
                setFilterChainResolver(resolver);
            }
        }

        @Override
        protected void executeChain(ServletRequest request, ServletResponse response, FilterChain origChain) throws IOException, ServletException {
            if(loggerService != null) {
                HttpServletRequest servletRequest = (HttpServletRequest) request;
                final String requestURI = servletRequest.getRequestURI();
                final LoggerMenu loggerMenu = loggerService.getLoggerMenu(requestURI);
                // 需要采集记录日志
                if(loggerMenu != null && loggerMenu.isCollect()) {
                    long startMills = System.currentTimeMillis();
                    final AccessLogger logger = new AccessLogger(requestURI, "执行成功");
                    try {
                        logger.setStatus(true).setIp(WebUtils.getIpAddress(servletRequest));
                        super.executeChain(request, response, origChain);
                        final Object status = request.getAttribute(CoreConst.AUTH_STATUS);
                        if(status instanceof Boolean) {
                            logger.setStatus((Boolean) status);
                            logger.setRemark("未认证");
                        }

                    } catch (UnauthorizedException e) {
                        logger.setRemark("无权限");
                        logger.setStatus(false);
                        throw e;
                    } catch (UnauthenticatedException e) {
                        logger.setRemark("未认证");
                        logger.setStatus(false);
                        throw e;
                    } catch (Exception e) {
                        logger.setRemark("未知错误");
                        logger.setStatus(false);
                        throw e;
                    } finally {
                        long endMills = System.currentTimeMillis();
                        logger.setExecTime(endMills - startMills);

                        loggerService.record(logger);
                    }
                } else {
                    super.executeChain(request, response, origChain);
                }
            } else {
                super.executeChain(request, response, origChain);
            }
        }
    }
}
