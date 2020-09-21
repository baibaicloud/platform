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
 *   Loon  2019年11月15日 下午10:28:54  created
 */
package com.loon.bridge.service.remotecpe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.loon.bridge.core.comenum.FRPProxyType;
import com.loon.bridge.core.comenum.RemoteProtocolType;
import com.loon.bridge.core.exception.BusinessException;
import com.loon.bridge.core.utils.TaskEngine;
import com.loon.bridge.core.utils.Util;
import com.loon.bridge.service.hacker.HackerService;
import com.loon.bridge.service.session.Client;
import com.loon.bridge.service.session.SessionService;
import com.loon.bridge.service.user.UserService;
import com.loon.bridge.uda.entity.Device;
import com.loon.bridge.uda.entity.User;

/**
 * 
 * git https://github.com/fatedier/frp
 * 
 * @author nbflow
 */
@Service
public class NatService implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${guac.depth.color}")
    private String GUAC_DEPTH_COLOR;

    @Value("${frp.port.max.count}")
    private Integer PORT_MAX_COUNT;

    @Value("${frp.port.start}")
    private Integer portStart;

    @Value("${frp.server.address}")
    private String NET_ADDRESS;

    @Value("${frp.server.port}")
    private Integer NET_PORT;

    @Value("${frp.last.date.max.sec}")
    private Integer LAST_DATE_MAX_SEC;

    @Autowired
    private HackerService hackerService;

    @Autowired
    private UserService userService;

    @Autowired
    private RemotecpeService remotecpeService;

    @Autowired
    private SessionService sessionService;

    private BlockingQueue<Integer> ports = null;

    /***
     * 存放已分配的端口
     */
    private Map<Integer, NatInfo> useings = new ConcurrentHashMap<>();
    private Map<String, Integer> uuidToPort = new ConcurrentHashMap<>();
    private Map<String, String> snToUuid = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        ports = new ArrayBlockingQueue<>(PORT_MAX_COUNT);
        int index = 0;
        while (index < PORT_MAX_COUNT) {
            ports.add(portStart);
            index++;
            portStart++;
        }

        logger.info("make net port success,start:{},end:{},size:{}", portStart - PORT_MAX_COUNT, portStart, ports.size());

        TaskEngine.getInstance().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    timeoutUsePorts();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }, 1000, 10 * 1000);
    }

    /**
     * 回收超时的端口
     */
    private void timeoutUsePorts() {
        Long curtime = System.currentTimeMillis();
        List<NatInfo> delPort = new ArrayList<>();
        useings.forEach((port, item) -> {
            long sec = curtime - item.getLastDate().getTime();
            sec = sec / 1000;
            if (sec > LAST_DATE_MAX_SEC) {
                delPort.add(item);
            }
        });

        delPort.forEach((port) -> {
            gcPort(port.getUid(), port.getRemotePort());
        });

        if (delPort.size() != 0) {
            logger.info("port timeout size:{}", delPort.size());
            delPort.clear();
        }
    }

    /**
     * 获取可用端口
     * 
     * @param uid
     * @return
     */
    public NatInfo makeNatInfo(User user, Device device, RemoteProtocolType type, Map<String, String> params) {
        Integer port = ports.poll();
        if(port == null) {
            logger.error("make port error,uid:{}", user.getId());
            throw new BusinessException("系统资源不足，请稍后");
        }

        NatInfo natInfo = new NatInfo();
        natInfo.setServerPort(NET_PORT);
        natInfo.setServerAddress(NET_ADDRESS);
        natInfo.setTunnetType(type);
        natInfo.setUuid(Util.UUID());
        natInfo.setUid(user.getId());
        natInfo.setRemotePort(port);
        natInfo.setCtime(new Date());
        natInfo.setLastDate(natInfo.getCtime());
        natInfo.setSelfname(user.getSelfname());
        natInfo.setDeviceId(device.getId());
        natInfo.setDeviceSn(device.getSn());
        natInfo.setLocalPort(Integer.valueOf(params.get("localPort")));

        if (type == RemoteProtocolType.VNC) {
            natInfo.setPassword(Util.UUID8());
        } else if (type == RemoteProtocolType.RDP) {
            natInfo.setUsername(params.get("username"));
            natInfo.setPassword(params.get("password"));
        }

        Client client = sessionService.getClientByDeviceId(device.getId());
        natInfo.setProxyName(FRPProxyType.RP + "_" + client.getToken() + "_" + natInfo.getUuid());

        useings.put(natInfo.getRemotePort(), natInfo);
        uuidToPort.put(natInfo.getUuid(), natInfo.getRemotePort());
        snToUuid.put(natInfo.getDeviceSn(), natInfo.getUuid());

        logger.info("make port success,uid:{},port:{},pw:{}", user.getId(), port, natInfo.getPassword());
        return natInfo;
    }

    /**
     * 归还端口
     * 
     * @param uid
     * @param port
     * @return
     */
    public Integer gcPort(Long uid, Integer port) {

        if (uid == null) {
            uid = -1L;
        }

        NatInfo natInfo = useings.remove(port);
        if (natInfo == null) {
            logger.warn("gc port error,not find port info,port:{},uid:{}", port, uid);
            return null;
        }

        ports.add(port);
        uuidToPort.remove(natInfo.getUuid());
        snToUuid.remove(natInfo.getDeviceSn());

        logger.debug("gc port success,uid:{},port:{}", uid, port);
        return port;
    }

    /**
     * 根据uuid获取端口信息
     * 
     * @param uid
     * @param uuid
     * @return
     */
    public NatInfo getPortInfoByUUID(String username, String uuid) {
        Integer port = uuidToPort.get(uuid);
        if (port == null || StringUtils.isEmpty(username)) {
            throw new BusinessException("参数错误");
        }
        
        NatInfo natInfo = useings.get(port);
        if(natInfo == null) {
            throw new BusinessException("参数错误");
        }
        
        User user = userService.getUserByUsername(username);
        if (!natInfo.getUid().equals(user.getId())) {
            hackerService.append(user, "get port error");
        }
        
        return natInfo;
    }

    /**
     * 根据uuid获取nat信息
     * 
     * @param uuid
     * @return
     */
    public NatInfo getPortInfoByUUID(String uuid) {
        Integer port = uuidToPort.get(uuid);
        if (port == null) {
            throw new BusinessException("参数错误");
        }

        NatInfo natInfo = useings.get(port);
        if (natInfo == null) {
            throw new BusinessException("参数错误");
        }

        return natInfo;
    }

    /**
     * 判断是否正在远程中
     * 
     * @param sn
     * @return
     */
    public boolean isRemoteingByDeviceSN(String sn) {
        return snToUuid.containsKey(sn);
    }

    /**
     * 根据sn获取nat信息
     * 
     * @param sn
     * @param uid
     * @return
     */
    public NatInfo getNatInfoByDeviceSN(Client client) {
        String uuid = snToUuid.get(client.getSn());
        if (StringUtils.isEmpty(uuid)) {
            return null;
        }

        Integer port = uuidToPort.get(uuid);
        NatInfo natInfo = useings.get(port);

        return natInfo;
    }

    /**
     * 保持心跳
     * 
     * @param uid
     * @param port
     */
    public void ping(Integer port) {
        NatInfo natInfo = useings.get(port);
        if (natInfo == null) {
            return;
        }

        natInfo.setLastDate(new Date());
        logger.debug("ping port,port:{}", port);
    }

    /**
     * 根据uuid及时回收端口
     * 
     * @param uuid
     */
    public void gcPortByUUID(String uuid) {
        Integer port = uuidToPort.get(uuid);
        if (port == null) {
            return;
        }

        NatInfo natInfo = useings.get(port);
        if (natInfo == null) {
            return;
        }

        gcPort(null, port);
        remotecpeService.closeRemoteControl(natInfo);
    }

    /**
     * 获取画质颜色
     * 
     * @return
     */
    public String getGuacDepthColor() {
        return GUAC_DEPTH_COLOR;
    }

}
