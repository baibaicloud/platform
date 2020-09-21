/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2008-2014 loon. All rights reserved.
 * <p>
 * This software is the confidential and proprietary information of loon.
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the agreements you entered into with loon.
 * 
 * Modified history:
 *   Administrator  2019年6月16日 下午5:06:47  created
 */
package com.loon.bridge.web.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * 
 *
 * @author Administrator
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${http.white.url.active}")
    private String whiteActiveUrl;

    @Autowired
    private Environment env;

    @Autowired
    private UserDetailsService myCustomUserService;

    @Autowired
    private CustomPasswordEncoder myPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        String[] whiteActiveUrls = whiteActiveUrl.split(",");
        List<String> whiteUrls = new ArrayList<String>();

        for (int i = 0; i < whiteActiveUrls.length; i++) {
            String item = whiteActiveUrls[i];
            item = env.getProperty("http.white.urls." + item);
            if (StringUtils.isEmpty(item)) {
                continue;
            }
            whiteUrls.add(item);
        }

        whiteUrls.add("/static/**");
        
        http.cors().and().csrf().disable();
        
        http
            //使用form表单post方式进行登录
            .formLogin()
            //登录页面为自定义的登录页面
                .loginPage("/login").successForwardUrl("/index").defaultSuccessUrl("/index").failureUrl("/login?error=true")
            .permitAll()
            .and()
            //允许不登陆就可以访问的方法，多个用逗号分隔
                .authorizeRequests().antMatchers(whiteUrls.toArray(new String[whiteUrls.size()])).permitAll()
            //其他的需要授权后访问
            .anyRequest().authenticated();        
        
            //session管理,失效后跳转  
            http.sessionManagement().invalidSessionUrl("/login"); 
            //单用户登录，如果有一个登录了，同一个用户在其他地方登录将前一个剔除下线 
            //http.sessionManagement().maximumSessions(1).expiredSessionStrategy(expiredSessionStrategy()); 
            //单用户登录，如果有一个登录了，同一个用户在其他地方不能登录 
            http.sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(true); 
            //退出时情况cookies
            http.logout().deleteCookies("JESSIONID"); 
            //解决中文乱码问题 
            CharacterEncodingFilter filter = new CharacterEncodingFilter(); 
            filter.setEncoding("UTF-8"); filter.setForceEncoding(true); 
            http.addFilterBefore(filter,CsrfFilter.class); 
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider bean = new DaoAuthenticationProvider();
        bean.setHideUserNotFoundExceptions(true);
        bean.setUserDetailsService(myCustomUserService);
        bean.setPasswordEncoder(myPasswordEncoder);
        return bean;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(this.daoAuthenticationProvider());
    }

}

