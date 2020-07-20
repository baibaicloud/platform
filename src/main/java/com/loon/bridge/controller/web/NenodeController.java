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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.loon.bridge.core.aop.RightTarget;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.utils.Util;
import com.loon.bridge.core.web.Result;
import com.loon.bridge.service.nenode.NenodeInfo;
import com.loon.bridge.service.nenode.NenodeService;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.Nenode;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.web.security.SecurityUser;

/**
 * 
 *
 * @author nbflow
 */
@Controller
public class NenodeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private NenodeService nenodeService;

    @RequestMapping(value = "/nenode/move", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "nenode_manage")
    public Object moveNenode(HttpServletRequest request) {

        try {
            User user = this.getCurUser();
            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            List<Long> cids = Util.toArrayLong(params.get("cids"));
            Long id = Long.valueOf(params.get("id"));
            Long pid = Long.valueOf(params.get("pid"));
            SecurityUser securityUser = RequestUtil.getSecurityUser();
            nenodeService.moveNenode(user, securityUser.getType(), securityUser.getTargetId(), id, pid, cids);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/nenode/del", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "nenode_manage")
    public Object delNenode(HttpServletRequest request) {

        try {
            User user = this.getCurUser();
            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            SecurityUser securityUser = RequestUtil.getSecurityUser();
            nenodeService.deleteNenode(user, Long.valueOf(params.get("id")), securityUser.getType(), securityUser.getTargetId());
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/nenode/update", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "nenode_manage")
    public Object updateNenode(HttpServletRequest request) {

        try {
            User user = this.getCurUser();
            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            nenodeService.updateUserNenode(user, Long.valueOf(params.get("id")), params.get("title"));
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/nenode/create", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "nenode_manage")
    public Object createNenode(HttpServletRequest request) {

        try {
            User user = this.getCurUser();
            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            Nenode nenode = nenodeService.createNenode(user, params.get("title"), Long.valueOf(params.get("pid")));
            return Result.Success(nenode);
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/nenode/list/get", method = {RequestMethod.POST})
    @ResponseBody
    public Object getNenodeList(HttpServletRequest request) {

        try {
            User user = this.getCurUser();
            List<NenodeInfo> list = nenodeService.getUserNenodeTree(user);
            List<Map<String, String>> retList = new ArrayList<>();
            list.forEach((item) -> {
                Map<String, String> tempitem = new HashMap<>();
                tempitem.put("parent", item.getPid());
                tempitem.put("id", item.getId());
                tempitem.put("text", item.getTitle());
                retList.add(tempitem);
            });
            return Result.Success(retList);
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
