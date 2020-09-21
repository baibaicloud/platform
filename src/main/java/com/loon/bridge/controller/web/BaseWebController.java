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
 *   Loon  2019年11月2日 下午11:58:03  created
 */
package com.loon.bridge.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.web.security.SecurityUser;

/**
 * 
 *
 * @author nbflow
 */
abstract class BaseWebController {

    @Autowired
    private UserService userService;

    /**
     * 获取SecurityUser
     * 
     * @return
     */
    protected SecurityUser getSecurityUser() {
        return RequestUtil.getSecurityUser();
    }

    /**
     * 获取用户信息
     * 
     * @return
     */
    protected User getCurUser() {
        String username = RequestUtil.getUsername();
        if (StringUtils.isEmpty(username)) {
            throw new BusinessException("无授权信息");
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new BusinessException("无授权信息");
        }

        return user;
    }
}
