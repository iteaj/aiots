package com.iteaj.security.shiro.online;

import com.iteaj.framework.spi.OnlineSession;
import com.iteaj.security.shiro.AutoLoginToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.session.ProxiedSession;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * create time: 2021/3/20
 *
 * @author iteaj
 * @since 1.0
 */
public class OnlineSubjectDao extends DefaultSubjectDAO {

    @Override
    public Subject save(Subject subject) {
        Subject save = super.save(subject);

        // 此用客户端还没有认证
        if(!subject.isAuthenticated() && subject instanceof WebDelegatingSubject) {
            Session session = subject.getSession(false);
            if(session instanceof ProxiedSession) {
                // 验证此用户是否已经在线, 并且在线状态还未过期
                Object online = session.getAttribute("online");
                if(online instanceof OnlineSession) {
                    session.removeAttribute("online"); // 移除在线标志
                    String account = ((OnlineSession) online).getAccount();
                    try {
                        if(StringUtils.hasText(account)) {
                            SecurityUtils.getSecurityManager().login(subject
                                    , new AutoLoginToken(account, session.getId()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        session.removeAttribute("online");
                    }
                }
            }
        }


        return save;
    }
}
