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
 *   Loon  2019年11月14日 下午9:46:54  created
 */
package com.loon.bridge.service.device;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.loon.bridge.core.comenum.Status;
import com.loon.bridge.core.comenum.UserType;
import com.loon.bridge.core.commo.AuthInfo;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.RequestUtil;
import com.loon.bridge.core.utils.TaskEngine;
import com.loon.bridge.core.utils.Util;
import com.loon.bridge.service.enterprise.EnterpriseService;
import com.loon.bridge.service.lastuse.DeviceUserLastUseService;
import com.loon.bridge.service.nenode.NenodeService;
import com.loon.bridge.service.remotecpe.NatService;
import com.loon.bridge.service.session.Client;
import com.loon.bridge.service.session.Message;
import com.loon.bridge.service.session.SessionService;
import com.loon.bridge.service.tunnel.TunnelService;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.Device;
import com.loon.bridge.uda.entity.DeviceSettings;
import com.loon.bridge.uda.entity.DeviceUser;
import com.loon.bridge.uda.entity.DeviceUserLastUse;
import com.loon.bridge.uda.entity.Enterprise;
import com.loon.bridge.uda.entity.Nenode;
import com.loon.bridge.uda.entity.User;
import com.loon.bridge.uda.mapper.DeviceMapper;
import com.loon.bridge.uda.mapper.DeviceSettingsMapper;
import com.loon.bridge.uda.mapper.DeviceUserMapper;
import com.loon.bridge.web.security.SecurityUser;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class DeviceService implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${device.timeout.bingkey.max.sec}")
    private Integer DEVICE_TIMEOUT_BINGKEY_SEC;

    @Autowired
    private DeviceUserMapper deviceUserMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceSettingsMapper deviceSettingsMapper;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private NenodeService nenodeService;

    @Autowired
    private DeviceUserLastUseService deviceUserLastUseService;

    @Autowired
    private NatService natService;

    @Autowired
    private UserService userService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private TunnelService tunnelService;

    /**
     * 存放sn对应的bingkey 信息
     */
    private Map<String, BingKey> bindkeys = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        TaskEngine.getInstance().schedule(new TimerTask() {

            @Override
            public void run() {
                timeoutBingkey();
            }
        }, 1000, DEVICE_TIMEOUT_BINGKEY_SEC * 1000 / 2);
    }

    /**
     * 处理过期的bingkey
     */
    private void timeoutBingkey() {

        long curtime = new Date().getTime();
        long maxsec = DEVICE_TIMEOUT_BINGKEY_SEC * 1000;

        List<String> dels = new ArrayList<String>();
        bindkeys.forEach((sn, key) -> {
            if ((curtime - key.getCtime().getTime()) > maxsec) {
                dels.add(sn);
            }
        });

        dels.forEach((sn) -> {
            bindkeys.remove(sn);
            logger.debug("bingkey timeout,sn:{}", sn);
        });

    }

    /**
     * 添加设备
     * 
     * @param device
     */
    public Device addDevice(Device device) {
        device.setCtime(new Date());
        deviceMapper.insert(device);
        logger.info("add device success,name:{},id:{}", device.getName(), device.getId());
        return device;
    }

    /**
     * 根据设备sn进行查询
     * 
     * @param sn
     * @return
     */
    public Device getDeviceBySN(String sn) {

        if (StringUtils.isEmpty(sn)) {
            return null;
        }

        Device device = new Device();
        device.setSn(sn);
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(device);
        return deviceMapper.selectOne(queryWrapper);
    }

    /**
     * 更新设备信息
     * 
     * @param oper
     * @param update
     */
    public void updateBySN(Device update) {
        
        Device tempDevice = this.getDeviceById(update.getId());
        if (tempDevice == null) {
            throw new BusinessException("参数错误");
        }

        deviceMapper.updateById(update);
        logger.info("update device info success,did:{}", tempDevice.getId());
    }

    /**
     * 根据用户获取设备列表
     * 
     * @param nodeId
     * @return
     */
    public List<Device> getDeviceByCurUser(Long nodeId) {

        SecurityUser securityUser = RequestUtil.getSecurityUser();
        List<DeviceUser> deviceUsers = this.getDeviceUserByTargetIdNodeId(securityUser.getTargetId(), nodeId);
        if (deviceUsers.isEmpty()) {
            return new ArrayList<Device>();
        }
        
        List<Long> ids = new ArrayList<Long>();
        deviceUsers.forEach((item) -> {
            ids.add(item.getDeviceId());
        });

        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        queryWrapper.orderByDesc("id");

        List<Device> list = deviceMapper.selectList(queryWrapper);
        list.forEach((item) -> {
            item.setIsonline(sessionService.deviceIsOnline(item.getId()));
            item.setIsremote(natService.isRemoteingByDeviceSN(item.getSn()));
        });
        return list;
    }

    /**
     * 根据节点获取资源,主意，包含所有子节点
     * 
     * @param nodeId
     * @return
     */
    public List<DeviceUser> getDeviceListByNodeId(User user, Long nodeId) {
        List<Nenode> nenodes = nenodeService.getChildNenodeFullByPid(user, nodeId);
        if (nenodes.isEmpty()) {
            return new ArrayList<DeviceUser>();
        }

        List<Long> ids = new ArrayList<Long>();
        nenodes.forEach((item) -> {
            ids.add(item.getId());
        });

        QueryWrapper<DeviceUser> queryWrapper = new QueryWrapper<DeviceUser>();
        queryWrapper.in("id", ids);
        return deviceUserMapper.selectList(queryWrapper);
    }

    /**
     * 根据id获取设备信息
     * 
     * @param deviceId
     * @return
     */
    public Device getDeviceById(Long deviceId) {
        return deviceMapper.selectById(deviceId);
    }

    /**
     * 更新设备信息
     * 
     * @param user
     * @param update
     */
    public void updateDevice(User user, Device update) {
        if (update == null || update.getId() == null) {
            throw new BusinessException("错误错误");
        }

        DeviceUser deviceUser = this.getDeviceUserByDeviceidByCurUser(update.getId());
        if (deviceUser == null) {
            throw new BusinessException("错误错误");
        }

        Device tempDevice = getDeviceById(deviceUser.getDeviceId());
        if (tempDevice == null) {
            throw new BusinessException("错误错误");
        }

        deviceMapper.updateById(update);
        deviceUserLastUseService.append(user, deviceUser);

        logger.info("user update device success,uid:{},did:{}", user.getId(), update.getId());
    }

    /**
     * 获取设备配置信息
     * 
     * @param user
     * @param sn
     * @return
     */
    public DeviceSettings getDeviceSettings(String sn) {
        Device device = this.getDeviceBySN(sn);
        if (!device.getSn().equals(sn)) {
            return null;
        }

        DeviceSettings deviceSettings = new DeviceSettings();
        deviceSettings.setDeviceId(device.getId());
        QueryWrapper<DeviceSettings> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(deviceSettings);

        deviceSettings = deviceSettingsMapper.selectOne(queryWrapper);
        if (deviceSettings == null) {
            deviceSettings = new DeviceSettings();
            deviceSettings.setDeviceId(device.getId());
            deviceSettings.setAutoRemote(Status.YES);
            deviceSettings.setAutoRun(Status.YES);
            deviceSettingsMapper.insert(deviceSettings);
        }

        return deviceSettings;
    }

    /**
     * 更新设备信息
     * 
     * @param user
     * @param sn
     * @param deviceSettings
     */
    public void updateDeviceSettings(String sn, DeviceSettings deviceSettings) {
        Device device = this.getDeviceBySN(sn);
        if (!device.getSn().equals(sn)) {
            return;
        }

        DeviceSettings whereDeviceSettings = new DeviceSettings();
        whereDeviceSettings.setDeviceId(device.getId());
        QueryWrapper<DeviceSettings> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(whereDeviceSettings);
        
        deviceSettingsMapper.update(deviceSettings, queryWrapper);
    }

    /**
     * 获取绑定设备
     * 
     * @param sn
     * @param params
     * @return
     */
    public Object getBindDeviceKey(Client client, Map<String, String> params) {
        Device device = this.getDeviceBySN(client.getSn());
        if (device == null) {
            throw new BusinessException("参数错误");
        }

        BingKey key = new BingKey();
        key.setDeviceId(device.getId());
        key.setSn(client.getSn());
        key.setUuid(Util.getAutoIndex());
        key.setCtime(new Date());
        key.setTimeout(DEVICE_TIMEOUT_BINGKEY_SEC);
        bindkeys.put(client.getSn(), key);

        logger.info("device make bind key,sn:{},uuid:{}", client.getSn(), key.getUuid());
        return key;
    }

    /**
     * 发起绑定动作
     * 
     * @param user
     * @param uuid
     */
    public void bingDeviceUser(User user, String uuid, Integer code) {

        if (StringUtils.isEmpty(uuid)) {
            throw new BusinessException("非法授权码");
        }
        
        BingKey keyinfo = null;
        for (BingKey item : bindkeys.values()) {
            if (item.getUuid().equals(uuid)) {
                keyinfo = item;
                break;
            }
        }

        if (keyinfo == null) {
            throw new BusinessException("非法授权码");
        }

        DeviceUser tempDeviceUser = getDeviceUserByDeviceidByCurUser(keyinfo.getDeviceId());
        if (tempDeviceUser != null) {
            throw new BusinessException("用户已授权此网元设备 ，无需再授权");
        }

        keyinfo.setCode(code);
        keyinfo.setPriuuid(Util.UUID8());
        keyinfo.setTargetName(getTargetName());

        SecurityUser securityUser = RequestUtil.getSecurityUser();
        Message msg = new Message();
        msg.setEvent(Message.Type.ADD_NE_INVITE);
        msg.setBody(keyinfo);
        sessionService.sendMsg(keyinfo.getDeviceId(), msg);
        keyinfo.setTargetId(securityUser.getTargetId());
        keyinfo.setUserType(securityUser.getType());

        logger.info("user start bing device,uid:{}, uuid:{}", user.getId(), uuid);
    }

    /**
     * 获取当前会话名称
     * 
     * @return
     */
    private String getTargetName() {
        SecurityUser securityUser = RequestUtil.getSecurityUser();
        if (securityUser.getType() == UserType.SOLE) {
            User user = userService.getUserById(securityUser.getTargetId());
            if (StringUtils.isEmpty(user.getSelfname())) {
                return user.getUsername();
            }
            return user.getSelfname();
        }

        Enterprise enterprise = enterpriseService.getEnterpriseById(securityUser.getTargetId());
        return enterprise.getTitle();
    }

    /**
     * 判断是否拥有此此设备
     * 
     * @param deviceId
     * @return
     */
    public boolean hasDeviceByCurUser(Long deviceId) {
        DeviceUser tempDeviceUser = this.getDeviceUserByDeviceidByCurUser(deviceId);
        return tempDeviceUser != null;
    }

    /**
     * 获取用户设备id
     * 
     * @param deviceId
     * @return
     */
    public DeviceUser getDeviceUserByDeviceidByCurUser(Long deviceId) {
        DeviceUser entity = new DeviceUser();
        entity.setTargetId(RequestUtil.getSecurityUser().getTargetId());
        entity.setDeviceId(deviceId);

        QueryWrapper<DeviceUser> wrapper = new QueryWrapper<DeviceUser>();
        wrapper.setEntity(entity);
        return deviceUserMapper.selectOne(wrapper);
    }

    /**
     * 判断设备是否跟用户有绑定关系
     * 
     * @param deviceId
     * @param targetId
     * @return
     */
    public boolean deviceHasTargetid(Long deviceId, Long targetId) {
        DeviceUser entity = new DeviceUser();
        entity.setTargetId(targetId);
        entity.setDeviceId(deviceId);

        QueryWrapper<DeviceUser> wrapper = new QueryWrapper<DeviceUser>();
        wrapper.setEntity(entity);
        return deviceUserMapper.selectCount(wrapper) > 0;
    }

    /**
     * 获取用户设备id
     * 
     * @param targetId
     * @param deviceId
     * @return
     */
    private DeviceUser getDeviceUserByTargetidDeviceId(Long targetId, Long deviceId) {
        DeviceUser entity = new DeviceUser();
        entity.setTargetId(targetId);
        entity.setDeviceId(deviceId);

        QueryWrapper<DeviceUser> wrapper = new QueryWrapper<DeviceUser>();
        wrapper.setEntity(entity);
        return deviceUserMapper.selectOne(wrapper);
    }

    /**
     * 根据节点获取网元
     * 
     * @param user
     * @param nodeId
     * @return
     */
    private List<DeviceUser> getDeviceUserByTargetIdNodeId(Long targetId, Long nodeId) {

        DeviceUser entity = new DeviceUser();
        entity.setTargetId(targetId);
        entity.setNodeId(nodeId);

        QueryWrapper<DeviceUser> wrapper = new QueryWrapper<DeviceUser>();
        wrapper.setEntity(entity);
        return deviceUserMapper.selectList(wrapper);
    }

    private List<DeviceUser> getDeviceUserByIds(List<Long> ids) {
        return deviceUserMapper.selectBatchIds(ids);
    }

    /**
     * 确认授权码
     * 
     * @param sn
     * @param params
     * @return
     */
    public Object bindDeviceKeyACK(Client client, Map<String, String> params) {

        BingKey keyInfo = bindkeys.remove(client.getSn());
        if (keyInfo == null) {
            throw new BusinessException("授权码过期");
        }

        if (!keyInfo.getUuid().equals(params.get("uuid"))) {
            throw new BusinessException("授权码信息核对错误");
        }

        if (!keyInfo.getPriuuid().equals(params.get("priuuid"))) {
            throw new BusinessException("授权码信息核对错误");
        }

        DeviceUser tempDeviceUser = getDeviceUserByTargetidDeviceId(keyInfo.getTargetId(), keyInfo.getDeviceId());
        if (tempDeviceUser != null) {
            throw new BusinessException("用户已授权此网元设备 ，无需再授权");
        }

        Nenode nenodeDefault = nenodeService.getNenodeDefaultByTargetId(Long.valueOf(params.get("targetId")));
        DeviceUser deviceUser = new DeviceUser();
        deviceUser.setCtime(new Date());
        deviceUser.setDeviceId(keyInfo.getDeviceId());
        deviceUser.setNodeId(nenodeDefault.getId());
        deviceUser.setSn(keyInfo.getSn());
        deviceUser.setTargetId(keyInfo.getTargetId());
        deviceUser.setUserType(keyInfo.getUserType());
        deviceUserMapper.insert(deviceUser);

        logger.info("device auth add success,sn:{}, did:{}", client.getSn(), keyInfo.getDeviceId());
        return null;
    }

    /**
     * 网元移动
     * 
     * @param user
     * @param deviceId
     * @param nenodeId
     */
    public void moveDeviceNenode(User user, Long deviceId, Long nenodeId) {

        DeviceUser deviceUser = this.getDeviceUserByDeviceidByCurUser(deviceId);
        if (deviceUser == null) {
            throw new BusinessException("参数错误");
        }

        SecurityUser securityUser = RequestUtil.getSecurityUser();

        Nenode nenode = nenodeService.getNenodeById(nenodeId);
        if (nenode == null || !nenode.getOperId().equals(securityUser.getTargetId())) {
            throw new BusinessException("参数错误");
        }

        DeviceUser update = new DeviceUser();
        update.setId(deviceUser.getId());
        update.setNodeId(nenodeId);
        deviceUserMapper.updateById(update);

        logger.info("device move  success,uid:{}, did:{},nenode:{}", user.getId(), deviceId, nenodeId);
    }

    /**
     * 删除设备
     * 
     * @param user
     * @param deviceId
     */
    public void deleteDevice(User user, Long deviceId) {

        DeviceUser deviceUser = this.getDeviceUserByDeviceidByCurUser(deviceId);
        if (deviceUser == null) {
            throw new BusinessException("参数错误");
        }

        tunnelService.delTunnelByCurUserDeviceid(deviceId);
        deviceUserMapper.deleteById(deviceUser.getId());
        deviceUserLastUseService.deleteLastuse(user, deviceUser);
        logger.info("del device success,uid:{}, did:{}", user.getId(), deviceId);
    }

    /**
     * 获取用户最近使用的网元, 暂时屏蔽最近使用，原因是权限判断过于复杂，后续再实现
     * 
     * @param user
     * @return
     */
    public List<Device> getDeviceLastuseByUser(User user) {

        List<DeviceUserLastUse> lastuseList = deviceUserLastUseService.getDeviceUserLastUseListByUser(user);

        // 暂时屏蔽最近使用，原因是权限判断过于复杂，后续再实现
        lastuseList.clear();
        if (lastuseList.isEmpty()) {
            return new ArrayList<Device>();
        }

        List<Long> ids = new ArrayList<Long>();

        lastuseList.forEach((item) -> {
            ids.add(item.getDeviceUserId());
        });

        List<DeviceUser> deviceUsers = getDeviceUserByIds(ids);
        if (deviceUsers.isEmpty()) {
            return new ArrayList<Device>();
        }

        ids.clear();
        deviceUsers.forEach((item) -> {
            ids.add(item.getDeviceId());
        });

        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        queryWrapper.orderByDesc("id");

        List<Device> list = deviceMapper.selectList(queryWrapper);
        list.forEach((item) -> {
            boolean isonline = sessionService.deviceIsOnline(item.getId());
            item.setIsonline(isonline);
        });
        return list;
    }

    /**
     * 获取设备绑定关系
     * 
     * @param client
     * @param params
     * @return
     */
    public Object getAuthBindList(Client client, Map<String, String> params) {
        List<AuthInfo> retList = new ArrayList<>();
        
        DeviceUser entity = new DeviceUser();
        entity.setDeviceId(client.getDevice().getId());

        QueryWrapper<DeviceUser> wrapper = new QueryWrapper<DeviceUser>();
        wrapper.setEntity(entity);
        List<DeviceUser> list = deviceUserMapper.selectList(wrapper);
        if (list.isEmpty()) {
            return retList;
        }

        list.forEach((item) -> {
            AuthInfo authInfo = new AuthInfo();
            authInfo.setId(item.getId());
            if (item.getUserType() == UserType.SOLE) {
                User user = this.userService.getUserById(item.getTargetId());
                authInfo.setName(user.getUsername());
                authInfo.setType(UserType.SOLE);
                retList.add(authInfo);
                return;
            }

            Enterprise enterprise = this.enterpriseService.getEnterpriseById(item.getTargetId());
            authInfo.setName(enterprise.getTitle());
            authInfo.setType(UserType.ENTERPRISE);
            retList.add(authInfo);
        });

        return retList;
    }

    /**
     * 取消绑定
     * 
     * @param client
     * @param params
     * @return
     */
    public Object unbindDevice(Client client, Map<String, String> params) {
        Long targetId = Long.valueOf(params.get("targetId"));
        this.deviceUserMapper.deleteById(targetId);
        return null;
    }

}

