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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.loon.bridge.core.aop.RightTarget;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.DateUtil;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.web.Result;
import com.loon.bridge.service.enterprise.EnterpriseService;
import com.loon.bridge.service.nenode.NenodeInfo;
import com.loon.bridge.service.nenode.NenodeService;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.EnterpriseNenodeRole;
import com.loon.bridge.uda.entity.EnterpriseRole;
import com.loon.bridge.uda.entity.Nenode;
import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
@Controller()
@RequestMapping("enterprise/")
public class EnterpriseController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private NenodeService nenodeService;

    @RequestMapping(value = "/role/nenode/del", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object delRoleNenode(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            Long roleId = Long.valueOf(params.get("roleId"));
            Long eid = RequestUtil.getSecurityUser().getTargetId();
            Long nenodeId = Long.valueOf(params.get("nenodeId"));

            enterpriseService.delEnterpriseRoleNenode(user, eid, roleId, nenodeId);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/role/nenode/update", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object updateRoleNenode(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            Long roleId = Long.valueOf(params.get("roleId"));
            Long eid = RequestUtil.getSecurityUser().getTargetId();
            String[] idsStr = params.get("ids").split(",");
            List<Long> ids = new ArrayList<Long>();

            for (String item : idsStr) {
                if (StringUtils.isEmpty(item)) {
                    continue;
                }
                ids.add(Long.valueOf(item));
            }

            enterpriseService.updateEnterpriseRoleNenode(user, eid, roleId, ids);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "/role/nenode/list/get", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object getRoleNenodeList(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            Long roleId = Long.valueOf(params.get("roleId"));
            Long eid = RequestUtil.getSecurityUser().getTargetId();

            List<EnterpriseNenodeRole> list = enterpriseService.getEnterpriseNenodeRoleListByEidUidRoleId(eid, user.getId(), roleId);
            List<Nenode> retList = new ArrayList<>();

            list.forEach((item) -> {
                retList.add(nenodeService.getNenodeById(item.getNenodeId()));
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

    @RequestMapping(value = "/nenode/list/get", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object getNenodeList(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = this.getCurUser();
            Long roleId = Long.valueOf(params.get("roleId"));
            Long eid = RequestUtil.getSecurityUser().getTargetId();
            List<EnterpriseNenodeRole> list = enterpriseService.getEnterpriseNenodeRoleListByEidUidRoleId(eid, user.getId(), roleId);

            List<NenodeInfo> nenodes = nenodeService.getUserNenodeTree(user);
            List<Map<String, String>> retList = new ArrayList<>();

            nenodes.forEach((item) -> {
                Map<String, String> tempitem = new HashMap<>();
                tempitem.put("id", item.getId());
                tempitem.put("pid", item.getPid());
                tempitem.put("title", item.getTitle());
                tempitem.put("checked", hasRoleNenode(list, Long.valueOf(item.getId())).toString());
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

    private Boolean hasRoleNenode(List<EnterpriseNenodeRole> list, Long nenodeId) {
        for (EnterpriseNenodeRole item : list) {
            if (item.getNenodeId().equals(nenodeId)) {
                return true;
            }
        }

        return false;
    }

    @RequestMapping(value = "role/right/update", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object updateRoleRightEnterprise(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User oper = getCurUser();
            Long roleId = Long.valueOf(params.get("roleId"));
            String target = params.get("target");
            boolean isadd = Boolean.valueOf(params.get("isadd"));
            
            enterpriseService.updateEnterpriseRoleRightByRoleId(oper, roleId, target, isadd);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "role/right/list/get", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object getRoleRightListEnterprise(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            return Result.Success(enterpriseService.getEnterpriseRoleRightListByRoleId(Long.valueOf(params.get("roleId"))));
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "role/list/get", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object getRoleListEnterprise(HttpServletRequest request) {

        try {
            return Result.Success(enterpriseService.getEnterpriseRoleListByCurUser());
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "user/list/get", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object getUserListEnterprise(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            int page = Integer.valueOf(params.get("page"));
            int size = Integer.valueOf(params.get("size"));

            Long eid = RequestUtil.getSecurityUser().getTargetId();
            User user = getCurUser();
            IPage<User> pageInfo = enterpriseService.getEnterpriseUserList(user, page, size, params);

            List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
            pageInfo.getRecords().forEach((item) -> {
                Map<String, String> tempitem = new HashMap<>();
                tempitem.put("id", item.getId().toString());
                tempitem.put("email", item.getEmail());
                tempitem.put("username", item.getUsername());
                tempitem.put("ctime", DateUtil.dataFormat(item.getCtime()));
                tempitem.put("selfname", item.getSelfname());

                EnterpriseRole role = this.enterpriseService.getEnterpriseRoleByEidUid(eid, item.getId());
                if (role == null) {
                    tempitem.put("rolename", "");
                } else {
                    tempitem.put("rolename", role.getTitle());
                }

                retList.add(tempitem);
            });

            return Result.SuccessPage(pageInfo, retList);
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "user/del", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object delUserEnterprise(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = getCurUser();
            enterpriseService.delEnterpriseUser(user, Long.valueOf(params.get("id")));
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "role/del", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object delRoleEnterprise(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = getCurUser();
            enterpriseService.delEnterpriseRole(user, Long.valueOf(params.get("id")));
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "role/update", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object updateRoleEnterprise(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = getCurUser();
            enterpriseService.updateEnterpriseRole(user, Long.valueOf(params.get("id")), params.get("title"));
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "role/add", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object addRoleEnterprise(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = getCurUser();
            enterpriseService.addEnterpriseRole(user, params.get("title"));
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "user/edit", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object updateUserEnterprise(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = getCurUser();
            enterpriseService.editEnterpriseUser(user, RequestUtil.getSecurityUser().getTargetId(), params);
            return Result.Success();
        } catch (BusinessException e) {
            logger.error(e.getMessage(), e);
            return Result.Error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Result.Error();
        }
    }

    @RequestMapping(value = "user/add", method = {RequestMethod.POST})
    @ResponseBody
    @RightTarget(value = "enterprise_manage")
    public Object addUserEnterprise(HttpServletRequest request) {

        try {

            Map<String, String> params = RequestUtil.getBody(request);
            if (params == null || params.isEmpty()) {
                return null;
            }

            User user = getCurUser();
            enterpriseService.addEnterpriseUser(user, params.get("username"), Long.valueOf(params.get("roleId")));
            return Result.Success();
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
