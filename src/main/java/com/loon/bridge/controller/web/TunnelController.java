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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.loon.bridge.core.aop.RightTarget;
import com.loon.bridge.core.comenum.Status;
import com.loon.bridge.core.comenum.TunnelType;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.web.Result;
import com.loon.bridge.service.device.DeviceService;
import com.loon.bridge.service.tunnel.TunnelService;
import com.loon.bridge.uda.entity.Device;
import com.loon.bridge.uda.entity.Tunnel;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.web.security.SecurityUser;

/**
 * 
 *
 * @author nbflow
 */
@Controller
@RequestMapping("/tunnelweb")
public class TunnelController extends BaseWebController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TunnelService tunnelService;

    @Autowired
    private DeviceService deviceService;

    @RequestMapping(value = "/tunnel/switch", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "tunnel_manage")
    public Object switchTunnel(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            Long id = Long.valueOf(params.get("id"));
            Status status = Status.valueOf(params.get("status"));
            tunnelService.switchTunnel(user, id, status);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/tunnel/del", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "tunnel_manage")
    public Object delTunnel(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            Long id = Long.valueOf(params.get("id"));

            tunnelService.delTunnel(user, id);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/tunnel/add", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "tunnel_manage")
    public Object addTunnel(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            Long deviceId = Long.valueOf(params.get("deviceId"));
            String localIp = params.get("localIp");
            Integer localPort = Integer.valueOf(params.get("localPort"));
            Integer remotePort = Integer.valueOf(params.get("remotePort"));
            String remark = params.get("remark");
            TunnelType tunnetType = TunnelType.valueOf(params.get("tunneltype"));

            Tunnel tunnel = new Tunnel();
            tunnel.setDeviceId(deviceId);
            tunnel.setIsactive(Status.YES);
            tunnel.setLocalIp(localIp);
            tunnel.setLocalPort(localPort);
            tunnel.setRemotePort(remotePort);
            tunnel.setTunnetType(tunnetType);
            tunnel.setRemarks(remark);
            
            tunnelService.addTunnel(user, tunnel);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/list/get", method = {RequestMethod.GET})
    @ResponseBody
    @RightTarget(value = "tunnel_manage")
    public Object getTunnelList(HttpServletRequest request) {

        try {

            int draw = Integer.valueOf(request.getParameter("draw"));
            int pagesize = Integer.valueOf(request.getParameter("length"));
            int page = Integer.valueOf(request.getParameter("start")) / pagesize + 1;

            SecurityUser securityUser = getSecurityUser();
            IPage<Tunnel> pageInfo = tunnelService.getTunnelListByUid(securityUser.getTargetId(), page, pagesize);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            int autoindex = Integer.valueOf(request.getParameter("start")) + 1;
            for (Tunnel item : pageInfo.getRecords()) {
                Map<String, Object> tempitem = new HashMap<String, Object>();
                tempitem.put("ctime", item.getCtime());
                tempitem.put("localIp", item.getLocalIp());
                tempitem.put("localPort", item.getLocalPort());
                tempitem.put("remotePort", item.getRemotePort());
                tempitem.put("isactive", item.getIsactive());
                tempitem.put("remarks", item.getRemarks());
                tempitem.put("tunnelType", item.getTunnetType());
                tempitem.put("id", item.getId());
                tempitem.put("autoid", autoindex++);

                Device tempDevice = deviceService.getDeviceById(item.getDeviceId());
                if (tempDevice == null) {
                    tempitem.put("deviceName", "-");
                    tempitem.put("deviceId", "");
                } else {
                    tempitem.put("deviceName", tempDevice.getName());
                    tempitem.put("deviceId", tempDevice.getId().toString());
                }

                list.add(tempitem);
            }

            Map<String, Object> retInfo = new HashMap<String, Object>();
            retInfo.put("draw", draw);
            retInfo.put("recordsTotal", pageInfo.getTotal());
            retInfo.put("recordsFiltered", pageInfo.getTotal());
            retInfo.put("data", list);

            return retInfo;
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

}
