package com.iteaj.framework.shiro;

import com.iteaj.framework.authority.AuthToken;
import com.iteaj.framework.authority.DefaultAuthToken;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * create time: 2019/12/6
 *  基于Shiro框架实现的认证token
 * @author iteaj
 * @since 1.0
 */
public class ShiroTokenDecorate extends UsernamePasswordToken {

    private DefaultAuthToken authToken;

    public ShiroTokenDecorate(AuthToken authToken) {
        this.authToken = (DefaultAuthToken) authToken;
    }

    @Override
    public String getUsername() {
        return this.authToken.getUserName();
    }

    @Override
    public void setUsername(String username) {
        this.authToken.setUserName(username);
    }

    @Override
    public char[] getPassword() {
        return this.authToken.getPassword().toCharArray();
    }

    @Override
    public void setPassword(char[] password) {
        this.authToken.setPassword(new String(password));
    }

    @Override
    public Object getPrincipal() {
        return this.authToken.getPrincipal();
    }

    @Override
    public Object getCredentials() {
        return this.authToken.getCredentials();
    }

    @Override
    public boolean isRememberMe() {
        return this.authToken.isRememberMe();
    }

    @Override
    public void setRememberMe(boolean rememberMe) {
        this.authToken.setRememberMe(rememberMe);
    }

    public DefaultAuthToken getAuthToken() {
        return authToken;
    }

    public ShiroTokenDecorate setAuthToken(DefaultAuthToken authToken) {
        this.authToken = authToken;
        return this;
    }
}
