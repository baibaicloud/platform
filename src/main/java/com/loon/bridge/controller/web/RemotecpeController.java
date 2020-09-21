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

import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.web.Result;
import com.loon.bridge.service.remotecpe.NatInfo;
import com.loon.bridge.service.remotecpe.RemotecpeService;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
@Controller
public class RemotecpeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private RemotecpeService remotecpeService;

    @RequestMapping(value = "/remotecpe/shutdown", method = {RequestMethod.POST})
    @ResponseBody
    public Object shutdown(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            remotecpeService.shutdownDevice(user, Long.valueOf(params.get("deviceId")));
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/remotecpe/start", method = {RequestMethod.POST})
    @ResponseBody
    public Object startRemotecpe(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            NatInfo natInfo = remotecpeService.startRemoteControl(user, params);
            Map<String, Object> ret = new HashMap<String, Object>();
            ret.put("uuid", natInfo.getUuid());
            return Result.Success(ret);
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
