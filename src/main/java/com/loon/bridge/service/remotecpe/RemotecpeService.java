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
 *   Loon  2019年11月19日 下午11:04:04  created
 */
package com.loon.bridge.service.remotecpe;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.loon.bridge.core.comenum.RemoteProtocolType;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.service.ack.ACKService;
import com.loon.bridge.service.ack.CompletableFutureInfo;
import com.loon.bridge.service.device.DeviceService;
import com.loon.bridge.service.lastuse.DeviceUserLastUseService;
import com.loon.bridge.service.session.Message;
import com.loon.bridge.service.session.SessionService;
import com.loon.bridge.uda.entity.Device;
import com.loon.bridge.uda.entity.DeviceSettings;
import com.loon.bridge.uda.entity.DeviceUser;
import com.loon.bridge.uda.entity.User;

/**
 * 
 *
 * @author nbflow
 */
@Service
public class RemotecpeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${remotedevice.shutdown.seconds}")
    private Integer SHOWDOWN_DEVICE_SECONDS;

    @Autowired
    private NatService frpService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ACKService ackService;

    @Autowired
    private DeviceUserLastUseService deviceUserLastUseService;

    @Autowired
    private NatService natService;

    /**
     * 关闭远程连接
     * 
     * @param natInfo
     */
    public void closeRemoteControl(NatInfo natInfo) {
        Message msg = new Message();
        msg.setEvent(Message.Type.CLOSE_REMOTE_CONTROL);
        sessionService.sendMsg(natInfo.getDeviceId(), msg);
    }

    /**
     * 开始远程控制 type is vnc
     * 
     * @param user
     * @param deviceId
     */
    public NatInfo startRemoteControl(User user, Map<String, String> params) {

        Long deviceId = Long.valueOf(params.get("deviceId"));
        
        boolean isonline = sessionService.deviceIsOnline(deviceId);
        if (!isonline) {
            throw new BusinessException("设备已离线，无法发起远程");
        }

        DeviceUser deviceUser = deviceService.getDeviceUserByDeviceidByCurUser(deviceId);
        if (deviceUser == null) {
            throw new BusinessException("找不到设备");
        }

        Device device = deviceService.getDeviceById(deviceId);
        if (device == null) {
            throw new BusinessException("找不到设备");
        }

        RemoteProtocolType protocolType = RemoteProtocolType.valueOf(params.get("type").toUpperCase());
        NatInfo natInfo = frpService.makeNatInfo(user, device, protocolType, params);
        if (natInfo == null) {
            throw new BusinessException("系统资源不足，请稍后");
        }

        DeviceSettings deviceSettings = deviceService.getDeviceSettings(device.getSn());
        CompletableFutureInfo tempCompletableFutureInfo = ackService.make(user);
        natInfo.setAckuuid(tempCompletableFutureInfo.getUuid());
        natInfo.setRemoteack(deviceSettings.getAutoRemote());

        Message msg = new Message();
        msg.setEvent(Message.Type.START_REMOTE_CONTROL);
        msg.setBody(natInfo);
        sessionService.sendMsg(deviceId, msg);

        logger.info("user start remote control success,uid:{},did:{}", user.getId(), deviceId);
        String acktype = null;
        try {
            acktype = tempCompletableFutureInfo.get("请求超时，请检查网络是否正常").toString();
        } catch (Exception e) {
            natService.gcPortByUUID(natInfo.getUuid());
            throw new BusinessException(e.getMessage());
        }

        if (acktype.equals("no")) {
            throw new BusinessException("远程机器拒绝了你的请求");
        }

        deviceUserLastUseService.append(user, deviceUser);
        return natInfo;
    }

    /**
     * 关闭计算机
     * 
     * @param user
     * @param deviceId
     */
    public void shutdownDevice(User user, Long deviceId) {

        boolean isonline = sessionService.deviceIsOnline(deviceId);
        if (!isonline) {
            throw new BusinessException("设备已离线，无法发起关机");
        }

        DeviceUser deviceUser = deviceService.getDeviceUserByDeviceidByCurUser(deviceId);
        if (deviceUser == null) {
            throw new BusinessException("找不到设备");
        }

        Device device = deviceService.getDeviceById(deviceId);
        if (device == null) {
            throw new BusinessException("找不到设备");
        }

        Message msg = new Message();
        msg.setEvent(Message.Type.SHUTDOWN);
        Map<String, Object> body = new HashMap<>();
        body.put("seconds", SHOWDOWN_DEVICE_SECONDS);
        msg.setBody(body);

        sessionService.sendMsg(deviceId, msg);

        deviceUserLastUseService.append(user, deviceUser);
        logger.info("user shutdown success,uid:{},did:{}", user.getId(), deviceId);
    }
}
