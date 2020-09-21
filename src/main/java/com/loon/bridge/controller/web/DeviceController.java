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

import com.loon.bridge.core.aop.RightTarget;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.web.Result;
import com.loon.bridge.service.device.DeviceService;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.Device;
import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
@Controller
public class DeviceController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @RequestMapping(value = "/device/delete", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "add_device")
    public Object deleteDevice(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            Long deviceId = Long.valueOf(params.get("deviceId"));

            deviceService.deleteDevice(user, deviceId);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/device/nenode/move", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "add_device")
    public Object moveDeviceNenode(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            Long deviceId = Long.valueOf(params.get("deviceId"));
            Long nenodeId = Long.valueOf(params.get("nenodeId"));

            deviceService.moveDeviceNenode(user, deviceId, nenodeId);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/device/user/bind", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "add_device")
    public Object bingDeviceUser(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            String uuid = params.get("uuid");
            int code = Integer.valueOf(params.get("code"));
            deviceService.bingDeviceUser(user, uuid, code);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/device/update", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "add_device")
    public Object updateDevice(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            Device update = new Device();
            update.setId(Long.valueOf(params.get("id")));
            update.setDevdesc(params.get("devdesc"));

            User user = this.getCurUser();
            deviceService.updateDevice(user, update);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/device/list/get", method = {RequestMethod.POST})
    @ResponseBody
    public Object getDeviceList(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }
            
            User user = this.getCurUser();
            if (params.get("id").equals("")) {
                List<Device> list = deviceService.getDeviceLastuseByUser(user);
                return Result.Success(list);
            }

            Long nodeid = Long.valueOf(params.get("id"));
            List<Device> list = deviceService.getDeviceByCurUser(nodeid);
            return Result.Success(list);
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
