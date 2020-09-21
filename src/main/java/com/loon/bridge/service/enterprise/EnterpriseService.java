/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2008-2014 nbflow. All rights reserved.
 * <p>
 * This software is the confidential and proprietary information of nbflow.
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the agreements you entered into with nbflow.
 * 
 * Modified history:
 *   nbflow  2020年1月29日 下午3:52:46  created
 */
package com.loon.bridge.service.enterprise;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.loon.bridge.core.comenum.Status;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.service.nenode.NenodeService;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.Enterprise;
import com.loon.bridge.uda.entity.EnterpriseNenodeRole;
import com.loon.bridge.uda.entity.EnterpriseRole;
import com.loon.bridge.uda.entity.EnterpriseRoleRight;
import com.loon.bridge.uda.entity.EnterpriseUser;
import com.loon.bridge.uda.entity.EnterpriseUserRole;
import com.loon.bridge.uda.entity.Nenode;
import com.loon.bridge.uda.entity.Right;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.uda.mapper.EnterpriseMapper;
import com.loon.bridge.uda.mapper.EnterpriseNenodeRoleMapper;
import com.loon.bridge.uda.mapper.EnterpriseRoleMapper;
import com.loon.bridge.uda.mapper.EnterpriseRoleRightMapper;
import com.loon.bridge.uda.mapper.EnterpriseUserMapper;
import com.loon.bridge.uda.mapper.EnterpriseUserRoleMapper;
import com.loon.bridge.uda.mapper.RightMapper;

/**
 * 
 * 
 *
 * @author nbflow
 */
@Service
public class EnterpriseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RightMapper rightMapper;

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Autowired
    private EnterpriseUserMapper enterpriseUserMapper;

    @Autowired
    private EnterpriseRoleMapper enterpriseRoleMapper;

    @Autowired
    private EnterpriseRoleRightMapper enterpriseRoleRightMapper;

    @Autowired
    private EnterpriseNenodeRoleMapper enterpriseNenodeRoleMapper;

    @Autowired
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private NenodeService nenodeService;

    /**
     * 判断uid是否未为启用创建者
     * 
     * @param eid
     * @param uid
     * @return
     */
    public boolean isEnterpriseOwner(Long eid, Long uid) {

        if (eid == null || uid == null) {
            return false;
        }

        Enterprise enterprise = this.enterpriseMapper.selectById(eid);
        if (enterprise == null) {
            return false;
        }

        return enterprise.getCuid().equals(uid);
    }

    /**
     * 根据上下文获取角色列表
     * 
     * @return
     */
    public List<EnterpriseRole> getEnterpriseRoleListByCurUser() {
        EnterpriseRole entity = new EnterpriseRole();
        entity.setEid(RequestUtil.getSecurityUser().getTargetId());

        QueryWrapper<EnterpriseRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        queryWrapper.orderByAsc("id");
        return enterpriseRoleMapper.selectList(queryWrapper);
    }

    /**
     * 根据企业名称获取企业信息
     * 
     * @param title
     * @return
     */
    public Enterprise getEnterpriseByTitle(String title) {

        Enterprise entity = new Enterprise();
        entity.setTitle(title);

        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return enterpriseMapper.selectOne(queryWrapper);
    }

    /**
     * 注册企业
     * 
     * @param enname 企业名称
     * @param user
     */
    public void signup(String enname, User user) {
        if (StringUtils.isEmpty(enname)) {
            throw new BusinessException("错误错误");
        }

        Enterprise enterprise = getEnterpriseByTitle(enname);
        if (enterprise != null) {
            throw new BusinessException("企业名称已存在");
        }
        Date ctime = new Date();

        // 创建企业
        Enterprise enterpriseEntity = new Enterprise();
        enterpriseEntity.setCtime(ctime);
        enterpriseEntity.setTitle(enname);
        enterpriseMapper.insert(enterpriseEntity);

        // 创建用户
        User newuser = userService.signup(enterpriseEntity.getId(), user);

        Enterprise updateEnterprise = new Enterprise();
        updateEnterprise.setId(enterpriseEntity.getId());
        updateEnterprise.setCuid(newuser.getId());
        enterpriseMapper.updateById(updateEnterprise);

        // 添加企业用户关系
        EnterpriseUser enterpriseUserEntity = new EnterpriseUser();
        enterpriseUserEntity.setCtime(ctime);
        enterpriseUserEntity.setEid(enterpriseEntity.getId());
        enterpriseUserEntity.setUid(newuser.getId());
        enterpriseUserEntity.setUsername(newuser.getUsername());
        enterpriseUserMapper.insert(enterpriseUserEntity);

        // 创建默认角色
        EnterpriseRole enterpriseRoleEntity = new EnterpriseRole();
        enterpriseRoleEntity.setIsdefault(Status.YES);
        enterpriseRoleEntity.setCtime(ctime);
        enterpriseRoleEntity.setEid(enterpriseEntity.getId());
        enterpriseRoleEntity.setTitle("超级管理员");
        enterpriseRoleMapper.insert(enterpriseRoleEntity);

        // 创建默认角色对应节点的关系
        Nenode nenode = nenodeService.createNenodeDefault(enterpriseEntity);
        EnterpriseNenodeRole enterpriseNenodeRoleentity = new EnterpriseNenodeRole();
        enterpriseNenodeRoleentity.setCtime(ctime);
        enterpriseNenodeRoleentity.setEid(enterpriseEntity.getId());
        enterpriseNenodeRoleentity.setRoleId(enterpriseRoleEntity.getId());
        enterpriseNenodeRoleentity.setNenodeId(nenode.getId());
        enterpriseNenodeRoleMapper.insert(enterpriseNenodeRoleentity);

        // 创建企业用户对应的角色关系
        EnterpriseUserRole enterpriseUserRole = new EnterpriseUserRole();
        enterpriseUserRole.setCtime(ctime);
        enterpriseUserRole.setEid(enterpriseEntity.getId());
        enterpriseUserRole.setRoleId(enterpriseRoleEntity.getId());
        enterpriseUserRole.setUid(newuser.getId());
        enterpriseUserRoleMapper.insert(enterpriseUserRole);

        logger.info("signup success,title:{}", enname);
    }

    /**
     * 根据id获取企业信息
     * 
     * @param id
     * @return
     */
    public Enterprise getEnterpriseById(Long id) {
        return enterpriseMapper.selectById(id);
    }

    /**
     * 根据上下文获取企业信息
     * 
     * @return
     */
    public Enterprise getEnterpriseByCurUser() {
        return enterpriseMapper.selectById(RequestUtil.getSecurityUser().getTargetId());
    }

    /**
     * 根据用户获取所有企业
     * 
     * @param uid
     * @return
     */
    public List<Enterprise> getEnterpriseListByUid(Long uid) {
        List<Enterprise> enlist = new ArrayList<Enterprise>();
        EnterpriseUser entity = new EnterpriseUser();
        entity.setUid(uid);

        QueryWrapper<EnterpriseUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        List<EnterpriseUser> list = enterpriseUserMapper.selectList(queryWrapper);
        if (list.isEmpty()) {
            return enlist;
        }

        list.forEach((item) -> {
            Enterprise tempitem = getEnterpriseById(item.getEid());
            if (tempitem == null) {
                return;
            }
            enlist.add(tempitem);
        });

        return enlist;
    }

    /**
     * 获取企业用户对应的角色
     * 
     * @param eid
     * @param user
     * @return
     */
    public EnterpriseUserRole getEnterpriseUserRoleByEidUser(Long eid, User user) {
        return getEnterpriseUserRoleByEidUid(eid, user.getId());
    }

    /**
     * 获取企业用户对应的角色
     * 
     * @param eid
     * @param uid
     * @return
     */
    public EnterpriseUserRole getEnterpriseUserRoleByEidUid(Long eid, Long uid) {

        EnterpriseUserRole entity = new EnterpriseUserRole();
        entity.setEid(eid);
        entity.setUid(uid);

        QueryWrapper<EnterpriseUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return enterpriseUserRoleMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户id和角色id获取角色对应的节点
     * 
     * @param uid
     * @param roleId
     * @return
     */
    public List<EnterpriseNenodeRole> getEnterpriseNenodeRoleListByEidUidRoleId(Long eid, Long uid, Long roleId) {

        if (!this.hasUser(eid, uid)) {
            throw new BusinessException("参数错误");
        }

        return this.getEnterpriseNenodeRoleListByEidRoleId(eid, roleId);
    }

    /**
     * 获取企业用户对应角色的资源节点
     * 
     * @param enterpriseId
     * @param roleId
     * @return
     */
    public List<EnterpriseNenodeRole> getEnterpriseNenodeRoleListByEidRoleId(Long enterpriseId, Long roleId) {
        EnterpriseNenodeRole entity = new EnterpriseNenodeRole();
        entity.setEid(enterpriseId);
        entity.setRoleId(roleId);

        QueryWrapper<EnterpriseNenodeRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return enterpriseNenodeRoleMapper.selectList(queryWrapper);
    }

    /**
     * 根据id获取角色信息
     * 
     * @param roleId
     * @return
     */
    public EnterpriseRole getEnterpriseRoleById(Long roleId) {
        return enterpriseRoleMapper.selectById(roleId);
    }

    /**
     * 根据id获取角色信息
     * 
     * @param eid
     * @param uie
     * @return
     */
    public EnterpriseRole getEnterpriseRoleByEidUid(Long eid, Long uid) {
        EnterpriseUserRole userRole = getEnterpriseUserRoleByEidUid(eid, uid);
        if (userRole == null) {
            return null;
        }

        return this.getEnterpriseRoleById(userRole.getRoleId());
    }

    /**
     * 判断用户是否在企业内
     * 
     * @param eid
     * @param uid
     * @return
     */
    public boolean hasUser(Long eid, Long uid) {

        EnterpriseUser entity = new EnterpriseUser();
        entity.setEid(eid);
        entity.setUid(uid);

        QueryWrapper<EnterpriseUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);

        return enterpriseUserMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 添加用户到企业
     * 
     * @param oper
     * @param username
     */
    public void addEnterpriseUser(User oper, String username, Long roleId) {
        if (StringUtils.isEmpty(username) || roleId == null) {
            throw new BusinessException("参数错误");
        }

        User adduser = userService.getUserByUsername(username);
        if (adduser == null) {
            throw new BusinessException(username + " 用户未找到，请先注册。");
        }

        Long eid = RequestUtil.getSecurityUser().getTargetId();
        if (hasUser(eid, adduser.getId())) {
            throw new BusinessException("此用户已存在企业中");
        }

        EnterpriseRole enterpriseRole = enterpriseRoleMapper.selectById(roleId);
        if (enterpriseRole == null || !enterpriseRole.getEid().equals(eid)) {
            throw new BusinessException("参数错误");
        }

        Date ctime = new Date();
        EnterpriseUser entity = new EnterpriseUser();
        entity.setEid(eid);
        entity.setUid(adduser.getId());
        entity.setUsername(adduser.getUsername());
        entity.setCtime(ctime);
        enterpriseUserMapper.insert(entity);

        EnterpriseUserRole enterpriseUserRole = new EnterpriseUserRole();
        enterpriseUserRole.setCtime(ctime);
        enterpriseUserRole.setEid(eid);
        enterpriseUserRole.setRoleId(roleId);
        enterpriseUserRole.setUid(adduser.getId());
        enterpriseUserRoleMapper.insert(enterpriseUserRole);

        logger.info("add enterprise user success,operuid:{}.tuid:{}", oper.getId(), adduser.getId());
    }

    /**
     * 获取企业用户列表
     * 
     * @param user
     * @param filter
     * @return
     */
    public IPage<User> getEnterpriseUserList(User user, int page, int size, Map<String, String> filter) {
        EnterpriseUser entity = new EnterpriseUser();
        entity.setEid(RequestUtil.getSecurityUser().getTargetId());

        QueryWrapper<EnterpriseUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        queryWrapper.orderByDesc("id");

        if (!StringUtils.isEmpty(filter.get("username"))) {
            queryWrapper.like("username", filter.get("username"));
        }

        Page<EnterpriseUser> pageWrapper = new Page<EnterpriseUser>(page, size);
        IPage<EnterpriseUser> retPage = enterpriseUserMapper.selectPage(pageWrapper, queryWrapper);

        List<Long> uids = new ArrayList<Long>();
        retPage.getRecords().forEach((item) -> {
            uids.add(item.getUid());
        });

        if (uids.isEmpty()) {
            return new Page<User>();
        }

        List<User> userList = userService.getUserListByIds(uids);
        IPage<User> userPage = new Page<User>();
        userPage.setRecords(userList);
        userPage.setTotal(retPage.getTotal());
        userPage.setPages(retPage.getPages());
        userPage.setCurrent(retPage.getCurrent());
        userPage.setSize(retPage.getSize());

        return userPage;
    }

    /**
     * 根据企业id，员工id获取员工关系信息
     * 
     * @param eid
     * @param uid
     * @return
     */
    public EnterpriseUser getEnterpriseUserByEidUid(Long eid, Long uid) {
        EnterpriseUser entity = new EnterpriseUser();
        entity.setEid(eid);
        entity.setUid(uid);

        QueryWrapper<EnterpriseUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);

        return enterpriseUserMapper.selectOne(queryWrapper);
    }

    /**
     * 删除企业员工
     * 
     * @param user
     * @param uid
     */
    public void delEnterpriseUser(User user, Long uid) {

        Long eid = RequestUtil.getSecurityUser().getTargetId();
        boolean flag = this.hasUser(eid, user.getId());
        if (!flag) {
            throw new BusinessException("非法参数");
        }
        
        if (this.isEnterpriseOwner(eid, uid)) {
            throw new BusinessException("无法删除超级超级管理员");
        }

        EnterpriseUser enuser = getEnterpriseUserByEidUid(eid, uid);
        if (enuser == null || !enuser.getEid().equals(eid)) {
            throw new BusinessException("非法参数");
        }

        if (enuser.getUid().equals(user.getId())) {
            throw new BusinessException("自己不能删除");
        }

        enterpriseUserMapper.deleteById(enuser.getId());
        logger.info("user del enterprise user success,oper:{},target uid:{}", user.getId(), enuser.getUid());
    }

    /**
     * 添加企业角色
     * 
     * @param user
     * @param title
     */
    public void addEnterpriseRole(User user, String title) {

        if (StringUtils.isEmpty(title)) {
            throw new BusinessException("参数错误");
        }

        Enterprise enterprise = this.getEnterpriseByCurUser();
        EnterpriseRole enterpriseRoleEntity = new EnterpriseRole();
        enterpriseRoleEntity.setIsdefault(Status.NO);
        enterpriseRoleEntity.setCtime(new Date());
        enterpriseRoleEntity.setEid(enterprise.getId());
        enterpriseRoleEntity.setTitle(title);
        enterpriseRoleMapper.insert(enterpriseRoleEntity);
        logger.info("oper add enterprise role success,uid:{}, title:{}", user.getId(), title);
    }

    /**
     * 删除企业角色
     * 
     * @param oper
     * @param id
     */
    public void delEnterpriseRole(User oper, Long id) {

        EnterpriseRole role = enterpriseRoleMapper.selectById(id);
        if (role == null || !role.getEid().equals(RequestUtil.getSecurityUser().getTargetId())) {
            throw new BusinessException("参数错误");
        }

        if (role.getIsdefault() == Status.YES) {
            throw new BusinessException("超级管理员无法删除");
        }

        EnterpriseUserRole entity = new EnterpriseUserRole();
        entity.setRoleId(role.getId());

        QueryWrapper<EnterpriseUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        if (enterpriseUserRoleMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("此角色正在使用中，无法删除");
        }

        EnterpriseRoleRight entityEnterpriseRoleRight = new EnterpriseRoleRight();
        entity.setRoleId(id);
        QueryWrapper<EnterpriseRoleRight> queryWrapperEnterpriseRoleRight = new QueryWrapper<>();
        queryWrapperEnterpriseRoleRight.setEntity(entityEnterpriseRoleRight);
        enterpriseRoleRightMapper.delete(queryWrapperEnterpriseRoleRight);

        enterpriseRoleMapper.deleteById(id);
        logger.info("oper del enterprise role success,uid:{}, title:{}", oper.getId(), role.getTitle());
    }

    /**
     * 获取企业角色对应的权限
     * 
     * @param roleId
     * @return
     */
    public List<String> getEnterpriseRoleRightListByRoleId(Long roleId) {
        
        List<String> allright = new ArrayList<String>();
        EnterpriseRole roleInfo = this.enterpriseRoleMapper.selectById(roleId);
        if (roleInfo == null) {
            throw new BusinessException("参数错误");
        }

        if (roleInfo.getIsdefault() == Status.YES) {
            List<Right> templist = rightMapper.selectList(null);
            for (Right item : templist) {
                allright.add(item.getTarget());
            }
            return allright;
        }

        EnterpriseRoleRight entity = new EnterpriseRoleRight();
        entity.setEid(RequestUtil.getSecurityUser().getTargetId());
        entity.setRoleId(roleId);

        QueryWrapper<EnterpriseRoleRight> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        

        enterpriseRoleRightMapper.selectList(queryWrapper).forEach((item) -> {
            allright.add(item.getTarget());
        });

        return allright;
    }

    /**
     * 更新企业角色对应的权限
     * 
     * @param roleId
     * @param target
     * @param isadd
     */
    public void updateEnterpriseRoleRightByRoleId(User oper, Long roleId, String target, boolean isadd) {

        Long eid = RequestUtil.getSecurityUser().getTargetId();
        EnterpriseRole role = enterpriseRoleMapper.selectById(roleId);
        if (role == null || !role.getEid().equals(eid)) {
            throw new BusinessException("参数错误");
        }
        
        if (role.getIsdefault() == Status.YES) {
            throw new BusinessException("超级管理员无法修改");
        }

        if (isadd) {
            EnterpriseRoleRight entity = new EnterpriseRoleRight();
            entity.setEid(eid);
            entity.setRoleId(roleId);
            entity.setTarget(target);
            enterpriseRoleRightMapper.insert(entity);
        } else {
            EnterpriseRoleRight entity = new EnterpriseRoleRight();
            entity.setEid(eid);
            entity.setRoleId(roleId);
            entity.setTarget(target);
            QueryWrapper<EnterpriseRoleRight> queryWrapper = new QueryWrapper<>();
            queryWrapper.setEntity(entity);
            enterpriseRoleRightMapper.delete(queryWrapper);
        }

        logger.info("user update role right success,uid:{},roleid:{},target:{},isadd:{}", oper.getId(), roleId, target, isadd);
    }

    /**
     * 编辑企业角色
     * 
     * @param user
     * @param roleId
     * @param title
     */
    public void updateEnterpriseRole(User user, Long roleId, String title) {

        Long eid = RequestUtil.getSecurityUser().getTargetId();
        EnterpriseRole role = enterpriseRoleMapper.selectById(roleId);
        if (role == null || !role.getEid().equals(eid)) {
            throw new BusinessException("参数错误");
        }

        if (role.getIsdefault() == Status.YES) {
            throw new BusinessException("超级管理员无法修改");
        }

        EnterpriseRole entity = new EnterpriseRole();
        entity.setId(roleId);
        entity.setTitle(title);
        enterpriseRoleMapper.updateById(entity);

        logger.info("user update role success,oper:{},roleid:{}", user.getId(), roleId);

    }

    /**
     * 判断节点是否被角色使用中...
     * 
     * @param nenodeId
     * @return
     */
    public boolean checkRoleNenodeUseing(Long nenodeId) {
        EnterpriseNenodeRole entity = new EnterpriseNenodeRole();
        entity.setNenodeId(nenodeId);

        QueryWrapper<EnterpriseNenodeRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return enterpriseNenodeRoleMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 更新企业角色对应的节点
     * 
     * 
     * @param oper
     * @param roleId
     * @param eid
     * @param ids
     */
    public void updateEnterpriseRoleNenode(User oper, Long eid, Long roleId, List<Long> ids) {

        if (!this.hasRole(eid, roleId)) {
            throw new BusinessException("参数错误");
        }
        
        Map<String, Object> columnMap = new HashMap<String, Object>();
        columnMap.put("eid", eid);
        columnMap.put("roleId", roleId);
        enterpriseNenodeRoleMapper.deleteByMap(columnMap);

        Date ctime = new Date();
        ids.forEach((item) -> {
            EnterpriseNenodeRole enterpriseNenodeRoleentity = new EnterpriseNenodeRole();
            enterpriseNenodeRoleentity.setCtime(ctime);
            enterpriseNenodeRoleentity.setEid(eid);
            enterpriseNenodeRoleentity.setRoleId(roleId);
            enterpriseNenodeRoleentity.setNenodeId(item);
            enterpriseNenodeRoleMapper.insert(enterpriseNenodeRoleentity);
        });

        logger.info("update role nenode success,oper:{},roleid, ids:{}", oper, roleId, ids);
    }

    /**
     * 判断企业是否包含此角色
     * 
     * @param eid
     * @param roleId
     * @return
     */
    public boolean hasRole(Long eid, Long roleId) {
        EnterpriseRole role = enterpriseRoleMapper.selectById(roleId);
        if (role == null) {
            return false;
        }

        return role.getEid().equals(eid);
    }

    /**
     * 删除角色对应的节点
     * 
     * @param oper
     * @param eid
     * @param roleId
     * @param nenodeId
     */
    public void delEnterpriseRoleNenode(User oper, Long eid, Long roleId, Long nenodeId) {

        Map<String, Object> columnMap = new HashMap<String, Object>();
        columnMap.put("nenodeId", nenodeId);
        columnMap.put("eid", eid);
        columnMap.put("roleId", roleId);
        enterpriseNenodeRoleMapper.deleteByMap(columnMap);

        logger.info("del role nenode success,oper:{},roleid, ids:{}", oper.getId(), roleId, nenodeId);
    }

    /**
     * 判断企业用户是否拥有某个权限
     * 
     * @param eid
     * @param user
     * @param rightname
     * @return
     */
    public boolean hasRight(Long eid, User user, String rightname) {
        if (!this.hasUser(eid, user.getId())) {
            return false;
        }
        
        EnterpriseUserRole userRole = this.getEnterpriseUserRoleByEidUser(eid, user);
        if (userRole == null) {
            return false;
        }

        EnterpriseRole role = getEnterpriseRoleById(userRole.getRoleId());
        if (role == null) {
            return false;
        }

        if (role.getIsdefault() == Status.YES) {
            return true;
        }

        List<String> rights = this.getEnterpriseRoleRightListByRoleId(userRole.getRoleId());
        if (rights.isEmpty()) {
            return false;
        }

        return rights.contains(rightname);
    }

    /**
     * 编辑企业用户
     * 
     * @param oper
     * @param eid
     * @param uid
     * @param params
     */
    public void editEnterpriseUser(User oper, Long eid, Map<String, String> params) {

        if (params.containsKey("roleId")) {
            editEnterpriseUserRole(oper, eid, params);
        }
        
        logger.info("user update enterprise user success,oper:{},eid:{},info:{}", oper.getId(), eid, params);
    }

    /**
     * 更新用户角色
     * 
     * @param oper
     * @param eid
     * @param uid
     * @param params
     */
    private void editEnterpriseUserRole(User oper, Long eid, Map<String, String> params) {
        Long roleId = Long.valueOf(params.get("roleId"));
        Long uid = Long.valueOf(params.get("uid"));
        EnterpriseRole role = this.getEnterpriseRoleById(roleId);

        if (role == null || !role.getEid().equals(eid)) {
            logger.error("skip update enterprise role info,role is null or roleid or eid is error,oper:{},eid:{},params:{}", oper.getId(), eid, params);
            return;
        }

        if (!this.hasUser(eid, uid)) {
            logger.error("skip update enterprise role info,enterprise user is error, oper:{},eid:{},params:{}", oper.getId(), eid, params);
            return;
        }

        if (this.isEnterpriseOwner(eid, uid)) {
            logger.error("skip update enterprise role info,uid error, oper:{},eid:{},params:{}", oper.getId(), eid, params);
            return;
        }

        EnterpriseUserRole userRole = this.getEnterpriseUserRoleByEidUid(eid, uid);
        if (userRole == null) {
            logger.error("skip update enterprise role info,user role is error, oper:{},eid:{},params:{}", oper.getId(), eid, params);
            return;
        }

        EnterpriseUserRole entity = new EnterpriseUserRole();
        entity.setId(userRole.getId());
        entity.setRoleId(roleId);
        enterpriseUserRoleMapper.updateById(entity);

        logger.info("update enterprise role info success, oper:{},eid:{},params:{}", oper.getId(), eid, params);
    }

}
