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
 *   Loon  2019年11月13日 下午11:57:57  created
 */
package com.loon.bridge.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
@Component
public class CustomUserService implements UserDetailsService {

    @Autowired
    private UserService userService;

    /**
     * 登陆验证时，通过username获取用户的所有权限信息 并返回UserDetails放到spring的全局缓存SecurityContextHolder中，以供授权器使用
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUser myUserDetail = new SecurityUser();
        if (StringUtils.isEmpty(username)) {
            throw new RuntimeException("用户名不能为空！");
        }

        User user = userService.getUserByUsername(username);

        if (user == null) {
            throw new RuntimeException("未找到用户信息");
        }

        myUserDetail.setUsername(username);
        myUserDetail.setPassword(user.getPassword());
        return myUserDetail;
    }
}
