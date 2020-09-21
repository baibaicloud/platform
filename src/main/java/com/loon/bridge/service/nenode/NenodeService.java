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
 *   Loon  2019年12月21日 下午5:45:29  created
 */
package com.loon.bridge.service.nenode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.loon.bridge.core.comenum.Status;
import com.loon.bridge.core.comenum.UserType;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.utils.Util;
import com.loon.bridge.service.device.DeviceService;
import com.loon.bridge.service.enterprise.EnterpriseService;
import com.loon.bridge.uda.entity.DeviceUser;
import com.loon.bridge.uda.entity.Enterprise;
import com.loon.bridge.uda.entity.EnterpriseNenodeRole;
import com.loon.bridge.uda.entity.EnterpriseRole;
import com.loon.bridge.uda.entity.EnterpriseUserRole;
import com.loon.bridge.uda.entity.Nenode;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.uda.mapper.NenodeMapper;
import com.loon.bridge.web.security.SecurityUser;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class NenodeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${nenode.max.level}")
    private Integer NENODE_MAX_LEVEL;

    /**
     * 默认父节点，此节点无法删除
     */
    public static final Long ROOT_NODE_ID_DEFAULT = -1L;

    /**
     * 默认节点id
     */
    public static final Long NODE_ID_DEFAULT = 0L;

    /**
     * 默认的排序位置节点
     */
    public static final int NODE_ORDER_DEFAULT = 1;

    @Autowired
    private NenodeMapper nenodeMapper;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private EnterpriseService enterpriseService;

    /**
     * 添加节点
     * 
     * @param user
     * @param pid
     */
    public Nenode createNenode(User user, String title, Long pid) {

        if (pid == null) {
            throw new BusinessException("父节点不能为空");
        }

        if (StringUtils.isEmpty(title)) {
            throw new BusinessException("节点名称不能为空");
        }

        Nenode nenode = new Nenode();
        nenode.setOperId(RequestUtil.getSecurityUser().getTargetId());
        nenode.setPid(pid);
        nenode.setTitle(title);

        Nenode parentNenode = this.getNenodeById(pid);
        if (parentNenode == null) {
            throw new BusinessException("父节点非法");
        }

        if (parentNenode.getPath().split(",").length >= NENODE_MAX_LEVEL) {
            throw new BusinessException("节点层次太多，无法创建");
        }

        nenodeMapper.insert(nenode);

        Nenode updateNenode = new Nenode();
        updateNenode.setId(nenode.getId());
        updateNenode.setPath(parentNenode.getPath() + "," + nenode.getId());
        nenode.setPath(updateNenode.getPath());
        nenodeMapper.updateById(updateNenode);
        
        List<Nenode> list = getNenodeList(user);
        list.add(parentNenode.getOrderIndex() + 1, nenode);

        for (int i = 0; i < list.size(); i++) {
            Nenode itemNenode = list.get(i);
            Nenode itemUpdateNenode = new Nenode();
            itemUpdateNenode.setId(itemNenode.getId());
            itemUpdateNenode.setOrderIndex(i);
            nenodeMapper.updateById(itemUpdateNenode);
        }

        logger.info("user add nenode success,uid:{},path:{}", user.getId(), nenode.getPath());

        return nenode;
    }

    /**
     * 根据id获取节点
     * 
     * @param id
     * @return
     */
    public Nenode getNenodeById(Long id) {

        if (id == NODE_ID_DEFAULT) {
            return null;
        }

        return nenodeMapper.selectById(id);
    }

    /**
     * 获取默认节点
     * 
     * @param targetId 此id可能是user也可能是企业id
     * @return
     */
    public Nenode getNenodeDefaultByTargetId(Long targetId) {

        Nenode entity = new Nenode();
        entity.setOperId(targetId);
        entity.setPid(ROOT_NODE_ID_DEFAULT);
        QueryWrapper<Nenode> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);

        return nenodeMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户获取资源列表,针对个人
     * 
     * @param user
     * @return
     */
    private List<Nenode> getNenodeListFromSoleByUser(User user) {

        if (user == null) {
            throw new BusinessException("错误错误");
        }

        Nenode entity = new Nenode();
        entity.setOperId(user.getId());
        QueryWrapper<Nenode> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        queryWrapper.orderByAsc("orderIndex");

        return nenodeMapper.selectList(queryWrapper);
    }

    /**
     * 根据用户获取资源列表,针对企业,企业有权限过滤
     * 
     * @param eid
     * @param user
     * @return
     */
    private List<Nenode> getNenodeListFromEnterpriseByUser(Long eid, User user) {

        if (eid == null || user == null) {
            throw new BusinessException("错误错误");
        }

        EnterpriseUserRole userRole = enterpriseService.getEnterpriseUserRoleByEidUser(eid, user);
        EnterpriseRole enterpriseRole = enterpriseService.getEnterpriseRoleById(userRole.getRoleId());
        
        if (enterpriseRole.getIsdefault() == Status.YES) {
            QueryWrapper<Nenode> queryWrapper = (QueryWrapper<Nenode>) new QueryWrapper<Nenode>().apply("operId = {0}", eid);
            queryWrapper.orderByAsc("orderIndex");
            return nenodeMapper.selectList(queryWrapper);
        }

        List<EnterpriseNenodeRole> enterpriseNenodeRoleList = enterpriseService.getEnterpriseNenodeRoleListByEidRoleId(eid, userRole.getRoleId());
        if (enterpriseNenodeRoleList.isEmpty()) {
            return new ArrayList<Nenode>();
        }

        List<Long> nenodeIds = new ArrayList<Long>();
        for (EnterpriseNenodeRole item : enterpriseNenodeRoleList) {
            nenodeIds.add(item.getNenodeId());
        }

        List<Nenode> retList = nenodeMapper.selectBatchIds(nenodeIds);
        if (retList.isEmpty()) {
            return new ArrayList<Nenode>();
        }

        Set<Long> allnenodeIds = new HashSet<Long>();
        for (Nenode item : retList) {
            allnenodeIds.addAll(Util.toArrayLong(item.getPath()));
        }

        Nenode entity = new Nenode();
        entity.setOperId(eid);
        QueryWrapper<Nenode> queryWrapper = new QueryWrapper<Nenode>();
        queryWrapper.orderByAsc("orderIndex");
        queryWrapper.in("id", allnenodeIds);
        return nenodeMapper.selectList(queryWrapper);
    }

    /**
     * 根据用户获取指定节点
     * 
     * @param user
     * @return
     */
    public List<Nenode> getNenodeList(User user) {
        SecurityUser securityUser = RequestUtil.getSecurityUser();
        if (securityUser.getType() == UserType.SOLE) {
            return getNenodeListFromSoleByUser(user);
        } else if (securityUser.getType() == UserType.ENTERPRISE) {
            return getNenodeListFromEnterpriseByUser(securityUser.getTargetId(), user);
        } else {
            throw new BusinessException("auth type error");
        }
    }

    /**
     * 用户用户资源树
     * 
     * @param user
     * @return
     */
    public List<NenodeInfo> getUserNenodeTree(User user) {

        List<Nenode> list = getNenodeList(user);
        List<NenodeInfo> retList = new ArrayList<>();

        list.forEach((item) -> {
            NenodeInfo itemInfo = new NenodeInfo();
            itemInfo.setId(item.getId().toString());
            itemInfo.setPid((item.getPid().equals(ROOT_NODE_ID_DEFAULT)) ? "#" : item.getPid().toString());
            itemInfo.setLevel(item.getPath().split(",").length + "");
            itemInfo.setTitle(item.getTitle());
            itemInfo.setPath(item.getPath());
            retList.add(itemInfo);
        });

        return retList;
    }

    /**
     * 更新节点
     * 
     * @param user
     * @param nenodeId
     * @param title
     */
    public void updateUserNenode(User user, Long nenodeId, String title) {

        if (!this.hasNenode(nenodeId)) {
            throw new BusinessException();
        }

        Nenode update = new Nenode();
        update.setId(nenodeId);
        update.setTitle(title);
        nenodeMapper.updateById(update);
        logger.info("user update nenode success,uid:{},nenode:{}", user.getId(), title);
    }

    /**
     * 删除节点
     * 
     * @param user
     * @param nenodeId
     */
    public void deleteNenode(User user, Long nenodeId, UserType authType, Long targetId) {

        if (!this.hasNenode(nenodeId)) {
            throw new BusinessException();
        }

        Nenode nenode = this.getNenodeById(nenodeId);
        if (nenode.getPid() == ROOT_NODE_ID_DEFAULT) {
            throw new BusinessException("根节点无法删除");
        }

        if (enterpriseService.checkRoleNenodeUseing(nenodeId)) {
            throw new BusinessException("此节点被企业角色使用中，无法删除。");
        }

        List<DeviceUser> devList = deviceService.getDeviceListByNodeId(user, nenodeId);
        if (!devList.isEmpty()) {
            throw new BusinessException("请先删除该节点下的所有网元");
        }

        List<Nenode> list = getChildNenodeByPid(nenodeId);
        if (!list.isEmpty()) {
            throw new BusinessException("请先删除子节点");
        }

        nenodeMapper.deleteById(nenodeId);
        logger.info("user del nenode success,uid:{},nenode:{}", user.getId(), nenodeId);
    }

    /**
     * 获取子节点，只包含根节点
     * 
     * @param nenodeId
     * @return
     */
    private List<Nenode> getChildNenodeByPid(Long nenodeId) {
        Nenode entity = new Nenode();
        entity.setPid(nenodeId);
        entity.setOperId(RequestUtil.getSecurityUser().getTargetId());
        QueryWrapper<Nenode> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(entity);
        return nenodeMapper.selectList(queryWrapper);
    }

    /**
     * 获取子节点，包含所有节点
     * 
     * @param nenodeId
     * @return
     */
    public List<Nenode> getChildNenodeFullByPid(User user, Long nenodeId) {
        QueryWrapper<Nenode> queryWrapper = new QueryWrapper<Nenode>().apply("operId = {0} and find_in_set({1} ,path)", user.getId(), nenodeId);
        return nenodeMapper.selectList(queryWrapper);
    }

    /**
     * 创建默认节点
     * 
     * @param user
     */
    public Nenode createNenodeDefault(User user) {

        Nenode nenode = new Nenode();
        nenode.setOperId(user.getId());
        nenode.setOrderIndex(NODE_ORDER_DEFAULT);
        nenode.setPid(ROOT_NODE_ID_DEFAULT);
        nenode.setTitle("我的节点");
        nenodeMapper.insert(nenode);

        Nenode update = new Nenode();
        update.setId(nenode.getId());
        update.setPath(ROOT_NODE_ID_DEFAULT + "," + nenode.getId());
        nenodeMapper.updateById(update);

        nenode.setPath(update.getPath());
        return nenode;
    }

    /**
     * 创建默认节点
     * 
     * @param user
     */
    public Nenode createNenodeDefault(Enterprise enterprise) {

        Nenode nenode = new Nenode();
        nenode.setOperId(enterprise.getId());
        nenode.setOrderIndex(NODE_ORDER_DEFAULT);
        nenode.setPid(ROOT_NODE_ID_DEFAULT);
        nenode.setTitle("我的节点");
        nenodeMapper.insert(nenode);

        Nenode update = new Nenode();
        update.setId(nenode.getId());
        update.setPath(ROOT_NODE_ID_DEFAULT + "," + nenode.getId());
        nenodeMapper.updateById(update);

        nenode.setPath(update.getPath());
        return nenode;
    }

    /**
     * 判断某个节点是否属于自己
     * 
     * @param nenodeId
     * @return
     */
    public boolean hasNenode(Long nenodeId) {

        Nenode nenode = nenodeMapper.selectById(nenodeId);
        if (nenode == null) {
            return false;
        }

        Long targetId = RequestUtil.getSecurityUser().getTargetId();

        return nenode.getOperId().equals(targetId);
    }

    /**
     * 移动节点
     * 
     * @param user
     * @param type
     * @param targetId
     * @param nenodeId
     * @param newPid
     * @param cids new child ids list order
     */
    public void moveNenode(User user, UserType type, Long targetId, Long nenodeId, Long newPid, List<Long> cids) {

        Nenode nenode = nenodeMapper.selectById(nenodeId);
        if(nenode == null || nenode.getPid().equals(ROOT_NODE_ID_DEFAULT)) {
            throw new BusinessException();
        }
        
        if (!this.hasNenode(nenodeId) || !this.hasNenode(newPid)) {
            throw new BusinessException();
        }

        // 更新节点关系
        Nenode parentNenode = nenodeMapper.selectById(newPid);
        Nenode update = new Nenode();
        update.setId(nenodeId);
        update.setPid(newPid);
        update.setPath(parentNenode.getPath() + "," + nenodeId);
        nenodeMapper.updateById(update);

        // 开始排序节点
        for (int i = 0; i < cids.size(); i++) {
            Nenode updateorder = new Nenode();
            updateorder.setId(cids.get(i));
            updateorder.setOrderIndex(i);
            nenodeMapper.updateById(updateorder);
        }
        logger.info("move node success,oper:{},nodeid:{},newpid:{},cids:{}", user.getId(), nenodeId, newPid, cids);
    }

}
