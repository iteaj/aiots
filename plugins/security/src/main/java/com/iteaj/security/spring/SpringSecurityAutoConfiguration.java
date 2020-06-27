package com.iteaj.security.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * create time: 2020/6/22
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
public class SpringSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
//        auth.userDetailsService()
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.set
    }
}
