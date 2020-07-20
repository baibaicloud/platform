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
 *   Loon  2019年11月2日 下午11:54:43  created
 */
package com.loon.bridge.service.session;

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

import com.loon.bridge.config.ErrorCode;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.TaskEngine;
import com.loon.bridge.core.utils.Util;
import com.loon.bridge.service.device.DeviceService;
import com.loon.bridge.service.remotecpe.NatInfo;
import com.loon.bridge.service.remotecpe.NatService;
import com.loon.bridge.uda.entity.Device;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class SessionService implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${device.timeout.max.sec}")
    private Integer DEVICE_TIMEOUT_MAX;

    @Value("${device.timeout.interval.sec}")
    private Integer DEVICE_TIMEOUT_INTERVAL;

    @Value("${device.timeout.msg.max.sec}")
    private Integer DEVICE_TIMEOUT_MSG_MAX_SEC;

    @Autowired
    private NatService natService;

    @Autowired
    private DeviceService deviceService;

    /**
     * 存放在线设备信息 key:token
     */
    private Map<String, Client> clients = new ConcurrentHashMap<>();

    /**
     * 存放在线设备id和token的关系数据
     */
    private Map<Long, String> devicesToToken = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        TaskEngine.getInstance().schedule(new TimerTask() {
            @Override
            public void run() {
                checkTimeout();
            }
        }, 0, DEVICE_TIMEOUT_INTERVAL * 1000);
    }

    /**
     * 检测设备是否离线
     */
    private void checkTimeout() {
        final Long curtime = System.currentTimeMillis();
        List<String> delTokens = new ArrayList<>();
        clients.forEach((token, client) -> {
            int temp = (int) ((curtime - client.getLastDate().getTime()) / 1000);
            if (temp > DEVICE_TIMEOUT_MAX) {
                delTokens.add(token);
                if (devicesToToken.containsKey(client.getDevice().getId())) {
                    String temptoken = devicesToToken.get(client.getDevice().getId());
                    if (temptoken.equals(token)) {
                        devicesToToken.remove(client.getDevice().getId());
                    }
                }
            }
        });

        delTokens.forEach((token) -> {
            clients.remove(token);
        });

        logger.info("end check device timeout,timeout size:{},online:{}", delTokens.size(), clients.size());

        if (!delTokens.isEmpty()) {
            delTokens.clear();
        }
    }

    /**
     * 创建用token
     * 
     * @param uid
     * @param ip
     * @param uid
     * @return
     */
    public String makeUserToken(String sn) {

        if (StringUtils.isEmpty(sn)) {
            throw new BusinessException("参数非法", ErrorCode.RESET_LOGIN);
        }

        Device device = deviceService.getDeviceBySN(sn);
        if (device == null) {
            device = new Device();
            device.setSn(sn);
            device = deviceService.addDevice(device);
        }

        String token = devicesToToken.get(device.getId());
        if (!StringUtils.isEmpty(token)) {
            return token;
        }

        Client client = new Client(device);
        client.setToken(Util.UUID());
        clients.put(client.getToken(), client);
        devicesToToken.put(device.getId(), client.getToken());

        logger.info("make token success,sn:{},token:{}", sn, client.getToken());
        return client.getToken();
    }

    /**
     * 获取用户信息
     * 
     * @param token
     * @return
     */
    public Message getClientMessageByToken(String token) {

        if (StringUtils.isEmpty(token)) {
            throw new BusinessException("无授权信息", ErrorCode.RESET_LOGIN);
        }

        Client client = clients.get(token);
        if (client == null) {
            throw new BusinessException("无授权信息", ErrorCode.RESET_AUTH);
        }

        this.ping(token);

        Message msg = client.pollMessage();
        if (msg == null) {
            msg = new Message();
            msg.setEvent(Message.Type.NONE);
            return msg;
        }

        long sec = System.currentTimeMillis() - msg.getCtime().getTime();
        sec = sec / 1000;
        if (sec > DEVICE_TIMEOUT_MSG_MAX_SEC) {
            return getClientMessageByToken(token);
        }

        return msg;
    }

    /**
     * 根据token获取client信息
     * 
     * @param token
     * @return
     */
    public Client getClientByToken(String token) {
        return clients.get(token);
    }

    /**
     * ping
     * 
     * @param token
     */
    public void ping(String token) {

        if (token == null) {
            throw new BusinessException("无授权信息", 1);
        }

        Client client = clients.get(token);
        if (client == null) {
            throw new BusinessException("无授权信息", 1);
        }

        client.setLastDate(new Date());
        logger.debug("ping token:{}", token);
    }

    /**
     * 获取设备是否在线
     * 
     * @param deviceId
     * @return
     */
    public boolean deviceIsOnline(Long deviceId) {
        return devicesToToken.containsKey(deviceId);
    }

    /**
     * 推送消息到设备
     * 
     * @param deviceId
     * @param msg
     */
    public void sendMsg(Long deviceId, Message msg) {
        String token = devicesToToken.get(deviceId);
        if (token == null) {
            throw new BusinessException("设备离线");
        }

        Client client = clients.get(token);
        client.put(msg);
        logger.debug("push msg to device success,did:{},msg type:{}", deviceId, msg.getEvent());
    }

    /**
     * 根据client获取token信息
     * 
     * @param deviceId
     * @return
     */
    public Client getClientByDeviceId(Long deviceId) {
        String token = devicesToToken.get(deviceId);
        if (token == null) {
            throw new BusinessException("设备离线");
        }

        Client client = clients.get(token);
        return client;
    }

    /**
     * 推送消息到设备
     * 
     * @param deviceId
     * @param msg
     */
    public void asynSendMsg(Long deviceId, Message msg) {
        String token = devicesToToken.get(deviceId);
        if (token == null) {
            return;
        }

        Client client = clients.get(token);
        client.put(msg);
        logger.debug("push msg to device success,did:{},msg type:{}", deviceId, msg.getEvent());
    }

    /**
     * 获取用户当前会话状态
     * 
     * @param user
     * @param sn
     * @return
     */
    public UserSessionStatus getUserStatus(Client client) {

        NatInfo natInfo = natService.getNatInfoByDeviceSN(client);

        UserSessionStatus userStatus = new UserSessionStatus();
        if (natInfo != null) {
            userStatus.setRemoteIng(natInfo != null);
            userStatus.setRemoteSelfname(natInfo.getSelfname());
        }

        return userStatus;
    }
}
