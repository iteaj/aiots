package com.iteaj.framework.security.shiro;

import com.iteaj.framework.consts.CoreConst;
import com.iteaj.framework.logger.AccessLogger;
import com.iteaj.framework.logger.LoggerMenu;
import com.iteaj.framework.logger.LoggerService;
import com.iteaj.framework.spi.auth.WebAuthHandler;
import com.iteaj.framework.web.WebUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.ExecutionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
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
import java.util.concurrent.Callable;

/**
 * create time: 2021/6/29
 *
 * @author iteaj
 * @since 1.0
 */
public class ShiroFilterLogFactoryBean extends ShiroFilterFactoryBean {

    @Autowired(required = false)
    private LoggerService loggerService;
    @Autowired
    private WebAuthHandler webAuthHandler;
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
        protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {

            Throwable t = null;

            try {
                final ServletRequest request = prepareServletRequest(servletRequest, servletResponse, chain);
                final ServletResponse response = prepareServletResponse(request, servletResponse, chain);

                ThreadContext.put(CoreConst.HTTP_SERVLET_REQUEST, request);
                webAuthHandler.matcher((HttpServletRequest) request);
                final Subject subject = createSubject(request, response);

                //noinspection unchecked
                subject.execute((Callable) () -> {
                    // ThreadContext会移除一次绑定信息, 需要重新设置
                    ThreadContext.put(CoreConst.HTTP_SERVLET_REQUEST, request);

                    updateSessionLastAccessTime(request, response);
                    executeChain(request, response, chain);
                    return null;
                });
            } catch (ExecutionException ex) {
                t = ex.getCause();
            } catch (Throwable throwable) {
                t = throwable;
            }

            if (t != null) {
                if (t instanceof ServletException) {
                    throw (ServletException) t;
                }
                if (t instanceof IOException) {
                    throw (IOException) t;
                }
                //otherwise it's not one of the two exceptions expected by the filter method signature - wrap it in one:
                String msg = "Filtered request failed.";
                throw new ServletException(msg, t);
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

                        final Object attribute = request.getAttribute(CoreConst.EXEC_EXCEPTION_STATUS);
                        if(attribute instanceof String) {
                            logger.setStatus(false);
                            logger.setRemark((String) attribute);
                        }
                    } catch (Throwable e) {
                        logger.setStatus(false);
                        logger.setRemark("未知错误");
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
