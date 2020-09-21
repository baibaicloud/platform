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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.loon.bridge.core.comenum.UserType;
import com.loon.bridge.core.commo.AuthInfo;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.web.Result;
import com.loon.bridge.service.enterprise.EnterpriseService;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.web.security.SecurityUser;

/**
 * 
 *
 * @author nbflow
 */
@Controller
public class UserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private EnterpriseService enterpriseService;

    @RequestMapping(value = "/user/switch/info/set", method = {RequestMethod.POST})
    @ResponseBody
    public Object setSwitchInfo(HttpServletRequest request) {

        try {

            SecurityUser securityUser = RequestUtil.getSecurityUser();

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            UserType type = UserType.valueOf(params.get("type"));
            Long targetId = Long.valueOf(params.get("targetId"));
            securityUser.setType(type);
            securityUser.setTargetId(targetId);

            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/user/switch/info/list/get", method = {RequestMethod.POST})
    @ResponseBody
    public Object getSwitchInfoList(HttpServletRequest request) {

        try {
            User user = this.getCurUser();
            Map<String, Object> retInfo = new HashMap<String, Object>();
            List<AuthInfo> list = userService.getSwitchInfoList(user);
            retInfo.put("list", list);

            if (list.size() == 1) {
                SecurityUser securityUser = RequestUtil.getSecurityUser();
                securityUser.setType(UserType.SOLE);
                securityUser.setTargetId(user.getId());
            }

            SecurityUser securityUser = RequestUtil.getSecurityUser();
            if (securityUser.getType() == null) {
                retInfo.put("cur", null);
            } else {
                Map<String, Object> cur = new HashMap<String, Object>();
                cur.put("type", securityUser.getType());
                retInfo.put("cur", cur);
            }

            return Result.Success(retInfo);
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/user/password/reset/ack", method = {RequestMethod.POST})
    @ResponseBody
    public Object resetPasswordACK(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            userService.sendResetPasswordACK(params.get("uuid"), params.get("password"));
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/user/password/reset", method = {RequestMethod.POST})
    @ResponseBody
    public Object resetPassword(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            userService.sendResetPasswordEmail(params.get("email"));
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/user/info/get", method = {RequestMethod.POST})
    @ResponseBody
    public Object getUserInfo(HttpServletRequest request) {

        try {
            User user = this.getCurUser();
            user.setPassword(null);
            return Result.Success(user);
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/user/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update(HttpServletRequest request) {

        try {
            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }
            
            User curuser = this.getCurUser();
            
            User updateUser = new User();
            updateUser.setSelfname(params.get("selfname"));
            updateUser.setPassword(params.get("password"));

            userService.updateUserInfo(curuser, params.get("oldPw"), updateUser);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/user/signup", method = {RequestMethod.POST})
    @ResponseBody
    public Object signup(HttpServletRequest request) {

        try {
            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }
            
            boolean isuser = Boolean.valueOf(params.get("isuser"));
            String enname = params.get("enname");
            User user = new User();
            user.setEmail(params.get("email"));
            user.setUsername(params.get("username"));
            user.setPassword(params.get("password"));
            if (isuser) {
                return Result.Success(userService.signup(null, user));
            } else {
                enterpriseService.signup(enname, user);
                return Result.Success();
            }
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    /**
     * 获取用户信息
     * 
     * @return
     */
    private User getCurUser() {
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
